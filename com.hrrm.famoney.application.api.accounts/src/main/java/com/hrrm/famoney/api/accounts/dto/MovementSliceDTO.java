package com.hrrm.famoney.api.accounts.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSlice", allOf = { MoventSliceDataDTO.class })
public interface MovementSliceDTO extends DTO, WithIdDTO, MoventSliceDataDTO {

}
