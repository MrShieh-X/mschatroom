package com.mrshiehx.mschatroom.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//MySQL工具类
public class AccountUtils {
    Connection conn;
    public static final String BY_ACCOUNT = "account";
    public static final String BY_EMAIL = "email";
    String dbTableName;
    String RETURN;

    public AccountUtils(Connection connection, String dbTableName) {
        this.dbTableName = dbTableName;
        conn = connection;
    }

    public AccountUtils(String databaseName, String databaseUser, String databasePassword, String databaseTableName) {
        this.dbTableName = databaseTableName;
        conn = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, databaseName, databaseUser, databasePassword);
    }

    public AccountUtils() {
        this.dbTableName = Variables.DATABASE_TABLE_NAME;
        conn = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
    }

    public Connection getConnection() {
        return conn;
    }

    public void reconnect() {
        conn = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
    }

    public String getEmailByAccount(Context context, String account) {
        return getEmailByAccount(true, context, account);
    }

    public String getEmailByAccount(boolean check, Context context, String account) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return getEmailByAccount(false,context, account);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return getEmailByAccount(false,context, account);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return getEmailByAccount(false,context, account);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where account='" + account + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    RETURN = set.getString("email");
                }
                return RETURN;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    public String getAccountByEmail(Context context, String email) {
        return getAccountByEmail(true, context, email);
    }

    public String getAccountByEmail(boolean check, Context context, String email) {
        if(check) {
        if (conn == null) {
            reconnect();
            boolean closed = true;
            if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            } else {
                return getAccountByEmail(false,context, email);
            }
        } else {
            boolean closed = true;
            try {
                closed = conn.isClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!closed) {
                return getAccountByEmail(false,context, email);
            } else {
                reconnect();
                boolean closed2 = true;
                try {
                    closed2 = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (conn == null || closed2) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return getAccountByEmail(false,context, email);
                }
            }
        }
    }


        if (conn != null) {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where email='" + email + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    RETURN = set.getString("account");
                }
                return RETURN;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    public String getString(Context context, String needToGet, String by, String byContent) {
        return getString(true, context, needToGet, by, byContent);
    }

    public String getString(boolean check, Context context, String needToGet, String by, String byContent) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return getString(false,context, needToGet,by,byContent);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return getString(false,context, needToGet,by,byContent);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return getString(false,context, needToGet,by,byContent);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    RETURN = set.getString(needToGet);
                }
                return RETURN;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return "";
    }


    public byte[] getBytes(Context context, String needToGet, String by, String byContent) {
        return getBytes(true, context, needToGet, by, byContent);
    }

    public byte[] getBytes(boolean check, Context context, String needToGet, String by, String byContent) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return getBytes(false,context,needToGet,by,byContent);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return getBytes(false,context,needToGet,by,byContent);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return getBytes(false,context,needToGet,by,byContent);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
                set = prepar.executeQuery();
                byte[] bytes = new byte[8192];
                while (set.next()) {
                    bytes = set.getBytes(needToGet);
                }
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return null;
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return new byte[0];
    }

    public byte[] getBytesWithException(Context context, String needToGet, String by, String byContent) throws Exception {
        return getBytesWithException(true, context, needToGet, by, byContent);
    }

    public byte[] getBytesWithException(boolean check, Context context, String needToGet, String by, String byContent) throws Exception {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return getBytesWithException(false,context, needToGet,by,byContent);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return getBytesWithException(false,context, needToGet,by,byContent);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return getBytesWithException(false,context, needToGet,by,byContent);
                    }
                }
            }
        }


        if (conn != null) {
            ResultSet set = null;
            PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
            set = prepar.executeQuery();
            byte[] bytes = new byte[8192];
            while (set.next()) {
                bytes = set.getBytes(needToGet);
            }
            return bytes;
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return new byte[0];
    }

    public int register(Context context, String email, String account, String password) throws IOException {
        return register(true, context, email, account, password);
    }

    public int register(boolean check, Context context, String email, String account, String password) throws IOException {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return register(false,context, email,account,password);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return register(false,context, email,account,password);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return register(false,context, email,account,password);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "insert into " + dbTableName + " values(?,?,?,?,?,?);";
            try {
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, email);
                pre.setString(2, account);
                pre.setString(3, password);
                pre.setBytes(4, Utils.createNewUserInformation("", "", ""));
                pre.setBinaryStream(5, null);
                pre.setBinaryStream(6, null);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public boolean login(Context context, String loginMethod, String accountOrEmail, String password) {
        return login(true, context, loginMethod, accountOrEmail, password);
    }

    public boolean login(boolean check, Context context, String loginMethod, String accountOrEmail, String password) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return login(false,context, loginMethod,accountOrEmail,password);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return login(false,context, loginMethod,accountOrEmail,password);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return login(false,context, loginMethod,accountOrEmail,password);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=? and password=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                pres.setString(2, password);
                ResultSet res = pres.executeQuery();
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean tryLoginWithoutPassword(Context context, String loginMethod, String accountOrEmail) {
        return tryLoginWithoutPassword(true, context, loginMethod, accountOrEmail);
    }

    public boolean tryLoginWithoutPassword(boolean check, Context context, String loginMethod, String accountOrEmail) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return tryLoginWithoutPassword(false,context, loginMethod,accountOrEmail);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return tryLoginWithoutPassword(false,context, loginMethod,accountOrEmail);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return tryLoginWithoutPassword(false,context, loginMethod,accountOrEmail);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                ResultSet res = pres.executeQuery();
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean tryLoginWithoutPasswordInt(Context context, String loginMethod, String accountOrEmail) {
        return tryLoginWithoutPasswordInt(true, context, loginMethod, accountOrEmail);
    }

    public boolean tryLoginWithoutPasswordInt(boolean check, Context context, String loginMethod, String accountOrEmail) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return tryLoginWithoutPasswordInt(false,context, loginMethod,accountOrEmail);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return tryLoginWithoutPasswordInt(false,context, loginMethod,accountOrEmail);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return tryLoginWithoutPasswordInt(false,context, loginMethod,accountOrEmail);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                ResultSet res = pres.executeQuery();
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public int resetPassword(Context context, String by, String byC, String password, String newPassword) {
        return resetPassword(true, context, by, byC, password, newPassword);
    }

    public int resetPassword(boolean check, Context context, String by, String byC, String password, String newPassword) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return resetPassword(false,context, by,byC,password,newPassword);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return resetPassword(false,context, by,byC,password,newPassword);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return resetPassword(false,context, by,byC,password,newPassword);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set password='" + newPassword + "' where " + by + "='" + byC + "' and password='" + password + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public int uploadUserInformation(Context context, String email, String account, String password, byte[] newUserInformation) {
        return uploadUserInformation(true, context, email, account, password, newUserInformation);
    }

    public int uploadUserInformation(boolean check, Context context, String email, String account, String password, byte[] newUserInformation) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return uploadUserInformation(false,context, email,account,password,newUserInformation);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return uploadUserInformation(false,context, email,account,password,newUserInformation);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return uploadUserInformation(false,context, email,account,password,newUserInformation);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                String sql = "update " + dbTableName + " set information=? where email='" + email + "' and account='" + account + "' and password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBytes(1, newUserInformation);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public int uploadAvatar(Context context, String email, String account, String password, byte[] newAvatar) {
        return uploadAvatar(true, context, email, account, password, newAvatar);
    }

    public int uploadAvatar(boolean check, Context context, String email, String account, String password, byte[] newAvatar) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return uploadAvatar(false,context, email,account,password,newAvatar);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return uploadAvatar(false,context, email,account,password,newAvatar);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return uploadAvatar(false,context, email,account,password,newAvatar);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                String sql = "update " + dbTableName + " set avatar=? where email='" + email + "' and account='" + account + "' and password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBytes(1, newAvatar);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public int deleteAccount(Context context, String by, String byContent, String password) {
        return deleteAccount(true, context, by, byContent, password);
    }

    public int deleteAccount(boolean check, Context context, String by, String byContent, String password) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return deleteAccount(false,context, by,byContent,password);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return deleteAccount(false,context, by,byContent,password);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return deleteAccount(false,context, by,byContent,password);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "delete from " + dbTableName + " where " + by + "='" + byContent + "' and password='" + password + "';";
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public boolean find(Context context, String by, String byContent) {
        return find(true, context, by, byContent);
    }

    public boolean find(boolean check, Context context, String by, String byContent) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return find(false,context, by,byContent);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return find(false,context, by,byContent);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return find(false,context, by,byContent);
                    }
                }
            }
        }


        if (conn != null) {
            String sql = "select * from " + dbTableName + " where " + by + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, byContent);
                ResultSet res = pres.executeQuery();
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public int updateMessages(Context context, String by, String byContent, String newMessages) {
        return updateMessages(true, context, by, byContent, newMessages);
    }

    public int updateMessages(boolean check, Context context, String by, String byContent, String newMessages) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return updateMessages(false,context, by,byContent,newMessages);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return updateMessages(false,context, by,byContent,newMessages);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return updateMessages(false,context, by,byContent,newMessages);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                String sql = "update " + dbTableName + " set messages=? where " + by + "='" + byContent + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, newMessages);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public int setString(Context context, String needToSet, String needToSetContent, String by, String byContent) {
        return setString(true, context, needToSet, needToSetContent, by, byContent);
    }

    public int setString(boolean check, Context context, String needToSet, String needToSetContent, String by, String byContent) {
        if(check) {
            if (conn == null) {
                reconnect();
                boolean closed = true;
                if(conn!=null){
try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
}
                if (conn == null || closed) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                } else {
                    return setString(false,context, needToSet,needToSetContent,by,byContent);
                }
            } else {
                boolean closed = true;
                try {
                    closed = conn.isClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!closed) {
                    return setString(false,context, needToSet,needToSetContent,by,byContent);
                } else {
                    reconnect();
                    boolean closed2 = true;
                    try {
                        closed2 = conn.isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (conn == null || closed2) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        return setString(false,context, needToSet,needToSetContent,by,byContent);
                    }
                }
            }
        }


        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set " + needToSet + "='" + needToSetContent + "' where " + by + "='" + byContent + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }else{
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
}

