package com.huixiangshenghuo.app.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.mine.CommentActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestParam;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;

public class PayResultActivity extends BaseActivity {


    private TextView moneyView,resultView,shopView,orderView,payTimeView,payTypeView,jifenView;
    private Button button;
    private ImageView errorView;
    private LinearLayout jifenLinearLayout;

    public static final int PAY_GOLD = 0;//金币支付
    public static final int PAY_ALI = 1;//支付宝支付
    private static final int PAY_WECHAT = 2;//微信支付（因为是线下商家跳转过来 线下商家 0 金币 1 支付宝 2 微信）


    private String orderId;
    private static final int SUCCESS = 0;
    private static final int ERROR = -1;
    private int result = ERROR;
    private GoodsOrderInfoResponseParam orderInfo;
    private int payType;


    public static void startPayResultActivity(Context context,String orderId,int payType){
        Intent intent = new Intent(context,PayResultActivity.class);
        intent.putExtra("orderId",orderId);
        intent.putExtra("pay",payType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        orderId = getIntent().getStringExtra("orderId");
        payType = getIntent().getIntExtra("pay",PAY_GOLD);
        initTitleBar_1();
        initView();
    }

    private void initView(){
        titleView.setText("支付结果");
        moneyView = (TextView)findViewById(R.id.money);
        resultView = (TextView)findViewById(R.id.result);
        shopView = (TextView)findViewById(R.id.shop);
        jifenView = (TextView)findViewById(R.id.jifen);
        orderView = (TextView)findViewById(R.id.order);
        payTimeView = (TextView)findViewById(R.id.pay_time);
        payTypeView = (TextView)findViewById(R.id.pay_type);
        button = (Button)findViewById(R.id.next);
        errorView = (ImageView)findViewById(R.id.error);
        jifenLinearLayout = (LinearLayout)findViewById(R.id.jifen_label);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void setViewValue(){
        if (result == SUCCESS){
            double payMoney = orderInfo.getPrice();
            if (payMoney == 0d){
                payMoney = orderInfo.getIntegralNum();
            }
            moneyView.setText("-"+payMoney);
            moneyView.setVisibility(View.VISIBLE);
            resultView.setText("支付成功");
            button.setText("去评价");
//            jifenLinearLayout.setVisibility(View.VISIBLE);
//            jifenView.setText("+"+orderInfo.getReturnIntegral());
        }else{
            int color = ContextCompat.getColor(getApplicationContext(),R.color.colorAccent);
            errorView.setVisibility(View.VISIBLE);
            resultView.setText("支付失败");
            resultView.setTextColor(color);
            button.setText("重新支付");
        }
        shopView.setText(orderInfo.getShopName());
        orderView.setText(orderInfo.getOrderId());
        payTimeView.setText(orderInfo.getCreateDate());
        String pay = "惠宝支付";
//        if (payType == PAY_ALI){
//            pay = "支付宝支付";
//        }
        if (payType == PAY_ALI){
            pay = "支付宝支付";
        } else if (payType == PAY_WECHAT) {
            pay = "微信支付";
        }
        payTypeView.setText(pay);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (result){
                    case SUCCESS:
                        CommentActivity.startCommentActivity(PayResultActivity.this,orderId);
                        finish();
                        break;
                    case ERROR:
                        finish();
                        break;

                }
            }
        });
    }


    private void loadData(){

        GoodsOrderInfoRequestParam goodsOrderInfoRequestParam = new GoodsOrderInfoRequestParam();
        goodsOrderInfoRequestParam.setOrderId(Long.parseLong(orderId));
        GoodsOrderInfoRequestObject goodsOrderInfoRequestObject = new GoodsOrderInfoRequestObject();
        goodsOrderInfoRequestObject.setParam(goodsOrderInfoRequestParam);

        showProgressDialog("正在加载..");
        httpTool.post(goodsOrderInfoRequestObject, URLConfig.ORDER_INFO, new HttpTool.HttpCallBack<GoodsOrderInfoResponseObject>() {
            @Override
            public void onSuccess(GoodsOrderInfoResponseObject o) {
                dismissProgressDialog();
                orderInfo = o.getRecord();
                String status = orderInfo.getStatus();
                if (TextUtils.equals("0",status)){
                    result = ERROR;
                }else{
                    result = SUCCESS;
                }
                setViewValue();
            }

            @Override
            public void onError(GoodsOrderInfoResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }
}
