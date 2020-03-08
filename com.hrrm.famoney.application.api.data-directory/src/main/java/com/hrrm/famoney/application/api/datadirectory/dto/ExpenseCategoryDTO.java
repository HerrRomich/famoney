package com.hrrm.famoney.application.api.datadirectory.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ExpenseCategory", allOf = {
        ExpenseCategoryDataDTO.class
})
@JsonTypeName("expense")
@Value.Immutable
@ImmutableDtoStyle
public interface ExpenseCategoryDTO extends IdDTO, ExpenseCategoryDataDTO, EntryCategoryDTO<ExpenseCategoryDTO> {

}
