package com.hrrm.famoney.api.accounts.dto;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "MovementSlice",
        allOf = { MovementSliceDataDTO.class },
        subTypes = { MovementSliceWithMovementsDTO.class })
@Value.Immutable
@ImmutableDtoStyle
public interface MovementSliceDTO extends DTO, IdDTO, MovementSliceDataDTO {

}
