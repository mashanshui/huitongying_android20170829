package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;

import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 * 商家详情  用户协议
 */
public class ShopInfoActivity extends BaseActivity {
   private WebView tv_info;
   private static final String INFO = "info";
   private static final String TYPE = "type";
   /**
    * 简介
    */
   private String info;
   /**
    * 类型
    */
   private String type;// 1 商户详情 2 商户协议 3用户协议

   public static void startShopInfoActivity(Context context, String info, String type) {
      Intent intent = new Intent(context, ShopInfoActivity.class);
      intent.putExtra(INFO, info);
      intent.putExtra(TYPE, type);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shop_info);
      info = getIntent().getStringExtra(INFO);
      type = getIntent().getStringExtra(TYPE);
      initTitleBar_1();
      initView();
   }

   private void initView() {
      tv_info = (WebView) findViewById(R.id.tv_shop_info);
      if (type.equals("1")) {
         titleView.setText("商家详情");
         tv_info.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);
      } else if (TextUtils.equals("2",type)){
         titleView.setText("商户协议详情");
         loadDataIndex();
      }else{
         titleView.setText("用户协议详情");
         loadDataIndex();
      }
   }

   //加载数据字典
   public void loadDataIndex() {
      AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
      AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
      appDicInfoRequestObject.setParam(appDicInfoParam);
      httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
         @Override
         public void onSuccess(AppConfigureResponseObject o) {
            List<AppConfigureResponseParam> dataList = o.getDataList();
            for (AppConfigureResponseParam app : dataList) {
               if (TextUtils.equals("3",type)){
                  if (app.getName().equals(CustomConfig.SYS_PROTOCOL)) {
                     tv_info.loadDataWithBaseURL(null,  app.getContent(), "text/html", "utf-8", null);
                     break;
                  }
               }else if (TextUtils.equals("2",type)){
                  if (app.getName().equals(CustomConfig.SHOP_PROTOCOL)) {
                     tv_info.loadDataWithBaseURL(null,  app.getContent(), "text/html", "utf-8", null);
                     break;
                  }
               }
            }
         }
         @Override
         public void onError(AppConfigureResponseObject o) {

         }
      });
   }


}
