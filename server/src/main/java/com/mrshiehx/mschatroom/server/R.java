package com.mrshiehx.mschatroom.server;

import java.util.Locale;

public class R {
    public static class string{
        public static String app_title;
        public static String send_text;
        public static String message_connected_message;
        public static String message_connect_closed;
        public static String message_have_a_error;
        public static String message_success_send;
        public static String message_presend_content;
        public static String message_input_port;
        public static String connections_number;
        public static String message_connecting_to_database;
        public static String message_success_connect_to_database;
        public static String message_failed_connect_to_database;
        static {
            if(Locale.getDefault().getLanguage().equals("zh")){
                app_title="MSCR服务器";
                send_text="发送";
                message_connected_message="连接成功";
                message_connect_closed="连接关闭";
                message_have_a_error="出现错误：%s";
                message_success_send="发送成功";
                message_presend_content="待发送内容";
                message_input_port="请输入一个端口号（整数，默认6553）：";
                connections_number="连接数：%1$s/%2$s";
                message_connecting_to_database="正在连接MySQL数据库...";
                message_success_connect_to_database="连接MySQL数据库成功";
                message_failed_connect_to_database="连接MySQL数据库失败";
            }else{
                app_title="com.mrshiehx.mschatroom.server.MSCRServer";
                message_connected_message="Connected";
                message_connect_closed="Connection is closed";
                message_have_a_error="An error occurred: %s";
                message_success_send="Successfully to send";
                message_presend_content="Content to be sent";
                message_input_port="Please input a port number (integer, default is 6553): ";
                connections_number="Number of connections: %1$s/%2$s";
                message_connecting_to_database="Connecting to the MySQL database...";
                message_success_connect_to_database="Successfully to connect to the MySQL database";
                message_failed_connect_to_database="Failed to connect to the MySQL database";
            }
        }
    }
}
