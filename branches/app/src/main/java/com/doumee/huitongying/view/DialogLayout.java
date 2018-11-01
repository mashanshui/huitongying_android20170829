/**
 *
 */
package com.huixiangshenghuo.app.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.huixiangshenghuo.app.R;


/**
 * 使用本Layout作为Dialog的容器，才可以灵活定义适配规则
 */
public final class DialogLayout extends LinearLayout {

   /**
    * 最小宽度
    */
   private int minWidth = 0;

   @SuppressLint("NewApi")
   public DialogLayout(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);

      // 获取自定义属性和默认值
      TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DialogLayout);
      minWidth = mTypedArray.getDimensionPixelOffset(R.styleable.DialogLayout_minDialogWidth, 0);
      mTypedArray.recycle();
   }

   public DialogLayout(Context context, AttributeSet attrs) {
      super(context, attrs);

      // 获取自定义属性和默认值
      TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DialogLayout);
      minWidth = mTypedArray.getDimensionPixelOffset(R.styleable.DialogLayout_minDialogWidth, 0);
      mTypedArray.recycle();
   }

   public DialogLayout(Context context) {
      super(context);
   }

   /* (non-Javadoc)
    * @see android.view.View#onConfigurationChanged(android.content.res.Configuration)
    */
   @Override
   protected void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
   }

   /* (non-Javadoc)
    * @see android.com.doumee.xiaofeibao.widget.LinearLayout#onMeasure(int, int)
    */
   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

      int maxWidth = MeasureSpec.getSize(widthMeasureSpec);

      int childMaxWidth = 0;
      for (int i = 0; i < getChildCount(); i++) {
         View childView = getChildAt(i);
         childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
         int childWidth = childView.getMeasuredWidth();
         childMaxWidth = Math.max(childMaxWidth, childWidth);
      }

      int fitWidth = 0;
      if (childMaxWidth > maxWidth) {
         fitWidth = maxWidth;
      } else if (childMaxWidth < minWidth) {
         fitWidth = minWidth;
      } else {
         fitWidth = childMaxWidth;
      }

      super.onMeasure(//
            MeasureSpec.makeMeasureSpec(fitWidth, MeasureSpec.getMode(widthMeasureSpec)), //
            heightMeasureSpec//
      );

   }

}