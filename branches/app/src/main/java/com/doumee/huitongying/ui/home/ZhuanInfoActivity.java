package com.huixiangshenghuo.app.ui.home;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.bitmap.CuttingBitmap;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.exchange.ExchangeAddRequestObject;
import com.doumee.model.request.exchange.ExchangeAddRequestParam;
import com.doumee.model.request.goodsorder.GoodsorderOffAddRequestObject;
import com.doumee.model.request.goodsorder.GoodsorderOffAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.exchange.ExchangeAddResponseObject;
import com.doumee.model.response.exchange.ExchangeAddResponseParam;
import com.doumee.model.response.goodsorder.GoodsorderOffAddResponseObject;
import com.doumee.model.response.goodsorder.GoodsorderOffAddResponseParam;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ZhuanInfoActivity extends BaseActivity {


    private ImageView faceView;
    private TextView nameView,phoneView,payLabelView;
    private EditText moneyView,noteView,goldView;
    private RadioGroup radioGroup;
    private Button submitButton;
    private AlertDialog alertDialog;
    private Bitmap defaultBitmap;


    private static final int PAY_TYPE_1 = 0;//金币
    private static final int PAY_TYPE_ALI = 1;//支付宝
    //===================================================
    private static final int WECHAT_TYPE_PAY = 2;//微信

    private static final int WECHAT_PAY = 0;//微信支付
    private static final int PAY_ALI = 1;//支付宝支付

    private int payWeChat = 0;
    private int pay = PAY_ALI;
    //===================================================
    private int payType = PAY_TYPE_1;
    private String payPassword;

    private MemberListByNamesResponseParam userInfo;
    private ShopInfoResponseParam shopInfo;

    private String aliParams;
    private double gold;
    private String orderId;

    public static void startZhuanInfoActivity(Context context,MemberListByNamesResponseParam user){
        Intent intent = new Intent(context,ZhuanInfoActivity.class);
        intent.putExtra("userInfo",user);
        context.startActivity(intent);
    }

    public static void startZhuanInfoActivity(Context context,ShopInfoResponseParam shopInfo){
        Intent intent = new Intent(context,ZhuanInfoActivity.class);
        intent.putExtra("shopInfo",shopInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuan_info);
        shopInfo = (ShopInfoResponseParam)getIntent().getSerializableExtra("shopInfo");
        userInfo = (MemberListByNamesResponseParam)getIntent().getSerializableExtra("userInfo");
        defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.header_img_bg);
        defaultBitmap = CuttingBitmap.toRoundBitmap(defaultBitmap);
        initTitleBar_1();
        initView();
        initListener();
        setViewValue();
        initDialog();
    }

    private void initView(){

        faceView = (ImageView)findViewById(R.id.face);
        nameView = (TextView)findViewById(R.id.name);
        phoneView = (TextView)findViewById(R.id.phone);
        moneyView = (EditText)findViewById(R.id.input_money);
        noteView = (EditText)findViewById(R.id.input_notes);
        goldView = (EditText)findViewById(R.id.input_gold);
        radioGroup = (RadioGroup)findViewById(R.id.pay_type);
        submitButton = (Button)findViewById(R.id.next);
        payLabelView = (TextView)findViewById(R.id.pay_label);

        moneyView.setFilters(new InputFilter[]{lengthfilter});
        goldView.setFilters(new InputFilter[]{lengthfilter});
        if (null != shopInfo){
            submitButton.setText("确定买单");
            titleView.setText("买单");
            payLabelView.setText("买单方式");
        }
        else if (null != userInfo){
            titleView.setText("转账");
            submitButton.setText("确定转账");
        }

        goldView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 String moneyStr = "0";
                 if (!TextUtils.isEmpty(s.toString())){
                     moneyStr = s.toString();
                 }
                double money = Double.parseDouble(moneyStr);
                if (money> gold){
                    goldView.setText(gold + "");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        gold = Double.parseDouble(NumberFormatTool.numberFormat(userInfo.getMoney()))  ;
        goldView.setHint("当前可用惠宝" + gold);
    }

    private void setViewValue(){
        faceView.setImageBitmap(defaultBitmap);
        if (null != userInfo){
        String imageUrl = userInfo.getImgUrl();
        if (!TextUtils.isEmpty(imageUrl)){
            ImageLoader.getInstance().loadImage(imageUrl,new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    faceView.setImageBitmap(CuttingBitmap.toRoundBitmap(loadedImage));
                }
            });
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userInfo.getLoginName());
        if (!TextUtils.isEmpty(userInfo.getName())){
            stringBuilder.append("(*");
            stringBuilder.append(userInfo.getName().substring(1));
            stringBuilder.append(")");
        }
        nameView.setText(stringBuilder.toString());
        if (!TextUtils.isEmpty(userInfo.getPhone())){
            phoneView.setText(userInfo.getPhone().substring(0,3)+"****"+userInfo.getPhone().substring(8));
        }
       }else if (shopInfo != null){
            String shopImage = shopInfo.getImgurl();
            if (!TextUtils.isEmpty(shopImage)){
                ImageLoader.getInstance().loadImage(shopImage,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        faceView.setImageBitmap(CuttingBitmap.toRoundBitmap(loadedImage));
                    }
                });
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(shopInfo.getName());
            nameView.setText(stringBuilder.toString());
            if (!TextUtils.isEmpty(shopInfo.getPhone())){
                phoneView.setText(shopInfo.getPhone());
            }
        }
    }

    private void initListener(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pay_1://金币
                        payType = PAY_TYPE_1;
                        goldView.setVisibility(View.VISIBLE);
                        moneyView.setVisibility(View.GONE);
                        break;
                    case R.id.pay_ali://支付宝
                        payType = PAY_TYPE_ALI;
                        goldView.setVisibility(View.GONE);
                        moneyView.setVisibility(View.VISIBLE);
                        //====================================
                        pay = PAY_ALI;
                        //====================================
                        break;
                    case R.id.rb_pay_wechat://微信
                        payType = WECHAT_TYPE_PAY;
                        goldView.setVisibility(View.GONE);
                        moneyView.setVisibility(View.VISIBLE);
                        //====================================
                        pay = WECHAT_PAY;
                        //====================================
                        break;
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void submitData(){
       switch (payType){
            case PAY_TYPE_1:
                String gold = goldView.getText().toString().trim();
                if (!TextUtils.isEmpty(gold) && Double.parseDouble(gold) > 0d){
                    alertDialog.show();
                }else{
                    ToastView.show("请输入惠宝");
                }
                break;

            case PAY_TYPE_ALI:
                String money = moneyView.getText().toString().trim();
                if (TextUtils.isEmpty(money)){
                    ToastView.show("请输入金额");
                    return;
                }
                double mo = Double.parseDouble(money);
                if (mo <= 0d){
                    ToastView.show("请输入金额");
                    return;
                }
                if (TextUtils.isEmpty(aliParams)){
                    if (shopInfo != null){
                        shopPay();//到店消费
                    }else{
                        submit();//会员转账
                    }
                }else{
                    aliPay();//支付宝支付
                }
                break;
           case WECHAT_TYPE_PAY:
               String money2 = moneyView.getText().toString().trim();
               if (TextUtils.isEmpty(money2)) {
                   ToastView.show("请输入金额");
                   return;
               }
               double mo2 = Double.parseDouble(money2);
               if (mo2 <= 0d) {
                   ToastView.show("请输入金额");
                   return;
               }
               if (shopInfo != null) {
                   shopPay();//到店消费
               } else {
                   submit();//会员转账
               }
               break;
        }
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
                RegActivity.startRegActivity(ZhuanInfoActivity.this,RegActivity.EDIT_PAY_PASSWORD);
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
                if (shopInfo != null){
                    shopPay();//到店消费
                }else{
                    submit();//会员转账
                }

            }
        });
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog = builder.create();
    }

    //会员转账
    private void submit(){
        String info = noteView.getText().toString().trim();
        ExchangeAddRequestParam exchangeAddRequestParam = new ExchangeAddRequestParam();
        if (payType == PAY_TYPE_1){
            String gold = goldView.getText().toString().trim();
            if (TextUtils.isEmpty(gold)){
                ToastView.show("请输入惠宝");
                return;
            }
            double payGold = Double.parseDouble(gold);
            exchangeAddRequestParam.setIntegralNum(payGold);
            exchangeAddRequestParam.setPayPwd(payPassword);
            exchangeAddRequestParam.setMoney(payGold);
        }else{
            String money = moneyView.getText().toString().trim();
            if (TextUtils.isEmpty(money)){
                ToastView.show("请输入金额");
                return;
            }
            double payMoney = Double.parseDouble(money);
            exchangeAddRequestParam.setMoney(payMoney);
        }
        exchangeAddRequestParam.setInfo(info);
//        exchangeAddRequestParam.setPayMethod("1");//支付方式 0微信转账 1支付宝转账
        exchangeAddRequestParam.setPayMethod(pay + "");//支付方式 0微信转账 1支付宝转账
        if (null != userInfo){
            exchangeAddRequestParam.setObjectId(userInfo.getMemberId());
        }
        else if (null != shopInfo){
         exchangeAddRequestParam.setObjectId(shopInfo.getMemberId());
        }
        ExchangeAddRequestObject exchangeAddRequestObject = new ExchangeAddRequestObject();
        exchangeAddRequestObject.setParam(exchangeAddRequestParam);
        showProgressDialog("正在操作..");
        httpTool.post(exchangeAddRequestObject, URLConfig.USER_ZHUANZHANG, new HttpTool.HttpCallBack<ExchangeAddResponseObject>() {
            @Override
            public void onSuccess(ExchangeAddResponseObject o) {
                dismissProgressDialog();
                ExchangeAddResponseParam record = o.getRecord();
                orderId = record.getOrderId();
                String isPayDone = record.getIsPayDone();
                if (TextUtils.equals("0",isPayDone)){//未支付完成
//                    aliParams  = o.getParam().getParamStr();
//                    aliPay();//支付宝支付
                    if (pay == WECHAT_PAY) {//选择微信的时候 o.getParam().getParamStr() 没有传值过来
                        if (ThirdPartyClient.isWeixinAvilible(ZhuanInfoActivity.this)) {
                            payOrder(record.getOrderId(), "");
                        } else {
                            ToastView.show("请安装微信客户端，或者使用其它支付方式");
                        }


//                        payOrder(record.getOrderId(), "");
                    } else {
                        aliParams = o.getParam().getParamStr();
                        payOrder(record.getOrderId(), o.getParam().getParamStr());
                    }
                }else{
                    success();
                }
            }
            @Override
            public void onError(ExchangeAddResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    //到店消费 线下商家
    private void shopPay(){
        String info = noteView.getText().toString().trim();
        GoodsorderOffAddRequestParam goodsorderOffAddRequestParam = new GoodsorderOffAddRequestParam();
        goodsorderOffAddRequestParam.setObjectId(shopInfo.getMemberId());
//        goodsorderOffAddRequestParam.setPayMethod("1");//支付方式 0微信转账 1支付宝转账
        goodsorderOffAddRequestParam.setPayMethod(pay + "");//支付方式 0微信转账 1支付宝转账
        goodsorderOffAddRequestParam.setInfo(info);
        goodsorderOffAddRequestParam.setShopId(shopInfo.getShopId());
        if (payType == PAY_TYPE_1){
            String gold = goldView.getText().toString().trim();
            if (TextUtils.isEmpty(gold)){
                ToastView.show("请输入惠宝");
                return;
            }
            double payGold = Double.parseDouble(gold);
            goodsorderOffAddRequestParam.setIntegralNum(payGold);
            goodsorderOffAddRequestParam.setPayPwd(payPassword);
            goodsorderOffAddRequestParam.setMoney(payGold);
        }else{
            String money = moneyView.getText().toString().trim();
            if (TextUtils.isEmpty(money)){
                ToastView.show("请输入金额");
                return;
            }
            double payMoney = Double.parseDouble(money);
            goodsorderOffAddRequestParam.setMoney(payMoney);
        }
        GoodsorderOffAddRequestObject goodsorderOffAddRequestObject = new GoodsorderOffAddRequestObject();
        goodsorderOffAddRequestObject.setParam(goodsorderOffAddRequestParam);
        showProgressDialog("正在操作..");
        httpTool.post(goodsorderOffAddRequestObject, URLConfig.ORDER_SHOP_PAY, new HttpTool.HttpCallBack<GoodsorderOffAddResponseObject>() {
            @Override
            public void onSuccess(GoodsorderOffAddResponseObject o) {
                dismissProgressDialog();
                GoodsorderOffAddResponseParam record = o.getRecord();
                orderId = record.getOrderId();
                String isPayDone = record.getIsPayDone();
                if (TextUtils.equals("0",isPayDone)){//未支付完成
//                    aliParams  = o.getParam().getParamStr();
//                    aliPay();//支付宝支付
                    //==============================================
                    if (pay == WECHAT_PAY) {//选择微信的时候 o.getParam().getParamStr() 没有传值过来
                        if (ThirdPartyClient.isWeixinAvilible(ZhuanInfoActivity.this)) {
                            payOrder(record.getOrderId(), "");
                        } else {
                            ToastView.show("请安装微信客户端，或者使用其它支付方式");
                        }

//                        payOrder(record.getOrderId(), "");
                    } else {
                        aliParams = o.getParam().getParamStr();
                        payOrder(record.getOrderId(), o.getParam().getParamStr());
                    }
                    //==============================================
                }else{
                    success();
                }
            }

            @Override
            public void onError(GoodsorderOffAddResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });


    }

    //支付宝支付
    private void aliPay(){
        AliPayTool aliPayTool = new AliPayTool(this);
        aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
            @Override
            public void onPaySuccess() {
                success();
            }
            @Override
            public void onPayError(String resultInfo) {
                finish();
            }
        });
        aliPayTool.pay(aliParams);
    }

    //用户信息
    private void success(){
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
                UserInfoResponseParam userInfo = o.getMember();
                SaveObjectTool.saveObject(userInfo);
                if (null != shopInfo){
                    PayResultActivity.startPayResultActivity( ZhuanInfoActivity.this,orderId,payType);
                }else{
                    ToastView.show("支付成功");
                }
                finish();
            }
            @Override
            public void onError(MemberInfoResponseObject o) {
                   dismissProgressDialog();
            }
        });
    }
    //========================================================================================

    @Override
    protected void onResume() {
        super.onResume();
        if (pay == WECHAT_PAY && payWeChat == 1) {
            loadPayResult();
        }
    }

    //支付订单
    private void payOrder(final String orderId, String aliParams) {

        switch (pay) {
            case WECHAT_PAY:
                this.orderId = orderId;
                showProgressDialog(getString(R.string.loading));
                WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
                weixinOrderAddRequestParam.setOrderId(orderId);
                weixinOrderAddRequestParam.setTradeType("APP");
                if (shopInfo != null) {
                    //到店消费
                    weixinOrderAddRequestParam.setType("0");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
                } else {
                    //会员转账
                    weixinOrderAddRequestParam.setType("3");//订单类型：订单类型：0商品订单 1充值 2升级 3转账 4成为商户
                }
//                weixinOrderAddRequestParam.setType("0");//订单类型：订单类型：0商品订单 1充值 2升级 3转账 4成为商户
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
                        WXPayTool wxPayTool = new WXPayTool(ZhuanInfoActivity.this, weiXin.getParam().getAppid());
                        wxPayTool.payRequest(wxPay);
                    }

                    @Override
                    public void onError(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        Toast.makeText(ZhuanInfoActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case PAY_TYPE_ALI:
                AliPayTool aliPayTool = new AliPayTool(ZhuanInfoActivity.this);
                aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                        success();
                    }

                    @Override
                    public void onPayError(String resultInfo) {
                        finish();
                    }
                });
                aliPayTool.pay(aliParams);
                break;


        }
    }

    //订单支付结果
    private void loadPayResult() {
        showProgressDialog(getString(R.string.loading));
        WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
        weixinOrderQueryRequestParam.setOrderId(orderId);
//        weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_PRODUCT);
        if (shopInfo != null) {
            //到店消费
            weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_PRODUCT);//订单类型：订单类型：0商品订单 1充值 2升级 3转账 4成为商户
        } else {
            //会员转账
            weixinOrderQueryRequestParam.setType("3");//订单类型：订单类型：0商品订单 1充值 2升级 3转账 4成为商户
        }

        WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
        weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

        httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                success();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
//                payAlertTip.show();
                finish();
            }
        });
    }
    //========================================================================================
}
