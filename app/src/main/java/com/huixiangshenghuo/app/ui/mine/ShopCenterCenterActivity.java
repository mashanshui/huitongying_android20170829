package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homemine.OrderDetailsAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopOrderManagerActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopPicsActivity;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderListRequestParam;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/4.
 */

public class ShopCenterCenterActivity extends BaseActivity implements View.OnClickListener, RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   private TextView tab0;
   private TextView tab1;
   private TextView tab2;
   private TextView tab3;
   private TextView tab5;
   private TextView tab6;
   private Button bt_tj;
   private TextView tv_total_income;
   private TextView tv_income;
   private TextView tv_saleNum;
   private String adContent;
   private String shopId;//商家id


   private LinearLayout ll_income;

   private RefreshLayout refreshLayout;
   private MyListView listview;
   private OrderDetailsAdapter adapter;//适配器
   //数据源
   private ArrayList<GoodsOrderListResponseParam> arrlist = new ArrayList<GoodsOrderListResponseParam>();
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间

   BitmapUtils bitmapUtils;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shopcenter_center);

      initView();
      initBitmapParames();
      initDate();
      request();

   }

   private void initDate() {
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
      adapter = new OrderDetailsAdapter(arrlist, ShopCenterCenterActivity.this, bitmapUtils);
      listview.setAdapter(adapter);
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("商户中心");
//      actionButton.setVisibility(View.VISIBLE);
      actionImageButton.setVisibility(View.VISIBLE);
      actionImageButton.setImageResource(R.mipmap.code_share);
      actionButton.setText("明细"); //暂时不显示
      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ShopCenterCenterActivity.this, IncomeDetailsActivity.class));
         }
      });
      actionImageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ShopCenterCenterActivity.this, ShopShouKuanActivity.class));
         }
      });
      tab0 = (TextView) findViewById(R.id.tab0);
      tab1 = (TextView) findViewById(R.id.tab1);
      tab2 = (TextView) findViewById(R.id.tab2);
      tab3 = (TextView) findViewById(R.id.tab3);
      tab5 = (TextView) findViewById(R.id.tab5);
      tab6 = (TextView) findViewById(R.id.tab6);
      bt_tj = (Button) findViewById(R.id.bt_tianjia);


      listview = (MyListView) findViewById(R.id.lv_shopcenter_center);
      refreshLayout = (RefreshLayout) findViewById(R.id.rl_sx_shopcenter_center);


      //头部
      View headView = View.inflate(ShopCenterCenterActivity.this, R.layout.activity_shopcenter_center_top, null);
      ll_income = (LinearLayout) headView.findViewById(R.id.ll_income);
      tv_total_income = (TextView) headView.findViewById(R.id.tv_shopcenter_total_income);
      tv_income = (TextView) headView.findViewById(R.id.tv_shopcenter_month_income);
      tv_saleNum = (TextView) headView.findViewById(R.id.tv_shopcenter_month_saleNum);
      listview.addHeaderView(headView);


      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      //查看是否是线上线下商家 IsOnline 0 线下 1 线上  商品 分类 订单
//      Toast.makeText(ShopCenterCenterActivity.this,userInfoResponseParam.getIsOnline(),Toast.LENGTH_LONG).show();
      try {
         if (userInfoResponseParam.getShopParam().getIsOnline().equals("0")) {
            tab1.setVisibility(View.GONE);
            tab2.setVisibility(View.GONE);
            tab3.setVisibility(View.GONE);
            bt_tj.setVisibility(View.GONE);

         } else {
            tab1.setVisibility(View.VISIBLE);
            tab2.setVisibility(View.VISIBLE);
            tab3.setVisibility(View.VISIBLE);
            bt_tj.setVisibility(View.VISIBLE);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      tab0.setOnClickListener(this);
      tab1.setOnClickListener(this);
      tab2.setOnClickListener(this);
      tab3.setOnClickListener(this);
      tab5.setOnClickListener(this);
      tab6.setOnClickListener(this);
      bt_tj.setOnClickListener(this);

      ll_income.setOnClickListener(this);

   }

   /**
    * 图片加载
    */
   private void initBitmapParames() {
      bitmapUtils = new BitmapUtils(ShopCenterCenterActivity.this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.tab0://店铺管理
            ShopCenterSubmitActivity.startShopCenterSubmitActivity(ShopCenterCenterActivity.this, ShopCenterSubmitActivity.FLAG_UPDATE);
            break;
         case R.id.tab1://商品管理
            startActivity(new Intent(ShopCenterCenterActivity.this, ShangpinManageActivity.class));
            //           startActivity(new Intent(ShopCenterCenterActivity.this, ShopDetailsOnlineActivity.class));
            break;
         case R.id.tab2://订单管理
            startActivity(new Intent(ShopCenterCenterActivity.this, ShopOrderManagerActivity.class));
            break;
         case R.id.tab3://商品类别
            startActivity(new Intent(ShopCenterCenterActivity.this, ShangpinCategoryActivity.class));
            break;
         case R.id.tab5://公告管理
            //      startActivity(new Intent(ShopCenterCenterActivity.this, NoticeActivity.class));

            Intent intent = new Intent();

            intent.putExtra(NoticeActivity.ADCONTENT, adContent);
            intent.setClass(ShopCenterCenterActivity.this, NoticeActivity.class);
            startActivity(intent);
            break;
         case R.id.tab6://相册管理
            ShopPicsActivity.startShopPicsActivity(ShopCenterCenterActivity.this);
            break;
         case R.id.bt_tianjia://添加商品
            startActivity(new Intent(ShopCenterCenterActivity.this, AddShangpinActivity.class));
            break;
         case R.id.ll_income://收入明细
//            GoldRecordActivity.startGoldRecordActivity(this, "3");//账单类型 0会员积分账单  1会员惠宝账单 2公益资金账单 3商家账单
            break;
      }
   }

   /**
    * 商家详情
    */
   private void request() {
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
      shopInfoRequestParam.setShopId(userInfo.getShopId());
      shopInfoRequestObject.setParam(shopInfoRequestParam);
      httpTool.post(shopInfoRequestObject, URLConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
         @Override
         public void onSuccess(ShopInfoResponseObject o) {
            tv_total_income.setText(o.getShop().getTotalIncome() + "");
            tv_income.setText(o.getShop().getMonthIncome() + "");
            tv_saleNum.setText(o.getShop().getMonthSalenum() + "");
            adContent = o.getShop().getAdContent();
            shopId = o.getShop().getShopId();
            loadData();
         }

         @Override
         public void onError(ShopInfoResponseObject o) {

            Toast.makeText(ShopCenterCenterActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });

   }

   private void loadData() {
//      IntegralrecordListRequestParam integralrecordListRequestParam = new IntegralrecordListRequestParam();
//      integralrecordListRequestParam.setType("3");//账单类型 0会员积分账单  1会员惠宝账单 2公益资金账单 3商家账单
//      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
//
//      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
//      paginationBaseObject.setPage(page);
//      if (page == 1) {
//         paginationBaseObject.setFirstQueryTime("");
//      } else {
//         paginationBaseObject.setFirstQueryTime(firstQueryTime);
//      }
//
//      IntegralrecordListRequestObject integralrecordListRequestObject = new IntegralrecordListRequestObject();
//      integralrecordListRequestObject.setParam(integralrecordListRequestParam);
//      integralrecordListRequestObject.setPagination(paginationBaseObject);
//      httpTool.post(integralrecordListRequestObject, URLConfig.ORDER_LIST, new HttpTool.HttpCallBack<IntegralrecordListResponseObject>() {
//         @Override
//         public void onSuccess(IntegralrecordListResponseObject o) {
//            if (o.getErrorCode().equals("00000")) {
//               if (o.getErrorMsg().equals("success")) {
//                  refreshLayout.setLoading(false);
//                  refreshLayout.setRefreshing(false);
//                  if (o != null && o.getRecordList() != null) {
//                     if (page == 1 && !arrlist.isEmpty()) {
//                        arrlist.clear();
//                     }
//                     firstQueryTime = o.getFirstQueryTime();
//                     arrlist.addAll(o.getRecordList());
//                     adapter.notifyDataSetChanged();
//                  }
//                  if (o.getRecordList().isEmpty()){
//                     listview.setBackgroundResource(R.mipmap.gwc_default);
//                  }else{
//                     listview.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
//                  }
//
//               }
//            }
//
//         }
//
//         @Override
//         public void onError(IntegralrecordListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
//            ToastView.show(o.getErrorMsg());
//         }
//      });


      GoodsOrderListRequestParam goodsOrderListRequestParam = new GoodsOrderListRequestParam();

      goodsOrderListRequestParam.setStatus("4");//订单状态(0待确认1待发货2待使用3已发货4已完成5已取消6已退款)
      goodsOrderListRequestParam.setShopId(shopId);

      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();

      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }

      GoodsOrderListRequestObject goodsOrderListRequestObject = new GoodsOrderListRequestObject();
      goodsOrderListRequestObject.setParam(goodsOrderListRequestParam);
      goodsOrderListRequestObject.setPagination(paginationBaseObject);

      httpTool.post(goodsOrderListRequestObject, URLConfig.MY_ORDER_LIST, new HttpTool.HttpCallBack<GoodsOrderListResponseObject>() {
         @Override
         public void onSuccess(GoodsOrderListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getOrderList() != null) {
                     if (page == 1 && !arrlist.isEmpty()) {
                        arrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getOrderList());
                     adapter.notifyDataSetChanged();
                  }
//                  if (o.getOrderList().size()<=0) {
//                     listview.setBackgroundResource(R.mipmap.gwc_default);
//                  } else {
//                     listview.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.white));
//                  }

               }
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

   @Override
   public void onRefresh() {


      refreshLayout.setRefreshing(true);
      page = 1;
      loadData();
   }

   @Override
   public void onLoad() {

      refreshLayout.setLoading(true);
      page++;
      loadData();
   }
}
