package com.mrshiehx.mschatroom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.widget.ScrollView;
import android.widget.Toast;


import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.account.profile.screen.AccountProfileScreen;
import com.mrshiehx.mschatroom.beans.AccountInformation;
import com.mrshiehx.mschatroom.chat.Communicator;
import com.mrshiehx.mschatroom.chat.message.MessageItem;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.start.screen.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//工具类
public class Utils {
    private boolean closed;

    public static String getAndroidDirCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getAbsolutePath();
        } else {
            return context.getCacheDir().getAbsolutePath();
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

    public static AlertDialog showDialog(final Context context, CharSequence title, CharSequence message) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);
        dialog.setTitle(title).setMessage(message);
        return dialog.show();
    }

    public static AlertDialog showDialog(final Context context, CharSequence title, CharSequence message, CharSequence buttonName, DialogInterface.OnClickListener buttonOnClickListener) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);
        dialog.setTitle(title).setMessage(message);
        dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
        dialog.setPositiveButton(buttonName, buttonOnClickListener);
        return dialog.show();
    }


    public static AlertDialog exceptionDialog(final Context context, final Exception exception) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);

        String dialogExceptionMessage = String.format(context.getResources().getString(R.string.dialog_exception_message), context.getClass().getName(), exception);
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
                sendMail(context, Variables.AUTHOR_MAIL, "AN ERROR OF MSCR", "There is a problem, application package name is: (" + getPackageName(context) + "), application version name is: (" + getVersionName(context) + "), application version code is(" + getVersionCode(context) + "), android version is: (" + getSystemVersion() + "), device brand is: (" + getDeviceBrand() + "), device model is: (" + getDeviceModel() + "), class is: (" + context.getClass().getName() + "), error is: (" + exception + ")");
            }
        });
        return dialog.show();
    }

    public static AlertDialog exceptionDialog(final Context context, final Exception exception, final String detailException) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context);

        String dialogExceptionMessage = String.format(context.getResources().getString(R.string.dialog_exception_detail_message), context.getClass().getName(), exception, detailException);
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
                sendMail(context, Variables.AUTHOR_MAIL, "AN ERROR OF MSCR", "There is a problem, application package name is: (" + getPackageName(context) + "), application version name is: (" + getVersionName(context) + "), application version code is(" + getVersionCode(context) + "), android version is: (" + getSystemVersion() + "), device brand is: (" + getDeviceBrand() + "), device model is: (" + getDeviceModel() + "), class is: (" + context.getClass().getName() + "), error is: (" + exception + "), detail is:(" + detailException + ")");
            }
        });
        return dialog.show();
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

    public static void sendMail(Context context, String receiver, String subject, String text) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + receiver + "?subject=" + subject + "&body=" + text));
        //data.putExtra(Intent.EXTRA_EMAIL,receiver);
        //data.putExtra("subject", subject);
        //data.putExtra("text", text);
        //data.putExtra("body", text);
        try {
            context.startActivity(data);
        } catch (Exception e) {
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


    public static void initializationNoTheme(Activity context, @StringRes int titleId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun) {
            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "dark").apply();
                    editor.putString(Variables.SHARED_PREFERENCE_THEME, "dark");
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "light").apply();
                    editor.putString(Variables.SHARED_PREFERENCE_THEME, "light");
                    break;
            }
            //Get default language, and set it
            editor.putString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry());
            //setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry()));
        }
        editor.apply();
        setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE));
        makeIsFirstRunFalse(context);
        String[] languageAndCountry = sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
        context.setTitle(getStringByLocale(context, titleId, languageAndCountry[0], languageAndCountry[1]));
        MSChatRoom.getInstance().addActivity(context);
    }


    public static void initialization(Activity context, @StringRes int titleId) {
        initializationNoTheme(context, titleId);
        initializationTheme(context, R.style.AppThemeDark, R.style.AppTheme);
    }

    public static void initialization(Activity context, @StringRes int titleId, @StyleRes int darkTheme, @StyleRes int lightTheme) {
        initializationNoTheme(context, titleId);
        initializationTheme(context, darkTheme, lightTheme);
    }


    /*public static void initializationForPictureViewer(Activity context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun == false) {
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE));
        } else {
            //Get default language, and set it
            editor.putString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry());
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry()));
        }
        makeIsFirstRunFalse(context);
        //String[] languageAndCountry = sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
        //context.setTitle(getStringByLocale(context, titleId, languageAndCountry[0], languageAndCountry[1]));
        //initializationTheme(context);
        MyApplication.getInstance().addActivity(context);
    }



    public static void initializationForLoadingScreen(Activity context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun == false) {
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE));
        } else {
            //Get default language, and set it
            editor.putString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry());
            setLanguage(context, sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, getSystemLanguage() + "_" + getSystemCountry()));
        }
        makeIsFirstRunFalse(context);
        //String[] languageAndCountry = sharedPreferences.getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
        //context.setTitle(getStringByLocale(context, titleId, languageAndCountry[0], languageAndCountry[1]));
        initializationTheme(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        MyApplication.getInstance().addActivity(context);
    }*/


    public static boolean networkAvailableDialog(Context context) {
        if (!isNetworkConnected(context)) {

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
                    MSChatRoom.getInstance().exit();
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


    /*public static void initializationTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun) {
            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "dark").apply();
                    context.setTheme(R.style.AppThemeDark);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "light").apply();
                    context.setTheme(R.style.AppTheme);
                    break;
            }

        }
        if (sharedPreferences.getString(Variables.SHARED_PREFERENCE_THEME, "dark").equals("light")) {
            context.setTheme(R.style.AppTheme);
        } else {
            context.setTheme(R.style.AppThemeDark);
        }


    }*/


    public static void initializationTheme(Context context, @StyleRes int dark, @StyleRes int light) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //editor.putBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN,true);
        if (isFirstRun) {
            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    //PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "dark").apply();
                    context.setTheme(dark);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    //PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Variables.SHARED_PREFERENCE_THEME, "light").apply();
                    context.setTheme(light);
                    break;
            }
        }
        if (sharedPreferences.getString(Variables.SHARED_PREFERENCE_THEME, "dark").equals("light")) {
            context.setTheme(light);
        } else {
            context.setTheme(dark);
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
        editor.apply();

    }

    public static boolean isEmail(String string) {
        if (string == null) return false;
        if (string.length() == 0) return false;
        return string.matches("^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9-]{2,24}$");
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
            //Toast.makeText(context, context.getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!AccountInformationStorager.isLogined()) {
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
                String account = "";
                try {
                    String s = AccountInformationStorager.getMainAccountAndPassword();
                    account = EnDeCryptTextUtils.decrypt(s, Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                    password = EnDeCryptTextUtils.decrypt(s, Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
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
                AccountUtils ud = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Utils.getAccountUtils();
                String accountE = "";
                String passwordE = "";
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
                boolean result = ud.login(context, AccountUtils.BY_ACCOUNT, accountE, passwordE);
                loggingIn.dismiss();
                if (!result) {
                    if (Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null) {
                        Utils.showDialog(context,
                                context.getResources().getString(R.string.dialog_title_notice),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_message),
                                context.getResources().getString(R.string.dialog_failed_login_insettings_button_gotologin_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginScreen.can_i_back = true;
                                        Intent intent = new Intent(context, LoginScreen.class);
                                        intent.putExtra("account", password);
                                        context.startActivity(intent);
                                    }
                                });
                    }
                    return false;
                } else {
                    return true;
                }
                 /*else {
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
                    AccountUtils ud = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*Variables.ACCOUNT_UTILS;
                    String emailE = "";
                    String passwordE = "";
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
                                        Intent intent=new Intent(context, LoginScreen.class);
                                        intent.putExtra("account",password);
                                        context.startActivity(intent);
                                    }
                                });
                        return false;
                    } else {
                        return true;
                    }
                }
            }*/
            }
        }
    }

    /**
     * 0失败
     * 1成功
     * 2登录失败
     * <p>
     * static int RETURN_INT=-1;
     * public static int checkLoginInformationAndNetworkForSettings(final Context context) {
     * final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
     * if (!Utils.isNetworkConnected(context)) {
     * RETURN_INT = 0;
     * Toast.makeText(context, context.getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
     * } else {
     * if (com.mrshiehx.mschatroom.account.information.storage.modes.AccountInformationDatabaseUtils.isLogined() == false) {
     * RETURN_INT = 0;
     * Utils.showDialog(context,
     * context.getResources().getString(R.string.dialog_title_notice),
     * context.getResources().getString(R.string.dialog_no_login_message),
     * context.getResources().getString(R.string.dialog_no_login_button_gotologin_text),
     * new DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface dialog, int which) {
     * LoginScreen.can_i_back = true;
     * Utils.startActivity(context, LoginScreen.class);
     * }
     * });
     * } else {
     * final ProgressDialog loggingIn = new ProgressDialog(context);
     * int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1);
     * if (loginMethod == 0) {
     * new Thread(new Runnable() {
     * @Override public void run() {
     * String account = "";
     * try {
     * account = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
     * password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
     * } catch (InvalidKeyException e) {
     * e.printStackTrace();
     * } catch (InvalidKeySpecException e) {
     * e.printStackTrace();
     * } catch (NoSuchPaddingException e) {
     * e.printStackTrace();
     * } catch (IllegalBlockSizeException e) {
     * e.printStackTrace();
     * } catch (BadPaddingException e) {
     * e.printStackTrace();
     * }
     * AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
     * String accountE = null;
     * String passwordE = null;
     * try {
     * accountE = EnDeCryptTextUtils.encrypt(account, Variables.TEXT_ENCRYPTION_KEY);
     * passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
     * } catch (InvalidKeySpecException e) {
     * e.printStackTrace();
     * } catch (InvalidKeyException e) {
     * e.printStackTrace();
     * } catch (NoSuchPaddingException e) {
     * e.printStackTrace();
     * } catch (IllegalBlockSizeException e) {
     * e.printStackTrace();
     * } catch (BadPaddingException e) {
     * e.printStackTrace();
     * }
     * Boolean result = ud.login(context, loggingIn, AccountUtils.BY_ACCOUNT, accountE, passwordE);
     * if (!result) {
     * RETURN_INT=2;
     * } else {
     * RETURN_INT = 1;
     * }
     * }
     * }).start();
     * } else {
     * new Thread(new Runnable() {
     * @Override public void run() {
     * String email = "";
     * try {
     * email = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
     * password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
     * } catch (InvalidKeyException e) {
     * e.printStackTrace();
     * } catch (InvalidKeySpecException e) {
     * e.printStackTrace();
     * } catch (NoSuchPaddingException e) {
     * e.printStackTrace();
     * } catch (IllegalBlockSizeException e) {
     * e.printStackTrace();
     * } catch (BadPaddingException e) {
     * e.printStackTrace();
     * }
     * AccountUtils ud = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
     * String emailE = null;
     * String passwordE = null;
     * try {
     * emailE = EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY);
     * passwordE = EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY);
     * } catch (InvalidKeySpecException e) {
     * e.printStackTrace();
     * } catch (InvalidKeyException e) {
     * e.printStackTrace();
     * } catch (NoSuchPaddingException e) {
     * e.printStackTrace();
     * } catch (IllegalBlockSizeException e) {
     * e.printStackTrace();
     * } catch (BadPaddingException e) {
     * e.printStackTrace();
     * }
     * Boolean result = ud.login(context, loggingIn, AccountUtils.BY_EMAIL, emailE, passwordE);
     * if (!result) {
     * RETURN_INT=2;
     * } else {
     * RETURN_INT = 1;
     * }
     * return;
     * }
     * }).start();
     * }
     * }
     * }
     * return RETURN_INT;
     * }
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


    public static byte[] createNewUserInformation(String accountName, String accountGender, String accountWhatSUp) throws IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", accountName);
            jsonObject.put("gender", accountGender);
            jsonObject.put("whatIsUp", accountWhatSUp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString().getBytes("UTF-8");
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

    public static void copy(Context context, CharSequence content) {
        ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(content);
    }

    public static void showLongSnackbar(View v, String s) {
        Snackbar.make(v, s, Snackbar.LENGTH_LONG).show();
    }

    public static void showShortSnackbar(View v, String s) {
        Snackbar.make(v, s, Snackbar.LENGTH_SHORT).show();
    }

    public static void showIndefiniteSnackbar(View v, String s) {
        Snackbar.make(v, s, Snackbar.LENGTH_INDEFINITE).show();
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

    /**
     * 删除文件夹和其里面的内容
     */
    public static void deleteDirectory(File folder) {
        if (folder.exists()) {
            deleteDirectoryContent(folder.getAbsolutePath());
            folder.delete();
        }
    }

    /**
     * 只删除文件夹里面的内容，不删除文件夹
     */
    /*public static void deleteDirectoryContent(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
    }*/
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹目录下的文件，不删除文件夹
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static void deleteDirectoryContent(String filePath) {
        File dirFile = new File(filePath);
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    //删除子文件
                    deleteFile(file.getAbsolutePath());
                } else {
                    //删除子目录
                    deleteDirectory(file);
                }
            }
        }
        //if (!flag) return false;
        //删除当前空目录
        //return dirFile.delete();
    }

    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isFile()) {
                    item.delete();
                } else {
                    deleteFilesByDirectory(item);
                }
            }
        }
    }

    public static void scrollViewDown(final ScrollView scrollView) {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    public static JSONArray jsonArrayRemove(JSONArray jsonArray, int index) {
        JSONArray mJsonArray = new JSONArray();
        if (index < 0)
            return mJsonArray;

        if (index > jsonArray.length())
            return mJsonArray;

        for (int i = 0; i < index; i++) {
            try {
                mJsonArray.put(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = index + 1; i < jsonArray.length(); i++) {
            try {
                mJsonArray.put(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mJsonArray;
    }


    public static Uri stringToUri(Context context, String path) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果是7.0android系统
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, path);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        return uri;
    }

    public static String getLastTime(List<MessageItem> list) {
        List<Integer> integers = new ArrayList<>();
        for (MessageItem item : list) {
            integers.add(item.getType());
        }
        int indexOf = integers.lastIndexOf(MessageItem.TYPE_TIME);
        if (indexOf != -1)
            return list.get(indexOf).getContent();
        else
            return "";
    }

    /**
     * 格式化时间
     *
     * @return 格式化后的时间
     * @throws Exception 防止出错
     */
    public static String formatTime(long timeYMDHM) throws Exception {
        Time tim = new Time();
        tim.set(timeYMDHM);
        String timYear = tim.year + "";
        String timMonth = tim.month + 1 + "";
        String timDay = tim.monthDay + "";
        if (timMonth.length() == 1) timMonth = "0" + timMonth;
        if (timDay.length() == 1) timDay = "0" + timDay;

        String tim0 = timYear + "-" + timMonth + "-" + timDay;


        String timHour = tim.hour + "";
        String timMinute = tim.minute + "";
        if (timHour.length() == 1) timHour = "0" + timHour;
        if (timMinute.length() == 1) timMinute = "0" + timMinute;
        String tim1 = timHour + ":" + timMinute;


        int year = tim.year;
        int month = tim.month + 1;
        int day = tim.monthDay;
        Time Time = new Time();
        Time.setToNow();
        int cYear = Time.year;
        int cMonth = Time.month + 1;
        int cDay = Time.monthDay;
        String timeText;
        if (year == cYear) {
            if (month == cMonth && day == cDay) {
                timeText = tim1;
            } else {
                timeText = timMonth + "-" + timDay + " " + tim1;
            }
        } else {
            timeText = tim0 + " " + tim1;
        }
        return timeText;
    }

    public static String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) MSChatRoom.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

    public static AccountUtils getAccountUtils() {
        boolean b = true;
        if (Variables.ACCOUNT_UTILS != null) {
            try {
                b = Variables.ACCOUNT_UTILS.getConnection().isClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Variables.ACCOUNT_UTILS == null || Variables.ACCOUNT_UTILS.getConnection() == null || b) {
            Variables.ACCOUNT_UTILS = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
        }
        return Variables.ACCOUNT_UTILS;
    }

    /**
     * @return did not has problem
     */
    public static boolean checkLoginStatus(Context context) {
        if (AccountInformationStorager.isLogined()) {
            String s = AccountInformationStorager.getMainAccountAndPassword();
            if (!TextUtils.isEmpty(s)) {
                try {
                    String sss=EnDeCryptTextUtils.decrypt(s);

                    if(sss.contains(Variables.SPLIT_SYMBOL)) {
                        String[] ss = sss.split(Variables.SPLIT_SYMBOL);
                        if (ss.length < 2) {
                            String a="";
                            if(ss.length>=1){
                                a=ss[0];
                            }
                            showLoginStatusHasProblemDialog(context, a);
                            return false;
                        }
                    }else{
                        showLoginStatusHasProblemDialog(context);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showLoginStatusHasProblemDialog(context);
                    return false;
                }
            } else {
                showLoginStatusHasProblemDialog(context);
                return false;
            }
        } else {
            showLoginStatusHasProblemDialog(context);
            return false;
        }

        return true;
    }

    public static AlertDialog showLoginStatusHasProblemDialog(Context context) {
        return showLoginStatusHasProblemDialog(context, "");
    }

    public static AlertDialog showLoginStatusHasProblemDialog(Context context, String eoa) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_title_notice);
        dialog.setMessage(R.string.dialog_login_status_has_problem_message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.dialog_no_login_button_gotologin_text, (dialog1, which) -> {
            Intent intent = new Intent(context, LoginScreen.class);
            if (!TextUtils.isEmpty(eoa)) {
                intent.putExtra("account", eoa);
            }
            context.startActivity(intent);
        });
        dialog.setNegativeButton(R.string.menu_main_exit, (dialog1, which) -> {

        });
        return dialog.show();
    }

    public static void reload(Context context,boolean shouldIReconnectToCommunicator) {
        reload(context, true, true,shouldIReconnectToCommunicator);
    }

    public static void reload(Context context, boolean shouldShowDialog, boolean toast,boolean shouldIReconnectToCommunicator) {
        ProgressDialog dialog = null;
        if (shouldShowDialog)
            dialog = ConnectionUtils.showConnectingDialog(context);
        ProgressDialog finalDialog = dialog;
        new Thread(() -> {
            Looper.prepare();
            reloadInThread(context, shouldShowDialog, finalDialog,toast,shouldIReconnectToCommunicator);
            Looper.loop();
        }).start();
    }

    public static void reloadInThread(Context context, boolean shouldShowDialog, AlertDialog finalDialog,boolean toast,boolean shouldIReconnectToCommunicator) {
        Variables.ACCOUNT_UTILS = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);

        if (Variables.ACCOUNT_UTILS.getConnection() != null) {
            boolean closed = true;
            try {
                closed = Variables.ACCOUNT_UTILS.getConnection().isClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(context, String.valueOf(closed), Toast.LENGTH_SHORT).show();

                if (!closed) {
                    if(toast)Toast.makeText(context, R.string.toast_successfully_connect_database_server, Toast.LENGTH_SHORT).show();
                    if(MainScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 100;
                        MainScreen.handler.sendMessage(msg);
                    }
                    if(SettingsScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 100;
                        SettingsScreen.handler.sendMessage(msg);
                    }
                    if(AccountProfileScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 100;
                        AccountProfileScreen.handler.sendMessage(msg);
                    }
                } else {
                    if(toast)Toast.makeText(context, R.string.toast_connect_failed, Toast.LENGTH_SHORT).show();
                    if(MainScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 101;
                        MainScreen.handler.sendMessage(msg);
                    }
                    if(SettingsScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 101;
                        SettingsScreen.handler.sendMessage(msg);
                    }
                    if(AccountProfileScreen.handler!=null) {
                        Message msg = new Message();
                        msg.what = 101;
                        AccountProfileScreen.handler.sendMessage(msg);
                    }
                }
        } else {
            if(toast)Toast.makeText(context, R.string.toast_connect_failed, Toast.LENGTH_SHORT).show();
            if(MainScreen.handler!=null) {
                Message msg = new Message();
                msg.what = 101;
                MainScreen.handler.sendMessage(msg);
            }
            if(SettingsScreen.handler!=null) {
                Message msg = new Message();
                msg.what = 101;
                SettingsScreen.handler.sendMessage(msg);
            }
            if(AccountProfileScreen.handler!=null) {
                Message msg = new Message();
                msg.what = 101;
                AccountProfileScreen.handler.sendMessage(msg);
            }
        }
        if(shouldIReconnectToCommunicator) {
            boolean connected=false;
            if(Variables.SESSION!=null){
                connected=Variables.SESSION.isConnected();
            }
            if (Variables.COMMUNICATOR == null && AccountInformationStorager.isLogined() && !connected) {
                String accountEn = valueOf(getAccountInformation().getAccountE());
                String emailEn = valueOf(getAccountInformation().getEmailE());
                Variables.COMMUNICATOR = new Communicator(context, accountEn, emailEn);
                try {
                    if (Variables.COMMUNICATOR.connect()) {
                        if (toast)
                            Toast.makeText(context, R.string.loadinglog_success_connect_communication_server, Toast.LENGTH_SHORT).show();
                    } else {
                        if (toast)
                            Toast.makeText(context, R.string.loadinglog_failed_connect_communication_server, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, String.format(MSChatRoom.getContext().getString(R.string.loadinglog_failed_connect_communication_server_withcause), e + ""), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (shouldShowDialog && finalDialog != null) {
            finalDialog.dismiss();
        }
    }

    public static String valueOf(CharSequence c) {
        if (c == null) return "";
        return c.toString();
    }

    public static String valueOf(Object c) {
        if (c == null) return "";
        return String.valueOf(c);
    }


    public static String getServerAddress(String domain) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            return addresses[0].getHostAddress();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        return "";
    }

    public static boolean isBytesAllZero(byte[] bytes) {
        int i = 0;
        for (byte bytea : bytes) {
            if (bytea == (byte) 0) i++;
        }
        return i == bytes.length;
    }

    public static File createLocalPictureFile(byte[]bytes) {
        return new File(DataFiles.PICTURES_DIR, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "."+/*".png"*/ImageFormatUtils.getExtension(bytes));
    }

    public static File createLocalPictureFileAndCreate(byte[]bytes) throws IOException {
        File file = createLocalPictureFile(bytes);
        createFile(file);
        return file;
    }

    public static void createFile(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else {
            if (file.exists()) file.delete();
        }
        file.createNewFile();
    }

    public static AccountInformation getAccountInformation() {
        if (Variables.ACCOUNT_INFORMATION == null) {
            StartActivity.makeOfflineAccountInformation();
        }
        return Variables.ACCOUNT_INFORMATION;
    }

    public static void openFileByOtherApplication(Context context, File file) {
        openFileByOtherApplication(context, file, MIMETypeUtils.getMIMEType(file.getAbsolutePath()));
    }

    public static void openFileByOtherApplication(Context context, File file, String type){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uriForFile;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(context,
                    "com.mrshiehx.mschatroom.FileProvider",
                    file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uriForFile = Uri.fromFile(file);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uriForFile, type);
        context.startActivity(intent);
    }

    public static boolean isNumber(String value){
        for(char a:value.toCharArray()){
            if(a!='0'&&a!='1'&&a!='2'&&a!='3'&&a!='4'&&a!='5'&&a!='6'&&a!='7'&&a!='8'&&a!='9'){
                return false;
            }
        }
        return true;
    }

    public static String replaceLongToString(String longg){
        char[]b=new char[longg.length()];
        char[]a=longg.toCharArray();
        for(int i=0;i<a.length;i++){
            char c=a[i];
            switch (c){
                case '0':
                    b[i]='a';
                    break;
                case '1':
                    b[i]='b';
                    break;
                case '2':
                    b[i]='c';
                    break;
                case '3':
                    b[i]='d';
                    break;
                case '4':
                    b[i]='e';
                    break;
                case '5':
                    b[i]='f';
                    break;
                case '6':
                    b[i]='g';
                    break;
                case '7':
                    b[i]='h';
                    break;
                case '8':
                    b[i]='i';
                    break;
                case '9':
                    b[i]='j';
                    break;
            }
        }
        return new String(b);
    }

    public static String replaceStringToLong(String str){
        char[]b=new char[str.length()];
        char[]a=str.toCharArray();
        for(int i=0;i<a.length;i++){
            char c=a[i];
            switch (c){
                case 'a':
                    b[i]='0';
                    break;
                case 'b':
                    b[i]='1';
                    break;
                case 'c':
                    b[i]='2';
                    break;
                case 'd':
                    b[i]='3';
                    break;
                case 'e':
                    b[i]='4';
                    break;
                case 'f':
                    b[i]='5';
                    break;
                case 'g':
                    b[i]='6';
                    break;
                case 'h':
                    b[i]='7';
                    break;
                case 'i':
                    b[i]='8';
                    break;
                case 'j':
                    b[i]='9';
                    break;
            }
        }
        return new String(b);
    }
}
