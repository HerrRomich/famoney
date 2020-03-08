package com.hrrm.famoney.infrastructure.persistence.migrations;

public class MigrationException extends Exception {

    public MigrationException(final String message) {
        super(message);
    }

    public MigrationException(final Throwable cause) {
        super(cause);
    }

    public MigrationException(final String message, final Throwable cause) {
        super(message,
            cause);
    }

}
