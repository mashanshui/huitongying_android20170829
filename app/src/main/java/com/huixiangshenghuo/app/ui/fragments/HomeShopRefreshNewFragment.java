package com.huixiangshenghuo.app.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import com.baidu.location.BDLocation;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homemall.ProductListNewActivity;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.MyViewPagerAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.NearbyShopAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.LocationActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.SearchProductActivity;
import com.huixiangshenghuo.app.view.BannerViewImageHolder;
import com.huixiangshenghuo.app.view.DisplayUtil;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshScrollviewLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.ad.AdRequestObject;
import com.doumee.model.request.ad.AdRequestParam;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.category.CategoryListRequestObject;
import com.doumee.model.request.category.CategoryListRequestParam;
import com.doumee.model.request.shop.ShopListRequestObject;
import com.doumee.model.request.shop.ShopListRequestParam;
import com.doumee.model.response.ad.AdListResponseObject;
import com.doumee.model.response.ad.AdListResponseParam;
import com.doumee.model.response.category.CategoryListResponseObject;
import com.doumee.model.response.category.CategoryListResponseParam;
import com.doumee.model.response.shop.ShopListResponseObject;
import com.doumee.model.response.shop.ShopListResponseParam;
import com.doumee.model.response.userinfo.CityResponseParam;
import com.doumee.model.response.userinfo.ProvinceResponseObject;
import com.doumee.model.response.userinfo.ProvinceResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */

public class HomeShopRefreshNewFragment extends Fragment implements RefreshScrollviewLayout.OnRefreshListener,
      RefreshScrollviewLayout.OnLoadListener {
   private static final String ARG_PARAM1 = "sortType";
   private String sortType;
   private HttpTool httpTool;
   private BaiduLocationTool baiduLocationTool;
   private SimpleDateFormat simpleDateFormat;
   View view;


   private TextView cityName;//地址
   private TextView search_bar;//搜索商家
   private RelativeLayout titleRelativeLayout;
   private LinearLayout ll_businessHead;
   private TextView moreView;//更多
   RefreshScrollviewLayout refreshLayout;

   //广告
   private ConvenientBanner cb_adsLyt;
   private List<AdListResponseParam> ads;

   //商品分类
   private ViewPager vp_menu;//商品分类
   private RadioGroup iv_group;//商品分类 小圆点
   private List<View> pagerView;

   //附近商品
   private MyListView listView;
   private int page = 1;
   private String querytime = "";
   private ArrayList<ShopListResponseParam> arrlist = new ArrayList<ShopListResponseParam>();
   private NearbyShopAdapter mAdapter;
   private double lat;
   private double lng;
   private String cityId = "";
   public static final String REFRESH_CITY = "com.doumee.refresh.city";
   private RefCityBroadcastReceiver refCityBroadcastReceiver;

   private SharedPreferences sharedPreferences;

   public HomeShopRefreshNewFragment() {

   }

   public static HomeShopRefreshNewFragment newInstance(String param1) {
      HomeShopRefreshNewFragment fragment = new HomeShopRefreshNewFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         sortType = getArguments().getString(ARG_PARAM1);
      }
      sharedPreferences = CustomApplication.getAppUserSharedPreferencesCity();
      baiduLocationTool = BaiduLocationTool.newInstance(getActivity());
      refCityBroadcastReceiver = new RefCityBroadcastReceiver();
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(HomeShopRefreshNewFragment.REFRESH_CITY);
      getActivity().registerReceiver(refCityBroadcastReceiver, intentFilter);
      simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      httpTool = HttpTool.newInstance(getActivity());

      view = inflater.inflate(R.layout.fragment_home_shop_refresh_new, container, false);
      pagerView = new ArrayList<View>();
      initview();
      requestAds();
      loadType();
      refresh();
      libCity();
      return view;
   }

   private void initview() {
      listView = (MyListView) view.findViewById(R.id.list_view_new);
      cityName = (TextView) view.findViewById(R.id.tv_home_add_new);
      search_bar = (TextView) view.findViewById(R.id.search_bar_new);

      refreshLayout = (RefreshScrollviewLayout) view.findViewById(R.id.rl_sx_home_shop_refresh_new);
      cb_adsLyt = (ConvenientBanner) view.findViewById(R.id.fh_ads_lyt_new);
      vp_menu = (ViewPager) view.findViewById(R.id.vp_menu_new);
      iv_group = (RadioGroup) view.findViewById(R.id.iv_group_new);
      iv_group.requestFocus();
      listView.requestFocus();
//      //头部
//      View headView = View.inflate(getActivity(), R.layout.fragment_home_shop_refresh_top_new, null);
//      cb_adsLyt = (ConvenientBanner) headView.findViewById(R.id.fh_ads_lyt_new);
//      vp_menu = (ViewPager) headView.findViewById(R.id.vp_menu_new);
//      iv_group = (RadioGroup) headView.findViewById(R.id.iv_group_new);
//
//      listView.addHeaderView(headView);


      search_bar.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            SearchProductActivity.startSearchProductActivity(getActivity(), 0);
         }
      });
      cityName.setOnClickListener(new View.OnClickListener() {//城市地址
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(getActivity(), LocationActivity.class);//
            startActivity(intent);
         }
      });

   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);
      mAdapter = new NearbyShopAdapter(arrlist, getActivity());
      listView.setAdapter(mAdapter);
   }

   private class RefCityBroadcastReceiver extends BroadcastReceiver {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         if (TextUtils.equals(action, REFRESH_CITY)) {
            page = 1;
            querytime = "";
            arrlist.clear();
            UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
            String city = user.getCityId();
            cityId = city;
            cityName.setText(user.getCityName());
            loadShopData();
         }
      }
   }

   /**
    * 广告
    */
   private void requestAds() {
      AdRequestParam adRequestParam = new AdRequestParam();
      adRequestParam.setPosition("2");//广告位置 0首页广告 1商城 2商圈页广告
      AdRequestObject adRequestObject = new AdRequestObject();
      adRequestObject.setParam(adRequestParam);
      httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
         @Override
         public void onSuccess(AdListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            ads = o.getAdLst();
            if (ads != null) {

               cb_adsLyt.setPages(new CBViewHolderCreator<BannerViewImageHolder>() {
                  @Override
                  public BannerViewImageHolder createHolder() {
                     return new BannerViewImageHolder();
                  }
               }, ads).setPageIndicator(new int[]{R.drawable.bg_dot_gray, R.drawable.bg_dot_blue});
            }

            if (o.getAdLst().size() <= 0) {
               cb_adsLyt.setVisibility(View.GONE);
            } else {
               cb_adsLyt.setVisibility(View.VISIBLE);
            }


         }

         @Override
         public void onError(AdListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   /**
    * 商品一级菜单列表
    */
   private void loadType() {
      CategoryListRequestParam categoryListRequestParam = new CategoryListRequestParam();
      categoryListRequestParam.setType("0");//查询对象类型 0 商家 1商品
      CategoryListRequestObject categoryListRequestObject = new CategoryListRequestObject();
      categoryListRequestObject.setParam(categoryListRequestParam);

      httpTool.post(categoryListRequestObject, URLConfig.GOODS_MENU, new HttpTool.HttpCallBack<CategoryListResponseObject>() {
         @Override
         public void onSuccess(CategoryListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            List<CategoryListResponseParam> recordList = o.getRecordList();
            initTypeView(recordList);
         }

         @Override
         public void onError(CategoryListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   private void initTypeView(List<CategoryListResponseParam> recordList) {
      pagerView.clear();
      iv_group.removeAllViews();
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
         vp_menu.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vp_weight_0)));//将设置好的布局参数应用到控件中

      } else if (size > 5) {
         //dp转px
         int vp_weight_1 = DisplayUtil.dip2px(getActivity(), 136);
//      LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) vp_classification.getLayoutParams();
//获取当前控件的布局对象
//      params.height=100;//设置当前控件布局的高度
         vp_menu.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vp_weight_1)));//将设置好的布局参数应用到控件中
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
      vp_menu.setAdapter(pagerAdapter);
      vp_menu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
            iv_group.check(position);
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
            // ProductListActivity.startProductListActivity(getActivity(), info.getCateId(), info.getCateName());
            ProductListNewActivity.startProductListNewActivity(getActivity(), info.getCateId(), info.getCateName());
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
      iv_group.addView(radioButton);
   }

   private void loadShopData() {
      ShopListRequestParam shopListRequestParam = new ShopListRequestParam();
      shopListRequestParam.setName("");
      shopListRequestParam.setSortType(sortType);
      shopListRequestParam.setCityId(cityId);
      shopListRequestParam.setLat(lat);
      shopListRequestParam.setLng(lng);
      shopListRequestParam.setCateId("");
      shopListRequestParam.setParentCateId("");
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);

      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(querytime);
      }

      ShopListRequestObject shopListRequestObject = new ShopListRequestObject();
      shopListRequestObject.setPagination(paginationBaseObject);
      shopListRequestObject.setParam(shopListRequestParam);

      httpTool.post(shopListRequestObject, URLConfig.SHOP_LIST, new HttpTool.HttpCallBack<ShopListResponseObject>() {
         @Override
         public void onSuccess(ShopListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  if (o != null && o.getRecordList() != null) {
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }
                     querytime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());
                     mAdapter.notifyDataSetChanged();
//                     if (o.getRecordList().size()<=0) {
//                        listView.setBackgroundResource(R.mipmap.gwc_default);
//                     } else {
//                        listView.setBackgroundResource(0);
//                     }
                  }
               }
            }


         }

         @Override
         public void onError(ShopListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   /**
    * 定位
    */
   private void libCity() {
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            baiduLocationTool.stopLocation();
            if (null != location) {
               lat = location.getLatitude();
               lng = location.getLongitude();
               String city = location.getCity();
               loadCity(city);
            } else {
//                    ToastView.show("定位失败,请选择城市");
               loadCity("");
            }
         }
      });
   }

   /**
    * 查看 用户是否有 默认的城市id
    */
   private void loadCity(String name) {
//      UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
//      String city = user.getCityId();
      String city = sharedPreferences.getString(CustomConfig.CITY_ID, "");
      String cityName = sharedPreferences.getString(CustomConfig.CITY_NAME, "");

      if (!TextUtils.isEmpty(city)) {
         //用户有默认的城市
//         cityId = city;
//         cityName.setText(user.getCityName());
         initCityData(cityName);
      } else {
         initCityData(name);
      }
   }

   /**
    * 去查看城市列表 是否有该城市
    */
   private void initCityData(final String name) {
      if (name.equals("")) {
         cityName.setText("选择城市");
         loadShopData();
         return;
      }
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceResponseObject>() {
         @Override
         public void onSuccess(ProvinceResponseObject o) {
            List<ProvinceResponseParam> lstProvince = o.getLstProvince();
            for (ProvinceResponseParam provinceResponseParam : lstProvince) {
               List<CityResponseParam> lstCity = provinceResponseParam.getLstCity();
               for (CityResponseParam cityResponseParam : lstCity) {
                  String city = cityResponseParam.getCityName();
                  try {

                     if (city.contains(name)) {
                        //城市列表有该城市
                        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                        user.setCityId(cityResponseParam.getCityId());
                        user.setCityName(cityResponseParam.getCityName());
                        SaveObjectTool.saveObject(user);
                        cityId = cityResponseParam.getCityId();
                        cityName.setText(user.getCityName());
//                        loadShopData();
                        break;
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }

            loadShopData();
         }

         @Override
         public void onError(ProvinceResponseObject o) {
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);


      refreshLayout.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新

            page = 1;
            loadType();
            requestAds();
//      refresh();
            libCity();
         }
      }, 2000);


   }

   @Override
   public void onLoad() {
      refreshLayout.setLoading(true);

      refreshLayout.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新
            page++;
//            loadType();
//      refresh();
            libCity();
         }
      }, 2000);



   }
}
