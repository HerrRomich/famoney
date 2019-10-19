package com.hrrm.famoney.api.accounts.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public interface WithIdDTO extends DTO {

    @Schema(required = true)
    public Integer getId();

}
