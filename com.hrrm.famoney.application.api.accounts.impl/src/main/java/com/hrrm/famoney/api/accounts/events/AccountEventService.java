package com.hrrm.famoney.api.accounts.events;

import org.osgi.util.pushstream.PushStream;

import com.hrrm.famoney.api.accounts.events.dto.AccountEventDTO;

public interface AccountEventService {

    public static final String ACCOUNTS_CHANGE_TOPIC = "com/hrrm/famoney/event/accounts";

    PushStream<AccountEventDTO> registerEventListener();

    void putChangeEvent(Integer accountId);

}
