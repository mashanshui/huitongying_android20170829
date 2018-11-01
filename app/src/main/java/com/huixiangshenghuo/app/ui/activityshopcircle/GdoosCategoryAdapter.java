package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doumee.model.response.shopCate.CateListResponseParam;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhaoyunhai on 16/4/7.
 */
public class GdoosCategoryAdapter extends BaseAdapter {
   private final Context mContext;
   private List<CateListResponseParam> mList = new ArrayList<>();
   private OnFirstChildLoadListener mFirstChild;

   public GdoosCategoryAdapter(Context context) {
      this.mContext = context;
   }

   @Override
   public int getCount() {
      return mList.size();
   }

   @Override
   public CateListResponseParam getItem(int position) {
      return mList.get(position);
   }

   @Override
   public long getItemId(int position) {
      return position;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      Holder holder;
      if (convertView == null) {
         holder = new Holder();
         convertView = View.inflate(mContext, R.layout.item_category_gdoos, null);
         holder.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName_categoryGdoos);

         convertView.setTag(holder);
      } else {
         holder = (Holder) convertView.getTag();
      }

      holder.tv_categoryName.setText(mList.get(position).getName());

      if (position == 0) {
         if (mFirstChild != null) {
            mFirstChild.onLoad(convertView);
         }
         convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
      }
      return convertView;
   }

   public void setDate(List<CateListResponseParam> categoryList) {
      this.mList = categoryList;
      this.notifyDataSetChanged();

   }

   public void addDate() {
      this.notifyDataSetChanged();
   }

   public void setFirstChildListener(OnFirstChildLoadListener listener) {
      this.mFirstChild = listener;
   }

   public interface OnFirstChildLoadListener {
      void onLoad(View view);
   }

   private class Holder {
      public TextView tv_categoryName;
   }
}
