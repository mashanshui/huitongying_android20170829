package com.huixiangshenghuo.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.WindowManager;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;


import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    @Deprecated
    private int maxTime = 3;

    private int status = CustomConfig.LOGOUT;
    private SharedPreferences sharedPreferences;
    private int startCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        status = sharedPreferences.getInt(CustomConfig.APP_LOGIN_STATUS,CustomConfig.LOGOUT);
        startCount = sharedPreferences.getInt(CustomConfig.START_COUNT,0);
       // timer.schedule(timerTask,0,1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startCount > 0){
                    loadUser();
                }else{
                    startActivity( new Intent(SplashActivity.this,GuideActivity.class));
                    finish();
                }
            }
        },CustomConfig.LOAD_TIME);
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message =  handler.obtainMessage();
            message.obj = maxTime;
            handler.sendMessage(message);
            maxTime--;
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(maxTime == 0){
                login();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadUser(){
        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        if (null == userInfoResponseParam){
            userInfoResponseParam = new UserInfoResponseParam();
            userInfoResponseParam.setMemberId("");
        }
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);
        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                UserInfoResponseParam userInfo = o.getMember();
                SaveObjectTool.saveObject(userInfo);
                login();
                LogoutTool.TAG_LIST.clear();
                LogoutTool.TAG_LIST.add(userInfo.getMemberId());
            }
            @Override
            public void onError(MemberInfoResponseObject o) {
                login();
            }
        });
    }


    private void login(){
        if (status == CustomConfig.LOGOUT){
            LoginActivity.startLoginActivity(this,LoginActivity.LOGIN_DEFAULT);
        }else{
            HomeActivity.startHomeActivity(this);
        }
        finish();
    }
}
