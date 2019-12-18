package com.hrrm.famoney.api.accounts.resource.internalexceptions;

public enum AccountApiError implements AccountsApiError {

    NO_ACCOUNT_ON_GET_ALL_ACCOUNT_MOVEMENTS(1001,
            "No account was found for request of all account movements."), NO_ACCOUNT_BY_CHANGE(
                    1002, "No account was found for request on account change."),
    NO_ACCOUNT_ON_GET_MOVEMENT_SLICES_BY_ACCOUNT(1003,
            "No account was found for request of all account slices."),
    NO_ACCOUNT_ON_GET_MOVEMENTS_BY_SLICE(1004,
            "No account was found for request of all account movements in a specified slice."),
    NO_MOVEMENT_SLICE_ON_GET_MOVEMENTS_BY_SLICE(1005,
            "No movement slice was found for request of all movements in a specified slice of specified account."),
    NO_ACCOUNT_ON_GET_ACCOUNT(1006, "No account was found for request by id.");

    private final Integer code;
    private final String message;

    AccountApiError(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
