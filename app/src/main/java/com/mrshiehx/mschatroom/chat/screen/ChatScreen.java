package com.mrshiehx.mschatroom.chat.screen;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageTypes;
import com.mrshiehx.mschatroom.chat.message.MessagesAdapter;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Folder;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    List<MessageItem> messageItemList = new ArrayList<>();
    boolean canContinueFromIntent;
    boolean canLogin;
    boolean canConnect;
    File tempPng;
    private Uri imageUri;
    ProgressDialog sendingPicture;

    private static final int RESULT_CHOOSE_PICTURE_FOR_SEND = 10001;
    private static final int RESULT_TAKE_PICTURE_FOR_SEND = 1000;

    void send(String content, int type, String storageContent) {
        if (!"".equals(content)) {
            final MessageItem storage = new MessageItem(storageContent, MessageItem.TYPE_SELF, type);
            Time Time = new Time();
            long aaaa = storage.getTime();
            Time.set(aaaa);
            int year = Time.year;
            /**START FROM 1*/
            int month = Time.month + 1;
            int day = Time.monthDay;
            int hour = Time.hour;
            int minute = Time.minute;
            boolean shouldAddTime = false;

            if (!chatFile.exists()) {
                shouldAddTime = true;
            } else {
                try {
                    List<Integer> types = new ArrayList<>();
                    //Gson gson = new Gson();
                    List<MessageItem> list = /*gson.fromJson(FileUtils.getString(chatFile), new TypeToken<List<MessageItem>>() {
                    }.getType())*/new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(FileUtils.getString(chatFile));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        list.add(MessageItem.valueOf(jsonArray.optJSONObject(i)));
                    }
                    for (MessageItem item : list) {
                        types.add(item.getType());
                    }
                    if (list.size() > 0) {
                        int indexOf = types.lastIndexOf(MessageItem.TYPE_TIME);
                        MessageItem timeItem = list.get(indexOf);
                        /*String*/
                        long time = timeItem.getTime();

                        Time tim = new Time();
                        tim.set(time);


                        int yearF = tim.year;
                        int monthF = tim.month + 1;
                        int dayF = tim.monthDay;
                        int hourF = tim.hour;
                        // int minuteF = tim.minute;
                        //int xiangcha=minute-minuteF;
                        //Toast.makeText(context, Utils.valueOf(yearF==year&&monthF==month&&dayF==day&&hourF==hour), Toast.LENGTH_SHORT).show();
                        if (list.size() > 0) {
                            if (yearF == year && monthF == month && dayF == day && hourF == hour) {
                                int xiangcha = 6;
                                //try {
                                Time timm = new Time();
                                timm.set(list.get(list.size() - 1).getTime());
                                xiangcha = minute - timm.minute;
                                /*} catch (Throwable ignored) {
                                    Toast.makeText(context, "145", Toast.LENGTH_SHORT).show();
                                }*/
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
                    } else {
                        shouldAddTime = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "161", Toast.LENGTH_SHORT).show();
                }
            }

            if (shouldAddTime) {
                MessageItem timeMsg = new MessageItem(/*year + "-" + month + "-" + day + ";" + (Utils.valueOf(hour).length() != 2 ? "0" + hour : hour) + ":" + (Utils.valueOf(minute).length() != 2 ? "0" + minute : minute)*/Utils.valueOf(aaaa), MessageItem.TYPE_TIME, MessageTypes.TEXT.code);
                writeToFile(timeMsg);
                messageItemList.add(timeMsg);
            }

            writeToFile(storage);
            messageItemList.add(storage);


            MessageItem failed = null;
            if (Utils.isNetworkConnected(context)) {
                //String content = input_chat_content.getText().toString();
                if (MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD)) {
                    if (canConnect) {
                        if (canLogin) {
                            //int hour = Utils.valueOf(Time.hour).length()!=2?Integer.parseInt("0"+Time.hour):Time.hour;
                            //int minute = Utils.valueOf(Time.minute).length()!=2?Integer.parseInt("0"+Time.minute):Time.minute;

                            if (Variables.SESSION == null || Variables.COMMUNICATOR == null) {
                                failed = new MessageItem(storageContent, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_SO : MessageItem.TYPE_FAILED_SEND, MessageTypes.TEXT.code);
                            } else {
                                Variables.COMMUNICATOR.send(eoaEncrypted, content, type);
                            }
                        } else {
                            failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO : MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED, MessageTypes.TEXT.code);
                        }
                    } else {
                        failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO : MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED, MessageTypes.TEXT.code);
                    }
                } else {
                    failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO : MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED, MessageTypes.TEXT.code);
                }
            } else {
                failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_OFFLINE_SO : MessageItem.TYPE_FAILED_SEND_OFFLINE, MessageTypes.TEXT.code);
            }
            if (failed != null) {
                writeToFile(failed);
                messageItemList.add(failed);
            }
            freshRV();
        }
    }

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
        tempPng = new File(Utils.getDataCachePath(context), "temp.png");
        canContinueFromIntent = intent.getBooleanExtra("canContinue", false);
        recycler_view = findViewById(R.id.recycler_view);
        input_chat_content = findViewById(R.id.input_chat_content);
        send = findViewById(R.id.send);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler_view.setLayoutManager(linearLayoutManager);
        send.setEnabled(canContinueFromIntent);
        chatFile = new File(DataFiles.CHATS_DIR, eoaEncrypted + ".json");
        sendingPicture = new ProgressDialog(context);
        sendingPicture.setMessage(getText(R.string.dialog_sending_picture_message));
        if (chatFile.exists()) {
            try {
                String content = FileUtils.getString(chatFile);
                JSONArray messages = new JSONArray(content);
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject object = messages.getJSONObject(i);
                    MessageItem messageItem = new MessageItem(object.optString("c"), object.optInt("t"), object.optInt("y"));
                    messageItem.setTime(object.optLong("s"));
                    messageItemList.add(messageItem);
                }

                /*messageItemList = new Gson().fromJson(, new TypeToken<List<MessageItem>>() {
                }.getType());*/
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_load_chat));
            }
        }
        adapter = new MessagesAdapter(context, messageItemList, new File(DataFiles.CHAT_AVATARS_DIR, eoaEncrypted).getAbsolutePath(), eoaEncrypted);

        recycler_view.setAdapter(adapter);
        recycler_view.scrollToPosition(messageItemList.size() - 1);
        if (Variables.ACCOUNT_INFORMATION != null) {
            canLogin = Variables.ACCOUNT_INFORMATION.isCanLogin();
        }
        canConnect = (Variables.COMMUNICATOR != null);
        send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));

        input_chat_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //if (canSend) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
                //}
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (canSend) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if (canSend) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
                //}
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = Utils.valueOf(input_chat_content.getText());
                try {
                    send(content, MessageTypes.TEXT.code, content);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showDialog(context, getText(R.string.dialog_exception_failed_to_send_message), e.toString());
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
        try {
            JSONArray jsonArray;
            if (!chatFile.exists()) {
                File folder = DataFiles.CHATS_DIR;
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                chatFile.createNewFile();
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(FileUtils.getString(chatFile));
            }

            JSONObject object = new JSONObject();
            object.put("t", msg.getType());
            object.put("c", msg.getContent());
            object.put("s", msg.getTime());
            object.put("y", msg.getContentType());
            jsonArray.put(object);
            FileUtils.modifyFile(chatFile, jsonArray.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void whenNetworkConnected() {
        final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
        new Thread(() -> {
            Looper.prepare();
            Variables.ACCOUNT_UTILS = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);

            if (Utils.checkLoginStatus(context)) {
                canLogin = Utils.checkLoginInformationAndNetwork(context);
                String account = GetAccountUtils.getAccount(Utils.getAccountUtils(), context, eoaEncrypted);
                Variables.COMMUNICATOR = new Communicator(context, account, eoaEncrypted);
                ;
                canConnect = (Variables.COMMUNICATOR != null);

                if (canConnect) {
                    if (!TextUtils.isEmpty(input_chat_content.getText())) {
                        runOnUiThread(() -> send.setEnabled(true));
                    }
                }
            }
            dialog.dismiss();
            Looper.loop();
        }
        ).start();
    }

    public void onDisconnectNetwork() {
        Variables.COMMUNICATOR = null;
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
            case R.id.menu_chat_send_picture:
                //startActivity(new Intent(this, SettingsActivity.class));
                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.dialog_choose_photo_title))
                        .setMessage(getResources().getString(R.string.dialog_choose_photo_message))
                        .setPositiveButton(getResources().getString(android.R.string.cancel), null)
                        .setNegativeButton(getResources().getString(R.string.dialog_choose_photo_button_select), (dialog1, which) -> {
                            Intent intentToPickPic = new Intent(Intent.ACTION_GET_CONTENT, null);
                            intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intentToPickPic, RESULT_CHOOSE_PICTURE_FOR_SEND);
                        }).setNeutralButton(getResources().getString(R.string.dialog_choose_photo_button_take), (dialog12, which) -> {
                    try {
                        if (tempPng.exists()) {
                            tempPng.delete();
                        } else {
                            if (!tempPng.getParentFile().exists()) {
                                tempPng.getParentFile().mkdirs();
                            }
                        }
                        tempPng.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(context, "com.mrshiehx.mschatroom.FileProvider", tempPng);
                    } else {
                        imageUri = Uri.fromFile(tempPng);
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, RESULT_TAKE_PICTURE_FOR_SEND);
                }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_TAKE_PICTURE_FOR_SEND:
                if (resultCode == RESULT_OK) {
                    sendingPicture.show();
                    new Thread(() -> {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            byte[] var = null;
                            try {
                                var = Utils.inputStream2ByteArray(getContentResolver().openInputStream(imageUri));
                                tempPng.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, getText(R.string.dialog_exception_failed_to_load_image), Toast.LENGTH_SHORT).show();
                            }

                            if (var != null && var.length != 0) {
                                long a = System.currentTimeMillis();
                                File var2 = new File(DataFiles.IMAGES_DIR, Utils.valueOf(a));
                                if (!var2.getParentFile().exists()) {
                                    var2.getParentFile().mkdirs();
                                }
                                if (var2.exists()) var2.delete();
                                try {
                                    var2.createNewFile();
                                    FileUtils.bytes2File(var, var2);
                                    //JSONArray messages=new JSONArray(FileUtils.getString(chatFile));
                                    //FileUtils.modifyFile(chatFile,messages.put(new JSONObject().put("c",Utils.valueOf(a)).put("s",new MessageItem("",0).getTime()).put("t",MessageItem.TYPE_SELF).put("y", MessageTypes.PICTURE.code)).toString(),false);
                                    send(FileUtils.bytesToString(FileUtils.toByteArray(var2)), MessageTypes.PICTURE.code, Utils.valueOf(a));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            sendingPicture.dismiss();
                        });
                        Looper.loop();
                    }).start();
                }
                break;
            case RESULT_CHOOSE_PICTURE_FOR_SEND:
                if (resultCode == RESULT_OK && data != null) {
                    sendingPicture.show();
                    new Thread(() -> {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            if (Build.VERSION.SDK_INT >= 19) {
                                handleImageOnKitKat(data);
                            } else {
                                handleImageBeforeKitKat(data);
                            }
                            sendingPicture.dismiss();
                        });
                        Looper.loop();
                    }).start();
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = "";
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {/* 如果是document类型的Uri，则通过document id处理*/
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];/* 解析出数字格式的id*/
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {/* 如果是content类型的Uri，则使用普通方式处理*/
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {/* 如果是file类型的Uri，直接获取图片路径即可*/
            imagePath = uri.getPath();
        }/* 根据图片路径显示图片*/
        doImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式 @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        doImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = "";/* 通过Uri和selection来获取真实的图片路径*/
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private void doImage(String imagePath) {
        if (imagePath != null) {
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            /*try {
                avatarIs = new FileInputStream(new File(imagePath));
                uploadAvatar(avatarIs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e);
            }*/


            if (new File(imagePath).exists()) {
                long a = System.currentTimeMillis();
                File var2 = new File(DataFiles.IMAGES_DIR, Utils.valueOf(a));
                if (!var2.getParentFile().exists()) {
                    var2.getParentFile().mkdirs();
                }
                if (var2.exists()) var2.delete();
                try {
                    var2.createNewFile();
                    FileUtils.copy(new File(imagePath), var2);
                    //JSONArray messages=new JSONArray(FileUtils.getString(chatFile));
                    //FileUtils.modifyFile(chatFile,messages.put(new JSONObject().put("c",Utils.valueOf(a)).put("s",new MessageItem("",0).getTime()).put("t",MessageItem.TYPE_SELF).put("y", MessageTypes.PICTURE.code)).toString(),false);
                    send(FileUtils.bytesToString(FileUtils.toByteArray(var2)), MessageTypes.PICTURE.code, Utils.valueOf(a));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, getText(R.string.toast_target_file_not_found), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_failed_to_get_photo_from_album), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * LM: latestMsg
     * LMD: latestMsgDate
     */
    /*void modifyLMLMD(){
        File chatsFile=new File(Utils.getDataFilesPath(context),"chats.json");
        if(chatsFile.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(chatsFile);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer("");
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                String content = sb.toString();
                List<ChatItem> list = new Gson().fromJson(content, new TypeToken<List<ChatItem>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{

        }
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        } else {
            String c = "";
            try {
                c = EnDeCryptTextUtils.decrypt(eoaEncrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.isEmail(c)) {
                new Thread(() -> {
                    Looper.prepare();
                    String account = "";
                    try {
                        account = GetAccountUtils.getAccount(Utils.getAccountUtils(), context, eoaEncrypted);
                        Variables.COMMUNICATOR = new Communicator(context, account, eoaEncrypted);
                        try {
                            if (Variables.COMMUNICATOR.connect()) {
                                Toast.makeText(context, R.string.loadinglog_success_connect_communication_server, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.loadinglog_failed_connect_communication_server, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, String.format(MSCRApplication.getContext().getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }).start();
            } else {
                new Thread(() -> {
                    Looper.prepare();
                    String email = "";
                    try {
                        email = GetAccountUtils.getEmail(Utils.getAccountUtils(), context, eoaEncrypted);
                        Variables.COMMUNICATOR = new Communicator(context, eoaEncrypted, email);
                        try {
                            if (Variables.COMMUNICATOR.connect()) {
                                Toast.makeText(context, R.string.loadinglog_success_connect_communication_server, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.loadinglog_failed_connect_communication_server, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, String.format(MSCRApplication.getContext().getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }).start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    public void messageReceived(IoSession session, Object message, int contentType) {
        try {
            JSONObject jsonObject = new JSONObject(Utils.valueOf(message));
            String fromEncrypted = jsonObject.optString("f");
            String[] array = GetAccountUtils.getAnotherIDAndSelf(Utils.getAccountUtils(), context, fromEncrypted);
            if (fromEncrypted.equals(eoaEncrypted) || fromEncrypted.equals(array[0]) || fromEncrypted.equals(array[1])) {

                int a = jsonObject.optInt("t", 0);
                if (a == MessageTypes.PICTURE.code) {
                    String ab = jsonObject.optString("c");
                    long abc = System.currentTimeMillis();
                    File file = new File(DataFiles.IMAGES_DIR, Utils.valueOf(abc));
                    if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();
                    FileUtils.bytes2File(FileUtils.hexString2Bytes(ab), file);
                    messageItemList.add(new MessageItem(Utils.valueOf(abc), MessageItem.TYPE_RECEIVER, contentType));
                } else {
                    messageItemList.add(new MessageItem(jsonObject.optString("c"), MessageItem.TYPE_RECEIVER, contentType));
                }
                runOnUiThread(() -> {
                    adapter.notifyItemInserted(messageItemList.size());
                    recycler_view.scrollToPosition(messageItemList.size() - 1);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
