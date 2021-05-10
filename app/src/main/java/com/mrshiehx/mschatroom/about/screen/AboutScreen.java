package com.mrshiehx.mschatroom.about.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.utils.Utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AboutScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Context context = AboutScreen.this;
    private ListView listView;
    private ArrayAdapter<String> listViewAdapter;
    TextView author;
    TextView copyright;
    TextView copyrightAndAllRightsReservedForChinese;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.initialization(AboutScreen.this, R.string.activity_about_screen_name);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about);
        author = findViewById(R.id.author);
        copyright = findViewById(R.id.copyright);
        copyrightAndAllRightsReservedForChinese = findViewById(R.id.copyrightAndAllRightsReservedForChinese);
        TextView appVersionNameAndCode;
        appVersionNameAndCode = findViewById(R.id.versionNameAndCode);
        appVersionNameAndCode.setText(getResources().getString(R.string.textview_about_version_partical) + " " + Utils.getVersionName(context) + "(" + Utils.getVersionCode(context) + ")");
        listView = findViewById(R.id.usersCanDoListview);
        String[] arr_data = {
                getResources().getString(R.string.listviewtext_about_users_can_do_item_contact),
                getResources().getString(R.string.listviewtext_about_users_can_do_item_visit_msxw),
                getResources().getString(R.string.listviewtext_about_users_can_do_item_visit_author_github),
                getResources().getString(R.string.listviewtext_about_users_can_do_item_visit_github),
                getResources().getString(R.string.listviewtext_about_users_can_do_item_visit_gitee),
                getString(R.string.listviewtext_about_users_can_do_item_check)};
        listViewAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arr_data);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Utils.sendMail(this, Variables.AUTHOR_MAIL, "", "");
                break;
            case 1:
                Utils.goToWebsite(this, Variables.AUTHOR_WEBSITE_URL);
                break;
            case 2:
                Utils.goToWebsite(this, Variables.AUTHOR_GITHUB_URL);
                break;
            case 3:
                Utils.goToWebsite(this, Variables.APP_GITHUB_REPOSITORY_URL);
                break;
            case 4:
                Utils.goToWebsite(this, Variables.APP_GITEE_REPOSITORY_URL);
                break;
            case 5:
                if (Utils.isNetworkConnected(context)) {
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    //progressDialog.setTitle(getString(R.string.dialog_title_wait));
                    progressDialog.setMessage(getString(R.string.dialog_checking_updates_message));

                    progressDialog.show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                URL url = new URL(Variables.NEW_VERSION_FILE_INFORMATION_URL);
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                con.setReadTimeout(20000);
                                con.setConnectTimeout(30000);
                                con.setRequestProperty("Charset", "UTF-8");
                                con.setRequestMethod("GET");
                                if (con.getResponseCode() == 200) {
                                    InputStream is = con.getInputStream();
                                    if (is != null) {
                                        StringBuilder sb = new StringBuilder();
                                        String line;

                                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                        while ((line = br.readLine()) != null) {
                                            sb.append(line);
                                        }
                                        try{is.close();br.close();}catch (Exception e){e.printStackTrace();}
                                        String content = sb.toString();
                                        JSONObject object = new JSONObject(content);
                                        int latest_version_code = object.getInt("latest_version_code");
                                        if (latest_version_code > Utils.getVersionCode(context)) {
                                            String latest_version_name = object.getString("latest_version_name");
                                            final String latest_version_download_url = object.getString("latest_version_download_url");
                                            String update_date = object.getString("update_date");
                                            String update_content_enUS = object.getString("update_content_enUS");
                                            String update_content_zhCN = object.getString("update_content_zhCN");
                                            String updateContent = update_content_enUS;
                                            if (MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_LANGUAGE, Variables.DEFAULT_LANGUAGE).equals("zh_CN")) {
                                                updateContent = update_content_zhCN;
                                            }

                                            final AlertDialog.Builder update = new AlertDialog.Builder(context);
                                            update.setTitle(String.format(getString(R.string.dialog_new_version_title), latest_version_name, latest_version_code));
                                            update.setMessage(String.format(getString(R.string.dialog_new_version_message), update_date, updateContent));
                                            update.setNegativeButton(getString(android.R.string.cancel), null);

                                            update.setPositiveButton(getString(R.string.dialog_new_version_button_update), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Looper.prepare();
                                                            final ProgressDialog[] downloading_new_version = new ProgressDialog[1];
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    downloading_new_version[0] = new ProgressDialog(context);
                                                                    //downloading_new_version[0].setTitle(getString(R.string.dialog_title_wait));
                                                                    downloading_new_version[0].setMessage(getString(R.string.dialog_downloading_file_message));
                                                                    downloading_new_version[0].show();
                                                                }
                                                            });
                                                            try {
                                                                URL url = new URL(latest_version_download_url);
                                                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                                                con.setReadTimeout(120000);
                                                                con.setConnectTimeout(30000);
                                                                con.setRequestProperty("Charset", "UTF-8");
                                                                con.setRequestMethod("GET");
                                                                if (con.getResponseCode() == 200) {
                                                                    InputStream is = con.getInputStream();
                                                                    if (is != null) {
                                                                        File file = new File(Utils.getDataCachePath(context), "new_version.apk");
                                                                        if (file.exists()) {
                                                                            file.delete();
                                                                        }
                                                                        file.createNewFile();
                                                                        Utils.inputStream2File(is, file);
                                                                        try{
                                                                            is.close();
                                                                        }catch (Exception e){
                                                                            e.printStackTrace();
                                                                        }
                                                                        /**
                                                                         * 安装Apk
                                                                         */
                                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                        //安装完成后，启动app（源码中少了这句话）

                                                                        try {
                                                                            //兼容7.0
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                                Uri contentUri = FileProvider.getUriForFile(context, "com.mrshiehx.mschatroom.FileProvider", file);
                                                                                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                                                                //兼容8.0
                                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                    boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
                                                                                    if (!hasInstallPermission) {
                                                                                        //注意这个是8.0新API
                                                                                        Intent intent2 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                                                                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                        startActivity(intent2);
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            }
                                                                            if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                                                                                startActivity(intent);
                                                                            }
                                                                        } catch (Throwable e) {
                                                                            e.printStackTrace();
                                                                        }


                                                                        //Toast.makeText(AboutScreen.this, Utils.inputStream2String(is,true), Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(context, getString(R.string.dialog_exception_failed_download_file), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(context, getString(R.string.dialog_exception_failed_download_file), Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                Utils.exceptionDialog(context, e, getString(R.string.dialog_exception_failed_download_file));
                                                            }
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    downloading_new_version[0].dismiss();
                                                                }
                                                            });
                                                            Looper.loop();

                                                        }
                                                    }).start();

                                                }
                                            });
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    update.show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, getString(R.string.toast_check_updates_isnew), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(context, getString(R.string.dialog_exception_failed_download_file), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, getString(R.string.dialog_exception_failed_download_file), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.exceptionDialog(context, e, getString(R.string.toast_failed_check_updates));
                            }
                            progressDialog.dismiss();
                            Looper.loop();
                        }
                    });
                    if (!thread.isAlive()) {
                        thread.start();
                    }

                } else {
                    Utils.showLongSnackbar(listView, getString(R.string.toast_please_check_your_network));
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Variables.COMMUNICATOR != null) {
            Variables.COMMUNICATOR.setContext(context);
        }
    }

}
