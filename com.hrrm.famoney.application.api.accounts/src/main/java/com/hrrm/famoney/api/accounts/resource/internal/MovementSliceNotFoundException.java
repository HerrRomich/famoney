package com.hrrm.famoney.api.accounts.resource.internal;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.api.accounts.AccountsApiException;
import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public class MovementSliceNotFoundException extends AccountsApiException {

    public MovementSliceNotFoundException(ApiError error, String description, Throwable cause) {
        super(error, description, cause);
    }

    public MovementSliceNotFoundException(ApiError error, String description) {
        super(error, description);
    }

    public MovementSliceNotFoundException(ApiError error) {
        super(error);
    }

    @Override
    public Status getResponseStatus() {
        return Status.NOT_FOUND;
    }

}
