package com.hrrm.famoney.api.accounts.resource.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import com.hrrm.famoney.api.accounts.dto.MovementSliceDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceWithMovementsDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSlicesInfoDTO;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementSliceDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementSliceWithMovementsDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementSlicesInfoDTOImpl;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountApiError;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountNotFoundException;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.MovementSliceNotFoundException;
import com.hrrm.famoney.application.service.accounts.AccountMovementService;
import com.hrrm.famoney.application.service.accounts.MovementSliceNotFound;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;
import com.hrrm.famoney.function.ThrowingSupplier;
import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = AccountsApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi {

    private static final MovementSliceDTO FIRST_MOVEMENT_SLICE_DTO = MovementSliceDTOImpl.builder()
        .id(MovementSlice.FIRST_SLICE_ID)
        .date(MovementSlice.FIRST_SLICE_DATE)
        .movementSum(BigDecimal.ZERO)
        .movementCount(0)
        .bookingSum(BigDecimal.ZERO)
        .bookingCount(0)
        .build();

    private static final Function<? super MovementSlice, ? extends MovementSliceDTO> MOVEMENT_SLICE_TO_DTO_MAPPER = slice -> MovementSliceDTOImpl
        .builder()
        .id(slice.getId())
        .date(slice.getDate())
        .movementSum(slice.getMovementSum())
        .movementCount(slice.getMovementCount())
        .bookingSum(slice.getMovementSum())
        .bookingCount(slice.getMovementCount())
        .build();

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementSliceRepository movementSliceRepository;
    private final AccountMovementService accountMovementService;
    private final TransactionControl txControl;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference AccountRepository accountRepository,
            @Reference MovementRepository movementRepository,
            @Reference MovementSliceRepository movementSliceRepository,
            @Reference AccountMovementService accountMovementService,
            @Reference TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.movementSliceRepository = movementSliceRepository;
        this.accountMovementService = accountMovementService;
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
    public AccountDataDTO changeAccount(Integer accountId, AccountDataDTO accountData) {
        try {
            return txControl.required(() -> {
                var account = getAccountByIdOrThrowNotFound(accountId,
                        AccountApiError.NO_ACCOUNT_BY_CHANGE);
                account.setName(accountData.getName())
                    .setOpenDate(accountData.getOpenDate())
                    .setTags(accountData.getTags());
                accountRepository.save(account);
                return accountData;
            });
        } catch (ScopedWorkException ex) {
            throw ex.as(AccountNotFoundException.class);
        }
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull Integer accountId) {
        logger.debug("Getting all movemnts of account with ID: {}", accountId);
        getAccountByIdOrThrowNotFound(accountId,
                AccountApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
        final var accountMovements = movementRepository.findMovementsByAccountId(accountId)
            .stream()
            .map(this::mapMovementToMovementDTO)
            .sorted(Comparator.comparing(MovementDTO::getDate))
            .collect(Collectors.toList());
        logger.debug(l -> l.debug("Got {} movemnts of account with ID: {}", accountMovements.size(),
                accountId));
        logger.trace(l -> l.trace("Got movemnts of account with ID: {}. {}", accountId,
                accountMovements));
        return accountMovements;
    }

    private Account getAccountByIdOrThrowNotFound(@NotNull Integer accountId, ApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE,
                        accountId);
                final var exception = new AccountNotFoundException(error, errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                return exception;
            });
    }

    private MovementDTO mapMovementToMovementDTO(Movement movement) {
        return MovementDTOImpl.builder()
            .id(movement.getId())
            .date(movement.getDate())
            .amount(movement.getAmount())
            .build();
    }

    @Override
    public MovementSlicesInfoDTO getMovementSlicesByAccountId(@NotNull Integer accountId) {
        logger.debug("Getting all movemnt slices of account with id: {}", accountId);
        getAccountByIdOrThrowNotFound(accountId,
                AccountApiError.NO_ACCOUNT_ON_GET_MOVEMENT_SLICES_BY_ACCOUNT);
        Long movementCount = movementRepository.getMoventsCountByAccountId(accountId);
        Stream<MovementSliceDTO> movementSliceDTOStream = movementSliceRepository
            .getMovementSlicesByAccountId(accountId)
            .stream()
            .map(MOVEMENT_SLICE_TO_DTO_MAPPER);
        final var accountMovementSlices = Stream.concat(Stream.of(FIRST_MOVEMENT_SLICE_DTO),
                movementSliceDTOStream)
            .sorted(Comparator.comparing(MovementSliceDTO::getDate))
            .collect(Collectors.toList());
        logger.debug(l -> l.debug("Got {} movement slice(s) of account with ID: {}",
                accountMovementSlices.size(), accountId));
        logger.trace(l -> l.trace("Got movement slice(s) of account with ID: {}. {}", accountId,
                accountMovementSlices));
        return MovementSlicesInfoDTOImpl.builder()
            .accountId(accountId)
            .movementCount(movementCount)
            .movementSlices(accountMovementSlices)
            .build();
    }

    @Override
    public MovementSliceWithMovementsDTO getMovementsBySliceId(@NotNull Integer accountId,
            @NotNull Integer sliceId, @NotNull MovementOrder order) {
        logger.debug("Getting all movemnts in a slice with id: {} of an account with id: {}",
                sliceId, accountId);
        getAccountByIdOrThrowNotFound(accountId,
                AccountApiError.NO_ACCOUNT_ON_GET_MOVEMENTS_BY_SLICE);
        final var movementSliceDTO = getMovementSliceByIdAndAccountIdOrThrowNotFound(accountId,
                sliceId, AccountApiError.NO_MOVEMENT_SLICE_ON_GET_MOVEMENTS_BY_SLICE);
        final var movements = getMovementsByAccountIdAndSliceIdOverMovementOrderAndOrThrowNotFound(
                accountId, sliceId, order,
                AccountApiError.NO_MOVEMENT_SLICE_ON_GET_MOVEMENTS_BY_SLICE).stream()
                    .map(this::mapMovementToMovementDTO)
                    .collect(Collectors.toList());

        return MovementSliceWithMovementsDTOImpl.builder()
            .from((MovementSliceDataDTO) movementSliceDTO)
            .movementOrder(order)
            .movements(movements)
            .build();
    }

    private List<Movement> getMovementsByAccountIdAndSliceIdOverMovementOrderAndOrThrowNotFound(
            Integer accountId, Integer sliceId, @NotNull MovementOrder order,
            AccountApiError error) {
        try {
            Supplier<List<Movement>> movementsSupplier;
            if (order == MovementOrder.BOOKING_DATE) {
                movementsSupplier = ThrowingSupplier.unchecked(() -> accountMovementService
                    .findAllMovementsBySliceIdOverDate(accountId, sliceId));
            } else {
                movementsSupplier = ThrowingSupplier.unchecked(() -> accountMovementService
                    .findAllMovementsBySliceIdOverBookingDate(accountId, sliceId));
            }
            return movementsSupplier.get();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof MovementSliceNotFound) {
                final var errorMessage = MessageFormat.format(
                        "No movment slice is found for id: {0} in account with id: {1}.", sliceId,
                        accountId);
                final var exception = new MovementSliceNotFoundException(error, errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                throw exception;
            } else {
                throw e;
            }
        }
    }

    private MovementSliceDTO getMovementSliceByIdAndAccountIdOrThrowNotFound(Integer accountId,
            Integer sliceId, AccountApiError error) {
        if (MovementSlice.FIRST_SLICE_ID.equals(sliceId)) {
            return FIRST_MOVEMENT_SLICE_DTO;
        }
        return movementSliceRepository.find(sliceId)
            .filter(slice -> accountId.equals(slice.getAccount()
                .getId()))
            .map(MOVEMENT_SLICE_TO_DTO_MAPPER)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(
                        "No movment slice is found for id: {0} in account with id: {1}.", sliceId,
                        accountId);
                final var exception = new MovementSliceNotFoundException(error, errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                return exception;
            });
    }

}
