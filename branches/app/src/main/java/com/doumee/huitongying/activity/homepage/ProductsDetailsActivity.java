package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.model.ShopcartListParam;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.fragments.homepage.CommodityDetailsFragment;
import com.huixiangshenghuo.app.ui.fragments.homepage.ReviewDetailsFragment;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.product.ProductInfoRequestObject;
import com.doumee.model.request.product.ProductInfoRequestParam;
import com.doumee.model.request.shopcart.ShopcartManageRequestObject;
import com.doumee.model.request.shopcart.ShopcartManageRequestParam;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.product.ProductInfoResponseObject;
import com.doumee.model.response.product.ProductInfoResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 * 商品详情
 */

public class ProductsDetailsActivity extends BaseActivity implements View.OnClickListener {
   public static String PRODUCTSID = "productsId";
   private String productsId;
   BitmapUtils bitmapUtils;

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

   public static void startProductsDetailsActivity(Context context, String productsId) {
      Intent intent = new Intent(context, ProductsDetailsActivity.class);
      intent.putExtra(PRODUCTSID, productsId);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_products_details);
      initview();
//      initdate();
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
      viewTab = (RadioGroup) findViewById(R.id.ao_rg);
      iv_products = (ImageView) findViewById(R.id.iv_products_details_imgurl);
      tv_name = (TextView) findViewById(R.id.tv_name_products_details);
      tv_price = (TextView) findViewById(R.id.tv_price_products_details);
      bt_move = (Button) findViewById(R.id.bt_move_products_details);
      tv_num = (TextView) findViewById(R.id.tv_num);
      bt_add = (Button) findViewById(R.id.bt_add_products_details);
      tv_phone = (TextView) findViewById(R.id.tv_phone_products_details);
      rl_gwc = (RelativeLayout) findViewById(R.id.rl_gwc_products_details);
      rl_gw = (RelativeLayout) findViewById(R.id.rl_gw_products_details);

      actionImageButton2.setOnClickListener(this);
      bt_move.setOnClickListener(this);
      bt_add.setOnClickListener(this);
      tv_phone.setOnClickListener(this);
      rl_gwc.setOnClickListener(this);
      rl_gw.setOnClickListener(this);
//      try {
//         num = Integer.valueOf(tv_num.getText().toString()).intValue();
//      } catch (NumberFormatException e) {
//         e.printStackTrace();
//      }
   }

   private void initdate() {
      fragmentManager = getSupportFragmentManager();
      fragmentTransaction = fragmentManager.beginTransaction();

      commodityFragment = new CommodityDetailsFragment();
      Bundle bundle = new Bundle();
      bundle.putString(CommodityDetailsFragment.ARG_PARAM1, info);
      commodityFragment.setArguments(bundle);
      fragmentTransaction.replace(R.id.ao_frame_lyt, commodityFragment);
      fragmentTransaction.commit();
      viewTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.ao_all_rb:
                  if (commodityFragment == null) {
                     commodityFragment = new CommodityDetailsFragment();
                     Bundle bundle = new Bundle();
                     bundle.putString(CommodityDetailsFragment.ARG_PARAM1, info);
                     commodityFragment.setArguments(bundle);
                  }
                  fragmentTransaction = fragmentManager.beginTransaction();
                  fragmentTransaction.replace(R.id.ao_frame_lyt, commodityFragment);
                  fragmentTransaction.commit();
                  break;

               case R.id.ao_self_rb:
                  if (reviewFragment == null) {
                     reviewFragment = new ReviewDetailsFragment();
                     Bundle bundle = new Bundle();
                     bundle.putString(ReviewDetailsFragment.ARG_PARAM1, productsId);
                     reviewFragment.setArguments(bundle);
                  }
                  fragmentTransaction = fragmentManager.beginTransaction();
                  fragmentTransaction.replace(R.id.ao_frame_lyt, reviewFragment);
                  fragmentTransaction.commit();
                  break;


            }
         }
      });
   }

   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
   }

   //加载商品信息
   private void request() {
      showProgressDialog(getString(R.string.loading));
      ProductInfoRequestParam productInfoRequestParam = new ProductInfoRequestParam();
      productInfoRequestParam.setProId(productsId);
      ProductInfoRequestObject productInfoRequestObject = new ProductInfoRequestObject();
      productInfoRequestObject.setParam(productInfoRequestParam);
      httpTool.post(productInfoRequestObject, URLConfig.PRODUCT_DETAILS, new HttpTool.HttpCallBack<ProductInfoResponseObject>() {
         @Override
         public void onSuccess(ProductInfoResponseObject o) {
            dismissProgressDialog();
            goodDatails = o.getRecord();
            bitmapUtils.display(iv_products, o.getRecord().getImgurl());
            tv_name.setText(o.getRecord().getName());
            tv_price.setText(CustomConfig.RMB + o.getRecord().getPrice());
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
         }

         @Override
         public void onError(ProductInfoResponseObject o) {
            dismissProgressDialog();
            Toast.makeText(ProductsDetailsActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
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
            ToastView.show(num + "");
            tv_num.setText(num + "");
            break;
         case R.id.tv_phone_products_details:
            dh();
            break;
         case R.id.rl_gwc_products_details:
            addShopCart();
            break;
         case R.id.rl_gw_products_details:
            //先屏蔽
            param = new ShopcartListParam();


            param.setNum(num);
            param.setPrice(goodDatails.getPrice());
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
            break;

      }
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
            Toast.makeText(ProductsDetailsActivity.this, "已加入购物车", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ProductsDetailsActivity.this, responseBaseObject.getErrorMsg(), Toast.LENGTH_SHORT).show();
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

}
