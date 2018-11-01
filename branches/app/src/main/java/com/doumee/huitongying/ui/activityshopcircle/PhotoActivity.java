package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.PhotoFragment;

import java.util.ArrayList;

public class PhotoActivity extends BaseActivity implements ViewPager.OnPageChangeListener  {



    private ArrayList<String> imageList;
    private CustomFragmentPagerAdapter adapter;
    private ArrayList<Fragment> fragments;
    private int pageNo;
    private TextView textView;
    private ViewPager viewPager;

    public static void startPhotoActivity(Context context,String path,ArrayList<String> imageList){
        Intent intent = new Intent(context,PhotoActivity.class);
        intent.putExtra("path",path);
        intent.putStringArrayListExtra("data",imageList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageList = getIntent().getStringArrayListExtra("data");
        String currentItem =  getIntent().getStringExtra("path");
        pageNo = imageList.indexOf(currentItem);
        fragments = new ArrayList<>();
        for (String path : imageList){
            PhotoFragment photoFragment = PhotoFragment.newInstance(path);
            fragments.add(photoFragment);
        }
        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        initView();

    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("图片查看");
        textView = (TextView) findViewById(R.id.image_size);
        viewPager = (ViewPager)findViewById(R.id.view_page);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pageNo);
        textView.setText((pageNo+1) + "/"+imageList.size());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onPageSelected(int i) {
        textView.setText((i+1)+"/"+imageList.size());
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
}
