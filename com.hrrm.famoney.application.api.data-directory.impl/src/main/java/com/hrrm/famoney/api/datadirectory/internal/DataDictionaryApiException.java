package com.hrrm.famoney.api.datadirectory.internal;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

public class DataDictionaryApiException extends ApiException {

    public DataDictionaryApiException(ApiError error, String description, Throwable cause) {
        super(error, description, cause);
    }

    public DataDictionaryApiException(ApiError error, String description) {
        super(error, description);
    }

    public DataDictionaryApiException(ApiError error) {
        super(error);
    }

    @Override
    public final String getApiErrorCodePrefix() {
        return "data-dictionary";
    }

}
