package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.view.Dialog;
import com.doumee.model.request.product.ProductDelRequestObject;
import com.doumee.model.request.product.ProductDelRequestParam;
import com.doumee.model.request.product.ProductEditRequestObject;
import com.doumee.model.request.product.ProductEditRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.collects.CollectsListResponseParam;
import com.doumee.model.response.product.ProductListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */

public class SalesAdapter extends AppAdapter<ProductListResponseParam> {
   private HttpTool httpTool;
   private BitmapUtils bitmapUtils;

   public SalesAdapter(List<ProductListResponseParam> list, Context context, BitmapUtils bitmapUtils) {
      super(list, context);
      this.bitmapUtils = bitmapUtils;
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final ProductListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_sales, null, false);
         viewHolder = new ViewHolder();
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name);
         viewHolder.tv_kucun = (TextView) converView.findViewById(R.id.tv_kucun);
         viewHolder.tv_xl = (TextView) converView.findViewById(R.id.tv_xl);
         viewHolder.tv_jiage = (TextView) converView.findViewById(R.id.tv_jiage);
         viewHolder.iv_Icon = (ImageView) converView.findViewById(R.id.iv_resIcon_sell);
         viewHolder.rl_compile_sell = (RelativeLayout) converView.findViewById(R.id.rl_compile_sell);
         viewHolder.rl_sc_sell = (RelativeLayout) converView.findViewById(R.id.rl_sc_sell);
         viewHolder.rl_withdraw_sell = (RelativeLayout) converView.findViewById(R.id.rl_withdraw_sell);


         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      viewHolder.tv_name.setText(newsRows.getProName());
      viewHolder.tv_kucun.setText("库存" + newsRows.getStockNum() + "");
      bitmapUtils.display(viewHolder.iv_Icon, newsRows.getImgurl());
      viewHolder.tv_xl.setText("月销售" + newsRows.getSaleNum() + "");
      viewHolder.tv_jiage.setText("￥" + newsRows.getPrice() + "");
      viewHolder.rl_compile_sell.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent_jifen = new Intent();
            intent_jifen.putExtra(EditShangpinManageActivity.IMGURL, newsRows.getImgurl());
            intent_jifen.putExtra(EditShangpinManageActivity.NAME, newsRows.getProName());
            intent_jifen.putExtra(EditShangpinManageActivity.PRICE, newsRows.getPrice() + "");
            intent_jifen.putExtra(EditShangpinManageActivity.STOCKNUM, newsRows.getStockNum() + "");
            intent_jifen.putExtra(EditShangpinManageActivity.PROID, newsRows.getProId());
            /*intent_jifen.putExtra(EditShangpinManageActivity.CATEGORYID, newsRows.getCategoryId());
            intent_jifen.putExtra(EditShangpinManageActivity.CATEGROYNAME, newsRows.getCategroyName());*/
            intent_jifen.putExtra(EditShangpinManageActivity.CATEGORYID, newsRows.getProCateId());
            intent_jifen.putExtra(EditShangpinManageActivity.CATEGROYNAME, newsRows.getProCateName());
            intent_jifen.putExtra(EditShangpinManageActivity.STATE, "0");
            intent_jifen.setClass(context, EditShangpinManageActivity.class);
            context.startActivity(intent_jifen);
    /*        final Dialog dialog1 = new Dialog(context);
            dialog1.setTitle("温馨提示");
            dialog1.setMessage("修改该商品");
            dialog1.setConfirmText("确定");
            dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  Intent intent_jifen = new Intent();
                  intent_jifen.putExtra(EditShangpinManageActivity.IMGURL, newsRows.getImgurl());
                  intent_jifen.putExtra(EditShangpinManageActivity.NAME, newsRows.getProName());
                  intent_jifen.putExtra(EditShangpinManageActivity.PRICE, newsRows.getPrice());
                  intent_jifen.putExtra(EditShangpinManageActivity.STOCKNUM, newsRows.getProName());
                  intent_jifen.setClass(context, EditShangpinManageActivity.class);
                  context.startActivity(intent_jifen);


                  dialog1.dismiss();
               }
            });
            dialog1.show();*/
         }
      });
      viewHolder.rl_sc_sell.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            final Dialog dialog1 = new Dialog(context);
            dialog1.setTitle("温馨提示");
            dialog1.setMessage("删除该商品");
            dialog1.setConfirmText("确定");
            dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                  httpTool = HttpTool.newInstance((Activity) context);

                  ProductDelRequestObject productDelRequestObject = new ProductDelRequestObject();
                  ProductDelRequestParam productDelRequestParam = new ProductDelRequestParam();
                  productDelRequestParam.setProductId(newsRows.getProId());
                  productDelRequestObject.setParam(productDelRequestParam);
                  httpTool.post(productDelRequestObject, URLConfig.SHOP_DEL, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                     @Override
                     public void onSuccess(ResponseBaseObject o) {
                        list.remove(newsRows);
                        notifyDataSetChanged();
                        Toast.makeText(context, "已删除该商品", Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void onError(ResponseBaseObject o) {

                     }
                  });
                  dialog1.dismiss();
               }
            });
            dialog1.show();
         }
      });
      viewHolder.rl_withdraw_sell.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            final Dialog dialog1 = new Dialog(context);
            dialog1.setTitle("温馨提示");
            dialog1.setMessage("下架该商品");
            dialog1.setConfirmText("确定");
            dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                  httpTool = HttpTool.newInstance((Activity) context);

                  ProductEditRequestObject productEditRequestObject = new ProductEditRequestObject();
                  ProductEditRequestParam productEditRequestParam = new ProductEditRequestParam();
                  productEditRequestParam.setProductId(newsRows.getProId());
                  productEditRequestParam.setStatus("1"); //status	上架或下架 0 上架 1下架
                  productEditRequestParam.setSortNum(newsRows.getStockNum());
                  productEditRequestObject.setParam(productEditRequestParam);
                  httpTool.post(productEditRequestObject, URLConfig.SHOP_EDIT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                     @Override
                     public void onSuccess(ResponseBaseObject o) {
                        list.remove(newsRows);
                        notifyDataSetChanged();
                        Toast.makeText(context, "已下架该商品", Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void onError(ResponseBaseObject o) {

                     }
                  });
                  dialog1.dismiss();
               }
            });
            dialog1.show();
         }
      });


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      private TextView tv_name;
      private TextView tv_kucun;
      private TextView tv_xl;
      private TextView tv_jiage;
      private ImageView iv_Icon;

      private RelativeLayout rl_compile_sell;
      private RelativeLayout rl_sc_sell;
      private RelativeLayout rl_withdraw_sell;

   }

   public void remove(CollectsListResponseParam newsRows) {
      list.remove(newsRows);
      notifyDataSetChanged();

   }
}
