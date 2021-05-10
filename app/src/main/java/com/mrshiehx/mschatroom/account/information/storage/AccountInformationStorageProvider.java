package com.mrshiehx.mschatroom.account.information.storage;

public interface AccountInformationStorageProvider {
    boolean isLogined();
    String getMainAccountAndPassword();
    long putAccount(String content);
    int deleteAccount();
    String getInformation();
    int putInformation(String content);
}
