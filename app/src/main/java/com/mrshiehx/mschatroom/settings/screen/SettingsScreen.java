package com.mrshiehx.mschatroom.settings.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mrshiehx.mschatroom.AccountInformation;
import com.mrshiehx.mschatroom.StartActivity;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.modify_user_information.screen.ModifyUserInformationScreen;
import com.mrshiehx.mschatroom.preference.HasFilesSizePreference;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//设置界面
public class SettingsScreen extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public ListPreference modify_theme, modify_language;
    public Preference accountPreference, logout;
    HasFilesSizePreference clearCache, clearData;
    Context context = SettingsScreen.this;
    ProgressDialog gettingUI;
    //boolean canLogin;
    String password;//, emailString, accountString;
    byte[] avatarInputStream;
    NetworkStateReceiver myReceiver;
    public static int anInt = 0;

    //int clianNotThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_settings_screen_name);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*loggingIn = new ProgressDialog(context);
        loggingIn.setTitle(getString(R.string.dialog_title_notice));
        loggingIn.setMessage(getString(R.string.dialog_loggingIn_message));
        loggingIn.setCancelable(false);
        */
        gettingUI = new ProgressDialog(context);
        gettingUI.setTitle(getString(R.string.dialog_title_notice));
        gettingUI.setMessage(getString(R.string.dialog_getting_user_information));
        gettingUI.setCancelable(false);
        modify_theme = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_THEME);
        modify_language = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE);
        //accountPreference=(AccountPreference) getPreferenceScreen().findPreference("account");
        accountPreference = getPreferenceScreen().findPreference("account");
        logout = getPreferenceScreen().findPreference("logout");
        clearCache = (HasFilesSizePreference) getPreferenceScreen().findPreference("clear_cache");
        clearData = (HasFilesSizePreference) getPreferenceScreen().findPreference("clear_application_data");


        myReceiver = new NetworkStateReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, itFilter);


        //iv=findViewById(R.id.account_icon);
        //tv=findViewById(R.id.account_name);
        getFilesSize();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        accountPreference.setSummary(getResources().getString(R.string.preference_account_notlogged_summary));
        accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD) == false) {
                    LoginScreen.can_i_back = true;
                    Utils.startActivity(SettingsScreen.this, LoginScreen.class);
                } else {
                    Utils.startActivity(SettingsScreen.this, ModifyUserInformationScreen.class);
                }

                return true;
            }
        });
        if (MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD) == false) {
            logout.setEnabled(false);
        }


        modify_language.setValue(sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, Utils.getSystemLanguage() + "_" + Utils.getSystemCountry()));
        modify_theme.setValue(sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_THEME, "dark"));
        //}
        dynamicModifyListSummaryTheme();
        dynamicModifyListSummaryLanguage();

        //init();


        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                final View dialogView = LayoutInflater.from(context)
                        .inflate(R.layout.checkbox_dialog, null);
                dialog.setView(dialogView);
                dialog.setTitle(getResources().getString(R.string.dialog_title_notice));
                TextView textView = dialogView.findViewById(R.id.checkbox_dialog_message);
                textView.setText(getResources().getString(R.string.dialog_logout_message));
                final CheckBox checkBox = dialogView.findViewById(R.id.checkbox_dialog_checkbox);
                checkBox.setText(getString(R.string.dialog_logout_checkbox_text));
                //checkBox.setChecked(false);
                //checkBox.setEnabled(false);
                dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
                dialog.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkBox.isChecked()) {
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chat_avatars"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chats"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "information"));
                            File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
                            chatsFile.delete();
                        }
                        File file;
                        //if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) != 1) {
                        //account
                        file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted())/*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY))*/;
                        /*} else {
                            //email
                            file = new File(Utils.getDataFilesPath(context), "avatar_" + getEmailEncrypted()*//*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY)*//*);
                        }*/
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                        if (Variables.COMMUNICATOR != null) {
                            try {
                                Variables.COMMUNICATOR.disConnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            editor.remove(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utils.startActivity(context, StartActivity.class);
                        finish();
                    }
                });
                dialog.show();
                return true;
            }
        });
        clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder clearCache = new AlertDialog.Builder(context);
                clearCache.setTitle(getString(R.string.dialog_title_notice));
                clearCache.setMessage(getString(R.string.dialog_clear_cache_message));
                clearCache.setNegativeButton(getString(android.R.string.cancel), null);
                clearCache.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Utils.deleteDirectory(getCacheDir());
                            Toast.makeText(context, getString(R.string.toast_successfully_deleted_cache), Toast.LENGTH_SHORT).show();
                            getFilesSize();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_deleted_cache));
                            Toast.makeText(context, getString(R.string.toast_failed_to_deleted_cache), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                clearCache.show();
                return true;
            }
        });
        clearData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder clearData = new AlertDialog.Builder(context);
                clearData.setTitle(getString(R.string.dialog_title_notice));
                clearData.setMessage(getString(R.string.dialog_clear_application_data_message));
                clearData.setNegativeButton(getString(android.R.string.cancel), null);
                clearData.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //getFilesDir().getParentFile().delete();

                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context) + File.separator + "chat_avatars");
                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context) + File.separator + "chats");
                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context) + File.separator + "information");
                            Utils.deleteDirectoryContent(getFilesDir().getAbsolutePath());
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chat_avatars"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chats"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "information"));
                            Utils.deleteDirectory(getFilesDir());

                            editor.clear().apply();
                            Toast.makeText(context, getString(R.string.toast_successfully_deleted_application_data), Toast.LENGTH_SHORT).show();
                            MSCRApplication.getInstance().exit();
                            //getFilesSize();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.exceptionDialog(context, e, getString(R.string.toast_failed_to_deleted_application_data));
                            Toast.makeText(context, getString(R.string.toast_failed_to_deleted_application_data), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                clearData.show();
                return true;
            }
        });
    }

    void init() {
        if (Utils.isNetworkConnected(context)) {
            //Toast.makeText(context, String.valueOf(Utils.getAccountUtils()==null), Toast.LENGTH_SHORT).show();
            if (Variables.ACCOUNT_UTILS == null) {
                final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Connection connection = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
                        if (connection == null) {
                            Toast.makeText(context, getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                            initAvatarFromLocal();
                        } else {
                            Variables.ACCOUNT_UTILS = new AccountUtils(connection, Variables.DATABASE_TABLE_NAME);
                        }
                        dialog.dismiss();
                        Looper.loop();
                    }
                }).start();
            }
        } else {
            setTitle(getString(R.string.activity_settings_screen_offline_mode_name));
        }
        initUserInformation();
    }

    void initUserInformation() {
        if (MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    initAvatar();
                    Looper.loop();
                }
            }).start();

        }
        new Thread(() -> {
            Looper.prepare();
            if (Utils.checkLoginStatus(context)) {

                if (Utils.checkLoginInformationAndNetwork(context)) {
                    initUserInformationOnline();
                } else {
                    initUserInformationOffline();
                }

                if (!TextUtils.isEmpty(getWhatsup())) {
                    runOnUiThread(() -> accountPreference.setSummary(getWhatsup()));
                } else {
                    runOnUiThread(() -> accountPreference.setSummary(getString(R.string.preference_account_summary_tip_set_whatsup)));
                }

                if (TextUtils.isEmpty(getNickname())) {
                    runOnUiThread(() -> {
                        try {
                            accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString(), Variables.TEXT_ENCRYPTION_KEY)));
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
                    });
                } else {
                    //Set Name
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setTitle(getNickname());

                        }
                    });
                }
            }
            Looper.loop();
        }).start();


    }

    void initUserInformationOnline() {
        if (Utils.isNetworkConnected(context)) {
            runOnUiThread(() -> {
                UserInformation information = UserInformationUtils.read(context, Utils.getAccountUtils().getBytesNoThread(context, "information", AccountUtils.BY_ACCOUNT, getAccountEncrypted().toString()));
                if (information == null) {
                    information = new UserInformation((String) getNickname(), (String) getGender(), (String) getWhatsup());
                }
                final String nickname = information.nameContent;
                String whatsup = information.whatsupContent;
                if (!TextUtils.isEmpty(whatsup)) {
                    accountPreference.setSummary(getWhatsup());
                }
                if (TextUtils.isEmpty(nickname)) {
                    if (!TextUtils.isEmpty(getAccountEncrypted())) {
                        try {
                            accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString(), Variables.TEXT_ENCRYPTION_KEY)));
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
                    }
                } else {
                    if (!TextUtils.isEmpty(nickname)) {
                        accountPreference.setTitle(nickname);
                    }
                }
            });


        }

    }

    void initUserInformationOffline() {
        if (!TextUtils.isEmpty(getWhatsup())) {
            runOnUiThread(() -> accountPreference.setSummary(getWhatsup()));
        }


        if (TextUtils.isEmpty(getNickname())) {
            if (!TextUtils.isEmpty(getAccountEncrypted())) {
                try {
                    accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString(), Variables.TEXT_ENCRYPTION_KEY)));
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
            }
        } else {
            if (!TextUtils.isEmpty(getNickname())) {
                runOnUiThread(() -> accountPreference.setTitle(getNickname()));
            }
        }

    }


    void getFilesSize() {
        try {
            clearCache.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)))));
            clearData.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)).getParentFile())));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_get_files_size));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Variables.SHARED_PREFERENCE_MODIFY_THEME)) {
            dynamicModifyListSummaryTheme();
        }
        if (s.equals(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE)) {
            dynamicModifyListSummaryLanguage();
        }
    }

    public void dynamicModifyListSummaryTheme() {
        if (!TextUtils.isEmpty(modify_theme.getValue())) {
            modify_theme.setSummary(modify_theme.getEntry());
        }
    }


    public void dynamicModifyListSummaryLanguage() {
        if (!TextUtils.isEmpty(modify_language.getValue())) {
            modify_language.setSummary(modify_language.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dynamicModifyListSummaryTheme();
        dynamicModifyListSummaryLanguage();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
        init();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }


    void initAvatar() {
        if (Utils.isNetworkConnected(context)) {
            try {
                avatarInputStream = Utils.getAccountUtils().getBytesNoThread(context, "avatar", AccountUtils.BY_ACCOUNT, getAccountEncrypted().toString());/*new FileInputStream(avatar)getAvatar();*/
            } catch (Exception e) {
                e.printStackTrace();
                //Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
                initAvatarFromLocal();
            }
            //}
        } else {
            initAvatarFromLocal();
        }
        if (avatarInputStream != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    accountPreference.setIcon(FormatTools.getInstance().Bytes2Drawable(avatarInputStream));
                }
            });
        }
    }

    void initAvatarFromLocal() {
        try {
            File file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted());
            if (file.exists())
                avatarInputStream = FileUtils.toByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, getResources().getString(R.string.loadinglog_failed_get_account_avatar));
            Toast.makeText(context, getResources().getString(R.string.loadinglog_failed_get_account_avatar), Toast.LENGTH_SHORT).show();
        }
    }

    AccountInformation getAccountInformation() {
        return Variables.ACCOUNT_INFORMATION;
    }

    CharSequence getAccountEncrypted() {
        if (getAccountInformation() != null)
            return getAccountInformation().getAccountE();
        return "";
    }

    CharSequence getEmailEncrypted() {
        if (getAccountInformation() != null)
            return getAccountInformation().getEmailE();
        return "";
    }

    boolean isNetworkConnected() {
        if (getAccountInformation() != null)
            return getAccountInformation().isNetworkConnected();
        return false;
    }

    boolean isCanLogin() {
        if (getAccountInformation() != null)
            return getAccountInformation().isCanLogin();
        return false;
    }

    boolean isLogined() {
        if (getAccountInformation() != null)
            return getAccountInformation().isLogined();
        return false;
    }

    CharSequence getNickname() {
        if (getAccountInformation() != null)
            return getAccountInformation().getNickname();
        return "";
    }

    CharSequence getWhatsup() {
        if (getAccountInformation() != null)
            return getAccountInformation().getWhatsup();
        return "";
    }

    CharSequence getGender() {
        if (getAccountInformation() != null)
            return getAccountInformation().getGender();
        return "";
    }

}
