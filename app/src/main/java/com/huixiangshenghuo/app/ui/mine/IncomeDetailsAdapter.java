package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huixiangshenghuo.app.City;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 * 收入明细 Bean 是暂时的
 */

public class IncomeDetailsAdapter extends AppAdapter<City> {
   public IncomeDetailsAdapter(List<City> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final City newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_income_details, null, false);
         viewHolder = new ViewHolder();
         viewHolder.tv_order = (TextView) converView.findViewById(R.id.tv_income_details_order);
         viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_income_details_time);
         viewHolder.tv_income = (TextView) converView.findViewById(R.id.tv_income_details_income);

         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      viewHolder.tv_order.setText(newsRows.getOrder());
      viewHolder.tv_time.setText(newsRows.getTime());

      viewHolder.tv_income.setText(newsRows.getIncome());

      return converView;
   }

   //持有者模式
   public class ViewHolder {
      private TextView tv_order;
      private TextView tv_time;
      private TextView tv_income;

   }


}
