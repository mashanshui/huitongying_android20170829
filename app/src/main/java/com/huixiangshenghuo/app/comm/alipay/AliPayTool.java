package com.huixiangshenghuo.app.comm.alipay;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * 支付宝支付
 * Created by lenovo on 2016/11/8.
 */
public class AliPayTool {


    private Activity context;
    private OnAliPayResultListener listener;

    public AliPayTool(Activity context){
        this.context = context;
    }
    public void pay(final String orderInfo){
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                final  Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handlerResult(result);
                    }
                });
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void handlerResult(Map<String, String> result){
        @SuppressWarnings("unchecked")
        PayResult payResult = new PayResult(result);
        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
        String resultStatus = payResult.getResultStatus();
        Log.e("info", "handlerResult: " + resultInfo + resultStatus);
         if (TextUtils.equals(resultStatus, "9000")) {
             if(null != listener){
                listener.onPaySuccess();
            }
        } else {
           if(null != listener){
                listener.onPayError(resultInfo);
            }
        }
    }

    public void setOnAliPayResultListener(OnAliPayResultListener listener){
        this.listener = listener;
    }

    public interface OnAliPayResultListener {
         void onPaySuccess();
         void onPayError(String resultInfo);
    }
}
