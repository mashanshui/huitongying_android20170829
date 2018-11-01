package com.huixiangshenghuo.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.membercharge.MemberChargeAddRequestObject;
import com.doumee.model.request.membercharge.MemberChargeAddRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.membercharge.MemberChargeAddResponseObject;
import com.doumee.model.response.userinfo.AlipayResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

public class PayMoneyActivity extends BaseActivity {

    private EditText moneyView;
    private Button button;

    //===============================
    private static final int PAY_ALI = 1;//支付宝（选择支付宝）
    private int payType = PAY_ALI;
    private static final int WECHAT_PAY = 0;//微信支付
    private String orderId;
    private int payWeChat = 0;
    private RadioGroup radioGroup;
    //===============================

    public static void startPayMoneyActivity(Context context){
        Intent intent = new Intent(context,PayMoneyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_money);
        initTitleBar_1();
        initView();
    }

    private void initView(){
        titleView.setText("充值");
        moneyView = (EditText)findViewById(R.id.pay_money);
        button = (Button)findViewById(R.id.submit);
        radioGroup = (RadioGroup) findViewById(R.id.pay_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.pay_ali_chz:
                        payType = PAY_ALI;
                        break;
                    case R.id.rb_pay_wecha_chz:
                        payType = WECHAT_PAY;
                        break;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        moneyView.setFilters(new InputFilter[]{lengthfilter});
    }

    /**
     *  设置小数位数控制
     */
    InputFilter lengthfilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if ("".equals(source.toString())) {
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                int diff = dotValue.length() + 1 - 2;
                if (diff > 0) {
                    return source.subSequence(start, end - diff);
                }
            }
            return null;
        }
    };

    private void submit(){
        String money = moneyView.getText().toString().trim();
        if (TextUtils.isEmpty(money)){
            ToastView.show(getString(R.string.input_money));
            return;
        }
        double mo = Double.parseDouble(money);
        if (mo <= 0d){
            ToastView.show(getString(R.string.input_money));
            return;
        }
        showProgressDialog("正在充值..");
        MemberChargeAddRequestParam memberChargeAddRequestParam = new MemberChargeAddRequestParam();
        memberChargeAddRequestParam.setMoney(mo);
//        memberChargeAddRequestParam.setPayMethod("1");//支付方式 0微信转账 1支付宝转账
        memberChargeAddRequestParam.setPayMethod(payType + "");//支付方式 0微信转账 1支付宝转账
        MemberChargeAddRequestObject memberChargeAddRequestObject = new MemberChargeAddRequestObject();
        memberChargeAddRequestObject.setParam(memberChargeAddRequestParam);
        httpTool.post(memberChargeAddRequestObject, URLConfig.USER_PAY_MONEY, new HttpTool.HttpCallBack<MemberChargeAddResponseObject>() {
            @Override
            public void onSuccess(MemberChargeAddResponseObject o) {
                dismissProgressDialog();
                if (payType == WECHAT_PAY) {
                    if (ThirdPartyClient.isWeixinAvilible(PayMoneyActivity.this)) {
                        payOrder(o.getRecord().getOrderId(), "");
                    } else {
                        ToastView.show("请安装微信客户端，或者使用其它支付方式");
                    }

                    //                   payOrder(o.getRecord().getOrderId(), "");
                } else {
                    AlipayResponseParam alipayResponseParam = o.getParam();
                    aliPay(alipayResponseParam.getParamStr());
                }

//                AlipayResponseParam alipayResponseParam = o.getParam();
//                aliPay(alipayResponseParam.getParamStr());
            }

            @Override
            public void onError(MemberChargeAddResponseObject o) {
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
               updateUser();
            }
            @Override
            public void onPayError(String resultInfo) {

            }
        });
        aliPayTool.pay(params);
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
                ToastView.show("充值成功");
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

    //==========================================================================================
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
                weixinOrderAddRequestParam.setType("1");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
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
                        WXPayTool wxPayTool = new WXPayTool(PayMoneyActivity.this, weiXin.getParam().getAppid());
                        wxPayTool.payRequest(wxPay);
                    }

                    @Override
                    public void onError(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        Toast.makeText(PayMoneyActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case PAY_ALI:
                AliPayTool aliPayTool = new AliPayTool(this);
                aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                        updateUser();
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
        weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_CHARGE);//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户

        WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
        weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

        httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                updateUser();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
//                if(o.getErrorCode().equals("104703")){
//                    updateUser();
//                }
                //               payAlertTip.show();
            }
        });
    }

    //===========================================================================================
}
