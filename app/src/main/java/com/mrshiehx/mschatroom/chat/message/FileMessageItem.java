package com.mrshiehx.mschatroom.chat.message;

import com.mrshiehx.mschatroom.utils.FileUtils;

public class FileMessageItem extends MessageItem{
    public String f;
    public long i;
    public FileMessageItem(String c, int t, int y) {
        super(c, t, y);
    }

    public String getFormattedFileSize(){
        return FileUtils.getFormatSize(i);
    }

    public FileMessageItem setFileSize(long fileSize) {
        this.i = fileSize;
        return this;
    }

    public FileMessageItem setFileName(String fileName) {
        this.f = fileName;
        return this;
    }

    public String getFileName() {
        return f;
    }

    public long getFileSize() {
        return i;
    }

    /*@Override
    public String getContent() {
        return Utils.replaceLongToString(super.getContent());
    }


    public static FileMessageItem valueOf(JSONObject jsonObject) {
        if (jsonObject == null) throw new NullPointerException();
        return ((FileMessageItem) new FileMessageItem(jsonObject.optString("c"), jsonObject.optInt("t"), jsonObject.optInt("y")).setTime(jsonObject.optLong("s"))).setFileName(jsonObject.optString("f")).setFileSize(jsonObject.optLong("i"));
    }*/
}
