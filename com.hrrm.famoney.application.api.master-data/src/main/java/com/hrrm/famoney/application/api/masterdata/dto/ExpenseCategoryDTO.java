package com.hrrm.famoney.application.api.masterdata.dto;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.application.api.masterdata.dto.impl.ExpenseCategoryDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ExpenseCategory", allOf = {
        EntryCategoryDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = "expense")
        })
})
@JsonTypeName("expense")
@Value.Immutable
@ImmutableDtoStyle
public interface ExpenseCategoryDTO extends IdDTO, EntryCategoryDTO<ExpenseCategoryDTO> {

    class Builder extends ExpenseCategoryDTOImpl.Builder implements EntryCategoryDTOBuilder<ExpenseCategoryDTO> {
    }

    @Schema(name = "children", hidden = false)
    List<ExpenseCategoryDTO> getChildren();

}
