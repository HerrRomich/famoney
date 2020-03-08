package com.hrrm.famoney.application.api.datadirectory.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategoryData", subTypes = {
        ExpenseCategoryDataDTO.class,
        IncomeCategoryDataDTO.class,
        ExpenseCategoryDTO.class,
        IncomeCategoryDTO.class
})
@JsonTypeInfo(property = "type", use = Id.NAME)
@JsonSubTypes({
        @Type(name = "expense", value = ExpenseCategoryDataDTO.class),
        @Type(name = "income", value = IncomeCategoryDataDTO.class)
})
public interface EntryCategoryDataDTO extends DTO {

    @Schema(required = true)
    String getName();

}
