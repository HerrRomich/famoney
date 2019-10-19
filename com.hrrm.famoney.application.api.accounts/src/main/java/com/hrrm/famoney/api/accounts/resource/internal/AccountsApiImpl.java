package com.hrrm.famoney.api.accounts.resource.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceWithMovementsDTO;
import com.hrrm.famoney.api.accounts.dto.internal.AccountDTOImpl;
import com.hrrm.famoney.api.accounts.dto.internal.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.dto.internal.MovementSliceDTOImpl;
import com.hrrm.famoney.api.accounts.dto.internal.MovementSliceWithMovementsDTOImpl;
import com.hrrm.famoney.api.accounts.resource.AccountNotFoundException;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = AccountsApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi {

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";
    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementSliceRepository movementSliceRepository;
    private final TransactionControl txControl;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) Logger logger, @Reference ModelMapper modelMapper,
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
        logger.debug(l -> l.debug("Getting all accounts by {} tasg(s).", tags.size()));
        logger.trace(l -> l.trace("Getting all accounts by tasg(s): {}.", tags));

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
            logger.trace("Got accounts: {}", result);
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
            .name(account.getName())
            .tags(account.getTags())
            .build();
    }

    @Override
    public List<String> getAllAccountTags() {
        return accountRepository.findAllTags();
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull Integer accountId) {
        logger.debug("Getting all movemnts of account with ID: {}", accountId);
        if (!accountRepository.find(accountId)
            .isPresent()) {
            final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE, accountId);
            final var exception = new AccountNotFoundException(1001, errorMessage,
                    "No account was found for request of all account movements.");
            logger.warn(errorMessage);
            logger.trace(errorMessage, exception);
            throw exception;
        }
        final var accountMovements = movementRepository.findMovementsByAccountId(accountId)
            .stream()
            .map(this::mapMovementToMovementDTO)
            .collect(Collectors.toList());
        logger.debug(l -> l.debug("Got {} movemnts of account with ID: {}", accountMovements.size(), accountId));
        logger.trace(l -> l.debug("Got movemnts of account with ID: {}. {}", accountId, accountMovements));
        return accountMovements;
    }

    private MovementDTO mapMovementToMovementDTO(Movement movement) {
        return MovementDTOImpl.builder()
            .id(movement.getId())
            .date(movement.getDate())
            .amount(movement.getAmount())
            .build();
    }

    @Override
    public List<MovementSliceDTO> getMovementSlicesByAccountId(@NotNull Integer accountId) {
        logger.debug("Getting all movemnt slices of account with id: {}", accountId);
        if (!accountRepository.find(accountId)
            .isPresent()) {
            final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE, accountId);
            final var exception = new AccountNotFoundException(1002, errorMessage,
                    "No account was found for request of all account slices.");
            logger.warn(errorMessage);
            logger.trace(errorMessage, exception);
            throw exception;
        }
        final var accountMovementSlices = movementSliceRepository.getMovementSlicesByAccountId(accountId)
            .stream()
            .<MovementSliceDTO>map(slice -> mapMovmentSliceToMovementSliceDTO(MovementSliceDTOImpl.builder(), slice)
                .build())
            .collect(Collectors.toList());
        logger.debug(l -> l.debug("Got {} movemnt slices of account with ID: {}", accountMovementSlices.size(),
                                  accountId));
        logger.trace(l -> l.debug("Got movemnt slices of account with ID: {}. {}", accountId, accountMovementSlices));
        return accountMovementSlices;
    }

    private <T extends MovementSliceDTOImpl.MovementSliceDTOImplBuilder> T mapMovmentSliceToMovementSliceDTO(T builder,
            MovementSlice movementSlice) {
        return (T) builder.id(movementSlice.getId())
            .date(movementSlice.getDate())
            .sum(movementSlice.getSum())
            .count(movementSlice.getCount());
    }

    @Override
    public MovementSliceWithMovementsDTO getMovementsBySliceId(@NotNull Integer accountId, @NotNull Integer sliceId) {
        logger.debug("Getting all movemnts in a slice with id: {} of an account with id: {}", sliceId, accountId);
        if (!accountRepository.find(accountId)
            .isPresent()) {
            final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE, accountId);
            final var exception = new AccountNotFoundException(1003, errorMessage,
                    "No account was found for request of all movements in a specified account.");
            logger.warn(errorMessage);
            logger.trace(errorMessage, exception);
            throw exception;
        }
        final var movementSlice = movementSliceRepository.find(sliceId)
            .filter(slice -> slice.getAccount()
                .getId() == accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(
                                                              "No movment slice is found for id: {0} in account with id: {1}.",
                                                              sliceId, accountId);
                final var exception = new AccountNotFoundException(1004, errorMessage,
                        "No movement slice was found for request of all movements in a specified slice of specified account.");
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                return exception;
            });
        final var movements = movementRepository.findAllMovementsBySliceId(sliceId)
            .stream()
            .map(this::mapMovementToMovementDTO)
            .collect(Collectors.toList());
        return MovementSliceWithMovementsDTOImpl.builder()
            .id(movementSlice.getId())
            .date(movementSlice.getDate())
            .count(movementSlice.getCount())
            .sum(movementSlice.getSum())
            .movements(movements)
            .build();
    }

}
