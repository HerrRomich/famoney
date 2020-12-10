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

@Component(service = {
        MovementEventService.class,
        EventHandler.class
})
@JaxrsExtension
@JaxrsExtensionSelect("(osgi.jaxrs.name=configProvider)")
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@EventTopics({
        MovementEventService.MOVEMENTS_TOPIC + "/*"
})
@RequireEventAdmin
public class MovementEventServiceImpl implements MovementEventService, EventHandler {

    private final Logger logger;
    private final EventAdmin eventAdmin;

    private final PushStreamProvider pushStreamProvider;
    private final SimplePushEventSource<MovementEventDTO> movementEvents;

    @Activate
    public MovementEventServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference EventAdmin eventAdmin) {
        super();
        this.logger = logger;
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
        return this.pushStreamProvider.createStream(movementEvents).filter(event -> {
            return event.getAccountId().equals(accountId);
        });
    }

    @Override
    public void putAddEvent(Integer accountId, Integer position, LocalDateTime timestamp) {
        logger.debug("An event for movement adding will be put.");
        logger.trace("An event for movement adding to account id: {} into position: {} will be put.", accountId,
                position);
        final var addEvnetProperties = new HashMap<String, Object>();
        addEvnetProperties.put("com.hrrm.famoney.event.acconts.id", accountId);
        addEvnetProperties.put("com.hrrm.famoney.event.acconts.movements.position", position);
        addEvnetProperties.put("com.hrrm.famoney.event.timestamp", timestamp);
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_ADD_TOPIC,
            addEvnetProperties));
        logger.debug("An event for movement adding is successfully put.");
        logger.trace("An event for movement adding to account id: {} into position: {} is successfully put.", accountId,
                position);
    }

    @Override
    public void putChangeEvent(final Integer accountId, Integer position, Integer positionAfter,
            LocalDateTime timestamp) {
        logger.debug("An event for movement changing will be put.");
        logger.trace("An event for movement changing in account id: {} from position: {} to position: {} will be put.",
                accountId, position, positionAfter);
        final var changeEvnetProperties = new HashMap<String, Object>();
        changeEvnetProperties.put("com.hrrm.famoney.event.acconts.id", accountId);
        changeEvnetProperties.put("com.hrrm.famoney.event.acconts.movements.position", position);
        changeEvnetProperties.put("com.hrrm.famoney.event.acconts.movements.positionAfter", positionAfter);
        changeEvnetProperties.put("com.hrrm.famoney.event.timestamp", timestamp);
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_CHANGE_TOPIC,
            changeEvnetProperties));
        logger.debug("An event for movement changing is successfully put.");
        logger.trace("An event for movement changing in account id: {} from position: {} to position: {}" +
            " is successfully put.", accountId, position, positionAfter);
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
        final var position = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.movements.position");
        final var positionAfter = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.movements.positionAfter");
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
