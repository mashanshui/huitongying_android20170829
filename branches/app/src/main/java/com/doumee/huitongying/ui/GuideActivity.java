package com.huixiangshenghuo.app.ui;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;
import java.util.List;

//引导页
public class GuideActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button button;
    private RadioGroup radioGroup;
    private List<View> viewList;
    private GuideViewPageAdapter guideViewPageAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        viewPager = (ViewPager)findViewById(R.id.view_page);
        button = (Button)findViewById(R.id.next) ;
        radioGroup = (RadioGroup)findViewById(R.id.item_ico) ;
        viewList = new ArrayList<>();
        guideViewPageAdapter = new GuideViewPageAdapter();
        viewPager.setAdapter(guideViewPageAdapter);
        sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        addItemListView();
        addTypeItem();
        initListener();
    }

    private void addItemListView(){
        for (int image : CustomConfig.GUIDE_IMAGE){
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),image));
            viewList.add(imageView);
        }
        guideViewPageAdapter.notifyDataSetChanged();
    }

    private void addTypeItem() {
      for (int i = 0 ; i < CustomConfig.GUIDE_IMAGE.length ; i++){
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 0, 0, 0);
        RadioButton radioButton = new RadioButton(this);
        radioButton.setLayoutParams(layoutParams);
        radioButton.setId(i);
        if (i == 0) radioButton.setChecked(true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.vipager_doc_select);
        radioButton.setButtonDrawable(drawable);
        radioGroup.addView(radioButton);
        }
    }

    private void initListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
              if (position == CustomConfig.GUIDE_IMAGE.length - 1){
                  button.setVisibility(View.VISIBLE);
              }else{
                  button.setVisibility(View.GONE);
              }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putInt(CustomConfig.START_COUNT,1).commit();
                LoginActivity.startLoginActivity(GuideActivity.this,LoginActivity.LOGIN_DEFAULT);
                finish();
            }
        });
    }

    private class GuideViewPageAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return viewList.size();
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewList.get(position);
            container.addView(view);
            return view;
        }
    }
}
