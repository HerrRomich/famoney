package com.hrrm.famoney.api.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Movement", allOf = { MovementDataDTO.class })
public interface MovementDTO extends WithIdDTO, MovementDataDTO {

}
