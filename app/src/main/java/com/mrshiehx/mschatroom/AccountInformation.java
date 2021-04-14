package com.mrshiehx.mschatroom;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * 当离线模式时，全部界面使用这个账户信息
 */
public class AccountInformation {
    boolean isNetworkConnected;
    boolean canLogin;
    boolean logined;
    boolean canConnectToServer;
    Drawable avatar;
    CharSequence accountE;
    CharSequence emailE;
    CharSequence nickname;
    CharSequence whatsup;
    CharSequence gender;

    public AccountInformation() {

    }

    public AccountInformation(boolean isNetworkConnected, boolean logined, boolean canLogin, Drawable avatar, CharSequence accountE, CharSequence emailE, InputStream information/*, InputStream avatar*/) {
        this.isNetworkConnected = isNetworkConnected;
        this.logined = logined;
        this.canLogin = canLogin;
        this.accountE = accountE;
        this.emailE = emailE;
        this.avatar = avatar;
        //this.avatar=avatar;
        if (information != null) {
            try {
                String content = FileUtils.getString(information);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject = new JSONObject();
                }
                this.nickname = jsonObject.optString("name");
                this.gender = jsonObject.optString("gender");
                this.whatsup = jsonObject.optString("whatIsUp");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MSCRApplication.getContext(), MSCRApplication.getContext().getString(R.string.toast_failed_to_get_information), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setIsNetworkConnected(boolean isNetworkConnected) {
        this.isNetworkConnected = isNetworkConnected;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public void setCanLogin(boolean canLogin) {
        this.canLogin = canLogin;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

    public void setAccountE(CharSequence accountE) {
        this.accountE = accountE;
    }

    public void setEmailE(CharSequence emailE) {
        this.emailE = emailE;
    }

    public void setInformation(InputStream information) {
        if (information != null) {
            try {
                String content = FileUtils.getString(information);
                information.close();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject = new JSONObject();
                }
                this.nickname = jsonObject.optString("name");
                this.gender = jsonObject.optString("gender");
                this.whatsup = jsonObject.optString("whatIsUp");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MSCRApplication.getContext(), MSCRApplication.getContext().getString(R.string.toast_failed_to_get_information), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setNickname(CharSequence nickname) {
        this.nickname = nickname;
    }

    public void setGender(CharSequence gender) {
        this.gender = gender;
    }

    public void setWhatsup(CharSequence whatsup) {
        this.whatsup = whatsup;
    }

    public void setCanConnectToServer(boolean canConnectToServer) {
        this.canConnectToServer = canConnectToServer;
    }


    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public boolean isCanLogin() {
        return canLogin;
    }

    public boolean isLogined() {
        return logined;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public CharSequence getAccountE() {
        return accountE.toString().toLowerCase();
    }

    public CharSequence getEmailE() {
        return Utils.valueOf(emailE).toLowerCase();
    }

    public CharSequence getNickname() {
        return nickname;
    }

    public CharSequence getWhatsup() {
        return whatsup;
    }

    public CharSequence getGender() {
        return gender;
    }

    public boolean isCanConnectToServer() {
        return canConnectToServer;
    }

}
