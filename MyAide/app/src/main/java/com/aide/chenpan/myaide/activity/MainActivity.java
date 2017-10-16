package com.aide.chenpan.myaide.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.MyFragmentPagerAdapter;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.fragment.BusinessFragment;
import com.aide.chenpan.myaide.fragment.MoreFragment;
import com.aide.chenpan.myaide.fragment.TripFragment;
import com.aide.chenpan.myaide.fragment.WeatherFragment;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.MainPresenter;
import com.aide.chenpan.myaide.utils.WallpaperUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.fragment_container)
    ViewPager mViewPager;
    @Bind(R.id.tv_weather)
    TextView tvWeather;
    @Bind(R.id.tv_business)
    TextView tvBusiness;
    @Bind(R.id.tv_trip)
    TextView tvTrip;
    @Bind(R.id.tv_more)
    TextView tvMore;
    @Bind(R.id.tab_weather)
    LinearLayout tabWeather;
    @Bind(R.id.tab_business)
    LinearLayout tabBusiness;
    @Bind(R.id.tab_trip)
    LinearLayout tabTrip;
    @Bind(R.id.tab_more)
    LinearLayout tabMore;
    @Bind(R.id.llyt_activity_main)
    FrameLayout llytActivityMain;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager mFm;

    /**
     * Tab未选中文字颜色
     */
    private int mUnSelectColor;

    /**
     * Tab选中时文字颜色
     */
    private int mSelectColor;


    /**
     * Tab页面集合
     */
    private List<Fragment> mFragmentList;

    /**
     * 当前Tab的Index
     */
    private int mCurrentIndex = -1;

    /**
     * 展示天气的Fragment
     */
    private WeatherFragment mWeaFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        // 设置主题壁纸
       // setThemeWallpaper();
        //禁止滑动返回
        setSwipeBackEnable(false);
        WallpaperUtils.setBackground(llytActivityMain, this);
        mFm = getSupportFragmentManager();
        // Tab选中文字颜色
        mSelectColor = getResources().getColor(R.color.white);
        // Tab未选中文字颜色
        mUnSelectColor = getResources().getColor(R.color.white_trans50);
        initFragment();
        // 启动程序后选中Tab为天气
        setTabSelection(0);
    }

    /**
     *  设置fragment的个数  并初始化
     */
    private void initFragment() {

        // 设置Tab页面集合
        mFragmentList = new ArrayList<>();
        // 展示事务的Fragment
        BusinessFragment   businessFragment= new BusinessFragment();
        // 展示天气的Fragment
        mWeaFragment = new WeatherFragment();
        // 展示 出行的Fragment
        TripFragment mTimeFragment = new TripFragment();
        // 展示更多的Fragment
        MoreFragment mMoreFragment = new MoreFragment();

        mFragmentList.add(mWeaFragment);
        mFragmentList.add(businessFragment);
        mFragmentList.add(mTimeFragment);
        mFragmentList.add(mMoreFragment);

        // 设置ViewPager
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(mFm,mFragmentList));
        // 设置一边加载的page数
        mViewPager.setOffscreenPageLimit(3);
        // TODO：切换渐变
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                setTabSelection(index);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    public BasePresenter getPresenter() {
        return new MainPresenter();
    }




    @OnClick({R.id.tab_weather, R.id.tab_business, R.id.tab_trip, R.id.tab_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_weather:
                // 切换天气视图
                setTabSelection(0);
                break;
            case R.id.tab_business:
                // 切换事务视图
                setTabSelection(1);
                break;
            case R.id.tab_trip:
                // 切换出行视图
                setTabSelection(2);
                break;
            case R.id.tab_more:
                // 切换更多视图
                setTabSelection(3);
                break;
        }
    }

    /**
     * 设置选中的Tab
     *
     * @param index 每个tab对应的下标。0表示天气，1表示事务，2表示出行，3表示更多。
     */
    private void setTabSelection(int index) {
        // 当重复选中相同Tab时不进行任何处理
        if (mCurrentIndex == index) {
            return;
        }

//        if (index != 0) {
//            // 当不是天气界面并且已经开始延迟刷新天气线程
//            if (mWeaFragment.mHandler != null && mWeaFragment.mIsPostDelayed) {
//                // 取消线程
//                mWeaFragment.mHandler.removeCallbacks(mWeaFragment.mRun);
//                mWeaFragment.mIsPostDelayed = false;
//                LogUtil.i(LOG_TAG, "已移除刷新天气线程");
//            }
//            if (mWeaFragment.mPullRefreshScrollView != null) {
//                // 当正在刷新
//                if (mWeaFragment.mPullRefreshScrollView.isRefreshing()) {
//                    // 停止刷新
//                    mWeaFragment.mPullRefreshScrollView.onRefreshComplete();
//                    LogUtil.i(LOG_TAG, "已停止刷新天气动画");
//                }
//            }
//            // 停止刷新动画
//            if (mWeaFragment.mRefreshBtn != null) {
//                mWeaFragment.mRefreshBtn.clearAnimation();
//            }
//        }

        // 设置当前Tab的Index值为传入的Index值
        mCurrentIndex = index;
        // 改变ViewPager视图
        mViewPager.setCurrentItem(index, false);
        // 清除掉上次的选中状态
        clearSelection();
        // 判断传入的Index
        switch (index) {
            // 天气
            case 0:
                // 改变天气控件的图片和文字颜色
                setTextView(R.drawable.ic_weather_select, tvWeather, mSelectColor);
                break;
            // 事务
            case 1:
                // 改变事务控件的图片和文字颜色
                setTextView(R.drawable.calendar_15_select, tvBusiness, mSelectColor);
                break;
            // 出行
            case 2:
                // 改变出行控件的图片和文字颜色
                setTextView(R.drawable.ic_trip_select, tvTrip, mSelectColor);
                break;
            // 更多
            case 3:
                // 改变更多控件的图片和文字颜色
                setTextView(R.drawable.ic_more_select, tvMore, mSelectColor);
                break;
        }

    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        // 设置天气Tab为未选中状态
        setTextView(R.drawable.ic_weather_unselect, tvWeather,
                mUnSelectColor);
        // 设置事务Tab为未选中状态
        setTextView(R.drawable.calendar_15_unselect, tvBusiness, mUnSelectColor);
        // 设置出行Tab为未选中状态
        setTextView(R.drawable.ic_trip_unselect, tvTrip, mUnSelectColor);
        // 设置更多Tab为未选中状态
        setTextView(R.drawable.ic_more_unselect, tvMore, mUnSelectColor);
    }

    /**
     * 设置Tab布局
     *
     * @param iconId   Tab图标
     * @param textView Tab文字
     * @param color    Tab文字颜色
     */
    private void setTextView(int iconId, TextView textView, int color) {
        @SuppressWarnings("deprecation")
        Drawable drawable = getResources().getDrawable(iconId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            // 设置图标
            textView.setCompoundDrawables(null, drawable, null, null);
        }
        // 设置文字颜色
        textView.setTextColor(color);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
