package com.mrshiehx.mschatroom.utils;

import android.content.Context;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//XML工具类
public class UserInformationUtils {
    public static UserInformation read(Context context, InputStream file) {
        try {
            StringBuilder sb = new StringBuilder();
            String line;

            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String str = sb.toString();
            //System.out.println("fuckfuck="+FileUtils.getString(file));
            //String str=new String(bytes);
            JSONObject jsonObject = new JSONObject(str);
            return new UserInformation(jsonObject.optString("name"), jsonObject.optString("gender"), jsonObject.optString("whatIsUp"));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, MSCRApplication.getContext().getString(R.string.dialog_exception_parsing_json_failed));
        }
        return null;
    }

    public static UserInformation read(Context context, byte[] file) {
        try {
            String str = new String(file);
            //String str=new String(bytes);
            JSONObject jsonObject = new JSONObject(str);
            return new UserInformation(jsonObject.optString("name"), jsonObject.optString("gender"), jsonObject.optString("whatIsUp"));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(context, e, MSCRApplication.getContext().getString(R.string.dialog_exception_parsing_json_failed));
        }
        return null;
    }
}
