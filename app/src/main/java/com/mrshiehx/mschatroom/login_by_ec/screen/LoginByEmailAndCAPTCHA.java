package com.mrshiehx.mschatroom.login_by_ec.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.mrshiehx.mschatroom.MainActivity;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.reset_password.screen.ResetPasswordScreen2;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.CountDownTimerUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.SendEmailUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//使用邮箱验证码登录
public class LoginByEmailAndCAPTCHA extends AppCompatActivity {
    Context context=LoginByEmailAndCAPTCHA.this;
    public static boolean can_i_back;
    private long firstTime = 0;
    AppCompatEditText input_email,input_captcha;
    Button get_captcha,login;
    String email,captcha,clean_password;
    public CountDownTimerUtils mCountDownTimerUtils;
    ProgressDialog loggingIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initialization(LoginByEmailAndCAPTCHA.this, R.string.activity_login_by_ec_screen_name);
        if (can_i_back) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_login_by_ec);
        input_email=findViewById(R.id.lbec_input_email);
        input_captcha=findViewById(R.id.lbec_input_captcha);
        get_captcha=findViewById(R.id.lbec_get_captcha);
        login=findViewById(R.id.lbec_login);

        mCountDownTimerUtils = new CountDownTimerUtils(context, get_captcha, Variables.GET_CAPTCHA_TIME, 1000);
        if (!Utils.networkAvailableDialog(context)) {
            get_captcha.setEnabled(false);
            //login.setEnabled(false);
        } else {
            if (TextUtils.isEmpty(input_email.getText().toString())) {
                get_captcha.setEnabled(false);
                //login.setEnabled(false);
            }
        }
        get_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context)) {
                    captcha = Utils.newRandomNumber(6);
                    //Get captcha
                    try {
                        get_captcha.setEnabled(false);
                        SendEmailUtils sendEmail = new SendEmailUtils(input_email.getText().toString());
                        sendEmail.sendCaptcha(captcha);
                        Toast.makeText(context, getResources().getString(R.string.toast_successfully_got_captcha), Toast.LENGTH_SHORT).show();
                        email = input_email.getText().toString();
                        input_email.setEnabled(false);
                        mCountDownTimerUtils.start();
                    } catch (Exception e) {
                        Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_get_captcha));
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.toast_failed_get_captcha), Toast.LENGTH_SHORT).show();
                        get_captcha.setEnabled(true);
                    }

                } else {
                    Toast.makeText(context, getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                }
            }
        });
        input_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isEmail(input_email.getText().toString()) && charSequence.length() > 0 == true) {
                    if (Utils.isNetworkConnected(context)) {
                        if (mCountDownTimerUtils.isRunning() == true) {
                            get_captcha.setEnabled(false);
                        } else {
                            get_captcha.setEnabled(true);
                        }
                    } else {
                        get_captcha.setEnabled(false);
                    }
                } else {
                    get_captcha.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isEmail(input_email.getText().toString()) && charSequence.length() > 0 == true) {
                    if (Utils.isNetworkConnected(context)) {
                        if (mCountDownTimerUtils.isRunning() == true) {
                            get_captcha.setEnabled(false);
                        } else {
                            get_captcha.setEnabled(true);
                        }
                    } else {
                        get_captcha.setEnabled(false);
                    }
                } else {
                    get_captcha.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Utils.isEmail(input_email.getText().toString()) && editable.length() > 0 == true) {
                    if (Utils.isNetworkConnected(context)) {
                        if (mCountDownTimerUtils.isRunning() == true) {
                            get_captcha.setEnabled(false);
                        } else {
                            get_captcha.setEnabled(true);
                        }
                    } else {
                        get_captcha.setEnabled(false);
                    }
                } else {
                    input_email.setError(getResources().getString(R.string.aceterror_invalid_email));
                    get_captcha.setEnabled(false);
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context)) {
                    if (TextUtils.isEmpty(input_email.getText().toString()) || TextUtils.isEmpty(input_captcha.getText().toString())) {
                        Toast.makeText(context, getResources().getString(R.string.toast_input_content_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        loggingIn=new ProgressDialog(context);
                        loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
                        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
                        loggingIn.setCancelable(false);
                        loggingIn.show();
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (input_captcha.getText().toString().equals(captcha)) {
                                            AccountUtils mysqlUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                                            try {
                                                clean_password = EnDeCryptTextUtils.decrypt(mysqlUtils.getString(context, "password", AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY)), Variables.TEXT_ENCRYPTION_KEY);
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
                                            try {

                                                if (mysqlUtils.login(context, loggingIn, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(clean_password, Variables.TEXT_ENCRYPTION_KEY))) {

                                                    Looper.prepare();
                                                    loggingIn.dismiss();
                                                    Toast.makeText(context, getResources().getString(R.string.toast_successfully_login), Toast.LENGTH_SHORT).show();
                                                    //after login
                                                    try {
                                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                                                        editor.putBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, true);
                                                        editor.putInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 1);
                                                        editor.putString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, EnDeCryptTextUtils.encrypt(email + Variables.SPLIT_SYMBOL + clean_password, Variables.TEXT_ENCRYPTION_KEY));
                                                        editor.commit();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        //Looper.prepare();
                                                        Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_save_data));
                                                        //Looper.loop();
                                                    }
                                                    finish();
                                                    Utils.startActivity(context, MainActivity.class);

                                                    Looper.loop();
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(context, getResources().getString(R.string.toast_failed_login), Toast.LENGTH_SHORT).show();
                                                    loggingIn.dismiss();
                                                    Looper.loop();
                                                }
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

                                        } else {
                                            Looper.prepare();
                                            loggingIn.dismiss();
                                            Toast.makeText(context, getResources().getString(R.string.toast_captcha_incorrect), Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }catch (Exception e){
                                        Looper.prepare();
                                        loggingIn.dismiss();
                                        Utils.exceptionDialog(context,e,getResources().getString(R.string.toast_failed_login));
                                        Looper.loop();
                                    }
                                }
                            }).start();
                        }catch (Exception e){
                            loggingIn.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.toast_failed_login), Toast.LENGTH_SHORT).show();
                            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_login));
                        }

                    }
                } else {
                    Toast.makeText(context, getResources().getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                }
            }
        });







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
                if (can_i_back == false) {
                    long secondTime = System.currentTimeMillis();
                    if (secondTime - firstTime > 2000) {
                        Toast.makeText(this, getResources().getString(R.string.toast_press_again_exit_application), Toast.LENGTH_SHORT).show();
                        firstTime = secondTime;
                        return true;
                    } else {
                        System.exit(0);
                    }
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
