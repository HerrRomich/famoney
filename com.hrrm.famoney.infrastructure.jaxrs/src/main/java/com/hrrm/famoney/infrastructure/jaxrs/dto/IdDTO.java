package com.hrrm.famoney.infrastructure.jaxrs.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public interface IdDTO extends DTO {

    @Schema(required = true)
    public Integer getId();

}
