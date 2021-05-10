package com.mrshiehx.mschatroom.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static String hexReadFile(String file) throws IOException {
        InputStream is = new FileInputStream(new File(file));

        int bytesCounter = 0;
        int value = 0;
        StringBuilder sbHex = new StringBuilder();
        StringBuilder sbText = new StringBuilder();
        StringBuilder sbResult = new StringBuilder();

        while ((value = is.read()) != -1) {
            //convert to hex value with "X" formatter
            sbHex.append(String.format("%02X", value));

            //If the chracater is not convertable, just print a dot symbol "."
            if (!Character.isISOControl(value)) {
                sbText.append((char) value);
            } else {
                sbText.append("");
            }

            //if 16 bytes are read, reset the counter,
            //clear the StringBuilder for formatting purpose only.
            if (bytesCounter == 15) {
                sbResult.append(sbHex);//.append("      ").append(sbText).append("\n");
                sbHex.setLength(0);
                sbText.setLength(0);
                bytesCounter = 0;
            } else {
                bytesCounter++;
            }
        }
        //if still got content
        if (bytesCounter != 0) {
            //add spaces more formatting purpose only
            for (; bytesCounter < 16; bytesCounter++) {
                //1 character 3 spaces
                sbHex.append("");
            }
            sbResult.append(sbHex);//.append("      ").append(sbText).append("\n");
        }
        is.close();
        return sbResult.toString();// + "\n\n";
    }

    public static void hexWrite(String bytes, File file) throws IOException {
        hexWrite(hexString2Bytes(bytes), file);
    }

    public static void hexWrite(byte[] bytes, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(bytes);
        fop.flush();
        fop.close();
    }

    public static byte[] hexString2Bytes(String hex1) {
        if ((hex1 == null) || (hex1.equals(""))) {
            return null;
        } else if (hex1.length() % 2 != 0) {
            return null;
        } else {
            String hex = hex1.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
            }
            return b;
        }

    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void bytes2File(byte[] bytes, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes, 0, bytes.length);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static void copy(File source, File to) throws IOException {
        FileUtils.copy(source, to);
    }

    public static byte[] inputStream2ByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        byte[] s=output.toByteArray();
        try{
            output.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
}
