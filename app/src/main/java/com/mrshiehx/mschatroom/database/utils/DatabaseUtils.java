package com.mrshiehx.mschatroom.database.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mrshiehx.mschatroom.database.MSCRDatabase0Helper;

public class DatabaseUtils {
    public static SQLiteDatabase getAccountInformationWritableDatabase(Context context){
        return getWritableDatabase(context, MSCRDatabase0Helper.DATABASE_NAME);
    }
    public static SQLiteDatabase getAccountInformationReadableDatabase(Context context){
        return getReadableDatabase(context, MSCRDatabase0Helper.DATABASE_NAME);
    }

    public static SQLiteDatabase getWritableDatabase(Context context, String name){
        return new MSCRDatabase0Helper(context,name,null,1).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context, String name){
        return new MSCRDatabase0Helper(context,name,null,1).getReadableDatabase();
    }
}
