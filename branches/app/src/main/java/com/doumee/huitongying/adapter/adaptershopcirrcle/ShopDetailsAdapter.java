package com.huixiangshenghuo.app.adapter.adaptershopcirrcle;

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

import com.doumee.model.response.comment.ShopCommentListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 * 商家详情 评论
 */
public class ShopDetailsAdapter extends AppAdapter<ShopCommentListResponseParam> {

   private Bitmap defaultBitmap;

   public ShopDetailsAdapter(List<ShopCommentListResponseParam> list, Context context) {
      super(list, context);
      defaultBitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.header_img_bg);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder;
      //可填
      final ShopCommentListResponseParam aetTeachersRoows = list.get(position);
      if (converView == null) {
         converView = LayoutInflater.from(context).inflate(R.layout.fragment_ping_item, null, false);
         //找控件
         viewHolder = new ViewHolder();


         viewHolder.headView = (ImageView) converView.findViewById(R.id.iv_head);
         viewHolder.nameView = (TextView) converView.findViewById(R.id.tv_item_service_cellphone);
         viewHolder.ratingBar = (RatingBar) converView.findViewById(R.id.rb_service_item_ping);
         viewHolder.timeView = (TextView) converView.findViewById(R.id.tv_item_service_datetime);
         viewHolder.contentView = (TextView) converView.findViewById(R.id.tv_service_item_ping_comment);

         //关联
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }
      viewHolder.headView.setImageBitmap(defaultBitmap);
      String imagePath = aetTeachersRoows.getImgurl();
      if (!TextUtils.isEmpty(imagePath)) {
         ImageLoader.getInstance().displayImage(imagePath, viewHolder.headView);
      }
      viewHolder.nameView.setText(aetTeachersRoows.getMemberName());
      viewHolder.ratingBar.setRating(Float.parseFloat(aetTeachersRoows.getScore()));
      viewHolder.timeView.setText(aetTeachersRoows.getCreateDate());
      viewHolder.contentView.setText(aetTeachersRoows.getContent());

      return converView;
   }

   //持有者模式
   public class ViewHolder {
      ImageView headView;
      TextView nameView;
      RatingBar ratingBar;
      TextView timeView;
      TextView contentView;

   }


}
