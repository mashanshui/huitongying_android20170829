package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.Dialog;
import com.doumee.model.request.collects.CollectsDelRequestObject;
import com.doumee.model.request.collects.CollectsDelRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.collects.CollectsListResponseParam;
import com.huixiangshenghuo.app.R;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class AdapterShopCollect extends AppAdapter<CollectsListResponseParam> {
   private BitmapUtils bitmapUtils;
   private HttpTool httpTool;

   public AdapterShopCollect(List<CollectsListResponseParam> list,
                             Context context, BitmapUtils bitmapUtils) {
      super(list, context);
      // TODO Auto-generated constructor stub
      this.bitmapUtils = bitmapUtils;
   }

   @Override
   public View createItemView(final int position, View converView, ViewGroup parent) {
      // TODO Auto-generated method stub
      ViewHolder viewHolder = null;
      //可填
      final CollectsListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_shop_collect, null, false);
         viewHolder = new ViewHolder();
         viewHolder.shopImage = (ImageView) converView.findViewById(R.id.shop_collect_image);
         viewHolder.nameView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_shopname);
         viewHolder.ratingBar = (RatingBar) converView.findViewById(R.id.shop_collect_rating_bar);
         viewHolder.saleNumView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_salecount);
         viewHolder.shopQSView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_qs);
         viewHolder.kdfeeView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_ps);
         viewHolder.addressSView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_add);
         viewHolder.disView = (TextView) converView.findViewById(R.id.tv_item_shop_collect_dis);
         viewHolder.ll_shop = (LinearLayout) converView.findViewById(R.id.ll_shop_collect);
         viewHolder.categoryName = (TextView) converView.findViewById(R.id.tv_shop_collect_lx);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }


      String imageUrl = newsRows.getImgurl();
      String name = newsRows.getObjName();
      int score = newsRows.getScore();
      int saleNum = newsRows.getMothSaleNum();

      String address = newsRows.getAddr();

      bitmapUtils.display(viewHolder.shopImage, newsRows.getImgurl());
      viewHolder.nameView.setText(name);
      viewHolder.ratingBar.setRating(score);
      viewHolder.saleNumView.setText("月销量：" + saleNum);
//      viewHolder.kdfeeView.setText("邮费："+ "¥"+kdfee);
      viewHolder.addressSView.setText(address);

      //   viewHolder.shopQSView.setText("包邮金额："+"¥"+aetTeachersRoows.getFreeFee());
      //    String cateName = newsRows.getCategoryName();
//      if (TextUtils.isEmpty(cateName)){
//         cateName = "其他";
//      }
//      viewHolder.categoryName.setText(cateName);
      /**
       * 点击图片弹出弹窗
       */
      viewHolder.disView.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            Dialog dialog1 = new Dialog(context);
            dialog1.setTitle("温馨提示");
            dialog1.setMessage("确定取消该收藏");
            dialog1.setConfirmText("确定");
            dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  httpTool = HttpTool.newInstance((Activity) context);
//                  list.remove(newsRows);
//                  notifyDataSetChanged();
                  CollectsDelRequestObject object = new CollectsDelRequestObject();
                  object.setParam(new CollectsDelRequestParam());
                  object.getParam().setCollectionId(newsRows.getObjId());

                  httpTool.post(object, URLConfig.DIS_COLLECT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                     @Override
                     public void onSuccess(ResponseBaseObject o) {
                        if (o.getErrorCode().equals("00000")) {
                           if (o.getErrorMsg().equals("success")) {
                              Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                              remove(newsRows);
                           }
                        }

                        //        lv_recommend.onRefreshComplete();
                     }

                     @Override
                     public void onError(ResponseBaseObject o) {

                        Toast.makeText(context, o.getErrorMsg(), Toast.LENGTH_SHORT).show();
                     }
                  });

               }
            });
            dialog1.show();


         }
      });


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView shopImage;
      TextView nameView;
      RatingBar ratingBar;
      TextView saleNumView;
      TextView shopQSView;
      TextView kdfeeView;
      TextView addressSView;
      TextView disView;
      LinearLayout ll_shop;
      TextView categoryName;
   }

   public void remove(CollectsListResponseParam newsRows) {
      list.remove(newsRows);
      notifyDataSetChanged();

   }

}
