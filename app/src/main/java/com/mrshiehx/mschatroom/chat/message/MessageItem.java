package com.mrshiehx.mschatroom.chat.message;

import android.text.format.Time;

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
    String s;//时间

    public MessageItem(String c, int t) {
        this.c = c;
        this.t = t;
        if(t==TYPE_RECEIVER||t==TYPE_SELF) {
            Time time = new Time();
            time.setToNow();
            this.s = time.year + "-" + (time.month + 1) + "-" + time.monthDay + ";" + time.hour + ":" + time.minute;
        }
    }

    public void setContent(String c) {
        this.c = c;
    }

    public void setType(int t) {
        this.t = t;
    }

    public void setTime(String s){
        this.s=s;
    }

    public String getContent() {
        return c;
    }

    public int getType() {
        return t;
    }

    public String getTime() {
        if (t == TYPE_TIME)
            try {
                if (c.split(";")[1].split(":")[1].length() == 2) {
                    return c;
                } else {
                    c=c.split(";")[0]+";"+c.split(";")[1].split(":")[0]+":"+"0"+c.split(";")[1].split(":")[1];
                    return c;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return c;
            }
        else
            try {
                if (s.split(";")[1].split(":")[1].length() == 2) {
                    return s;
                } else {
                    s=s.split(";")[0]+";"+s.split(";")[1].split(":")[0]+":"+"0"+s.split(";")[1].split(":")[1];
                    return s;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return s;
            }
    }
}
