package com.hrrm.famoney.api.accounts.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Id", subTypes = { MovementDTO.class, AccountDTO.class, MovementSliceDTO.class })
public interface IdDTO extends DTO {

    @Schema(required = true)
    public Integer getId();

}
