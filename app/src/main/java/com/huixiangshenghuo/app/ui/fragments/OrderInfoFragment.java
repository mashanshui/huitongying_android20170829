package com.huixiangshenghuo.app.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopDetailsOnlineActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.goodsorder.GoodsDetailsResponeParam;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;

import java.util.List;


public class OrderInfoFragment extends Fragment {

   private static final String ORDER_NO = "orderNo";
   private GoodsOrderInfoResponseParam orderNo;
   private TextView shopNameView;
   private LinearLayout goodsListView;
   private TextView peiFeeView;
   private TextView jifenFeeView;//积分
   private TextView totalFeeView;
   private TextView payFeeView;
   //   private TextView peisongTimeView;//配送时间
   private TextView addressView, orderNoView, orderTimeView, payTypeView, peisongRenView, tv_beizhui;
   private LinearLayout ll_beizhu;
   private RelativeLayout rl_beizhu;
   private TextView tv_courier_number;//快递单号
   private TextView tv_courier_name;//快递名称
   private TextView tv_phone;//客服电话
   private String phone;
   protected HttpTool httpTool;
   View view;

   public OrderInfoFragment() {

   }

   public static OrderInfoFragment newInstance(GoodsOrderInfoResponseParam orderNo) {
      OrderInfoFragment fragment = new OrderInfoFragment();
      Bundle args = new Bundle();
      args.putSerializable(ORDER_NO, orderNo);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         orderNo = (GoodsOrderInfoResponseParam) getArguments().getSerializable(ORDER_NO);
      }
      httpTool = HttpTool.newInstance(getActivity());
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_order_info, container, false);
      loadDataIndex();
      return view;
//      return inflater.inflate(R.layout.fragment_order_info, container, false);
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      View view = getView();
      shopNameView = (TextView) view.findViewById(R.id.shop_name);
      goodsListView = (LinearLayout) view.findViewById(R.id.goods_list);
      peiFeeView = (TextView) view.findViewById(R.id.peisong_fee);
      jifenFeeView = (TextView) view.findViewById(R.id.jifen_fee);
      totalFeeView = (TextView) view.findViewById(R.id.total_fee);
      payFeeView = (TextView) view.findViewById(R.id.pay_fee);
//      peisongTimeView = (TextView) view.findViewById(R.id.peisong_time);
      addressView = (TextView) view.findViewById(R.id.address);
      orderNoView = (TextView) view.findViewById(R.id.order_no);
      orderTimeView = (TextView) view.findViewById(R.id.order_time);
      payTypeView = (TextView) view.findViewById(R.id.pay_type);
      peisongRenView = (TextView) view.findViewById(R.id.peisong_renyuan);
      tv_beizhui = (TextView) view.findViewById(R.id.tv_beizhui);
      ll_beizhu = (LinearLayout) view.findViewById(R.id.ll_beizhu);
      rl_beizhu = (RelativeLayout) view.findViewById(R.id.rl_beizhu);
      tv_courier_number = (TextView) view.findViewById(R.id.tv_courier_number);
      tv_courier_name = (TextView) view.findViewById(R.id.tv_courier_name);
      tv_phone = (TextView) view.findViewById(R.id.tv_customer_service_phone);

      setUpView();
   }


   @Override
   public void onStart() {
      super.onStart();
   }

   private void setUpView() {

      shopNameView.setText(orderNo.getShopName());
      peiFeeView.setText(CustomConfig.RMB + orderNo.getSendFee());
      jifenFeeView.setText(CustomConfig.RMB + orderNo.getIntegralNum());

      //   payFeeView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(orderNo.getTotalPrice() - orderNo.getIntegralNum()));

//      peisongTimeView.setText("立即配送");
      addressView.setText(orderNo.getMemberName() + " " + orderNo.getPhone() + " " + orderNo.getAddr());
      orderNoView.setText(orderNo.getOrderId());
      orderTimeView.setText(orderNo.getCreateDate());
      // orderNo.get
      payTypeView.setText("在线支付");
      //   peisongRenView.setText(orderNo.getDeliverName());
      //   totalFeeView.setText(CustomConfig.RMB + orderNo.getTotalPrice());
      totalFeeView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(orderNo.getTotalPrice()));//总计
      payFeeView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(orderNo.getTotalPrice() - orderNo.getIntegralNum()));//实付
      if (orderNo.getInfo() == null || TextUtils.isEmpty(orderNo.getInfo())) {
         ll_beizhu.setVisibility(View.GONE);
         rl_beizhu.setVisibility(View.GONE);
      } else {
         ll_beizhu.setVisibility(View.VISIBLE);
         rl_beizhu.setVisibility(View.VISIBLE);
         tv_beizhui.setText(orderNo.getInfo());//备注信息 有的时候显示，空的时候不显示
      }
      //快递信息
      tv_courier_number.setText(orderNo.getKdCode());
      tv_courier_name.setText(orderNo.getKdName());


      shopNameView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            orderNo.getShopId();
            //        ShopActivity.startShopActivity(getActivity(),orderNo.getShopId(),"0");
            //   ShopDetailsActivity.startShopDetailsActivity(getActivity(), orderNo.getShopId());
            ShopDetailsOnlineActivity.startShopActivity(getActivity(), orderNo.getShopId(), "");
//            if (ShopOrderInfoActivity.Type.equals("0")) {//0  线下  1 线上
//               ShopDetailsActivity.startShopDetailsActivity(getActivity(), orderNo.getShopId());
//            } else {
//               ShopDetailsOnlineActivity.startShopActivity(getActivity(),orderNo.getShopId(), "");
//
//            }

         }
      });
      tv_phone.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dh();
         }
      });

      List<GoodsDetailsResponeParam> goodsList = orderNo.getGoodsList();

      double total = 0d;
      for (GoodsDetailsResponeParam goods : goodsList) {
//         View view = View.inflate(getContext(), R.layout.fragment_order_info_item, null);
//         TextView goodsNameV = (TextView) view.findViewById(R.id.goods_name);
//         TextView goodsNumberV = (TextView) view.findViewById(R.id.number);
//         TextView goodsPriceV = (TextView) view.findViewById(R.id.price);
//         TextView goodsSizeV = (TextView) view.findViewById(R.id.size);
//         LinearLayout sizeBar = (LinearLayout) view.findViewById(R.id.size_bar);
//         goodsNameV.setText(goods.getProName());
//         goodsNumberV.setText(CustomConfig.RMB + goods.getPrice() + "*" + goods.getNum());
//         double p = goods.getPrice() * goods.getNum();
//
//         total += goods.getPrice() * goods.getNum();
//
//         goodsPriceV.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(p));
//         String sku = goods.getSkuInfo();
//         if (!TextUtils.isEmpty(sku)) {
//            goodsSizeV.setText(sku);
//            sizeBar.setVisibility(View.VISIBLE);
//         }
//         goodsListView.addView(view);
         //带有商品图片的
         View view = View.inflate(getContext(), R.layout.fragment_order_info_item_new, null);
         TextView goodsNameV = (TextView) view.findViewById(R.id.goods_name);
         TextView goodsNumberV = (TextView) view.findViewById(R.id.number);

         TextView goodsSizeV = (TextView) view.findViewById(R.id.size);
         LinearLayout sizeBar = (LinearLayout) view.findViewById(R.id.size_bar);
         goodsNameV.setText(goods.getProName());
         goodsNumberV.setText(CustomConfig.RMB + goods.getPrice() + "\n x" + goods.getNum());
         double p = goods.getPrice() * goods.getNum();

         total += goods.getPrice() * goods.getNum();


         String sku = goods.getSkuInfo();
         if (!TextUtils.isEmpty(sku)) {
            goodsSizeV.setText(sku);
            sizeBar.setVisibility(View.VISIBLE);
         }
         goodsListView.addView(view);
      }
//      totalFeeView.setText(CustomConfig.RMB+NumberFormatTool.numberFormat(total));//总计
//      payFeeView.setText(CustomConfig.RMB + NumberFormatTool.numberFormat(total - orderNo.getIntegralNum()));//实付
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }

   @Override
   public void onDetach() {
      super.onDetach();
   }

   //加载数据字典
   private void loadDataIndex() {
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


                  phone = app.getContent();
                  tv_phone.setText(phone);
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

   private void dh() {
      final Dialog dialog1 = new Dialog(getActivity());
      dialog1.setTitle("温馨提示");
      dialog1.setMessage(phone);
      dialog1.setConfirmText("呼叫");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog1.dismiss();
         }
      });
      dialog1.show();
   }
}
