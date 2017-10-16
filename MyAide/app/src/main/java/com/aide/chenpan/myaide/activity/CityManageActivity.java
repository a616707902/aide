/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.aide.chenpan.myaide.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.CityManageAdapter;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.bean.CityManage;
import com.aide.chenpan.myaide.bean.WeatherDaysForecast;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.CityManagePresenter;
import com.aide.chenpan.myaide.mvp.view.CityManageView;
import com.aide.chenpan.myaide.other.DBObserverListener;
import com.aide.chenpan.myaide.other.NotifyListener;
import com.aide.chenpan.myaide.other.WeatherDBOperate;
import com.aide.chenpan.myaide.utils.ButtonUtils;
import com.aide.chenpan.myaide.utils.NetworkUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.aide.chenpan.myaide.utils.WeatherUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 城市管理activity
 *
 * @author 咖枯
 * @version 1.0 2015/10/22
 */
public class CityManageActivity extends BaseActivity implements View.OnClickListener, CityManageView {

    /**
     * Log tag ：CityManageActivity
     */
    private static final String LOG_TAG = "CityManageActivity";

    /**
     * 城市管理的requestCode
     */
    private static final int REQUEST_CITY_MANAGE = 1;

    /**
     * 添加城市
     */
    private static final int ADD_CITY = -1;

    /**
     * 添加定位
     */
    private static final int ADD_LOCATION = -2;

    /**
     * 城市管理列表
     */
    private List<CityManage> mCityManageList;

    /**
     * 城市管理adapter
     */
    private CityManageAdapter mCityManageAdapter;

    /**
     * 天气代码
     */
    private String mWeatherCode;

    /**
     * 城市管理GridView
     */
    private GridView mGridView;

    /**
     * 操作栏编辑按钮
     */
    private ImageView mEditBtn;

    /**
     * 操作栏编辑完成按钮
     */
    private ImageView mAcceptBtn;

    /**
     * 刷新按钮
     */
    private ImageView mRefreshBtn;

    /**
     * 取消刷新按钮
     */
    private ImageView mCancelRefreshBtn;

    /**
     * 监听城市点击事件Listener
     */
    private AdapterView.OnItemClickListener mOnItemClickListener;

    /**
     * 城市管理实例
     */
    private CityManage mCityManage;

    /**
     * 是否正在刷新
     */
    private boolean mIsRefreshing;

    /**
     * 默认城市是否变更过
     */
    private boolean mIsDefaultCityChanged = false;

    /**
     * 是否正在添加城市
     */
    private boolean mIsCityAdding = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_manage;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        // 设置布局元素
        initViews();
        LinearLayout backGround = (LinearLayout) findViewById(R.id.city_manage_background);
        // 设置页面高斯模糊背景
        WallpaperUtils.setBackgroundBlur(backGround, this);
    }

    @Override
    protected void initEventAndData() {
        // 设置adapter
        initAdapter();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        mCityManageList = new ArrayList<>();
        mCityManageList = WeatherDBOperate.getInstance().loadCityManages();
        // 添加城市按钮用
        CityManage cityManage1 = new CityManage();
        mCityManageList.add(cityManage1);
        mCityManageAdapter = new CityManageAdapter(this, mCityManageList);
        // 城市列表为空的更新回调
        mCityManageAdapter.setDBObserverListener(new DBObserverListener() {
            @Override
            public void onDBDataChanged() {
                // 允许列表item点击
                mGridView.setOnItemClickListener(mOnItemClickListener);
                // 显示编辑按钮
                mEditBtn.setVisibility(View.VISIBLE);
                // 隐藏完成按钮
                mAcceptBtn.setVisibility(View.GONE);
            }
        });
        // 默认城市改动回调
        mCityManageAdapter.setNotifyListener(new NotifyListener() {
            @Override
            public void onChanged() {
                mIsDefaultCityChanged = true;
            }
        });
        mGridView.setAdapter(mCityManageAdapter);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != (mCityManageList.size() - 1)) {
                    mIsRefreshing = false;
                    // 显示删除，完成按钮，隐藏修改按钮
                    displayDeleteAccept();
                }
                return true;
            }
        });

    }

    /**
     * 初始化布局元素
     */
    private void initViews() {
        mOnItemClickListener = new OnItemClickListenerImpl();
        mGridView = (GridView) findViewById(R.id.gv_city_manage);

        // 返回按钮
        ImageView returnBtn = (ImageView) findViewById(R.id.action_return);
        returnBtn.setOnClickListener(this);
        // 编辑闹钟
        mEditBtn = (ImageView) findViewById(R.id.action_edit);
        mEditBtn.setOnClickListener(this);
        // 完成按钮
        mAcceptBtn = (ImageView) findViewById(R.id.action_accept);
        mAcceptBtn.setOnClickListener(this);
        // 刷新按钮
        mRefreshBtn = (ImageView) findViewById(R.id.action_refresh);
        mRefreshBtn.setOnClickListener(this);
        // 取消刷新按钮
        mCancelRefreshBtn = (ImageView) findViewById(R.id.action_refresh_cancel);
        mCancelRefreshBtn.setOnClickListener(this);
    }

    @Override
    public BasePresenter getPresenter() {
        return new CityManagePresenter();
    }

    @Override
    public void showWeatherData(WeatherInfo mWeatherInfo, int position) {
        if (position == -1) {
            processCityManage(mWeatherInfo.getCity(), null, mWeatherCode, mWeatherInfo);
            // 添加定位
        } else if (-2 == position) {
            mCityManage.setLocationCity(mWeatherInfo.getCity());
            processCityManage(getString(R.string.auto_location), mWeatherInfo.getCity(),
                    getString(R.string.auto_location), mWeatherInfo);
        } else {
            // 取得城市管理列表对应的城市管理item
            CityManage cityManage = mCityManageList.get(position);
            // 更新城市管理列表item信息
            setCityManageInfo(cityManage, mWeatherInfo);
            // 修改城市管理item信息
            WeatherDBOperate.getInstance().updateCityManage(cityManage);
            continueRefreshOrStop(position);
        }
    }

    @Override
    public void runOnUiForFailed(String string, int position) {
        // 添加
        if (position == -1 || position == -2) {
            // 移除临时添加的新城市item
            mCityManageList.remove(mCityManageList.size() - 2);
            mCityManageAdapter.notifyDataSetChanged();
            // GridView设置点击事件
            mGridView.setOnItemClickListener(mOnItemClickListener);
            mIsCityAdding = false;
        } else {
            continueRefreshOrStop(position);
        }
        ToastUtil.showShortToast(CityManageActivity.this, string);
    }

    @Override
    public void showLoadProgressDialog(String str) {

    }

    @Override
    public void dissDialog() {

    }

    class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mIsRefreshing = false;
            // 当为列表最后一项（添加城市）
            if (position == (mCityManageList.size() - 1)) {
                // 不响应重复点击
                if (ButtonUtils.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(CityManageActivity.this, AddCityActivity.class);
                startActivityForResult(intent, REQUEST_CITY_MANAGE);
            } else {
                CityManage cityManage = mCityManageAdapter.getItem(position);
                // 不是自动定位
                if (cityManage.getLocationCity() == null) {
                    myFinish(cityManage.getCityName());
                } else {
                    myFinish(cityManage.getLocationCity());
                }
            }
        }
    }

    private void myFinish(String cityName) {
        if (cityName != null) {
            Intent intent = getIntent();
            intent.putExtra(Constants.CITY_NAME, cityName);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    /**
     * 显示删除，完成按钮，隐藏修改按钮
     */
    private void displayDeleteAccept() {
        // 禁止gridView点击事件
        mGridView.setOnItemClickListener(null);
        mCityManageAdapter.setCityDeleteButton(true);
        mCityManageAdapter.notifyDataSetChanged();
        mEditBtn.setVisibility(View.GONE);
        mAcceptBtn.setVisibility(View.VISIBLE);

    }

    /**
     * 隐藏删除，完成按钮,显示修改按钮
     */
    private void hideDeleteAccept() {
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mCityManageAdapter.setCityDeleteButton(false);
        mCityManageAdapter.notifyDataSetChanged();
        mAcceptBtn.setVisibility(View.GONE);
        mEditBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮
            case R.id.action_return:
                mIsRefreshing = false;
                onBack();
                break;
            // 编辑按钮
            case R.id.action_edit:
                // 当正在添加城市或者列表内容为空时禁止响应编辑事件
                if (mIsCityAdding || mGridView.getChildCount() == 1) {
                    return;
                }
                mIsRefreshing = false;
                // 显示删除，完成按钮，隐藏修改按钮
                displayDeleteAccept();
                break;
            // 完成按钮
            case R.id.action_accept:
                // 隐藏删除，完成按钮,显示修改按钮
                hideDeleteAccept();
                break;
            // 刷新按钮
            case R.id.action_refresh:
                // 列表为空（只有添加按钮）不刷新
                if (mCityManageList.size() == 1) {
                    return;
                }

                // 判断网络是否可用
                if (!NetworkUtils.isNetworkAvailable(this)) {
                    ToastUtil.showShortToast(this, getString(R.string.internet_error));
                    return;
                }

                if (mEditBtn.getVisibility() == View.GONE) {
                    // 隐藏删除，完成按钮,显示修改按钮
                    hideDeleteAccept();
                }

                mRefreshBtn.setVisibility(View.INVISIBLE);
                mCancelRefreshBtn.setVisibility(View.VISIBLE);

                mIsRefreshing = true;
                refresh(0);
                break;
            // 取消更新
            case R.id.action_refresh_cancel:
                mIsRefreshing = false;
                mRefreshBtn.setVisibility(View.VISIBLE);
                mCancelRefreshBtn.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 返回，变更当前城市为默认城市
     */
    private void modifyCity() {
        SharedPreferences share = getSharedPreferences(Constants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        myFinish(share.getString(Constants.DEFAULT_CITY_NAME, null));
    }

    /**
     * 根据传入的代号和类型从服务器上查询天气代号、天气数据
     *
     * @param address  查询地址
     * @param position 更新位置:-1,添加城市; -2,添加定位; 其他，更新城市的位置
     * @param cityName 刷新城市名
     */
    private void queryFormServer(final String address, final int position, final String cityName) {
        ((CityManagePresenter) mPresenter).getServerData(this, position, cityName);

    }

    /**
     * 执行UI方法
     *
     * @param info 显示的错误信息
     */

    private void runOnUi(final String info, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 添加
                if (position == -1 || position == -2) {
                    // 移除临时添加的新城市item
                    mCityManageList.remove(mCityManageList.size() - 2);
                    mCityManageAdapter.notifyDataSetChanged();
                    // GridView设置点击事件
                    mGridView.setOnItemClickListener(mOnItemClickListener);
                    mIsCityAdding = false;
                } else {
                    continueRefreshOrStop(position);
                }
                ToastUtil.showShortToast(CityManageActivity.this, info);
            }
        });
    }

    /**
     * 隐藏进度条显示刷新按钮
     */
    private void hideProgressBarDisplayRefresh() {
        mCityManageAdapter.displayProgressBar(-1);
        mCityManageAdapter.notifyDataSetChanged();

        mRefreshBtn.setVisibility(View.VISIBLE);
        mCancelRefreshBtn.setVisibility(View.GONE);
    }


    private void processCityManage(String cityName, String locationCity, String weatherCode, WeatherInfo mWeatherInfo) {
        mCityManage.setCityName(cityName);
        // 设置城市管理列表item信息
        setCityManageInfo(mCityManage, mWeatherInfo);
        // 隐藏进度条
        mCityManageAdapter.displayProgressBar(-1);
        mCityManageAdapter.notifyDataSetChanged();
        // GridView设置点击事件
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mIsCityAdding = false;
        // 存储城市管理item信息
        WeatherDBOperate.getInstance().saveCityManage(mCityManage);
        // 首次添加城市
        if (mCityManageList.size() == 2) {
            SharedPreferences share = getSharedPreferences(
                    Constants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();
            // 保存城市管理的默认城市
            editor.putString(Constants.DEFAULT_CITY, cityName);

            mCityManageAdapter.setDefaultCity(cityName);
            mCityManageAdapter.notifyDataSetChanged();

            // 保存默认的城市名
            if (locationCity != null) {
                cityName = locationCity;
            }
            editor.putString(Constants.DEFAULT_CITY_NAME, cityName);
            // 保存默认的天气代号
            editor.apply();

            mIsDefaultCityChanged = true;

        }

    }


    /**
     * 继续刷新/停止刷新
     *
     * @param position 当前城市位置
     */
    private void continueRefreshOrStop(int position) {
        // 当为列表最后一项或者不是正在刷新状态
        if (position >= (mCityManageList.size() - 2) || !mIsRefreshing) {
            // 隐藏进度条显示刷新按钮
            hideProgressBarDisplayRefresh();
        } else {
            refresh(position + 1);
        }
    }

    /**
     * 刷新
     *
     * @param position 当前城市位置
     */
    private void refresh(int position) {
        // 下一项显示进度条
        mCityManageAdapter.displayProgressBar(position);
        mCityManageAdapter.notifyDataSetChanged();
        String cityName;
        String address;
        CityManage cityManage1 = mCityManageList.get(position);
        // 不是自动定位
        if (cityManage1.getLocationCity() == null) {
            cityName = cityManage1.getCityName();
            address = getString(R.string.address_weather_city, cityName);
        } else {
            cityName = cityManage1.getLocationCity();
            address = null;
        }
        // 更新查询下一项
        queryFormServer(address, position, cityName);
    }

    /**
     * 设置城市管理列表item信息
     *
     * @param cityManage  城市管理列表item
     * @param weatherInfo 天气信息
     */
    private void setCityManageInfo(CityManage cityManage, WeatherInfo weatherInfo) {
        if (weatherInfo.getWeatherDaysForecast().size() >= 5) {
            WeatherDaysForecast weather;

            Calendar calendar = Calendar.getInstance();
            // 现在小时
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            String time[] = weatherInfo.getUpdateTime().split(":");
            int hour1 = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            //更新时间从23：45开始到05：20以前的数据，或者早上5点以前，后移一天填充
            if ((hour1 == 23 && minute >= 45) || (hour1 < 5) ||
                    ((hour1 == 5) && (minute < 20)) || hour <= 5) {
                weather = weatherInfo.getWeatherDaysForecast().get(2);
            } else {
                weather = weatherInfo.getWeatherDaysForecast().get(1);
            }

            cityManage.setTempHigh(weather.getHigh().substring(3));
            cityManage.setTempLow(weather.getLow().substring(3));

            cityManage.setWeatherType(WeatherUtils.getWeatherType
                    (this, weather.getTypeDay(), weather.getTypeNight()));

            cityManage.setWeatherTypeDay(weather.getTypeDay());
            cityManage.setWeatherTypeNight(weather.getTypeNight());
        } else {
            cityManage.setTempHigh(getString(R.string.dash));
            cityManage.setTempLow(getString(R.string.dash));
            cityManage.setWeatherType(getString(R.string.no));
            cityManage.setWeatherTypeDay(getString(R.string.no));
            cityManage.setWeatherTypeNight(getString(R.string.no));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CITY_MANAGE) {
            // GridView禁用点击事件
            mGridView.setOnItemClickListener(null);
            mIsCityAdding = true;

            mCityManage = new CityManage();
            // 插在最后一项（添加按钮）之前
            mCityManageList.add(mCityManageList.size() - 1, mCityManage);
            // 显示progressBar
            mCityManageAdapter.displayProgressBar(mCityManageList.size() - 2);
            mCityManageAdapter.notifyDataSetChanged();

            String cityName = data.getStringExtra(Constants.CITY_NAME);

//            if (weatherCode != null) {
//                mWeatherCode = weatherCode;
//                queryFormServer(getString(R.string.address_weather_city, cityName),
//                        ADD_CITY, null);
//                // 添加定位
//            } else {
                queryFormServer(null,
                        ADD_CITY, cityName);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsRefreshing) {
            mIsRefreshing = false;
        } else {
            onBack();
        }
    }

    /**
     * 当按下返回/后退键
     */
    private void onBack() {
        // 当修改了默认城市
        if (mIsDefaultCityChanged) {
            modifyCity();
        } else {
            String cityName = getIntent().getStringExtra(Constants.CITY_NAME);
            int number = WeatherDBOperate.getInstance().queryCityManage(cityName);
            // 当前城市没有被删除
            if (number == 1) {
                finish();
            } else {
                // 返回加载默认城市
                modifyCity();
            }
        }
    }
}