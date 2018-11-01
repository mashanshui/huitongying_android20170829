package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.doumee.model.request.comment.CommentAddRequestObject;
import com.doumee.model.request.comment.CommentAddRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;

public class CommentActivity extends BaseActivity {



    private String orderId;

    private RatingBar ratingBar;
    private EditText contentView;

    public static void startCommentActivity(Context context,String orderId){
        Intent intent = new Intent(context,CommentActivity.class);
        intent.putExtra("orderId",orderId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        orderId = getIntent().getStringExtra("orderId");
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("评价");
        actionButton.setText("提交");
        actionButton.setVisibility(View.VISIBLE);

        ratingBar = (RatingBar)findViewById(R.id.rating_bar) ;
        contentView = (EditText)findViewById(R.id.content) ;

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit();
            }
        });
    }

    private void submit(){
        Float re = ratingBar.getRating();
        String content = contentView.getText().toString().trim();
        if (re.intValue() == 0){
            ToastView.show("请输入评分");
            return;
        }
        if (TextUtils.isEmpty(content)){
            ToastView.show("请输入您的评价");
            return;
        }
        showProgressDialog("正在提交..");
        CommentAddRequestParam commentAddRequestParam = new CommentAddRequestParam();
        commentAddRequestParam.setOrderId(Long.parseLong(orderId));
        commentAddRequestParam.setScore(re.intValue());
        commentAddRequestParam.setContent(content);
        CommentAddRequestObject commentAddRequestObject = new CommentAddRequestObject();
        commentAddRequestObject.setParam(commentAddRequestParam);
        httpTool.post(commentAddRequestObject, URLConfig.ORDER_COMMENT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                ToastView.show("评价成功");
                Intent intent = new Intent();
                intent.putExtra("data",orderId);
                setResult(RESULT_OK,intent);
                finish();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
                 ToastView.show(o.getErrorMsg());
            }
        });
    }
}
