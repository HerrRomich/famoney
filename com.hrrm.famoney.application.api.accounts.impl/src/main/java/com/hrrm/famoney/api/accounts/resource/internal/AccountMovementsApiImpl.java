package com.hrrm.famoney.api.accounts.resource.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
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
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.annotations.RequireEventAdmin;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.util.promise.Promises;

import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.api.accounts.dto.EntryItemDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTOBuilder;
import com.hrrm.famoney.api.accounts.dto.impl.MovementDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.RefundDataDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.TransferDataDTOImpl;
import com.hrrm.famoney.api.accounts.internal.AccountsApiService;
import com.hrrm.famoney.api.accounts.resource.AccountMovementsApi;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.api.accounts.sse.impl.ChangeMovementEventDTOImpl;
import com.hrrm.famoney.application.service.accounts.MovementsService;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Entry;
import com.hrrm.famoney.domain.accounts.movement.EntryItem;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Refund;
import com.hrrm.famoney.domain.accounts.movement.Transfer;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = {
        AccountMovementsApi.class,
        EventHandler.class
})
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
@EventTopics({
        AccountMovementsApiImpl.MOVEMENTS_CHANGE_TOPIC
})
@RequireEventAdmin
public class AccountMovementsApiImpl implements AccountMovementsApi, EventHandler {

    public static final String MOVEMENTS_CHANGE_TOPIC = "com/hrrm/famoney/event/accounts/movements";

    private static final String NO_ACCOUNT_MOVEMENT_IS_FOUND_MESSAGE = "No movement info for id: {1} is found in account with id: {0}.";

    private final Logger logger;
    private final MovementRepository movementRepository;
    private final MovementsService movementsService;
    private final TransactionControl txControl;
    private final EventAdmin eventAdmin;
    private final AccountsApiService accountsApiService;

    @Context
    private HttpServletResponse httpServletResponse;
    @Context
    private UriInfo uriInfo;
    @Context
    private Sse sse;

    private final Map<Integer, AccountBroadcaster> accountsBroadcasters = new ConcurrentHashMap<>();

    @Activate
    public AccountMovementsApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final MovementRepository movementRepository, @Reference final MovementsService movementsService,
            @Reference final TransactionControl txControl, @Reference final EventAdmin eventAdmin,
            @Reference final AccountsApiService accountsApiService) {
        super();
        this.logger = logger;
        this.movementRepository = movementRepository;
        this.movementsService = movementsService;
        this.txControl = txControl;
        this.eventAdmin = eventAdmin;
        this.accountsApiService = accountsApiService;
    }

    @Deactivate
    public void deactivate() {
        accountsBroadcasters.forEach((key, value) -> value.broadcaster.close());
    }

    @Override
    public List<MovementDTO> getMovements(@NotNull final Integer accountId, final Integer offset, final Integer limit) {
        final var offsetOptional = Optional.ofNullable(offset);
        final var limitOptional = Optional.ofNullable(limit);
        logger.debug("Getting all movemnts of account with id: {}, offset: {} and count: {}.",
                accountId,
                offsetOptional.map(Object::toString)
                    .orElse("\"from beginning\""),
                limitOptional.map(Object::toString)
                    .orElse("\"all\""));
        try {
            return txControl.required(() -> {
                accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                        AccountsApiError.NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS);
                final var movements = movementRepository.getMovementsByAccountIdOffsetAndLimit(accountId,
                        offsetOptional,
                        limitOptional);
                List<MovementDTO> movementDTOs = movements.stream()
                    .map(movement -> MovementDTOImpl.builder()
                        .id(movement.getId())
                        .data(AccountMovementsApiImpl.this.convertMovement(movement))
                        .total(movement.getTotal())
                        .build())
                    .collect(Collectors.toList());
                logger.debug("Got {} movemnts of account with ID: {}",
                        movementDTOs.size(),
                        accountId);
                logger.trace("Got movemnts of account with ID: {}. {}",
                        accountId,
                        movementDTOs);
                return movementDTOs;
            });
        } catch (final ScopedWorkException e) {
            throw e.as(ApiException.class);
        }
    }

    @Override
    public MovementDTO getMovement(@NotNull Integer accountId, @NotNull Integer movementId) {
        logger.debug("Getting movement info.");
        logger.trace("Geting movement info by id {} from account with id: {} with data: {}",
                movementId,
                accountId);
        try {
            return txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
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
    public MovementDTO addMovement(@NotNull Integer accountId, MovementDataDTO movementDataDTO) {
        logger.debug("Adding movement");
        logger.trace("Adding movement to account id: {} with data: {}",
                accountId,
                movementDataDTO);
        try {
            var movementDTO = txControl.required(() -> {
                final var account = accountsApiService.getAccountByIdOrThrowNotFound(accountId,
                        AccountsApiError.NO_ACCOUNT_ON_ADD_MOVEMENT);
                Movement movement = null;
                if (movementDataDTO instanceof EntryDataDTO) {
                    movement = createEntryMovement(account,
                            movementDataDTO);
                }
                final var resultMovement = movementsService.addMovement(movement);
                httpServletResponse.setStatus(Status.CREATED.getStatusCode());
                final var location = uriInfo.getAbsolutePathBuilder()
                    .path(AccountMovementsApi.class,
                            "getMovement")
                    .build(account.getId(),
                            resultMovement.getId());
                httpServletResponse.setHeader(HttpHeaders.LOCATION,
                        location.toString());
                MovementDataDTO data = convertMovement(movement);
                return MovementDTOImpl.builder()
                    .id(movement.getId())
                    .data(data)
                    .total(data.getAmount())
                    .build();
            });
            logger.debug("A movement was added to account.");
            logger.trace("A movement was added to account by id : {}. A new id: {} was generated.",
                    accountId,
                    movementDTO.getId());
            final var changeEvnetProperties = new HashMap<String, Object>();
            changeEvnetProperties.put("com.hrrm.famoney.event.acconts.id",
                    accountId);
            changeEvnetProperties.put("com.hrrm.famoney.event.acconts.movements.id",
                    movementDTO.getId());
            eventAdmin.postEvent(new Event(MOVEMENTS_CHANGE_TOPIC,
                changeEvnetProperties));
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
                .atTime(LocalTime.now()))
            .setBookingDate(entryDataDTO.getBookingDate()
                .map(bookingDate -> bookingDate.atTime(LocalTime.now()))
                .orElse(null))
            .setBudgetPeriod(entryDataDTO.getBudgetPeriod()
                .orElse(null))
            .setTotal(BigDecimal.ZERO);
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

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sendChangeMovement(@PathParam("accountId") Integer accountId, @Context SseEventSink sink) {
        accountsBroadcasters.computeIfAbsent(accountId,
                k -> new AccountBroadcaster(accountId))
            .register(sink);
        Promises.resolved("Roman")
            .delay(10000)
            .onSuccess(t -> {
                sink.send(sse.newEvent("Test",
                        t));
            })
            .thenAccept(f -> {
                sink.send(sse.newEvent("Quest",
                        f));
            });
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
        case MOVEMENTS_CHANGE_TOPIC:
            publishMovementChange(event);
            break;
        }
    }

    private void publishMovementChange(Event event) {
        Integer accountId = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.id");
        Integer movementId = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.movements.id");
        accountsBroadcasters.compute(accountId,
                (key, value) -> {
                    value.broadcast(sse.newEventBuilder()
                        .data(ChangeMovementEventDTOImpl.builder()
                            .accountId(accountId)
                            .movementId(movementId)
                            .build())
                        .build());
                    return value;
                });
    }

    private final class AccountBroadcaster {
        private final Integer accountId;
        private final SseBroadcaster broadcaster;
        private Long count = 0l;

        public AccountBroadcaster(Integer accountId) {
            super();
            this.accountId = accountId;
            broadcaster = AccountMovementsApiImpl.this.sse.newBroadcaster();
            broadcaster.onClose(this::closeSink);
        }

        public synchronized void register(SseEventSink sseEventSink) {
            broadcaster.register(sseEventSink);
            count++;
        }

        public synchronized CompletionStage<?> broadcast(OutboundSseEvent event) {
            return broadcaster.broadcast(event);
        }

        private synchronized void closeSink(SseEventSink sseEventSink) {
            count--;
            if (count == 0) {
                broadcaster.close();
                AccountMovementsApiImpl.this.accountsBroadcasters.remove(accountId);
            }
        }

    }

}
