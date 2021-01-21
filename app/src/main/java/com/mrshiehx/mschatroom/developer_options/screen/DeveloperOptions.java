package com.mrshiehx.mschatroom.developer_options.screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.preference.AppCompatPreferenceActivity;
import com.mrshiehx.mschatroom.utils.Utils;

public class DeveloperOptions extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    Context context=DeveloperOptions.this;
    EditTextPreference server_address,database_name,database_user_name,database_user_password,database_table_name;
    CheckBoxPreference isShowPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.initialization(DeveloperOptions.this, R.string.activity_developer_options_name);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_developer_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        server_address=(EditTextPreference) getPreferenceManager().findPreference(Variables.SHARED_PREFERENCE_SERVER_ADDRESS);
        database_name=(EditTextPreference) getPreferenceManager().findPreference(Variables.SHARED_PREFERENCE_DATABASE_NAME);
        database_user_name=(EditTextPreference) getPreferenceManager().findPreference(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME);
        database_user_password=(EditTextPreference) getPreferenceManager().findPreference(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD);
        database_table_name=(EditTextPreference) getPreferenceManager().findPreference(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME);
        isShowPassword=(CheckBoxPreference)getPreferenceManager().findPreference("isShowPassword");
        dynamicModifyETsSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dynamicModifyETsSummary();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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

    void dynamicModifyETsSummary(){
        Utils.dynamicModifyETSummary(server_address,getString(R.string.preference_server_address_summary));
        Utils.dynamicModifyETSummary(database_name,getString(R.string.preference_database_name_summary));
        Utils.dynamicModifyETSummary(database_user_name,getString(R.string.preference_database_user_name_summary));

        if (!TextUtils.isEmpty(database_user_password.getText())) {
            database_user_password.setSummary(getString(R.string.preference_database_user_password_seted_summary));
        } else {
            database_user_password.setSummary(getString(R.string.preference_database_user_password_summary));
        }

        Utils.dynamicModifyETSummary(database_table_name,getString(R.string.preference_database_table_name_summary));
        if(isShowPassword.isChecked()){
            database_user_password.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            database_user_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Variables.SHARED_PREFERENCE_SERVER_ADDRESS)) {
            Utils.dynamicModifyETSummary(server_address,getString(R.string.preference_server_address_summary));
        }
        if (key.equals(Variables.SHARED_PREFERENCE_DATABASE_NAME)) {
            Utils.dynamicModifyETSummary(database_name,getString(R.string.preference_database_name_summary));
        }
        if (key.equals(Variables.SHARED_PREFERENCE_DATABASE_USER_NAME)) {
            Utils.dynamicModifyETSummary(database_user_name,getString(R.string.preference_database_user_name_summary));
        }
        if (key.equals(Variables.SHARED_PREFERENCE_DATABASE_USER_PASSWORD)) {
            if (!TextUtils.isEmpty(database_user_password.getText())) {
                database_user_password.setSummary(getString(R.string.preference_database_user_password_seted_summary));
            } else {
                database_user_password.setSummary(getString(R.string.preference_database_user_password_summary));
            }
        }
        if (key.equals(Variables.SHARED_PREFERENCE_DATABASE_TABLE_NAME)) {
            Utils.dynamicModifyETSummary(database_table_name,getString(R.string.preference_database_table_name_summary));
        }

        if(isShowPassword.isChecked()){
            database_user_password.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            database_user_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
