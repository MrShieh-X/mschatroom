package com.mrshiehx.mschatroom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;


import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//工具类
public class Utils {

    public static File createFile(String path, String fileName) {
        return new File(path, fileName);
    }

    public static String getAndroidDirCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    public static String getDataFilesPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getDataCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static boolean fileIsExists(Context context, String filePathAndName) {
        try {
            File f = new File(filePathAndName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e);
            return false;
        }
        return true;
    }

    public static void deleteFile(Context context, String filePathAndName) {
        try {
            File f = new File(filePathAndName);
            if (f.exists()) {
                if (f.delete()) {
                    Log.i("MSCR.Utils.deleteFile", "Delete file (" + filePathAndName + ") is success!");
                } else {
                    showDialog(context, "", "delete failed");
                    Log.e("MSCR.Utils.deleteFile", "Delete file (" + filePathAndName + ") is failed!");
                }
            } else {
                Log.e("MSCR.Utils.deleteFile", "Delete file (" + filePathAndName + ") is not exists!");
            }
        } catch (Exception e) {
            Looper.prepare();
            e.printStackTrace();
            Utils.exceptionDialog(context, e);
            Looper.loop();
        }
    }

    public static void downloadFile(final Context context, View forsb, final String downloadFileUrl, final String afterDownloadFileName, final String downloadToPath) {
        if (isNetworkConnected(context) == true) {
            File file = new File(getDataFilesPath(context));
            if (!file.exists()) {
                file.mkdirs();
            }
            final ProgressDialog downloading = new ProgressDialog(context);
            downloading.setTitle(context.getResources().getString(R.string.dialog_title_wait));
            downloading.setMessage(context.getResources().getString(R.string.dialog_downloading_message));
            downloading.setCancelable(false);
            downloading.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(downloadFileUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            InputStream is = con.getInputStream();
                            FileOutputStream fileOutputStream = null;
                            if (is != null) {
                                //FileUtils fileUtils = new FileUtils();
                                fileOutputStream = new FileOutputStream(createFile(downloadToPath, afterDownloadFileName));
                                byte[] buf = new byte[1024];
                                int ch;
                                while ((ch = is.read(buf)) != -1) {
                                    fileOutputStream.write(buf, 0, ch);
                                }
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                        }
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (Utils.fileIsExists(context, downloadToPath + "/" + afterDownloadFileName) == true) {
                                    downloading.dismiss();
                                    timer.cancel();
                                } else {

                                }
                            }
                        }, 0, 100);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Looper.prepare();
                        exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_downloadfailed));
                        Looper.loop();
                    }
                }
            }).start();
        } else {
            Snackbar.make(forsb, context.getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
        }
    }

    public static String getJson(Context context, String filePathAndName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName), "utf-8"));
            BufferedReader bufferedReader=null;
            try {
                bufferedReader= new BufferedReader(new FileReader(filePathAndName));
            }catch (Exception e){
                exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_file_not_found));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException e) {
            exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_parsing_json_failed));
        }
        return stringBuilder.toString();
    }

    public static String getJsonByAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader bufferedReader=null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName), "utf-8"));
            }catch (Exception e){
                exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_file_not_found));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException e) {
            exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_parsing_json_failed));
        }
        return stringBuilder.toString();
    }

    public static void showDialog(final Context context, String title, String message) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);
        dialog.setTitle(title).setMessage(message);
        dialog.show();
    }

    public static void showDialog(final Context context, String title, String message, String buttonName, DialogInterface.OnClickListener buttonOnClickListener) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);
        dialog.setTitle(title).setMessage(message);
        dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
        dialog.setPositiveButton(buttonName, buttonOnClickListener);
        dialog.show();
    }


    public static void exceptionDialog(final Context context, final Exception exception) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);

        String dialogExceptionMessage = String.format(context.getResources().getString(R.string.dialog_exception_message), context, exception);
        dialog.setTitle(context.getResources().getString(R.string.dialog_exception_title)).setMessage(dialogExceptionMessage);
        dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.setPositiveButton(context.getResources().getString(R.string.dialog_exception_button_feedback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMail(context, Variables.AUTHOR_MAIL, "AN ERROR OF MSCR", "There is a problem, application package name is: (" + getPackageName(context) + "), application version name is: (" + getVersionName(context) + "), application version code is(" + getVersionCode(context) + "), android version is: (" + getSystemVersion() + "), device brand is: (" + getDeviceBrand() + "), device model is: (" + getDeviceModel() + "), class is: (" + context + "), error is: (" + exception + ")");
            }
        });
        dialog.show();
    }

    public static void exceptionDialog(final Context context, final Exception exception, final String detailException) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);

        String dialogExceptionMessage = String.format(context.getResources().getString(R.string.dialog_exception_detail_message), context, exception, detailException);
        dialog.setTitle(context.getResources().getString(R.string.dialog_exception_title)).setMessage(dialogExceptionMessage);
        dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.setPositiveButton(context.getResources().getString(R.string.dialog_exception_button_feedback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMail(context, Variables.AUTHOR_MAIL, "AN ERROR OF MSCR", "There is a problem, application package name is: (" + getPackageName(context) + "), application version name is: (" + getVersionName(context) + "), application version code is(" + getVersionCode(context) + "), android version is: (" + getSystemVersion() + "), device brand is: (" + getDeviceBrand() + "), device model is: (" + getDeviceModel() + "), class is: (" + context + "), error is: (" + exception + "), detail is:(" + detailException + ")");
            }
        });
        dialog.show();
    }


    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void sendMail(Context context, String reciver, String subject, String text) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + reciver));
        data.putExtra(Intent.EXTRA_SUBJECT, subject);
        data.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(data);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.toast_no_mail_app), Toast.LENGTH_SHORT).show();
        }
    }

    public static void goToWebsite(Context context, String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }

    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static String getVersionName(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            exceptionDialog(context, e);
        }

        return null;

    }

    public static int getVersionCode(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            exceptionDialog(context, e);
        }

        return 0;

    }

    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
            exceptionDialog(context, e);
        }
        return null;
    }


    public static void initialization(Activity context, @StringRes int titleId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun == false) {
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US"));
        } else {
            //Get default language, and set it
            editor.putString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry());
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry()));
        }
        makeIsFirstRunFalse(context);
        String[] languageAndCountry = sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US").split("_");
        context.setTitle(getStringByLocale(context, titleId, languageAndCountry[0], languageAndCountry[1]));
        initializationTheme(context);
        MyApplication.getInstance().addActivity(context);
    }


    public static void initializationForPictureViewer(Activity context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun == false) {
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US"));
        } else {
            //Get default language, and set it
            editor.putString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry());
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry()));
        }
        makeIsFirstRunFalse(context);
        //String[] languageAndCountry = sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, "en_US").split("_");
        //context.setTitle(getStringByLocale(context, titleId, languageAndCountry[0], languageAndCountry[1]));
        //initializationTheme(context);
        MyApplication.getInstance().addActivity(context);
    }

    public static boolean networkAvailableDialog(Context context) {
        if (isNetworkConnected(context) == false) {

            AlertDialog.Builder noNetworkDialog = new AlertDialog.Builder(context);
            noNetworkDialog.setTitle(context.getResources().getString(R.string.dialog_no_network_title))
                    .setMessage(context.getResources().getString(R.string.dialog_no_network_message));
            noNetworkDialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            noNetworkDialog.setPositiveButton(context.getResources().getString(R.string.dialog_no_network_button_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyApplication.getInstance().exit();
                }
            });
            noNetworkDialog.show();

        }
        return isNetworkConnected(context);
    }

    public static String getSystemLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static String getSystemCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }


    public static void initializationTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_THEME, "light").equals("dark")) {
            context.setTheme(R.style.AppThemeDark);
        } else {
            context.setTheme(R.style.AppTheme);
        }

    }

    public static void setLanguage(Activity context, String language) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh_CN")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("zh_TW")) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else {
            config.locale = Locale.ENGLISH;
        }
        resources.updateConfiguration(config, dm);
    }

    public static String getStringByLocale(Context context, int stringId, String language, String country) {
        Resources resources = getApplicationResource(context.getApplicationContext().getPackageManager(),
                getPackageName(context), new Locale(language, country));
        if (resources == null) {
            return "";
        } else {
            try {
                return resources.getString(stringId);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    private static Resources getApplicationResource(PackageManager pm, String pkgName, Locale l) {
        Resources resourceForApplication = null;
        try {
            resourceForApplication = pm.getResourcesForApplication(pkgName);
            updateResource(resourceForApplication, l);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resourceForApplication;
    }

    private static void updateResource(Resources resource, Locale l) {
        Configuration config = resource.getConfiguration();
        config.locale = l;
        resource.updateConfiguration(config, null);
    }

    public static boolean isUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public static void makeIsFirstRunFalse(Context context) {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, false);
        editor.commit();

    }

    public static boolean isEmail(String string) {
        return string.matches("^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9-]{2,24}$");
        //return string.matches("\\\\w+([-+.]\\\\w+)*@\\\\w+([-.]\\\\w+)*\\\\.\\\\w+([-.]\\\\w+)*");
        //return string.matches("^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$");
        //return string.matches("^(\\w+((-\\w+)|(\\.\\w+))*)\\+\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
        //return string.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    public static String newRandomNumber(int length) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            buffer.append(random.nextInt(10) + "");
        }
        return buffer.toString();
    }


    static String password;

    public static boolean checkLoginInformationAndNetwork(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false) == false) {
                Utils.showDialog(context,
                        context.getResources().getString(R.string.dialog_title_notice),
                        context.getResources().getString(R.string.dialog_no_login_message),
                        context.getResources().getString(R.string.dialog_no_login_button_gotologin_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginScreen.can_i_back = true;
                                Utils.startActivity(context, LoginScreen.class);
                            }
                        });
                return false;
            } else {
                final ProgressDialog loggingIn = new ProgressDialog(context);
                int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
                if (loginMethod == 0) {
                    String account = "";
                    try {
                        account = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                        password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                    String accountE = null;
                    String passwordE = null;
                    try {
                        accountE = EnDeCryptTextUtils.encrypt(account, Variables.TEXT_ENCRYPTION_KEY);
                        passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    Boolean result = ud.login(context, loggingIn, AccountUtils.BY_ACCOUNT, accountE, passwordE);
                    if (!result) {
                        Utils.showDialog(context,
                                context.getResources().getString(R.string.dialog_title_notice),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_message),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_button_gotologin_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginScreen.can_i_back = true;
                                        Utils.startActivity(context, LoginScreen.class);
                                    }
                                });
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    String email = "";
                    try {
                        email = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                        password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                    String emailE = null;
                    String passwordE = null;
                    try {
                        emailE = EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY);
                        passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    Boolean result = ud.login(context, loggingIn, AccountUtils.BY_EMAIL, emailE, passwordE);
                    if (!result) {
                        Utils.showDialog(context,
                                context.getResources().getString(R.string.dialog_title_notice),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_message),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_button_gotologin_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginScreen.can_i_back = true;
                                        Utils.startActivity(context, LoginScreen.class);
                                    }
                                });
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    /**
     * 0失败
     * 1成功
     * 2登录失败
     *
    static int RETURN_INT=-1;
    public static int checkLoginInformationAndNetworkForSettings(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!Utils.isNetworkConnected(context)) {
            RETURN_INT = 0;
            Toast.makeText(context, context.getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
        } else {
            if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false) == false) {
                RETURN_INT = 0;
                Utils.showDialog(context,
                        context.getResources().getString(R.string.dialog_title_notice),
                        context.getResources().getString(R.string.dialog_no_login_message),
                        context.getResources().getString(R.string.dialog_no_login_button_gotologin_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginScreen.can_i_back = true;
                                Utils.startActivity(context, LoginScreen.class);
                            }
                        });
            } else {
                final ProgressDialog loggingIn = new ProgressDialog(context);
                int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
                if (loginMethod == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String account = "";
                            try {
                                account = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                                password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                            String accountE = null;
                            String passwordE = null;
                            try {
                                accountE = EnDeCryptTextUtils.encrypt(account, Variables.TEXT_ENCRYPTION_KEY);
                                passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            Boolean result = ud.login(context, loggingIn, AccountUtils.BY_ACCOUNT, accountE, passwordE);
                            if (!result) {
                                RETURN_INT=2;
                            } else {
                                RETURN_INT = 1;
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String email = "";
                            try {
                                email = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                                password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                            String emailE = null;
                            String passwordE = null;
                            try {
                                emailE = EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY);
                                passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            Boolean result = ud.login(context, loggingIn, AccountUtils.BY_EMAIL, emailE, passwordE);
                            if (!result) {
                                RETURN_INT=2;
                            } else {
                                RETURN_INT = 1;
                            }
                            return;
                        }
                    }).start();
                }
            }
        }
        return RETURN_INT;
    }
     */

    public static void dynamicModifyETSummary(EditTextPreference editTextPreference, String whenEmptySummary) {
        if (!TextUtils.isEmpty(editTextPreference.getText())) {
            editTextPreference.setSummary(editTextPreference.getText());
        } else {
            editTextPreference.setSummary(whenEmptySummary);
        }
    }

    public static void dynamicModifyListSummary(ListPreference listPreference, String whenEmptySummary) {
        if (!TextUtils.isEmpty(listPreference.getValue())) {
            listPreference.setSummary(listPreference.getEntry());
        } else {
            listPreference.setSummary(whenEmptySummary);
        }
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static InputStream replaceUserInformationContents(InputStream file, String accountName, String accountGender, String accountWhatSUp) throws IOException {
        String content = getString(file);
        //InputStream in = con.getInputStream();
        //String content = IOUtils.toString(file, "UTF-8");
        Map<String, String> m = new HashMap<>();
        m.put("${ACCOUNT_NAME}", accountName);
        m.put("${ACCOUNT_GENDER}", accountGender);
        m.put("${ACCOUNT_WHATSUP}", accountWhatSUp);
        String str = parse(content, m);
        InputStream lastFile = new ByteArrayInputStream(str.getBytes("UTF-8"));
        return lastFile;
    }

    private static String parse(String content, Map<String, String> kvs) {
        Pattern p = Pattern.compile("(\\$\\{)([\\w]+)(\\})");
        Matcher m = p.matcher(content);
        StringBuffer sr = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            m.appendReplacement(sr, kvs.get(group));
        }
        m.appendTail(sr);
        return sr.toString();
    }

    public static void copy(Context context,String content){
        ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(content);
    }

    public static void showLongSnackbar(View v,String s){
        Snackbar.make(v,s,Snackbar.LENGTH_LONG).show();
    }

    public static void showShortSnackbar(View v,String s){
        Snackbar.make(v,s,Snackbar.LENGTH_SHORT).show();
    }

    public static void showIndefiniteSnackbar(View v,String s){
        Snackbar.make(v,s,Snackbar.LENGTH_INDEFINITE).show();
    }
    public static byte[] inputStream2ByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
    public static void inputStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }
}
