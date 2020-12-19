package com.hrrm.famoney.api.accounts.events;

import java.time.LocalDateTime;
import java.util.HashMap;

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
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtensionSelect;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

import com.hrrm.famoney.api.accounts.events.dto.MovementEventDTO;
import com.hrrm.famoney.api.accounts.events.dto.impl.MovementChangeEventDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.OperationTimestampProvider;

@Component(service = { MovementEventService.class, EventHandler.class })
@JaxrsExtension
@JaxrsExtensionSelect("(osgi.jaxrs.name=configProvider)")
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@EventTopics({ MovementEventService.MOVEMENTS_TOPIC
        + "/*" })
@RequireEventAdmin
public class MovementEventServiceImpl implements MovementEventService, EventHandler {

    private final Logger logger;
    private final OperationTimestampProvider operationTimestamProvider;
    private final EventAdmin eventAdmin;

    private final PushStreamProvider pushStreamProvider;
    private final SimplePushEventSource<MovementEventDTO> movementEvents;

    @Activate
    public MovementEventServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final OperationTimestampProvider operationTimestamProvider,
            @Reference EventAdmin eventAdmin) {
        super();
        this.logger = logger;
        this.operationTimestamProvider = operationTimestamProvider;
        this.eventAdmin = eventAdmin;
        pushStreamProvider = new PushStreamProvider();
        movementEvents = pushStreamProvider.createSimpleEventSource(MovementEventDTO.class);
    }

    @Deactivate
    public void deactivate() {
        movementEvents.close();
    }

    @Override
    public PushStream<MovementEventDTO> registerEventListener(Integer accountId) {
        return this.pushStreamProvider.createStream(movementEvents)
            .filter(event -> {
                return event.getAccountId()
                    .equals(accountId);
            });
    }

    
    
    @Override
    public void putEvent(EventData eventData) {
        if (eventData instanceof AddEventData) {
            putAddEvent((AddEventData)eventData);
        } else if (eventData instanceof ChangeEventData) {
            putChangeEvent((ChangeEventData)eventData);
        } else if (eventData instanceof DeleteEventData) {
            putDeleteEvent((DeleteEventData)eventData);
        }
    }

    private void putAddEvent(AddEventData eventData) {
        logger.debug("An event for movement adding will be put.");
        logger.trace(
            "An event for movement adding to account id: {} into position: {} will be put.",
            eventData.getAccountId(), eventData.getPosition());
        final var evnetProperties = new HashMap<String, Object>();
        evnetProperties.put("com.hrrm.famoney.event.acconts.id", eventData.getAccountId());
        evnetProperties.put("com.hrrm.famoney.event.acconts.movements.position", eventData
            .getPosition());
        evnetProperties.put("com.hrrm.famoney.event.timestamp", operationTimestamProvider
            .getTimestamp());
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_ADD_TOPIC, evnetProperties));
        logger.debug("An event for movement adding is successfully put.");
        logger.trace(
            "An event for movement adding to account id: {} into position: {} is successfully put.",
            eventData.getAccountId(), eventData.getPosition());
    }

    private void putChangeEvent(ChangeEventData eventData) {
        logger.debug("An event for movement changing will be put.");
        logger.trace(
            "An event for movement changing in account id: {} from position: {} to position: {} will be put.",
            eventData.getAccountId(), eventData.getPosition(), eventData.getPositionAfter());
        final var evnetProperties = new HashMap<String, Object>();
        evnetProperties.put("com.hrrm.famoney.event.acconts.id", eventData.getAccountId());
        evnetProperties.put("com.hrrm.famoney.event.acconts.movements.position", eventData
            .getPosition());
        evnetProperties.put("com.hrrm.famoney.event.acconts.movements.positionAfter", eventData
            .getPositionAfter());
        evnetProperties.put("com.hrrm.famoney.event.timestamp", operationTimestamProvider
            .getTimestamp());
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_CHANGE_TOPIC,
                evnetProperties));
        logger.debug("An event for movement changing is successfully put.");
        logger.trace(
            "An event for movement changing in account id: {} from position: {} to position: {}"
                    + " is successfully put.", eventData.getAccountId(), eventData.getPosition(),
            eventData.getPositionAfter());
    }

    private void putDeleteEvent(DeleteEventData eventData) {
        logger.debug("An event for movement deletion will be put.");
        logger.trace(
            "An event for movement deletion in account id: {} from position: {} will be put.",
            eventData.getAccountId(), eventData.getPosition());
        final var evnetProperties = new HashMap<String, Object>();
        evnetProperties.put("com.hrrm.famoney.event.acconts.id", eventData.getAccountId());
        evnetProperties.put("com.hrrm.famoney.event.acconts.movements.position", eventData
            .getPosition());
        evnetProperties.put("com.hrrm.famoney.event.timestamp", operationTimestamProvider
            .getTimestamp());
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_DELETE_TOPIC,
                evnetProperties));
        logger.debug("An event for movement deletion is successfully put.");
        logger.trace(
            "An event for movement deletion in account id: {} from position: {} is successfully put.",
            eventData.getAccountId(), eventData.getPosition());
    }

    @Override
    public void handleEvent(final Event event) {
        switch (event.getTopic()) {
        case MovementEventService.MOVEMENTS_ADD_TOPIC:
            publishMovementAdd(event);
            break;
        case MovementEventService.MOVEMENTS_CHANGE_TOPIC:
            publishMovementChange(event);
            break;
        case MovementEventService.MOVEMENTS_DELETE_TOPIC:
            publishMovementDelete(event);
            break;
        }
    }

    private void publishMovementAdd(Event event) {
        // TODO Auto-generated method stub

    }

    private void publishMovementChange(final Event event) {
        logger.debug("Movement change event triggered.");
        final var timestamp = (LocalDateTime) event.getProperty("com.hrrm.famoney.event.timestamp");
        final var accountId = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.id");
        final var position = (Integer) event.getProperty(
            "com.hrrm.famoney.event.acconts.movements.position");
        final var positionAfter = (Integer) event.getProperty(
            "com.hrrm.famoney.event.acconts.movements.positionAfter");
        this.movementEvents.publish(MovementChangeEventDTOImpl.builder()
            .timestamp(timestamp)
            .accountId(accountId)
            .position(position)
            .positionAfter(positionAfter)
            .build());
        logger.debug("Movement change event successfully published.");
    }

    private void publishMovementDelete(Event event) {
        // TODO Auto-generated method stub

    }

}
