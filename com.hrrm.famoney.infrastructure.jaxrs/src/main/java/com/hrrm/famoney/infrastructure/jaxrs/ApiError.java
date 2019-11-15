package com.hrrm.famoney.infrastructure.jaxrs;

public interface ApiError {

    Integer getCode();

    String getMessage();

    String getPrefix();

}
