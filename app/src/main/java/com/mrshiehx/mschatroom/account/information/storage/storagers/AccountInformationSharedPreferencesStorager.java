package com.mrshiehx.mschatroom.account.information.storage.storagers;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.AccountInformationStorageProvider;
import com.mrshiehx.mschatroom.beans.AccountInformation;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;

public class AccountInformationSharedPreferencesStorager implements AccountInformationStorageProvider {
    public boolean isLogined(){
        return MSChatRoom.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION);
    }

    public String getMainAccountAndPassword(){
        return MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION,"");
    }

    public long putAccount(String content){
        AccountInformationDatabaseStorager dbs=new AccountInformationDatabaseStorager();
        if(dbs.isLogined()){
            dbs.deleteAccount();
        }
        MSChatRoom.getSharedPreferences().edit().putString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION,content).apply();
        return 0;
    }

    public int deleteAccount(){
        AccountInformationDatabaseStorager dbs=new AccountInformationDatabaseStorager();
        MSChatRoom.getSharedPreferences().edit().remove(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION).apply();
        if(dbs.isLogined()){
            dbs.deleteAccount();
        }
        return 0;
    }

    public String getInformation(){
        try{
            return MSChatRoom.getSharedPreferences().getString(String.format(Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME, EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(getMainAccountAndPassword()).split(Variables.SPLIT_SYMBOL)[0].toUpperCase())),"");
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public int putInformation(String content){
        try{
            MSChatRoom.getSharedPreferences().edit().putString(String.format(Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME, EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(getMainAccountAndPassword()).split(Variables.SPLIT_SYMBOL)[0].toUpperCase())),content).apply();
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
