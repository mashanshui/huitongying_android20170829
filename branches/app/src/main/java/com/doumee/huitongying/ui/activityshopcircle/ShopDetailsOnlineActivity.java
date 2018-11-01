package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomFragmentPagerAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.collects.CollectsAddRequestObject;
import com.doumee.model.request.collects.CollectsAddRequestParam;
import com.doumee.model.request.collects.CollectsDelRequestObject;
import com.doumee.model.request.collects.CollectsDelRequestParam;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.request.shopImg.ShopImgListRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.shopImg.ShopImgListResponseObject;
import com.doumee.model.response.shopImg.ShopImgListResponseParam;
import com.huixiangshenghuo.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/2/8.
 * 线上商家详情
 */

public class ShopDetailsOnlineActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, View.OnClickListener {


   ViewPager viewPager;

   RadioGroup radioGroup;

   RadioButton goodsButton;

//RadioButton serverButton;

   RadioButton shopButton;

   ImageButton leftButton;
//@Bind(R.id.collect)
//ImageView collectButton;
//@Bind(R.id.search)
//ImageButton searchButton;
//@Bind(R.id.shop_ic)
//ImageView shopBGView;
//@Bind(R.id.shop_image)
//ImageView shopImageView;


   //   TextView shopTipView;
   private TextView tv_operating_state;
   private TextView tv_shop_name;
   private TextView tv_shop_addr;
   private TextView tv_shop_salenum;
   private TextView tv_shop_notice;
   private ImageView iv_imgurl;



   private ArrayList<Fragment> fragmentArrayList;
   private CustomFragmentPagerAdapter customFragmentPagerAdapter;
   private String shopId;
   private String collect;//上个界面传递过来 查看是否营业

   private int State;//是否 营业  0 营业 1 未营业

   final ArrayList<String> imageList = new ArrayList<>();
   /**
    * 是否收藏 0：未收藏 1已收藏
    */
   private String isCollected;
   private OnloadDataListener onloadDataListener;
   private String path;//商家相册
   private TextView tv_shop_details;
   private String imgurl;//商家图片
   private Bitmap defaultImage;//

   public static void startShopActivity(Context context, String shopId, String collect) {
      Intent intent = new Intent(context, ShopDetailsOnlineActivity.class);
      intent.putExtra("shopId", shopId);
      intent.putExtra("collect", collect);
      context.startActivity(intent);
   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shop_details_online);
      shopId = getIntent().getStringExtra("shopId");
      collect = getIntent().getStringExtra("collect");
      initView();

   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("商户详情");
      bt_action_image.setVisibility(View.VISIBLE);
//      actionButton.setVisibility(View.GONE);
      bt_action_image.setText("收藏");

      viewPager = (ViewPager) findViewById(R.id.view_page);
      radioGroup = (RadioGroup) findViewById(R.id.group);
      goodsButton = (RadioButton) findViewById(R.id.goods_s);
//      serverButton = (RadioButton) findViewById(R.id.server_s);
      shopButton = (RadioButton) findViewById(R.id.shop_s);
      leftButton = (ImageButton) findViewById(R.id.back);
//      shopTipView = (TextView) findViewById(R.id.shop_tip);
      tv_operating_state = (TextView) findViewById(R.id.tv_operating_state);
      tv_shop_name = (TextView) findViewById(R.id.tv_shop_details_online_name);
      tv_shop_addr = (TextView) findViewById(R.id.tv_shop_details_online_addr);
      tv_shop_salenum = (TextView) findViewById(R.id.tv_shop_details_online_salenum);
      tv_shop_notice = (TextView) findViewById(R.id.tv_shop_details_online_notice);
      iv_imgurl = (ImageView) findViewById(R.id.iv_imgurl);
      tv_shop_details = (TextView) findViewById(R.id.tv_shop_details);
      //默认图片
      defaultImage = BitmapFactory.decodeResource(this.getResources(), R.mipmap.business_default);

      request();


      leftButton.setOnClickListener(this);
      tv_shop_notice.setOnClickListener(this);
////      searchButton.setOnClickListener(this);
//      fragmentArrayList = new ArrayList<>();
//      fragmentArrayList.add(GoodsFragment.newInstance(shopId, State + "",tv_shop_name.getText().toString().trim()));
////      fragmentArrayList.add(ServerFragment.newInstance(shopId,""));
//      fragmentArrayList.add(ShopFragment.newInstance(shopId, ""));
//
//      customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
//
//      viewPager.setAdapter(customFragmentPagerAdapter);
//      viewPager.addOnPageChangeListener(this);
//      radioGroup.setOnCheckedChangeListener(this);

//      loadPics();
      bt_action_image.setBackgroundResource(R.drawable.button_yuanjiao_biankuang);
      bt_action_image.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (isCollected.equals("0")) {//0 未收藏 1 已收藏
               collectShop();
            } else {
               CancelCollection();
            }
         }
      });


      iv_imgurl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            if (TextUtils.isEmpty(path)) {
//
//            } else {
//               PhotoActivity.startPhotoActivity(ShopDetailsOnlineActivity.this, path, imageList);
//            }
//            if (TextUtils.isEmpty(imgurl)) {
//               ToastView.show("暂无更多图片");
//            } else {
//               PhotoActivity.startPhotoActivity(ShopDetailsOnlineActivity.this, imgurl, imageList);
//            }
            if (imageList.size() > 0) {
//               PhotoActivity.startPhotoActivity(ShopDetailsOnlineActivity.this, imgurl, imageList);
               PhotoActivity.startPhotoActivity(ShopDetailsOnlineActivity.this, imageList.get(0), imageList);
            } else {
               ToastView.show("暂无更多图片");
            }

         }
      });


   }


   @Override
   public void onCheckedChanged(RadioGroup group, int checkedId) {
      int c = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
      int w = ContextCompat.getColor(getApplicationContext(), R.color.white);
      switch (checkedId) {
         case R.id.goods:
            goodsButton.setBackgroundColor(c);
//            serverButton.setBackgroundColor(w);
            shopButton.setBackgroundColor(w);
            viewPager.setCurrentItem(0);
            break;

//         case R.id.server:
//            goodsButton.setBackgroundColor(w);
//            serverButton.setBackgroundColor(c);
//            shopButton.setBackgroundColor(w);
//            viewPager.setCurrentItem(1);
//            break;
//
//         case R.id.shop :
//            goodsButton.setBackgroundColor(w);
//            serverButton.setBackgroundColor(w);
//            shopButton.setBackgroundColor(c);
//            viewPager.setCurrentItem(2);
//            break;

         case R.id.shop:
            goodsButton.setBackgroundColor(w);
//            serverButton.setBackgroundColor(c);
            shopButton.setBackgroundColor(c);
            viewPager.setCurrentItem(1);
            break;
      }

   }

   @Override
   protected void onStart() {
      super.onStart();
      loadData();
   }

   /**
    * 取消收藏
    */
   private void CancelCollection() {
      CollectsDelRequestObject object = new CollectsDelRequestObject();
      object.setParam(new CollectsDelRequestParam());
      object.getParam().setCollectionId(shopId);

      httpTool.post(object, URLConfig.DIS_COLLECT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  isCollected = "0";
                  bt_action_image.setText("收藏");
                  Toast.makeText(ShopDetailsOnlineActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();

               }
            }

            //        lv_recommend.onRefreshComplete();
         }

         @Override
         public void onError(ResponseBaseObject o) {

            Toast.makeText(ShopDetailsOnlineActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void loadData() {
//      Bitmap defaultBitmap  = BitmapFactory.decodeResource(getResources(),R.mipmap.shangjia_defaullt);
//      shopBGView.setImageBitmap(FastBlurUtil.toBlur(defaultBitmap,8));
//      shopImageView.setImageBitmap(CuttingBitmap.toRoundBitmap(defaultBitmap));
//
//      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
//      shopInfoRequestParam.setShopId(shopId);
//      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
//      shopInfoRequestObject.setParam(shopInfoRequestParam);
//      httpTool.post(shopInfoRequestObject, HttpUrlConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
//         @Override
//         public void onSuccess(ShopInfoResponseObject o) {
//            ShopInfoResponseParam shop = o.getShop();
//            String name = shop.getName();
//            String startTime = shop.getStartTime();
//            String endTime = shop.getEndTime();
//            String notes = shop.getNotice();
//            String image = shop.getImgurl();
//            shopNameView.setText(name);
//            shopTimeView.setText("营业时间："+startTime+"-"+endTime);
//            shopTipView.setText(notes);
//            isCollected = shop.getIsCollected();
//            if(!TextUtils.isEmpty(image)){
//
//               ImageLoader.getInstance().loadImage(image,new SimpleImageLoadingListener(){
//                  @Override
//                  public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                     super.onLoadingComplete(imageUri, view, loadedImage);
//                     shopBGView.setImageBitmap(FastBlurUtil.toBlur(loadedImage,6));
//                     shopImageView.setImageBitmap(CuttingBitmap.toRoundBitmap(loadedImage));
//                  }
//               });
//            }
//            sc();
//         /*       if (TextUtils.equals("1", isCollected)) {
//                    collectButton.setBackgroundResource(R.drawable.collected_btn);
//                    collectButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            CancelCollection();
//                        }
//                    });
//
//                } else {
//
//                    collectButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            collectShop();
//                        }
//                    });
//
//                }*/
////                if (TextUtils.equals("1",isCollected)){
////                    collectButton.setChecked(true);
////                    collectButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                        @Override
////                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                            if (!LoginTool.login(ShopActivity.this)) {
////
////                                return;
////                            }
////                    /*if (isChecked){
////
////                    }*/
////                            CancelCollection();
////                        }
////                    });
////                }else{
////                    collectButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                        @Override
////                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                            if (!LoginTool.login(ShopActivity.this)) {
////
////                                return;
////                            }
////                            if (isChecked){
////                                collectShop();
////                            }
////                        }
////                    });
////                }
//
//         }
//         @Override
//         public void onError(ShopInfoResponseObject o) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
//         }
//      });

   }

   private void sc() {
//      //    Toast.makeText(ShopActivity.this,isCollected,Toast.LENGTH_SHORT).show();
//      if (TextUtils.equals("1", isCollected)) {
//         collectButton.setBackgroundResource(R.drawable.collected_btn);
//         collectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               CancelCollection();
//            }
//         });
//
//      } else {
//         collectButton.setBackgroundResource(R.drawable.uncollect_icon);
//         collectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               collectShop();
//            }
//         });
//
//      }
   }

   //收藏商家
   private void collectShop() {
      CollectsAddRequestParam collectsAddRequestParam = new CollectsAddRequestParam();
      collectsAddRequestParam.setObjectId(shopId);
      collectsAddRequestParam.setType("1");
      CollectsAddRequestObject collectsAddRequestObject = new CollectsAddRequestObject();
      collectsAddRequestObject.setParam(collectsAddRequestParam);
      httpTool.post(collectsAddRequestObject, URLConfig.COLLECT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            isCollected = "1";

            bt_action_image.setText("已收藏");
            Toast.makeText(ShopDetailsOnlineActivity.this, "收藏商家", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onError(ResponseBaseObject o) {
            Toast.makeText(ShopDetailsOnlineActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }


   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
   }

   @Override
   public void onPageSelected(int position) {
   }

   @Override
   public void onPageScrollStateChanged(int state) {
      if (state == 2) {
         switch (viewPager.getCurrentItem()) {
            case 0:
               radioGroup.check(R.id.goods);
               break;
//            case 1:
//               radioGroup.check(R.id.server);
//               break;
//            case 2:
//               radioGroup.check(R.id.shop);
//               break;
            case 1:
               radioGroup.check(R.id.shop);
               break;

         }
      }
   }


   @Override
   public void onClick(View v) {

      switch (v.getId()) {
         case R.id.back:
            finish();
            break;
//         case R.id.search ://查询
//            SearchGoodsActivity.startSearchGoodsActivity(ShopActivity.this);
//            break;
         case R.id.tv_shop_details_online_notice:
            notice();
            break;
      }
   }

   /**
    * 商家详情
    */
   private void request() {
      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
      shopInfoRequestParam.setShopId(shopId);
      shopInfoRequestObject.setParam(shopInfoRequestParam);
      httpTool.post(shopInfoRequestObject, URLConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
         @Override
         public void onSuccess(ShopInfoResponseObject o) {

            //获取开始时间分钟
            String Start = o.getShop().getStartTime().substring(o.getShop().getStartTime().indexOf(":") + 1, o.getShop().getStartTime().length());
            //获取开始时间小时
            String StartHour = o.getShop().getStartTime().substring(0, o.getShop().getStartTime().lastIndexOf(":"));
            //获取结束时间分钟
            String End = o.getShop().getEndTime().substring(o.getShop().getEndTime().indexOf(":") + 1, o.getShop().getEndTime().length());
            //获取结束时间小时
            String EndHour = o.getShop().getEndTime().substring(0, o.getShop().getEndTime().lastIndexOf(":"));

            Log.i("开始时间", StartHour);

            Calendar cal = Calendar.getInstance();// 当前日期
            int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
            int minute = cal.get(Calendar.MINUTE);// 获取分钟
            int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
            final int start = Integer.parseInt(StartHour) * 60 + Integer.parseInt(Start);// 起始时间 17:20的分钟数
            final int end = Integer.parseInt(EndHour) * 60 + Integer.parseInt(End);// 结束时间 19:00的分钟数
            if (minuteOfDay >= start && minuteOfDay <= end) {
               /*System.out.println("在外围内");
               Toast.makeText(ShopDetailsOnlineActivity.this,"在外围内",Toast.LENGTH_LONG).show();*/
               tv_operating_state.setText("正在\n营业");
               State = 0;

            } else {
               /*System.out.println("在外围外");
               Toast.makeText(ShopDetailsOnlineActivity.this,"在外围外",Toast.LENGTH_LONG).show();*/
               tv_operating_state.setText("店家\n休息");
               tv_operating_state.setTextColor(ShopDetailsOnlineActivity.this.getResources().getColor(R.color.shopstate));
               State = 1;
            }


            //加载商品
            //      searchButton.setOnClickListener(this);
            fragmentArrayList = new ArrayList<>();
            fragmentArrayList.add(GoodsFragment.newInstance(shopId, State + "", tv_shop_name.getText().toString().trim()));
//         fragmentArrayList.add(ServerFragment.newInstance(shopId,""));
            fragmentArrayList.add(ShopFragment.newInstance(shopId, ""));
            customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
            viewPager.setAdapter(customFragmentPagerAdapter);
            viewPager.addOnPageChangeListener(ShopDetailsOnlineActivity.this);
            radioGroup.setOnCheckedChangeListener(ShopDetailsOnlineActivity.this);







            tv_shop_name.setText(o.getShop().getName());
            if (o.getShop().getAddr() != null) {
               tv_shop_addr.setText("(" + o.getShop().getAddr() + ")");
            }

            //         tv_shop_salenum.setText("月销售" + o.getShop().getSaleNum());
            tv_shop_salenum.setText("月销售" + o.getShop().getMonthSalenum() + "笔");
            tv_shop_notice.setText(o.getShop().getAdContent());

            if (o.getShop().getIsCollected().equals("0") && o.getShop().getIsCollected() != null) { //针对登录用户：0：未收藏 1已收藏
               bt_action_image.setText("收藏");
               isCollected = o.getShop().getIsCollected();
            } else {
               bt_action_image.setText("已收藏");
               isCollected = o.getShop().getIsCollected();
            }
            imgurl = o.getShop().getImgurl();
            loadPics();
//            if (!TextUtils.isEmpty(o.getShop().getImgurl())) {
//               ImageLoader.getInstance().displayImage(o.getShop().getImgurl(), iv_imgurl);
//            }

            /*
            shopInfo = o.getShop();
            String path = o.getShop().getImgurl();
            ImageLoader.getInstance().displayImage(path, iv_shop_img);
            tv_shop_name.setText(o.getShop().getName());
            int score = o.getShop().getScore().intValue();
            rating_bar_shop_details.setRating(score);
            tv_shop_xx.setText(score + "");
            String cateName = o.getShop().getCategoryName();
            if (TextUtils.isEmpty(cateName)){
               cateName = "其他";
            }
            tv_shop_type.setText(cateName);
            tv_shop_address.setText(o.getShop().getAddr());
            tv_shop_phone.setText(o.getShop().getPhone());
            tv_shop_hours.setText("营业时间：" + o.getShop().getStartTime() + "-" + o.getShop().getEndTime());
            ShopPhone = o.getShop().getPhone();
            name = o.getShop().getName();
            info = o.getShop().getInfo();
            titleView.setText(name);*/
            if (onloadDataListener != null) {
               onloadDataListener.onLoad();
            }
         }

         @Override
         public void onError(ShopInfoResponseObject o) {

            Toast.makeText(ShopDetailsOnlineActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });

   }

   public interface OnloadDataListener {
      void onLoad();
   }

   public void setOnloadDataListener(OnloadDataListener onloadDataListener) {
      this.onloadDataListener = onloadDataListener;
   }

   AlertDialog alertDialog2;

   private void notice() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(ShopDetailsOnlineActivity.this, R.layout.activity_shop_details_online_notice, null);

      TextView tv_dialog_notice = (TextView) view.findViewById(R.id.tv_dialog_notice);
      ImageView iv_dialog_dismiss = (ImageView) view.findViewById(R.id.iv_dialog_dismiss);

      tv_dialog_notice.setText("\t\t" + tv_shop_notice.getText().toString().trim());
      iv_dialog_dismiss.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alertDialog2.dismiss();
         }
      });
      builder.setView(view);
      builder.setCancelable(false);
      alertDialog2 = builder.create();
      alertDialog2.show();
   }

   private void loadPics() {
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setFirstQueryTime("");
      paginationBaseObject.setPage(1);
      paginationBaseObject.setRows(100);
      ShopImgListRequestObject shopImgListRequestObject = new ShopImgListRequestObject();
      ShopImgListResponseParam shopImgListResponseParam = new ShopImgListResponseParam();
      shopImgListResponseParam.setShopId(shopId);
      shopImgListRequestObject.setParam(shopImgListResponseParam);
      shopImgListRequestObject.setPagination(paginationBaseObject);
      httpTool.post(shopImgListRequestObject, URLConfig.SHOP_PICS, new HttpTool.HttpCallBack<ShopImgListResponseObject>() {
         @Override
         public void onSuccess(ShopImgListResponseObject o) {
            if (o.getRecordList() != null && o.getRecordList().size() > 0) {
//               for (int i = 0; i < o.getRecordList().size(); i++) {
//                  imageList.add(o.getRecordList().get(i).getImgurl());
//               }
//               path = o.getRecordList().get(0).getImgurl();
//               tv_shop_details.setText("1/" + o.getTotalCount());
//               ImageLoader.getInstance().displayImage(o.getRecordList().get(0).getImgurl(), iv_imgurl);
               //================================================================
               path = o.getRecordList().get(0).getImgurl();
//               if (TextUtils.isEmpty(imgurl)) {
////                  iv_imgurl.setImageBitmap(defaultImage);
//                  ImageLoader.getInstance().displayImage(o.getRecordList().get(0).getImgurl(), iv_imgurl);
//                  tv_shop_details.setText("1/" + o.getTotalCount());
//               } else {
//                  ImageLoader.getInstance().displayImage(imgurl, iv_imgurl);
//                  tv_shop_details.setText("1/" + (o.getTotalCount()+1));
//               }
               imageList.add(imgurl);
               for (int i = 0; i < o.getRecordList().size(); i++) {
                  imageList.add(o.getRecordList().get(i).getImgurl());
               }
               if (TextUtils.isEmpty(imgurl)) {
//                  iv_imgurl.setImageBitmap(defaultImage);
                  ImageLoader.getInstance().displayImage(o.getRecordList().get(0).getImgurl(), iv_imgurl);
//                  tv_shop_details.setText("1/" + imageList.size());
               } else {
                  ImageLoader.getInstance().displayImage(imgurl, iv_imgurl);
//                  tv_shop_details.setText("1/" + (imageList.size()));
               }
               tv_shop_details.setText("1/" + imageList.size());
               //==============================================================
            } else {
               path = "";
               if (TextUtils.isEmpty(imgurl)) {
                  iv_imgurl.setImageBitmap(defaultImage);
               } else {
                  ImageLoader.getInstance().displayImage(imgurl, iv_imgurl);
               }
               //====================================
               imageList.add(imgurl);
               //====================================

            }
//            ToastView.show(imageList.size()+";;;");
            //优化版
//            if (!TextUtils.isEmpty(imgurl)) {
//               imageList.add(imgurl);
//            }
//            if(o.getRecordList() != null&&o.getRecordList().size()>0){
//               for (int i = 0; i < o.getRecordList().size(); i++) {
//                  imageList.add(o.getRecordList().get(i).getImgurl());
//               }
//            }
//            if(imageList.size()>0){
//               ImageLoader.getInstance().displayImage(imageList.get(0), iv_imgurl);
//               tv_shop_details.setText("1/" + imageList.size());
//            }else{
//               iv_imgurl.setImageBitmap(defaultImage);
//            }



         }

         @Override
         public void onError(ShopImgListResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }
}
