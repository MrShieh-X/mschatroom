package com.mrshiehx.mschatroom.exceptions;

public class NoTargetChatObjectException extends RuntimeException{
    public NoTargetChatObjectException() {
        super();
    }

    public NoTargetChatObjectException(String message) {
        super(message);
    }

    public NoTargetChatObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTargetChatObjectException(Throwable cause) {
        super(cause);
    }
}
