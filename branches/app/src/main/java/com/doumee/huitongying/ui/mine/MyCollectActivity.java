package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.collects.CollectsListRequestObject;
import com.doumee.model.request.collects.CollectsListRequestParam;
import com.doumee.model.response.collects.CollectsListResponseObject;
import com.doumee.model.response.collects.CollectsListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/30.
 */

public class MyCollectActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {

   ListView lv_collsj;

   RefreshLayout refreshLayout;
   /**
    * 适配器
    */
   private AdapterShopCollect adapter;
   //数据源
   private ArrayList<CollectsListResponseParam> arrlist = new ArrayList<CollectsListResponseParam>();
   /**
    * 设置页面
    */
   private int page = 1;
   /**
    * 获取当前时间
    */
   private String firstQueryTime;

   BitmapUtils bitmapUtils;

   public static void CollectActivity(Context context) {
      Intent intent = new Intent(context, MyCollectActivity.class);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_collect);
      initview();
      initBitmapParames();
      refresh();
      request();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("我的收藏");
      lv_collsj = (ListView) findViewById(R.id.lv_collsj);
      refreshLayout = (RefreshLayout) findViewById(R.id.rl_collsj_sx);
   }
//   @Override
//   public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                            Bundle savedInstanceState) {
//
//      View view = inflater.inflate(R.layout.fragment_collsj, container, false);
//      ButterKnife.bind(this, view);
//      httpTool = HttpTool.newInstance(getActivity());
//      initBitmapParames();
//      refresh();
//      request();
//      return view;
//
//   }


   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(MyCollectActivity.this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
      adapter = new AdapterShopCollect(arrlist, MyCollectActivity.this, bitmapUtils);
      lv_collsj.setAdapter(adapter);
   }

   /**
    * 收藏商家网络请求
    */
   private void request() {
      // TODO Auto-generated method stub
      CollectsListRequestObject object = new CollectsListRequestObject();
      object.setParam(new CollectsListRequestParam());
      object.getParam().setType("1");//类型 1:商家 0:商品

      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(10);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else if (page != 1) {

         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.SHOP_COLLECT, new HttpTool.HttpCallBack<CollectsListResponseObject>() {
         @Override
         public void onSuccess(CollectsListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     /**
                      * 分页
                      */
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }
                     Log.i("店铺收藏数据2", o.getRecordList() + "");
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());

                     adapter.notifyDataSetChanged();
                  }

               }
            }

            //        lv_recommend.onRefreshComplete();
         }

         @Override
         public void onError(CollectsListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            Toast.makeText(MyCollectActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });


   }

   @Override
   public void onRefresh() {
      page = 1;

      refreshLayout.setRefreshing(true);
      request();
   }

   @Override
   public void onLoad() {
      page++;
      refreshLayout.setLoading(true);
      request();
   }

}
