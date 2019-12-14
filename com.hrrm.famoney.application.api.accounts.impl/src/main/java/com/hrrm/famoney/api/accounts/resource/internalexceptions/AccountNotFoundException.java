package com.hrrm.famoney.api.accounts.resource.internalexceptions;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.api.accounts.internal.AccountsApiException;
import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public class AccountNotFoundException extends AccountsApiException {

    public AccountNotFoundException(ApiError error, String description) {
        super(error, description);
    }

    public AccountNotFoundException(ApiError error) {
        super(error);
    }

    public AccountNotFoundException(ApiError error, String errorDescription, Throwable cause) {
        super(error, errorDescription, cause);
    }

    @Override
    public Status getResponseStatus() {
        return Status.NOT_FOUND;
    }

}
