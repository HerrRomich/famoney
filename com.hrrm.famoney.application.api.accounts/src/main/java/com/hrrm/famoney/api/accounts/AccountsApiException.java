package com.hrrm.famoney.api.accounts;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

public class AccountsApiException extends ApiException {

    public AccountsApiException(ApiError error, String description, Throwable cause) {
        super(error, description, cause);
    }

    public AccountsApiException(ApiError error, String description) {
        super(error, description);
    }

    public AccountsApiException(ApiError error) {
        super(error);
    }

    @Override
    public final String getApiErrorCodePrefix() {
        return "accounts";
    }

}
