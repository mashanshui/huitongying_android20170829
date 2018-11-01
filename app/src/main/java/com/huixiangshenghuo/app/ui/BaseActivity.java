package com.huixiangshenghuo.app.ui;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;

/**
 * Created by lenovo on 2016/12/9.
 */
public class BaseActivity extends AppCompatActivity {

    protected ImageButton backButton,actionImageButton;
    protected TextView titleView;
    protected Button actionButton, bt_action_image;
    protected HttpTool httpTool;

    protected RelativeLayout rl_action_image;
    protected ImageButton actionImageButton2;
    protected TextView actionTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogoutTool.activities.add(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        httpTool = HttpTool.newInstance(this);
    }

    protected void initTitleBar_1(){
        backButton = (ImageButton)findViewById(R.id.back);
        titleView = (TextView)findViewById(R.id.title);
        actionButton = (Button)findViewById(R.id.action);
        bt_action_image = (Button) findViewById(R.id.bt_action_image);
        actionImageButton = (ImageButton)findViewById(R.id.action_image);
        rl_action_image = (RelativeLayout) findViewById(R.id.rl_action_image);
        actionImageButton2 = (ImageButton) findViewById(R.id.action_image2);
        actionTxt = (TextView) findViewById(R.id.count_txt);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String msg){
//        progressDialog =  ProgressDialog.show(this,"",msg);
//        progressDialog.setCancelable(true);
//        progressDialog.show();
        try {
            progressDialog = ProgressDialog.show(this, "", msg);
            progressDialog.setCancelable(true);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void dismissProgressDialog(){
        if(null != progressDialog){
            progressDialog.dismiss();
        }
    }
}
