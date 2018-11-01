package com.huixiangshenghuo.app.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;
import com.doumee.model.response.goodsorder.OrderStatusResponeParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.view.PopUpwindowLayout;

import java.util.ArrayList;
import java.util.List;

public class OrderStateFragment extends Fragment {

   private static final String ORDER_NO = "orderNo";
   private static final String KD_CODE = "kdCode";
   private static final String KD_NAME = "kdname";
   private GoodsOrderInfoResponseParam goodsOrderInfoResponseParam;
   private ListView listView;
   private CustomBaseAdapter<OrderStatusResponeParam> adapter;
   private ArrayList<OrderStatusResponeParam> arrayList;
   private String kdCode;//快递单号
   private String kdname;//快递名称
   //==============================
   private LayoutInflater mInflater;
   //===============================

   public OrderStateFragment() {

   }

   //   public static OrderStateFragment newInstance(GoodsOrderInfoResponseParam orderNo) {
//      OrderStateFragment fragment = new OrderStateFragment();
//      Bundle args = new Bundle();
//      args.putSerializable(ORDER_NO, orderNo);
//      fragment.setArguments(args);
//      return fragment;
//   }
   public static OrderStateFragment newInstance(GoodsOrderInfoResponseParam orderNo, String kdCode, String kdname) {
      OrderStateFragment fragment = new OrderStateFragment();
      Bundle args = new Bundle();
      args.putSerializable(ORDER_NO, orderNo);
      args.putString(KD_CODE, kdCode);
      args.putString(KD_NAME, kdname);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         goodsOrderInfoResponseParam = (GoodsOrderInfoResponseParam) getArguments().getSerializable(ORDER_NO);
         kdCode = getArguments().getString(KD_CODE);
         kdname = getArguments().getString(KD_NAME);
      }
      arrayList = new ArrayList<>();
      List<OrderStatusResponeParam> statusList = goodsOrderInfoResponseParam.getStatusList();
      for (OrderStatusResponeParam order : statusList) {
         if ("0".equals(order.getFlag()) || "1".equals(order.getFlag()) || "3".equals(order.getFlag()) || "4".equals(order.getFlag()))
            arrayList.add(order);
      }
      //==============================
      mInflater = getActivity().getLayoutInflater();
      //===============================
      adapter = new CustomBaseAdapter<OrderStatusResponeParam>(arrayList, R.layout.fragment_order_state_item) {
         @Override
         public void bindView(ViewHolder holder, OrderStatusResponeParam obj) {

            final String flat = obj.getFlag();
            View view = holder.getItemView();
            TextView stateView = (TextView) view.findViewById(R.id.state);
            TextView timeView = (TextView) view.findViewById(R.id.time);
            TextView notesView = (TextView) view.findViewById(R.id.notes);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            TextView text_up = (TextView) view.findViewById(R.id.text_up);
            TextView text_down = (TextView) view.findViewById(R.id.text_down);
            stateView.setText(obj.getStatusName());
            timeView.setText(obj.getHappenDate());
            notesView.setText(obj.getInfo());

            notesView.setOnClickListener(new View.OnClickListener() {
               @TargetApi(Build.VERSION_CODES.CUPCAKE)
               @Override
               public void onClick(View v) {
//                     if(Integer.parseInt(flat)==3) {

// TODO Auto-generated method stub
                  ArrayList<String> titles = new ArrayList<String>();
                  titles.add("复制");
//                        titles.add("删除");

                  View view = mInflater.inflate(R.layout.layout_popupwindow, null);
                  PopUpwindowLayout popUpwindowLayout = (PopUpwindowLayout) view.findViewById(R.id.llayout_popupwindow);
                  popUpwindowLayout.initViews(getActivity(), titles, false);
                  final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                  view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                  int popupWidth = view.getMeasuredWidth();
                  int popupHeight = view.getMeasuredHeight();
                  int[] location = new int[2];
                  // 允许点击外部消失
                  popupWindow.setBackgroundDrawable(new BitmapDrawable());
                  popupWindow.setOutsideTouchable(true);
                  popupWindow.setFocusable(true);
                  // 获得位置
                  v.getLocationOnScreen(location);
                  popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
                  popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
                  popUpwindowLayout.setClickListener(new PopUpwindowLayout.OnClickCallback() {

                     @Override
                     public void onItemClick(LinearLayout parentView, int size, int index) {
                        switch (index) {
                           case 0:
                              // 从API11开始android推荐使用android.content.ClipboardManager
                              // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                              ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                              // 将文本内容放到系统剪贴板里。
                              cm.setText(kdCode);
                              Toast.makeText(getActivity(), "已复制", Toast.LENGTH_SHORT).show();
                              popupWindow.dismiss();
                              break;
//                                 case 1:
//                                    Toast.makeText(getActivity(), "删除", Toast.LENGTH_SHORT).show();
//                                    popupWindow.dismiss();
//                                    break;
                           default:
                              break;
                        }
                     }
                  });
                  //                    }
               }
            });


            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.complete_icon);
            switch (Integer.parseInt(flat)) {
               case 0:
                  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.payment_icon);
                  text_up.setVisibility(View.INVISIBLE);
                  break;
               case 1:
                  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.payment_icon);
                  break;
               case 3:
                  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.receipt_icon);
                  break;
               case 4:
                  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.complete_icon);
                  //    text_down.setVisibility(View.INVISIBLE);//暂时屏蔽
                  break;
            }

            imageView.setImageBitmap(bitmap);
         }
      };
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_order_state, container, false);
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      listView = (ListView) getView().findViewById(R.id.state_list);
      listView.setAdapter(adapter);
   }

   @Override
   public void onStart() {
      super.onStart();
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }

   @Override
   public void onDetach() {
      super.onDetach();
   }


}
