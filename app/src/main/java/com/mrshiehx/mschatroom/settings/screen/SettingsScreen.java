package com.mrshiehx.mschatroom.settings.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.CheckBoxPreference;
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

import com.mrshiehx.mschatroom.beans.AccountInformation;
import com.mrshiehx.mschatroom.start.screen.StartActivity;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.account.profile.screen.AccountProfileScreen;
import com.mrshiehx.mschatroom.preference.HasFilesSizePreference;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.ImageFormatConverter;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;
import com.mrshiehx.mschatroom.beans.UserInformation;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//设置界面
public class SettingsScreen extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public ListPreference modify_theme, modify_language;
    public Preference accountPreference, logout;
    CheckBoxPreference show;
    HasFilesSizePreference clearCache, clearData;
    Context context = SettingsScreen.this;
    ProgressDialog gettingUI;
    byte[] avatarBytes;
    NetworkStateReceiver myReceiver;
    public static Handler handler;
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
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==100){
                    makeTitleToOnline();
                }else if(msg.what==101){
                    makeTitleToOffline();
                }
            }
        };

        gettingUI = new ProgressDialog(context);
        gettingUI.setTitle(getString(R.string.dialog_title_notice));
        gettingUI.setMessage(getString(R.string.dialog_getting_user_information));
        gettingUI.setCancelable(false);
        modify_theme = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_THEME);
        modify_language = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_LANGUAGE);
        //accountPreference=(AccountPreference) getPreferenceScreen().findPreference("account");
        accountPreference = getPreferenceScreen().findPreference("account");
        logout = getPreferenceScreen().findPreference("logout");
        clearCache = (HasFilesSizePreference) getPreferenceScreen().findPreference("clear_cache");
        clearData = (HasFilesSizePreference) getPreferenceScreen().findPreference("clear_application_data");
        show=(CheckBoxPreference)getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING);

        /*if(Utils.getVersionCode(context)==14){
            show.setEnabled(false);
        }*/


        myReceiver = new NetworkStateReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, itFilter);


        //iv=findViewById(R.id.account_icon);
        //tv=findViewById(R.id.account_name);
        getFilesSize();

        accountPreference.setSummary(getResources().getString(R.string.preference_account_notlogged_summary));
        accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (AccountInformationStorager.isLogined() == false) {
                    LoginScreen.can_i_back = true;
                    Utils.startActivity(SettingsScreen.this, LoginScreen.class);
                } else {
                    Utils.startActivity(SettingsScreen.this, AccountProfileScreen.class);
                }

                return true;
            }
        });
        if (AccountInformationStorager.isLogined() == false) {
            logout.setEnabled(false);
        }


        modify_language.setValue(MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LANGUAGE, Utils.getSystemLanguage() + "_" + Utils.getSystemCountry()));
        modify_theme.setValue(MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_THEME, "dark"));
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
                            List<File> list = DataFiles.getUserFilesInFilesDir();
                            for (File file : list) {
                                if (file.isDirectory()) {
                                    Utils.deleteDirectory(file);
                                } else {
                                    file.delete();
                                }
                            }
                        }
                        File file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted())/*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY))*/;
                        /*} else {
                            //email
                            file = new File(Utils.getDataFilesPath(context), "avatar_" + getEmailEncrypted()*//*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY)*//*);
                        }*/
                        if (file.exists()) {
                            file.delete();
                        }
                        if (Variables.COMMUNICATOR != null) {
                            try {
                                Variables.COMMUNICATOR.disConnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        AccountInformationStorager.deleteAccount();
                        finish();
                        Utils.startActivity(context, StartActivity.class);
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

                            MSChatRoom.getSharedPreferences().edit().clear().commit();
                            MSChatRoom.getSharedPreferences().edit().clear().apply();
                            List<File> list = DataFiles.getChangelessFilesInFilesDir();
                            for (File file : list) {
                                if (file.isDirectory()) {
                                    //Utils.deleteDirectoryContent(file.getAbsolutePath());
                                    Utils.deleteDirectory(file);
                                } else {
                                    file.delete();
                                }
                            }
                            Utils.deleteDirectory(new File(getFilesDir().getParent(),"databases"));
                            Utils.deleteDirectory(getFilesDir());

                            MSChatRoom.getSharedPreferences().edit().clear().commit();
                            MSChatRoom.getSharedPreferences().edit().clear().apply();
                            Toast.makeText(context, getString(R.string.toast_successfully_deleted_application_data), Toast.LENGTH_SHORT).show();
                            MSChatRoom.getInstance().exit();
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

    void initUserInformation() {
        initUserInformationOffline();
        if (AccountInformationStorager.isLogined()) {
            initAvatar();
        }
        if (Utils.checkLoginStatus(context)) {

            boolean b=true;
            if(Variables.ACCOUNT_UTILS!=null){try{
                b=Variables.ACCOUNT_UTILS.getConnection().isClosed();}catch (Exception e){e.printStackTrace();}}
            if (Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null&&!b && Utils.checkLoginInformationAndNetwork(context)) {
                initUserInformationOnline();
            }
        }
    }

    void initUserInformationOnline() {
        UserInformation information = UserInformationUtils.read(context, Utils.getAccountUtils().getBytes(context, "information", AccountUtils.BY_ACCOUNT, getAccountEncrypted().toString().toUpperCase()));
        //Toast.makeText(context, getAccountEncrypted().toString().toUpperCase(), Toast.LENGTH_SHORT).show();
        final String nickname = information.nameContent;
        String whatsup = information.whatsupContent;
        if (!TextUtils.isEmpty(whatsup)) {
            runOnUiThread(()->accountPreference.setSummary(getWhatsup()));
        }
        if (TextUtils.isEmpty(nickname)) {
            if (!TextUtils.isEmpty(getAccountEncrypted())) {
                runOnUiThread(()->{
                try {
                    accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString().toUpperCase(), Variables.TEXT_ENCRYPTION_KEY)));
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
                }});
            }
        } else {
            if (!TextUtils.isEmpty(nickname)) {
                runOnUiThread(()->accountPreference.setTitle(nickname));
            }
        }
    }

    void initUserInformationOffline() {
        if (TextUtils.isEmpty(getNickname())) {
            if (!TextUtils.isEmpty(getAccountEncrypted())) {
                runOnUiThread(() -> {
                    try {
                        accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString().toUpperCase(), Variables.TEXT_ENCRYPTION_KEY)));
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
            }
        } else {
            runOnUiThread(() -> accountPreference.setTitle(getNickname()));
        }
        if (!TextUtils.isEmpty(getWhatsup())) {
            runOnUiThread(() -> accountPreference.setSummary(getWhatsup()));
        } else {
            runOnUiThread(() -> accountPreference.setSummary(getString(R.string.preference_account_whatsup_summary)));
        }

    }


    void getFilesSize() {
        try {
            clearCache.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)))));
            clearData.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)).getParentFile())+FileUtils.getFolderSize(DataFiles.INTERNAL_DATA_DIR)));
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
        if (s.equals(Variables.SHARED_PREFERENCE_THEME)) {
            dynamicModifyListSummaryTheme();
        }
        if (s.equals(Variables.SHARED_PREFERENCE_LANGUAGE)) {
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
        if (!Utils.isNetworkConnected(context)) {
            setTitle(getString(R.string.activity_settings_screen_offline_mode_name));
            //final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
        }
        new Thread(() -> {
            Looper.prepare();
            initUserInformation();
            Looper.loop();
        }).start();
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
        initAvatarFromLocal();
        boolean b=true;
        if(Variables.ACCOUNT_UTILS!=null){try{
            b=Variables.ACCOUNT_UTILS.getConnection().isClosed();}catch (Exception e){e.printStackTrace();}}
        if (Utils.isNetworkConnected(context) && Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null&&!b) {
            try {
                avatarBytes = Utils.getAccountUtils().getBytesWithException(context, "avatar", AccountUtils.BY_ACCOUNT, getAccountEncrypted().toString().toUpperCase());/*new FileInputStream(avatar)getAvatar();*/

                if (avatarBytes != null&&avatarBytes.length!=0&&!Utils.isBytesAllZero(avatarBytes)) {
                    runOnUiThread(() -> accountPreference.setIcon(ImageFormatConverter.bytes2Drawable(avatarBytes)));
                }else{
                    runOnUiThread(() -> accountPreference.setIcon(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
                //initAvatarFromLocal();
            }
            //}
        }
    }

    void initAvatarFromLocal() {
        try {
            File file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted());
            if (file.exists())
                avatarBytes = FileUtils.toByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.loadinglog_failed_load_account_avatar), Toast.LENGTH_SHORT).show();
        }
    }

    AccountInformation getAccountInformation() {
        return Utils.getAccountInformation();
    }

    String getDecryptedAccountAndPassword(){
        try{
            return EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    CharSequence getAccountEncrypted() {
        try {
            return EnDeCryptTextUtils.encrypt(getDecryptedAccountAndPassword().split(Variables.SPLIT_SYMBOL)[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    public void onDisconnectNetwork() {
        Variables.COMMUNICATOR = null;
        Variables.SESSION=null;
    }
    public void makeTitleToOffline(){
        //Toast.makeText(context, "1089", Toast.LENGTH_SHORT).show();
        new Thread(()->runOnUiThread(()->{
            String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
            setTitle(Utils.getStringByLocale(context, R.string.activity_settings_screen_offline_mode_name, languageAndCountry[0], languageAndCountry[1]));
        })).start();
    }

    public void makeTitleToOnline(){
        //Toast.makeText(context, "1097", Toast.LENGTH_SHORT).show();
        new Thread(()->runOnUiThread(()->{
            String[] languageAndCountry = PreferenceManager.getDefaultSharedPreferences(context).getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).split("_");
            setTitle(Utils.getStringByLocale(context, R.string.activity_settings_screen_name, languageAndCountry[0], languageAndCountry[1]));
        })).start();
    }
}
