package com.hrrm.famoney.api.accounts.events.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementEvent", allOf = {
        AccountEventDTO.class
}, subTypes = {
        MovementAddEventDTO.class,
        MovementChangeEventDTO.class,
        MovementDeleteEventDTO.class
}, discriminatorProperty = "event", discriminatorMapping = {
        @DiscriminatorMapping(schema = MovementAddEventDTO.class, value = MovementEventDTO.ADD_EVENT),
        @DiscriminatorMapping(schema = MovementChangeEventDTO.class, value = MovementEventDTO.CHANGE_EVENT),
        @DiscriminatorMapping(schema = MovementDeleteEventDTO.class, value = MovementEventDTO.DELETE_EVENT)
})
@JsonSubTypes({
        @Type(name = MovementEventDTO.ADD_EVENT, value = MovementAddEventDTO.class),
        @Type(name = MovementEventDTO.CHANGE_EVENT, value = MovementChangeEventDTO.class),
        @Type(name = MovementEventDTO.DELETE_EVENT, value = MovementDeleteEventDTO.class)
})
public interface MovementEventDTO extends AccountEventDTO {

    public static final String ADD_EVENT = "movementAdd";
    public static final String CHANGE_EVENT = "movementChange";
    public static final String DELETE_EVENT = "movementDelete";

    @Schema(required = true)
    Integer getPosition();

}
