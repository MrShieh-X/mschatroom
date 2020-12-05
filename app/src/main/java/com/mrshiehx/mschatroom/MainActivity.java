package com.mrshiehx.mschatroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.settings.screen.SettingsScreen;
import com.mrshiehx.mschatroom.utils.Utils;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
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
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean isLogined = sharedPreferences.getBoolean(Variables.SHARED_PREFERENCE_IS_LOGINED, false);
                //try{
                if (isLogined == false) {
                    Utils.startActivity(MainActivity.this, LoginScreen.class);
                } else {
                    Utils.startActivity(MainActivity.this, SettingsScreen.class);
                }
            }
        });
        inputDialog.setNegativeButton(getResources().getString(R.string.input_information_login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startActivity(MainActivity.this, LoginScreen.class);
            }
        });
        inputDialog.setNeutralButton(getResources().getString(R.string.input_information_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startActivity(MainActivity.this, SettingsScreen.class);
            }
        });
        inputDialog.setCancelable(false);
        editText.setText(Variables.SERVER_ADDRESS + Variables.SPLIT_SYMBOL + Variables.DATABASE_NAME + Variables.SPLIT_SYMBOL + Variables.DATABASE_USER + Variables.SPLIT_SYMBOL + Variables.DATABASE_PASSWORD + Variables.SPLIT_SYMBOL + Variables.DATABASE_TABLE_NAME);
        inputDialog.show();
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        //}catch (Exception e){
        //Utils.exceptionDialog(this,e);
        //e.printStackTrace();
        //}
    }
}