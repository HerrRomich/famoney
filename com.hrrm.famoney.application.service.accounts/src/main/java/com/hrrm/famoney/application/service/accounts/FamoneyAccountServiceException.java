package com.hrrm.famoney.application.service.accounts;

public class FamoneyAccountServiceException extends Exception {

    public FamoneyAccountServiceException() {
        super();
    }

    public FamoneyAccountServiceException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FamoneyAccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FamoneyAccountServiceException(String message) {
        super(message);
    }

    public FamoneyAccountServiceException(Throwable cause) {
        super(cause);
    }

}
