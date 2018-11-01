package com.huixiangshenghuo.app.adapter;

import android.content.Context;

import com.huixiangshenghuo.app.ui.mine.CityParam;
import com.lljjcoder.citypickerview.widget.wheel.adapters.AbstractWheelTextAdapter;

import java.util.List;


/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class CityWheelAdapter extends AbstractWheelTextAdapter {
   /**
    * Constructor
    *
    * @param context the current context
    * @param items   the items
    */
   // items
   private List<CityParam> items;

   public CityWheelAdapter(Context context, List<CityParam> items) {
      super(context);
      //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
      this.items = items;
   }

   @Override
   public CharSequence getItemText(int index) {
      if (index >= 0 && index < items.size()) {
         return items.get(index).getCityName();
      }
      return null;
   }

   @Override
   public int getItemsCount() {
      return items.size();
   }
}
