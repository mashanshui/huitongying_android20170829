package com.huixiangshenghuo.app.activity.homepage;

import android.os.Bundle;
import android.widget.ListView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homepage.HomePageNoticeAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.notices.NoticesListRequestObject;
import com.doumee.model.request.notices.NoticesListRequestParam;
import com.doumee.model.response.notices.NoticesListResponseObject;
import com.doumee.model.response.notices.NoticesListResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/31.
 * 公告通知
 */

public class AnnouncementInformActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {


   ListView lv_collsj;

   RefreshLayout refreshLayout;
   private HomePageNoticeAdapter adapter;//适配器
   //数据源
   private ArrayList<NoticesListResponseParam> arrlist = new ArrayList<NoticesListResponseParam>();
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_announcement_inform);
      initview();
      initData();
      refresh();

      loadData();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("公告通知");
      lv_collsj = (ListView) findViewById(R.id.lv_announcement_inform);
      refreshLayout = (RefreshLayout) findViewById(R.id.rl_sx_announcement_inform);

   }

   private void initData() {
      adapter = new HomePageNoticeAdapter(arrlist, AnnouncementInformActivity.this);
      lv_collsj.setAdapter(adapter);
   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
   }


   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);
      page = 1;
      loadData();


   }

   @Override
   public void onLoad() {
      refreshLayout.setLoading(true);
      page++;
      loadData();

   }

   /**
    * 系统通知
    */
   private void loadData() {
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
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     if (page == 1 && !arrlist.isEmpty()) {
                        arrlist.clear();
                     }

                     arrlist.addAll(o.getRecordList());

                     firstQueryTime = o.getFirstQueryTime();
                     adapter.notifyDataSetChanged();
                  }

               }
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
