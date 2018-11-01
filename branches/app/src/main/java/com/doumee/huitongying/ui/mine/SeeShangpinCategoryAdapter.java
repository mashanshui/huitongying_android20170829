package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doumee.model.response.shopCate.CateListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */

public class SeeShangpinCategoryAdapter extends AppAdapter<CateListResponseParam> {
   public SeeShangpinCategoryAdapter(List<CateListResponseParam> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final CateListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_see_shangpin_category, null, false);
         viewHolder = new ViewHolder();
         viewHolder.tv_see = (TextView) converView.findViewById(R.id.tv_see_shangpin_category);
         viewHolder.tv_num = (TextView) converView.findViewById(R.id.tv_see_shangpin_num);
         viewHolder.tv_px = (TextView) converView.findViewById(R.id.tv_see_shangpin_px);

         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      viewHolder.tv_see.setText(newsRows.getName());
      viewHolder.tv_num.setText("商品分类" + newsRows.getProNum() + "件");
      viewHolder.tv_px.setText("排序码：" + newsRows.getSortNum());

      return converView;
   }

   //持有者模式
   public class ViewHolder {
      private TextView tv_see;
      private TextView tv_num;
      private TextView tv_px;

   }
}
