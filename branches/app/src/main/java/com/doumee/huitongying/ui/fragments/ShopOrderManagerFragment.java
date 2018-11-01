package com.huixiangshenghuo.app.ui.fragments;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.mine.OrderInfoActivity;
import com.huixiangshenghuo.app.view.ImageCompressUtil;
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
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopOrderManagerActivity;
import com.huixiangshenghuo.app.ui.mine.ShopOrderInfoActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;


public class ShopOrderManagerFragment extends Fragment implements RefreshLayout.OnRefreshListener,RefreshLayout.ILoadListener{

    private static final String ARG_PARAM1 = "state";
    private int state;

    private RefreshLayout refreshLayout;
    private ListView listView;
    private int page;
    private String shopId;
    private String queryTime;
    private ArrayList<ShopOrderManager> dataList;
    private CustomBaseAdapter<ShopOrderManager> adapter;
    private Bitmap defaultBitmap;
    private HttpTool httpTool;
    private ShopOrderManager shopOrderManager;

    private BitmapUtils bitmapUtils;

    public ShopOrderManagerFragment() {

    }

    public static ShopOrderManagerFragment newInstance(int state) {
        ShopOrderManagerFragment fragment = new ShopOrderManagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getInt(ARG_PARAM1);
        }
        httpTool = HttpTool.newInstance(getActivity());
        shopId = SaveObjectTool.openUserInfoResponseParam().getShopId();
        defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.business_default);
        initBitmapParames();
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<ShopOrderManager>(dataList,R.layout.fragment_shop_order_manager_item) {
            @Override
            public void bindView(ViewHolder holder,final ShopOrderManager obj) {
                TextView nameView = holder.getView(R.id.name);
                TextView stateView = holder.getView(R.id.state);
                LinearLayout goodsListView =  holder.getView(R.id.goods_list);
                TextView numView = holder.getView(R.id.goods_num);
                TextView totalView = holder.getView(R.id.total);
                RelativeLayout actionBar = holder.getView(R.id.action_bar);
                Button actionButton =  holder.getView(R.id.action);
                nameView.setText(obj.name);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        final Dialog dialog1 = new Dialog(getActivity());
//                        dialog1.setTitle("温馨提示");
//                        dialog1.setMessage("是否确认发货");
//                        dialog1.setConfirmText("确认");
//                        dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                shopOrderManager = obj;
//                                refreshState();
//                            }
//                        });
//                        dialog1.show();
                        express(obj);

                    }
                });
                actionBar.setVisibility(View.GONE);
                stateView.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
                switch (obj.state){
                    case ShopOrderManagerActivity.TAB_1:
                        stateView.setText("待付款");

                        break;
                    case ShopOrderManagerActivity.TAB_2:
                        stateView.setText("待发货");
                        actionBar.setVisibility(View.VISIBLE);
                        break;
                    case ShopOrderManagerActivity.TAB_3:
                        stateView.setText("待收货");
                        break;
                    case ShopOrderManagerActivity.TAB_4:
                        stateView.setText("已完成");
                        stateView.setTextColor(ContextCompat.getColor(getActivity(), R.color.help_button_view));
                        break;
                    case 5:
                        stateView.setText("已取消");
                        stateView.setTextColor(ContextCompat.getColor(getActivity(),R.color.help_button_view));
                        break;
                }
                initAdapterItem(obj.lists,totalView,numView,goodsListView);
            }
        };
    }

    private void initAdapterItem(List<ShopOrderList> lists,TextView totalView, TextView totalNumView, LinearLayout goodsListView){

        double total = 0d;
        int num = 0;
        goodsListView.removeAllViews();
        for (ShopOrderList shopOrderList : lists){
           View view = View.inflate(getActivity(),R.layout.fragment_shop_order_manager_item_2,null);
            ImageView imageView = (ImageView)view.findViewById(R.id.goods_image);
            TextView nameView = (TextView)view.findViewById(R.id.goods_name);
            TextView priceView = (TextView)view.findViewById(R.id.goods_price);
            TextView dateView = (TextView)view.findViewById(R.id.date);
            TextView numView = (TextView)view.findViewById(R.id.goods_nn);
            LinearLayout ll_zong = (LinearLayout) view.findViewById(R.id.ll_shop_order_manager_item_2);
            total += shopOrderList.price * shopOrderList.num;
            num += shopOrderList.num;
            //改为 bitmapUtils 加载
//            imageView.setImageBitmap(defaultBitmap);
//            if (!TextUtils.isEmpty(shopOrderList.path)){
//                ImageLoader.getInstance().displayImage(shopOrderList.path,imageView);
//            }
//            bitmapUtils.display(imageView, shopOrderList.path);
            ImageCompressUtil.setImage(imageView, shopOrderList.path, getActivity());
            nameView.setText(shopOrderList.goodsName);
            priceView.setText(CustomConfig.RMB+ NumberFormatTool.numberFormat(shopOrderList.price));
            dateView.setText(shopOrderList.date);
            numView.setText("×"+shopOrderList.num);
           goodsListView.addView(view);
        }
        totalNumView.setText("共"+num+"件商品");
        totalView.setText("总价："+CustomConfig.RMB+NumberFormatTool.numberFormat(total));
    }

    /**
     * 图片加载
     */
    public void initBitmapParames() {
        bitmapUtils = new BitmapUtils(getActivity());
        // 设置加载失败图片
        bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
        // 设置没有加载成功图片
        bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_order_manager, container, false);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refresh);
        listView = (ListView)view.findViewById(R.id.list_view);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(true);

        onRefresh();
        initView();
        return view;
    }

    private void initView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //订单类型 0到店消费订单 1 普通商品订单
                //dataList.get(position).Type()
                //订单类别 0  我的订单 1 用来判断是否显示 评论按钮
                if (dataList.get(position).Type.equals("0")) {
                    OrderInfoActivity.startOrderInfoActivity(getActivity(), dataList.get(position).orderId, dataList.get(position).Type, "0");
                } else {
                    ShopOrderInfoActivity.startOrderInfoActivity(getActivity(), dataList.get(position).orderId, dataList.get(position).Type);
                }

            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        queryTime = "";
        dataList.clear();
        loadData();
    }

    @Override
    public void onLoad() {
        page++;
        loadData();
    }

    //======================================================
    //快递信息
    AlertDialog alertDialog;

    private void express(final ShopOrderManager obj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.fragment_shop_order_manager_express, null);
        final EditText et_kd = (EditText) view.findViewById(R.id.et_shop_order_manager_express_kd);
        final EditText et_dh = (EditText) view.findViewById(R.id.et_shop_order_manager_express_dh);
        Button bt_qx = (Button) view.findViewById(R.id.bt_qx);
        Button bt_qr = (Button) view.findViewById(R.id.bt_qr);


        bt_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_kd.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "请设置快递名称", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(et_dh.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "请设置快递单号", Toast.LENGTH_LONG).show();
                    return;
                }
                shopOrderManager = obj;
                refreshState(et_kd.getText().toString().trim(), et_dh.getText().toString().trim());

                alertDialog.dismiss();

            }
        });
        bt_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void refreshState(String kdName, String kdCode) {
        if (null != shopOrderManager){
            GoodsOrderEditRequestParam goodsOrderEditRequestParam = new GoodsOrderEditRequestParam();
            goodsOrderEditRequestParam.setOrderId(Long.parseLong(shopOrderManager.orderId));
            goodsOrderEditRequestParam.setStatus(ShopOrderManagerActivity.TAB_3+"");
            if (goodsOrderEditRequestParam.getStatus().equals(ShopOrderManagerActivity.TAB_3 + "")) {
                goodsOrderEditRequestParam.setKdName(kdName);
                goodsOrderEditRequestParam.setKdCode(kdCode);
            }
            GoodsOrderEditRequestObject goodsOrderEditRequestObject = new GoodsOrderEditRequestObject();
            goodsOrderEditRequestObject.setParam(goodsOrderEditRequestParam);
            httpTool.post(goodsOrderEditRequestObject, URLConfig.ORDER_UPDATE, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                @Override
                public void onSuccess(ResponseBaseObject o) {
                    shopOrderManager.state = ShopOrderManagerActivity.TAB_3;
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onError(ResponseBaseObject o) {
                    ToastView.show(o.getErrorMsg());
                }
            });
        }
    }


    //=======================================================

//    private void refreshState(){
//        if (null != shopOrderManager){
//            GoodsOrderEditRequestParam goodsOrderEditRequestParam = new GoodsOrderEditRequestParam();
//            goodsOrderEditRequestParam.setOrderId(Long.parseLong(shopOrderManager.orderId));
//            goodsOrderEditRequestParam.setStatus(ShopOrderManagerActivity.TAB_3+"");
//            GoodsOrderEditRequestObject goodsOrderEditRequestObject = new GoodsOrderEditRequestObject();
//            goodsOrderEditRequestObject.setParam(goodsOrderEditRequestParam);
//            httpTool.post(goodsOrderEditRequestObject, URLConfig.ORDER_UPDATE, new HttpTool.HttpCallBack<ResponseBaseObject>() {
//                @Override
//                public void onSuccess(ResponseBaseObject o) {
//                    shopOrderManager.state = ShopOrderManagerActivity.TAB_3;
//                    adapter.notifyDataSetChanged();
//                }
//                @Override
//                public void onError(ResponseBaseObject o) {
//                    ToastView.show(o.getErrorMsg());
//                }
//            });
//        }
//    }

    private void loadData(){
        GoodsOrderListRequestParam goodsOrderListRequestParam = new GoodsOrderListRequestParam();
        if (state != ShopOrderManagerActivity.TAB_0){
            goodsOrderListRequestParam.setStatus(state+"");
        }
        goodsOrderListRequestParam.setShopId(shopId);
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(queryTime);
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
                queryTime = o.getFirstQueryTime();
                List<GoodsOrderListResponseParam> orderList = o.getOrderList();
                for (GoodsOrderListResponseParam goodsOrderListResponseParam : orderList){
                    ShopOrderManager shopOrderManager = new ShopOrderManager();
                    shopOrderManager.state = Integer.parseInt(goodsOrderListResponseParam.getStatus()) ;
                    shopOrderManager.name = goodsOrderListResponseParam.getMemberName();
                    shopOrderManager.orderId = goodsOrderListResponseParam.getOrderId();
                    shopOrderManager.Type = goodsOrderListResponseParam.getType();
                    List<ShopOrderList> lists = new ArrayList<>();
                    List<GoodsDetailsResponeParam> goodsList = goodsOrderListResponseParam.getGoodsList();
                    for (GoodsDetailsResponeParam goods :goodsList){
                        ShopOrderList shopOrderList = new ShopOrderList();
                        shopOrderList.date = goodsOrderListResponseParam.getCreateDate();
                        shopOrderList.goodsName = goods.getProName();
                        shopOrderList.price = goods.getPrice();
                        shopOrderList.num = goods.getNum();
                        shopOrderList.path = goods.getProImg();
                        lists.add(shopOrderList);
                    }
                    shopOrderManager.lists = lists;
                    dataList.add(shopOrderManager);
                }
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

    private class ShopOrderManager{
        String orderId;
        String name;
        int state;
        String Type;
        List<ShopOrderList> lists;


    }
    private class ShopOrderList{
        String path;
        String goodsName;
        String date;
        int num;
        double price;
    }
}
