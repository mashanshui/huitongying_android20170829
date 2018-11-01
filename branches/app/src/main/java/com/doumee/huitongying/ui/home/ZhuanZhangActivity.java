package com.huixiangshenghuo.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberListByNamesRequestObject;
import com.doumee.model.request.userInfo.MemberListByNamesRequestParam;
import com.doumee.model.response.userinfo.MemberListByNamesResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;

import java.util.LinkedList;
import java.util.List;

public class ZhuanZhangActivity extends BaseActivity {


    private EditText editText;
    private Button button;

    public static void startZhuanZhangActivity(Context context){
        Intent intent = new Intent(context,ZhuanZhangActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuan_zhang);
        initTitleBar_1();
        initView();
    }

    private void initView(){
        titleView.setText("转账");
        editText = (EditText)findViewById(R.id.username);
        button = (Button)findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit(){
        String username = editText.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToastView.show(getString(R.string.input_loginname));
            return;
        }
        showProgressDialog("正在加载..");
        MemberListByNamesRequestParam memberListByNamesRequestParam = new MemberListByNamesRequestParam();
        List<String> list = new LinkedList<>();
        list.add(username);
        memberListByNamesRequestParam.setLoginNames(list);
        MemberListByNamesRequestObject memberListByNamesRequestObject = new MemberListByNamesRequestObject();
        memberListByNamesRequestObject.setParam(memberListByNamesRequestParam);

        httpTool.post(memberListByNamesRequestObject, URLConfig.USER_LIST_BY_LOGIN_NAME, new HttpTool.HttpCallBack<MemberListByNamesResponseObject>() {
            @Override
            public void onSuccess(MemberListByNamesResponseObject o) {
                dismissProgressDialog();
                List<MemberListByNamesResponseParam>  list = o.getRecordList();
                if (!list.isEmpty()){
                    ZhuanInfoActivity.startZhuanInfoActivity(ZhuanZhangActivity.this,list.get(0));
                }else{
                    ToastView.show("账户不存在");
                }
            }
            @Override
            public void onError(MemberListByNamesResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
   }
}
