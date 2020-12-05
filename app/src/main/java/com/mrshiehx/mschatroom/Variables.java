package com.mrshiehx.mschatroom;

public class Variables {
    //服务器地址
    public static String SERVER_ADDRESS = "xxx.xxx.xx.x";
    //获得验证码时间（秒）
    public static final int GET_CAPTCHA_TIME_SECOND = 60;
    //获得验证码时间（毫秒）
    public static final int GET_CAPTCHA_TIME = GET_CAPTCHA_TIME_SECOND * 1000;
    //MySQL数据库名称
    public static String DATABASE_NAME = "mschatroom";
    //MySQL数据库账号
    public static String DATABASE_USER = "xxxx";
    //MySQL数据库密码
    public static String DATABASE_PASSWORD = "";
    //MySQL表格名称
    public static String DATABASE_TABLE_NAME = "";
    //作者电子邮箱地址（用于出现错误反馈给作者）
    public static final String AUTHOR_MAIL = "xx@xx.com";

    public static final String TAG = "MSChatRoom";
    public static final String TAG_SHORT = "MSCR";

    //拆分符号（不要使用字母和数字）
    public static final String SPLIT_SYMBOL = "&";
    //文本加密密钥（不能太长，也不能太短）
    public static final String TEXT_ENCRYPTION_KEY = "xxxxxxxx";


    //SharedPreferences，除了SHARED_PREFERENCE_MODIFY_THEME和SHARED_PREFERENCE_MODIFY_LANGUAGE要在res/xml/activity_settings.xml里改，其他的可以随便改
    public static final String SHARED_PREFERENCE_MODIFY_THEME = "modify_theme";
    public static final String SHARED_PREFERENCE_IS_LOGINED = "isLogined";
    public static final String SHARED_PREFERENCE_IS_FIRST_RUN = "isFirstRun";
    public static final String SHARED_PREFERENCE_MODIFY_LANGUAGE = "modify_language";
    public static final String SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD = "accountAndPassword";
    public static final String SHARED_PREFERENCE_EMAIL_AND_PASSWORD = "emailAndPassword";
    public static final String SHARED_PREFERENCE_EMAIL_OR_ACCOUNT_AND_PASSWORD = "emailOrAccountAndPassword";
    public static final String SHARED_PREFERENCE_IS_REMEMBER_EOA_AND_PASSWORD = "isRememberEOAAndPassword";
    public static final String SHARED_PREFERENCE_LOGIN_METHOD = "loginMethod";
}
