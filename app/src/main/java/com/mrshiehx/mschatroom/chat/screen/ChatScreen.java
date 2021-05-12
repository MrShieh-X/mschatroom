package com.mrshiehx.mschatroom.chat.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.FileMessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageTypes;
import com.mrshiehx.mschatroom.chat.message.MessagesAdapter;
import com.mrshiehx.mschatroom.exceptions.NoTargetChatObjectException;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.shared_variables.ServerCommands;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileChooseUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.PermissionsGranter;
import com.mrshiehx.mschatroom.utils.StreamUtils;
import com.mrshiehx.mschatroom.utils.StringUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
    List<MessageItem> messageItemList = new ArrayList<>();
    Boolean canLogin=null;
    File tempPng;
    private Uri imageUri;
    ProgressDialog sendingPicture,sendingFile;
    private Handler handler;
    private PermissionsGranter permissionsGranterForChoosePhoto;
    private PermissionsGranter permissionsGranterForChooseFile;

    private PermissionsGranter permissionsGranterForSavePictureToLocal;
    private PermissionsGranter permissionsGranterForSaveFileToLocal;
    private PermissionsGranter permissionsGranterForShowDownloadFileDialog;

    private static final int REQUEST_CHOOSE_PICTURE_FOR_SEND = 10002;
    private static final int REQUEST_TAKE_PICTURE_FOR_SEND = 10001;
    private static final int REQUEST_CHOOSE_FILE = 10000;

    private static final int REQUEST_CODE_01 = 10003;
    private static final int REQUEST_CODE_02 = 10004;

    private static final int REQUEST_CODE_03 = 10005;
    private static final int REQUEST_CODE_04 = 10006;
    private static final int REQUEST_CODE_05 = 10007;
    private static final int REQUEST_CODE_06 = 10008;
    private static final int REQUEST_CODE_07 = 10009;
    private static final int REQUEST_CODE_08 = 10010;
    private static final int REQUEST_CODE_09 = 10011;
    private static final int REQUEST_CODE_10 = 10012;
    private static final int REQUEST_CODE_11 = 10013;
    private static final int REQUEST_CODE_12 = 10014;

    void realSend(String content, int type, Object[]args) {
        Variables.COMMUNICATOR.send(eoaEncrypted, content, type,args);
    }

    void send(String content, int type, String storageContent, Object[]extraArguments) {
        if (!"".equals(content)) {
            final MessageItem storage;
            /**code for send types*/
            if(type==MessageTypes.FILE.code){
                storage=new FileMessageItem(storageContent, MessageItem.TYPE_SELF, type).setFileName((String) extraArguments[0]).setFileSize((long)extraArguments[1]);
            }else{
                storage= new MessageItem(storageContent, MessageItem.TYPE_SELF, type);
            }
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
                    List<MessageItem> list = new ArrayList<>();
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
                        if (list.size() > 0) {
                            if (yearF == year && monthF == month && dayF == day && hourF == hour) {
                                int xiangcha;
                                Time timm = new Time();
                                timm.set(list.get(list.size() - 1).getTime());
                                xiangcha = minute - timm.minute;
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
                if (AccountInformationStorager.isLogined()) {
                    //if (canConnect) {
                    if (canLogin) {
                        //int hour = Utils.valueOf(Time.hour).length()!=2?Integer.parseInt("0"+Time.hour):Time.hour;
                        //int minute = Utils.valueOf(Time.minute).length()!=2?Integer.parseInt("0"+Time.minute):Time.minute;

                        if (Variables.SESSION != null && Variables.COMMUNICATOR != null && Variables.SESSION.isConnected()) {
                            realSend(content, type,extraArguments);
                        } else {
                            try {
                                String b = Utils.valueOf(Utils.getAccountInformation().getAccountE());
                                String c = Utils.valueOf(Utils.getAccountInformation().getEmailE());
                                Variables.COMMUNICATOR = new Communicator(context, b, c);
                                if (Variables.COMMUNICATOR.connect()) {
                                    realSend(content, type,extraArguments);
                                } else {
                                    failed = new MessageItem(storageContent, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_SO : MessageItem.TYPE_FAILED_SEND, type);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                failed = new MessageItem(storageContent, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_SO : MessageItem.TYPE_FAILED_SEND, type);
                            }
                        }
                    } else {
                        failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO : MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED, type);
                    }
                    /*} else {
                        failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO : MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED, type);
                    }*/
                } else {
                    failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO : MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED, type);
                }
            } else {
                failed = new MessageItem(null, type != MessageTypes.TEXT.code ? MessageItem.TYPE_FAILED_SEND_OFFLINE_SO : MessageItem.TYPE_FAILED_SEND_OFFLINE, type);
            }
            if (failed != null) {
                writeToFile(failed);
                messageItemList.add(failed);
            }
            runOnUiThread(this::freshAndClear);
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
        }else{
            throw new NoTargetChatObjectException();
        }
        permissionsGranterForChoosePhoto = new PermissionsGranter(ChatScreen.this, (args)->choosePhoto(), null, REQUEST_CODE_01, REQUEST_CODE_02);
        permissionsGranterForChooseFile = new PermissionsGranter(ChatScreen.this, (args)->chooseFile(), null, REQUEST_CODE_03, REQUEST_CODE_04);
        permissionsGranterForSavePictureToLocal=new PermissionsGranter(ChatScreen.this,(args) -> savePictureToLocal((MessageItem)args[0]), null, REQUEST_CODE_05, REQUEST_CODE_06);
        permissionsGranterForSaveFileToLocal=new PermissionsGranter(ChatScreen.this,(args) -> saveFileToLocal((MessageItem)args[0]), null, REQUEST_CODE_07, REQUEST_CODE_08);
        permissionsGranterForShowDownloadFileDialog=new PermissionsGranter(ChatScreen.this,(args) -> showDownloadFileDialog((String)args[0]), null, REQUEST_CODE_09, REQUEST_CODE_10);
        //permissionsGranterForShowFileDetailsDialog=new PermissionsGranter(ChatScreen.this,(args) -> showFileDetailsDialog((FileMessageItem)args[0]), null, REQUEST_CODE_11, REQUEST_CODE_12);



        tempPng = new File(Utils.getDataCachePath(context), "temp.png");
        recycler_view = findViewById(R.id.recycler_view);
        input_chat_content = findViewById(R.id.input_chat_content);
        send = findViewById(R.id.send);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler_view.setLayoutManager(linearLayoutManager);
        chatFile = new File(DataFiles.CHATS_DIR, eoaEncrypted + ".json");
        sendingPicture = new ProgressDialog(context);
        sendingPicture.setMessage(getText(R.string.dialog_sending_picture_message));
        sendingPicture.setCancelable(false);
        sendingFile = new ProgressDialog(context);
        sendingFile.setMessage(getText(R.string.dialog_sending_file_message));
        sendingFile.setCancelable(false);
        init();
        /*if (Utils.getAccountInformation() != null) {
            canLogin = Utils.getAccountInformation().isCanLogin();
        }*/
        new Thread(() -> {
            Looper.prepare();
            try {
                canLogin = GetAccountUtils.checkCanLogin(Utils.getAccountUtils(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
        send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));

        input_chat_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                send.setEnabled(!TextUtils.isEmpty(input_chat_content.getText()));
            }
        });
        send.setOnClickListener(v -> {
            String content = Utils.valueOf(input_chat_content.getText());
            new Thread(()->{
                Looper.prepare();
                try {
                    send(content, MessageTypes.TEXT.code, content,null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showDialog(context, getText(R.string.dialog_exception_failed_to_send_message), e.toString());
                }
                Looper.loop();
            }).start();
        });
    }

    void freshAndClear() {
        adapter.notifyItemInserted(messageItemList.size() - 1); // 当有新消息时，刷新ListView中的显示
        recycler_view.scrollToPosition(messageItemList.size() - 1); // 将ListView定位到最后一行
        input_chat_content.setText(""); // 清空输入框中的内容
    }

    void fresh() {
        adapter.notifyItemInserted(messageItemList.size() - 1);
        recycler_view.scrollToPosition(messageItemList.size() - 1);
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
            /**code for send type*/
            if(msg.getContentType()==MessageTypes.FILE.code){
                object.put("f",((FileMessageItem)msg).getFileName());
                object.put("i",((FileMessageItem)msg).getFileSize());
            }
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
                    Toast.makeText(context, String.format(getString(R.string.loadinglog_failed_connect_communication_server_withcause), e), Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(input_chat_content.getText())) {
                    runOnUiThread(() -> send.setEnabled(true));
                }

            }
            dialog.dismiss();
            Looper.loop();
        }
        ).start();
    }

    public void onDisconnectNetwork() {
        Variables.COMMUNICATOR = null;
        Variables.SESSION=null;
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
                            permissionsGranterForChoosePhoto.start();
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
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE_FOR_SEND);
                }).show();
                break;
            case R.id.menu_chat_send_file:
                permissionsGranterForChooseFile.start();
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
            case REQUEST_TAKE_PICTURE_FOR_SEND:
                if (resultCode == RESULT_OK) {
                    sendingPicture.show();
                    new Thread(() -> {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            byte[] var = null;
                            try {
                                var = StreamUtils.inputStream2ByteArray(getContentResolver().openInputStream(imageUri));
                                tempPng.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, getText(R.string.dialog_exception_failed_to_load_image), Toast.LENGTH_SHORT).show();
                            }

                            byte[] finalVar = var;
                                if (finalVar != null && finalVar.length != 0) {
                                    long a = System.currentTimeMillis();
                                    File var2 = new File(DataFiles.IMAGES_DIR, Utils.valueOf(a));
                                    if (!var2.getParentFile().exists()) {
                                        var2.getParentFile().mkdirs();
                                    }
                                    if (var2.exists()) var2.delete();

                                    try {
                                        var2.createNewFile();
                                        FileUtils.bytes2File(finalVar, var2);
                                        send(StringUtils.hexBytesToString(FileUtils.toByteArray(var2)), MessageTypes.PICTURE.code, Utils.valueOf(a),null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Utils.showDialog(context, getText(R.string.dialog_exception_failed_to_send_message), e.toString());
                                    }
                                }
                                sendingPicture.dismiss();
                        });
                        Looper.loop();
                    }).start();
                }
                break;
            case REQUEST_CHOOSE_PICTURE_FOR_SEND:
                if (resultCode == RESULT_OK && data != null) {
                    sendingPicture.show();
                    FileChooseUtils.onActivityResult(context, data.getData(), this::doImage);
                    /*
                    new Thread(() -> {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            if (Build.VERSION.SDK_INT >= 19) {
                                handleImageOnKitKat(data);
                            } else {
                                handleImageBeforeKitKat(data);
                            }
                        });
                        Looper.loop();
                    }).start();*/
                }
                break;
            case REQUEST_CHOOSE_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    sendingFile.show();
                    FileChooseUtils.onActivityResult(context, uri, filePath -> new Thread(()->{
                        Looper.prepare();
                        File file=new File(filePath);
                        if(file.exists()){
                            String a = Utils.replaceLongToString(Utils.valueOf(System.currentTimeMillis()));
                            File var2 = new File(DataFiles.FILES_DIR,a);
                            if (!var2.getParentFile().exists()) {
                                var2.getParentFile().mkdirs();
                            }
                            if (var2.exists()) var2.delete();
                                try {
                                    var2.createNewFile();
                                    FileUtils.copy(file, var2);
                                    send(StringUtils.hexBytesToString(FileUtils.toByteArray(file)), MessageTypes.FILE.code, a,new Object[]{file.getName(),file.length(),a});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.showDialog(context, getText(R.string.dialog_exception_failed_to_send_message), e.toString());
                                }
                                sendingFile.dismiss();
                        }else{
                            Toast.makeText(context, R.string.toast_target_file_not_found, Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }).start());
                }
                break;
            case REQUEST_CODE_02:
                permissionsGranterForChoosePhoto.onActivityResult();
                break;
            case REQUEST_CODE_04:
                permissionsGranterForChooseFile.onActivityResult();
                break;

            case REQUEST_CODE_06:
                permissionsGranterForSavePictureToLocal.onActivityResult();
                break;
            case REQUEST_CODE_08:
                permissionsGranterForSaveFileToLocal.onActivityResult();
                break;
            case REQUEST_CODE_10:
                permissionsGranterForShowDownloadFileDialog.onActivityResult();
                break;
            /*case REQUEST_CODE_12:
                permissionsGranterForShowFileDetailsDialog.onActivityResult();
                break;*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_01:
                permissionsGranterForChoosePhoto.onRequestPermissionsResult(permissions, grantResults);
                break;
            case REQUEST_CODE_03:
                permissionsGranterForChooseFile.onRequestPermissionsResult(permissions, grantResults);
                break;

            case REQUEST_CODE_05:
                permissionsGranterForSavePictureToLocal.onRequestPermissionsResult(permissions, grantResults);
                break;
            case REQUEST_CODE_07:
                permissionsGranterForSaveFileToLocal.onRequestPermissionsResult(permissions, grantResults);
                break;
            case REQUEST_CODE_09:
                permissionsGranterForShowDownloadFileDialog.onRequestPermissionsResult(permissions, grantResults);
                break;
            /*case REQUEST_CODE_11:
                permissionsGranterForShowFileDetailsDialog.onRequestPermissionsResult(permissions, grantResults);
                break;*/
        }
    }

    private void doImage(String imagePath) {
        if (imagePath != null) {
            if (new File(imagePath).exists()) {
                long a = System.currentTimeMillis();
                File var2 = new File(DataFiles.IMAGES_DIR, Utils.valueOf(a));
                if (!var2.getParentFile().exists()) {
                    var2.getParentFile().mkdirs();
                }
                if (var2.exists()) var2.delete();
                new Thread(()->{
                    Looper.prepare();
                    try {
                        var2.createNewFile();
                        FileUtils.copy(new File(imagePath), var2);
                        send(StringUtils.hexBytesToString(FileUtils.toByteArray(var2)), MessageTypes.PICTURE.code, Utils.valueOf(a),null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.showDialog(context, getText(R.string.dialog_exception_failed_to_send_message), e.toString());
                    }
                    sendingPicture.dismiss();
                    Looper.loop();
                }).start();
            } else {
                Toast.makeText(context, getText(R.string.toast_target_file_not_found), Toast.LENGTH_SHORT).show();
                sendingPicture.dismiss();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_failed_to_get_photo_from_album), Toast.LENGTH_SHORT).show();
            sendingPicture.dismiss();
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
        if(canLogin==null){
            new Thread(() -> {
                Looper.prepare();
                try {
                    canLogin = GetAccountUtils.checkCanLogin(Utils.getAccountUtils(), context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }).start();
        }
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        } else {
            new Thread(() -> {
                Looper.prepare();
                try {
                    String b = Utils.valueOf(Utils.getAccountInformation().getAccountE());
                    String c = Utils.valueOf(Utils.getAccountInformation().getEmailE());
                    Variables.COMMUNICATOR = new Communicator(this, b, c);
                    try {
                        if (Variables.COMMUNICATOR.connect()) {
                            //Toast.makeText(context, R.string.loadinglog_success_connect_communication_server, Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(context, R.string.loadinglog_failed_connect_communication_server, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(context, String.format(MSChatRoom.getContext().getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void messageReceived(IoSession session, Object message, int contentType, String if_the_message_is_a_picture_then_i_am_millis) {
        try {
            JSONObject jsonObject = new JSONObject(Utils.valueOf(message));
            String fromEncrypted = jsonObject.optString("from");
            String[] array = GetAccountUtils.getAnotherIDAndSelf(Utils.getAccountUtils(), context, fromEncrypted);
            if (fromEncrypted.equals(eoaEncrypted) || fromEncrypted.equals(array[0]) || fromEncrypted.equals(array[1])) {

                //int a = jsonObject.optInt("type", MessageTypes.TEXT.code);
                /**code for send types*/
                if (contentType == MessageTypes.PICTURE.code) {
                    messageItemList.add(new MessageItem(if_the_message_is_a_picture_then_i_am_millis, MessageItem.TYPE_RECEIVER, contentType));
                }else if (contentType == MessageTypes.FILE.code) {
                    messageItemList.add(new FileMessageItem(jsonObject.optString("content"), MessageItem.TYPE_RECEIVER, contentType)
                            .setFileName(jsonObject.optString("fileName"))
                            .setFileSize(jsonObject.optLong("fileSize")));
                } else {
                    messageItemList.add(new MessageItem(StringUtils.unicodeToText(jsonObject.optString("content")), MessageItem.TYPE_RECEIVER, contentType));
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

    void choosePhoto() {
        FileChooseUtils.startChooserActivity(ChatScreen.this, REQUEST_CHOOSE_PICTURE_FOR_SEND, "image/*");
    }

    void chooseFile() {
        FileChooseUtils.startChooserActivity(ChatScreen.this, REQUEST_CHOOSE_FILE);
    }

    void init() {
        messageItemList=new ArrayList<MessageItem>();
        if (chatFile.exists()) {
            try {
                String content = FileUtils.getString(chatFile);
                if (!"[]".equals(content)) {
                    JSONArray messages = new JSONArray(content);
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject object = messages.getJSONObject(i);
                        if(object.has("f")){
                            FileMessageItem messageItem = new FileMessageItem(object.optString("c"), object.optInt("t"), object.optInt("y"));
                            messageItem.setFileName(object.optString("f"));
                            messageItem.setFileSize(object.optLong("i"));
                            /**code4*/
                            messageItem.setTime(object.optLong("s"));
                            messageItemList.add(messageItem);
                        }else{
                            MessageItem messageItem = new MessageItem(object.optString("c"), object.optInt("t"), object.optInt("y"));
                            messageItem.setTime(object.optLong("s"));
                            messageItemList.add(messageItem);
                        }
                    }
                }

                /*messageItemList = new Gson().fromJson(, new TypeToken<List<MessageItem>>() {
                }.getType());*/
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_load_chat));
            }
        }
        adapter = new MessagesAdapter(context, messageItemList, new File(DataFiles.CHAT_AVATARS_DIR, eoaEncrypted).getAbsolutePath(), eoaEncrypted, (messageItem) -> {
            int indexOf = messageItemList.indexOf(messageItem);

            /**code for send types*/
            if(messageItem.getContentType()==MessageTypes.PICTURE.code){
                File file=new File(DataFiles.IMAGES_DIR,messageItem.getContent());
                if(file.exists()){
                    file.delete();
                }
            }else if(messageItem.getContentType()==MessageTypes.FILE.code){
                File file=new File(DataFiles.FILES_DIR,messageItem.getContent());
                if(file.exists()){
                    file.delete();
                }
            }


            //messageItemList.remove(indexOf);
            //adapter.notifyDataSetChanged();

            try {
                String content = FileUtils.getString(chatFile);
                JSONArray messages = new JSONArray(content);
                /**code: failed to delete (file)*/
                JSONArray newa = Utils.jsonArrayRemove(messages, indexOf);
                if (newa.optJSONObject(indexOf - 1).optInt("t", 0) == MessageItem.TYPE_TIME) {

                    //Toast.makeText(context, "279", Toast.LENGTH_SHORT).show();

                    /**Explain
                     List<String>s=new ArrayList<>();
                     s.add("0001");
                     s.add("0002");
                     s.add("0003");

                     int indexOf= s.indexOf("0002");

                     System.out.println("(s.size()-1 >= indexOf+1) = " + (s.size()-1 >= indexOf+1));//true
                     */

                    if (newa.length() == indexOf) {
                        //Toast.makeText(context, "726", Toast.LENGTH_SHORT).show();
                        newa = Utils.jsonArrayRemove(newa, indexOf - 1);
                    } else {
                        JSONObject va = newa.optJSONObject(indexOf);
                        if (va != null) {
                            int var = va.optInt("t", 0);
                            if (var == MessageItem.TYPE_TIME) {
                                newa = Utils.jsonArrayRemove(newa, indexOf - 1);
                                //Toast.makeText(context, "739", Toast.LENGTH_SHORT).show();
                            } else if (var == MessageItem.TYPE_FAILED_SEND
                                    || var == MessageItem.TYPE_FAILED_SEND_SO
                                    || var == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED
                                    || var == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO
                                    || var == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED
                                    || var == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO
                                    || var == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED
                                    || var == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO
                                    || var == MessageItem.TYPE_FAILED_SEND_OFFLINE
                                    || var == MessageItem.TYPE_FAILED_SEND_OFFLINE_SO) {
                                //Toast.makeText(context, "749", Toast.LENGTH_SHORT).show();
                                newa = Utils.jsonArrayRemove(newa, indexOf);
                                newa = Utils.jsonArrayRemove(newa, indexOf - 1);
                            }

                        } else {
                            //Toast.makeText(context, "757", Toast.LENGTH_SHORT).show();
                            newa = Utils.jsonArrayRemove(newa, indexOf - 1);
                        }
                    }

                }


                int lastOffset = 0, lastPosition = 0;
                LinearLayoutManager layoutManager = (LinearLayoutManager) recycler_view.getLayoutManager();
                //获取可视的第一个view
                View topView = layoutManager.getChildAt(0);
                if (topView != null) {
                    //获取与该view的顶部的偏移量
                    lastOffset = topView.getTop();
                    //得到该View的数组位置
                    lastPosition = layoutManager.getPosition(topView);
                }


                FileUtils.modifyFile(chatFile, newa.toString(), false);
                recycler_view.setAdapter(null);

                messageItemList = new ArrayList<>();
                init();

                if (recycler_view.getLayoutManager() != null && lastPosition >= 0) {
                    ((LinearLayoutManager) recycler_view.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.toast_failed_to_delete, Toast.LENGTH_SHORT).show();
            }
        }, (var0)->{
            permissionsGranterForSaveFileToLocal.setArguments(new Object[]{var0});
            permissionsGranterForSaveFileToLocal.start();
        },(var0)->{
            permissionsGranterForSavePictureToLocal.setArguments(new Object[]{var0});
            permissionsGranterForSavePictureToLocal.start();
        },(var0)->{
            permissionsGranterForShowDownloadFileDialog.setArguments(new Object[]{var0});
            permissionsGranterForShowDownloadFileDialog.start();
        },(var0)->{
            //permissionsGranterForShowFileDetailsDialog.setArguments(new Object[]{var0});
            //permissionsGranterForShowFileDetailsDialog.start();
            showFileDetailsDialog((FileMessageItem)var0);
        });

        runOnUiThread(()->{
            recycler_view.setAdapter(adapter);
            recycler_view.scrollToPosition(messageItemList.size() - 1);
        });
    }

    void freshAndKeepLocation(){
        int lastOffset = 0, lastPosition = 0;
        LinearLayoutManager layoutManager = (LinearLayoutManager) recycler_view.getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if (topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = layoutManager.getPosition(topView);
        }


        recycler_view.setAdapter(null);

        messageItemList = new ArrayList<>();
        init();

        if (recycler_view.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) recycler_view.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }

    void savePictureToLocal(MessageItem messageItem){
        File file = new File(DataFiles.IMAGES_DIR, messageItem.getContent());
        if (file.exists()) {
            try {
                File to = Utils.createLocalPictureFileAndCreate(FileUtils.toByteArray(file));
                StreamUtils.copy(file, to);
                Toast.makeText(context, String.format(context.getString(R.string.toast_successfully_save_picture), to.getAbsolutePath()), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.toast_failed_to_save_file, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.toast_target_file_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Download file to /storage/emulated/0/MSChatRoom/Files
     */
    void saveFileToLocal(MessageItem messageItem){
        File file = new File(DataFiles.FILES_DIR, messageItem.getContent());
        if (file.exists()) {
            try {
                File to = new File(DataFiles.INTERNAL_FILES_DIR,((FileMessageItem)messageItem).getFileName());
                if(to.exists()){
                    for (int i=1;;i++){
                        String s=" ("+i+")";
                        String fileFullName=to.getName();

                        String fileNameNoExi=fileFullName;
                        String houzhui="";

                        int i1=fileFullName.lastIndexOf(".");
                        if(i1!=-1){
                            fileNameNoExi=fileFullName.substring(0,i1);
                            houzhui=to.getName().substring(i1);
                        }

                        if(fileNameNoExi.contains(" (")&&fileNameNoExi.endsWith(")")){
                            String numberWithKuohao=fileNameNoExi.substring(fileNameNoExi.lastIndexOf(" ("));
                            String inside=numberWithKuohao.substring(" (".length(),numberWithKuohao.length()-1);
                            if(Utils.isNumber(inside)){
                                int insideInt=Integer.parseInt(inside);
                                String fileNameNoExiNoNumber=fileNameNoExi.substring(0,fileNameNoExi.lastIndexOf(" ("));
                                to=new File(DataFiles.INTERNAL_FILES_DIR,fileNameNoExiNoNumber+" ("+String.valueOf(insideInt+1)+")"+houzhui);
                            }else{
                                to=new File(DataFiles.INTERNAL_FILES_DIR,fileNameNoExi+s+houzhui);
                            }
                        }else{
                            to=new File(DataFiles.INTERNAL_FILES_DIR,fileNameNoExi+s+houzhui);
                        }
                        if(!to.exists()) break;
                    }
                    //Toast.makeText(context, to.getName(), Toast.LENGTH_SHORT).show();
                }

                StreamUtils.copy(file, to);
                Toast.makeText(context, String.format(context.getString(R.string.toast_successfully_save_picture), to.getAbsolutePath()), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.toast_failed_to_save_file, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.toast_target_file_not_found, Toast.LENGTH_SHORT).show();
        }
    }



    void showDownloadFileDialog(String millis) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        dialog.setMessage(context.getText(R.string.dialog_downloading_message));
        if (Utils.isNetworkConnected(context)) {
            if (Variables.SESSION != null && Variables.COMMUNICATOR != null && Variables.SESSION.isConnected()) {
                downloadFile(dialog, millis);
            } else {
                //dialog.show();
                new Thread(() -> {
                    Looper.prepare();
                    try {
                        String b = Utils.valueOf(Utils.getAccountInformation().getAccountE());
                        String c = Utils.valueOf(Utils.getAccountInformation().getEmailE());
                        Variables.COMMUNICATOR = new Communicator(context, b, c);
                        if (Variables.COMMUNICATOR.connect()) {
                            downloadFile(dialog, millis);
                        } else {
                            Toast.makeText(context, R.string.toast_failed_connect_to_server, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, R.string.toast_failed_connect_to_server, Toast.LENGTH_SHORT).show();
                    }
                    //dialog.dismiss();

                    Looper.loop();
                }).start();
            }
        } else {
            Toast.makeText(context, R.string.toast_please_check_your_network, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Download file to /data/user/0/com.mrshiehx.mschatroom/files/files/
     */
    void downloadFile(ProgressDialog dialog, String millis) {
        dialog.show();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        freshAndKeepLocation();
                        break;
                    case 1001:
                        Toast.makeText(context, R.string.toast_failed_to_download_file_from_server, Toast.LENGTH_SHORT).show();
                        break;
                    case 1002:
                        ((ProgressDialog)msg.obj).dismiss();
                        break;
                }
            }
        };
        Variables.COMMUNICATOR.sendCommand(ServerCommands.downloadFile(millis), (content) -> {
            if (!TextUtils.isEmpty(content)) {
                File file=new File(DataFiles.FILES_DIR,millis);
                try {
                    StreamUtils.hexWrite(content, file);
                    Message message=new Message();
                    message.what=1000;
                    handler.sendMessage(message);
                    //onFileDownloaded.onDownloaded();
                }catch (IOException e){
                    e.printStackTrace();
                    Utils.exceptionDialog(context,e,context.getString(R.string.dialog_exception_failed_to_download_file));
                }
            } else {
                //System.out.println(getString(R.string.toast_failed_to_download_file_from_server));
                Message message=new Message();
                message.what=1001;
                handler.sendMessage(message);
            }
            Message message=new Message();
            message.what=1002;
            message.obj=dialog;
            handler.sendMessage(message);
        });
    }

    void showFileDetailsDialog(FileMessageItem messageItem) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_file_details_title);
        dialog.setMessage(String.format(context.getString(R.string.dialog_file_details_message),messageItem.getFileName(),messageItem.getFormattedFileSize()));
        dialog.setNeutralButton(R.string.dialog_file_details_button_open,(var0,var1)->{
            //File src=new File(DataFiles.FILES_DIR,millis);
            File dest=new File(DataFiles.INTERNAL_FILES_DIR,messageItem.getFileName());
            if(dest.exists()){
                Utils.openFileByOtherApplication(context,dest);
            }else{
                Toast.makeText(context, R.string.toast_please_try_after_downlaod, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton(android.R.string.cancel,null);
        dialog.setPositiveButton(R.string.message_options_save_to_local,(var0,var1)->{
            permissionsGranterForSaveFileToLocal.setArguments(new Object[]{messageItem});
            permissionsGranterForSaveFileToLocal.start();
        });
        dialog.show();
    }
}
