package com.hrrm.famoney.infrastructure.jaxrs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ApiErrorDTO implements DTO {

    public final Integer code;

    public final String message;

    @JsonInclude(Include.NON_NULL)
    public final String description;

    @JsonInclude(Include.NON_NULL)
    public final String stackTrace;

}
