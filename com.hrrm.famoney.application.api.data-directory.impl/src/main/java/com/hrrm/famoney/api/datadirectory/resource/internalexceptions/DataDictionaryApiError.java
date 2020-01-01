package com.hrrm.famoney.api.datadirectory.resource.internalexceptions;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public interface DataDictionaryApiError extends ApiError {

    default @Override String getPrefix() {
        return "data-dicrionary";
    }

}
