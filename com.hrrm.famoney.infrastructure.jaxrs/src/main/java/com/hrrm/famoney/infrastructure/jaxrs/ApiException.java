package com.hrrm.famoney.infrastructure.jaxrs;

import javax.ws.rs.core.Response.Status;

public class ApiException extends RuntimeException {

    private final Integer errorCode;

    private final String errorMessage;

    private final String errorDescription;

    public ApiException(Integer errorCode, String errorMessage, String errorDescription, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public ApiException(Integer errorCode, String errorMessage) {
        this(errorCode, errorMessage, null, null);
    }

    public ApiException(Integer errorCode, String errorMessage, String description) {
        this(errorCode, errorMessage, description, null);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public Status getResponseStatus() {
        return Status.INTERNAL_SERVER_ERROR;
    }

}
