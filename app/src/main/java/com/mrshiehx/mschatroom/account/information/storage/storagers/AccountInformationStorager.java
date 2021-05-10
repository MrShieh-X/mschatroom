package com.mrshiehx.mschatroom.account.information.storage.storagers;

import com.mrshiehx.mschatroom.account.information.storage.AccountInformationStorageProvider;

public class AccountInformationStorager {
    public static final AccountInformationStorageProvider provider=new AccountInformationDatabaseStorager();
    public static boolean isLogined(){
        return provider.isLogined();
    }

    public static String getMainAccountAndPassword(){
        return provider.getMainAccountAndPassword();
    }

    public static long putAccount(String content){
        return provider.putAccount(content);
    }

    public static int deleteAccount(){
        return provider.deleteAccount();
    }

    public static String getInformation(){
        return provider.getInformation();
    }

    public static int putInformation(String content){
        return provider.putInformation(content);
    }
}
