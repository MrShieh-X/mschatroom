package com.mrshiehx.mschatroom.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
    static Connection conn2;
    String dbName;
    String dbUser;
    String dbPassword;
    String dbTableName;
    String RETURN;
    InputStream inputStream = null;
    int RETURN_INT;
    InputStream RETURN_INPUTSTREAM;

    public AccountUtils(String dbName, String dbUser, String dbPassword, String dbTableName) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTableName = dbTableName;
        conn = jdbcUtil.getConnection(dbName, dbUser, dbPassword);
    }

    public String getEmailByAccount(Context context, String account) {
        if (conn == null) {
            Log.i(Variables.TAG, "getEmailByAccount:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return null;
            }
        }
    }


    public String getAccountByEmail(Context context, String email) {
        if (conn == null) {
            Log.i(Variables.TAG, "getAccountByEmail:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return null;
            }
        }
    }

    public String getString(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getString:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return null;
            }
        }
    }

    public int getInt(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getInt:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0x0;
        } else {
            try {
                ResultSet set = null;
                PreparedStatement prepar = conn.prepareStatement("select * from " + dbTableName + " where " + by + "='" + byContent + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    RETURN_INT = set.getInt(needToGet);
                }
                return RETURN_INT;
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return 0x0;
            }
        }
    }

    public InputStream getInputStream(Context context, String needToGet, String by, String byContent) {
        if (conn == null) {
            Log.i(Variables.TAG, "getInputStream:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return null;
            }
        }
    }

    public int register(Context context, ProgressDialog ingDialog, String email, String account, String password) throws IOException {
        if (conn == null) {
            Log.i(Variables.TAG, "register:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        } else {
            String sql = "insert into " + dbTableName + " values(?,?,?,?,?);";
            try {
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, email);
                pre.setString(2, account);
                pre.setString(3, password);
                InputStream in = context.getResources().getAssets().open("userInformation.xml");
                pre.setBinaryStream(4, Utils.replaceUserInformationContents(in, "", "", ""));
                pre.setBinaryStream(5, null);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                Looper.loop();
                return 0;
            } finally {
                ingDialog.dismiss();
            }
        }
    }

    public boolean login(Context context, ProgressDialog ingDialog, String loginMethod, String accountOrEmail, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "login:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return false;
            } finally {
                ingDialog.dismiss();
            }

        }
    }

    public boolean tryLoginWithoutPassword(Context context, String loginMethod, String accountOrEmail) {
        if (conn == null) {
            Log.i(Variables.TAG, "login:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
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
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return false;
            }

        }
    }

    public int resetPassword(Context context, ProgressDialog ingDialog, String email, String newPassword) {
        if (conn == null) {
            Log.i(Variables.TAG, "reset password:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();

            return 0;

        } else {
            try {
                Statement stmt = conn.createStatement();
                String sql = "update " + dbTableName + " set password='" + newPassword + "' where email='" + email + "'";
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                Looper.loop();
                return 0;
            } finally {
                ingDialog.dismiss();
            }

        }
    }

    public InputStream getUserInformation(Context context, String password) {
        PreparedStatement prepar = null;
        if (conn == null) {
            Log.i(Variables.TAG, "getUserInformation:conn is null");
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return null;
        } else {
            try {
                ResultSet set;
                prepar = conn.prepareStatement("select * from " + dbTableName + " where password='" + password + "'");
                set = prepar.executeQuery();
                while (set.next()) {
                    inputStream = set.getBinaryStream("information");
                }
                return inputStream;
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_download_data));
                Looper.loop();
                return null;
            }

        }
    }

    public int uploadUserInformation(Context context, ProgressDialog ingDialog, String password, InputStream newUserInformation) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadUserInformation:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set information=? where password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newUserInformation);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                Looper.loop();
                return 0;
            } finally {
                ingDialog.dismiss();
            }

        }
    }

    public int uploadAvatar(Context context, ProgressDialog ingDialog, String password, InputStream newAvatar) {
        if (conn == null) {
            Log.i(Variables.TAG, "uploadAvatar:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        } else {
            try {
                String sql = "update " + dbTableName + " set avatar=? where password='" + password + "'";
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setBinaryStream(1, newAvatar);
                return pre.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                Looper.loop();
                return 0;
            } finally {
                ingDialog.dismiss();
            }

        }
    }

    public int delectAccount(Context context, ProgressDialog ingDialog, String by, String byContent, String password) {
        if (conn == null) {
            Log.i(Variables.TAG, "delectAccount:conn is null");
            Looper.prepare();
            ingDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        } else {
            String sql = "delete from " + dbTableName + " where " + by + "='" + byContent + "' and password='" + password + "';";
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Looper.prepare();
                ingDialog.dismiss();
                Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_failed_upload_data));
                Looper.loop();
                return 0;
            } finally {
                ingDialog.dismiss();
            }
        }
    }
}

