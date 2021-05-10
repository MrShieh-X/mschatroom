package com.mrshiehx.mschatroom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;

public class PermissionsGranter {
    private final Activity activity;
    private final OnHasPermissions onHasPermissions;
    private final OnDeny onDeny;
    private boolean ps=false;
    public static final int REQUEST_CODE_ON_REQUEST_PERMISSIONS_RESULT=321;
    public static final int REQUEST_CODE_ON_ACTIVITY_RESULT=123;
    private final int requestCodeOnRequestPermissionsResult;
    private final int requestCodeOnActivityResult;

    private AlertDialog dialog2;

    private Object[]arguments;

    public PermissionsGranter(Activity activity, OnHasPermissions onHasPermissions, @Nullable OnDeny onDeny){
        this(activity,onHasPermissions,onDeny,REQUEST_CODE_ON_REQUEST_PERMISSIONS_RESULT,REQUEST_CODE_ON_ACTIVITY_RESULT);
    }

    public PermissionsGranter(Activity activity, OnHasPermissions onHasPermissions, @Nullable OnDeny onDeny, int requestCodeOnRequestPermissionsResult, int requestCodeOnActivityResult){
        this.activity=activity;
        this.onHasPermissions=onHasPermissions;
        this.onDeny=onDeny!=null?onDeny:()->{};
        this.requestCodeOnRequestPermissionsResult=requestCodeOnRequestPermissionsResult;
        this.requestCodeOnActivityResult=requestCodeOnActivityResult;
    }

    public void start(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ps = activity.shouldShowRequestPermissionRationale(Variables.PERMISSIONS[0]);
        }
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Variables.PERMISSIONS[0]);
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            startRequestPermission();
        } else {
            onHasPermissions.execute(arguments);
        }
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return arguments;
    }

    private void startRequestPermission() {
        if (!ps) {
            ActivityCompat.requestPermissions(activity, Variables.PERMISSIONS, requestCodeOnRequestPermissionsResult);
        } else {
            showDialogTipUserGoToAppSettting();
        }
    }

    private void showDialogTipUserGoToAppSettting() {
        AlertDialog.Builder dialog_no_permissions = new AlertDialog.Builder(activity);
        dialog_no_permissions.setTitle(activity.getString(R.string.dialog_no_permissions_title))
                .setMessage(activity.getString(R.string.dialog_no_permissions_message))
                .setPositiveButton(activity.getString(R.string.dialog_no_permissions_button_gotosettings), (dialog, which) -> goToAppSetting()).setNegativeButton(activity.getString(android.R.string.cancel), (dialog, which) -> onDeny.execute());
        dialog2=dialog_no_permissions.show();
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCodeOnActivityResult);
    }

    public void onActivityResult(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(activity, Variables.PERMISSIONS[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                showDialogTipUserGoToAppSettting();
            } else {
                if (dialog2 != null && dialog2.isShowing()) {
                    dialog2.dismiss();
                }
                onHasPermissions.execute(arguments);
            }
        }
    }

    public void onRequestPermissionsResult(String[] permissions, @NonNull int[] grantResults){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                boolean b = activity.shouldShowRequestPermissionRationale(permissions[0]);
                if (b) {
                    showDialogTipUserGoToAppSettting();
                } else {
                    onDeny.execute();
                }
            } else {
                onHasPermissions.execute(arguments);
            }
        }
    }

    public interface OnHasPermissions{
        void execute(Object[]args);
    }

    public interface OnDeny{
        void execute();
    }
}
