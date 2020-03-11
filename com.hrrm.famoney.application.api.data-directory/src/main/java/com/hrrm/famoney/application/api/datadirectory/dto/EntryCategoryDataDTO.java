package com.hrrm.famoney.application.api.datadirectory.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategoryData", subTypes = {
        ExpenseCategoryDataDTO.class,
        IncomeCategoryDataDTO.class
}, discriminatorProperty = "type", discriminatorMapping = {
        @DiscriminatorMapping(schema = ExpenseCategoryDataDTO.class, value = "expense_data"),
        @DiscriminatorMapping(schema = IncomeCategoryDataDTO.class, value = "income_data")
})
@JsonTypeInfo(property = "type", use = Id.NAME)
@JsonSubTypes({
        @Type(name = "expense_data", value = ExpenseCategoryDataDTO.class),
        @Type(name = "income_data", value = IncomeCategoryDataDTO.class)
})
public interface EntryCategoryDataDTO extends DTO {

    @Schema(required = true)
    String getName();

}
