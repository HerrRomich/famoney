package com.hrrm.famoney.api.accounts.resource.internalexceptions;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public enum AccountsApiError implements ApiError {

    NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS("No account was found for request of all account movements.", Status.NOT_FOUND),
    NO_ACCOUNT_BY_CHANGE("No account was found for request on account change.", Status.NOT_FOUND),
    NO_ACCOUNT_ON_GET_ACCOUNT("No account was found for request by id.", Status.NOT_FOUND),
    NO_ACCOUNT_ON_ADD_MOVEMENT("No account was found for adding a movement.", Status.NOT_FOUND),
    NO_MOVEMENT_ON_GET_MOVEMENT("No movement was found for request by id in a specified account.", Status.NOT_FOUND),
    NO_ACCOUNT_ON_CHANGE_MOVEMENT("No account was found for changing a movement.", Status.NOT_FOUND),
    NO_MOVEMENT_ON_CHANGE_MOVEMENT("No movement was found for changing.", Status.NOT_FOUND);

    private final String message;
    private final Status status;

    AccountsApiError(final String message, final Status status) {
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
        return "accounts";
    }

}
