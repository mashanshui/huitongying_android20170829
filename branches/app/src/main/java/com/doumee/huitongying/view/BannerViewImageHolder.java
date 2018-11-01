package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.activity.homeshoprefresh.ShopDetailsNewActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.AdInfoActivity;
import com.doumee.model.response.ad.AdListResponseParam;


/**
 * Created by Administrator on 2017/4/1 0001.
 */
public class BannerViewImageHolder implements Holder<AdListResponseParam> {
   private ImageView imageView;

   @Override
   public View createView(Context context) {
      //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
      imageView = new ImageView(context);
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);
      return imageView;
   }

   @Override
   public void UpdateUI(final Context context, int position, final AdListResponseParam data) {
      Glide.with(context).load(data.getImgurl()).placeholder(R.mipmap.business_default).into(imageView);
      imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            if (!data.getType().equals("2")) {
////                    Intent intent = new Intent(context, WebActivity.class);
////                    Bundle bundle = new Bundle();
////                    bundle.putString("title", data.getTitle());
////                    bundle.putString("type", data.getType());
////                    bundle.putString("content", data.getContent());
////                    intent.putExtras(bundle);
////                    context.startActivity(intent);
//
//            }
            if (!data.getType().equals("2")) {//类型（0内容 1外链  2商家广告），默认0

               AdInfoActivity.startAdInfoActivity(context, data.getType(), data.getContent(), data.getLink(), data.getTitle());//0内容1外链接
            } else {
               ShopDetailsNewActivity.startShopDetailsActivity(context, data.getRecPrd());
            }


         }
      });
   }
}
