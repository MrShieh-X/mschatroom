package com.mrshiehx.mschatroom.account.remember.storage.storagers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrshiehx.mschatroom.account.remember.storage.AccountInformationRememberStorageProvider;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.database.MSCRDatabase0Helper;
import com.mrshiehx.mschatroom.database.utils.DatabaseUtils;

public class AccountInformationRememberDatabaseStorager implements AccountInformationRememberStorageProvider {
    @Override
    public boolean isRemember() {
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
            Cursor cursor = sqLiteDatabase.query(MSCRDatabase0Helper.REM_TABLE_NAME, null, null, null, null, null, null);
            boolean b = cursor.getCount() > 0;
            cursor.close();
            sqLiteDatabase.close();
            a.close();
            return b;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getContent() {
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            Cursor cursor = getCursor();
            String s = cursor.getString(cursor.getColumnIndex(MSCRDatabase0Helper.REM_COLUMN_ACCOUNT_OR_EMAIL_AND_PASSWORD));
            cursor.close();
            a.close();
            return s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long putContent(String content) {
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationWritableDatabase(MSChatRoom.getContext());
            sqLiteDatabase.execSQL("drop table " + MSCRDatabase0Helper.REM_TABLE_NAME);
            sqLiteDatabase.execSQL(MSCRDatabase0Helper.CREATE_REM_COMMAND);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MSCRDatabase0Helper.REM_COLUMN_ACCOUNT_OR_EMAIL_AND_PASSWORD, content);
            long i = sqLiteDatabase.insert(MSCRDatabase0Helper.REM_TABLE_NAME, null, contentValues);
            sqLiteDatabase.close();
            a.close();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete() {
        try {
            //MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationWritableDatabase(MSChatRoom.getContext());
            sqLiteDatabase.execSQL("drop table " + MSCRDatabase0Helper.REM_TABLE_NAME);
            sqLiteDatabase.execSQL(MSCRDatabase0Helper.CREATE_REM_COMMAND);
            /*int s = sqLiteDatabase.delete(MSCRDatabase0Helper.REM_TABLE_NAME, MSCRDatabase0Helper.REM_COLUMN_ACCOUNT_OR_EMAIL_AND_PASSWORD + " = ?", new String[]{getMainAccountAndPassword()});
            sqLiteDatabase.close();
            a.close();
            return s;*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    private Cursor getCursor(){
        SQLiteDatabase sqLiteDatabase=DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
        Cursor cursor=sqLiteDatabase.query(MSCRDatabase0Helper.REM_TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }

    public MSCRDatabase0Helper createAccountInformationDatabaseHelper(){
        return new MSCRDatabase0Helper(MSChatRoom.getContext(), MSCRDatabase0Helper.DATABASE_NAME,null,1);
    }

}
