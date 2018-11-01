package com.huixiangshenghuo.app.activity.homemall;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homepage.ProductsDetailsNewActivity;
import com.huixiangshenghuo.app.adapter.homemall.LatestShelvesAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.SearchProductNewActivity;
import com.huixiangshenghuo.app.view.MyGridView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/2.
 * 商品列表 可以根据 选项 排序
 */

public class GoodsListActivity extends BaseActivity implements View.OnClickListener, RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   BitmapUtils bitmapUtils;

   private String parentId;
   private String parentName;
   private CheckBox cb_qb;
   private CheckBox cb_rq;
   private CheckBox cb_xl;
   private CheckBox cb_jg;

   private String cb_rqType = "1";
   private String cb_xlType = "1";
   private String cb_jgType = "1";

   RefreshLayout refreshLayout;
   private MyGridView gridview;
   private LatestShelvesAdapter re_adapter;//适配器
   private ArrayList<ProductListResponseParam> arrlist = new ArrayList<ProductListResponseParam>();//数据源
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间
   private String Type;//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序

   public static void startGoodsListActivity(Context context, String parentId, String parentName) {
      Intent intent = new Intent(context, GoodsListActivity.class);
      intent.putExtra("parentId", parentId);
      intent.putExtra("parentName", parentName);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_goos_list);
      initview();
      initData();
      initBitmapParames();
      initListener();
      Type = "0";
      request();
   }

   private void initview() {
      initTitleBar_1();
      parentId = getIntent().getStringExtra("parentId");
      parentName = getIntent().getStringExtra("parentName");
      titleView.setText(parentName);
      actionImageButton.setVisibility(View.VISIBLE);
      gridview = (MyGridView) findViewById(R.id.gv_goos_list);
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
      cb_qb = (CheckBox) findViewById(R.id.cb_qb);
      cb_rq = (CheckBox) findViewById(R.id.cb_rq);
      cb_xl = (CheckBox) findViewById(R.id.cb_xl);
      cb_jg = (CheckBox) findViewById(R.id.cb_jg);
      actionImageButton.setOnClickListener(this);

   }

   private void initListener() {
      final Drawable drawable0 = getResources().getDrawable(R.drawable.arrow_top_red);
      drawable0.setBounds(0, 0, drawable0.getMinimumWidth(), drawable0.getMinimumHeight()); //设置边界

      final Drawable drawable = getResources().getDrawable(R.drawable.sel_st_clk);
      drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界

      final Drawable drawable2 = getResources().getDrawable(R.drawable.sel_st_clk2);
      drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight()); //设置边界

      final Drawable drawable3 = getResources().getDrawable(R.drawable.sel_st_nor);
      drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight()); //设置边界

      final Drawable drawable4 = getResources().getDrawable(R.drawable.arrow_bottom_gray);
      drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight()); //设置边界

      cb_qb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//               ToastView.show("全部");
//               cb_rq.setTextColor(getResources().getColor(R.color.Grey));
//               cb_rq.setCompoundDrawables(null, null, drawable3, null);//画在右边
//               cb_xl.setTextColor(getResources().getColor(R.color.Grey));
//               cb_xl.setCompoundDrawables(null, null, drawable3, null);//画在右边
//               cb_jg.setTextColor(getResources().getColor(R.color.Grey));
//               cb_jg.setCompoundDrawables(null, null, drawable3, null);//画在右边
//            } else {
//               ToastView.show("全部");
//            }
            Type = "0";
            page = 1;
            request();
            cb_rqType = "1";
            cb_xlType = "1";
            cb_jgType = "1";
            cb_rq.setTextColor(getResources().getColor(R.color.Grey));
            cb_rq.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_xl.setTextColor(getResources().getColor(R.color.Grey));
            cb_xl.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_jg.setTextColor(getResources().getColor(R.color.Grey));
            cb_jg.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_qb.setTextColor(getResources().getColor(R.color.colorPrimary));
            cb_qb.setCompoundDrawables(null, null, drawable0, null);//画在右边
         }
      });
      cb_rq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//               //drawableRigh
//               Type = "6";
//               page = 1;
//               request();
//               cb_rq.setCompoundDrawables(null, null, drawable, null);//画在右边
//
//            } else {
//               Type = "5";
//               page = 1;
//               request();
//
//               cb_rq.setCompoundDrawables(null, null, drawable2, null);//画在右边
//            }
            if (cb_rqType.equals("1")) {
               Type = "6";
               page = 1;
               request();
               cb_rq.setCompoundDrawables(null, null, drawable, null);//画在右边

               cb_rqType = "2";
               cb_xlType = "1";
               cb_jgType = "1";
            } else {
               Type = "5";
               page = 1;
               request();
               cb_rq.setCompoundDrawables(null, null, drawable2, null);//画在右边

               cb_rqType = "1";
               cb_xlType = "1";
               cb_jgType = "1";
            }

            cb_qb.setTextColor(getResources().getColor(R.color.Grey));
            cb_qb.setCompoundDrawables(null, null, drawable4, null);//画在右边
            cb_rq.setTextColor(getResources().getColor(R.color.colorPrimary));
            cb_xl.setTextColor(getResources().getColor(R.color.Grey));
            cb_xl.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_jg.setTextColor(getResources().getColor(R.color.Grey));
            cb_jg.setCompoundDrawables(null, null, drawable3, null);//画在右边
         }
      });
      cb_xl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//               Type = "2";
//               page = 1;
//               request();
//
//               cb_xl.setCompoundDrawables(null, null, drawable, null);//画在右边
//            } else {
//               Type = "1";
//               page = 1;
//               request();
//
//
//               cb_xl.setCompoundDrawables(null, null, drawable2, null);//画在右边
//            }
            if (cb_xlType.equals("1")) {
               Type = "2";
               page = 1;
               request();
               cb_xl.setCompoundDrawables(null, null, drawable, null);//画在右边

               cb_rqType = "1";
               cb_xlType = "2";
               cb_jgType = "1";
            } else {
               Type = "1";
               page = 1;
               request();
               cb_xl.setCompoundDrawables(null, null, drawable2, null);//画在右边

               cb_rqType = "1";
               cb_xlType = "1";
               cb_jgType = "1";
            }

            cb_qb.setTextColor(getResources().getColor(R.color.Grey));
            cb_qb.setCompoundDrawables(null, null, drawable4, null);//画在右边
            cb_rq.setTextColor(getResources().getColor(R.color.Grey));
            cb_rq.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_xl.setTextColor(getResources().getColor(R.color.colorPrimary));
            cb_jg.setTextColor(getResources().getColor(R.color.Grey));
            cb_jg.setCompoundDrawables(null, null, drawable3, null);//画在右边
         }
      });
      cb_jg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//               Type = "4";
//               page = 1;
//               request();
//
//               cb_jg.setCompoundDrawables(null, null, drawable, null);//画在右边
//            } else {
//               Type = "3";
//               page = 1;
//               request();
//
//               cb_jg.setCompoundDrawables(null, null, drawable2, null);//画在右边
//            }
            if (cb_jgType.equals("1")) {
               Type = "4";
               page = 1;
               request();
               cb_jg.setCompoundDrawables(null, null, drawable, null);//画在右边

               cb_rqType = "1";
               cb_xlType = "1";
               cb_jgType = "2";
            } else {
               Type = "3";
               page = 1;
               request();
               cb_jg.setCompoundDrawables(null, null, drawable2, null);//画在右边

               cb_rqType = "1";
               cb_xlType = "1";
               cb_jgType = "1";
            }

            cb_qb.setTextColor(getResources().getColor(R.color.Grey));
            cb_qb.setCompoundDrawables(null, null, drawable4, null);//画在右边
            cb_rq.setTextColor(getResources().getColor(R.color.Grey));
            cb_rq.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_xl.setTextColor(getResources().getColor(R.color.Grey));
            cb_xl.setCompoundDrawables(null, null, drawable3, null);//画在右边
            cb_jg.setTextColor(getResources().getColor(R.color.colorPrimary));
         }
      });
   }

   private void initData() {
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
   }

   /**
    * 图片加载
    */
   private void initBitmapParames() {
      bitmapUtils = new BitmapUtils(this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
      re_adapter = new LatestShelvesAdapter(arrlist, GoodsListActivity.this, bitmapUtils);
      gridview.setAdapter(re_adapter);
      gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ProductsDetailsActivity.startProductsDetailsActivity(GoodsListActivity.this, arrlist.get(position).getProId());

            ProductsDetailsNewActivity.startProductsDetailsNewActivity(GoodsListActivity.this, arrlist.get(position).getProId());

         }
      });
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.action_image:
//            SearchProductActivity.startSearchProductActivity(GoodsListActivity.this, 0);
            SearchProductNewActivity.startSearchProductNewActivity(GoodsListActivity.this, 0);
            break;
      }
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

   private void request() {
      // TODO Auto-generated method stub
      ProductListRequestObject object = new ProductListRequestObject();
      object.setParam(new ProductListRequestParam());
      object.getParam().setParentCateId(parentId);
      object.getParam().setType("0");
      object.getParam().setSortType(Type);//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(CustomConfig.PAGE_SIZE);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else {
         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.SHOP_QUERY, new HttpTool.HttpCallBack<ProductListResponseObject>() {
         @Override
         public void onSuccess(ProductListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getProList() != null) {
                     if (page == 1 && !arrlist.isEmpty()) {
                        arrlist.clear();
                     }
                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getProList());
                     re_adapter.notifyDataSetChanged();
                  }
                  if (page == 1) {
                     if (o.getProList().size() <= 0) {
                        gridview.setBackgroundResource(R.mipmap.gwc_default);
                     } else {
                        gridview.setBackgroundResource(0);
                     }
                  }


               }
            }


         }

         @Override
         public void onError(ProductListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });


   }

}
