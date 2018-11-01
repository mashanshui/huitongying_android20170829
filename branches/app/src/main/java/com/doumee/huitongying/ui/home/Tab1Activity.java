package com.huixiangshenghuo.app.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;

public class Tab1Activity extends BaseActivity implements View.OnClickListener{

    private TextView tab1View,tab2View;

    public static void startTab1Activity(Context context){
        Intent intent = new Intent(context,Tab1Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1);
        initTitleBar_1();
        initView();
    }
    private void initView(){
        titleView.setText("转账");
        tab1View = (TextView)findViewById(R.id.tab1);
        tab2View = (TextView)findViewById(R.id.tab2);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1:
                ZhuanZhangActivity.startZhuanZhangActivity(this);
                break;
            case R.id.tab2:
                ZhuanZhangActivity.startZhuanZhangActivity(this);
                break;
        }
    }
}
