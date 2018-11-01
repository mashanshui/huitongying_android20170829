package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.NearbyShopAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.EditTextWithDel;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.shop.ShopListRequestObject;
import com.doumee.model.request.shop.ShopListRequestParam;
import com.doumee.model.response.shop.ShopListResponseObject;
import com.doumee.model.response.shop.ShopListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;


/**
 * Created by wl on 2016/10/28.
 * 搜索商品
 */

public class SearchProductActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {

   EditTextWithDel searchBar;
   RefreshLayout refreshLayout;
   MyListView gridView;
   LinearLayout linearLayout;
   ListView listView;
   Button cancelButton;
   Button clearButton;
   HttpTool httpTool;

   private int page = 1;
   private String firstQueryTime = "";
   private Bitmap defaultBitmap;
   private String name = "";


   private ArrayList<ShopListResponseParam> arrlist = new ArrayList<ShopListResponseParam>();
   private NearbyShopAdapter mAdapter;
   BaiduLocationTool baiduLocationTool;
   private double lat;
   private double lng;
   private String shopName;
   private String cateId = "";
   private String parentCateId = "";
   private String sortType = "0";
   private String cityId = "";
   private CustomBaseAdapter<String> adapter;
   private int flag;

   public static void startSearchProductActivity(Context context,int flag) {
      Intent intent = new Intent(context, SearchProductActivity.class);
      intent.putExtra("flag",flag);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_searchproduct);
      httpTool = HttpTool.newInstance(this);
      flag = getIntent().getIntExtra("flag",0);
      baiduLocationTool = BaiduLocationTool.newInstance(this);
      //    initTitleBar_1();
      UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
      cityId = user.getCityId();
      initView();
      refresh();
      initListener();

      page = 1;
      firstQueryTime = "";
      dw();
   }


   public void initView() {


      searchBar = (EditTextWithDel) findViewById(R.id.edt_product_search);
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
      gridView = (MyListView) findViewById(R.id.lv_searchprodut);
      linearLayout = (LinearLayout) findViewById(R.id.last_record);
      listView = (ListView) findViewById(R.id.list_view);
      cancelButton = (Button) findViewById(R.id.cancel_button);

      View footView = View.inflate(SearchProductActivity.this, R.layout.activity_searchproduct_foot, null);
      clearButton = (Button) footView.findViewById(R.id.clear_button);
      listView.addFooterView(footView);
   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      mAdapter = new NearbyShopAdapter(arrlist, SearchProductActivity.this);
      gridView.setAdapter(mAdapter);
   }

   private void initListener() {

      searchBar.setOnChangeListener(new EditTextWithDel.OnChangeListener() {
         @Override
         public void afterTextChanged(Editable s) {
            name = s.toString();
            onRefresh();
            if (!TextUtils.isEmpty(name)) {
               linearLayout.setVisibility(View.GONE);

            } else {
               linearLayout.setVisibility(View.GONE);
            }
         }
      });
      adapter = new CustomBaseAdapter<String>(CustomApplication.getCustomApplication().getSearchList(), R.layout.activity_searchproduct_item) {
         @Override
         public void bindView(ViewHolder holder, String obj) {
            TextView textView = holder.getView(R.id.search_name);
            textView.setText(obj);
         }
      };
      listView.setAdapter(adapter);
      clearButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            CustomApplication.getCustomApplication().getSearchList().clear();
            adapter.notifyDataSetChanged();
            linearLayout.setVisibility(View.GONE);
         }
      });
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            name = CustomApplication.getCustomApplication().getSearchList().get(position);
            searchBar.setText(name);
         }
      });
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
         }
      });

   }



   @Override
   public void onRefresh() {
      page = 1;
      firstQueryTime = "";
      refreshLayout.setRefreshing(true);
      dw();
   }

   @Override
   public void onLoad() {
      page++;
      refreshLayout.setLoading(true);
      loadShopData();
   }


   private void loadShopData() {
      ShopListRequestParam shopListRequestParam = new ShopListRequestParam();
      shopListRequestParam.setName(name);
      shopListRequestParam.setSortType(sortType);//排序：0默认排序 1按距离 2按销量 3经常去的 4按评价
//      shopListRequestParam.setCityId(cityId);
      shopListRequestParam.setLat(lat);
      shopListRequestParam.setLng(lng);
      shopListRequestParam.setCateId(cateId);
      shopListRequestParam.setParentCateId(parentCateId);
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(10);
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
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (page == 1 && !arrlist.isEmpty()) {
                     //清空
                     arrlist.clear();
                  }
                  //            lastrefereeid=response.getLastRefereeId();
                  firstQueryTime = o.getFirstQueryTime();

                  arrlist.addAll(o.getRecordList());

                  mAdapter.notifyDataSetChanged();

                  if (arrlist.isEmpty()){
                     listView.setBackgroundResource(R.mipmap.gwc_default);
                  }else{
                     listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                  }
                  if (!o.getRecordList().isEmpty() && !TextUtils.isEmpty(name)) {
                     CustomApplication.getCustomApplication().getSearchList().add(name);
                     adapter.notifyDataSetChanged();
                  }
               }
            }
         }

         @Override
         public void onError(ShopListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            Toast.makeText(SearchProductActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   @Override
   public void onStart() {
      super.onStart();
      if (flag == 1){
         sortType = "1";
         onRefresh();
      }
   }

   @Override
   public void onStop() {
      super.onStop();
      if (null != baiduLocationTool) {
         baiduLocationTool.stopLocation();
      }
   }

   /**
    * 定位
    */
   private void dw() {
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            baiduLocationTool.stopLocation();
            if (null != location) {
               lat = location.getLatitude();
               lng = location.getLongitude();
            }
            loadShopData();
         }
      });
   }

}
