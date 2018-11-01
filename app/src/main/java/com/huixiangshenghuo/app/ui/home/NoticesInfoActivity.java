package com.huixiangshenghuo.app.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.R;

public class NoticesInfoActivity extends BaseActivity {

    private String title;
    private String content;
    private String time;
    private TextView noteTitleView,timeView,contentView;

    public static void startNoticesInfoActivity(Context context,String title,String content,String time){
        Intent intent = new Intent(context,NoticesInfoActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        intent.putExtra("time",time);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices_info);
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        time = getIntent().getStringExtra("time");
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("通知详情");
        noteTitleView = (TextView) findViewById(R.id.note_title);
        timeView = (TextView) findViewById(R.id.time);
        contentView = (TextView) findViewById(R.id.content);
        noteTitleView.setText(title);
        timeView.setText(time);
        contentView.setText(content);
    }
}
