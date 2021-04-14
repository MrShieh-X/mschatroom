package com.mrshiehx.mschatroom.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.format.Time;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.StartActivity;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.message.MessageTypes;
import com.mrshiehx.mschatroom.chat.screen.ChatScreen;
import com.mrshiehx.mschatroom.chat.screen.ChatScreenLauncher;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Communicator {
    Context context;
    int notificationId = 0;
    private final String aEncrypted;
    private final String eEncrypted;

    public Communicator(Context context, String aEncrypted, String eEncrypted) {
        this.context = context;
        this.aEncrypted = aEncrypted;
        this.eEncrypted = eEncrypted;
        init();
    }

    public void init() {
        Variables.ADDRESS = new InetSocketAddress(Variables.SERVER_ADDRESS_COMMUNICATION, Variables.SERVER_PORT);
        try {
            Variables.CONNECTOR = new NioSocketConnector();
            Variables.CONNECTOR.getSessionConfig().setReadBufferSize(2048);
            Variables.CONNECTOR.getFilterChain().addLast("logging", new LoggingFilter());
            TextLineCodecFactory var2 = new TextLineCodecFactory(Charset.forName("UTF-8"));
            var2.setDecoderMaxLineLength(1024 * 10240);
            var2.setEncoderMaxLineLength(1024 * 10240);
            Variables.CONNECTOR.getFilterChain().addLast("codec", new ProtocolCodecFilter(var2));
            Variables.CONNECTOR.setHandler(new IoHandlerAdapter() {
                @Override
                public void sessionCreated(IoSession session) throws Exception {
                    if (context instanceof StartActivity) {
                        ((StartActivity) context).sessionCreated(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                }

                @Override
                public void sessionOpened(IoSession session) throws Exception {
                    if (context instanceof StartActivity) {
                        ((StartActivity) context).sessionOpened(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                    Variables.SESSION = session;
                    session.write("aoE:" + aEncrypted + "\\" + eEncrypted);
                }

                @Override
                public void sessionClosed(IoSession session) throws Exception {
                    if (context instanceof StartActivity) {
                        ((StartActivity) context).sessionClosed(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                    Variables.SESSION = null;
                }

                @Override
                public void messageReceived(IoSession session, Object messageObject) throws Exception {
                    String message = URLDecoder.decode(Utils.valueOf(messageObject), "UTF-8");
                    JSONObject jsonObj = new JSONObject(message);
                    String fromEncrypted = jsonObj.optString("f");
                    String content = jsonObj.optString("c");
                    int type = jsonObj.optInt("t", 0);
                    File folder = DataFiles.CHATS_DIR;
                    //MessageItem messageItem = new MessageItem(content, MessageItem.TYPE_RECEIVER,type);

                    String from = EnDeCryptTextUtils.decrypt(fromEncrypted);
                    new Thread(() -> ((Activity) context).runOnUiThread(() -> {
                        String accountEn = "";
                        String emailEn = "";
                        if (Utils.isEmail(from)) {
                            emailEn = fromEncrypted;
                            accountEn = GetAccountUtils.getAccount(Utils.getAccountUtils(), context, emailEn);
                        } else {
                            accountEn = fromEncrypted;
                            emailEn = GetAccountUtils.getEmail(Utils.getAccountUtils(), context, accountEn);
                        }

                        /*File fileAccount=new File(Utils.getDataFilesPath(context),"chats"+File.separator+accountEn+".json");
                        File fileEmail=new File(Utils.getDataFilesPath(context),"chats"+File.separator+emailEn+".json");

                        //File file;
                        if(fileAccount.exists()){
                            file=fileAccount;
                        }else{
                            if(fileEmail.exists()){
                                file=fileEmail;
                            }else{
                                file=fileAccount;
                            }
                        }*/
                        try {
                            List<ChatItem> list = new Gson().fromJson(FileUtils.getString(com.mrshiehx.mschatroom.shared_variables.DataFiles.CHATS_FILE), new TypeToken<List<ChatItem>>() {
                            }.getType());

                            int when = -1000;
                            for (int i = 0; i < list.size(); i++) {
                                ChatItem item = list.get(i);
                                String eoa = item.getEmailOrAccount();
                                if (accountEn.equals(eoa) || emailEn.equals(eoa)) {
                                    when = i;
                                }
                            }
                            File file = new File(DataFiles.CHATS_DIR, fromEncrypted + ".json");
                            if (when != -1000) {
                                ChatItem item = list.get(when);
                                String eoa = item.getEmailOrAccount();
                                file = new File(DataFiles.CHATS_DIR, eoa + ".json");
                            }
                            long a = System.currentTimeMillis();
                            MessageItem newTimeItem = new MessageItem(/*year2 + "-" + month2 + "-" + day2 + ";" + (Utils.valueOf(hour2).length() != 2 ? "0" + hour2 : hour2) + ":" + (Utils.valueOf(minute2).length() != 2 ? "0" + minute2 : minute2)*/Utils.valueOf(a), MessageItem.TYPE_TIME, MessageTypes.TEXT.code);
                            if (file.exists()) {
                                String fileContent = FileUtils.getString(file);
                                JSONArray array = new JSONArray(fileContent);
                                List<Integer> types = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    types.add(array.optJSONObject(i).optInt("t"));
                                }
                                int indexOf = types.lastIndexOf(MessageItem.TYPE_TIME);


                                Time Time = new Time();
                                Time.set(a);
                                int year = Time.year;
                                /**START FROM 1*/
                                int month = Time.month + 1;
                                int day = Time.monthDay;
                                int hour = Time.hour;
                                int minute = Time.minute;


                                if (array.length() > 0) {
                                    MessageItem timeItem = MessageItem.valueOf(array.optJSONObject(indexOf));
                                    long t = timeItem.getTime();
                                    Time time = new Time();
                                    time.set(t);
                                    int yearF = time.year;
                                    int monthF = time.month + 1;
                                    int dayF = time.monthDay;
                                    int hourF = time.hour;
                                    // int minuteF = Integer.parseInt(hm[1]);
                                    //int xiangcha=minute-minuteF;
                                    //Toast.makeText(context, Utils.valueOf(yearF==year&&monthF==month&&dayF==day&&hourF==hour), Toast.LENGTH_SHORT).show();
                                    if (array.length() > 0) {
                                        if (yearF == year && monthF == month && dayF == day && hourF == hour) {
                                            int xiangcha = 6;
                                            try {
                                                Time time1 = new Time();
                                                time1.set(MessageItem.valueOf(array.optJSONObject(array.length() - 1)).getTime());
                                                xiangcha = minute - time1.minute;
                                            } catch (Throwable ignored) {
                                            }
                                            if (xiangcha >= 5) {
                                                //YES
                                                array.put(newTimeItem.toJSONObject());
                                            }

                                        } else {
                                            //YES
                                            array.put(newTimeItem.toJSONObject());
                                        }
                                    } else array.put(newTimeItem.toJSONObject());
                                } else array.put(newTimeItem.toJSONObject());


                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("y", type);
                                /**code for*/
                                if (type == MessageTypes.PICTURE.code) {
                                    jsonObject.put("c", Utils.valueOf(a));
                                } else {
                                    jsonObject.put("c", content);
                                }
                                jsonObject.put("t", MessageItem.TYPE_RECEIVER);
                                jsonObject.put("s", a);
                                /**code for*/
                                if (type == MessageTypes.PICTURE.code) {
                                    File images = DataFiles.IMAGES_DIR;
                                    if (!images.exists()) images.mkdirs();
                                    File file1 = new File(images, Utils.valueOf(a));
                                    if (file1.exists()) file1.delete();
                                    file1.createNewFile();
                                    FileUtils.bytes2File(FileUtils.hexString2Bytes(content), file1);
                                }

                                array.put(jsonObject);

                                FileUtils.modifyFile(file, array.toString(), false);

                            } else {
                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }
                                file.createNewFile();
                                JSONArray array = new JSONArray();


                                long aaa = System.currentTimeMillis();

                                /*Time Time = new Time();
                                Time.set(aaa);
                                int year = Time.year;
                                *//**START FROM 1*//*
                                int month = Time.month + 1;
                                int day = Time.monthDay;
                                int hour = Time.hour;
                                int minute = Time.minute;
                                MessageItem timeItem = new MessageItem(year + "-" + month + "-" + day + ";" + (Utils.valueOf(hour).length() != 2 ? "0" + hour : hour) + ":" + (Utils.valueOf(minute).length() != 2 ? "0" + minute : minute), MessageItem.TYPE_TIME,MessageTypes.TEXT.code);
*/


                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("y", type);
                                /**code for*/
                                if (type == MessageTypes.PICTURE.code) {
                                    jsonObject.put("c", Utils.valueOf(aaa));
                                } else {
                                    jsonObject.put("c", content);
                                }
                                jsonObject.put("t", MessageItem.TYPE_RECEIVER);
                                jsonObject.put("s", aaa);
                                if (type == MessageTypes.PICTURE.code) {
                                    File images = DataFiles.IMAGES_DIR;
                                    if (!images.exists()) images.mkdirs();
                                    File file1 = new File(images, Utils.valueOf(aaa));
                                    if (file1.exists()) file1.delete();
                                    file1.createNewFile();
                                    FileUtils.hexWrite(content, file1);
                                    //FileUtils.bytes2File(FileUtils.hexString2Bytes(content), file1);
                                }
                                array.put(newTimeItem);
                                array.put(jsonObject);

                                FileUtils.modifyFile(file, array.toString(), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (context instanceof MainScreen) {
                            String var;
                            /**code for*/
                            if (type == MessageTypes.PICTURE.code) {
                                var = context.getString(R.string.message_type_picture);
                            } else {
                                var = content;
                            }
                            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss
                            //Date date = new Date(System.currentTimeMillis());
                            ((MainScreen) context).messageReceived(session, message/*,var,simpleDateFormat.format(date)*/);
                        } else if (context instanceof ChatScreen) {
                            ((ChatScreen) context).messageReceived(session, message, type);
                        } else {
                            try {
                                String var = content;
                                /**code for*/
                                if (type == MessageTypes.PICTURE.code) {
                                    var = context.getString(R.string.message_type_picture);
                                }
                                if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY, false)) {
                                    List<ChatItem> list = new Gson().fromJson(FileUtils.getString(com.mrshiehx.mschatroom.shared_variables.DataFiles.CHATS_FILE), new TypeToken<List<ChatItem>>() {
                                    }.getType());
                                    List<String> stringList = new ArrayList<>();
                                    for (int i = 0; i < list.size(); i++) {
                                        stringList.add(list.get(i).getEmailOrAccount());
                                    }
                                    int indexOf = stringList.indexOf(fromEncrypted);
                                    ChatItem item = list.get(indexOf);
                                    String name = item.getName();
                                    String finalName;
                                    if (TextUtils.isEmpty(name)) {
                                        finalName = EnDeCryptTextUtils.decrypt(fromEncrypted);
                                    } else {
                                        finalName = EnDeCryptTextUtils.decrypt(name);
                                    }
                                    Intent intent = new ChatScreenLauncher(context, fromEncrypted, finalName, false).getIntent();
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MSCRApplication.getContext(), 0, intent, 0);
                                    String channelId = Utils.createNotificationChannel("mscrChannel", MSCRApplication.getContext().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(MSCRApplication.getContext(), channelId)
                                            .setContentTitle(finalName)
                                            .setContentText(var)
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(R.drawable.ic_launcher_single_color)
                                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setAutoCancel(true);
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MSCRApplication.getContext());
                                    notificationManager.notify(notificationId++, notification.build());

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        vibrate();
                    })).start();
                }
            });
            Variables.CONNECTOR.setDefaultRemoteAddress(Variables.ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect() throws Exception {
        if (Variables.CONNECTOR == null || Variables.ADDRESS == null || Variables.SESSION == null)
            init();
        ConnectFuture future = Variables.CONNECTOR.connect();
        future.awaitUninterruptibly();
        Variables.SESSION = future.getSession();
        return Variables.SESSION != null;
    }

    public void disConnect() {
        if (Variables.CONNECTOR != null) Variables.CONNECTOR.dispose();
        Variables.CONNECTOR = null;
        Variables.SESSION = null;
        Variables.ADDRESS = null;
    }

    public void send(String contentClean) {
        Variables.SESSION.write(URLEncoder.encode(contentClean));
    }

    private void send(String targetEOAEncrypted, String content) {
        send(targetEOAEncrypted, content, 0);
    }


    /**
     * Types
     * 0 is text
     * 1 is image
     **/
    public void send(String targetEOAEncrypted, String content, int type) {
        String jos;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("t", targetEOAEncrypted);
            jsonObject.put("c", content);
            jsonObject.put("y", type);
            jsonObject.put("f", aEncrypted);
            jos = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            jos = "{\"t\":\"" + targetEOAEncrypted + "\",\"c\":\"" + content + "\",\"f\":\"" + aEncrypted + "\",\"y\"" + type + "}";
        }
        send(jos);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    void vibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(150);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(150);
    }
}
