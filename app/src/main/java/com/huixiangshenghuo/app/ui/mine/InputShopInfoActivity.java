package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.huixiangshenghuo.app.R;

public class InputShopInfoActivity extends BaseActivity {


    private String content;
    private EditText contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_shop_info);
        content = getIntent().getStringExtra("content");
        initTitleBar_1();
        titleView.setText("输入商家详情");
        contentView = (EditText)findViewById(R.id.content);
        contentView.setText(content);
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("完成");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = contentView.getText().toString().trim();
                if (TextUtils.isEmpty(d)){
                    ToastView.show("请输入商家详情");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("data",d);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
