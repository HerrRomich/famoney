package com.hrrm.famoney.api.accounts.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;

import javax.persistence.LockModeType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;

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
    private final TransactionControl txControl;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final EntryApiService entryApiService;

    @Activate
    public MovementApiService(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final TransactionControl txControl, @Reference final AccountRepository accountRepository,
            @Reference final MovementRepository movementRepository, @Reference final EntryApiService entryApiService) {
        this.logger = logger;
        this.txControl = txControl;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.entryApiService = entryApiService;
    }

    public Movement addMovement(final Account account, final MovementDataDTO movementDataDTO)
            throws UnknownMovementType {
        logger.debug("Creating movement.");
        logger.trace(l -> l.trace("A new movement will be created with DTO data {}.", movementDataDTO));
        Movement movement;
        try {
            movement = txControl.required(() -> {
                return callAddMovement(account, movementDataDTO);
            });
        } catch (ScopedWorkException e) {
            throw e.as(UnknownMovementType.class);
        }
        logger.debug("A new movement is created.");
        logger.trace(l -> l.trace("A new movement {} is created.", movement));
        return movement;
    }

    private Movement callAddMovement(final Account account, final MovementDataDTO movementDataDTO)
            throws UnknownMovementType {
        Movement movement;
        if (movementDataDTO instanceof EntryDataDTO) {
            movement = entryApiService.createMovement((EntryDataDTO) movementDataDTO);
        } else {
            String message = MessageFormat.format(UNKNOWN_DTO_MESSAGE, movementDataDTO.getClass());
            logger.warn(message);
            throw new UnknownMovementType(message);
        }
        accountRepository.lock(account, LockModeType.PESSIMISTIC_WRITE);
        Integer newPosition = movementRepository.getLastPositionByAccountOnDate(account, movementDataDTO.getDate());
        movement = setMovementAttributes(account, movement, movementDataDTO).setPosition(newPosition);
        final var addedMovement = movementRepository.save(movement);
        adjustAccountMovements(addedMovement, newPosition);
        return addedMovement;
    }

    private Movement setMovementAttributes(Account account, Movement movement, MovementDataDTO movementDataDTO) {
        return movement.setAccount(account)
            .setDate(movementDataDTO.getDate())
            .setBookingDate(movementDataDTO.getBookingDate()
                .orElse(null))
            .setBudgetPeriod(movementDataDTO.getBudgetPeriod()
                .orElse(null));
    }

    private void adjustAccountMovements(final Movement movement, Integer positionAfter) {
        final var account = movement.getAccount();
        account.setMovementCount(account.getMovementCount() + 1);
        account.setMovementTotal(account.getMovementTotal()
            .add(movement.getAmount()));
        movementRepository.adjustMovementPositionsAndSumsByAccountAfterPosition(movement, positionAfter);
        final var totalBeforeMovement = movementRepository.findNextMovementByAccountIdBeforePosition(account, positionAfter)
            .map(Movement::getTotal)
            .orElse(BigDecimal.ZERO);
        movement.setTotal(totalBeforeMovement.add(movement.getAmount())).setPosition(positionAfter);
    }

    public Movement updateMovement(final Movement movement, final MovementDataDTO movementDataDTO)
            throws IncompatibleMovementType {
        logger.debug("Updateing movement.");
        logger.trace(l -> l.trace("Movement {} will be updated with DTO data {}.", movement, movementDataDTO));
        accountRepository.lock(movement.getAccount(), LockModeType.PESSIMISTIC_WRITE);
        final var positionBefore = movement.getPosition();
        rollbackAccount(movement);
        var positionAfter = positionBefore;
        if (!movement.getDate().equals(movementDataDTO.getDate())) {
            positionAfter = movementRepository.getLastPositionByAccountOnDate(movement.getAccount(), movementDataDTO.getDate());
        }
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
        adjustAccountMovements(updatedMovement, positionAfter);
        logger.debug("Movement is updated.");
        logger.trace(l -> l.trace("Movement {} is updated.", movement));
        return updatedMovement;
    }

    private void rollbackAccount(Movement movement) {
        final var positionBefore = movement.getPosition();
        movement.setPosition(-1);
        final var account = movement.getAccount();
        account.setMovementCount(account.getMovementCount() - 1);
        account.setMovementTotal(account.getMovementTotal()
            .subtract(movement.getAmount()));
        movementRepository.rollbackMovementPositionsAndSumsByAccountAfterPosition(movement, positionBefore);
    }

}
