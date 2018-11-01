package com.huixiangshenghuo.app.ui.fragments.mine;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homepage.ConfirmOrderPingjiaActivity;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.mine.CommentActivity;
import com.huixiangshenghuo.app.ui.mine.MineOrderListActivity;
import com.huixiangshenghuo.app.ui.mine.OrderInfoActivity;
import com.huixiangshenghuo.app.ui.mine.PayActivity;
import com.huixiangshenghuo.app.ui.mine.ShopOrderInfoActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.goodsorder.GoodsOrderEditRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderEditRequestParam;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.GoodsDetailsResponeParam;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import static com.huixiangshenghuo.app.R.id.status;
/**
 * 我的订单列表
 */
public class MineOrderListFragment extends Fragment implements RefreshLayout.ILoadListener,RefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "state";

    private int state;
    private ListView listView;
    private RefreshLayout refreshLayout;

    private ArrayList<GoodsOrderListResponseParam> dataList;
    private CustomBaseAdapter<GoodsOrderListResponseParam> adapter;
    private String firstQueryTime;
    private int page = 1;
    private HttpTool httpTool;

    //     private CustomBaseAdapter<GoodsDetailsResponeParam> lv_adapter;
    private Bitmap defaultBitmap;
    private GoodsOrderListResponseParam goodsOrderListResponseParam;
    private BitmapUtils bitmapUtils;

    private static final int COMMENT_SUCCESS = 1;//评价成功
    public MineOrderListFragment() {

    }

    public static MineOrderListFragment newInstance(int  param1) {
        MineOrderListFragment fragment = new MineOrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getInt(ARG_PARAM1);
        }
        //    defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.business_default);
        httpTool = HttpTool.newInstance(getActivity());
        initBitmapParames();
        dataList = new ArrayList<>();

        adapter = new CustomBaseAdapter<GoodsOrderListResponseParam>(dataList,R.layout.fragment_mine_order_list_item) {
            @Override
            public void bindView(ViewHolder holder,final GoodsOrderListResponseParam obj) {

                ImageView imageView = holder.getView(R.id.image);
                TextView nameView = holder.getView(R.id.name);
                TextView order_number = holder.getView(R.id.order_number);
                TextView priceView = holder.getView(R.id.price);
                TextView dateView = holder.getView(R.id.date);
                Button actionButton = holder.getView(R.id.action);
                Button cancelButton = holder.getView(R.id.cancel_action);
                Button payButton = holder.getView(R.id.pay_action);
                TextView statusView = holder.getView(status);
                TextView numView = holder.getView(R.id.goods_num);
                TextView totalView = holder.getView(R.id.total);
                LinearLayout goodsListView = holder.getView(R.id.goods_list);
                Button bt_shouhuo = holder.getView(R.id.bt_shouhuo);

//              ListView lv = holder.getView(R.id.lv_mine_order_list_item);

//                totalView.setText("总价：" + CustomConfig.RMB + NumberFormatTool.numberFormat(obj.getTotalPrice()));
                totalView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(obj.getTotalPrice()));
                String status = obj.getStatus();
                nameView.setText(obj.getShopName());
                order_number.setText("订单号：" + obj.getOrderId());
                priceView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(obj.getTotalPrice()));
                dateView.setText(obj.getCreateDate());
                String statusStr = "已完成";
                statusView.setTextColor(getResources().getColor(R.color.lightGrey));
                String path = obj.getImgurl();
//                if (!TextUtils.isEmpty(path)){
//                    ImageLoader.getInstance().displayImage(path,imageView);
//                }else{
//                    imageView.setImageBitmap(defaultBitmap);
//                }
                //       bitmapUtils.display(imageView, path);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goodsOrderListResponseParam = obj;

                        if (obj.getType().equals("0")) {//订单类型  0到店消费订单 1 普通商品订单
                            Intent intent = new Intent(getActivity(), CommentActivity.class);
                            intent.putExtra("orderId", obj.getOrderId());
                            startActivityForResult(intent, 1);
                        } else {
                            Intent intent = new Intent(getActivity(), ConfirmOrderPingjiaActivity.class);
                            intent.putExtra("order", obj.getOrderId());
                            startActivityForResult(intent, COMMENT_SUCCESS);
                        }

                    }
                });
                //确认收货
                bt_shouhuo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog1 = new Dialog(getActivity());
                        dialog1.setTitle("温馨提示");
                        dialog1.setMessage("是否确认收货");
                        dialog1.setConfirmText("确认");
                        dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goodsOrderListResponseParam = obj;
                                cancelOrder("4");
                            }
                        });
                        dialog1.show();


                    }
                });
                bt_shouhuo.setVisibility(View.GONE);
                actionButton.setVisibility(View.GONE);
                payButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                if (TextUtils.equals("0",status)){//未支付
                    cancelButton.setVisibility(View.VISIBLE);
                    payButton.setVisibility(View.VISIBLE);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog1 = new Dialog(getActivity());
                            dialog1.setTitle("温馨提示");
                            dialog1.setMessage("是否取消支付");
                            dialog1.setConfirmText("确认");
                            dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goodsOrderListResponseParam = obj;
                                    cancelOrder("5");
                                }
                            });
                            dialog1.show();

                        }
                    });

                    payButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goodsOrderListResponseParam = obj;
//                            goPay(obj.getParamStr(), obj.getType());
                            PayActivity.startPayActivity(getActivity(), obj.getOrderId(), obj.getParamStr());

                        }
                    });
                    statusStr = "未支付";
                    statusView.setTextColor(getResources().getColor(R.color.red));
                }else if (TextUtils.equals("4",status) && TextUtils.equals("0",obj.getIsCommented())){
                    actionButton.setVisibility(View.VISIBLE);
                    statusStr = "待评价";
                    statusView.setTextColor(getResources().getColor(R.color.red));
                }else if (TextUtils.equals("5",status)){
                    statusStr = "已取消";
                    statusView.setTextColor(getResources().getColor(R.color.lightGrey));
                } else if (TextUtils.equals("1", status)) {
                    statusStr = "待发货";
                    statusView.setTextColor(getResources().getColor(R.color.red));
                } else if (TextUtils.equals("3", status)) {
                    statusStr = "待收货";
                    statusView.setTextColor(getResources().getColor(R.color.red));
                    bt_shouhuo.setVisibility(View.VISIBLE);
                }
                statusView.setText(statusStr);
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //订单类型  0到店消费订单 1 普通商品订单
                        //订单类别 订单管理0  我的订单 1 用来判断是否显示 评论按钮
                        if (obj.getType().equals("0")) {
                            OrderInfoActivity.startOrderInfoActivity(getActivity(), obj.getOrderId(), obj.getType(), "1");
                        } else {
                            ShopOrderInfoActivity.startOrderInfoActivity(getActivity(), obj.getOrderId(), obj.getType());
                        }



                    }
                });
                //listview 嵌套 listview
//                lv_adapter = new CustomBaseAdapter<GoodsDetailsResponeParam>((ArrayList<GoodsDetailsResponeParam>) obj.getGoodsList(),R.layout.fragment_shop_order_manager_item_2) {
//                    @Override
//                    public void bindView(ViewHolder holder, GoodsDetailsResponeParam obj) {
//                        double total = 0d;
//                        int num = 0;
//
//
//
//                        ImageView imageView2 =  holder.getView(R.id.goods_image);
//                        TextView nameView =  holder.getView(R.id.goods_name);
//                        TextView priceView =  holder.getView(R.id.goods_price);
//                        TextView dateView =  holder.getView(R.id.date);
//                        TextView numView2 = holder.getView(R.id.goods_nn);
//                        LinearLayout ll_zong =  holder.getView(R.id.ll_shop_order_manager_item_2);
//                        total += obj.getPrice() * obj.getNum();
//                        num += obj.getNum();
//
//                        bitmapUtils.display(imageView2, obj.getProImg());
//                        nameView.setText(obj.getProName());
//                        priceView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(obj.getPrice()));
//                        dateView.setText(obj.getCreateDate());
//                        numView2.setText("×" + obj.getNum());
//
//
//                        if (num <= 0) {
//                            numView.setText("线下买单");
//                        } else {
//                            numView.setText("共" + num + "件商品");
//                        }
//                    }
//                };
//
//
//                lv.setAdapter(lv_adapter);
                if (obj.getType().equals("0")) {//订单类型 1普通商品订单 0到店消费订单

                    numView.setText("线下买单");
                } else {
                    numView.setText(obj.getCreateDate());
                }
                initAdapterItem(obj.getGoodsList(), totalView, numView, goodsListView, obj.getType());
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mine_order_list, container, false);
    }

    /**
     * 图片加载
     */
    public void initBitmapParames() {
        bitmapUtils = new BitmapUtils(getActivity());
//        bitmapUtils.configDefaultShowOriginal(false);//true:显示原始图片，false：将会对图片压缩处理
        // 设置加载失败图片
        bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
        // 设置没有加载成功图片
        bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        bitmapUtils.configDefaultShowOriginal(false);//true:显示原始图片，false：将会对图片压缩处理
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list_view);
        refreshLayout = (RefreshLayout)getView().findViewById(R.id.refresh);
        listView.setAdapter(adapter);
        refreshLayout.setLoading(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        refreshLayout.setRefreshing(true);
        firstQueryTime = "";
        dataList.clear();
        loadData();
    }

    @Override
    public void onLoad() {
        page ++;
        refreshLayout.setLoading(true);
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1){
                if (null != goodsOrderListResponseParam){
                    goodsOrderListResponseParam.setIsCommented("1");
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    //现在需要跳转页面
    private void goPay(String params, final String type) {////订单类型  0到店消费订单 1 普通商品订单
        AliPayTool aliPayTool = new AliPayTool(getActivity());
        aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
            @Override
            public void onPaySuccess() {
                if (null != goodsOrderListResponseParam){
                    ToastView.show("支付成功");
                    if (type.equals("0")) {
                        goodsOrderListResponseParam.setStatus("4");
                    } else {
                        goodsOrderListResponseParam.setStatus("1");
                    }

                    //            goodsOrderListResponseParam.setStatus("4");
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onPayError(String resultInfo) {

            }
        });
        aliPayTool.pay(params);
    }

    private void cancelOrder(final String status) {//订单状态( 3已发货 4确认收货5取消订单)
        GoodsOrderEditRequestParam goodsOrderEditRequestParam = new GoodsOrderEditRequestParam();
        goodsOrderEditRequestParam.setOrderId(Long.parseLong(goodsOrderListResponseParam.getOrderId()));
        goodsOrderEditRequestParam.setStatus(status);
        GoodsOrderEditRequestObject goodsOrderEditRequestObject = new GoodsOrderEditRequestObject();
        goodsOrderEditRequestObject.setParam(goodsOrderEditRequestParam);
        httpTool.post(goodsOrderEditRequestObject, URLConfig.ORDER_UPDATE, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
//                if(status.equals("4")){
//                    final Dialog dialog1 = new Dialog(getActivity());
//                    dialog1.setTitle("温馨提示");
//                    dialog1.setMessage("是否确认收货");
//                    dialog1.setConfirmText("确认");
//                    dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            goodsOrderListResponseParam.setStatus(status);
//                            adapter.notifyDataSetChanged();
//                            dialog1.dismiss();
//                        }
//                    });
//                    dialog1.show();
//                }else{
//                    goodsOrderListResponseParam.setStatus(status);
//                    adapter.notifyDataSetChanged();
//                }

                goodsOrderListResponseParam.setStatus(status);
                adapter.notifyDataSetChanged();


            }
            @Override
            public void onError(ResponseBaseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void loadData(){
        GoodsOrderListRequestParam goodsOrderListRequestParam = new GoodsOrderListRequestParam();
        if (state == MineOrderListActivity.ORDER_START_ALL){
            goodsOrderListRequestParam.setStatus("");
        }else{
            goodsOrderListRequestParam.setStatus(state+"");
        }
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(firstQueryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);

        GoodsOrderListRequestObject goodsOrderListRequestObject = new GoodsOrderListRequestObject();
        goodsOrderListRequestObject.setParam(goodsOrderListRequestParam);
        goodsOrderListRequestObject.setPagination(paginationBaseObject);

        httpTool.post(goodsOrderListRequestObject, URLConfig.MY_ORDER_LIST, new HttpTool.HttpCallBack<GoodsOrderListResponseObject>() {
            @Override
            public void onSuccess(GoodsOrderListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                firstQueryTime = o.getFirstQueryTime();
                List<GoodsOrderListResponseParam> orderList = o.getOrderList();
                dataList.addAll(orderList);
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundResource(0);
                }
            }
            @Override
            public void onError(GoodsOrderListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void initAdapterItem(List<GoodsDetailsResponeParam> lists, TextView totalView, TextView totalNumView, LinearLayout goodsListView, String TYPE) {
        double total = 0d;
        int num = 0;
        goodsListView.removeAllViews();

        //==============================
        if (lists == null) {
            return;
        }
        //==============================
        for (GoodsDetailsResponeParam shopOrderList : lists) {
            View view = View.inflate(getActivity(), R.layout.fragment_shop_order_manager_item_2, null);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.goods_image);
            TextView nameView = (TextView) view.findViewById(R.id.goods_name);
            TextView priceView = (TextView) view.findViewById(R.id.goods_price);
            TextView dateView = (TextView) view.findViewById(R.id.date);
            TextView numView = (TextView) view.findViewById(R.id.goods_nn);
            LinearLayout ll_zong = (LinearLayout) view.findViewById(R.id.ll_shop_order_manager_item_2);
            total += shopOrderList.getPrice() * shopOrderList.getNum();
            num += shopOrderList.getNum();
//            imageView.setImageBitmap(defaultBitmap);
//            if (!TextUtils.isEmpty(shopOrderList.getProImg())) {
//                ImageLoader.getInstance().displayImage(shopOrderList.getProImg(), imageView);
//            }
            bitmapUtils.display(imageView2, shopOrderList.getProImg());
            nameView.setText(shopOrderList.getProName());
            priceView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(shopOrderList.getPrice()));
//            dateView.setText(shopOrderList.getCreateDate());
            numView.setText("×" + shopOrderList.getNum());
            goodsListView.addView(view);
        }

//        if (TYPE.equals("1")) {//订单类型 1普通商品订单 0到店消费订单
//
//            totalNumView.setText("共" + num + "件商品");
//        }

//        if (num <= 0) {
//            totalNumView.setText("线下买单");
//        } else {
//            totalNumView.setText("共" + num + "件商品");
//        }

        //    totalView.setText("总价："+CustomConfig.RMB+NumberFormatTool.numberFormat(total));

    }

}
