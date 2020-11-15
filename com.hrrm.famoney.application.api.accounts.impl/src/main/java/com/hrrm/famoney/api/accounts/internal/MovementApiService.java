package com.hrrm.famoney.api.accounts.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.persistence.LockModeType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.google.common.base.Supplier;
import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTO;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Entry;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = MovementApiService.class)
public class MovementApiService {

    private static final String INCOMPATIBLE_ENTITY_AND_DTO_MESSAGE = "Movement entity class [{0}] for account id: {1} and movement id: {2] are incompatible with DTO class [{3}].";
    private static final String UNKNOWN_DTO_MESSAGE = "Unknown DTO class [{0}].";

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final EntryApiService entryApiService;

    @Activate
    public MovementApiService(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository,
            @Reference final MovementRepository movementRepository, @Reference final EntryApiService entryApiService) {
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.entryApiService = entryApiService;
    }

    public Movement addMovement(final Account account, final MovementDataDTO movementDataDTO)
            throws UnknownMovementType {
        logger.debug("Creating movement.");
        logger.trace(l -> l.trace("A new movement will be created with DTO data {}.", movementDataDTO));
        Movement movement;
        if (movementDataDTO instanceof EntryDataDTO) {
            movement = entryApiService.createMovement((EntryDataDTO) movementDataDTO);
        } else {
            String message = MessageFormat.format(UNKNOWN_DTO_MESSAGE, movementDataDTO.getClass());
            logger.warn(message);
            throw new UnknownMovementType(message);
        }
        accountRepository.lock(account, LockModeType.PESSIMISTIC_WRITE);
        movement = setMovementAttributes(account, movement, movementDataDTO);
        final var addedMovement = movementRepository.save(movement);
        movementRepository.flush();
        adjustAccountMovements(addedMovement);
        logger.debug("A new movement is created.");
        logger.trace(l -> l.trace("A new movement {} is created.", addedMovement));
        return movement;
    }

    private Movement setMovementAttributes(Account account, Movement movement, MovementDataDTO movementDataDTO) {
        final var movementTime = Optional.ofNullable(movement.getDate())
            .filter(date -> date.toLocalDate()
                .equals(movementDataDTO.getDate()))
            .map(LocalDateTime::toLocalTime)
            .orElseGet(() -> getLastMovementTime(movement.getAccount(), movementDataDTO.getDate()));
        final var bookingTimeOptional = Optional.ofNullable(movement.getBookingDate())
            .filter(bookingDate -> bookingDate.toLocalDate()
                .equals(movementDataDTO.getBookingDate()
                    .orElse(null)))
            .map(LocalDateTime::toLocalTime);
        return movement.setAccount(account)
            .setDate(movementDataDTO.getDate()
                .atTime(movementTime))
            .setBookingDate(movementDataDTO.getBookingDate()
                .map(bookingDate -> bookingDate.atTime(bookingTimeOptional.orElseGet(() -> getLastBookingTime(account,
                        bookingDate))))
                .orElse(null))
            .setBudgetPeriod(movementDataDTO.getBudgetPeriod()
                .orElse(null));
    }

    private LocalTime getLastMovementTime(Account account, LocalDate movementDate) {
        return getLastTimeByMovementProvider(account, movementDate,
                movementRepository::findNextMovementByAccountIdBeforeDate);
    }

    private LocalTime getLastBookingTime(Account account, LocalDate bookingDate) {
        return getLastTimeByMovementProvider(account, bookingDate,
                movementRepository::findNextMovementByAccountIdBeforeBookingDate);
    }

    private LocalTime getLastTimeByMovementProvider(Account account, LocalDate movementDate,
            BiFunction<Account, LocalDateTime, Optional<Movement>> lastMovementForADayProvider) {
        final var beginOfMovementDate = movementDate.atStartOfDay();
        final var beginOfNextDay = beginOfMovementDate.plusDays(1);
        final var lastMovementForADay = lastMovementForADayProvider.apply(account, beginOfNextDay);
        LocalDateTime last = lastMovementForADay.map(Movement::getDate)
            .orElse(beginOfMovementDate);
        final var halfDuration = Duration.between(last, beginOfNextDay)
            .dividedBy(2);
        return last.plus(halfDuration)
            .toLocalTime();
    }

    private void adjustAccountMovements(final Movement movement) {
        final var account = movement.getAccount();
        account.setMovementCount(account.getMovementCount() + 1);
        account.setMovementTotal(account.getMovementTotal()
            .add(movement.getAmount()));
        final var totalBeforeMovement = movementRepository.findNextMovementByAccountIdBeforeDate(account, movement
            .getDate())
            .map(Movement::getTotal)
            .orElse(BigDecimal.ZERO);
        movement.setTotal(totalBeforeMovement.add(movement.getAmount()));
        movementRepository.adjustMovementSumsByAccountAfterDate(account, movement.getDate(), movement.getAmount());
    }

    public Movement updateMovement(final Movement movement, final MovementDataDTO movementDataDTO)
            throws IncompatibleMovementType {
        logger.debug("Updateing movement.");
        logger.trace(l -> l.trace("Movement {} will be updated with DTO data {}.", movement, movementDataDTO));
        accountRepository.lock(movement.getAccount(), LockModeType.PESSIMISTIC_WRITE);
        rollbackAccount(movement);
        Movement updatedMovement;
        if (movementDataDTO instanceof EntryDataDTO && movement instanceof Entry) {
            updatedMovement = entryApiService.updateMovement((Entry) movement, (EntryDataDTO) movementDataDTO);
        } else {
            String message = MessageFormat.format(INCOMPATIBLE_ENTITY_AND_DTO_MESSAGE, movement.getClass(), movement
                .getAccount()
                .getId(), movement.getId(), movementDataDTO.getClass());
            logger.warn(message);
            throw new IncompatibleMovementType(message);
        }
        updatedMovement = setMovementAttributes(movement.getAccount(), updatedMovement, movementDataDTO);
        movementRepository.flush();
        adjustAccountMovements(updatedMovement);
        logger.debug("Movement is updated.");
        logger.trace(l -> l.trace("Movement {} is updated.", movement));
        return updatedMovement;
    }

    private void rollbackAccount(Movement movement) {
        final var account = movement.getAccount();
        account.setMovementCount(account.getMovementCount() - 1);
        account.setMovementTotal(account.getMovementTotal()
            .subtract(movement.getAmount()));
        movementRepository.adjustMovementSumsByAccountAfterDate(account, movement.getDate(), movement.getAmount()
            .negate());
    }

}
