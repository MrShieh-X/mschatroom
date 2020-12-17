package com.mrshiehx.mschatroom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/***
 * @Description: 实现真正退出应用程序，关闭所有Activity和全局获得Context
 */

public class MyApplication extends Application {
    //对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList实现了基于动态数组的数据结构，要移动数据。LinkedList基于链表的数据结构,便于增加删除
    private List<Activity> activityList = new LinkedList<Activity>();
    private static MyApplication instance;
    static Context context;
    public MyApplication() {
    }

    //单例模式中获取唯一的MyApplication实例
    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}