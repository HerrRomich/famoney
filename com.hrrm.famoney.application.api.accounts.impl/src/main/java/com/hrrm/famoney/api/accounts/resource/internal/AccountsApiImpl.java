package com.hrrm.famoney.api.accounts.resource.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

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
import com.hrrm.famoney.application.service.accounts.MovementsProcesor;
import com.hrrm.famoney.application.service.accounts.dataobject.AccountMovementsRequest;
import com.hrrm.famoney.application.service.accounts.dataobject.impl.AccountMovementsRequestImpl;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
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
        private final List<MovementDTO> movementDTOs;

        public MovementDTOCollector(final BigDecimal sum) {
            super();
            this.sum = sum;
            movementDTOs = new ArrayList<>();
        }

        public void addNextMovement(final Movement movement) {
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
    private final MovementsProcesor movementsProcessorByBookingDate;
    private final MovementsProcesor movementsProcessorByMovementDate;
    private final TransactionControl txControl;

    @Context
    private HttpServletResponse httpServletResponse;
    @Context
    private UriInfo uriInfo;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository, @Reference(
                    target = "(component.name=MovementsProcessorByMovementDate)") final MovementsProcesor movementsProcessorByBookingDate,
            @Reference(
                    target = "(component.name=MovementsProcessorByMovementDate)") final MovementsProcesor movementsProcessorByMovementDate,
            @Reference final TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementsProcessorByBookingDate = movementsProcessorByBookingDate;
        this.movementsProcessorByMovementDate = movementsProcessorByMovementDate;
        this.txControl = txControl;
    }

    @Override
    public List<AccountDTO> getAllAccounts(final Set<String> tags) {
        logger.debug(l -> l.debug("Getting all accounts by {} tag(s).",
                tags.size()));
        logger.trace(l -> l.trace("Getting all accounts by tag(s): {}.",
                tags));

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
            logger.debug("Got {} accounts.",
                    result.size());
            logger.trace(l -> l.trace("Got accounts: {}",
                    result));
            return result;
        });
    }

    private Predicate<Account> createTagPredicate(final Set<String> t) {
        return account -> !Sets.intersection(account.getTags(),
                t)
            .isEmpty();
    }

    private AccountDTO mapAccountToAccountDTO(final Account account) {
        return AccountDTOImpl.builder()
            .id(account.getId())
            .from(mapAccountToAccountDataDTO(account))
            .movementCount(account.getMovementCount())
            .sum(account.getMovementSum())
            .build();
    }

    private AccountDataDTO mapAccountToAccountDataDTO(final Account account) {
        return AccountDataDTOImpl.builder()
            .name(account.getName())
            .openDate(account.getOpenDate())
            .tags(account.getTags())
            .build();
    }

    @Override
    public void addAccount(@NotNull final AccountDataDTO accountData) {
        logger.info("Creating new account.");
        logger.debug("Creating new account with name: {}.",
                accountData.getName());
        final var accountId = txControl.required(() -> {
            final var account = new Account().setName(accountData.getName())
                .setOpenDate(accountData.getOpenDate())
                .setTags(accountData.getTags());
            return accountRepository.save(account)
                .getId();
        });
        final var location = uriInfo.getAbsolutePathBuilder()
            .path(AccountsApi.class,
                    "getAccount")
            .build(accountId);
        httpServletResponse.addHeader(HttpHeaders.LOCATION,
                location.toString());
        logger.info("New account is successfully created.");
        logger.debug("New account is successfully created with id: {}.",
                accountId);
    }

    @Override
    public AccountDataDTO changeAccount(@NotNull final Integer id, @NotNull final AccountDataDTO accountData) {
        try {
            return txControl.required(() -> {
                final var account = getAccountByIdOrThrowNotFound(id,
                        AccountApiError.NO_ACCOUNT_BY_CHANGE);
                account.setName(accountData.getName())
                    .setOpenDate(accountData.getOpenDate())
                    .setTags(accountData.getTags());
                accountRepository.save(account);
                return accountData;
            });
        } catch (final ScopedWorkException ex) {
            throw ex.as(ApiException.class);
        }
    }

    @Override
    public AccountDTO getAccount(@NotNull final Integer id) {
        logger.debug("Getting account info with ID: {}",
                id);
        final var account = getAccountByIdOrThrowNotFound(id,
                AccountApiError.NO_ACCOUNT_ON_GET_ACCOUNT);
        logger.debug("Got account info with ID: {}",
                account.getId());
        return mapAccountToAccountDTO(account);
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull final Integer id, final Integer offset, final Integer limit,
            final MovementOrder order) {
        final AccountMovementsRequest accountMovementsRequest = AccountMovementsRequestImpl.builder()
            .accountId(id)
            .offset(offset)
            .limit(limit)
            .build();

        final var orderdByText = order == MovementOrder.BOOKING_DATE ? "booking date" : "movement date";
        logger.debug("Getting all movemnts of account with id: {}, offset: {} and count: {}," + " ordered by {}",
                id,
                accountMovementsRequest.getOffset()
                    .map(Object::toString)
                    .orElse("\"from beginning\""),
                accountMovementsRequest.getLimit()
                    .map(Object::toString)
                    .orElse("\"all\""),
                orderdByText);
        getAccountByIdOrThrowNotFound(id,
                AccountApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
        final var movementProcessor = order == MovementOrder.BOOKING_DATE ? movementsProcessorByBookingDate
                : movementsProcessorByMovementDate;
        try {
            final var movementDTOs = txControl.required(() -> {
                final var accountMovementsResponse = movementProcessor.getMovementsSlice(accountMovementsRequest);
                final var movementDTOCollector = new MovementDTOCollector(accountMovementsResponse.getStartSum());
                accountMovementsResponse.getMovements()
                    .stream()
                    .forEachOrdered(movementDTOCollector::addNextMovement);
                return movementDTOCollector.getMovements();
            });
            logger.debug(l -> l.debug("Got {} movemnts of account with ID: {}",
                    movementDTOs.size(),
                    id));
            logger.trace(l -> l.trace("Got movemnts of account with ID: {}. {}",
                    id,
                    movementDTOs));
            return movementDTOs;
        } catch (final ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    private Account getAccountByIdOrThrowNotFound(@NotNull final Integer accountId, final ApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE,
                        accountId);
                final var exception = new AccountNotFoundException(error,
                    errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage,
                        exception);
                return exception;
            });
    }

}
