package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homepage.ProductsDetailsNewActivity;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.adapter.homepage.RecommendAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.EditTextWithDel;
import com.huixiangshenghuo.app.view.MyGridView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/19.
 * 搜索商品
 */

public class SearchProductNewActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {

   EditTextWithDel searchBar;
   RefreshLayout refreshLayout;
   MyGridView gridView;
   LinearLayout linearLayout;
   ListView listView;
   Button cancelButton;
   Button clearButton;
   HttpTool httpTool;

   private int page = 1;
   private String firstQueryTime = "";
   private Bitmap defaultBitmap;
   private String name = "";
   BitmapUtils bitmapUtils;

   private ArrayList<ProductListResponseParam> arrlist = new ArrayList<ProductListResponseParam>();
   private RecommendAdapter mAdapter;
   BaiduLocationTool baiduLocationTool;
   private double lat;
   private double lng;
   private String shopName;
   private String cateId = "";
   private String parentCateId = "";
   private String sortType = "0";
   private String cityId = "";
   private CustomBaseAdapter<String> adapter;
   private int flag;

   public static void startSearchProductNewActivity(Context context, int flag) {
      Intent intent = new Intent(context, SearchProductNewActivity.class);
      intent.putExtra("flag", flag);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search_product_new);
      httpTool = HttpTool.newInstance(this);
      flag = getIntent().getIntExtra("flag", 0);

      //    initTitleBar_1();
      UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
      cityId = user.getCityId();
      initView();
      initBitmapParames();
      refresh();
      initListener();

      page = 1;
      firstQueryTime = "";
      loadShopData();
   }


   public void initView() {


      searchBar = (EditTextWithDel) findViewById(R.id.edt_product_search);
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
      gridView = (MyGridView) findViewById(R.id.gv_searchprodut);
      linearLayout = (LinearLayout) findViewById(R.id.last_record);
      listView = (ListView) findViewById(R.id.list_view);
      cancelButton = (Button) findViewById(R.id.cancel_button);

      View footView = View.inflate(SearchProductNewActivity.this, R.layout.activity_searchproduct_foot, null);
      clearButton = (Button) footView.findViewById(R.id.clear_button);
      listView.addFooterView(footView);

      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ProductsDetailsActivity.startProductsDetailsActivity(SearchProductNewActivity.this, arrlist.get(position).getProId());
            ProductsDetailsNewActivity.startProductsDetailsNewActivity(SearchProductNewActivity.this, arrlist.get(position).getProId());


         }
      });
   }

   /**
    * 图片加载
    */
   private void initBitmapParames() {
      bitmapUtils = new BitmapUtils(SearchProductNewActivity.this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);

   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      mAdapter = new RecommendAdapter(arrlist, SearchProductNewActivity.this, bitmapUtils);
      gridView.setAdapter(mAdapter);
   }

   private void initListener() {

      searchBar.setOnChangeListener(new EditTextWithDel.OnChangeListener() {
         @Override
         public void afterTextChanged(Editable s) {
            name = s.toString();
            onRefresh();
            if (!TextUtils.isEmpty(name)) {
               linearLayout.setVisibility(View.GONE);

            } else {
               linearLayout.setVisibility(View.GONE);
            }
         }
      });
      adapter = new CustomBaseAdapter<String>(CustomApplication.getCustomApplication().getSearchList(), R.layout.activity_searchproduct_item) {
         @Override
         public void bindView(ViewHolder holder, String obj) {
            TextView textView = holder.getView(R.id.search_name);
            textView.setText(obj);
         }
      };
      listView.setAdapter(adapter);
      clearButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            CustomApplication.getCustomApplication().getSearchList().clear();
            adapter.notifyDataSetChanged();
            linearLayout.setVisibility(View.GONE);
         }
      });
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            name = CustomApplication.getCustomApplication().getSearchList().get(position);
            searchBar.setText(name);
         }
      });
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
         }
      });

   }


   @Override
   public void onRefresh() {
      page = 1;
      firstQueryTime = "";
      refreshLayout.setRefreshing(true);
      loadShopData();

   }

   @Override
   public void onLoad() {
      page++;
      refreshLayout.setLoading(true);
      loadShopData();
   }


   private void loadShopData() {
      // TODO Auto-generated method stub
      ProductListRequestObject object = new ProductListRequestObject();
      object.setParam(new ProductListRequestParam());
      object.getParam().setName(name);
      object.getParam().setType("0");
      object.getParam().setStatus("0");//商品类型：0 销售中 1已下架
      object.getParam().setSortType("2");//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(CustomConfig.PAGE_SIZE);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else if (page != 1) {
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
                     /**
                      * 分页
                      */
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }

                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getProList());

                     mAdapter.notifyDataSetChanged();
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

   @Override
   public void onStart() {
      super.onStart();
      if (flag == 1) {
         sortType = "1";
         onRefresh();
      }
   }


}
