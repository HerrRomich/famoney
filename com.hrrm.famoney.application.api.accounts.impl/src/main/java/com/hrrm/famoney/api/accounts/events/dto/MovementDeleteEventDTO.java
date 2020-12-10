package com.hrrm.famoney.api.accounts.events.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementDeleteEvent", allOf = {
        MovementEventDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = MovementEventDTO.DELETE_EVENT)
        })
})
@JsonTypeName(MovementEventDTO.DELETE_EVENT)
@Value.Immutable
@ImmutableDtoStyle
public interface MovementDeleteEventDTO extends MovementEventDTO {

}
