package com.mrshiehx.mschatroom.utils;

public class StringUtils {
    public static final char SPLIT_SYMBOL=';';
    public static String unicodeToText(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split(String.valueOf(SPLIT_SYMBOL));
        StringBuilder returnStr = new StringBuilder();
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i=1;i<strs.length;i++) {
            returnStr.append((char) Integer.valueOf(strs[i], 16).intValue());
        }
        return returnStr.toString();
    }

    public static String textToUnicode(String text) {
        char[] chars = text.toCharArray();
        StringBuilder returnStr = new StringBuilder();
        for (char aChar : chars) {
            returnStr.append(SPLIT_SYMBOL).append(Integer.toString(aChar, 16));
        }
        return returnStr.toString();
    }

    /**十六进制*/
    public static String hexBytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(String.format("%02X", aByte));
        }
        return builder.toString().toUpperCase();
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }
}
