package com.huixiangshenghuo.app.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.huixiangshenghuo.app.view.BannerView;
import com.huixiangshenghuo.app.view.BannerViewImageHolder;
import com.huixiangshenghuo.app.view.ToastView;
import com.huixiangshenghuo.app.view.XListView;
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
import java.util.Date;
import java.util.List;


public class HomeShopRefreshFragment extends Fragment implements XListView.IXListViewListener {

    private static final String ARG_PARAM1 = "sortType";

    private String sortType;
    private String mParam2;
    private XListView listView;
    private TextView cityName;//地址
    private TextView search_bar;
    private RelativeLayout titleRelativeLayout;
    private LinearLayout ll_businessHead;
    private BannerView bannerView;
    private TextView moreView;

    private ViewPager vp_menu;
    private List<View> pagerView;
    private RadioGroup pageGroup;
    private LinearLayout ll_gaodu;

    private HttpTool httpTool;
    private ArrayList<AdListResponseParam> bannerList;
    //广告
    private ConvenientBanner adsLyt;
    private List<AdListResponseParam> ads;

    private int page = 1;
    private String querytime = "";
    private ArrayList<ShopListResponseParam> arrlist = new ArrayList<ShopListResponseParam>();
    private NearbyShopAdapter mAdapter;
    private BaiduLocationTool baiduLocationTool;
    private double lat;
    private double lng;
    private String cityId;
    private SimpleDateFormat simpleDateFormat;

    public static final String REFRESH_CITY = "com.doumee.refresh.city";
    private RefCityBroadcastReceiver refCityBroadcastReceiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x20:
                    BDLocation location = (BDLocation) msg.obj;
                    if (null != location) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        String city = location.getCity();
                        if (city != null) {
                            loadCity(city);
                        }
                    } else {
                        ToastView.show("定位失败,请选择城市");
                        loadShopData();
                    }
                    break;
            }
        }
    };

    public HomeShopRefreshFragment() {

    }


    public static HomeShopRefreshFragment newInstance(String param1) {
        HomeShopRefreshFragment fragment = new HomeShopRefreshFragment();
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
        baiduLocationTool = BaiduLocationTool.newInstance(getActivity());
        refCityBroadcastReceiver = new RefCityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HomeShopFragment.REFRESH_CITY);
        getActivity().registerReceiver(refCityBroadcastReceiver, intentFilter);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        httpTool = HttpTool.newInstance(getActivity());
        bannerList = new ArrayList<>();
        pagerView = new ArrayList<View>();
        mAdapter = new NearbyShopAdapter(arrlist, getActivity());
        View view = inflater.inflate(R.layout.fragment_home_shop_refresh, container, false);

        search_bar = (TextView) view.findViewById(R.id.search_bar);
        cityName = (TextView) view.findViewById(R.id.tv_home_add);
        listView = (XListView)view.findViewById(R.id.list_view);
        titleRelativeLayout = (RelativeLayout)view.findViewById(R.id.rl_businessHead) ;
        ll_businessHead = (LinearLayout) view.findViewById(R.id.ll_businessHead);
        initView();
        requestAds();
        onRefresh();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private int alpha = 0;
    private void initView(){
//        titleRelativeLayout.setAlpha(0);
        titleRelativeLayout.getBackground().mutate().setAlpha(0);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                View itemView = listView.getChildAt(0);
//                int firstVisiblePosition = firstVisibleItem;
//                int t =   firstVisiblePosition * itemView.getHeight() ;
//                if (t <= 332) {
//                    alpha+=10;
//                    if (t == 0)alpha=0;
//                    titleRelativeLayout.setAlpha(alpha / 300f);
//                } else {
//                    alpha = 0;
//                    titleRelativeLayout.setAlpha(1);
//                }
                // 判断当前最上面显示的是不是头布局，因为Xlistview有刷新控件，所以头布局的位置是1，即第二个
                if (firstVisibleItem == 1) {
                    // 获取头布局
                    View itemView = listView.getChildAt(0);
                    if (itemView != null) {
                        // 获取头布局现在的最上部的位置的相反数
                        int top = -itemView.getTop();
                        // 获取头布局的高度
                        int headerHeight = itemView.getHeight();
                        // 满足这个条件的时候，是头布局在XListview的最上面第一个控件的时候，只有这个时候，我们才调整透明度
                        if (top <= headerHeight && top >= 0) {
                            // 获取当前位置占头布局高度的百分比
                            float f = (float) top / (float) headerHeight;
                            titleRelativeLayout.getBackground().mutate().setAlpha((int) (f * 255));
                            // 通知标题栏刷新显示
                            titleRelativeLayout.invalidate();
                        }
                    }
                } else if (firstVisibleItem > 1) {
                    titleRelativeLayout.getBackground().mutate().setAlpha(255);
                } else {
                    titleRelativeLayout.getBackground().mutate().setAlpha(0);
                }
            }
        });

        listView.setXListViewListener(this);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);

        View headView = View.inflate(getActivity(),R.layout.fragment_home_shop_refresh_header,null);
        View headBannerView = View.inflate(getActivity(),R.layout.fragment_home_shop_refresh_banner,null);
        bannerView = (BannerView) headBannerView.findViewById(R.id.view_banner);
        adsLyt = (ConvenientBanner) headBannerView.findViewById(R.id.fh_ads_lyt2);
        vp_menu = (ViewPager) headView.findViewById(R.id.vp_menu);
        pageGroup = (RadioGroup) headView.findViewById(R.id.iv_group);
        ll_gaodu = (LinearLayout) headView.findViewById(R.id.ll_home_shop_refresh_heander);
        moreView = (TextView)headView.findViewById(R.id.more);
        listView.addHeaderView(headBannerView);
        listView.addHeaderView(headView);
        listView.setAdapter(mAdapter);

        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchProductActivity.startSearchProductActivity(getActivity(),0);
            }
        });
        cityName.setOnClickListener(new View.OnClickListener() {//城市地址
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);//
                startActivity(intent);
            }
        });
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchProductActivity.startSearchProductActivity(getActivity(),1);
            }
        });
    }


    @Override
    public void onRefresh() {
        page = 1;
        querytime = "";
        arrlist.clear();
        loadType();
        libCity();
        requestAds();
    }

    @Override
    public void onLoadMore() {
        page ++;
        loadShopData();
    }

    private class RefCityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, REFRESH_CITY)) {
                page = 1;
                querytime = "";
                listView.setPullLoadEnable(true);
                arrlist.clear();
                UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                String city = user.getCityId();
                cityId = city;
                cityName.setText(user.getCityName());
                loadShopData();
            }
        }
    }

    //*********************首页广告开始*************************//
    //加载首页广告
//    private void loadServerAD() {
//        AdRequestParam adRequestParam = new AdRequestParam();
//        adRequestParam.setPosition("1");//广告位置 0首页广告 1商家页广告
//        AdRequestObject adRequestObject = new AdRequestObject();
//        adRequestObject.setParam(adRequestParam);
//        httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
//            @Override
//            public void onSuccess(AdListResponseObject o) {
//                List<AdListResponseParam> adLst = o.getAdLst();
//                bannerList.clear();
//                bannerList.addAll(adLst);
//                //===========================================
//                if (bannerList.size() > 0) { //当有广告时候，广告控件显示
//                    bannerView.setVisibility(View.VISIBLE);
//                } else {
//                    bannerView.setVisibility(View.GONE);
//                }
//                if (bannerList.size() > 0) {//当有广告时候，一级列表 距离 顶部没有距离
//                    ll_gaodu.setVisibility(View.GONE);
////                    ll_businessHead.setAlpha(0);
//                } else {
//                    ll_businessHead.setBackgroundColor(getResources().getColor(R.color.colorAccent));//设置背景色
//                    ll_gaodu.setVisibility(View.VISIBLE);
//                }
//                //=========================================================================================
//                updateAd();
//            }
//
//            @Override
//            public void onError(AdListResponseObject o) {
//                Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateAd() {
//        if (!bannerList.isEmpty()) {
//            BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter<AdListResponseParam>(getActivity(), bannerList) {
//                @Override
//                public View setView(int position) {
//                    ImageView imageView = new ImageView(getActivity());
//                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                    imageView.setLayoutParams(layoutParams);
//                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//
//                    String path = bannerList.get(position).getImgurl();
//                    //          ImageLoader.getInstance().displayImage(path, imageView);
//                    //========================================================
//                    if (!TextUtils.isEmpty(path)) {
//                        ImageLoader.getInstance().displayImage(path, imageView);
//                    } else {
//                        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.business_default));//加载默认的
//                    }
//                    //=========================================================
//                    return imageView;
//                }
//            };
//
//            bannerView.setDotGravity(BannerView.CENTER).
//                    setDot(R.drawable.no_selected_dot, R.drawable.selected_dot);
//            bannerView.setAdapter(bannerPagerAdapter);
//            bannerPagerAdapter.setOnItemClickListener(new BannerPagerAdapter.onItemClickListener() {
//                @Override
//                public void onClick(int position) {
//                    AdListResponseParam adListResponseParam = bannerList.get(position);
//                    String type = adListResponseParam.getType();
//                    String content = adListResponseParam.getContent();
//                    String link = adListResponseParam.getLink();
//                    if (type.equals("0") || type.equals("1")) {//类型（0内容 1外链  2商家广告），默认0
//                        AdInfoActivity.startAdInfoActivity(getActivity(), type, content, link, adListResponseParam.getTitle());//0内容1外链接
//                    } else {
//                        if (adListResponseParam.getIsShopOnline().equals("0")) {//是否在线商家 0不是 1是
//                            ShopDetailsActivity.startShopDetailsActivity(getActivity(), adListResponseParam.getRecPrd());
//                        } else {
//                            ShopDetailsOnlineActivity.startShopActivity(getActivity(), adListResponseParam.getRecPrd(), "");
//
//                        }
//                    }
//                    //                AdInfoActivity.startAdInfoActivity(getActivity(), type, content, link, adListResponseParam.getTitle());//0内容1外链接
//                }
//            });
//            bannerView.startAutoPlay();
//        }
//    }


    /**
     * 广告
     */
    private void requestAds() {
        AdRequestParam adRequestParam = new AdRequestParam();
        adRequestParam.setPosition("1");//广告位置 0首页广告 1商家页广告
        AdRequestObject adRequestObject = new AdRequestObject();
        adRequestObject.setParam(adRequestParam);
        httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
            @Override
            public void onSuccess(AdListResponseObject o) {
                listView.stopLoadMore();
                listView.stopRefresh();
                ads = o.getAdLst();
                if (ads != null) {
                    adsLyt.setPages(new CBViewHolderCreator<BannerViewImageHolder>() {
                        @Override
                        public BannerViewImageHolder createHolder() {
                            return new BannerViewImageHolder();
                        }
                    }, ads).setPageIndicator(new int[]{R.drawable.bg_dot_gray, R.drawable.bg_dot_blue});
                }

            }

            @Override
            public void onError(AdListResponseObject o) {
                listView.stopLoadMore();
                listView.stopRefresh();
                Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //*********************首页广告结束*************************//
    //***************************商品分类*********************************//

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
                 listView.stopLoadMore();
                listView.stopRefresh();
                List<CategoryListResponseParam> recordList = o.getRecordList();
                initTypeView(recordList);
            }

            @Override
            public void onError(CategoryListResponseObject o) {
                listView.stopLoadMore();
                listView.stopRefresh();
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
        pageGroup.addView(radioButton);
    }

    //***************************商品分类结束*********************************//

    //***************************附近商家开始*********************************//
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
        //======================================
        if (TextUtils.isEmpty(querytime)) {
            paginationBaseObject.setPage(1);
        } else {
            paginationBaseObject.setPage(page);
        }
        //======================================
        //     paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
//        paginationBaseObject.setFirstQueryTime(querytime);
        //======================================
        if (page == 1) {
            paginationBaseObject.setFirstQueryTime("");
        } else {
            paginationBaseObject.setFirstQueryTime(querytime);
        }
        //======================================
        ShopListRequestObject shopListRequestObject = new ShopListRequestObject();
        shopListRequestObject.setPagination(paginationBaseObject);
        shopListRequestObject.setParam(shopListRequestParam);

        httpTool.post(shopListRequestObject, URLConfig.SHOP_LIST, new HttpTool.HttpCallBack<ShopListResponseObject>() {
            @Override
            public void onSuccess(ShopListResponseObject o) {
                //========================================
//                if (!arrlist.isEmpty()&&page==1) {
//                    //清空
//                    arrlist.clear();
//                }
                //=======================================
                listView.stopLoadMore();
                listView.stopRefresh();
                listView.setRefreshTime(simpleDateFormat.format(new Date()));
                querytime = o.getFirstQueryTime();
                arrlist.addAll(o.getRecordList());
                mAdapter.notifyDataSetChanged();
                if (arrlist.isEmpty()){
//                    ToastView.show("没有附近商家了");
                    listView.setPullLoadEnable(false);
                }else{
                    listView.setPullLoadEnable(true);
                }
            }
            @Override
            public void onError(ShopListResponseObject o) {
                listView.stopLoadMore();
                listView.stopRefresh();
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    /**
     * 定位
     */
    private void libCity() {
//        baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation location) {
//                baiduLocationTool.stopLocation();
//                if (null != location) {
//                    lat = location.getLatitude();
//                    lng = location.getLongitude();
//                    String city =  location.getCity();
//                    loadCity(city);
//                }else{
//                    ToastView.show("定位失败,请选择城市");
//                    loadShopData();
//                }
//            }
//        });
        //================================================================================
        baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                baiduLocationTool.stopLocation();
                Message message = Message.obtain();
                message.obj = location;
                message.what = 0x20;
                handler.sendMessage(message);
            }
        });


    }

    private void loadCity(String name) {
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        String city = user.getCityId();
        if (!TextUtils.isEmpty(city)){
            cityId = city;
            cityName.setText(user.getCityName());
            loadShopData();
        }else{
            initCityData(name);
        }
    }

    private void initCityData(final String name) {
        RequestBaseObject requestBaseObject = new RequestBaseObject();
        httpTool.post(requestBaseObject, URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceResponseObject>() {
            @Override
            public void onSuccess(ProvinceResponseObject o) {
                List<ProvinceResponseParam> lstProvince = o.getLstProvince();
                for (ProvinceResponseParam provinceResponseParam : lstProvince) {
                    List<CityResponseParam> lstCity = provinceResponseParam.getLstCity();
                    for (CityResponseParam cityResponseParam : lstCity){
                        String city = cityResponseParam.getCityName();
                        //=============
//                        if(city==null){
//                            return;
//                        }
//                        if(name==null){
//                            loadShopData();
//                            return;
//                        }
                        //=============
                        try {
                            if (city.contains(name)) {
                                UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                                user.setCityId(cityResponseParam.getCityId());
                                user.setCityName(cityResponseParam.getCityName());
                                SaveObjectTool.saveObject(user);
                                cityId = cityResponseParam.getCityId();
                                cityName.setText(user.getCityName());
                                loadShopData();
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onError(ProvinceResponseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bannerView.stopAutoPlay();
        getActivity().unregisterReceiver(refCityBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        adsLyt.startTurning(5000);

    }

    @Override
    public void onPause() {
        super.onPause();
        adsLyt.stopTurning();

    }
}
