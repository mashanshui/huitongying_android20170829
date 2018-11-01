package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestParam;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseObject;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;
import java.util.List;

public class GoldRecordActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.ILoadListener{
   public static String TYPE = "Type";
   private String Type;

    private RefreshLayout refreshLayout;
    private ListView listView;

    private ArrayList<IntegralrecordListResponseParam> dataList;
    private CustomBaseAdapter<IntegralrecordListResponseParam> adapter;

    private int page = 1;
    private UserInfoResponseParam userInfo;
    private String userId;
    private String firstQueryTime;

   public static void startGoldRecordActivity(Context context, String Type) {
        Intent intent = new Intent(context,GoldRecordActivity.class);
      intent.putExtra(TYPE, Type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold_record);
        userInfo = SaveObjectTool.openUserInfoResponseParam();
        userId = userInfo.getMemberId();
       Type = getIntent().getStringExtra(TYPE);
        initAdapter();
        initView();
    }

    private void initView(){
        initTitleBar_1();
       if (Type.equals("1")) {
          titleView.setText("惠宝");
       } else {
          titleView.setText("收入明细");
       }

        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
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


    private void loadData(){
        IntegralrecordListRequestParam integralrecordListRequestParam = new IntegralrecordListRequestParam();
       integralrecordListRequestParam.setType(Type);//账单类型 0会员积分账单  1会员惠宝账单 2公益资金账单 3商家账单
        integralrecordListRequestParam.setMemberId(userId);

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
}
