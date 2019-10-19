package com.hrrm.famoney.api.accounts.resource;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

public class AccountNotFoundException extends ApiException {

    public AccountNotFoundException(Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public AccountNotFoundException(Integer errorCode, String errorMessage, String description) {
        super(errorCode, errorMessage, description);
    }

    @Override
    public Status getResponseStatus() {
        return Status.NOT_FOUND;
    }

}
