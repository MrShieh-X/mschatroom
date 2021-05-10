package com.mrshiehx.mschatroom.beans;

import android.widget.Toast;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.utils.StringUtils;
import com.mrshiehx.mschatroom.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 当离线模式时，全部界面使用这个账户信息
 */
public class AccountInformation {
    CharSequence accountE;
    CharSequence emailE;
    CharSequence nickname;
    CharSequence whatsup;
    CharSequence gender;

    public void setAccountE(CharSequence accountE) {
        this.accountE = accountE;
    }

    public void setEmailE(CharSequence emailE) {
        this.emailE = emailE;
    }

    public void setInformation(byte[] information) {
        if (information != null&&information.length!=0&&!Utils.isBytesAllZero(information)) {
            try {
                String content = StringUtils.bytesToString(information);
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
                Toast.makeText(MSChatRoom.getContext(), MSChatRoom.getContext().getString(R.string.toast_failed_to_get_information), Toast.LENGTH_SHORT).show();
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

    public CharSequence getAccountE() {
        return accountE;
    }

    public CharSequence getEmailE() {
        return emailE;
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
}
