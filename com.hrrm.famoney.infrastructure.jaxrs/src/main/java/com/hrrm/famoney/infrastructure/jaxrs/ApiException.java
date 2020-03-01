package com.hrrm.famoney.infrastructure.jaxrs;

import javax.ws.rs.core.Response.Status;

public class ApiException extends RuntimeException {

    private static final String COMMON_ERROR_PREFIX = "common";

    private final String errorCode;

    private final String errorDescription;

    public ApiException(ApiError error, String description, Throwable cause) {
        super(error.getMessage(),
            cause);
        this.errorCode = getApiErrorCodePrefix() + "-" + error.getCode();
        this.errorDescription = description;
    }

    public ApiException(ApiError error, String description) {
        this(error,
            description,
            null);
    }

    public ApiException(ApiError error) {
        this(error,
            null);
    }

    public String getApiErrorCodePrefix() {
        return COMMON_ERROR_PREFIX;
    }

    public final String getErrorCode() {
        return errorCode;
    }

    public final String getErrorMessage() {
        return getMessage();
    }

    public final String getErrorDescription() {
        return errorDescription;
    }

    public Status getResponseStatus() {
        return Status.INTERNAL_SERVER_ERROR;
    }

}
