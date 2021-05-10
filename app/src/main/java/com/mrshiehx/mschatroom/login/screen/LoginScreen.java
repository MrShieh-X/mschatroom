package com.mrshiehx.mschatroom.login.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.account.remember.storage.storagers.AccountInformationRememberStorager;
import com.mrshiehx.mschatroom.start.screen.StartActivity;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.login.by_ec.screen.LoginByEmailAndCAPTCHA;
import com.mrshiehx.mschatroom.reset_password.screen.ResetPasswordScreen1;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.GetAccountUtils;
import com.mrshiehx.mschatroom.utils.KeyboardUtils;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.register.screen.RegisterScreen;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//登录界面
public class LoginScreen extends AppCompatActivity {
    //public TextInputLayout til_input_account,til_input_password;
    public AppCompatEditText input_account_or_email, input_password;
    public CheckBox remember_account_and_password, show_password;
    public Button go_to_register, reset_password, login/*switch_login_mode*/, login_by_ec;
    public static boolean can_i_back;
    public TextView input_content_empty;
    int loginMode;//0 is account, 1 is email
    Context context = LoginScreen.this;
    ProgressDialog loggingIn;
    private long firstTime = 0;
    View.OnClickListener doLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyboardUtils.disappearKeybaroad(LoginScreen.this);
            if (TextUtils.isEmpty(input_account_or_email.getText()) || TextUtils.isEmpty(input_password.getText())) {
                input_content_empty.setVisibility(View.VISIBLE);
            } else {
                //Login
                input_content_empty.setVisibility(View.GONE);
                if (Utils.isEmail(input_account_or_email.getText().toString().toLowerCase())) {
                    if (Utils.isNetworkConnected(context)) {
                        //if(sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED,false)==false) {
                        loggingIn = new ProgressDialog(context);
                        loginMode = 1;
                        //Login by email
                        if (AccountInformationStorager.isLogined()) {
                            Utils.showDialog(context, getResources().getString(R.string.dialog_title_notice), getResources().getString(R.string.dialog_relogin_message), getResources().getString(R.string.button_login), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AccountInformationStorager.deleteAccount();
                                    loginByEmail();
                                }
                            });
                        } else {
                            loginByEmail();
                        }


                    } else {
                        Snackbar.make(login, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    if (Utils.isNetworkConnected(context)) {

                        loggingIn = new ProgressDialog(context);
                        loginMode = 0;

                        //Login by account
                        if (AccountInformationStorager.isLogined() == true) {
                            Utils.showDialog(context, getResources().getString(R.string.dialog_title_notice), getResources().getString(R.string.dialog_relogin_message), getResources().getString(R.string.button_login), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //MSCRApplication.getSharedPreferences().edit().remove(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION).apply();
                                    AccountInformationStorager.deleteAccount();
                                    loginByAccount();
                                }
                            });
                        } else {
                            loginByAccount();
                        }

                    } else {
                        Snackbar.make(login, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    void loginByEmail() {
        //loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
        loggingIn.setCancelable(false);
        loggingIn.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    String email = input_account_or_email.getText().toString().toLowerCase();
                    String password = input_password.getText().toString();
                    AccountUtils ud = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Utils.getAccountUtils();
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
                    boolean result = ud.login(context, AccountUtils.BY_EMAIL, emailE, passwordE);
                    if (!result) {
                        loggingIn.dismiss();
                        Snackbar.make(login, getResources().getString(R.string.toast_email_or_password_incorrect), Snackbar.LENGTH_SHORT).show();
                    } else {
                        loggingIn.dismiss();
                        Snackbar.make(login, getResources().getString(R.string.toast_successfully_login), Snackbar.LENGTH_SHORT).show();
                        afterLogin();
                    }
                } catch (Exception e) {
                    loggingIn.dismiss();
                    e.printStackTrace();
                    Snackbar.make(login, getResources().getString(R.string.toast_failed_login), Snackbar.LENGTH_SHORT).show();
                    Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_login));
                }
                Looper.loop();
            }
        }).start();

    }

    void loginByAccount() {
        //loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
        loggingIn.setCancelable(false);
        loggingIn.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    String account = input_account_or_email.getText().toString().toLowerCase();
                    String password = input_password.getText().toString();
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
                    if (!result) {
                        loggingIn.dismiss();
                        Snackbar.make(login, getResources().getString(R.string.toast_account_or_password_incorrect), Snackbar.LENGTH_SHORT).show();
                    } else {
                        loggingIn.dismiss();
                        Snackbar.make(login, getResources().getString(R.string.toast_successfully_login), Snackbar.LENGTH_SHORT).show();
                        afterLogin();
                    }
                } catch (Exception e) {
                    loggingIn.dismiss();
                    e.printStackTrace();
                    Snackbar.make(login, getResources().getString(R.string.toast_failed_login), Snackbar.LENGTH_SHORT).show();
                    Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_login));
                }
                Looper.loop();
            }
        }).start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_login_screen_name);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        if (can_i_back) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //til_input_account=findViewById(R.id.til_input_account);
        //til_input_password=findViewById(R.id.til_input_password);
        input_account_or_email = findViewById(R.id.input_account_or_email);
        input_password = findViewById(R.id.input_password);
        show_password = findViewById(R.id.log_show_password);
        remember_account_and_password = findViewById(R.id.remember_account_and_password);
        go_to_register = findViewById(R.id.go_to_register);
        reset_password = findViewById(R.id.go_to_reset_password);
        login = findViewById(R.id.login);
        input_content_empty = findViewById(R.id.input_content_empty);
        login_by_ec = findViewById(R.id.login_by_emailandcaptcha);
        //switch_login_mode=findViewById(R.id.switch_login_mode);
        //loginMode=0;

        //input_password.setInputType(129);
        //input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
        input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ////input_password.setInputType(144);
                    input_password.setSelection(input_password.getText().length());
                    //input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                } else {
                    input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //input_password.setInputType(129);
                    input_password.setSelection(input_password.getText().length());
                    //input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                }


                /*
                if(show_password.isChecked()==true){
                    input_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    input_password.setSelection(input_password.getText().length());
                    input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                }else{
                    input_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input_password.setSelection(input_password.getText().length());
                    input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                }*/
            }
        });
        /*Utils.inputFilterSpace(input_account);
        Utils.setEditTextInhibitInputSpeChat(input_account);
        Utils.inputFilterSpace(input_password);
        Utils.setEditTextInhibitInputSpeChat(input_password);*/
        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterScreen.can_i_back = true;
                Utils.startActivity(context, RegisterScreen.class);
            }
        });
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(context, ResetPasswordScreen1.class);
            }
        });
        login.setOnClickListener(doLogin);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (AccountInformationRememberStorager.isRemember()) {
            try {
                String emailOrAccountAndPasswordTogether = EnDeCryptTextUtils.decrypt(AccountInformationRememberStorager.getContent(), Variables.TEXT_ENCRYPTION_KEY);
                String[] emailOrAccountAndPassword = emailOrAccountAndPasswordTogether.split(Variables.SPLIT_SYMBOL);
                input_account_or_email.setText(emailOrAccountAndPassword[0]);
                input_password.setText(emailOrAccountAndPassword[1]);
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
        login_by_ec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginByEmailAndCAPTCHA.can_i_back = true;
                Utils.startActivity(context, LoginByEmailAndCAPTCHA.class);
            }
        });
        Intent i = getIntent();
        if (i != null) {
            String a = i.getStringExtra("account");
            if (!TextUtils.isEmpty(a)) {
                input_account_or_email.setText(a);
            }
        }
    }

    void afterLogin() {
        try {
            /**
             * Login method
             * 0 is account
             * 1 is email
             */
            if (!Utils.isEmail(input_account_or_email.getText().toString().toLowerCase())) {
                AccountInformationStorager.putAccount(EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase() + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
                //editor.putString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION, EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase() + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
            } else {
                AccountInformationStorager.putAccount(EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(GetAccountUtils.getAccount(Utils.getAccountUtils(), context, EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase()))) + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
                //editor.putString(Variables.SHARED_PREFERENCE_LOGIN_INFORMATION, EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(GetAccountUtils.getAccount(Utils.getAccountUtils(), context, EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase()))) + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
            }
            if (remember_account_and_password.isChecked()) {
                AccountInformationRememberStorager.putContent(EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase() + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
                //editor.putString(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT, EnDeCryptTextUtils.encrypt(input_account_or_email.getText().toString().toLowerCase() + Variables.SPLIT_SYMBOL + input_password.getText().toString(), Variables.TEXT_ENCRYPTION_KEY));
            } else {
                /*editor.remove(Variables.SHARED_PREFERENCE_IS_REMEMBER_EOA_AND_PASSWORD);
                editor.remove(Variables.SHARED_PREFERENCE_REMEMBER_CONTENT);*/
                AccountInformationRememberStorager.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_save_data));
        }
        finish();
        Utils.startActivity(context, StartActivity.class);
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
                if (!can_i_back) {
                    long secondTime = System.currentTimeMillis();
                    if (secondTime - firstTime > 2000) {
                        Toast.makeText(context, getResources().getString(R.string.toast_press_again_exit_application), Toast.LENGTH_SHORT).show();
                        firstTime = secondTime;
                        return true;
                    } else {
                        MSChatRoom.getInstance().exit();
                    }
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
    }
}
