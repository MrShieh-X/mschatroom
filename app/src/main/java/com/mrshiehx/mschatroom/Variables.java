package com.mrshiehx.mschatroom;

public class Variables {
    //服务器地址
    public static String SERVER_ADDRESS = "xx.xx.xx.xx";
    //获得验证码时间（秒）
    public static final int GET_CAPTCHA_TIME_SECOND = 60;
    //获得验证码时间（毫秒）
    public static final int GET_CAPTCHA_TIME = GET_CAPTCHA_TIME_SECOND * 1000;
    //MySQL数据库名称
    public static String DATABASE_NAME = "";
    //MySQL数据库账号
    public static String DATABASE_USER = "";
    //MySQL数据库密码
    public static String DATABASE_PASSWORD = "";
    //MySQL表格名称
    public static String DATABASE_TABLE_NAME = "";
    //作者电子邮箱地址（用于出现错误反馈给作者和关于页面联系作者）
    public static final String AUTHOR_MAIL = "bntoylort@outlook.com";

    public static final String TAG = "MSChatRoom";
    public static final String TAG_SHORT = "MSCR";

    //拆分符号（不要使用字母和数字）
    public static final String SPLIT_SYMBOL = "&";
    //文本加密密钥（不能太长，也不能太短）
    public static final String TEXT_ENCRYPTION_KEY = "";
    //发送验证码邮件的邮箱的SMTP服务器地址
    public static final String CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS = "smtp.xxx.com";
    //发送验证码邮件的邮箱地址
    public static String CAPTCHA_EMAIL_ADDRESS = "xx@xxx.com";
    //发送验证码邮件的邮箱的授权码
    public static String AUTHENTICATOR = "XXXXX";
    //作者的网站
    public static final String AUTHOR_WEBSITE_URL="https://mrshieh-x.github.io";
    //作者的GitHub地址
    public static final String AUTHOR_GITHUB_URL="https://www.github.com/MrShieh-X";
    //应用程序的GitHub地址
    public static final String APP_GITHUB_REPOSITORY_URL="https://www.github.com/MrShieh-X/mschatroom";

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
