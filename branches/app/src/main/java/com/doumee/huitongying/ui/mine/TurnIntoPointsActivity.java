package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;

/**
 * Created by Administrator on 2017/8/29.
 * <p>
 * 转入积分
 */

public class TurnIntoPointsActivity extends BaseActivity {

   public static void startTurnIntoPointsActivity(Context context) {
      Intent intent = new Intent(context, TurnIntoPointsActivity.class);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_turn_into_points);

      initView();


   }

   private void initView() {
      initTitleBar_1();
      titleView.setText(getString(R.string.title_change_into));
   }
}
