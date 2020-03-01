package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementData", subTypes = {
        MovementDTO.class
})
@Value.Immutable
@ImmutableDtoStyle
public interface MovementDataDTO {

    @Schema(required = true)
    LocalDateTime getDate();

    LocalDateTime getBookingDate();

    @Schema(required = true)
    BigDecimal getAmount();

}
