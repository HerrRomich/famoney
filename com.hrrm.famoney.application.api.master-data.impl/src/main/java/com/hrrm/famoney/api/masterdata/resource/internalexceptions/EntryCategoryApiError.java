package com.hrrm.famoney.api.masterdata.resource.internalexceptions;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public enum EntryCategoryApiError implements ApiError {

    NO_ENTRY_CATEGORY_BY_CHANGE("No entry category was found for request on entry category change.", Status.NOT_FOUND);

    private final String message;
    private final Status status;

    EntryCategoryApiError(final String message, final Status status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return toString();
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
