package com.mrshiehx.mschatroom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class StartScreen extends Activity {

    public static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE};
    AlertDialog.Builder dialog_no_permissions;
    AlertDialog dialog2;
    boolean ps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // 检查该权限是否已经获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ps = shouldShowRequestPermissionRationale(permissions[0]);
        }
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
        int INTERNET = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
        int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[2]);
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED || INTERNET != PackageManager.PERMISSION_GRANTED || ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            // 如果没有授予该权限，就去提示用户请求
            startRequestPermission();
        } else {
            method01();
        }
        //}
    }

    private void startRequestPermission() {
        if(!ps) {
            ActivityCompat.requestPermissions(this, permissions, 321);
        }else{
            showDialogTipUserGoToAppSettting();
        }
    }

    /**
     * 用户权限 申请 的回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否点击了不再提醒。(检测该权限是否还可以申请)true=没按
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    // 以前是!b
                    if (b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        //Toast.makeText(this, "asdfiasdflf", Toast.LENGTH_SHORT).show();
                        MyApplication.getInstance().exit();
                    }
                } else {
                    method01();
                }
            }
        }


    }
    private void showDialogTipUserGoToAppSettting() {
        dialog_no_permissions = new AlertDialog.Builder(this);
        dialog_no_permissions.setTitle(getString(R.string.dialog_no_permissions_title)/*"存储权限不可用"*/)
                .setMessage(getString(R.string.dialog_no_permissions_message)/*"请在-应用设置-权限-中，允许应用使用存储权限来保存用户数据"*/)
                .setPositiveButton(getString(R.string.dialog_no_permissions_button_gotosettings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getInstance().exit();
                    }
                }).setCancelable(false);
        dialog2=dialog_no_permissions.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //权限管理
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog2 != null&&dialog2.isShowing()) {
                        dialog2.dismiss();
                    }
                    method01();
                }
            }
        }
    }
    void method01(){
        final EditText editText = new EditText(StartScreen.this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StartScreen.this);
        String tip = getResources().getString(R.string.input_information_tip);
        inputDialog.setTitle(tip).setView(editText);
        inputDialog.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] vars = editText.getText().toString().split(Variables.SPLIT_SYMBOL);
                Variables.SERVER_ADDRESS = vars[0];
                Variables.DATABASE_NAME = vars[1];
                Variables.DATABASE_USER = vars[2];
                Variables.DATABASE_PASSWORD = vars[3];
                Variables.DATABASE_TABLE_NAME = vars[4];
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StartScreen.this);
                boolean isLogined = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false);
                //try{
                if (isLogined == false) {
                    Utils.startActivity(StartScreen.this, LoginScreen.class);
                } else {
                    Utils.startActivity(StartScreen.this, MainScreen.class);
                }
            }
        });
        inputDialog.setNegativeButton(getResources().getString(R.string.input_information_login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startActivity(StartScreen.this, LoginScreen.class);
            }
        });
        inputDialog.setNeutralButton(getResources().getString(R.string.input_information_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startActivity(StartScreen.this, MainScreen.class);
            }
        });
        inputDialog.setCancelable(false);
        editText.setText(Variables.SERVER_ADDRESS + Variables.SPLIT_SYMBOL + Variables.DATABASE_NAME + Variables.SPLIT_SYMBOL + Variables.DATABASE_USER + Variables.SPLIT_SYMBOL + Variables.DATABASE_PASSWORD + Variables.SPLIT_SYMBOL + Variables.DATABASE_TABLE_NAME);
        inputDialog.show();
        Toast.makeText(StartScreen.this, tip, Toast.LENGTH_SHORT).show();
        //}catch (Exception e){
        //Utils.exceptionDialog(this,e);
        //e.printStackTrace();
        //}
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }
}