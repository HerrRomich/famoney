package com.hrrm.famoney.infrastructure.jaxrs;

import javax.ws.rs.core.Response.Status;

public class ApiException extends RuntimeException {

    private static final String COMMON_ERROR_PREFIX = "common";

    private final String errorCode;

    private final String errorDescription;

    private final Status status;

    public ApiException(String message) {
        super(message);
        this.errorCode = COMMON_ERROR_PREFIX;
        this.errorDescription = null;
        this.status = Status.INTERNAL_SERVER_ERROR;
    }

    public ApiException(ApiError error, String description, Throwable cause) {
        super(error.getMessage(),
            cause);
        this.errorCode = error.getPrefix() + "-" + error.getCode();
        this.errorDescription = description;
        this.status = error.getStatus();
    }

    public ApiException(ApiError error, String description) {
        super(error.getMessage());
        this.errorCode = error.getPrefix() + "-" + error.getCode();
        this.errorDescription = description;
        this.status = error.getStatus();
    }

    public ApiException(ApiError error) {
        this(error,
            null);
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
        return status;
    }

}
