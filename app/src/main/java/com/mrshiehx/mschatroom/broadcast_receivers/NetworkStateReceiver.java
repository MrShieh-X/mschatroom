package com.mrshiehx.mschatroom.broadcast_receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.chat.screen.ChatScreen;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.account.profile.screen.AccountProfileScreen;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.utils.Utils;

public class NetworkStateReceiver extends BroadcastReceiver {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WIFI = 1;
    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int netWorkStates = getNetWorkStates(context);
        if (netWorkStates == TYPE_MOBILE || netWorkStates == TYPE_WIFI) {
            if (context instanceof MainScreen) {
                ((Activity) context).setTitle(context.getString(R.string.app_name));
                if (i != 0) {
                    Utils.reload(context,true);
                }
            } else if (context instanceof SettingsScreen) {
                ((Activity) context).setTitle(context.getString(R.string.activity_settings_screen_name));
                if (i != 0) {
                    Utils.reload(context,true);
                }
            } else if (context instanceof AccountProfileScreen) {
                ((Activity) context).setTitle(context.getString(R.string.activity_modify_user_information_screen_name));
                ((AccountProfileScreen) context).changeEnabledOfPreferencesOfEnabled(true);
                if (i != 0) {
                    Utils.reload(context,true);
                }
            } else if (context instanceof ChatScreen) {
                //((Activity)context).setTitle(context.getString(R.string.activity_modify_user_information_screen_name));
                if (i != 0)
                    ((ChatScreen) context).whenNetworkConnected();
            }
        } else {
            if (context instanceof MainScreen) {
                ((Activity) context).setTitle(context.getString(R.string.activity_main_screen_offline_mode_name));
                ((MainScreen) context).onDisconnectNetwork();
            } else if (context instanceof SettingsScreen) {
                ((Activity) context).setTitle(context.getString(R.string.activity_settings_screen_offline_mode_name));
                ((SettingsScreen) context).onDisconnectNetwork();
            } else if (context instanceof AccountProfileScreen) {
                ((Activity) context).setTitle(context.getString(R.string.activity_modify_user_information_screen_offline_mode_name));
                ((AccountProfileScreen) context).changeEnabledOfPreferencesOfEnabled(false);
                ((AccountProfileScreen) context).onDisconnectNetwork();
            } else if (context instanceof ChatScreen) {
                //((Activity)context).setTitle(context.getString(R.string.activity_modify_user_information_screen_name));
                ((ChatScreen) context).onDisconnectNetwork();
            }
        }
        i++;
    }

    public static int getNetWorkStates(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return TYPE_NONE;//没网
        }
        int type = activeNetworkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return TYPE_MOBILE;//移动数据
            case ConnectivityManager.TYPE_WIFI:
                return TYPE_WIFI;//WIFI
            default:
                break;
        }
        return TYPE_NONE;
    }
}
