package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.CountView;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestParam;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseObject;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.view.RefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Deprecated
public class MineJiesuanActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.ILoadListener{


    private TextView biLiView;
    private RefreshLayout refreshLayout;
    private CountView integralView;
    private ListView listView;

    private ArrayList<IntegralrecordListResponseParam> dataList;
    private CustomBaseAdapter<IntegralrecordListResponseParam> adapter;

    private int page = 1;
    private double integral ;
    private UserInfoResponseParam userInfo;
    private String userId;
    private String firstQueryTime;
    private String startDate;
    private String endDate;

    public static void startMineJiesuanActivity(Context context){
        Intent intent = new Intent(context,MineJiesuanActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_jiesuan);
        userInfo = SaveObjectTool.openUserInfoResponseParam();
        userId = userInfo.getMemberId();
        initAdapter();
        initView();
        initDate();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("昨日结算");
        integralView = (CountView)findViewById(R.id.gold);
        biLiView = (TextView)findViewById(R.id.bili);
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);

        biLiView.setText(CustomConfig.RMB+userInfo.getTotalMoney());
        listView.setAdapter(adapter);

        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        integral = userInfo.getYesterdayMoney();
        integralView.setText(NumberFormatTool.numberFormatTo4(integral));
       // integralView.showNumberWithAnimation(Float.parseFloat(integral+""));
    }

    private void initAdapter(){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<IntegralrecordListResponseParam>(dataList,R.layout.activity_order_list_item) {
            @Override
            public void bindView(ViewHolder holder, IntegralrecordListResponseParam obj) {
                TextView contentView = holder.getView(R.id.content);
                TextView moneyView = holder.getView(R.id.money);
                TextView timeView = holder.getView(R.id.time);
                contentView.setText(obj.getContent());
                String type = obj.getType();
                if (TextUtils.equals("0",type)){
                    type = "-";
                }else{
                    type = "+";
                }
                moneyView.setText(type+obj.getIntegralNum());
                timeView.setText(obj.getCreateDate());
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        refreshLayout.setRefreshing(true);
        firstQueryTime = "";
        dataList.clear();
        loadData();
    }
    @Override
    public void onLoad() {
        page++;
        refreshLayout.setLoading(true);
        loadData();
    }

    private void initDate(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.DATE,-1);
        Date date = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        startDate = dateString + " 00:00:00";
        endDate = dateString + " 23:59:59";
    }

    private void loadData(){
        IntegralrecordListRequestParam integralrecordListRequestParam = new IntegralrecordListRequestParam();
        integralrecordListRequestParam.setType("");
        integralrecordListRequestParam.setMemberId(userId);
        integralrecordListRequestParam.setStartDate(startDate);
        integralrecordListRequestParam.setEndDate(endDate);

        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(firstQueryTime);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        paginationBaseObject.setPage(page);

        IntegralrecordListRequestObject integralrecordListRequestObject = new IntegralrecordListRequestObject();
        integralrecordListRequestObject.setParam(integralrecordListRequestParam);
        integralrecordListRequestObject.setPagination(paginationBaseObject);
        httpTool.post(integralrecordListRequestObject, URLConfig.ORDER_LIST, new HttpTool.HttpCallBack<IntegralrecordListResponseObject>() {
            @Override
            public void onSuccess(IntegralrecordListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                List<IntegralrecordListResponseParam> recordList = o.getRecordList();
                firstQueryTime = o.getFirstQueryTime();
                dataList.addAll(recordList);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(IntegralrecordListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }

}
