package com.huixiangshenghuo.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.HomeActivity;
import com.huixiangshenghuo.app.R;

public class SetPayPasswordActivity extends BaseActivity {

    private Button setButton,cancelButton;

    public static void startSetPayPasswordActivity(Context context){
        Intent intent = new Intent(context,SetPayPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_password);
        initTitleBar_1();
        initView();
    }

    private void initView(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.startHomeActivity(SetPayPasswordActivity.this);
                finish();
            }
        });
        titleView.setText("设置支付密码");
        setButton = (Button)findViewById(R.id.set_password);
        cancelButton = (Button)findViewById(R.id.cancel);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegActivity.startRegActivity(SetPayPasswordActivity.this,RegActivity.EDIT_PAY_PASSWORD);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.startHomeActivity(SetPayPasswordActivity.this);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        HomeActivity.startHomeActivity(SetPayPasswordActivity.this);
        super.onBackPressed();
    }
}
