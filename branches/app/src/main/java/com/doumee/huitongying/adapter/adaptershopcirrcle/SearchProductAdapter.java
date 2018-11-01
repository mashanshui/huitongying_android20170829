package com.huixiangshenghuo.app.adapter.adaptershopcirrcle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doumee.model.response.product.ProductListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */
public class SearchProductAdapter extends AppAdapter<ProductListResponseParam> {
   private Bitmap defaultBitmap;

   public SearchProductAdapter(List<ProductListResponseParam> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder;
      //可填
      final ProductListResponseParam aetTeachersRoows = list.get(position);
      if (converView == null) {
         converView = LayoutInflater.from(context).inflate(R.layout.item_product_list, null, false);
         //找控件
         viewHolder = new ViewHolder();


         viewHolder.goodsImage = (ImageView) converView.findViewById(R.id.iv_product_info);
         viewHolder.goodsName = (TextView) converView.findViewById(R.id.tv_product_title);
         viewHolder.goodsDes = (TextView) converView.findViewById(R.id.tv_product_description);
         viewHolder.goodsPrice = (TextView) converView.findViewById(R.id.tv_product_price);
         viewHolder.saleView = (TextView) converView.findViewById(R.id.tv_product_sall_count);

         //关联
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.business_default);
      String imagePath = aetTeachersRoows.getImgurl();

      viewHolder.goodsImage.setImageBitmap(defaultBitmap);
      if (!TextUtils.isEmpty(imagePath)) {
         ImageLoader.getInstance().displayImage(imagePath, viewHolder.goodsImage);
      }
      viewHolder.goodsName.setText(aetTeachersRoows.getProName());
      viewHolder.goodsDes.setVisibility(View.GONE);
      viewHolder.goodsPrice.setText(CustomConfig.RMB + aetTeachersRoows.getPrice());
      viewHolder.saleView.setText("已售" + aetTeachersRoows.getSaleNum());

      return converView;
   }

   //持有者模式
   public class ViewHolder {

      ImageView goodsImage;
      TextView goodsName;
      TextView goodsDes;
      TextView goodsPrice;
      TextView saleView;

   }
}
