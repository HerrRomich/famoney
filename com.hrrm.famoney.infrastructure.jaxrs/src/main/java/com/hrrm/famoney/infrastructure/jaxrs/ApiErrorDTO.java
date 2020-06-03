package com.hrrm.famoney.infrastructure.jaxrs;

import java.util.Optional;

import org.immutables.value.Value;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiError")
@Value.Immutable
@ImmutableDtoStyle
public interface ApiErrorDTO extends DTO {

    String getCode();

    String getMessage();

    @JsonIncludeNonNull
    Optional<String> getDescription();

}
