package com.mrshiehx.mschatroom.utils;

import com.mrshiehx.mschatroom.Variables;

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
    private final String to;
    private final Properties props;

    public SendEmailUtils(String toEmail) {
        to = toEmail;
        props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", Variables.CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS);
        props.setProperty("mail.smtp.port", "25");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.debug", "true");
    }

    public void sendCaptcha(String captcha) throws Exception {

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        Session session = Session.getInstance(props, new MyAuthenticator());

        MimeMessage message = new MimeMessage(session);
        //验证码邮件标题
        message.setSubject("验证码（CAPTACHA）");
        message.setFrom(new InternetAddress(Variables.CAPTCHA_EMAIL_ADDRESS));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        //验证码邮件内容
        message.setContent("您本次的验证码为（Your CAPTCHA is）：" + "</br><span style='font-size: 60px;font-weight:bold'>" + captcha + "</span></br>", "text/html;charset=gbk");
        message.saveChanges();
        Transport.send(message);
    }

    static class MyAuthenticator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(Variables.CAPTCHA_EMAIL_ADDRESS.split("@")[0], Variables.AUTHENTICATOR);
        }
    }
}