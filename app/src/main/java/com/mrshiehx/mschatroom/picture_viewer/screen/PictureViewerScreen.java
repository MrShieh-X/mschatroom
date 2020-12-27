package com.mrshiehx.mschatroom.picture_viewer.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.StartScreen;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureViewerScreen extends Activity {
    Context context=PictureViewerScreen.this;
    //Button back;
    ImageView image;
    //private View contentViewGroup;
    boolean ps;
    String[] permissions=StartScreen.permissions;
    AlertDialog.Builder dialog_no_permissions;
    AlertDialog dialog2;
    String contentType;
    String content;
    public static InputStream imageInputStream;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initializationForPictureViewer(PictureViewerScreen.this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayout mainLayout = new LinearLayout(this);
        //Button b=new Button(this);
        image = new ImageView(context);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(llp);
        mainLayout.setLayoutParams(llp);
        mainLayout.addView(image);
        setContentView(mainLayout);
        //setContentView(R.layout.activity_picture_viewer);
        //back=findViewById(R.id.back);
        //image=findViewById(R.id.main_image);
        //setStatusBarFullTransparent();
        //setFitSystemWindow(false);
        //setFitSystemWindow(true);
        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ps = shouldShowRequestPermissionRationale(StartScreen.permissions[0]);
        }
        //InputStreamItem inputStream = (InputStreamItem) getIntent().getSerializableExtra("image");
        /**
         *  优先加载Intent的，Intent没有就加载Scheme的
         */
        if (imageInputStream == null) {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            if (uri != null) {
                //query部分
                String[] queryString = uri.getQuery().split("=");
                contentType = queryString[0];
                content = queryString[1];


                if (contentType.equals("localPath")) {
                    int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), StartScreen.permissions[0]);
                    int INTERNET = ContextCompat.checkSelfPermission(getApplicationContext(), StartScreen.permissions[1]);
                    int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(getApplicationContext(), StartScreen.permissions[2]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED || INTERNET != PackageManager.PERMISSION_GRANTED || ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
                        // 如果没有授予该权限，就去提示用户请求
                        startRequestPermission();
                    } else {
                        method01(content);
                    }
                } else if (contentType.equals("url")) {
                    method02(content);
                } else {
                    Toast.makeText(context, getString(R.string.toast_picture_viewer_nothing), Toast.LENGTH_SHORT).show();
                    //Utils.showLongSnackbar(image,getString(R.string.toast_picture_viewer_nothing));
                }


            } else {
                Toast.makeText(context, getString(R.string.toast_picture_viewer_nothing), Toast.LENGTH_SHORT).show();
                //Utils.showLongSnackbar(image,getString(R.string.toast_picture_viewer_nothing));
            }
        } else {
            try {
                //Toast.makeText(context, "117", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, imageInputStream.toString(), Toast.LENGTH_SHORT).show();
                image.setImageDrawable(FormatTools.getInstance().InputStream2Drawable(imageInputStream));

                //Toast.makeText(context, "120", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_to_load_image));
                //Toast.makeText(context, "124", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, "126", Toast.LENGTH_SHORT).show();
        }

    }

    private void startRequestPermission() {
        if(!ps) {
            ActivityCompat.requestPermissions(this, StartScreen.permissions, 321);
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
                    }
                } else {

                    if(contentType.equals("localPath")){
                        method01(content);
                    } else{
                        Toast.makeText(context, getString(R.string.toast_picture_viewer_nothing), Toast.LENGTH_SHORT).show();
                        //Utils.showLongSnackbar(image,getString(R.string.toast_picture_viewer_nothing));
                    }
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
                }).setNegativeButton(getString(android.R.string.cancel), null).setCancelable(false);
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

                    if(contentType.equals("localPath")){
                        method01(content);
                    } else{
                        Toast.makeText(context, getString(R.string.toast_picture_viewer_nothing), Toast.LENGTH_SHORT).show();
                        //Utils.showLongSnackbar(image,getString(R.string.toast_picture_viewer_nothing));
                    }
                }
            }
        }
    }
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }
    void method01(final String content) {
        File file=new File(content);
        if(file.exists()){
            try {
                InputStream is = new FileInputStream(file);
                image.setImageDrawable(FormatTools.getInstance().InputStream2Drawable(is));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Utils.exceptionDialog(context,e,getString(R.string.dialog_exception_failed_to_read_file));
                Toast.makeText(context, getString(R.string.dialog_exception_failed_to_read_file), Toast.LENGTH_SHORT).show();
                //Utils.showLongSnackbar(image,getString(R.string.dialog_exception_failed_to_read_file));
            }

        }else{
            Toast.makeText(context, getString(R.string.dialog_exception_file_not_found), Toast.LENGTH_SHORT).show();
            //Utils.showLongSnackbar(image,getString(R.string.dialog_exception_file_not_found));
        }
    }
    void method02(final String content){
        if(Utils.networkAvailableDialog(context)){
            final ProgressDialog downloading = new ProgressDialog(context);
            downloading.setTitle(context.getResources().getString(R.string.dialog_title_wait));
            downloading.setMessage(context.getResources().getString(R.string.dialog_downloading_message));
            downloading.setCancelable(false);
            downloading.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(content);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(120000);
                        con.setConnectTimeout(50000);
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            final InputStream is = con.getInputStream();
                            if (is != null) {
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            image.setImageDrawable(FormatTools.getInstance().InputStream2Drawable(is));
                                        }
                                    });
                                }catch (Exception e){
                                    Looper.prepare();
                                    downloading.dismiss();
                                    Utils.exceptionDialog(context,e,getString(R.string.dialog_exception_failed_to_load_image));
                                    Looper.loop();
                                }
                            }else{
                                Looper.prepare();
                                downloading.dismiss();
                                Toast.makeText(context, getString(R.string.dialog_exception_downloadfailed), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                //Utils.showLongSnackbar(image,getString(R.string.dialog_exception_downloadfailed));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Looper.prepare();
                        downloading.dismiss();
                        Utils.exceptionDialog(context, e, context.getResources().getString(R.string.dialog_exception_downloadfailed));
                        Looper.loop();
                    }
                    downloading.dismiss();
                }
            }).start();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
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
}
