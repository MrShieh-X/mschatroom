package com.mrshiehx.mschatroom.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrshiehx.mschatroom.LoadingScreen;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.chat.screen.ChatScreen;
import com.mrshiehx.mschatroom.chat.screen.ChatScreenLauncher;
import com.mrshiehx.mschatroom.main.chats.ChatItem;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Communicator {
    Context context;
    int notificationId=0;
    String eoaEncrypted;
    public Communicator(Context context, String eoaEncrypted) {
        this.context=context;
        this.eoaEncrypted=eoaEncrypted;
        init();
    }

    public void init(){
        Variables.ADDRESS = new InetSocketAddress(Variables.SERVER_ADDRESS, Variables.SERVER_PORT);
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
                    if (context instanceof LoadingScreen) {
                        ((LoadingScreen) context).sessionCreated(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                }

                @Override
                public void sessionOpened(IoSession session) throws Exception {
                    if (context instanceof LoadingScreen) {
                        ((LoadingScreen) context).sessionOpened(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                    Variables.SESSION=session;
                    session.write("eoaE:"+eoaEncrypted);
                }

                @Override
                public void sessionClosed(IoSession session) throws Exception {
                    if (context instanceof LoadingScreen) {
                        ((LoadingScreen) context).sessionClosed(session);
                    } else if (context instanceof MainScreen) {
                    } else if (context instanceof ChatScreen) {
                    }
                    Variables.SESSION=null;
                }

                @Override
                public void messageReceived(IoSession session, Object messageObject) throws Exception {
                    String message= URLDecoder.decode(messageObject.toString(),"UTF-8");
                    String fromEncrypted=new JSONObject(message).getString("f");
                    String content=new JSONObject(message).getString("c");
                    File folder=new File(Utils.getDataFilesPath(context),"chats");
                    File file=new File(Utils.getDataFilesPath(context),"chats"+File.separator+fromEncrypted+".json");
                    MessageItem messageItem=new MessageItem(content,MessageItem.TYPE_RECEIVER);
                    if(file.exists()){
                        String fileContent=FileUtils.getStringNoException(file);
                        JSONArray array=new JSONArray(fileContent);
                        String string = new Gson().toJson(messageItem);
                        array.put(new JSONObject(string));
                        FileUtils.modifyFile(file, array.toString(), false);

                    }else{
                        if(!folder.exists()){
                            folder.mkdirs();
                        }
                        file.createNewFile();
                        JSONArray array=new JSONArray();
                        String string = new Gson().toJson(messageItem);
                        array.put(new JSONObject(string));
                        FileUtils.modifyFile(file, array.toString(), false);
                    }

                    if (context instanceof MainScreen) {
                        ((MainScreen) context).messageReceived(session, message);
                        vibrate();
                    } else if (context instanceof ChatScreen) {
                        ((ChatScreen) context).messageReceived(session, message);
                        vibrate();
                    } else {
                        if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY, false)) {
                            List<ChatItem> list = new Gson().fromJson(FileUtils.getStringNoException(new File(Utils.getDataFilesPath(context), "chats.json")), new TypeToken<List<ChatItem>>() {
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
                                    .setContentText(content)
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.ic_launcher_single_color)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MSCRApplication.getContext());
                            notificationManager.notify(notificationId++, notification.build());
                            vibrate();
                        }
                    }
                }
            });
            Variables.CONNECTOR.setDefaultRemoteAddress(Variables.ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect() throws Exception {
        if(Variables.CONNECTOR==null||Variables.ADDRESS==null||Variables.SESSION==null)
            init();
        ConnectFuture future = Variables.CONNECTOR.connect();
        future.awaitUninterruptibly();
        Variables.SESSION = future.getSession();
        return Variables.SESSION != null;
    }

    public void disConnect() {
        if(Variables.CONNECTOR!=null) Variables.CONNECTOR.dispose();
        Variables.CONNECTOR = null;
        Variables.SESSION = null;
        Variables.ADDRESS = null;
    }

    public static void send(String contentClean){
        Variables.SESSION.write(URLEncoder.encode(contentClean));
    }

    public static void send(String targetEOAEncrypted, String contnet){
        send("{\"t\":\""+targetEOAEncrypted+"\",\"c\":\""+contnet+"\"}");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    void vibrate(){
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(150);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(150);
    }
}
