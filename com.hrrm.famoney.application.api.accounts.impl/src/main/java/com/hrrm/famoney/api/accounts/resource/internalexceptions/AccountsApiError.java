package com.hrrm.famoney.api.accounts.resource.internalexceptions;

import javax.ws.rs.core.Response.Status;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public enum AccountsApiError implements ApiError {

    NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS(1001, "No account was found for request of all account movements.", Status.NOT_FOUND),
    NO_ACCOUNT_BY_CHANGE(1002, "No account was found for request on account change.", Status.NOT_FOUND),
    NO_ACCOUNT_ON_GET_ACCOUNT(1006, "No account was found for request by id.", Status.NOT_FOUND),
    NO_ACCOUNT_ON_ADD_MOVEMENT(1007, "No account was found for adding a movement.", Status.NOT_FOUND),
    NO_MOVEMENT_ON_GET_MOVEMENT(1008, "No movement was found for request by id in a specified account.", Status.NOT_FOUND);

    private final Integer code;
    private final String message;
    private final Status status;

    AccountsApiError(final int code, final String message, final Status status) {
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
        return "accounts";
    }

}
