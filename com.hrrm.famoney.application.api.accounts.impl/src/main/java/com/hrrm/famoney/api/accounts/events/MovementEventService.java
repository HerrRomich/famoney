package com.hrrm.famoney.api.accounts.events;

import org.immutables.value.Value;
import org.osgi.util.pushstream.PushStream;

import com.hrrm.famoney.api.accounts.events.dto.MovementEventDTO;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

public interface MovementEventService {

    public static final String MOVEMENTS_TOPIC = "com/hrrm/famoney/event/accounts/movements";
    public static final String MOVEMENTS_ADD_TOPIC = MOVEMENTS_TOPIC
            + "/add";
    public static final String MOVEMENTS_CHANGE_TOPIC = MOVEMENTS_TOPIC
            + "/change";
    public static final String MOVEMENTS_DELETE_TOPIC = MOVEMENTS_TOPIC
            + "/delete";

    public PushStream<MovementEventDTO> registerEventListener(Integer accountId);

    public void putEvent(EventData eventData);

    public static interface EventData {

        Integer getAccountId();

        Integer getPosition();

    }

    @Value.Immutable
    @ImmutableDtoStyle
    public static interface AddEventData extends EventData {
    }

    @Value.Immutable
    @ImmutableDtoStyle
    public static interface ChangeEventData extends EventData {

        Integer getPositionAfter();

    }

    @Value.Immutable
    @ImmutableDtoStyle
    public static interface DeleteEventData extends EventData {
    }

}
