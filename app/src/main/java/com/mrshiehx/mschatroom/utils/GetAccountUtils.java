package com.mrshiehx.mschatroom.utils;

import android.content.Context;

import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 获得账户信息工具类
 *
 * Login method
 * 0 is account
 * 1 is email
 */
public class GetAccountUtils {
    /**
     * Don't need thread
     *
     * @return result
     */
    public static boolean isLogined() {
        return AccountInformationStorager.isLogined();
    }

    /**
     * Need thread
     *
     * @return result
     */
    public static boolean checkCanLogin(AccountUtils accountUtils, Context context) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (isLogined()) {
            String method;
            String[] accountOrEmailAndPassword;
            method = AccountUtils.BY_ACCOUNT;
            accountOrEmailAndPassword = EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword(), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);

            //Toast.makeText(context, EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0],Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1],Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, checkCanLogin(context,method,EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0],Variables.TEXT_ENCRYPTION_KEY),EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1],Variables.TEXT_ENCRYPTION_KEY))+"", Toast.LENGTH_SHORT).show();
            return checkCanLogin(accountUtils, context, method, EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[0], Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountOrEmailAndPassword[1], Variables.TEXT_ENCRYPTION_KEY));
        }
        return false;
    }

    public static String getEmailOrAccount() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (!AccountInformationStorager.isLogined())
            return "";

        return EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword(), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];

    }

    /**
     * Need thread
     *
     * @return result
     */
    public static boolean checkCanLogin(AccountUtils accountUtils, Context context, String loginMethod, String accountOrEmailEncrypted, String passwordEncrypted) {
        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.login(context, loginMethod, accountOrEmailEncrypted, passwordEncrypted);
    }

    /**
     * Need thread
     * @return emailEncrypted
     */
    /*public static String getEmail(Context context) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        SharedPreferences sharedPreferences=MSCRApplication.getSharedPreferences();
        String[] accountAndPassword=EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
        return getEmail(context,accountAndPassword[0]);
    }*/

    /**
     * Need thread
     *
     * @return emailEncrypted
     */
    public static String getEmail(AccountUtils accountUtils, Context context, String accountEncrypted) {
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getString(context, "email", "account", accountEncrypted);
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
     *
     * @return accountEncrypted
     */
    public static String getAccount(AccountUtils accountUtils, Context context, String emailEncrypted) {
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getString(context, "account", "email", emailEncrypted);
    }

    /**
     * Need thread
     *
     * @return userInformation
     */
    public static byte[] getUserInformation(AccountUtils accountUtils, Context context, String accountEncrypted) {
        //AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);
        return accountUtils.getBytes(context, "information", AccountUtils.BY_ACCOUNT, accountEncrypted);
    }

    /**
     * @return [0] is encrypted account, [1] is encrypted email
     **/
    public static String[] getAnotherIDAndSelf(AccountUtils accountUtils, Context context, String eoaEncrypted) {
        String eoa = "";
        try {
            eoa = EnDeCryptTextUtils.decrypt(eoaEncrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String account;
        String email;
        if (Utils.isEmail(eoa)) {
            email = eoaEncrypted;
            account = GetAccountUtils.getAccount(accountUtils, context, email);
        } else {
            account = eoaEncrypted;
            email = GetAccountUtils.getEmail(accountUtils, context, account);
        }
        return new String[]{account, email};
    }

    public static String getPasswordDecrypted(){
        String password="";
        try {
            password = EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword()).split(Variables.SPLIT_SYMBOL)[1];
        }catch (Exception e){
            e.printStackTrace();
        }
        return password;
    }

    public static String getPasswordEncrypted(){
        String password="";
        try {
            password = EnDeCryptTextUtils.encrypt(getPasswordDecrypted());
        }catch (Exception e){
            e.printStackTrace();
        }
        return password;
    }
}
