package com.mrshiehx.mschatroom.chat.screen;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

//聊天界面（临时）
public class ChatScreen extends AppCompatActivity {
    public TextView chat_content;
    public EditText input_chat_content;
    public Button send;
    public ScrollView scroll_view;
    boolean can_send;
    Context context = ChatScreen.this;
    Context oContext;
    /*public ChatScreen(Context context){
        oContext=context;
    }
    public void startActivity(){
        Utils.startActivity(oContext, ChatScreen.class);
    }*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.initialization(ChatScreen.this, R.string.activity_chat_screen_name);
        setContentView(R.layout.activity_chat_screen);
        if (!Utils.networkAvailableDialog(context)) {
            can_send = false;
        } else {
            can_send = true;
        }


        chat_content = findViewById(R.id.chat_content);
        input_chat_content = findViewById(R.id.input_chat_content);
        send = findViewById(R.id.send);
        scroll_view = findViewById(R.id.scroll_view);
        if (TextUtils.isEmpty(input_chat_content.getText()) == true) {
            send.setEnabled(false);
        } else {
            if (can_send) {
                send.setEnabled(true);
            }
        }
        input_chat_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.isEmpty(input_chat_content.getText()) == true) {
                    send.setEnabled(false);
                } else {
                    if (can_send) {
                        send.setEnabled(true);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(input_chat_content.getText()) == true) {
                    send.setEnabled(false);
                } else {
                    if (can_send) {
                        send.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(input_chat_content.getText()) == true) {
                    send.setEnabled(false);
                } else {
                    if (can_send) {
                        send.setEnabled(true);
                    }
                }
            }
        });
        send.setEnabled(can_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context)) {
                    try {
                        URL url = new URL("http://www.bjtime.cn");
                        URLConnection uc = url.openConnection();//生成连接对象
                        uc.connect(); //发出连接
                        long ld = uc.getDate(); //取得网站日期时间
                        Date date = new Date(ld); //转换为标准时间对象
                        //分别取得时间中的小时，分钟和秒，并输出
                        System.out.print(date.getHours() + "时" + date.getMinutes() + "分" + date.getSeconds() + "秒");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e);
                    }


                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
                    Date date = new Date(System.currentTimeMillis());
                    chat_content.setText(chat_content.getText() + "[" + simpleDateFormat.format(date) + "]" + input_chat_content.getText() + "\n");
                    input_chat_content.setText(null);
                    scroll_view.scrollTo(0, chat_content.getBottom());
                } else {
                    //Snackbar.make(context, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    ServerSocket serverSocket;//创建ServerSocket对象
    Socket clicksSocket;//连接通道，创建Socket对象
    Button startButton;//发送按钮
    EditText portEditText;//端口号
    EditText receiveEditText;//接收消息框
    Button sendButton;//发送按钮
    EditText sendEditText;//发送消息框
    InputStream inputstream;//创建输入数据流
    OutputStream outputStream;//创建输出数据流
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        /**
         * 读一下手机wifi状态下的ip地址，只有知道它的ip才能连接它嘛
         *
        Snackbar.make(MainActivity.this, getLocalIpAddress(), Snackbar.LENGTH_SHORT).show();

        startButton = (Button) findViewById(R.id.start_button);
        portEditText = (EditText) findViewById(R.id.port_EditText);
        receiveEditText = (EditText) findViewById(R.id.receive_EditText);
        sendButton = (Button) findViewById(R.id.send_button);
        sendEditText = (EditText) findViewById(R.id.message_EditText);

        startButton.setOnClickListener(startButtonListener);
        sendButton.setOnClickListener(sendButtonListener);
    }*/
    /**
     * 启动服务按钮监听事件
     */
    private View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /**
             * 启动服务器监听线程
             */
            ServerSocket_thread serversocket_thread = new ServerSocket_thread();
            serversocket_thread.start();
        }
    };

    /**
     * 服务器监听线程
     */
    class ServerSocket_thread extends Thread {
        public void run()//重写Thread的run方法
        {
            try {
                int port = Integer.valueOf(portEditText.getText().toString());//获取portEditText中的端口号
                serverSocket = new ServerSocket(8080);//监听port端口，这个程序的通信端口就是port了
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Utils.exceptionDialog(context, e);
            }
            while (true) {
                try {
                    //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                    clicksSocket = serverSocket.accept();
                    inputstream = clicksSocket.getInputStream();//
                    //启动接收线程
                    Receive_Thread receive_Thread = new Receive_Thread();
                    receive_Thread.start();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Utils.exceptionDialog(context, e);
                }
            }
        }
    }

    /**
     * 接收线程
     */
    class Receive_Thread extends Thread//继承Thread
    {
        public void run()//重写run方法
        {
            while (true) {
                try {
                    final byte[] buf = new byte[1024];
                    final int len = inputstream.read(buf);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            receiveEditText.setText(new String(buf, 0, len));
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Utils.exceptionDialog(context, e);
                }
            }
        }
    }

    /**
     * 发送消息按钮事件
     */
    private View.OnClickListener sendButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            try {
                //获取输出流
                outputStream = clicksSocket.getOutputStream();
                //发送数据
                outputStream.write(sendEditText.getText().toString().getBytes());
                //outputStream.write("0".getBytes());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Utils.exceptionDialog(context, e);
            }
        }
    };

    /**
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
}
