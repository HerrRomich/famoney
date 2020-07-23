package com.hrrm.famoney.application.api.masterdata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategory", discriminatorProperty = "type", discriminatorMapping = {
        @DiscriminatorMapping(value = "expense", schema = ExpenseCategoryDTO.class),
        @DiscriminatorMapping(value = "income", schema = IncomeCategoryDTO.class)
})
@JsonTypeInfo(property = "type", use = Id.NAME, include = As.PROPERTY)
@JsonSubTypes({
        @Type(name = "expense", value = ExpenseCategoryDTO.class),
        @Type(name = "income", value = IncomeCategoryDTO.class)
})
public interface EntryCategoryDTO<T extends EntryCategoryDTO<T>> extends IdDTO {

    @Schema(required = true)
    String getName();

    @Schema(hidden = true)
    List<T> getChildren();

}
