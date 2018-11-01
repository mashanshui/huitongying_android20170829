package com.huixiangshenghuo.app.comm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.baidu.android.pushservice.PushManager;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.ui.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.File;
import java.util.LinkedList;

/**
 * 退出登录
 * Created by lenovo on 2016/10/24.
 */
public class LogoutTool {


    public static LinkedList<Activity> activities = new LinkedList<>();
    public static LinkedList<String> TAG_LIST = new LinkedList<>();

    public static void logout(Context context){
        CustomApplication.getAppUserSharedPreferences().edit()
                .putInt(CustomConfig.APP_LOGIN_STATUS,CustomConfig.LOGOUT).commit();
        for (Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        clearData(context);
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void autoLogin(Context context){
        CustomApplication.getAppUserSharedPreferences().edit()
                .putInt(CustomConfig.APP_LOGIN_STATUS,CustomConfig.LOGOUT).commit();
        for (Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        clearData(context);
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("loginType",LoginActivity.LOGIN_AUTO);
        context.startActivity(intent);
    }

    private static void clearData(Context context){
        SharedPreferences sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        sharedPreferences.edit().remove(CustomConfig.MESSAGE_COUNT).remove(CustomConfig.APP_IS_RUNNING).commit();
        activities.clear();
        File file = new File(CustomConfig.USER_CODE_IMAGE);
        if (file.exists()){
            file.delete();
        }
        File shareFile = new File(CustomConfig.USER_SHARE_IMAGE);
        if (shareFile.exists()){
            shareFile.delete();
        }
        PushManager.delTags(context,TAG_LIST);
        PushManager.stopWork(context);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }
}


