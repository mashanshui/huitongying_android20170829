package com.huixiangshenghuo.app.comm.baidu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
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
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter2;
import com.huixiangshenghuo.app.comm.app.PermissionTool;
import com.huixiangshenghuo.app.comm.utils.StringUtils;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.EditTextWithDel;
import com.huixiangshenghuo.app.view.ToastView;

import java.util.ArrayList;
import java.util.List;

public class BaiduPoiSearchActivity extends BaseActivity {

    EditTextWithDel searchBar;
    ListView listView;
    MapView mapView;
    TextView cityView;

    private PoiSearch poiSearch;
//    private ArrayList<PoiInfo> poiAddrInfos;
//    private CustomBaseAdapter<PoiInfo> adapter;

   private List<PoiInfo> poiAddrInfos;
   private CustomBaseAdapter2<PoiInfo> adapter;


    private BaiduMap baiduMap;
    private BaiduLocationTool baiduLocationTool;
    private String city = "";

    public static final int FLAG_SHOW_ADDRESS = 0;
    public static final int FLAG_SEARCH_ADDRESS = 1;
    private int flag;
    private Double longitude;
    private Double latitude;

    public static void startBaiduPoiSearchActivity(Context context ,Double longitude,Double latitude,int flag){
        Intent intent = new Intent(context,BaiduPoiSearchActivity.class);
        intent.putExtra("longitude",longitude);
        intent.putExtra("latitude",latitude);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_poi_search);
        longitude = getIntent().getDoubleExtra("longitude",0);
        latitude = getIntent().getDoubleExtra("latitude",0);
        flag = getIntent().getIntExtra("flag",FLAG_SEARCH_ADDRESS);
        initView();
        baiduLocationTool = BaiduLocationTool.newInstance(this);
        baiduMap = mapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        baiduMap.setMapStatus(msu);
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        poiAddrInfos = new ArrayList<>();
//        adapter = new CustomBaseAdapter<PoiInfo>(poiAddrInfos, R.layout.activity_baidu_poi_search_item) {
//            @Override
//            public void bindView(ViewHolder holder, PoiInfo obj) {
//                TextView textView = holder.getView(R.id.name);
//                TextView addressView = holder.getView(R.id.address);
//                textView.setText(obj.name);
//                addressView.setText("地址:"+obj.address);
//            }
//        };
       //===============================================================
       adapter = new CustomBaseAdapter2<PoiInfo>(poiAddrInfos, R.layout.activity_baidu_poi_search_item) {
            @Override
            public void bindView(ViewHolder holder, PoiInfo obj, int position) {
                TextView textView = holder.getView(R.id.name);
                TextView addressView = holder.getView(R.id.address);
                textView.setText(obj.name);
                addressView.setText("地址:"+obj.address);
            }

        };


       //======================================================================
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = poiAddrInfos.get(position);
                Intent intent = new Intent();
                intent.putExtra("data",poiInfo.address);
                intent.putExtra("longitude", poiInfo.location.longitude + "");
                intent.putExtra("latitude", poiInfo.location.latitude + "");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        searchBar.setOnChangeListener(new EditTextWithDel.OnChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s))
                    searchAddress(s.toString());
            }
        });
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("搜索商户");
        searchBar = (EditTextWithDel)findViewById(R.id.search_bar);
        listView = (ListView)findViewById(R.id.list_view);
        mapView = (MapView)findViewById(R.id.bmapView);
        cityView = (TextView)findViewById(R.id.label_1);
        if (flag == FLAG_SHOW_ADDRESS){
            searchBar.setVisibility(View.GONE);
            titleView.setText("商户地址");
            listView.setVisibility(View.GONE);
            cityView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mapView.setLayoutParams(layoutParams);
        }
    }


    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            poiAddrInfos.clear();
            List<PoiInfo> poiInfos = result.getAllPoi();
            if (null != poiInfos){
                poiAddrInfos.addAll( poiInfos) ;
            }
            adapter.notifyDataSetChanged();
            baiduMap.clear();
            for (int i = 0 ; i < poiAddrInfos.size() ; i++){
                PoiInfo poiInfo =  poiAddrInfos.get(i);
                addOverlay(poiInfo);
                if ( i == 0){
                    location(poiInfo);
                }
            }
        }
        public void onGetPoiDetailResult(PoiDetailResult result){

            //获取Place详情页检索结果
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };


    private void addOverlay(PoiInfo poiInfo){
        if (null !=poiInfo && null != poiInfo.location){
            LatLng point = new LatLng(poiInfo.location.latitude, poiInfo.location.longitude);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(9).draggable(true);;
            baiduMap.addOverlay(option);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (flag == FLAG_SHOW_ADDRESS){
            LatLng point = new LatLng(latitude ,longitude );
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(9).draggable(true);;
            baiduMap.addOverlay(option);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(point).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            return;
        }

        if (Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }else{
            loc();
        }
    }

    private void loc(){
        baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (null == location){
                    return;
                }
                city = location.getCity();
                cityView.setText("当前定位城市："+city);
                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                        .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                baiduMap.setMyLocationData(locData);
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(15.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

               //=================================================================
               longitude2 = location.getLongitude();
               //纬度
               latitude2 = location.getLatitude();
               if (!StringUtils.isEmpty(city)) {

                  initGeoCoder();

                  geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude2, longitude2)));
               }

               //=================================================================

            }
        });
    }


   private Double longitude2;
   private Double latitude2;
   private GeoCoder geoCoder;

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
               poiAddrInfos = result.getPoiList();
               adapter.getmData().clear();
               adapter.getmData().addAll(poiAddrInfos);
               adapter.notifyDataSetChanged();
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






    @TargetApi(23)
    private void checkPermission(){
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ToastView.show("请开启定位权限，否则可能无法定位");
            requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION ,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PermissionTool.ACCESS_COARSE_LOCATION);
        }else{
            loc();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PermissionTool.ACCESS_COARSE_LOCATION :
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loc();
                }
                break;
        }
    }

    private void searchAddress(String address){
        poiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(address)
                .pageCapacity(50)
                .pageNum(0));
    }

    private void location(PoiInfo poiInfo){
        LatLng latLng = poiInfo.location ;
        if (null != latLng){
            MyLocationData locData = new MyLocationData.Builder()
                    .direction(100).latitude(poiInfo.location.latitude).longitude(poiInfo.location.longitude).build();
            baiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(poiInfo.location.latitude,
                    poiInfo.location.longitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(15.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        baiduLocationTool.stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        poiSearch.destroy();
        mapView.onDestroy();

       //========================================
       if (geoCoder != null) {
          geoCoder.destroy();
       }

       //========================================

    }
}
