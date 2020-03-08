package com.hrrm.famoney.application.api.datadirectory.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategory")
@JsonTypeInfo(property = "type", use = Id.NAME)
@JsonSubTypes({
        @Type(name = "expense", value = ExpenseCategoryDTO.class),
        @Type(name = "income", value = IncomeCategoryDTO.class)
})
public interface EntryCategoryDTO<T extends EntryCategoryDTO<T>> extends IdDTO, EntryCategoryDataDTO {

    @ArraySchema
    List<T> getChildren();

}
