package com.mrshiehx.mschatroom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.Variables;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 获得账户信息工具类
 * Login method
 * 0 is account
 * 1 is email
 */
public class GetAccountUtils {
    /**
     * Don't need thread
     * @return result
     */
    public static boolean isLogined(){
        return MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED,false);
    }
    /**
     * Need thread
     * @return result
     */
    public static boolean checkCanLogin(AccountUtils accountUtils,Context context) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        SharedPreferences sharedPreferences=MSCRApplication.getSharedPreferences();
        String method;
        String[] accountOrEmailAndPassword;
        if(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1)==1) {
            method = AccountUtils.BY_EMAIL;
            accountOrEmailAndPassword = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
        } else{
            method = AccountUtils.BY_ACCOUNT;
            accountOrEmailAndPassword = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
        }
        //Toast.makeText(context, EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0],Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1],Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, checkCanLogin(context,method,EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0],Variables.TEXT_ENCRYPTION_KEY),EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1],Variables.TEXT_ENCRYPTION_KEY))+"", Toast.LENGTH_SHORT).show();
        return checkCanLogin(accountUtils,context,method,EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0],Variables.TEXT_ENCRYPTION_KEY),EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1],Variables.TEXT_ENCRYPTION_KEY));
    }

    /**
     * Need thread
     * @return result
     */
    public static boolean checkCanLogin(AccountUtils accountUtils,Context context, String loginMethod, String accountOrEmailEncrypted, String passwordEncrypted){
        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.loginNoThreadAndDialog(context,loginMethod,accountOrEmailEncrypted,passwordEncrypted);
    }

    /**
     * Need thread
     * @return emailEncrypted
     */
    /*public static String getEmail(Context context) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        SharedPreferences sharedPreferences=MSCRApplication.getSharedPreferences();
        String[] accountAndPassword=EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
        return getEmail(context,accountAndPassword[0]);
    }*/
    /**
     * Need thread
     * @return emailEncrypted
     */
    public static String getEmail(AccountUtils accountUtils, Context context, String accountEncrypted){
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getStringNoThread(context,"email","account",accountEncrypted);
    }

    /**
     * Need thread
     * @return accountEncrypted
     */
    /*public static String getAccount(Context context) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        SharedPreferences sharedPreferences=MSCRApplication.getSharedPreferences();
        String[] emailAndPassword=EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
        return getAccount(context,emailAndPassword[0]);
    }*/
    /**
     * Need thread
     * @return accountEncrypted
     */
    public static String getAccount(AccountUtils accountUtils, Context context, String emailEncrypted){
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getStringNoThread(context,"account","email",emailEncrypted);
    }
    /**
     * Need thread
     * @return userInformation
     */
    public static InputStream getUserInformation(AccountUtils accountUtils, Context context, String emailEncrypted,String accountEncrypted, String passwordEncrypted){
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getUserInformationNoThread(context,emailEncrypted,accountEncrypted,passwordEncrypted);
    }
}
