package com.huixiangshenghuo.app.ui.home;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.mine.MineBankCardActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.presentrecord.PresentrecordAddRequestObject;
import com.doumee.model.request.presentrecord.PresentrecordAddRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.List;

public class TiXianActivity extends BaseActivity {

    private ImageView bankLogoView;
    private TextView bankNameView,bankCardView;
    private EditText moneyView;
    private Button allButton,submitButton;
    private RelativeLayout bankRelativeLayout;
    private UserInfoResponseParam userInfo;
    private int tiXianMoney = 0;
    private AlertDialog alertDialog;
    private TextView dialogContentView;
    private double bili;//提现手续费比例
    private String isShop;
    private double public_bili;//提现公益比例


    public static void startTiXianActivity(Context context){
        Intent intent = new Intent(context,TiXianActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_xian);
        initTitleBar_1();
        initView();
//        isShop = SaveObjectTool.openUserInfoResponseParam().getIsShop();
        loadUser();
        initAlertDialog();
    }

    private void initView(){
        titleView.setText("提现");
//        bankLogoView = (ImageView)findViewById(R.id.bank_logo);
        bankNameView = (TextView)findViewById(R.id.bank_name);
        bankCardView = (TextView)findViewById(R.id.bank_card);
        moneyView = (EditText)findViewById(R.id.money);
        allButton = (Button)findViewById(R.id.send);
        submitButton = (Button)findViewById(R.id.submit);
        bankRelativeLayout = (RelativeLayout)findViewById(R.id.bank) ;
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyView.setText(tiXianMoney+"");
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        bankRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineBankCardActivity.startMineBankCardActivity(TiXianActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setView();
    }

    private void setView(){
        userInfo = SaveObjectTool.openUserInfoResponseParam();
        String bankName = userInfo.getBankName();
        String bankCard = userInfo.getBankCode();
        if (!TextUtils.isEmpty(bankCard)){
            bankNameView.setText(bankName+"|"+userInfo.getBankAddr());
            bankCardView.setText("尾号("+bankCard.substring(bankCard.length() - 4)+")");
        }
        Double money = userInfo.getMoney();
        tiXianMoney = money.intValue();
        int num = tiXianMoney / 100 ;
        tiXianMoney = num * 100;
        moneyView.setHint("可提现"+tiXianMoney+"元");
    }

    private void initAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_ti_xian_dialog,null);
        dialogContentView = (TextView) view.findViewById(R.id.content);
        Button cancelButton = (Button)view.findViewById(R.id.cancel);
        Button yesButton = (Button)view.findViewById(R.id.yes);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String money = moneyView.getText().toString().trim();
                showProgressDialog("正在提现..");
                PresentrecordAddRequestParam presentrecordAddRequestParam = new PresentrecordAddRequestParam();
                presentrecordAddRequestParam.setIntegralNum(Double.parseDouble(money));
                PresentrecordAddRequestObject presentrecordAddRequestObject = new PresentrecordAddRequestObject();
                presentrecordAddRequestObject.setParam(presentrecordAddRequestParam);
                httpTool.post(presentrecordAddRequestObject, URLConfig.USER_TIXIAN, new HttpTool.HttpCallBack<ResponseBaseObject>() {
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
        });
        dialogContentView.setText("");
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    private void submit(){
        String money = moneyView.getText().toString().trim();
        String bankCard = bankCardView.getText().toString();

        if (TextUtils.isEmpty(bankCard)){
            ToastView.show("请添加提现账户");
            return;
        }

        if (TextUtils.isEmpty(money)){
            ToastView.show("请输入提现金额");
            return;
        }
        Double payMoney =  Double.parseDouble(money);
        if (payMoney < 100){
            ToastView.show("提现金额必须大于100");
            return;
        }

        int num = payMoney.intValue() / 100;
        int yu = payMoney.intValue() - (num * 100);
        if (yu > 0){
            ToastView.show("提现金额必须100的倍数");
            return;
        }
        if (payMoney.intValue() > tiXianMoney){
            ToastView.show("提现金额超过总金额");
            return;
        }

        StringBuffer content = new StringBuffer();
        content.append("尊敬的会员您好，提现");
        content.append(NumberFormatTool.numberFormat(payMoney));
        content.append("元，需要扣除");
        content.append(NumberFormatTool.numberFormat(payMoney * bili));
        content.append("元手续费，");
        content.append("和");
        content.append(NumberFormatTool.numberFormat(payMoney * public_bili));
        content.append("元公益基金，实际到账");
        content.append(NumberFormatTool.numberFormat(payMoney - payMoney * bili - payMoney * public_bili)).append("元。");
        dialogContentView.setText(content.toString());
        alertDialog.show();
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
                ToastView.show("提现审核中，请稍等");
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

    private void loadUser() {

        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        if (null == userInfoResponseParam) {
            userInfoResponseParam = new UserInfoResponseParam();
            userInfoResponseParam.setMemberId("");
        }
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);

        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                isShop = o.getMember().getIsShop();
                loadDataIndex();
            }

            @Override
            public void onError(MemberInfoResponseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }



    //加载数据字典
    public void loadDataIndex() {
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList) {
                    if (TextUtils.equals("0", isShop)) {//是否是商家 0不是 1是
                        if (app.getName().equals(CustomConfig.DATA_INDEX_TIXIAN)) {
                            bili = Double.parseDouble(app.getContent());
                        }
                        if (app.getName().equals(CustomConfig.MEMBER_WITHDRAW_PUBLIC_RATE)) {
                            public_bili = Double.parseDouble(app.getContent());
                            Log.i("公益积金1", app.getContent() + "");
//                            break;
                        }
                    }
                    else if (TextUtils.equals("1",isShop)){
                        if (app.getName().equals(CustomConfig.DATA_INDEX_SHOP_TIXIAN)) {
                            bili = Double.parseDouble(app.getContent());

                        }
                        if (app.getName().equals(CustomConfig.SHOP_WITHDRAW_PUBLIC_RATE)) {
                            public_bili = Double.parseDouble(app.getContent());
//                            break;
                        }
                    }


                }
            }
            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }
}
