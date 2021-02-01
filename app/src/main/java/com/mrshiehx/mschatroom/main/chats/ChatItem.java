package com.mrshiehx.mschatroom.main.chats;

import androidx.annotation.NonNull;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.File;

public class ChatItem {
    private String emailOrAccount;
    private String name;
    private String latestMsg;
    private String latestMsgDate;

    public ChatItem(@NonNull String emailOrAccount, String name) {
        this(emailOrAccount,name,null,null);
    }

    public ChatItem(@NonNull String emailOrAccount, String name, String latestMsg, String latestMsgDate) {
        this.emailOrAccount = emailOrAccount;
        this.name = name;
        this.latestMsg=latestMsg;
        this.latestMsgDate=latestMsgDate;
    }

    public String getEmailOrAccount(){
        return emailOrAccount;
    }

    public String getAvatarFilePAN() {
        return new File(Utils.getDataFilesPath(MSCRApplication.getContext()),"chat_avatars"+File.separator+emailOrAccount).getAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public void setEmailOrAccount(String emailOrAccount){
        this.emailOrAccount=emailOrAccount;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getLatestMsg(){
        return latestMsg;
    }

    public String getLatestMsgDate() {
        return latestMsgDate;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public void setLatestMsgDate(String latestMsgDate) {
        this.latestMsgDate = latestMsgDate;
    }
}
