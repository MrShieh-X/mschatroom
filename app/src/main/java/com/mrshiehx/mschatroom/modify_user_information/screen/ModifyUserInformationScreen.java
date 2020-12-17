package com.mrshiehx.mschatroom.modify_user_information.screen;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.StartScreen;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*修改用户信息界面*/
public class ModifyUserInformationScreen extends AppCompatPreferenceActivity {
    Context context = ModifyUserInformationScreen.this;
    Preference avatarP, gender, name, whatsup, delete_account, account, account_email;
    public static final int TAKE_CAMERA = 101;
    public static final int RC_CHOOSE_PHOTO = 102;
    private Uri imageUri;
    Bitmap avatarBitmap;
    InputStream avatarIs;
    ProgressDialog uploading, gettingAvatar, loggingIn;
    public static String password = null;
    String tempAvatarName = "temp_avatar.png";
    InputStream avatarInputStream;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean passwordCanUse;
    InputStream informationFile;
    String accountNameContent;
    String accountGenderContent;
    String accountWhatSUpContent;
    List<UserInformation> userInformationList = null;
    int accountNameIndex = 0;
    int accountGenderIndex = 1;
    int accountWhatSUpIndex = 2;
    //boolean canLogin;
    int genderChoice;
    int nowGender = -1;
    AlertDialog.Builder genderChoiceDialog;
    EditText nameET, whatsupET;
    String tNameET, tWhatsupET;
    String accountString, emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.initialization(this, R.string.activity_modify_user_information_screen_name);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_modify_user_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uploading = new ProgressDialog(context);
        gettingAvatar = new ProgressDialog(context);
        avatarP = getPreferenceScreen().findPreference("account_avatar");
        name = getPreferenceScreen().findPreference("account_name");
        whatsup = getPreferenceScreen().findPreference("account_whatsup");
        gender = getPreferenceScreen().findPreference("account_gender");
        delete_account = getPreferenceScreen().findPreference("delete_account");
        account = getPreferenceScreen().findPreference("account");
        account_email = getPreferenceScreen().findPreference("account_email");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!Utils.networkAvailableDialog(context)) {
            avatarP.setEnabled(false);
            name.setEnabled(false);
            whatsup.setEnabled(false);
            gender.setEnabled(false);
            delete_account.setEnabled(false);
            account.setEnabled(false);
            account_email.setEnabled(false);
        }
        uploading = new ProgressDialog(context);
        uploading.setCancelable(false);
        uploading.setTitle(getResources().getString(R.string.dialog_title_wait));
        uploading.setMessage(getResources().getString(R.string.dialog_uploading_message));
        gettingAvatar = new ProgressDialog(context);
        gettingAvatar.setCancelable(false);
        gettingAvatar.setTitle(getResources().getString(R.string.dialog_title_wait));
        gettingAvatar.setMessage(getResources().getString(R.string.dialog_downloading_avatar));/*gettingAvatar.show();*/
        //canLogin = Utils.checkLoginInformationAndNetwork(context);
        /*try {
            if (canLogin||Variables.INT_FOR_MUIS<3) {
                if (initPassword()) {
                    initAccountAndEmail();
                    initInformations();/*initAvatar();*
                } else {
                    delete_account.setEnabled(false);
                }
            }else{
                finish();
                Utils.startActivity(context,ModifyUserInformationScreen.class);
                Variables.INT_FOR_MUIS++;
            }
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }*/
        loggingIn = new ProgressDialog(context);
        loggingIn.setTitle(getResources().getString(R.string.dialog_title_wait));
        loggingIn.setMessage(getResources().getString(R.string.dialog_loggingIn_message));
        loggingIn.setCancelable(false);
        loggingIn.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Utils.checkLoginInformationAndNetwork(context)) {
                    try {
                        if (initPassword()) {
                            initAccountAndEmail();
                            initInformations();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    delete_account.setEnabled(false);
                                }
                            });
                        }
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            delete_account.setEnabled(false);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loggingIn.dismiss();
                    }
                });
                Looper.loop();
            }
        }).start();
        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!TextUtils.isEmpty(account.getSummary().toString())){
                    Utils.copy(context,account.getSummary().toString());
                    Toast.makeText(context, getString(R.string.toast_account_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        account_email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!TextUtils.isEmpty(account_email.getSummary().toString())){
                    Utils.copy(context,account_email.getSummary().toString());
                    Toast.makeText(context, getString(R.string.toast_email_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        avatarP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.dialog_choose_photo_title)).setMessage(getResources().getString(R.string.dialog_choose_photo_message)).setPositiveButton(getResources().getString(android.R.string.cancel), null).setNegativeButton(getResources().getString(R.string.dialog_choose_photo_button_select), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (initPassword()) {
                                Intent intentToPickPic = new Intent(Intent.ACTION_GET_CONTENT, null);
                                intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
                                uploading.show();
                                gettingAvatar.show();
                            }
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }

                    }
                }).setNeutralButton(getResources().getString(R.string.dialog_choose_photo_button_take), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (initPassword()) {
                                File outputImage = new File(getExternalCacheDir(), tempAvatarName);
                                try {
                                    if (outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    imageUri = FileProvider.getUriForFile(context, "com.mrshiehx.pickphoto.fileprovider", outputImage);
                                } else {
                                    imageUri = Uri.fromFile(outputImage);
                                }

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, TAKE_CAMERA);

                                uploading.show();
                                gettingAvatar.show();
                            }
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();/*}*/
                return true;
            }
        });
        gender.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] items = {getResources().getString(R.string.preference_account_gender_male), getResources().getString(R.string.preference_account_gender_female)};
                genderChoice = -1;
                genderChoiceDialog = new AlertDialog.Builder(context);
                genderChoiceDialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
                genderChoiceDialog.setTitle(getResources().getString(R.string.preference_account_gender_dialogtitle)).setSingleChoiceItems(items, nowGender,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        //int which2=which;
                                        if (genderChoice != -1) {
                                            //Utils.dynamicModifyETSummary(whatsup,getResources().getString(R.string.preference_account_whatsup_summary));
                                            if (Utils.checkLoginInformationAndNetwork(context)) {
                                                String gen = "male";
                                                if (which == 0) {
                                                    gen = "male";
                                                } else if (which == 1) {
                                                    gen = "female";
                                                }
                                                if (uploadInformation(accountNameContent, gen, accountWhatSUpContent) != 0) {
                                                    if (which != -1) {
                                                        accountGenderContent = gen;
                                                        final int finalWhich = which;
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                gender.setSummary(items[finalWhich]);
                                                            }
                                                        });
                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    //which = -1;
                                                    nowGender = -1;
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                                                        }
                                                    });
                                                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                //which = -1;
                                                nowGender = -1;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                                                    }
                                                });
                                            }
                                        }
                                        Looper.loop();
                                    }
                                }).start();

                                genderChoice = which;
                                nowGender = which;
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });
        name.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                nameET = new EditText(context);
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.preference_account_name_title)).setView(nameET).setNegativeButton(context.getResources().getString(android.R.string.cancel), null).setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (Utils.checkLoginInformationAndNetwork(context)) {
                                    if (uploadInformation(nameET.getText().toString(), accountGenderContent, accountWhatSUpContent) != 0) {
                                        accountNameContent = nameET.getText().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                name.setSummary(nameET.getText().toString());
                                            }
                                        });
                                        tNameET = nameET.getText().toString();
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                                            }
                                        });
                                        tNameET = null;
                                        Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    tNameET = null;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                                        }
                                    });

                                }
                                Looper.loop();
                            }
                        }).start();

                    }
                });
                inputDialog.show();
                if (!TextUtils.isEmpty(tNameET)) nameET.setText(tNameET);
                nameET.setSelection(nameET.getText().toString().length());
                return true;
            }
        });
        whatsup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                whatsupET = new EditText(context);
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.preference_account_whatsup_title)).setView(whatsupET).setNegativeButton(context.getResources().getString(android.R.string.cancel), null).setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (Utils.checkLoginInformationAndNetwork(context)) {
                                    if (uploadInformation(accountNameContent, accountGenderContent, whatsupET.getText().toString()) != 0) {
                                        accountWhatSUpContent = whatsupET.getText().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                whatsup.setSummary(whatsupET.getText().toString());
                                            }
                                        });
                                        tWhatsupET = whatsupET.getText().toString();
                                    } else {
                                        tWhatsupET = null;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                                            }
                                        });
                                        Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    tWhatsupET = null;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                                        }
                                    });

                                }
                                Looper.loop();
                            }
                        }).start();

                    }
                });
                inputDialog.show();
                if (!TextUtils.isEmpty(tWhatsupET)) whatsupET.setText(tWhatsupET);
                whatsupET.setSelection(whatsupET.getText().toString().length());
                return true;
            }
        });
        delete_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {/*if (Utils.checkLoginInformationAndNetwork(context, false)) {*/
                final EditText et = new EditText(context);
                AlertDialog.Builder d = new AlertDialog.Builder(context).setView(et).setTitle(getResources().getString(R.string.preference_delete_account_input_email)).setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (Utils.checkLoginInformationAndNetwork(context)) {
                                    final AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                                    final Boolean[] ok = new Boolean[1];
                                    int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
                                    if (loginMethod == 0) {
                                        String injmpw = "";
                                        try {
                                            injmpw = EnDeCryptTextUtils.encrypt(et.getText().toString(), Variables.TEXT_ENCRYPTION_KEY);
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
                                        String acoe = "";
                                        if (loginMethod == 0) {
                                            try {
                                                acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                        } else {
                                            try {
                                                acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                        try {
                                            if (au.login(context, new ProgressDialog(context), AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw)) {
                                                showDeleteAccountDialog(et);
                                            } else {
                                                if (loginMethod == 0) {
                                                    Toast.makeText(context, getResources().getString(R.string.toast_account_or_password_incorrect), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, getResources().getString(R.string.toast_email_or_password_incorrect), Toast.LENGTH_SHORT).show();
                                                }
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
                                        String injmpw = "";
                                        try {
                                            injmpw = EnDeCryptTextUtils.encrypt(et.getText().toString(), Variables.TEXT_ENCRYPTION_KEY);
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

                                        String acoe = "";
                                        if (loginMethod == 0) {
                                            try {
                                                acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                        } else {
                                            try {
                                                acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                        try {
                                            if (au.login(context, new ProgressDialog(context), AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw)) {
                                                showDeleteAccountDialog(et);
                                            } else {
                                                if (loginMethod == 0) {
                                                    Toast.makeText(context, getResources().getString(R.string.toast_account_or_password_incorrect), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, getResources().getString(R.string.toast_email_or_password_incorrect), Toast.LENGTH_SHORT).show();
                                                }
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
                                    }
                                }
                                Looper.loop();
                            }
                        }).start();

                    }
                }).setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
                d.show();
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());/*}*/
                return true;
            }
        });


    }

    void initAccountAndEmail() {
        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
        if (loginMethod == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account.setSummary(finalAccountClean);
                            accountString = finalAccountClean;
                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account_email.setSummary(finalEmailClean);
                            emailString = finalEmailClean;
                        }
                    });
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account_email.setSummary(finalEmailClean);
                            emailString = finalEmailClean;
                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account.setSummary(finalAccountClean);
                            accountString = finalAccountClean;
                        }
                    });
                }
            }).start();
        }
    }

    void showDeleteAccountDialog(final EditText et) {
        Utils.showDialog(context, getResources().getString(R.string.dialog_title_notice), getResources().getString(R.string.dialog_delete_account_message), getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog deleting = new ProgressDialog(context);
                deleting.setTitle(getResources().getString(R.string.dialog_title_wait));
                deleting.setMessage(getResources().getString(R.string.dialog_deleting_message));
                deleting.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String injmpw = "";
                        try {
                            injmpw = EnDeCryptTextUtils.encrypt(et.getText().toString(), Variables.TEXT_ENCRYPTION_KEY);
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
                        String acoe = "";
                        int loginMethod = sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0);
                        if (loginMethod == 0)
                            try {
                                acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                        else try {
                            acoe = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                        AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                        int sta = -1;
                        if (loginMethod == 0) try {
                            sta = au.delectAccount(context, deleting, AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw);
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
                        else try {
                            sta = au.delectAccount(context, deleting, AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw);
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
                        if (sta != 0) {
                            Looper.prepare();
                            Toast.makeText(ModifyUserInformationScreen.this, getResources().getString(R.string.toast_successfully_deleted_account), Toast.LENGTH_SHORT).show();
                            try {
                                editor.remove(Variables.SHARED_PREFERENCE_IS_LOGINED).remove(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD).remove(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD).remove(Variables.SHARED_PREFERENCE_LOGIN_METHOD).commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            finish();
                            Utils.startActivity(context, StartScreen.class);
                            Looper.loop();
                        } else {
                            Looper.prepare();
                            Toast.makeText(ModifyUserInformationScreen.this, getResources().getString(R.string.toast_failed_to_delete_account), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });
    }

    void initInformations() {
        initUserInformationFile();
        initAvatar();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (informationFile != null) {
                    initUserInformationContents();
                    timer.cancel();
                }
            }
        }, 0, 100);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(accountNameContent) || !TextUtils.isEmpty(accountGenderContent) || !TextUtils.isEmpty(accountWhatSUpContent)) {
                    initUserInformations();
                    timer2.cancel();
                }
            }
        }, 0, 100);
    }

    int RETURN = 1;

    int uploadInformation(final String name, final String gender, final String whatSUp) {
        uploading.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in;
                InputStream newI = null;
                try {
                    in = context.getResources().getAssets().open("userInformation.xml");/*newI = Utils.replaceUserInformationContents(in, name, gender, whatSUp);*/
                    newI = Utils.replaceUserInformationContents(in, name, gender, whatSUp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                ;
                int s = 0;
                try {
                    s = accountUtils.uploadUserInformation(context, uploading, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY), newI);
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
                if (s != 0) {
                    Looper.prepare();
                    Toast.makeText(context, getResources().getString(R.string.toast_successfully_to_upload_userinformation), Toast.LENGTH_SHORT).show();
                    RETURN = s;
                    Looper.loop();
                } else {
                    Looper.prepare();
                    RETURN = s;
                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_upload_userinformation), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
        return RETURN;
    }

    void initUserInformations() {
        Looper.prepare();
        if (!TextUtils.isEmpty(accountGenderContent)) if (accountGenderContent.equals("male")) {
            nowGender = 0;
            genderChoice = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gender.setSummary(getResources().getString(R.string.preference_account_gender_male));
                }
            });/*Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();*/
        } else if (accountGenderContent.equals("female")) {
            nowGender = 1;
            genderChoice = 1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gender.setSummary(getResources().getString(R.string.preference_account_gender_female));
                }
            });/*Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();*/
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_gender), Toast.LENGTH_SHORT).show();
            nowGender = -1;
            genderChoice = -1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                }
            });
        }
        else {
            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_gender), Toast.LENGTH_SHORT).show();
            nowGender = -1;
            genderChoice = -1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                }
            });
        }
        if (!TextUtils.isEmpty(accountNameContent)) {
            tNameET = accountNameContent;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    name.setSummary(accountNameContent);
                }
            });
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_name), Toast.LENGTH_SHORT).show();
            tNameET = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                }
            });
        }
        if (!TextUtils.isEmpty(accountWhatSUpContent)) {
            tWhatsupET = accountWhatSUpContent;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    whatsup.setSummary(accountWhatSUpContent);
                }
            });
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_whatsup), Toast.LENGTH_SHORT).show();
            tWhatsupET = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                }
            });
        }
        Looper.loop();
    }

    public void initUserInformationFile() {
        if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0)
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account.setSummary(finalAccountClean);
                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            account_email.setSummary(finalEmailClean);
                        }
                    });
                    emailString = finalEmailClean;
                    try {
                        //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                        informationFile = accountUtils.getUserInformation(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Looper.prepare();
                        Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
                        Looper.loop();
                        e.printStackTrace();
                    }
                }
            }).start();
        else new Thread(new Runnable() {
            @Override
            public void run() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        account_email.setSummary(finalEmailClean);
                    }
                });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        account.setSummary(finalAccountClean);
                    }
                });
                accountString = finalAccountClean;
                //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                try {
                    informationFile = accountUtils.getUserInformation(context, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY));
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
        }).start();
    }

    public void initUserInformationContents() {
        Looper.prepare();
        if (informationFile == null) {
            Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
        } else {
            userInformationList = XMLUtils.readXmlBySAX(informationFile);
            if (userInformationList != null) {
                accountNameContent = userInformationList.get(accountNameIndex).getNameContent();
                accountGenderContent = userInformationList.get(accountGenderIndex).getGenderContent();
                accountWhatSUpContent = userInformationList.get(accountWhatSUpIndex).getWhatsupContent();
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            }
        }
        Looper.loop();
    }

    void initAvatar() {
        try {
            if (initPassword())
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                            Looper.prepare();
                            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        if (avatarInputStream != null) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                avatarP.setIcon(FormatTools.getInstance().InputStream2Drawable(avatarInputStream));
                            }
                        });
                        else {
                            Looper.prepare();
                            Toast.makeText(context, getResources().getString(R.string.toast_tip_set_avatar), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    boolean initPassword() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false))
            if (sharedPreferences.getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
                password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                passwordCanUse = true;
                return true;
            } else {
                password = EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1];
                passwordCanUse = true;
                return true;
            }
        else {
            Toast.makeText(context, getResources().getString(R.string.toast_login_retry), Toast.LENGTH_SHORT).show();
            passwordCanUse = false;
            return false;
        }
    }/*@Override public void onResume() { super.onResume(); getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); } @Override public void onPause() { super.onPause(); getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); }*/

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case TAKE_CAMERA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (resultCode == RESULT_OK && Utils.checkLoginInformationAndNetwork(context))
                            try {
                                avatarBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));/*Drawable drawable = new BitmapDrawable(avatarBitmap); avatar.setIcon(drawable);*/
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                avatarIs = new ByteArrayInputStream(baos.toByteArray());
                                uploadAvatar();
                                File file = new File(getExternalCacheDir(), tempAvatarName);
                                file.delete();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploading.dismiss();
                                gettingAvatar.dismiss();
                            }
                        });
                        Looper.loop();
                    }
                }).start();

                break;
            case RC_CHOOSE_PHOTO:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (resultCode == RESULT_OK && Utils.checkLoginInformationAndNetwork(context) && resultCode == RESULT_OK) { /* 判断手机系统版本号*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Build.VERSION.SDK_INT >= 19) {/* 4.4及以上系统使用这个方法处理图片*/
                                        handleImageOnKitKat(data);
                                    } else {/* 4.4以下系统使用这个方法处理图片*/
                                        handleImageBeforeKitKat(data);
                                    }
                                }
                            });

                        }/*Uri uri = data.getData(); String filePath = FileUtil.getFilePathByUri(this, uri); if (!TextUtils.isEmpty(filePath)) { RequestOptions requestOptions1 = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE); //将照片显示在 ivImage上 Glide.with(this).load(filePath).apply(requestOptions1).into(ivImage); }*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploading.dismiss();
                                gettingAvatar.dismiss();
                            }
                        });
                        Looper.loop();
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {/* 如果是document类型的Uri，则通过document id处理*/
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];/* 解析出数字格式的id*/
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {/* 如果是content类型的Uri，则使用普通方式处理*/
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {/* 如果是file类型的Uri，直接获取图片路径即可*/
            imagePath = uri.getPath();
        }/* 根据图片路径显示图片*/
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式 @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;/* 通过Uri和selection来获取真实的图片路径*/
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            avatarIs = FormatTools.getInstance().Bitmap2InputStream(bitmap);
            uploadAvatar();
        } else
            Toast.makeText(this, getResources().getString(R.string.toast_failed_to_get_photo_from_album), Toast.LENGTH_SHORT).show();
    }

    void uploadAvatar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                int status = 0;
                try {
                    //status = au.uploadAvatar(context, uploading, EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY), avatarIs);
                    status = au.uploadAvatar(context, uploading, EnDeCryptTextUtils.encrypt(emailString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(accountString, Variables.TEXT_ENCRYPTION_KEY), EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY), avatarIs);
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    uploading.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_changeavatar), Toast.LENGTH_SHORT).show();
                    Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_changeavatar));
                    Looper.loop();
                }
                if (status != 0) {
                    Looper.prepare();
                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {*/
                    //avatarP.setIcon(FormatTools.getInstance().InputStream2Drawable(avatarIs));
                    //avatarP.setIcon(null);
                                            /*}
                                        });*/
                    initAvatar();
                    uploading.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.toast_successfully_changed_avatar), Toast.LENGTH_SHORT).show();
                    Looper.loop();

                                        /*try {
                                            avatar = au2.getInputStream(context, "avatar", "password", EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    avatarP.setIcon(FormatTools.getInstance().InputStream2Drawable(avatar));
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
                                            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                                            Looper.loop();
                                        }*/
                    //initAvatar();
                } else {
                    Looper.prepare();
                    uploading.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_changeavatar), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }).start();
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

    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



        initInformations();
        if (key.equals(Variables.SHARED_PREFERENCE_ACCOUNT_NAME)) {
            if(Utils.checkLoginInformationAndNetwork(context,false)) {
                if(uploadInformation(name.getText(),accountGenderContent,accountWhatSUpContent)!=0) {
                    Utils.dynamicModifyETSummary(name, getResources().getString(R.string.preference_account_name_summary));
                }else{
                    name.setText(null);
                    name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                }
            }else{
                name.setText(null);
                name.setSummary(getResources().getString(R.string.preference_account_name_summary));
            }
        }
        if (key.equals(Variables.SHARED_PREFERENCE_ACCOUNT_WHATSUP)) {
            //Utils.dynamicModifyETSummary(whatsup,getResources().getString(R.string.preference_account_whatsup_summary));
            if(Utils.checkLoginInformationAndNetwork(context,false)) {
                if(uploadInformation(accountNameContent,accountGenderContent,whatsup.getText())!=0) {
                    Utils.dynamicModifyETSummary(whatsup, getResources().getString(R.string.preference_account_whatsup_summary));
                }else{
                    whatsup.setText(null);
                    whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                    Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                }
            }else{
                whatsup.setText(null);
                whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
            }
        }
        if (key.equals(Variables.SHARED_PREFERENCE_ACCOUNT_GENDER)) {
            //Utils.dynamicModifyListSummary(gender,getResources().getString(R.string.preference_account_gender_summary));
            if (key.equals(Variables.SHARED_PREFERENCE_ACCOUNT_GENDER)) {
                //Utils.dynamicModifyETSummary(whatsup,getResources().getString(R.string.preference_account_whatsup_summary));
                if(Utils.checkLoginInformationAndNetwork(context,false)) {
                    if(uploadInformation(accountNameContent,gender.getValue(),accountWhatSUpContent)!=0) {
                        Utils.dynamicModifyListSummary(gender, getResources().getString(R.string.preference_account_gender_summary));
                    }else{
                        gender.setValue(null);
                        gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                        Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    gender.setValue(null);
                    gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                }
            }
        }
        //initInformations();
    }*/

}