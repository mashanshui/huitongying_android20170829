package com.huixiangshenghuo.app.adapter;

import android.content.Context;

import com.huixiangshenghuo.app.ui.mine.ProvinceParam;
import com.lljjcoder.citypickerview.widget.wheel.adapters.AbstractWheelTextAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class ProvinceWheelAdapter extends AbstractWheelTextAdapter {
   /**
    * Constructor
    *
    * @param context the current context
    * @param items   the items
    */
   // items
   private List<ProvinceParam> items;

   public ProvinceWheelAdapter(Context context, List<ProvinceParam> items) {
      super(context);
      //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
      this.items = items;
   }

   @Override
   public CharSequence getItemText(int index) {
      if (index >= 0 && index < items.size()) {
         return items.get(index).getName();
      }
      return null;
   }

   @Override
   public int getItemsCount() {
      return items.size();
   }
}
