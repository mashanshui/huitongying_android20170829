package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.R;

public class HelpActivity extends BaseActivity implements View.OnClickListener{

    private TextView tab1View ,tab2View;

    public static void startHelpActivity(Context context){
        Intent intent = new Intent(context,HelpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("帮助与反馈");
        tab1View = (TextView)findViewById(R.id.tab1);
        tab2View = (TextView)findViewById(R.id.tab2);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1:
                HelpTabOneActivity.startHelpTabOneActivity(this);
                break;
            case R.id.tab2:
                HelpTwoActivity.startHelpTwoActivity(this);
                break;
        }
    }
}
