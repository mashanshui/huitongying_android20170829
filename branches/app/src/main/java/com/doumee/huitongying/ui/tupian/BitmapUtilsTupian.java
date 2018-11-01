package com.huixiangshenghuo.app.ui.tupian;

import android.content.Context;

import com.huixiangshenghuo.app.R;
import com.lidroid.xutils.BitmapUtils;

/**
 * Created by Administrator on 2017/6/6.
 * 加载默认图片
 */

public class BitmapUtilsTupian {
   BitmapUtils bitmapUtils;
   Context context;

   public BitmapUtilsTupian(BitmapUtils bitmapUtils, Context context) {
      this.bitmapUtils = bitmapUtils;
      this.context = context;
   }

   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(context);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);

   }
}
