package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.adaptershopcirrcle.ShopDetailsAdapter;
import com.huixiangshenghuo.app.comm.baidu.BaiduPoiSearchActivity;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.Dialog;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.shop.ShopInfoRequestObject;
import com.doumee.model.request.shop.ShopInfoRequestParam;
import com.doumee.model.request.shopcomment.ShopCommentListRequestObject;
import com.doumee.model.request.shopcomment.ShopCommentListRequestParam;
import com.doumee.model.response.comment.ShopCommentListResponseObject;
import com.doumee.model.response.comment.ShopCommentListResponseParam;
import com.doumee.model.response.shop.ShopInfoResponseObject;
import com.doumee.model.response.shop.ShopInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.home.ZhuanInfoActivity;
import com.huixiangshenghuo.app.view.MyListView;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/2/9.
 */

public class ShopFragment extends Fragment {
   private static final String ARG_PARAM1 = "shopId";
   private static final String ARG_PARAM2 = "param2";
   View view;


   private TextView tv_shop_address;//商家地址
   private TextView tv_shop_phone;//商家电话
   private TextView rl_shop_details;//商家详情
   private TextView tv_shop_hours;//营业时间
   private MyListView lv_shop_comment;//商家评论

   private Button bt_check;


   private static String SHOPID = "shopId";

   /**
    * 商家id
    */
   private String shopId;
   private String mParam2;
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
   /**
    * 数据源
    */
   private ArrayList<ShopCommentListResponseParam> arrlist = new ArrayList<ShopCommentListResponseParam>();

   private ShopInfoResponseParam shopInfo;

/*   public static void startShopDetailsActivity(Context context, String shopId) {
      Intent intent = new Intent(context, ShopDetailsActivity.class);
      intent.putExtra(SHOPID, shopId);
      context.startActivity(intent);
   }*/

   public static ShopFragment newInstance(String param1, String param2) {
      ShopFragment fragment = new ShopFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.fragment_shop_info_list, container, false);

      httpTool = HttpTool.newInstance(getActivity());
      if (getArguments() != null) {
         shopId = getArguments().getString(ARG_PARAM1);
         mParam2 = getArguments().getString(ARG_PARAM2);
      }
      initview();
      request();
      refresh();
      loadShopComment();
      return view;
   }


   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      /*
      httpTool = HttpTool.newInstance(getActivity());

      initview();
      request();
      refresh();
      loadShopComment();*/
   }

   private void refresh() {
      mAdapter = new ShopDetailsAdapter(arrlist, getActivity());
      lv_shop_comment.setAdapter(mAdapter);
   }

   private void initview() {

      tv_shop_address = (TextView) view.findViewById(R.id.tv_shop_address);
      tv_shop_phone = (TextView) view.findViewById(R.id.tv_shop_phone);
      rl_shop_details = (TextView) view.findViewById(R.id.tv_shop_details);
      tv_shop_hours = (TextView) view.findViewById(R.id.tv_shop_hours);
      lv_shop_comment = (MyListView) view.findViewById(R.id.lv_shop_comment);
      bt_check = (Button) view.findViewById(R.id.bt_shop_info_list_check);

      tv_shop_phone.setOnClickListener(new ShopFragment.MyOnClick());
      rl_shop_details.setOnClickListener(new ShopFragment.MyOnClick());
      tv_shop_address.setOnClickListener(new ShopFragment.MyOnClick());
      bt_check.setOnClickListener(new ShopFragment.MyOnClick());
   }

   class MyOnClick implements View.OnClickListener {

      @Override
      public void onClick(View arg0) {
         switch (arg0.getId()) {
            case R.id.tv_shop_address:
               BaiduPoiSearchActivity.startBaiduPoiSearchActivity(getActivity(), shopInfo.getLongitude(), shopInfo.getLatitude(), BaiduPoiSearchActivity.FLAG_SHOW_ADDRESS);
               break;
            case R.id.iv_shop_details_back:
               //      finish();
               break;
            case R.id.bt_shop_info_list_check:
               ZhuanInfoActivity.startZhuanInfoActivity(getActivity(), shopInfo);
               break;
            case R.id.tv_shop_phone:
               dh();
               break;
            case R.id.tv_shop_details:
               ShopInfoActivity.startShopInfoActivity(getActivity(), info, "1");
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

            int score = o.getShop().getScore().intValue();

            String cateName = o.getShop().getCategoryName();
            if (TextUtils.isEmpty(cateName)) {
               cateName = "其他";
            }

            tv_shop_address.setText(o.getShop().getAddr());
            tv_shop_phone.setText(o.getShop().getPhone());
            tv_shop_hours.setText("营业时间：" + o.getShop().getStartTime() + "-" + o.getShop().getEndTime());
            ShopPhone = o.getShop().getPhone();
            name = o.getShop().getName();
            info = o.getShop().getInfo();


         }

         @Override
         public void onError(ShopInfoResponseObject o) {

            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_LONG).show();

         }
      });
   }

   private void dh() {
      final Dialog dialog1 = new Dialog(getActivity());
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


