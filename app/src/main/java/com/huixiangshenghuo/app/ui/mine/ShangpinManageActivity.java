package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/6.
 * 商品管理
 */

public class ShangpinManageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

   ViewPager viewpage_mycollect;

   RadioGroup rg_mycollect;

   RadioButton rb_mycollect_sj;

   RadioButton rb_mycollect_sp;

   ImageView iv_mycollect_fh;

   private Button bt_tianjia;


   private MyCollectFragmentPagerAdapter mAdapter;
   private ArrayList<Fragment> mList;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_mycollect);
      initView();
   }

   public void initView() {

      //   View view = View.inflate(this, R.layout.activity_mycollect, null);

      viewpage_mycollect = (ViewPager) findViewById(R.id.viewpage_mycollect);
      rg_mycollect = (RadioGroup) findViewById(R.id.rg_mycollect);
      rb_mycollect_sj = (RadioButton) findViewById(R.id.rb_mycollect_sj);
      rb_mycollect_sp = (RadioButton) findViewById(R.id.rb_mycollect_sp);
      iv_mycollect_fh = (ImageView) findViewById(R.id.iv_mycollect_fh);
      bt_tianjia = (Button) findViewById(R.id.bt_mycollect_tianjia);

      iv_mycollect_fh.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
         }
      });
      bt_tianjia.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ShangpinManageActivity.this, AddShangpinActivity.class));
         }
      });
      configView();
   }

   private void configView() {
      mList = new ArrayList<Fragment>();
      mList.add(new SalesFragment());
      mList.add(new yixiajiaFragment());
      mAdapter = new MyCollectFragmentPagerAdapter(getSupportFragmentManager(), mList);
      viewpage_mycollect.setAdapter(mAdapter);
      viewpage_mycollect.setCurrentItem(0);
      viewpage_mycollect.addOnPageChangeListener(this);

      rg_mycollect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.rb_mycollect_sj:
                  viewpage_mycollect.setCurrentItem(0);
                  break;
               case R.id.rb_mycollect_sp:
                  viewpage_mycollect.setCurrentItem(1);
                  break;
            }
         }
      });
   }

   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

   }

   @Override
   public void onPageSelected(int position) {
      switch (position) {
         case 0:
            rb_mycollect_sj.setChecked(true);
            break;
         case 1:
            rb_mycollect_sp.setChecked(true);
            break;
      }
   }

   @Override
   public void onPageScrollStateChanged(int state) {

   }
}
