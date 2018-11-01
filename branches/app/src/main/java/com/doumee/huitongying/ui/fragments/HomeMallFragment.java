package com.huixiangshenghuo.app.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homemall.GoodsListActivity;
import com.huixiangshenghuo.app.activity.homepage.ProductsDetailsNewActivity;
import com.huixiangshenghuo.app.activity.homepage.ShopCartActivity;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.MyViewPagerAdapter;
import com.huixiangshenghuo.app.adapter.homemall.LatestShelvesAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.model.ShopcartListObject;
import com.huixiangshenghuo.app.ui.activityshopcircle.SearchProductNewActivity;
import com.huixiangshenghuo.app.view.BannerViewImageHolder;
import com.huixiangshenghuo.app.view.DisplayUtil;
import com.huixiangshenghuo.app.view.MyGridView;
import com.huixiangshenghuo.app.view.RefreshScrollviewLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.ad.AdRequestObject;
import com.doumee.model.request.ad.AdRequestParam;
import com.doumee.model.request.category.CategoryListRequestObject;
import com.doumee.model.request.category.CategoryListRequestParam;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.request.shopcart.ShopcartListRequestObject;
import com.doumee.model.response.ad.AdListResponseObject;
import com.doumee.model.response.ad.AdListResponseParam;
import com.doumee.model.response.category.CategoryListResponseObject;
import com.doumee.model.response.category.CategoryListResponseParam;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 * 商城
 */

public class HomeMallFragment extends Fragment implements View.OnClickListener, RefreshScrollviewLayout.OnRefreshListener,
      RefreshScrollviewLayout.OnLoadListener {
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";
   private HttpTool httpTool;
   BitmapUtils bitmapUtils;
   View view;
   private RefreshScrollviewLayout rl_sx;//刷新控件


   //搜索商品
   private TextView search_bar;

   //购物车
   private RelativeLayout rl_gwc;//购物车
   private ImageView fo_cart_dot_img;

   //商家一级分类
   private ViewPager vp_classification;//商品分类
   private List<View> pagerView;
   private RadioGroup pageGroup;
   //广告
   private ConvenientBanner cb_gg;//广告栏
   private List<AdListResponseParam> ads;
   //最新上架
   private MyGridView gv_latest_shelves;
   //   private HeaderGridView gv_latest_shelves;
   private LatestShelvesAdapter re_adapter;//适配器
   private ArrayList<ProductListResponseParam> arrlist = new ArrayList<ProductListResponseParam>();//数据源
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间


   private boolean isVisibleHint = true;

   public static HomeMallFragment newInstance(String param1, String param2) {
      HomeMallFragment fragment = new HomeMallFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      pagerView = new ArrayList<View>();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      httpTool = HttpTool.newInstance(getActivity());

      view = inflater.inflate(R.layout.fragment_home_mall, container, false);

      initview();

      refresh();
      initBitmapParames();
//      requestShopcartList();
      requestAds();
      loadType();
      recommendrequest();
      return view;
   }

   private void initview() {
      rl_sx = (RefreshScrollviewLayout) view.findViewById(R.id.rl_sx_home_mall);
      search_bar = (TextView) view.findViewById(R.id.search_bar);
      rl_gwc = (RelativeLayout) view.findViewById(R.id.rl_gwc_home_mall);
      fo_cart_dot_img = (ImageView) view.findViewById(R.id.fo_cart_dot_img);


      gv_latest_shelves = (MyGridView) view.findViewById(R.id.gv_latest_shelves_home_mall);

      cb_gg = (ConvenientBanner) view.findViewById(R.id.cb_gg_home_mall);
      vp_classification = (ViewPager) view.findViewById(R.id.vp_classification_home_mall);
      pageGroup = (RadioGroup) view.findViewById(R.id.iv_group_home_mall);
//      pageGroup.requestFocus();
//      gv_latest_shelves.requestFocus();
      gv_latest_shelves.setFocusable(false);
//      //头部
//      View headView = View.inflate(getActivity(), R.layout.fragment_home_top_mall, null);
//
//      cb_gg = (ConvenientBanner) headView.findViewById(R.id.cb_gg_home_mall);
//      vp_classification = (ViewPager) headView.findViewById(R.id.vp_classification_home_mall);
//      pageGroup = (RadioGroup) headView.findViewById(R.id.iv_group_home_mall);
//
//      gv_latest_shelves.addHeaderView(headView);

      search_bar.setOnClickListener(this);
      rl_gwc.setOnClickListener(this);

      //dp转px
      int vp_weight = DisplayUtil.dip2px(getActivity(), 100);
//      LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) vp_classification.getLayoutParams();
//获取当前控件的布局对象
//      params.height=100;//设置当前控件布局的高度
      vp_classification.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vp_weight)));//将设置好的布局参数应用到控件中

   }


//   @Override
//   public void setUserVisibleHint(boolean isVisibleToUser) {
//      super.setUserVisibleHint(isVisibleToUser);
//      if (isVisibleToUser) {
//         //   相当于Fragment的onResume
//         if (isVisibleHint == false) {
//
//            requestShopcartList();
//         }
//
//
//      } else {
//         //相当于Fragment的onPause
//
//      }
//   }


   /**
    * 图片加载
    */
   private void initBitmapParames() {
      bitmapUtils = new BitmapUtils(getActivity());
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
      re_adapter = new LatestShelvesAdapter(arrlist, getActivity(), bitmapUtils);
      gv_latest_shelves.setAdapter(re_adapter);
      gv_latest_shelves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ProductsDetailsActivity.startProductsDetailsActivity(getActivity(), arrlist.get(position).getProId());
            //HeaderGridView  position 要向前移动2位
            try {
//               ProductsDetailsActivity.startProductsDetailsActivity(getActivity(), arrlist.get(position - 2).getProId());
               ProductsDetailsNewActivity.startProductsDetailsNewActivity(getActivity(), arrlist.get(position).getProId());
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   private void refresh() {
      // TODO Auto-generated method stub
      rl_sx.setLoading(false);
      rl_sx.setRefreshing(false);
      rl_sx.setOnRefreshListener(this);
      rl_sx.setOnLoadListener(this);

   }

   //广告
   private void requestAds() {
      AdRequestParam adRequestParam = new AdRequestParam();
      adRequestParam.setPosition("1");//广告位置 0首页广告 1商城 2商圈页广告
      AdRequestObject adRequestObject = new AdRequestObject();
      adRequestObject.setParam(adRequestParam);
      httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
         @Override
         public void onSuccess(AdListResponseObject o) {
//            rl_sx.setLoading(false);
//            rl_sx.setRefreshing(false);
            ads = o.getAdLst();
            if (ads != null) {

               cb_gg.setPages(new CBViewHolderCreator<BannerViewImageHolder>() {
                  @Override
                  public BannerViewImageHolder createHolder() {
                     return new BannerViewImageHolder();
                  }
               }, ads).setPageIndicator(new int[]{R.drawable.bg_dot_gray, R.drawable.bg_dot_blue});
            }

            if (o.getAdLst().size() <= 0) {
               cb_gg.setVisibility(View.GONE);
            } else {
               cb_gg.setVisibility(View.VISIBLE);
            }

         }

         @Override
         public void onError(AdListResponseObject o) {
//            rl_sx.setLoading(false);
//            rl_sx.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      cb_gg.startTurning(5000);

      requestShopcartList();

   }

   @Override
   public void onPause() {
      super.onPause();
      cb_gg.stopTurning();

   }

   //***************************商品分类*********************************//

   /**
    * 商品一级菜单列表
    */
   private void loadType() {
      CategoryListRequestParam categoryListRequestParam = new CategoryListRequestParam();
      categoryListRequestParam.setType("1");//查询对象类型 0 商家 1商品
      CategoryListRequestObject categoryListRequestObject = new CategoryListRequestObject();
      categoryListRequestObject.setParam(categoryListRequestParam);

      httpTool.post(categoryListRequestObject, URLConfig.GOODS_MENU, new HttpTool.HttpCallBack<CategoryListResponseObject>() {
         @Override
         public void onSuccess(CategoryListResponseObject o) {
//            rl_sx.setLoading(false);
//            rl_sx.setRefreshing(false);
            List<CategoryListResponseParam> recordList = o.getRecordList();
            initTypeView(recordList);
         }

         @Override
         public void onError(CategoryListResponseObject o) {

            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   private void initTypeView(List<CategoryListResponseParam> recordList) {
      pagerView.clear();
      pageGroup.removeAllViews();
      int size = recordList.size();
      int rows = CustomConfig.PAGE_SIZE;
      int pageSize = size / rows;

      //=============================================================
      //设置viewpager 高度
      if (size <= 5 && size > 0) {
         //dp转px
         int vp_weight_0 = DisplayUtil.dip2px(getActivity(), 69);
//      LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) vp_classification.getLayoutParams();
//获取当前控件的布局对象
//      params.height=100;//设置当前控件布局的高度
         vp_classification.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vp_weight_0)));//将设置好的布局参数应用到控件中

      } else if (size > 5) {
         //dp转px
         int vp_weight_1 = DisplayUtil.dip2px(getActivity(), 136);
//      LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) vp_classification.getLayoutParams();
//获取当前控件的布局对象
//      params.height=100;//设置当前控件布局的高度
         vp_classification.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vp_weight_1)));//将设置好的布局参数应用到控件中
      }

      //=============================================================
      int y = size - (pageSize * rows);
      if (y > 0) {
         pageSize += 1;
      }
      for (int i = 0; i < pageSize; i++) {
         View view = View.inflate(getActivity(), R.layout.viewpager_one, null);
         GridView gridView = (GridView) view.findViewById(R.id.gv_one);
         ArrayList<CategoryListResponseParam> dateList = new ArrayList<>();
         for (int j = i * rows; j < size && j < (i * rows) + rows; j++) {
            dateList.add(recordList.get(j));
         }
         initTypeGrid(gridView, dateList);
         if (pageSize > 1)
            addTypeItem(i);
         pagerView.add(view);
      }

      MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(getActivity(), pagerView);
      vp_classification.setAdapter(pagerAdapter);
      vp_classification.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
            pageGroup.check(position);
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
      });
   }

   private void initTypeGrid(GridView gridView, final ArrayList<CategoryListResponseParam> recordList) {
      CustomBaseAdapter<CategoryListResponseParam> adapter = new CustomBaseAdapter<CategoryListResponseParam>(recordList, R.layout.grildview_adapter_item) {
         @Override
         public void bindView(ViewHolder holder, CategoryListResponseParam obj) {
            ImageView imageView = holder.getView(R.id.iv_menu);
            TextView textView = holder.getView(R.id.tv_menu);
            textView.setText(obj.getCateName());
            String icon = obj.getIcon();
            if (!TextUtils.isEmpty(icon)) {
               ImageLoader.getInstance().displayImage(obj.getIcon(), imageView);
            } else {
               Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
               imageView.setImageBitmap(bitmap);
            }
         }
      };
      gridView.setAdapter(adapter);
      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CategoryListResponseParam info = recordList.get(position);
            //        Toast.makeText(getActivity(),"你点击了商品分类",Toast.LENGTH_SHORT).show();
            GoodsListActivity.startGoodsListActivity(getActivity(), info.getCateId(), info.getCateName());
         }
      });
   }

   private void addTypeItem(int id) {
      RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.setMargins(10, 0, 0, 0);
      RadioButton radioButton = new RadioButton(getActivity());
      radioButton.setLayoutParams(layoutParams);
      radioButton.setId(id);
      if (id == 0) radioButton.setChecked(true);
      Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.vipager_doc_select);
      radioButton.setButtonDrawable(drawable);
      pageGroup.addView(radioButton);
   }

   @Override
   public void onRefresh() {
      rl_sx.setRefreshing(true);


      rl_sx.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新

            page = 1;
            requestAds();
            requestShopcartList();

            loadType();
            recommendrequest();
         }
      }, 2000);

   }

   @Override
   public void onLoad() {
      rl_sx.setLoading(true);


      rl_sx.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新
            page++;
            recommendrequest();
         }
      }, 2000);

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.search_bar:
//            SearchProductActivity.startSearchProductActivity(getActivity(), 0);
            SearchProductNewActivity.startSearchProductNewActivity(getActivity(), 0);

            break;
         case R.id.rl_gwc_home_mall:
            startActivity(new Intent(getActivity(), ShopCartActivity.class));
            break;
      }
   }

   //***************************商品分类结束*********************************//

   /**
    * 最新上架 商品排序
    */
   private void recommendrequest() {
      // TODO Auto-generated method stub
      ProductListRequestObject object = new ProductListRequestObject();
      object.setParam(new ProductListRequestParam());
      object.getParam().setType("0");//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序
      object.getParam().setSortType("7");//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序
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
                  rl_sx.setLoading(false);
                  rl_sx.setRefreshing(false);
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

                     re_adapter.notifyDataSetChanged();
                  }

               }
            }


         }

         @Override
         public void onError(ProductListResponseObject o) {
            rl_sx.setLoading(false);
            rl_sx.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });


   }

   //购物车列表
   private void requestShopcartList() {


      ShopcartListRequestObject requestObject = new ShopcartListRequestObject();
      httpTool.post(requestObject, URLConfig.CART_LIST, new HttpTool.HttpCallBack<ShopcartListObject>() {
         @Override
         public void onSuccess(ShopcartListObject resp) {
//            rl_sx.setLoading(false);
//            rl_sx.setRefreshing(false);
            isVisibleHint = false;
            if (resp.getRecordList() != null && resp.getRecordList().size() > 0) {
               fo_cart_dot_img.setVisibility(View.VISIBLE);
            } else {
               fo_cart_dot_img.setVisibility(View.GONE);
            }


         }

         @Override
         public void onError(ShopcartListObject shopcartListObject) {
//            rl_sx.setLoading(false);
//            rl_sx.setRefreshing(false);
            isVisibleHint = false;
            ToastView.show(shopcartListObject.getErrorMsg());

         }


      });
   }


}
