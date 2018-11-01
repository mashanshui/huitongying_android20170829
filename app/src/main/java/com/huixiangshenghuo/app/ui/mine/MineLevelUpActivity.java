package com.huixiangshenghuo.app.ui.mine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.memberupgrade.MemberUpgradeAddRequestObject;
import com.doumee.model.request.memberupgrade.MemberUpgradeAddRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.memberupgrade.MemberUpgradeAddResponseObject;
import com.doumee.model.response.memberupgrade.MemberUpgradeAddResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.List;

public class MineLevelUpActivity extends BaseActivity {


    private TextView biliView,priceView;
    private RadioGroup radioGroup;
    private Button button;
    private static final int PAY_GOLD = 2;//金币（选择金币）
    private static final int PAY_ALI = 1;//支付宝（选择支付宝）
    private int payType = PAY_GOLD;
    //===============================
    private static final int WECHAT_PAY = 0;//微信支付
    private String orderId;
    private int payWeChat = 0;
    //===============================

    private AlertDialog alertDialog;
    private String payPassword;
    private Double payMoney;

    public static void startMineLevelUpActivity(Context context){
        Intent intent = new Intent(context,MineLevelUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_level_up);
        initView();
        initDialog();
        loadDataIndex();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("会员升级");
        biliView = (TextView)findViewById(R.id.bili);
        priceView = (TextView)findViewById(R.id.price);
        radioGroup = (RadioGroup)findViewById(R.id.pay_type);
        button = (Button)findViewById(R.id.submit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pay_gold:
                        payType = PAY_GOLD;
                        break;
                    case R.id.pay_ali:
                        payType = PAY_ALI;
                        break;
                    case R.id.rb_pay_wecha_up:
                        payType = WECHAT_PAY;
                        break;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (payType == PAY_ALI){
//                    submit();//升级VIP
//                }
//                else{
//                    UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
//                    double money = userInfoResponseParam.getMoney();
//                    if (money < payMoney){
//                        ToastView.show("金币不足，请使用其他支付方式");
//                        return;
//                    }
//                    alertDialog.show();
//                }
                if (payType == PAY_GOLD) {
                    UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
                    double money = userInfoResponseParam.getMoney();
                    if (money < payMoney){
                        ToastView.show("惠宝不足，请使用其他支付方式");
                        return;
                    }
                    alertDialog.show();

                } else {
                    submit();//升级VIP
                }
            }
        });
    }

    //升级VIP
    private void submit(){
        showProgressDialog("正在操作..");
        MemberUpgradeAddRequestParam memberUpgradeAddRequestParam = new MemberUpgradeAddRequestParam();
        memberUpgradeAddRequestParam.setPayMethod(payType + "");//支付方式 0微信转账 1支付宝转账
        if (payType == PAY_GOLD){
            memberUpgradeAddRequestParam.setPaypwd(payPassword);
            memberUpgradeAddRequestParam.setIntegralnum(payMoney);
        }
        MemberUpgradeAddRequestObject memberUpgradeAddRequestObject = new MemberUpgradeAddRequestObject();
        memberUpgradeAddRequestObject.setParam(memberUpgradeAddRequestParam);
        httpTool.post(memberUpgradeAddRequestObject, URLConfig.USER_UP_VIP, new HttpTool.HttpCallBack<MemberUpgradeAddResponseObject>() {
            @Override
            public void onSuccess(MemberUpgradeAddResponseObject o) {
                dismissProgressDialog();
                MemberUpgradeAddResponseParam info = o.getRecord();
                String isPayDone = info.getIsPayDone();
//                if (TextUtils.equals("0",isPayDone)) {//未支付完成
//                    aliPay(o.getParam().getParamStr());//支付宝支付
//                }else{
//                    loadUser();
//                }
                //==================================================
                if (TextUtils.equals("0",isPayDone)) {//未支付完成
                    if (payType == WECHAT_PAY) {
                        if (ThirdPartyClient.isWeixinAvilible(MineLevelUpActivity.this)) {
                            payOrder(info.getOrderId(), "");
                        } else {
                            ToastView.show("请安装微信客户端，或者使用其它支付方式");
                        }

//                        payOrder(info.getOrderId(), "");
                    } else {
                        aliPay(o.getParam().getParamStr());//支付宝支付
                    }
                }else{
                    loadUser();
                }
                //==================================================



            }
            @Override
            public void onError(MemberUpgradeAddResponseObject o) {
                    dismissProgressDialog();
                    ToastView.show(o.getErrorMsg());
            }
        });
    }


    private void aliPay(String params){
        AliPayTool aliPayTool = new AliPayTool(this);
        aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
            @Override
            public void onPaySuccess() {
                loadUser();
            }
            @Override
            public void onPayError(String resultInfo) {

            }
        });
        aliPayTool.pay(params);
    }


    private void loadUser(){
        showProgressDialog("正在加载..");
        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);
        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                dismissProgressDialog();
                ToastView.show("恭喜您升级为VIP会员");
                UserInfoResponseParam userInfo = o.getMember();
                SaveObjectTool.saveObject(userInfo);
                finish();
            }
            @Override
            public void onError(MemberInfoResponseObject o) {
               dismissProgressDialog();
            }
        });
    }


    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_zhuan_info_dialog,null);
        ImageButton closeButton = (ImageButton)view.findViewById(R.id.close);
        TextView button = (TextView)view.findViewById(R.id.pay) ;
        TextView cancelButton = (TextView)view.findViewById(R.id.cancel) ;
        final EditText editText = (EditText)view.findViewById(R.id.pay_password);
        Button helpButton = (Button)view.findViewById(R.id.help);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                alertDialog.dismiss();
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegActivity.startRegActivity(MineLevelUpActivity.this,RegActivity.EDIT_PAY_PASSWORD);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                alertDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPassword = editText.getText().toString().trim();
                if (TextUtils.isEmpty(payPassword)){
                    ToastView.show("请输入支付密码");
                    return;
                }
                editText.setText("");
                alertDialog.dismiss();
                submit();//升级VIP
            }
        });
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog = builder.create();
    }

    //加载数据字典
    public void loadDataIndex(){
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList){
                    if (app.getName().equals(CustomConfig.DATA_INDEX_VIP_INTEGRAL)){
                        Double d = Double.parseDouble(app.getContent());
                        d = d * 100 ;
                        biliView.setText(d  +"%");
                    }
                    if (app.getName().equals(CustomConfig.DATA_INDEX_UP_VIP_MONEY)){
                        payMoney = Double.parseDouble(app.getContent());
                        priceView.setText(CustomConfig.RMB+payMoney);
                    }
                }
            }
            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }

    //============================================================================================
    @Override
    protected void onResume() {
        super.onResume();
        if (payType == WECHAT_PAY && payWeChat == 1) {
            loadPayResult();
        }
    }

    //支付订单
    private void payOrder(final String orderId, String aliParams) {

        switch (payType) {
            case WECHAT_PAY:
                this.orderId = orderId;
                showProgressDialog(getString(R.string.loading));
                WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
                weixinOrderAddRequestParam.setOrderId(orderId);
                weixinOrderAddRequestParam.setTradeType("APP");
                weixinOrderAddRequestParam.setType("2");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
                WeixinOrderAddRequestObject weixinOrderAddRequestObject = new WeixinOrderAddRequestObject();
                weixinOrderAddRequestObject.setParam(weixinOrderAddRequestParam);
                httpTool.post(weixinOrderAddRequestObject, URLConfig.ORDER_WECHAT_PAY, new HttpTool.HttpCallBack<WeixinOrderAddResponseObject>() {
                    @Override
                    public void onSuccess(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        WeixinOrderResponseParam weiXin = o.getData();
                        PayOrderRequestEntity payOrderRequestEntity = weiXin.getParam();
                        payWeChat = 1;
                        WXPayTool.WXPay wxPay = new WXPayTool.WXPay();
                        wxPay.setNonceStr(payOrderRequestEntity.getNoncestr());
                        wxPay.setPrepayId(payOrderRequestEntity.getPrepayid());
                        wxPay.setSign(payOrderRequestEntity.getSign());
                        wxPay.setAppId(payOrderRequestEntity.getAppid());
                        wxPay.setPartnerId(payOrderRequestEntity.getPartnerid());
                        wxPay.setTimeStamp(payOrderRequestEntity.getTimestamp());
                        wxPay.setPackageStr(payOrderRequestEntity.getPackageStr());
                        WXPayTool wxPayTool = new WXPayTool(MineLevelUpActivity.this, weiXin.getParam().getAppid());
                        wxPayTool.payRequest(wxPay);
                    }

                    @Override
                    public void onError(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        Toast.makeText(MineLevelUpActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case PAY_ALI:
                AliPayTool aliPayTool = new AliPayTool(this);
                aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                        loadUser();
                    }

                    @Override
                    public void onPayError(String resultInfo) {

                    }
                });
                aliPayTool.pay(aliParams);
                break;


        }
    }

    private void loadPayResult() {
        showProgressDialog(getString(R.string.loading));
        WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
        weixinOrderQueryRequestParam.setOrderId(orderId);
        weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_UPGRADE);//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户

        WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
        weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

        httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                loadUser();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
//                payAlertTip.show();
            }
        });
    }


    //============================================================================================
}
