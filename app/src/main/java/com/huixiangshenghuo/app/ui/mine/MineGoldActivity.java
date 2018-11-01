package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.home.PayMoneyActivity;
import com.huixiangshenghuo.app.ui.home.TiXianActivity;
import com.huixiangshenghuo.app.view.CountView;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

public class MineGoldActivity extends BaseActivity implements View.OnClickListener {

    private TextView tab1View,tab2View;
    private CountView goldView;

    private double gold ;

    public static void startMineGoldActivity(Context context){
        Intent intent = new Intent(context,MineGoldActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_gold);
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText(getString(R.string.gold));
        actionButton.setText("明细");
        actionButton.setVisibility(View.VISIBLE);
        goldView = (CountView)findViewById(R.id.gold) ;
        tab1View = (TextView)findViewById(R.id.tab1) ;
        tab2View = (TextView)findViewById(R.id.tab2) ;

        actionButton.setOnClickListener(this);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        gold = userInfo.getMoney();
    }

    @Override
    protected void onResume() {
        super.onResume();
        goldView.setText(NumberFormatTool.numberFormatTo4(gold));
       // goldView.showNumberWithAnimation(Float.parseFloat());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action :
                info();
                break;
            case R.id.tab1:
                PayMoneyActivity.startPayMoneyActivity(this);
                break;
            case R.id.tab2:
                UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
                String bank = userInfo.getBankCode();
                if (TextUtils.isEmpty(bank)){
                    MineBankCardActivity.startMineBankCardActivity(this);
                }else{
                    TiXianActivity.startTiXianActivity(this);
                }
                break;
        }
    }

    private void info(){
        GoldRecordActivity.startGoldRecordActivity(this, "1");//账单类型 0会员积分账单  1会员惠宝账单 2公益资金账单 3商家账单
    }
}
