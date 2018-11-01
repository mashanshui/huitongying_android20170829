package com.huixiangshenghuo.app.activity.homemall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homeshoprefresh.ShopDetailsNewActivity;
import com.huixiangshenghuo.app.adapter.homemall.ProductListNewAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.shop.ShopListRequestObject;
import com.doumee.model.request.shop.ShopListRequestParam;
import com.doumee.model.response.shop.ShopListResponseObject;
import com.doumee.model.response.shop.ShopListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/8.
 * 商圈 分类 二级页面 商家分类列表
 */

public class ProductListNewActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   private static String PARENTID = "parentId";
   private static String PARENTNAME = "parentName";
   private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 0x12;
   String parentId;
   String parentName;
   private RadioGroup viewTab;


   private RefreshLayout refresh;
   private ListView listview;
   private ProductListNewAdapter adapter;
   private ArrayList<ShopListResponseParam> arrlist = new ArrayList<ShopListResponseParam>();
   private int page = 1;
   private String firstQueryTime;

   private String sortType = "0";

   private BaiduLocationTool baiduLocationTool;//百度
   private double lat;//纬度
   private double lng;//经度
   private String cityId;
   private String city;


   public static void startProductListNewActivity(Context context, String parentId, String parentName) {
      Intent intent = new Intent(context, ProductListNewActivity.class);
      intent.putExtra(PARENTID, parentId);
      intent.putExtra(PARENTNAME, parentName);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_productlist_new);
      baiduLocationTool = BaiduLocationTool.newInstance(ProductListNewActivity.this);
      parentId = getIntent().getStringExtra(PARENTID);
      parentName = getIntent().getStringExtra(PARENTNAME);
      UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
      cityId = user.getCityId();
      initview();
      initData();
      tj();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText(parentName);
      refresh = (RefreshLayout) findViewById(R.id.refresh);
      listview = (ListView) findViewById(R.id.lv_productlist_new);
      viewTab = (RadioGroup) findViewById(R.id.rg_productlidt_new);

      listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ShopDetailsActivity.startShopDetailsActivity(ProductListNewActivity.this, arrlist.get(position).getShopId());
            ShopDetailsNewActivity.startShopDetailsActivity(ProductListNewActivity.this, arrlist.get(position).getShopId());
         }
      });

      viewTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.rb_sales_volume:
                  sortType = "2";
                  page = 1;
                  request();
                  break;

               case R.id.rb_distance:
                  sortType = "1";
                  page = 1;
                  request();
                  break;

               case R.id.rb_comment:
                  sortType = "4";
                  page = 1;
                  request();
                  break;
               case R.id.rb_shelves:
                  sortType = "0";
                  page = 1;
                  request();
                  break;
            }
         }
      });

   }

   private void initData() {
      refresh.setOnLoadListener(this);
      refresh.setOnRefreshListener(this);
      refresh.setLoading(false);
      refresh.setRefreshing(false);
      adapter = new ProductListNewAdapter(arrlist, ProductListNewActivity.this);
      listview.setAdapter(adapter);
   }

   @Override
   public void onRefresh() {
      refresh.setRefreshing(true);
      page = 1;
      request();
   }

   @Override
   public void onLoad() {
      refresh.setLoading(true);
      page++;
      request();
   }

   private void request() {
      ShopListRequestParam shopListRequestParam = new ShopListRequestParam();
      shopListRequestParam.setSortType(sortType);//排序：0默认排序 1按距离 2按销量 3经常去的 4按评价
      shopListRequestParam.setCityId(cityId);
      shopListRequestParam.setLat(lat);
      shopListRequestParam.setLng(lng);
      shopListRequestParam.setParentCateId(parentId);//parentId 一级分类编码
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }


      ShopListRequestObject shopListRequestObject = new ShopListRequestObject();
      shopListRequestObject.setPagination(paginationBaseObject);
      shopListRequestObject.setParam(shopListRequestParam);

      httpTool.post(shopListRequestObject, URLConfig.SHOP_LIST, new HttpTool.HttpCallBack<ShopListResponseObject>() {
         @Override
         public void onSuccess(ShopListResponseObject o) {
            refresh.setLoading(false);
            refresh.setRefreshing(false);
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  if (o != null && o.getRecordList() != null) {

                     if (page == 1 && !arrlist.isEmpty()) {
                        arrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());
                     adapter.notifyDataSetChanged();
                  }
                  if (o.getRecordList().isEmpty()) {
                     listview.setBackgroundResource(R.mipmap.gwc_default);
                  } else {
                     listview.setBackgroundResource(0);
                  }
               }
            }

         }

         @Override
         public void onError(ShopListResponseObject o) {
            refresh.setLoading(false);
            refresh.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });
   }


   //==========================================================================


   private void tj() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            libCity();

         } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
         }
      } else {
         libCity();
      }
   }


   /**
    * 定位
    */
   private void libCity() {

      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            baiduLocationTool.stopLocation();
            if (null != location) {
               lat = location.getLatitude();
               lng = location.getLongitude();
               city = location.getCity();

               if (city == null) {

                  if (sortType.equals("4")) {
                     ToastView.show("定位失败,请开启定位");
                  } else {
                     request();
                  }
               } else {

                  request();
               }

            } else {

               if (sortType.equals("4")) {
                  ToastView.show("定位失败,请开启定位");
               } else {
                  request();
               }

            }
         }
      });
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            libCity();

         } else {
            ToastView.show("请到设置界面设置定位权限");
         }
      }
   }

   //==========================================================================
}
