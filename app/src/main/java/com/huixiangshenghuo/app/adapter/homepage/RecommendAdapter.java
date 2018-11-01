package com.huixiangshenghuo.app.adapter.homepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.product.ProductListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 * 首页猜你喜欢
 */

public class RecommendAdapter extends AppAdapter<ProductListResponseParam> {
   private BitmapUtils bitmapUtils;

   public RecommendAdapter(List<ProductListResponseParam> list, Context context, BitmapUtils bitmapUtils) {
      super(list, context);
      this.bitmapUtils = bitmapUtils;
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      // TODO Auto-generated method stub
      ViewHolder viewHolder = null;
      //可填
      final ProductListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_recommend, null, false);
         viewHolder = new ViewHolder();
         viewHolder.iv_pic = (ImageView) converView.findViewById(R.id.iv_pic_recommend);
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name_recommend);
         viewHolder.tv_price = (TextView) converView.findViewById(R.id.tv_price_recommend);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      bitmapUtils.display(viewHolder.iv_pic, newsRows.getImgurl());
      viewHolder.tv_name.setText(newsRows.getProName());
      viewHolder.tv_price.setText(CustomConfig.RMB + " " + newsRows.getPrice());


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView iv_pic;
      TextView tv_name;
      TextView tv_price;
   }



}
