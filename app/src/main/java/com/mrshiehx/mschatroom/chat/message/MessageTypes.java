package com.mrshiehx.mschatroom.chat.message;

public enum MessageTypes {
    TEXT(0),
    PICTURE(1);

    public final int code;

    MessageTypes(int code) {
        this.code = code;
    }
}
