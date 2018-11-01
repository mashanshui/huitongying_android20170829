package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homeshoprefresh.ShopDetailsNewActivity;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestParam;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;

/**
 * 线下商家订单
 */
public class OrderInfoActivity extends BaseActivity {

    private String orderId;

    private TextView orderNoView,payView,payDateView,stateView,shopView;
    private Button commentButton;
    private LinearLayout shopButton;
    private String Type;
    private String OrderType; //订单类别 订单管理 0  我的订单 1 用来判断是否显示 评论按钮

    public static void startOrderInfoActivity(Context context, String orderId, String Type, String OrderType) {
        Intent intent = new Intent(context,OrderInfoActivity.class);
        intent.putExtra("orderId",orderId);
        intent.putExtra("Type", Type);
        intent.putExtra("OrderType", OrderType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        initView();
        orderId = getIntent().getStringExtra("orderId");
        Type = getIntent().getStringExtra("Type");
        OrderType = getIntent().getStringExtra("OrderType");
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("订单详情");
        orderNoView = (TextView)findViewById(R.id.order_no);
        payView = (TextView)findViewById(R.id.pay_total);
        payDateView = (TextView)findViewById(R.id.pay_date);
        stateView = (TextView)findViewById(R.id.order_state);
        shopView = (TextView)findViewById(R.id.shop);
        shopButton = (LinearLayout)findViewById(R.id.shop_button);
        commentButton = (Button)findViewById(R.id.comment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData(){
        showProgressDialog("正在加载..");
        GoodsOrderInfoRequestParam goodsOrderInfoRequestParam = new GoodsOrderInfoRequestParam();
        goodsOrderInfoRequestParam.setOrderId(Long.parseLong(orderId));
        GoodsOrderInfoRequestObject goodsOrderInfoRequestObject = new GoodsOrderInfoRequestObject();
        goodsOrderInfoRequestObject.setParam(goodsOrderInfoRequestParam);
        httpTool.post(goodsOrderInfoRequestObject, URLConfig.ORDER_INFO, new HttpTool.HttpCallBack<GoodsOrderInfoResponseObject>() {
            @Override
            public void onSuccess(GoodsOrderInfoResponseObject o) {
                dismissProgressDialog();
                GoodsOrderInfoResponseParam goodsOrderInfoResponseParam = o.getRecord();
                setUpView(goodsOrderInfoResponseParam);
            }

            @Override
            public void onError(GoodsOrderInfoResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void setUpView(GoodsOrderInfoResponseParam goodsOrderInfoResponseParam){
        orderNoView.setText(goodsOrderInfoResponseParam.getOrderId());
        double payMoney = goodsOrderInfoResponseParam.getPrice();
        if (payMoney == 0){
            payMoney = goodsOrderInfoResponseParam.getIntegralNum();
        }
        payView.setText(CustomConfig.RMB+payMoney);
        payDateView.setText(goodsOrderInfoResponseParam.getCreateDate());
        String state = goodsOrderInfoResponseParam.getStatus();

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.startCommentActivity(OrderInfoActivity.this,orderId);
            }
        });
        commentButton.setVisibility(View.GONE);
        if (TextUtils.equals("0",state)){
            state = "未支付";
        } else if (TextUtils.equals("4", state) && TextUtils.equals("0", goodsOrderInfoResponseParam.getIsCommented()) && TextUtils.equals("1", OrderType)) {
            commentButton.setVisibility(View.VISIBLE);
            state = "已完成";
        }else if (TextUtils.equals("5",state)){
            state = "已取消";
        } else if (TextUtils.equals("1", state)) {
            state = "待发货";
        } else if (TextUtils.equals("3", state)) {
            state = "待收货";

        } else if (TextUtils.equals("4", state) && TextUtils.equals("0", goodsOrderInfoResponseParam.getIsCommented()) && TextUtils.equals("0", OrderType)) {

            state = "已完成";
        }else {
            state = "已完成";
        }
        stateView.setText(state);
        shopView.setText(goodsOrderInfoResponseParam.getShopName());
        final String shopId = goodsOrderInfoResponseParam.getShopId();
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopDetailsNewActivity.startShopDetailsActivity(OrderInfoActivity.this, shopId);
//                if (Type.equals("0")) {//0  线下  1 线上
//                    ShopDetailsActivity.startShopDetailsActivity(OrderInfoActivity.this, shopId);
//                } else {
//                    ShopDetailsOnlineActivity.startShopActivity(OrderInfoActivity.this,shopId, "");
//
//                }

            }
        });

    }
}
