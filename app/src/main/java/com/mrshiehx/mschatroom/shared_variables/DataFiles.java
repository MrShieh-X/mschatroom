package com.mrshiehx.mschatroom.shared_variables;

import android.os.Environment;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFiles {
    public static final File IMAGES_DIR = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "images");
    public static final File CHATS_DIR = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "chats");
    public static final File CHAT_AVATARS_DIR = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "chat_avatars");
    public static final File INFORMATION_DIR = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "informations");
    public static final File CHATS_FILE = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "chats.json");
    public static final File INTERNAL_DATA_DIR = new File(Environment.getExternalStorageDirectory(), "MSChatRoom");
    public static final File PICTURES_DIR = new File(INTERNAL_DATA_DIR, "Pictures");
    public static final File INTERNAL_FILES_DIR = new File(INTERNAL_DATA_DIR, "Files");//For save
    public static final File FILES_DIR = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "files");//For storage

    public static List<File> getUserFilesInFilesDir() {
        List<File> files = new ArrayList<>();
        files.add(IMAGES_DIR);
        files.add(CHATS_DIR);
        files.add(CHAT_AVATARS_DIR);
        files.add(INFORMATION_DIR);
        files.add(CHATS_FILE);
        files.add(FILES_DIR);
        return files;
    }

    public static List<File> getChangelessFilesInFilesDir() {
        List<File> files = new ArrayList<>();
        files.add(IMAGES_DIR);
        files.add(CHATS_DIR);
        files.add(CHAT_AVATARS_DIR);
        files.add(INFORMATION_DIR);
        files.add(CHATS_FILE);
        files.add(FILES_DIR);
        files.add(PICTURES_DIR);
        files.add(INTERNAL_FILES_DIR);
        files.add(INTERNAL_DATA_DIR);
        return files;
    }
}
