package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.doumee.model.request.feedback.FeedbackAddRequestObject;
import com.doumee.model.request.feedback.FeedbackAddRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;

public class HelpTwoActivity extends BaseActivity {

    private EditText contentView;

    public static void startHelpTwoActivity(Context context){
        Intent intent = new Intent(context,HelpTwoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_two);
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("意见反馈");
        contentView = (EditText)findViewById(R.id.content);
        actionButton.setText("提交");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit(){
        String content = contentView.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            ToastView.show("请输入您的宝贵意见");
            return;
        }
        request();
    }

    private void request() {

        FeedbackAddRequestObject object = new FeedbackAddRequestObject();
        object.setParam(new FeedbackAddRequestParam());
        object.getParam().setContent(contentView.getText().toString().trim());
        httpTool.post(object, URLConfig.READBACK, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                Toast.makeText(HelpTwoActivity.this, "谢谢你的意见~", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(ResponseBaseObject o) {

                Toast.makeText(HelpTwoActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
