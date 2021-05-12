package com.mrshiehx.mschatroom.chat.message;

import android.text.format.Time;

import com.mrshiehx.mschatroom.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  c: content
 *  y: send type
 *  s: time(millis)
 *  t: type(like TYPE_TIME, TYPE_RECEIVER and TYPE_SELF)
 *
 *  for file:
 *  f: file name
 *  i: file size
 **/
public class MessageItem {
    /**message type*/
    public static final int TYPE_TIME = 0;
    public static final int TYPE_RECEIVER = 1;
    public static final int TYPE_SELF = 2;
    public static final int TYPE_FAILED_SEND = 3;
    public static final int TYPE_FAILED_SEND_OFFLINE = 4;
    public static final int TYPE_FAILED_SEND_NOT_LOGGINED = 5;
    public static final int TYPE_FAILED_SEND_LOGIN_FAILED = 6;
    public static final int TYPE_FAILED_SEND_CONNECT_FAILED = 11;

    public static final int TYPE_FAILED_SEND_SO = 7;
    public static final int TYPE_FAILED_SEND_OFFLINE_SO = 8;
    public static final int TYPE_FAILED_SEND_NOT_LOGGINED_SO = 9;
    public static final int TYPE_FAILED_SEND_LOGIN_FAILED_SO = 10;
    public static final int TYPE_FAILED_SEND_CONNECT_FAILED_SO = 12;
    String c;
    int t;
    int y;//发送类型
    long s;//时间

    public MessageItem(String c, int t, int y) {
        this.c = c;
        this.t = t;
        if (t == TYPE_TIME) {
            this.s = Long.parseLong(c);
        } else {
            this.s = /*toTimeString(System.currentTimeMillis())*/System.currentTimeMillis();
        }
        this.y = y;
    }

    public static String toTimeString(long millis) {
        Time time = new Time();
        time.set(millis);
        return time.year + "-" + (time.month + 1) + "-" + time.monthDay + ";" + time.hour + ":" + time.minute;
    }

    public MessageItem setContent(String c) {
        this.c = c;
        return this;
    }

    public MessageItem setType(int t) {
        this.t = t;
        return this;
    }

    public MessageItem setContentType(int y) {
        this.y = y;
        return this;
    }

    public MessageItem setTime(long s) {
        this.s = s;
        return this;
    }

    /*public MessageItem setTimeMillis(long millis) {
        this.s = toTimeString(millis);
        return this;
    }*/

    public String getContent() {
        return c;
    }

    public int getType() {
        return t;
    }

    public int getContentType() {
        return y;
    }

    public long getTime() {
        /*if (t == TYPE_TIME)
            try {
                if (c.split(";")[1].split(":")[1].length() == 2) {
                    return c;
                } else {
                    c = c.split(";")[0] + ";" + c.split(";")[1].split(":")[0] + ":" + "0" + c.split(";")[1].split(":")[1];
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
                    s = s.split(";")[0] + ";" + s.split(";")[1].split(":")[0] + ":" + "0" + s.split(";")[1].split(":")[1];
                    return s;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return s;
            }*/
        return s;
    }

    public JSONObject toJSONObject(/*long millisForSO*/) {
        JSONObject jsonObject = new JSONObject();
        try {
            /**code for send types*/
            if (y == MessageTypes.PICTURE.code) {
                jsonObject.put("c", getContent());
            } else if (y == MessageTypes.FILE.code) {
                jsonObject.put("c", getContent());
            } else {
                jsonObject.put("c", getContent());
            }
            jsonObject.put("y", getContentType());
            jsonObject.put("t", getType());
            jsonObject.put("s", getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static MessageItem valueOf(JSONObject jsonObject) {
        if (jsonObject == null) throw new NullPointerException();
        return new MessageItem(jsonObject.optString("c"), jsonObject.optInt("t"), jsonObject.optInt("y")).setTime(jsonObject.optLong("s"));
    }

    public static MessageItem valueOf(String string) throws JSONException {
        return valueOf(new JSONObject(string));
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
