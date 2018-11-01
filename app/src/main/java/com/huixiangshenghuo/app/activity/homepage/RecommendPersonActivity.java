package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.Dialog;

/**
 * Created by Administrator on 2017/5/31.
 * 推荐人
 */

public class RecommendPersonActivity extends BaseActivity implements View.OnClickListener {
   public static String RECPEOPLENAME = "recPeopleName";
   public static String RECPEOPLEPHONE = "recPeoplePhone";

   private String recPeopleName;
   private String recPeoplePhone;

   private RelativeLayout rl_name;
   private TextView tv_name;
   private RelativeLayout rl_phone;
   private TextView tv_phone;
   private TextView tv_qq;

   public static void startRecommendPersonActivity(Context context, String recPeopleName, String recPeoplePhone) {
      Intent intent = new Intent(context, RecommendPersonActivity.class);
      intent.putExtra(RECPEOPLENAME, recPeopleName);
      intent.putExtra(RECPEOPLEPHONE, recPeoplePhone);

      context.startActivity(intent);
   }
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_recommend_person);
      initview();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("我的推荐人");
      recPeopleName = getIntent().getStringExtra(RECPEOPLENAME);
      recPeoplePhone = getIntent().getStringExtra(RECPEOPLEPHONE);
      rl_name = (RelativeLayout) findViewById(R.id.rl_name_recommend_person);
      tv_name = (TextView) findViewById(R.id.tv_name_recommend_person);
      rl_phone = (RelativeLayout) findViewById(R.id.rl_phone_recommend_person);
      tv_phone = (TextView) findViewById(R.id.tv_phone_recommend_person);
      tv_qq = (TextView) findViewById(R.id.tv_qq_recommend_person);
      rl_phone.setOnClickListener(this);
      tv_name.setText(recPeopleName);
      tv_phone.setText(recPeoplePhone);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.rl_phone_recommend_person:
            dh();
            break;
      }
   }

   private void dh() {
      final Dialog dialog1 = new Dialog(this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage(recPeoplePhone);
      dialog1.setConfirmText("呼叫");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + recPeoplePhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog1.dismiss();
         }
      });
      dialog1.show();
   }
}
