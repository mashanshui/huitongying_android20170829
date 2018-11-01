package com.huixiangshenghuo.app.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.doumee.model.request.message.SendMessageByParamsParam;
import com.doumee.model.request.message.SendMessageByParamsRequestObject;
import com.doumee.model.request.message.SendMessageCodeParam;
import com.doumee.model.request.message.SendMessageCodeRequestObject;
import com.doumee.model.request.userInfo.MemberInfoByNameParamObject;
import com.doumee.model.request.userInfo.MemberInfoByNameRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.message.SendMessageCodeResponseObject;
import com.doumee.model.response.userinfo.MemberInfoByNameResponseObject;
import com.doumee.model.response.userinfo.MemberInfoByNameResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;

import java.util.Timer;
import java.util.TimerTask;

public class RegActivity extends BaseActivity {



    private EditText phoneView,codeView,userView;
    private Button sendButton,nextButton;

    private boolean isSend = false;
    private int time = 60;

    public static final int REG = 0;
    public static final int EDIT_PASSWORD = 1;
    public static final int EDIT_PAY_PASSWORD = 2;
    private int flag;

    public static void startRegActivity(Context context,int flag){
        Intent intent = new Intent(context,RegActivity.class);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        flag = getIntent().getIntExtra("flag",REG);
        initTitleBar_1();
        initView();
        initListener();
        timer.schedule(timerTask,0,1000);
        httpTool = HttpTool.newInstance(this);
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          if (isSend)
           handler.sendEmptyMessage(0)  ;
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           if (time>= 1){
               time--;
               sendButton.setEnabled(false);
               sendButton.setText(time+"获取验证码");
           }else{
               isSend = false;
               time = 60;
               sendButton.setEnabled(true);
               sendButton.setText("获取验证码");
           }
        }
    };

    private void initView(){
        actionButton.setText(getString(R.string.login));
        phoneView = (EditText)findViewById(R.id.phone);
        codeView = (EditText)findViewById(R.id.code);
        sendButton = (Button)findViewById(R.id.send);
        nextButton = (Button)findViewById(R.id.next);
        userView = (EditText)findViewById(R.id.username);
        actionButton.setVisibility(View.VISIBLE);
        if (flag == REG){
            titleView.setText(getString(R.string.reg));
        }
        else if (flag == EDIT_PASSWORD){
            phoneView.setVisibility(View.GONE);
            userView.setVisibility(View.VISIBLE);
            titleView.setText("忘记密码");
        }else{
            phoneView.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            titleView.setText("设置支付密码");
        }
    }

    private void initListener(){
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == REG ){
                    String phone = phoneView.getText().toString().trim();
                    if (TextUtils.isEmpty(phone)){
                        ToastView.show(getString(R.string.input_phone));
                        return;
                    }
                    sendCode();
                }else if(flag == EDIT_PASSWORD){
                    loadUserByLoginName();
                }
                else{
                    sendCodeUser();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void sendCode(){
        isSend = true;
        String phone = phoneView.getText().toString().trim();
        SendMessageCodeParam sendMessageCodeParam = new SendMessageCodeParam();
        sendMessageCodeParam.setPhone(phone);
        sendMessageCodeParam.setType(SendMessageCodeParam.TYPE_TEXT);
        sendMessageCodeParam.setActionType(SendMessageCodeParam.TYPE_REGISTER);
        SendMessageCodeRequestObject sendMessageCodeRequestObject = new SendMessageCodeRequestObject();
        sendMessageCodeRequestObject.setParam(sendMessageCodeParam);
        httpTool.post(sendMessageCodeRequestObject, URLConfig.SEND_CODE, new HttpTool.HttpCallBack<SendMessageCodeResponseObject>() {
            @Override
            public void onSuccess(SendMessageCodeResponseObject o) {
                ToastView.show("验证码已经发送");
            }
            @Override
            public void onError(SendMessageCodeResponseObject o) {
                time = 0;
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    private void loadUserByLoginName(){
        String username = userView.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToastView.show(getString(R.string.input_username));
            return;
        }
        showProgressDialog("正在加载..");
        final MemberInfoByNameParamObject memberInfoByNameParamObject = new MemberInfoByNameParamObject();
        memberInfoByNameParamObject.setLoginName(username);
        MemberInfoByNameRequestObject memberInfoByNameRequestObject = new MemberInfoByNameRequestObject();
        memberInfoByNameRequestObject.setParam(memberInfoByNameParamObject);
        httpTool.post(memberInfoByNameRequestObject, URLConfig.USER_BY_LOGIN_NAME, new HttpTool.HttpCallBack<MemberInfoByNameResponseObject>() {
            @Override
            public void onSuccess(MemberInfoByNameResponseObject o) {
                dismissProgressDialog();
                MemberInfoByNameResponseParam memberInfoByNameResponseParam = o.getMember();
                UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                if (null == user){
                    user = new UserInfoResponseParam();
                }
                user.setMemberId(memberInfoByNameResponseParam.getMemberId());
                user.setPhone(memberInfoByNameResponseParam.getPhone());
                SaveObjectTool.saveObject(user);
                sendCodeUser();
            }
            @Override
            public void onError(MemberInfoByNameResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void sendCodeUser(){
        isSend = true;
        String userId = "";
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        if (null != user){
            userId = user.getMemberId();
        }
        SendMessageByParamsParam sendMessageByParamsParam = new SendMessageByParamsParam();
        sendMessageByParamsParam.setActionType(SendMessageByParamsParam.TYPE_FORGET_PWD);
        sendMessageByParamsParam.setType(SendMessageByParamsParam.TYPE_TEXT);
        sendMessageByParamsParam.setMemberId(userId);

        SendMessageByParamsRequestObject sendMessageByParamsRequestObject = new SendMessageByParamsRequestObject();
        sendMessageByParamsRequestObject.setParam(sendMessageByParamsParam);

        httpTool.post(sendMessageByParamsRequestObject, URLConfig.SEND_CODE_USER, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                ToastView.show("验证码已经发送");
            }
            @Override
            public void onError(ResponseBaseObject o) {
                time = 0;
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void next(){
        String code = codeView.getText().toString().trim();
       if (flag == REG){
           String phone = phoneView.getText().toString().trim();
           if (TextUtils.isEmpty(phone)){
               ToastView.show(getString(R.string.input_phone));
               return;
           }
           if (TextUtils.isEmpty(code)){
               ToastView.show(getString(R.string.input_phone_code));
               return;
           }
           RegUserActivity.startRegUserActivity(this,phone,code);
       }else if (flag == EDIT_PASSWORD){
           if (TextUtils.isEmpty(code)){
               ToastView.show(getString(R.string.input_phone_code));
               return;
           }
           String phone = "";
           UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
           if (null != user){
               phone = user.getPhone();
           }
           FindPasswordActivity.startFindPasswordActivity(this,FindPasswordActivity.SET_LOGIN_PASSWORD,phone,code);
       }else{
           FindPasswordActivity.startFindPasswordActivity(this,FindPasswordActivity.SET_PAY_PASSWORD,"",code);
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
