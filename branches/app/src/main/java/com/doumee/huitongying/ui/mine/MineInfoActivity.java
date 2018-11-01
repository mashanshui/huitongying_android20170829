package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collection;

public class MineInfoActivity extends BaseActivity implements View.OnClickListener{

    private SharedPreferences sharedPreferences;
    private TextView tab1View, tab2View, tab3View, tab4View, tab5View, tab6View;
    private Button button;


    public static void startMineInfoActivity(Context context){
        Intent intent = new Intent(context,MineInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_info);
        sharedPreferences = CustomApplication.getAppUserSharedPreferencesCity();
        initTitleBar_1();
        initView();
    }

    private void initView(){
        titleView.setText("我的信息");
        tab1View = (TextView)findViewById(R.id.tab1);
        tab2View = (TextView)findViewById(R.id.tab2);
        tab3View = (TextView)findViewById(R.id.tab3);
        tab4View = (TextView)findViewById(R.id.tab4);
        tab5View = (TextView)findViewById(R.id.tab5);
        tab6View = (TextView) findViewById(R.id.tab6);
        button = (Button)findViewById(R.id.logout);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
        tab3View.setOnClickListener(this);
        tab4View.setOnClickListener(this);
        tab5View.setOnClickListener(this);
        tab6View.setOnClickListener(this);
        button.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        String idCardCheckStatus = userInfo.getIdCardCheckStatus();
        if (TextUtils.equals("0",idCardCheckStatus)){
            idCardCheckStatus = "（未认证）";
        }else if (TextUtils.equals("1",idCardCheckStatus)){
            idCardCheckStatus = "（已提交审核）";
        }else if (TextUtils.equals("2",idCardCheckStatus)){
            idCardCheckStatus = "（已认证）";
        }else if (TextUtils.equals("3",idCardCheckStatus)){
            idCardCheckStatus = "（认证失败）";
        }
        tab2View.setText("实名认证"+idCardCheckStatus);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1:
                MyInfoActivity.startMyInfoActivity(this);
                break;
            case R.id.tab2://安全认证
                MineRealActivity.startMineRealActivity(this);
                break;
            case R.id.tab3:
                SetPassActivity.startSetPassActivity(this);
                break;
            case R.id.tab4:
                MineCodeActivity.startMineCodeActivity(this);
                break;
            case R.id.tab5:
                ImageLoader.getInstance().getMemoryCache().clear();
                onStart();
                break;
            case R.id.tab6:
                MineBankCardActivity.startMineBankCardActivity(this);
                break;
            case R.id.logout:
                sharedPreferences.edit().clear().commit();
                LogoutTool.logout(this);
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        int cacheSize = 0;
        Collection<String>  keys = ImageLoader.getInstance().getMemoryCache().keys();
        for (String k : keys){
            cacheSize+= ImageLoader.getInstance().getMemoryCache().get(k).getByteCount();
        }
        cacheSize = cacheSize / (1024*1024);
        tab5View.setText("清空缓存("+cacheSize + "MB)");
    }
}
