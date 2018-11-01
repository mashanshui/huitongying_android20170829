package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.adapter.AppAdapter;
import com.huixiangshenghuo.app.R;

import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */

public class ListViewSlideAdapter extends AppAdapter<String> {

   private List<String> bulbList;
   private Context context;
//   private OnClickListenerEditOrDelete onClickListenerEditOrDelete;

   public ListViewSlideAdapter(List<String> list, Context context) {
      super(list, context);
   }


   @Override
   public View createItemView(final int position, View convertView, ViewGroup parent) {
      final String bulb = bulbList.get(position);
      View view;
      ViewHolder viewHolder;
      if (null == convertView) {
         view = View.inflate(context, R.layout.item_slide_delete_edit, null);
         viewHolder = new ViewHolder();
         viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
         viewHolder.img = (ImageView) view.findViewById(R.id.imgLamp);
         viewHolder.tvDelete = (TextView) view.findViewById(R.id.delete);
         viewHolder.tvEdit = (TextView) view.findViewById(R.id.tvEdit);
         view.setTag(viewHolder);//store up viewHolder
      } else {
         view = convertView;
         viewHolder = (ViewHolder) view.getTag();
      }

      viewHolder.img.setImageResource(R.mipmap.ic_launcher);
      viewHolder.tvName.setText(bulb);
 /*     viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (onClickListenerEditOrDelete!=null){
               onClickListenerEditOrDelete.OnClickListenerDelete(position);
            }
         }
      });
      viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (onClickListenerEditOrDelete!=null){
               onClickListenerEditOrDelete.OnClickListenerEdit(position);
            }
         }
      });*/
      return view;
   }


   private class ViewHolder {
      TextView tvName, tvEdit, tvDelete;
      ImageView img;
   }

 /*  public interface OnClickListenerEditOrDelete{
      void OnClickListenerEdit(int position);
      void OnClickListenerDelete(int position);
   }

   public void setOnClickListenerEditOrDelete(OnClickListenerEditOrDelete onClickListenerEditOrDelete1){
      this.onClickListenerEditOrDelete=onClickListenerEditOrDelete1;
   }*/
}
