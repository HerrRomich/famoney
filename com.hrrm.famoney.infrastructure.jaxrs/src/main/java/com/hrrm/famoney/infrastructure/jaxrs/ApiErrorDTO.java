package com.hrrm.famoney.infrastructure.jaxrs;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@ImmutableDtoStyle
public interface ApiErrorDTO extends DTO {

    String getCode();

    String getMessage();

    @JsonIncludeNonNull
    Optional<String> getDescription();

    @JsonIncludeNonNull
    Optional<String> getStackTrace();

}
