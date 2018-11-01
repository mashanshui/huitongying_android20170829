package com.huixiangshenghuo.app.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.userInfo.RefereeListRequestObject;
import com.doumee.model.request.userInfo.RefereeListRequestParam;
import com.doumee.model.response.userinfo.RefereeListResponseObject;
import com.doumee.model.response.userinfo.RefereeListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MineTuiJianActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.ILoadListener {

    private RefreshLayout refreshLayout;
    private ListView listView;

    private String queryTime = "";
    private int page = 1;
    private Bitmap defaultBitmap;
    private ArrayList<RefereeListResponseParam> dataList;
    private CustomBaseAdapter<RefereeListResponseParam> adapter;

    public static void startMineTuiJianActivity(Context context){
        Intent intent = new Intent(context,MineTuiJianActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_tui_jian);
        initAdapter();
        initView();
        defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.header_img_bg);
        onRefresh();
    }
    private void initView(){
        initTitleBar_1();
        titleView.setText("我的推荐");
        refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.list_view);

        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
        listView.setAdapter(adapter);
    }

    private void initAdapter(){
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<RefereeListResponseParam>(dataList,R.layout.activity_mine_tui_jian_item) {
            @Override
            public void bindView(ViewHolder holder, RefereeListResponseParam obj) {
                ImageView faceView = holder.getView(R.id.face);
                TextView nameView = holder.getView(R.id.name);
                TextView levelView = holder.getView(R.id.level);
                TextView dateView = holder.getView(R.id.date);
                faceView.setImageBitmap(defaultBitmap);
                String name = obj.getName();
                if (TextUtils.isEmpty(name)){
                    name = obj.getLoginName();
                }
                nameView.setText(name);
                String imagePath = obj.getImgUrl();
                if (!TextUtils.isEmpty(imagePath)){
                    ImageLoader.getInstance().displayImage(imagePath,faceView);
                }
                String levelStr = "普通会员";
                String type = obj.getType();
                if (TextUtils.equals("1",type)){
                    levelStr = "VIP会员";
                }
                levelView.setText(levelStr);
                dateView.setText(obj.getJoinDate());
            }
        };
    }

    @Override
    public void onRefresh() {
        queryTime = "";
        page = 1;
        dataList.clear();
        refreshLayout.setRefreshing(true);
        loadData();
    }

    @Override
    public void onLoad() {
        page++;
        loadData();
    }

    private void loadData(){
        RefereeListRequestParam refereeListRequestParam = new RefereeListRequestParam();
        refereeListRequestParam.setMemberId(SaveObjectTool.openUserInfoResponseParam().getMemberId());
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(queryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        RefereeListRequestObject refereeListRequestObject = new RefereeListRequestObject();
        refereeListRequestObject.setPagination(paginationBaseObject);
        refereeListRequestObject.setParam(refereeListRequestParam);

        httpTool.post(refereeListRequestObject, URLConfig.MINE_TUI_JIAN, new HttpTool.HttpCallBack<RefereeListResponseObject>() {
            @Override
            public void onSuccess(RefereeListResponseObject o) {
                queryTime = o.getFirstQueryTime();
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                List<RefereeListResponseParam> data = o.getRecordList();
                dataList.addAll(data);
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    listView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    listView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
                }
            }
            @Override
            public void onError(RefereeListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });

    }


}
