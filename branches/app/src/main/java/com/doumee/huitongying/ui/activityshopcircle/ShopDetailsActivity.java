package com.huixiangshenghuo.app.ui.activityshopcircle;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.collects.CollectsAddRequestObject;
import com.doumee.model.request.collects.CollectsAddRequestParam;
import com.doumee.model.request.collects.CollectsDelRequestObject;
import com.doumee.model.request.collects.CollectsDelRequestParam;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.request.shopImg.ShopImgListRequestObject;
import com.doumee.model.request.shopcomment.ShopCommentListRequestObject;
import com.doumee.model.request.shopcomment.ShopCommentListRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.comment.ShopCommentListResponseObject;
import com.doumee.model.response.comment.ShopCommentListResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.shop.ShopInfoResponseParam;
import com.doumee.model.response.shopImg.ShopImgListResponseObject;
import com.doumee.model.response.shopImg.ShopImgListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.ShopDetailsAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduPoiSearchActivity;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.home.ZhuanInfoActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ListenerScrollView;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import static com.huixiangshenghuo.app.R.id.tv_shop_details;


/**
 * Created by Administrator on 2016/12/15.
 * 商家详情
 */
public class ShopDetailsActivity extends Activity {
   private ImageView iv_shop_img;//商家图片
   private TextView tv_shop_name,titleView;//商家名称
   private RatingBar rating_bar_shop_details;//商家星星
   private TextView tv_shop_xx;//商家评分
   private TextView tv_shop_type;//商家分类
   private TextView tv_shop_address;//商家地址
   private TextView tv_shop_phone;//商家电话
   private TextView rl_shop_details;//商家详情
   private TextView tv_shop_hours;//营业时间
   private MyListView lv_shop_comment;//商家评论
   private Button bt_shop_check;//买单
   private ImageView iv_back;//返回
   private Button bt_sc;//收藏
   private TextView tv_picture;//商家图片数量

   private ListenerScrollView listenerScrollView;
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
      Intent intent = new Intent(context, ShopDetailsActivity.class);
      intent.putExtra(SHOPID, shopId);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         Window window = getWindow();
//         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                 | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                 | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//         window.setStatusBarColor(Color.TRANSPARENT);
//         window.setNavigationBarColor(Color.TRANSPARENT);
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
               | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.setStatusBarColor(Color.TRANSPARENT);
      }
      setContentView(R.layout.activity_shop_details);
      httpTool = HttpTool.newInstance(this);
      shopId = getIntent().getStringExtra(SHOPID);
      initview();
      request();
//      loadPics();
      refresh();

      loadShopComment();
      iv_shop_img.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            if (TextUtils.isEmpty(Path)) {
//               ToastView.show("暂无更多图片");
//            } else {
//               PhotoActivity.startPhotoActivity(ShopDetailsActivity.this, Path, imageList);
//            }
//            if (TextUtils.isEmpty(imgurl)) {
//               ToastView.show("暂无更多图片");
//            } else {
//               PhotoActivity.startPhotoActivity(ShopDetailsActivity.this, imgurl, imageList);
//            }
            if (imageList.size() > 0) {
//               PhotoActivity.startPhotoActivity(ShopDetailsActivity.this, imgurl, imageList);
               PhotoActivity.startPhotoActivity(ShopDetailsActivity.this, imageList.get(0), imageList);
            } else {
               ToastView.show("暂无更多图片");
            }



         }
      });
   }

   private void refresh() {
      mAdapter = new ShopDetailsAdapter(arrlist, ShopDetailsActivity.this);
      lv_shop_comment.setAdapter(mAdapter);
   }

   private void initview() {
      iv_shop_img = (ImageView) findViewById(R.id.iv_shop_top_picture);
      tv_shop_name = (TextView) findViewById(R.id.tv_shop_name);
      rating_bar_shop_details = (RatingBar) findViewById(R.id.rating_bar_shop_details);
      tv_shop_xx = (TextView) findViewById(R.id.tv_shop_xx);
      tv_shop_type = (TextView) findViewById(R.id.tv_shop_type);
      tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);
      tv_shop_phone = (TextView) findViewById(R.id.tv_shop_phone);
      rl_shop_details = (TextView) findViewById(tv_shop_details);
      tv_shop_hours = (TextView) findViewById(R.id.tv_shop_hours);
      lv_shop_comment = (MyListView) findViewById(R.id.lv_shop_comment);
      bt_shop_check = (Button) findViewById(R.id.bt_shop_check);
      listenerScrollView = (ListenerScrollView) findViewById(R.id.rl_sx);
      rl_businessHead = (View) findViewById(R.id.rl_businessHead);
      iv_back = (ImageView) findViewById(R.id.iv_shop_details_back);
      titleView = (TextView)findViewById(R.id.title) ;
      bt_sc = (Button) findViewById(R.id.bt_shop_details_sc);
      tv_picture = (TextView) findViewById(R.id.tv_shop_details_picture);

      rl_businessHead.setAlpha(0);
      listenerScrollView.setOnScrollChangeListener(new ListenerScrollView.OnScrollChangeListener() {
         @Override
         public void onScroll(int l, int t, int oldl, int oldt) {
            if (t <= 300) {
               rl_businessHead.setAlpha(t / 300f);
               if (t >= 50) {
                  //            ll_title.setBackground(null);
                  //            tv_menu.setBackground(null);
               } else {
                  //            ll_title.setBackground(GlobalConfig.getAppContext().getResources().getDrawable(R.drawable.bg_title_text_business));
                  //            tv_menu.setBackground(GlobalConfig.getAppContext().getResources().getDrawable(R.drawable.bg_title_business));
               }

            } else {
               rl_businessHead.setAlpha(1);
            }
         }
      });
      //默认图片
      defaultImage = BitmapFactory.decodeResource(this.getResources(), R.mipmap.business_default);

      initDate();
   }

   private void initDate() {
      iv_back.setOnClickListener(new MyOnClick());
      bt_shop_check.setOnClickListener(new MyOnClick());
      tv_shop_phone.setOnClickListener(new MyOnClick());
      rl_shop_details.setOnClickListener(new MyOnClick());
      tv_shop_address.setOnClickListener(new MyOnClick());
      bt_sc.setOnClickListener(new MyOnClick());
   }


   class MyOnClick implements View.OnClickListener {

      @Override
      public void onClick(View arg0) {
         switch (arg0.getId()) {
            case R.id.tv_shop_address:
               BaiduPoiSearchActivity.startBaiduPoiSearchActivity(ShopDetailsActivity.this,shopInfo.getLongitude(),shopInfo.getLatitude(),BaiduPoiSearchActivity.FLAG_SHOW_ADDRESS);
               break;
               case R.id.iv_shop_details_back:
               finish();
               break;
            case R.id.bt_shop_check:
               ZhuanInfoActivity.startZhuanInfoActivity(ShopDetailsActivity.this, shopInfo);
               break;
            case R.id.tv_shop_phone:
               dh();
               break;
            case tv_shop_details:
               ShopInfoActivity.startShopInfoActivity(ShopDetailsActivity.this, info, "1");
               break;
            case R.id.bt_shop_details_sc:
               if (isCollected.equals("0")) {//0 未收藏 1 已收藏
                  collectShop();
               } else {
                  CancelCollection();
               }
               break;

            default:
               break;
         }

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

            shopInfo = o.getShop();
            String path = o.getShop().getImgurl();
            imgurl = o.getShop().getImgurl();
            //         ImageLoader.getInstance().displayImage(path, iv_shop_img);
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
            titleView.setText(name);
            if (o.getShop().getIsCollected().equals("0") && o.getShop().getIsCollected() != null) { //针对登录用户：0：未收藏 1已收藏
               bt_sc.setText("收藏");
               isCollected = o.getShop().getIsCollected();
            } else {
               bt_sc.setText("已收藏");
               isCollected = o.getShop().getIsCollected();
            }
            loadPics();

         }

         @Override
         public void onError(ShopInfoResponseObject o) {

            Toast.makeText(ShopDetailsActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
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
      paginationBaseObject.setFirstQueryTime(firstQueryTime);

      ShopCommentListRequestObject shopCommentListRequestObject = new ShopCommentListRequestObject();
      shopCommentListRequestObject.setParam(shopCommentListRequestParam);
      shopCommentListRequestObject.setPagination(paginationBaseObject);

      httpTool.post(shopCommentListRequestObject, URLConfig.SHOP_COMMENT, new HttpTool.HttpCallBack<ShopCommentListResponseObject>() {
         @Override
         public void onSuccess(ShopCommentListResponseObject o) {

            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {

                  if (page == 1 && !arrlist.isEmpty()) {
                     //清空
                     arrlist.clear();
                  }
                  //            lastrefereeid=response.getLastRefereeId();
                  firstQueryTime = o.getFirstQueryTime();

                  arrlist.addAll(o.getRecordList());

                  mAdapter.notifyDataSetChanged();


               }
            }
         }

         @Override
         public void onError(ShopCommentListResponseObject o) {
            Toast.makeText(ShopDetailsActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();

         }
      });
   }

   private void dh() {
      final Dialog dialog1 = new Dialog(ShopDetailsActivity.this);
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
               Path = o.getRecordList().get(0).getImgurl();
               //      tv_shop_details.setText("1/"+o.getTotalCount());
               //              tv_picture.setText("1/" + o.getTotalCount());

//               ImageLoader.getInstance().displayImage(Path, iv_shop_img);
               //===================================================================
//               if (TextUtils.isEmpty(imgurl)) {
////                  iv_shop_img.setImageBitmap(defaultImage);
//                  ImageLoader.getInstance().displayImage(Path, iv_shop_img);
//                  tv_picture.setText("1/" + o.getTotalCount());
//               } else {
//                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
//                  tv_picture.setText("1/" + (o.getTotalCount()+1));
//               }
               imageList.add(imgurl);
               for (int i = 0; i < o.getRecordList().size(); i++) {
                  imageList.add(o.getRecordList().get(i).getImgurl());
               }
               if (TextUtils.isEmpty(imgurl)) {
//                  iv_shop_img.setImageBitmap(defaultImage);
                  ImageLoader.getInstance().displayImage(Path, iv_shop_img);
//                  tv_picture.setText("1/" + imageList.size());
               } else {
                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
//                  tv_picture.setText("1/" + imageList.size());
               }
               tv_picture.setText("1/" + imageList.size());
               //=================================================================

            } else {
               Path = "";

               if (TextUtils.isEmpty(imgurl)) {
                  iv_shop_img.setImageBitmap(defaultImage);
               } else {
                  ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);
               }
               //====================================
               imageList.add(imgurl);
               //====================================
               /* ImageLoader.getInstance().displayImage(imgurl, iv_shop_img);*/
            }
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
//               ImageLoader.getInstance().displayImage(imageList.get(0), iv_shop_img);
//               tv_picture.setText("1/" + imageList.size());
//            }else{
//               iv_shop_img.setImageBitmap(defaultImage);
//            }


         }

         @Override
         public void onError(ShopImgListResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
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
                  bt_sc.setText("收藏");
                  Toast.makeText(ShopDetailsActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();

               }
            }

            //        lv_recommend.onRefreshComplete();
         }

         @Override
         public void onError(ResponseBaseObject o) {

            Toast.makeText(ShopDetailsActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
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

            bt_sc.setText("已收藏");
            Toast.makeText(ShopDetailsActivity.this, "收藏商家", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onError(ResponseBaseObject o) {
            Toast.makeText(ShopDetailsActivity.this, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }

}
