package com.mrshiehx.mschatroom.main.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    protected void onCreate(Bundle savedInstanceState) {
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
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    if(Utils.checkLoginInformationAndNetwork(context)) {
                                        AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                                        //AccountUtils au;
                                        if (!TextUtils.isEmpty(etT)) {
                                            if (Utils.isEmail(etT)) {
                                                //email
                                                String account = "";//DIRTY
                                                try {
                                                    account = accountUtils.getString(context, "account", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY));
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
                                                File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");

                                                if (chatsFile.exists()) {
                                                    String chatsFileContent = FileUtils.getString(chatsFile);

                                                    if (!chatsFileContent.contains("\"emailOrAccount\":\"" + account + "\"")) {
                                                        add(etT, AccountUtils.BY_EMAIL);
                                                    } else {
                                                        String jiamiaccount = null;
                                                        try {
                                                            jiamiaccount = EnDeCryptTextUtils.decrypt(account, Variables.TEXT_ENCRYPTION_KEY);
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
                                                        Snackbar.make(lv, String.format(getString(R.string.toast_add_chat_by_email_but_already_add_with_account), etT, jiamiaccount), Snackbar.LENGTH_LONG).show();

                                                    }
                                                } else {
                                                    add(etT, AccountUtils.BY_EMAIL);
                                                }


                                            } else {
                                                //account
                                                String email = "";//DIRTY
                                                try {
                                                    email = accountUtils.getString(context, "email", AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY));
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
                                                File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
                                                if (chatsFile.exists()) {

                                                    String chatsFileContent = FileUtils.getString(chatsFile);

                                                    if (!chatsFileContent.contains("\"emailOrAccount\":\"" + email + "\"")) {
                                                        add(etT, AccountUtils.BY_ACCOUNT);
                                                    } else {
                                                        String jiamiemail = "";
                                                        try {

                                                            jiamiemail = EnDeCryptTextUtils.decrypt(email, Variables.TEXT_ENCRYPTION_KEY);
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
                                                        Snackbar.make(lv, String.format(getString(R.string.toast_add_chat_by_account_but_already_add_with_email), etT, jiamiemail), Snackbar.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    add(etT, AccountUtils.BY_ACCOUNT);
                                                }
                                            }

                                        } else {
                                            Snackbar.make(lv, getString(R.string.toast_input_content_empty), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                    Looper.loop();
                                }
                            }).start();


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

    void add(final String 懂的都懂, final String by) {
        String encrypted = "";
        try {
            encrypted = EnDeCryptTextUtils.encrypt(懂的都懂, Variables.TEXT_ENCRYPTION_KEY);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finding.show();
            }
        });
        AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
        boolean ok = accountUtils.find(context, finding, by, encrypted);
        if (ok) {
            finding.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adding.show();
                }
            });
            InputStream avatar = accountUtils.getInputStream(context, "avatar", by, encrypted);
            String accountOrName = "";
            ChatItem item = null;
            if (TextUtils.isEmpty(XMLUtils.readXmlBySAX(accountUtils.getInputStream(context, "information", by, encrypted)).get(accountNameIndex).getNameContent())) {
                try {
                    accountOrName = EnDeCryptTextUtils.decrypt(accountUtils.getString(context, "account", by, encrypted), Variables.TEXT_ENCRYPTION_KEY);
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
            } else {
                accountOrName = XMLUtils.readXmlBySAX(accountUtils.getInputStream(context, "information", by, encrypted)).get(accountNameIndex).getNameContent();
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
                    try {
                        item = new ChatItem(encrypted, Utils.getDataFilesPath(context) + "/" + encrypted, EnDeCryptTextUtils.encrypt(accountOrName, Variables.TEXT_ENCRYPTION_KEY), "", "");
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
            } else {
                avatar = FormatTools.getInstance().Drawable2InputStream(getResources().getDrawable(R.drawable.account));
                try {
                    item = new ChatItem(encrypted, "", EnDeCryptTextUtils.encrypt(accountOrName, Variables.TEXT_ENCRYPTION_KEY), "", "");
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

            /**
             * 添加聊天到JSON
             */
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
                    //final List<ChatItem> chatItems = new ArrayList<ChatItem>();
                    //新建json
                    //Log.e("fuck",String.valueOf(content));
                    //chatItems.addAll(content);
                    //chatItems.addAll(content);
                    content.add(item);
                    //og.e("fuck2",String.valueOf(content));
                    Gson gson = new Gson();
                    JSONArray chatArray = new JSONArray();
                    for (int i = 0; i < content.size(); i++) {

                        String chatStr = gson.toJson(content.get(i));

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
                                    initForAdd(lv, content);
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
                content.clear();
                content.add(item);
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
                                initForAdd(lv, content);
                        adding.dismiss();
                    }
                });
            }
        } else {
            finding.dismiss();
            Snackbar.make(lv, getString(R.string.toast_account_not_exist), Snackbar.LENGTH_SHORT).show();
        }

    }


    /**
     * 真的给开始用，不能给添加后用
     */
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
                List<String> eoas = new ArrayList<String>();//email or account s,DECRYPTED
                for (ChatItem item : list) {
                    eoas.add(EnDeCryptTextUtils.decrypt(item.getEmailOrAccount(), Variables.TEXT_ENCRYPTION_KEY));
                }
                //Toast.makeText(context, String.valueOf(eoas), Toast.LENGTH_LONG).show();
                List<ChatItem> items = new ArrayList<>();
                for (int a = 0; a < eoas.size(); a++) {
                    final String[] eoa = {eoas.get(a)};
                    if (Utils.isEmail(eoa[0])) {
                        addForStart(eoa, chatsFile, a, items, AccountUtils.BY_EMAIL);
                    } else {
                        /**
                         * Init by account for startActivity
                         */
                        addForStart(eoa, chatsFile, a, items, AccountUtils.BY_ACCOUNT);
                    }
                }

                contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, items);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv.setAdapter(contentAdapter);
                        initializing.dismiss();
                    }
                });
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
                //File chatsFile1=new File(Utils.getDataFilesPath(context),"chats.json");
                //chatsFile1.delete();
                //chatsFile1.createNewFile();
                //FileUtils.writeToFile(chatArray.toString(),Utils.getDataFilesPath(context),"chats.json");
                FileUtils.modifyFile(Utils.getDataFilesPath(context) + "/" + "chats.json", chatArray.toString(), false);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_initialize));
            }
        }
        initializing.dismiss();
    }

    void addForStart(final String[] eoa, final File chatsFile, final int a, final List<ChatItem> items, final String by) throws JSONException {
        ChatItem item = null;
        try {
            AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            boolean status = false;
            try {
                status = au.tryLoginWithoutPassword(context, by, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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
                    avatar = au.getInputStream(context, "avatar", by, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                    info = au.getUserInformationWithoutPassword(context, by, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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

                        //JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                        //jsonObject.put("avatarFilePAN", Utils.getDataFilesPath(context) + "/" + EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                    }
                } else {
                    File avatarFile = new File(Utils.getDataFilesPath(context), EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                    if (avatarFile.exists()) {
                        avatarFile.delete();
                    }
                }
                String accountName = "";
                try {
                    accountName = XMLUtils.readXmlBySAX(info).get(0).getNameContent();//干净的
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_get_information));
                }
                if (TextUtils.isEmpty(accountName)) {
                    /**
                     * 昵称空，获得账号
                     * */
                    String account = "";
                    try {
                        //account = au.getAccountByEmail(context, EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                        account = au.getString(context,"account",by,EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
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

                    jsonObject.put("avatarFilePAN", Utils.getDataFilesPath(context) + "/" + EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY));
                    String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context), EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.getString("avatarFilePAN");
                    }

                    String latestMsgS = jsonObject.getString("latestMsg");
                    String latestMsgDateS = jsonObject.getString("latestMsgDate");
                    item = new ChatItem(emailOrAccountS, avatarS, account, latestMsgS, latestMsgDateS);
                } else {
                    //昵称不空，set it！
                    JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
                    jsonObject.put("name", EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY));
                    String emailOrAccountS = jsonObject.getString("emailOrAccount");
                    String avatarS = "";
                    if (new File(Utils.getDataFilesPath(context), EnDeCryptTextUtils.encrypt(eoa[0], Variables.TEXT_ENCRYPTION_KEY)).exists()) {
                        avatarS = jsonObject.getString("avatarFilePAN");
                    }
                    String latestMsgS = jsonObject.getString("latestMsg");
                    String latestMsgDateS = jsonObject.getString("latestMsgDate");
                    item = new ChatItem(emailOrAccountS, avatarS, EnDeCryptTextUtils.encrypt(accountName, Variables.TEXT_ENCRYPTION_KEY), latestMsgS, latestMsgDateS);
                }
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONArray(FileUtils.getString(chatsFile)).getJSONObject(a);
            String name = jsonObject.getString("name");
            String emailOrAccountS = jsonObject.getString("emailOrAccount");
            String avatarS = jsonObject.getString("avatarFilePAN");
            String latestMsgS = jsonObject.getString("latestMsg");
            String latestMsgDateS = jsonObject.getString("latestMsgDate");
            item = new ChatItem(emailOrAccountS, avatarS, name, latestMsgS, latestMsgDateS);
        }
        items.add(item);
        content.clear();
        content.add(item);
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

    void initForAdd(final ListView lv, List<ChatItem> content) {
        File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
        if (chatsFile.exists()) {
            contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, content);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lv.setAdapter(contentAdapter);
                }
            });
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
