package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.ProvinceRequestObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.AreaExpandAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class AreaRegisterActivity extends BaseActivity {

   private ExpandableListView expandableList;
   private List<CityParam> cityParams;
   private AreaExpandAdapter areaExpandAdapter;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_area_register);
      initTitleBar_1();
      initData();
   }

   private void initData() {
      titleView.setText("选择区域");
      expandableList = (ExpandableListView) findViewById(R.id.aa_expand_list);
      cityParams = new ArrayList<>();
      areaExpandAdapter = new AreaExpandAdapter(this, cityParams);
      expandableList.setAdapter(areaExpandAdapter);
      expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
         @Override
         public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (cityParams.get(groupPosition).isExpand()) {
               cityParams.get(groupPosition).setExpand(false);
            } else {
               cityParams.get(groupPosition).setExpand(true);
            }
            areaExpandAdapter.notifyDataSetChanged();
            return false;
         }
      });
      expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
         @Override
         public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Intent intent = new Intent();
            intent.putExtra("cityId", areaExpandAdapter.getGroup(groupPosition).getCityId());
            intent.putExtra("areaId", areaExpandAdapter.getChild(groupPosition, childPosition).getAreaId());
            intent.putExtra("areaName", areaExpandAdapter.getGroup(groupPosition).getProvinceName() + "/" + areaExpandAdapter.getGroup(groupPosition).getCityName() + "/" + areaExpandAdapter.getChild(groupPosition, childPosition).getAreaName());
            setResult(RESULT_OK, intent);
            finish();
            return false;
         }
      });
      requestArea();
   }

   private void requestArea() {
      showProgressDialog(null);
      httpTool.post(new ProvinceRequestObject(), URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceObject>() {
         @Override
         public void onSuccess(ProvinceObject resp) {
            dismissProgressDialog();
            if (resp.getLstProvince() != null && resp.getLstProvince().size() > 0) {
               for (ProvinceParam param : resp.getLstProvince()) {
                  if (param.getLstCity() != null && param.getLstCity().size() > 0) {
                     for (CityParam cityParam : param.getLstCity()) {
                        cityParam.setProvinceName(param.getName());
                     }
                     cityParams.addAll(param.getLstCity());
                  }
               }
               areaExpandAdapter.notifyDataSetChanged();
            }
         }

         @Override
         public void onError(ProvinceObject provinceObject) {
            dismissProgressDialog();
            ToastView.show(provinceObject.getErrorMsg());
         }


      });
   }
}