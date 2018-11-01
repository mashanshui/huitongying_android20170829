package com.huixiangshenghuo.app.adapter.homemine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.doumee.model.response.integralrecord.IntegralrecordListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 * 公益积金 adapter
 */

public class PublicWelfareFundAdapter extends AppAdapter<IntegralrecordListResponseParam> {


   public PublicWelfareFundAdapter(List<IntegralrecordListResponseParam> list, Context context) {
      super(list, context);
      // TODO Auto-generated constructor stub

   }

   @Override
   public View createItemView(final int position, View converView, ViewGroup parent) {
      // TODO Auto-generated method stub
      ViewHolder viewHolder = null;
      //可填
      final IntegralrecordListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_public_welfare_fund, null, false);
         viewHolder = new ViewHolder();

         viewHolder.tv_integralnum = (TextView) converView.findViewById(R.id.tv_integralnum_public_welfare_fund);
         viewHolder.tv_name = (TextView) converView.findViewById(R.id.tv_name_public_welfare_fund);
         viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_time_public_welfare_fund);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      viewHolder.tv_name.setText(userInfo.getLoginName());
      viewHolder.tv_integralnum.setText("捐赠了" + newsRows.getIntegralNum());
      viewHolder.tv_time.setText(newsRows.getCreateDate());


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      TextView tv_name;
      TextView tv_integralnum;
      TextView tv_time;
   }

}
