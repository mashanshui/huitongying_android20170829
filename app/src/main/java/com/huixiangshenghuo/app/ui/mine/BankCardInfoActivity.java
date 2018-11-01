package com.huixiangshenghuo.app.ui.mine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.request.userInfo.UpdateMemberRequestObject;
import com.doumee.model.request.userInfo.UpdateMemberRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;

public class BankCardInfoActivity extends BaseActivity {

    private TextView bankNameView,bankAddressView,bankCardView,bankUserView;

    private String bankName;
    private AlertDialog alertDialog;
    private ArrayList<Bank> dataList;
    private CustomBaseAdapter<Bank> adapter;

    public static void startBankCardInfoActivity(Context context){
        Intent intent = new Intent(context,BankCardInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_info);
        initView();
        initDialog();
        loadBank();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("银行卡");
        actionButton.setText("保存");
        actionButton.setVisibility(View.VISIBLE);
        bankNameView = (TextView)findViewById(R.id.bank_list);
        bankAddressView = (TextView)findViewById(R.id.bank_address);
        bankCardView = (TextView)findViewById(R.id.bank_card);
        bankUserView = (TextView)findViewById(R.id.bank_user);
        bankNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit();
            }
        });
    }

    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_bank_card_info_dialog,null);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        initAdapter(listView);
        builder.setView(view);
        alertDialog = builder.create();
    }

    private void initAdapter(ListView listView){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<Bank>(dataList,R.layout.activity_bank_card_info_item) {
            @Override
            public void bindView(ViewHolder holder, Bank obj) {
                TextView nameView = holder.getView(R.id.bank_name);
                nameView.setText(obj.name);
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bank bank = dataList.get(position);
                bankName = bank.name;
                bankNameView.setText(bank.name);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfoResponseParam userInfo =  SaveObjectTool.openUserInfoResponseParam();
        String bankCode = userInfo.getBankCode();
        if (!TextUtils.isEmpty(bankCode)){
            bankName = userInfo.getBankName();
            String bankAdd = userInfo.getBankAddr();
            bankNameView.setText(bankName);
            bankAddressView.setText(bankAdd);
            bankCardView.setText(bankCode);
            bankUserView.setText(userInfo.getBankPeopleName());
        }
    }

    private void submit(){
        String address = bankAddressView.getText().toString().trim();
        String card = bankCardView.getText().toString().trim();
        String user = bankUserView.getText().toString().trim();
        if (TextUtils.isEmpty(bankName)){
            ToastView.show("请选择开户银行");
            return;
        }
        if (TextUtils.isEmpty(address)){
            ToastView.show("请输入开户行所在地");
            return;
        }
        if (TextUtils.isEmpty(card)){
            ToastView.show("请输入银行卡号");
            return;
        }
        if (card.trim().length() <= 5){
            ToastView.show("银行卡号必须大于5位");
            return;
        }
        if (TextUtils.isEmpty(user)){
            ToastView.show("请输入持卡人姓名");
            return;
        }

        showProgressDialog("正在提交..");
        UpdateMemberRequestParam updateMemberRequestParam = new UpdateMemberRequestParam();
        updateMemberRequestParam.setBankAddr(address);
        updateMemberRequestParam.setBankName(bankName);
        updateMemberRequestParam.setBankno(card);
        updateMemberRequestParam.setBankPeopleName(user);

        UpdateMemberRequestObject updateMemberRequestObject = new UpdateMemberRequestObject();
        updateMemberRequestObject.setParam(updateMemberRequestParam);
        httpTool.post(updateMemberRequestObject, URLConfig.UPDATE_USER_INFO, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                updateUser();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    private void updateUser(){
        showProgressDialog("正在加载..");
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfo.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);
        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                dismissProgressDialog();
                ToastView.show("保存成功");
                UserInfoResponseParam userInfoResponseParam = o.getMember();
                SaveObjectTool.saveObject(userInfoResponseParam);
                finish();
            }
            @Override
            public void onError(MemberInfoResponseObject o) {
                dismissProgressDialog();
            }
        });
    }


    private void loadBank(){
        String[] bankList = getResources().getStringArray(R.array.bank_list);
        for (String bankName : bankList){
            Bank bank = new Bank();
            bank.name = bankName;
            dataList.add(bank);
        }
        adapter.notifyDataSetChanged();
    }

    private class Bank{
        String id;
        String name;
    }
}
