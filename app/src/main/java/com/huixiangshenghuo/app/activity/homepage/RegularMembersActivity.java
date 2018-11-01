package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.userInfo.RefereeListRequestObject;
import com.doumee.model.request.userInfo.RefereeListRequestParam;
import com.doumee.model.request.userInfo.RefereeShopListRequestObject;
import com.doumee.model.request.userInfo.RefereeShopListRequestParam;
import com.doumee.model.response.userinfo.RefereeListResponseObject;
import com.doumee.model.response.userinfo.RefereeListResponseParam;
import com.doumee.model.response.userinfo.RefereeShopListResponseObject;
import com.doumee.model.response.userinfo.RefereeShopListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homepage.RefereeShopAdapter;
import com.huixiangshenghuo.app.adapter.homepage.RegularMembersAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/31.
 * 普通用户
 */

public class RegularMembersActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   public static String TYPE = "type";//推荐类型：0普通会员 1VIP会员

   public static String ZERO = "0";
   public static String ONE = "1";
   public static String TWO = "2";
   private String type;


   ListView listview;

   RefreshLayout refreshLayout;


   private RegularMembersAdapter refereeadapter;//适配器 普通会员 vip会员

   private ArrayList<RefereeListResponseParam> refereearrlist = new ArrayList<RefereeListResponseParam>();//数据源 普通会员 vip会员

   private RefereeShopAdapter shopadapter;//适配器 商家
   //数据源
   private ArrayList<RefereeShopListResponseParam> shoparrlist = new ArrayList<RefereeShopListResponseParam>();


   private int page = 1;//设置页面
   private String firstQueryTime;//获取刷新当前时间
   BitmapUtils bitmapUtils;

   public static void startRegularMembersActivity(Context context, String type) {
      Intent intent = new Intent(context, RegularMembersActivity.class);
      intent.putExtra(TYPE, type);

      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_regular_members);
      type = getIntent().getStringExtra(TYPE);
      initview();
      initBitmapParames();
      refresh();

      if (type.equals(ZERO) || type.equals(ONE)) {
         request();
      } else if (type.equals(TWO)) {
         Shoprequest();
      }

   }

   private void initview() {
      initTitleBar_1();


      if (type.equals(ZERO)) {
         titleView.setText("普通会员");
      } else if (type.equals(ONE)) {
         titleView.setText("VIP会员");
      } else {
         titleView.setText("商户");
      }

      listview = (ListView) findViewById(R.id.lv_regular_members);
      refreshLayout = (RefreshLayout) findViewById(R.id.rl_sx_regular_members);

   }



   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
      if (type.equals(ZERO) || type.equals(ONE)) {
         refereeadapter = new RegularMembersAdapter(refereearrlist, RegularMembersActivity.this, bitmapUtils);
         listview.setAdapter(refereeadapter);
      } else if (type.equals(TWO)) {
         shopadapter = new RefereeShopAdapter(shoparrlist, RegularMembersActivity.this, bitmapUtils);
         listview.setAdapter(shopadapter);
      }

   }

   /**
    * 图片加载
    */
   private void initBitmapParames() {
      bitmapUtils = new BitmapUtils(RegularMembersActivity.this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.header_img_bg);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.header_img_bg);
   }

   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);
      page = 1;
      if (type.equals(ZERO) || type.equals(ONE)) {
         request();
      } else if (type.equals(TWO)) {
         Shoprequest();
      }
   }

   @Override
   public void onLoad() {
      refreshLayout.setLoading(true);
      page++;
      if (type.equals(ZERO) || type.equals(ONE)) {
         request();
      } else if (type.equals(TWO)) {
         Shoprequest();
      }
   }

   /**
    * 直推 普通会员 VIP会员
    */
   private void request() {
      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      RefereeListRequestParam refereeListRequestParam = new RefereeListRequestParam();
      refereeListRequestParam.setType(type);//推荐类型：0普通会员 1VIP会员
      refereeListRequestParam.setMemberId(userInfoResponseParam.getMemberId());
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }
      RefereeListRequestObject refereeListRequestObject = new RefereeListRequestObject();
      refereeListRequestObject.setPagination(paginationBaseObject);
      refereeListRequestObject.setParam(refereeListRequestParam);

      httpTool.post(refereeListRequestObject, URLConfig.MINE_TUI_JIAN, new HttpTool.HttpCallBack<RefereeListResponseObject>() {

         @Override
         public void onSuccess(RefereeListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     if (page == 1 && !refereearrlist.isEmpty()) {
                        refereearrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     refereearrlist.addAll(o.getRecordList());
                     refereeadapter.notifyDataSetChanged();
                  }

               }
            }
         }

         @Override
         public void onError(RefereeListResponseObject o) {
            ToastView.show(o.getErrorMsg());

         }
      });


   }

   private void Shoprequest() {
      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      RefereeShopListRequestParam refereeShopListRequestParam = new RefereeShopListRequestParam();

      refereeShopListRequestParam.setMemberId(userInfoResponseParam.getMemberId());
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }
      RefereeShopListRequestObject refereeShopListRequestObject = new RefereeShopListRequestObject();
      refereeShopListRequestObject.setPagination(paginationBaseObject);
      refereeShopListRequestObject.setParam(refereeShopListRequestParam);

      httpTool.post(refereeShopListRequestObject, URLConfig.RECOMMENDED_BUSINESS, new HttpTool.HttpCallBack<RefereeShopListResponseObject>() {

         @Override
         public void onSuccess(RefereeShopListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     if (page == 1 && !shoparrlist.isEmpty()) {
                        shoparrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     shoparrlist.addAll(o.getRecordList());
                     shopadapter.notifyDataSetChanged();
                  }

               }
            }
         }

         @Override
         public void onError(RefereeShopListResponseObject o) {
            ToastView.show(o.getErrorMsg());

         }
      });


   }


}
