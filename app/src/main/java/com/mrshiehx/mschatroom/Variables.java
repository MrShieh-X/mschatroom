package com.mrshiehx.mschatroom;

import android.Manifest;

import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.utils.AccountUtils;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class Variables {
    public static final String TAG = "MSChatRoom";
    public static final String TAG_SHORT = "MSCR";

    //默认服务器地址
    public static String DEFAULT_SERVER_ADDRESS = "192.168.101.157";
    //默认服务器端口
    public static int DEFAULT_SERVER_PORT = 6553;
    //默认MySQL数据库名称
    public static String DEFAULT_DATABASE_NAME = "mscr";
    //默认MySQL数据库账号
    public static String DEFAULT_DATABASE_USER_NAME = "root";
    //默认MySQL数据库密码
    public static String DEFAULT_DATABASE_USER_PASSWORD = "wwwwwww";
    //默认MySQL表格名称
    public static String DEFAULT_DATABASE_TABLE_NAME = "users";

    //服务器地址
    public static String SERVER_ADDRESS = DEFAULT_SERVER_ADDRESS;
    //服务器端口
    public static int SERVER_PORT = DEFAULT_SERVER_PORT;
    //MySQL数据库名称
    public static String DATABASE_NAME = DEFAULT_DATABASE_NAME;
    //MySQL数据库账号
    public static String DATABASE_USER = DEFAULT_DATABASE_USER_NAME;
    //MySQL数据库密码
    public static String DATABASE_PASSWORD = DEFAULT_DATABASE_USER_PASSWORD;
    //MySQL表格名称
    public static String DATABASE_TABLE_NAME = DEFAULT_DATABASE_TABLE_NAME;
    //作者电子邮箱地址（用于出现错误反馈给作者和关于页面联系作者）
    public static final String AUTHOR_MAIL = "bntoylort@outlook.com";
    //拆分符号（不要使用字母和数字）
    public static final String SPLIT_SYMBOL = "&";
    //文本加密密钥（不能太长，也不能太短）
    public static final String TEXT_ENCRYPTION_KEY = "fuckfuckfuckfuckfuckfuck";
    //发送验证码邮件的邮箱的SMTP服务器地址
    public static final String CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS = "smtp.163.com";
    //发送验证码邮件的邮箱地址
    public static String CAPTCHA_EMAIL_ADDRESS = "mrsxservice@163.com";
    //发送验证码邮件的邮箱的授权码
    public static String AUTHENTICATOR = "TFXQXEGWNOJVWOVE";
    //作者的网站
    public static final String AUTHOR_WEBSITE_URL = "https://mrshieh-x.github.io";
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
    public static final int GET_CAPTCHA_TIME_SECOND = 5;
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
        SHARED_PREFERENCE_MODIFY_THEME、
        SHARED_PREFERENCE_MODIFY_LANGUAGE、
        SHARED_PREFERENCE_SERVER_ADDRESS、
        SHARED_PREFERENCE_SERVER_PORT、
        SHARED_PREFERENCE_DATABASE_NAME、
        SHARED_PREFERENCE_DATABASE_USER_NAME、
        SHARED_PREFERENCE_DATABASE_USER_PASSWORD、
        SHARED_PREFERENCE_DATABASE_TABLE_NAME、
        SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING、
        SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY、
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME和
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT，
    其他的可以随便改*/
    public static final String SHARED_PREFERENCE_MODIFY_THEME = "modify_theme";
    public static final String SHARED_PREFERENCE_IS_FIRST_RUN = "isFirstRun";
    public static final String SHARED_PREFERENCE_MODIFY_LANGUAGE = "modify_language";
    public static final String SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD = "loginInfomation";
    public static final String SHARED_PREFERENCE_EMAIL_OR_ACCOUNT_AND_PASSWORD = "emailOrAccountAndPassword";
    public static final String SHARED_PREFERENCE_IS_REMEMBER_EOA_AND_PASSWORD = "isRememberEOAAndPassword";
    public static final String SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING = "show_avatar_when_chatting";
    public static final String SHARED_PREFERENCE_NEW_MESSAGES_NOTIFY = "new_messages_notify";
    //存储用户信息
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME = "%s_information";
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT = "%1$s=%2$s;nickname=%3$s;gender=%4$s;whatsup=%5$s";
}

