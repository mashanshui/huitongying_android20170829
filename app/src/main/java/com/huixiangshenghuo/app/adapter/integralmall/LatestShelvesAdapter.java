package com.huixiangshenghuo.app.adapter.integralmall;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.doumee.model.request.shopcart.ShopcartManageRequestObject;
import com.doumee.model.request.shopcart.ShopcartManageRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 * 商城 最新上架 adapter
 */

public class LatestShelvesAdapter extends AppAdapter<ProductListResponseParam> {
   private BitmapUtils bitmapUtils;
   private HttpTool httpTool;

   public LatestShelvesAdapter(List<ProductListResponseParam> list, Context context, BitmapUtils bitmapUtils) {
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
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_latest_shelves_integral, null, false);
         viewHolder = new ViewHolder();
         viewHolder.iv_pic = (ImageView) converView.findViewById(R.id.iv_pic_latest_shelves);
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name_latest_shelves);
         viewHolder.tv_price = (TextView) converView.findViewById(R.id.tv_price_latest_shelves);
         viewHolder.iv_gwc = (ImageView) converView.findViewById(R.id.iv_gwc_latest_shelves);
         viewHolder.tv_saleNum = (TextView) converView.findViewById(R.id.tv_sale_num);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      bitmapUtils.display(viewHolder.iv_pic, newsRows.getImgurl());
      viewHolder.tv_name.setText(newsRows.getProName());
      viewHolder.tv_price.setText( "积分: " + newsRows.getIntegral());
      viewHolder.tv_saleNum.setText("销量:" + newsRows.getSaleNum());

      httpTool = HttpTool.newInstance((Activity) context);

      viewHolder.iv_gwc.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            ShopcartManageRequestParam shopcartManageRequestParam = new ShopcartManageRequestParam();
            shopcartManageRequestParam.setProId(newsRows.getProId());
            shopcartManageRequestParam.setNum(1);
            shopcartManageRequestParam.setType("0");//0设置数量 1 删除
            ShopcartManageRequestObject shopcartManageRequestObject = new ShopcartManageRequestObject();
            shopcartManageRequestObject.setParam(shopcartManageRequestParam);
            httpTool.post(shopcartManageRequestObject, URLConfig.SHOP_CART_ADD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
               @Override
               public void onSuccess(ResponseBaseObject o) {
                  Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show();

               }

               @Override
               public void onError(ResponseBaseObject responseBaseObject) {
                  Toast.makeText(context, responseBaseObject.getErrorMsg(), Toast.LENGTH_SHORT).show();
               }


            });
         }
      });


      return converView;
   }


   //持有者模式
   public class ViewHolder {
      ImageView iv_pic;
      TextView tv_name;
      TextView tv_price;
      ImageView iv_gwc;
      TextView tv_saleNum;
   }


}
