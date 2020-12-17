package com.mrshiehx.mschatroom.settings.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.StartScreen;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.modify_user_information.screen.ModifyUserInformationScreen;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//设置界面
public class SettingsScreen extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public ListPreference modify_theme, modify_language;
    //public AccountPreference accountPreference;
    public Preference accountPreference, logout;
    //ImageView iv;
    //TextView tv;
    List<UserInformation> userInformationList;
    InputStream inputStream;
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
    ProgressDialog loggingIn;
    //boolean canLogin;
    String password, emailString, accountString;
    InputStream avatarInputStream;

    //int clianNotThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_settings_screen_name);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loggingIn = new ProgressDialog(context);
        modify_theme = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_THEME);
        modify_language = (ListPreference) getPreferenceScreen().findPreference(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE);
        //accountPreference=(AccountPreference) getPreferenceScreen().findPreference("account");
        accountPreference = (Preference) getPreferenceScreen().findPreference("account");
        logout = (Preference) getPreferenceScreen().findPreference("logout");
        //iv=findViewById(R.id.account_icon);
        //tv=findViewById(R.id.account_name);

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
        if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false) == false) {
            logout.setEnabled(false);
        }
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.showDialog(context,
                        getResources().getString(R.string.dialog_title_notice),
                        getResources().getString(R.string.dialog_logout_message),
                        getResources().getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    editor.remove(Variables.SHARED_PREFERENCE_IS_LOGINED);
                                    editor.remove(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD);
                                    editor.remove(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD);
                                    editor.remove(Variables.SHARED_PREFERENCE_LOGIN_METHOD);
                                    editor.commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                finish();
                                Utils.startActivity(context, StartScreen.class);
                            }
                        });
                return true;
            }
        });

        boolean isFirstRun = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_FIRST_RUN, true);
        if (isFirstRun == true) {
            modify_language.setValue(Utils.getSystemLanguage() + "_" + Utils.getSystemCountry());
        } else {
            modify_language.setValue(sharedPreferences.getString(Variables.SHARED_PREFERENCE_MODIFY_LANGUAGE, Utils.getSystemLanguage() + "_" + Utils.getSystemCountry()));
        }
        dynamicModifyListSummaryTheme();
        dynamicModifyListSummaryLanguage();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Utils.checkLoginInformationAndNetwork(context)) {
                    //if(Utils.networkAvailableDialog(context)) {
                    int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
                    if (loginMethod == 0) {
                        //account
                        loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
                        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
                        loggingIn.setCancelable(false);
                        loggingIn.show();
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
                            loggingIn.dismiss();
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
                        } else {
                            //canLogin = true;
                            initUserInformationFile();
                            loggingIn.dismiss();
                        }
                    } else {
                        loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
                        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
                        loggingIn.setCancelable(false);
                        loggingIn.show();
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
                            loggingIn.dismiss();
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
                        } else {
                            //canLogin = true;
                            initUserInformationFile();
                            loggingIn.dismiss();
                        }
                    }


                }
                Looper.loop();


            }
        }).start();


        //clianNotThread=Utils.checkLoginInformationAndNetworkForSettings(context);


        init();
    }

    void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Utils.checkLoginInformationAndNetwork(context)) {
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (inputStream != null) {
                                Looper.prepare();
                                if (Utils.checkLoginInformationAndNetwork(context)) {
                                    initAvatar();
                                    initAccountAndEmail();
                                    initUserInformation();
                                    //onCreate(null);
                                    //finish();
                                    //Utils.startActivity(context,SettingsScreen.class);
                                    timer.cancel();
                                }
                                Looper.loop();
                            }
                        }
                    }, 0, 500);
                }
                Looper.loop();
            }
        }).start();

    }


    void initAccountAndEmail() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
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
                        emailClean = EnDeCryptTextUtils.decrypt(accountUtils.getString(context, "email", AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(accountClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
                accountClean = EnDeCryptTextUtils.decrypt(accountUtils.getString(context, "account", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(emailClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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

    public void initUserInformationFile() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
        if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
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
                        emailClean = EnDeCryptTextUtils.decrypt(accountUtils.getString(context, "email", AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(accountClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
                    try {
                        //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);


                        inputStream = accountUtils.getUserInformation(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));
                        //ModifyUserInformationScreen.emailAndAccount=EnDeCryptTextUtils.encrypt(email + Variables.SPLIT_SYMBOL + account, Variables.TEXT_ENCRYPTION_KEY);
                    } catch (InvalidKeySpecException e) {
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
                        e.printStackTrace();
                    } catch (Exception e) {
                        Looper.prepare();
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
                        Looper.loop();
                    }
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
                        accountClean = EnDeCryptTextUtils.decrypt(accountUtils.getString(context, "account", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(emailClean, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
                    //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                    try {
                        inputStream = accountUtils.getUserInformation(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));
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
                    }


        }
    }


    public boolean initUserInformation() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (inputStream == null) {
            Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            userInformationList = XMLUtils.readXmlBySAX(inputStream);
            if (userInformationList != null) {
                //accountNameContent = userInformationList.get(accountNameIndex).getNameContent();
                //accountGenderContent = userInformationList.get(accountGenderIndex).getGenderContent();
                //accountWhatSUpContent = userInformationList.get(accountWhatSUpIndex).getWhatsupContent();

                if (!TextUtils.isEmpty(userInformationList.get(accountWhatSUpIndex).getWhatsupContent())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setSummary(userInformationList.get(accountWhatSUpIndex).getWhatsupContent());
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

                if (TextUtils.isEmpty(userInformationList.get(accountNameIndex).getNameContent())) {
                    if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0]));
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
                                String[] emailAndPassword = new String[0];
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
                                    account = EnDeCryptTextUtils.decrypt(accountUtils.getAccountByEmail(context, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));

                                }
                                accountPreference.setTitle(String.format(getResources().getString(R.string.preference_account_title_no_name_set), account));
                            }
                        });
                    }
                    /*Looper.prepare();
                    Snackbar.make(context, getResources().getString(R.string.toast_tip_set_name), Snackbar.LENGTH_SHORT).show();
                    Looper.loop();*/
                } else {
                    //Set Name
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountPreference.setTitle(userInformationList.get(accountNameIndex).getNameContent());

                        }
                    });
                }
                /*if (!accountGenderIsHave) {
                    Looper.prepare();
                    Snackbar.make(context, getResources().getString(R.string.toast_tip_set_gender), Snackbar.LENGTH_SHORT).show();
                    Looper.loop();
                }*/
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    void initAvatar() {
        final AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);

        try {
            avatarInputStream = au.getInputStream(context, "avatar", "password", EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY));
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
}
