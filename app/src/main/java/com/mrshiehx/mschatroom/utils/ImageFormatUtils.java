package com.mrshiehx.mschatroom.utils;

public class ImageFormatUtils {
    public static String getExtension(byte[] bytes) {
        byte[] bufHeaders = new byte[8];
        System.arraycopy(bytes, 0, bufHeaders, 0, 8);
        if (isJPEGHeader(bufHeaders)) {
            byte[] end = new byte[2];
            System.arraycopy(bytes, bytes.length-1-1, end, 0, 2);
            if (isJPEGFooter(end)) {
                return "jpeg";
            }
        }
        if (isPNG(bufHeaders)) {
            return "png";
        }
        if (isGIF(bufHeaders)) {
            return "gif";
        }
        if (isWEBP(bufHeaders)) {
            return "webp";
        }
        if (isBMP(bufHeaders)) {
            return "bmp";
        }
        if (isICON(bufHeaders)) {
            return "ico";
        }
        return "png";
    }

    public static boolean isBMP(byte[] buf) {
        byte[] markBuf = "BM".getBytes();  //BMP图片文件的前两个字节
        return compare(buf, markBuf);
    }

    public static boolean isICON(byte[] buf) {
        byte[] markBuf = {0, 0, 1, 0, 1, 0, 32, 32};
        return compare(buf, markBuf);
    }

    public static boolean isWEBP(byte[] buf) {
        byte[] markBuf = "RIFF".getBytes(); //WebP图片识别符
        return compare(buf, markBuf);
    }

    public static boolean isGIF(byte[] buf) {

        byte[] markBuf = "GIF89a".getBytes(); //GIF识别符
        if (compare(buf, markBuf)) {
            return true;
        }
        markBuf = "GIF87a".getBytes(); //GIF识别符
        return compare(buf, markBuf);
    }


    public static boolean isPNG(byte[] buf) {

        byte[] markBuf = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}; //PNG识别符
        // new String(buf).indexOf("PNG")>0 //也可以使用这种方式
        return compare(buf, markBuf);
    }

    public static boolean isJPEGHeader(byte[] buf) {
        byte[] markBuf = {(byte) 0xff, (byte) 0xd8}; //JPEG开始符

        return compare(buf, markBuf);
    }

    public static boolean isJPEGFooter(byte[] buf)//JPEG结束符
    {
        byte[] markBuf = {(byte) 0xff, (byte) 0xd9};
        return compare(buf, markBuf);
    }

    public static boolean compare(byte[] buf, byte[] markBuf) {
        for (int i = 0; i < markBuf.length; i++) {
            byte b = markBuf[i];
            byte a = buf[i];

            if (a != b) {
                return false;
            }
        }
        return true;
    }
}
