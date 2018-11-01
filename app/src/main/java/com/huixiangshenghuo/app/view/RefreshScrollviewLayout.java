package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.view.MyFreshScrollview.ScrollViewListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/6/30.
 * <p>
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 * <p>
 * 子控件是ScrollView
 */

public class RefreshScrollviewLayout extends SwipeRefreshLayout {

   /**
    * 滑动到最下面时的上拉操作
    */
   private int mTouchSlop;
   /**
    * listview实例
    */
   private MyFreshScrollview mListView;

   /**
    * 上拉监听器, 到了最底部的上拉加载操作
    */
   private OnLoadListener mOnLoadListener;

   /**
    * ListView的加载中footer
    */
   private View mListViewFooter;

   /**
    * 按下时的y坐标
    */
   private int mYDown;
   /**
    * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
    */
   private int mLastY;
   /**
    * 是否在加载中 ( 上拉加载更多 )
    */
   private boolean isLoading = false;

   /**
    * 是否在加载中 ( 上拉加载更多 )
    */
   private boolean isCanLoading = true;

   /**
    * @param context
    */
   private LinearLayout loadingLayout;
   private Context context;

   public RefreshScrollviewLayout(Context context) {
      this(context, null);
      this.context = context;
   }

   public RefreshScrollviewLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.context = context;
      mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

      mListViewFooter = LayoutInflater.from(context).inflate(
            R.layout.listview_footer, null, false);
      initData(context);
   }

   private void initData(Context context) {
      mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
      int c = ContextCompat.getColor(getContext(), R.color.colorPrimary);
      setColorSchemeColors(c);
   }

   @Override
   protected void onLayout(boolean changed, int left, int top, int right,
                           int bottom) {
      super.onLayout(changed, left, top, right, bottom);
      // 初始化ListView对象
      if (mListView == null) {
         getListView();
      }
   }

   /**
    * 获取ListView对象
    */
   private void getListView() {
      int childs = getChildCount();
      if (childs > 0) {
         View childView = getChildAt(0);
         if (childView instanceof ScrollView) {
            mListView = (MyFreshScrollview) childView;
            // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
            mListView.setScrollViewListener(new ScrollViewListener() {

               @Override
               public void onScrollChanged(ScrollView scroll, int x, int y, int oldx,
                                           int oldy) {
                  // TODO Auto-generated method stub

                  if (canLoad()) {
//							Log.e("shiye", "------"+System.currentTimeMillis());
                     loadData();
                  }
               }
            });
            Log.d("shiye", "### 找到listview");
         }
      }
   }

   /*
    * (non-Javadoc)
    *
    * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
    */
   @Override
   public boolean dispatchTouchEvent(MotionEvent event) {
      final int action = event.getAction();

      switch (action) {
         case MotionEvent.ACTION_DOWN:
            // 按下
            mYDown = (int) event.getRawY();
            break;

         case MotionEvent.ACTION_MOVE:
            // 移动
            mLastY = (int) event.getRawY();
            break;

         case MotionEvent.ACTION_UP:
            // 抬起
            if (canLoad()) {
               loadData();
            }
            break;
         default:
            break;
      }

      return super.dispatchTouchEvent(event);
   }

   /**
    * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
    *
    * @return
    */
   private boolean canLoad() {
      return isCanLoading && isBottom() && !isLoading && isPullUp();
   }

   /**
    * 判断是否到了最底部
    */
   private boolean isBottom() {

      // 对于ScrollView
      if (mListView != null) {
         // 子scroll view滑动到最顶端
         View child = mListView.getChildAt(0);
         if (child.getMeasuredHeight() <= getHeight() + mListView.getScrollY()) {
            return true;
         }
      }
      return false;
   }

   /**
    * 是否是上拉操作
    *
    * @return
    */
   private boolean isPullUp() {
      return (mYDown - mLastY) >= mTouchSlop;
   }

   /**
    * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
    */
   private void loadData() {
      if (mOnLoadListener != null) {
         // 设置状态
         setLoading(true);
         Log.e("shiye", "----" + System.currentTimeMillis());
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e) {
//			}
         mOnLoadListener.onLoad();
      }
   }

   /**
    * @param loading
    */
   public void setLoading(boolean loading) {
      isLoading = loading;
      if (isLoading) {
         if (loadingLayout != null) {
            loadingLayout.addView(mListViewFooter);
         }
         mListView.scrollTo(0, mListView.getChildAt(0).getMeasuredHeight() + mListViewFooter.getMeasuredHeight());
      } else {
         if (loadingLayout != null) {
            loadingLayout.removeView(mListViewFooter);
         }
         mYDown = 0;
         mLastY = 0;
      }
   }

   /**
    * @param loadListener
    */
   public void setOnLoadListener(OnLoadListener loadListener) {
      mOnLoadListener = loadListener;
   }


   /**
    * 设置刷新
    */
   public static void setRefreshing(SwipeRefreshLayout refreshLayout,
                                    boolean refreshing, boolean notify) {
      Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout
            .getClass();
      if (refreshLayoutClass != null) {

         try {
            Method setRefreshing = refreshLayoutClass.getDeclaredMethod(
                  "setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(refreshLayout, refreshing, notify);
         } catch (NoSuchMethodException e) {
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            e.printStackTrace();
         }
      }
   }

   public void setLoadingLayout(LinearLayout loadingLayout) {
      this.loadingLayout = loadingLayout;
   }

   /**
    * 加载更多的监听器
    *
    * @author mrsimple
    */
   public static interface OnLoadListener {
      public void onLoad();
   }


   public void isCanLoad(int num) {
      if (num < 10) {
         isCanLoading = false;
      } else {
         isCanLoading = true;
      }
   }

   private int mXDown;

   @Override
   public boolean onInterceptTouchEvent(MotionEvent event) {
      switch (event.getAction()) {
         case MotionEvent.ACTION_DOWN:
            mXDown = (int) event.getX();
            break;

         case MotionEvent.ACTION_MOVE:
            final float eventX = event.getX();
            float xDiff = Math.abs(eventX - mXDown);
            // Log.d("refresh" ,"move----" + eventX + "   " + mPrevX + "   " + mTouchSlop);
            // 增加10的容差，让下拉刷新在竖直滑动时就可以触发
            if (xDiff > mTouchSlop + 10) {
               return false;
            }
      }

      return super.onInterceptTouchEvent(event);
   }
}
