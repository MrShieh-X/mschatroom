package com.mrshiehx.mschatroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mrshiehx.mschatroom.chat.screen.ChatScreen;
import com.mrshiehx.mschatroom.login.screen.LoginScreen;
import com.mrshiehx.mschatroom.main.screen.MainScreen;
import com.mrshiehx.mschatroom.utils.Utils;

public class StartScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);

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
}