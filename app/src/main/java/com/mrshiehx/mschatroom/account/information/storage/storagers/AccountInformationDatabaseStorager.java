package com.mrshiehx.mschatroom.account.information.storage.storagers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.account.information.storage.AccountInformationStorageProvider;
import com.mrshiehx.mschatroom.database.MSCRDatabase0Helper;
import com.mrshiehx.mschatroom.database.utils.DatabaseUtils;
import com.mrshiehx.mschatroom.exceptions.OperateDatabaseException;

public class AccountInformationDatabaseStorager implements AccountInformationStorageProvider {
    public boolean isLogined(){
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
            Cursor cursor = sqLiteDatabase.query(MSCRDatabase0Helper.AI_TABLE_NAME, null, null, null, null, null, null);
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

    public String getMainAccountAndPassword(){
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase=DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
            Cursor cursor=sqLiteDatabase.query(MSCRDatabase0Helper.AI_TABLE_NAME,null,null,null,null,null,null);
            cursor.moveToFirst();
            if (!isLogined())
                throw new OperateDatabaseException(MSChatRoom.getContext().getString(R.string.preference_account_notlogged_title));
            String s = cursor.getString(cursor.getColumnIndex(MSCRDatabase0Helper.AI_COLUMN_ACCOUNT_AND_PASSWORD));
            cursor.close();
            a.close();
            return s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public long putAccount(String content) {
        try {
            new AccountInformationSharedPreferencesStorager().deleteAccount();
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationWritableDatabase(MSChatRoom.getContext());
            sqLiteDatabase.execSQL("drop table " + MSCRDatabase0Helper.AI_TABLE_NAME);
            sqLiteDatabase.execSQL(MSCRDatabase0Helper.CREATE_ACCOUNT_AND_PASSWORD_COMMAND);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MSCRDatabase0Helper.AI_COLUMN_ACCOUNT_AND_PASSWORD, content);
            long i = sqLiteDatabase.insert(MSCRDatabase0Helper.AI_TABLE_NAME, null, contentValues);
            sqLiteDatabase.close();
            a.close();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteAccount(){
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationWritableDatabase(MSChatRoom.getContext());
            if (!isLogined())
                throw new OperateDatabaseException(MSChatRoom.getContext().getString(R.string.preference_account_notlogged_title));
            int s = sqLiteDatabase.delete(MSCRDatabase0Helper.AI_TABLE_NAME, MSCRDatabase0Helper.AI_COLUMN_ACCOUNT_AND_PASSWORD + " = ?", new String[]{getMainAccountAndPassword()});
            sqLiteDatabase.close();
            a.close();
            AccountInformationSharedPreferencesStorager sharedPreferencesStorager=new AccountInformationSharedPreferencesStorager();
            if(sharedPreferencesStorager.isLogined()){
                sharedPreferencesStorager.deleteAccount();
            }
            return s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public String getInformation(){
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase=DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
            Cursor cursor=sqLiteDatabase.query(MSCRDatabase0Helper.AI_TABLE_NAME,null,null,null,null,null,null);
            cursor.moveToFirst();
            if (!isLogined())
                throw new OperateDatabaseException(MSChatRoom.getContext().getString(R.string.preference_account_notlogged_title));
            String s = cursor.getString(cursor.getColumnIndex(MSCRDatabase0Helper.AI_COLUMN_INFORMATION));
            cursor.close();
            a.close();
            return s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public int putInformation(String content){
        try {
            MSCRDatabase0Helper a = createAccountInformationDatabaseHelper();
            SQLiteDatabase sqLiteDatabase = DatabaseUtils.getAccountInformationWritableDatabase(MSChatRoom.getContext());
            if (!isLogined())
                throw new OperateDatabaseException(MSChatRoom.getContext().getString(R.string.preference_account_notlogged_title));
            ContentValues contentValues = new ContentValues();
            contentValues.put(MSCRDatabase0Helper.AI_COLUMN_INFORMATION, content);
            int i = sqLiteDatabase.update(MSCRDatabase0Helper.AI_TABLE_NAME, contentValues, MSCRDatabase0Helper.AI_COLUMN_ACCOUNT_AND_PASSWORD + " = ?", new String[]{getMainAccountAndPassword()});
            sqLiteDatabase.close();
            a.close();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private Cursor getCursor(){
        SQLiteDatabase sqLiteDatabase=DatabaseUtils.getAccountInformationReadableDatabase(MSChatRoom.getContext());
        Cursor cursor=sqLiteDatabase.query(MSCRDatabase0Helper.AI_TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }

    public MSCRDatabase0Helper createAccountInformationDatabaseHelper(){
        return new MSCRDatabase0Helper(MSChatRoom.getContext(), MSCRDatabase0Helper.DATABASE_NAME,null,1);
    }
}
