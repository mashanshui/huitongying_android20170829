package com.huixiangshenghuo.app.comm.http;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.huixiangshenghuo.app.CustomApplication;

/**
 * Created by lenovo on 2016/12/9.
 */
public class SystemUtil {

    public static String getIMEI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) CustomApplication.getCustomApplication()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String appDeviceNumber = telephonyManager.getDeviceId();
            Log.d("appDeviceNumber" , appDeviceNumber);
            if (TextUtils.isEmpty(appDeviceNumber)){
                appDeviceNumber = System.currentTimeMillis()+"";
            }
            Log.d("appDeviceNumber" , appDeviceNumber);
            return appDeviceNumber;
        } catch (Exception e) {
            return System.currentTimeMillis()+"";
        }
    }
}
