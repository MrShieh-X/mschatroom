package com.mrshiehx.mschatroom.main.chats;

public class ChatItem {
    private String emailOrAccount;
    private String avatarFilePAN;
    private String name;
    private String latestMsg;
    private String latestMsgDate;

    public ChatItem(String emailOrAccount,String avatarFilePAN, String name, String latestMsg, String latestMsgDate) {
        this.emailOrAccount = emailOrAccount;
        this.avatarFilePAN = avatarFilePAN;
        this.name = name;
        this.latestMsg = latestMsg;
        this.latestMsgDate = latestMsgDate;
    }

    public String getEmailOrAccount(){
        return emailOrAccount;
    }

    public String getAvatarFilePAN() {
        return avatarFilePAN;
    }

    public String getName() {
        return name;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public String getLatestMsgDate(){
        return latestMsgDate;
    }

    public void setEmailOrAccount(String emailOrAccount){
        this.emailOrAccount=emailOrAccount;
    }
    public void setAvatarFilePAN(String avatarFilePAN){
        this.avatarFilePAN=avatarFilePAN;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setLatestMsg(String latestMsg){
        this.latestMsg=latestMsg;
    }

    public void setLatestMsgDate(String latestMsgDate){
        this.latestMsgDate=latestMsgDate;
    }

    @Override
    public String toString() {
        return "Chat [emailOrAccount="+emailOrAccount+", avatarFilePAN=" + avatarFilePAN + ", name=" + name + ", latestMsg=" + latestMsg + ", latestMsgDate=" + latestMsgDate + "]\n\n";
    }
}
