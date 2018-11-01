package com.huixiangshenghuo.app.adapter.homemine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.goodsorder.GoodsOrderListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;


/**
 * Created by Administrator on 2017/6/5.
 * 商户中心 订单明细 adapter
 */

public class OrderDetailsAdapter extends AppAdapter<GoodsOrderListResponseParam> {
   private BitmapUtils bitmapUtils;

   public OrderDetailsAdapter(List<GoodsOrderListResponseParam> list, Context context, BitmapUtils bitmapUtils) {
      super(list, context);
      this.bitmapUtils = bitmapUtils;
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final GoodsOrderListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_order_details, null, false);
         viewHolder = new ViewHolder();
         viewHolder.iv_resIcon_sell = (ImageView) converView.findViewById(R.id.iv_resIcon_sell);
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name_order_details);

         viewHolder.tv_num = (TextView) converView.findViewById(R.id.tv_num_order_details);
         viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_time_order_details);

         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      bitmapUtils.display(viewHolder.iv_resIcon_sell, newsRows.getImgurl());
      viewHolder.tv_name.setText("订货编号:" + newsRows.getOrderId());

      viewHolder.tv_num.setText(CustomConfig.RMB + newsRows.getTotalPrice());

      viewHolder.tv_time.setText(newsRows.getCreateDate());


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView iv_resIcon_sell;
      TextView tv_name;
      TextView tv_num;
      TextView tv_time;

   }

}
