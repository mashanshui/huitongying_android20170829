package com.huixiangshenghuo.app.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.notices.NoticesListRequestObject;
import com.doumee.model.request.notices.NoticesListRequestParam;
import com.doumee.model.response.notices.NoticesListResponseObject;
import com.doumee.model.response.notices.NoticesListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;

import java.util.ArrayList;

public class NocationActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.ILoadListener {


    private RefreshLayout refreshLayout;
    private ListView listView;
    private int page = 1;
    private ArrayList<NoticesListResponseParam> dataList;
    private CustomBaseAdapter<NoticesListResponseParam> adapter;
    private String firstQueryTime;

    public static void startNocationActivity(Context context){
        Intent intent = new Intent(context,NocationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nocation);
        initTitleBar_1();
        dataList = new ArrayList<>();
        initView();
        onRefresh();
    }

    private void initView(){
        titleView.setText("通知");
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);
        refreshLayout.setLoading(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);

        adapter = new CustomBaseAdapter<NoticesListResponseParam>(dataList,R.layout.activity_nocation_item) {
            @Override
            public void bindView(ViewHolder holder, NoticesListResponseParam obj) {
                ImageView imageView = holder.getView(R.id.logo);
                TextView nameView = holder.getView(R.id.name);
                TextView timeView = holder.getView(R.id.time);
                TextView messageView = holder.getView(R.id.message);
                nameView.setText(obj.getTitle());
                timeView.setText(obj.getCreateDate());
                messageView.setText(obj.getContent());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticesListResponseParam info =  dataList.get(position);
                NoticesInfoActivity.startNoticesInfoActivity(NocationActivity.this,info.getTitle(),info.getContent(),info.getCreateDate());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onRefresh() {
        page = 1;
        firstQueryTime = "";
        refreshLayout.setRefreshing(true);
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
        NoticesListRequestParam noticesListRequestParam = new NoticesListRequestParam();
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(firstQueryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        NoticesListRequestObject noticesListRequestObject = new NoticesListRequestObject();
        noticesListRequestObject.setParam(noticesListRequestParam);
        noticesListRequestObject.setPagination(paginationBaseObject);
        httpTool.post(noticesListRequestObject, URLConfig.SYS_MESSAGE, new HttpTool.HttpCallBack<NoticesListResponseObject>() {
            @Override
            public void onSuccess(NoticesListResponseObject o) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                firstQueryTime = o.getFirstQueryTime();
                dataList.addAll(o.getRecordList());
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                }
            }
            @Override
            public void onError(NoticesListResponseObject o) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }
}
