package com.huixiangshenghuo.app.adapter.homepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.userinfo.RefereeShopListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 * 直推商家
 */

public class RefereeShopAdapter extends AppAdapter<RefereeShopListResponseParam> {
   private BitmapUtils bitmapUtils;

   public RefereeShopAdapter(List<RefereeShopListResponseParam> list, Context context, BitmapUtils bitmapUtils) {
      super(list, context);
      this.bitmapUtils = bitmapUtils;
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final RefereeShopListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_regular_members, null, false);
         viewHolder = new ViewHolder();

         viewHolder.iv_pic = (ImageView) converView.findViewById(R.id.iv_pic_regular_members);
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name_regular_members);
         viewHolder.tv_phone = (TextView) converView.findViewById(R.id.tv_phone_regular_members);
         viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_recommend_time_regular_members);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      bitmapUtils.display(viewHolder.iv_pic, newsRows.getImgUrl());

      viewHolder.tv_name.setText(newsRows.getName());
      viewHolder.tv_phone.setText("(" + newsRows.getPhone() + ")");

      viewHolder.tv_time.setText(newsRows.getJoinDate());

      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView iv_pic;
      TextView tv_name;
      TextView tv_phone;
      TextView tv_time;
   }

}
