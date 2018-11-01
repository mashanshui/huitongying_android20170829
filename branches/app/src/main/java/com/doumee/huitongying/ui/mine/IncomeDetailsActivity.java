package com.huixiangshenghuo.app.ui.mine;

import android.os.Bundle;
import android.widget.ListView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.City;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.view.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 * 收入明细
 */

public class IncomeDetailsActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   private RefreshLayout refreshLayout;
   private ListView listView;

   /**
    * 适配器
    */
   private IncomeDetailsAdapter adapter;
   //数据源
   private List<City> list;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_income_details);
      initView();
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("收入明细");
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
      listView = (ListView) findViewById(R.id.lv_income_details);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);
      initData();
      //构建适配器
      adapter = new IncomeDetailsAdapter(list, this);
      listView.setAdapter(adapter);
   }


   /**
    * 初始化数据源
    */
   public void initData() {

      list = new ArrayList<City>();
      City city1 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city2 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city3 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city4 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city5 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city6 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city7 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city8 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      City city9 = new City("订单123", "2016-04-12 14:30", "+200", R.mipmap.icon, "");
      list.add(city1);
      list.add(city2);
      list.add(city3);
      list.add(city4);
      list.add(city5);
      list.add(city6);
      list.add(city7);
      list.add(city8);
      list.add(city9);
   }

   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
   }

   @Override
   public void onLoad() {
      refreshLayout.setRefreshing(true);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
   }
}
