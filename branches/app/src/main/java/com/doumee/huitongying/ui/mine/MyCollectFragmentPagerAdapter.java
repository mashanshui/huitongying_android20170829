package com.huixiangshenghuo.app.ui.mine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by wl on 2016/11/2.
 */
public class MyCollectFragmentPagerAdapter extends FragmentPagerAdapter {

   private ArrayList<Fragment> fragments;

   public MyCollectFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
      super(fm);
      this.fragments = fragments;
   }

   @Override
   public int getCount() {
      return fragments.size();
   }

   @Override
   public Object instantiateItem(ViewGroup vg, int position) {
      return super.instantiateItem(vg, position);
   }

   @Override
   public void destroyItem(ViewGroup container, int position, Object object) {
      super.destroyItem(container, position, object);
   }

   @Override
   public Fragment getItem(int position) {
      return fragments.get(position);
   }

}
