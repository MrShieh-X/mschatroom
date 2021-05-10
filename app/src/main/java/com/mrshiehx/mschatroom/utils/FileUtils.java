package com.mrshiehx.mschatroom.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * 遍历文件夹下的文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return null;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f);
                }
            }
        }
        return list;
    }

    /**
     * 删除文件
     *
     * @param target 目标文件
     * @return
     */
    public static boolean deleteFiles(File target) {
        List<File> files = getFile(target);
        if (files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                /**  如果是文件则删除  如果都删除可不必判断  */
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        return true;
    }

    /**
     * 向文件中添加内容
     *
     * @param strcontent 内容
     * @param subfile    目标文件
     */
    public static void writeToFile(String strcontent, File subfile) {
        //生成文件夹之后，再生成文件，不然会出错
        // 每次写入时，都换行写
        RandomAccessFile raf = null;
        try {
            /**   构造函数 第二个是读写方式    */
            raf = new RandomAccessFile(subfile, "rw");
            /**  将记录指针移动到该文件的最后  */
            raf.seek(subfile.length());
            /** 向文件末尾追加内容  */
            raf.write(strcontent.getBytes());
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件内容（覆盖或者添加）
     *
     * @param file    目标文件
     * @param content 覆盖内容
     * @param append  指定了写入的方式，是覆盖写还是追加写(true=追加)(false=覆盖)
     */
    public static void modifyFile(File file, String content, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(file, append);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.append(content);
        writer.flush();
        writer.close();
    }

    /**
     * 读取文件内容
     *
     * @param target 目标File对象
     * @return 返回文件内容
     */
    public static String getString(File target) throws IOException {
        FileInputStream s=new FileInputStream(target);
        String b=getString(s);
        try{
            s.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public static String getString(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        //inputStream.close();
        inputStreamReader.close();
        reader.close();
        return sb.toString();
    }


    public static String readFileByBytes(InputStream in) throws IOException {
        byte[] tempbytes = new byte[100];
        int byteread = 0;
        // 读入多个字节到字节数组中，byteread为一次读入的字节数
        StringBuilder builder = new StringBuilder();
        while ((byteread = in.read(tempbytes)) != -1) {
            builder.append(new String(tempbytes));
        }
        in.close();
        return builder.toString();
    }

    public static byte[] toByteArray(File file) throws IOException {
        FileChannel fc = new RandomAccessFile(file, "r").getChannel();
        MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                fc.size()).load();
        //System.out.println(byteBuffer.isLoaded());
        byte[] result = new byte[(int) fc.size()];
        if (byteBuffer.remaining() > 0) {
            // System.out.println("remain");
            byteBuffer.get(result, 0, byteBuffer.remaining());
        }
        fc.close();
        return result;
    }

    public static byte[] getFileBytes(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("File is too big");
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File value : fileList) {
                    // 如果下面还有文件
                    if (value.isDirectory()) {
                        size = size + getFolderSize(value);
                    } else {
                        size = size + value.length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;

        if (size==0) {
            return "0.00MB";
        }
        if (kiloByte < 1) {
            return (int)size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static void bytes2File(byte[] bytes, File file) throws IOException {
        StreamUtils.bytes2File(bytes,file);
    }

    public static void copy(File source, File to) throws IOException {
        if(!to.getParentFile().exists())to.getParentFile().mkdirs();
        if(!to.exists())to.createNewFile();
        InputStream input = new FileInputStream(source);
        OutputStream output = new FileOutputStream(to);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) != -1) {
            output.write(buf, 0, bytesRead);
        }
        input.close();
        output.close();
    }
}
