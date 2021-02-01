package com.mrshiehx.mschatroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.XMLUtils;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * 加载界面 Loading
 * //@see_code CODE1
 * //@see_code CODE2
 * //@see_code CODE3
 * //@see_code CODE4
 * //@see_code CODE5
 *
 * @see_codes CODE6
 */
public class LoadingScreen extends Activity {
    /**
     * tp = temp
     * sa = Server Address
     * dn = Database Name
     * dtn = Database User Name
     * dp = Database User Password
     * dtn = Database Table Name
     */
    Context context = LoadingScreen.this;
    TextView appName;
    /*boolean isNetworkConnected;
    boolean canLogin;
    boolean logined;*/
    long firstTime;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //String sat, dnt, dunt,dpt,dtnt;
    TextView log;
    LinearLayout log_layout;
    ScrollView log_sv;
    boolean ps;
    AlertDialog.Builder dialog_no_permissions;
    AlertDialog dialog2;
    String tpSa, tpDn, tpDun, tpDp, tpDtn,tpSp;
    boolean isFirstRun;
    //AccountInformation Variables.ACCOUNT_INFORMATION;
    public static final int OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK = 0;
    public static final int OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER = 1;
    Thread loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.ACCOUNT_INFORMATION = new AccountInformation();
        sharedPreferences = MSCRApplication.getSharedPreferences();
        editor = sharedPreferences.edit();
        isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        /**CODE6S*/
        if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true)
                && TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""))
                && (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT, 0)==0)
                && TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""))
                && TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""))
                && TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""))
                && TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""))) {
            editor.putString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, Variables.DEFAULT_SERVER_ADDRESS);
            editor.putInt(Variables.SHARED_PREFERENCE_SERVER_PORT, Variables.DEFAULT_SERVER_PORT);
            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_NAME, Variables.DEFAULT_DATABASE_NAME);
            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, Variables.DEFAULT_DATABASE_USER_NAME);
            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, Variables.DEFAULT_DATABASE_USER_PASSWORD);
            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, Variables.DEFAULT_DATABASE_TABLE_NAME);
            editor.apply();
        }
        /**CODE6E*/
        Utils.initialization(LoadingScreen.this, R.string.app_name, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_loading);
        appName = findViewById(R.id.loading_app_name);
        log = findViewById(R.id.log);
        log_layout = findViewById(R.id.log_layout);
        log_sv = findViewById(R.id.log_sv);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        boolean c = MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE);
        if (c) {
            appName.setText(Utils.getStringByLocale(context, R.string.app_name, sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_")[0], sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_")[1]));
        }
        if (sharedPreferences.getBoolean("show_logs_on_loading_interface", true)) {
            log_layout.setVisibility(View.VISIBLE);
        } else {
            log_layout.setVisibility(View.GONE);
        }

        if (log_layout.getVisibility() == View.VISIBLE) {
            log.setText(String.format(getString(R.string.loadinglog_main_information), Utils.getVersionName(context), String.valueOf(Utils.getVersionCode(context))));
            newLog(getString(R.string.loadinglog_start_loading));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ps = shouldShowRequestPermissionRationale(Variables.PERMISSIONS[0]);
        }
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Variables.PERMISSIONS[0]);
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            startRequestPermission();
        } else {
            mainMethod();
        }

    }


    void mainMethod() {
        newLog(getString(R.string.loadinglog_checking_network));
        if (Utils.isNetworkConnected(context)) {

            Variables.ACCOUNT_INFORMATION.setIsNetworkConnected(true);
            newLog(getString(R.string.loadinglog_network_connected));
            newLog(getString(R.string.loadinglog_preparing_server_information));
            if (isFirstRun) {
                firstUseSSI();
            } else {
                if (TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""))
                        || TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""))
                        || (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT, 0)==0)
                        || TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""))
                        || TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""))
                        || TextUtils.isEmpty(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""))) {
                    missingSSI();
                } else {
                    startConn();
                }
            }
        } else {
            Variables.ACCOUNT_INFORMATION.setIsNetworkConnected(false);
            newLog(getString(R.string.loadinglog_network_not_connected));
            if (!Utils.isNetworkConnected(context)) {
                AlertDialog.Builder noNetworkDialog = new AlertDialog.Builder(context);
                noNetworkDialog.setTitle(context.getResources().getString(R.string.dialog_no_network_title));
                noNetworkDialog.setCancelable(false);
                noNetworkDialog.setMessage(context.getResources().getString(R.string.dialog_no_network_message));
                noNetworkDialog.setPositiveButton(context.getResources().getString(R.string.dialog_set_server_information_button_retry_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainMethod();
                    }
                });
                noNetworkDialog.setNegativeButton(context.getResources().getString(R.string.dialog_no_network_button_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MSCRApplication.getInstance().exit();
                    }
                });
                noNetworkDialog.setNeutralButton(getString(R.string.dialog_button_offline_mode_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**CODE1*/
                        offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK);
                    }
                });
                noNetworkDialog.show();

            }
        }


        //return isNetworkConnected(context);




        /*if (Utils.isNetworkConnected(context)) {
            load.start();
        } else {
            AlertDialog.Builder noNetworkDialog = new AlertDialog.Builder(context);
            noNetworkDialog.setTitle(context.getResources().getString(R.string.dialog_no_network_title))
                    .setMessage(context.getResources().getString(R.string.dialog_no_network_message));
            noNetworkDialog.setPositiveButton(context.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Utils.startActivity(context, MainScreen.class);
                }
            });
            noNetworkDialog.setNegativeButton(context.getResources().getString(R.string.dialog_no_network_button_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MSCRApplication.getInstance().exit();
                }
            });
            noNetworkDialog.setCancelable(false);
            noNetworkDialog.show();

        }*/
    }

    void failedConnectSSI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newLog(getString(R.string.loadinglog_failed_connect_setinfo));
                final AlertDialog.Builder dialog_set_server_information = new AlertDialog.Builder(context);
                dialog_set_server_information.setTitle(getString(R.string.dialog_set_server_information_title));
                dialog_set_server_information.setCancelable(false);
                final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_server_information, null);
                TextView tip = dialogView.findViewById(R.id.dialog_set_server_information_tip);
                tip.setText(getString(R.string.dialog_set_server_information_tip_failed_connect));
                final EditText sa = dialogView.findViewById(R.id.dialog_set_server_information_server_address);
                final EditText sp = dialogView.findViewById(R.id.dialog_set_server_information_server_port);
                final EditText dn = dialogView.findViewById(R.id.dialog_set_server_information_database_name);
                final EditText dun = dialogView.findViewById(R.id.dialog_set_server_information_database_user_name);
                final EditText dp = dialogView.findViewById(R.id.dialog_set_server_information_database_user_password);
                final EditText dtn = dialogView.findViewById(R.id.dialog_set_server_information_database_table_name);
                dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                CheckBox sP = dialogView.findViewById(R.id.dialog_set_server_information_show_password);
                sP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                    }
                });
                sa.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""));
                sp.setText(String.valueOf(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT,80)));
                dn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""));
                dun.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""));
                dp.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""));
                dtn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""));
                dialog_set_server_information.setView(dialogView);
                dialog_set_server_information.setNegativeButton(getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MSCRApplication.getInstance().exit();
                    }
                });
                dialog_set_server_information.setPositiveButton(getString(R.string.dialog_set_server_information_button_retry_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(sa.getText().toString()) ||
                                TextUtils.isEmpty(sa.getText().toString()) ||
                                TextUtils.isEmpty(dn.getText().toString()) ||
                                TextUtils.isEmpty(dun.getText().toString()) ||
                                TextUtils.isEmpty(dp.getText().toString()) ||
                                TextUtils.isEmpty(dtn.getText().toString())) {
                            newLog(getString(R.string.loadinglog_empty_content));
                            failedConnectSSI();
                        } else {
                    /*Variables.SERVER_ADDRESS = sa.getText().toString();
                    Variables.DATABASE_NAME = dn.getText().toString();
                    Variables.DATABASE_USER = dun.getText().toString();
                    Variables.DATABASE_PASSWORD = dp.getText().toString();
                    Variables.DATABASE_TABLE_NAME = dtn.getText().toString();*/
                            tpSa = sa.getText().toString();
                            tpSp = sp.getText().toString();
                            tpDn = dn.getText().toString();
                            tpDun = dun.getText().toString();
                            tpDp = dp.getText().toString();
                            tpDtn = dtn.getText().toString();
                            newLog(getString(R.string.loadinglog_setinfo_success));
                            startConn(tpSa, tpDn, tpDun, tpDp, tpDtn, true,Integer.parseInt(tpSp));
                        }
                    }
                });
                dialog_set_server_information.setNeutralButton(getString(R.string.dialog_set_server_information_button_retry_operations), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String[] operations = {
                                getString(R.string.dialog_button_offline_mode_text),
                                getString(R.string.dialog_set_server_information_button_retry_save_text),
                                getString(android.R.string.cancel)
                        };
                        AlertDialog.Builder so = new AlertDialog.Builder(context);
                        so.setItems(operations, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        /**CODE5*/
                                        offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
                                        break;
                                    case 1:
                                        if (TextUtils.isEmpty(sa.getText().toString()) ||
                                                TextUtils.isEmpty(dn.getText().toString()) ||
                                                TextUtils.isEmpty(dun.getText().toString()) ||
                                                TextUtils.isEmpty(dp.getText().toString()) ||
                                                TextUtils.isEmpty(dtn.getText().toString())) {
                                            newLog(getString(R.string.loadinglog_empty_content));
                                            failedConnectSSI();
                                        } else {
                    /*Variables.SERVER_ADDRESS = sa.getText().toString();
                    Variables.DATABASE_NAME = dn.getText().toString();
                    Variables.DATABASE_USER = dun.getText().toString();
                    Variables.DATABASE_PASSWORD = dp.getText().toString();
                    Variables.DATABASE_TABLE_NAME = dtn.getText().toString();*/
                                            editor.putString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, sa.getText().toString());
                                            editor.putInt(Variables.SHARED_PREFERENCE_SERVER_PORT, Integer.parseInt(sp.getText().toString()));
                                            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_NAME, dn.getText().toString());
                                            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, dun.getText().toString());
                                            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, dp.getText().toString());
                                            editor.putString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, dtn.getText().toString());
                                            editor.apply();
                                            newLog(getString(R.string.loadinglog_setinfo_success));
                                            startConn();
                                        }

                                        break;
                                    case 2:
                                        failedConnectSSI();
                                        break;
                                }
                            }
                        });
                        so.show();


                    }
                });
                dialog_set_server_information.show();
            }
        });
    }

    void startConn() {
        startConn(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""),
                sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""),
                sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""),
                sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""),
                sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""),
                false,
                sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT, 80));
    }

    void startConn(final String sa, final String dn, final String dun, final String dp, final String dtn, final boolean showSaveDialog,final int sp) {
        /**
         *  连接，检测是否可行
         */
        newLog(getString(R.string.loadinglog_start_connecting));
        loading = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //Connection connection=new ConnectionUtils(sa).getConnection(dn, dun, dp);
                final AccountUtils accountUtils = new AccountUtils(dn, dun, dp, Variables.DATABASE_TABLE_NAME);
                if (accountUtils.getConnection() != null) {
                    /**
                     * Non Null
                     */
                    Variables.ACCOUNT_UTILS = accountUtils;
                    Variables.SERVER_ADDRESS = sa;
                    Variables.SERVER_PORT = sp;
                    Variables.DATABASE_NAME = dn;
                    Variables.DATABASE_USER = dun;
                    Variables.DATABASE_PASSWORD = dp;
                    Variables.DATABASE_TABLE_NAME = dtn;

                    newLog(getString(R.string.loadinglog_success_connect));
                    newLog(getString(R.string.loadinglog_checking_login_status));

                    /**
                     * 检测能不能登录
                     */
                    if (GetAccountUtils.isLogined()) {
                        Variables.ACCOUNT_INFORMATION.setLogined(true);
                        newLog(getString(R.string.loadinglog_logined));
                        newLog(getString(R.string.loadinglog_checking_can_login));

                        try {
                            if (GetAccountUtils.checkCanLogin(accountUtils, context)) {
                                Variables.ACCOUNT_INFORMATION.setCanLogin(true);

                                newLog(getString(R.string.loadinglog_result_can_login));
                                newLog(getString(R.string.loadinglog_getting_account_information));
                                String accountEncrypted = "";
                                String passwordEncrypted = "";
                                //String accountClean="";
                                String emailEncrypted = "";
                                //String emailClean="";
                                String by;
                                String getAvatarByContent = "";
                                if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) == 0) {
                                    /**Account*/
                                    by = AccountUtils.BY_ACCOUNT;
                                    try {
                                        accountEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0], Variables.TEXT_ENCRYPTION_KEY);
                                        passwordEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY);
                                        //accountClean=EnDeCryptTextUtils.decrypt(accountEncrypted,Variables.TEXT_ENCRYPTION_KEY);
                                        emailEncrypted = GetAccountUtils.getEmail(accountUtils, context, accountEncrypted);
                                        //emailClean=EnDeCryptTextUtils.decrypt(emailEncrypted,Variables.TEXT_ENCRYPTION_KEY);
                                    } catch (InvalidKeyException e) {
                                        e.printStackTrace();
                                    } catch (InvalidKeySpecException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchPaddingException e) {
                                        e.printStackTrace();
                                    } catch (IllegalBlockSizeException e) {
                                        e.printStackTrace();
                                    } catch (BadPaddingException e) {
                                        e.printStackTrace();
                                    }
                                    getAvatarByContent = accountEncrypted;
                                } else {
                                    /**Email*/
                                    by = AccountUtils.BY_EMAIL;
                                    try {
                                        emailEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0], Variables.TEXT_ENCRYPTION_KEY);
                                        passwordEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY);
                                        //emailClean=EnDeCryptTextUtils.decrypt(emailEncrypted,Variables.TEXT_ENCRYPTION_KEY);
                                        accountEncrypted = GetAccountUtils.getAccount(accountUtils, context, emailEncrypted);
                                        //accountClean=EnDeCryptTextUtils.decrypt(accountEncrypted,Variables.TEXT_ENCRYPTION_KEY);
                                    } catch (InvalidKeyException e) {
                                        e.printStackTrace();
                                    } catch (InvalidKeySpecException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchPaddingException e) {
                                        e.printStackTrace();
                                    } catch (IllegalBlockSizeException e) {
                                        e.printStackTrace();
                                    } catch (BadPaddingException e) {
                                        e.printStackTrace();
                                    }
                                    getAvatarByContent = emailEncrypted;

                                }
                                InputStream info = GetAccountUtils.getUserInformation(accountUtils, context, emailEncrypted, accountEncrypted, passwordEncrypted);
                                Variables.ACCOUNT_INFORMATION.setAccountE(accountEncrypted);
                                Variables.ACCOUNT_INFORMATION.setEmailE(emailEncrypted);
                                Variables.ACCOUNT_INFORMATION.setInformation(info);
                                newLog(getString(R.string.loadinglog_success_get_account_information));
                                InputStream avatar = accountUtils.getInputStreamNoThread(context, "avatar", by, getAvatarByContent);
                                //Variables.ACCOUNT_INFORMATION.setAvatar(avatar);

                                if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) == 0) {
                                    editor.putString(
                                            String.format(
                                                    Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME,
                                                    accountEncrypted),
                                            EnDeCryptTextUtils.encrypt(
                                                    String.format(
                                                            Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT,
                                                            "email",
                                                            emailEncrypted,
                                                            Variables.ACCOUNT_INFORMATION.getNickname(),
                                                            Variables.ACCOUNT_INFORMATION.getGender(),
                                                            Variables.ACCOUNT_INFORMATION.getWhatsup()),
                                                    Variables.TEXT_ENCRYPTION_KEY));
                                } else {
                                    editor.putString(
                                            String.format(
                                                    Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME,
                                                    emailEncrypted),
                                            EnDeCryptTextUtils.encrypt(
                                                    String.format(
                                                            Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT,
                                                            "account",
                                                            accountEncrypted,
                                                            Variables.ACCOUNT_INFORMATION.getNickname(),
                                                            Variables.ACCOUNT_INFORMATION.getGender(),
                                                            Variables.ACCOUNT_INFORMATION.getWhatsup()),
                                                    Variables.TEXT_ENCRYPTION_KEY));
                                }
                                editor.apply();


                                //newLog(Variables.ACCOUNT_INFORMATION.isNetworkConnected() + "/" + Variables.ACCOUNT_INFORMATION.isLogined() + "/" + Variables.ACCOUNT_INFORMATION.isCanLogin() + "/" + Variables.ACCOUNT_INFORMATION.getAccountE() + "/" + Variables.ACCOUNT_INFORMATION.getEmailE() + "/" + String.valueOf(Variables.ACCOUNT_INFORMATION.getAvatar() != null) + "/" + Variables.ACCOUNT_INFORMATION.getGender() + "/" + Variables.ACCOUNT_INFORMATION.getNickname() + "/" + Variables.ACCOUNT_INFORMATION.getWhatsup());

                                newLog(getString(R.string.loadinglog_getting_account_avatar));
                                if (avatar != null) {
                                    try {
                                        if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) == 0) {
                                            Utils.inputStream2File(avatar, new File(Utils.getDataFilesPath(context), "avatar_" + Variables.ACCOUNT_INFORMATION.getAccountE()));
                                        } else {
                                            Utils.inputStream2File(avatar, new File(Utils.getDataFilesPath(context), "avatar_" + Variables.ACCOUNT_INFORMATION.getEmailE()));
                                        }
                                        newLog(getString(R.string.loadinglog_success_get_account_avatar));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        newLog(String.format(getString(R.string.loadinglog_failed_get_account_avatar), e));
                                    }
                                } else {
                                    newLog(getString(R.string.loadinglog_account_avatar_is_empty));
                                }
                                //Variables.ACCOUNT_INFORMATION = Variables.ACCOUNT_INFORMATION;

                                /**
                                 * 加载聊天
                                 */
                                loadChats(accountUtils);

                                newLog(getString(R.string.loadinglog_downloading_messages));
                                try {
                                    String messages;
                                    if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) != 1) {
                                        messages = accountUtils.getStringNoThread(context, "messages", AccountUtils.BY_ACCOUNT, Variables.ACCOUNT_INFORMATION.getAccountE().toString());
                                        accountUtils.setString(context, "messages", "", AccountUtils.BY_ACCOUNT, Variables.ACCOUNT_INFORMATION.getAccountE().toString());
                                    } else {
                                        messages = accountUtils.getStringNoThread(context, "messages", AccountUtils.BY_EMAIL, Variables.ACCOUNT_INFORMATION.getEmailE().toString());
                                        accountUtils.setString(context, "messages", "", AccountUtils.BY_EMAIL, Variables.ACCOUNT_INFORMATION.getEmailE().toString());
                                    }

                                    if (!TextUtils.isEmpty(messages)) {
                                        JSONObject jsonObject = new JSONObject(messages);
                                        List<ChatItem> chatItemList = new Gson().fromJson(FileUtils.getStringNoException(new File(Utils.getDataFilesPath(context), "chats.json")), new TypeToken<List<ChatItem>>() {
                                        }.getType());

                                        for (int in = 0; in < chatItemList.size(); in++) {

                                            JSONArray resultArray = jsonObject.optJSONArray(chatItemList.get(in).getEmailOrAccount());

                                            if (resultArray != null) {
                                                ChatItem item = chatItemList.get(in);
                                                String eoa = item.getEmailOrAccount();
                                                File chatFile = new File(Utils.getDataFilesPath(context), "chats" + File.separator + eoa + ".json");
                                                String chatFileContent = FileUtils.getStringNoException(chatFile);
                                                List<MessageItem> messageItemListNew = new ArrayList<>();
                                                List<MessageItem> messageItemList;
                                                for (int i = 0; i < resultArray.length(); i++) {
                                                    String message = resultArray.getString(i);
                                                    if (!TextUtils.isEmpty(message)) {
                                                        /**
                                                         * 开始添加到<chat>.json
                                                         */
                                                        messageItemListNew.add(new MessageItem(message, MessageItem.TYPE_RECEIVER));
                                                    }
                                                }
                                                messageItemList = new Gson().fromJson(chatFileContent, new TypeToken<List<MessageItem>>() {
                                                }.getType());
                                                messageItemList.addAll(messageItemListNew);


                                                JSONArray messagesArray = new JSONArray();
                                                for (int i = 0; i < messageItemList.size(); i++) {

                                                    String chatStr = new Gson().toJson(messageItemList.get(i));

                                                    JSONObject chatObject;
                                                    try {
                                                        chatObject = new JSONObject(chatStr);
                                                        messagesArray.put(i, chatObject);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if(chatFile.exists()){
                                                    chatFile.delete();
                                                }else{
                                                    new File(Utils.getDataFilesPath(context),"chats").mkdirs();
                                                }
                                                chatFile.createNewFile();
                                                FileUtils.modifyFile(chatFile, messagesArray.toString(), false);
                                            }
                                        }
                                        newLog(getString(R.string.loadinglog_success_download_messages));
                                    }else{
                                        newLog(getString(R.string.loadinglog_messages_is_empty));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    newLog(getString(R.string.loadinglog_failed_download_messages));
                                }

                                boolean showEditAddressAndPortDialog = false;
                                newLog(R.string.loadinglog_connecting_communication_server);
                                if(Variables.COMMUNICATOR==null) {
                                    Variables.COMMUNICATOR=new Communicator(context,(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1)?accountEncrypted:emailEncrypted);
                                    try {
                                        if (Variables.COMMUNICATOR.connect()) {
                                            newLog(R.string.loadinglog_success_connect_communication_server);
                                        } else {
                                            showEditAddressAndPortDialog = true;
                                            newLog(R.string.loadinglog_failed_connect_communication_server);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        showEditAddressAndPortDialog = true;
                                        newLog(String.format(getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""));
                                    }
                                }else{
                                    newLog(R.string.loadinglog_success_connect_communication_server);
                                }

                                newLog(getString(R.string.loadinglog_finish_loading));
                                Intent intent = new Intent(context, MainScreen.class);
                                if (showSaveDialog) {
                                    intent.putExtra("showSaveDialog", true);
                                    intent.putExtra("infos", new String[]{sa, dn, dun, dp, dtn});
                                }
                                if(showEditAddressAndPortDialog){
                                    intent.putExtra("showEditAddressAndPortDialog", true);
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                Variables.ACCOUNT_INFORMATION.setCanLogin(false);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        newLog(getString(R.string.loadinglog_result_cannot_login));
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                        dialog.setTitle(getResources().getString(R.string.dialog_title_notice))
                                                .setMessage(context.getResources().getString(R.string.dialog_failed_login_insettings_message));
                                        dialog.setNeutralButton(getResources().getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MSCRApplication.getInstance().exit();
                                            }
                                        });
                                        dialog.setPositiveButton(getResources().getString(R.string.dialog_no_login_button_gotologin_text), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, LoginScreen.class);
                                                if (showSaveDialog) {
                                                    intent.putExtra("showSaveDialog", true);
                                                    intent.putExtra("infos", new String[]{sa, dn, dun, dp, dtn});
                                                }
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        dialog.setNegativeButton(getString(R.string.dialog_loading_not_logged_in_button_gotomain_text), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                loadChats(accountUtils);
                                                makeOfflineAccountInformation(false, true, true, true);
                                                Intent intent = new Intent(context, MainScreen.class);
                                                if (showSaveDialog) {
                                                    intent.putExtra("showSaveDialog", true);
                                                    intent.putExtra("infos", new String[]{sa, dn, dun, dp, dtn});
                                                }
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Variables.ACCOUNT_INFORMATION.setLogined(false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newLog(getString(R.string.loadinglog_not_login));
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setTitle(getResources().getString(R.string.dialog_title_notice))
                                        .setMessage(getResources().getString(R.string.dialog_no_login_message));
                                dialog.setNeutralButton(getResources().getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MSCRApplication.getInstance().exit();
                                    }
                                });
                                dialog.setPositiveButton(getResources().getString(R.string.dialog_no_login_button_gotologin_text), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, LoginScreen.class);
                                        if (showSaveDialog) {
                                            intent.putExtra("showSaveDialog", true);
                                            intent.putExtra("infos", new String[]{sa, dn, dun, dp, dtn});
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.setNegativeButton(getString(R.string.dialog_loading_not_logged_in_button_gotomain_text), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadChats(accountUtils);
                                        //makeOfflineAccountInformation(false,true,false,true);
                                        Intent intent = new Intent(context, MainScreen.class);
                                        if (showSaveDialog) {
                                            intent.putExtra("showSaveDialog", true);
                                            intent.putExtra("infos", new String[]{sa, dn, dun, dp, dtn});
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.setCancelable(false);
                                dialog.show();
                            }
                        });
                    }

                    /**CODE2*/

                    /**
                     * 是否保存
                     */
                    /*if(showSaveDialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder dialog_retry_connect_success = new AlertDialog.Builder(context);
                                dialog_retry_connect_success.setTitle(getString(R.string.dialog_title_notice));
                                dialog_retry_connect_success.setMessage(getString(R.string.dialog_retry_connect_success_message));
                                dialog_retry_connect_success.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS,sa);
                                        editor.putString(Variables.SHARED_PREFERENCE_DATABASE_NAME,dn);
                                        editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME,dun);
                                        editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD,dp);
                                        editor.putString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME,dtn);
                                        editor.apply();
                                        finish();
                                        Utils.startActivity(context, MainScreen.class);
                                    }
                                });
                                dialog_retry_connect_success.setNegativeButton(getString(android.R.string.no), null);
                                dialog_retry_connect_success.show();

                            }
                        });
                    }*/
                } else {
                    /**
                     * Null
                     */
                    failedConnectSSI();
                }
                Looper.loop();
            }
        });
        loading.start();
    }


    void firstUseSSI() {
        newLog(getString(R.string.loadinglog_firstuse_setinfo));
                /*startDialog(getString(R.string.dialog_set_server_information_tip_firstuse), getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });*/

        AlertDialog.Builder dialog_set_server_information = new AlertDialog.Builder(context);
        dialog_set_server_information.setTitle(getString(R.string.dialog_set_server_information_title));
        dialog_set_server_information.setCancelable(false);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_server_information, null);
        TextView tip = dialogView.findViewById(R.id.dialog_set_server_information_tip);
        tip.setText(getString(R.string.dialog_set_server_information_tip_firstuse));
        final EditText sa = dialogView.findViewById(R.id.dialog_set_server_information_server_address);
        final EditText sp = dialogView.findViewById(R.id.dialog_set_server_information_server_port);
        final EditText dn = dialogView.findViewById(R.id.dialog_set_server_information_database_name);
        final EditText dun = dialogView.findViewById(R.id.dialog_set_server_information_database_user_name);
        final EditText dp = dialogView.findViewById(R.id.dialog_set_server_information_database_user_password);
        final EditText dtn = dialogView.findViewById(R.id.dialog_set_server_information_database_table_name);
        dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
        CheckBox sP = dialogView.findViewById(R.id.dialog_set_server_information_show_password);
        sP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        sa.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""));
        sp.setText(String.valueOf(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT,80)));
        dn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""));
        dun.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""));
        dp.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""));
        dtn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""));
        dialog_set_server_information.setView(dialogView);
        dialog_set_server_information.setNegativeButton(getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MSCRApplication.getInstance().exit();
            }
        });
        dialog_set_server_information.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //newLog(getString(R.string.loadinglog_firstuse_setinfo_success));

                if (TextUtils.isEmpty(sa.getText().toString()) ||
                        TextUtils.isEmpty(sp.getText().toString()) ||
                        TextUtils.isEmpty(dn.getText().toString()) ||
                        TextUtils.isEmpty(dun.getText().toString()) ||
                        TextUtils.isEmpty(dp.getText().toString()) ||
                        TextUtils.isEmpty(dtn.getText().toString())) {
                    newLog(getString(R.string.loadinglog_empty_content));
                    firstUseSSI();
                } else {
                    /*Variables.SERVER_ADDRESS = sa.getText().toString();
                    Variables.DATABASE_NAME = dn.getText().toString();
                    Variables.DATABASE_USER = dun.getText().toString();
                    Variables.DATABASE_PASSWORD = dp.getText().toString();
                    Variables.DATABASE_TABLE_NAME = dtn.getText().toString();*/
                    editor.putString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, sa.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_NAME, dn.getText().toString());
                    editor.putInt(Variables.SHARED_PREFERENCE_SERVER_PORT, Integer.parseInt(sp.getText().toString()));
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, dun.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, dp.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, dtn.getText().toString());
                    editor.apply();
                    newLog(getString(R.string.loadinglog_setinfo_success));
                    startConn();
                }


            }
        });
        dialog_set_server_information.setNeutralButton(getString(R.string.dialog_button_offline_mode_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**CODE3*/
                offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
            }
        });
        dialog_set_server_information.show();
    }


    void missingSSI() {
        newLog(getString(R.string.loadinglog_missing_information));
        AlertDialog.Builder dialog_set_server_information = new AlertDialog.Builder(context);
        dialog_set_server_information.setTitle(getString(R.string.dialog_set_server_information_title));
        dialog_set_server_information.setCancelable(false);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_server_information, null);
        TextView tip = dialogView.findViewById(R.id.dialog_set_server_information_tip);
        tip.setText(getString(R.string.dialog_set_server_information_tip_missing));
        final EditText sa = dialogView.findViewById(R.id.dialog_set_server_information_server_address);
        final EditText sp = dialogView.findViewById(R.id.dialog_set_server_information_server_port);
        final EditText dn = dialogView.findViewById(R.id.dialog_set_server_information_database_name);
        final EditText dun = dialogView.findViewById(R.id.dialog_set_server_information_database_user_name);
        final EditText dp = dialogView.findViewById(R.id.dialog_set_server_information_database_user_password);
        final EditText dtn = dialogView.findViewById(R.id.dialog_set_server_information_database_table_name);
        dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
        CheckBox sP = dialogView.findViewById(R.id.dialog_set_server_information_show_password);
        sP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        sa.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, ""));
        sp.setText(String.valueOf(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_SERVER_PORT,80)));
        dn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME, ""));
        dun.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, ""));
        dp.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, ""));
        dtn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, ""));
        dialog_set_server_information.setView(dialogView);
        dialog_set_server_information.setNegativeButton(getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MSCRApplication.getInstance().exit();
            }
        });
        dialog_set_server_information.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(sa.getText().toString()) ||
                        TextUtils.isEmpty(sp.getText().toString()) ||
                        TextUtils.isEmpty(dn.getText().toString()) ||
                        TextUtils.isEmpty(dun.getText().toString()) ||
                        TextUtils.isEmpty(dp.getText().toString()) ||
                        TextUtils.isEmpty(dtn.getText().toString())) {
                    newLog(getString(R.string.loadinglog_empty_content));
                    missingSSI();
                } else {
                    /*Variables.SERVER_ADDRESS = sa.getText().toString();
                    Variables.DATABASE_NAME = dn.getText().toString();
                    Variables.DATABASE_USER = dun.getText().toString();
                    Variables.DATABASE_PASSWORD = dp.getText().toString();
                    Variables.DATABASE_TABLE_NAME = dtn.getText().toString();*/
                    editor.putString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS, sa.getText().toString());
                    editor.putInt(Variables.SHARED_PREFERENCE_SERVER_PORT, Integer.parseInt(sp.getText().toString()));
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_NAME, dn.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME, dun.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD, dp.getText().toString());
                    editor.putString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME, dtn.getText().toString());
                    editor.apply();
                    newLog(getString(R.string.loadinglog_setinfo_success));
                    startConn();
                }
            }
        });
        dialog_set_server_information.setNeutralButton(getString(R.string.dialog_button_offline_mode_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**CODE4*/
                offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
            }
        });
        dialog_set_server_information.show();
    }

    void offlineMode(int mode) {
        Intent intent = new Intent(context, MainScreen.class);
        switch (mode) {
            case OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK:
                intent.putExtra("offlineMode", OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK);
                if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false)) {
                    makeOfflineAccountInformation(false, false, false, false);
                }
                break;
            case OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER:
                intent.putExtra("offlineMode", OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
                if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false)) {
                    makeOfflineAccountInformation(false, false, false, true);
                }
                break;
        }
        startActivity(intent);
        finish();
    }

    void loadChats(AccountUtils au) {
        File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
        if (chatsFile.exists()) {
            newLog(getString(R.string.loadinglog_start_load_chats));
            Gson gson = new Gson();
            try {
                List<ChatItem> list = gson.fromJson(FileUtils.getString(chatsFile), new TypeToken<List<ChatItem>>() {
                }.getType());
                //CLEAN
                List<String> eoas = new ArrayList<String>();
                for (ChatItem item : list) {
                    eoas.add(EnDeCryptTextUtils.decrypt(item.getEmailOrAccount(), Variables.TEXT_ENCRYPTION_KEY));
                }
                List<ChatItem> items = new ArrayList<>();
                for (int a = 0; a < eoas.size(); a++) {
                    final String eoa = eoas.get(a);
                    if (Utils.isEmail(eoa)) {
                        addForStart(au, eoa, chatsFile, a, items, AccountUtils.BY_EMAIL);
                    } else {
                        addForStart(au, eoa, chatsFile, a, items, AccountUtils.BY_ACCOUNT);
                    }
                }
                Gson gson1 = new Gson();
                JSONArray chatArray = new JSONArray();
                for (int i = 0; i < items.size(); i++) {

                    String chatStr = gson1.toJson(items.get(i));

                    JSONObject chatObject;
                    try {
                        chatObject = new JSONObject(chatStr);
                        chatArray.put(i, chatObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                FileUtils.modifyFile(chatsFile, chatArray.toString(), false);
                newLog(getString(R.string.loadinglog_success_load_chats));
            } catch (Exception e) {
                e.printStackTrace();
                newLog(getString(R.string.loadinglog_failed_load_chats));
            }
        }
    }

    void addForStart(final AccountUtils au, final String eoa, final File chatsFile, final int a, final List<ChatItem> items, final String by) throws Exception {
        ChatItem item = null;
        try {
            //AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            int status = au.tryLoginWithoutPasswordNoThreadAndDialogInt(context, by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
            if (status == 1) {
                /**
                 * 如果账号存在
                 */
                InputStream avatar = au.getInputStreamNoThread(context, "avatar", by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                InputStream info = au.getUserInformationWithoutPasswordNoThread(context, by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                File cafile = new File(Utils.getDataFilesPath(context), "chat_avatars");
                File avatarFile = new File(Utils.getDataFilesPath(context), "chat_avatars" + File.separator + EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                File infoFile = new File(Utils.getDataFilesPath(context), "information" + File.separator + EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY) + ".xml");
                File infoFolder = new File(Utils.getDataFilesPath(context), "information");

                if (infoFile.exists()) {
                    infoFile.delete();
                } else {
                    if (!infoFolder.exists()) {
                        infoFolder.mkdirs();
                    }
                }

                infoFile.createNewFile();
                Utils.inputStream2File(info, infoFile);

                if (avatar != null) {
                    /**
                     * 保存头像
                     */
                    if (!cafile.exists()) {
                        cafile.mkdirs();
                    } else {
                        if (avatarFile.exists()) {
                            avatarFile.delete();
                        }
                    }
                    try {
                        avatarFile.createNewFile();
                        int index;
                        byte[] bytes = new byte[1024];
                        FileOutputStream downloadFile = new FileOutputStream(avatarFile);
                        while ((index = avatar.read(bytes)) != -1) {
                            downloadFile.write(bytes, 0, index);
                            downloadFile.flush();
                        }
                        downloadFile.close();
                        avatar.close();
                        ;
                    } catch (IOException e) {
                        e.printStackTrace();
                        //获得用户“%s”的头像失败
                        newLog(String.format(getString(R.string.loadinglog_failed_get_user_avatar), eoa));
                    }
                } else {
                    //File avatarFile = new File(Utils.getDataFilesPath(context), "chat_avatars"+File.separator+EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    //File avatarFile = new File(Utils.getDataFilesPath(context)+"/chat_avatars", EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    if (avatarFile.exists()) {
                        avatarFile.delete();
                    }
                }
                String accountName = "";
                try {
                    accountName = XMLUtils.readXmlBySAX(info).get(0).getNameContent();//干净的
                } catch (Exception e) {
                    e.printStackTrace();
                    //获得用户“%s”的昵称失败
                    newLog(String.format(getString(R.string.loadinglog_failed_get_user_name), eoa));
                }
                if (TextUtils.isEmpty(accountName)) {
                    /**
                     * 昵称空，获得账号
                     * */
                    /*String account = "";
                    try {
                        //account = au.getAccountByEmail(context, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                        account = au.getStringNoThread(context,"account",by,EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }*/
                    /**
                     * 写入
                     */
                    JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                    jsonObject.put("name", "");
                    String emailOrAccountS = jsonObject.getString("emailOrAccount");

                    /*jsonObject.put("avatarFilePAN", Utils.getDataFilesPath(context)+File.separator+"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context),"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.getString("avatarFilePAN");
                    }*/

                    //String latestMsgS = jsonObject.getString("latestMsg");
                    //String latestMsgDateS = jsonObject.getString("latestMsgDate");
                    item = new ChatItem(emailOrAccountS, "");
                } else {
                    //昵称不空，set it！
                    JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                    jsonObject.put("name", EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY));
                    String emailOrAccountS = jsonObject.getString("emailOrAccount");
                    /*String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context),"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.getString("avatarFilePAN");
                    }
                    String latestMsgS = jsonObject.getString("latestMsg");
                    String latestMsgDateS = jsonObject.getString("latestMsgDate");*/
                    item = new ChatItem(emailOrAccountS, EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY));
                }
            } else if (status == 2) {
                JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                String name = jsonObject.getString("name");
                String emailOrAccountS = jsonObject.getString("emailOrAccount");
                /*String avatarS = jsonObject.getString("avatarFilePAN");
                String latestMsgS = jsonObject.getString("latestMsg");
                String latestMsgDateS = jsonObject.getString("latestMsgDate");*/
                item = new ChatItem(emailOrAccountS, name);
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
            String name = jsonObject.getString("name");
            String emailOrAccountS = jsonObject.getString("emailOrAccount");/*
            String avatarS = jsonObject.getString("avatarFilePAN");
            String latestMsgS = jsonObject.getString("latestMsg");
            String latestMsgDateS = jsonObject.getString("latestMsgDate");*/
            item = new ChatItem(emailOrAccountS, name);
        }
        items.add(item);
        //content.clear();
        //content.add(item);
    }

    void makeOfflineAccountInformation(boolean canLogin, boolean canConnectToServer, boolean logined, boolean isNetworkConnected) {
        String informationByText = "";
        String accountE = "";
        String emailE = "";
        if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) != 1) {
            //account
            String accountEncrypted = "";
            try {
                accountEncrypted =
                        EnDeCryptTextUtils.encrypt(
                                EnDeCryptTextUtils.decrypt(
                                        sharedPreferences.getString(
                                                Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD,
                                                ""),
                                        Variables.TEXT_ENCRYPTION_KEY)
                                        .split(Variables.SPLIT_SYMBOL)[0],
                                Variables.TEXT_ENCRYPTION_KEY);
                informationByText = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(String.format(Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME, accountEncrypted), ""), Variables.TEXT_ENCRYPTION_KEY);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            accountE = accountEncrypted;
        } else {
            //email
            String emailEncrypted = "";
            try {
                emailEncrypted =
                        EnDeCryptTextUtils.encrypt(
                                EnDeCryptTextUtils.decrypt(
                                        sharedPreferences.getString(
                                                Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD,
                                                ""),
                                        Variables.TEXT_ENCRYPTION_KEY)
                                        .split(Variables.SPLIT_SYMBOL)[0],
                                Variables.TEXT_ENCRYPTION_KEY);
                informationByText = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(String.format(Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME, emailEncrypted), ""), Variables.TEXT_ENCRYPTION_KEY);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            emailE = emailEncrypted;
        }
        String[] infos = informationByText.split(";");
        if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) != 1) {
            emailE = infos[0].split("=")[1];
        } else {
            accountE = infos[0].split("=")[1];
        }
        String abstractNickname = infos[1];
        String abstractGender = infos[2];
        String abstractWhatsup = infos[3];
        String nickname = abstractNickname.endsWith("=") ? "" : abstractNickname.split("=")[1];
        String gender = abstractGender.endsWith("=") ? "" : abstractGender.split("=")[1];
        String whatsup = abstractWhatsup.endsWith("=") ? "" : abstractWhatsup.split("=")[1];
        AccountInformation accountInformation = new AccountInformation();
        accountInformation.setAccountE(accountE);
        accountInformation.setEmailE(emailE);
        accountInformation.setNickname(nickname);
        accountInformation.setGender(gender);
        accountInformation.setWhatsup(whatsup);
        accountInformation.setCanLogin(canLogin);
        accountInformation.setCanConnectToServer(canConnectToServer);
        accountInformation.setLogined(logined);
        accountInformation.setIsNetworkConnected(isNetworkConnected);
        Variables.ACCOUNT_INFORMATION = accountInformation;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, getResources().getString(R.string.toast_press_again_exit_application), Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                //loading.stop();
                MSCRApplication.getInstance().exit();
            }
            //super.onBackPressed();
        }
        return super.onKeyUp(keyCode, event);
    }



    /*Object[] startDialog(CharSequence tipText, CharSequence yesButtonText, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder dialog_set_server_information=new AlertDialog.Builder(context);
        dialog_set_server_information.setTitle(getString(R.string.dialog_set_server_information_title));
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_server_information,null);
        TextView tip=dialogView.findViewById(R.id.dialog_set_server_information_tip);
        tip.setText(tipText);
        EditText sa=dialogView.findViewById(R.id.dialog_set_server_information_server_address);
        EditText dn=dialogView.findViewById(R.id.dialog_set_server_information_database_name);
        EditText dun=dialogView.findViewById(R.id.dialog_set_server_information_database_user_name);
        final EditText dp=dialogView.findViewById(R.id.dialog_set_server_information_database_user_password);
        EditText dtn=dialogView.findViewById(R.id.dialog_set_server_information_database_table_name);
        CheckBox sP=dialogView.findViewById(R.id.dialog_set_server_information_show_password);
        sP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    dp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        sa.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_SERVER_ADDRESS,""));
        dn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_NAME,""));
        dun.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME,""));
        dp.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD,""));
        dtn.setText(sharedPreferences.getString(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME,""));
        dialog_set_server_information.setView(dialogView);
        dialog_set_server_information.setNegativeButton(getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MSCRApplication.getInstance().exit();
            }
        });
        dialog_set_server_information.setPositiveButton(yesButtonText,onClickListener);
        dialog_set_server_information.show();
        return new Object[]{new EditText[]{sa,dn,dun,dp,dtn},dialog_set_server_information};
    }*/


    private void startRequestPermission() {
        if (!ps) {
            ActivityCompat.requestPermissions(this, Variables.PERMISSIONS, 321);
        } else {
            showDialogTipUserGoToAppSettting();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (b) {
                        showDialogTipUserGoToAppSettting();
                    } else {
                        MSCRApplication.getInstance().exit();
                    }
                } else {
                    mainMethod();
                }
            }
        }


    }

    private void showDialogTipUserGoToAppSettting() {
        dialog_no_permissions = new AlertDialog.Builder(context);
        dialog_no_permissions.setTitle(getString(R.string.dialog_no_permissions_title))
                .setMessage(getString(R.string.dialog_no_permissions_message))
                .setPositiveButton(getString(R.string.dialog_no_permissions_button_gotosettings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToAppSetting();
                    }
                }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MSCRApplication.getInstance().exit();
            }
        }).setCancelable(false);
        dialog2 = dialog_no_permissions.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int i = ContextCompat.checkSelfPermission(this, Variables.PERMISSIONS[0]);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog2 != null && dialog2.isShowing()) {
                        dialog2.dismiss();
                    }
                    mainMethod();
                }
            }
        }
    }


    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    void newLog(@StringRes int resId) {
        newLog(getString(resId));
    }

    void newLog(final String content) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        final String time = simpleDateFormat.format(new Date());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (log_layout.getVisibility() == View.VISIBLE) {
                    Utils.scrollViewDown(log_sv);
                    log.setText(log.getText() + "\n" + time + content);
                }
            }
        });
        Log.i("MSCRLoadingScreen", time + content);
    }

    public void sessionCreated(IoSession session){

    }
    public void sessionOpened(IoSession session){

    }
    public void sessionClosed(IoSession session){

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Variables.COMMUNICATOR!=null){
            Variables.COMMUNICATOR.setContext(context);
        }
    }
}
