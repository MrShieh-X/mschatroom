package com.mrshiehx.mschatroom;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.icu.util.Freezable;

import com.mrshiehx.mschatroom.beans.AccountInformation;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Variables {
    private Variables(){}

    public static final String TAG = "MSChatRoom";
    public static final String TAG_SHORT = "MSCR";

    //服务器地址（数据库）
    public static String SERVER_ADDRESS = "xxx.xxx.xx.xx";
    //服务器地址（通讯）
    public static String SERVER_ADDRESS_COMMUNICATION = "xxx.xxx.xx.xx";
    //服务器端口（通讯）
    public static int SERVER_PORT = 6553;
    //MySQL数据库名称
    public static String DATABASE_NAME = "mschatroom";
    //MySQL数据库账号
    public static String DATABASE_USER = "mschatroom";
    //MySQL数据库密码
    public static String DATABASE_PASSWORD = "mypassword";
    //MySQL表格名称
    public static String DATABASE_TABLE_NAME = "users";
    //作者电子邮箱地址（用于出现错误反馈给作者和关于页面联系作者）
    public static final String AUTHOR_MAIL = "bntoylort@outlook.com";
    //拆分符号（不要使用字母和数字）
    public static final String SPLIT_SYMBOL = "&";
    //文本加密密钥（获取应用签名字符串）
    public static final String TEXT_ENCRYPTION_KEY = "encryptKey";
    //发送验证码邮件的邮箱的SMTP服务器地址
    public static final String CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS = "smtp.xxx.com";
    //发送验证码邮件的邮箱地址
    public static String CAPTCHA_EMAIL_ADDRESS = "xxxxxxx@xxx.com";
    //发送验证码邮件的邮箱的授权码
    public static String AUTHENTICATOR = "XXXXXXXXXXXXX";
    //作者的网站
    public static final String AUTHOR_WEBSITE_URL = "https://www.mrshiehx.xyz";
    //作者的GitHub地址
    public static final String AUTHOR_GITHUB_URL = "https://www.github.com/MrShieh-X";
    //应用程序的GitHub地址
    public static final String APP_GITHUB_REPOSITORY_URL = "https://www.github.com/MrShieh-X/mschatroom";
    //应用程序的Gitee地址
    public static final String APP_GITEE_REPOSITORY_URL = "https://www.gitee.com/MrShiehX/mschatroom";
    //应用程序默认显示语言
    public static final String DEFAULT_LANGUAGE = "en_US";
    //应用所需要获得的权限
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //离线模式所使用的账户信息
    public static AccountInformation ACCOUNT_INFORMATION;
    //检查更新更新文件地址
    public static final String NEW_VERSION_FILE_INFORMATION_URL = "https://gitee.com/MrShiehX/mschatroom/raw/master/new_version.json";
    //获得验证码时间（秒）
    public static final int GET_CAPTCHA_TIME_SECOND = 60;
    //获得验证码时间（毫秒）
    public static final int GET_CAPTCHA_TIME = GET_CAPTCHA_TIME_SECOND * 1000;
    //MySQL 账户工具类，在LoadingScreen提前建立连接，进入界面后不用再次建立
    public static AccountUtils ACCOUNT_UTILS;

    public static NioSocketConnector CONNECTOR;
    public static InetSocketAddress ADDRESS;
    public static IoSession SESSION;

    //Do not place Android context classes in static fields (static reference to Communicator which has field context pointing to Context); this is a memory leak
    public static Communicator COMMUNICATOR;

    /*SharedPreferences，除了
        SHARED_PREFERENCE_THEME、
        SHARED_PREFERENCE_LANGUAGE、
        SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING、
        SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY、
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME和
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT，
    其他的可以随便改*/
    public static final String SHARED_PREFERENCE_THEME = "theme";
    public static final String SHARED_PREFERENCE_IS_FIRST_RUN = "isFirstRun";
    public static final String SHARED_PREFERENCE_LANGUAGE = "language";
    public static final String SHARED_PREFERENCE_LOGIN_INFORMATION = "loginInformation";
    public static final String SHARED_PREFERENCE_REMEMBER_CONTENT = "rememberContent";
    public static final String SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING = "show_avatar_when_chatting";
    public static final String SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY = "new_messages_notify";
    public static final String SHARED_PREFERENCE_RECEIVE_STRANGERS_OFFLINE_MESSAGES = "receive_strangers_offline_messages";
    //存储用户信息
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME = "%s_information";
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT = "%1$s=%2$s;nickname=%3$s;gender=%4$s;whatsup=%5$s";
}

