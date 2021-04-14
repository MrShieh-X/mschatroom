package com.mrshiehx.mschatroom.shared_variables;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFiles {
    public static final File IMAGES_DIR = new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "images");
    public static final File CHATS_DIR = new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "chats");
    public static final File CHAT_AVATARS_DIR = new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "chat_avatars");
    public static final File INFORMATION_DIR = new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "information");
    public static final File CHATS_FILE = new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "chats.json");

    public static List<File> getChangelessFilesInFilesDir() {
        List<File> files = new ArrayList<>();
        files.add(IMAGES_DIR);
        files.add(CHATS_DIR);
        files.add(CHAT_AVATARS_DIR);
        files.add(INFORMATION_DIR);
        files.add(CHATS_FILE);
        return files;
    }
}
