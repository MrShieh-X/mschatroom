package com.mrshiehx.mschatroom.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.format.Time;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.beans.Command;
import com.mrshiehx.mschatroom.start.screen.StartActivity;
import com.mrshiehx.mschatroom.MSChatRoom;
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
import com.mrshiehx.mschatroom.utils.StreamUtils;
import com.mrshiehx.mschatroom.utils.StringUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Communicator {
    Context context;
    int notificationId = 0;
    private final String aEncrypted;
    private final String eEncrypted;

    private final Map<Long,OnServerReturned>commands;

    public Communicator(Context context, String aEncrypted, String eEncrypted) {
        this.context = context;
        this.aEncrypted = aEncrypted;
        this.eEncrypted = eEncrypted;
        this.commands=new HashMap<>();
        init();
    }

    public void init() {
        Variables.ADDRESS = new InetSocketAddress(Variables.SERVER_ADDRESS_COMMUNICATION, Variables.SERVER_PORT);
        try {
            Variables.CONNECTOR = new NioSocketConnector();
            Variables.CONNECTOR.getSessionConfig().setReadBufferSize(2048);
            Variables.CONNECTOR.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
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
                    session.write("aoe=" + aEncrypted + "/" + eEncrypted);
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
                    boolean toClient=jsonObj.optBoolean("toClient");
                    if(!toClient){

                        String fromEncrypted = jsonObj.optString("from");
                        int type = jsonObj.optInt("type", MessageTypes.TEXT.code);
                        String content=type==MessageTypes.TEXT.code?StringUtils.unicodeToText(jsonObj.optString("content")):jsonObj.optString("content");
                        File folder = DataFiles.CHATS_DIR;
                        //MessageItem messageItem = new MessageItem(content, MessageItem.TYPE_RECEIVER,type);

                        String from = EnDeCryptTextUtils.decrypt(fromEncrypted);
                        new Thread(() -> {
                            Looper.prepare();
                            try {
                                String accountEn;
                                String emailEn;
                                if (Utils.isEmail(from)) {
                                    emailEn = fromEncrypted;
                                    accountEn = GetAccountUtils.getAccount(Utils.getAccountUtils(), context, emailEn);
                                } else {
                                    accountEn = fromEncrypted;
                                    emailEn = GetAccountUtils.getEmail(Utils.getAccountUtils(), context, accountEn);
                                }
                                List<ChatItem> list = new Gson().fromJson(FileUtils.getString(DataFiles.CHATS_FILE), new TypeToken<List<ChatItem>>() {
                                }.getType());

                                int when = -1000;
                                for (int i = 0; i < list.size(); i++) {
                                    ChatItem item = list.get(i);
                                    String eoa = item.getEmailOrAccount();
                                    if (accountEn.equals(eoa) || emailEn.equals(eoa)) {
                                        when = i;
                                        break;
                                    }
                                }

                                if (when != -1000) {
                                    ChatItem item = list.get(when);
                                    String eoa = item.getEmailOrAccount();
                                    File file = new File(DataFiles.CHATS_DIR, eoa + ".json");
                                    long a = System.currentTimeMillis();
                                    MessageItem newTimeItem = new MessageItem(Utils.valueOf(a), MessageItem.TYPE_TIME, MessageTypes.TEXT.code);
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
                                        /*START FROM 1*/
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
                                        /**code for send types*/
                                        if (type == MessageTypes.PICTURE.code) {
                                            jsonObject.put("c", Utils.valueOf(a));
                                        } else if (type == MessageTypes.FILE.code) {
                                            jsonObject.put("c", jsonObj.optString("content"));
                                            jsonObject.put("f", jsonObj.optString("fileName"));
                                            jsonObject.put("i", jsonObj.optString("fileSize"));
                                        } else {
                                            jsonObject.put("c", content);
                                        }
                                        jsonObject.put("t", MessageItem.TYPE_RECEIVER);
                                        jsonObject.put("s", a);
                                        /**code for send types*/
                                        if (type == MessageTypes.PICTURE.code) {
                                            File images = DataFiles.IMAGES_DIR;
                                            if (!images.exists()) images.mkdirs();
                                            File file1 = new File(images, Utils.valueOf(a));
                                            if (file1.exists()) file1.delete();
                                            file1.createNewFile();
                                            FileUtils.bytes2File(StreamUtils.hexString2Bytes(content), file1);
                                        }

                                        array.put(jsonObject);

                                        FileUtils.modifyFile(file, array.toString(), false);

                                    } else {
                                        if (!folder.exists()) {
                                            folder.mkdirs();
                                        }
                                        file.createNewFile();
                                        JSONArray array = new JSONArray();




                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("y", type);
                                        /**code for send types*/
                                        if (type == MessageTypes.PICTURE.code) {
                                            jsonObject.put("c", Utils.valueOf(a));
                                        } else if (type == MessageTypes.FILE.code) {
                                            jsonObject.put("c", jsonObj.optString("content"));
                                            jsonObject.put("f", jsonObj.optString("fileName"));
                                            jsonObject.put("i", jsonObj.optString("fileSize"));
                                        } else {
                                            jsonObject.put("c", content);
                                        }
                                        jsonObject.put("t", MessageItem.TYPE_RECEIVER);
                                        jsonObject.put("s", a);
                                        /**code for send types*/
                                        if (type == MessageTypes.PICTURE.code) {
                                            File images = DataFiles.IMAGES_DIR;
                                            if (!images.exists()) images.mkdirs();
                                            File file1 = new File(images, Utils.valueOf(a));
                                            if (file1.exists()) file1.delete();
                                            file1.createNewFile();
                                            StreamUtils.hexWrite(content, file1);
                                            //FileUtils.bytes2File(FileUtils.hexString2Bytes(content), file1);
                                        }
                                        array.put(newTimeItem.toJSONObject());
                                        array.put(jsonObject);

                                        FileUtils.modifyFile(file, array.toString(), false);
                                    }

                                    if (context instanceof MainScreen) {
                                        /*String var;
                                        *//*code for send types*//*
                                        if (type == MessageTypes.PICTURE.code) {
                                            var = context.getString(R.string.message_type_picture);
                                        } else if (type == MessageTypes.FILE.code) {
                                            var = context.getString(R.string.message_type_file);
                                        } else {
                                            var = content;
                                        }*/
                                        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss
                                        ((MainScreen) context).messageReceived(session, message/*,var,simpleDateFormat.format(date)*/);
                                    } else if (context instanceof ChatScreen) {
                                        ((ChatScreen) context).messageReceived(session, message, type, Utils.valueOf(a));
                                    } else {
                                        try {
                                            String var = content;
                                            /**code for send types*/
                                            if (type == MessageTypes.PICTURE.code) {
                                                var = context.getString(R.string.message_type_picture);
                                            } else if (type == MessageTypes.FILE.code) {
                                                var = context.getString(R.string.message_type_file);
                                            }
                                            if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY, false)) {
                                            /*List<ChatItem> list = new Gson().fromJson(FileUtils.getString(com.mrshiehx.mschatroom.shared_variables.DataFiles.CHATS_FILE), new TypeToken<List<ChatItem>>() {
                                            }.getType());*/
                                                /*List<String> stringList = new ArrayList<>();
                                                for (int i = 0; i < list.size(); i++) {
                                                    stringList.add(list.get(i).getEmailOrAccount());
                                                }*/
                                                //int indexOf = stringList.indexOf(fromEncrypted);
                                                //ChatItem item = list.get(indexOf);
                                                String name = item.getName();
                                                String finalName;
                                                if (TextUtils.isEmpty(name)) {
                                                    finalName = EnDeCryptTextUtils.decrypt(fromEncrypted);
                                                } else {
                                                    finalName = EnDeCryptTextUtils.decrypt(name);
                                                }
                                                Intent intent = new ChatScreenLauncher(context, fromEncrypted, finalName, false).getIntent();
                                                PendingIntent pendingIntent = PendingIntent.getActivity(MSChatRoom.getContext(), 0, intent, 0);
                                                String channelId = Utils.createNotificationChannel("mscrChannel", MSChatRoom.getContext().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_UNSPECIFIED);
                                                NotificationCompat.Builder notification = new NotificationCompat.Builder(MSChatRoom.getContext(), channelId)
                                                        .setContentTitle(finalName)
                                                        .setContentText(var)
                                                        .setContentIntent(pendingIntent)
                                                        .setSmallIcon(R.drawable.ic_launcher_single_color)
                                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);
                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MSChatRoom.getContext());
                                                notificationManager.notify(notificationId++, notification.build());

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    vibrate();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }).start();
                    }else{
                        long id=jsonObj.optLong("id");
                        String content=jsonObj.optString("content",null);
                        List<Long>ids=new ArrayList<>(commands.keySet());
                        List<OnServerReturned>onServerReturnedList=new ArrayList<>(commands.values());
                        int indexOf=ids.indexOf(id);
                        if(indexOf!=-1){
                            onServerReturnedList.get(indexOf).execute(content);
                            commands.remove(id);
                        }
                    }
                }
            });
            Variables.CONNECTOR.setDefaultRemoteAddress(Variables.ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要线程
     */
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


    /**
     * Types
     * 0 is text
     * 1 is image
     **/
    public void send(String targetEOAEncrypted, String content, int type,Object[]args) {
        String unicodeContent=(type==MessageTypes.TEXT.code)?StringUtils.textToUnicode(content):content;
        String jos=null;
        try {
            JSONObject d=new JSONObject();
            /**code for send types*/
            if(type==MessageTypes.FILE.code&&args!=null&&args.length>0){
                d.put("fileName",(String)args[0]);
                d.put("fileSize",(long)args[1]);
                d.put("millis",args[2]);
            }
            jos =d.put("to", targetEOAEncrypted).put("content", unicodeContent).put("type", type).put("from", aEncrypted).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jos!=null)
        send(jos);
    }

    public void sendCommand(Command command, OnServerReturned onServerReturned){
        long millis=System.currentTimeMillis();
        commands.put(millis,onServerReturned);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("toServer", true);
            jsonObject.put("content", command.toString());
            jsonObject.put("id", millis);
        }catch (Exception e){
            e.printStackTrace();
        }
        send(jsonObject.toString());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    void vibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(150);
        try {
            Thread.sleep(350);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(150);
    }

    public interface OnServerReturned{
        void execute(String content);
    }
}
