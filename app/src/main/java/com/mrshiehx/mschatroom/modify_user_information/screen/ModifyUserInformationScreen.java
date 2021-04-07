package com.mrshiehx.mschatroom.modify_user_information.screen;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.Preference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.mrshiehx.mschatroom.AccountInformation;
import com.mrshiehx.mschatroom.StartActivity;
import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.broadcast_receivers.NetworkStateReceiver;
import com.mrshiehx.mschatroom.picture_viewer.screen.PictureViewerScreen;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.ConnectionUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;

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
    ProgressDialog uploading, gettingAvatar, loggingIn, verifying;
    public static String passwordEncrypted = null;
    String tempAvatarName = "temp_avatar.png";
    byte[] avatarBytes;
    boolean passwordCanUse;
    byte[] informationFile;
    String accountNameContent;
    String accountGenderContent;
    String accountWhatSUpContent;
    UserInformation userInformationList = null;
    //boolean canLogin;
    int genderChoice;
    int nowGender = -1;
    AlertDialog.Builder genderChoiceDialog;
    EditText nameET, whatsupET;
    String tNameET, tWhatsupET;
    String accountStringEncrypted, emailStringEncrypted;
    NetworkStateReceiver myReceiver;

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

        myReceiver = new NetworkStateReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, itFilter);

        if (!Utils.isNetworkConnected(context)) {
            setTitle(getString(R.string.activity_modify_user_information_screen_offline_mode_name));
            //avatarP.setEnabled(false);
            //name.setEnabled(false);
            //whatsup.setEnabled(false);
            //gender.setEnabled(false);
            delete_account.setEnabled(false);
            //account.setEnabled(false);
            //account_email.setEnabled(false);
        } else {
            if (Variables.ACCOUNT_UTILS == null) {
                final ProgressDialog dialog = ConnectionUtils.showConnectingDialog(context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Connection connection = ConnectionUtils.getConnection(Variables.SERVER_ADDRESS, Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD);
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
        }
        uploading = new ProgressDialog(context);
        uploading.setCancelable(false);
        uploading.setTitle(getResources().getString(R.string.dialog_title_wait));
        uploading.setMessage(getResources().getString(R.string.dialog_uploading_message));
        gettingAvatar = new ProgressDialog(context);
        gettingAvatar.setCancelable(false);
        gettingAvatar.setTitle(getResources().getString(R.string.dialog_title_wait));
        gettingAvatar.setMessage(getResources().getString(R.string.dialog_downloading_avatar));/*gettingAvatar.show();*/
        verifying = new ProgressDialog(context);
        verifying.setCancelable(false);
        verifying.setTitle(getResources().getString(R.string.dialog_title_wait));
        verifying.setMessage(getString(R.string.dialog_verifying_message));
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
        initAccountAndEmail();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Utils.checkLoginStatus(context)) {
                    if (Utils.checkLoginInformationAndNetwork(context)) {
                        runOnUiThread(() -> loggingIn.show());
                        try {
                            if (initPassword()) {
                                initUserInformationFile();
                                initUserInformationContents();
                                initUserInformations();
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

                        initAvatar(true);
                    } else {
                        initAvatar(false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                delete_account.setEnabled(false);

                                if (getGender().equals("male")) {
                                    nowGender = 0;
                                    genderChoice = 0;
                                    gender.setSummary(getString(R.string.preference_account_gender_male));
                                } else if (getGender().equals("female")) {
                                    nowGender = 1;
                                    genderChoice = 1;
                                    gender.setSummary(getString(R.string.preference_account_gender_female));
                                } else {
                                    genderChoice = -1;
                                }
                                if (!TextUtils.isEmpty(getNickname())) {
                                    tNameET = getNickname().toString();
                                    name.setSummary(getNickname());
                                }
                                if (!TextUtils.isEmpty(getWhatsup())) {
                                    tWhatsupET = getWhatsup().toString();
                                    whatsup.setSummary(getWhatsup());
                                }
                            }
                        });

                    }
                }
                loggingIn.dismiss();
                Looper.loop();
            }
        }).start();
        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!TextUtils.isEmpty(account.getSummary())) {
                    Utils.copy(context, account.getSummary().toString());
                    Toast.makeText(context, getString(R.string.toast_account_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        account_email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!TextUtils.isEmpty(account_email.getSummary())) {
                    Utils.copy(context, account_email.getSummary().toString());
                    Toast.makeText(context, getString(R.string.toast_email_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        avatarP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder view_or_modify = new AlertDialog.Builder(context);
                view_or_modify.setTitle(getString(R.string.dialog_view_or_modify_avatar_title));
                final String[] items = {
                        getString(R.string.dialog_view_or_modify_avatar_operation_view),
                        getString(R.string.dialog_view_or_modify_avatar_operation_modify)};
                view_or_modify.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case 0:
                                if (avatarP.getIcon() != null) {
                                    PictureViewerScreen.imageInputStream = /*avatarBytes;*/FormatTools.getInstance().Drawable2InputStream(avatarP.getIcon());
                                    startActivity(new Intent(context, PictureViewerScreen.class));
                                }
                                break;
                            case 1:
                                if (Utils.isNetworkConnected(context)) {
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
                                                    File outputImage = new File(Utils.getDataCachePath(context), tempAvatarName);
                                                    try {
                                                        if (outputImage.exists()) {
                                                            outputImage.delete();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        imageUri = FileProvider.getUriForFile(context, "com.mrshiehx.mschatroom.FileProvider", outputImage);
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
                                } else {
                                    Toast.makeText(context, getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                        }
                    }
                });
                view_or_modify.show();
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
                                if (Utils.isNetworkConnected(context)) {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            //int which2=which;
                                            if (genderChoice != -1) {
                                                //Utils.dynamicModifyETSummary(whatsup,getResources().getString(R.string.preference_account_whatsup_summary));
                                                if (Utils.checkLoginStatus(context)) {
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
                                                                final String finalGen = gen;
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        gender.setSummary(items[finalWhich]);
                                                                        Variables.ACCOUNT_INFORMATION.setGender(finalGen);
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
                                            }
                                            Looper.loop();
                                        }
                                    }).start();

                                    genderChoice = which;
                                    nowGender = which;
                                } else {
                                    Toast.makeText(context, getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                                }

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
                InputFilter[] filters = {new InputFilter.LengthFilter(32)};
                nameET.setFilters(filters);
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.preference_account_name_title)).setView(nameET).setNegativeButton(context.getResources().getString(android.R.string.cancel), null).setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Utils.isNetworkConnected(context)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();

                                    if (Utils.checkLoginStatus(context)) {
                                        if (Utils.checkLoginInformationAndNetwork(context)) {
                                            //if(!TextUtils.isEmpty(nameET.getText())&&!TextUtils.isEmpty(tNameET)) {
                                            if (uploadInformation(nameET.getText().toString(), accountGenderContent, accountWhatSUpContent) != 0) {
                                                accountNameContent = nameET.getText().toString();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        name.setSummary(TextUtils.isEmpty(nameET.getText().toString()) ? getString(R.string.preference_account_name_summary) : nameET.getText().toString());
                                                    }
                                                });
                                                tNameET = nameET.getText().toString();
                                                Variables.ACCOUNT_INFORMATION.setNickname(tNameET);
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                                                    }
                                                });
                                                tNameET = "";
                                                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                                            }
                                            //}
                                        } else {
                                            tNameET = "";
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    name.setSummary(getResources().getString(R.string.preference_account_name_summary));
                                                }
                                            });

                                        }
                                    }
                                    Looper.loop();
                                }
                            }).start();
                        } else {
                            Toast.makeText(context, getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                        }


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
                InputFilter[] filters = {new InputFilter.LengthFilter((int) Math.pow(2, 7))};
                whatsupET.setFilters(filters);
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.preference_account_whatsup_title)).setView(whatsupET).setNegativeButton(context.getResources().getString(android.R.string.cancel), null).setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Utils.isNetworkConnected(context)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();

                                    if (Utils.checkLoginStatus(context)) {
                                        if (Utils.checkLoginInformationAndNetwork(context)) {

                                            //if(!TextUtils.isEmpty(whatsupET.getText())&&!TextUtils.isEmpty(tWhatsupET)) {
                                            if (uploadInformation(accountNameContent, accountGenderContent, whatsupET.getText().toString()) != 0) {
                                                accountWhatSUpContent = whatsupET.getText().toString();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        whatsup.setSummary(TextUtils.isEmpty(whatsupET.getText().toString()) ? getString(R.string.preference_account_whatsup_summary) : whatsupET.getText().toString());
                                                    }
                                                });
                                                tWhatsupET = whatsupET.getText().toString();
                                                Variables.ACCOUNT_INFORMATION.setWhatsup(tWhatsupET);
                                            } else {
                                                tWhatsupET = "";
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                                                    }
                                                });
                                                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_modify_information), Toast.LENGTH_SHORT).show();
                                            }
                                            // }

                                        } else {
                                            tWhatsupET = "";
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                                                }
                                            });

                                        }
                                    }
                                    Looper.loop();
                                }
                            }).start();
                        } else {
                            Toast.makeText(context, getString(R.string.toast_please_check_your_network), Toast.LENGTH_SHORT).show();
                        }
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

                        final Thread thread;
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();

                                if (Utils.checkLoginStatus(context)) {
                                    if (Utils.checkLoginInformationAndNetwork(context)) {
                                        //final AccountUtils au = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Variables.ACCOUNT_UTILS;

                                        //if (loginMethod == 0) {
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
                                        //if (loginMethod == 0) {
                                        try {
                                            acoe = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                            /*} else {
                                                try {
                                                    acoe = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                            }*/
                                        try {
                                            boolean var = Utils.getAccountUtils().login(context, new ProgressDialog(context), AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw);
                                            verifying.dismiss();
                                            if (var) {
                                                showDeleteAccountDialog(et);
                                            } else {
                                                Toast.makeText(context, getResources().getString(R.string.toast_account_or_password_incorrect), Toast.LENGTH_SHORT).show();
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


                                        //}
                                        /*else {
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
                                                    acoe = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                                    acoe = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_EMAIL_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                                                boolean var = Variables.ACCOUNT_UTILS.login(context, new ProgressDialog(context), AccountUtils.BY_EMAIL, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw);
                                                verifying.dismiss();
                                                if (var) {
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
                                        }*/
                                    }
                                }
                                Looper.loop();
                            }
                        });
                        thread.start();

                        verifying.show();

                    }
                }).setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
                d.show();
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());/*}*/
                return true;
            }
        });


    }

    void initAccountAndEmail() {
        accountStringEncrypted = getAccountEncrypted().toString();
        emailStringEncrypted = getEmailEncrypted().toString();
        if (!TextUtils.isEmpty(accountStringEncrypted)) {
            try {
                account.setSummary(EnDeCryptTextUtils.decrypt(accountStringEncrypted, Variables.TEXT_ENCRYPTION_KEY));
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
        if (!TextUtils.isEmpty(emailStringEncrypted)) {
            try {
                account_email.setSummary(EnDeCryptTextUtils.decrypt(emailStringEncrypted, Variables.TEXT_ENCRYPTION_KEY));
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


    }

    void showDeleteAccountDialog(final EditText et) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.checkbox_dialog, null);
        dialog.setView(dialogView);
        dialog.setTitle(getResources().getString(R.string.dialog_title_notice));
        TextView textView = dialogView.findViewById(R.id.checkbox_dialog_message);
        textView.setText(getResources().getString(R.string.dialog_delete_account_message));
        final CheckBox checkBox = dialogView.findViewById(R.id.checkbox_dialog_checkbox);
        checkBox.setText(getString(R.string.dialog_logout_checkbox_text));
        checkBox.setChecked(true);
        dialog.setNegativeButton(context.getResources().getString(android.R.string.cancel), null);
        dialog.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog deleting = new ProgressDialog(context);
                deleting.setTitle(getResources().getString(R.string.dialog_title_wait));
                deleting.setMessage(getResources().getString(R.string.dialog_deleting_message));
                deleting.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
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
                        try {
                            acoe = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
                        AccountUtils au = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Utils.getAccountUtils();
                        int sta = -1;
                        try {
                            sta = au.delectAccountNoThreadAndDialog(context, AccountUtils.BY_ACCOUNT, EnDeCryptTextUtils.encrypt(acoe, Variables.TEXT_ENCRYPTION_KEY), injmpw);
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
                        deleting.dismiss();
                        if (sta != 0) {
                            Toast.makeText(ModifyUserInformationScreen.this, getResources().getString(R.string.toast_successfully_deleted_account), Toast.LENGTH_SHORT).show();
                            MSCRApplication.getSharedPreferences()
                                    .edit()
                                    .remove(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD)
                                    .apply();
                            if (checkBox.isChecked()) {
                                Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chat_avatars"));
                                Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "chats"));
                                Utils.deleteDirectory(new File(Utils.getDataFilesPath(context), "information"));
                                File chatsFile = new File(Utils.getDataFilesPath(context), "chats.json");
                                chatsFile.delete();
                            }
                            File file;
                            //account
                            file = new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted())/*EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(sharedPreferences.getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD,""),Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0],Variables.TEXT_ENCRYPTION_KEY))*/;

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
                            finish();
                            Utils.startActivity(context, StartActivity.class);
                        } else {
                            Toast.makeText(ModifyUserInformationScreen.this, getResources().getString(R.string.toast_failed_to_delete_account), Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                }).start();

            }
        }).show();
    }

    public int uploadInformation(final String name, final String gender, final String whatSUp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploading.show();
            }
        });
        InputStream newI = null;
        try {
            newI = Utils.createNewUserInformation(name, gender, whatSUp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AccountUtils accountUtils = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Utils.getAccountUtils();
        int s = accountUtils.uploadUserInformation(context, uploading, emailStringEncrypted, accountStringEncrypted, passwordEncrypted, newI);

        if (s != 0) {
            //Toast.makeText(context, getResources().getString(R.string.toast_successfully_to_upload_userinformation), Toast.LENGTH_SHORT).show();
            return s;
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_upload_userinformation), Toast.LENGTH_SHORT).show();
            return s;
        }
    }

    void initUserInformations() {
        if (!TextUtils.isEmpty(accountGenderContent)) {
            if (accountGenderContent.equals("male")) {
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
                //Toast.makeText(context, getResources().getString(R.string.toast_tip_set_gender), Toast.LENGTH_SHORT).show();
                nowGender = -1;
                genderChoice = -1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gender.setSummary(getResources().getString(R.string.preference_account_gender_summary));
                    }
                });
            }
        } else {
            //Toast.makeText(context, getResources().getString(R.string.toast_tip_set_gender), Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(context, getResources().getString(R.string.toast_tip_set_name), Toast.LENGTH_SHORT).show();
            tNameET = "";
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
            //Toast.makeText(context, getResources().getString(R.string.toast_tip_set_whatsup), Toast.LENGTH_SHORT).show();
            tWhatsupET = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    whatsup.setSummary(getResources().getString(R.string.preference_account_whatsup_summary));
                }
            });
        }
    }

    public void initUserInformationFile() {
        //if (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0) {
            /*String accountClean = "";
            try {
                accountClean = EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[0];
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
            accountString = finalAccountClean;*/
        //AccountUtils accountUtils = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);*/Variables.ACCOUNT_UTILS;
        //String emailClean = EnDeCryptTextUtils.decrypt(accountUtils.getStringNoThread(context, "email", AccountUtils.BY_ACCOUNT, accountStringEncrypted);
        try {
            //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
            informationFile = Utils.getAccountUtils().getBytesNoThread(context, "information", AccountUtils.BY_ACCOUNT, accountStringEncrypted);
        } catch (Exception e) {
            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
            e.printStackTrace();
        }
        //} else {
        //AccountUtils accountUtils = /*new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME)*/Variables.ACCOUNT_UTILS;
        //AccountUtils accountUtils = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
         /*   try {
                informationFile = Variables.ACCOUNT_UTILS.getUserInformationNoThread(context, emailStringEncrypted, accountStringEncrypted, passwordEncrypted);
            } catch (Exception e) {
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_get_user_information));
                e.printStackTrace();
            }
        }*/
    }

    public void initUserInformationContents() {
        if (informationFile == null) {
            Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
        } else {
            userInformationList = UserInformationUtils.read(context, informationFile);
            if (userInformationList != null) {
                accountNameContent = userInformationList.nameContent;
                accountGenderContent = userInformationList.genderContent;
                accountWhatSUpContent = userInformationList.whatsupContent;
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_no_user_information), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void initAvatar(boolean connected) {
        //final AccountUtils au = Variables.ACCOUNT_UTILS;
        if (connected) {
            try {
                String by;
                String byContent;
                by = AccountUtils.BY_ACCOUNT;
                byContent = getAccountEncrypted().toString();
                avatarBytes = Utils.getAccountUtils().getBytesNoThread(context, "avatar", by, byContent);
                if (avatarBytes != null && avatarBytes.length != 0)
                    FileUtils.bytes2File(avatarBytes, new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted()));
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_downloadavatar));
                Toast.makeText(context, getResources().getString(R.string.toast_failed_to_downloadavatar), Toast.LENGTH_SHORT).show();
            }

        } else {
            try {
                avatarBytes = FileUtils.toByteArray(new File(Utils.getDataFilesPath(context), "avatar_" + getAccountEncrypted()));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, getString(R.string.dialog_exception_failed_to_read_file), Toast.LENGTH_SHORT).show();
            }
        }
        /*try {
            avatarBytes = new FileInputStream(new File(Utils.getDataFilesPath(context), "avatar_" + (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, 0) == 0 ? getAccountEncrypted() : getEmailEncrypted())));
        }catch(FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.dialog_exception_file_not_found), Toast.LENGTH_SHORT).show();
        }*/
        if (avatarBytes != null) {
            runOnUiThread(() -> avatarP.setIcon(FormatTools.getInstance().Bytes2Drawable(avatarBytes)));
        }
    }

    boolean initPassword() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (MSCRApplication.getSharedPreferences().contains(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD)) {
            passwordEncrypted = EnDeCryptTextUtils.encrypt(EnDeCryptTextUtils.decrypt(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_ACCOUNT_AND_PASSWORD, ""), Variables.TEXT_ENCRYPTION_KEY).split(Variables.SPLIT_SYMBOL)[1], Variables.TEXT_ENCRYPTION_KEY);
            passwordCanUse = true;
            return true;
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_login_retry), Toast.LENGTH_SHORT).show();
            passwordCanUse = false;
            return false;
        }
    }/*@Override public void onResume() { super.onResume(); getPreferenceManager().getMSCRApplication.getSharedPreferences()().registerOnSharedPreferenceChangeListener(this); } @Override public void onPause() { super.onPause(); getPreferenceManager().getMSCRApplication.getSharedPreferences()().unregisterOnSharedPreferenceChangeListener(this); }*/

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case TAKE_CAMERA:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (Utils.checkLoginStatus(context)) {
                            if (resultCode == RESULT_OK && Utils.checkLoginInformationAndNetwork(context)) {
                                try {
                                    avatarBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                                    //*Drawable drawable = new BitmapDrawable(avatarBitmap); avatar.setIcon(drawable);*//*
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    avatarIs = new ByteArrayInputStream(baos.toByteArray());
                                    uploadAvatar(avatarIs);
                                    File file = new File(Utils.getDataCachePath(context), tempAvatarName);
                                    file.delete();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Utils.exceptionDialog(context, e);
                                }
                            }
                        }
                        Looper.loop();
                    }
                }).start();

                break;
            case RC_CHOOSE_PHOTO:
                new Thread(() -> {
                    Looper.prepare();
                    if (resultCode == RESULT_OK && Utils.checkLoginInformationAndNetwork(context)) { /* 判断手机系统版本号*/
                        if (Build.VERSION.SDK_INT >= 19) {/* 4.4及以上系统使用这个方法处理图片*/
                            handleImageOnKitKat(data);
                        } else {/* 4.4以下系统使用这个方法处理图片*/
                            handleImageBeforeKitKat(data);
                        }
                    }
                    Looper.loop();
                }).start();
                /*Uri uri = data.getData(); String filePath = FileUtil.getFilePathByUri(this, uri); if (!TextUtils.isEmpty(filePath)) { RequestOptions requestOptions1 = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE); //将照片显示在 ivImage上 Glide.with(this).load(filePath).apply(requestOptions1).into(ivImage); }*/

                        /*uploading.dismiss();
                        gettingAvatar.dismiss();*/
                break;
            default:
                break;
        }

        uploading.dismiss();
        gettingAvatar.dismiss();
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = "";
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
        String path = "";/* 通过Uri和selection来获取真实的图片路径*/
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
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            try {
                avatarIs = new FileInputStream(new File(imagePath));
                uploadAvatar(avatarIs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_failed_to_get_photo_from_album), Toast.LENGTH_SHORT).show();
        }
    }

    void uploadAvatar(InputStream avatarIs) {
        int status = 0;
        try {
            //status = au.uploadAvatar(context, uploading, EnDeCryptTextUtils.encrypt(password, Variables.TEXT_ENCRYPTION_KEY), avatarIs);
            status = Utils.getAccountUtils().uploadAvatarNoThreadAndDialog(context, emailStringEncrypted, accountStringEncrypted, passwordEncrypted, avatarIs);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_changeavatar), Toast.LENGTH_SHORT).show();
            Utils.exceptionDialog(context, e, getResources().getString(R.string.dialog_exception_failed_to_changeavatar));
        }
        if (status != 0) {
            initAvatar(true);
            Toast.makeText(context, getResources().getString(R.string.toast_successfully_changed_avatar), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_failed_to_changeavatar), Toast.LENGTH_SHORT).show();
        }
        uploading.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    public void changeEnabledOfPreferencesOfEnabled(boolean enabled) {
        delete_account.setEnabled(enabled);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
        if(Variables.ACCOUNT_UTILS!=null){
            if(Variables.ACCOUNT_UTILS.getConnection()==null){
                Utils.reload(context);
            }
        }else{
            Utils.reload(context);
        }
    }
}