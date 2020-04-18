package com.hrrm.famoney.application.api.datadirectory.dto;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.IncomeCategoryDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IncomeCategory", allOf = {
        EntryCategoryDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = "income")
        })
})
@JsonTypeName("income")
@Value.Immutable
@ImmutableDtoStyle
public interface IncomeCategoryDTO extends IdDTO, EntryCategoryDTO<IncomeCategoryDTO> {

    class Builder extends IncomeCategoryDTOImpl.Builder implements EntryCategoryDTOBuilder<IncomeCategoryDTO> {
    }

    @Schema(name = "children", hidden = false)
    List<IncomeCategoryDTO> getChildren();

}
