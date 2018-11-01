package com.huixiangshenghuo.app.comm.wxpay;


import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * 微信支付工具
 */
public class WXPayTool {

   private static final String PACKAGE = "Sign=WXPay";//暂填写固定值Sign=WXPay

   private IWXAPI api;

   public WXPayTool(Context context, String appId) {
      api = WXAPIFactory.createWXAPI(context, null);
      api.registerApp(appId);
   }

   public void payRequest(WXPay wxPay) {
      PayReq request = new PayReq();
      request.appId = wxPay.getAppId();
      request.partnerId = wxPay.getPartnerId();
      request.prepayId = wxPay.getPrepayId();
      request.packageValue = wxPay.getPackageStr();
      request.nonceStr = wxPay.getNonceStr();
      request.timeStamp = wxPay.getTimeStamp();
      request.sign = wxPay.getSign();
      api.sendReq(request);
   }

   public IWXAPI getIWXApi() {
      return api;
   }


   public static class WXPay {
      private String prepayId;
      private String nonceStr;
      private String sign;
      private String appId;
      private String partnerId;
      private String timeStamp;
      private String packageStr;

      public String getPackageStr() {
         return packageStr;
      }

      public void setPackageStr(String packageStr) {
         this.packageStr = packageStr;
      }

      public String getPartnerId() {
         return partnerId;
      }

      public void setPartnerId(String partnerId) {
         this.partnerId = partnerId;
      }

      public String getTimeStamp() {
         return timeStamp;
      }

      public void setTimeStamp(String timeStamp) {
         this.timeStamp = timeStamp;
      }

      public String getAppId() {
         return appId;
      }

      public void setAppId(String appId) {
         this.appId = appId;
      }

      public String getPrepayId() {
         return prepayId;
      }

      public void setPrepayId(String prepayId) {
         this.prepayId = prepayId;
      }

      public String getNonceStr() {
         return nonceStr;
      }

      public void setNonceStr(String nonceStr) {
         this.nonceStr = nonceStr;
      }

      public String getSign() {
         return sign;
      }

      public void setSign(String sign) {
         this.sign = sign;
      }
   }

}
