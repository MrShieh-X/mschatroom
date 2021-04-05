package com.mrshiehx.mschatroom.chat;

import android.app.Activity;
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
import com.mrshiehx.mschatroom.StartActivity;
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
                    String message = URLDecoder.decode(messageObject.toString(), "UTF-8");
                    String fromEncrypted = new JSONObject(message).getString("f");
                    String content = new JSONObject(message).getString("c");
                    File folder = new File(Utils.getDataFilesPath(context), "chats");
                    MessageItem messageItem = new MessageItem(content, MessageItem.TYPE_RECEIVER);

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
                            List<ChatItem> list = new Gson().fromJson(FileUtils.getString(new File(Utils.getDataFilesPath(context), "chats.json")), new TypeToken<List<ChatItem>>() {
                            }.getType());

                            int when = -1000;
                            for (int i = 0; i < list.size(); i++) {
                                ChatItem item = list.get(i);
                                String eoa = item.getEmailOrAccount();
                                if (accountEn.equals(eoa) || emailEn.equals(eoa)) {
                                    when = i;
                                }
                            }
                            File file = new File(Utils.getDataFilesPath(context), "chats" + File.separator + fromEncrypted + ".json");
                            if (when != -1000) {
                                ChatItem item = list.get(when);
                                String eoa = item.getEmailOrAccount();
                                file = new File(Utils.getDataFilesPath(context), "chats" + File.separator + eoa + ".json");
                            }
                            if (file.exists()) {
                                String fileContent = FileUtils.getString(file);
                                JSONArray array = new JSONArray(fileContent);
                                String string = new Gson().toJson(messageItem);
                                array.put(new JSONObject(string));
                                FileUtils.modifyFile(file, array.toString(), false);

                            } else {
                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }
                                file.createNewFile();
                                JSONArray array = new JSONArray();
                                String string = new Gson().toJson(messageItem);
                                array.put(new JSONObject(string));
                                FileUtils.modifyFile(file, array.toString(), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (context instanceof MainScreen) {
                            ((MainScreen) context).messageReceived(session, message);
                        } else if (context instanceof ChatScreen) {
                            ((ChatScreen) context).messageReceived(session, message);
                        } else {
                            try {
                                if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY, false)) {
                                    List<ChatItem> list = new Gson().fromJson(FileUtils.getString(new File(Utils.getDataFilesPath(context), "chats.json")), new TypeToken<List<ChatItem>>() {
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

    public void send(String targetEOAEncrypted, String content) {
        send("{\"t\":\"" + targetEOAEncrypted + "\",\"c\":\"" + content + "\",\"f\":\"" + aEncrypted + "\"}");
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
