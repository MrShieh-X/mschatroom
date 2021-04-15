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
    Connection conn;
    public static final String BY_ACCOUNT = "account";
    public static final String BY_EMAIL = "email";
    String dbTableName;
    String RETURN;
    InputStream inputStream = null;
    InputStream RETURN_INPUTSTREAM;

    public AccountUtils(Connection connection, String dbTableName) {
        this.dbTableName = dbTableName;
        conn = connection;
    }

    public AccountUtils(String databaseName, String databaseUser, String databasePassword, String databaseTableName) {
        this.dbTableName = databaseTableName;
        conn = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, databaseName, databaseUser, databasePassword);
    }

    public String getEmailByAccount(Context context, String account) {
        if (conn == null) {
            Log.i(Variables.TAG, "getEmailByAccount:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return "";
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
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }
    }


    public String getAccountByEmail(Context context, String email) {
        if (conn == null) {
            Log.i(Variables.TAG, "getAccountByEmail:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return "";
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
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }
    }


    public String getString(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getString:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return "";
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
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return "";
            }
        }
    }

    public InputStream getInputStream(Context context, String needToGet, String by, String byContent) {
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
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    public byte[] getBytes(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getBytes:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
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
        }
    }

    public InputStream getAvatar(Context context, String by, String byContent, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "getInputStream:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "' and password='" + password + "'");
                ResultSet set = prepar.executeQuery();
                while (set.next()) {
                    RETURN_INPUTSTREAM = set.getBinaryStream("avatar");
                }
                return RETURN_INPUTSTREAM;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    public int register(Context context, String email, String account, String password) throws IOException {
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
                pre.setBinaryStream(4, Utils.createNewUserInformation("", "", ""));
                pre.setBinaryStream(5, null);
                pre.setBinaryStream(6, null);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
    }

    public boolean login(Context context, String loginMethod, String accountOrEmail, String password) {
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
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }

        }
    }

    public boolean tryLoginWithoutPassword(Context context, String loginMethod, String accountOrEmail) {
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
                return res.next();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }

        }
    }

    public int tryLoginWithoutPasswordInt(Context context, String loginMethod, String accountOrEmail) {
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
                return t ? 1 : 0;
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return 2;
            }

        }
    }

    public int resetPassword(Context context, String by, String byC, String password, String newPassword) {
        if (conn == null) {
            Log.i(Variables.TAG, "reset password:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;

        } else {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set password='" + newPassword + "' where "+by+"='" + byC + "' and password='"+password+"'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }
    }

    public InputStream getUserInformation(Context context, String email, String account, String password) {
        PreparedStatement prepar;
        if (conn == null) {
            Log.i(Variables.TAG, "getUserInformation:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                ResultSet set;
                prepar = conn.prepareStatement("select * from " + dbTableName + " where email='" + email + "' and account='" + account + "' and password='" + password + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    inputStream = set.getBinaryStream("information");
                }
                return inputStream;
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return null;
            }

        }
    }

    public InputStream getUserInformationWithoutPassword(Context context, String by, String byContent) {
        PreparedStatement prepar;
        if (conn == null) {
            Log.i(Variables.TAG, "getUserInformationWithoutPassword:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            try {
                ResultSet set;
                prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
                set = prepar.executeQuery();
                InputStream inputStream = null;
                while (set.next()) {
                    inputStream = set.getBinaryStream("information");
                }
                return inputStream;
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return null;
            }

        }
    }

    public int uploadUserInformation(Context context, String email, String account, String password, InputStream newUserInformation) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadUserInformation:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set information=? where email='" + email + "' and account='" + account + "' and password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newUserInformation);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }
    }

    public int uploadAvatar(Context context, String email, String account, String password, InputStream newAvatar) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadAvatar:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set avatar=? where email='" + email + "' and account='" + account + "' and password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newAvatar);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }
    }

    public int deleteAccount(Context context, String by, String byContent, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "deleteAccount:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            String sql = "delete from " + dbTableName + " where " + by + "='" + byContent + "' and password='" + password + "';";
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
    }

    public boolean find(Context context, ProgressDialog ingDialog, String by, String byContent) {
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
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_download_data), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public int updateMessages(Context context, String by, String byContent, String newMessages) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadAvatar:conn is null");
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            return 0;
        } else {
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
                String sql = "update " + dbTableName + " set " + needToSet + "='" + needToSetContent + "' where " + by + "='" + byContent + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.dialog_exception_failed_upload_data), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }
    }
}

