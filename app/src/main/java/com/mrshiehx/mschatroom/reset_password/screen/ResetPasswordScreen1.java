package com.mrshiehx.mschatroom.reset_password.screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.utils.CountDownTimerUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.SendEmailUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//重置密码界面1（验证电子邮箱）
public class ResetPasswordScreen1 extends AppCompatActivity {
    public TextInputLayout til_input_email, til_rp_input_captcha;
    public AppCompatEditText input_email, input_captcha;
    public Button get_captcha, next;
    public String email, captcha;
    public Context context = ResetPasswordScreen1.this;
    public CountDownTimerUtils mCountDownTimerUtils;
    public static ResetPasswordScreen1 THIS = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_reset_password_name);
        super.onCreate(savedInstanceState);

        THIS = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reset_password_1);

        til_input_email = findViewById(R.id.til_rp_input_email);
        til_rp_input_captcha = findViewById(R.id.til_rp_input_captcha);
        input_email = findViewById(R.id.rp_input_email);
        input_captcha = findViewById(R.id.rp_input_captcha);
        get_captcha = findViewById(R.id.rp_get_captcha);
        next = findViewById(R.id.rp_next);


        mCountDownTimerUtils = new CountDownTimerUtils(context, get_captcha, Variables.GET_CAPTCHA_TIME, 1000);
        if (!Utils.networkAvailableDialog(context)) {
            get_captcha.setEnabled(false);
        } else {
            if (TextUtils.isEmpty(input_email.getText().toString())) {
                get_captcha.setEnabled(false);
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
                        Snackbar.make(get_captcha, getResources().getString(R.string.toast_successfully_got_captcha), Snackbar.LENGTH_SHORT).show();
                        email = input_email.getText().toString();
                        input_email.setEnabled(false);
                        mCountDownTimerUtils.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_get_captcha));
                        Snackbar.make(get_captcha, getResources().getString(R.string.toast_failed_get_captcha), Snackbar.LENGTH_SHORT).show();
                        get_captcha.setEnabled(true);
                    }

                } else {
                    Snackbar.make(get_captcha, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context)) {
                    if (TextUtils.isEmpty(input_email.getText().toString()) || TextUtils.isEmpty(input_captcha.getText().toString())) {
                        Snackbar.make(get_captcha, getResources().getString(R.string.toast_input_content_empty), Snackbar.LENGTH_SHORT).show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (input_captcha.getText().toString().equals(captcha)) {
                                    AccountUtils mysqlUtils = Utils.getAccountUtils();
                                    try {
                                        if (mysqlUtils.tryLoginWithoutPasswordNoThreadAndDialog(context, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY))) {
                                            //ResetPasswordScreen2.email = email;
                                            //Utils.startActivity(context, ResetPasswordScreen2.class);
                                            Intent intent = new Intent(context, ResetPasswordScreen2.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                        } else {
                                            Snackbar.make(get_captcha, getResources().getString(R.string.toast_account_not_exist), Snackbar.LENGTH_SHORT).show();
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
                                    Snackbar.make(get_captcha, getResources().getString(R.string.toast_captcha_incorrect), Snackbar.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            }
                        }).start();


                    }
                } else {
                    Snackbar.make(get_captcha, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
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
                finish();
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
