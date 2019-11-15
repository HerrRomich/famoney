package com.hrrm.famoney.api.accounts.resource.internal;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public interface AccountsApiError extends ApiError {

    default @Override String getPrefix() {
        return "accounts";
    }

}
