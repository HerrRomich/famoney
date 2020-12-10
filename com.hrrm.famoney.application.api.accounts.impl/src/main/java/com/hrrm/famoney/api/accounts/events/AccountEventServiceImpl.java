package com.hrrm.famoney.api.accounts.events;

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
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

import com.hrrm.famoney.api.accounts.events.dto.AccountEventDTO;
import com.hrrm.famoney.api.accounts.events.dto.impl.AccountChangeEventDTOImpl;

@Component(service = {
        AccountEventService.class,
        EventHandler.class
})
@EventTopics({
        AccountEventService.ACCOUNTS_CHANGE_TOPIC + "/*"
})
@RequireEventAdmin
public class AccountEventServiceImpl implements AccountEventService, EventHandler {

    private final Logger logger;
    private final EventAdmin eventAdmin;

    private final PushStreamProvider pushStreamProvider;
    private final SimplePushEventSource<AccountEventDTO> accountEvents;

    @Activate
    public AccountEventServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final EventAdmin eventAdmin) {
        super();
        this.logger = logger;
        this.eventAdmin = eventAdmin;
        pushStreamProvider = new PushStreamProvider();
        accountEvents = pushStreamProvider.createSimpleEventSource(AccountEventDTO.class);
    }

    @Deactivate
    public void deactivate() {
        accountEvents.close();
    }

    @Override
    public PushStream<AccountEventDTO> registerEventListener() {
        return this.pushStreamProvider.createStream(accountEvents);
    }

    @Override
    public void putChangeEvent(Integer accountId) {
        logger.debug("An event for account adding will be put.");
        logger.trace("An event for account adding to account id: {} into position: {} will be put.", accountId);
        final var addEvnetProperties = new HashMap<String, Object>();
        addEvnetProperties.put("com.hrrm.famoney.event.acconts.id", accountId);
        eventAdmin.postEvent(new Event(MovementEventService.MOVEMENTS_ADD_TOPIC,
            addEvnetProperties));
        logger.debug("An event for account adding is successfully put.");
        logger.trace("An event for account adding to account id: {} into position: {} is successfully put.", accountId);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
        case ACCOUNTS_CHANGE_TOPIC:
            publishAccountChange(event);
            break;
        }
    }

    private void publishAccountChange(Event event) {
        logger.debug("Account change event triggered.");
        Integer accountId = (Integer) event.getProperty("com.hrrm.famoney.event.acconts.id");
        this.accountEvents.publish(AccountChangeEventDTOImpl.builder()
            .accountId(accountId)
            .build());
        logger.debug("Account change event successfully published.");
    }
}
