package com.huixiangshenghuo.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huixiangshenghuo.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * @author zhaoyunhai on 16/4/26.
 */
public class ImagePagerActivity extends Activity {
   public static final String INTENT_IMGURLS = "imgurls";
   public static final String INTENT_POSITION = "position";
   private List<View> guideViewList = new ArrayList<View>();
   private LinearLayout guideGroup;
   /**
    * imageSize[0] 表示width
    * imageSize[1] 表示height
    */
   public static int[] imageSize;


   public static void startImagePagerActivity(Context context, List<String> imgUrls, int position) {
      Intent intent = new Intent(context, ImagePagerActivity.class);
      intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
      intent.putExtra(INTENT_POSITION, position);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_imagepager);
      ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.pager);
      //原来的 如果要改过来 布局也要改
      //    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
      guideGroup = (LinearLayout) findViewById(R.id.guideGroup);

      int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
      ArrayList<String> imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);

      ImageAdapter mAdapter = new ImageAdapter(this);
      mAdapter.setDatas(imgUrls);
      viewPager.setAdapter(mAdapter);
      viewPager.setOffscreenPageLimit(0);
      viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
            for (int i = 0; i < guideViewList.size(); i++) {
               guideViewList.get(i).setSelected(i == position ? true : false);
            }
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
      });
      viewPager.setCurrentItem(startPos);

      addGuideView(guideGroup, startPos, imgUrls);
   }

   private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls) {
      if (imgUrls != null && imgUrls.size() > 0) {
         guideViewList.clear();
         for (int i = 0; i < imgUrls.size(); i++) {
            View view = new View(this);
            view.setBackgroundResource(R.drawable.selector_guide_bg);
            view.setSelected(i == startPos ? true : false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                  getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
            layoutParams.setMargins(10, 0, 0, 0);
            guideGroup.addView(view, layoutParams);
            guideViewList.add(view);
         }
      }
   }

   private static class ImageAdapter extends PagerAdapter {

      private List<String> datas = new ArrayList<String>();
      private LayoutInflater inflater;
      private Context context;

      public void setDatas(List<String> datas) {
         if (datas != null)
            this.datas = datas;
      }

      public ImageAdapter(Context context) {
         this.context = context;
         this.inflater = LayoutInflater.from(context);

      }

      @Override
      public int getCount() {
         if (datas == null) return 0;
         return datas.size();
      }


      @Override
      public Object instantiateItem(ViewGroup container, final int position) {
         View view = inflater.inflate(R.layout.item_pager_image, container, false);
         if (view != null) {
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);
            //预览imageView
            final ImageView smallImageView = new ImageView(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize[0], imageSize[1]);
            layoutParams.gravity = Gravity.CENTER;
            smallImageView.setLayoutParams(layoutParams);
            smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((FrameLayout) view).addView(smallImageView);
            //loading
               /* final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout)view).addView(loading);*/

            final String imgurl = datas.get(position);
            ImageLoader.getInstance().displayImage(imgurl, imageView);
            ImageLoader.getInstance().displayImage(imgurl, imageView, new SimpleImageLoadingListener() {
               @Override
               public void onLoadingStarted(String imageUri, View view) {
                  //获取内存中的缩略图
                  String memoryCacheKey = MemoryCacheUtils.generateKey(imageUri, new ImageSize(imageSize[0], imageSize[1]));
                  Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(memoryCacheKey);
                  if (bmp != null && !bmp.isRecycled()) {
//                            smallImageView.setVisibility(View.VISIBLE);
//                            smallImageView.setImageBitmap(bmp);

                  }
               }

               @Override
               public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                  smallImageView.setVisibility(View.GONE);
               }

            });

            container.addView(view, 0);
         }
         return view;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
         container.removeView((View) object);
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
         return view.equals(object);
      }

      @Override
      public void restoreState(Parcelable state, ClassLoader loader) {
      }

      @Override
      public Parcelable saveState() {
         return null;
      }


   }
}
