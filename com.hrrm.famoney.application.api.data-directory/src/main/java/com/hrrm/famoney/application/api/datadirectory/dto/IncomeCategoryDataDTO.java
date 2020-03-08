package com.hrrm.famoney.application.api.datadirectory.dto;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IncomeCategoryData", subTypes = {
        IncomeCategoryDTO.class
}, allOf = {
        EntryCategoryDataDTO.class
})
@Value.Immutable
@ImmutableDtoStyle
public interface IncomeCategoryDataDTO extends EntryCategoryDataDTO {

}
