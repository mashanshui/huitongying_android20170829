package com.huixiangshenghuo.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

//adapter 基础类
public abstract class AppAdapter<T> extends BaseAdapter {
   public Context context;
   public List<T> list;

   //填充器
   public LayoutInflater inflater;

   //有参构造
   public AppAdapter(List<T> list, Context context) {
      this.context = context;
      this.list = list;
      this.inflater = LayoutInflater.from(context);

   }


   @Override
   public int getCount() {
      // TODO Auto-generated method stub
      return list != null && !list.isEmpty() ? list.size() : 0;
   }

   @Override
   public Object getItem(int position) {
      // TODO Auto-generated method stub
      return list.get(position);
   }

   @Override
   public long getItemId(int position) {
      // TODO Auto-generated method stub
      return position;
   }

   @Override
   public View getView(int position, View converView, ViewGroup parent) {

      return createItemView(position, converView, parent);
   }

   public abstract View createItemView(int position, View converView, ViewGroup parent);

}
