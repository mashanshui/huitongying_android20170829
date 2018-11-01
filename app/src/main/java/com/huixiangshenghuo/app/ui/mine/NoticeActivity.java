package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.shop.ShopAdEditRequestObject;
import com.doumee.model.request.shop.ShopAdEditRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;

/**
 * Created by Administrator on 2017/2/6.
 */

public class NoticeActivity extends BaseActivity {

   private EditText contentView;
   public static String ADCONTENT = "adContent";
   private String adContent;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_notice);
      Intent intent = getIntent();
      adContent = intent.getStringExtra(ADCONTENT);

      initView();
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("公告管理");
      contentView = (EditText) findViewById(R.id.content);
      contentView.setText(adContent);
      actionButton.setText("保存");
      actionButton.setVisibility(View.VISIBLE);
      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            submit();
         }
      });
   }

   private void submit() {
      String content = contentView.getText().toString().trim();
      if (TextUtils.isEmpty(content)) {
         ToastView.show("请输入公告管理");
         return;
      }
      request();
   }

   private void request() {
      ShopAdEditRequestObject shopAdEditRequestObject = new ShopAdEditRequestObject();
      ShopAdEditRequestParam shopAdEditRequestParam = new ShopAdEditRequestParam();
      shopAdEditRequestParam.setAdContent(contentView.getText().toString().trim());
      shopAdEditRequestObject.setParam(shopAdEditRequestParam);
      httpTool.post(shopAdEditRequestObject, URLConfig.SHOP_AD_EDIT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {

            Toast.makeText(NoticeActivity.this, "添加成功", Toast.LENGTH_LONG).show();
            finish();
         }

         @Override
         public void onError(ResponseBaseObject o) {

         }
      });
   }
}
