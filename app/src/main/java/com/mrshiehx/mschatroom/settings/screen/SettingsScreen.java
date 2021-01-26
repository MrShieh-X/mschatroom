package com.mrshiehx.mschatroom.settings.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mrshiehx.mschatroom.AccountInformation;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.StartScreen;
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
import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//设置界面
public class SettingsScreen extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public ListPreference modify_theme, modify_language;
    //public AccountPreference accountPreference;
    public Preference accountPreference, logout;
    HasFilesSizePreference clearCache,clearData;
    //ImageView iv;
    //TextView tv;
    //List<UserInformation> userInformationList;
    //InputStream inputStream;
    Context context = SettingsScreen.this;
    int accountNameIndex = 0;
    int accountGenderIndex = 1;
    int accountWhatSUpIndex = 2;
    //String accountNameName;
    //boolean accountNameIsHave;
    //String accountNameContent;
    //String accountAvatarName;
    //boolean accountAvatarIsHave;
    //String accountAvatarContent;
    //String accountGenderName;
    //boolean accountGenderIsHave;
    //String accountGenderContent;
    //String accountWhatSUpName;
    //boolean accountWhatSUpIsHave;
    //String accountWhatSUpContent;
    ProgressDialog gettingUI;
    //boolean canLogin;
    String password;//, emailString, accountString;
    InputStream avatarInputStream;
    NetworkStateReceiver myReceiver;
    public static int anInt=0;
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
        gettingUI=new ProgressDialog(context);
        gettingUI.setTitle(getString(R.string.dialog_title_notice));
        gettingUI.setMessage(getString(R.string.dialog_getting_user_information));
        gettingUI.setCancelable(false);
        modify_theme = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_THEME);
        modify_language = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE);
        //accountPreference=(AccountPreference) getPreferenceScreen().findPreference("account");
        accountPreference =  getPreferenceScreen().findPreference("account");
        logout = getPreferenceScreen().findPreference("logout");
        clearCache=(HasFilesSizePreference)getPreferenceScreen().findPreference("clear_cache");
        clearData=(HasFilesSizePreference)getPreferenceScreen().findPreference("clear_application_data");


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

                if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false) == false) {
                    LoginScreen.can_i_back = true;
                    Utils.startActivity(SettingsScreen.this, LoginScreen.class);
                } else {
                    Utils.startActivity(SettingsScreen.this, ModifyUserInformationScreen.class);
                }

                return true;
            }
        });

        /*Log.e("fuck",getAccountEncrypted().toString());
        Log.e("fuck",getEmailEncrypted().toString());
        Log.e("fuck",getNickname().toString());
        Log.e("fuck",getGender().toString());
        Log.e("fuck",getWhatsup().toString());*/

        if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false) == false) {
            logout.setEnabled(false);
        }

        //boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        //if (isFirstRun == true) {

            //modify_language.setValue(Utils.getSystemLanguage() + "_" + Utils.getSystemCountry());

            /*switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    modify_theme.setValue("dark");
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    modify_theme.setValue("light");
                    break;
            }*/
        //} else {

        modify_language.setValue(sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, Utils.getSystemLanguage() + "_" + Utils.getSystemCountry()));
        modify_theme.setValue(sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_THEME, "dark"));
        //}
        dynamicModifyListSummaryTheme();
        dynamicModifyListSummaryLanguage();



        /*gettingUI.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //if (Utils.checkLoginInformationAndNetwork(context)) {
                    //if(Utils.networkAvailableDialog(context)) {
                    int loginMethod = MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1);
                    //loggingIn=ProgressDialog.show(context,getResources().getString(R.string.dialog_title_wait),getResources().getString(R.string.dialog_loggingIn_message),false,false);
                    //loggingIn.show();
                    //loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
                    //loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
                    //loggingIn.setCancelable(false);
                    //loggingIn.show();
                    //loggingIn.cancel();
                    //loggingIn.dismiss();
                    if (loginMethod == 0) {
                        //account
                        String account = "";
                        try {
                            account = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
                            password = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
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
                        Boolean result = ud.loginNoThreadAndDialog(context, AccountUtils.BY_ACCOUNT, accountE, passwordE);
                        if (!result) {
                            //canLogin = false;
                            Utils.showDialog(context,
                                    getResources().getString(R.string.dialog_title_notice),
                                    getResources().getString(R.string.dialog_failed_login_insettings_message),
                                    getResources().getString(R.string.dialog_failed_login_insettings_button_gotologin_text),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            LoginScreen.can_i_back = true;
                                            Utils.startActivity(context, LoginScreen.class);
                                        }
                                    });
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
                        Boolean result = ud.loginNoThreadAndDialog(context, AccountUtils.BY_EMAIL, emailE, passwordE);
                        if (!result) {
                            Utils.showDialog(context,
                                    getResources().getString(R.string.dialog_title_notice),
                                    getResources().getString(R.string.dialog_failed_login_insettings_message),
                                    getResources().getString(R.string.dialog_failed_login_insettings_button_gotologin_text),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            LoginScreen.can_i_back = true;
                                            Utils.startActivity(context, LoginScreen.class);
                                        }
                                    });
                            //canLogin = false;
                        }
                    }
                    initAvatar();
                    //initAccountAndEmail();
                    initUserInformation();
                //}
                //loggingIn.dismiss();
                gettingUI.dismiss();
                Looper.loop();


            }
        }).start();
*/
        if(Utils.isNetworkConnected(context)) {
            //Toast.makeText(context, String.valueOf(Variables.ACCOUNT_UTILS==null), Toast.LENGTH_SHORT).show();
            if (Variables.ACCOUNT_UTILS == null) {
                final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Connection connection = new ConnectionUtils(Variables.SERVER_ADDRESS).getConnection(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
                        if (connection == null) {
                            Toast.makeText(context, getString(R.string.toast_connect_failed), Toast.LENGTH_SHORT).show();
                        } else {
                            AccountUtils accountUtils = new AccountUtils(connection, Variables.DATABASE_TABLE_NAME);
                            Variables.ACCOUNT_UTILS = accountUtils;
                        }
                        dialog.dismiss();
                        Looper.loop();
                    }
                }).start();
            }
        }else{
            setTitle(getString(R.string.activity_settings_screen_offline_mode_name));
        }
        if(MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED,false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    initAvatar();
                    Looper.loop();
                }
            }).start();
            
        }
        if(Variables.ACCOUNT_UTILS!=null){
            if(Variables.ACCOUNT_UTILS.getConnection()!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if(Utils.checkLoginInformationAndNetwork(context)){
                            initUserInformation();
                        }
                        Looper.loop();
                    }
                }).start();
            }
        }

        /*if(getAccountInformation()!=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Looper.loop();
                }
            }).start();
        }*/
        //clianNotThread=Utils.checkLoginInformationAndNetworkForSettings(context);


        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                final View dialogView = LayoutInflater.from(context)
                        .inflate(R.layout.checkbox_dialog,null);
                dialog.setView(dialogView);
                dialog.setTitle(getResources().getString(R.string.dialog_title_notice));
                TextView textView=dialogView.findViewById(R.id.checkbox_dialog_message);
                textView.setText(getResources().getString(R.string.dialog_logout_message));
                final CheckBox checkBox = dialogView.findViewById(R.id.checkbox_dialog_checkbox);
                checkBox.setText(getString(R.string.dialog_logout_checkbox_text));
                //checkBox.setChecked(false);
                //checkBox.setEnabled(false);
                dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
                dialog.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(checkBox.isChecked()){
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"chat_avatars"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"chats"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"information"));
                            File chatsFile=new File(Utils.getDataFilesPath(context),"chats.json");
                            chatsFile.delete();
                        }
                        File file;
                        if(sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1){
                            //account
                            file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted())/*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY))*/;
                        }else{
                            //email
                            file = new File(Utils.getDataFilesPath(context), "avatar_" + getEmailEncrypted()/*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY)*/);
                        }
                        if(file!=null&&file.exists()) {
                            file.delete();
                        }
                        try {
                            editor.remove(Variables.SHARED_PREFERENCE_IS_LOGINED);
                            editor.remove(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD);
                            editor.remove(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD);
                            editor.remove(Variables.SHARED_PREFERENCE_LOGIN_METHOD);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utils.startActivity(context, StartScreen.class);
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
                AlertDialog.Builder clearCache=new AlertDialog.Builder(context);
                clearCache.setTitle(getString(R.string.dialog_title_notice));
                clearCache.setMessage(getString(R.string.dialog_clear_cache_message));
                clearCache.setNegativeButton(getString(android.R.string.cancel),null);
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
                AlertDialog.Builder clearData=new AlertDialog.Builder(context);
                clearData.setTitle(getString(R.string.dialog_title_notice));
                clearData.setMessage(getString(R.string.dialog_clear_application_data_message));
                clearData.setNegativeButton(getString(android.R.string.cancel),null);
                clearData.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //getFilesDir().getParentFile().delete();

                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context)+File.separator+"chat_avatars");
                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context)+File.separator+"chats");
                            Utils.deleteDirectoryContent(Utils.getDataFilesPath(context)+File.separator+"information");
                            Utils.deleteDirectoryContent(getFilesDir().getAbsolutePath());
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"chat_avatars"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"chats"));
                            Utils.deleteDirectory(new File(Utils.getDataFilesPath(context),"information"));
                            Utils.deleteDirectory(getFilesDir());

                            editor.clear().apply();
                            Toast.makeText(context, getString(R.string.toast_successfully_deleted_application_data), Toast.LENGTH_SHORT).show();
                            MSCRApplication.getInstance().exit();
                            //getFilesSize();
                        }catch (Exception e){
                            e.printStackTrace();
                            Utils.exceptionDialog(context,e,getString(R.string.toast_failed_to_deleted_application_data));
                            Toast.makeText(context, getString(R.string.toast_failed_to_deleted_application_data), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                clearData.show();
                return true;
            }
        });
    }


    /*void initAccountAndEmail() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1);
        if (loginMethod == 0) {
                    String accountClean = "";
                    try {
                        accountClean = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                    final String finalAccountClean = accountClean;
                    accountString = finalAccountClean;
                    AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                    String emailClean = "";
                    try {
                        emailClean = EnDeCryptTextUtils.decrypt(accountUtils.getStringNoThread(context, "email", AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(accountClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
                    final String finalEmailClean = emailClean;
                    emailString = finalEmailClean;
        } else {
            String emailClean = "";
            try {
                emailClean = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
            final String finalEmailClean = emailClean;
            emailString = finalEmailClean;
            AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            String accountClean = "";
            try {
                accountClean = EnDeCryptTextUtils.decrypt(accountUtils.getStringNoThread(context, "account", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(emailClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
            final String finalAccountClean = accountClean;
            accountString = finalAccountClean;
        }
    }*/

    void getFilesSize(){
        try {
            clearCache.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)))));
            clearData.setFilesSize(FileUtils.getFormatSize(FileUtils.getFolderSize(new File(Utils.getDataCachePath(context)).getParentFile())));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context,e,getString(R.string.dialog_exception_failed_to_get_files_size));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (TextUtils.isEmpty(modify_theme.getValue())) {
            //modify_theme.setSummary(getResources().getString(R.string.preference_settings_edittexts_input_versions_source_hint));
        } else {
            modify_theme.setSummary(modify_theme.getEntry());
        }
    }


    public void dynamicModifyListSummaryLanguage() {
        if (TextUtils.isEmpty(modify_language.getValue())) {
            //modify_language.setSummary(getResources().getString(R.string.preference_settings_edittexts_input_versions_source_hint));
        } else {
            modify_language.setSummary(modify_language.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dynamicModifyListSummaryTheme();
        dynamicModifyListSummaryLanguage();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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

    /*public void initUserInformationFile() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1);
        if (loginMethod == 0) {
            *//*String accountClean = "";
            try {
                accountClean = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
            final String finalAccountClean = accountClean;
            accountString = finalAccountClean;
            String emailClean = "";
            try {
                emailClean = EnDeCryptTextUtils.decrypt(accountUtils.getStringNoThread(context, "email", AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(accountClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
            final String finalEmailClean = emailClean;
            emailString = finalEmailClean;*//*
            try {
                AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);

                //inputStream = accountUtils.getUserInformationNoThread(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));

                inputStream=accountUtils.getInputStreamNoThread(context,"information",AccountUtils.BY_ACCOUNT,getAccountEncrypted().toString());
            *//*} catch (InvalidKeySpecException e) {
                //System.out.printf("errorInvalidKeySpecException"+e);
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                //System.out.printf("errorInvalidKeyException"+e);
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                //System.out.printf("errorNoSuchPaddingException"+e);
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                //System.out.printf("errorIllegalBlockSizeException"+e);
                e.printStackTrace();*//*
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
            }
        } else {
            *//*String emailClean = "";
            try {
                emailClean = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
            final String finalEmailClean = emailClean;
            emailString = finalEmailClean;
            AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            String accountClean = "";
            try {
                accountClean = EnDeCryptTextUtils.decrypt(accountUtils.getStringNoThread(context, "account", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(emailClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
            final String finalAccountClean = accountClean;
            accountString = finalAccountClean;*//*
            //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            try {
            AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                inputStream=accountUtils.getInputStreamNoThread(context,"information",AccountUtils.BY_EMAIL,getEmailEncrypted().toString());
                *//*inputStream = accountUtils.getUserInformationNoThread(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));
                //ModifyUserInformationScreen.emailAndAccount=EnDeCryptTextUtils.encrypt(email + Variables.SPLIT_SYMBOL + account, Variables.TEXT_ENCRYPTION_KEY);
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
            }*//*
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
            }

        }
    }*/


    void initUserInformation() {
        if(Utils.isNetworkConnected(context)){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    Looper.prepare();
                    List<UserInformation> information=XMLUtils.readXmlBySAX(Variables.ACCOUNT_UTILS.getUserInformationWithoutPasswordNoThread(context,(MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1?AccountUtils.BY_ACCOUNT:AccountUtils.BY_EMAIL),(MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1?getAccountEncrypted().toString():getEmailEncrypted().toString())));
                    final String nickname=information.get(accountNameIndex).getNameContent();
                    String whatsup=information.get(accountWhatSUpIndex).getWhatsupContent();
                    if (!TextUtils.isEmpty(whatsup)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                accountPreference.setSummary(getWhatsup());
                            }
                        });
                    }
                    if (TextUtils.isEmpty(nickname)) {
                        if (!TextUtils.isEmpty(getAccountEncrypted())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
                            });
                        }
                    }else{
                        if(!TextUtils.isEmpty(nickname)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    accountPreference.setTitle(nickname);
                                }
                            });
                        }
                    }
                    Looper.loop();
                }
            }).start();


        }else{
            if (!TextUtils.isEmpty(getWhatsup())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        accountPreference.setSummary(getWhatsup());
                    }
                });
            }


            if (TextUtils.isEmpty(getNickname())){
                if(!TextUtils.isEmpty(getAccountEncrypted())) {
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
            }else{
                if(!TextUtils.isEmpty(getNickname())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setTitle(getNickname());
                        }
                    });
                }
            }



            /* else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        accountPreference.setSummary(getString(R.string.preference_account_summary_tip_set_whatsup));
                    }
                });
            }*/
        }

        //final SharedPreferences sharedPreferences = MSCRApplication.getSharedPreferences();
        /*if (inputStream == null) {
            Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            return false;
        } else {*/
            /*userInformationList = XMLUtils.readXmlBySAX(inputStream);
            if (userInformationList != null) {*/
                //accountNameContent = userInformationList.get(accountNameIndex).getNameContent();
                //accountGenderContent = userInformationList.get(accountGenderIndex).getGenderContent();
                //accountWhatSUpContent = userInformationList.get(accountWhatSUpIndex).getWhatsupContent();

                if (!TextUtils.isEmpty(getWhatsup())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setSummary(getWhatsup());
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setSummary(getString(R.string.preference_account_summary_tip_set_whatsup));
                        }
                    });
                }

                if (TextUtils.isEmpty(getNickname())) {
                    /*if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(getAccountEncrypted().toString(),Variables.TEXT_ENCRYPTION_KEY)));
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
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                *//*String[] emailAndPassword = new String[0];
                                try {
                                    emailAndPassword = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL);
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
                                String email = emailAndPassword[0];
                                String account = "";
                                try {
                                    AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                                    account = EnDeCryptTextUtils.decrypt(accountUtils.getAccountByEmailNoThread(context, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));

                                }*//*
                                accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), getAccountEncrypted()));
                            }
                        });
                    }*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                    });
                    /*Looper.prepare();
                    Snackbar.make(context, getResources().getString(R.string.toast_tip_set_name), Snackbar.LENGTH_SHORT).show();
                    Looper.loop();*/
                }else {
                    //Set Name
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setTitle(getNickname());

                        }
                    });
                }
                /*if (!accountGenderIsHave) {
                    Looper.prepare();
                    Snackbar.make(context, getResources().getString(R.string.toast_tip_set_gender), Snackbar.LENGTH_SHORT).show();
                    Looper.loop();
                }*/
            /*} else {
                Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            }*/
            //return true;
        //}
    }

    void initAvatar() {
        //final AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);

        /*try {
            avatarInputStream = au.getInputStreamNoThread(context, "avatar", "password", EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY));
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
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
        }*/

        /*File avatar;
        if(MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1) {
            avatar=new File(Utils.getDataFilesPath(context),"avatar_" + getAccountEncrypted());
        }else{
            avatar=new File(Utils.getDataFilesPath(context),"avatar_" + getEmailEncrypted());
        }*/
        //if(avatar.exists()) {
        if(Utils.isNetworkConnected(context)) {
            try {
                avatarInputStream = Variables.ACCOUNT_UTILS.getInputStreamNoThread(context, "avatar", "account", getAccountEncrypted().toString());/*new FileInputStream(avatar)getAvatar();*/
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
            }
            //}
        }else{
            try {
                avatarInputStream = new FileInputStream(new File(Utils.getDataFilesPath(context),"avatar_"+(MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD,-1)!=1?getAccountEncrypted():getEmailEncrypted())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_file_not_found));
                Toast.makeText(context, getResources().getString(R.string.dialog_exception_file_not_found), Toast.LENGTH_SHORT).show();
            }
        }
        if (avatarInputStream != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    accountPreference.setIcon(FormatTools.getInstance().InputStream2Drawable(avatarInputStream));
                }
            });
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_avatar), Toast.LENGTH_SHORT).show();
        }
    }
/*

    @Override
    protected void onRestart() {
        super.onRestart();
        load();
    }*/

    AccountInformation getAccountInformation(){
        return Variables.ACCOUNT_INFORMATION;
    }

    CharSequence getAccountEncrypted(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getAccountE();
        return null;
    }

    CharSequence getEmailEncrypted(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getEmailE();
        return null;
    }

    boolean isNetworkConnected(){
        if(getAccountInformation()!=null)
            return getAccountInformation().isNetworkConnected();
        return false;
    }
    boolean isCanLogin(){
        if(getAccountInformation()!=null)
            return getAccountInformation().isCanLogin();
        return false;
    }
    boolean isLogined(){
        if(getAccountInformation()!=null)
            return getAccountInformation().isLogined();
        return false;
    }

    CharSequence getNickname(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getNickname();
        return null;
    }
    CharSequence getWhatsup(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getWhatsup();
        return null;
    }
    CharSequence getGender(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getGender();
        return null;
    }
    /*InputStream getAvatar(){
        if(getAccountInformation()!=null)
            return getAccountInformation().getAvatar();
        return null;
    }*/

}
