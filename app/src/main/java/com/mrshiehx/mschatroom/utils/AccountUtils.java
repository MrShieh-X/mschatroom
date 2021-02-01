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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//MySQL工具类
public class AccountUtils {

    ConnectionUtils jdbcUtil = new ConnectionUtils(Variables.SERVER_ADDRESS);
    Connection conn;
    public static final String BY_ACCOUNT = "account";
    public static final String BY_EMAIL = "email";
    String dbName;
    String dbUser;
    String dbPassword;
    String dbTableName;
    String RETURN;
    InputStream inputStream = null;
    InputStream RETURN_INPUTSTREAM;

    public AccountUtils(Connection connection, String dbTableName){
        this.dbTableName = dbTableName;
        conn=connection;
    }

    public AccountUtils(String dbName, String dbUser, String dbPassword, String dbTableName) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTableName = dbTableName;
        conn = jdbcUtil.getConnection(dbName, dbUser, dbPassword);
    }

    public String getEmailByAccountNoThread(Context context, String account) {
        if (conn == null) {
            Log.i(Variables.TAG, "getEmailByAccount:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
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
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }
        }
    }


    public String getAccountByEmailNoThread(Context context, String email) {
        if (conn == null) {
            Log.i(Variables.TAG, "getAccountByEmail:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
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
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }
        }
    }


    public String getStringNoThread(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getString:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
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
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }
        }
    }

    public InputStream getInputStreamNoThread(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getInputStream:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    RETURN_INPUTSTREAM = set.getBinaryStream(needToGet);
                }
                return RETURN_INPUTSTREAM;
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }
        }
    }

    public InputStream getAvatar(Context context, String by, String byContent, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "getInputStream:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "' and password='"+password+"'");
                ResultSet set = prepar.executeQuery();
                while (set.next()) {
                    RETURN_INPUTSTREAM = set.getBinaryStream("avatar");
                }
                return RETURN_INPUTSTREAM;
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }
        }
    }

    public int register(Context context, ProgressDialog ingDialog, String email, String account, String password) throws IOException {
        int s=registerNoThreadAndDialog(context,email,account,password);
        ingDialog.dismiss();
        return s;
    }

    public int registerNoThreadAndDialog(Context context, String email, String account, String password) throws IOException {
        if (conn == null) {
            Log.i(Variables.TAG, "register:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            String sql = "insert into " + dbTableName + " values(?,?,?,?,?,?);";
            try {
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, email);
                pre.setString(2, account);
                pre.setString(3, password);
                InputStream in = context.getResources().getAssets().open("userInformation.xml");
                pre.setBinaryStream(4, Utils.replaceUserInformationContents(in, "", "", ""));
                pre.setBinaryStream(5, null);
                pre.setBinaryStream(6, null);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }
        }
    }

    public boolean login(Context context, ProgressDialog ingDialog, String loginMethod, String accountOrEmail, String password) {
        boolean s=loginNoThreadAndDialog(context,loginMethod,accountOrEmail,password);
        ingDialog.dismiss();


        return s;
    }

    public boolean loginNoThreadAndDialog(Context context, String loginMethod, String accountOrEmail, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "login:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return false;

        } else {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=? and password=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                pres.setString(2, password);
                ResultSet res = pres.executeQuery();
                boolean t = res.next();
                return t;
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return false;
            }

        }
    }

    public boolean tryLoginWithoutPasswordNoThreadAndDialog(Context context, String loginMethod, String accountOrEmail) {
        if (conn == null) {
            Log.i(Variables.TAG, "tryLoginWithoutPassword:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return false;

        } else {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                ResultSet res = pres.executeQuery();
                boolean t = res.next();
                return t;
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return false;
            }

        }
    }

    public int tryLoginWithoutPasswordNoThreadAndDialogInt(Context context, String loginMethod, String accountOrEmail) {
        if (conn == null) {
            Log.i(Variables.TAG, "tryLoginWithoutPassword:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;

        } else {
            String sql = "select * from " + dbTableName + " where " + loginMethod + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, accountOrEmail);
                ResultSet res = pres.executeQuery();
                boolean t = res.next();
                return t?1:0;
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return 2;
            }

        }
    }

    public int resetPassword(Context context,ProgressDialog ingDialog,String email,String newPassword){
        int s=resetPasswordNoThreadAndDialog(context,email,newPassword);
        ingDialog.dismiss();
        return s;
    }

    public int resetPasswordNoThreadAndDialog(Context context, String email, String newPassword) {
        if (conn == null) {
            Log.i(Variables.TAG, "reset password:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;

        } else {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set password='" + newPassword + "' where email='" + email + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }

        }
    }

    public InputStream getUserInformationNoThread(Context context, String email,String account, String password) {
        PreparedStatement prepar = null;
        if (conn == null) {
            Log.i(Variables.TAG, "getUserInformation:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                ResultSet set;
                prepar = conn.prepareStatement("select * from " + dbTableName + " where email='"+email+"' and account='"+account+"' and password='" + password + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    inputStream = set.getBinaryStream("information");
                }
                return inputStream;
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }

        }
    }

    public InputStream getUserInformationWithoutPasswordNoThread(Context context, String by,String byContent) {
        PreparedStatement prepar = null;
        if (conn == null) {
            Log.i(Variables.TAG, "getUserInformationWithoutPassword:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                ResultSet set;
                prepar = conn.prepareStatement("select * from " + dbTableName + " where "+by+"='"+byContent+"'");
                set = prepar.executeQuery();
                while (set.next()) {
                    inputStream = set.getBinaryStream("information");
                }
                return inputStream;
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return null;
            }

        }
    }

    public int uploadUserInformation(Context context, ProgressDialog ingDialog, String email, String account,String password, InputStream newUserInformation){
        int s=uploadUserInformationNoThreadAndDialog(context,email,account,password,newUserInformation);
        ingDialog.dismiss();
        return s;
    }

    public int uploadUserInformationNoThreadAndDialog(Context context, String email, String account,String password, InputStream newUserInformation) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadUserInformation:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set information=? where email='" +email+ "' and account='"+account+"' and password='"+password+"'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newUserInformation);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }

        }
    }

    public int uploadAvatarNoThreadAndDialog(Context context , String email,String account,String password, InputStream newAvatar) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadAvatar:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set avatar=? where email='"+email+"' and account='"+account+"' and password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newAvatar);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }

        }
    }

    public int delectAccount(Context context, ProgressDialog ingDialog,String by, String byContent, String password){
        int s=delectAccountNoThreadAndDialog(context,by,byContent,password);
        ingDialog.dismiss();
        return s;
    }

    public int delectAccountNoThreadAndDialog(Context context, String by, String byContent, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "delectAccount:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            String sql = "delete from " + dbTableName + " where " + by + "='" + byContent + "' and password='" + password + "';";
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }
        }
    }

    public boolean find(Context context,ProgressDialog ingDialog,String by,String byContent){
        boolean s=findNoThreadAndDialog(context,by,byContent);
        ingDialog.dismiss();
        return s;
    }

    public boolean findNoThreadAndDialog(Context context, String by, String byContent){
        if (conn == null) {
            Log.i(Variables.TAG, "find:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String sql = "select * from " + dbTableName + " where " + by + "=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, byContent);
                ResultSet res = pres.executeQuery();
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                return false;
            }
        }
    }

    public Connection getConnection(){
        return conn;
    }

    public int updateMessagesNoThreadAndDialog(Context context, String by, String byContent, String newMessages) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadAvatar:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set messages=? where "+by+"='"+byContent+"'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, newMessages);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }

        }
    }


    public int setString(Context context, String needToSet, String needToSetContent, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "setString:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set "+needToSet+"='" + needToSetContent + "' where "+by+"='" + byContent + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                return 0;
            }

        }
    }
}

