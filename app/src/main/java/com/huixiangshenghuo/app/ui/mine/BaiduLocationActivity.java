package com.huixiangshenghuo.app.ui.mine;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.PermissionTool;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;

import java.util.ArrayList;
import java.util.List;


public class BaiduLocationActivity extends BaseActivity {


   ListView listView;
   private BaiduLocationTool baiduLocationTool;

   private ArrayList<Address> dataList;
   private CustomBaseAdapter<Address> adapter;
   private String address;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_baidu_location);
      initView();
      dataList = new ArrayList<>();
      baiduLocationTool = BaiduLocationTool.newInstance(this);
      adapter = new CustomBaseAdapter<Address>(dataList, R.layout.activity_area_group) {
         @Override
         public void bindView(ViewHolder holder, Address obj) {
            TextView textView = holder.getView(R.id.group_name);
            textView.setText(address + obj.poi.getName());
         }
      };
      listView.setAdapter(adapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String name = dataList.get(position).poi.getName();
            double longitude = dataList.get(position).longitude;
            double latitude = dataList.get(position).latitude;
            Intent intent = new Intent();
            intent.putExtra("data", name);
            intent.putExtra("longitude", longitude + "");
            intent.putExtra("latitude", latitude + "");
            setResult(RESULT_OK, intent);
            finish();
         }
      });
   }

   @Override
   protected void onStart() {
      super.onStart();
      if (Build.VERSION.SDK_INT >= 23) {
         checkPermission();
      } else {
         startLocation();
      }
   }


   @TargetApi(23)
   private void checkPermission() {
      if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //    ToastUtil.show(QianbaihuiApplication.getQianbaihuiApplication(),"请开启定位权限，否则可能无法定位");
         Toast.makeText(BaiduLocationActivity.this, "请开启定位权限，否则可能无法定位", Toast.LENGTH_LONG).show();
         requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
               Manifest.permission.ACCESS_FINE_LOCATION}, PermissionTool.ACCESS_COARSE_LOCATION);
      } else {
         startLocation();
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      switch (requestCode) {
         case PermissionTool.ACCESS_COARSE_LOCATION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startLocation();
            }
            break;
      }
   }

   private void startLocation() {
      showProgressDialog("正在定位..");
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(final BDLocation location) {
            address = location.getAddrStr();
            dataList.clear();
            List<Poi> list = location.getPoiList();
            for (Poi poi : list) {
               Address address = new Address();
               address.poi = poi;
               address.longitude = location.getLongitude();
               address.latitude = location.getLatitude();
               dataList.add(address);
            }
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  dismissProgressDialog();
                  adapter.notifyDataSetChanged();
               }
            });
         }
      });
   }


   public void initView() {
      initTitleBar_1();
      titleView.setText("定位地址");
      listView = (ListView) findViewById(R.id.list_view);


   }

   @Override
   protected void onResume() {
      super.onResume();

   }

   @Override
   protected void onPause() {
      super.onPause();

   }

   @Override
   protected void onStop() {
      super.onStop();
      baiduLocationTool.stopLocation();
   }

   private class Address {
      double longitude;
      double latitude;
      Poi poi;
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
   }
}


