package com.huixiangshenghuo.app.adapter.adaptershopcirrcle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.mine.CityParam;
import com.doumee.model.response.userinfo.AreaResponseParam;
import com.huixiangshenghuo.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class AreaExpandAdapter extends BaseExpandableListAdapter {


   private List<CityParam> citys;
   private Context context;

   public AreaExpandAdapter(Context context, List<CityParam> citys) {
      this.context = context;
      if (citys == null) {
         this.citys = new ArrayList<>();
      } else {
         this.citys = citys;
      }
   }

   @Override
   public int getGroupCount() {
      return citys.size();
   }

   @Override
   public int getChildrenCount(int groupPosition) {
      return citys.get(groupPosition).getLstArea().size();
   }

   @Override
   public CityParam getGroup(int groupPosition) {
      return citys.get(groupPosition);
   }

   @Override
   public AreaResponseParam getChild(int groupPosition, int childPosition) {
      return citys.get(groupPosition).getLstArea().get(childPosition);
   }

   @Override
   public long getGroupId(int groupPosition) {
      return groupPosition;
   }

   @Override
   public long getChildId(int groupPosition, int childPosition) {
      return childPosition;
   }

   @Override
   public boolean hasStableIds() {
      return true;
   }

   @Override
   public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
      if (convertView == null) {
         convertView = LayoutInflater.from(context).inflate(R.layout.item_expand_area, null);
      }
      TextView provinceTxt = (TextView) convertView.findViewById(R.id.iea_province_txt);
      ImageView img = (ImageView) convertView.findViewById(R.id.iea_img);
      provinceTxt.setText(getGroup(groupPosition).getProvinceName() + "/" + getGroup(groupPosition).getCityName());
      if (getGroup(groupPosition).isExpand()) {
         img.setImageResource(R.drawable.xl);
      } else {
         img.setImageResource(R.drawable.xl_b);
      }
      return convertView;
   }

   @Override
   public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
      if (convertView == null) {
         convertView = LayoutInflater.from(context).inflate(R.layout.item_area, null);
      }
      TextView areaTxt = (TextView) convertView.findViewById(R.id.ia_area_txt);
      areaTxt.setText(getChild(groupPosition, childPosition).getAreaName());
      return convertView;
   }

   @Override
   public boolean isChildSelectable(int groupPosition, int childPosition) {
      return true;
   }
}
