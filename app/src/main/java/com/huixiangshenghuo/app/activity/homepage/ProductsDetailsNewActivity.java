package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.comment.PrdCommentListRequestObject;
import com.doumee.model.request.comment.PrdCommentListRequestParam;
import com.doumee.model.request.product.ProductInfoRequestObject;
import com.doumee.model.request.product.ProductInfoRequestParam;
import com.doumee.model.request.shopcart.ShopcartManageRequestObject;
import com.doumee.model.request.shopcart.ShopcartManageRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.comment.PrdCommentListResponseObject;
import com.doumee.model.response.comment.PrdCommentListResponseParam;
import com.doumee.model.response.product.ProductInfoResponseObject;
import com.doumee.model.response.product.ProductInfoResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homepage.ReviewDetailsAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.model.ShopcartListParam;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.homepage.CommodityDetailsFragment;
import com.huixiangshenghuo.app.ui.fragments.homepage.ReviewDetailsFragment;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/6/22.
 */

public class ProductsDetailsNewActivity extends BaseActivity implements View.OnClickListener, RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   public static String PRODUCTSID = "productsId";
   private String productsId;
//   BitmapUtils bitmapUtils;

   private RadioGroup viewTab;
   private FragmentManager fragmentManager;
   private FragmentTransaction fragmentTransaction;
   private CommodityDetailsFragment commodityFragment;
   private ReviewDetailsFragment reviewFragment;

   private ImageView iv_products;//商品图片
   private TextView tv_name;//商品名字
   private TextView tv_price;//商品价格

   private Button bt_move;//数量减 按钮
   private TextView tv_num;//数量
   private Button bt_add;//数量加 按钮
   private int num = 1;//购买数量
   int stock;//库存
   private TextView tv_phone;//客服电话
   private String service_phone;//获取客服电话

   private String info = "";//商品详情

   private RelativeLayout rl_gwc;//购物车
   private RelativeLayout rl_gw;//下单

   private ProductInfoResponseParam goodDatails;//商品详情（先请求商品详情）
   private ShopcartListParam param;//立即进货参数传递


   RefreshLayout refreshLayout;

   private WebView webView;

   private String viewTab_type = "0";//0 商品详情 1 评论详情

   private MyListView lv_review_details;
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间
   private ReviewDetailsAdapter re_adapter;//适配器


   private ArrayList<PrdCommentListResponseParam> arrlist = new ArrayList<PrdCommentListResponseParam>();//数据源

   private String idCardCheckStatus;//是否实名认证 实名认证审核状态 0未申请 1申请中 2审核通过 3审核未通过

   public static void startProductsDetailsNewActivity(Context context, String productsId) {
      Intent intent = new Intent(context, ProductsDetailsNewActivity.class);
      intent.putExtra(PRODUCTSID, productsId);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_products_details_new);
      initview();
//      initdate();
      initData();
      initBitmapParames();
      loadDataIndex();
      request();
   }

   private void initview() {
      initTitleBar_1();
      productsId = getIntent().getStringExtra(PRODUCTSID);
      titleView.setText("商品详情");
      rl_action_image.setVisibility(View.VISIBLE);
      actionImageButton2.setVisibility(View.VISIBLE);

      refreshLayout = (RefreshLayout) findViewById(R.id.rl_sx_review_details);
      lv_review_details = (MyListView) findViewById(R.id.lv_review_details);
      rl_gwc = (RelativeLayout) findViewById(R.id.rl_gwc_products_details);
      rl_gw = (RelativeLayout) findViewById(R.id.rl_gw_products_details);

      //头部
      View headView = View.inflate(ProductsDetailsNewActivity.this, R.layout.activity_products_details_top_new, null);

      viewTab = (RadioGroup) headView.findViewById(R.id.ao_rg);
      iv_products = (ImageView) headView.findViewById(R.id.iv_products_details_imgurl);
      tv_name = (TextView) headView.findViewById(R.id.tv_name_products_details);
      tv_price = (TextView) headView.findViewById(R.id.tv_price_products_details);
      bt_move = (Button) headView.findViewById(R.id.bt_move_products_details);
      tv_num = (TextView) headView.findViewById(R.id.tv_num);
      bt_add = (Button) headView.findViewById(R.id.bt_add_products_details);
      tv_phone = (TextView) headView.findViewById(R.id.tv_phone_products_details);

      webView = (WebView) headView.findViewById(R.id.webview);


      actionImageButton2.setOnClickListener(this);
      bt_move.setOnClickListener(this);
      bt_add.setOnClickListener(this);
      tv_phone.setOnClickListener(this);
      rl_gwc.setOnClickListener(this);
      rl_gw.setOnClickListener(this);

      lv_review_details.addHeaderView(headView);

   }

   private void initdate() {


      viewTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.ao_all_rb:
                  viewTab_type = "0";
                  arrlist.clear();
                  webView.setVisibility(View.VISIBLE);
                  webView.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);//内容
                  break;

               case R.id.ao_self_rb:
                  webView.setVisibility(View.GONE);
                  page = 1;
                  viewTab_type = "1";
                  recommendrequest();
                  break;


            }
         }
      });
   }

   private void initData() {
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);

      re_adapter = new ReviewDetailsAdapter(arrlist, ProductsDetailsNewActivity.this);
      lv_review_details.setAdapter(re_adapter);

   }

   /**
    * 图片加载
    */
   private void initBitmapParames() {
//      bitmapUtils = new BitmapUtils(this);
//      // 设置加载失败图片
//      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
//      // 设置没有加载成功图片
//      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
   }

   //加载商品信息
   private void request() {
//      showProgressDialog(getString(R.string.loading));
      ProductInfoRequestParam productInfoRequestParam = new ProductInfoRequestParam();
      productInfoRequestParam.setProId(productsId);
      ProductInfoRequestObject productInfoRequestObject = new ProductInfoRequestObject();
      productInfoRequestObject.setParam(productInfoRequestParam);
      httpTool.post(productInfoRequestObject, URLConfig.PRODUCT_DETAILS, new HttpTool.HttpCallBack<ProductInfoResponseObject>() {
         @Override
         public void onSuccess(ProductInfoResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
//            dismissProgressDialog();
            goodDatails = o.getRecord();

//            WindowManager wm = ProductsDetailsNewActivity.this.getWindowManager();
//
//            int width = wm.getDefaultDisplay().getWidth();
////            int height = wm.getDefaultDisplay().getHeight();
//
//            resource = ReturnBitmap.returnBitmap(o.getRecord().getImgurl());
//            int bWidth = ReturnBitmap.returnBitmap(o.getRecord().getImgurl()).getWidth();
//            int bHeight = ReturnBitmap.returnBitmap(o.getRecord().getImgurl()).getHeight();
//            int Proportion = bHeight/bWidth;
//
//
//            //设置图片参数
//            ViewGroup.LayoutParams layoutParams = iv_products.getLayoutParams();
//            layoutParams.width = width;
//            layoutParams.height = width*Proportion;
//            iv_products.setLayoutParams(layoutParams);


//            bitmapUtils.display(iv_products, o.getRecord().getImgurl());
            //ProductsDetailsNewActivity
            imview(iv_products, o.getRecord().getImgurl());

            tv_name.setText(o.getRecord().getName());
            if (TextUtils.equals(o.getRecord().getType(), CustomConfig.INTEGRAL_GOODS)) {
               tv_price.setText(CustomConfig.INTEGRAL + o.getRecord().getIntegral());
            } else {
               tv_price.setText(CustomConfig.RMB + o.getRecord().getPrice());
            }
            try {
               if (o.getRecord().getStock() > 0) {
                  stock = o.getRecord().getStock();
               }
            } catch (Exception e) {
               e.printStackTrace();
               stock = 0;
            }
            if (o.getRecord().getInfo() != null) {
               info = o.getRecord().getInfo();
            }
            initdate();
            if (viewTab_type.equals("0")) {//viewTab_type 类型 0 商品详情 1 评论详情
               arrlist.clear();
               if (info == null || info == "" || info.equals("")) {
                  webView.setVisibility(View.GONE);
               } else {
                  webView.setVisibility(View.VISIBLE);
                  webView.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);//内容
               }
            } else {
               webView.setVisibility(View.GONE);
               recommendrequest();
            }

            loadUser();
         }

         @Override
         public void onError(ProductInfoResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
//            dismissProgressDialog();
            Toast.makeText(ProductsDetailsNewActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   private void imview(final ImageView img, String path) {
      WindowManager wm = ProductsDetailsNewActivity.this.getWindowManager();
      final int width = wm.getDefaultDisplay().getWidth();//获取屏幕的宽度

      Glide.with(getApplication())
            .load(path)
            .listener(new RequestListener<String, GlideDrawable>() {
               @Override
               public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                  return false;
               }

               @Override
               public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                  if (img == null) {
                     return false;
                  }
                  if (img.getScaleType() != ImageView.ScaleType.FIT_XY) {
                     img.setScaleType(ImageView.ScaleType.FIT_XY);
                  }
                  int bWidth = resource.getIntrinsicWidth();//获取图片原有宽度
                  int bHeight = resource.getIntrinsicHeight();//获取图片原有高度

                  int height = width * bHeight / bWidth;
                  ViewGroup.LayoutParams para = img.getLayoutParams();
                  para.height = height;
                  img.setLayoutParams(para);

                  return false;
               }
            })
            .placeholder(R.mipmap.business_default)
            .error(R.mipmap.business_default)
            .into(img);
      //    .diskCacheStrategy(DiskCacheStrategy.SOURCE)


//      Glide.with(ProductsDetailsNewActivity.this).load(path).asBitmap().placeholder(R.drawable.business_default).error(R.drawable.business_default).into(new BitmapImageViewTarget(img) {
//         @Override
//         protected void setResource(Bitmap resource) {
//      int bWidth = resource.getWidth();
//      int bHeight = resource.getHeight();
////            int width = 1;
//      Log.e("====", bWidth + " " + bHeight + " " + width);
//      int height = width * bHeight / bWidth;
//      ViewGroup.LayoutParams para = img.getLayoutParams();
//      para.height = height;
//      img.setLayoutParams(para);
////      RoundedBitmapDrawable circularBitmapDrawable =
////            RoundedBitmapDrawableFactory.create(ProductsDetailsNewActivity.this.getResources(), resource);
////      circularBitmapDrawable.setCornerRadius(5);
////      img.setImageDrawable(circularBitmapDrawable);
//            img.setImageBitmap(resource);
//         }
//
//      });


   }


   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.action_image2:
            startActivity(new Intent(this, ShopCartActivity.class));
            break;
         case R.id.bt_move_products_details:
            if (num <= 1) {
               ToastView.show("购买数量不能少于1");
            } else {
               num--;
            }
            tv_num.setText(num + "");
            break;
         case R.id.bt_add_products_details:
            num++;
            if (num > stock) {
               ToastView.show("库存不足");
               num--;
            }
//            ToastView.show(num + "");
            tv_num.setText(num + "");
            break;
         case R.id.tv_phone_products_details:
            dh();
            break;
         case R.id.rl_gwc_products_details:
            addShopCart();
            break;
         case R.id.rl_gw_products_details:
            if (TextUtils.isEmpty(idCardCheckStatus)) {
               break;
            }
            if (TextUtils.equals(idCardCheckStatus, "2")) {//实名认证审核状态 0未申请 1申请中 2审核通过 3审核未通过
               //先屏蔽
               try {
                  param = new ShopcartListParam();

                  param.setNum(num);
                  param.setPrice(goodDatails.getPrice());
                  param.setIntegral(goodDatails.getIntegral());
                  param.setType(goodDatails.getType());
//            param.setProId(goodDatails.getProId());
                  param.setProId(productsId);
                  param.setProName(goodDatails.getName());
                  param.setProImg(goodDatails.getImgurl());

                  Intent intent = new Intent(this, ConfirmOrderNewActivity.class);
                  Bundle bundle = new Bundle();
                  ArrayList<ShopcartListParam> listParams = new ArrayList<>();
                  listParams.add(param);
                  bundle.putSerializable("list", listParams);
                  intent.putExtras(bundle);
                  startActivity(intent);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            } else {
               ToastView.show("对不起，您还未通过审核!");
            }
//            initDialog();
            break;
         default:
            break;
      }
   }

   private void initDialog() {
      NiceDialog.init()
              .setLayoutId(R.layout.buy_confirm_count_dialog)     //设置dialog布局文件
              .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
                 @Override
                 public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {

                 }
              })
              .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
              .setShowBottom(true)     //是否在底部显示dialog，默认flase
              .setHeight(getAndroiodScreenProperty())     //dialog高度（单位：dp），默认为WRAP_CONTENT
              .setOutCancel(true)     //点击dialog外是否可取消，默认true
              .show(getSupportFragmentManager());     //显示dialog
   }

   public int getAndroiodScreenProperty(){
      WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
      DisplayMetrics dm = new DisplayMetrics();
      wm.getDefaultDisplay().getMetrics(dm);
      int width = dm.widthPixels;// 屏幕宽度（像素）
      int height= dm.heightPixels; // 屏幕高度（像素）
      float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
      int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
      //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
      int screenWidth = (int) (width/density);//屏幕宽度(dp)
      int screenHeight = (int)(height/density);//屏幕高度(dp)
      Log.e("123", screenWidth + "======" + screenHeight);
      return screenHeight * 2 / 3;
   }

   private void dh() {
      final Dialog dialog1 = new Dialog(this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage(service_phone);
      dialog1.setConfirmText("呼叫");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + service_phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog1.dismiss();
         }
      });
      dialog1.show();
   }

   private int shopCount;

   //加入购物车
   private void addShopCart() {
      ShopcartManageRequestParam shopcartManageRequestParam = new ShopcartManageRequestParam();
      shopcartManageRequestParam.setProId(productsId);
      shopcartManageRequestParam.setNum(num);
      shopcartManageRequestParam.setType("0");//0设置数量 1 删除
      ShopcartManageRequestObject shopcartManageRequestObject = new ShopcartManageRequestObject();
      shopcartManageRequestObject.setParam(shopcartManageRequestParam);
      httpTool.post(shopcartManageRequestObject, URLConfig.SHOP_CART_ADD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            Toast.makeText(ProductsDetailsNewActivity.this, "已加入购物车", Toast.LENGTH_SHORT).show();
            shopCount += num;
            if (shopCount >= 100) {
               actionTxt.setText("99+");
            } else {
               actionTxt.setText("+" + shopCount);
            }
            actionTxt.setVisibility(View.VISIBLE);
         }

         @Override
         public void onError(ResponseBaseObject responseBaseObject) {
            Toast.makeText(ProductsDetailsNewActivity.this, responseBaseObject.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }


      });
   }

   //加载数据字典
   public void loadDataIndex() {
//      showProgressDialog("正在加载");
      AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
      AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
      appDicInfoRequestObject.setParam(appDicInfoParam);
      httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
         @Override
         public void onSuccess(AppConfigureResponseObject o) {
//            dismissProgressDialog();
            List<AppConfigureResponseParam> dataList = o.getDataList();
            for (AppConfigureResponseParam app : dataList) {
               if (app.getName().equals(CustomConfig.SERVICE_PHONE)) {

                  service_phone = app.getContent();

               }

            }

         }

         @Override
         public void onError(AppConfigureResponseObject o) {
            //           dismissProgressDialog();
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   @Override
   protected void onPause() {
      super.onPause();
      shopCount = 0;
      actionTxt.setVisibility(View.GONE);
   }

   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);
      page = 1;
      request();
   }

   @Override
   public void onLoad() {
      if (viewTab_type.equals("0")) {

      } else {
         refreshLayout.setRefreshing(true);
         page++;
         recommendrequest();
      }
   }

   /**
    * 商品评论记录列表
    */
   private void recommendrequest() {
      // TODO Auto-generated method stub
      PrdCommentListRequestObject object = new PrdCommentListRequestObject();
      object.setParam(new PrdCommentListRequestParam());
      object.getParam().setPrdId(productsId);
      object.getParam().setScoreLevel("0");//0全部1好评2中评3差评
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(CustomConfig.PAGE_SIZE);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else if (page != 1) {
         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.PRODUCT_REVIEWS, new HttpTool.HttpCallBack<PrdCommentListResponseObject>() {
         @Override
         public void onSuccess(PrdCommentListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     /**
                      * 分页
                      */
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }

                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());

                     re_adapter.notifyDataSetChanged();

                  }

               }
            }


         }

         @Override
         public void onError(PrdCommentListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });


   }

   /**
    * 查询是否实名认证
    */
   private void loadUser() {

      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      if (null == userInfoResponseParam) {
         userInfoResponseParam = new UserInfoResponseParam();
         userInfoResponseParam.setMemberId("");
      }
      MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
      memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
      MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
      memberInfoRequestObject.setParam(memberInfoParamObject);

      httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
         @Override
         public void onSuccess(MemberInfoResponseObject o) {

            idCardCheckStatus = o.getMember().getIdCardCheckStatus();
         }

         @Override
         public void onError(MemberInfoResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }



}
