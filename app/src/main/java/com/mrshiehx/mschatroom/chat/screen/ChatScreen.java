package com.mrshiehx.mschatroom.chat.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.LoadingScreen;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessagesAdapter;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//聊天界面（临时）
public class ChatScreen extends AppCompatActivity {
    public EditText input_chat_content;
    public Button send;
    public RecyclerView recycler_view;
    Context context = ChatScreen.this;
    //List<MessageItem> messageItemList = new ArrayList<MessageItem>();
    MessagesAdapter adapter;
    String eoaEncrypted;
    NetworkStateReceiver myReceiver;
    File chatFile;
    List<MessageItem> messageItemList = new ArrayList<MessageItem>();
    boolean canContinueFromIntent;
    boolean canSend;
    boolean canLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(ChatScreen.this, R.string.activity_chat_screen_name);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_chat_screen);
        myReceiver = new NetworkStateReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, itFilter);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
        }
        String eoaFromIntent = intent.getStringExtra("eoa");
        if (!TextUtils.isEmpty(eoaFromIntent)) {
            eoaEncrypted = eoaFromIntent;
        }
        canContinueFromIntent = intent.getBooleanExtra("canContinue", false);
        recycler_view = findViewById(R.id.recycler_view);
        input_chat_content = findViewById(R.id.input_chat_content);
        send = findViewById(R.id.send);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler_view.setLayoutManager(linearLayoutManager);
        send.setEnabled(canContinueFromIntent);
        chatFile = new File(Utils.getDataFilesPath(context), "chats" + File.separator + eoaEncrypted + ".json");
        if (chatFile.exists()) {
            try {
                messageItemList = new Gson().fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_load_chat));
            }
        }/*else{
            initMessages();
        }*/
        adapter = new MessagesAdapter(context, messageItemList, new File(Utils.getDataFilesPath(context), "chat_avatars" + File.separator + eoaEncrypted).getAbsolutePath(), eoaEncrypted);

        recycler_view.setAdapter(adapter);
        recycler_view.scrollToPosition(messageItemList.size() - 1);
        /*if (canContinueFromIntent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    canLogin = Utils.checkLoginInformationAndNetwork(context);
                    if (!canLogin) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                send.setEnabled(false);
                            }
                        });
                    } else {

                    }
                    Looper.loop();
                }
            }).start();

        }*/
        if (Variables.ACCOUNT_INFORMATION != null) {
            canLogin = Variables.ACCOUNT_INFORMATION.isCanLogin();
        }
        if (TextUtils.isEmpty(input_chat_content.getText()) == true) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    send.setEnabled(false);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    send.setEnabled(true);
                }
            });

        }

        input_chat_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //if (canSend) {
                if (TextUtils.isEmpty(input_chat_content.getText())) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
                //}
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (canSend) {
                if (TextUtils.isEmpty(input_chat_content.getText())) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if (canSend) {
                if (TextUtils.isEmpty(input_chat_content.getText())) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
                //}
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input_chat_content.getText().toString();
                if (!"".equals(content)) {
                    if (Utils.isNetworkConnected(context)) {
                        //String content = input_chat_content.getText().toString();
                        if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false)) {
                            if (canLogin) {
                                Time Time = new Time();
                                Time.setToNow();
                                int year = Time.year;
                                /**START FROM 1*/
                                int month = Time.month + 1;
                                int day = Time.monthDay;
                                int hour = Time.hour;
                                int minute = Time.minute;

                                final MessageItem msg;
                                MessageItem msg2 = null;
                                if (content.startsWith("r")) {
                                    msg = new MessageItem(content, MessageItem.TYPE_RECEIVER);
                                } else if (content.startsWith("t")) {
                                    msg = new MessageItem(content.substring(1), MessageItem.TYPE_TIME);
                                } else if (content.startsWith("f")) {
                                    msg = new MessageItem(content.substring(1), MessageItem.TYPE_FAILED_SEND);
                                } else {
                                    msg = new MessageItem(content, MessageItem.TYPE_SELF);
                                }
                                msg2 = new MessageItem(year + "-" + month + "-" + day + ";" + hour + ":" + minute, MessageItem.TYPE_TIME);


                                if (!chatFile.exists()) {
                                    final MessageItem finalMsg2 = msg2;
                                    new Thread(new Runnable() {
                                        @Override
                                        public synchronized void run() {
                                            writeToFileInThread(finalMsg2);
                                            writeToFileInThread(msg);
                                        }
                                    }).start();
                                    //writeToFile(msg2);
                                    messageItemList.add(msg2);
                                    //writeToFile(msg);
                                    messageItemList.add(msg);
                                    freshRV();
                                } else {
                                    try {
                                        List<Integer> types = new ArrayList<Integer>();
                                        Gson gson = new Gson();
                                        List<MessageItem> list = gson.fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                                        }.getType());
                                        for (MessageItem item:list) {
                                            types.add(item.getType());
                                        }
                                        int indexOf = types.lastIndexOf(MessageItem.TYPE_TIME);
                                        MessageItem timeItem = list.get(indexOf);
                                        String time = timeItem.getContent();
                                        String[] timeYMDAndHM = time.split(";");
                                        String[] ymd = timeYMDAndHM[0].split("-");
                                        String[] hm = timeYMDAndHM[1].split(":");
                                        int yearF = Integer.parseInt(ymd[0]);
                                        int monthF = Integer.parseInt(ymd[1]);
                                        int dayF = Integer.parseInt(ymd[2]);
                                        int hourF = Integer.parseInt(hm[0]);
                                        int minuteF = Integer.parseInt(hm[1]);
                                        //int xiangcha=minute-minuteF;
                                        //Toast.makeText(context, String.valueOf(yearF==year&&monthF==month&&dayF==day&&hourF==hour), Toast.LENGTH_SHORT).show();
                                        if (yearF==year&&monthF==month&&dayF==day&&hourF==hour) {
                                            int xiangcha=minute-minuteF;
                                            if(xiangcha<5||xiangcha>0){
                                                //NO
                                                writeToFile(msg);
                                                messageItemList.add(msg);
                                            }else{
                                                //YES
                                                final MessageItem finalMsg = msg2;
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public synchronized void run() {
                                                        writeToFileInThread(finalMsg);
                                                        writeToFileInThread(msg);
                                                    }
                                                }).start();
                                                //writeToFile(msg2);
                                                messageItemList.add(msg2);
                                                //writeToFile(msg);
                                                messageItemList.add(msg);
                                            }

                                        } else {
                                            //YES
                                            final MessageItem finalMsg1 = msg2;
                                            new Thread(new Runnable() {
                                                @Override
                                                public synchronized void run() {
                                                    writeToFileInThread(finalMsg1);
                                                    writeToFileInThread(msg);
                                                }
                                            }).start();
                                            //writeToFile(msg2);
                                            messageItemList.add(msg2);
                                            //writeToFile(msg);
                                            messageItemList.add(msg);
                                        }

                                         freshRV();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                final MessageItem msg = new MessageItem(content, MessageItem.TYPE_SELF);
                                final MessageItem msg2 = new MessageItem(null, MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED);
                                messageItemList.add(msg);
                                messageItemList.add(msg2);
                                new Thread(new Runnable() {
                                    @Override
                                    public synchronized void run() {
                                        writeToFileInThread(msg);
                                        writeToFileInThread(msg2);
                                    }
                                }).start();
                                freshRV();
                            }
                        } else {
                            final MessageItem msg = new MessageItem(content, MessageItem.TYPE_SELF);
                            final MessageItem msg2 = new MessageItem(null, MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED);
                            messageItemList.add(msg);
                            messageItemList.add(msg2);
                            new Thread(new Runnable() {
                                @Override
                                public synchronized void run() {
                                    writeToFileInThread(msg);
                                    writeToFileInThread(msg2);
                                }
                            }).start();
                            freshRV();
                        }
                    } else {
                        final MessageItem msg = new MessageItem(content, MessageItem.TYPE_SELF);
                        final MessageItem msg2 = new MessageItem(null, MessageItem.TYPE_FAILED_SEND_OFFLINE);
                        messageItemList.add(msg);
                        messageItemList.add(msg2);
                        new Thread(new Runnable() {
                            @Override
                            public synchronized void run() {
                                writeToFileInThread(msg);
                                writeToFileInThread(msg2);
                            }
                        }).start();
                        freshRV();
                    }
                }
            }
        });
    }

    void freshRV() {
        adapter.notifyItemInserted(messageItemList.size() - 1); // 当有新消息时，刷新ListView中的显示
        recycler_view.scrollToPosition(messageItemList.size() - 1); // 将ListView定位到最后一行
        input_chat_content.setText(""); // 清空输入框中的内容
    }

    void writeToFile(final MessageItem msg) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                writeToFileInThread(msg);
            }
        }).start();
    }

    void writeToFileInThread(MessageItem msg) {
        if (chatFile.exists()) {
            try {
                Gson gson = new Gson();
                List<MessageItem> list = gson.fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                }.getType());
                //JSONArray array=new Gson().fromJson(FileUtils.getString(chatFile));
                list.add(msg);
                JSONArray array = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    String string = gson.toJson(list.get(i));
                    array.put(i, new JSONObject(string));
                }
                FileUtils.modifyFile(chatFile, array.toString(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                File folder = new File(Utils.getDataFilesPath(context), "chats");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                chatFile.createNewFile();
                JSONObject object = new JSONObject();
                object.put("t", msg.getType());
                object.put("c", msg.getContent());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(object);
                FileUtils.modifyFile(chatFile, jsonArray.toString(), false);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void whenNetworkConnected() {
        final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Variables.ACCOUNT_UTILS = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                canLogin = Utils.checkLoginInformationAndNetwork(context);
                if (canLogin) {
                    if (!TextUtils.isEmpty(input_chat_content.getText())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                send.setEnabled(true);
                            }
                        });
                    }
                }
                dialog.dismiss();
                Looper.loop();
            }
        }).start();
    }

    public void whenNetworkUnconnected() {
        //canSendFromIntent = false;
        //send.setEnabled(false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}
