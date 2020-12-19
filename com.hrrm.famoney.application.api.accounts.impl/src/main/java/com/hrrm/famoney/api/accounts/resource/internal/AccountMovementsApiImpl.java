package com.hrrm.famoney.api.accounts.resource.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.immutables.value.Value;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.annotations.RequireEventAdmin;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;

import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.api.accounts.dto.EntryItemDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTOBuilder;
import com.hrrm.famoney.api.accounts.dto.impl.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.RefundDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.TransferDataDTOImpl;
import com.hrrm.famoney.api.accounts.events.MovementEventService;
import com.hrrm.famoney.api.accounts.events.MovementEventService.EventData;
import com.hrrm.famoney.api.accounts.events.impl.AddEventDataImpl;
import com.hrrm.famoney.api.accounts.events.impl.ChangeEventDataImpl;
import com.hrrm.famoney.api.accounts.internal.MovementApiService;
import com.hrrm.famoney.api.accounts.resource.AccountMovementsApi;
import com.hrrm.famoney.api.accounts.resource.internal.impl.MovementWihEventDataImpl;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.domain.accounts.movement.Entry;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Refund;
import com.hrrm.famoney.domain.accounts.movement.Transfer;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.OperationTimestampProvider;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = { AccountMovementsApi.class })
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
@RequireEventAdmin
public class AccountMovementsApiImpl implements AccountMovementsApi {

    private static final String NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE = "No movement info for id: {1} is found in account with id: {0}.";

    private final Logger logger;
    private final MovementRepository movementRepository;
    private final AccountsApiService accountsApiService;
    private final MovementApiService movementsApiService;
    private final TransactionControl txControl;
    private final MovementEventService movementEventService;
    private final OperationTimestampProvider operationTimestamProvider;

    @Context
    private HttpServletResponse httpServletResponse;
    @Context
    private UriInfo uriInfo;
    @Context
    private Sse sse;
    @Context
    private HttpHeaders httpHeaders;

    @Activate
    public AccountMovementsApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final MovementRepository movementRepository,
            @Reference final AccountsApiService accountsApiService,
            @Reference final MovementApiService movementsApiService,
            @Reference final TransactionControl txControl,
            @Reference final MovementEventService movementEventService,
            @Reference final OperationTimestampProvider operationTimestamProvider) {
        super();
        this.logger = logger;
        this.movementRepository = movementRepository;
        this.accountsApiService = accountsApiService;
        this.movementsApiService = movementsApiService;
        this.txControl = txControl;
        this.movementEventService = movementEventService;
        this.operationTimestamProvider = operationTimestamProvider;
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull final Integer accountId, final Integer offset,
        final Integer limit) {
        final var offsetOptional = Optional.ofNullable(offset);
        final var limitOptional = Optional.ofNullable(limit);
        logger.debug("Getting all movemnts of account with id: {}, offset: {} and count: {}.",
            accountId, offsetOptional.map(Object::toString)
                .orElse("\"from beginning\""), limitOptional.map(Object::toString)
                    .orElse("\"all\""));
        try {
            final var movements = txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                    AccountsApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
                operationTimestamProvider.setTimestamp();
                return movementRepository.getMovementsByAccountIdWithOffsetAndLimitOrderedByPos(
                    account, offset, limit);
            });
            List<MovementDTO> movementDTOs = movements.stream()
                .map(movement -> MovementDTOImpl.builder()
                    .id(movement.getId())
                    .data(convertMovement(movement))
                    .position(movement.getPosition())
                    .total(movement.getTotal())
                    .build())
                .collect(Collectors.toList());
            logger.debug("Got {} movemnts of account with ID: {}", movementDTOs.size(), accountId);
            logger.trace("Got movemnts of account with ID: {}. {}", accountId, movementDTOs);
            return movementDTOs;
        } catch (final ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    @Override
    public MovementDTO getMovement(@NotNull final Integer accountId,
        @NotNull final Integer movementId) {
        logger.debug("Geting movement info by id {} from account with id: {} with data: {}",
            movementId, accountId);
        try {
            final var movement = txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                    AccountsApiError.NO_ACCOUNT_ON_ADD_MOVEMENT);
                operationTimestamProvider.setTimestamp();
                return movementRepository.find(movementId)
                    .filter(m -> account.equals(m.getAccount()))
                    .orElseThrow(() -> {
                        final var errorMessage = MessageFormat.format(
                            NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE, account.getId(), movementId);
                        final var exception = new ApiException(
                                AccountsApiError.NO_MOVEMENT_ON_GET_MOVEMENT, errorMessage);
                        logger.warn(errorMessage);
                        logger.trace(errorMessage, exception);
                        return exception;
                    });
            });
            MovementDTO movementDTO = MovementDTOImpl.builder()
                .id(movement.getId())
                .data(convertMovement(movement))
                .build();
            logger.debug("Got movement info by id {} from account with id: {}.", movementId,
                accountId);
            logger.trace(l -> l.trace(
                "Got movement info by id {} from account with id: {} with data: {}", movementId,
                accountId, movementDTO));
            return movementDTO;
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    @Override
    public MovementDTO changeMovement(@NotNull final Integer accountId,
        @NotNull final Integer movementId, MovementDataDTO movementDataDTO) {
        logger.debug("Changing movement id: {} in account id: {}.", movementId, accountId);
        try {
            var movementDTO = txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                    AccountsApiError.NO_ACCOUNT_ON_CHANGE_MOVEMENT);
                operationTimestamProvider.setTimestamp();
                final var movementToChange = movementRepository.find(movementId)
                    .filter(movement -> account.equals(movement.getAccount()))
                    .orElseThrow(() -> {
                        final var errorMessage = MessageFormat.format(
                            NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE, account.getId(), movementId);
                        final var exception = new ApiException(
                                AccountsApiError.NO_MOVEMENT_ON_CHANGE_MOVEMENT, errorMessage);
                        logger.warn(errorMessage);
                        logger.trace(errorMessage, exception);
                        return exception;
                    });
                final var position = movementToChange.getPosition();
                final var resultMovement = movementsApiService.updateMovement(movementToChange,
                    movementDataDTO);
                final var positionAfter = resultMovement.getPosition();
                MovementDataDTO data = convertMovement(resultMovement);
                final var eventData = ChangeEventDataImpl.builder()
                    .accountId(accountId)
                    .position(position)
                    .positionAfter(positionAfter)
                    .build();
                movementEventService.putEvent(eventData);
                return MovementDTOImpl.builder()
                    .id(resultMovement.getId())
                    .data(data)
                    .position(positionAfter)
                    .total(data.getAmount())
                    .build();
            });
            logger.debug("A movement was added to account.");
            logger.trace("A movement was added to account by id : {}. A new id: {} was generated.",
                accountId, movementDTO.getId());
            return movementDTO;
        } catch (TransactionException e) {
            String message = "Problem during adding a movement to account.";
            logger.error(message, e);
            throw new ApiException(message);
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    @Override
    public MovementDTO addMovement(@NotNull final Integer accountId,
        final MovementDataDTO movementDataDTO) {
        logger.debug("Adding movement.");
        logger.trace("Adding movement to account id: {} with data: {}", accountId, movementDataDTO);
        try {
            var movementWithEventData = txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                    AccountsApiError.NO_ACCOUNT_ON_ADD_MOVEMENT);
                operationTimestamProvider.setTimestamp();
                final var resultMovement = movementsApiService.addMovement(account,
                    movementDataDTO);
                final var position = resultMovement.getPosition();
                final var eventData = AddEventDataImpl.builder()
                    .accountId(accountId)
                    .position(position)
                    .build();
                return MovementWihEventDataImpl.builder()
                    .movement(resultMovement)
                    .eventData(eventData)
                    .build();
            });
            httpServletResponse.setStatus(Status.CREATED.getStatusCode());
            final var eventData = movementWithEventData.getEventData();
            final var resultMovement = movementWithEventData.getMovement();
            final var location = uriInfo.getAbsolutePathBuilder()
                .path(AccountMovementsApi.class, "getMovement")
                .build(accountId, resultMovement.getId());
            httpServletResponse.setHeader(HttpHeaders.LOCATION, location.toString());
            MovementDataDTO data = convertMovement(resultMovement);
            movementEventService.putEvent(eventData);
            final var movementDTO = MovementDTOImpl.builder()
                .id(resultMovement.getId())
                .data(data)
                .position(resultMovement.getPosition())
                .total(data.getAmount())
                .build();
            logger.debug("A movement was added to account.");
            logger.trace("A movement was added to account id : {}. A new id: {} was generated.",
                accountId, movementDTO.getId());
            return movementDTO;
        } catch (TransactionException e) {
            String message = "Problem during adding a movement to account.";
            logger.error(message, e);
            throw new ApiException(message);
        } catch (ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    private MovementDataDTO convertMovement(final Movement movement) {
        return getMovementDataDTOBuilder(movement).date(movement.getDate())
            .bookingDate(Optional.ofNullable(movement.getBookingDate()))
            .budgetPeriod(Optional.ofNullable(movement.getBudgetPeriod()))
            .amount(movement.getAmount())
            .build();
    }

    private MovementDataDTOBuilder<? extends MovementDataDTO> getMovementDataDTOBuilder(
        final Movement movement) {
        if (movement instanceof Entry) {
            var entry = (Entry) movement;
            Iterable<EntryItemDataDTO> iterable = () -> entry.getEntryItems()
                .stream()
                .map(entryItem -> new EntryItemDataDTO.Builder().categoryId(entryItem
                    .getCategoryId())
                    .amount(entryItem.getAmount())
                    .comments(Optional.ofNullable(entryItem.getComments()))
                    .build())
                .iterator();
            return new EntryDataDTO.Builder().addAllEntryItems(iterable);
        } else if (movement instanceof Refund) {
            var refund = (Refund) movement;
            return new RefundDataDTOImpl.Builder().categoryId(refund.getCategoryId())
                .comments(refund.getComments());
        } else {
            var transfer = (Transfer) movement;
            return new TransferDataDTOImpl.Builder().oppositAccountId(transfer
                .getOppositAccountId());
        }
    }

    @Override
    public void deleteMovement(@NotNull Integer accountId, @NotNull Integer movementId) {
        // TODO Should be implemented.
        new UnsupportedOperationException("Not implemented yet");
    }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sendChangeMovement(@PathParam("accountId") final Integer accountId,
        @Context final SseEventSink sink) {
        final var eventBuilder = sse.newEventBuilder();
        movementEventService.registerEventListener(accountId)
            .map(changeMovementEvent -> eventBuilder.id(changeMovementEvent.getTimestamp()
                .toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(changeMovementEvent)
                .build())
            .forEach(t -> {
                sink.send(t);
            })
            .onResolve(sink::close);
    }

    @Value.Immutable
    @ImmutableDtoStyle
    public static interface MovementWihEventData {

        Movement getMovement();

        EventData getEventData();

    }

}
