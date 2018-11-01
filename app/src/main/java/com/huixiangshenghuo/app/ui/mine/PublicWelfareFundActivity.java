package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homemine.PublicWelfareFundAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.CountView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestObject;
import com.doumee.model.request.integralrecord.IntegralrecordListRequestParam;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseObject;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/5.
 * 公益积金
 */

public class PublicWelfareFundActivity extends BaseActivity implements RefreshLayout.OnRefreshListener, RefreshLayout.ILoadListener {


   private RefreshLayout refreshLayout;
   private CountView integralView;
   private ListView listView;

   private PublicWelfareFundAdapter adapter;//适配器
   //数据源
   private ArrayList<IntegralrecordListResponseParam> arrlist = new ArrayList<IntegralrecordListResponseParam>();

   private int page = 1;//设置页面

   private String firstQueryTime;//获取当前时间

   private double publicFee;

   private UserInfoResponseParam userInfo;

   public static void startPublicWelfareFundActivity(Context context) {
      Intent intent = new Intent(context, PublicWelfareFundActivity.class);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_public_welfare_fund);
      userInfo = SaveObjectTool.openUserInfoResponseParam();
      initview();
      initdate();
      loadData();
   }


   private void initview() {
      initTitleBar_1();
      titleView.setText("公益积金");
      integralView = (CountView) findViewById(R.id.gold);
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
      listView = (ListView) findViewById(R.id.list_view);

      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      publicFee = userInfo.getPublicFee();
   }

   @Override
   protected void onResume() {
      super.onResume();
      integralView.setText(NumberFormatTool.numberFormatTo4(publicFee));
   }

   private void initdate() {



      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);

      adapter = new PublicWelfareFundAdapter(arrlist, PublicWelfareFundActivity.this);
      listView.setAdapter(adapter);
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

   private void loadData() {
      IntegralrecordListRequestParam integralrecordListRequestParam = new IntegralrecordListRequestParam();
      integralrecordListRequestParam.setType("2");//账单类型 0会员积分账单  1会员惠宝账单 2公益资金账单 3商家账单
      integralrecordListRequestParam.setMemberId(userInfo.getMemberId());

      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }

      IntegralrecordListRequestObject integralrecordListRequestObject = new IntegralrecordListRequestObject();
      integralrecordListRequestObject.setParam(integralrecordListRequestParam);
      integralrecordListRequestObject.setPagination(paginationBaseObject);
      httpTool.post(integralrecordListRequestObject, URLConfig.ORDER_LIST, new HttpTool.HttpCallBack<IntegralrecordListResponseObject>() {
         @Override
         public void onSuccess(IntegralrecordListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  if (o != null && o.getRecordList() != null) {
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());
                     adapter.notifyDataSetChanged();
                     if (o.getRecordList().isEmpty()) {
                        listView.setBackgroundResource(R.mipmap.gwc_default);
                     } else {
                        listView.setBackgroundResource(0);
                     }

                  }

               }
            }

            //        lv_recommend.onRefreshComplete();
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
