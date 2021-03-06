package com.mrshiehx.mschatroom.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

//MySQL连接工具类
public class ConnectionUtils {

    /*public static JdbcUtils getInstance(){
        if (instance ==null){
            instance = new JdbcUtils(Variables.SERVER_ADDRESS);
        }
        return instance;
    }*/
    public static Connection getConnection(String serverAddress, String dbName, String name, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + serverAddress + "/" + dbName,
                    name, password);
        } catch (Exception e) {
            Log.e(Variables.TAG, "failed to connection server");
            e.printStackTrace();
        }
        return null;
    }

    public static ProgressDialog showConnectingDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        //dialog.setTitle(context.getString(R.string.dialog_title_wait));
        dialog.setMessage(context.getString(R.string.dialog_connecting_message));
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
}

