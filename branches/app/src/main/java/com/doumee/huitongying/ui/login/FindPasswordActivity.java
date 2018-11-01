package com.huixiangshenghuo.app.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.ForgetPasswordParamObject;
import com.doumee.model.request.userInfo.ForgetPasswordRequestObject;
import com.doumee.model.request.userInfo.ForgetPayPasswordParamObject;
import com.doumee.model.request.userInfo.ForgetPayPasswordRequestObject;
import com.doumee.model.request.userInfo.UpdatePayPwdRequestObject;
import com.doumee.model.request.userInfo.UpdatePayPwdRequestParam;
import com.doumee.model.request.userInfo.UpdatePwdRequestObject;
import com.doumee.model.request.userInfo.UpdatePwdRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.HomeActivity;

/**
 * 忘记密码
 */
public class FindPasswordActivity extends BaseActivity {


    private EditText passwordView,passwordTwoView;
    private Button submitButton;
    public static final int SET_LOGIN_PASSWORD = 0;//设置登录密码
    public static final int SET_PAY_PASSWORD = 1;//设置支付密码
    public static final int UPDATE_LOGIN_PASSWORD = 2;//修改密码
    public static final int UPDATE_PAY_PASSWORD = 3;//修改支付密码
    public int flag = SET_LOGIN_PASSWORD;

    private String phone,code,oldPass;

    public static void startFindPasswordActivity(Context context,int flag,String phone,String code){
        Intent intent = new Intent(context,FindPasswordActivity.class);
        intent.putExtra("flag",flag);
        intent.putExtra("phone",phone);
        intent.putExtra("code",code);
        context.startActivity(intent);
    }

    public static void startFindPasswordActivity(Context context,int flag,String oldPass){
        Intent intent = new Intent(context,FindPasswordActivity.class);
        intent.putExtra("flag",flag);
        intent.putExtra("oldPass",oldPass);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        flag = getIntent().getIntExtra("flag",SET_LOGIN_PASSWORD);
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
        oldPass = getIntent().getStringExtra("oldPass");
        initView();
    }

    private void initView(){
        initTitleBar_1();
        passwordView = (EditText)findViewById(R.id.set_password);
        passwordTwoView = (EditText)findViewById(R.id.set_password_two);
        if (flag == SET_LOGIN_PASSWORD)
            titleView.setText("设置登录密码");
        else if (flag == SET_PAY_PASSWORD){
            titleView.setText("设置支付密码");
            passwordView.setHint("设置支付密码");
            passwordTwoView.setHint("确认支付密码");
        }else if (flag == UPDATE_LOGIN_PASSWORD){
            titleView.setText("修改密码");
        }else{
            titleView.setText("修改支付密码");
        }
        submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit(){
        String pass = passwordView.getText().toString().trim();
        String passTwo = passwordTwoView.getText().toString().trim();
        if (TextUtils.isEmpty(pass)){
            ToastView.show(getString(R.string.set_password));
            return;
        }
        if (TextUtils.isEmpty(passTwo)){
            ToastView.show(getString(R.string.set_password_two));
            return;
        }
        if (!TextUtils.equals(pass,passTwo)){
            ToastView.show(getString(R.string.password_error));
            return;
        }
        if (SET_PAY_PASSWORD == flag){
            setPayPassword(pass);
        }else if (flag == SET_LOGIN_PASSWORD){
            setLoginPassword(pass);
        }else if (flag == UPDATE_LOGIN_PASSWORD){
            updatePassword(pass);
        }else{
            updatePayPassword(pass);
        }
    }

    private void setLoginPassword(String password){
        ForgetPasswordParamObject forgetPasswordParamObject = new ForgetPasswordParamObject();
        forgetPasswordParamObject.setPhone(phone);
        forgetPasswordParamObject.setCaptcha(code);
        forgetPasswordParamObject.setUserPwd(password);
        ForgetPasswordRequestObject forgetPasswordRequestObject = new ForgetPasswordRequestObject();
        forgetPasswordRequestObject.setParam(forgetPasswordParamObject);
        httpTool.post(forgetPasswordRequestObject, URLConfig.EDIT_PASSWORD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                ToastView.show("设置密码成功");
                LogoutTool.logout(FindPasswordActivity.this);
            }
            @Override
            public void onError(ResponseBaseObject o) {
                      ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void setPayPassword(String pass){
        ForgetPayPasswordParamObject forgetPayPasswordParamObject = new ForgetPayPasswordParamObject();
        forgetPayPasswordParamObject.setCaptcha(code);
        forgetPayPasswordParamObject.setPhone(phone);
        forgetPayPasswordParamObject.setUserPwd(pass);
        ForgetPayPasswordRequestObject forgetPayPasswordRequestObject = new ForgetPayPasswordRequestObject();
        forgetPayPasswordRequestObject.setParam(forgetPayPasswordParamObject);
        httpTool.post(forgetPayPasswordRequestObject, URLConfig.EDIT_PAY_PASSWORD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                ToastView.show("设置支付密码成功");
                HomeActivity.startHomeActivity(FindPasswordActivity.this);
                finish();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }
    private void updatePassword(String pass){
        UpdatePwdRequestParam updatePwdRequestParam = new UpdatePwdRequestParam();
        updatePwdRequestParam.setNewPwd(pass);
        updatePwdRequestParam.setOldPwd(oldPass);
        UpdatePwdRequestObject updatePwdRequestObject = new UpdatePwdRequestObject();
        updatePwdRequestObject.setParam(updatePwdRequestParam);
        httpTool.post(updatePwdRequestObject, URLConfig.UPDATE_PASSWORD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                ToastView.show("设置密码成功");
                LogoutTool.logout(FindPasswordActivity.this);
            }
            @Override
            public void onError(ResponseBaseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void updatePayPassword(String pass){
        UpdatePayPwdRequestParam updatePayPwdRequestParam = new UpdatePayPwdRequestParam();
        updatePayPwdRequestParam.setOldPwd(oldPass);
        updatePayPwdRequestParam.setPayPwd(pass);
        UpdatePayPwdRequestObject updatePayPwdRequestObject = new UpdatePayPwdRequestObject();
        updatePayPwdRequestObject.setParam(updatePayPwdRequestParam);
        httpTool.post(updatePayPwdRequestObject, URLConfig.UPDATE_PAY_PASSWORD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                ToastView.show("设置支付密码成功");
                finish();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

}
