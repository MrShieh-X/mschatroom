package com.mrshiehx.mschatroom.main.screen;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.about.screen.AboutScreen;
import com.mrshiehx.mschatroom.main.chats.ChatsAdapter;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainScreen extends AppCompatActivity {
    Context context = MainScreen.this;
    long firstTime;
    public static List<ChatItem> content = new ArrayList<>();
    public static ChatsAdapter contentAdapter;
    ProgressDialog finding, adding, initializing;
    List<UserInformation> userInformationList;
    int accountNameIndex = 0;
    ListView lv;
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.initialization(MainScreen.this, R.string.app_name);
        CoordinatorLayout cl = new CoordinatorLayout(context);
        CoordinatorLayout.LayoutParams clp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        cl.setLayoutParams(clp);
        lv = new ListView(context);
        CoordinatorLayout.LayoutParams llp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.FILL_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        lv.setLayoutParams(llp);
        CoordinatorLayout.LayoutParams fablp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        fab = new FloatingActionButton(context);
        final float scale = context.getResources().getDisplayMetrics().density;
        int zhi = (int) (getResources().getDimension(R.dimen.fab_margin) * scale + 0.5f);
        fablp.setMargins(zhi, zhi, zhi, zhi);
        fablp.gravity = Gravity.END | Gravity.BOTTOM;
        fab.setLayoutParams(fablp);
        finding = new ProgressDialog(context);
        finding.setTitle(getResources().getString(R.string.dialog_title_wait));
        finding.setMessage(getResources().getString(R.string.dialog_finding_message));
        finding.setCancelable(false);
        adding = new ProgressDialog(context);
        adding.setTitle(getResources().getString(R.string.dialog_title_wait));
        adding.setMessage(getResources().getString(R.string.dialog_adding_chat));
        adding.setCancelable(false);
        initializing = new ProgressDialog(context);
        initializing.setTitle(getResources().getString(R.string.dialog_title_wait));
        initializing.setMessage(getResources().getString(R.string.dialog_initializing_chat));
        initializing.setCancelable(false);
        lv.setAdapter(null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Utils.checkLoginInformationAndNetwork(context)) {
                    initForStart(lv, content);
                } else {
                    /**init from local*/
                    initFromFile(lv, content);
                }
                Looper.loop();
            }
        }).start();
        //FloatingActionButton fab = findViewById(R.id.fab);
        if (!Utils.networkAvailableDialog(context)) {
            String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US").split("_");
            setTitle(Utils.getStringByLocale(context, R.string.activity_main_screen_no_network_name, languageAndCountry[0], languageAndCountry[1]));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnected(context)) {
                    String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US").split("_");
                    setTitle(Utils.getStringByLocale(context, R.string.app_name, languageAndCountry[0], languageAndCountry[1]));

                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    final EditText et = new EditText(context);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    et.setHint(getString(R.string.dialog_dialog_add_chat_edittext_hint));
                    dialog.setView(et);
                    dialog.setTitle(getString(R.string.dialog_add_chat_title));
                    dialog.setNegativeButton(getString(android.R.string.cancel), null);
                    dialog.setPositiveButton(getString(R.string.dialog_add_chat_button_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String etT = et.getText().toString();
                            if (!TextUtils.isEmpty(etT)) {
                                if (Utils.isEmail(etT)) {
                                    //email
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String encrypted = "";
                                            try {
                                                encrypted = EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY);
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
                                            Looper.prepare();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finding.show();
                                                }
                                            });
                                            AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                                            boolean ok = accountUtils.find(context, finding, AccountUtils.BY_EMAIL, encrypted);
                                            if (ok) {
                                                finding.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adding.show();
                                                    }
                                                });
                                                InputStream avatar = accountUtils.getInputStream(context, "avatar", AccountUtils.BY_EMAIL, encrypted);
                                                String accountOrName = "";
                                                ChatItem item = null;
                                                if (TextUtils.isEmpty(XMLUtils.readXmlBySAX(accountUtils.getInputStream(context, "information", accountUtils.BY_EMAIL, encrypted)).get(accountNameIndex).getNameContent())) {
                                                    accountOrName = accountUtils.getString(context, "account", accountUtils.BY_EMAIL, encrypted);
                                                } else {
                                                    try {
                                                        accountOrName = EnDeCryptTextUtils.encrypt(XMLUtils.readXmlBySAX(accountUtils.getInputStream(context, "information", accountUtils.BY_EMAIL, encrypted)).get(accountNameIndex).getNameContent(), Variables.TEXT_ENCRYPTION_KEY);
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
                                                }
                                                if (avatar != null) {
                                                    File file = new File(Utils.getDataFilesPath(context), encrypted);
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                    try {
                                                        file.createNewFile();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                                                        Snackbar.make(lv, getString(R.string.toast_failed_to_save_avatar), Snackbar.LENGTH_SHORT).show();
                                                    }

                                                    if (file.exists()) {
                                                        OutputStream os = null;
                                                        try {
                                                            os = new FileOutputStream(file);
                                                            int ch = 0;
                                                            while ((ch = avatar.read()) != -1) {
                                                                os.write(ch);
                                                            }
                                                            os.flush();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                                                            Snackbar.make(lv, getString(R.string.toast_failed_to_save_avatar), Snackbar.LENGTH_SHORT).show();
                                                        } finally {
                                                            try {
                                                                os.close();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        item = new ChatItem(encrypted, Utils.getDataFilesPath(context) + "/" + encrypted, accountOrName, "", "");
                                                    }
                                                } else {
                                                    avatar = FormatTools.getInstance().Drawable2InputStream(getResources().getDrawable(R.drawable.account));
                                                    item = new ChatItem(encrypted, "", accountOrName, "", "");
                                                }

                                                File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
                                                if (chatsFile.exists()) {
                                                    String chatsFileContent = FileUtils.getString(chatsFile);
                                                    /**
                                                     * 查找准备添加的聊天是否存在
                                                     */
                                                    if (!chatsFileContent.contains("\"emailOrAccount\":\"" + encrypted + "\"")) {
                                                        //不存在，正常执行
                                                        /**
                                                         * 插入JSON项
                                                         */
                                                        //content.add(item);
                                                        final List<ChatItem> chatItems = new ArrayList<ChatItem>();
                                                        //新建json
                                                        //Log.e("fuck",String.valueOf(content));
                                                        //chatItems.addAll(content);
                                                        chatItems.addAll(content);
                                                        chatItems.add(item);
                                                        //og.e("fuck2",String.valueOf(content));
                                                        Gson gson = new Gson();
                                                        JSONArray chatArray = new JSONArray();
                                                        for (int i = 0; i < chatItems.size(); i++) {

                                                            String chatStr = gson.toJson(chatItems.get(i));

                                                            JSONObject chatObject;
                                                            try {
                                                                chatObject = new JSONObject(chatStr);
                                                                chatArray.put(i, chatObject);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        //写入json
                                                        //String jsonString = gson.toJson(item);
                                                        try {
                                                            FileWriter fileWriter = new FileWriter(chatsFile, false);
                                                            BufferedWriter writer = new BufferedWriter(fileWriter);
                                                            writer.append(chatArray.toString());
                                                            writer.flush();
                                                            writer.close();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                            Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                            Snackbar.make(lv, getString(R.string.toast_failed_to_add_chat), Snackbar.LENGTH_SHORT).show();
                                                        }

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //contentAdapter=null;
                                                                try {
                                                                    contentAdapter.clear();
                                                                    contentAdapter.notifyDataSetChanged();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                lv.setAdapter(null);
                                                                initForAdd(lv, chatItems);
                                                            }
                                                        });
                                                        adding.dismiss();
                                                    } else {
                                                        //存在，提示
                                                        //Snackbar.make(context, getString(R.string.toast_add_chat_exists), Snackbar.LENGTH_SHORT).show();
                                                        Snackbar.make(lv, getString(R.string.toast_add_chat_exists), Snackbar.LENGTH_SHORT).show();
                                                        adding.dismiss();
                                                    }
                                                } else {
                                                    /**
                                                     * 新建JSON
                                                     */
                                                    List<ChatItem> chatItems = new ArrayList<ChatItem>();
                                                    //新建json
                                                    chatItems.add(item);
                                                    Gson gson = new Gson();
                                                    JSONArray chatArray = new JSONArray();
                                                    for (int i = 0; i < chatItems.size(); i++) {

                                                        String chatStr = gson.toJson(chatItems.get(i));

                                                        JSONObject chatObject;
                                                        try {
                                                            chatObject = new JSONObject(chatStr);
                                                            chatArray.put(i, chatObject);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    //写入json
                                                    //String jsonString = gson.toJson(item);
                                                    try {
                                                        FileWriter fileWriter = new FileWriter(chatsFile, false);
                                                        BufferedWriter writer = new BufferedWriter(fileWriter);
                                                        writer.append(chatArray.toString());
                                                        writer.flush();
                                                        writer.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                        Snackbar.make(lv, getString(R.string.toast_failed_to_add_chat), Snackbar.LENGTH_SHORT).show();
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                adding.dismiss();
                                                            }
                                                        });
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            lv.setAdapter(null);
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    initForStart(lv, content);
                                                                }
                                                            }).start();
                                                            adding.dismiss();
                                                        }
                                                    });
                                                }
                                            } else {
                                                finding.dismiss();
                                                Snackbar.make(lv, getString(R.string.toast_account_not_exist), Snackbar.LENGTH_SHORT).show();
                                            }
                                            Looper.loop();
                                        }
                                    }).start();
                                } else {
                                    /**原来的代码
                                     *
                                     * String encrypted = "";
                                     *try {
                                     *                                         encrypted = EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY);
                                     *                                     } catch (InvalidKeySpecException e) {
                                     *                                         e.printStackTrace();
                                     *                                     } catch (InvalidKeyException e) {
                                     *                                         e.printStackTrace();
                                     *                                     } catch (NoSuchPaddingException e) {
                                     *                                         e.printStackTrace();
                                     *                                     } catch (IllegalBlockSizeException e) {
                                     *                                         e.printStackTrace();
                                     *                                     } catch (BadPaddingException e) {
                                     *                                         e.printStackTrace();
                                     *                                     }
                                     */
                                    //account


                                }
                            } else {
                                Snackbar.make(lv, getString(R.string.toast_input_content_empty), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                } else {
                    String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US").split("_");
                    setTitle(Utils.getStringByLocale(context, R.string.activity_main_screen_no_network_name, languageAndCountry[0], languageAndCountry[1]));

                    Snackbar.make(fab, getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
        /*try {
            InputStream inputStream=FormatTools.getInstance().Drawable2InputStream(getResources().getDrawable(R.drawable.add));
        byte[] bytes = new byte[0];
        bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);

            /*StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = FormatTools.getInstance().Drawable2InputStream(getResources().getDrawable(R.drawable.add)).read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            out.toString();*
            Log.e("fuck","its:"+str);
            AlertDialog.Builder a=new AlertDialog.Builder(context);
            a.setTitle("asfd").setMessage("fds");
            a.setIcon(FormatTools.getInstance().InputStream2Drawable(new ByteArrayInputStream(str.getBytes("UTF-8"))));
            //a.show();
        }catch (IOException e){
            e.printStackTrace();
        }*/


        cl.addView(lv, 0);
        cl.addView(fab, 1);
        setContentView(cl);
    }

    void initForStart(final ListView lv, final List<ChatItem> content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                initializing.show();
            }
        });
        File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
        if (chatsFile.exists()) {
            Gson gson = new Gson();
            try {
                List<ChatItem> list = gson.fromJson(FileUtils.getString(chatsFile), new TypeToken<List<ChatItem>>() {
                }.getType());
                List<String> eoas = new ArrayList<String>();//email or account s
                for (ChatItem item : list) {
                    eoas.add(item.getEmailOrAccount());
                }
                //Toast.makeText(context, String.valueOf(eoas), Toast.LENGTH_LONG).show();
                List<ChatItem> items = new ArrayList<>();
                for (int a = 0; a < eoas.size(); a++) {
                    final String[] eoa = {EnDeCryptTextUtils.decrypt(eoas.get(a), Variables.TEXT_ENCRYPTION_KEY)};
                    if (Utils.isEmail(eoa[0])) {
                        AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                        boolean status = false;
                        try {
                            status = au.tryLoginWithoutPassword(context, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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
                        if (status) {
                            /**
                             * 如果账号存在
                             */
                            InputStream avatar = null;
                            InputStream info = null;
                            try {
                                avatar = au.getInputStream(context, "avatar", au.BY_EMAIL, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                                info = au.getUserInformationWithoutPassword(context, au.BY_EMAIL, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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
                            if (avatar != null) {
                                /**
                                 * 保存头像
                                 */
                                File avatarFile = new File(Utils.getDataFilesPath(context), EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                                if (avatarFile.exists()) {
                                    avatarFile.delete();
                                }
                                try {
                                    avatarFile.createNewFile();
                                    int index;
                                    byte[] bytes = new byte[1024];
                                    FileOutputStream downloadFile = new FileOutputStream(Utils.getDataFilesPath(context) + "/" + EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                                    while ((index = avatar.read(bytes)) != -1) {
                                        downloadFile.write(bytes, 0, index);
                                        downloadFile.flush();
                                    }
                                    downloadFile.close();
                                    avatar.close();

                                    JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                                    jsonObject.put("avatarFilePAN", Utils.getDataFilesPath(context) + "/" + EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                                    Looper.loop();
                                }
                            }
                            String accountName = XMLUtils.readXmlBySAX(info).get(0).getNameContent();
                            ChatItem item;
                            if (TextUtils.isEmpty(accountName)) {
                                /**
                                 * 昵称空，获得账号
                                 * */
                                String account = "";
                                try {
                                    account = au.getAccountByEmail(context, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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
                                /**
                                 * 写入
                                 */
                                JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                                jsonObject.put("name", account);
                                String emailOrAccountS = jsonObject.getString("emailOrAccount");
                                String avatarS = jsonObject.getString("avatarFilePAN");
                                String latestMsgS = jsonObject.getString("latestMsg");
                                String latestMsgDateS = jsonObject.getString("latestMsgDate");
                                item = new ChatItem(emailOrAccountS, avatarS, account, latestMsgS, latestMsgDateS);
                            } else {
                                //昵称不空，set it！
                                JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                                jsonObject.put("name", accountName);
                                String emailOrAccountS = jsonObject.getString("emailOrAccount");
                                String avatarS = jsonObject.getString("avatarFilePAN");
                                String latestMsgS = jsonObject.getString("latestMsg");
                                String latestMsgDateS = jsonObject.getString("latestMsgDate");
                                item = new ChatItem(emailOrAccountS, avatarS, accountName, latestMsgS, latestMsgDateS);
                            }
                            items.add(item);
                            content.clear();
                            content.add(item);
                        }
                        contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, items);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv.setAdapter(contentAdapter);
                                initializing.dismiss();
                            }
                        });
                    }
                    else {

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_initialize));
                initializing.dismiss();
            }
        } else {
            /**don't init*/
            initializing.dismiss();
        }
    }


    void initFromFile(ListView lv, List<ChatItem> content) {
        File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
        if (chatsFile.exists()) {
            try {

                //String contentCon = Utils.getJsonByAssets(context, "chats.json");

                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File(Utils.getDataFilesPath(context), "chats.json"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_file_not_found));
                    initializing.dismiss();
                }
                InputStreamReader inputStreamReader = null;
                try {
                    inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    initializing.dismiss();
                    Utils.exceptionDialog(context, e1, getString(R.string.dialog_exception_failed_to_read_file));
                }
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer("");
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    initializing.dismiss();
                    Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_read_file));
                }

                JSONArray jsonArray = new JSONArray(sb.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    {
                        String emailOrAccount = jsonObject.getString("emailOrAccount");
                        String avatar = jsonObject.getString("avatarFilePAN");
                        String name = jsonObject.getString("name");
                        String latestMsg = jsonObject.getString("latestMsg");
                        String latestMsgDate = jsonObject.getString("latestMsgDate");

                        ChatItem item = new ChatItem(emailOrAccount, avatar, name, latestMsg, latestMsgDate);
                        content.add(item);

                    }
                }


                contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, content);

                lv.setAdapter(contentAdapter);

                initializing.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_parsing_json_failed));
                initializing.dismiss();
            }
        }
    }

    void initForAdd(ListView lv, List<ChatItem> content) {
        File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
        if (chatsFile.exists()) {
            contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, content);
            lv.setAdapter(contentAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Utils.startActivity(context, SettingsScreen.class);
                return true;
            case R.id.menu_about:
                Utils.startActivity(context, AboutScreen.class);
                return true;
            case R.id.menu_exit:
                Utils.showDialog(context, getResources().getString(R.string.dialog_title_notice), getString(R.string.dialog_exit_message), getString(R.string.dialog_exit_button_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MyApplication.getInstance().exit();
                    }
                });
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
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
                MyApplication.getInstance().exit();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
