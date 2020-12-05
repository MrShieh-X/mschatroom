package com.mrshiehx.mschatroom.utils;

import android.os.StrictMode;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//发送邮件（验证码）工具类
public class SendEmailUtils {

    private String PROTOCOL = "smtp";
    //发送验证码邮件的邮箱的SMTP服务器地址
    private String HOST = "smtp.163.com";
    private String PORT = "25";
    private String IS_AUTH = "true";
    private String IS_ENABLED_DEBUG_MOD = "true";
    //发送验证码邮件的邮箱地址
    private String from = "mrsxservice@163.com";
    private String to;
    private Properties props = null;

    public SendEmailUtils(String toEmail) {
        to = toEmail;
        props = new Properties();
        props.setProperty("mail.transport.protocol", PROTOCOL);
        props.setProperty("mail.smtp.host", HOST);
        props.setProperty("mail.smtp.port", PORT);
        props.setProperty("mail.smtp.auth", IS_AUTH);
        props.setProperty("mail.debug", IS_ENABLED_DEBUG_MOD);
    }

    public void sendCaptcha(String captcha) throws Exception {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Session session = Session.getInstance(props, new MyAuthenticator());

        MimeMessage message = new MimeMessage(session);
        //验证码邮件标题
        message.setSubject("验证码（CAPTACHA）");
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        //验证码邮件内容
        message.setContent("您本次的验证码为（Your CAPTCHA is）：" + "</br><span style='font-size: 60px;font-weight:bold'>" + captcha + "</span></br>", "text/html;charset=gbk");
        message.saveChanges();
        Transport.send(message);
    }

    static class MyAuthenticator extends Authenticator {

        //发送验证码邮件的邮箱地址符号@前面的部分
        private String username = "mrsxservice";

        //发送验证码邮件的邮箱的授权码
        private String password = "OZQQZHENQDAKOORH";

        public MyAuthenticator() {
            super();
        }

        public MyAuthenticator(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }
    }
}