package com.huixiangshenghuo.app.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.LoginParamObject;
import com.doumee.model.request.userInfo.LoginRequestObject;
import com.doumee.model.response.userinfo.LoginResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.login.SetPayPasswordActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText usernameView ,passwordView;
    private Button loginButton,editButton,regButton;
    public static final int LOGIN_DEFAULT = 0;
    public static final int LOGIN_FINISH = 1;
    public static final int LOGIN_AUTO = 2;//自动登录
    private int loginType = LOGIN_DEFAULT;
    private SharedPreferences sharedPreferences;

    public static void startLoginActivity(Context context,int loginType){
        Intent intent = new Intent(context,LoginActivity.class);
        intent.putExtra("loginType",loginType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginType = getIntent().getIntExtra("loginType",LOGIN_DEFAULT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initView();
        sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        httpTool = HttpTool.newInstance(this);
    }

    private void initView(){
        usernameView = (EditText)findViewById(R.id.username);
        passwordView = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);
        editButton = (Button)findViewById(R.id.edit_password);
        regButton = (Button)findViewById(R.id.reg);
        loginButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        regButton.setOnClickListener(this);
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        if (null != user){
            usernameView.setText(user.getLoginName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (loginType == LOGIN_AUTO){
            UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
            usernameView.setText(user.getLoginName());
            passwordView.setText(user.getCityName());
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                loadData();
                break;
            case R.id.edit_password:
                RegActivity.startRegActivity(LoginActivity.this,RegActivity.EDIT_PASSWORD);
                break;
            case R.id.reg:
               RegActivity.startRegActivity(this,RegActivity.REG);
                break;
        }
    }


    private void loadData(){
        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToastView.show(getString(R.string.input_username));
            return;
        }
        if (TextUtils.isEmpty(password)){
            ToastView.show(getString(R.string.input_password));
            return;
        }
        showProgressDialog("正在登录..");
        LoginParamObject loginParamObject = new LoginParamObject();
        loginParamObject.setLoginName(username);
        loginParamObject.setUserPwd(password);
        LoginRequestObject loginRequestObject = new LoginRequestObject();
        loginRequestObject.setParam(loginParamObject);
        httpTool.post(loginRequestObject, URLConfig.LOGIN, new HttpTool.HttpCallBack<LoginResponseObject>() {
            @Override
            public void onSuccess(LoginResponseObject o) {
                dismissProgressDialog();
                UserInfoResponseParam user = o.getMember();
                LogoutTool.TAG_LIST.clear();
                LogoutTool.TAG_LIST.add(user.getMemberId());
                startHome(user);
            }
            @Override
            public void onError(LoginResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void startHome(UserInfoResponseParam user){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CustomConfig.APP_LOGIN_STATUS,CustomConfig.LOGIN).commit();
        SaveObjectTool.saveObject(user);
        if (loginType == LOGIN_DEFAULT || loginType == LOGIN_AUTO){
            if (TextUtils.equals("1",user.getIsPaypwdNull()) ){
                SetPayPasswordActivity.startSetPayPasswordActivity(this);
            }else{
                Intent intent = new Intent(this,HomeActivity.class);
                startActivity(intent);
            }
        }
        finish();
    }


}
