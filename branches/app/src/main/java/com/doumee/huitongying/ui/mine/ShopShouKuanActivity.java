package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.bitmap.CuttingBitmap;
import com.huixiangshenghuo.app.comm.bitmap.QRCodeTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.home.ShouKuanActivity;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

/**
 * Created by Administrator on 2017/6/15.
 * 商家收款码
 */

public class ShopShouKuanActivity extends BaseActivity {

   private ImageView imageView;
   private Bitmap defaultBitmap;
   private UserInfoResponseParam userInfo;
   private String filePath;

   public static void startShouKuanActivity(Context context) {
      Intent intent = new Intent(context, ShouKuanActivity.class);
      context.startActivity(intent);
   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shou_kuan);
      defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.header_img_bg);
      filepath();
      initTitleBar_1();
      initView();

   }

   private void filepath() {
      String path = CustomConfig.IMAGE_PATH;
      File f = new File(path);
      if (!f.exists()) {  //如果文件夹不存在，创建文件夹
         f.mkdirs();
      }
      filePath = CustomConfig.USER_CODE_IMAGE;
   }


   private void initView() {
      titleView.setText("收款");
      imageView = (ImageView) findViewById(R.id.code_image);
   }

   @Override
   protected void onStart() {
      super.onStart();
      userInfo = SaveObjectTool.openUserInfoResponseParam();
//      File file = new File(filePath);
//      if (file.exists()){
//         imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
//         return;
//      }
      String face = userInfo.getImgUrl();
      if (!TextUtils.isEmpty(face)) {
         ImageLoader.getInstance().loadImage(face, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
               super.onLoadingComplete(imageUri, view, loadedImage);
               defaultBitmap = CuttingBitmap.toRoundBitmap(loadedImage);
               createImage();
            }
         });
      } else {
         createImage();
      }
   }

   private void createImage() {
      new Thread(new Runnable() {
         @Override
         public void run() {
            boolean success = QRCodeTool.createQRImage(userInfo.getShopId(), 800, 800, defaultBitmap, filePath);
            if (success) {
               runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                  }
               });
            }
         }
      }).start();
   }
}
