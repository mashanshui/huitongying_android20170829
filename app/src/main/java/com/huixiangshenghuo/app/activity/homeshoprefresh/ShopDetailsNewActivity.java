package com.huixiangshenghuo.app.activity.homeshoprefresh;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.request.shopcomment.ShopCommentListRequestObject;
import com.doumee.model.request.shopcomment.ShopCommentListRequestParam;
import com.doumee.model.response.comment.ShopCommentListResponseObject;
import com.doumee.model.response.comment.ShopCommentListResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.shop.ShopInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.ShopDetailsAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduPoiSearchActivity;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.home.ZhuanInfoActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ToastView;
import com.huixiangshenghuo.app.view.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/8.
 * 商家详情 新
 */
public class ShopDetailsNewActivity extends Activity implements View.OnClickListener, XListView.IXListViewListener {
   private ImageView iv_shop_img;//商家图片
   private TextView tv_shop_name, titleView;//商家名称
   private RatingBar rating_bar_shop_details;//商家星星
   private TextView tv_shop_xx;//商家评分
   private TextView tv_shop_type;//商家分类
   private TextView tv_shop_address;//商家地址
   private TextView tv_shop_phone;//商家电话
   private WebView wv_info;//商家详情
   private TextView tv_shop_hours;//营业时间
   private XListView lv_shop_comment;//商家评论
   private Button bt_shop_check;//买单
   private ImageView iv_back;//返回

   private TextView tv_picture;//商家图片数量


   private Button bt_phone;//商家电话
   private Button bt_ditu;//地图

   private View rl_businessHead;
   private static String SHOPID = "shopId";

   /**
    * 商家id
    */
   private String shopId;
   /**
    * 商家电话
    */
   private String ShopPhone;
   /**
    * 商家名称
    */
   private String name;
   /**
    * 商家简介
    */
   private String info;
   private HttpTool httpTool;

   private int page = 1;
   private String firstQueryTime;
   private ShopDetailsAdapter mAdapter;
   private Bitmap defaultImage;//

   /**
    * 是否收藏 0：未收藏 1已收藏
    */
   private String isCollected;

   /**
    * 数据源
    */
   private ArrayList<ShopCommentListResponseParam> arrlist = new ArrayList<ShopCommentListResponseParam>();

   private ShopInfoResponseParam shopInfo;

   private String Path;//商家相册
   private String imgurl;//商家图片
   final ArrayList<String> imageList = new ArrayList<>();//商家相册 （将商家首图也放入）

   public static void startShopDetailsActivity(Context context, String shopId) {
      Intent intent = new Intent(context, ShopDetailsNewActivity.class);
      intent.putExtra(SHOPID, shopId);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         Window window = getWindow();

         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
               | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.setStatusBarColor(Color.TRANSPARENT);
      }
      setContentView(R.layout.activity_shop_details_new);
      httpTool = HttpTool.newInstance(this);
      shopId = getIntent().getStringExtra(SHOPID);
      initview();
      request();
//      loadPics();
      refresh();

      loadShopComment();

   }

   private void refresh() {
      mAdapter = new ShopDetailsAdapter(arrlist, ShopDetailsNewActivity.this);
      lv_shop_comment.setAdapter(mAdapter);
   }

   private void initview() {
      lv_shop_comment = (XListView) findViewById(R.id.lv_shop_comment_new);
      rl_businessHead = (View) findViewById(R.id.rl_businessHead_new);
      iv_back = (ImageView) findViewById(R.id.iv_shop_details_back_new);
      titleView = (TextView) findViewById(R.id.title_new);
      bt_phone = (Button) findViewById(R.id.bt_shop_phone_new);
      bt_ditu = (Button) findViewById(R.id.bt_ditu);
      bt_shop_check = (Button) findViewById(R.id.bt_shop_check_new);


      rl_businessHead.getBackground().mutate().setAlpha(0);
      lv_shop_comment.setOnScrollListener(new AbsListView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(AbsListView view, int scrollState) {
         }

         @Override
         public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // 判断当前最上面显示的是不是头布局，因为Xlistview有刷新控件，所以头布局的位置是1，即第二个
            if (firstVisibleItem == 1) {
               // 获取头布局
               View itemView = lv_shop_comment.getChildAt(0);
               if (itemView != null) {
                  // 获取头布局现在的最上部的位置的相反数
                  int top = -itemView.getTop();
                  // 获取头布局的高度
                  int headerHeight = itemView.getHeight();
                  // 满足这个条件的时候，是头布局在XListview的最上面第一个控件的时候，只有这个时候，我们才调整透明度
                  if (top <= headerHeight && top >= 0) {
                     // 获取当前位置占头布局高度的百分比
                     float f = (float) top / (float) headerHeight;
                     rl_businessHead.getBackground().mutate().setAlpha((int) (f * 255));
                     // 通知标题栏刷新显示
                     rl_businessHead.invalidate();
                  }
               }
            } else if (firstVisibleItem > 1) {
               rl_businessHead.getBackground().mutate().setAlpha(255);
            } else {
               rl_businessHead.getBackground().mutate().setAlpha(0);
            }
         }
      });

      lv_shop_comment.setXListViewListener(this);
      lv_shop_comment.setPullRefreshEnable(true);
      lv_shop_comment.setPullLoadEnable(true);


      initDate();


   }

   private void initDate() {


      //头部
      View headView = View.inflate(ShopDetailsNewActivity.this, R.layout.activity_shop_details_top_new, null);

      iv_shop_img = (ImageView) headView.findViewById(R.id.iv_shop_top_picture_top_new);
      tv_shop_name = (TextView) headView.findViewById(R.id.tv_shop_name_top_new);
      rating_bar_shop_details = (RatingBar) headView.findViewById(R.id.rating_bar_shop_details_top_new);
      tv_shop_xx = (TextView) headView.findViewById(R.id.tv_shop_xx_top_new);
      tv_shop_type = (TextView) headView.findViewById(R.id.tv_shop_type_top_new);
      tv_shop_address = (TextView) headView.findViewById(R.id.tv_shop_address_top_new);
      tv_shop_phone = (TextView) headView.findViewById(R.id.tv_shop_phone_top_new);
      wv_info = (WebView) headView.findViewById(R.id.wv_shop_details_top_new);
      tv_shop_hours = (TextView) headView.findViewById(R.id.tv_shop_hours_top_new);
      tv_picture = (TextView) headView.findViewById(R.id.tv_shop_details_picture_top_new);

      lv_shop_comment.addHeaderView(headView);


      iv_back.setOnClickListener(this);
      bt_shop_check.setOnClickListener(this);
      tv_shop_phone.setOnClickListener(this);
      tv_shop_address.setOnClickListener(this);
      bt_phone.setOnClickListener(this);
      bt_ditu.setOnClickListener(this);
      //默认图片
      defaultImage = BitmapFactory.decodeResource(this.getResources(), R.mipmap.business_default);


   }

   @Override
   public void onRefresh() {
      page = 1;
      firstQueryTime = "";
      request();
      loadShopComment();
   }

   @Override
   public void onLoadMore() {
      page++;
      loadShopComment();
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.tv_shop_address_top_new:
            if (shopInfo == null) {
               ToastView.show("网络不佳,请检测网络");
            } else {
               if (shopInfo.getLatitude() == null || shopInfo.getLatitude() <= 0) {
                  ToastView.show("暂无商家地址");
               } else {
                  BaiduPoiSearchActivity.startBaiduPoiSearchActivity(ShopDetailsNewActivity.this, shopInfo.getLongitude(), shopInfo.getLatitude(), BaiduPoiSearchActivity.FLAG_SHOW_ADDRESS);
               }
            }


            break;
         case R.id.iv_shop_details_back_new:
            finish();
            break;
         case R.id.bt_shop_check_new:
            ZhuanInfoActivity.startZhuanInfoActivity(ShopDetailsNewActivity.this, shopInfo);
            break;
         case R.id.tv_shop_phone_top_new:
            dh();
            break;
         case R.id.bt_shop_phone_new:
            dh();
            break;
         case R.id.bt_ditu:
            if (shopInfo == null) {
               ToastView.show("网络不佳,请检测网络");
            } else {
            if (shopInfo.getLatitude() == null || shopInfo.getLatitude() <= 0) {
               ToastView.show("暂无商家地址");
            } else {
               Intent intent3 = new Intent(ShopDetailsNewActivity.this, BaiduRoteActivity.class);
               Bundle bundle = new Bundle();
               bundle.putDouble(BaiduRoteActivity.LAN, shopInfo.getLatitude());
               bundle.putDouble(BaiduRoteActivity.LNG, shopInfo.getLongitude());
               intent3.putExtras(bundle);
               startActivity(intent3);
            }
            }

            break;

         default:
            break;
      }
   }


   /**
    * 商家详情
    */
   private void request() {
      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
      shopInfoRequestParam.setShopId(shopId);
      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
      shopInfoRequestObject.setParam(shopInfoRequestParam);
      httpTool.post(shopInfoRequestObject, URLConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
         @Override
         public void onSuccess(ShopInfoResponseObject o) {
            lv_shop_comment.stopLoadMore();
            lv_shop_comment.stopRefresh();
            shopInfo = o.getShop();
            String path = o.getShop().getImgurl();
            imgurl = o.getShop().getImgurl();
            //         ImageLoader.getInstance().displayImage(path, iv_shop_img);
            ImageLoader.getInstance().displayImage(path, iv_shop_img);
            tv_shop_name.setText(o.getShop().getName());
            int score = o.getShop().getScore().intValue();
            rating_bar_shop_details.setRating(score);
            tv_shop_xx.setText(score + "");
            String cateName = o.getShop().getCategoryName();
            if (TextUtils.isEmpty(cateName)) {
               cateName = "其他";
            }
            tv_shop_type.setText(cateName);
            tv_shop_address.setText(o.getShop().getAddr());
            tv_shop_phone.setText(o.getShop().getPhone());
            tv_shop_hours.setText("营业时间：" + o.getShop().getStartTime() + "-" + o.getShop().getEndTime());
            ShopPhone = o.getShop().getPhone();
            name = o.getShop().getName();
            info = o.getShop().getInfo();
            titleView.setText(name);
            wv_info.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);
//            loadPics();

         }

         @Override
         public void onError(ShopInfoResponseObject o) {
            lv_shop_comment.stopLoadMore();
            lv_shop_comment.stopRefresh();
            ToastView.show(o.getErrorMsg());
         }
      });

   }

   //商家评论
   private void loadShopComment() {
      ShopCommentListRequestParam shopCommentListRequestParam = new ShopCommentListRequestParam();
      shopCommentListRequestParam.setShopId(shopId);
      shopCommentListRequestParam.setScoreLevel("0");//0全部1好评2中评3差评

      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(page);
      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
      if (page == 1) {
         paginationBaseObject.setFirstQueryTime("");
      } else {
         paginationBaseObject.setFirstQueryTime(firstQueryTime);
      }


      ShopCommentListRequestObject shopCommentListRequestObject = new ShopCommentListRequestObject();
      shopCommentListRequestObject.setParam(shopCommentListRequestParam);
      shopCommentListRequestObject.setPagination(paginationBaseObject);

      httpTool.post(shopCommentListRequestObject, URLConfig.SHOP_COMMENT, new HttpTool.HttpCallBack<ShopCommentListResponseObject>() {
         @Override
         public void onSuccess(ShopCommentListResponseObject o) {
            lv_shop_comment.stopLoadMore();
            lv_shop_comment.stopRefresh();
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {

                  if (page == 1 && !arrlist.isEmpty()) {
                     //清空
                     arrlist.clear();
                  }
                  //            lastrefereeid=response.getLastRefereeId();
                  firstQueryTime = o.getFirstQueryTime();
                  lv_shop_comment.setRefreshTime(o.getFirstQueryTime());
                  arrlist.addAll(o.getRecordList());
                  mAdapter.notifyDataSetChanged();

                  if (o.getRecordList().size() <= 0) {
                     page--;
                  }


               }
            }
         }

         @Override
         public void onError(ShopCommentListResponseObject o) {
            lv_shop_comment.stopLoadMore();
            lv_shop_comment.stopRefresh();
            ToastView.show(o.getErrorMsg());

         }
      });
   }

   private void dh() {
      final Dialog dialog1 = new Dialog(ShopDetailsNewActivity.this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage(ShopPhone);
      dialog1.setConfirmText("呼叫");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ShopPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog1.dismiss();
         }
      });
      dialog1.show();
   }


}


//public class ShopDetailsNewActivity extends Activity {
//   private ImageView iv_shop_img;//商家图片
//   private TextView tv_shop_name, titleView;//商家名称
//   private RatingBar rating_bar_shop_details;//商家星星
//   private TextView tv_shop_xx;//商家评分
//   private TextView tv_shop_type;//商家分类
//   private TextView tv_shop_address;//商家地址
//   private TextView tv_shop_phone;//商家电话
//   private WebView wv_info;
//   ;//商家详情
//   private TextView tv_shop_hours;//营业时间
//   private MyListView lv_shop_comment;//商家评论
//   private Button bt_shop_check;//买单
//   private ImageView iv_back;//返回
//
//   private TextView tv_picture;//商家图片数量
//
//
//   private Button bt_phone;//商家电话
//   private Button bt_ditu;//地图
//
//   private ListenerScrollView listenerScrollView;
//   private View rl_businessHead;
//   private static String SHOPID = "shopId";
//
//   /**
//    * 商家id
//    */
//   private String shopId;
//   /**
//    * 商家电话
//    */
//   private String ShopPhone;
//   /**
//    * 商家名称
//    */
//   private String name;
//   /**
//    * 商家简介
//    */
//   private String info;
//   private HttpTool httpTool;
//
//   private int page = 1;
//   private String firstQueryTime;
//   private ShopDetailsAdapter mAdapter;
//   private Bitmap defaultImage;//
//
//   /**
//    * 是否收藏 0：未收藏 1已收藏
//    */
//   private String isCollected;
//
//   /**
//    * 数据源
//    */
//   private ArrayList<ShopCommentListResponseParam> arrlist = new ArrayList<ShopCommentListResponseParam>();
//
//   private ShopInfoResponseParam shopInfo;
//
//   private String Path;//商家相册
//   private String imgurl;//商家图片
//   final ArrayList<String> imageList = new ArrayList<>();//商家相册 （将商家首图也放入）
//
//   public static void startShopDetailsActivity(Context context, String shopId) {
//      Intent intent = new Intent(context, ShopDetailsNewActivity.class);
//      intent.putExtra(SHOPID, shopId);
//      context.startActivity(intent);
//   }
//
//   @Override
//   protected void onCreate(Bundle savedInstanceState) {
//      super.onCreate(savedInstanceState);
//      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//         Window window = getWindow();
//
//         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//               | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//         window.setStatusBarColor(Color.TRANSPARENT);
//      }
//      setContentView(R.layout.activity_shop_details_new);
//      httpTool = HttpTool.newInstance(this);
//      shopId = getIntent().getStringExtra(SHOPID);
//      initview();
//      request();
////      loadPics();
//      refresh();
//
//      loadShopComment();
////      iv_shop_img.setOnClickListener(new View.OnClickListener() {
////         @Override
////         public void onClick(View v) {
////
////            if (imageList.size() > 0) {
//////               PhotoActivity.startPhotoActivity(ShopDetailsActivity.this, imgurl, imageList);
////               PhotoActivity.startPhotoActivity(ShopDetailsNewActivity.this, imageList.get(0), imageList);
////            } else {
////               ToastView.show("暂无更多图片");
////            }
////
////
////         }
////      });
//   }
//
//   private void refresh() {
//      mAdapter = new ShopDetailsAdapter(arrlist, ShopDetailsNewActivity.this);
//      lv_shop_comment.setAdapter(mAdapter);
//   }
//
//   private void initview() {
//      iv_shop_img = (ImageView) findViewById(R.id.iv_shop_top_picture_new);
//      tv_shop_name = (TextView) findViewById(R.id.tv_shop_name_new);
//      rating_bar_shop_details = (RatingBar) findViewById(R.id.rating_bar_shop_details_new);
//      tv_shop_xx = (TextView) findViewById(R.id.tv_shop_xx_new);
//      tv_shop_type = (TextView) findViewById(R.id.tv_shop_type_new);
//      tv_shop_address = (TextView) findViewById(R.id.tv_shop_address_new);
//      tv_shop_phone = (TextView) findViewById(R.id.tv_shop_phone_new);
//      wv_info = (WebView) findViewById(R.id.wv_shop_details_new);
//      tv_shop_hours = (TextView) findViewById(R.id.tv_shop_hours_new);
//      lv_shop_comment = (MyListView) findViewById(R.id.lv_shop_comment_new);
//      bt_shop_check = (Button) findViewById(R.id.bt_shop_check_new);
//      listenerScrollView = (ListenerScrollView) findViewById(R.id.rl_sx_new);
//      rl_businessHead = (View) findViewById(R.id.rl_businessHead_new);
//      iv_back = (ImageView) findViewById(R.id.iv_shop_details_back_new);
//      titleView = (TextView) findViewById(R.id.title_new);
//      bt_phone = (Button) findViewById(R.id.bt_shop_phone_new);
//      bt_ditu = (Button) findViewById(R.id.bt_ditu);
//
//      tv_picture = (TextView) findViewById(R.id.tv_shop_details_picture_new);
//
//      rl_businessHead.setAlpha(0);
//      listenerScrollView.setOnScrollChangeListener(new ListenerScrollView.OnScrollChangeListener() {
//         @Override
//         public void onScroll(int l, int t, int oldl, int oldt) {
//            if (t <= 300) {
//               rl_businessHead.setAlpha(t / 300f);
//               if (t >= 50) {
//                  //            ll_title.setBackground(null);
//                  //            tv_menu.setBackground(null);
//               } else {
//                  //            ll_title.setBackground(GlobalConfig.getAppContext().getResources().getDrawable(R.drawable.bg_title_text_business));
//                  //            tv_menu.setBackground(GlobalConfig.getAppContext().getResources().getDrawable(R.drawable.bg_title_business));
//               }
//
//            } else {
//               rl_businessHead.setAlpha(1);
//            }
//         }
//      });
//      //默认图片
//      defaultImage = BitmapFactory.decodeResource(this.getResources(), R.mipmap.business_default);
//
//      initDate();
//   }
//
//   private void initDate() {
//      iv_back.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//      bt_shop_check.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//      tv_shop_phone.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//
//      tv_shop_address.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//      bt_phone.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//      bt_ditu.setOnClickListener(new ShopDetailsNewActivity.MyOnClick());
//   }
//
//
//   class MyOnClick implements View.OnClickListener {
//
//      @Override
//      public void onClick(View arg0) {
//         switch (arg0.getId()) {
//            case R.id.tv_shop_address_new:
//
//               if (shopInfo.getLatitude() == null || shopInfo.getLatitude() <= 0) {
//                  ToastView.show("暂无商家地址");
//               } else {
//                  BaiduPoiSearchActivity.startBaiduPoiSearchActivity(ShopDetailsNewActivity.this, shopInfo.getLongitude(), shopInfo.getLatitude(), BaiduPoiSearchActivity.FLAG_SHOW_ADDRESS);
//               }
//
//               break;
//            case R.id.iv_shop_details_back_new:
//               finish();
//               break;
//            case R.id.bt_shop_check_new:
//               ZhuanInfoActivity.startZhuanInfoActivity(ShopDetailsNewActivity.this, shopInfo);
//               break;
//            case R.id.tv_shop_phone_new:
//               dh();
//               break;
//            case R.id.bt_shop_phone_new:
//               dh();
//               break;
//            case R.id.bt_ditu:
//               if (shopInfo.getLatitude() == null || shopInfo.getLatitude() <= 0) {
//                  ToastView.show("暂无商家地址");
//               } else {
//                  Intent intent3 = new Intent(ShopDetailsNewActivity.this, BaiduRoteActivity.class);
//                  Bundle bundle = new Bundle();
//                  bundle.putDouble(BaiduRoteActivity.LAN, shopInfo.getLatitude());
//                  bundle.putDouble(BaiduRoteActivity.LNG, shopInfo.getLongitude());
//                  intent3.putExtras(bundle);
//                  startActivity(intent3);
//               }
//
//
//               break;
//
//            default:
//               break;
//         }
//
//      }
//
//   }
//
//   /**
//    * 商家详情
//    */
//   private void request() {
//      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
//      shopInfoRequestParam.setShopId(shopId);
//      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
//      shopInfoRequestObject.setParam(shopInfoRequestParam);
//      httpTool.post(shopInfoRequestObject, URLConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
//         @Override
//         public void onSuccess(ShopInfoResponseObject o) {
//
//            shopInfo = o.getShop();
//            String path = o.getShop().getImgurl();
//            imgurl = o.getShop().getImgurl();
//            //         ImageLoader.getInstance().displayImage(path, iv_shop_img);
//            ImageLoader.getInstance().displayImage(path, iv_shop_img);
//            tv_shop_name.setText(o.getShop().getName());
//            int score = o.getShop().getScore().intValue();
//            rating_bar_shop_details.setRating(score);
//            tv_shop_xx.setText(score + "");
//            String cateName = o.getShop().getCategoryName();
//            if (TextUtils.isEmpty(cateName)) {
//               cateName = "其他";
//            }
//            tv_shop_type.setText(cateName);
//            tv_shop_address.setText(o.getShop().getAddr());
//            tv_shop_phone.setText(o.getShop().getPhone());
//            tv_shop_hours.setText("营业时间：" + o.getShop().getStartTime() + "-" + o.getShop().getEndTime());
//            ShopPhone = o.getShop().getPhone();
//            name = o.getShop().getName();
//            info = o.getShop().getInfo();
//            titleView.setText(name);
//            wv_info.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);
////            loadPics();
//
//         }
//
//         @Override
//         public void onError(ShopInfoResponseObject o) {
//
//            ToastView.show(o.getErrorMsg());
//         }
//      });
//
//   }
//
//   //商家评论
//   private void loadShopComment() {
//      ShopCommentListRequestParam shopCommentListRequestParam = new ShopCommentListRequestParam();
//      shopCommentListRequestParam.setShopId(shopId);
//      shopCommentListRequestParam.setScoreLevel("0");//0全部1好评2中评3差评
//
//      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
//      paginationBaseObject.setPage(page);
//      paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
//      paginationBaseObject.setFirstQueryTime(firstQueryTime);
//
//      ShopCommentListRequestObject shopCommentListRequestObject = new ShopCommentListRequestObject();
//      shopCommentListRequestObject.setParam(shopCommentListRequestParam);
//      shopCommentListRequestObject.setPagination(paginationBaseObject);
//
//      httpTool.post(shopCommentListRequestObject, URLConfig.SHOP_COMMENT, new HttpTool.HttpCallBack<ShopCommentListResponseObject>() {
//         @Override
//         public void onSuccess(ShopCommentListResponseObject o) {
//
//            if (o.getErrorCode().equals("00000")) {
//               if (o.getErrorMsg().equals("success")) {
//
//                  if (page == 1 && !arrlist.isEmpty()) {
//                     //清空
//                     arrlist.clear();
//                  }
//                  //            lastrefereeid=response.getLastRefereeId();
//                  firstQueryTime = o.getFirstQueryTime();
//
//                  arrlist.addAll(o.getRecordList());
//
//                  mAdapter.notifyDataSetChanged();
//
//
//               }
//            }
//         }
//
//         @Override
//         public void onError(ShopCommentListResponseObject o) {
//            ToastView.show(o.getErrorMsg());
//
//         }
//      });
//   }
//
//   private void dh() {
//      final Dialog dialog1 = new Dialog(ShopDetailsNewActivity.this);
//      dialog1.setTitle("温馨提示");
//      dialog1.setMessage(ShopPhone);
//      dialog1.setConfirmText("呼叫");
//      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
//         @Override
//         public void onClick(DialogInterface dialog, int which) {
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ShopPhone));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            dialog1.dismiss();
//         }
//      });
//      dialog1.show();
//   }
//
////   private void loadPics() {
////      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
////      paginationBaseObject.setFirstQueryTime("");
////      paginationBaseObject.setPage(1);
////      paginationBaseObject.setRows(100);
////      ShopImgListRequestObject shopImgListRequestObject = new ShopImgListRequestObject();
////      ShopImgListResponseParam shopImgListResponseParam = new ShopImgListResponseParam();
////      shopImgListResponseParam.setShopId(shopId);
////      shopImgListRequestObject.setParam(shopImgListResponseParam);
////      shopImgListRequestObject.setPagination(paginationBaseObject);
////      httpTool.post(shopImgListRequestObject, URLConfig.SHOP_PICS, new HttpTool.HttpCallBack<ShopImgListResponseObject>() {
////         @Override
////         public void onSuccess(ShopImgListResponseObject o) {
////            if (o.getRecordList() != null && o.getRecordList().size() > 0) {
//////               for (int i = 0; i < o.getRecordList().size(); i++) {
//////                  imageList.add(o.getRecordList().get(i).getImgurl());
//////               }
////               Path = o.getRecordList().get(0).getImgurl();
////               //      tv_shop_details.setText("1/"+o.getTotalCount());
////               //              tv_picture.setText("1/" + o.getTotalCount());
////
//////               ImageLoader.getInstance().displayImage(Path, iv_shop_img);
////               //===================================================================
//////               if (TextUtils.isEmpty(imgurl)) {
////////                  iv_shop_img.setImageBitmap(defaultImage);
//////                  ImageLoader.getInstance().displayImage(Path, iv_shop_img);
//////                  tv_picture.setText("1/" + o.getTotalCount());
//////               } else {
//////                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
//////                  tv_picture.setText("1/" + (o.getTotalCount()+1));
//////               }
////               imageList.add(imgurl);
////               for (int i = 0; i < o.getRecordList().size(); i++) {
////                  imageList.add(o.getRecordList().get(i).getImgurl());
////               }
////               if (TextUtils.isEmpty(imgurl)) {
//////                  iv_shop_img.setImageBitmap(defaultImage);
////                  ImageLoader.getInstance().displayImage(Path, iv_shop_img);
//////                  tv_picture.setText("1/" + imageList.size());
////               } else {
////                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
//////                  tv_picture.setText("1/" + imageList.size());
////               }
////               tv_picture.setText("1/" + imageList.size());
////               //=================================================================
////
////            } else {
////               Path = "";
////
////               if (TextUtils.isEmpty(imgurl)) {
////                  iv_shop_img.setImageBitmap(defaultImage);
////               } else {
////                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
////               }
////               //====================================
////               imageList.add(imgurl);
////               //====================================
////               /* ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);*/
////            }
////            //优化版
//////            if (!TextUtils.isEmpty(imgurl)) {
//////               imageList.add(imgurl);
//////            }
//////            if(o.getRecordList() != null&&o.getRecordList().size()>0){
//////               for (int i = 0; i < o.getRecordList().size(); i++) {
//////                  imageList.add(o.getRecordList().get(i).getImgurl());
//////               }
//////            }
//////            if(imageList.size()>0){
//////               ImageLoader.getInstance().displayImage(imageList.get(0), iv_shop_img);
//////               tv_picture.setText("1/" + imageList.size());
//////            }else{
//////               iv_shop_img.setImageBitmap(defaultImage);
//////            }
////
////
////         }
////
////         @Override
////         public void onError(ShopImgListResponseObject o) {
////
////            ToastView.show(o.getErrorMsg());
////         }
////      });
////   }
//
//
//}
