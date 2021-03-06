package com.mrshiehx.mschatroom.start.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.beans.AccountInformation;
import com.mrshiehx.mschatroom.beans.UserInformation;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageTypes;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.services.AutoReconnectService;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.StreamUtils;
import com.mrshiehx.mschatroom.utils.StringUtils;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * 加载界面 Loading
 */
public class StartActivity extends Activity {
    /**
     * tp = temp
     * sa = Server Address
     * dn = Database Name
     * dtn = Database User Name
     * dp = Database User Password
     * dtn = Database Table Name
     */
    Context context = StartActivity.this;
    TextView appName;
    long firstTime;
    //String sat, dnt, dunt,dpt,dtnt;
    TextView log;
    LinearLayout log_layout;
    ScrollView log_sv;
    //AccountInformation Variables.ACCOUNT_INFORMATION;
    public static final int OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK = 0;
    public static final int OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.ACCOUNT_INFORMATION = new AccountInformation();
        Utils.initializationNoTheme(StartActivity.this, R.string.app_name);
        makeStatusBarBeTranslucent();
        setContentView(R.layout.activity_loading);
        appName = findViewById(R.id.loading_app_name);
        log = findViewById(R.id.log);
        log_layout = findViewById(R.id.log_layout);
        log_sv = findViewById(R.id.log_sv);

        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        /*boolean c = MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_LANGUAGE);
        if (c) {
            appName.setText(Utils.getStringByLocale(context, R.string.app_name, MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_")[0], MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_")[1]));
        }
        if (MSCRApplication.getSharedPreferences().getBoolean("show_logs_on_loading_interface", true)) {
            log_layout.setVisibility(View.VISIBLE);
        } else {
            log_layout.setVisibility(View.GONE);
        }

        if (log_layout.getVisibility() == View.VISIBLE) {
            log.setText(String.format(getString(R.string.loadinglog_main_information), Utils.getVersionName(context), Utils.valueOf(Utils.getVersionCode(context))));
            newLog(getString(R.string.loadinglog_start_loading));
        }*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ps = shouldShowRequestPermissionRationale(Variables.PERMISSIONS[0]);
        }
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Variables.PERMISSIONS[0]);
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            startRequestPermission();
        } else {
        }*/
        mainMethod();

    }


    void mainMethod() {
        newLog(getString(R.string.loadinglog_checking_network));
        if (Utils.isNetworkConnected(context)) {
            newLog(getString(R.string.loadinglog_network_connected));
            startConn();
        } else {
            newLog(getString(R.string.loadinglog_network_not_connected));
            offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK);
        }
    }

    void failedConnectSSI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newLog(getString(R.string.loadinglog_failed_connect_setinfo));
                Toast.makeText(context, R.string.toast_failed_connect_to_server, Toast.LENGTH_SHORT).show();
                offlineMode(OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
            }
        });
    }

    void startConn() {
        startConn(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME, Variables.SERVER_PORT);
    }

    void startConn(final String sa, final String dn, final String dun, final String dp, final String dtn, final int sp) {
        /**
         *  连接，检测是否可行
         */
        newLog(getString(R.string.loadinglog_start_connecting));
        new Thread(() -> {
            Looper.prepare();
            //Connection connection=new ConnectionUtils(sa).getConnection(dn, dun, dp);
            final AccountUtils accountUtils = new AccountUtils(dn, dun, dp, dtn);
            boolean closed = true;
            try {
                closed = accountUtils.getConnection().isClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (accountUtils.getConnection() != null && !closed) {
                Variables.ACCOUNT_UTILS = accountUtils;
                /**
                 * Non Null
                 */

                newLog(getString(R.string.loadinglog_success_connect));
                newLog(getString(R.string.loadinglog_checking_login_status));

                /**
                 * 检测能不能登录
                 */
                if (GetAccountUtils.isLogined()) {
                    newLog(getString(R.string.loadinglog_logined));
                    newLog(getString(R.string.loadinglog_checking_can_login));

                    try {
                        if (GetAccountUtils.checkCanLogin(accountUtils, context)) {
                            newLog(getString(R.string.loadinglog_result_can_login));
                            newLog(getString(R.string.loadinglog_getting_account_information));
                            String accountEncrypted = "";
                            //String passwordEncrypted = "";
                            //String accountClean="";
                            String emailEncrypted = "";
                            //String emailClean="";
                            String by;
                            //if (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
                            /**Account*/
                            by = AccountUtils.BY_ACCOUNT;
                            try {
                                accountEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword(), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0], Variables.TEXT_ENCRYPTION_KEY);
                                //passwordEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY);
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
                            //Toast.makeText(context, EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword(), Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();

                            byte[] info = GetAccountUtils.getUserInformation(accountUtils, context, accountEncrypted);
                            Variables.ACCOUNT_INFORMATION.setAccountE(accountEncrypted);
                            Variables.ACCOUNT_INFORMATION.setEmailE(emailEncrypted);
                            Variables.ACCOUNT_INFORMATION.setInformation(info);
                            newLog(getString(R.string.loadinglog_success_get_account_information));
                            byte[] avatar;
                            try {
                                newLog(getString(R.string.loadinglog_getting_account_avatar));
                                avatar = accountUtils.getBytesWithException(context, "avatar", by, accountEncrypted);

                                File file = new File(Utils.getDataFilesPath(context), "avatar_" + Variables.ACCOUNT_INFORMATION.getAccountE());
                                if (avatar != null && avatar.length != 0 && !Utils.isBytesAllZero(avatar)) {
                                    try {
                                        if (file.exists()) file.delete();
                                        file.createNewFile();
                                        StreamUtils.bytes2File(avatar, file);
                                        newLog(getString(R.string.loadinglog_success_get_account_avatar));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        newLog(String.format(getString(R.string.loadinglog_failed_get_account_avatar), e));
                                    }

                                } else {
                                    file.delete();
                                    newLog(getString(R.string.loadinglog_account_avatar_is_empty));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Variables.ACCOUNT_INFORMATION.setAvatar(avatar);

                            //if (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
                            /*MSCRApplication.getSharedPreferences().edit().putString(
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
                                            Variables.TEXT_ENCRYPTION_KEY)).apply();*/
                            AccountInformationStorager.putInformation(EnDeCryptTextUtils.encrypt(
                                    String.format(
                                            Variables.SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT,
                                            "email",
                                            emailEncrypted,
                                            Variables.ACCOUNT_INFORMATION.getNickname(),
                                            Variables.ACCOUNT_INFORMATION.getGender(),
                                            Variables.ACCOUNT_INFORMATION.getWhatsup()),
                                    Variables.TEXT_ENCRYPTION_KEY));
                            /*} else {
                                MSCRApplication.getSharedPreferences().edit().putString(
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
                            }*/


                            //Variables.ACCOUNT_INFORMATION = Variables.ACCOUNT_INFORMATION;


                            newLog(getString(R.string.loadinglog_downloading_messages));
                            try {
                                File chatsFile = DataFiles.CHATS_FILE;
                                String messages;
                                messages = accountUtils.getString(context, "messages", AccountUtils.BY_ACCOUNT, Utils.valueOf(Variables.ACCOUNT_INFORMATION.getAccountE()).toUpperCase());

                                if (!TextUtils.isEmpty(messages)) {
                                    JSONArray head;
                                    try {
                                        head = new JSONArray(messages);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        head = new JSONArray();
                                    }

                                    List<ChatItem> chatItemList = /*new Gson().fromJson(, new TypeToken<List<ChatItem>>(){}.getType())*/new ArrayList<>();
                                    if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_RECEIVE_STRANGERS_OFFLINE_MESSAGES, false)) {
                                        String var;
                                        try {
                                            var = FileUtils.getString(chatsFile);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            var = "";
                                        }
                                        JSONArray var2;
                                        if (var.length() != 0) {
                                            try {
                                                var2 = new JSONArray(var);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                var2 = new JSONArray();
                                            }
                                        } else {
                                            var2 = new JSONArray();
                                        }
                                        for (int i = 0; i < var2.length(); i++) {
                                            JSONObject var3 = var2.getJSONObject(i);
                                            chatItemList.add(new ChatItem(var3.optString("emailOrAccount"), var3.optString("name")));
                                        }
                                    }
                                    for (int i = 0; i < head.length(); i++) {
                                        JSONObject jsonObject = head.getJSONObject(i);
                                        String acco = jsonObject.optString("account");
                                        if (!TextUtils.isEmpty(acco)) {
                                            JSONArray messagesArray = jsonObject.optJSONArray("messages");
                                            if (messagesArray != null) {
                                                File chatFile = new File(DataFiles.CHATS_DIR, acco + ".json");
                                                if (!chatFile.getParentFile().exists()) {
                                                    chatFile.getParentFile().mkdirs();
                                                }
                                                if (!chatFile.exists()) {
                                                    chatFile.createNewFile();
                                                }
                                                String chatFileContent = FileUtils.getString(chatFile);
                                                if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_RECEIVE_STRANGERS_OFFLINE_MESSAGES, false)) {

                                                    boolean contains = false;
                                                    for (int j = 0; j < chatItemList.size(); j++) {
                                                        ChatItem item = chatItemList.get(j);
                                                        if (acco.toLowerCase().equals(item.getEmailOrAccount().toLowerCase())) {
                                                            contains = true;
                                                            break;
                                                        }
                                                    }

                                                    if (!contains) {
                                                        /**not exists*/
                                                        chatItemList.add(new ChatItem(acco, ""));
                                                    }
                                                }


                                                JSONArray var3;
                                                try {
                                                    var3 = new JSONArray(chatFileContent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    var3 = new JSONArray();
                                                }

                                                for (int j = 0; j < messagesArray.length(); j++) {
                                                    JSONObject var4 = messagesArray.optJSONObject(j);
                                                    if (var4 != null) {
                                                        String var5 = StringUtils.unicodeToText(var4.optString("text"));
                                                        int var6 = var4.optInt("type");
                                                        long time = var4.optLong("time");
                                                        JSONObject var7 = new JSONObject();
                                                        var7.put("y", var6);
                                                        //long millis=System.currentTimeMillis();
                                                        /**code for send types*/
                                                        if (var6 == MessageTypes.PICTURE.code) {
                                                            File var8 = new File(DataFiles.IMAGES_DIR, Utils.valueOf(time));
                                                            if (!var8.getParentFile().exists())
                                                                var8.getParentFile().mkdirs();
                                                            if (var8.exists()) var8.delete();
                                                            var8.createNewFile();
                                                            var7.put("c", Utils.valueOf(time));
                                                            try {
                                                                StreamUtils.hexWrite(var4.optString("text"), var8);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else if (var6 == MessageTypes.FILE.code) {
                                                            var7.put("c", var4.optString("text"));
                                                            var7.put("f", var4.optString("fileName"));
                                                            var7.put("i", var4.optLong("fileSize"));
                                                        } else {
                                                            var7.put("c", var5);
                                                        }
                                                        /**code for send types*/
                                                        var7.put("t", MessageItem.TYPE_RECEIVER);

                                                        var7.put("s", time);
                                                        boolean shouldAddTime = false;

                                                        JSONObject jsonObject1 = new JSONObject();

                                                        jsonObject1.put("c", /*MessageItem.toTimeString(time)*/String.valueOf(time));
                                                        jsonObject1.put("s", time);
                                                        jsonObject1.put("t", MessageItem.TYPE_TIME);
                                                        jsonObject1.put("y", MessageTypes.TEXT.code);
                                                        if (j == 0 || var3.length() == 0) {
                                                            shouldAddTime = true;
                                                        } else {
                                                            List<Integer> types = new ArrayList<>();
                                                            List<MessageItem> list = new ArrayList<>();
                                                            for (int ij = 0; ij < var3.length(); ij++) {
                                                                list.add(MessageItem.valueOf(var3.optJSONObject(ij)));
                                                            }
                                                            for (MessageItem item : list) {
                                                                types.add(item.getType());
                                                            }
                                                            int indexOf = types.lastIndexOf(MessageItem.TYPE_TIME);
                                                            Time Time = new Time();
                                                            Time.set(time);
                                                            int year = Time.year;
                                                            /**START FROM 1*/
                                                            int month = Time.month + 1;
                                                            int day = Time.monthDay;
                                                            int hour = Time.hour;
                                                            int minute = Time.minute;
                                                            MessageItem timeItem = list.get(indexOf);
                                                            long timeYMDAndHM = timeItem.getTime();
                                                            Time time1 = new Time();
                                                            time1.set(timeYMDAndHM);
                                                            int yearF = time1.year;
                                                            int monthF = time1.month + 1;
                                                            int dayF = time1.monthDay;
                                                            int hourF = time1.hour;
                                                            int minuteF = time1.minute;
                                                            //int xiangcha=minute-minuteF;
                                                            //Toast.makeText(context, Utils.valueOf(yearF==year&&monthF==month&&dayF==day&&hourF==hour), Toast.LENGTH_SHORT).show();
                                                            if (list.size() > 0) {
                                                                if (yearF == year && monthF == month && dayF == day && hourF == hour) {
                                                                    int xiangcha = 6;
                                                                    try {
                                                                        Time timm = new Time();
                                                                        timm.set(list.get(list.size() - 1).getTime());
                                                                        xiangcha = minute - timm.minute;
                                                                    } catch (Throwable ignored) {
                                                                    }
                                                                    if (xiangcha >= 5) {
                                                                        //YES
                                                                        shouldAddTime = true;
                                                                    }
                                                                } else {
                                                                    //YES
                                                                    shouldAddTime = true;
                                                                }
                                                            } else {
                                                                shouldAddTime = true;
                                                            }

                                                        }
                                                        if (shouldAddTime) {
                                                            var3.put(jsonObject1);
                                                        }
                                                        var3.put(var7);
                                                    }
                                                }
                                                try {
                                                    FileUtils.modifyFile(chatFile, Utils.valueOf(var3), false);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }


                                    if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_RECEIVE_STRANGERS_OFFLINE_MESSAGES, false)) {
                                        JSONArray var3 = new JSONArray();
                                        for (ChatItem item : chatItemList) {
                                            JSONObject var4 = new JSONObject();
                                            String var5 = item.getEmailOrAccount();
                                            String var6 = item.getName();
                                            //String var7=item.getLatestMsg();
                                            //String var8=item.getLatestMsgDate();
                                            if (!TextUtils.isEmpty(var5))
                                                var4.put("emailOrAccount", var5);
                                            if (!TextUtils.isEmpty(var6)) var4.put("name", var5);
                                            //if(!TextUtils.isEmpty(var7))var4.put("latestMsg",var6);
                                            //if(!TextUtils.isEmpty(var8))var4.put("latestMsgDate",var7);
                                            var3.put(var4);
                                        }
                                        try {
                                            FileUtils.modifyFile(chatsFile, var3.toString(), false);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    accountUtils.setString(context, "messages", "", AccountUtils.BY_ACCOUNT, Utils.valueOf(Variables.ACCOUNT_INFORMATION.getAccountE()).toUpperCase());


/*
                                    JSONObject jsonObject = new JSONObject(messages);
                                    List<ChatItem> chatItemList = new Gson().fromJson(FileUtils.getString(com.mrshiehx.mschatroom.shared_variables.DataFiles.CHATS_FILE), new TypeToken<List<ChatItem>>() {
                                    }.getType());

                                    for (int in = 0; in < chatItemList.size(); in++) {

                                        JSONArray resultArray = jsonObject.optJSONArray(chatItemList.get(in).getEmailOrAccount());

                                        if (resultArray != null) {
                                            ChatItem item = chatItemList.get(in);
                                            String eoa = item.getEmailOrAccount();
                                            File chatFile = new File(Utils.getDataFilesPath(context), "chats" + File.separator + eoa + ".json");
                                            String chatFileContent = FileUtils.getString(chatFile);
                                            List<MessageItem> messageItemListNew = new ArrayList<>();
                                            List<MessageItem> messageItemList;
                                            for (int i = 0; i < resultArray.length(); i++) {
                                                String message = resultArray.getString(i);
                                                if (!TextUtils.isEmpty(message)) {
                                                    *//**
                                     * 开始添加到<chat>.json
                                     *//*
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
                                            if (chatFile.exists()) {
                                                chatFile.delete();
                                            } else {
                                                new File(Utils.getDataFilesPath(context), "chats").mkdirs();
                                            }
                                            chatFile.createNewFile();
                                            FileUtils.modifyFile(chatFile, messagesArray.toString(), false);
                                        }
                                    }*/
                                    newLog(getString(R.string.loadinglog_success_download_messages));
                                } else {
                                    newLog(getString(R.string.loadinglog_messages_is_empty));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                newLog(getString(R.string.loadinglog_failed_download_messages));
                            }


                            /**
                             * 加载聊天
                             */
                            loadChats(accountUtils);

                            newLog(R.string.loadinglog_connecting_communication_server);
                            //if (Variables.COMMUNICATOR == null) {
                            //Toast.makeText(context, emailEncrypted, Toast.LENGTH_SHORT).show();
                            Variables.COMMUNICATOR = new Communicator(context, accountEncrypted, emailEncrypted);
                            try {
                                if (!Variables.COMMUNICATOR.connect()) {/*
                                    newLog(R.string.loadinglog_success_connect_communication_server);
                                } else {*/
                                    newLog(R.string.loadinglog_failed_connect_communication_server);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                newLog(String.format(getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""));
                            }
                            /*} else {
                                newLog(R.string.loadinglog_success_connect_communication_server);
                            }*/

                            newLog(getString(R.string.loadinglog_finish_loading));
                            startService(new Intent(this, AutoReconnectService.class));
                            Intent intent = new Intent(context, MainScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
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
                                            MSChatRoom.getInstance().exit();
                                        }
                                    });
                                    dialog.setPositiveButton(getResources().getString(R.string.dialog_no_login_button_gotologin_text), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(context, LoginScreen.class);
                                            try {
                                                intent.putExtra("account", GetAccountUtils.getEmailOrAccount());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Utils.exceptionDialog(context, e);
                                            }
                                            startService(new Intent(context, AutoReconnectService.class));
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    dialog.setCancelable(false);
                                    dialog.show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e);
                    }
                } else {
                    startService(new Intent(context, AutoReconnectService.class));
                    Intent intent = new Intent(context, LoginScreen.class);
                    startActivity(intent);
                }

            } else {
                /**
                 * Null
                 */
                failedConnectSSI();
            }
            Looper.loop();
        }).start();
    }


    void offlineMode(int mode) {
        Intent intent = new Intent(context, AccountInformationStorager.isLogined() ? MainScreen.class : LoginScreen.class);
        switch (mode) {
            case OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK:
                intent.putExtra("offlineMode", OFFLINE_MODE_CANNOT_CONNECT_TO_NETWORK);
                if (AccountInformationStorager.isLogined()) {
                    makeOfflineAccountInformation();
                }
                break;
            case OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER:
                intent.putExtra("offlineMode", OFFLINE_MODE_CANNOT_CONNECT_TO_SERVER);
                if (AccountInformationStorager.isLogined()) {
                    makeOfflineAccountInformation();
                }
                intent.putExtra("doNotConnectToServer", true);
                break;
        }
        startService(new Intent(context, AutoReconnectService.class));
        startActivity(intent);
        finish();
    }

    void loadChats(AccountUtils au) {
        File chatsFile = DataFiles.CHATS_FILE;
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
            boolean status = au.tryLoginWithoutPasswordInt(context, by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
            if (status) {
                /**
                 * 如果账号存在
                 */
                try {
                    byte[] avatar = au.getBytesWithException(context, "avatar", by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    File cafile = DataFiles.CHAT_AVATARS_DIR;
                    File avatarFile = new File(cafile, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    if (avatar != null && avatar.length != 0 && !Utils.isBytesAllZero(avatar)) {
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
                        /*int index;
                        byte[] bytes = new byte[1024];
                        FileOutputStream downloadFile = new FileOutputStream(avatarFile);
                        while ((index = avatar.read(bytes)) != -1) {
                            downloadFile.write(bytes, 0, index);
                            downloadFile.flush();
                        }*/
                            //Utils.inputStream2File(new ByteArrayInputStream(avatar),avatarFile);
                            FileUtils.bytes2File(avatar, avatarFile);

                            //downloadFile.close();
                            //avatar.close();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] info = au.getBytes(context, "information", by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                //byte[] infob = au.getBytes(context,"information", by, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                File infoFile = new File(DataFiles.INFORMATION_DIR, EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY) + ".json");
                File infoFolder = DataFiles.INFORMATION_DIR;

                if (infoFile.exists()) {
                    infoFile.delete();
                } else {
                    if (!infoFolder.exists()) {
                        infoFolder.mkdirs();
                    }
                }

                infoFile.createNewFile();
                StreamUtils.bytes2File(info, infoFile);


                String accountName = "";

                try {
                    if (info != null && info.length != 0) {
                        UserInformation userInformation = UserInformationUtils.read(context, info);
                        if (userInformation != null)
                            accountName = userInformation.nameContent;//干净的
                    }
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
                        account = au.getString(context,"account",by,EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
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
                    String emailOrAccountS = jsonObject.optString("emailOrAccount");

                    /*jsonObject.put("avatarFilePAN", Utils.getDataFilesPath(context)+File.separator+"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY));
                    String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context),"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.optString("avatarFilePAN");
                    }*/

                    //String latestMsgS = jsonObject.optString("latestMsg");
                    //String latestMsgDateS = jsonObject.optString("latestMsgDate");
                    item = new ChatItem(emailOrAccountS, "");
                } else {
                    //昵称不空，set it！
                    JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                    jsonObject.put("name", EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY));
                    String emailOrAccountS = jsonObject.optString("emailOrAccount");
                    /*String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context),"chat_avatars"+File.separator+ EnDeCryptTextUtils.encrypt(eoa, Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.optString("avatarFilePAN");
                    }
                    String latestMsgS = jsonObject.optString("latestMsg");
                    String latestMsgDateS = jsonObject.optString("latestMsgDate");*/
                    item = new ChatItem(emailOrAccountS, EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY));
                }
            } else {
                JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                String name = jsonObject.optString("name");
                String emailOrAccountS = jsonObject.optString("emailOrAccount");
                /*String avatarS = jsonObject.optString("avatarFilePAN");
                String latestMsgS = jsonObject.optString("latestMsg");
                String latestMsgDateS = jsonObject.optString("latestMsgDate");*/
                item = new ChatItem(emailOrAccountS, name);
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
            String name = jsonObject.optString("name");
            String emailOrAccountS = jsonObject.optString("emailOrAccount");/*
            String avatarS = jsonObject.optString("avatarFilePAN");
            String latestMsgS = jsonObject.optString("latestMsg");
            String latestMsgDateS = jsonObject.optString("latestMsgDate");*/
            item = new ChatItem(emailOrAccountS, name);
        }
        items.add(item);
        //content.clear();
        //content.add(item);
    }

    public static void makeOfflineAccountInformation() {
        String informationByText = "";
        String accountE = "";
        String emailE;
        //if (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) != 1) {
        //account
        try {
            accountE =
                    EnDeCryptTextUtils.encrypt(
                            EnDeCryptTextUtils.decrypt(
                                    AccountInformationStorager.getMainAccountAndPassword(),
                                    Variables.TEXT_ENCRYPTION_KEY)
                                    .split(Variables.SPLIT_SYMBOL)[0],
                            Variables.TEXT_ENCRYPTION_KEY);
            informationByText = EnDeCryptTextUtils.decrypt(AccountInformationStorager.getInformation(), Variables.TEXT_ENCRYPTION_KEY);
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
        String[] infos = informationByText.split(";");
        emailE = infos[0].split("=")[1];
        /*} else {
            accountE = infos[0].split("=")[1];
        }*/
        String abstractNickname = infos[1];
        String abstractGender = infos[2];
        String abstractWhatsup = infos[3];
        String nickname = abstractNickname.endsWith("=") ? "" : abstractNickname.split("=")[1];
        String gender = abstractGender.endsWith("=") ? "" : abstractGender.split("=")[1];
        String whatsup = abstractWhatsup.endsWith("=") ? "" : abstractWhatsup.split("=")[1];
        AccountInformation accountInformation = new AccountInformation();
        /*try {
            File file = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "avatar_" + accountE);
            if (file.exists())
                accountInformation.setAvatar(FormatTools.bytes2Drawable(FileUtils.toByteArray(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        accountInformation.setAccountE(accountE);
        accountInformation.setEmailE(emailE);
        accountInformation.setNickname(nickname);
        accountInformation.setGender(gender);
        accountInformation.setWhatsup(whatsup);
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
                MSChatRoom.getInstance().exit();
            }
            //super.onBackPressed();
        }else{
            super.onKeyUp(keyCode, event);
        }
        return true;
    }

    void newLog(@StringRes int resId) {
        newLog(getString(resId));
    }

    void newLog(final String content) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        final String time = simpleDateFormat.format(new Date());
        Log.i("MSCRStartActivity", time + content);
    }

    public void sessionCreated(IoSession session) {

    }

    public void sessionOpened(IoSession session) {

    }

    public void sessionClosed(IoSession session) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
    }

    void makeStatusBarBeTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}