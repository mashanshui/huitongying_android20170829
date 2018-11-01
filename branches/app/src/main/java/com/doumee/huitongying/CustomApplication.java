package com.huixiangshenghuo.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.huixiangshenghuo.app.view.GlobalConfig;
import com.mob.MobApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/12/9.
 */
public class CustomApplication extends MobApplication {


    private static CustomApplication application;
    private ArrayList<String> searchList;//存储搜索记录
    private static Bitmap emptyDataBitmap;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        searchList = new ArrayList<>();
        SDKInitializer.initialize(getApplicationContext());
        initLoadImage();
        //===============================
        GlobalConfig.setAppContext(this);
        //===============================
    }

    //4.0 以下的要 重写该方法 不然 ftp 无法使用
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static CustomApplication getCustomApplication(){
        return  application;
    }
    public static SharedPreferences getAppUserSharedPreferences(){
        return application.getSharedPreferences(CustomConfig.APP_INFO, MODE_PRIVATE);
    }

    /**
     *保存用户选择的城市信息
     */
    public static SharedPreferences getAppUserSharedPreferencesCity() {
        return application.getSharedPreferences(CustomConfig.CITY_CONTENT, MODE_WORLD_WRITEABLE);
    }

    /**
     * 空记录默认图标
     * @return
     */
    public static Bitmap getEmptyDataBitmap(){
        if (null == emptyDataBitmap){
            emptyDataBitmap = BitmapFactory.decodeResource(getCustomApplication().getResources(), R.mipmap.gwc_default);
        }
        return  emptyDataBitmap;
    }

    //初始化图片加载器
    private void initLoadImage(){

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
              .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
/*.cacheInMemory(true)*/
        ImageLoaderConfiguration  config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(5)//线程数，最好是1-5
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(20 * 1024 * 1024)
                .build();

        ImageLoader.getInstance().init(config);

    }
    public ArrayList<String> getSearchList(){
        return  searchList;
    }
}
