package com.huixiangshenghuo.app.adapter.adaptershopcirrcle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doumee.model.response.shop.ShopListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.activity.homeshoprefresh.ShopDetailsNewActivity;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/15.
 * 附近商家 适配器  暂时改为 跳转到带有是商品的 页面
 */
public class NearbyShopAdapter extends AppAdapter<ShopListResponseParam> {
   private BitmapUtils bitmapUtils;
   private Bitmap defaultImage;

   public NearbyShopAdapter(List<ShopListResponseParam> list, Context context) {
      super(list, context);
      defaultImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.business_default);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder;
      //可填
      final ShopListResponseParam aetTeachersRoows = list.get(position);
      if (converView == null) {
         converView = LayoutInflater.from(context).inflate(R.layout.item_shop_list, null, false);
         //找控件
         viewHolder = new ViewHolder();

         viewHolder.shopImage = (ImageView) converView.findViewById(R.id.shop_image);
         viewHolder.nameView = (TextView) converView.findViewById(R.id.tv_item_shop_shopname);
         viewHolder.ratingBar = (RatingBar) converView.findViewById(R.id.rating_bar);
         viewHolder.saleNumView = (TextView) converView.findViewById(R.id.tv_item_shop_salecount);
         viewHolder.shopQSView = (TextView) converView.findViewById(R.id.tv_item_shop_qs);
         viewHolder.kdfeeView = (TextView) converView.findViewById(R.id.tv_item_shop_ps);
         viewHolder.addressSView = (TextView) converView.findViewById(R.id.tv_item_shop_add);
         viewHolder.disView = (TextView) converView.findViewById(R.id.tv_item_shop_dis);
         viewHolder.ll_shop = (LinearLayout) converView.findViewById(R.id.ll_shop);
         viewHolder.categoryName = (TextView) converView.findViewById(R.id.tv_shop_lx);

         //关联
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }
/*      viewHolder.tv_name.setText(aetTeachersRoows.getName());
      bitmapUtils.display(viewHolder.iv_img, aetTeachersRoows.getImgUrl());*/

      viewHolder.shopImage.setImageBitmap(defaultImage);
      String imageUrl = aetTeachersRoows.getImgurl();
      String name = aetTeachersRoows.getName();
      int score = aetTeachersRoows.getScore();
      int saleNum = aetTeachersRoows.getSaleNum();
//      double kdfee = aetTeachersRoows.getKdfee();
      String address = aetTeachersRoows.getAddr();
      double dis = aetTeachersRoows.getDistance() / 1000;
      final String shopId = aetTeachersRoows.getShopId();
//      final String collect = aetTeachersRoows.getIsCollected();
      if (!TextUtils.isEmpty(imageUrl)) {
         ImageLoader.getInstance().displayImage(imageUrl, viewHolder.shopImage);
      } else {
         viewHolder.shopImage.setImageBitmap(defaultImage);
      }
      viewHolder.nameView.setText(name);
      viewHolder.ratingBar.setRating(score);
      viewHolder.saleNumView.setText("月销量：" + saleNum);
//      viewHolder.kdfeeView.setText("邮费："+ "¥"+kdfee);
      viewHolder.addressSView.setText(address);
      viewHolder.disView.setText(dis + "km");
      //   viewHolder.shopQSView.setText("包邮金额："+"¥"+aetTeachersRoows.getFreeFee());
      String cateName = aetTeachersRoows.getCategoryName();
      if (TextUtils.isEmpty(cateName)){
         cateName = "其他";
      }
      viewHolder.categoryName.setText(cateName);
      viewHolder.ll_shop.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            if (aetTeachersRoows.getIsOnline().equals("0")) {//0  线下  1 线上
//               ShopDetailsActivity.startShopDetailsActivity(context, aetTeachersRoows.getShopId());
//            } else {
//               ShopDetailsOnlineActivity.startShopActivity(context, aetTeachersRoows.getShopId(), "");
//
//            }
//            ShopDetailsActivity.startShopDetailsActivity(context, aetTeachersRoows.getShopId());
            ShopDetailsNewActivity.startShopDetailsActivity(context, aetTeachersRoows.getShopId());


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
}
