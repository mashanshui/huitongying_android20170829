package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.category.CategoryListRequestObject;
import com.doumee.model.request.category.CategoryListRequestParam;
import com.doumee.model.response.category.CategoryListResponseObject;
import com.doumee.model.response.category.CategoryListResponseParam;
import com.huixiangshenghuo.app.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ShopTypeActivity extends BaseActivity {


    private ExpandableListView expandableListView;
    private List<CategoryListResponseParam> groupInfoList;
    private List<List<CategoryListResponseParam>> childList;
    private HashMap<String,String> selectorMap;
    private ExpandableListViewAdapter adapter;


    public static void startShopTypeActivity(Activity context,int requestCode){
        Intent intent = new Intent(context,ShopTypeActivity.class);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_type);
        initAdapter();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupInfoList.clear();
        childList.clear();
         loadData();
    }


    /**
     * 商家 经营分类请求
     */
    private void loadData() {
        showProgressDialog("正在加载..");
        CategoryListRequestParam categoryListRequestParam = new CategoryListRequestParam();
        categoryListRequestParam.setType("0");//商家、商品一级分类列表
        CategoryListRequestObject categoryListRequestObject = new CategoryListRequestObject();
        categoryListRequestObject.setParam(categoryListRequestParam);

        httpTool.post(categoryListRequestObject, URLConfig.GOODS_MENU, new HttpTool.HttpCallBack<CategoryListResponseObject>() {
            @Override
            public void onSuccess(CategoryListResponseObject o) {
                dismissProgressDialog();
                List<CategoryListResponseParam> recordList = o.getRecordList();
                for (CategoryListResponseParam info : recordList){
                    groupInfoList.add(info);
                    childList.add(info.getChildrenList())  ;
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(CategoryListResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("经营产品分类");
        expandableListView = (ExpandableListView)findViewById(R.id.list_view);
        expandableListView.setAdapter(adapter);

        actionButton.setText("确定");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectorMap.isEmpty()){
                    ToastView.show("请选择经营产品分类");
                    return;
                }
                if (selectorMap.size() > 1){
                    ToastView.show("经营产品分类只能选择一个");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("data",selectorMap);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initAdapter(){
        groupInfoList = new LinkedList<>();
        childList = new LinkedList<>();
        selectorMap = new HashMap<>();
        adapter = new ExpandableListViewAdapter();
    }

    private class ExpandableListViewAdapter extends BaseExpandableListAdapter{

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            return super.getChildType(groupPosition, childPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupInfoList.get(groupPosition);
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
            return groupInfoList.size();
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
                convertView = View.inflate(ShopTypeActivity.this,R.layout.shop_type_child,null);
                holdView.checkBox = (CheckBox) convertView.findViewById(R.id.child);
                convertView.setTag(holdView);
            }else {
                holdView = (ChildHoldView) convertView.getTag();
            }
            CategoryListResponseParam areaResponseParam = (CategoryListResponseParam) getChild(groupPosition,childPosition);
            holdView.checkBox.setText(areaResponseParam.getCateName());
            final String areaId = areaResponseParam.getCateId();
            final String areaName = groupInfoList.get(groupPosition).getCateName() + "/" + areaResponseParam.getCateName();
            holdView.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        selectorMap.put(areaId,areaName);
                    }else{
                        selectorMap.remove(areaId);
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
                convertView = View.inflate(ShopTypeActivity.this,R.layout.shop_type_group,null);
                groupHoldView.textView =(TextView) convertView.findViewById(R.id.group);
                convertView.setTag(groupHoldView);
            }else{
                groupHoldView = (GroupHoldView) convertView.getTag();
            }
            groupHoldView.textView.setText(groupInfoList.get(groupPosition).getCateName());
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
