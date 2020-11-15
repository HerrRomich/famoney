package com.hrrm.famoney.infrastructure.core;

public class FamoneyException extends Exception {

    public FamoneyException() {
        super();
    }

    public FamoneyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message,
            cause,
            enableSuppression,
            writableStackTrace);
    }

    public FamoneyException(String message, Throwable cause) {
        super(message,
            cause);
    }

    public FamoneyException(String message) {
        super(message);
    }

    public FamoneyException(Throwable cause) {
        super(cause);
    }

}
