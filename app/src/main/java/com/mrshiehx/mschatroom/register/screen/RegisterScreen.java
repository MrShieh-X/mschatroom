package com.mrshiehx.mschatroom.register.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.login.by_ec.screen.LoginByEmailAndCAPTCHA;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.reset_password.screen.ResetPasswordScreen1;
import com.mrshiehx.mschatroom.utils.CountDownTimerUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.KeyboardUtils;
import com.mrshiehx.mschatroom.utils.SendEmailUtils;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//注册界面
public class RegisterScreen extends AppCompatActivity {
    //public TextInputLayout til_reg_input_email,til_reg_input_account,til_reg_input_password,til_reg_input_confirm_password,til_reg_input_captcha;

    public AppCompatEditText reg_input_email, reg_input_account, reg_input_password, reg_input_confirm_password, reg_input_captcha;
    public Button button_get_captcha, go_to_login, go_to_reset_password, register, reinput_email;
    public static boolean can_i_back;
    public TextView password_different, input_content_empty;
    public CheckBox show_password;
    Context context = RegisterScreen.this;
    String captcha="thecaptcha";
    int captchaLength = 6;
    String email;
    ProgressDialog registering;
    //int captchaTime=61;
    //final Timer timer = new Timer();
    //public static int time = 60;
    //public boolean gettingCaptcha=false;
    CountDownTimerUtils mCountDownTimerUtils;
    private long firstTime = 0;
    private View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            KeyboardUtils.disappearKeybaroad(RegisterScreen.this);
            if (TextUtils.isEmpty(Utils.valueOf(reg_input_account.getText())) || TextUtils.isEmpty(reg_input_email.getText().toString()) || TextUtils.isEmpty(reg_input_password.getText().toString()) || TextUtils.isEmpty(reg_input_confirm_password.getText().toString()) || TextUtils.isEmpty(reg_input_captcha.getText().toString())) {
                input_content_empty.setVisibility(View.VISIBLE);
            } else {
                input_content_empty.setVisibility(View.GONE);
                if (Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password.getText())) == false) {
                    password_different.setVisibility(View.VISIBLE);
                } else if (Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password.getText())) == true) {
                    password_different.setVisibility(View.GONE);
                    if (reg_input_password.length() < 8 || reg_input_password.length() > 16) {

                    } else {
                        if (Utils.valueOf(reg_input_captcha.getText()).equals(captcha) == true) {
                            if (reg_input_account.getText().length() < 4 || reg_input_account.getText().length() > 20) {
                                Snackbar.make(register, getResources().getString(R.string.toast_account_insufficient_length_or_too_long), Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (Utils.isNetworkConnected(context)) {
                                    registering = new ProgressDialog(context);
                                    try {
                                        //Register
                                        //String insertSql = "insert into userInfo (email,account,password) values ('" + email + "','" + reg_input_account.getText().toString() + "','" + reg_input_password.getText().toString() + "');";
                                        //Snackbar.make(context, "captcha right", Snackbar.LENGTH_SHORT).show();
                                        //sQLiteOpenHelper = new CRSQLiteOpenHelper(context, "mydb.db", null, 1);
                                        // database = sQLiteOpenHelper.getWritableDatabase();
                                        //database.execSQL(createTableSql);
                                        //database.execSQL(insertSql);
                                        //registering.setTitle(getResources().getString(R.string.dialog_title_wait));
                                        registering.setMessage(getResources().getString(R.string.dialog_registering_message));
                                        registering.setCancelable(false);
                                        registering.show();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Looper.prepare();
                                                String account = reg_input_account.getText().toString().toLowerCase();
                                                AccountUtils mysqlUtils = Utils.getAccountUtils();
                                                //Boolean resultAccount = registerAndLogin.loginByEmail(context, account, password);
                                                //Boolean resultEmail = registerAndLogin.loginByEmail(context, email, password);
                                                //String emaila=reg_input_email.getText().toString();
                                                try {
                                                    if (mysqlUtils.tryLoginWithoutPassword(context, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY)) == true) {
                                                        //邮箱已存在
                                                        Snackbar.make(register, getResources().getString(R.string.toast_registered_email), Snackbar.LENGTH_LONG).show();
                                                    } else if (mysqlUtils.tryLoginWithoutPassword(context, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY)) == false) {
                                                        if (mysqlUtils.tryLoginWithoutPassword(context, AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(account, Variables.TEXT_ENCRYPTION_KEY)) == true) {
                                                            //账号已存在
                                                            Snackbar.make(register, getResources().getString(R.string.toast_registered_account), Snackbar.LENGTH_SHORT).show();
                                                        } else if (mysqlUtils.tryLoginWithoutPassword(context, AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(account, Variables.TEXT_ENCRYPTION_KEY)) == false) {
                                                            //Real register
                                                            String accountR = Utils.valueOf(reg_input_account.getText()).toLowerCase();
                                                            String passwordR = Utils.valueOf(reg_input_password.getText());
                                                            AccountUtils mysqlUtils2 = Utils.getAccountUtils();
                                                            int result = 0;
                                                            try {
                                                                result = mysqlUtils2.register(context, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountR, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(passwordR, Variables.TEXT_ENCRYPTION_KEY));
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
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                                Snackbar.make(register, getResources().getString(R.string.toast_failed_register), Snackbar.LENGTH_SHORT).show();
                                                                Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_register));
                                                            }
                                                            registering.dismiss();
                                                            if (result <= 0) {
                                                                Snackbar.make(register, getResources().getString(R.string.toast_failed_register), Snackbar.LENGTH_SHORT).show();
                                                            } else {
                                                                Snackbar.make(register, getResources().getString(R.string.toast_successfully_registered), Snackbar.LENGTH_SHORT).show();
                                                                Utils.startActivity(context, LoginScreen.class);
                                                                LoginScreen.can_i_back = true;
                                                            }
                                                        /*Looper.prepare();
                                                        Snackbar.make(context, Utils.valueOf(result), Snackbar.LENGTH_SHORT).show();*/

                                                        }

                                                    }
                                                    registering.dismiss();
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
                                                Looper.loop();
                                            }
                                        }).start();
                                    } catch (Exception e) {
                                        registering.dismiss();
                                        e.printStackTrace();
                                        Snackbar.make(register, getResources().getString(R.string.toast_failed_register), Snackbar.LENGTH_SHORT).show();
                                        Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_register));
                                    }
                                } else {
                                    Snackbar.make(register, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Snackbar.make(register, getResources().getString(R.string.toast_captcha_incorrect), Snackbar.LENGTH_SHORT).show();
                            //Snackbar.make(context, "captcha notright", Snackbar.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_register_screen_name);
        super.onCreate(savedInstanceState);

        if (can_i_back == true) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_register);

        /*til_reg_input_email=findViewById(R.id.til_reg_input_email);
        til_reg_input_account=findViewById(R.id.til_reg_input_account);
        til_reg_input_password=findViewById(R.id.til_reg_input_password);
        til_reg_input_confirm_password=findViewById(R.id.til_reg_input_confirm_password);
        til_reg_input_captcha=findViewById(R.id.til_reg_input_captcha);
        */
        reg_input_email = findViewById(R.id.reg_input_email);
        reg_input_account = findViewById(R.id.reg_input_account);
        show_password = findViewById(R.id.reg_show_password);
        reg_input_password = findViewById(R.id.reg_input_password);
        reg_input_confirm_password = findViewById(R.id.reg_input_confirm_password);
        reg_input_captcha = findViewById(R.id.reg_input_captcha);
        button_get_captcha = findViewById(R.id.get_captcha);
        go_to_login = findViewById(R.id.go_to_login);
        go_to_reset_password = findViewById(R.id.go_to_reset_password);
        register = findViewById(R.id.register);
        password_different = findViewById(R.id.password_different);
        input_content_empty = findViewById(R.id.reg_input_content_empty);
        reinput_email = findViewById(R.id.reinput_email);


        /*if (!Utils.networkAvailableDialog(context)) {
            button_get_captcha.setEnabled(false);
            //register.setEnabled(false);
        } else {
            if (TextUtils.isEmpty(Utils.valueOf(reg_input_email.getText()))) {
                button_get_captcha.setEnabled(false);
            }
        }*/


        reinput_email.setEnabled(false);
        mCountDownTimerUtils = new CountDownTimerUtils(context, button_get_captcha, Variables.GET_CAPTCHA_TIME, 1000);

        reg_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        reg_input_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password_different.setVisibility(View.GONE);
        button_get_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnected(context)) {
                    captcha = Utils.newRandomNumber(captchaLength);
                    //Get captcha
                    new Thread(()->{
                        Looper.prepare();
                        try {
                            //gettingCaptcha=true;
                            runOnUiThread(()->button_get_captcha.setEnabled(false));
                            SendEmailUtils sendEmail = new SendEmailUtils(Utils.valueOf(reg_input_email.getText()).toLowerCase());
                            sendEmail.sendCaptcha(captcha);
                            runOnUiThread(()->Snackbar.make(register, getResources().getString(R.string.toast_successfully_got_captcha), Snackbar.LENGTH_SHORT).show());
                            email = reg_input_email.getText().toString().toLowerCase();
                            runOnUiThread(()->reg_input_email.setEnabled(false));
                            runOnUiThread(()->reinput_email.setEnabled(true));
                            //timer.schedule(captchaTimeDynamic, 0, 1000);
                            //CaptchaTimeDynamic ctd=new CaptchaTimeDynamic("CaptchaTimeDynamic");
                            //ctd.start();

                            runOnUiThread(()->mCountDownTimerUtils.start());
                        } catch (Exception e) {
                            runOnUiThread(()->Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_get_captcha)));
                            e.printStackTrace();
                            runOnUiThread(()->Snackbar.make(register, getResources().getString(R.string.toast_failed_get_captcha), Snackbar.LENGTH_SHORT).show());
                            runOnUiThread(()->button_get_captcha.setEnabled(true));
                        }
                        Looper.loop();
                    }).start();

                } else {
                    Snackbar.make(register, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginScreen.can_i_back = true;
                Utils.startActivity(context, LoginScreen.class);
            }
        });
        go_to_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.startActivity(context, ResetPasswordScreen1.class);
            }
        });
        register.setOnClickListener(registerOnClickListener);

        reg_input_password.addTextChangedListener(checkPasswordsTextWatcher);
        reg_input_confirm_password.addTextChangedListener(checkPasswordsTextWatcher);
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (show_password.isChecked() == true) {
                    reg_input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reg_input_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //reg_input_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //reg_input_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //reg_input_password.setSelection(reg_input_password.getText().length());
                    //reg_input_confirm_password.setSelection(reg_input_confirm_password.getText().length());
                    //reg_input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                    //reg_input_confirm_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                } else {
                    reg_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reg_input_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //reg_input_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //reg_input_confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //reg_input_password.setSelection(reg_input_password.getText().length());
                    //reg_input_confirm_password.setSelection(reg_input_confirm_password.getText().length());
                    //reg_input_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                    //reg_input_confirm_password.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm1234567890_QWERTYUIOPASDFGHJKLZXCVBNM"));
                }
                reg_input_password.setSelection(reg_input_password.getText().length());
                reg_input_confirm_password.setSelection(reg_input_confirm_password.getText().length());
            }
        });
        /*Utils.inputFilterSpace(reg_input_email);
        Utils.setEditTextInhibitInputSpeChat(reg_input_email);
        Utils.inputFilterSpace(reg_input_account);
        Utils.setEditTextInhibitInputSpeChat(reg_input_account);
        Utils.inputFilterSpace(reg_input_password);
        Utils.setEditTextInhibitInputSpeChat(reg_input_password);
        Utils.inputFilterSpace(reg_input_confirm_password);
        Utils.setEditTextInhibitInputSpeChat(reg_input_confirm_password);
        Utils.inputFilterSpace(reg_input_captcha);
        Utils.setEditTextInhibitInputSpeChat(reg_input_captcha);*/
        button_get_captcha.setEnabled(false);
        reg_input_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isEmail(Utils.valueOf(reg_input_email.getText())) && charSequence.length() > 0 == true) {
                        button_get_captcha.setEnabled(!mCountDownTimerUtils.isRunning());
                } else {
                    button_get_captcha.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isEmail(Utils.valueOf(reg_input_email.getText())) && charSequence.length() > 0) {
                        button_get_captcha.setEnabled(!mCountDownTimerUtils.isRunning());
                } else {
                    button_get_captcha.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Utils.isEmail(Utils.valueOf(reg_input_email.getText())) && editable.length() > 0 == true) {
                    button_get_captcha.setEnabled(!mCountDownTimerUtils.isRunning());
                } else {
                    reg_input_email.setError(getResources().getString(R.string.aceterror_invalid_email));
                    button_get_captcha.setEnabled(false);
                }
            }
        });
        reinput_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCountDownTimerUtils.cancel();
                //button_get_captcha.setText(getResources().getString(R.string.button_get_captcha));
                //button_get_captcha.setEnabled(true);
                reg_input_email.setEnabled(true);
                reinput_email.setEnabled(false);
                reg_input_email.requestFocus();
            }
        });
    }

    private TextWatcher checkPasswordsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password))) {
                password_different.setVisibility(View.VISIBLE);
            } else if (Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password.getText()))) {
                password_different.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password.getText())) == false) {
                password_different.setVisibility(View.VISIBLE);
            } else if (Utils.valueOf(reg_input_password.getText()).equals(Utils.valueOf(reg_input_confirm_password.getText())) == true) {
                password_different.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (reg_input_password.getText().toString().equals(reg_input_confirm_password.getText().toString()) == false) {
                password_different.setVisibility(View.VISIBLE);
            } else if (reg_input_password.getText().toString().equals(reg_input_confirm_password.getText().toString())) {
                password_different.setVisibility(View.GONE);
                if (editable.length() < 8 || editable.length() > 16) {
                    reg_input_password.setError(getResources().getString(R.string.aceterror_password_insufficient_length_or_too_long));
                }
            }
        }
    };

    /*TimerTask captchaTimeDynamic=new TimerTask() {
        @Override
        public void run() {
            if(button_get_captcha.getText().toString().equals(getResources().getString(R.string.button_get_captcha))){
                //int captchaTimeDynamic=captchaTime-1;
                captchaTime=captchaTime-1;
                //int captchaTimeDynamicX=captchaTimeDynamic-1;
                button_get_captcha.setText(button_get_captcha.getText().toString()+"("+captchaTime+")");
                //if(captchaTimeDynamic==0){
                //button_get_captcha.setEnabled(true);
                //button_get_captcha.setText(getResources().getString(R.string.button_get_captcha));
                //timer.cancel();
                //}
            }else{
                int x=captchaTime-1;
                button_get_captcha.setText(getResources().getString(R.string.button_get_captcha)+"("+x+")");
                if(captchaTime==0){
                    button_get_captcha.setEnabled(true);
                    button_get_captcha.setText(getResources().getString(R.string.button_get_captcha));
                    timer.cancel();
                }
            }
        }
    };
*/
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
    /*private class CaptchaTimeDynamic extends Thread {

        CaptchaTimeDynamic(String name) {
            super(name);
        }

        @Override
        public void run() {
            timer.schedule(captchaTimeDynamic,0,1000);
        }
    }*/

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

