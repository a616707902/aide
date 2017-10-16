package com.aide.chenpan.myaide.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
     * ViewPager适配器
     */
  public  class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    /**
     * Tab页面集合
     */
    private List<Fragment> mFragmentList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mFragmentList=list;

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }