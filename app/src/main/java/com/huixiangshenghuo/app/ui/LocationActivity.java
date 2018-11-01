package com.huixiangshenghuo.app.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.PermissionTool;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.fragments.HomeShopRefreshNewFragment;
import com.huixiangshenghuo.app.view.EditTextWithDel;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.response.userinfo.CityResponseParam;
import com.doumee.model.response.userinfo.ProvinceResponseObject;
import com.doumee.model.response.userinfo.ProvinceResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wl on 2016/10/28.
 * 74 行暂时屏蔽
 */

public class LocationActivity extends BaseActivity {


   EditTextWithDel searchBar;

   TextView cityName;

   ListView listView;

   HttpTool httpTool;
   private ArrayList<CityResponseParam> arrayList;
   private CustomBaseAdapter<CityResponseParam> adapter;
   private BaiduLocationTool baiduLocationTool;

   private SharedPreferences sharedPreferences;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      httpTool = HttpTool.newInstance(this);
      setContentView(R.layout.activity_location_list);
      sharedPreferences = CustomApplication.getAppUserSharedPreferencesCity();

      initTitleBar_1();
      initView();
      arrayList = new ArrayList<>();
      baiduLocationTool = BaiduLocationTool.newInstance(this);
      adapter = new CustomBaseAdapter<CityResponseParam>(arrayList, R.layout.activity_location_list_item) {
         @Override
         public void bindView(ViewHolder holder, final CityResponseParam obj) {

            TextView textView = holder.getView(R.id.city);
            textView.setText(obj.getCityName());
            textView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  String cityId = obj.getCityId();
                  String cityName = obj.getCityName();
                  UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                  if (null == user) {
                     user = new UserInfoResponseParam();
                  }
                  user.setCityId(cityId);
                  user.setCityName(cityName);
                  SaveObjectTool.saveObject(user);

                  sharedPreferences.edit().putString(CustomConfig.CITY_ID, cityId).commit();
                  sharedPreferences.edit().putString(CustomConfig.CITY_NAME, cityName).commit();

                  Intent intent = new Intent();
//                  intent.setAction(HomeShopFragment.REFRESH_CITY);
                  intent.setAction(HomeShopRefreshNewFragment.REFRESH_CITY);
                  sendBroadcast(intent);
                  finish();
               }
            });
         }
      };
      listView.setAdapter(adapter);
   }


   public void initView() {
      titleView.setText("选择城市");
      searchBar = (EditTextWithDel) findViewById(R.id.search_bar);
      cityName = (TextView) findViewById(R.id.city);
      listView = (ListView) findViewById(R.id.list_view);
      searchBar.setOnChangeListener(new EditTextWithDel.OnChangeListener() {
         @Override
         public void afterTextChanged(Editable s) {
            searchCity(s.toString());
         }
      });

      cityName.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String city = cityName.getText().toString();
            for (CityResponseParam obj : arrayList){
               if (obj.getCityName().contains(city)){
                  String cityId = obj.getCityId();
                  String cityName = obj.getCityName();
                  UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                  if (null == user) {
                     user = new UserInfoResponseParam();
                  }
                  user.setCityId(cityId);
                  user.setCityName(cityName);
                  SaveObjectTool.saveObject(user);
                  sharedPreferences.edit().putString(CustomConfig.CITY_ID, cityId).commit();
                  sharedPreferences.edit().putString(CustomConfig.CITY_NAME, cityName).commit();

                  Intent intent = new Intent();
//                  intent.setAction(HomeShopFragment.REFRESH_CITY);
                  intent.setAction(HomeShopRefreshNewFragment.REFRESH_CITY);
                  sendBroadcast(intent);
                  finish();

               }
            }
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
      loadCityData();
   }

   @TargetApi(23)
   private void checkPermission() {
      if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         ToastView.show("请开启定位权限，否则可能无法定位");
         requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
               Manifest.permission.ACCESS_FINE_LOCATION}, PermissionTool.ACCESS_COARSE_LOCATION);
      } else {
         startLocation();
      }
   }

   private void startLocation() {
      showProgressDialog("正在定位..");
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            final String city = location.getCity();
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  dismissProgressDialog();
                  cityName.setText(city);
               }
            });
         }
      });
   }

   @Override
   protected void onStop() {
      super.onStop();
      baiduLocationTool.stopLocation();
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

   private void loadCityData() {
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceResponseObject>() {
         @Override
         public void onSuccess(ProvinceResponseObject o) {
            List<ProvinceResponseParam> lstProvince = o.getLstProvince();
            for (ProvinceResponseParam provinceResponseParam : lstProvince) {
               arrayList.addAll(provinceResponseParam.getLstCity());
               adapter.notifyDataSetChanged();
            }
         }

         @Override
         public void onError(ProvinceResponseObject o) {
         }
      });
   }

   private void searchCity(String city) {
      if (TextUtils.isEmpty(city)) {
         adapter.updateAdapterList(arrayList);
         return;
      }
      ArrayList<CityResponseParam> cityList = new ArrayList<>();
      for (CityResponseParam cityResponseParam : arrayList) {
         if (cityResponseParam.getCityName().contains(city)) {
            cityList.add(cityResponseParam);
         }
      }
      adapter.updateAdapterList(cityList);
   }
}


