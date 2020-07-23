package com.hrrm.famoney.application.api.masterdata.dto;

import java.util.List;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategories")
@Value.Immutable
@ImmutableDtoStyle
public interface EntryCategoriesDTO {

    @Schema(required = true)
    List<IncomeCategoryDTO> getIncomes();

    @Schema(required = true)
    List<ExpenseCategoryDTO> getExpenses();

}
