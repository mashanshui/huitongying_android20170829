package com.huixiangshenghuo.app.adapter.homemall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.shop.ShopListResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 * 商圈 分类 二级页面 商家分类 adapter
 */

public class ProductListNewAdapter extends AppAdapter<ShopListResponseParam> {

   private Bitmap defaultBitmap;

   public ProductListNewAdapter(List<ShopListResponseParam> list, Context context) {
      super(list, context);
      defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.business_default);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final ShopListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.item_shop_list, null, false);
         viewHolder = new ViewHolder();
         viewHolder.goodsImage = (ImageView) converView.findViewById(R.id.shop_image);
         viewHolder.goodsName = (TextView) converView.findViewById(R.id.tv_item_shop_shopname);
         viewHolder.ratingBar = (RatingBar) converView.findViewById(R.id.rating_bar);
         viewHolder.tv_lx = (TextView) converView.findViewById(R.id.tv_shop_lx);
         viewHolder.tv_add = (TextView) converView.findViewById(R.id.tv_item_shop_add);
         viewHolder.tv_dis = (TextView) converView.findViewById(R.id.tv_item_shop_dis);
         viewHolder.saleView = (TextView) converView.findViewById(R.id.tv_item_shop_salecount);


         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      String imagePath = newsRows.getImgurl();
      int score = newsRows.getScore();
      double dis = newsRows.getDistance() / 1000;
      String add = newsRows.getAddr();
      String categoryName = newsRows.getCategoryName();
      viewHolder.goodsImage.setImageBitmap(defaultBitmap);
      if (!TextUtils.isEmpty(imagePath)) {
         ImageLoader.getInstance().displayImage(imagePath, viewHolder.goodsImage);
      }
      viewHolder.goodsName.setText(newsRows.getName());
//      try {
//         viewHolder.ratingBar.setRating(score);
//      } catch (Exception e) {
//         e.printStackTrace();
//         viewHolder.ratingBar.setRating(0);
//      }
      viewHolder.ratingBar.setRating(score);
      viewHolder.tv_dis.setText(dis + "km");
      viewHolder.tv_add.setText(add);
      viewHolder.tv_lx.setText(categoryName);
      viewHolder.saleView.setText("月销量：" + newsRows.getSaleNum());

      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView goodsImage;
      TextView goodsName;
      RatingBar ratingBar;
      TextView tv_lx;
      TextView tv_add;
      TextView tv_dis;
      TextView saleView;
   }

}
