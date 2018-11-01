package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;

/**
 * Created by Administrator on 2017/6/2.
 * 修改昵称
 */

public class ActivityNickName extends BaseActivity {
   //文本框 昵称

   EditText et_name;
   /**
    * 传递过来的昵称
    */
   private String name;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_nickname);

      initView();


   }


   public void initView() {
      initTitleBar_1();
      Intent intent = getIntent();
      /**
       * 传递过来的昵称
       */
      name = intent.getStringExtra("name");

      titleView.setText("修改昵称");
      actionButton.setText("保存");
      actionButton.setVisibility(View.VISIBLE);
      et_name = (EditText) findViewById(R.id.et_name_nickname);
      et_name.setText(name);

      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            // "姓名"+name name 是你要传递的数据
            intent.putExtra("name", et_name.getText().toString().trim());
            // 这个 0 是个标识
            ActivityNickName.this.setResult(-1, intent);
            ActivityNickName.this.finish();
         }
      });

   }
}
