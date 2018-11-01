package com.huixiangshenghuo.app.adapter.homepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.doumee.model.response.articles.ArticlesListResponseParam;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 * 首页 二级页面 新手指南 adapter
 */

public class BeginnerGuideAdapter extends AppAdapter<ArticlesListResponseParam> {

   public BeginnerGuideAdapter(List<ArticlesListResponseParam> list, Context context) {
      super(list, context);
   }

   @Override
   public View createItemView(int position, View converView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      //可填
      final ArticlesListResponseParam newsRows = list.get(position);
      if (converView == null) {
         //找控件
         converView = LayoutInflater.from(context).inflate(R.layout.adapter_beginner_guide, null, false);
         viewHolder = new ViewHolder();
         viewHolder.tv_content = (TextView) converView.findViewById(R.id.tv_content_beginner_guide);

         converView.setTag(viewHolder);
      } else {
         viewHolder = (ViewHolder) converView.getTag();
      }


      viewHolder.tv_content.setText(newsRows.getTitle());


      return converView;
   }

   //持有者模式
   public class ViewHolder {
      TextView tv_content;
   }


}
