package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Administrator on 2017/2/14.
 */

public class GoodsFragment2 extends Fragment implements View.OnClickListener {
   @Override
   public void onClick(View v) {

   }
//   @ViewInject(R.id.lv_gdoosCategory_fragmentGdoos)
//   private ListView lv_gdoosCategory;
//
//   @ViewInject(R.id.lv_gdoosDetail_fragmentGdoos)
//   private ListView lv_gdoosDetail;
//
//   @ViewInject(R.id.tv_noDate_fragmentGdoos)
//   private TextView tv_noDate;
//
//   @ViewInject(R.id.tv_curentCount_fragmentGdoos)
//   private TextView tv_currentCount;
//
//   @ViewInject(R.id.iv_shopCart_fragmentGdoos)
//   private ImageView iv_shopCart;
//
//
//   @ViewInject(R.id.tv_totalPrice_fragmentGdoos)
//   private TextView tv_totalPrice;
//
//
//   @ViewInject(R.id.tv_startSend_fragmentGdoos)
//   private TextView tv_startSend;
//
//   private View mView;
//   private ShopDetailsOnlineActivity mActivity;
//   private GdoosCategoryAdapter categoryAdapter;
//   private GoodsDetailAdapter gdoosDeatilAdapter;
//   private List<ProductsListResponseCustomParam> shopCartList = new ArrayList<>();
//   private float currentCartPrice = 0.0f;
//   private ShopCatAdapter shopCatAdapter;
//   private HashMap<String, List<ProductsListResponseCustomParam>> gdoosMap = new HashMap<>();
//   private View mLastView;
//
//   @Nullable
//   @Override
//   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//      mView = inflater.inflate(R.layout.fragment_gdoos, container, false);
//      ViewUtils.inject(this, mView);
//      mActivity = (ShopDetailsOnlineActivity) getActivity();
//      initView();
//      setListener();
//      return mView;
//   }
//
//
//   private void initView() {
//      categoryAdapter = new GdoosCategoryAdapter(getActivity());
//      lv_gdoosCategory.setAdapter(categoryAdapter);
//
//       /* lv_gdoosCategory.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                lv_gdoosCategory.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                lv_gdoosCategory.setSelected(true);
//                lv_gdoosCategory.setSelection(1);
//                lv_gdoosCategory.setItemChecked(1, true);
//                categoryAdapter.notifyDataSetChanged();
//            }
//        }, 5000);*/
//
//      gdoosDeatilAdapter = new GoodsDetailAdapter(mActivity);
//      lv_gdoosDetail.setAdapter(gdoosDeatilAdapter);
//      mActivity.setOnloadDataListener(new ShopDetailsOnlineActivity.OnloadDataListener() {
//         @Override
//         public void onLoad() {
//            tv_startSend.setText(mActivity.getResources().getString(R.string.text_startSend_fragmentGdoos, mActivity.getShopDetailInfo().getStartSendPrice()));
//         }
//      });
//      if (mActivity.getShopDetailInfo() != null) {
//         tv_startSend.setText(mActivity.getResources().getString(R.string.text_startSend_fragmentGdoos, mActivity.getShopDetailInfo().getStartSendPrice()));
//      }
//
//      iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.bg_shop_icon_unselect));
//      tv_totalPrice.setText(R.string.text_no_order);
//   }
//
//
//   private void setListener() {
//
//      categoryAdapter.setFirstChildListener(new GdoosCategoryAdapter.OnFirstChildLoadListener() {
//         @Override
//         public void onLoad(View view) {
//            mLastView = view;
//         }
//      });
//      lv_gdoosCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//         @Override
//         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                /*if (position == 0 && mLastView != null) {
//                    mLastView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
//                } else */
//            if (mLastView != null) {
//               mLastView.setBackgroundColor(mActivity.getResources().getColor(R.color.dark_gray));
//            }
//            view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
//            mLastView = view;
//
//            ProductCateListResponseParam item = (ProductCateListResponseParam) parent.getItemAtPosition(position);
//            getGdoosDetail(item.getCateId(), 1);
//         }
//      });
//
//      gdoosDeatilAdapter.setListener(new GoodsDetailAdapter.OnGdoosChangeListener() {
//         @Override
//         public void onChangeListener(ProductsListResponseCustomParam bean, int currentNum) {
//            iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_shop_cart));
//            tv_totalPrice.setTextColor(mActivity.getResources().getColor(R.color.app_main_color));
//            tv_currentCount.setVisibility(View.VISIBLE);
//            updateShopCart(bean);
//
//         }
//      });
//      iv_shopCart.setOnClickListener(this);
//   }
//
//   @Override
//   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//      super.onActivityCreated(savedInstanceState);
//
//      CateListRequestParam proCategoryListRequestParam = new CateListRequestParam();
//      proCategoryListRequestParam.setShopId(shopId);
//      CateListRequestObject proCategoryListRequestObject = new CateListRequestObject();
//      proCategoryListRequestObject.setParam(proCategoryListRequestParam);
//      httpTool.post(proCategoryListRequestObject, URLConfig.SHOP_CATE, new HttpTool.HttpCallBack<CateListResponseObject>() {
//         @Override
//         public void onSuccess(CateListResponseObject o) {
//            if (o.getRecordList().size() > 0) {
//               String cateId = o.getRecordList().get(0).getCateId();
//               categoryAdapter.setDate(o.getRecordList());
//               getGdoosDetail(cateId, 1);
//
//
////                    lv_gdoosCategory.setSelection(0);
//
//            }
//         }
//
//         @Override
//         public void onError(CateListResponseObject o) {
//
//         }
//      });
//   }
//
//   private void getGdoosDetail(final String cateId, int page) {
//      //请求商品详情
//      //商品详情
//      ProductsListRequestObject productListObject = new ProductsListRequestObject();
//      PaginationBaseObject pageObject = new PaginationBaseObject();
//
//      if (page == 1) {
//         List<ProductsListResponseCustomParam> productList = gdoosMap.get(cateId);
//         if (productList != null && productList.size() > 0) {
//            tv_noDate.setVisibility(productList.size() == 0 ? View.VISIBLE : View.GONE);
//            gdoosDeatilAdapter.setDate(productList);
//
//
//            return;
//         }
//      }
//      pageObject.setPage(page);
//      pageObject.setRows(10);
//
//      ProductsListRequestParam productListParam = new ProductsListRequestParam();
//      productListParam.setShopId(mActivity.getShopId());
//      productListParam.setCategoryId(cateId);
//      productListParam.setIsDeleted(ProductsModel.ONLINE_YES);
//
//      productListObject.setParam(productListParam);
//      productListObject.setPagination(pageObject);
//      BaseRequestBuilder<ProductsListRequestObject, ProductsListResponseBean> builderList = new BaseRequestBuilder<>(productListObject, Configs.GDOOS_CATEGORY_LIST);
//      builderList.setCallBack(new BaseNetCallBack<ProductsListResponseBean>() {
//         @Override
//         public void onSuccess(ProductsListResponseBean responseList) {
//            tv_noDate.setVisibility(responseList.getProList().size() == 0 ? View.VISIBLE : View.GONE);
//            gdoosDeatilAdapter.setDate(responseList.getProList());
//            gdoosMap.put(cateId, responseList.getProList());
//         }
//      }).send();
//   }
//
//   @Override
//   public void onClick(View v) {
//      switch (v.getId()) {
//         case R.id.iv_shopCart_fragmentGdoos:
//            if (tv_currentCount.getText().toString().trim().equals("0") || tv_currentCount.getText().toString().trim().equals("")) {
//
//            } else {
//               showShopCartPop();
//            }
//
//            break;
//      }
//   }
//
//   private void showShopCartPop() {
//      View view = View.inflate(mActivity, R.layout.popup_shop_cart, null);
//
//      final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//      popupWindow.setBackgroundDrawable(new BitmapDrawable());
//      popupWindow.setOutsideTouchable(false);
//      final ListView listView = (ListView) view.findViewById(R.id.lv_shoCart_pop);
//      LinearLayout ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty_shoCart_pop);
//
//      shopCatAdapter = new ShopCatAdapter(getActivity());
//      shopCatAdapter.setData(shopCartList);
//      listView.setAdapter(shopCatAdapter);
//      shopCatAdapter.setListener(new ShopCatAdapter.OnShopCarChangeListener() {
//         @Override
//         public void onChangeListener(ProductsListResponseCustomParam bean, int count) {
//            gdoosDeatilAdapter.notifyDataSetChanged();
//            updateShopCart(bean);
//         }
//      });
//      //清空
//      ll_empty.setOnClickListener(new View.OnClickListener() {
//
//         @Override
//         public void onClick(View v) {
//            // TODO Auto-generated method stub
////				listView.removeAllViews();
//            //String.valueOf(
//            for (int i = 0; i < shopCartList.size(); i++) {
//               gdoosDeatilAdapter.setinto(shopCartList.get(i).getCount());
//            }
//            gdoosDeatilAdapter.notifyDataSetChanged();
//            tv_startSend.setText(mActivity.getResources().getString(R.string.text_startSend_fragmentGdoos, mActivity.getShopDetailInfo().getStartSendPrice()));
//            tv_startSend.setBackgroundColor(mActivity.getResources().getColor(R.color.bussiness_allDone));
//            tv_startSend.setTextColor(mActivity.getResources().getColor(R.color.textyanse));
//            iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.bg_shop_icon_unselect));
//            tv_totalPrice.setText(R.string.text_no_order);
//            tv_totalPrice.setTextColor(mActivity.getResources().getColor(R.color.gray));
//            shopCartList.clear();
//            shopCatAdapter.notifyDataSetChanged();
//            tv_currentCount.setText("0");
//            popupWindow.dismiss();
//         }
//      });
//
////        popupWindow.setFocusable(true);
//
//
//      int[] location = new int[2];
//      iv_shopCart.getLocationOnScreen(location);
//
//
////   popupWindow.showAtLocation(iv_shopCart, Gravity.TOP, location[0], location[1] +iv_shopCart.getHeight() - LayoutUtils.px2dip(mActivity, 40) * shopCartList.size());
//      popupWindow.showAtLocation(iv_shopCart, Gravity.NO_GRAVITY, location[0], location[1] - LayoutUtils.dip2px(260));
//   }
//
//   @Override
//   public void onResume() {
//      super.onResume();
//   }
//
//   private void updateShopCart(ProductsListResponseCustomParam bean) {
//      if (shopCartList.contains(bean)) {
//         if (bean.getCount() == 0) {
//            shopCartList.remove(bean);
//            try {
//               shopCatAdapter.notifyDataSetChanged();
//
//            } catch (Exception e) {
//               // TODO: handle exception
//            }
//
//         } else {
//            shopCartList.set(shopCartList.indexOf(bean), bean);
//         }
//      } else {
//         shopCartList.add(bean);
//      }
//      int shopCartCount = 0;
//      float shopCartPrice = 0.0f;
//      for (ProductsListResponseCustomParam param : shopCartList) {
//         shopCartCount += param.count;
//         shopCartPrice += param.getCount() * param.getPrice();
//      }
//      DecimalFormat fnum = new DecimalFormat("##0.00");
//      String remainPay = fnum.format(shopCartPrice);
//      tv_totalPrice.setText("￥" + remainPay);
//      tv_currentCount.setText(String.valueOf(shopCartCount));
//      float startSend = 0.0f;
//      try {
//         startSend = Float.valueOf(mActivity.getShopDetailInfo().getStartSendPrice());
//      } catch (NumberFormatException e) {
//         e.printStackTrace();
//      }
//      if (shopCartPrice < startSend) {
//         float startSendPrice = Float.valueOf(mActivity.getShopDetailInfo().getStartSendPrice()) - shopCartPrice;
//         tv_startSend.setText(mActivity.getResources().getString(R.string.text_startSend_fragmentGdoos, startSendPrice));
//         tv_startSend.setBackgroundColor(mActivity.getResources().getColor(R.color.bussiness_allDone));
//         tv_startSend.setTextColor(mActivity.getResources().getColor(R.color.textyanse));
//         iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.bg_shop_icon_unselect));
//
//
//      } else {
//         if (tv_currentCount.getText().toString().trim().equals("0")) {
//            tv_startSend.setText(mActivity.getResources().getString(R.string.text_startSend_fragmentGdoos, mActivity.getShopDetailInfo().getStartSendPrice()));
//            tv_startSend.setBackgroundColor(mActivity.getResources().getColor(R.color.bussiness_allDone));
//            tv_startSend.setTextColor(mActivity.getResources().getColor(R.color.textyanse));
//            iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.bg_shop_icon_unselect));
//            tv_totalPrice.setText(R.string.text_no_order);
//            tv_totalPrice.setTextColor(mActivity.getResources().getColor(R.color.gray));
//            return;
//         }
//         tv_startSend.setText(mActivity.getResources().getString(R.string.text_allSure_fragmentGdoos));
//         tv_startSend.setBackgroundColor(mActivity.getResources().getColor(R.color.app_main_color));
//         tv_startSend.setTextColor(mActivity.getResources().getColor(R.color.white));
//         iv_shopCart.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_shop_cart));
//      }
//
//      final float finalStartSend = startSend;
//      final float finalShopCartPrice = shopCartPrice;
//      tv_startSend.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            if (TextUtils.isEmpty(PreferencesConfig.memberId.get())) {
//               startActivityForResult(new Intent(getActivity(), ActivityLogin.class), 0);
//               return;
//            }
//
//            if (finalShopCartPrice >= finalStartSend) {
//               if (finalShopCartPrice == 0) {
//                  Toast.makeText(getActivity(), "请选择商品", Toast.LENGTH_SHORT).show();
//                  return;
//               }
//               Intent intent = new Intent(mActivity, OrderSureActivity.class);
//               ArrayList<ShopCartBean> shopList = new ArrayList<>();
//               for (ProductsListResponseCustomParam param : shopCartList) {
//                  ShopCartBean shopCartBean = new ShopCartBean();
//                  shopCartBean.count = String.valueOf(param.getCount());
//                  shopCartBean.name = param.getGoodsName();
//                  shopCartBean.price = param.getPrice();
//                  shopCartBean.gdoosID = param.getGoodsId();
//                  shopList.add(shopCartBean);
//               }
//               intent.putExtra(OrderSureActivity.SHOP_ID, mActivity.getShopId());
//               intent.putExtra(OrderSureActivity.SHOP_CAR, shopList);
//               intent.putExtra(OrderSureActivity.TRANSI_TTIME, mActivity.getShopDetailInfo().getTransitTime());
//               intent.putExtra(OrderSureActivity.SHOP_NAME, mActivity.getShopDetailInfo().getShopName());
//               String str = tv_totalPrice.getText().toString();
//
//               intent.putExtra(OrderSureActivity.TOTAL_PRICE, str.substring(str.indexOf("￥") + 1));
//               intent.putExtra(OrderSureActivity.TRANSI_PRICE, mActivity.getShopDetailInfo().getSendPrice());
//               startActivity(intent);
//            } else {
//               Toast.makeText(mActivity, "未达到配送价格", Toast.LENGTH_LONG).show();
//            }
//         }
//      });
//   }
}