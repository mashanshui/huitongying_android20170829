package com.huixiangshenghuo.app.comm.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * 6.0获取权限工具类
 * Created by lenovo on 2016/12/1.
 */
public class PermissionTool {

    private Activity context;
    public static final int READ_PHONE_STATE = 1001;//获取手机状态
    public static final int ACCESS_COARSE_LOCATION = 1002;//获取位置信息
    public static final int WRITE_EXTERNAL_STORAGE = 1005;//写SD卡
    public static final int CAMERA = 1006;//拍照
    public static final int RECORD_AUDIO = 1007;

    public static final String CAMERA_TIPS = "请开启拍照权限，否则可能无法使用拍照功能";
    public static final String RECORD_AUDIO_TIPS = "请开启录音权限，否则可能无法使用语音功能";



    public PermissionTool(Activity context){
        this.context = context;
    }

    //获取读取号码权限
    @TargetApi(23)
    public void checkReadPhoneState(){
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            context.requestPermissions(new String[]{ Manifest.permission.READ_PHONE_STATE },READ_PHONE_STATE);
        }
    }
    //获取地理位置权限
    @TargetApi(23)
    public void checkCoarseLocation(){
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            context.requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION ,
                    Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_COARSE_LOCATION);
        }
    }

    //获取写SD卡权限
    @TargetApi(23)
    public void checkWriteSDStore(){
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            context.requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                         Manifest.permission.READ_EXTERNAL_STORAGE  },WRITE_EXTERNAL_STORAGE);
        }
    }
}
