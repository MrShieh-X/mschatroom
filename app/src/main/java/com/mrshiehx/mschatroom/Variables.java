package com.mrshiehx.mschatroom;

import android.Manifest;

import com.mrshiehx.mschatroom.utils.AccountUtils;

public class Variables {
    public static final String TAG = "MSChatRoom";
    public static final String TAG_SHORT = "MSCR";

    //默认服务器地址
    public static String DEFAULT_SERVER_ADDRESS = "xxx.xxx.xxx.xx";
    //默认MySQL数据库名称
    public static String DEFAULT_DATABASE_NAME = "";
    //默认MySQL数据库账号
    public static String DEFAULT_DATABASE_USER_NAME = "";
    //默认MySQL数据库密码
    public static String DEFAULT_DATABASE_USER_PASSWORD = "";
    //默认MySQL表格名称
    public static String DEFAULT_DATABASE_TABLE_NAME = "";

    public static final String REAL_SHARED_PREFERENCE_SERVER_ADDRESS = "server_address";
    public static final String REAL_SHARED_PREFERENCE_DATABASE_NAME = "database_name";
    public static final String REAL_SHARED_PREFERENCE_DATABASE_USER_NAME = "database_user_name";
    public static final String REAL_SHARED_PREFERENCE_DATABASE_USER_PASSWORD = "database_user_password";
    public static final String REAL_SHARED_PREFERENCE_DATABASE_TABLE_NAME = "database_table_name";
    //服务器地址
    public static String SERVER_ADDRESS = MSCRApplication.getSharedPreferences().getString(REAL_SHARED_PREFERENCE_SERVER_ADDRESS,DEFAULT_SERVER_ADDRESS);
    //MySQL数据库名称
    public static String DATABASE_NAME = MSCRApplication.getSharedPreferences().getString(REAL_SHARED_PREFERENCE_DATABASE_NAME,DEFAULT_DATABASE_NAME);
    //MySQL数据库账号
    public static String DATABASE_USER = MSCRApplication.getSharedPreferences().getString(REAL_SHARED_PREFERENCE_DATABASE_USER_NAME,DEFAULT_DATABASE_USER_NAME);
    //MySQL数据库密码
    public static String DATABASE_PASSWORD = MSCRApplication.getSharedPreferences().getString(REAL_SHARED_PREFERENCE_DATABASE_USER_PASSWORD,DEFAULT_DATABASE_USER_PASSWORD);
    //MySQL表格名称
    public static String DATABASE_TABLE_NAME = MSCRApplication.getSharedPreferences().getString(REAL_SHARED_PREFERENCE_DATABASE_TABLE_NAME,DEFAULT_DATABASE_TABLE_NAME);
    //作者电子邮箱地址（用于出现错误反馈给作者和关于页面联系作者）
    public static final String AUTHOR_MAIL = "bntoylort@outlook.com";
    //拆分符号（不要使用字母和数字）
    public static final String SPLIT_SYMBOL = "&";
    //文本加密密钥（不能太长，也不能太短）
    public static final String TEXT_ENCRYPTION_KEY = "";
    //发送验证码邮件的邮箱的SMTP服务器地址
    public static final String CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS = "smtp.xxx.com";
    //发送验证码邮件的邮箱地址
    public static String CAPTCHA_EMAIL_ADDRESS = "xx@xxx.com";
    //发送验证码邮件的邮箱的授权码
    public static String AUTHENTICATOR = "XXXXXXXXXX";
    //作者的网站
    public static final String AUTHOR_WEBSITE_URL="https://mrshieh-x.github.io";
    //作者的GitHub地址
    public static final String AUTHOR_GITHUB_URL="https://www.github.com/MrShieh-X";
    //应用程序的GitHub地址
    public static final String APP_GITHUB_REPOSITORY_URL="https://www.github.com/MrShieh-X/mschatroom";
    //应用程序默认显示语言
    public static final String DEFAULT_LANGUAGE="en_US";
    //应用所需要获得的权限
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //离线模式所使用的账户信息
    public static AccountInformation ACCOUNT_INFORMATION;
    //检查更新更新文件地址
    public static final String NEW_VERSION_FILE_INFORMATION_URL="https://gitee.com/MrShiehX/mschatroom/raw/master/new_version.json";
    //获得验证码时间（秒）
    public static final int GET_CAPTCHA_TIME_SECOND = 60;
    //获得验证码时间（毫秒）
    public static final int GET_CAPTCHA_TIME = GET_CAPTCHA_TIME_SECOND * 1000;
    //MySQL 账户工具类，在LoadingScreen提前建立连接，进入界面后不用再次建立
    public static AccountUtils ACCOUNT_UTILS;

    /*SharedPreferences，除了
        SHARED_PREFERENCE_MODIFY_THEME、
        SHARED_PREFERENCE_MODIFY_LANGUAGE、
        SHARED_PREFERENCE_SERVER_ADDRESS、
        SHARED_PREFERENCE_DATABASE_NAME、
        SHARED_PREFERENCE_DATABASE_USER_NAME、
        SHARED_PREFERENCE_DATABASE_USER_PASSWORD、
        SHARED_PREFERENCE_DATABASE_TABLE_NAME、
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME和
        SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT，
    其他的可以随便改*/
    public static final String SHARED_PREFERENCE_MODIFY_THEME = "modify_theme";
    public static final String SHARED_PREFERENCE_IS_LOGINED = "isLogined";
    public static final String SHARED_PREFERENCE_IS_FIRST_RUN = "isFirstRun";
    public static final String SHARED_PREFERENCE_MODIFY_LANGUAGE = "modify_language";
    public static final String SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD = "accountAndPassword";
    public static final String SHARED_PREFERENCE_EMAIL_AND_PASSWORD = "emailAndPassword";
    public static final String SHARED_PREFERENCE_EMAIL_OR_ACCOUNT_AND_PASSWORD = "emailOrAccountAndPassword";
    public static final String SHARED_PREFERENCE_IS_REMEMBER_EOA_AND_PASSWORD = "isRememberEOAAndPassword";
    public static final String SHARED_PREFERENCE_LOGIN_METHOD = "loginMethod";
    public static final String SHARED_PREFERENCE_SERVER_ADDRESS = REAL_SHARED_PREFERENCE_SERVER_ADDRESS;
    public static final String SHARED_PREFERENCE_DATABASE_NAME = REAL_SHARED_PREFERENCE_DATABASE_NAME;
    public static final String SHARED_PREFERENCE_DATABASE_USER_NAME = REAL_SHARED_PREFERENCE_DATABASE_USER_NAME;
    public static final String SHARED_PREFERENCE_DATABASE_USER_PASSWORD = REAL_SHARED_PREFERENCE_DATABASE_USER_PASSWORD;
    public static final String SHARED_PREFERENCE_DATABASE_TABLE_NAME = REAL_SHARED_PREFERENCE_DATABASE_TABLE_NAME;
    //存储用户信息
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_NAME="%s_information";
    public static final String SHARED_PREFERENCE_ACCOUNT_INFORMATION_CONTENT="%1$s=%2$s;nickname=%3$s;gender=%4$s;whatsup=%5$s";
}

