package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.category.CateChildListRequestObject;
import com.doumee.model.request.category.CateChildListRequestParam;
import com.doumee.model.request.shop.ShopListRequestObject;
import com.doumee.model.request.shop.ShopListRequestParam;
import com.doumee.model.response.category.CateChildListResponseObject;
import com.doumee.model.response.category.CateChildListResponseParam;
import com.doumee.model.response.shop.ShopListResponseObject;
import com.doumee.model.response.shop.ShopListResponseParam;
import com.doumee.model.response.userinfo.AreaResponseParam;
import com.doumee.model.response.userinfo.CityResponseParam;
import com.doumee.model.response.userinfo.ProvinceResponseObject;
import com.doumee.model.response.userinfo.ProvinceResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduLocationTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * 商品列表
 * Created by wl on 2016/10/25.
 * titleView 是要传值
 */
public class ProductListActivity extends BaseActivity implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {


   CheckBox typeCheckBox;

   CheckBox sortCheckBox;

   MyListView gridView;

   RefreshLayout refreshLayout;

   private PopupWindow typeMenu;
   private PopupWindow sortMenu;

   private GridView typeGridView,areaGridView;

   private ArrayList<CateChildListResponseParam> typeMenuList;
   private CustomBaseAdapter<CateChildListResponseParam> typeMenuAdapter;

   private ArrayList<AreaResponseParam> areaMenuList;
   private CustomBaseAdapter<AreaResponseParam> areaMenuAdapter;

   private ArrayList<ShopListResponseParam> goodsList;
   private CustomBaseAdapter<ShopListResponseParam> goodsAdapter;

   private HttpTool httpTool;

   private String parentId;//父类I分级ID
   private String cateId = "",ardaId = "";//二级分类ID 区域ID
   private String cityId;
   /**
    * 排序：0默认排序 1按距离 2按销量 3经常去的 4按评价
    */
   private String sortType = "0";//排序：0默认排序 1按距离 2按销量 3经常去的 4按评价
   private String parentName;//父类I分级名字
   private int page = 1;
   private String firstQueryTime = "";
   private Bitmap defaultBitmap;

   BaiduLocationTool baiduLocationTool;
   private double lat;
   private double lng;


   public static void startProductListActivity(Context context, String parentId, String parentName) {
      Intent intent = new Intent(context, ProductListActivity.class);
      intent.putExtra("parentId", parentId);
      intent.putExtra("parentName", parentName);
      context.startActivity(intent);
   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      setContentView(R.layout.activity_product_list);

      initTitleBar_1();
      UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
      cityId = user.getCityId();
      parentId = getIntent().getStringExtra("parentId");
      parentName = getIntent().getStringExtra("parentName");
      baiduLocationTool = BaiduLocationTool.newInstance(ProductListActivity.this);
      initView();
      super.onCreate(savedInstanceState);
      typeMenuList = new ArrayList<>();
      areaMenuList = new ArrayList<>();
      goodsList = new ArrayList<>();
      httpTool = HttpTool.newInstance(this);
      defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.business_default);
      initTypeMenu();
      initSortMenu();
      initListener();
      initGoodsAdapter();
   }


   public void initView() {

      typeCheckBox = (CheckBox) findViewById(R.id.goods_type);
      sortCheckBox = (CheckBox) findViewById(R.id.goods_sort);
      gridView = (MyListView) findViewById(R.id.gv_product_list);
      refreshLayout = (RefreshLayout) findViewById(R.id.refresh);

      titleView.setText(parentName);
      actionImageButton.setVisibility(View.VISIBLE);
      actionImageButton.setOnClickListener(new View.OnClickListener() {//搜索
         @Override
         public void onClick(View v) {
            SearchProductActivity.startSearchProductActivity(ProductListActivity.this,0);
         }
      });

      //typeCheckBox.setText(parentName);
      refreshLayout.setOnLoadListener(this);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
   }

   private void initListener() {
      typeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               typeMenu.showAsDropDown(typeCheckBox);
            } else {
               typeMenu.dismiss();
            }
         }
      });
      sortCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               sortMenu.showAsDropDown(sortCheckBox);
            } else {
               sortMenu.dismiss();
            }
         }
      });
   }

   @Override
   protected void onStart() {
      super.onStart();
      loadMenuData();
      loadAreaData();
      dw();
   }

   @Override
   public void onRefresh() {
      page = 1;
      firstQueryTime = "";
      goodsList.clear();
      refreshLayout.setRefreshing(true);
      loadGoodsData();
   }

   @Override
   public void onLoad() {
      page++;
      refreshLayout.setLoading(true);
      loadGoodsData();
   }

   private void initTypeMenu() {
      View view = LayoutInflater.from(this).inflate(R.layout.goods_type_menu, null, false);
      typeGridView = (GridView) view.findViewById(R.id.type_menu);
      areaGridView = (GridView) view.findViewById(R.id.area_menu);
      Button clearButton = (Button)view.findViewById(R.id.clear_button);
      Button yesButton = (Button)view.findViewById(R.id.yes_button);
      typeMenu = new PopupWindow(view,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
      typeMenu.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
      typeMenu.setTouchable(true);
      typeMenu.setTouchInterceptor(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            return false;

         }
      });
      int color = ContextCompat.getColor(getApplication(), R.color.lineColor);
      typeMenu.setBackgroundDrawable(new ColorDrawable(color));    //要为popWindow设置一个背景才有效

      clearButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ardaId = "";
            cateId = "";
            resetMenu(typeGridView);
            resetMenu(areaGridView);
            typeMenu.dismiss();
            onRefresh();
         }
      });
      yesButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            typeMenu.dismiss();
            onRefresh();
         }
      });
      initTypeGridAdapter();
      typeMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
         @Override
         public void onDismiss() {
            typeCheckBox.setChecked(false);
         }
      });
   }

   private void initTypeGridAdapter() {
      typeMenuAdapter = new CustomBaseAdapter<CateChildListResponseParam>(typeMenuList, R.layout.goods_type_menu_item) {
         @Override
         public void bindView(ViewHolder holder, CateChildListResponseParam obj) {
            final RadioButton itemView = holder.getView(R.id.item_name);
            itemView.setText(obj.getCateName());
            final String id = obj.getCateId();
            itemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
                     resetMenu(typeGridView);
                     itemView.setChecked(true);
                     cateId = id;
                  }
               }
            });
         }
      };
      typeGridView.setAdapter(typeMenuAdapter);

      areaMenuAdapter = new CustomBaseAdapter<AreaResponseParam>(areaMenuList,R.layout.goods_type_menu_item) {
         @Override
         public void bindView(ViewHolder holder, AreaResponseParam obj) {
            final RadioButton itemView = holder.getView(R.id.item_name);
            itemView.setText(obj.getAreaName());
            final String id = obj.getAreaId();
            itemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
                     resetMenu(areaGridView);
                     itemView.setChecked(true);
                     ardaId = id;
                  }
               }
            });
         }
      };
      areaGridView.setAdapter(areaMenuAdapter);
   }

   //加载区域信息
   private void loadAreaData(){
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceResponseObject>() {
         @Override
         public void onSuccess(ProvinceResponseObject o) {
            List<ProvinceResponseParam> lstProvince = o.getLstProvince();
            areaMenuList.clear();
            for (ProvinceResponseParam provinceResponseParam : lstProvince) {
               List<CityResponseParam> lstCity = provinceResponseParam.getLstCity();
               for (CityResponseParam cityResponseParam: lstCity){
                  String city = cityResponseParam.getCityId();
                  if (TextUtils.equals(city,cityId)){
                     List<AreaResponseParam> areaList = cityResponseParam.getLstArea();
                     areaMenuList.addAll(areaList);
                     break;
                  }
               }
            }
            areaMenuAdapter.notifyDataSetChanged();
         }
         @Override
         public void onError(ProvinceResponseObject o) {
         }
      });
   }

   private void initSortMenu() {
      View view = LayoutInflater.from(this).inflate(R.layout.goods_type_sort_item, null, false);
      RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sort_menu);
      RadioButton allRadioButton = (RadioButton) view.findViewById(R.id.item_all);
      final RadioButton saleRadioButton = (RadioButton) view.findViewById(R.id.item_sale);
//      final RadioButton priceRadioButton = (RadioButton) view.findViewById(R.id.item_price);
      sortMenu = new PopupWindow(view,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
      sortMenu.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
      sortMenu.setTouchable(true);
      sortMenu.setTouchInterceptor(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            return false;

         }
      });
      int color = ContextCompat.getColor(getApplication(), R.color.lineColor);
      sortMenu.setBackgroundDrawable(new ColorDrawable(color));    //要为popWindow设置一个背景才有效

      allRadioButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sortMenu.dismiss();
            sortType = "0";
            sortCheckBox.setText("综合排序");
            saleRadioButton.setText("离我最近");
            onRefresh();
         }
      });

      saleRadioButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sortMenu.dismiss();
            sortType = "1";
            dw();
            sortCheckBox.setText("离我最近");
         }
      });
      sortMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
         @Override
         public void onDismiss() {
            sortCheckBox.setChecked(false);
         }
      });
   }



   private void resetMenu(GridView gridView) {
      int size = gridView.getChildCount();
      for (int i = 0; i < size; i++) {
         View view = gridView.getChildAt(i);
         RadioButton itemView = (RadioButton) view.findViewById(R.id.item_name);
         itemView.setChecked(false);
      }
   }


   private void loadMenuData() {
      CateChildListRequestParam cateChildListRequestParam = new CateChildListRequestParam();
      cateChildListRequestParam.setParentId(parentId);
      CateChildListRequestObject cateChildListRequestObject = new CateChildListRequestObject();
      cateChildListRequestObject.setParam(cateChildListRequestParam);
      httpTool.post(cateChildListRequestObject, URLConfig.GOODS_MENU_LIST, new HttpTool.HttpCallBack<CateChildListResponseObject>() {
         @Override
         public void onSuccess(CateChildListResponseObject o) {
            typeMenuList.clear();
            typeMenuList.addAll(o.getRecordList());
            typeMenuAdapter.notifyDataSetChanged();
         }

         @Override
         public void onError(CateChildListResponseObject o) {

         }
      });
   }

   private void loadGoodsData() {
      ShopListRequestParam shopListRequestParam = new ShopListRequestParam();
      shopListRequestParam.setSortType(sortType);//排序：0默认排序 1按距离 2按销量 3经常去的 4按评价
      shopListRequestParam.setCityId(cityId);
      shopListRequestParam.setLat(lat);
      shopListRequestParam.setLng(lng);
      shopListRequestParam.setCateId(cateId);//cateId 二级分类编码
      shopListRequestParam.setAreaId(ardaId);
      shopListRequestParam.setParentCateId(parentId);//parentId 一级分类编码
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      paginationBaseObject.setFirstQueryTime(firstQueryTime);

      ShopListRequestObject shopListRequestObject = new ShopListRequestObject();
      shopListRequestObject.setPagination(paginationBaseObject);
      shopListRequestObject.setParam(shopListRequestParam);

      httpTool.post(shopListRequestObject, URLConfig.SHOP_LIST, new HttpTool.HttpCallBack<ShopListResponseObject>() {
         @Override
         public void onSuccess(ShopListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            firstQueryTime = o.getFirstQueryTime();
            List<ShopListResponseParam> proList = o.getRecordList();
            goodsList.addAll(proList);
            goodsAdapter.notifyDataSetChanged();
            if (goodsList.isEmpty()){
               gridView.setBackgroundResource(R.mipmap.gwc_default);
            }else{
               gridView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.white));
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

   private void initGoodsAdapter() {
      goodsAdapter = new CustomBaseAdapter<ShopListResponseParam>(goodsList, R.layout.item_shop_list) {
         @Override
         public void bindView(ViewHolder holder, ShopListResponseParam obj) {
            ImageView goodsImage = holder.getView(R.id.shop_image);
            TextView goodsName = holder.getView(R.id.tv_item_shop_shopname);
            RatingBar ratingBar = holder.getView(R.id.rating_bar);
            TextView tv_lx = holder.getView(R.id.tv_shop_lx);
            TextView tv_add = holder.getView(R.id.tv_item_shop_add);
            TextView tv_dis = holder.getView(R.id.tv_item_shop_dis);
            TextView saleView = holder.getView(R.id.tv_item_shop_salecount);
             String imagePath = obj.getImgurl();
            int score = obj.getScore();
            double dis = obj.getDistance() / 1000;
            String add = obj.getAddr();
            String categoryName = obj.getCategoryName();


            goodsImage.setImageBitmap(defaultBitmap);
            if (!TextUtils.isEmpty(imagePath)) {
               ImageLoader.getInstance().displayImage(imagePath, goodsImage);
            }
            goodsName.setText(obj.getName());
            ratingBar.setRating(score);
            tv_dis.setText(dis + "km");
            tv_add.setText(add);
            tv_lx.setText(categoryName);
            saleView.setText("月销量："+obj.getSaleNum());
         }
      };
      gridView.setAdapter(goodsAdapter);
      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String proId = goodsList.get(position).getShopId();
            String isOnline = goodsList.get(position).getIsOnline();
            if (goodsList.get(position).getIsOnline().equals("0")) {//0  线下  1 线上
               ShopDetailsActivity.startShopDetailsActivity(ProductListActivity.this, proId);
            } else {
               ShopDetailsOnlineActivity.startShopActivity(ProductListActivity.this, proId, "");
            }


         }
      });
   }

   /**
    * 定位
    */
   private void dw() {
      baiduLocationTool.startLocation(new BaiduLocationTool.LocationListener() {
         @Override
         public void onReceiveLocation(BDLocation location) {
            baiduLocationTool.stopLocation();
            if (null != location) {
               lat = location.getLatitude();
               lng = location.getLongitude();
            }
            onRefresh();
         }
      });
   }


   @Override
   public void onStop() {
      super.onStop();
      if (null != baiduLocationTool) {
         baiduLocationTool.stopLocation();
      }
   }

}
