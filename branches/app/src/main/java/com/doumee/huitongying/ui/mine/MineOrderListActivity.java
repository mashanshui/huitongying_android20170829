package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.mine.MineOrderListFragment;

import java.util.ArrayList;

public class MineOrderListActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private RadioGroup tabBar;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private CustomFragmentPagerAdapter adapter;
    //Char(1)	订单状态(0待确认1待发货2待使用3已发货4已完成5已取消6已退款)
    public static final int ORDER_START_ALL = -1;//全部
    public static final int ORDER_STATE_START = 0;//未付款
    public static final int ORDER_STATE_DELIVERY = 1;//待发货
    public static final int ORDER_STATE_GOODS = 3;//代收货
    public static final int ORDER_STATE_END = 4;//已完成

    public static void startMineOrderListActivity(Context context){
        Intent intent = new Intent(context,MineOrderListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order_list);
        fragments = new ArrayList<>();
        fragments.add(MineOrderListFragment.newInstance(ORDER_START_ALL));//全部
        fragments.add(MineOrderListFragment.newInstance(ORDER_STATE_START));//未支付
        fragments.add(MineOrderListFragment.newInstance(ORDER_STATE_DELIVERY));//待发货
        fragments.add(MineOrderListFragment.newInstance(ORDER_STATE_GOODS));//代收货
        fragments.add(MineOrderListFragment.newInstance(ORDER_STATE_END));//已完成
        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("我的订单");
        tabBar = (RadioGroup)findViewById(R.id.tab_bar);
        viewPager = (ViewPager)findViewById(R.id.view_page);
        tabBar.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            case R.id.tab0:
                viewPager.setCurrentItem(0);
                break;

            case R.id.tab1:
                viewPager.setCurrentItem(1);
                break;

            case R.id.tab2:
                viewPager.setCurrentItem(2);
                break;
            case R.id.tab3:
                viewPager.setCurrentItem(3);
                break;
            case R.id.tab4:
                viewPager.setCurrentItem(4);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position){
            case 0:
                tabBar.check(R.id.tab0);
                break;
            case 1:
                tabBar.check(R.id.tab1);
                break;
            case 2:
                tabBar.check(R.id.tab2);
                break;
            case 3:
                tabBar.check(R.id.tab3);
                break;
            case 4:
                tabBar.check(R.id.tab4);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
}
