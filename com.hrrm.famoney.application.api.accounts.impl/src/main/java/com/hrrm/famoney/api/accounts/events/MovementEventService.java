package com.hrrm.famoney.api.accounts.events;

import java.time.LocalDateTime;

import org.osgi.util.pushstream.PushStream;

import com.hrrm.famoney.api.accounts.events.dto.MovementEventDTO;

public interface MovementEventService {

    public static final String MOVEMENTS_TOPIC = "com/hrrm/famoney/event/accounts/movements";
    public static final String MOVEMENTS_ADD_TOPIC = MOVEMENTS_TOPIC + "/add";
    public static final String MOVEMENTS_CHANGE_TOPIC = MOVEMENTS_TOPIC + "/change";
    public static final String MOVEMENTS_DELETE_TOPIC = MOVEMENTS_TOPIC + "/delete";

    public PushStream<MovementEventDTO> registerEventListener(Integer accountId);

    public void putAddEvent(Integer accountId, Integer position, LocalDateTime timestamp);

    public void putChangeEvent(Integer accountId, Integer positionBefore, Integer positionAfter,
            LocalDateTime timestamp);

}
