package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.proImg.ProImgListRequestObject;
import com.doumee.model.request.proImg.ProImgListRequestParam;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.request.shopCate.CateListRequestObject;
import com.doumee.model.request.shopCate.CateListRequestParam;
import com.doumee.model.response.proImg.ProImgListResponseObject;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.doumee.model.response.shopCate.CateListResponseObject;
import com.doumee.model.response.shopCate.CateListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.LayoutUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Administrator on 2017/2/9.
 */

public class GoodsFragment extends Fragment {

   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";
   private static final String ARG_PARAM3 = "param3";
   //是否营业
   private String State;
   //商家名称
   private String shopName;
   private ListView menuList;
   private ListView contentList;
   private TextView sumView;
   private TextView sumPriceView;
   private TextView menuView;
   private Button submitButton;
   private FrameLayout shopCartView;
   private ImageView iv_shopCart;

   private String shopId;
   private int cartNum;//购物车数量

   /*  private CustomBaseAdapter<ProCategoryListResponseParam> menuAdapter;
     private ArrayList<ProCategoryListResponseParam> menuDataList;*/
   private CustomBaseAdapter<CateListResponseParam> menuAdapter;
   private ArrayList<CateListResponseParam> menuDataList;
   private ArrayList<GoodsInfo> goodsInfoArrayList;
   private CustomBaseAdapter<GoodsInfo> contentAdapter;

   private int num = 0;//数量
   private double totalPrice = 0d;//总价格
   private String checkMenuId;//当前选中的左侧菜单
   private HashMap<String, ArrayList<GoodsInfo>> map;

   private View rootView;
   private AlertDialog alert;
   private HttpTool httpTool;
   private Bitmap defaultBitmap;
   private HashMap<String, String> shopCartGoodsMap;
   private LinkedList<GoodsInfo> cartGoodsList;

   private int shopnum = 0;//商品数量

   private int zongnum = 0;

   private ShopCatAdapter shopCatAdapter;
   //===========================================

   final ArrayList<String> dataList = new ArrayList<>();//商品相册 （将商品首图也放入）
   //============================================

   public GoodsFragment() {

   }

   public static GoodsFragment newInstance(String param1, String param2, String param3) {
      GoodsFragment fragment = new GoodsFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      args.putString(ARG_PARAM3, param3);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         shopId = getArguments().getString(ARG_PARAM1);
         State = getArguments().getString(ARG_PARAM2);
         shopName = getArguments().getString(ARG_PARAM3);
      }
      cartGoodsList = new LinkedList<>();
      httpTool = HttpTool.newInstance(getActivity());
      defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.business_default);
      menuDataList = new ArrayList<>();

      menuAdapter = new CustomBaseAdapter<CateListResponseParam>(menuDataList, R.layout.fragment_goods_menu) {
         @Override
         public void bindView(ViewHolder holder, final CateListResponseParam obj) {
            final RadioButton radioButton = (RadioButton) holder.getView(R.id.menu_radio);
            final TextView menuLine = (TextView) holder.getView(R.id.menu_line);
//                int num = 0;
//               if (null != obj.getProNum()){
//                   num = obj.getProNum().intValue();
//               }
            radioButton.setText(obj.getName());

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
                     checkMenuId = obj.getCateId();
                     initMenuList(obj.getName());
                     radioButton.setChecked(true);
                     menuLine.setVisibility(View.VISIBLE);
                     loadGoodsInfo(obj.getCateId());
                  }
               }
            });
            if (obj.getCateId().equals(checkMenuId)) {
               radioButton.setChecked(true);
               menuLine.setVisibility(View.VISIBLE);
            } else {
               radioButton.setChecked(false);
               menuLine.setVisibility(View.GONE);
            }

         }
      };
      goodsInfoArrayList = new ArrayList<>();

      contentAdapter = new CustomBaseAdapter<GoodsInfo>(goodsInfoArrayList, R.layout.fragment_goods_content) {
         @Override
         public void bindView(ViewHolder holder, final GoodsInfo obj) {
            View view = holder.getItemView();
            setUpContentAdapterView(view, obj);
            view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
//                  ProductDetailActivity.startProductDetailActivity(getActivity(),obj.groupId);
               }
            });
         }
      };
      map = new HashMap<>();
      shopCartGoodsMap = new HashMap<>();
   }

   private void setUpContentAdapterView(final View view, final GoodsInfo goodsInfo) {
      ImageView imageView = (ImageView) view.findViewById(R.id.goods_image);
      TextView nameView = (TextView) view.findViewById(R.id.goods_name);
      TextView payNumView = (TextView) view.findViewById(R.id.pay_sum);
      TextView priceView = (TextView) view.findViewById(R.id.price);

      LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.add_button_lin);
      Button sizeButton = (Button) view.findViewById(R.id.select_size);
      Button moveButton = (Button) view.findViewById(R.id.move_button);
      Button addButton = (Button) view.findViewById(R.id.add_button);
      final TextView numView = (TextView) view.findViewById(R.id.num);

      imageView.setImageBitmap(defaultBitmap);
      nameView.setText(goodsInfo.name);
      payNumView.setText("月销售" + goodsInfo.payNum + "份");
      priceView.setText(CustomConfig.RMB + "" + goodsInfo.price);
      String imagePath = goodsInfo.image;
      if (!TextUtils.isEmpty(imagePath)) {
         ImageLoader.getInstance().displayImage(imagePath, imageView);
      }


      //选数量
      linearLayout.setVisibility(View.VISIBLE);
      sizeButton.setVisibility(View.GONE);

      numView.setText(goodsInfo.shuliang + "");


      moveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            //检验是否得登录  这里是先登陆
                /*  if (!LoginTool.login(getActivity())) {
                     return;
                  }*/

            if (goodsInfo.shuliang >= 1) {
               goodsInfo.shuliang--;
            }
            if (goodsInfo.shuliang == 0) {
               cartGoodsList.remove(goodsInfo);
            }
            numView.setText(goodsInfo.shuliang + "");

            totalPrice();

         }
      });


      addButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            //检验是否得登录  这里是先登陆
               /*if (!LoginTool.login(getActivity())) {
                  return;
               }*/
//               addShopCart(1,new LinkedList<String>(),goodsInfo);
            if (goodsInfo.shuliang == goodsInfo.stockNum) {
               ToastView.show("库存不足");
               return;
            }
            //增加
            goodsInfo.shuliang++;
            if (!cartGoodsList.contains(goodsInfo)) {
               cartGoodsList.add(goodsInfo);
            }
            numView.setText(goodsInfo.shuliang + "");
            totalPrice();

         }
      });
      //   }

      imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            loadPics(goodsInfo.groupId, goodsInfo.image);
         }
      });




   }

   //==============================================================================
   //查询商品图片
   private void loadPics(String proId, final String imagePath) {
      ProImgListRequestParam paginationBaseObject = new ProImgListRequestParam();
      paginationBaseObject.setProId(proId);
      ProImgListRequestObject proImgListRequestObject = new ProImgListRequestObject();
      proImgListRequestObject.setParam(paginationBaseObject);
      httpTool.post(proImgListRequestObject, URLConfig.SHANGP_PICS, new HttpTool.HttpCallBack<ProImgListResponseObject>() {
         @Override
         public void onSuccess(ProImgListResponseObject o) {
            dataList.clear();
            if (!TextUtils.isEmpty(imagePath)) {
               dataList.add(imagePath);
            }
            if (o.getRecordList().size() > 0) {
               for (int i = 0; i < o.getRecordList().size(); i++) {
                  dataList.add(o.getRecordList().get(i).getImgurl());
               }
            }
            if (dataList.size() > 0) {
               if (!TextUtils.isEmpty(imagePath)) {
                  PhotoActivity.startPhotoActivity(getActivity(), imagePath, dataList);
               } else {
                  PhotoActivity.startPhotoActivity(getActivity(), dataList.get(0), dataList);
               }

            } else {
               ToastView.show("暂无更多图片");
            }


         }

         @Override
         public void onError(ProImgListResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }


   //==============================================================================

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      rootView = inflater.inflate(R.layout.fragment_goods, container, false);
      menuList = (ListView) rootView.findViewById(R.id.menu_list);
      contentList = (ListView) rootView.findViewById(R.id.menu_content);
      sumView = (TextView) rootView.findViewById(R.id.sum_text);
      menuView = (TextView) rootView.findViewById(R.id.menu_name);
      sumPriceView = (TextView) rootView.findViewById(R.id.sum_price_view);
      submitButton = (Button) rootView.findViewById(R.id.submit_button);
      shopCartView = (FrameLayout) rootView.findViewById(R.id.shop_cart);
      iv_shopCart = (ImageView) rootView.findViewById(R.id.iv_shopCart_fragmentGdoos);
      return rootView;
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      View view = rootView;

      submitButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            if (State.equals("0")) {
               if (sumView.getText().toString().toString().equals("0")) {
                  Toast.makeText(getActivity(), "请选择商品", Toast.LENGTH_LONG).show();
                  return;
               }


               ArrayList<String> tempList = new ArrayList<String>();
               for (GoodsInfo info : cartGoodsList) {
                  StringBuilder stringBuilder = new StringBuilder();
                  stringBuilder.append(shopId).append("|");
                  stringBuilder.append(shopName).append("|");
                  stringBuilder.append(info.groupId).append("|");
                  stringBuilder.append(info.name).append("|");
                  stringBuilder.append(info.shuliang).append("|");
                  stringBuilder.append(info.price).append("|");
                  stringBuilder.append("");
                  tempList.add(stringBuilder.toString());
               }
               //订单支付
               Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
               intent.putStringArrayListExtra("data", tempList);
               startActivityForResult(intent, 1, null);

            } else {
               Toast.makeText(getActivity(), "商家休息中", Toast.LENGTH_LONG).show();
            }


            if (cartNum > 0) {
               //跳转到订单确认
//               //  ConfirmOrderActivity.startConfirmOrderActivity(getActivity());
//               ShopCarActivity.startShopCarActivity(getActivity());

            }
         }
      });
      shopCartView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            //跳转到购物车
//            if (cartNum > 0)
//               ShopCarActivity.startShopCarActivity(getActivity());

         }
      });
      iv_shopCart.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            showShopCartPop();
         }
      });
      addGoodsAdapter();
      loadShopMenuList();
   }

   private void addGoodsAdapter() {
      menuList.setAdapter(menuAdapter);
      contentList.setAdapter(contentAdapter);
   }

   private void initMenuList(String position) {
      menuView.setText(position);
      int count = menuList.getChildCount();
      for (int i = 0; i < count; i++) {
         View view = menuList.getChildAt(i);
         TextView menuLine = (TextView) view.findViewById(R.id.menu_line);
         RadioButton radioButton = (RadioButton) view.findViewById(R.id.menu_radio);
         radioButton.setChecked(false);
         menuLine.setVisibility(View.GONE);
      }
   }

   @Override
   public void onStart() {
      super.onStart();
      //检验是否得登录  这里是先登陆
      /*if (!LoginTool.LoginStatus(getActivity())) {

         return;
      }*/
      loadShopCart();
   }

   //加载购物车
   private void loadShopCart() {

   }


   //加载商品分类菜单
   private void loadShopMenuList() {

      CateListRequestParam proCategoryListRequestParam = new CateListRequestParam();
      proCategoryListRequestParam.setShopId(shopId);
      CateListRequestObject proCategoryListRequestObject = new CateListRequestObject();
      proCategoryListRequestObject.setParam(proCategoryListRequestParam);
      httpTool.post(proCategoryListRequestObject, URLConfig.SHOP_CATE, new HttpTool.HttpCallBack<CateListResponseObject>() {
         @Override
         public void onSuccess(CateListResponseObject o) {
            List<CateListResponseParam> listResponseParams = o.getRecordList();
            if (!listResponseParams.isEmpty()) {

               checkMenuId = listResponseParams.get(0).getCateId();
               menuView.setText(listResponseParams.get(0).getName());
               loadGoodsInfo(checkMenuId);

               menuDataList.clear();
               menuDataList.addAll(listResponseParams);
               menuAdapter.notifyDataSetChanged();
            }
         }

         @Override
         public void onError(CateListResponseObject o) {

         }
      });
   }

   private void loadGoodsInfo(final String checkMenuId) {

      ArrayList<GoodsInfo> goodsInfoList = map.get(checkMenuId);
      if (null != goodsInfoList && !goodsInfoList.isEmpty()) {
         goodsInfoArrayList.clear();
         goodsInfoArrayList.addAll(goodsInfoList);
         contentAdapter.notifyDataSetChanged();
         return;
      }

      ProductListRequestParam productListRequestParam = new ProductListRequestParam();
      productListRequestParam.setShopId(shopId);
/*      productListRequestParam.setProCateId(checkMenuId);*/
//      productListRequestParam.setCateId(checkMenuId);
      productListRequestParam.setProCateId(checkMenuId);
      productListRequestParam.setType("0");
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(1);
      paginationBaseObject.setRows(1000);
      paginationBaseObject.setFirstQueryTime("");

      ProductListRequestObject productListRequestObject = new ProductListRequestObject();
      productListRequestObject.setParam(productListRequestParam);
      productListRequestObject.setPagination(paginationBaseObject);

      httpTool.post(productListRequestObject, URLConfig.SHOP_QUERY, new HttpTool.HttpCallBack<ProductListResponseObject>() {
         @Override
         public void onSuccess(ProductListResponseObject o) {
            List<ProductListResponseParam> listResponseParams = o.getProList();
            ArrayList<GoodsInfo> goodsList = new ArrayList<>();
            for (ProductListResponseParam pro : listResponseParams) {
               GoodsInfo goodsInfo = new GoodsInfo();
               goodsInfo.groupId = pro.getProId();
               goodsInfo.name = pro.getProName();
               goodsInfo.payNum = pro.getSaleNum();
               goodsInfo.image = pro.getImgurl();
               goodsInfo.price = pro.getPrice();
               //            goodsInfo.propertyList = pro.getPropertyList();
               goodsInfo.stockNum = pro.getStockNum();
               goodsList.add(goodsInfo);
            }
            map.put(checkMenuId, goodsList);

            goodsInfoArrayList.clear();
            goodsInfoArrayList.addAll(goodsList);
            contentAdapter.notifyDataSetChanged();
         }

         @Override
         public void onError(ProductListResponseObject o) {

         }
      });
   }


   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }

   @Override
   public void onDetach() {
      super.onDetach();
   }

   private class GoodsInfo {
      String groupId;
      String name;
      String image;
      int payNum;//以往购买数量
      double price;//单价
      //   List<PropertyListResponseParam> propertyList;//商品规格
      //数量
      int shuliang;
      int stockNum;

   }

   private void showShopCartPop() {
      View view = View.inflate(getActivity(), R.layout.popup_shop_cart, null);

      final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
      popupWindow.setBackgroundDrawable(new BitmapDrawable());
      popupWindow.setOutsideTouchable(false);
      final ListView listView = (ListView) view.findViewById(R.id.lv_shoCart_pop);
      LinearLayout ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty_shoCart_pop);

      shopCatAdapter = new ShopCatAdapter(getActivity());
      shopCatAdapter.setData(cartGoodsList);
      listView.setAdapter(shopCatAdapter);

      //清空
      ll_empty.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            //		listView.removeAllViews();
            //String.valueOf(
            for (int i = 0; i < cartGoodsList.size(); i++) {
               cartGoodsList.get(i).shuliang = 0;
            }
            totalPrice();
            cartGoodsList.clear();
            contentAdapter.notifyDataSetChanged();
            shopCatAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
         }
      });

//        popupWindow.setFocusable(true);


      int[] location = new int[2];
      iv_shopCart.getLocationOnScreen(location);


//   popupWindow.showAtLocation(iv_shopCart, Gravity.TOP, location[0], location[1] +iv_shopCart.getHeight() - LayoutUtils.px2dip(mActivity, 40) * shopCartList.size());
      popupWindow.showAtLocation(iv_shopCart, Gravity.NO_GRAVITY, location[0], location[1] - LayoutUtils.dip2px(260));
   }

   //总价格 和 总数量
   private void totalPrice() {

      int shopCartCount = 0;
      double totalPrice = 0;
      for (GoodsInfo goodsInfo : cartGoodsList) {
         shopCartCount += goodsInfo.shuliang;
         totalPrice += goodsInfo.price * goodsInfo.shuliang;
      }
      sumView.setText(shopCartCount + "");
      sumPriceView.setText("共" + CustomConfig.RMB + NumberFormatTool.numberFormat(totalPrice));
      int color = 0;
      if (shopCartCount > 0) {
         color = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
      } else {
         color = ContextCompat.getColor(getActivity(), R.color.Grey);
      }
      submitButton.setBackgroundColor(color);


   }

   public class ShopCatAdapter extends BaseAdapter {
      private final Context mContext;
      private List<GoodsInfo> mList = new ArrayList<>();


      public ShopCatAdapter(Context context) {
         this.mContext = context;
      }

      @Override
      public int getCount() {
         return mList.size();
      }

      @Override
      public GoodsInfo getItem(int position) {
         return mList.get(position);
      }

      @Override
      public long getItemId(int position) {
         return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         final Holder holder;
         if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_shop_cart, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name_itemShopCart);
            holder.iv_remoe = (ImageView) convertView.findViewById(R.id.iv_remove_itemShopCart);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count_itemShopCart);
            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add_itemShopCart);
            convertView.setTag(holder);
         } else {
            holder = (Holder) convertView.getTag();
         }

         final GoodsInfo bean = mList.get(position);
         holder.tv_name.setText(bean.name);
         holder.tv_count.setText(String.valueOf(bean.shuliang));
         holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               bean.shuliang++;
               if (!cartGoodsList.contains(bean)) {
                  cartGoodsList.add(bean);
               }
               holder.tv_count.setText(bean.shuliang + "");
               contentAdapter.notifyDataSetChanged();
               totalPrice();
            }
         });

         holder.iv_remoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (bean.shuliang >= 1) {
                  bean.shuliang--;
               }
               if (bean.shuliang == 0) {
                  cartGoodsList.remove(bean);
               }
               holder.tv_count.setText(bean.shuliang + "");
               contentAdapter.notifyDataSetChanged();

               totalPrice();
            }
         });
         return convertView;
      }

      public void setData(List<GoodsInfo> shopCartList) {
         this.mList = shopCartList;
         this.notifyDataSetChanged();
      }

      private class Holder {
         public TextView tv_name;
         public ImageView iv_remoe;
         public TextView tv_count;
         public ImageView iv_add;
      }


   }
   //=============================================================================================


//   private static final String ARG_PARAM1 = "param1";
//
//
//   private ListView menuList;
//   private ListView contentList;
//   private TextView sumView;
//   private TextView sumPriceView;
//   private TextView menuView;
//   private Button submitButton;
//   private FrameLayout shopCartView;
//
//   private String shopId;
//   private int cartNum;//购物车数量
//
//   private CustomBaseAdapter<ProCategoryListResponseParam> menuAdapter;
//   private ArrayList<ProCategoryListResponseParam> menuDataList;
//   private ArrayList<GoodsInfo> goodsInfoArrayList;
//   private CustomBaseAdapter<GoodsInfo> contentAdapter;
//
//   private int num = 0;//数量
//   private double totalPrice = 0d;//总价格
//   private String checkMenuId ;//当前选中的左侧菜单
//   private HashMap<String,ArrayList<GoodsInfo>> map;
//
//   private View rootView;
//   private AlertDialog alert;
//   private HttpTool httpTool;
//   private Bitmap defaultBitmap;
//   private HashMap<String,String> shopCartGoodsMap;
//
//   public GoodsFragment() {
//
//   }
//
//   public static GoodsFragment newInstance(String param1, String param2) {
//      GoodsFragment fragment = new GoodsFragment();
//      Bundle args = new Bundle();
//      args.putString(ARG_PARAM1, param1);
//      fragment.setArguments(args);
//      return fragment;
//   }
//
//   @Override
//   public void onCreate(Bundle savedInstanceState) {
//      super.onCreate(savedInstanceState);
//      if (getArguments() != null) {
//         shopId = getArguments().getString(ARG_PARAM1);
//      }
//      httpTool = HttpTool.newInstance(getActivity());
//      defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.shangjia_defaullt);
//      menuDataList = new ArrayList<>();
//      menuAdapter = new CustomBaseAdapter<ProCategoryListResponseParam>(menuDataList,R.layout.fragment_goods_menu) {
//         @Override
//         public void bindView(ViewHolder holder,final ProCategoryListResponseParam obj) {
//            final RadioButton radioButton = (RadioButton)holder.getView(R.id.menu_radio);
//            final TextView menuLine = (TextView)holder.getView(R.id.menu_line);
//               /* int num = 0;
//               if (null != obj.getProductNum()){
//                   num = obj.getProductNum().intValue();
//               }*/
//            radioButton.setText(obj.getCateName());
//
//            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//               @Override
//               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                  if(isChecked){
//                     checkMenuId = obj.getCateId();
//                     initMenuList(obj.getCateName());
//                     radioButton.setChecked(true);
//                     menuLine.setVisibility(View.VISIBLE);
//                     loadGoodsInfo(obj.getCateId());
//                  }
//               }
//            });
//            if (obj.getCateId().equals(checkMenuId)){
//               radioButton.setChecked(true);
//               menuLine.setVisibility(View.VISIBLE);
//            }else{
//               radioButton.setChecked(false);
//               menuLine.setVisibility(View.GONE);
//            }
//
//         }
//      };
//      goodsInfoArrayList = new ArrayList<>();
//      contentAdapter = new CustomBaseAdapter<GoodsInfo>(goodsInfoArrayList,R.layout.fragment_goods_content) {
//         @Override
//         public void bindView(ViewHolder holder, final GoodsInfo obj) {
//            View view = holder.getItemView();
//            setUpContentAdapterView(view,obj);
//            view.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//                  ProductDetailActivity.startProductDetailActivity(getActivity(),obj.groupId);
//               }
//            });
//         }
//      };
//      map = new HashMap<>();
//      shopCartGoodsMap = new HashMap<>();
//   }
//
//   private void setUpContentAdapterView( View view, final GoodsInfo goodsInfo){
//      ImageView imageView = (ImageView) view.findViewById(R.id.goods_image);
//      TextView nameView = (TextView)view.findViewById(R.id.goods_name);
//      TextView payNumView = (TextView)view.findViewById(R.id.pay_sum);
//      TextView priceView = (TextView)view.findViewById(R.id.price);
//
//      LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.add_button_lin);
//      Button sizeButton = (Button)view.findViewById(R.id.select_size);
//      Button moveButton = (Button)view.findViewById(R.id.move_button);
//      Button addButton = (Button)view.findViewById(R.id.add_button);
//      TextView numView = (TextView)view.findViewById(R.id.num);
//
//      imageView.setImageBitmap(defaultBitmap);
//      nameView.setText(goodsInfo.name);
//      payNumView.setText("月销售"+goodsInfo.payNum+"份");
//      priceView.setText(CustomConfig.RMB+""+goodsInfo.price);
//      String imagePath = goodsInfo.image;
//      if(!TextUtils.isEmpty(imagePath)){
//         ImageLoader.getInstance().displayImage(imagePath,imageView);
//      }
//      if(goodsInfo.propertyList.isEmpty()){//选数量
//         linearLayout.setVisibility(View.VISIBLE);
//         sizeButton.setVisibility(View.GONE);
//         int num = 0;
//         String numStr = shopCartGoodsMap.get(goodsInfo.groupId+"num");
//         if (!TextUtils.isEmpty(numStr)){
//            num = Integer.parseInt(numStr);
//         }
//         numView.setText(num+"");
//         if (num > 1){
//            moveButton.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//                  if (!LoginTool.login(getActivity())) {
//                     return;
//                  }
//                  deleteShopCart(goodsInfo);
//               }
//            });
//         }
//         else if (num == 1){
//            moveButton.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//                  if (!LoginTool.login(getActivity())) {
//                     return;
//                  }
//                  clearShopCartByGoods(goodsInfo);
//               }
//            });
//         }else{
//            moveButton.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//
//               }
//            });
//         }
//
//         addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               if (!LoginTool.login(getActivity())) {
//                  return;
//               }
//               addShopCart(1,new LinkedList<String>(),goodsInfo);
//            }
//         });
//      }else{
//         linearLayout.setVisibility(View.GONE);
//         sizeButton.setVisibility(View.VISIBLE);
//         sizeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               if (!LoginTool.login(getActivity())) {
//                  return;
//               }
//               initDialog(goodsInfo);
//            }
//         });
//      }
//   }
//
//   @Override
//   public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                            Bundle savedInstanceState) {
//      rootView = inflater.inflate(R.layout.fragment_goods, container, false);
//      return  rootView;
//   }
//
//   @Override
//   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//      super.onActivityCreated(savedInstanceState);
//      View view = rootView;
//      menuList = (ListView)view.findViewById(R.id.menu_list);
//      contentList = (ListView)view.findViewById(R.id.menu_content);
//      sumView = (TextView)view.findViewById(R.id.sum_text);
//      menuView = (TextView)view.findViewById(R.id.menu_name);
//      sumPriceView = (TextView)view.findViewById(R.id.sum_price_view);
//      submitButton = (Button)view.findViewById(R.id.submit_button);
//      shopCartView = (FrameLayout) view.findViewById(R.id.shop_cart);
//      submitButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//
//            if(cartNum > 0 ){
//               //跳转到订单确认
//               //  ConfirmOrderActivity.startConfirmOrderActivity(getActivity());
//               ShopCarActivity.startShopCarActivity(getActivity());
//            }
//         }
//      });
//      shopCartView.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            //跳转到购物车
//            if (cartNum > 0)
//               ShopCarActivity.startShopCarActivity(getActivity());
//         }
//      });
//      addGoodsAdapter();
//      loadShopMenuList();
//   }
//
//   private void addGoodsAdapter(){
//      menuList.setAdapter(menuAdapter);
//      contentList.setAdapter(contentAdapter);
//   }
//
//   private void initMenuList(String position){
//      menuView.setText(position);
//      int count = menuList.getChildCount();
//      for (int i = 0 ; i < count ; i++){
//         View view = menuList.getChildAt(i);
//         TextView menuLine = (TextView)view.findViewById(R.id.menu_line);
//         RadioButton radioButton = (RadioButton) view.findViewById(R.id.menu_radio) ;
//         radioButton.setChecked(false);
//         menuLine.setVisibility(View.GONE);
//      }
//   }
//
//   private void initDialog(final GoodsInfo info){
//      final LinkedList<String> linkedList = new LinkedList<>();
//      num = 1;
//      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//      View view = View.inflate(getActivity(), R.layout.fragment_goods_size,null);
//      LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.size_list);
//
//      for (PropertyListResponseParam pro : info.propertyList ){
//         View itemView = View.inflate(getActivity(), R.layout.fragment_goods_size_item,null);
//         TextView titleView = (TextView)itemView.findViewById(R.id.title) ;
//         final GridView optionsGroup = (GridView)itemView.findViewById(R.id.gridview) ;
//         titleView.setText(pro.getPropertyName());
//         titleView.setTag(pro.getPropertId());
//         linearLayout.addView(itemView);
//         List<OptionListResponseParam> opList = pro.getOptionList();
//         final ArrayList<OptionListResponseParam> dataList = new ArrayList<>();
//         dataList.addAll(opList);
//         CustomBaseAdapter<OptionListResponseParam> adapter = new CustomBaseAdapter<OptionListResponseParam>(dataList,R.layout.fragment_goods_size_grid_item) {
//            @Override
//            public void bindView(ViewHolder holder, final OptionListResponseParam obj) {
//               final  RadioButton radioButton = holder.getView(R.id.radio);
//               radioButton.setText(obj.getOptionName());
//
//               radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                  @Override
//                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                     int count = optionsGroup.getChildCount();
//                     for (int i = 0; i < count ;i++){
//                        RadioButton radioButton = (RadioButton) optionsGroup.getChildAt(i).findViewById(R.id.radio);
//                        radioButton.setChecked(false);
//                     }
//                     if (isChecked){
//                        radioButton.setChecked(true);
//                        linkedList.add(obj.getOptionId());
//                     }else{
//                        linkedList.remove(obj.getOptionId());
//                     }
//                  }
//               });
//            }
//         };
//         optionsGroup.setAdapter(adapter);
//      }
//      Button moveButton = (Button)view.findViewById(R.id.move_button) ;
//      Button addButton = (Button)view.findViewById(R.id.add_button) ;
//      final TextView textView = (TextView)view.findViewById(R.id.num) ;
//      textView.setText(""+num);
//      Button cancelButton = (Button)view.findViewById(R.id.cancel_button) ;
//      Button yesButton = (Button)view.findViewById(R.id.yes_button) ;
//      moveButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            if(num > 0){
//               num--;
//               textView.setText(""+num);
//            }
//         }
//      });
//
//      addButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            num++;
//            textView.setText(""+num);
//         }
//      });
//
//      cancelButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            alert.dismiss();
//         }
//      });
//
//      yesButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            if (num == 0) num = 1;
//            addShopCart(num,linkedList,info);
//            //  Log.e("所选记录",linkedList.toString() + "记录数量:"+linkedList.size());
//            alert.dismiss();
//         }
//      });
//      builder.setView(view);
//      builder.setCancelable(false);
//      alert = builder.create();
//      alert.show();
//   }
//
//   //加入购物车
//   private void addShopCart(final int num,List<String> optionList,final GoodsInfo info){
//      ShopcartAddRequestParam shopcartAddRequestParam = new ShopcartAddRequestParam();
//      shopcartAddRequestParam.setProductId(info.groupId);
//      shopcartAddRequestParam.setNum(num);
//      shopcartAddRequestParam.setOptionList(optionList);
//      ShopcartAddRequestObject shopcartAddRequestObject = new ShopcartAddRequestObject();
//      shopcartAddRequestObject.setParam(shopcartAddRequestParam);
//      httpTool.post(shopcartAddRequestObject, HttpUrlConfig.SHOP_CART_ADD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
//         @Override
//         public void onSuccess(ResponseBaseObject o) {
//            loadShopCart();
//         }
//         @Override
//         public void onError(ResponseBaseObject o) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),o.getErrorMsg(),Toast.LENGTH_LONG).show();
//         }
//      });
//   }
//
//   //删除购物车商品
//   private void deleteShopCart(final GoodsInfo info){
//      ShopcartEditRequestParam shopcartEditRequestParam = new ShopcartEditRequestParam();
//      shopcartEditRequestParam.setNum(1);
//      shopcartEditRequestParam.setShopcartId(shopCartGoodsMap.get(info.groupId));
//      shopcartEditRequestParam.setActionType(ShopcartEditRequestParam.ACTION_TYPE_DECREASE);
//      ShopcartEditRequestObject shopcartEditRequestObject = new ShopcartEditRequestObject();
//      shopcartEditRequestObject.setParam(shopcartEditRequestParam);
//
//      httpTool.post(shopcartEditRequestObject, HttpUrlConfig.SHOP_CART_CHANGE, new HttpTool.HttpCallBack<ResponseBaseObject>() {
//         @Override
//         public void onSuccess(ResponseBaseObject o) {
//            loadShopCart();
//         }
//         @Override
//         public void onError(ResponseBaseObject o) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),o.getErrorMsg(),Toast.LENGTH_LONG).show();
//         }
//      });
//   }
//
//   //在购物车中清除单个商品
//   private void clearShopCartByGoods(final GoodsInfo info){
//      ShopcartDelRequestParam shopcartDelRequestParam = new ShopcartDelRequestParam();
//      shopcartDelRequestParam.setShopcartId(shopCartGoodsMap.get(info.groupId));
//      ShopcartDelRequestObject shopcartDelRequestObject = new ShopcartDelRequestObject();
//      shopcartDelRequestObject.setParam(shopcartDelRequestParam);
//      httpTool.post(shopcartDelRequestObject, HttpUrlConfig.SHOP_CART_REMOVE_GOODS, new HttpTool.HttpCallBack<ResponseBaseObject>() {
//         @Override
//         public void onSuccess(ResponseBaseObject o) {
//            loadShopCart();
//         }
//         @Override
//         public void onError(ResponseBaseObject o) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),o.getErrorMsg(),Toast.LENGTH_LONG).show();
//         }
//      });
//   }
//
//   private void updateSubmitColor(){
//      if (null != getActivity()){
//         int color = 0;
//         if (cartNum > 0){
//            color = ContextCompat.getColor(getActivity(),R.color.colorPrimary);
//         }else{
//            color = ContextCompat.getColor(getActivity(),R.color.Grey);
//         }
//         sumPriceView.setText("共"+CustomConfig.RMB+ NumberFormatTool.numberFormat(totalPrice) );
//         sumView.setText(""+cartNum);
//         submitButton.setBackgroundColor(color);
//      }
//   }
//
//   @Override
//   public void onStart() {
//      super.onStart();
//      if (!LoginTool.LoginStatus(getActivity())) {
//
//         return;
//      }
//      loadShopCart();
//   }
//
//   //加载购物车
//   private void loadShopCart(){
//      RequestBaseObject requestBaseObject = new RequestBaseObject();
//      httpTool.post(requestBaseObject, HttpUrlConfig.SHOP_CART_LIST, new HttpTool.HttpCallBack<ShopcartListResponseObject>() {
//         @Override
//         public void onSuccess(ShopcartListResponseObject o) {
//            cartNum = 0;
//            totalPrice = 0;
//            shopCartGoodsMap.clear();
//            List<ShopcartListResponseParam> recordList = o.getRecordList();
//            for (ShopcartListResponseParam cart : recordList){
//               List<GoodsDetailsResponeParam> goodsList = cart.getItemList();
//               for (GoodsDetailsResponeParam goods : goodsList){
//                  cartNum += goods.getNum();
//                  totalPrice += (goods.getNum() * goods.getPrice());
//                  String keyId = goods.getProId();
//                  String keyNum = goods.getProId()+"num";
//                  shopCartGoodsMap.put(keyId,goods.getShopcartId());
//                  shopCartGoodsMap.put(keyNum,goods.getNum()+"");
//               }
//            }
//            contentAdapter.notifyDataSetChanged();
//            updateSubmitColor();
//         }
//         @Override
//         public void onError(ShopcartListResponseObject o) {
//            Toast.makeText(QianbaihuiApplication.getQianbaihuiApplication(),o.getErrorMsg(),Toast.LENGTH_LONG).show();
//         }
//      });
//   }
//
//
//
//   //加载商品分类菜单
//   private void loadShopMenuList(){
//      ProCategoryListRequestParam proCategoryListRequestParam = new ProCategoryListRequestParam();
//      proCategoryListRequestParam.setShopId(shopId);
//      ProCategoryListRequestObject proCategoryListRequestObject = new ProCategoryListRequestObject();
//      proCategoryListRequestObject.setParam(proCategoryListRequestParam);
//      httpTool.post(proCategoryListRequestObject, HttpUrlConfig.GOODS_TYPE, new HttpTool.HttpCallBack<ProCategoryListResponseObject>() {
//         @Override
//         public void onSuccess(ProCategoryListResponseObject o) {
//            List<ProCategoryListResponseParam> listResponseParams = o.getRecordList();
//            if(!listResponseParams.isEmpty()){
//
//               checkMenuId = listResponseParams.get(0).getCateId();
//               menuView.setText(listResponseParams.get(0).getCateName());
//               loadGoodsInfo(checkMenuId);
//
//               menuDataList.clear();
//               menuDataList.addAll(listResponseParams);
//               menuAdapter.notifyDataSetChanged();
//            }
//         }
//         @Override
//         public void onError(ProCategoryListResponseObject o) {
//
//         }
//      });
//   }
//
//   private void loadGoodsInfo(final String checkMenuId){
//
//      ArrayList<GoodsInfo> goodsInfoList = map.get(checkMenuId);
//      if(null != goodsInfoList && !goodsInfoList.isEmpty()){
//         goodsInfoArrayList.clear();
//         goodsInfoArrayList.addAll(goodsInfoList);
//         contentAdapter.notifyDataSetChanged();
//         return;
//      }
//
//      ProductListRequestParam productListRequestParam = new ProductListRequestParam();
//      productListRequestParam.setShopId(shopId);
//      //productListRequestParam.setProCateId(checkMenuId);
//      productListRequestParam.setCateId(checkMenuId);
//      productListRequestParam.setType("0");
//      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
//      paginationBaseObject.setPage(1);
//      paginationBaseObject.setRows(1000);
//
//      ProductListRequestObject productListRequestObject = new ProductListRequestObject();
//      productListRequestObject.setParam(productListRequestParam);
//      productListRequestObject.setPagination(paginationBaseObject);
//
//      httpTool.post(productListRequestObject, HttpUrlConfig.GOODS_LIST, new HttpTool.HttpCallBack<ProductListResponseObject>() {
//         @Override
//         public void onSuccess(ProductListResponseObject o) {
//            List<ProductListResponseParam> listResponseParams = o.getProList();
//            ArrayList<GoodsInfo> goodsList = new ArrayList<>();
//            for (ProductListResponseParam pro : listResponseParams){
//               GoodsInfo goodsInfo = new GoodsInfo();
//               goodsInfo.groupId = pro.getProId();
//               goodsInfo.name = pro.getProName();
//               goodsInfo.payNum = pro.getSaleNum();
//               goodsInfo.image = pro.getImgurl();
//               goodsInfo.price = pro.getPrice();
//               goodsInfo.propertyList = pro.getPropertyList();
//               goodsList.add(goodsInfo);
//            }
//            map.put(checkMenuId,goodsList);
//
//            goodsInfoArrayList.clear();
//            goodsInfoArrayList.addAll(goodsList);
//            contentAdapter.notifyDataSetChanged();
//         }
//         @Override
//         public void onError(ProductListResponseObject o) {
//
//         }
//      });
//   }
//
//
//   @Override
//   public void onAttach(Context context) {
//      super.onAttach(context);
//   }
//
//   @Override
//   public void onDetach() {
//      super.onDetach();
//   }
//
//   private class GoodsInfo {
//      String groupId;
//      String name;
//      String image;
//      int payNum;//以往购买数量
//      double price;//单价
//      List<PropertyListResponseParam> propertyList;//商品规格
//   }

   //=============================================================================================












}
