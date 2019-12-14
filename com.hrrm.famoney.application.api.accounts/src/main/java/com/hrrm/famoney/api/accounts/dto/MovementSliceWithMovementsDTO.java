package com.hrrm.famoney.api.accounts.dto;

import java.util.List;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSliceWithMovements", allOf = { MovementSliceDTO.class })
@Value.Immutable
@ImmutableDtoStyle
public interface MovementSliceWithMovementsDTO extends DTO, MovementSliceDTO {

    MovementOrder getMovementOrder();

    List<MovementDTO> getMovements();

}
