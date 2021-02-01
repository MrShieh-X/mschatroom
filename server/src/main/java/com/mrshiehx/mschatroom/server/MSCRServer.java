package com.mrshiehx.mschatroom.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MSCRServer {
    static IoSession mSession;
    static int port;
    static List<IoSession> list;;
    static List<String> eoasEncrypted;
    static Connection connection;
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        System.out.print(R.string.message_input_port);
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            port = 6553;
        }

        list=new ArrayList<>();
        eoasEncrypted=new ArrayList<>();
        editConnectionsNumber(0,0);

        System.out.println(R.string.message_connecting_to_database);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection=getConnection(Variables.SERVER_ADDRESS,Variables.DATABASE_NAME,Variables.DATABASE_USER_NAME,Variables.DATABASE_USER_PASSWORD);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                if(connection!=null){
                    System.out.println(R.string.message_success_connect_to_database);
                }else{
                    System.out.println(R.string.message_failed_connect_to_database);
                }
            }
        }).start();

        NioSocketAcceptor acceptor;
        try {
            acceptor = new NioSocketAcceptor();
            acceptor.setHandler(new IoHandlerAdapter() {
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    System.out.println(String.format(R.string.message_have_a_error, cause));
                    System.out.println(String.format(R.string.message_have_a_error, cause));
                    cause.printStackTrace();
                }

                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    System.out.println("R1: "+message.toString());
                    System.out.println("R2: "+ URLDecoder.decode((String) message, "utf-8"));

                    if (((String) message).startsWith("eoaE:")) {
                        if(!eoasEncrypted.contains(((String)message).substring(5))&&list.contains(session)) {
                            eoasEncrypted.add(((String) message).substring(5));
                        }
                    } else{
                        String currentUserEOAEncrypted=eoasEncrypted.get(list.indexOf(session));
                        JSONObject jsonObject = JSONObject.parseObject(URLDecoder.decode((String) message));
                        String toUserEncrypted = jsonObject.getString("t");
                        String content = jsonObject.getString("c");
                        int indexOf=eoasEncrypted.indexOf(toUserEncrypted);
                        if(indexOf!=-1){
                            //online
                            list.get(indexOf).write(URLEncoder.encode("{\"f\":"+currentUserEOAEncrypted+",\"c\":"+content+"}","UTF-8"));
                        }else{
                            //offline
                            //String eoaEnc=eoasEncrypted.get(indexOf);
                            String eoaCle=EnDeCryptTextUtils.decrypt(toUserEncrypted);
                            String by="account";
                            if(isEmail(eoaCle)){
                                by="email";
                            }
                            String messages=getString(Variables.DATABASE_TABLE_NAME,"messages",by,toUserEncrypted);
                            if(messages.length()!=0){
                                //NOT EMPTY, USE OLD
                                JSONObject jsonObject2 = JSON.parseObject(messages);
                                JSONArray array=jsonObject2.getJSONArray(currentUserEOAEncrypted);
                                if(array!=null){
                                    //ARRAY IS EXISTS, USE OLD
                                    array.add(content);
                                    //array.toString();
                                    jsonObject2.replace(currentUserEOAEncrypted,array);
                                }else{
                                    //ARRAY IS NOT EXISTS, CREATE A
                                    JSONArray array1=new JSONArray();
                                    array1.add(content);
                                    jsonObject2.put(currentUserEOAEncrypted,array1);
                                }
                                setString(Variables.DATABASE_TABLE_NAME,"messages",jsonObject2.toString(),by,toUserEncrypted);
                            }else{
                                //EMPTY, NEW A
                                setString(Variables.DATABASE_TABLE_NAME,"messages","{\""+currentUserEOAEncrypted+"\":[\""+content+"\"]}",by,toUserEncrypted);
                            }
                        }
                    }
                    editConnectionsNumber(list.size(),eoasEncrypted.size());
                }

                @Override
                public void messageSent(IoSession session, Object message) throws Exception {
                    System.out.println(R.string.message_success_send);
                    System.out.println("S: "+message.toString());
                }

                @Override
                public void sessionClosed(IoSession session) throws Exception {
                    System.out.println(R.string.message_connect_closed);
                    int indexOf=list.indexOf(session);
                    System.out.println("sessionClosed: "+session.toString()+eoasEncrypted.get(indexOf));
                    if(indexOf!=-1){
                        list.remove(indexOf);
                        eoasEncrypted.remove(indexOf);
                    }

                    editConnectionsNumber(list.size(),eoasEncrypted.size());
                }

                @Override
                public void sessionOpened(IoSession session) throws Exception {
                    System.out.println(R.string.message_connected_message);
                    mSession = session;
                    if(!list.contains(session)) {
                        list.add(session);
                    }
                    System.out.println("sessionOpened: "+session.toString());
                    editConnectionsNumber(list.size(),eoasEncrypted.size());
                }

                @Override
                public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
                    System.out.println("sessionIdle");
                }
            });

            TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"));
            lineCodec.setDecoderMaxLineLength(1024 * 10240); //10M
            lineCodec.setEncoderMaxLineLength(1024 * 10240); //10M
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineCodec));

            acceptor.setReuseAddress(true);
            acceptor.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e + "");
        }
    }

    static void editConnectionsNumber(int n, int n2){
        System.out.println(String.format(R.string.connections_number, n,n2));
    }

    static Connection getConnection(String serverAddress, String databaseName, String userName, String userPassword) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        return DriverManager.getConnection("jdbc:mysql://"+serverAddress+"/" + databaseName, userName, userPassword);
    }

    static boolean setString(String dbTableName, String needToSet, String needToSetContent, String by, String byContent) throws SQLException {
        if (connection == null) {
            System.out.println("setString:connection is null");
            return false;
        } else {
            Statement stmt = connection.createStatement();
            String sql = "update " + dbTableName + " set " + needToSet + "='" + needToSetContent + "' where " + by + "='" + byContent + "'";
            return stmt.executeUpdate(sql)!=0;
        }
    }


    static String getString(String dbTableName, String needToGet, String by, String byContent) throws SQLException {
        if (connection == null) {
            System.out.println("getString:connection is null");
        } else {
            ResultSet set = null;
            PreparedStatement prepar = connection.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
            set = prepar.executeQuery();
            while (set.next()) {
                return set.getString(needToGet);
            }
        }
        return null;
    }

    public static boolean isEmail(String string) {
        return string.matches("^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9-]{2,24}$");
    }
}
