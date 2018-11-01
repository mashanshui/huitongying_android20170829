package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @author zhaoyunhai on 2016/5/26 0026.
 */
public class ListenerScrollView extends ScrollView {
   public ListenerScrollView(Context context) {
      super(context);
   }

   public ListenerScrollView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public ListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   protected void onScrollChanged(int l, int t, int oldl, int oldt) {
      super.onScrollChanged(l, t, oldl, oldt);
      if (listener != null) {
         listener.onScroll(l, t, oldl, oldt);
      }
   }

   private OnScrollChangeListener listener;

   public void setOnScrollChangeListener(OnScrollChangeListener listener) {
      this.listener = listener;
   }

   public interface OnScrollChangeListener {
      void onScroll(int l, int t, int oldl, int oldt);
   }

   @Override
   public boolean onTouchEvent(MotionEvent ev) {
      return super.onTouchEvent(ev);
   }
}
