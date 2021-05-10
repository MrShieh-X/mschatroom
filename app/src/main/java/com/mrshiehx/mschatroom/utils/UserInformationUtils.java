package com.mrshiehx.mschatroom.utils;

import android.content.Context;
import android.widget.Toast;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.beans.UserInformation;

import org.json.JSONObject;

//XML工具类
public class UserInformationUtils {
    public static UserInformation read(Context context, byte[] file) {
        try {
            String str = new String(file);
            JSONObject jsonObject = new JSONObject(str);
            System.out.println("fuck"+18);
            return new UserInformation(jsonObject.optString("name"), jsonObject.optString("gender"), jsonObject.optString("whatIsUp"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fuck"+22);
            Toast.makeText(context, MSChatRoom.getContext().getString(R.string.dialog_exception_parsing_json_failed), Toast.LENGTH_SHORT).show();
        }
        System.out.println("fuck"+25);
        return new UserInformation("", "", "");
    }
}
