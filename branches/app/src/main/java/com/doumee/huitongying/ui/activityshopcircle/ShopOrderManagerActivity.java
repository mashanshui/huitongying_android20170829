package com.huixiangshenghuo.app.ui.activityshopcircle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.ShopOrderManagerFragment;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;

public class ShopOrderManagerActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener{


    private RadioGroup tabBar;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private CustomFragmentPagerAdapter adapter;
    public static final int TAB_0 = -1;//全部
    public static final int TAB_1 = 0;//待付款
    public static final int TAB_2 = 1;//待发货
    public static final int TAB_3 = 3;//待收货
    public static final int TAB_4 = 4;//已完成


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order_manager);
        fragments = new ArrayList<>();

        fragments.add(ShopOrderManagerFragment.newInstance(TAB_0));
       // fragments.add(ShopOrderManagerFragment.newInstance(TAB_1));
        fragments.add(ShopOrderManagerFragment.newInstance(TAB_2));
        fragments.add(ShopOrderManagerFragment.newInstance(TAB_3));
        fragments.add(ShopOrderManagerFragment.newInstance(TAB_4));
        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("订单管理");
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

           /* case R.id.tab1:
                viewPager.setCurrentItem(1);
                break;*/

            case R.id.tab2:
                viewPager.setCurrentItem(1);
                break;

            case R.id.tab3:
                viewPager.setCurrentItem(2);
                break;

            case R.id.tab4:
                viewPager.setCurrentItem(3);
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
            /*case 1:
                tabBar.check(R.id.tab1);
                break;*/
            case 1:
                tabBar.check(R.id.tab2);
                break;
            case 2:
                tabBar.check(R.id.tab3);
                break;
            case 3:
                tabBar.check(R.id.tab4);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

}
