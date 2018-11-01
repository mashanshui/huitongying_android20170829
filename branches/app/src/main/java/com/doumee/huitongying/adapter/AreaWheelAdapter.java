package com.huixiangshenghuo.app.adapter;

import android.content.Context;

import com.doumee.model.response.userinfo.AreaResponseParam;
import com.lljjcoder.citypickerview.widget.wheel.adapters.AbstractWheelTextAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class AreaWheelAdapter extends AbstractWheelTextAdapter {
   /**
    * Constructor
    *
    * @param context the current context
    * @param items   the items
    */
   // items
   private List<AreaResponseParam> items;

   public AreaWheelAdapter(Context context, List<AreaResponseParam> items) {
      super(context);
      //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
      this.items = items;
   }

   @Override
   public CharSequence getItemText(int index) {
      if (index >= 0 && index < items.size()) {
         return items.get(index).getAreaName();
      }
      return null;
   }

   @Override
   public int getItemsCount() {
      return items.size();
   }
}
