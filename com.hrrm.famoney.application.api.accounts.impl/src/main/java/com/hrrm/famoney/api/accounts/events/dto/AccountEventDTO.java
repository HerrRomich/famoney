package com.hrrm.famoney.api.accounts.events.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountEvent", subTypes = {
        AccountAddEventDTO.class,
        AccountChangeEventDTO.class,
        AccountDeleteEventDTO.class,
        MovementEventDTO.class
}, discriminatorProperty = "event", discriminatorMapping = {
        @DiscriminatorMapping(schema = AccountAddEventDTO.class, value = AccountEventDTO.ADD_EVENT),
        @DiscriminatorMapping(schema = AccountChangeEventDTO.class, value = AccountEventDTO.CHANGE_EVENT),
        @DiscriminatorMapping(schema = AccountDeleteEventDTO.class, value = AccountEventDTO.DELETE_EVENT)
})
@JsonSubTypes({
        @Type(name = AccountEventDTO.ADD_EVENT, value = AccountAddEventDTO.class),
        @Type(name = AccountEventDTO.CHANGE_EVENT, value = AccountChangeEventDTO.class),
        @Type(name = AccountEventDTO.DELETE_EVENT, value = AccountDeleteEventDTO.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "event")
public interface AccountEventDTO extends EventDTO {

    public static final String ADD_EVENT = "accountAdd";
    public static final String CHANGE_EVENT = "accountChange";
    public static final String DELETE_EVENT = "accountDelete";

    @Schema(required = true)
    Integer getAccountId();

}
