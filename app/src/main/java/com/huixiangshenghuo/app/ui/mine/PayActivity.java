package com.huixiangshenghuo.app.ui.mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;

/**
 * Created by Administrator on 2017/4/12.
 */

public class PayActivity extends BaseActivity {


   RadioGroup radioGroup;

   Button payButton;


   private String orderId;
   private String aliParams;//支付宝支付订单参数
   private static final int WECHAT_PAY = 0;//微信支付
   private static final int ALI_PAY = 1;//支付宝支付
   //   private int pay = WECHAT_PAY;
   private int pay = ALI_PAY;
   private HttpTool httpTool;
   private AlertDialog payAlertTip;
   private int payWeChat = 0;


   public static void startPayActivity(Context context, String orderId, String paramsStr) {
      Intent intent = new Intent(context, PayActivity.class);
      intent.putExtra("orderId", orderId);
      intent.putExtra("paramsStr", paramsStr);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_pay);
      initView();
      orderId = getIntent().getStringExtra("orderId");
      aliParams = getIntent().getStringExtra("paramsStr");
      httpTool = HttpTool.newInstance(this);
      radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.wechat_pay:
                  pay = WECHAT_PAY;
                  break;
               case R.id.ali_pay:
                  pay = ALI_PAY;
                  break;

            }
         }
      });

      payButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            payOrder();
         }
      });
      payAlertTip();

   }

   @Override
   protected void onStart() {
      super.onStart();
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (pay == WECHAT_PAY && payWeChat == 1) {
         loadPayResult();
      }
   }


   private void initView() {
      initTitleBar_1();
      titleView.setText("支付订单");
//      mIbFinish.setVisibility(View.VISIBLE);
//      showTitleBar();
//      mTvTitle.setText("支付订单");
//      View view = View.inflate(mContext, R.layout.activity_pay, null);
//      ButterKnife.bind(this,view);
//      mFlContent.addView(view);
//      mIbFinish.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            finish();
//         }
//      });
      radioGroup = (RadioGroup) findViewById(R.id.pay_type);
      payButton = (Button) findViewById(R.id.pay);
   }

   //支付订单
   private void payOrder() {

      switch (pay) {
         case WECHAT_PAY:
            showProgressDialog(getString(R.string.loading));
            WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
            weixinOrderAddRequestParam.setOrderId(orderId);
            weixinOrderAddRequestParam.setTradeType("APP");
            weixinOrderAddRequestParam.setType("0");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
            WeixinOrderAddRequestObject weixinOrderAddRequestObject = new WeixinOrderAddRequestObject();
            weixinOrderAddRequestObject.setParam(weixinOrderAddRequestParam);
            httpTool.post(weixinOrderAddRequestObject, URLConfig.ORDER_WECHAT_PAY, new HttpTool.HttpCallBack<WeixinOrderAddResponseObject>() {
               @Override
               public void onSuccess(WeixinOrderAddResponseObject o) {
                  dismissProgressDialog();
                  payWeChat = 1;
                  WeixinOrderResponseParam weiXin = o.getData();
                  WXPayTool.WXPay wxPay = new WXPayTool.WXPay();
                  PayOrderRequestEntity payOrderRequestEntity = weiXin.getParam();
                  wxPay.setNonceStr(payOrderRequestEntity.getNoncestr());
                  wxPay.setPrepayId(payOrderRequestEntity.getPrepayid());
                  wxPay.setSign(payOrderRequestEntity.getSign());
                  wxPay.setAppId(payOrderRequestEntity.getAppid());
                  wxPay.setPartnerId(payOrderRequestEntity.getPartnerid());
                  wxPay.setPackageStr(payOrderRequestEntity.getPackageStr());
                  wxPay.setTimeStamp(payOrderRequestEntity.getTimestamp());
                  WXPayTool wxPayTool = new WXPayTool(PayActivity.this, weiXin.getParam().getAppid());
                  wxPayTool.payRequest(wxPay);
               }

               @Override
               public void onError(WeixinOrderAddResponseObject o) {
                  dismissProgressDialog();
                  Toast.makeText(PayActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
               }
            });
            break;
         case ALI_PAY:
            AliPayTool aliPayTool = new AliPayTool(PayActivity.this);
            aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
               @Override
               public void onPaySuccess() {
                  paySuccess();
               }

               @Override
               public void onPayError(String resultInfo) {
                  payAlertTip.show();
               }
            });
            aliPayTool.pay(aliParams);
            break;


      }
   }

   /**
    * 支付成功后，返回支付状态
    */
   private void paySuccess() {
      Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_LONG).show();
      finish();
   }


   private void loadPayResult() {
      showProgressDialog(getString(R.string.loading));
      WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
      weixinOrderQueryRequestParam.setOrderId(orderId);
      weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_PRODUCT);

      WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
      weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

      httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            dismissProgressDialog();
            paySuccess();
         }

         @Override
         public void onError(ResponseBaseObject o) {
            dismissProgressDialog();
            payAlertTip.show();
         }
      });
   }

   private void payAlertTip() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(getApplicationContext(), R.layout.app_user_login_tip, null);
      TextView textView = (TextView) view.findViewById(R.id.message);
      Button button = (Button) view.findViewById(R.id.cancel_button);
      textView.setText("该订单未支付，可以到订单列表中继续支付");
      button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            payAlertTip.dismiss();
            finish();
         }
      });
      builder.setView(view);
      builder.setCancelable(false);
      payAlertTip = builder.create();
   }
}
