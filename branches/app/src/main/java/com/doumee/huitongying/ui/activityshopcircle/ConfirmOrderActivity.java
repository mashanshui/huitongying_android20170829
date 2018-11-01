package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.ui.mine.SelectAcceptLocationActivity;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.goodsorder.GoodsDetailsRequestParam;
import com.doumee.model.request.goodsorder.GoodsOrderAddRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.GoodsDetailsResponeParam;
import com.doumee.model.response.goodsorder.GoodsOrderAddResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderAddResponseParam;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.memberaddr.AddrListResponseObject;
import com.doumee.model.response.memberaddr.AddrListResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.shop.ShopInfoResponseParam;
import com.doumee.model.response.userinfo.AlipayResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.huixiangshenghuo.app.R.id.address;

/**
 * Created by Administrator on 2017/2/15.
 */

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener {


   RelativeLayout selectAddress;

   TextView nameView;

   TextView telView;

   TextView addressView;

   Button timeButton;

   RadioGroup payType;

   MyListView myListView;

   EditText jifenView;

   TextView totalView;

   TextView jifenPrice;

   TextView payView;

   EditText notesView;

   TextView payTotalView;

   Button submitButton;

   TextView tv_jifen_price;

   TextView kdFeeView;

   TextView tv_zongjia;


   private double totalPrice;
   private ArrayList<GoodsInfo> goodsDataList;
   private CustomBaseAdapter<GoodsInfo> adapter;
   private static final int WECHAT_PAY = 0;//微信支付
   private static final int ALI_PAY = 1;//支付宝支付
   private static final int UNION__PAY = 2;//银联支付
   //   private int pay = WECHAT_PAY;
   private int pay = ALI_PAY;
   private String addId;//地址ID
   private double integral;//用户总积分
   private double payIntegral;//本次使用积分
   private double payTotal;
   private String payPassword = "";
   private String shopId;
   private AlertDialog alertDialog;
   private AlertDialog payAlertTip;
   private UserInfoResponseParam userInfo;
   public static final int PAY_SUCCESS = 1;//支付成功
   private String orderId;
   private int payWeChat = 0;

   private ArrayList<String> linkedList;

   public static void startConfirmOrderActivity(Context context) {
      Intent intent = new Intent(context, ConfirmOrderActivity.class);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_order_confirm);
      initView();
      goodsDataList = new ArrayList<>();
      userInfo = SaveObjectTool.openUserInfoResponseParam();
      if (null != userInfo) {
         //        integral = userInfo.getMoney();//改为动态获取
      }
      linkedList = getIntent().getStringArrayListExtra("data");
      loadUser();
      payAlertTip();
      initAdapter();
      initListener();
      loadUserAddress();
      loadShopCart();
      loadShopInfo();
   }


   public void initView() {
      initTitleBar_1();
      titleView.setText("确认订单");
      selectAddress = (RelativeLayout) findViewById(R.id.select_address);
      nameView = (TextView) findViewById(R.id.name);
      telView = (TextView) findViewById(R.id.tel);
      addressView = (TextView) findViewById(address);
      timeButton = (Button) findViewById(R.id.time);
      payType = (RadioGroup) findViewById(R.id.pay_type);
      myListView = (MyListView) findViewById(R.id.list_view);
      jifenView = (EditText) findViewById(R.id.jifen);
      totalView = (TextView) findViewById(R.id.total_price);
      jifenPrice = (TextView) findViewById(R.id.jifen_price);
      payView = (TextView) findViewById(R.id.pay_total);
      notesView = (EditText) findViewById(R.id.notes);
      payTotalView = (TextView) findViewById(R.id.total_pay);
      tv_zongjia = (TextView) findViewById(R.id.tv_zongjia);
      submitButton = (Button) findViewById(R.id.btn_order_submit);
      tv_jifen_price = (TextView) findViewById(R.id.tv_jifen_price);
      kdFeeView = (TextView) findViewById(R.id.kdfee);
      notesView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//限制20个字
//
//      jifenView.addTextChangedListener(new TextWatcher() {
//         public void afterTextChanged(Editable edt) {
//            String temp = edt.toString();
//            int posDot = temp.indexOf(".");
//            if (posDot <= 0) return;
//            if (temp.length() - posDot - 1 > 2) {
//               edt.delete(posDot + 3, posDot + 4);
//            }
//         }
//
//         public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//         }
//
//         public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//         }
//      });
      jifenView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//输入类型

   }

   private void initListener() {
      selectAddress.setOnClickListener(this);
      timeButton.setOnClickListener(this);
      submitButton.setOnClickListener(this);
      payType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               case R.id.wechat_pay:
                  pay = WECHAT_PAY;
                  break;
               case R.id.ali_pay:
                  pay = ALI_PAY;
                  break;
               case R.id.blank_pay:
                  pay = UNION__PAY;
                  break;
            }
         }
      });
   }

   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   @Override
   public void onClick(View v) {
      //   super.onClick(v);
      switch (v.getId()) {
         case R.id.select_address:
            Intent intent = new Intent(ConfirmOrderActivity.this, SelectAcceptLocationActivity.class);
            startActivityForResult(intent, 1, null);
            break;
         case R.id.btn_order_submit:
            if (TextUtils.isEmpty(addId)) {
               Toast.makeText(ConfirmOrderActivity.this, "请选择收货地址", Toast.LENGTH_LONG).show();
               return;
            }
            if (payIntegral > 0) {
               showPayDialog();
            } else {
               submitData();
            }
            break;
      }
   }

   @Override
   protected void onStart() {
      super.onStart();

   }

   @Override
   protected void onResume() {
      super.onResume();
      if (pay == WECHAT_PAY && payWeChat == 1) {
         loadPayResult();
      }
   }

   private void showPayDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(this, R.layout.activity_order_confirm_dialog, null);
      final EditText payPwdView = (EditText) view.findViewById(R.id.pay_password);
      Button editPdwButton = (Button) view.findViewById(R.id.edit_password);
      Button noButton = (Button) view.findViewById(R.id.no);
      Button yesButton = (Button) view.findViewById(R.id.yes);
      noButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alertDialog.dismiss();
         }
      });
      yesButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            payPassword = payPwdView.getText().toString().trim();
            if (!TextUtils.isEmpty(payPassword)) {
               submitData();
               alertDialog.dismiss();
            } else {
               Toast.makeText(ConfirmOrderActivity.this, "请输入积分支付密码", Toast.LENGTH_LONG).show();
            }
         }
      });
      editPdwButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent_modifypaymentpassword = new Intent(ConfirmOrderActivity.this, RegActivity.class);
            startActivity(intent_modifypaymentpassword);
         }
      });
      builder.setView(view);
      builder.setCancelable(false);
      alertDialog = builder.create();
      alertDialog.show();
   }


   private void submitData() {
      showProgressDialog(getString(R.string.loading));
      //======================================
      BigDecimal bg = new BigDecimal(payIntegral);
      double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
      //======================================
      GoodsOrderAddRequestParam goodsOrderAddRequestParam = new GoodsOrderAddRequestParam();
      //===============================================
//      goodsOrderAddRequestParam.setShopId(shopId);

      //===============================================
      goodsOrderAddRequestParam.setAddrId(addId);
//      goodsOrderAddRequestParam.setIntegralNum(payIntegral);
      //======================================
      goodsOrderAddRequestParam.setIntegralNum(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
      //======================================
      goodsOrderAddRequestParam.setPayPwd(payPassword);
      //   goodsOrderAddRequestParam.setInfo(notesView.getText().toString());
      //=======================================================================
      goodsOrderAddRequestParam.setInfo(notesView.getText().toString());
      //=======================================================================
      goodsOrderAddRequestParam.setInfo(notesView.getText().toString().trim());//备注
//      goodsOrderAddRequestParam.setPayMethod("1");//支付方式 0微信转账 1支付宝转账
      goodsOrderAddRequestParam.setPayMethod(pay + "");//支付方式 0微信转账 1支付宝转账
      List<GoodsDetailsRequestParam> proList = new ArrayList<>();
      for (GoodsInfo goodsInfo : goodsDataList) {
         if (goodsInfo.type == 1) {
            GoodsDetailsResponeParam goods = goodsInfo.goods;
            GoodsDetailsRequestParam goodsDetailsRequestParam = new GoodsDetailsRequestParam();
            goodsDetailsRequestParam.setProId(goods.getProId());
            goodsDetailsRequestParam.setNum(goods.getNum());
            if (!TextUtils.isEmpty(goods.getSkuId())) {
               goodsDetailsRequestParam.setSkuId(goods.getSkuId());
            }
            proList.add(goodsDetailsRequestParam);
         }
      }
      goodsOrderAddRequestParam.setProList(proList);
      GoodsOrderAddRequestObject goodsOrderAddRequestObject = new GoodsOrderAddRequestObject();
      goodsOrderAddRequestObject.setParam(goodsOrderAddRequestParam);
      httpTool.post(goodsOrderAddRequestObject, URLConfig.ORDER_CREATE, new HttpTool.HttpCallBack<GoodsOrderAddResponseObject>() {
         @Override
         public void onSuccess(GoodsOrderAddResponseObject o) {
            dismissProgressDialog();
            GoodsOrderAddResponseParam goodsOrderAddResponseParam = o.getRecord();
            String orderId = goodsOrderAddResponseParam.getOrderId();
            String isPayDone = goodsOrderAddResponseParam.getIsPayDone();
            AlipayResponseParam alipayResponseParam = o.getParam();
            if (TextUtils.equals(isPayDone, "1")) {//支付完成
               paySuccess();
            } else {
               if (pay == WECHAT_PAY) {
                  if (ThirdPartyClient.isWeixinAvilible(ConfirmOrderActivity.this)) {
                     payOrder(orderId, "");
                  } else {
                     ToastView.show("请安装微信客户端，或者使用其它支付方式");
                  }


//                  payOrder(orderId, "");
               } else {
                  payOrder(orderId, alipayResponseParam.getParamStr());
               }

//               payOrder(orderId, alipayResponseParam.getParamStr());
            }
         }

         @Override
         public void onError(GoodsOrderAddResponseObject o) {
            dismissProgressDialog();
            Toast.makeText(ConfirmOrderActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   //支付订单
   private void payOrder(final String orderId, String aliParams) {

      switch (pay) {
         case WECHAT_PAY:
            this.orderId = orderId;
            showProgressDialog(getString(R.string.loading));
            WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
            weixinOrderAddRequestParam.setOrderId(orderId);
            weixinOrderAddRequestParam.setTradeType("APP");
            weixinOrderAddRequestParam.setType("0");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
            WeixinOrderAddRequestObject weixinOrderAddRequestObject = new WeixinOrderAddRequestObject();
            weixinOrderAddRequestObject.setParam(weixinOrderAddRequestParam);
            httpTool.post(weixinOrderAddRequestObject, URLConfig.ORDER_WECHAT_PAY, new HttpTool.HttpCallBack<WeixinOrderAddResponseObject>() {
               @Override
               public void onSuccess(WeixinOrderAddResponseObject o) {
                  dismissProgressDialog();
                  WeixinOrderResponseParam weiXin = o.getData();
                  PayOrderRequestEntity payOrderRequestEntity = weiXin.getParam();
                  payWeChat = 1;
                  WXPayTool.WXPay wxPay = new WXPayTool.WXPay();
                  wxPay.setNonceStr(payOrderRequestEntity.getNoncestr());
                  wxPay.setPrepayId(payOrderRequestEntity.getPrepayid());
                  wxPay.setSign(payOrderRequestEntity.getSign());
                  wxPay.setAppId(payOrderRequestEntity.getAppid());
                  wxPay.setPartnerId(payOrderRequestEntity.getPartnerid());
                  wxPay.setTimeStamp(payOrderRequestEntity.getTimestamp());
                  wxPay.setPackageStr(payOrderRequestEntity.getPackageStr());
                  WXPayTool wxPayTool = new WXPayTool(ConfirmOrderActivity.this, weiXin.getParam().getAppid());
                  wxPayTool.payRequest(wxPay);
               }

               @Override
               public void onError(WeixinOrderAddResponseObject o) {
                  dismissProgressDialog();
                  Toast.makeText(ConfirmOrderActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
               }
            });
            break;
         case ALI_PAY:
            AliPayTool aliPayTool = new AliPayTool(ConfirmOrderActivity.this);
            aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
               @Override
               public void onPaySuccess() {
                  paySuccess();
               }

               @Override
               public void onPayError(String resultInfo) {
                  payAlertTip.show();
               }
            });
            aliPayTool.pay(aliParams);
            break;

         case UNION__PAY:
//            if (UPPayAssistEx.checkInstalled(this)){
//               UnionOrderAddRequestParam unionOrderAddRequestParam = new UnionOrderAddRequestParam();
//               unionOrderAddRequestParam.setOrderId(orderId);
//               unionOrderAddRequestParam.setType(UnionOrderAddRequestParam.TYPE_PRODUCT);
//               UnionOrderAddRequestObject unionOrderAddRequestObject = new UnionOrderAddRequestObject();
//               unionOrderAddRequestObject.setParam(unionOrderAddRequestParam);
//
//               httpTool.post(unionOrderAddRequestObject, HttpUrlConfig.ORDER_BLANK_PAY, new HttpTool.HttpCallBack<UnionOrderAddResponseObject>() {
//                  @Override
//                  public void onSuccess(UnionOrderAddResponseObject o) {
//                     String tnStr = o.getData().getTnStr();
//                     UPPayAssistEx.startPay (ConfirmOrderActivity.this, null, null, tnStr, CustomConfig.UNION_PAY);
//                  }
//                  @Override
//                  public void onError(UnionOrderAddResponseObject o) {
//                     Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),o.getErrorMsg(),Toast.LENGTH_LONG).show();
//                  }
//               });
//            }else{
//               Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),"请安装手机银联支付控件",Toast.LENGTH_LONG).show();
//            }
            break;
      }
   }

   private void initAdapter() {
      adapter = new CustomBaseAdapter<GoodsInfo>(goodsDataList, R.layout.fragment_order_info_item) {
         @Override
         public void bindView(ViewHolder holder, GoodsInfo obj) {
            TextView shopNameView = holder.getView(R.id.shop_name);
            LinearLayout goodsBar = holder.getView(R.id.goods_bar);
            TextView goodsNameView = holder.getView(R.id.goods_name);
            TextView goodsNumView = holder.getView(R.id.number);
            TextView goodsPriceView = holder.getView(R.id.price);
            LinearLayout sizeBar = holder.getView(R.id.size_bar);
            TextView goodsSizeView = holder.getView(R.id.size);
            if (obj.type == 0) {//店面名称
               shopNameView.setText(obj.shopName);
               shopNameView.setVisibility(View.VISIBLE);
               goodsBar.setVisibility(View.GONE);
               sizeBar.setVisibility(View.GONE);
            } else {
               shopNameView.setVisibility(View.GONE);
               goodsBar.setVisibility(View.VISIBLE);

               GoodsDetailsResponeParam goodsInfo = obj.goods;
               goodsNameView.setText(goodsInfo.getProName());
               goodsNumView.setText("×" + goodsInfo.getNum());
               goodsPriceView.setText(CustomConfig.RMB + goodsInfo.getPrice());
               String sku = goodsInfo.getSkuInfo();
               if (!TextUtils.isEmpty(sku)) {
                  goodsSizeView.setText(sku);
                  sizeBar.setVisibility(View.VISIBLE);
               } else {
                  sizeBar.setVisibility(View.GONE);
               }
            }
         }
      };
      myListView.setAdapter(adapter);
   }

   //加载购物车
   private void loadShopCart() {
      if (null != linkedList) {
         totalPrice = 0;
         String info = linkedList.get(0);
         String[] str = info.split("\\|");
         String id = str[0];
         String shopName = str[1];

         GoodsInfo shopInfo = new GoodsInfo();
         shopInfo.shopId = id;
         shopInfo.type = 0;
         shopInfo.shopName = shopName;
         goodsDataList.add(shopInfo);
         shopId = shopInfo.shopId;

         for (String string : linkedList) {
            String[] goods = string.split("\\|");
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.type = 1;
            GoodsDetailsResponeParam goodsDes = new GoodsDetailsResponeParam();
            String goodsId = goods[2];
            String name = goods[3];
            String num = goods[4];
            String price = goods[5];
            String sku = "";
            if (goods.length == 7)
               sku = goods[6];
            goodsDes.setProId(goodsId);
            goodsDes.setProName(name);
            double pr = Double.parseDouble(price);
            int n = Integer.parseInt(num);
            totalPrice += (pr * n);
            goodsDes.setPrice(pr);
            goodsDes.setNum(n);
            goodsDes.setSkuInfo(sku);
            goodsInfo.goods = goodsDes;
            goodsDataList.add(goodsInfo);
         }
         adapter.notifyDataSetChanged();
      }
   }


   //加载用户地址
   private void loadUserAddress() {
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.USER_ADDRESS_LIST, new HttpTool.HttpCallBack<AddrListResponseObject>() {
         @Override
         public void onSuccess(AddrListResponseObject o) {
//            for (AddrListResponseParam address : o.getRecordList()) {
//               String de = address.getIsDefault();
//               if (TextUtils.equals(de, "1")) {//默认的地址
//                  nameView.setText(address.getName());
//                  telView.setText(address.getPhone());
//                  addressView.setText(address.getInfo());
//                  addId = address.getAddrId();
//                  break;
//               }
//            }
            //====================================================
            if (o.getRecordList().size() > 0) {//拿第一条
               String de = o.getRecordList().get(0).getIsDefault();
               nameView.setText(o.getRecordList().get(0).getName());
               telView.setText(o.getRecordList().get(0).getPhone());
               addressView.setText(o.getRecordList().get(0).getAddr() + "   " + o.getRecordList().get(0).getInfo());
               addId = o.getRecordList().get(0).getAddrId();
            }


            //====================================================
         }

         @Override
         public void onError(AddrListResponseObject o) {

         }
      });
   }

   //加载商家信息
   public void loadShopInfo() {
      showProgressDialog(getString(R.string.loading));
      ShopInfoRequestParam shopInfoRequestParam = new ShopInfoRequestParam();
      shopInfoRequestParam.setShopId(shopId);
      ShopInfoRequestObject shopInfoRequestObject = new ShopInfoRequestObject();
      shopInfoRequestObject.setParam(shopInfoRequestParam);
      httpTool.post(shopInfoRequestObject, URLConfig.SHOP_INFO, new HttpTool.HttpCallBack<ShopInfoResponseObject>() {
         @Override
         public void onSuccess(ShopInfoResponseObject o) {
            dismissProgressDialog();
            ShopInfoResponseParam shop = o.getShop();
            double kdFree = shop.getSendFee();//配送费
            double freeFee = shop.getFreeSendFee();//包邮金额
            if (totalPrice < freeFee) {
               totalPrice += kdFree;
               kdFeeView.setText(CustomConfig.RMB + kdFree);
            } else {
               kdFeeView.setText("免费配送");
            }
            setUpView();
         }

         @Override
         public void onError(ShopInfoResponseObject o) {
            dismissProgressDialog();
            Toast.makeText(ConfirmOrderActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
         }
      });
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
         if (requestCode == 1) {
            AddrListResponseParam address = (AddrListResponseParam) data.getSerializableExtra("data");
            if (null != address) {
               nameView.setText(address.getName());
               telView.setText(address.getPhone());
               addressView.setText(address.getAddr() + "   " + address.getInfo());
               addId = address.getAddrId();
            }
         }
      }
      if (data == null) {
         return;
      }
//      String str = data.getExtras().getString("pay_result");
//      if (!TextUtils.isEmpty(str)){
//         if (str.equalsIgnoreCase("success")) {
//            paySuccess();
//         } else if (str.equalsIgnoreCase("fail")) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),"手机银联支付失败！",Toast.LENGTH_LONG).show();
//         } else if (str.equalsIgnoreCase("cancel")) {
//         }
//      }
   }

   private void setUpView() {
      jifenView.setHint("当前可兑换惠宝" + integral);
      //===============================================================
      DecimalFormat df = new DecimalFormat("######0.00");
      payTotalView.setText(CustomConfig.RMB + df.format(totalPrice));
      tv_zongjia.setText(CustomConfig.RMB + df.format(totalPrice));
      //===============================================================
//      payTotalView.setText(CustomConfig.RMB + totalPrice);
//      tv_zongjia.setText(CustomConfig.RMB + totalPrice);
      jifenView.addTextChangedListener(new TextWatcher() {
         @Override
         public void afterTextChanged(Editable s) {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) return;
            if (temp.length() - posDot - 1 > 2) {
               s.delete(posDot + 3, posDot + 4);
            }
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pay = "0";
            if (!TextUtils.isEmpty(s)) {
               pay = s.toString();
            }
            payIntegral = Double.parseDouble(pay);
            if (payIntegral > integral) {
               payIntegral = integral;
            }
            if (payIntegral > totalPrice) {
               payIntegral = totalPrice;
            }
            payTotal = totalPrice - payIntegral;
            //=======================================================================
            DecimalFormat df = new DecimalFormat("######0.00");
            tv_jifen_price.setText("积分抵用¥" + df.format(payIntegral));//更新TextView中的内容
            //=======================================================================
//            tv_jifen_price.setText("积分抵用¥" + payIntegral);//更新TextView中的内容
            payTotalView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(payTotal));
         }


      });
   }

   /**
    * 支付成功后，返回支付状态
    */
   private void paySuccess() {
      Toast.makeText(ConfirmOrderActivity.this, "支付成功", Toast.LENGTH_LONG).show();
      if (null != userInfo && payIntegral > 0) {//计算用户积分
         double num = userInfo.getIntegral() - payIntegral;
         userInfo.setIntegral(num);
         SaveObjectTool.saveObject(userInfo);
      }
      Intent intent = new Intent();
      intent.putExtra("data", PAY_SUCCESS);
      setResult(RESULT_OK, intent);
      finish();
   }

   private void loadPayResult() {
      showProgressDialog(getString(R.string.loading));
      WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
      weixinOrderQueryRequestParam.setOrderId(orderId);
      weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_PRODUCT);

      WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
      weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

      httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            dismissProgressDialog();
            paySuccess();
         }

         @Override
         public void onError(ResponseBaseObject o) {
            dismissProgressDialog();
            payAlertTip.show();
         }
      });
   }

   private void payAlertTip() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(getApplicationContext(), R.layout.app_user_login_tip, null);
      TextView textView = (TextView) view.findViewById(R.id.message);
      Button button = (Button) view.findViewById(R.id.cancel_button);
      textView.setText("该订单未支付，可以到订单列表中继续支付");
      button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            payAlertTip.dismiss();
            finish();
         }
      });
      builder.setView(view);
      builder.setCancelable(false);
      payAlertTip = builder.create();
   }

   private class GoodsInfo {
      String shopId;
      String shopName;
      int type; //0店面信息 1商品信息
      GoodsDetailsResponeParam goods;
   }

   //用户信息
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
            integral = o.getMember().getMoney();
            UserInfoResponseParam userInfo = o.getMember();
            SaveObjectTool.saveObject(userInfo);

         }

         @Override
         public void onError(MemberInfoResponseObject o) {
            integral = 0;
            ToastView.show(o.getErrorMsg());
         }
      });
   }


}
