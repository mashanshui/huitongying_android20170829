package com.huixiangshenghuo.app.activity.homepage;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.model.ShopcartListObject;
import com.huixiangshenghuo.app.comm.model.ShopcartListParam;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.utils.StringUtils;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.shopcart.ShopcartClearRequestObject;
import com.doumee.model.request.shopcart.ShopcartClearRequestParam;
import com.doumee.model.request.shopcart.ShopcartListRequestObject;
import com.doumee.model.request.shopcart.ShopcartManageRequestObject;
import com.doumee.model.request.shopcart.ShopcartManageRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class ShopCartActivity extends BaseActivity implements View.OnClickListener {
   private ImageButton backImg;
   private TextView clearTxt;
   private LinearLayout chooseAllLyt;
   private ImageView chooseAllImg;
   private TextView deleteTxt;
   private ListView goodList;
   private TextView allPriceTxt;
   private TextView buyTxt;
   private LinearLayout bottomLyt;
   private RelativeLayout topLyt;
   private ImageView emptyImg;

   private int chooseCount;
   private ArrayList<ShopcartListParam> chooseGoods;

   private List<ShopcartListParam> goods;
   private CustomBaseAdapter<ShopcartListParam> goodAdapter;

   private String idCardCheckStatus;//是否实名认证 实名认证审核状态 0未申请 1申请中 2审核通过 3审核未通过

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shop_cart);
      findView();
      initData();
      requestShopcartList();
      loadUser();
   }

   private void findView() {
      backImg = (ImageButton) findViewById(R.id.asc_back_img);
      clearTxt = (TextView) findViewById(R.id.asc_clear_txt);
      chooseAllLyt = (LinearLayout) findViewById(R.id.asc_choose_all_lyt);
      deleteTxt = (TextView) findViewById(R.id.asc_delete_txt);
      goodList = (ListView) findViewById(R.id.asc_good_list);
      allPriceTxt = (TextView) findViewById(R.id.asc_all_price_txt);
      buyTxt = (TextView) findViewById(R.id.asc_buy_txt);
      chooseAllImg = (ImageView) findViewById(R.id.asc_choose_all_img);
      bottomLyt = (LinearLayout) findViewById(R.id.asc_bottom_lyt);
      topLyt = (RelativeLayout) findViewById(R.id.asc_top_lyt);
      emptyImg = (ImageView) findViewById(R.id.asc_empty_img);

      backImg.setOnClickListener(this);
      clearTxt.setOnClickListener(this);
      chooseAllLyt.setOnClickListener(this);
      deleteTxt.setOnClickListener(this);
      buyTxt.setOnClickListener(this);
   }

   private void initData() {
      chooseGoods = new ArrayList<>();
      goods = new ArrayList<>();
      goodAdapter = new CustomBaseAdapter<ShopcartListParam>((ArrayList<ShopcartListParam>) goods, R.layout.item_shop_cart_new) {
         @Override
         public void bindView(ViewHolder holder, final ShopcartListParam obj) {
            ImageView goodImg = holder.getView(R.id.isc_good_img);
            TextView nameTxt = holder.getView(R.id.isc_name_txt);
            TextView priceTxt = holder.getView(R.id.isc_price_txt);
            final ImageView deleteImg = holder.getView(R.id.isc_j_img);
            final ImageView addImg = holder.getView(R.id.isc_add_img);
            TextView numTxt = holder.getView(R.id.isc_num_txt);
            ImageView chooseImg = holder.getView(R.id.isc_choose_img);
            RelativeLayout chooseLyt = holder.getView(R.id.isc_choose_lyt);
            if (StringUtils.isEmpty(obj.getProImg())) {
               goodImg.setImageDrawable(getResources().getDrawable(R.mipmap.business_default));
            } else {
               ImageLoader.getInstance().displayImage(obj.getProImg(), goodImg);
            }
            nameTxt.setText(obj.getProName());
            priceTxt.setText("￥" + obj.getPrice());
            numTxt.setText(obj.getNum() + "");
            if (obj.isChoose()) {
               chooseImg.setImageResource(R.mipmap.xz);
            } else {
               chooseImg.setImageResource(R.mipmap.wxz);
            }
            chooseLyt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  if (obj.isChoose()) {
                     obj.setChoose(false);
                     chooseCount--;
                     chooseGoods.remove(obj);
                     chooseAllImg.setBackgroundResource(R.mipmap.wxz);
                  } else {
                     obj.setChoose(true);
                     chooseCount++;
                     chooseGoods.add(obj);
                     if (chooseCount == goods.size()) {
                        chooseAllImg.setBackgroundResource(R.mipmap.xz);
                     }
                  }
                  allPriceTxt.setText(calculateTotalPrice());
                  goodAdapter.notifyDataSetChanged();
               }
            });
            deleteImg.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  if (obj.getNum() <= 1) {
                     return;
                  }
                  //添加购买数量
                  deleteImg.setClickable(false);
//                        ShopcartDelRequestObject requestObject=new ShopcartDelRequestObject();
//                        ShopcartDelRequestParam param=new ShopcartDelRequestParam();
//                        param.setShopcartId(obj.getShopcartId());
//                        param.setType("2");
//                        param.setNum(1);
//                        requestObject.setParam(param);
                  showProgressDialog("正在加载..");
                  ShopcartManageRequestParam shopcartManageRequestParam = new ShopcartManageRequestParam();
                  shopcartManageRequestParam.setProId(obj.getProId());
//                  shopcartManageRequestParam.setNum(1);
                  shopcartManageRequestParam.setNum(obj.getNum() - 1);
                  shopcartManageRequestParam.setType("0");//0设置数量 1 删除
                  ShopcartManageRequestObject shopcartManageRequestObject = new ShopcartManageRequestObject();
                  shopcartManageRequestObject.setParam(shopcartManageRequestParam);
                  httpTool.post(shopcartManageRequestObject, URLConfig.CART_GOOD_EDIT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                     @Override
                     public void onSuccess(ResponseBaseObject resp) {
                        dismissProgressDialog();
                        deleteImg.setClickable(true);
                        obj.setNum(obj.getNum() - 1);
                        allPriceTxt.setText(calculateTotalPrice());
                        goodAdapter.notifyDataSetChanged();
                     }

                     @Override
                     public void onError(ResponseBaseObject responseBaseObject) {
                        dismissProgressDialog();
                        deleteImg.setClickable(true);
                        ToastView.show(responseBaseObject.getErrorMsg());
                     }


                  });
               }
            });

            addImg.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  //添加购买数量
                  addImg.setClickable(false);
//                        ShopcartDelRequestObject requestObject=new ShopcartDelRequestObject();
//                        ShopcartDelRequestParam param=new ShopcartDelRequestParam();
//                        param.setShopcartId(obj.getShopcartId());
//                        param.setType("1");
//                        param.setNum(1);
//                        requestObject.setParam(param);
                  showProgressDialog("正在加载..");
                  ShopcartManageRequestParam shopcartManageRequestParam = new ShopcartManageRequestParam();
                  shopcartManageRequestParam.setProId(obj.getProId());
//                  shopcartManageRequestParam.setNum(1);
                  shopcartManageRequestParam.setNum(obj.getNum() + 1);
                  shopcartManageRequestParam.setType("0");//0设置数量 1 删除
                  ShopcartManageRequestObject shopcartManageRequestObject = new ShopcartManageRequestObject();
                  shopcartManageRequestObject.setParam(shopcartManageRequestParam);

                  httpTool.post(shopcartManageRequestObject, URLConfig.CART_GOOD_EDIT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                     @Override
                     public void onSuccess(ResponseBaseObject resp) {
                        dismissProgressDialog();
                        addImg.setClickable(true);
                        obj.setNum(obj.getNum() + 1);
                        allPriceTxt.setText(calculateTotalPrice());
                        goodAdapter.notifyDataSetChanged();
                     }

                     @Override
                     public void onError(ResponseBaseObject responseBaseObject) {
                        dismissProgressDialog();
                        addImg.setClickable(true);
                     }


                  });
               }
            });
         }


      };
      goodList.setAdapter(goodAdapter);
   }

   //购物车列表
   private void requestShopcartList() {


      ShopcartListRequestObject requestObject = new ShopcartListRequestObject();
//        ShopcartListRequestParam param=new ShopcartListRequestParam();
//        param.setCityId(ServiceApplication.getUser().getCityId());
//        requestObject.setParam(param);
      showProgressDialog(null);
      httpTool.post(requestObject, URLConfig.CART_LIST, new HttpTool.HttpCallBack<ShopcartListObject>() {
         @Override
         public void onSuccess(ShopcartListObject resp) {
            dismissProgressDialog();
            if (resp.getRecordList() != null && resp.getRecordList().size() > 0) {
               goods.addAll(resp.getRecordList());
               goodAdapter.notifyDataSetChanged();
               bottomLyt.setVisibility(View.VISIBLE);
               topLyt.setVisibility(View.VISIBLE);
            } else {
               emptyImg.setVisibility(View.VISIBLE);
               bottomLyt.setVisibility(View.GONE);
               topLyt.setVisibility(View.GONE);
            }
         }

         @Override
         public void onError(ShopcartListObject shopcartListObject) {
            ToastView.show(shopcartListObject.getErrorMsg());
            dismissProgressDialog();
         }


      });
   }

   //批量删除或清空
   private void clearShopCart(final List<ShopcartListParam> params) {
      ShopcartClearRequestObject requestObject = new ShopcartClearRequestObject();
      final ShopcartClearRequestParam param = new ShopcartClearRequestParam();
      List<String> ids = new ArrayList<>();
      if (params != null) {
         for (ShopcartListParam item : params) {
            ids.add(item.getShopcartId());
         }
      }
      param.setShopcartIdList(ids);
      requestObject.setParam(param);
      showProgressDialog("正在删除");
      deleteTxt.setClickable(false);
      clearTxt.setClickable(false);
      httpTool.post(requestObject, URLConfig.CLEAR_CART, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject resp) {
            dismissProgressDialog();
            deleteTxt.setClickable(true);
            clearTxt.setClickable(true);
            if (params == null) {
               goods.clear();

            } else {
               goods.removeAll(params);
            }
            if (goods.size() <= 0) {
               bottomLyt.setVisibility(View.GONE);
               topLyt.setVisibility(View.GONE);
               emptyImg.setVisibility(View.VISIBLE);
            }
            chooseGoods.clear();
            chooseCount = 0;
            allPriceTxt.setText("0.00");
            goodAdapter.notifyDataSetChanged();
         }

         @Override
         public void onError(ResponseBaseObject responseBaseObject) {
            deleteTxt.setClickable(true);
            clearTxt.setClickable(true);
            dismissProgressDialog();
         }


      });
   }


   //计算总价
   private String calculateTotalPrice() {
      double price = 0;
      for (ShopcartListParam param : chooseGoods) {
         price += param.getPrice() * param.getNum();
      }
      return "￥" + price;
   }

   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.asc_back_img:
            finish();
            break;
         case R.id.asc_clear_txt:
            if (goods.size() <= 0) {
               return;
            }
            clearShopCart(null);
            break;
         case R.id.asc_choose_all_lyt:
            if (chooseCount == goods.size()) {
               chooseCount = 0;
               chooseGoods.clear();
               for (ShopcartListParam param : goods) {
                  param.setChoose(false);
               }
               chooseAllImg.setBackgroundResource(R.mipmap.wxz);
            } else {
               chooseCount = goods.size();
               chooseGoods.clear();
               chooseGoods.addAll(goods);
               for (ShopcartListParam param : goods) {
                  param.setChoose(true);
               }
               chooseAllImg.setBackgroundResource(R.mipmap.xz);
            }
            allPriceTxt.setText(calculateTotalPrice());
            goodAdapter.notifyDataSetChanged();
            break;
         case R.id.asc_delete_txt:
            if (chooseGoods.size() <= 0) {
               ToastView.show("请选择商品");
               return;
            }
            clearShopCart(chooseGoods);
            break;
         case R.id.asc_buy_txt:
            if (!idCardCheckStatus.equals("2")) {
               ToastView.show("对不起，您还未通过审核!");
               return;
            }
            if (chooseGoods.size() <= 0) {
               ToastView.show("请选择商品");
               return;
            }
            Intent intent = new Intent(this, ConfirmOrderNewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", chooseGoods);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0x11);
            break;
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
         if (requestCode == 0x11) {
//            String sx = data.getStringExtra("data");
//            if (sx.equals("0")) {
//               chooseCount = 0;
//               chooseGoods.clear();
//               chooseAllImg.setBackgroundResource(R.mipmap.wxz);
//               goods.clear();
//               goodAdapter.notifyDataSetChanged();
//               requestShopcartList();
//               allPriceTxt.setText(calculateTotalPrice());
//            }
            int sx = data.getIntExtra("data", 0);
            if (sx == 1) {
               chooseCount = 0;
               chooseGoods.clear();
               chooseAllImg.setBackgroundResource(R.mipmap.wxz);
               goods.clear();
               goodAdapter.notifyDataSetChanged();
               requestShopcartList();
               allPriceTxt.setText(calculateTotalPrice());
            }

         }
      }
      if (data == null) {
         return;
      }

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
