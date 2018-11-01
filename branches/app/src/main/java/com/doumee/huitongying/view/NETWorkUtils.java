package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/7/4.
 */

public class NETWorkUtils {


   /**
    * 判断是否有网络连接
    *
    * @param context
    * @return
    */
   public static boolean isNetworkConnected(Context context) {
      if (context != null) {
         ConnectivityManager mConnectivityManager = (ConnectivityManager) context
               .getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
         if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
         }
      }
      return false;
   }


   /**
    * 判断WIFI网络是否可用
    *
    * @param context
    * @return
    */
   public static boolean isWifiConnected(Context context) {
      if (context != null) {
         ConnectivityManager mConnectivityManager = (ConnectivityManager) context
               .getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo mWiFiNetworkInfo = mConnectivityManager
               .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
         if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable();
         }
      }
      return false;
   }

}
