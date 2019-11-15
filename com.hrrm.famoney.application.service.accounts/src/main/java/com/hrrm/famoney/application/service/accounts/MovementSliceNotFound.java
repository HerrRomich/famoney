package com.hrrm.famoney.application.service.accounts;

public class MovementSliceNotFound extends FamoneyAccountServiceException {

    public MovementSliceNotFound() {
        super();
    }

    public MovementSliceNotFound(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MovementSliceNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public MovementSliceNotFound(String message) {
        super(message);
    }

    public MovementSliceNotFound(Throwable cause) {
        super(cause);
    }

}
