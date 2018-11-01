package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doumee.model.response.shopCate.CateListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */

public class ShangpinCategoryAdapter extends AppAdapter<CateListResponseParam> {
   public ShangpinCategoryAdapter(List<CateListResponseParam> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final CateListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_shangpin_category, null, false);
         viewHolder = new ViewHolder();
         viewHolder.tv_shp_fl = (TextView) converView.findViewById(R.id.tv_shp_fl);
         viewHolder.tv_shanp_num = (TextView) converView.findViewById(R.id.tv_shanp_num);
         viewHolder.tv_shp_px = (TextView) converView.findViewById(R.id.tv_shp_px);
         viewHolder.rl_shangpi_category = (RelativeLayout) converView.findViewById(R.id.rl_shangpi_category);


         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      viewHolder.tv_shp_fl.setText(newsRows.getName());
      viewHolder.tv_shanp_num.setText("共" + newsRows.getProNum() + "件商品");
      viewHolder.tv_shp_px.setText("排序码：" + newsRows.getSortNum());

      //list.remove(newsRows);
      // notifyDataSetChanged();

//      viewHolder.rl_shangpi_category.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//      //      Toast.makeText(context, "查看分类", Toast.LENGTH_LONG).show();
//         }
//      });


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      private TextView tv_shp_fl;
      private TextView tv_shanp_num;
      private TextView tv_shp_px;


      private RelativeLayout rl_shangpi_category;


   }

   public void remove(CateListResponseParam newsRows) {
      list.remove(newsRows);
      notifyDataSetChanged();

   }

}
