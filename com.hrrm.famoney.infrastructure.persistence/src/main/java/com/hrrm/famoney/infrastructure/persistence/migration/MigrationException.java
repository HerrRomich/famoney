package com.hrrm.famoney.infrastructure.persistence.migration;

public class MigrationException extends RuntimeException {

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
