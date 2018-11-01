package com.huixiangshenghuo.app.ui.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.response.userinfo.AreaResponseParam;
import com.doumee.model.response.userinfo.CityResponseParam;
import com.doumee.model.response.userinfo.ProvinceResponseObject;
import com.doumee.model.response.userinfo.ProvinceResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AreaActivity extends BaseActivity {

    ExpandableListView expandableListView;
    private ExpandableListViewAdapter adapter;

    private ArrayList<String> cityList;
    private ArrayList<List<AreaResponseParam>> areaList;
    private HashMap<String,String> areaMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        initView();
        cityList = new ArrayList<>();
        areaList = new ArrayList<>();
        areaMap = new HashMap<>();
        initAdapter();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("选择区域");
        expandableListView = (ExpandableListView)findViewById(R.id.address);
        actionButton.setText("确定");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaMap.isEmpty()){
                    ToastView.show("请选择区域");
                    return;
                }
                if (areaMap.size() > 1){
                    ToastView.show("只能选择一个区域");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("data",areaMap);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        actionButton.setVisibility(View.VISIBLE);
    }

    private void initAdapter(){
        adapter = new ExpandableListViewAdapter();
        expandableListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCityData();
    }

    private void loadCityData(){
        showProgressDialog("正在加载..");
        RequestBaseObject requestBaseObject = new RequestBaseObject();
        httpTool.post(requestBaseObject, URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceResponseObject>() {
            @Override
            public void onSuccess(ProvinceResponseObject o) {
                dismissProgressDialog();
                List<ProvinceResponseParam> lstProvince = o.getLstProvince();
                cityList.clear();
                areaList.clear();
                for (ProvinceResponseParam provinceResponseParam : lstProvince){
                    String proName = provinceResponseParam.getName();
                    List<CityResponseParam> lstCity =  provinceResponseParam.getLstCity();
                    for (CityResponseParam city : lstCity){
                        String cityName = city.getCityName();
                        List<AreaResponseParam> lstArea = city.getLstArea();
                        cityList.add(proName+"/"+cityName);
                        areaList.add(lstArea);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(ProvinceResponseObject o) {
                dismissProgressDialog();
            }
        });
    }

    private class ExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return areaList.get(groupPosition).get(childPosition);
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return areaList.get(groupPosition).size();
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            return super.getChildType(groupPosition, childPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return cityList.get(groupPosition);
        }

        @Override
        public int getGroupTypeCount() {
            return super.getGroupTypeCount();
        }

        @Override
        public int getChildTypeCount() {
            return super.getChildTypeCount();
        }

        @Override
        public int getGroupCount() {
            return cityList.size();
        }

        @Override
        public int getGroupType(int groupPosition) {
            return super.getGroupType(groupPosition);
        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return super.getCombinedChildId(groupId, childId);
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return super.getCombinedGroupId(groupId);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHoldView holdView = null;
            if(null == convertView){
                holdView = new ChildHoldView();
                convertView = View.inflate(AreaActivity.this,R.layout.shop_type_child,null);
                holdView.checkBox = (CheckBox) convertView.findViewById(R.id.child);
                convertView.setTag(holdView);
            }else {
                holdView = (ChildHoldView) convertView.getTag();
            }
            AreaResponseParam areaResponseParam = (AreaResponseParam) getChild(groupPosition,childPosition);
            holdView.checkBox.setText(areaResponseParam.getAreaName());
            final String areaId = areaResponseParam.getAreaId();
            final String areaName = cityList.get(groupPosition) + "/" + areaResponseParam.getAreaName();
            holdView.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        areaMap.put(areaId,areaName);
                    }else{
                        areaMap.remove(areaId);
                    }
                }
            });
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHoldView groupHoldView = null;
            if (convertView == null){
                groupHoldView = new GroupHoldView();
                convertView = View.inflate(AreaActivity.this,R.layout.shop_type_group,null);
                groupHoldView.textView =(TextView) convertView.findViewById(R.id.group);
                convertView.setTag(groupHoldView);
            }else{
                groupHoldView = (GroupHoldView) convertView.getTag();
            }
            groupHoldView.textView.setText(cityList.get(groupPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    private class GroupHoldView {
        TextView textView;
    }

    private class ChildHoldView{
        CheckBox checkBox;
    }
}
