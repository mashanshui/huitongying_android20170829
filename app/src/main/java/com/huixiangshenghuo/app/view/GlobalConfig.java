package com.huixiangshenghuo.app.view;

import android.content.Context;

/**
 * Created by Administrator on 2017/2/14.
 */

public class GlobalConfig {
   private static Context appContext;

   public static void setAppContext(Context context) {
      appContext = context;
   }

   public static Context getAppContext() {
      return appContext;
   }
}
