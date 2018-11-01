package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.FindPasswordActivity;

public class SetPassActivity extends BaseActivity implements View.OnClickListener {

    private TextView tab1View,tab2View;

    public static void startSetPassActivity(Context context){
        Intent intent = new Intent(context,SetPassActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass);
        initTitleBar_1();
        initView();
    }
    private void initView(){
        titleView.setText("安全设置");
        tab1View = (TextView)findViewById(R.id.tab1);
        tab2View = (TextView)findViewById(R.id.tab2);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1:
                OldPassActivity.startOldPassActivity(this, FindPasswordActivity.UPDATE_LOGIN_PASSWORD);
                break;
            case R.id.tab2:
              //  OldPassActivity.startOldPassActivity(this, FindPasswordActivity.UPDATE_PAY_PASSWORD);
                RegActivity.startRegActivity(this,RegActivity.EDIT_PAY_PASSWORD);
                break;
        }
    }
}
