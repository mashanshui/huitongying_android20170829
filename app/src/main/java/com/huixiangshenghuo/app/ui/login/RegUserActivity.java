package com.huixiangshenghuo.app.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.app.LogoutTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopInfoActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.RegisterRequestObject;
import com.doumee.model.request.userInfo.RegisterRequestParam;
import com.doumee.model.response.userinfo.RegisterResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

public class RegUserActivity extends BaseActivity {

    private EditText usernameView,passwordView,twoPasswordView,tuiUserView;
    private CheckBox checkBox;
    private Button regButton;
    private LinearLayout ll_sys_protocol;

    private String phone,code;

    public static void startRegUserActivity(Context context,String phone,String code){
        Intent intent = new Intent(context,RegUserActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("code",code);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user);
        initTitleBar_1();
        initView();
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
    }

    private void initView(){
        titleView.setText(getString(R.string.reg));
        usernameView = (EditText)findViewById(R.id.username);
        passwordView = (EditText)findViewById(R.id.password);
        twoPasswordView = (EditText)findViewById(R.id.password_two);
        tuiUserView = (EditText)findViewById(R.id.tui_user);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        regButton = (Button)findViewById(R.id.reg);
        ll_sys_protocol = (LinearLayout) findViewById(R.id.ll_sys_protocol);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });
        ll_sys_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopInfoActivity.startShopInfoActivity(RegUserActivity.this, "", "3");
            }
        });
    }

    private void reg(){
        final String username = usernameView.getText().toString().trim();
        final String password = passwordView.getText().toString().trim();
        String twoPassword = twoPasswordView.getText().toString().trim();
        String tuiUser = tuiUserView.getText().toString();

        if (TextUtils.isEmpty(username)){
            ToastView.show(getString(R.string.set_username));
            return;
        }
        if (TextUtils.isEmpty(password)){
            ToastView.show(getString(R.string.set_password));
            return;
        }
        if (TextUtils.isEmpty(twoPassword)){
            ToastView.show(getString(R.string.set_password_two));
            return;
        }
        if (!password.equals(twoPassword)){
            ToastView.show("二次输入密码不一致");
            return;
        }

        if (!checkBox.isChecked()){
            ToastView.show("请同意《"+getString(R.string.app_name)+"》协议");
            return;
        }

        RegisterRequestParam registerRequestParam = new RegisterRequestParam();
        registerRequestParam.setPhone(phone);
        registerRequestParam.setLoginName(username);
        registerRequestParam.setCaptch(code);
        registerRequestParam.setPwd(password);
        registerRequestParam.setRecPeople(tuiUser);
        RegisterRequestObject registerRequestObject = new RegisterRequestObject();
        registerRequestObject.setParam(registerRequestParam);

        httpTool.post(registerRequestObject, URLConfig.REG, new HttpTool.HttpCallBack<RegisterResponseObject>() {
            @Override
            public void onSuccess(RegisterResponseObject o) {
                ToastView.show("注册成功");
                UserInfoResponseParam userInfo = o.getMember();
                if (null == userInfo){
                    userInfo = new UserInfoResponseParam();
                }
//                userInfo.setCityName(password);
                userInfo.setLoginName(username);
                SaveObjectTool.saveObject(userInfo);
                LogoutTool.autoLogin(RegUserActivity.this);
            }
            @Override
            public void onError(RegisterResponseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

}
