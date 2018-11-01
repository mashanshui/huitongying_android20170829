package com.huixiangshenghuo.app.ui;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.MyUpdateDialog;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.app.PermissionTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.fragments.HomeMallFragment;
import com.huixiangshenghuo.app.ui.fragments.HomeMineFragment;
import com.huixiangshenghuo.app.ui.fragments.HomePagenewFragment;
import com.huixiangshenghuo.app.ui.fragments.HomeShopRefreshNewFragment;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.io.File;
import java.util.ArrayList;


public class HomeActivity extends BaseActivity {

    private long exitTime = 0;
    private ViewPager viewPager;
    private RadioGroup viewTab;
    private ArrayList<Fragment> fragments;
    private SharedPreferences sharedPreferences;
    public static final String REFRESH_USER = "com.doumee.huitongying.refresh.user";
    private RefreshUserReceiver userReceiver;


    //发送刷新用户广播
    public static void sendRefreshUserBroadcast(Context context){
        Intent intent = new Intent();
        intent.setAction(REFRESH_USER);
        context.sendBroadcast(intent);
    }

    public static void startHomeActivity(Context context){
        Intent intent = new Intent(context,HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }

        setContentView(R.layout.activity_home);
        initView();
        initAdapter();
        //版本更新
        MyUpdateDialog.checkVersion(HomeActivity.this, 0);
        initBaiduPush();
        sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        userReceiver = new RefreshUserReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REFRESH_USER);
        registerReceiver(userReceiver,intentFilter);
    }


    private void initAdapter(){
        fragments = new ArrayList<>();
//        fragments.add(HomePageFragment.newInstance("",""));
        fragments.add(HomePagenewFragment.newInstance("", ""));
        fragments.add(HomeMallFragment.newInstance("", ""));
       // fragments.add(HomeShopFragment.newInstance("1",""));
        fragments.add(HomeShopRefreshNewFragment.newInstance("1"));
//        fragments.add(HomeShopRefreshFragment.newInstance("1"));
        fragments.add(HomeMineFragment.newInstance("",""));
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.view_page);
        viewTab = (RadioGroup)findViewById(R.id.home_tab);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        viewTab.check(R.id.home_page);
                        break;
                    case 1:
                        viewTab.check(R.id.home_mall);
                        break;
                    case 2:
                        viewTab.check(R.id.home_shop);
                        break;
                    case 3:
                        viewTab.check(R.id.home_mine);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.home_page :
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.home_mall:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.home_shop:
                        viewPager.setCurrentItem(2);
                        break;

                    case R.id.home_mine:
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000){
            exitTime = System.currentTimeMillis();
            ToastView.show( "再按一次退出");
        }else{
            File file = new File(CustomConfig.USER_CODE_IMAGE);
            if (file.exists()){
                boolean oo =  file.delete();
                Log.d("删除收款二维码","删除结果："+oo);
            }
            File shareFile = new File(CustomConfig.USER_SHARE_IMAGE);
            if (shareFile.exists()){
                boolean oo =  shareFile.delete();
                Log.d("删除我的二维码","删除结果："+oo);
            }
            super.onBackPressed();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }
    }

    @TargetApi(23)
    private void checkPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ToastView.show("请开启权限，否则可能无法使用部分功能");
            requestPermissions(new String[]{ Manifest.permission.CAMERA ,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                   }, PermissionTool.CAMERA);
        }
    }

    //初始化百度推送
    private void initBaiduPush(){
        LogoutTool.TAG_LIST.clear();
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        LogoutTool.TAG_LIST.add(user.getMemberId());
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "1m8PRDmtbB55yG1vtiYtDkRP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(userReceiver);
    }

    private class RefreshUserReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   loadUser();
               }
           }).start();
       }
   }


    private void loadUser(){
       final UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);
        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                UserInfoResponseParam userInfo = o.getMember();
                userInfo.setCityId(userInfoResponseParam.getCityId());
                userInfo.setCityName(userInfoResponseParam.getCityName());
                SaveObjectTool.saveObject(userInfo);
            }
            @Override
            public void onError(MemberInfoResponseObject o) {

            }
        });
    }

}
