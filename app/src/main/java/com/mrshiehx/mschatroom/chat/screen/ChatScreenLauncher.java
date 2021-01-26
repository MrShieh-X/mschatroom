package com.mrshiehx.mschatroom.chat.screen;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatScreenLauncher {
    Context context;
    String targetUserAccountOrEmailEncrypted;
    String name;
    boolean canContinue;
    public ChatScreenLauncher(Context context, @NonNull String targetUserAccountOrEmailEncrypted, @Nullable String name, boolean canContinue){
        this.context=context;
        this.targetUserAccountOrEmailEncrypted=targetUserAccountOrEmailEncrypted;
        this.name=name;
        this.canContinue=canContinue;
    }

    public void startChatScreen(){
        Intent intent=new Intent(context,ChatScreen.class);
        if(!TextUtils.isEmpty(name)) {
            intent.putExtra("name", name);
            intent.putExtra("eoa",targetUserAccountOrEmailEncrypted);
            intent.putExtra("canContinue",canContinue);
        }
        context.startActivity(intent);
    }

    public void setTargetUserAccountOrEmailEncrypted(String targetUserAccountOrEmailEncrypted) {
        this.targetUserAccountOrEmailEncrypted = targetUserAccountOrEmailEncrypted;
    }

    public void setName(String name) {
        this.name = name;
    }
}
