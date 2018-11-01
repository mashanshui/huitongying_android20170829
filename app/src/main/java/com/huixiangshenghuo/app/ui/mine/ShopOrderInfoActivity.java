package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.OrderInfoFragment;
import com.huixiangshenghuo.app.ui.fragments.OrderStateFragment;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestParam;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/14.
 */

public class ShopOrderInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {


   RadioButton stateSButton;

   RadioButton infoSButton;

   ViewPager viewPager;

   RadioGroup radioGroup;

   private String orderNo;//订单编号
   private ArrayList<Fragment> fragmentArrayList;
   private CustomFragmentPagerAdapter adapter;
   private String Type;

   public static void startOrderInfoActivity(Context context, String orderNo, String Type) {
      Intent intent = new Intent(context, ShopOrderInfoActivity.class);
      intent.putExtra("orderNo", orderNo);
      intent.putExtra("type", Type);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shop_order_info);
      orderNo = getIntent().getStringExtra("orderNo");
      Type = getIntent().getStringExtra("type");
      initView();
      loadData();
   }


   public void initView() {


      initTitleBar_1();
      titleView.setText("订单详情");

      stateSButton = (RadioButton) findViewById(R.id.state_s);
      infoSButton = (RadioButton) findViewById(R.id.info_s);
      viewPager = (ViewPager) findViewById(R.id.view_page);
      radioGroup = (RadioGroup) findViewById(R.id.group);


      initListener();
      fragmentArrayList = new ArrayList<>();

      adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
      viewPager.setAdapter(adapter);
      viewPager.addOnPageChangeListener(this);
   }

   private void initListener() {
      radioGroup.setOnCheckedChangeListener(this);

   }

   @Override
   public void onCheckedChanged(RadioGroup group, int checkedId) {
      switch (checkedId) {
         case R.id.state:
            stateSButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            infoSButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            viewPager.setCurrentItem(0);
            break;

         case R.id.info:
            stateSButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            infoSButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            viewPager.setCurrentItem(1);
            break;
      }
   }

   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
   }

   @Override
   public void onPageSelected(int position) {
   }

   @Override
   public void onPageScrollStateChanged(int state) {
      if (state == 2) {
         switch (viewPager.getCurrentItem()) {
            case 0:
               radioGroup.check(R.id.state);
               break;
            case 1:
               radioGroup.check(R.id.info);
               break;
         }
      }
   }

   private void loadData() {
      showProgressDialog("加载中..");
      GoodsOrderInfoRequestParam goodsOrderInfoRequestParam = new GoodsOrderInfoRequestParam();
      goodsOrderInfoRequestParam.setOrderId(Long.parseLong(orderNo));
      GoodsOrderInfoRequestObject goodsOrderInfoRequestObject = new GoodsOrderInfoRequestObject();
      goodsOrderInfoRequestObject.setParam(goodsOrderInfoRequestParam);
      httpTool.post(goodsOrderInfoRequestObject, URLConfig.ORDER_INFO, new HttpTool.HttpCallBack<GoodsOrderInfoResponseObject>() {
         @Override
         public void onSuccess(GoodsOrderInfoResponseObject o) {
            dismissProgressDialog();
            GoodsOrderInfoResponseParam goodsOrderInfoResponseParam = o.getRecord();

//            fragmentArrayList.add(OrderStateFragment.newInstance(goodsOrderInfoResponseParam));
            fragmentArrayList.add(OrderStateFragment.newInstance(goodsOrderInfoResponseParam, o.getRecord().getKdCode(), ""));//加载快递号 和 快递名称
            fragmentArrayList.add(OrderInfoFragment.newInstance(goodsOrderInfoResponseParam));
            adapter.notifyDataSetChanged();
         }

         @Override
         public void onError(GoodsOrderInfoResponseObject o) {
            dismissProgressDialog();
            Toast.makeText(ShopOrderInfoActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }
}
