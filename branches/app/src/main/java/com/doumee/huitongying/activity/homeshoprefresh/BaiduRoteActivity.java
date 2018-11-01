package com.huixiangshenghuo.app.activity.homeshoprefresh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.ditu.DrivingRouteOverlay;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ToastView;


/**
 * Created by Administrator on 2017/4/6 0006.
 */
public class BaiduRoteActivity extends BaseActivity implements View.OnClickListener {
   private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 0x12;

   public static final String LAN = "lat";
   public static final String LNG = "lng";


   private RoutePlanSearch mSearch;
   MapView mapView;

   private BaiduMap aMap;


   private Button bt_navigation;

   private BaiduLocationTool baiduLocationTool;
   private double lat;//配送员的经纬度
   private double lng;//配送员的经纬度
   private String city;

   private Marker mMarkerC;

   private double customer_lat;//客户经纬度
   private double customer_lng;//客户经纬度

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_baidu_map_rote);

      //==========================================================================
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baiduLocationTool = BaiduLocationTool.newInstance(this);
            initView();
            Bundle bundle = this.getIntent().getExtras();
            customer_lat = bundle.getDouble(LAN, 0.0);
            customer_lng = bundle.getDouble(LNG, 0.0);

            if (aMap == null) {
               aMap = mapView.getMap();
            }
            Position();
         } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
         }
      } else {
         baiduLocationTool = BaiduLocationTool.newInstance(this);
         initView();
         Bundle bundle = this.getIntent().getExtras();
         customer_lat = bundle.getDouble(LAN, 0.0);
         customer_lng = bundle.getDouble(LNG, 0.0);

         if (aMap == null) {
            aMap = mapView.getMap();
         }
         Position();
      }

      //==========================================================================
//        baiduLocationTool = BaiduLocationTool.newInstance(BaiduRoteActivity.this);
//        initView();


//        Bundle bundle = this.getIntent().getExtras();
//        customer_lat = bundle.getDouble(LAN, 0.0);
//        customer_lng = bundle.getDouble(LNG, 0.0);
//
//        if (aMap == null) {
//            aMap = mapView.getMap();
//        }
//        Position();


   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("地图");
      mapView = (MapView) findViewById(R.id.abm_map_view2);

      bt_navigation = (Button) findViewById(R.id.bt_navigation);
      bt_navigation.setOnClickListener(this);
   }

   private void Position() {
      mapView.showZoomControls(false);
      aMap.setMyLocationEnabled(false);
      MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
      aMap.setMapStatus(msu);


      LatLng ll = new LatLng(customer_lat, customer_lng);
      MapStatus.Builder builder = new MapStatus.Builder();
      builder.target(ll).zoom(15.0f);
      aMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//      LatLng point = new LatLng(31.8361840000,117.2624760000);
      BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
      //不带从下到上
//      OverlayOptions option = new MarkerOptions()
//            .position(ll)
//            .icon(bitmap);
//      baiduMap.addOverlay(option);

      //从下到上
      MarkerOptions ooC = new MarkerOptions().position(ll).icon(bitmap);

      // 生长动画
      ooC.animateType(MarkerOptions.MarkerAnimateType.grow);

      mMarkerC = (Marker) (aMap.addOverlay(ooC));
   }

   // private double lat;
//private double lng;
   private void initViewsAndEvents(double Lat, double Lng) {
//        if (aMap == null) {
//            aMap = mapView.getMap();
//        }
      //       showLoading();
      mSearch = RoutePlanSearch.newInstance();
      OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
         public void onGetWalkingRouteResult(WalkingRouteResult result) {
            //获取步行线路规划结果
            Log.e("onGetWalkingRouteResult", "ok");
         }

         public void onGetTransitRouteResult(TransitRouteResult result) {
            //获取公交换乘路径规划结果
            Log.e("onGetTransitRouteResult", "ok");
         }

         @Override
         public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            if (massTransitRouteResult == null || massTransitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
               ToastView.show("抱歉，未找到结果");
            }
            if (massTransitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
               ToastView.show("起始点与终点有歧义");
               return;
            }
            if (massTransitRouteResult.getRouteLines().size() > 0) {
               DrivingRouteOverlay overlay = new DrivingRouteOverlay(aMap);
               aMap.setOnMarkerClickListener(overlay);
//                    overlay.setData(massTransitRouteResult.getRouteLines().get(0));
            }
            Log.e("MassTransit", "ok");
         }

         public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
               Toast.makeText(BaiduRoteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
               // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
               // result.getSuggestAddrInfo()
               return;
            }
            try {
               if (result.getRouteLines().size() >= 1) {
                  DrivingRouteOverlay overlay = new DrivingRouteOverlay(aMap);
                  aMap.setOnMarkerClickListener(overlay);
                  overlay.setData(result.getRouteLines().get(0));
                  overlay.addToMap();
                  overlay.zoomToSpan();
               } else {
                  Log.d("route result", "结果数<0");
                  ToastView.show("没有找到合适的路径");
                  return;
               }
            } catch (Exception e) {
               e.printStackTrace();
//                    ToastView.show("没有找到合适的路径");
            }

         }


         @Override
         public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            Log.e("success", "ok");
         }

         @Override
         public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            Log.e("success", "ok");
         }
      };

      mSearch.setOnGetRoutePlanResultListener(listener);
      PlanNode stNode = PlanNode.withLocation(new LatLng(Lat, Lng));
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("合肥", "合肥南站");
//        PlanNode enNode = PlanNode.withLocation(new LatLng(31.80424,117.296881));
      PlanNode enNode = PlanNode.withLocation(new LatLng(customer_lat, customer_lng));
      mSearch.drivingSearch((new DrivingRoutePlanOption())
            .from(stNode)
            .to(enNode));


      try {
         dismissProgressDialog();
      } catch (Exception e) {
         e.printStackTrace();
      }

      dt();
   }

   private void dt() {
      final Dialog dialog1 = new Dialog(BaiduRoteActivity.this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage("是否使用百度地图");
      dialog1.setConfirmText("确定");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog1.dismiss();
            startRoutePlanDriving();

         }
      });
      dialog1.show();
   }

   /**
    * 启动百度地图驾车路线规划
    */
   public void startRoutePlanDriving() {
      // 百度大厦坐标
//        double mLat2 = 40.056858;
//        double mLon2 = 116.308194;
//        LatLng ptStart = new LatLng(34.264642646862, 108.95108518068);
//        LatLng ptEnd = new LatLng(mLat2, mLon2);
      LatLng ptStart = new LatLng(lat, lng);
      LatLng ptEnd = new LatLng(customer_lat, customer_lng);

      // 构建 route搜索参数
      RouteParaOption para = new RouteParaOption()
            .startPoint(ptStart)
            .endPoint(ptEnd);
//              .startPoint(ptStart)
////            .startName("天安门")
////            .endPoint(ptEnd);
//              .endName("合肥南站")
//              .cityName("合肥");


//        RouteParaOption para = new RouteParaOption()
//                .startName("天安门").endName("百度大厦");

//        RouteParaOption para = new RouteParaOption()
//        .startPoint(pt_start).endPoint(pt_end);

      try {
         //经测试，当手机没有安装百度地图客户端或版本过低时，默认调起百度地图webApp
         BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
      } catch (Exception e) {
         e.printStackTrace();
         showDialog();
      }

   }

   /**
    * 提示未安装百度地图app或app版本过低
    */
   public void showDialog() {
      final Dialog dialog1 = new Dialog(BaiduRoteActivity.this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
      dialog1.setConfirmText("确定");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog1.dismiss();
            OpenClientUtil.getLatestBaiduMapApp(BaiduRoteActivity.this);
         }
      });
      dialog1.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
//        builder.setTitle("提示");
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                OpenClientUtil.getLatestBaiduMapApp(BaiduRoteActivity.this);
//            }
//        });
//
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.create().show();

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.bt_navigation:
            libCity();
//
            break;
      }
   }

   /**
    * 定位
    */
   private void libCity() {
      showProgressDialog("");
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            baiduLocationTool.stopLocation();
            if (null != location) {
               lat = location.getLatitude();
               lng = location.getLongitude();
               city = location.getCity();

               if (city == null) {
                  dismissProgressDialog();
                  ToastView.show("定位失败,请开启定位");
               } else {
                  initViewsAndEvents(lat, lng);
               }

            } else {
               dismissProgressDialog();
               ToastView.show("定位失败,请开启定位");

            }
         }
      });
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            baiduLocationTool = BaiduLocationTool.newInstance(this);
            initView();
            Bundle bundle = this.getIntent().getExtras();
            customer_lat = bundle.getDouble(LAN, 0.0);
            customer_lng = bundle.getDouble(LNG, 0.0);

            if (aMap == null) {
               aMap = mapView.getMap();
            }
            Position();
         } else {
            ToastView.show("请到设置界面设置定位权限");
         }
      }
   }
}
