package com.mrshiehx.mschatroom.account.remember.storage.storagers;

import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.remember.storage.AccountInformationRememberStorageProvider;
import com.mrshiehx.mschatroom.MSChatRoom;

public class AccountInformationRememberSharedPreferencesStorager implements AccountInformationRememberStorageProvider {
    @Override
    public boolean isRemember() {
        return MSChatRoom.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT);
    }

    @Override
    public String getContent() {
        return MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT,"");
    }

    @Override
    public long putContent(String content) {
        MSChatRoom.getSharedPreferences().edit().putString(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT,content).apply();
        return 0;
    }

    @Override
    public int delete() {
        MSChatRoom.getSharedPreferences().edit().remove(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT).apply();
        return 0;
    }
}
