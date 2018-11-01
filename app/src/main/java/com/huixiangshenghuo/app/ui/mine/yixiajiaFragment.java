package com.huixiangshenghuo.app.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/6.
 */

public class yixiajiaFragment extends android.support.v4.app.Fragment implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   private RefreshLayout refreshLayout;
   private ListView listView;
   /**
    * 适配器
    */
   private yixiajiaAdapter adapter;
   //数据源
//   private List<City> list;
   //数据源
   private ArrayList<ProductListResponseParam> arrlist = new ArrayList<ProductListResponseParam>();
   private BitmapUtils bitmapUtils;
   protected HttpTool httpTool;
   private int page = 1;
   private String firstQueryTime;

   View view;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_yixiajia, container, false);
      httpTool = HttpTool.newInstance(getActivity());
      ShopSelesStatic.setShopSeles_static("2");
      initView();
      return view;

   }

   private void initView() {
      refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh_yixiajia);
      listView = (ListView) view.findViewById(R.id.lv_yixiajia);
      initBitmapParames();
      initData();
      //构建适配器
      adapter = new yixiajiaAdapter(arrlist, getActivity(), bitmapUtils);
      listView.setAdapter(adapter);
      request();
   }
   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(getActivity());
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
   }

   @Override
   public void onResume() {
      super.onResume();
      if (ShopSelesStatic.getShopSeles_static().equals("3")) {
         page = 1;
         refreshLayout.setRefreshing(true);
         request();
         ShopSelesStatic.getShopSeles_static().equals("2");
      }
   }

   /**
    * 初始化数据源
    */
   public void initData() {
      refreshLayout.setLoading(false);
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);

/*      list = new ArrayList<City>();
      City city1 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city2 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city3 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city4 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city5 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city6 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city7 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city8 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      City city9 = new City("煲仔饭2", "库存128", "月销3620", R.mipmap.icon, "￥38");
      list.add(city1);
      list.add(city2);
      list.add(city3);
      list.add(city4);
      list.add(city5);
      list.add(city6);
      list.add(city7);
      list.add(city8);
      list.add(city9);*/
   }

   //查看列表
   private void request() {
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      ProductListRequestObject productListRequestObject = new ProductListRequestObject();
      productListRequestObject.setParam(new ProductListRequestParam());
      productListRequestObject.getParam().setType("0");
      productListRequestObject.getParam().setStatus("1");//查询类别	是	Char(1)	商品类型：0 销售中 1已上架
      productListRequestObject.getParam().setShopId(userInfo.getShopId());
      productListRequestObject.setPagination(new PaginationBaseObject());
      productListRequestObject.getPagination().setPage(page);//第一页
      productListRequestObject.getPagination().setRows(10);//每一页10行

      if (page == 1) {
         productListRequestObject.getPagination().setFirstQueryTime("");
      } else {

         productListRequestObject.getPagination().setFirstQueryTime(firstQueryTime);
      }
      httpTool.post(productListRequestObject, URLConfig.SHOP_QUERY, new HttpTool.HttpCallBack<ProductListResponseObject>() {
         @Override
         public void onSuccess(ProductListResponseObject o) {
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

               adapter.notifyDataSetChanged();
            }

         }

         @Override
         public void onError(ProductListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
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
