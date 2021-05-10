package com.mrshiehx.mschatroom.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MSCRDatabase0Helper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="mscrdb0.db";
    public static final String REM_TABLE_NAME="rmb";
    public static final String REM_COLUMN_ACCOUNT_OR_EMAIL_AND_PASSWORD="aoeap";
    public static final String AI_TABLE_NAME="ai";
    public static final String AI_COLUMN_ACCOUNT_AND_PASSWORD="aap";
    public static final String AI_COLUMN_INFORMATION="i";
    public static final String CREATE_ACCOUNT_AND_PASSWORD_COMMAND="create table "+AI_TABLE_NAME+"("+AI_COLUMN_ACCOUNT_AND_PASSWORD+" varchar(1000) not null, "+AI_COLUMN_INFORMATION+" varchar(1000000000))";
    public static final String CREATE_REM_COMMAND="create table "+REM_TABLE_NAME+"("+REM_COLUMN_ACCOUNT_OR_EMAIL_AND_PASSWORD+" varchar(1000))";

    public MSCRDatabase0Helper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT_AND_PASSWORD_COMMAND);
        db.execSQL(CREATE_REM_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
