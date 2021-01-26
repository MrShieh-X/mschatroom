package com.mrshiehx.mschatroom.chat.message;

public class MessageItem {
    public static final int TYPE_TIME = 0;
    public static final int TYPE_RECEIVER = 1;
    public static final int TYPE_SELF = 2;
    public static final int TYPE_FAILED_SEND = 3;
    public static final int TYPE_FAILED_SEND_OFFLINE = 4;
    public static final int TYPE_FAILED_SEND_NOT_LOGGINED = 5;
    public static final int TYPE_FAILED_SEND_LOGIN_FAILED = 6;
    String c;
    int t;

    public MessageItem(String c, int t) {
        this.c = c;
        this.t = t;
    }

    public void setContent(String c) {
        this.c = c;
    }

    public void setType(int t) {
        this.t = t;
    }

    public String getContent() {
        return c;
    }

    public int getType() {
        return t;
    }
}
