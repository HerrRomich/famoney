package com.hrrm.famoney.api.masterdata.resource.internalexceptions;

import com.hrrm.famoney.infrastructure.jaxrs.ApiError;

public interface DataDictionaryApiError extends ApiError {

    @Override
    default String getPrefix() {
        return "data-dicrionary";
    }

}
