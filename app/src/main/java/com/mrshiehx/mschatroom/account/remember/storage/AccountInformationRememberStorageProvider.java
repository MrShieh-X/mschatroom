package com.mrshiehx.mschatroom.account.remember.storage;

public interface AccountInformationRememberStorageProvider {
    boolean isRemember();
    String getContent();
    long putContent(String content);
    int delete();
}
