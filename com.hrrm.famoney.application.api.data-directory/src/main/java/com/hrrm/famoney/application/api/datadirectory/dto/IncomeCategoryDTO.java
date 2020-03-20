package com.hrrm.famoney.application.api.datadirectory.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.IncomeCategoryDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IncomeCategory", allOf = {
        IncomeCategoryDataDTO.class
})
@JsonTypeName("income")
@Value.Immutable
@ImmutableDtoStyle
public interface IncomeCategoryDTO extends IdDTO, IncomeCategoryDataDTO, EntryCategoryDTO<IncomeCategoryDTO> {

    class Builder extends IncomeCategoryDTOImpl.Builder implements EntryCategoryDTOBuilder<IncomeCategoryDTO> {
    }

}
