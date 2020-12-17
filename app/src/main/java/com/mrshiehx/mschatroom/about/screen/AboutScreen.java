package com.mrshiehx.mschatroom.about.screen;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mrshiehx.mschatroom.MyApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.utils.Utils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Context context=AboutScreen.this;
    private ListView listView;
    private ArrayAdapter<String> listViewAdapter;
    TextView author;
    TextView copyright;
    TextView copyrightAndAllRightsReservedForChinese;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initialization(AboutScreen.this, R.string.activity_about_screen_name);

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
                getResources().getString(R.string.listviewtext_about_users_can_do_item_visit_github)};
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
        }

    }
}
