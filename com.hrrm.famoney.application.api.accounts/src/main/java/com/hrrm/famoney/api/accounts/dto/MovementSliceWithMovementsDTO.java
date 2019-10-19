package com.hrrm.famoney.api.accounts.dto;

import java.util.List;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSliceWithMovements")
public interface MovementSliceWithMovementsDTO extends DTO, MovementSliceDTO {

    List<MovementDTO> getMovements();

}
