package com.huixiangshenghuo.app.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.BannerPagerAdapter;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.MyViewPagerAdapter;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.NearbyShopAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.activityshopcircle.AdInfoActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.ProductListActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.SearchProductActivity;
import com.huixiangshenghuo.app.view.BannerView;
import com.huixiangshenghuo.app.view.ListenerScrollView;
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
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.LocationActivity;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页商圈
 * 306行 暂时屏蔽
 */
public class HomeShopFragment extends Fragment implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RefreshLayout refreshLayout;

    View view;
    private BannerView bannerView;
    private TextView moreView;

    private ViewPager vp_menu;
    private List<View> pagerView;
    private RadioGroup pageGroup;

    private TextView cityName;//地址
    private View rootView;
    private RefCityBroadcastReceiver refCityBroadcastReceiver;
    public static final String REFRESH_CITY = "com.doumee.refresh.city";
    private ListenerScrollView listenerScrollView;
    private RelativeLayout relativeLayout;
    private String cityId;

    private HttpTool httpTool;
    private ArrayList<AdListResponseParam> bannerList;

    //========================附件商家
    private int page = 1;
    private String querytime;
    private ArrayList<ShopListResponseParam> arrlist = new ArrayList<ShopListResponseParam>();
    private NearbyShopAdapter mAdapter;
    BaiduLocationTool baiduLocationTool;
    private double lat;
    private double lng;
    private String shopName;
    private String cateId;
    private String parentCateId;
    private String sortType;
    MyListView lv_shop;
    //========================附件商家

    private TextView search_bar;



    public HomeShopFragment() {

    }


    public static HomeShopFragment newInstance(String param1, String param2) {
        HomeShopFragment fragment = new HomeShopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortType = getArguments().getString(ARG_PARAM1);
            parentCateId = getArguments().getString(ARG_PARAM2);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        baiduLocationTool = BaiduLocationTool.newInstance(getActivity());
        refCityBroadcastReceiver = new RefCityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HomeShopFragment.REFRESH_CITY);
        getActivity().registerReceiver(refCityBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_shop, container, false);
        httpTool = HttpTool.newInstance(getActivity());
        bannerList = new ArrayList<>();
        pagerView = new ArrayList<View>();
        initview();
        refresh();
        loadServerAD();
        onRefresh();
        return view;
    }

    private void refresh() {
        // TODO Auto-generated method stub
        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadListener(this);
        mAdapter = new NearbyShopAdapter(arrlist, getActivity());
        lv_shop.setAdapter(mAdapter);
    }

    private void initview() {
        bannerView = (BannerView) view.findViewById(R.id.view_banner);
        vp_menu = (ViewPager) view.findViewById(R.id.vp_menu);
        pageGroup = (RadioGroup) view.findViewById(R.id.iv_group);
        cityName = (TextView) view.findViewById(R.id.tv_home_add);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.rl_shop_sx);
        lv_shop = (MyListView) view.findViewById(R.id.lv_shop);
        search_bar = (TextView) view.findViewById(R.id.search_bar);
        listenerScrollView = (ListenerScrollView)view.findViewById(R.id.home_scrollview);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.rl_businessHead) ;
        moreView = (TextView)view.findViewById(R.id.more);
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
        relativeLayout.setAlpha(0);
        listenerScrollView.setOnScrollChangeListener(new ListenerScrollView.OnScrollChangeListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (t <= 300) {
                    relativeLayout.setAlpha(t / 300f);
                } else {
                    relativeLayout.setAlpha(1);
                }
            }
        });
    }

    //*********************首页广告开始*************************//
    //加载首页广告
    private void loadServerAD() {
        AdRequestParam adRequestParam = new AdRequestParam();
        adRequestParam.setPosition("1");//广告位置 0首页广告 1商家页广告
        AdRequestObject adRequestObject = new AdRequestObject();
        adRequestObject.setParam(adRequestParam);
        httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
            @Override
            public void onSuccess(AdListResponseObject o) {
                List<AdListResponseParam> adLst = o.getAdLst();
                bannerList.clear();
                bannerList.addAll(adLst);
                updateAd();
            }

            @Override
            public void onError(AdListResponseObject o) {
                Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAd() {
        if (!bannerList.isEmpty()) {
            BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter<AdListResponseParam>(getActivity(), bannerList) {
                @Override
                public View setView(int position) {
                    ImageView imageView = new ImageView(getActivity());
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    String path = bannerList.get(position).getImgurl();
                    ImageLoader.getInstance().displayImage(path, imageView);
                    return imageView;
                }
            };

            bannerView.setDotGravity(BannerView.CENTER).
                  setDot(R.drawable.no_selected_dot, R.drawable.selected_dot);
            bannerView.setAdapter(bannerPagerAdapter);
            bannerPagerAdapter.setOnItemClickListener(new BannerPagerAdapter.onItemClickListener() {
                @Override
                public void onClick(int position) {
                    AdListResponseParam adListResponseParam = bannerList.get(position);
                    String type = adListResponseParam.getType();
                    String content = adListResponseParam.getContent();
                    String link = adListResponseParam.getLink();
                    AdInfoActivity.startAdInfoActivity(getActivity(), type, content, link, adListResponseParam.getTitle());//0内容1外链接
                }
            });
            bannerView.startAutoPlay();
        }
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
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                List<CategoryListResponseParam> recordList = o.getRecordList();
                initTypeView(recordList);
            }

            @Override
            public void onError(CategoryListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
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
                ProductListActivity.startProductListActivity(getActivity(), info.getCateId(), info.getCateName());
            }
        });
    }

    private void addTypeItem(int id) {
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 0, 0);
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
        shopListRequestParam.setName(shopName);
        shopListRequestParam.setSortType(sortType);
        shopListRequestParam.setCityId(cityId);
        shopListRequestParam.setLat(lat);
        shopListRequestParam.setLng(lng);
        shopListRequestParam.setCateId(cateId);
        shopListRequestParam.setParentCateId(parentCateId);
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        paginationBaseObject.setFirstQueryTime(querytime);
        ShopListRequestObject shopListRequestObject = new ShopListRequestObject();
        shopListRequestObject.setPagination(paginationBaseObject);
        shopListRequestObject.setParam(shopListRequestParam);

        httpTool.post(shopListRequestObject, URLConfig.SHOP_LIST, new HttpTool.HttpCallBack<ShopListResponseObject>() {
            @Override
            public void onSuccess(ShopListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                querytime = o.getFirstQueryTime();
                arrlist.addAll(o.getRecordList());
                mAdapter.notifyDataSetChanged();
                if (arrlist.isEmpty()){

                }
              /*  if (arrlist.isEmpty()){
                    lv_shop.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    lv_shop.setBackgroundResource(0);
                }*/
            }
            @Override
            public void onError(ShopListResponseObject o) {
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    //***************************附近商家结束*********************************//


    @Override
    public void onRefresh() {
        page = 1;
        querytime = "";
        refreshLayout.setRefreshing(true);
        arrlist.clear();
        loadType();
        libCity();
    }

    @Override
    public void onLoad() {
        page++;
        loadShopData();
    }

    public void onSearch(String name) {
        shopName = name;
        cityId = "";
        onRefresh();
    }

    public void onShopList(String cateId, String sortType) {
        shopName = "";
        cityId = "";
        this.cateId = cateId;
        this.sortType = sortType;
        onRefresh();
    }

    private class RefCityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, REFRESH_CITY)) {
                page = 1;
                querytime = "";
                refreshLayout.setRefreshing(true);
                arrlist.clear();
                UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                String city = user.getCityId();
                cityId = city;
                cityName.setText(user.getCityName());
                loadShopData();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != baiduLocationTool) {
            baiduLocationTool.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(refCityBroadcastReceiver);
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
                    String city =  location.getCity();
                    loadCity(city);
                }else{
                   ToastView.show("定位失败,请选择城市");
                   loadShopData();
                }
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
                        if (city.contains(name)){
                            UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
                            user.setCityId(cityResponseParam.getCityId());
                            user.setCityName(cityResponseParam.getCityName());
                            SaveObjectTool.saveObject(user);
                            cityId = cityResponseParam.getCityId();
                            cityName.setText(user.getCityName());
                            loadShopData();
                            break;
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
}
