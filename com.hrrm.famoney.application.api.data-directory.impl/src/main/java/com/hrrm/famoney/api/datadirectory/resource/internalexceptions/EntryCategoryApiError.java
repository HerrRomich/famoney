package com.hrrm.famoney.api.datadirectory.resource.internalexceptions;

public enum EntryCategoryApiError implements DataDictionaryApiError {

    NO_ENTRY_CATEGORY_BY_CHANGE(1002, "No entry category was found for request on entry category change.");

    private final Integer code;
    private final String message;

    EntryCategoryApiError(final int code, final String message) {
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
