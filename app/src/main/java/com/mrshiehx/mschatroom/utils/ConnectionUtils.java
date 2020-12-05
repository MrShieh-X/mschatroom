package com.mrshiehx.mschatroom.utils;

import android.util.Log;

import com.mrshiehx.mschatroom.Variables;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

//MySQL连接工具类
public class ConnectionUtils {
    static String serverAddress;

    public ConnectionUtils(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /*public static JdbcUtils getInstance(){
        if (instance ==null){
            instance = new JdbcUtils(Variables.SERVER_ADDRESS);
        }
        return instance;
    }*/
    public Connection getConnection(String dbName, String name, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + serverAddress + "/" + dbName,
                    name, password);
        } catch (Exception e) {
            Log.e(Variables.TAG, "failed to connection server");
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection(String file) {
        File f = new File(file);
        if (!f.exists()) {
            return null;
        } else {
            Properties pro = new Properties();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                pro.load(new FileInputStream(f));
                String url = pro.getProperty("url");
                String name = pro.getProperty("name");
                String password = pro.getProperty("password");
                return DriverManager.getConnection(url, name, password);
            } catch (Exception e) {
                Log.e(Variables.TAG, "failed to connection server");
                return null;
            }
        }
    }
}

