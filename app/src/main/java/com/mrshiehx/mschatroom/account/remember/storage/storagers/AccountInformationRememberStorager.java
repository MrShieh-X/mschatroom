package com.mrshiehx.mschatroom.account.remember.storage.storagers;

import com.mrshiehx.mschatroom.account.remember.storage.AccountInformationRememberStorageProvider;

public class AccountInformationRememberStorager {
    public static final AccountInformationRememberStorageProvider provider=new AccountInformationRememberDatabaseStorager();
    public static boolean isRemember() {
        return provider.isRemember();
    }
    public static String getContent() {
        return provider.getContent();
    }
    public static long putContent(String content) {
        return provider.putContent(content);
    }
    public static int delete() {
        return provider.delete();
    }
}
