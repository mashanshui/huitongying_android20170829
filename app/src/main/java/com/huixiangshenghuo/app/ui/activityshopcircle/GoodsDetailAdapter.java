package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author zhaoyunhai on 16/4/8.
 */
public class GoodsDetailAdapter extends BaseAdapter {
   @Override
   public int getCount() {
      return 0;
   }

   @Override
   public Object getItem(int position) {
      return null;
   }

   @Override
   public long getItemId(int position) {
      return 0;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      return null;
   }
//    private final Context mContext;
//    private List<ProductsListResponseCustomParam> mList = new ArrayList<>();
//
//
//    private OnGdoosChangeListener listener;
//
//    public GoodsDetailAdapter(Context context) {
//        this.mContext = context;
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public ProductsListResponseParam getItem(int position) {
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final Holder holder;
//        if (convertView == null) {
//            holder = new Holder();
//            convertView = View.inflate(mContext, R.layout.item_gdoos_deatil, null);
//            holder.iv_gdoosIcon = (ImageView) convertView.findViewById(R.id.iv_gdoosIcon_itemGdoosDeatil);
//            holder.tv_gdoosName = (TextView) convertView.findViewById(R.id.tv_gdoosName_itemGdoosDeatil);
//            holder.tv_sales = (TextView) convertView.findViewById(R.id.tv_sales_itemGdoosDeatil);
//            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price_itemGdoosDeatil);
//
//            holder.tv_discount = (TextView) convertView.findViewById(R.id.tv_discount_itemGdoosDeatil);
//            holder.iv_remove = (ImageView) convertView.findViewById(R.id.iv_remove_itemGdoosDeatil);
//            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count_itemGdoosDeatil);
//            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add_itemGdoosDeatil);
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//
//        final ProductsListResponseCustomParam productBean = mList.get(position);
//        ImageUtils.display(holder.iv_gdoosIcon, productBean.getImgUrl());
//        if (productBean.count <= 0) {
//            holder.iv_remove.setVisibility(View.INVISIBLE);
//        } else {
//            holder.iv_remove.setVisibility(View.VISIBLE);
//        }
//        holder.tv_gdoosName.setText(productBean.getGoodsName());
//        holder.tv_sales.setText(mContext.getResources().getString(R.string.text_sales_itemBusiness, productBean.getMonthOrderNum() + ""));
//        holder.tv_price.setText(mContext.getResources().getString(R.string.text_price_itemGdoosDeatil, productBean.getPrice() + ""));
//        if (productBean.getReturnBate() <= 0) {
//            holder.tv_discount.setVisibility(View.INVISIBLE);
//        } else {
//            holder.tv_discount.setVisibility(View.VISIBLE);
//            holder.tv_discount.setText(mContext.getResources().getString(R.string.text_discount_itemGdoosDeatil, (productBean.getReturnBate() * 100) + "%"));
//        }
//
//        holder.tv_count.setText(String.valueOf(productBean.getCount()));
//        holder.iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.tv_count.setText(++productBean.count + "");
//                if (listener != null) {
//                    listener.onChangeListener(productBean, productBean.count);
//                }
//                holder.iv_remove.setVisibility(View.VISIBLE);
//
//            }
//        });
//
//        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (productBean.count > 0) {
//                    if (productBean.count == 1) {
//                        holder.iv_remove.setVisibility(View.INVISIBLE);
//                    }
//                    holder.tv_count.setText(--productBean.count + "");
//                    if (listener != null) {
//                        listener.onChangeListener(productBean, productBean.count);
//                    }
//                }
//            }
//        });
//        holder.iv_gdoosIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ActivityCommodityParticulars.class);
//                intent.putExtra("goodsid", productBean.getGoodsId());
//                mContext.startActivity(intent);
//
//            }
//        });
//
//
//        return convertView;
//    }
//
//    public void setDate(List<ProductsListResponseCustomParam> proList) {
//        mList.clear();
//        mList.addAll(proList);
//        this.notifyDataSetChanged();
//    }
//
//    private class Holder {
//
//        public ImageView iv_gdoosIcon;
//        public TextView tv_gdoosName;
//        public TextView tv_sales;
//        public TextView tv_price;
//        public TextView tv_discount;
//        public ImageView iv_remove;
//        public TextView tv_count;
//        public ImageView iv_add;
//    }
//
//    public interface OnGdoosChangeListener {
//        void onChangeListener(ProductsListResponseCustomParam bean, int currentNum);
//    }
//
//    public void setListener(OnGdoosChangeListener listener) {
//        this.listener = listener;
//    }
//    public void setinto(int param){
//			for (int i = 0; i < mList.size(); i++) {
//				mList.get(i).setCount(0);
//			}
//
//    }
}
