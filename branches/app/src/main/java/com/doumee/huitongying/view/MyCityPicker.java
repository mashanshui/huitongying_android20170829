package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AreaWheelAdapter;
import com.huixiangshenghuo.app.adapter.CityWheelAdapter;
import com.huixiangshenghuo.app.adapter.ProvinceWheelAdapter;
import com.huixiangshenghuo.app.ui.mine.CityParam;
import com.huixiangshenghuo.app.ui.mine.ProvinceParam;
import com.doumee.model.response.userinfo.AreaResponseParam;
import com.lljjcoder.citypickerview.widget.CanShow;
import com.lljjcoder.citypickerview.widget.wheel.OnWheelChangedListener;
import com.lljjcoder.citypickerview.widget.wheel.WheelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class MyCityPicker implements CanShow, OnWheelChangedListener {
   private Context context;

   private PopupWindow popwindow;

   private View popview;

   private WheelView mViewProvince;

   private WheelView mViewCity;

   private WheelView mViewDistrict;

   private RelativeLayout mRelativeTitleBg;

   private TextView mTvOK;

   private TextView mTvTitle;

   private TextView mTvCancel;

   /**
    * 所有省
    */
   protected List<ProvinceParam> mProvinceDatas;

   /**
    * key - 省 value - 市
    */
   protected Map<String, List<CityParam>> mCitisDatasMap = new HashMap<>();

   /**
    * key - 市 values - 区
    */
   protected Map<String, List<AreaResponseParam>> mDistrictDatasMap = new HashMap<>();


   /**
    * 当前省的名称
    */
   protected String mCurrentProviceId;

   /**
    * 当前市的名称
    */
   protected String mCurrentCityId;

   /**
    * 当前区的名称
    */
   protected String mCurrentDistrictId = "";


   private OnCityItemClickListener listener;

   public interface OnCityItemClickListener {
      void onSelected(int... citySelected);
   }

   public void setOnCityItemClickListener(OnCityItemClickListener listener) {
      this.listener = listener;
   }

   /**
    * Default text color
    */
   public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

   /**
    * Default text size
    */
   public static final int DEFAULT_TEXT_SIZE = 18;

   // Text settings
   private int textColor = DEFAULT_TEXT_COLOR;

   private int textSize = DEFAULT_TEXT_SIZE;

   /**
    * 滚轮显示的item个数
    */
   private static final int DEF_VISIBLE_ITEMS = 5;

   // Count of visible items
   private int visibleItems = DEF_VISIBLE_ITEMS;

   /**
    * 省滚轮是否循环滚动
    */
   private boolean isProvinceCyclic = false;

   /**
    * 市滚轮是否循环滚动
    */
   private boolean isCityCyclic = true;

   /**
    * 区滚轮是否循环滚动
    */
   private boolean isDistrictCyclic = true;

   /**
    * item间距
    */
   private int padding = 5;


   /**
    * Color.BLACK
    */
   private String cancelTextColorStr = "#000000";


   /**
    * Color.BLUE
    */
   private String confirmTextColorStr = "#0000FF";

   /**
    * 标题背景颜色
    */
   private String titleBackgroundColorStr = "#E9E9E9";


   /**
    * 两级联动
    */
   private boolean showProvinceAndCity = false;

   /**
    * 标题
    */
   private String mTitle = "选择地区";

   private MyCityPicker(Builder builder) {
      this.textColor = builder.textColor;
      this.textSize = builder.textSize;
      this.visibleItems = builder.visibleItems;
      this.isProvinceCyclic = builder.isProvinceCyclic;
      this.isDistrictCyclic = builder.isDistrictCyclic;
      this.isCityCyclic = builder.isCityCyclic;
      this.context = builder.mContext;
      this.padding = builder.padding;
      this.mTitle = builder.mTitle;
      this.titleBackgroundColorStr = builder.titleBackgroundColorStr;
      this.confirmTextColorStr = builder.confirmTextColorStr;
      this.cancelTextColorStr = builder.cancelTextColorStr;

      this.showProvinceAndCity = builder.showProvinceAndCity;

      LayoutInflater layoutInflater = LayoutInflater.from(context);
      popview = layoutInflater.inflate(R.layout.pop_my_citypicker, null);

      mViewProvince = (WheelView) popview.findViewById(com.lljjcoder.citypickerview.R.id.id_province);
      mViewCity = (WheelView) popview.findViewById(com.lljjcoder.citypickerview.R.id.id_city);
      mViewDistrict = (WheelView) popview.findViewById(com.lljjcoder.citypickerview.R.id.id_district);
      mRelativeTitleBg = (RelativeLayout) popview.findViewById(com.lljjcoder.citypickerview.R.id.rl_title);
      mTvOK = (TextView) popview.findViewById(com.lljjcoder.citypickerview.R.id.tv_confirm);
      mTvTitle = (TextView) popview.findViewById(com.lljjcoder.citypickerview.R.id.tv_title);
      mTvCancel = (TextView) popview.findViewById(com.lljjcoder.citypickerview.R.id.tv_cancel);


      popwindow = new PopupWindow(popview, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
      popwindow.setBackgroundDrawable(new ColorDrawable(0x80000000));
      popwindow.setAnimationStyle(com.lljjcoder.citypickerview.R.style.AnimBottom);
      popwindow.setTouchable(true);
      popwindow.setOutsideTouchable(true);
      popwindow.setFocusable(true);


      /**
       * 设置标题背景颜色
       */
      if (!TextUtils.isEmpty(this.titleBackgroundColorStr)) {
         mRelativeTitleBg.setBackgroundColor(Color.parseColor(this.titleBackgroundColorStr));
      }

      /**
       * 设置标题
       */
      if (!TextUtils.isEmpty(this.mTitle)) {
         mTvTitle.setText(this.mTitle);
      }

      //设置确认按钮文字颜色
      if (!TextUtils.isEmpty(this.confirmTextColorStr)) {
         mTvOK.setTextColor(Color.parseColor(this.confirmTextColorStr));
      }

      //设置取消按钮文字颜色
      if (!TextUtils.isEmpty(this.cancelTextColorStr)) {
         mTvCancel.setTextColor(Color.parseColor(this.cancelTextColorStr));
      }


      //只显示省市两级联动
      if (this.showProvinceAndCity) {
         mViewDistrict.setVisibility(View.GONE);
      } else {
         mViewDistrict.setVisibility(View.VISIBLE);
      }

      // 添加change事件
      mViewProvince.addChangingListener(this);
      // 添加change事件
      mViewCity.addChangingListener(this);
      // 添加change事件
      mViewDistrict.addChangingListener(this);
      // 添加onclick事件
      mTvCancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            hide();
         }
      });
      mTvOK.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            try {
               if (showProvinceAndCity) {
                  listener.onSelected(mViewProvince.getCurrentItem(), mViewCity.getCurrentItem(), 0);
               } else {
                  listener.onSelected(mViewProvince.getCurrentItem(), mViewCity.getCurrentItem(), mViewDistrict.getCurrentItem());
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
            hide();
         }
      });

   }

   public static class Builder {
      /**
       * Default text color
       */
      public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

      /**
       * Default text size
       */
      public static final int DEFAULT_TEXT_SIZE = 18;

      // Text settings
      private int textColor = DEFAULT_TEXT_COLOR;

      private int textSize = DEFAULT_TEXT_SIZE;

      /**
       * 滚轮显示的item个数
       */
      private static final int DEF_VISIBLE_ITEMS = 5;

      // Count of visible items
      private int visibleItems = DEF_VISIBLE_ITEMS;

      /**
       * 省滚轮是否循环滚动
       */
      private boolean isProvinceCyclic = false;

      /**
       * 市滚轮是否循环滚动
       */
      private boolean isCityCyclic = true;

      /**
       * 区滚轮是否循环滚动
       */
      private boolean isDistrictCyclic = true;

      private Context mContext;

      /**
       * item间距
       */
      private int padding = 5;


      /**
       * Color.BLACK
       */
      private String cancelTextColorStr = "#000000";


      /**
       * Color.BLUE
       */
      private String confirmTextColorStr = "#0000FF";

      /**
       * 标题背景颜色
       */
      private String titleBackgroundColorStr = "#E9E9E9";


      /**
       * 标题
       */
      private String mTitle = "选择地区";

      /**
       * 两级联动
       */
      private boolean showProvinceAndCity = false;

      public Builder(Context context) {
         this.mContext = context;
      }

      /**
       * 设置标题背景颜色
       *
       * @param colorBg
       * @return
       */
      public Builder titleBackgroundColor(String colorBg) {
         this.titleBackgroundColorStr = colorBg;
         return this;
      }

      /**
       * 设置标题
       *
       * @param mtitle
       * @return
       */
      public Builder title(String mtitle) {
         this.mTitle = mtitle;
         return this;
      }

      /**
       * 是否只显示省市两级联动
       *
       * @param flag
       * @return
       */
      public Builder onlyShowProvinceAndCity(boolean flag) {
         this.showProvinceAndCity = flag;
         return this;
      }


      //        /**
      //         * 确认按钮文字颜色
      //         * @param color
      //         * @return
      //         */
      //        public Builder confirTextColor(int color) {
      //            this.confirmTextColor = color;
      //            return this;
      //        }

      /**
       * 确认按钮文字颜色
       *
       * @param color
       * @return
       */
      public Builder confirTextColor(String color) {
         this.confirmTextColorStr = color;
         return this;
      }

      //        /**
      //         * 取消按钮文字颜色
      //         * @param color
      //         * @return
      //         */
      //        public Builder cancelTextColor(int color) {
      //            this.cancelTextColor = color;
      //            return this;
      //        }

      /**
       * 取消按钮文字颜色
       *
       * @param color
       * @return
       */
      public Builder cancelTextColor(String color) {
         this.cancelTextColorStr = color;
         return this;
      }

      /**
       * item文字颜色
       *
       * @param textColor
       * @return
       */
      public Builder textColor(int textColor) {
         this.textColor = textColor;
         return this;
      }

      /**
       * item文字大小
       *
       * @param textSize
       * @return
       */
      public Builder textSize(int textSize) {
         this.textSize = textSize;
         return this;
      }

      /**
       * 滚轮显示的item个数
       *
       * @param visibleItems
       * @return
       */
      public Builder visibleItemsCount(int visibleItems) {
         this.visibleItems = visibleItems;
         return this;
      }

      /**
       * 省滚轮是否循环滚动
       *
       * @param isProvinceCyclic
       * @return
       */
      public Builder provinceCyclic(boolean isProvinceCyclic) {
         this.isProvinceCyclic = isProvinceCyclic;
         return this;
      }

      /**
       * 市滚轮是否循环滚动
       *
       * @param isCityCyclic
       * @return
       */
      public Builder cityCyclic(boolean isCityCyclic) {
         this.isCityCyclic = isCityCyclic;
         return this;
      }

      /**
       * 区滚轮是否循环滚动
       *
       * @param isDistrictCyclic
       * @return
       */
      public Builder districtCyclic(boolean isDistrictCyclic) {
         this.isDistrictCyclic = isDistrictCyclic;
         return this;
      }

      /**
       * item间距
       *
       * @param itemPadding
       * @return
       */
      public Builder itemPadding(int itemPadding) {
         this.padding = itemPadding;
         return this;
      }

      public MyCityPicker build() {
         MyCityPicker cityPicker = new MyCityPicker(this);
         return cityPicker;
      }

   }

   private void setUpData() {
      ProvinceWheelAdapter arrayWheelAdapter = new ProvinceWheelAdapter(context, mProvinceDatas);
      mViewProvince.setViewAdapter(arrayWheelAdapter);
      //获取所设置的省的位置，直接定位到该位置
      mViewProvince.setCurrentItem(0);
      // 设置可见条目数量
      mViewProvince.setVisibleItems(visibleItems);
      mViewCity.setVisibleItems(visibleItems);
      mViewDistrict.setVisibleItems(visibleItems);
      mViewProvince.setCyclic(isProvinceCyclic);
      mViewCity.setCyclic(isCityCyclic);
      mViewDistrict.setCyclic(isDistrictCyclic);
      arrayWheelAdapter.setPadding(padding);
      arrayWheelAdapter.setTextColor(textColor);
      arrayWheelAdapter.setTextSize(textSize);

      updateCities();
      updateAreas();
   }

   public List<ProvinceParam> getmProvinceDatas() {
      return mProvinceDatas;
   }

   public Map<String, List<CityParam>> getmCitisDatasMap() {
      return mCitisDatasMap;
   }

   public Map<String, List<AreaResponseParam>> getmDistrictDatasMap() {
      return mDistrictDatasMap;
   }

   public void setmProvinceDatas(List<ProvinceParam> mProvinceDatas) {
      this.mProvinceDatas = mProvinceDatas;
   }

   public void setmCitisDatasMap(Map<String, List<CityParam>> mCitisDatasMap) {
      this.mCitisDatasMap = mCitisDatasMap;
   }

   public void setmDistrictDatasMap(Map<String, List<AreaResponseParam>> mDistrictDatasMap) {
      this.mDistrictDatasMap = mDistrictDatasMap;
   }

   /**
    * 根据当前的市，更新区WheelView的信息
    */
   private void updateAreas() {
      int pCurrent = mViewCity.getCurrentItem();
      try {
         mCurrentCityId = mCitisDatasMap.get(mCurrentProviceId).get(pCurrent).getCityId();
      } catch (Exception e) {
         e.printStackTrace();
         mCurrentCityId = "";
      }
      List<AreaResponseParam> areas = mDistrictDatasMap.get(mCurrentCityId);

      if (areas == null) {
         areas = new ArrayList<>();
      }

//        int districtDefault = -1;
//        if (!TextUtils.isEmpty(defaultDistrict) && areas.length > 0) {
//            for (int i = 0; i < areas.length; i++) {
//                if (areas[i].contains(defaultDistrict)) {
//                    districtDefault = i;
//                    break;
//                }
//            }
//        }

      AreaWheelAdapter districtWheel = new AreaWheelAdapter(context, areas);
      // 设置可见条目数量
      districtWheel.setTextColor(textColor);
      districtWheel.setTextSize(textSize);
      mViewDistrict.setViewAdapter(districtWheel);
      mViewDistrict.setCurrentItem(0);
      //获取第一个区名称
      try {
         mCurrentDistrictId = mDistrictDatasMap.get(mCurrentCityId).get(0).getAreaId();
      } catch (Exception e) {
         e.printStackTrace();
         mCurrentDistrictId = "";
      }
      districtWheel.setPadding(padding);
   }

   /**
    * 根据当前的省，更新市WheelView的信息
    */
   private void updateCities() {
      int pCurrent = mViewProvince.getCurrentItem();
      mCurrentProviceId = mProvinceDatas.get(pCurrent).getProvinceId();
      List<CityParam> cities = mCitisDatasMap.get(mCurrentProviceId);
      if (cities == null) {
         cities = new ArrayList<>();
      }

      CityWheelAdapter cityWheel = new CityWheelAdapter(context, cities);
      // 设置可见条目数量
      cityWheel.setTextColor(textColor);
      cityWheel.setTextSize(textSize);
      mViewCity.setViewAdapter(cityWheel);
      mViewCity.setCurrentItem(0);
      cityWheel.setPadding(padding);
      updateAreas();
   }

   @Override
   public void setType(int type) {
   }

   @Override
   public void show() {
      if (!isShow()) {
         setUpData();
         popwindow.showAtLocation(popview, Gravity.BOTTOM, 0, 0);
      }
   }

   @Override
   public void hide() {
      if (isShow()) {
         popwindow.dismiss();
      }
   }

   @Override
   public boolean isShow() {
      return popwindow.isShowing();
   }

   @Override
   public void onChanged(WheelView wheel, int oldValue, int newValue) {
      // TODO Auto-generated method stub
      if (wheel == mViewProvince) {
         updateCities();
      } else if (wheel == mViewCity) {
         updateAreas();
      } else if (wheel == mViewDistrict) {
         mCurrentDistrictId = mDistrictDatasMap.get(mCurrentCityId).get(newValue).getAreaId();
      }
   }
}
