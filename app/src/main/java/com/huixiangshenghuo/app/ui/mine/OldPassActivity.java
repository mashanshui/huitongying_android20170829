package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.FindPasswordActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.huixiangshenghuo.app.R;

public class OldPassActivity extends BaseActivity {


    private int flag;
    private EditText editText;
    private Button button;

    public static void startOldPassActivity(Context context,int flag){
        Intent intent = new Intent(context,OldPassActivity.class);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_pass);
        flag = getIntent().getIntExtra("flag", FindPasswordActivity.UPDATE_LOGIN_PASSWORD);
        initTitleBar_1();
        initView();
    }

    private void initView(){
        titleView.setText("修改密码");
        editText = (EditText)findViewById(R.id.old_pass);
        button = (Button)findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void next(){
        String oldPass = editText.getText().toString().trim();
        if (TextUtils.isEmpty(oldPass)){
            ToastView.show("输入旧密码");
            return;
        }
        FindPasswordActivity.startFindPasswordActivity(this,flag,oldPass);
    }
}
