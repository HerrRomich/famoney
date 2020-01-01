package com.hrrm.famoney.application.api.datadirectory.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public interface EntryCategoryDataDTO extends DTO {

    Integer getParentId();

    @Schema(required = true)
    String getName();

}
