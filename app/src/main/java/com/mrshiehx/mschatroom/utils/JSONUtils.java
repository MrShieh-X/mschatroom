package com.mrshiehx.mschatroom.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

//JSON工具类

public class JSONUtils {
    /**
     * 修改Json数据
     *
     * @param key    更换数据key
     * @param value  更换Value
     * @param object 解析对象
     */
    public static void analyzeJson(String key, Object value, Object object) {
        try {
            if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    analyzeJson(key, value, jsonObject);
                }
            } else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String jsonKey = iterator.next().toString();
                    Object ob = jsonObject.get(jsonKey);
                    if (ob != null) {
                        if (ob instanceof JSONArray) {
                            analyzeJson(key, value, ob);
                        } else if (ob instanceof JSONObject) {
                            analyzeJson(key, value, ob);
                        } else {
                            if (jsonKey.equals(key)) {
                                jsonObject.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
