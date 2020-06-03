package com.hrrm.famoney.api.datadirectory.resource.internalexceptions;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public enum EntryCategoryApiError implements ApiError {

    NO_ENTRY_CATEGORY_BY_CHANGE(1002, "No entry category was found for request on entry category change.", Status.NOT_FOUND);

    private final Integer code;
    private final String message;
    private final Status status;

    EntryCategoryApiError(final int code, final String message, final Status status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getPrefix() {
        return "data-dictionary";
    }

}
