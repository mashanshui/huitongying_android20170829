package com.huixiangshenghuo.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestParam;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseObject;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends BaseActivity implements RefreshLayout.ILoadListener,RefreshLayout.OnRefreshListener {



    private static final int ORDER_TAB_0 = 0;//0普通积分账单
    private static final int ORDER_TAB_1 = 1;//1有效积分账单
    private static final int ORDER_TAB_2 = 2;//2转账积分账单
    private static final int ORDER_TAB_3 = 3;//3提现账单
    private static final int ORDER_TAB_4 = 4;//4积分转换
    private static final int ORDER_TAB_5 = 5;//5充值
    private static final int ORDER_TAB_6 = 6;//积分返利



    private static final int TAB_1 = 1;
    private static final int TAB_2 = 2;

    private RefreshLayout refreshLayout;
    private ListView listView;
    private RadioButton typeCheckBox,sortCheckBox;
    private PopupWindow typeMenu,sortMenu ;

    private int page = 1;
    private String queryTime;
    private int queryType = ORDER_TAB_1;
    private boolean typeOff,sortOff = false;
    private int lastType = ORDER_TAB_1, lastSort = ORDER_TAB_0;
    private int currTab = TAB_1;

    private CustomBaseAdapter<IntegralrecordListResponseParam> adapter;
    private ArrayList<IntegralrecordListResponseParam> dataList;

    private Drawable defaultDrawable,redRightDrawable,red2RightDrawable;

    public static void startOrderListActivity(Context context){
        Intent intent = new Intent(context,OrderListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        dataList = new ArrayList<>();
        defaultDrawable = ContextCompat.getDrawable(getApplication(),R.drawable.arrow_bottom_gray);
        redRightDrawable = ContextCompat.getDrawable(getApplication(),R.drawable.arrow_bottom_red);
        red2RightDrawable = ContextCompat.getDrawable(getApplication(),R.drawable.arrow_bottom_red_2);
        initTitleBar_1();
        initView();
        initTypeMenu();
        initSortMenu();
        initListener();
        onRefresh();
    }

    private void initView(){
        titleView.setText("账单");
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);
        typeCheckBox = (RadioButton)findViewById(R.id.goods_type) ;
        sortCheckBox = (RadioButton)findViewById(R.id.goods_sort) ;
        refreshLayout.setLoading(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        typeCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,red2RightDrawable,null);
        sortCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,defaultDrawable,null);

        adapter = new CustomBaseAdapter<IntegralrecordListResponseParam>(dataList,R.layout.activity_order_list_item) {
            @Override
            public void bindView(ViewHolder holder, IntegralrecordListResponseParam obj) {
                TextView contentView = holder.getView(R.id.content);
                TextView moneyView = holder.getView(R.id.money);
                TextView timeView = holder.getView(R.id.time);
                String type = obj.getType();
                contentView.setText(obj.getContent());
                if (TextUtils.equals("0",type)){
                    moneyView.setText("-"+obj.getIntegralNum());
                }else{
                    moneyView.setText("+"+obj.getIntegralNum());
                }
                timeView.setText(obj.getCreateDate());
            }
        };
        listView.setAdapter(adapter);
    }

    private void initListener(){
        typeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = typeCheckBox.isChecked();
                currTab = TAB_1;
                if (isCheck && typeOff){
                    sortMenu.dismiss();
                    typeCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,redRightDrawable,null);
                    typeMenu.showAsDropDown(typeCheckBox);
                }else{
                    typeCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,red2RightDrawable,null);
                    queryType = lastType;
                    onRefresh();
                }

                typeOff = true;
                sortCheckBox.setChecked(false);
                sortCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,defaultDrawable,null);
                sortOff = false;
                sortMenu.dismiss();

            }
        });
        sortCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = sortCheckBox.isChecked();
                currTab = TAB_2;
                if (isCheck && sortOff){
                    typeMenu.dismiss();
                    sortMenu.showAsDropDown(sortCheckBox);
                    sortCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,redRightDrawable,null);
                }else{
                    if (lastSort == 0){
                        lastSort = ORDER_TAB_0;
                    }
                    queryType = lastSort;
                    sortCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,red2RightDrawable,null);
                    onRefresh();
                }
                sortOff = true;
                typeOff = false;
                typeCheckBox.setChecked(false);
                typeCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,defaultDrawable,null);
                typeMenu.dismiss();
            }
        });
    }

    private void initTypeMenu(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_order_list_nemu, null, false);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.menu) ;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                typeMenu.dismiss();
                switch (checkedId){
                    case R.id.tab2:
                        queryType = ORDER_TAB_1;
                        typeCheckBox.setText("惠宝总账");
                        break;
                    case R.id.tab3:
                        queryType = ORDER_TAB_3;
                        typeCheckBox.setText("提现");
                        break;
                    case R.id.tab4:
                        queryType = ORDER_TAB_5;
                        typeCheckBox.setText("充值");
                        break;
                    case R.id.tab5:
                        queryType = ORDER_TAB_2;
                        typeCheckBox.setText("转账");
                        break;
                    case R.id.tab6:
                        queryType = ORDER_TAB_4;
                        typeCheckBox.setText("积分转换");
                        break;
                }
                lastType = queryType;
                currTab = TAB_1;
                onRefresh();
            }
        });

        typeMenu = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        typeMenu.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
        typeMenu.setTouchable(true);
        typeMenu.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });
        int color = ContextCompat.getColor(getApplication(),R.color.lineColor);
        typeMenu.setBackgroundDrawable(new ColorDrawable(color));    //要为popWindow设置一个背景才有效
        typeMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                typeCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,red2RightDrawable,null);
            }
        });
    }

    private void initSortMenu(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_order_list_sort, null, false);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.menu) ;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sortMenu.dismiss();
                switch (checkedId){
                    case R.id.tab1:
                        queryType = ORDER_TAB_0;
                        sortCheckBox.setText("积分总账");
                        break;

                    case R.id.tab2:
                        queryType = ORDER_TAB_6;
                        sortCheckBox.setText("转账返利");
                        break;

                    case R.id.tab3:
                        queryType = ORDER_TAB_4;
                        sortCheckBox.setText("积分转换");
                        break;
                }
                lastSort = queryType;
                currTab = TAB_2;
                onRefresh();
            }
        });

        sortMenu = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        sortMenu.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
        sortMenu.setTouchable(true);
        sortMenu.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });
        int color = ContextCompat.getColor(getApplication(),R.color.lineColor);
        sortMenu.setBackgroundDrawable(new ColorDrawable(color));    //要为popWindow设置一个背景才有效
        sortMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                sortCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,null,red2RightDrawable,null);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        dataList.clear();
        page = 1;
        queryTime = "";
        loadData();
    }

    @Override
    public void onLoad() {
        page++;
        refreshLayout.setLoading(true);
        loadData();
    }


    private void loadData(){
        IntegralrecordListRequestParam integralrecordListRequestParam = new IntegralrecordListRequestParam();
        integralrecordListRequestParam.setType(queryType+"");
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(queryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        IntegralrecordListRequestObject integralrecordListRequestObject = new IntegralrecordListRequestObject();
        integralrecordListRequestObject.setPagination(paginationBaseObject);
        integralrecordListRequestObject.setParam(integralrecordListRequestParam);
        httpTool.post(integralrecordListRequestObject, URLConfig.ORDER_LIST, new HttpTool.HttpCallBack<IntegralrecordListResponseObject>() {
            @Override
            public void onSuccess(IntegralrecordListResponseObject o) {
                queryTime = o.getFirstQueryTime();
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                if (queryType == ORDER_TAB_4){
                    List<IntegralrecordListResponseParam> recordList = o.getRecordList();
                    for (IntegralrecordListResponseParam info : recordList){
                        String type  = info.getType();
                        if (currTab == TAB_1 ){
                            if (TextUtils.equals("1",type)) {
                                dataList.add(info);
                            }
                        }else{
                            if (TextUtils.equals("0",type)) {
                                dataList.add(info);
                            }
                        }
                    }
                }else{
                    dataList.addAll(o.getRecordList());
                }
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                }
            }

            @Override
            public void onError(IntegralrecordListResponseObject o) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                ToastView.show(o.getErrorMsg());
            }
        });
      }

}
