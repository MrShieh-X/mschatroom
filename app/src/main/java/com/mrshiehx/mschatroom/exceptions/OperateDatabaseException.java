package com.mrshiehx.mschatroom.exceptions;

public class OperateDatabaseException extends RuntimeException {
    public OperateDatabaseException() {
        super();
    }

    public OperateDatabaseException(String message) {
        super(message);
    }

    public OperateDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperateDatabaseException(Throwable cause) {
        super(cause);
    }
}
