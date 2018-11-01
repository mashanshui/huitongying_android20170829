package com.huixiangshenghuo.app.activity.homepage;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homepage.BeginnerGuideAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.AdInfoActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.articles.ArticlesListRequestObject;
import com.doumee.model.request.articles.ArticlesListRequestParam;
import com.doumee.model.response.articles.ArticlesListResponseObject;
import com.doumee.model.response.articles.ArticlesListResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/27.
 * 新手指南
 */

public class BeginnerGuideActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   ListView listview;

   RefreshLayout refreshLayout;

   private BeginnerGuideAdapter adapter;//适配器
   //数据源
   private ArrayList<ArticlesListResponseParam> arrlist = new ArrayList<ArticlesListResponseParam>();

   private int page = 1;//设置页面

   private String firstQueryTime;//获取刷新时候时间

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_beginner_guide);
      initview();
      refresh();
      request();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("新手指南");
      listview = (ListView) findViewById(R.id.lv_beginner_guide);
      refreshLayout = (RefreshLayout) findViewById(R.id.rl_sx_beginner_guide);

      listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AdInfoActivity.startAdInfoActivity(BeginnerGuideActivity.this, "0", arrlist.get(position).getContent(), "", arrlist.get(position).getTitle());//0内容1外链接
         }
      });
   }



   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
      adapter = new BeginnerGuideAdapter(arrlist, BeginnerGuideActivity.this);
      listview.setAdapter(adapter);
   }

   @Override
   public void onRefresh() {

      refreshLayout.setRefreshing(true);
      page = 1;
      request();

   }

   @Override
   public void onLoad() {
      refreshLayout.setLoading(true);
      page++;
      request();
   }

   /**
    * 新手任务
    */
   private void request() {
      // TODO Auto-generated method stub
      ArticlesListRequestObject object = new ArticlesListRequestObject();
      object.setParam(new ArticlesListRequestParam());
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(10);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else {
         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.NOVICE_GUIDE, new HttpTool.HttpCallBack<ArticlesListResponseObject>() {
         @Override
         public void onSuccess(ArticlesListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {

                     if (page == 1 && !arrlist.isEmpty()) {
                        arrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());
                     adapter.notifyDataSetChanged();
                     if (o.getRecordList().isEmpty()) {
                        listview.setBackgroundResource(R.mipmap.gwc_default);
                     } else {
                        listview.setBackgroundResource(0);
                     }
                  }
               }
            }
         }

         @Override
         public void onError(ArticlesListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });


   }
}
