package com.hrrm.famoney.api.accounts.resource.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementOrder;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountApiError;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountNotFoundException;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiError;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = AccountsApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi {

    private static class MovementDTOCollector {
        private BigDecimal sum;
        private List<MovementDTO> movementDTOs;

        public MovementDTOCollector(BigDecimal sum) {
            super();
            this.sum = sum;
            movementDTOs = new ArrayList<>();
        }

        public void addNextMovement(Movement movement) {
            sum = sum.add(movement.getAmount());
            final var movementDTO = MovementDTOImpl.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .bookingDate(movement.getBookingDate())
                .amount(movement.getAmount())
                .sum(sum)
                .build();
            movementDTOs.add(movementDTO);
        }

        public List<MovementDTO> getMovements() {
            return Collections.unmodifiableList(movementDTOs);
        }

    }

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementSliceRepository movementSliceRepository;
    private final TransactionControl txControl;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference AccountRepository accountRepository, @Reference MovementRepository movementRepository,
            @Reference MovementSliceRepository movementSliceRepository, @Reference TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.movementSliceRepository = movementSliceRepository;
        this.txControl = txControl;
    }

    @Override
    public List<AccountDTO> getAllAccounts(Set<String> tags) {
        logger.debug(l -> l.debug("Getting all accounts by {} tag(s).", tags.size()));
        logger.trace(l -> l.trace("Getting all accounts by tag(s): {}.", tags));

        final var tagFilterCondition = Optional.ofNullable(tags)
            .filter(Predicate.not((Set::isEmpty)))
            .map(this::createTagPredicate)
            .orElse(Predicates.alwaysTrue());
        return txControl.requiresNew(() -> {
            final var accountsStream = accountRepository.findAll()
                .stream()
                .filter(tagFilterCondition);

            final var result = accountsStream.map(this::mapAccountToAccountDTO)
                .collect(Collectors.toList());
            logger.debug("Got {} accounts.", result.size());
            logger.trace(l -> l.trace("Got accounts: {}", result));
            return result;
        });
    }

    private Predicate<Account> createTagPredicate(Set<String> t) {
        return account -> !Sets.intersection(account.getTags(), t)
            .isEmpty();
    }

    private AccountDTO mapAccountToAccountDTO(Account account) {
        return AccountDTOImpl.builder()
            .id(account.getId())
            .from(mapAccountToAccountDataDTO(account))
            .movementCount(account.getMovementCount())
            .sum(account.getMovementSum())
            .build();
    }

    private AccountDataDTO mapAccountToAccountDataDTO(Account account) {
        return AccountDataDTOImpl.builder()
            .name(account.getName())
            .openDate(account.getOpenDate())
            .tags(account.getTags())
            .build();
    }

    @Override
    public void addAccount(AccountDataDTO accountData) {
        txControl.required(() -> {
            var account = new Account().setName(accountData.getName())
                .setOpenDate(accountData.getOpenDate())
                .setTags(accountData.getTags());
            return accountRepository.save(account);
        });
    }

    @Override
    public AccountDataDTO changeAccount(Integer id, AccountDataDTO accountData) {
        try {
            return txControl.required(() -> {
                var account = getAccountByIdOrThrowNotFound(id, AccountApiError.NO_ACCOUNT_BY_CHANGE);
                account.setName(accountData.getName())
                    .setOpenDate(accountData.getOpenDate())
                    .setTags(accountData.getTags());
                accountRepository.save(account);
                return accountData;
            });
        } catch (ScopedWorkException ex) {
            throw ex.as(ApiException.class);
        }
    }

    @Override
    public AccountDTO getAccount(Integer id) {
        logger.debug("Getting account info with ID: {}", id);
        final var account = getAccountByIdOrThrowNotFound(id, AccountApiError.NO_ACCOUNT_ON_GET_ACCOUNT);
        logger.debug("Got account info with ID: {}", account.getId());
        return mapAccountToAccountDTO(account);
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull Integer id, Integer offset, Integer limit, MovementOrder order) {
        final var offsetOptional = Optional.ofNullable(offset);
        final var limitOptional = Optional.ofNullable(limit);
        String orderedByText;
        Function<Integer, Optional<MovementSlice>> findLastByAccountBeforeOffsetByDate;
        BiFunction<LocalDateTime, Optional<Integer>, List<Movement>> findMovementsByAccountIdAfterDate;
        Function<MovementSlice, Integer> getCount;
        Function<MovementSlice, BigDecimal> getSum;
        if (order == MovementOrder.BOOKING_DATE) {
            orderedByText = "booking date";
            findLastByAccountBeforeOffsetByDate = offsetVal -> movementSliceRepository
                .findLastByAccountBeforeOffsetByBookingDate(id, offsetVal);
            findMovementsByAccountIdAfterDate = (dateFrom, limitFromSliceOptional) -> movementRepository
                .findMovementsByAccountIdAfterBookingDate(id, dateFrom, limitFromSliceOptional);
            getCount = MovementSlice::getBookingCount;
            getSum = MovementSlice::getBookingSum;
        } else {
            orderedByText = "movement date";
            findLastByAccountBeforeOffsetByDate = offsetVal -> movementSliceRepository
                .findLastByAccountBeforeOffsetByMovementDate(id, offsetVal);
            findMovementsByAccountIdAfterDate = (dateFrom, limitFromSliceOptional) -> movementRepository
                .findMovementsByAccountIdAfterMovementDate(id, dateFrom, limitFromSliceOptional);
            getCount = MovementSlice::getMovementCount;
            getSum = MovementSlice::getMovementSum;
        }
        logger.debug("Getting all movemnts of account with id: {}, offset: {} and count: {}," + " ordered by {}", id,
            offsetOptional.map(Object::toString)
                .orElse("\"from beginning\""), limitOptional.map(Object::toString)
                    .orElse("\"all\""), orderedByText);
        try {
            final var movementDTOs = txControl.supports(() -> {
                getAccountByIdOrThrowNotFound(id, AccountApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
                final var movementSliceOptional = offsetOptional.flatMap(findLastByAccountBeforeOffsetByDate);
                final var dateFrom = movementSliceOptional.map(MovementSlice::getDate)
                    .orElse(MovementSlice.FIRST_SLICE_DATE)
                    .atTime(0, 0);
                final var offsetFromSlice = offsetOptional.orElse(0) -
                        movementSliceOptional.map(getCount)
                            .orElse(0);
                final var limitFromSliceOptional = limitOptional.map(limitVal -> limitVal + offsetFromSlice);
                List<Movement> movements = findMovementsByAccountIdAfterDate.apply(dateFrom, limitFromSliceOptional);
                var sum = Stream.concat(movementSliceOptional.map(getSum)
                    .stream(), movements.stream()
                        .limit(offsetFromSlice)
                        .map(Movement::getAmount))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                final var movementDTOCollector = new MovementDTOCollector(sum);
                movements.stream()
                    .skip(offsetFromSlice)
                    .forEachOrdered(movementDTOCollector::addNextMovement);
                return movementDTOCollector.getMovements();
            });
            logger.debug(l -> l.debug("Got {} movemnts of account with ID: {}", movementDTOs.size(), id));
            logger.trace(l -> l.trace("Got movemnts of account with ID: {}. {}", id, movementDTOs));
            return movementDTOs;
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    private Account getAccountByIdOrThrowNotFound(@NotNull Integer accountId, ApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE, accountId);
                final var exception = new AccountNotFoundException(error, errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                return exception;
            });
    }

}
