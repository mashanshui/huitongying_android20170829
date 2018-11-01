package com.huixiangshenghuo.app.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter2;
import com.huixiangshenghuo.app.comm.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31.
 */

public class AutoChooseAddressActivity extends BaseActivity implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMapTouchListener {
   private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 0x12;

   private ListView addressList;
   private EditText searchEdit;
   private RelativeLayout clearSearchLyt;
   private MapView mapView;

   private double latitude, longitude;
   private GeoCoder geoCoder;

   private PoiSearch mPoiSearch;
   private String cityTxt;

   //位置数据
   private List<PoiInfo> locationData;
   //搜索位置数据
   private List<PoiInfo> searchData;

   private BaiduMap aMap;

   private CustomBaseAdapter2<PoiInfo> locationAdapter;

   private BaiduLocationTool baiduLocationTool;

   private Handler mHandler = new Handler();
   private Runnable locRunnable = new Runnable() {
      @Override
      public void run() {
         searchVenue();
      }
   };

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_choose_location);
      initTitleBar_1();
      titleView.setText("选择地址");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baiduLocationTool = BaiduLocationTool.newInstance(this);
            findView();
            initData();
         } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
         }
      } else {
         baiduLocationTool = BaiduLocationTool.newInstance(this);
         findView();
         initData();
      }
   }

   private void findView() {
      addressList = (ListView) findViewById(R.id.acl_address_list);
      searchEdit = (EditText) findViewById(R.id.acl_search_edit);
      clearSearchLyt = (RelativeLayout) findViewById(R.id.acl_clear_search_lyt);
      mapView = (MapView) findViewById(R.id.acl_map_view);

   }

   private void initMap() {
      if (aMap == null) {
         aMap = mapView.getMap();
      }
      showView();
      aMap.setOnMapStatusChangeListener(this);// 对amap添加移动地图事件监听器
      aMap.setOnMapTouchListener(this);
   }


   //显示地图位置
   private void showView() {
      LatLng p = new LatLng(latitude, longitude);
      //绘制marker
      MapStatus mMapStatus = new MapStatus.Builder()
            .target(p)
            .zoom(18)
            .build();
      //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
      MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
      //改变地图状态
      aMap.setMapStatus(mMapStatusUpdate);
   }

   private void initData() {
      locationData = new ArrayList<>();
      locationAdapter = new CustomBaseAdapter2<PoiInfo>(locationData, R.layout.item_location) {

         @Override
         public void bindView(ViewHolder holder, PoiInfo obj, int position) {
            TextView nameTxt = holder.getView(R.id.il_name_txt);
            TextView detailsTxt = holder.getView(R.id.il_detail_txt);
            nameTxt.setText(obj.name != null ? obj.name : "暂无信息");
            detailsTxt.setText(obj.address != null ? obj.address : "暂无信息");
         }
      };
      addressList.setAdapter(locationAdapter);
      addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            isTouch = false;
            Intent intent = new Intent();
//            intent.putExtra("address",locationAdapter.getmData().get(position).address+locationAdapter.getmData().get(position).name);
            intent.putExtra("data", locationAdapter.getmData().get(position).address + locationAdapter.getmData().get(position).name);
            intent.putExtra("location", locationAdapter.getmData().get(position).location);
            setResult(RESULT_OK, intent);
            finish();
         }
      });
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude();
            //纬度
            latitude = location.getLatitude();
            cityTxt = location.getCity();
            if (!StringUtils.isEmpty(cityTxt)) {
               initListener();
               initGeoCoder();
               initMap();
               geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude)));
            }
         }
      });
   }

   //初始化反地理编码
   private void initGeoCoder() {
      geoCoder = GeoCoder.newInstance();
      OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
         // 反地理编码查询结果回调函数
         @Override
         public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
               // 没有检测到结果
               return;
            }
            if (result.getPoiList() != null && result.getPoiList().size() > 0) {
               locationData = result.getPoiList();
               locationAdapter.getmData().clear();
               locationAdapter.getmData().addAll(locationData);
               locationAdapter.notifyDataSetChanged();
            }

         }

         // 地理编码查询结果回调函数
         @Override
         public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
               // 没有检测到结果
               return;
            }


         }
      };
      // 设置地理编码检索监听者
      geoCoder.setOnGetGeoCodeResultListener(listener);
   }


   private void initListener() {
      clearSearchLyt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            searchEdit.setText("");
         }
      });

      searchEdit.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            String key = searchEdit.getText().toString().trim();
            if (StringUtils.isEmpty(key)) {
               clearSearchLyt.setVisibility(View.GONE);
               locationAdapter.setmData(locationData);
               locationAdapter.notifyDataSetChanged();
            } else {
               searchData = new ArrayList<>();
               locationAdapter.setmData(searchData);
               locationAdapter.notifyDataSetChanged();
               clearSearchLyt.setVisibility(View.VISIBLE);
               if (mPoiSearch == null) {
                  mPoiSearch = PoiSearch.newInstance();
                  OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
                     @Override
                     public void onGetPoiResult(PoiResult poiResult) {
                        if (StringUtils.isEmpty(searchEdit.getText().toString().trim())) {
                           clearSearchLyt.setVisibility(View.GONE);
                           locationAdapter.setmData(locationData);
                           locationAdapter.notifyDataSetChanged();
                           return;
                        }
                        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                           // 没有检测到结果
                           return;
                        }
                        if (poiResult.getAllPoi() != null) {
                           searchData = poiResult.getAllPoi();
                        }
                        locationAdapter.setmData(searchData);
                        locationAdapter.notifyDataSetChanged();
                     }

                     @Override
                     public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                     }

                     @Override
                     public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                     }
                  };
                  mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
               }

               mPoiSearch.searchInCity((new PoiCitySearchOption()).city(cityTxt).keyword(key));
            }
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
      });
   }


   @Override
   public void onMapStatusChangeStart(MapStatus mapStatus) {

   }

   @Override
   public void onMapStatusChange(MapStatus mapStatus) {

   }

   @Override
   public void onMapStatusChangeFinish(MapStatus mapStatus) {
      if (isTouch) {
         Log.e("touch", "拖动地图");
         LatLng latLng = mapStatus.target;
         latitude = latLng.latitude;
         longitude = latLng.longitude;
         getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
               mHandler.post(locRunnable);
            }
         });
      }
   }

   private boolean isTouch = false;

   @Override
   public void onTouch(MotionEvent motionEvent) {
      isTouch = true;
   }

   private void searchVenue() {
      Log.e("search", "查询信息");
      geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude)));
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (mapView != null) {
         mapView.onResume();
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            baiduLocationTool = BaiduLocationTool.newInstance(this);
            findView();
            initData();
         } else {
            ToastView.show("请到设置界面设置定位权限");
         }
      }
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (mapView != null) {
         mapView.onPause();
      }
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (mapView != null) {
         mapView.onSaveInstanceState(outState);
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      if (mapView != null) {
         try {
            mapView.onDestroy();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (geoCoder != null) {
         geoCoder.destroy();
      }
   }
}
