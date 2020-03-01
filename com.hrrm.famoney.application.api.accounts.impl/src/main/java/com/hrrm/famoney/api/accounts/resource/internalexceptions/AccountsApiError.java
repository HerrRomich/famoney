package com.hrrm.famoney.api.accounts.resource.internalexceptions;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public interface AccountsApiError extends ApiError {

    @Override
    default String getPrefix() {
        return "accounts";

    }
}
