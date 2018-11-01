package com.huixiangshenghuo.app.comm.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by lenovo on 2017/1/5.
 */
public class APPTool {

    public static boolean isAppRunningForeground(Context context){
        ActivityManager var1 = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
        try {
            List var2 = var1.getRunningTasks(1);
            if(var2 != null && var2.size() >= 1) {
                boolean var3 = context.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo)var2.get(0)).baseActivity.getPackageName());
                return var3;
            } else {
                return false;
            }
        } catch (SecurityException var4) {
            var4.printStackTrace();
            return false;
        }
    }
}
