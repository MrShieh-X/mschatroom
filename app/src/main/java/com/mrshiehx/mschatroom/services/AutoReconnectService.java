package com.mrshiehx.mschatroom.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;

public class AutoReconnectService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        reconnect();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=30*1000;
        long triggerTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoReconnectService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    void reconnect(){
        //Toast.makeText(this, "reconnect", Toast.LENGTH_SHORT).show();
        if(Utils.isNetworkConnected(this)) {
            new Thread(()->{
                Looper.prepare();
                boolean v = true;
                if(Variables.ACCOUNT_UTILS!=null) {
                    try {
                        v = Variables.ACCOUNT_UTILS.getConnection().isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Variables.ACCOUNT_UTILS == null || Variables.ACCOUNT_UTILS.getConnection() == null || v) {
                    Utils.reloadInThread(this,false,null,false,true);
                }
                if(AccountInformationStorager.isLogined()) {
                    if (Variables.COMMUNICATOR == null) {
                        String b = Utils.valueOf(Utils.getAccountInformation().getAccountE());
                        String c = Utils.valueOf(Utils.getAccountInformation().getEmailE());
                        Variables.COMMUNICATOR = new Communicator(this, b, c);
                            try {
                                Variables.COMMUNICATOR.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, String.format(getString(R.string.loadinglog_failed_connect_communication_server_withcause), e.toString()), Toast.LENGTH_SHORT).show();
                            }
                    }
                }
                Looper.loop();
            }).start();

        }
    }
}
