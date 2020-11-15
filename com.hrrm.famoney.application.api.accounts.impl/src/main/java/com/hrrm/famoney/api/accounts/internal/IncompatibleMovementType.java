package com.hrrm.famoney.api.accounts.internal;

import com.hrrm.famoney.infrastructure.core.FamoneyException;

public class IncompatibleMovementType extends FamoneyException {

    public IncompatibleMovementType() {
        super();
    }

    public IncompatibleMovementType(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message,
            cause,
            enableSuppression,
            writableStackTrace);
    }

    public IncompatibleMovementType(String message, Throwable cause) {
        super(message,
            cause);
    }

    public IncompatibleMovementType(String message) {
        super(message);
    }

    public IncompatibleMovementType(Throwable cause) {
        super(cause);
    }

}
