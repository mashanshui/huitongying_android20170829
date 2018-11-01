package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestParam;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ShopCenterActivity extends BaseActivity implements RefreshLayout.ILoadListener,RefreshLayout.OnRefreshListener{


    private TextView orderNumView;
    private RefreshLayout refreshLayout;
    private ListView listView;

    private int page = 1;
    private ArrayList<GoodsOrderListResponseParam> dataList;
    private CustomBaseAdapter<GoodsOrderListResponseParam> adapter;
    private String firstQueryTime;
    private String shopId;
    private Bitmap defaultBitmap;
    public static void startShopCenterActivity(Context context){
        Intent intent = new Intent(context,ShopCenterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_center);
        initAdapter();
        initView();
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        if (null != user){
            shopId = user.getShopId();
        }
        defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.business_default);
    }
    private void initView(){
        initTitleBar_1();
        titleView.setText("商户中心");
        orderNumView = (TextView)findViewById(R.id.order_num);
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
        actionButton.setText("修改");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopCenterSubmitActivity.startShopCenterSubmitActivity(ShopCenterActivity.this,ShopCenterSubmitActivity.FLAG_UPDATE);
            }
        });
        actionButton.setVisibility(View.GONE);
        actionImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.shop));
        actionImageButton.setVisibility(View.VISIBLE);
        actionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopCenterSubmitActivity.startShopCenterSubmitActivity(ShopCenterActivity.this,ShopCenterSubmitActivity.FLAG_UPDATE);
            }
        });
    }

    private void initAdapter(){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<GoodsOrderListResponseParam>(dataList,R.layout.activity_shop_center_item) {
            @Override
            public void bindView(ViewHolder holder, GoodsOrderListResponseParam obj) {
                ImageView imageView = holder.getView(R.id.image);
                TextView nameView = holder.getView(R.id.name);
                TextView priceView = holder.getView(R.id.price);
                TextView dateView = holder.getView(R.id.date);
                nameView.setText("订单编号："+obj.getOrderId());
                priceView.setText(CustomConfig.RMB+ NumberFormatTool.numberFormat(obj.getTotalPrice()));
                dateView.setText(obj.getCreateDate());
                String path = obj.getImgurl();
                if (!TextUtils.isEmpty(path)){
                    ImageLoader.getInstance().displayImage(path,imageView);
                }else{
                    imageView.setImageBitmap(defaultBitmap);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        refreshLayout.setRefreshing(true);
        dataList.clear();
        firstQueryTime = "";
        loadData();
    }

    @Override
    public void onLoad() {
        refreshLayout.setLoading(true);
        page++;
        loadData();
    }


    private void loadData(){
        GoodsOrderListRequestParam goodsOrderListRequestParam = new GoodsOrderListRequestParam();
        goodsOrderListRequestParam.setShopId(shopId);
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(firstQueryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);

        GoodsOrderListRequestObject goodsOrderListRequestObject = new GoodsOrderListRequestObject();
        goodsOrderListRequestObject.setParam(goodsOrderListRequestParam);
        goodsOrderListRequestObject.setPagination(paginationBaseObject);

        //showProgressDialog("正在加载..");
        httpTool.post(goodsOrderListRequestObject, URLConfig.MY_ORDER_LIST, new HttpTool.HttpCallBack<GoodsOrderListResponseObject>() {
            @Override
            public void onSuccess(GoodsOrderListResponseObject o) {
               // dismissProgressDialog();
                orderNumView.setText(o.getTotalCount()+"");
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                firstQueryTime = o.getFirstQueryTime();
                List<GoodsOrderListResponseParam> orderList = o.getOrderList();
                dataList.addAll(orderList);
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                }
            }
            @Override
            public void onError(GoodsOrderListResponseObject o) {
               // dismissProgressDialog();
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }

}
