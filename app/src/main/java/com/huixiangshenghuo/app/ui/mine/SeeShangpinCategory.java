package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doumee.model.request.shopCate.CateListRequestObject;
import com.doumee.model.request.shopCate.CateListRequestParam;
import com.doumee.model.response.shopCate.CateListResponseObject;
import com.doumee.model.response.shopCate.CateListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/7.
 */

public class SeeShangpinCategory extends BaseActivity {
   //   private RefreshLayout refreshLayout;
   private ListView listView;
   private int page = 1;
   /**
    * 适配器
    */
   private SeeShangpinCategoryAdapter adapter;
   //数据源
   private ArrayList<CateListResponseParam> arrlist = new ArrayList<CateListResponseParam>();
   private String firstQueryTime;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_see_shangpin_category);

      initView();
      initDate();
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("商品分类");
//      refreshLayout = (RefreshLayout) findViewById(R.id.refresh_see_shangpin_category);
      listView = (ListView) findViewById(R.id.lv_see_shangpin_category);
/*      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);*/

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("categoryId", arrlist.get(position).getCateId());
            intent.putExtra("categoryname", arrlist.get(position).getName());
            setResult(RESULT_OK, intent);
            finish();
         }
      });


   }

   private void initDate() {
      //构建适配器
      adapter = new SeeShangpinCategoryAdapter(arrlist, SeeShangpinCategory.this);
      listView.setAdapter(adapter);
      request();
   }

/*   @Override
   public void onRefresh() {
      page = 1;
      refreshLayout.setRefreshing(true);
      request();
   }

   @Override
   public void onLoad() {
      refreshLayout.setLoading(true);
      page++;
      request();
   }*/

   //查看列表
   private void request() {
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      CateListRequestObject memberInfoParamObject = new CateListRequestObject();
      CateListRequestParam cateListRequestParam = new CateListRequestParam();
      cateListRequestParam.setShopId(userInfo.getShopId());
      memberInfoParamObject.setParam(cateListRequestParam);
      httpTool.post(memberInfoParamObject, URLConfig.SHOP_CATE, new HttpTool.HttpCallBack<CateListResponseObject>() {
         @Override
         public void onSuccess(CateListResponseObject o) {
            /*refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);*/
            if (o != null && o.getRecordList() != null) {
               /**
                * 分页
                */
               if (page == 1 && !arrlist.isEmpty()) {
                  //清空
                  arrlist.clear();
               }
               firstQueryTime = o.getFirstQueryTime();
               arrlist.addAll(o.getRecordList());

               adapter.notifyDataSetChanged();
            }

         }

         @Override
         public void onError(CateListResponseObject o) {
            /*refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);*/

         }
      });
   }
}
