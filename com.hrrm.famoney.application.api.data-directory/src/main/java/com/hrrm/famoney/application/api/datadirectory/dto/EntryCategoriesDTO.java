package com.hrrm.famoney.application.api.datadirectory.dto;

import java.util.List;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategories")
@Value.Immutable
@ImmutableDtoStyle
public interface EntryCategoriesDTO {
    
    List<IncomeCategoryDTO> getIncomes();
    
    List<ExpenseCategoryDTO> getExpenses();

}
