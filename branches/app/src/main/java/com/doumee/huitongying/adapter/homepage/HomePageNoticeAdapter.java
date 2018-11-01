package com.huixiangshenghuo.app.adapter.homepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.notices.NoticesListResponseParam;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 * 首页公告通知Adapter
 */

public class HomePageNoticeAdapter extends AppAdapter<NoticesListResponseParam> {
   public HomePageNoticeAdapter(List<NoticesListResponseParam> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final NoticesListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_home_page_notice, null, false);
         viewHolder = new ViewHolder();


         viewHolder.tv_content = (TextView) converView.findViewById(R.id.tv_content_home_pager_notice);
         viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_time_home_pager_notice);
         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }

      viewHolder.tv_content.setText(newsRows.getTitle());

      viewHolder.tv_time.setText(newsRows.getCreateDate());


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      TextView tv_content;
      TextView tv_time;

   }

}
