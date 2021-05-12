package com.mrshiehx.mschatroom.main.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.about.screen.AboutScreen;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageTypes;
import com.mrshiehx.mschatroom.chat.screen.ChatScreen;
import com.mrshiehx.mschatroom.chat.screen.ChatScreenLauncher;
import com.mrshiehx.mschatroom.main.chats.ChatsAdapter;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.services.AutoReconnectService;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainScreen extends AppCompatActivity implements Serializable {
    Context context = MainScreen.this;
    long firstTime;
    List<ChatItem> content = new ArrayList<>();
    ChatsAdapter contentAdapter;
    ProgressDialog finding, adding, initializing;
    ListView lv;
    FloatingActionButton fab;
    NetworkStateReceiver myReceiver;
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.initialization(MainScreen.this, R.string.app_name);
        super.onCreate(savedInstanceState);
        startService(new Intent(this, AutoReconnectService.class));

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==100){
                    makeTitleToOnline();
                }else if(msg.what==101){
                    makeTitleToOffline();
                }
            }
        };

        myReceiver = new NetworkStateReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, itFilter);

        final CoordinatorLayout cl = new CoordinatorLayout(context);
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
        //finding.setTitle(getResources().getString(R.string.dialog_title_wait));
        finding.setMessage(getResources().getString(R.string.dialog_finding_message));
        finding.setCancelable(false);
        adding = new ProgressDialog(context);
        //adding.setTitle(getResources().getString(R.string.dialog_title_wait));
        adding.setMessage(getResources().getString(R.string.dialog_adding_chat));
        adding.setCancelable(false);
        initializing = new ProgressDialog(context);
        //initializing.setTitle(getResources().getString(R.string.dialog_title_wait));
        initializing.setMessage(getResources().getString(R.string.dialog_initializing_chat));
        initializing.setCancelable(false);
        lv.setAdapter(null);
        initializing.show();
        //accountInformation=Utils.getAccountInformation();
        initFromFile(lv, content);
        initializing.dismiss();
        //FloatingActionButton fab = findViewById(R.id.fab);
        Intent intent=getIntent();
        boolean doNotConnectToServer=intent.getBooleanExtra("doNotConnectToServer",false);
        if (!Utils.networkAvailableDialog(context)||doNotConnectToServer) {
            //try{Thread.sleep(3000);}catch (Exception e){e.printStackTrace();}
            makeTitleToOffline();
            //Toast.makeText(context, "136", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "138", Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, Utils.valueOf(Variables.ACCOUNT_UTILS==null), Toast.LENGTH_SHORT).show();
            boolean b = true;
                if(Variables.ACCOUNT_UTILS!=null) {
                    try {
                        b = Variables.ACCOUNT_UTILS.getConnection().isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Variables.ACCOUNT_UTILS == null || Variables.ACCOUNT_UTILS.getConnection() == null || b) {
                    final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Connection connection = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
                            if (connection == null) {
                                Toast.makeText(context, getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                AccountUtils accountUtils = new AccountUtils(connection, Variables.DATABASE_TABLE_NAME);
                                Variables.ACCOUNT_UTILS = accountUtils;
                            }
                            dialog.dismiss();
                            Looper.loop();
                        }
                    }).start();
                }

        }
        fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnected(context)) {
                    String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
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
                                new Thread(() -> {
                                    Looper.prepare();
                                    runOnUiThread(() -> adding.show());

                                    if (Utils.checkLoginStatus(context)) {
                                        if (Utils.checkLoginInformationAndNetwork(context)) {
                                            String account = "";
                                            String email = "";

                                            try {
                                                account = EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword(), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                                                email = EnDeCryptTextUtils.decrypt(GetAccountUtils.getEmail(Utils.getAccountUtils(), context, EnDeCryptTextUtils.encrypt(account)));

                                                if (etT.equalsIgnoreCase(account.toLowerCase()) || etT.equalsIgnoreCase(email.toLowerCase())) {
                                                    Utils.showShortSnackbar(fab, getString(R.string.toast_add_chat_cannot_add_self));
                                                    adding.dismiss();
                                                    return;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if (Utils.isEmail(etT)) {
                                                String[] aae = new String[0];
                                                try {
                                                    aae = GetAccountUtils.getAnotherIDAndSelf(Utils.getAccountUtils(), context, EnDeCryptTextUtils.encrypt(etT));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                /*if(aae!=null&&aae.length!=0) {
                                                    String accountE = aae[0];
                                                    String emailE = aae[1];
                                                }*/
                                                String accountE = aae[0];
                                                String emailE = aae[1];

                                                File chatsFile = DataFiles.CHATS_FILE;

                                                if (chatsFile.exists()) {
                                                    try {
                                                        String chatsFileContent = FileUtils.getString(chatsFile);
                                                        JSONArray array = new JSONArray(chatsFileContent);
                                                        List<ChatItem> list = new ArrayList<>();
                                                        for (int i = 0; i < array.length(); i++) {
                                                            JSONObject jsonObject = array.optJSONObject(i);
                                                            if (jsonObject != null) {
                                                                list.add(new ChatItem(jsonObject.optString("emailOrAccount"), jsonObject.optString("name"), jsonObject.optString("latestMsg"), jsonObject.optString("latestMsgDate")));
                                                            }
                                                        }

                                                        /*  Gson gson = new Gson();

                                                        List<ChatItem> list = gson.fromJson(chatsFileContent, new TypeToken<List<ChatItem>>() {
                                                        }.getType());*/

                                                        /**Encrypted*/
                                                        List<String> eoas = new ArrayList<String>();
                                                        /**Encrypted*/
                                                        List<String> names = new ArrayList<String>();
                                                        for (ChatItem item : list) {
                                                            eoas.add(item.getEmailOrAccount());
                                                            names.add(item.getName());
                                                        }

                                                        int indexOf = 0;
                                                        try {
                                                            indexOf = eoas.indexOf(EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY));
                                                            //Toast.makeText(MainScreen.this, "" + Arrays.toString(eoas.toArray()), Toast.LENGTH_SHORT).show();
                                                            //Toast.makeText(MainScreen.this, EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY), Toast.LENGTH_SHORT).show();
                                                            if (indexOf == -1) {
                                                                indexOf = eoas.indexOf(accountE);
                                                                if (indexOf == -1) {
                                                                    indexOf = eoas.indexOf(emailE);
                                                                }
                                                            }
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
                                                        if (/*!chatsFileContent.contains("\"emailOrAccount\":\"" + account + "\"")*/indexOf == -1) {
                                                            try {
                                                                add(etT, AccountUtils.BY_EMAIL);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                            }
                                                        } else {
                                                            String accountOrNameClean = "";
                                                            try {
                                                                if (!TextUtils.isEmpty(names.get(indexOf))) {
                                                                    accountOrNameClean = EnDeCryptTextUtils.decrypt(names.get(indexOf), Variables.TEXT_ENCRYPTION_KEY);
                                                                } else {
                                                                    accountOrNameClean = EnDeCryptTextUtils.decrypt(eoas.get(indexOf), Variables.TEXT_ENCRYPTION_KEY);
                                                                }
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
                                                            Snackbar.make(lv, String.format(getString(R.string.toast_add_chat_by_account_but_already_add), etT, accountOrNameClean), Snackbar.LENGTH_LONG).show();

                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                    }
                                                } else {
                                                    try {
                                                        add(etT, AccountUtils.BY_EMAIL);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                    }
                                                }


                                            } else {
                                                String[] aae = new String[0];
                                                try {
                                                    aae = GetAccountUtils.getAnotherIDAndSelf(Utils.getAccountUtils(), context, EnDeCryptTextUtils.encrypt(etT));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                String accountE = aae[0];
                                                String emailE = aae[1];

                                                File chatsFile = DataFiles.CHATS_FILE;

                                                if (chatsFile.exists()) {
                                                    try {
                                                        String chatsFileContent = FileUtils.getString(chatsFile);
                                                        Gson gson = new Gson();
                                                        List<ChatItem> list = gson.fromJson(chatsFileContent, new TypeToken<List<ChatItem>>() {
                                                        }.getType());

                                                        /**Encrypted*/
                                                        List<String> eoas = new ArrayList<String>();
                                                        /**Encrypted*/
                                                        List<String> names = new ArrayList<String>();
                                                        for (ChatItem item : list) {
                                                            eoas.add(item.getEmailOrAccount());
                                                            names.add(item.getName());
                                                        }

                                                        int indexOf = 0;
                                                        try {
                                                            indexOf = eoas.indexOf(EnDeCryptTextUtils.encrypt(etT, Variables.TEXT_ENCRYPTION_KEY));
                                                            if (indexOf == -1) {
                                                                indexOf = eoas.indexOf(emailE);
                                                                if (indexOf == -1) {
                                                                    indexOf = eoas.indexOf(accountE);
                                                                }
                                                            }
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
                                                        if (indexOf == -1) {
                                                            try {
                                                                add(etT, AccountUtils.BY_ACCOUNT);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                            }
                                                        } else {
                                                            String accountOrNameClean = "";
                                                            try {
                                                                if (!TextUtils.isEmpty(names.get(indexOf))) {
                                                                    accountOrNameClean = EnDeCryptTextUtils.decrypt(names.get(indexOf), Variables.TEXT_ENCRYPTION_KEY);
                                                                } else {
                                                                    accountOrNameClean = EnDeCryptTextUtils.decrypt(eoas.get(indexOf), Variables.TEXT_ENCRYPTION_KEY);
                                                                }
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

                                                            Snackbar.make(lv, String.format(getString(R.string.toast_add_chat_by_account_but_already_add), etT, accountOrNameClean), Snackbar.LENGTH_LONG).show();

                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                    }
                                                } else {
                                                    try {
                                                        add(etT, AccountUtils.BY_ACCOUNT);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_add_chat));
                                                    }
                                                }
                                            }
                                            contentAdapter = null;
                                            content.clear();
                                            initFromFile(lv, content);

                                        }
                                    }
                                    adding.dismiss();
                                    Looper.loop();
                                }).start();


                            } else {
                                Snackbar.make(lv, getString(R.string.toast_input_content_empty), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                } else {
                    String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
                    setTitle(Utils.getStringByLocale(context, R.string.activity_main_screen_offline_mode_name, languageAndCountry[0], languageAndCountry[1]));

                    Snackbar.make(fab, getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        /*try {
            InputStream inputStream=FormatTools.drawable2InputStream(getResources().getDrawable(R.drawable.add));
        byte[] bytes = new byte[0];
        bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);

            /*StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = FormatTools.drawable2InputStream(getResources().getDrawable(R.drawable.add)).read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            out.toString();*
            Log.e("fuck","its:"+str);
            AlertDialog.Builder a=new AlertDialog.Builder(context);
            a.setTitle("asfd").setMessage("fds");
            a.setIcon(FormatTools.inputStream2Drawable(new ByteArrayInputStream(str.getBytes("UTF-8"))));
            //a.show();
        }catch (IOException e){
            e.printStackTrace();
        }*/

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, getString(R.string.menu_main_item_delete));
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatItem item = (ChatItem) lv.getItemAtPosition(position);
                String name = item.getName();
                String nameDecrypted;
                String eoaDecrypted;
                String finalIntentString = "";
                if (!TextUtils.isEmpty(name)) {
                    try {
                        nameDecrypted = EnDeCryptTextUtils.decrypt(name, Variables.TEXT_ENCRYPTION_KEY);
                        finalIntentString = nameDecrypted;
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
                    try {
                        eoaDecrypted = EnDeCryptTextUtils.decrypt(item.getEmailOrAccount(), Variables.TEXT_ENCRYPTION_KEY);
                        finalIntentString = eoaDecrypted;
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
                }
                boolean canContinue = false;
                if (Utils.isNetworkConnected(context)) {
                    if (Variables.ACCOUNT_UTILS != null) {
                        if (AccountInformationStorager.isLogined()) {
                            /**检测是否能登录*/
                            canContinue = true;
                        }
                    }
                }
                new ChatScreenLauncher(context, item.getEmailOrAccount(), finalIntentString, canContinue).startChatScreen();
            }
        });
        cl.addView(lv, 0);
        cl.addView(fab, 1);
        setContentView(cl);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        final long selectedId = info.id;
        switch (item.getItemId()) {
            case 0:
                new AlertDialog.Builder(context).setTitle(getString(R.string.dialog_title_notice)).setMessage(getString(R.string.dialog_delete_chat_message)).setNegativeButton(getString(android.R.string.cancel), null).setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File chatsFile = DataFiles.CHATS_FILE;
                        File chatFile = new File(DataFiles.CHATS_DIR, ((ChatItem) lv.getItemAtPosition((int) selectedId)).getEmailOrAccount() + ".json");
                        File information = new File(DataFiles.INFORMATION_DIR, ((ChatItem) lv.getItemAtPosition((int) selectedId)).getEmailOrAccount() + ".json");
                        try {
                            List<File> files = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(FileUtils.getString(chatFile));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                /**code for send types*/
                                if (jsonObject.optInt("y") == MessageTypes.PICTURE.code) {
                                    String var = jsonObject.optString("c", "");
                                    if (!TextUtils.isEmpty(var))
                                        files.add(new File(DataFiles.IMAGES_DIR, var));
                                }else if (jsonObject.optInt("y") == MessageTypes.FILE.code) {
                                    String var = jsonObject.optString("c", "");
                                    if (!TextUtils.isEmpty(var))
                                        files.add(new File(DataFiles.FILES_DIR, var));
                                }
                            }
                            for (File file : files) {
                                if (file != null && file.exists()) {
                                    file.delete();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (chatFile.exists()) {
                            chatFile.delete();
                        }
                        if (information.exists()) {
                            information.delete();
                        }
                        //File chatsFile2 = new File(Utils.getDataFilesPath(context), "chats2.json");
                        if (chatsFile.exists()) {
                            try {
                                String chatsFileContent = FileUtils.getString(chatsFile);
                                Gson gson = new Gson();
                                List<ChatItem> list = gson.fromJson(chatsFileContent, new TypeToken<List<ChatItem>>() {
                                }.getType());

                                /**Encrypted*/
                                List<String> eoas = new ArrayList<String>();
                                for (ChatItem chatItem : list) {
                                    eoas.add(chatItem.getEmailOrAccount());
                                }

                                ChatItem itemOnListView = (ChatItem) lv.getItemAtPosition((int) selectedId);
                                String emailOrAccount = itemOnListView.getEmailOrAccount();

                                int indexOf = eoas.indexOf(emailOrAccount);
                                if (indexOf != -1) {
                                    /**
                                     * 删除
                                     */
                                    JSONArray array = new JSONArray(chatsFileContent);
                                    String newArray = Utils.jsonArrayRemove(array, indexOf).toString();
                            /*OutputStream outputStream=new FileOutputStream(chatsFile);
                            outputStream.write(array.toString().getBytes());
                            outputStream.flush();
                            outputStream.close();*/
                                    chatsFile.delete();
                                    chatsFile.createNewFile();
                                    FileUtils.writeToFile(newArray, chatsFile);

                                    File avatar = itemOnListView.getAvatarFile();
                                    if (avatar.exists()) {
                                        avatar.delete();
                                    }
                                    contentAdapter = null;
                                    content.clear();
                                    initFromFile(lv, content);

                                    Utils.showShortSnackbar(lv, getString(R.string.toast_successfully_delete_chat));
                                } else {
                                    Utils.showShortSnackbar(lv, getString(R.string.toast_not_found_target_chat));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.showShortSnackbar(lv, getString(R.string.toast_failed_delete_chat));
                                Utils.exceptionDialog(context, e, getString(R.string.toast_failed_delete_chat));
                            }
                        } else {
                            Utils.showShortSnackbar(lv, getString(R.string.toast_not_found_target_chat));
                        }
                    }
                }).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void add(final String var, final String by) throws Exception {
        String encrypted = "";
        try {
            encrypted = EnDeCryptTextUtils.encrypt(var, Variables.TEXT_ENCRYPTION_KEY);
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
        runOnUiThread(finding::show);
        boolean ok = Utils.getAccountUtils().find(context, by, encrypted);
        if (ok) {
            finding.dismiss();
            byte[] avatar=null;
            try {
                avatar = Utils.getAccountUtils().getBytesWithException(context, "avatar", by, encrypted);
            }catch (Exception e){
                e.printStackTrace();
            }
            ChatItem item = null;
            byte[] info = Utils.getAccountUtils().getBytes(context, "information", by, encrypted);
            String readName = UserInformationUtils.read(context, info).nameContent;
            if (avatar != null&&avatar.length!=0&&!Utils.isBytesAllZero(avatar)) {
                File cafile = DataFiles.CHAT_AVATARS_DIR;
                File file = new File(DataFiles.CHAT_AVATARS_DIR, encrypted);
                if (!cafile.exists()) {
                    cafile.mkdirs();
                } else {
                    if (file.exists()) {
                        file.delete();
                    }
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                    Snackbar.make(lv, getString(R.string.toast_failed_to_save_avatar), Snackbar.LENGTH_SHORT).show();
                }

                if (file.exists()) {
                    try {
                        FileUtils.bytes2File(avatar,file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_save_avatar));
                        Snackbar.make(lv, getString(R.string.toast_failed_to_save_avatar), Snackbar.LENGTH_SHORT).show();
                    }
                    try {
                        item = new ChatItem(encrypted, /*Utils.getDataFilesPath(context) + File.separator +"chat_avatars"+File.separator+encrypted,*/ TextUtils.isEmpty(readName) ? readName : EnDeCryptTextUtils.encrypt(readName, Variables.TEXT_ENCRYPTION_KEY)/*, "", ""*/);
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
                try {
                    item = new ChatItem(encrypted, /*"", */TextUtils.isEmpty(readName) ? readName : EnDeCryptTextUtils.encrypt(readName, Variables.TEXT_ENCRYPTION_KEY)/*, "", ""*/);
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
            File chatsFile = DataFiles.CHATS_FILE;
            if (chatsFile.exists()) {
                String chatsFileContent = FileUtils.getString(chatsFile);
                /**
                 * 查找准备添加的聊天是否存在
                 */
                Gson gson = new Gson();
                List<ChatItem> list = gson.fromJson(chatsFileContent, new TypeToken<List<ChatItem>>() {
                }.getType());

                /**Encrypted*/
                List<String> eoas = new ArrayList<String>();
                for (ChatItem itema : list) {
                    eoas.add(itema.getEmailOrAccount());
                }
                int indexOf = eoas.indexOf(encrypted);
                if (/*!chatsFileContent.contains("\"emailOrAccount\":\"" + encrypted + "\"")*/indexOf == -1) {
                    //不存在，正常执行
                    /**
                     * 插入JSON项
                     */
                    content.add(item);
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

                } else {
                    //存在，提示
                    //Snackbar.make(context, getString(R.string.toast_add_chat_exists), Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(lv, getString(R.string.toast_add_chat_exists), Snackbar.LENGTH_SHORT).show();
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
                    runOnUiThread(adding::dismiss);
                }
            }
            adding.dismiss();
            try {
                if (!DataFiles.CHATS_DIR.exists()) {
                    DataFiles.CHATS_DIR.mkdirs();
                }
                new File(DataFiles.CHATS_DIR, encrypted + ".json").createNewFile();
                FileUtils.modifyFile(new File(DataFiles.CHATS_DIR, encrypted + ".json"), "[]", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            finding.dismiss();
            Snackbar.make(lv, getString(R.string.toast_account_not_exist), Snackbar.LENGTH_SHORT).show();
        }

    }


    void initFromFile(final ListView lv, List<ChatItem> content) {
        File chatsFile = DataFiles.CHATS_FILE;
        if (chatsFile.exists()) {
            try {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(FileUtils.getString(DataFiles.CHATS_FILE));
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonArray = new JSONArray();
                    Toast.makeText(context, getText(R.string.dialog_exception_failed_to_load_chat), Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String emailOrAccount = jsonObject.optString("emailOrAccount");
                    String name = jsonObject.optString("name");
                    String latestMsg = "";
                    String latestMsgDate = "";

                    File chatFile = new File(DataFiles.CHATS_DIR, emailOrAccount + ".json");
                    if (chatFile.exists()) {
                        try {
                            List<MessageItem> list = new Gson().fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                            }.getType());
                            latestMsgDate = Utils.formatTime(list.get(list.size() - 1).getTime());
                            if (list.get(list.size() - 1).getType() != MessageItem.TYPE_TIME) {
                                MessageItem item = list.get(list.size() - 1);
                                /**code for send types*/
                                if (item.getContentType() == MessageTypes.PICTURE.code) {
                                    latestMsg = getString(R.string.message_type_picture);
                                }else if (item.getContentType() == MessageTypes.FILE.code) {
                                    latestMsg = getString(R.string.message_type_file);
                                } else {
                                    latestMsg = list.get(list.size() - 1).getContent();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ChatItem item = new ChatItem(emailOrAccount, name, latestMsg, latestMsgDate);
                    content.add(item);
                }


                contentAdapter = new ChatsAdapter(context, R.layout.item_main_screen_chat, content);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv.setAdapter(contentAdapter);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_parsing_json_failed));
            }
        }
    }

    void initForAdd(final ListView lv, List<ChatItem> content, ChatItem chatItem) {
        File chatsFile = DataFiles.CHATS_FILE;
        //Toast.makeText(context, ""+chatsFile.exists(), Toast.LENGTH_SHORT).show();
        if (chatsFile.exists()) {
            contentAdapter.add(chatItem);
            //contentAdapter = new ChatsAdapter(this, R.layout.item_main_screen_chat, content);
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {*/
            lv.setAdapter(contentAdapter);
                /*}
            });*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reconnect:
                if(Utils.isNetworkConnected(context)) {
                    boolean closed = true;
                    if (Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null) {
                        try {
                            closed = Variables.ACCOUNT_UTILS.getConnection().isClosed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (Variables.ACCOUNT_UTILS == null || Variables.ACCOUNT_UTILS.getConnection() == null || closed) {
                        Utils.reload(context, true,true,false);
                    }
                }else{
                    Toast.makeText(context, R.string.toast_please_check_your_network, Toast.LENGTH_SHORT).show();
                }
                if(Variables.COMMUNICATOR == null||Variables.SESSION==null||!Variables.SESSION.isConnected()){
                    ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
                    new Thread(() -> {
                        Looper.prepare();
                        try {
                            String b = Utils.valueOf(Utils.getAccountInformation().getAccountE());
                            String c = Utils.valueOf(Utils.getAccountInformation().getEmailE());
                            Variables.COMMUNICATOR = new Communicator(this, b, c);
                            try {
                                if (Variables.COMMUNICATOR.connect()) {
                                    Toast.makeText(context, R.string.loadinglog_success_connect_communication_server, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, R.string.loadinglog_failed_connect_communication_server, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, String.format(MSChatRoom.getContext().getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        Looper.loop();
                    }).start();
                }
                break;
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
                        MSChatRoom.getInstance().exit();
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
                MSChatRoom.getInstance().exit();
            }
            //super.onBackPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }


    void updateLatests() {
        for (int i = 0; i < content.size(); i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    Looper.prepare();
                    try {
                        ChatItem item = (ChatItem) lv.getItemAtPosition(finalI);
                        File chatFile = new File(DataFiles.CHATS_DIR, item.getEmailOrAccount() + ".json");
                        List<MessageItem> list = new Gson().fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                        }.getType());
                        String latestMsgDate = "";
                        String latestMsg = "";

                        if (chatFile.exists()&&list!=null&&list.size()!=0) {
                            MessageItem message=list.get(list.size() - 1);
                            latestMsgDate = Utils.formatTime(list.get(list.size() - 1).getTime());
                            if (message.getType() != MessageItem.TYPE_TIME) {
                                /**code for send types*/
                                if (message.getContentType() == MessageTypes.PICTURE.code) {
                                    latestMsg = getString(R.string.message_type_picture);
                                }else if (message.getContentType() == MessageTypes.FILE.code) {
                                    latestMsg = getString(R.string.message_type_file);
                                } else {
                                    if(message.getType()==MessageItem.TYPE_RECEIVER||message.getType()==MessageItem.TYPE_SELF) {
                                        latestMsg = message.getContent();
                                    }else{
                                        int i1=message.getType();
                                        /**code for message type*/
                                        switch (i1){
                                            case MessageItem.TYPE_FAILED_SEND_SO:
                                            case MessageItem.TYPE_FAILED_SEND:
                                                latestMsg=getString(R.string.chat_tip_failed_send_without_content);
                                                break;
                                            case MessageItem.TYPE_FAILED_SEND_OFFLINE_SO:
                                            case MessageItem.TYPE_FAILED_SEND_OFFLINE:
                                                latestMsg=getString(R.string.chat_tip_offline);
                                                break;
                                            case MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO:
                                            case MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED:
                                                latestMsg=getString(R.string.chat_tip_failed_connect);
                                                break;
                                            case MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO:
                                            case MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED:
                                                latestMsg=getString(R.string.chat_tip_failed_logined);
                                                break;
                                            case MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO:
                                            case MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED:
                                                latestMsg=getString(R.string.chat_tip_not_loggined);
                                                break;
                                            default:
                                                latestMsg = message.getContent();
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        content.get(finalI).setLatestMsg(latestMsg);
                        content.get(finalI).setLatestMsgDate(latestMsgDate);
                        runOnUiThread(() -> {
                            contentAdapter = new ChatsAdapter(context, R.layout.item_main_screen_chat, content);
                            lv.setAdapter(contentAdapter);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
        updateLatests();
        /*if (Variables.ACCOUNT_UTILS != null) {
            if (Variables.ACCOUNT_UTILS.getConnection() == null) {
                Utils.reload(context);
            }
        } else {
            Utils.reload(context);
        }*/
    }

    public void messageReceived(IoSession session, Object message/*, String latestMsg, String latestMsgDate*/) {
        updateLatests(/*latestMsg, latestMsgDate*/);
    }

    public void onDisconnectNetwork() {
        Variables.COMMUNICATOR = null;
        Variables.SESSION=null;
    }

    public void makeTitleToOffline(){
        //Toast.makeText(context, "1089", Toast.LENGTH_SHORT).show();
        new Thread(()->runOnUiThread(()->{
            String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
            setTitle(Utils.getStringByLocale(context, R.string.activity_main_screen_offline_mode_name, languageAndCountry[0], languageAndCountry[1]));
        })).start();
    }

    public void makeTitleToOnline(){
        //Toast.makeText(context, "1097", Toast.LENGTH_SHORT).show();
        new Thread(()->runOnUiThread(()->{
            String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
            setTitle(Utils.getStringByLocale(context, R.string.app_name, languageAndCountry[0], languageAndCountry[1]));
        })).start();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        boolean closed=true;
        if(Variables.ACCOUNT_UTILS!=null&&Variables.ACCOUNT_UTILS.getConnection()!=null){
            try{
                closed=Variables.ACCOUNT_UTILS.getConnection().isClosed();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        menu.getItem(0).setVisible(Variables.ACCOUNT_UTILS==null||Variables.ACCOUNT_UTILS.getConnection()==null||closed||Variables.COMMUNICATOR==null||Variables.SESSION==null);
        return super.onMenuOpened(featureId, menu);
    }
}
