package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.CountView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestParam;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseObject;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;
import java.util.List;

public class MineIntegralActivity extends BaseActivity implements View.OnClickListener, RefreshLayout.OnRefreshListener, RefreshLayout.ILoadListener {


    private TextView biLiView;
    private RefreshLayout refreshLayout;
    private CountView integralView;
    private ListView listView;

    private ArrayList<IntegralrecordListResponseParam> dataList;
    private CustomBaseAdapter<IntegralrecordListResponseParam> adapter;

    private int page = 1;
    private double integral ;
    private UserInfoResponseParam userInfo;
    private String firstQueryTime;

    public static void startMineIntegralActivity(Context context){
        Intent intent = new Intent(context,MineIntegralActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_integral);
        initAdapter();
        initView();
        userInfo = SaveObjectTool.openUserInfoResponseParam();
        loadDataIndex();

    }

    private void initView(){
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        initTitleBar_1();
        titleView.setText(getString(R.string.integral));
        actionButton.setText("转入");
        actionButton.setOnClickListener(this);
//        actionButton.setVisibility(View.VISIBLE);
        integralView = (CountView)findViewById(R.id.gold);
        biLiView = (TextView)findViewById(R.id.bili);
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);


        listView.setAdapter(adapter);

        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);

        integral = userInfo.getIntegral();
    }

    @Override
    protected void onResume() {
        super.onResume();
        integralView.setText(NumberFormatTool.numberFormatTo4(integral));
       // integralView.showNumberWithAnimation(Float.parseFloat(NumberFormatTool.numberFormatTo4(integral)));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                TurnIntoPointsActivity.startTurnIntoPointsActivity(MineIntegralActivity.this);
                break;
        }
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
                moneyView.setText(type+NumberFormatTool.numberFormatTo4(obj.getIntegralNum()));
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
        dataList.clear();
        firstQueryTime = "";
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
        integralrecordListRequestParam.setType("0");//账单类型 0普通积分账单1惠宝账单 2公益资金 3提现账单 4积分转换 5充值
        integralrecordListRequestParam.setMemberId(userInfo.getMemberId());

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
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                }
            }

            @Override
            public void onError(IntegralrecordListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    //加载数据字典
    public void loadDataIndex(){
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList){
                    String type = userInfo.getType();
                    if (TextUtils.equals("1",type)){
                        if (CustomConfig.DATA_INDEX_VIP_INTEGRAL.equals(app.getName())){
                            Double d = Double.parseDouble(app.getContent());
                            d = d * 100 ;
                            biLiView.setText(d +"%");
                        }
                    }else{
                        if (CustomConfig.DATA_INDEX_MINE_INTEGRAL.equals(app.getName())){
                            Double d = Double.parseDouble(app.getContent());
                            d = d * 100 ;
                            biLiView.setText(d + "%");
                        }
                    }
                }
            }
            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }


}
