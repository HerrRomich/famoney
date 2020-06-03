package com.hrrm.famoney.api.accounts.resource.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javax.ws.rs.core.Response.Status;
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
import org.osgi.service.transaction.control.TransactionException;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountDataDTO;
import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.api.accounts.dto.EntryItemDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTOBuilder;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.RefundDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.TransferDataDTOImpl;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.application.service.accounts.MovementsProcesor;
import com.hrrm.famoney.application.service.accounts.MovementsService;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Entry;
import com.hrrm.famoney.domain.accounts.movement.EntryItem;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Refund;
import com.hrrm.famoney.domain.accounts.movement.Transfer;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = AccountsApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi {

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";

    private static final String NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE = "No movement info for id: {1} is found in account with id: {0}.";

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementsProcesor movementsProcessor;
    private final MovementsService movementsService;
    private final TransactionControl txControl;

    @Context
    private HttpServletResponse httpServletResponse;
    @Context
    private UriInfo uriInfo;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository,
            @Reference final MovementRepository movementRepository,
            @Reference final MovementsProcesor movementsProcessor, @Reference final MovementsService movementsService,
            @Reference final TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.movementsProcessor = movementsProcessor;
        this.movementsService = movementsService;
        this.txControl = txControl;
    }

    @Override
    public List<AccountDTO> getAllAccounts(final Set<String> tags) {
        logger.debug("Getting all accounts by {} tag(s).",
                tags.size());
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
                        AccountsApiError.NO_ACCOUNT_BY_CHANGE);
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
                AccountsApiError.NO_ACCOUNT_ON_GET_ACCOUNT);
        logger.debug("Got account info with ID: {}",
                account.getId());
        return mapAccountToAccountDTO(account);
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull final Integer id, final Integer offset, final Integer limit) {
        final var accountMovementsRequest = MovementsProcesor.AccountMovementsRequestByOffsetAndLimit.builder()
            .accountId(id)
            .offset(offset)
            .limit(limit)
            .build();

        logger.debug("Getting all movemnts of account with id: {}, offset: {} and count: {}.",
                id,
                accountMovementsRequest.getOffset()
                    .map(Object::toString)
                    .orElse("\"from beginning\""),
                accountMovementsRequest.getLimit()
                    .map(Object::toString)
                    .orElse("\"all\""));
        getAccountByIdOrThrowNotFound(id,
                AccountsApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
        try {
            final var movementDTOs = txControl.required(() -> {
                final var accountMovementsResponse = movementsProcessor.getMovementsByOffsetAndLimit(accountMovementsRequest);
                final var movementDTOCollector = new MovementDTOCollector(accountMovementsResponse.getStartSum());
                accountMovementsResponse.getMovements()
                    .stream()
                    .forEachOrdered(movementDTOCollector::addNextMovement);
                return movementDTOCollector.getMovements();
            });
            logger.debug("Got {} movemnts of account with ID: {}",
                    movementDTOs.size(),
                    id);
            logger.trace("Got movemnts of account with ID: {}. {}",
                    id,
                    movementDTOs);
            return movementDTOs;
        } catch (final ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    private Account getAccountByIdOrThrowNotFound(@NotNull final Integer accountId, final AccountsApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE,
                        accountId);
                final var exception = new ApiException(error,
                    errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage,
                        exception);
                return exception;
            });
    }

    @Override
    public MovementDTO getMovement(@NotNull Integer id, @NotNull Integer movementId) {
        logger.debug("Getting movement info.");
        logger.trace("Geting movement info by id {} from account with id: {} with data: {}",
                movementId,
                id);
        try {
            return txControl.required(() -> {
                final var account = getAccountByIdOrThrowNotFound(id,
                        AccountsApiError.NO_ACCOUNT_ON_ADD_MOVEMENT);
                return movementRepository.find(movementId)
                    .map(movement -> MovementDTOImpl.builder()
                        .id(movement.getId())
                        .data(convertMovement(movement))
                        .build())
                    .orElseThrow(() -> {
                        final var errorMessage = MessageFormat.format(NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE,
                                account.getId(),
                                movementId);
                        final var exception = new ApiException(AccountsApiError.NO_MOVEMENT_ON_GET_MOVEMENT,
                            errorMessage);
                        logger.warn(errorMessage);
                        logger.trace(errorMessage,
                                exception);
                        return exception;
                    });
            });
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    @Override
    public MovementDTO addMovement(@NotNull Integer id, MovementDataDTO movementDataDTO) {
        logger.debug("Adding movement");
        logger.trace("Adding movement to account id: {} with data: {}",
                id,
                movementDataDTO);
        try {
            var movementDTO = txControl.required(() -> {
                final var account = getAccountByIdOrThrowNotFound(id,
                        AccountsApiError.NO_ACCOUNT_ON_ADD_MOVEMENT);
                Movement movement = null;
                if (movementDataDTO instanceof EntryDataDTO) {
                    movement = createEntryMovement(account,
                            movementDataDTO);
                }
                final var resultMovement = movementsService.addMovement(movement);
                httpServletResponse.setStatus(Status.CREATED.getStatusCode());
                final var location = uriInfo.getAbsolutePathBuilder()
                    .path(AccountsApi.class,
                            "getMovement")
                    .build(account.getId(),
                            resultMovement.getId());
                httpServletResponse.setHeader(HttpHeaders.LOCATION,
                        location.toString());
                MovementDataDTO data = convertMovement(movement);
                return MovementDTOImpl.builder()
                    .id(movement.getId())
                    .data(data)
                    .sum(data.getAmount())
                    .build();
            });
            logger.debug("A movement was added to account.");
            logger.trace("A movement was added to account by id : {}. A new id: {} was generated.",
                    id,
                    movementDTO.getId());
            return movementDTO;
        } catch (TransactionException e) {
            String message = "Problem during adding a movement to account.";
            logger.error(message,
                    e);
            throw new ApiException(message);
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    private Movement createEntryMovement(final Account account, MovementDataDTO movementDataDTO) {
        Movement movement;
        final var entryDataDTO = (EntryDataDTO) movementDataDTO;
        movement = new Entry().setEntryItems(entryDataDTO.getEntryItems()
            .stream()
            .map(entryItemDataDTO -> new EntryItem().setCategoryId(entryItemDataDTO.getCategoryId())
                .setAmount(entryItemDataDTO.getAmount())
                .setComments(entryItemDataDTO.getComments()))
            .collect(Collectors.toList()))
            .setAccount(account)
            .setAmount(entryDataDTO.getAmount())
            .setDate(entryDataDTO.getDate()
                .atTime(LocalTime.of(12,
                        0)))
            .setBookingDate(entryDataDTO.getBookingDate()
                .map(bookingDate -> bookingDate.atTime(LocalTime.of(12,
                        0)))
                .orElse(null))
            .setBudgetPeriod(entryDataDTO.getBudgetPeriod()
                .orElse(null));
        return movement;
    }

    private MovementDataDTO convertMovement(Movement movement) {
        return getMovementDataDTOBuilder(movement).date(movement.getDate()
            .toLocalDate())
            .bookingDate(Optional.ofNullable(movement.getBookingDate())
                .map(LocalDateTime::toLocalDate))
            .budgetPeriod(Optional.ofNullable(movement.getBudgetPeriod()))
            .amount(movement.getAmount())
            .build();
    }

    private MovementDataDTOBuilder<? extends MovementDataDTO> getMovementDataDTOBuilder(Movement movement) {
        if (movement instanceof Entry) {
            var entry = (Entry) movement;
            Iterable<EntryItemDataDTO> iterable = () -> entry.getEntryItems()
                .stream()
                .map(entryItem -> new EntryItemDataDTO.Builder().categoryId(entryItem.getCategoryId())
                    .amount(entryItem.getAmount())
                    .comments(entryItem.getComments())
                    .build())
                .iterator();
            return new EntryDataDTO.Builder().addAllEntryItems(iterable);
        } else if (movement instanceof Refund) {
            var refund = (Refund) movement;
            return new RefundDataDTOImpl.Builder().categoryId(refund.getCategoryId())
                .comments(refund.getComments());
        } else {
            var transfer = (Transfer) movement;
            return new TransferDataDTOImpl.Builder().oppositAccountId(transfer.getOppositAccountId());
        }
    }

    private class MovementDTOCollector {
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
                .data(AccountsApiImpl.this.convertMovement(movement))
                .build();
            movementDTOs.add(movementDTO);
        }

        public List<MovementDTO> getMovements() {
            return Collections.unmodifiableList(movementDTOs);
        }

    }

}
