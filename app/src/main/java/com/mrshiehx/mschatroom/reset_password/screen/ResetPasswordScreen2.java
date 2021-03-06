package com.mrshiehx.mschatroom.reset_password.screen;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.account.information.storage.storagers.AccountInformationStorager;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.KeyboardUtils;
import com.mrshiehx.mschatroom.utils.Utils;

//重置密码界面2（输入新密码）
public class ResetPasswordScreen2 extends AppCompatActivity {
    TextInputLayout til_rp_input_new_password, til_rp_input_confirm_password;
    AppCompatEditText input_new_password, input_confirm_password;
    Button reset_password;
    String email;
    CheckBox show_password;
    Context context = ResetPasswordScreen2.this;
    ProgressDialog resetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_reset_password_name);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reset_password_2);

        til_rp_input_new_password = findViewById(R.id.til_rp_input_new_password);
        til_rp_input_confirm_password = findViewById(R.id.til_rp_input_confirm_password);
        reset_password = findViewById(R.id.rp_reset_password);
        input_new_password = findViewById(R.id.rp_input_new_password);
        input_confirm_password = findViewById(R.id.rp_input_confirm_password);
        show_password = findViewById(R.id.rp_show_password);

        email = getIntent().getStringExtra("email");

        input_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input_new_password.addTextChangedListener(checkPasswordsTextWatcher);
        input_confirm_password.addTextChangedListener(checkPasswordsTextWatcher);
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    input_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    input_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    input_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    input_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                input_new_password.setSelection(input_new_password.getText().length());
                input_confirm_password.setSelection(input_confirm_password.getText().length());
            }
        });
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.disappearKeybaroad(ResetPasswordScreen2.this);
                if (Utils.isNetworkConnected(context)) {
                    if (TextUtils.isEmpty(input_new_password.getText()) || TextUtils.isEmpty(input_confirm_password.getText())) {
                        Snackbar.make(reset_password, getResources().getString(R.string.toast_input_content_empty), Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (input_new_password.getText().toString().equals(input_confirm_password.getText().toString()) == false) {
                            input_confirm_password.setError(getResources().getString(R.string.aceterror_password_different));
                        } else if (input_new_password.getText().toString().equals(input_confirm_password.getText().toString()) == true) {
                            if (input_new_password.length() < 8 || input_new_password.length() > 16) {
                            } else {
                                resetting = new ProgressDialog(context);
                                //resetting.setTitle(getResources().getString(R.string.dialog_title_wait));
                                resetting.setMessage(getResources().getString(R.string.dialog_resetting_message));
                                resetting.setCancelable(false);
                                resetting.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        try {
                                            String password = input_new_password.getText().toString();
                                            AccountUtils ud = Utils.getAccountUtils();
                                            String oldPassword="";
                                            try{
                                                oldPassword=ud.getString(context,"password",AccountUtils.BY_EMAIL,EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY));
                                                //oldPassword=EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(AccountInformationStorager.getMainAccountAndPassword()).split(Variables.SPLIT_SYMBOL)[1]);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            int result = 0;
                                            try {
                                                result = ud.resetPassword(context, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(email, Variables.TEXT_ENCRYPTION_KEY),oldPassword, EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (result <= 0) {
                                                resetting.dismiss();
                                                Snackbar.make(reset_password, getResources().getString(R.string.toast_failed_reset_password), Snackbar.LENGTH_SHORT).show();
                                            } else {
                                                resetting.dismiss();
                                                Snackbar.make(reset_password, getResources().getString(R.string.toast_successfully_reset_password), Snackbar.LENGTH_SHORT).show();
                                                LoginScreen.can_i_back = false;
                                                Utils.startActivity(context, LoginScreen.class);
                                            }
                                        } catch (Exception e) {
                                            resetting.dismiss();
                                            e.printStackTrace();
                                            Snackbar.make(reset_password, getResources().getString(R.string.toast_failed_reset_password), Snackbar.LENGTH_SHORT).show();
                                            Utils.exceptionDialog(context, e, getResources().getString(R.string.toast_failed_reset_password));
                                        }


                                        Looper.loop();
                                    }
                                }).start();

                            }


                        }
                    }
                } else {
                    Snackbar.make(reset_password, getResources().getString(R.string.toast_please_check_your_network), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }


    private TextWatcher checkPasswordsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Utils.valueOf(input_new_password.getText()).equals(Utils.valueOf(input_confirm_password.getText())) == false) {
                //Snackbar.make(context, getResources().getString(R.string.toast_password_different), Snackbar.LENGTH_SHORT).show();
                input_confirm_password.setError(getResources().getString(R.string.aceterror_password_different));
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Utils.valueOf(input_new_password.getText()).equals(Utils.valueOf(input_confirm_password.getText())) == false) {
                input_confirm_password.setError(getResources().getString(R.string.aceterror_password_different));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (Utils.valueOf(input_new_password.getText()).equals(Utils.valueOf(input_confirm_password.getText())) == false) {
                input_confirm_password.setError(getResources().getString(R.string.aceterror_password_different));
            } else {
                if (editable.length() < 8 || editable.length() > 16) {
                    input_new_password.setError(getResources().getString(R.string.aceterror_password_insufficient_length_or_too_long));
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ResetPasswordScreen1.THIS.finish();
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
                ResetPasswordScreen1.THIS.finish();
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