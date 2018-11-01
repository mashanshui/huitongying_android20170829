package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;

import java.util.ArrayList;

public class MineBankCardActivity extends BaseActivity {



    private ListView listView;
    private Button addButton;
    private ArrayList<CardInfo> dataList;
    private CustomBaseAdapter<CardInfo> adapter;


    public static void startMineBankCardActivity(Context context){
        Intent intent = new Intent(context,MineBankCardActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_bank_card);
        initAdapter();
        initView();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("银行卡号");
        listView = (ListView)findViewById(R.id.list_view);
        addButton = (Button)findViewById(R.id.add);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankCardInfoActivity.startBankCardInfoActivity(MineBankCardActivity.this);
            }
        });
    }

    private void initAdapter(){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<CardInfo>(dataList,R.layout.activity_mine_bank_card_item) {
            @Override
            public void bindView(ViewHolder holder, CardInfo obj) {
                ImageView logoView = holder.getView(R.id.logo);
                TextView nameView = holder.getView(R.id.name);
                TextView cardView =holder.getView(R.id.bank_card);
                Button button = holder.getView(R.id.button);

                nameView.setText(obj.name);
                cardView.setText(obj.card);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BankCardInfoActivity.startBankCardInfoActivity(MineBankCardActivity.this);
                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData(){
        UserInfoResponseParam  userInfo =  SaveObjectTool.openUserInfoResponseParam();
        String bankCode = userInfo.getBankCode();
        if (!TextUtils.isEmpty(bankCode)){
            dataList.clear();
            String bankName = userInfo.getBankName();
            String bankAdd = userInfo.getBankAddr();
            CardInfo cardInfo = new CardInfo();
            cardInfo.card = "尾号("+bankCode.substring(bankCode.length() - 4)+")";
            cardInfo.name = bankName+"|"+bankAdd;
            dataList.add(cardInfo);
            adapter.notifyDataSetChanged();
            addButton.setVisibility(View.GONE);
        }else{
            listView.setBackgroundResource(R.mipmap.gwc_default);
            addButton.setVisibility(View.VISIBLE);
        }
    }

    private class CardInfo{
        String logo;
        String name;
        String card;
    }
}
