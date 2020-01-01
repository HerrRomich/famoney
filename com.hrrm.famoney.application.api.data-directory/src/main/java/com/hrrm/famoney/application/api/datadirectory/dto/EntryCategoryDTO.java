package com.hrrm.famoney.application.api.datadirectory.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryCategory")
public interface EntryCategoryDTO<T extends EntryCategoryDTO<T>> extends EntryCategoryDataDTO {

    @ArraySchema
    List<T> getChildren();

}
