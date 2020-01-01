package com.hrrm.famoney.application.api.datadirectory.dto;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IncomeCategory", allOf = { IncomeCategoryDataDTO.class })
@Value.Immutable
@ImmutableDtoStyle
public interface IncomeCategoryDTO extends IdDTO, IncomeCategoryDataDTO, EntryCategoryDTO<IncomeCategoryDTO> {

}
