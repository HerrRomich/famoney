package com.hrrm.famoney.api.accounts.dto;

import java.util.List;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSlicesInfo")
@Value.Immutable
@ImmutableDtoStyle
public interface MovementSlicesInfoDTO {

    @Schema(required = true)
    Integer getAccountId();

    @Schema(required = true)
    Long getMovementCount();

    @Schema(required = true)
    List<MovementSliceDTO> getMovementSlices();

}
