package com.aide.chenpan.myaide.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.activity.CityManageActivity;
import com.aide.chenpan.myaide.adapter.MyFancyCoverFlowAdapter;
import com.aide.chenpan.myaide.base.BaseFragment;
import com.aide.chenpan.myaide.bean.CityManage;
import com.aide.chenpan.myaide.bean.ConstellationBean;
import com.aide.chenpan.myaide.bean.FancyCoverItem;
import com.aide.chenpan.myaide.bean.WeatherDaysForecast;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.bean.WeatherLifeIndex;
import com.aide.chenpan.myaide.common.AideApplication;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.event.OnVisibleListener;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.WeatherFragmentPresenter;
import com.aide.chenpan.myaide.mvp.view.WeatherFragmentView;
import com.aide.chenpan.myaide.other.WeatherDBOperate;
import com.aide.chenpan.myaide.service.LocationService;
import com.aide.chenpan.myaide.utils.ButtonUtils;
import com.aide.chenpan.myaide.utils.NetworkUtils;
import com.aide.chenpan.myaide.utils.SharedPreferencesUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.aide.chenpan.myaide.utils.WeatherUtils;
import com.aide.chenpan.myaide.widget.LineChartViewDouble;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.dalong.francyconverflow.FancyCoverFlow;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.ScrollViewListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Administrator on 2017/4/25.
 */
public class WeatherFragment extends BaseFragment implements WeatherFragmentView, View.OnClickListener {
    @Bind(R.id.action_home)
    ImageView actionHome;
    @Bind(R.id.action_title)
    TextView actionTitle;
    @Bind(R.id.action_refresh)
    ImageView actionRefresh;
    @Bind(R.id.loading_progress)
    CircularProgressBar loadingProgress;
    @Bind(R.id.loading_msg)
    TextView loadingMsg;
    @Bind(R.id.progress_bar_llyt)
    LinearLayout progressBarLlyt;
    @Bind(R.id.viewstub_aide)
    ViewStub viewstubAide;
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView pullRefreshScrollview;
    @Bind(R.id.wea_background)
    LinearLayout weaBackground;


    /**
     * 天气的requestCode
     */
    private static final int REQUEST_WEA = 1;

    /**
     * 城市名
     */
    private TextView mCityNameTv;

    /**
     * 警报
     */
    private TextView mAlarmTv;

    /**
     * 更新时间
     */
    private TextView mUpdateTimeTv;


    /**
     * 温度1
     */
    private ImageView mTemperature1Iv;

    /**
     * 温度2
     */
    private ImageView mTemperature2Iv;

    /**
     * 温度3
     */
    private ImageView mTemperature3Iv;

    /**
     * 天气类型
     */
    private TextView mWeatherTypeTv;


    /**
     * 大气环境
     */
    private TextView mAqiTv;

    /**
     * 湿度
     */
    private TextView mHumidityTv;

    /**
     * 风向、风力
     */
    private TextView mWindTv;


    /**
     * 今天天气类型图片
     */
    private ImageView mWeatherTypeIvToday;

    /**
     * 今天高温
     */
    private TextView mTempHighTvToday;

    /**
     * 今天低温
     */
    private TextView mTempLowTvToday;

    /**
     * 今天天气类型文字
     */
    private TextView mWeatherTypeTvToday;


    /**
     * 明天天气类型图片
     */
    private ImageView mWeatherTypeIvTomorrow;

    /**
     * 明天高温
     */
    private TextView mTempHighTvTomorrow;

    /**
     * 明天低温
     */
    private TextView mTempLowTvTomorrow;

    /**
     * 明天天气类型文字
     */
    private TextView mWeatherTypeTvTomorrow;


    /**
     * 后天天气类型图片
     */
    private ImageView mWeatherTypeIvDayAfterTomorrow;

    /**
     * 后天高温
     */
    private TextView mTempHighTvDayAfterTomorrow;

    /**
     * 后天低温
     */
    private TextView mTempLowTvDayAfterTomorrow;

    /**
     * 后天天气类型文字
     */
    private TextView mWeatherTypeTvDayAfterTomorrow;


    /**
     * 多天预报标题1
     */
    private TextView mDaysForecastTvWeek1;

    /**
     * 多天预报标题2
     */
    private TextView mDaysForecastTvWeek2;

    /**
     * 多天预报标题3
     */
    private TextView mDaysForecastTvWeek3;

    /**
     * 多天预报标题4
     */
    private TextView mDaysForecastTvWeek4;

    /**
     * 多天预报标题5
     */
    private TextView mDaysForecastTvWeek5;

    /**
     * 多天预报标题6
     */
    private TextView mDaysForecastTvWeek6;


    /**
     * 多天预报日期1
     */
    private TextView mDaysForecastTvDay1;

    /**
     * 多天预报日期2
     */
    private TextView mDaysForecastTvDay2;

    /**
     * 多天预报日期3
     */
    private TextView mDaysForecastTvDay3;

    /**
     * 多天预报日期4
     */
    private TextView mDaysForecastTvDay4;

    /**
     * 多天预报日期5
     */
    private TextView mDaysForecastTvDay5;

    /**
     * 多天预报日期6
     */
    private TextView mDaysForecastTvDay6;


    /**
     * 多天预报白天天气类型图片1
     */
    private ImageView mDaysForecastWeaTypeDayIv1;

    /**
     * 多天预报白天天气类型图片2
     */
    private ImageView mDaysForecastWeaTypeDayIv2;

    /**
     * 多天预报白天天气类型图片3
     */
    private ImageView mDaysForecastWeaTypeDayIv3;

    /**
     * 多天预报白天天气类型图片4
     */
    private ImageView mDaysForecastWeaTypeDayIv4;

    /**
     * 多天预报白天天气类型图片5
     */
    private ImageView mDaysForecastWeaTypeDayIv5;

    /**
     * 多天预报白天天气类型图片6
     */
    private ImageView mDaysForecastWeaTypeDayIv6;


    /**
     * 多天预报白天天气类型文字1
     */
    private TextView mDaysForecastWeaTypeDayTv1;

    /**
     * 多天预报白天天气类型文字2
     */
    private TextView mDaysForecastWeaTypeDayTv2;

    /**
     * 多天预报白天天气类型文字3
     */
    private TextView mDaysForecastWeaTypeDayTv3;

    /**
     * 多天预报白天天气类型文字4
     */
    private TextView mDaysForecastWeaTypeDayTv4;

    /**
     * 多天预报白天天气类型文字5
     */
    private TextView mDaysForecastWeaTypeDayTv5;

    /**
     * 多天预报白天天气类型文字6
     */
    private TextView mDaysForecastWeaTypeDayTv6;


    /**
     * 温度曲线
     */
    private LineChartViewDouble mCharView;


    /**
     * 多天预报夜间天气类型图片1
     */
    private ImageView mDaysForecastWeaTypeNightIv1;

    /**
     * 多天预报夜间天气类型图片2
     */
    private ImageView mDaysForecastWeaTypeNightIv2;

    /**
     * 多天预报夜间天气类型图片3
     */
    private ImageView mDaysForecastWeaTypeNightIv3;

    /**
     * 多天预报夜间天气类型图片4
     */
    private ImageView mDaysForecastWeaTypeNightIv4;

    /**
     * 多天预报夜间天气类型图片5
     */
    private ImageView mDaysForecastWeaTypeNightIv5;

    /**
     * 多天预报夜间天气类型图片6
     */
    private ImageView mDaysForecastWeaTypeNightIv6;


    /**
     * 多天预报夜间天气类型文字1
     */
    private TextView mDaysForecastWeaTypeNightTv1;

    /**
     * 多天预报夜间天气类型文字2
     */
    private TextView mDaysForecastWeaTypeNightTv2;

    /**
     * 多天预报夜间天气类型文字3
     */
    private TextView mDaysForecastWeaTypeNightTv3;

    /**
     * 多天预报夜间天气类型文字4
     */
    private TextView mDaysForecastWeaTypeNightTv4;

    /**
     * 多天预报夜间天气类型文字5
     */
    private TextView mDaysForecastWeaTypeNightTv5;

    /**
     * 多天预报夜间天气类型文字6
     */
    private TextView mDaysForecastWeaTypeNightTv6;


    /**
     * 多天预报风向1
     */
    private TextView mDaysForecastWindDirectionTv1;

    /**
     * 多天预报风向2
     */
    private TextView mDaysForecastWindDirectionTv2;

    /**
     * 多天预报风向3
     */
    private TextView mDaysForecastWindDirectionTv3;

    /**
     * 多天预报风向4
     */
    private TextView mDaysForecastWindDirectionTv4;

    /**
     * 多天预报风向5
     */
    private TextView mDaysForecastWindDirectionTv5;

    /**
     * 多天预报风向6
     */
    private TextView mDaysForecastWindDirectionTv6;


    /**
     * 多天预报风力1
     */
    private TextView mDaysForecastWindPowerTv1;

    /**
     * 多天预报风力2
     */
    private TextView mDaysForecastWindPowerTv2;

    /**
     * 多天预报风力3
     */
    private TextView mDaysForecastWindPowerTv3;

    /**
     * 多天预报风力4
     */
    private TextView mDaysForecastWindPowerTv4;

    /**
     * 多天预报风力5
     */
    private TextView mDaysForecastWindPowerTv5;

    /**
     * 多天预报风力6
     */
    private TextView mDaysForecastWindPowerTv6;


    /**
     * 雨伞指数TextView
     */
    private TextView mLifeIndexUmbrellaTv;

    /**
     * 紫外线指数TextView
     */
    private TextView mLifeIndexUltravioletRaysTv;

    /**
     * 穿衣指数TextView
     */
    private TextView mLifeIndexDressTv;

    /**
     * 感冒指数TextView
     */
    private TextView mLifeIndexColdTv;

    /**
     * 晨练指数TextView
     */
    private TextView mLifeIndexMorningExerciseTv;

    /**
     * 运动指数TextView
     */
    private TextView mLifeIndexSportTv;

    /**
     * 洗车指数TextView
     */
    private TextView mLifeIndexCarWashTv;

    /**
     * 晾晒指数TextView
     */
    private TextView mLifeIndexAirCureTv;


    /**
     * 雨伞指数详细
     */
    private String mLifeIndexUmbrellaDetail;

    /**
     * 紫外线指数详细
     */
    private String mLifeIndexUltravioletRaysDetail;

    /**
     * 穿衣指数详细
     */
    private String mLifeIndexDressDetail;

    /**
     * 感冒指数详细
     */
    private String mLifeIndexColdDetail;

    /**
     * 晨练指数详细
     */
    private String mLifeIndexMorningExerciseDetail;

    /**
     * 运动指数详细
     */
    private String mLifeIndexSportDetail;

    /**
     * 洗车指数详细
     */
    private String mLifeIndexCarWashDetail;

    /**
     * 晾晒指数详细
     */
    private String mLifeIndexAirCureDetail;

    /**
     * 下拉刷新ScrollView
     */
    public PullToRefreshScrollView mPullRefreshScrollView;

    /**
     * 刷新按钮
     */
    public ImageView mRefreshBtn;

    /**
     * 延迟刷新线程是否已经启动
     */
    public boolean mIsPostDelayed;

    /**
     * 延迟刷新Handler
     */
    public Handler mHandler;

    /**
     * 延迟刷新Runnable
     */
    public Runnable mRun;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean mIsPrepared;

    /**
     * 上次主动更新时间
     */
    private long mLastActiveUpdateTime;

    /**
     * 设置壁纸
     */
    private LinearLayout mBackGround;

    /**
     * 模糊处理过的Drawable
     */
    private Drawable mBlurDrawable;

    /**
     * 屏幕密度
     */
    private float mDensity;

    /**
     * 透明
     */
    private int mAlpha = 0;

    /**
     * 当前天气预报城市名
     */
    private String mCityName;

    /**
     * 当前天气预报城市天气代码
     */
   // private String mCityWeatherCode;


//    /**
//     * 百度定位服务
//     */
//    private LocationClient mLocationClient;
//
//    /**
//     * 百度定位监听
//     */
//    private BDLocationListener mBDLocationListener;
/*
    *//**
     * 首次打开天气界面
     *//*
    private boolean mIsFirstUse = false;*/

    /**
     * 是否立刻刷新
     */
    private boolean mIsPromptRefresh = true;

    /**
     * 是否自动定位过
     */
    private boolean mIsLocated = false;

    /**
     * 天气界面布局
     */
    private ViewGroup mWeatherGroup;

    /**
     * 加载中进度框
     */
    private ViewGroup mProgressBar;

    private OnVisibleListener mOnVisibleListener;

    /**
     * 默认城市名
     */
    private String mDefaultCityName;

    /**
     * 默认城市天气代号
     */
   // private String mDefaultCityWeatherCode;
    /**
     * 是否只是定位
     */
    private boolean mIsOnlyLocation;
    private LocationService locationService;
    private FancyCoverFlow mfancyCoverFlow;
    private MyFancyCoverFlowAdapter mMyFancyCoverFlowAdapter;
    private TextView mLuck;

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void initInjector() {
        //   onlyLocation();
        mHandler = new Handler();
        mOnVisibleListener = new OnVisibleListener() {
            @Override
            public void onVisible() {
                //初始化界面,隐藏viewstu吧、
                viewstubAide.inflate();
                init(weaBackground);
                mCityName = getDefaultCityName();
                // 不是第一次加载天气界面
                if (mCityName != null) {
                    // 初始化天气
                   // mIsPrepared = true;
                    try {
                        showWeatherMessage(WeatherUtils.readWeatherInfo(getActivity(), mCityName));
                    } catch (Exception e) {
                    }
                    showWeatherLayout();
                   // pullToRefresh(1000);
                    refreshWeather();
                } else {
                    //第一次加载天气界面
                    showWeatherLayout();
                    // 首次进入天气界面，自动定位天气
                    //  mIsFirstUse = true;
                    // 不立刻刷新
                    mIsPromptRefresh = false;
                    // 自动定位
                    startLocation();
                }

            }
        };

    }

    /**
     * 显示天气界面
     */
    private void showWeatherLayout() {
        // 隐藏加载进度框
        mProgressBar.setVisibility(View.GONE);
        // 首次进入天气界面显示初始布局
        mWeatherGroup.setVisibility(View.VISIBLE);
    }

    private void onlyLocation() {
        // 初次使用天气，首先需要定位以便能够正确显示天气提示内容
        if (getDefaultCityName() == null) {
            mIsOnlyLocation = true;
//            mIsFirstUse = true;
            startLocation();
        }
    }

    /**
     * 初始化控件
     *
     * @param view view
     */
    private void init(final View view) {

        mWeatherGroup = (ViewGroup) view.findViewById(R.id.weather_layout);
        mProgressBar = (ViewGroup) view.findViewById(R.id.progress_bar_llyt);

        mRefreshBtn = (ImageView) view.findViewById(R.id.action_refresh);
        mRefreshBtn.setOnClickListener(this);
        // HOME按钮
        ImageView homeBtn = (ImageView) view.findViewById(R.id.action_home);
        homeBtn.setOnClickListener(this);

        mCityNameTv = (TextView) view.findViewById(R.id.action_title);
        mAlarmTv = (TextView) view.findViewById(R.id.alarm);
        mUpdateTimeTv = (TextView) view.findViewById(R.id.update_time);

        mTemperature1Iv = (ImageView) view.findViewById(R.id.temperature1);
        mTemperature2Iv = (ImageView) view.findViewById(R.id.temperature2);
        mTemperature3Iv = (ImageView) view.findViewById(R.id.temperature3);
        mWeatherTypeTv = (TextView) view.findViewById(R.id.weather_type);

        mAqiTv = (TextView) view.findViewById(R.id.aqi);
        mHumidityTv = (TextView) view.findViewById(R.id.humidity);
        mWindTv = (TextView) view.findViewById(R.id.wind);

        mWeatherTypeIvToday = (ImageView) view.findViewById(R.id.weather_type_iv_today);
        mWeatherTypeIvTomorrow = (ImageView) view.findViewById(R.id.weather_type_iv_tomorrow);
        mWeatherTypeIvDayAfterTomorrow = (ImageView) view.findViewById(R.id.weather_type_iv_day_after_tomorrow);

        mTempHighTvToday = (TextView) view.findViewById(R.id.temp_high_today);
        mTempHighTvTomorrow = (TextView) view.findViewById(R.id.temp_high_tomorrow);
        mTempHighTvDayAfterTomorrow = (TextView) view.findViewById(R.id.temp_high_day_after_tomorrow);

        mTempLowTvToday = (TextView) view.findViewById(R.id.temp_low_today);
        mTempLowTvTomorrow = (TextView) view.findViewById(R.id.temp_low_tomorrow);
        mTempLowTvDayAfterTomorrow = (TextView) view.findViewById(R.id.temp_low_day_after_tomorrow);

        mWeatherTypeTvToday = (TextView) view.findViewById(R.id.weather_type_tv_today);
        mWeatherTypeTvTomorrow = (TextView) view.findViewById(R.id.weather_type_tv_tomorrow);
        mWeatherTypeTvDayAfterTomorrow = (TextView) view.findViewById(R.id.weather_type_tv_day_after_tomorrow);

        mDaysForecastTvWeek1 = (TextView) view.findViewById(R.id.wea_days_forecast_week1);
        mDaysForecastTvWeek2 = (TextView) view.findViewById(R.id.wea_days_forecast_week2);
        mDaysForecastTvWeek3 = (TextView) view.findViewById(R.id.wea_days_forecast_week3);
        mDaysForecastTvWeek4 = (TextView) view.findViewById(R.id.wea_days_forecast_week4);
        mDaysForecastTvWeek5 = (TextView) view.findViewById(R.id.wea_days_forecast_week5);
        mDaysForecastTvWeek6 = (TextView) view.findViewById(R.id.wea_days_forecast_week6);

        mDaysForecastTvDay1 = (TextView) view.findViewById(R.id.wea_days_forecast_day1);
        mDaysForecastTvDay2 = (TextView) view.findViewById(R.id.wea_days_forecast_day2);
        mDaysForecastTvDay3 = (TextView) view.findViewById(R.id.wea_days_forecast_day3);
        mDaysForecastTvDay4 = (TextView) view.findViewById(R.id.wea_days_forecast_day4);
        mDaysForecastTvDay5 = (TextView) view.findViewById(R.id.wea_days_forecast_day5);
        mDaysForecastTvDay6 = (TextView) view.findViewById(R.id.wea_days_forecast_day6);

        mDaysForecastWeaTypeDayIv1 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv1);
        mDaysForecastWeaTypeDayIv2 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv2);
        mDaysForecastWeaTypeDayIv3 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv3);
        mDaysForecastWeaTypeDayIv4 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv4);
        mDaysForecastWeaTypeDayIv5 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv5);
        mDaysForecastWeaTypeDayIv6 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv6);

        mDaysForecastWeaTypeDayTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv1);
        mDaysForecastWeaTypeDayTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv2);
        mDaysForecastWeaTypeDayTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv3);
        mDaysForecastWeaTypeDayTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv4);
        mDaysForecastWeaTypeDayTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv5);
        mDaysForecastWeaTypeDayTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv6);

        mCharView = (LineChartViewDouble) view.findViewById(R.id.line_char);

        mDaysForecastWeaTypeNightIv1 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv1);
        mDaysForecastWeaTypeNightIv2 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv2);
        mDaysForecastWeaTypeNightIv3 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv3);
        mDaysForecastWeaTypeNightIv4 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv4);
        mDaysForecastWeaTypeNightIv5 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv5);
        mDaysForecastWeaTypeNightIv6 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv6);

        mDaysForecastWeaTypeNightTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv1);
        mDaysForecastWeaTypeNightTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv2);
        mDaysForecastWeaTypeNightTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv3);
        mDaysForecastWeaTypeNightTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv4);
        mDaysForecastWeaTypeNightTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv5);
        mDaysForecastWeaTypeNightTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv6);

        mDaysForecastWindDirectionTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv1);
        mDaysForecastWindDirectionTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv2);
        mDaysForecastWindDirectionTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv3);
        mDaysForecastWindDirectionTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv4);
        mDaysForecastWindDirectionTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv5);
        mDaysForecastWindDirectionTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv6);

        mDaysForecastWindPowerTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv1);
        mDaysForecastWindPowerTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv2);
        mDaysForecastWindPowerTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv3);
        mDaysForecastWindPowerTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv4);
        mDaysForecastWindPowerTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv5);
        mDaysForecastWindPowerTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv6);

        mLifeIndexUmbrellaTv = (TextView) view.findViewById(R.id.wea_life_index_tv_umbrella);
        mLifeIndexUltravioletRaysTv = (TextView) view.findViewById(R.id.wea_life_index_tv_ultraviolet_rays);
        mLifeIndexDressTv = (TextView) view.findViewById(R.id.wea_life_tv_index_dress);
        mLifeIndexColdTv = (TextView) view.findViewById(R.id.wea_life_index_tv_cold);
        mLifeIndexMorningExerciseTv = (TextView) view.findViewById(R.id.wea_life_index_tv_morning_exercise);
        mLifeIndexSportTv = (TextView) view.findViewById(R.id.wea_life_index_tv_sport);
        mLifeIndexCarWashTv = (TextView) view.findViewById(R.id.wea_life_index_tv_car_wash);
        mLifeIndexAirCureTv = (TextView) view.findViewById(R.id.wea_life_index_tv_air_cure);

        // 雨伞指数控件
        RelativeLayout lifeIndexUmbrellaRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_umbrella);
        // 紫外线指数控件
        RelativeLayout lifeIndexUltravioletRaysRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_ultraviolet_rays);
        // 穿衣指数控件
        RelativeLayout lifeIndexDressRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_dress);
        // 感冒指数控件
        RelativeLayout lifeIndexColdRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_cold);
        // 晨练指数控件
        RelativeLayout lifeIndexMorningExerciseRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_morning_exercise);
        //  运动指数控件
        RelativeLayout lifeIndexSportRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_sport);
        // 洗车指数控件
        RelativeLayout lifeIndexCarWashRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_carwash);
        // 晾晒指数控件
        RelativeLayout lifeIndexAirCureRlyt = (RelativeLayout) view.findViewById(R.id.wea_life_index_rlyt_air_cure);

        //星座
        mfancyCoverFlow=(FancyCoverFlow) view.findViewById(R.id.fancyCoverFlow);
        mLuck=(TextView)view.findViewById(R.id.today_luck_tv);

        mMyFancyCoverFlowAdapter = new MyFancyCoverFlowAdapter(getActivity());
        mfancyCoverFlow.setAdapter(mMyFancyCoverFlowAdapter);
        mMyFancyCoverFlowAdapter.notifyDataSetChanged();
        mfancyCoverFlow.setUnselectedAlpha(0.5f);//通明度
        mfancyCoverFlow.setUnselectedSaturation(0.5f);//设置选中的饱和度
        mfancyCoverFlow.setUnselectedScale(0.3f);//设置选中的规模
        mfancyCoverFlow.setSpacing(0);//设置间距
        mfancyCoverFlow.setMaxRotation(0);//设置最大旋转
        mfancyCoverFlow.setScaleDownGravity(0.5f);
        mfancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
//        int num = Integer.MAX_VALUE / 2 % mMyFancyCoverFlowAdapter.getCount();
//        int selectPosition = Integer.MAX_VALUE / 2 - num;
        Random rand = new Random();
        int selectPosition=rand.nextInt(12);
        mfancyCoverFlow.setSelection(selectPosition);
        mfancyCoverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FancyCoverItem homeFancyCoverFlow = (FancyCoverItem) mfancyCoverFlow.getSelectedItem();
                if (homeFancyCoverFlow != null) {
                        String  name =homeFancyCoverFlow.getName();
                    ((WeatherFragmentPresenter) ( mPresenter)).getConstellation(getActivity(),name);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lifeIndexUmbrellaRlyt.setOnClickListener(this);
        lifeIndexUltravioletRaysRlyt.setOnClickListener(this);
        lifeIndexDressRlyt.setOnClickListener(this);
        lifeIndexColdRlyt.setOnClickListener(this);
        lifeIndexMorningExerciseRlyt.setOnClickListener(this);
        lifeIndexSportRlyt.setOnClickListener(this);
        lifeIndexCarWashRlyt.setOnClickListener(this);
        lifeIndexAirCureRlyt.setOnClickListener(this);

        mDensity = getResources().getDisplayMetrics().density;
        mBlurDrawable = WallpaperUtils.getWallPaperBlurDrawable(getActivity());
        //  mBackGround = (LinearLayout) view.findViewById(R.id.wea_background);

        mPullRefreshScrollView = (PullToRefreshScrollView) view
                .findViewById(R.id.pull_refresh_scrollview);
        // 设置下拉刷新
        setPullToRefresh();
        mPullRefreshScrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
//                LogUtil.i(LOG_TAG, "x: " + x + "y: " + y + "oldx: " + oldx + "oldy: " + oldy);
                // scroll最大滚动距离（xxxh：2320）/密度（xxxh：3）/1.5  =  515
                mAlpha = Math.round(Math.round(y / mDensity / 1.5));
                if (mAlpha > 255) {
                    mAlpha = 255;
                } else if (mAlpha < 0) {
                    mAlpha = 0;
                }
                // 设置模糊处理后drawable的透明度
                mBlurDrawable.setAlpha(mAlpha);
                // 设置背景
                //noinspection deprecation
                view.setBackgroundDrawable(mBlurDrawable);
            }
        });
    }

    /**
     * 设置下拉刷新
     */

    private void setPullToRefresh() {
        mPullRefreshScrollView.getLoadingLayoutProxy().setPullLabel(getString(R.string.pull_to_refresh));
        mPullRefreshScrollView.getLoadingLayoutProxy().setRefreshingLabel(
                getString(R.string.refreshing));
        mPullRefreshScrollView.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.leave_to_refresh));
        mPullRefreshScrollView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        locationOrRefresh();
                    }
                });

    }


    /**
     * 定位或者直接刷新
     */
    private void locationOrRefresh() {
        // 判断网络是否可用
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            dissDialog();
            ToastUtil.showShortToast(getActivity(), getString(R.string.internet_error));
            return;
        }

        // 不是从自动定位返回
        if (!mIsLocated && TextUtils.isEmpty(mCityName)) {
            locationPromptRefresh();
        } else {
            mIsLocated = false;
            refreshWeather();
        }
    }

    /**
     * 定位并立即刷新
     */
    private void locationPromptRefresh() {
//        mIsFirstUse = false;
        mIsPromptRefresh = true;
        startLocation();
    }

    @Override
    protected void initEventAndData() {


    }

    @Override
    protected void lazyLoadData() {
        //显示出界面来后调用的方法
        if (!mIsPrepared) {
            if (mOnVisibleListener != null) {
                mOnVisibleListener.onVisible();
            }
        }
        if (mIsPrepared) {
            pullToRefresh(1000);
        }

    }

    @Override
    public BasePresenter getPresenter() {
        return new WeatherFragmentPresenter();
    }


    @Override
    public void showLoadProgressDialog(String str) {
        pullToRefresh(10);
    }

    @Override
    public void dissDialog() {
        // 停止正在刷新动画
        mPullRefreshScrollView.onRefreshComplete();
        // 取消刷新按钮的动画
        mRefreshBtn.clearAnimation();
        // 最近一次更细时间
        mLastActiveUpdateTime = SystemClock.elapsedRealtime();

    }

    @Override
    public void onClick(View v) {
        if (ButtonUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_refresh:
                //刷新
                Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                // 匀速
                LinearInterpolator lin = new LinearInterpolator();
                // 设置速率
                operatingAnim.setInterpolator(lin);
                mRefreshBtn.startAnimation(operatingAnim);
                locationOrRefresh();
                break;
            case R.id.action_home:
                //城市管理
                // 保存默认城市名，判断滑动返回前是否更改了默认城市
                mDefaultCityName = getDefaultCityName();

                // 当不是天气界面并且已经开始延迟刷新天气线程
                if (mHandler != null && mIsPostDelayed) {
                    // 取消线程
                    mHandler.removeCallbacks(mRun);
                    mIsPostDelayed = false;
                }
                // 当正在刷新
                if (mPullRefreshScrollView.isRefreshing()) {
                    // 停止刷新
                    mPullRefreshScrollView.onRefreshComplete();
                }
                // 停止刷新动画
                mRefreshBtn.clearAnimation();

                Intent intent1 = new Intent(getActivity(), CityManageActivity.class);

                intent1.putExtra(Constants.CITY_NAME, mCityName);
                startActivityForResult(intent1, REQUEST_WEA);
                break;
        }

    }

    /**
     * 取得默认城市名
     *
     * @return 默认城市名
     */
    private String getDefaultCityName() {

        return SharedPreferencesUtils.getString(getActivity(), Constants.DEFAULT_CITY_NAME, null);
    }
/*
    *//**
     * 取得默认天气代号
     *
     * @return 默认天气代号
     *//*
    private String getDefaultWeatherCode() {
        return SharedPreferencesUtils.getString(getActivity(), Constants.DEFAULT_WEATHER_CODE, getString(R.string.auto_location));

    }*/

    /**
     * 开始定位
     */
    private void startLocation() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            ToastUtil.showShortToast(getActivity(), getString(R.string.internet_error));
            mIsOnlyLocation = false;
            return;
        }

        // 初始化定位管理，监听
        initLocationManager();

    }

    /**
     * 初始化定位管理监听
     */
    private void initLocationManager() {

        locationService = ((AideApplication) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                locationService.stop();
                locationService.unregisterListener(mListener);
                //定位成功
                if (161 == location.getLocType()) {
                    String cityName = location.getCity();
                    if (cityName != null) {
                        mCityName = cityName;
                        // 初次加载定位保存
                        if (getDefaultCityName() == null) {
                            SharedPreferencesUtils.saveString(getActivity(), Constants.DEFAULT_CITY_NAME, mCityName);
                          //  SharedPreferencesUtils.saveString(getActivity(), Constants.DEFAULT_WEATHER_CODE, mCityWeatherCode);
                        }
                        if (mIsOnlyLocation) {
                            mIsOnlyLocation = false;
                            return;
                        }
                        // 立刻更新
                        if (mIsPromptRefresh) {
                            refreshWeather();
                        } else {
                            mIsPromptRefresh = true;
                            mIsLocated = true;
                            pullToRefresh(1000);
                        }
                    } else {
                        mIsOnlyLocation = false;
                        dissDialog();
                        ToastUtil.showShortToast(getActivity(), getString(R.string.can_not_find_current_location));
                    }

                } else {
                    dissDialog();
                    ToastUtil.showShortToast(getActivity(), getString(R.string.location_fail));
                    mIsOnlyLocation = false;
                    return;
                }
            } else {
                mIsOnlyLocation = false;
                dissDialog();
                ToastUtil.showShortToast(getActivity(), getString(R.string.auto_location_error_retry));
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    /**
     * 调用接口刷新天气
     */
    private void refreshWeather() {
        ((WeatherFragmentPresenter) mPresenter).refreshWeather(getActivity(), mCityName);
    }


    /**
     * 下拉刷新
     */
    private void pullToRefresh(long time) {

        mRun = new Runnable() {
            @Override
            public void run() {
                try {
                    mIsPostDelayed = false;
                    if (!getActivity().isFinishing()) {
                        if (!hasActiveUpdated()) {
                            mPullRefreshScrollView.setRefreshing();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mHandler.postDelayed(mRun, time);
        mIsPostDelayed = true;
    }


    /**
     * 是否3秒内主动更新过
     *
     * @return 主动更新与否
     */
    private boolean hasActiveUpdated() {
        if (mLastActiveUpdateTime == 0) {
            return false;
        }
        long now = SystemClock.elapsedRealtime();
        long timeD = now - mLastActiveUpdateTime;
        // 间隔3秒内不再自动更新
        return timeD <= 3000;
    }

    @Override
    public void showWeatherMessage(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            return;
        }
        // 多天预报信息
        List<WeatherDaysForecast> weatherDaysForecasts = weatherInfo.getWeatherDaysForecast();

        // 昨天天气信息（23：45开始到05：20以前的数据的日期和周）
        WeatherDaysForecast weather;
        // 昨天天气信息
        WeatherDaysForecast weather1;
        // 今天天气信息
        WeatherDaysForecast weather2;
        // 明天天气信息
        WeatherDaysForecast weather3;
        // 后天天气信息
        WeatherDaysForecast weather4;
        // 第五天天天气信息
        WeatherDaysForecast weather5;
        // 第六天天气信息
        WeatherDaysForecast weather6;

        String time[] = weatherInfo.getUpdateTime().split(":");
        int hour1 = Integer.parseInt(time[0]);
        int minute1 = Integer.parseInt(time[1]);
        //更新时间从23：45开始到05：20以前的数据，后移一天填充
        if ((hour1 == 23 && minute1 >= 45) || (hour1 < 5) ||
                ((hour1 == 5) && (minute1 < 20))) {
            if (weatherDaysForecasts.size() >= 6) {
                weather = weatherDaysForecasts.get(0);
                weather1 = weatherDaysForecasts.get(1);
                weather2 = weatherDaysForecasts.get(2);
                weather3 = weatherDaysForecasts.get(3);
                weather4 = weatherDaysForecasts.get(4);
                weather5 = weatherDaysForecasts.get(5);
                weather6 = weatherDaysForecasts.get(5);
            } else {
                weather = null;
                weather1 = weatherDaysForecasts.get(0);
                weather2 = weatherDaysForecasts.get(1);
                weather3 = weatherDaysForecasts.get(2);
                weather4 = weatherDaysForecasts.get(3);
                weather5 = weatherDaysForecasts.get(4);
                weather6 = weatherDaysForecasts.get(4);

            }
        } else {
            if (weatherDaysForecasts.size() >= 6) {
                weather = weatherDaysForecasts.get(0);
                weather1 = weatherDaysForecasts.get(0);
                weather2 = weatherDaysForecasts.get(1);
                weather3 = weatherDaysForecasts.get(2);
                weather4 = weatherDaysForecasts.get(3);
                weather5 = weatherDaysForecasts.get(4);
                weather6 = weatherDaysForecasts.get(5);
            } else {
                weather = null;
                weather1 = null;
                weather2 = weatherDaysForecasts.get(0);
                weather3 = weatherDaysForecasts.get(1);
                weather4 = weatherDaysForecasts.get(2);
                weather5 = weatherDaysForecasts.get(3);
                weather6 = weatherDaysForecasts.get(4);
            }
        }

        Calendar calendar = Calendar.getInstance();
        // 现在小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // 设置城市名
        setCityName(weatherInfo);
        // 设置预警信息
        setAlarmInfo(weatherInfo);
        // 设置更新时间
        setUpdateTime(weatherInfo);
        // 设置温度
        setTemperature(weatherInfo);
        // 设置天气类型
        setWeatherType(weather2, hour);
        // 设置aqi
        setAQI(weatherInfo);
        // 设置湿度
        setHumidity(weatherInfo);
        // 设置风向、风力
        setWind(weatherInfo);
        // 设置今天，明天，后天大概天气
        setThreeDaysWeather(weather2, weather3, weather4, hour);

        // 设置多天天气预报
        setDaysForecast(weather, weather1, weather2, weather3, weather4, weather5, weather6,
                hour1, minute1, calendar);

        // 生活指数信息
        setLifeIndex(weatherInfo);

        // 处理城市管理表
        processCityManageTable(weatherInfo, weather2);

    }

    @Override
    public void showToastMessage(String msg) {

        ToastUtil.showShortToast(getActivity(), msg);

    }

    @Override
    public void showConstellation(ConstellationBean bean) {
        mLuck.setText("\t\t"+bean.getSummary());
    }

    /**
     * 处理城市管理表
     */
    private void processCityManageTable(WeatherInfo weatherInfo, WeatherDaysForecast weather2) {
//        String cityName;
        // 自动定位
//        if (getString(R.string.auto_location).equals(mCityWeatherCode)) {
//            cityName = mCityWeatherCode;
//        } else {
       //     cityName = weatherInfo.getCity();
//        }

        CityManage cityManage = new CityManage();
        cityManage.setTempHigh(weather2.getHigh().substring(3));
        cityManage.setTempLow(weather2.getLow().substring(3));
        cityManage.setWeatherType(WeatherUtils.getWeatherType
                (getActivity(), weather2.getTypeDay(), weather2.getTypeNight()));
        cityManage.setWeatherTypeDay(weather2.getTypeDay());
        cityManage.setWeatherTypeNight(weather2.getTypeNight());
        int num = WeatherDBOperate.getInstance().queryCityManage(mCityName);
        // CityManage表中存在此城市时
        if (1 == num) {
            // 修改城市管理item信息
            WeatherDBOperate.getInstance().updateCityManage(cityManage, mCityName);
        } else if (0 == num) {
            // 城市管理表不存在定位城市
            setCityManage(cityManage);
            // 存储城市管理表
            boolean result = WeatherDBOperate.getInstance().saveCityManage(cityManage);
            // 城市管理表城市个数
            int total = WeatherDBOperate.getInstance().queryCityManage();
            // 存储成功
            if (result && total <= 1) {
                SharedPreferencesUtils.saveString(getActivity(), Constants.DEFAULT_CITY, mCityName);
            } // 城市管理表存在定位
        }

    /*    int number = WeatherDBOperate.getInstance().queryCityManage(mCityName);
      //  if (mCityWeatherCode.equals(getString(R.string.auto_location))) {
            // 城市管理表不存在定位
            if (number == 0) {
                setCityManage(cityManage);
                // 存储城市管理表
                boolean result = WeatherDBOperate.getInstance().saveCityManage(cityManage);
                // 城市管理表城市个数
                int total = WeatherDBOperate.getInstance().queryCityManage();
                // 存储成功
                if (result && total <= 1) {
                    SharedPreferencesUtils.saveString(getActivity(),Constants.DEFAULT_CITY, mCityName);
                } // 城市管理表存在定位
            } else {
                int number1 = WeatherDBOperate.getInstance().queryCityManageLocationCity(mCityName);
                // 定位城市发生变更
                if (number1 == 0) {
                    setCityManage(cityManage);

                    // 更新城市管理表
                    WeatherDBOperate.getInstance().updateCityManage(cityManage, mCityName);

                    // 默认城市是自动定位
                    if (getDefaultWeatherCode().equals(mCityWeatherCode)) {
                        SharedPreferencesUtils.saveString(getActivity(),Constants.DEFAULT_CITY_NAME, mCityName);
                    }
                }
            }*/
    }
//    }

    private void setCityManage(CityManage cityManage) {
        cityManage.setCityName(mCityName);
       // cityManage.setWeatherCode(mCityWeatherCode);
        cityManage.setLocationCity(mCityName);
    }

    /**
     * 设置多天天气预报
     */
    private void setDaysForecast(WeatherDaysForecast weather, WeatherDaysForecast weather1,
                                 WeatherDaysForecast weather2, WeatherDaysForecast weather3,
                                 WeatherDaysForecast weather4, WeatherDaysForecast weather5,
                                 WeatherDaysForecast weather6, int hour1, int minute1,
                                 Calendar calendar) {
        // 日期和星期标题 【索引0：日期;索引1：星期】
        String[] day1;
        String[] day2;
        String[] day3;
        String[] day4;
        String[] day5;
        String[] day6;
        if ((hour1 == 23 && minute1 >= 45) || (hour1 < 5) || ((hour1 == 5) && (minute1 < 20))) {
            if (weather != null) {
                day1 = getDay(weather.getDate());
            } else {
                day1 = null;
            }

            assert weather1 != null;
            day2 = getDay(weather1.getDate());
            day3 = getDay(weather2.getDate());
            day4 = getDay(weather3.getDate());
            day5 = getDay(weather4.getDate());
            day6 = getDay(weather5.getDate());
        } else {
            if (weather1 != null) {
                day1 = getDay(weather1.getDate());
            } else {
                day1 = null;
            }

            day2 = getDay(weather2.getDate());
            day3 = getDay(weather3.getDate());
            day4 = getDay(weather4.getDate());
            day5 = getDay(weather5.getDate());
            day6 = getDay(weather6.getDate());
        }

        // 设置标题星期
        mDaysForecastTvWeek1.setText(getString(R.string.yesterday));
        mDaysForecastTvWeek2.setText(getString(R.string.today));
        mDaysForecastTvWeek3.setText(getWeek(day3[1]));
        mDaysForecastTvWeek4.setText(getWeek(day4[1]));
        mDaysForecastTvWeek5.setText(getWeek(day5[1]));
        mDaysForecastTvWeek6.setText(getWeek(day6[1]));

        // 月份
        calendar.add(Calendar.DATE, -1);
        String month1 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.DATE, 1);
        String month2 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.DATE, 1);
        String month3 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.DATE, 1);
        String month4 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.DATE, 1);
        String month5 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.DATE, 1);
        String month6 = String.valueOf(calendar.get(Calendar.MONTH) + 1);

        // 日
        String day01;
        if (day1 != null) {
            day01 = day1[0].split("日")[0];
        } else {
            day01 = null;
        }

        String day02 = day2[0].split("日")[0];
        String day03 = day3[0].split("日")[0];
        String day04 = day4[0].split("日")[0];
        String day05 = day5[0].split("日")[0];
        String day06 = day6[0].split("日")[0];

        // 斜杠
        String date = getString(R.string.date);
        // 设置日期
        if (day01 != null) {
            mDaysForecastTvDay1.setText(String.format(date, month1, day01));
        } else {
            mDaysForecastTvDay1.setText(R.string.dash);
        }

        mDaysForecastTvDay2.setText(String.format(date, month2, day02));
        mDaysForecastTvDay3.setText(String.format(date, month3, day03));
        mDaysForecastTvDay4.setText(String.format(date, month4, day04));
        mDaysForecastTvDay5.setText(String.format(date, month5, day05));
        mDaysForecastTvDay6.setText(String.format(date, month6, day06));

        // 取得白天天气类型图片id
        int weatherDayId1;
        if (weather != null) {
            assert weather1 != null;
            weatherDayId1 = WeatherUtils.getWeatherTypeImageID(weather1.getTypeDay(), true);
        } else {
            weatherDayId1 = R.drawable.ic_weather_no;
        }
        int weatherDayId2 = WeatherUtils.getWeatherTypeImageID(weather2.getTypeDay(), true);
        int weatherDayId3 = WeatherUtils.getWeatherTypeImageID(weather3.getTypeDay(), true);
        int weatherDayId4 = WeatherUtils.getWeatherTypeImageID(weather4.getTypeDay(), true);
        int weatherDayId5 = WeatherUtils.getWeatherTypeImageID(weather5.getTypeDay(), true);
        int weatherDayId6 = WeatherUtils.getWeatherTypeImageID(weather6.getTypeDay(), true);

        //设置白天天气类型图片
        mDaysForecastWeaTypeDayIv1.setImageResource(weatherDayId1);
        mDaysForecastWeaTypeDayIv2.setImageResource(weatherDayId2);
        mDaysForecastWeaTypeDayIv3.setImageResource(weatherDayId3);
        mDaysForecastWeaTypeDayIv4.setImageResource(weatherDayId4);
        mDaysForecastWeaTypeDayIv5.setImageResource(weatherDayId5);
        mDaysForecastWeaTypeDayIv6.setImageResource(weatherDayId6);

        // 设置白天天气类型文字
        if (weather != null) {
            mDaysForecastWeaTypeDayTv1.setText(weather1.getTypeDay());
        } else {
            mDaysForecastWeaTypeDayTv1.setText(R.string.dash);
        }

        mDaysForecastWeaTypeDayTv2.setText(weather2.getTypeDay());
        mDaysForecastWeaTypeDayTv3.setText(weather3.getTypeDay());
        mDaysForecastWeaTypeDayTv4.setText(weather4.getTypeDay());
        mDaysForecastWeaTypeDayTv5.setText(weather5.getTypeDay());
        mDaysForecastWeaTypeDayTv6.setText(weather6.getTypeDay());

        // 设置白天温度曲线
        if (weather != null) {
            mCharView.setTempDay(new int[]{getTemp(weather1.getHigh()),
                    getTemp(weather2.getHigh()), getTemp(weather3.getHigh()),
                    getTemp(weather4.getHigh()), getTemp(weather5.getHigh()),
                    getTemp(weather6.getHigh())});
        } else {
            mCharView.setTempDay(new int[]{-1000,
                    getTemp(weather2.getHigh()), getTemp(weather3.getHigh()),
                    getTemp(weather4.getHigh()), getTemp(weather5.getHigh()),
                    getTemp(weather6.getHigh())});
        }
        // 设置夜间温度曲线
        if (weather != null) {
            mCharView.setTempNight(new int[]{getTemp(weather1.getLow()),
                    getTemp(weather2.getLow()), getTemp(weather3.getLow()),
                    getTemp(weather4.getLow()), getTemp(weather5.getLow()),
                    getTemp(weather6.getLow())});
        } else {
            mCharView.setTempNight(new int[]{-1000,
                    getTemp(weather2.getLow()), getTemp(weather3.getLow()),
                    getTemp(weather4.getLow()), getTemp(weather5.getLow()),
                    getTemp(weather6.getLow())});
        }
        mCharView.invalidate();

        // 设置夜间天气类型文字
        if (weather != null) {
            mDaysForecastWeaTypeNightTv1.setText(weather1.getTypeNight());
        } else {
            mDaysForecastWeaTypeNightTv1.setText(R.string.dash);
        }
        mDaysForecastWeaTypeNightTv2.setText(weather2.getTypeNight());
        mDaysForecastWeaTypeNightTv3.setText(weather3.getTypeNight());
        mDaysForecastWeaTypeNightTv4.setText(weather4.getTypeNight());
        mDaysForecastWeaTypeNightTv5.setText(weather5.getTypeNight());
        mDaysForecastWeaTypeNightTv6.setText(weather6.getTypeNight());

        // 取得夜间天气类型图片id
        int weatherNightId1;
        if (weather != null) {
            weatherNightId1 = WeatherUtils.getWeatherTypeImageID(weather1.getTypeNight(), false);
        } else {
            weatherNightId1 = R.drawable.ic_weather_no;
        }
        int weatherNightId2 = WeatherUtils.getWeatherTypeImageID(weather2.getTypeNight(), false);
        int weatherNightId3 = WeatherUtils.getWeatherTypeImageID(weather3.getTypeNight(), false);
        int weatherNightId4 = WeatherUtils.getWeatherTypeImageID(weather4.getTypeNight(), false);
        int weatherNightId5 = WeatherUtils.getWeatherTypeImageID(weather5.getTypeNight(), false);
        int weatherNightId6 = WeatherUtils.getWeatherTypeImageID(weather6.getTypeNight(), false);

        //设置夜间天气类型图片
        mDaysForecastWeaTypeNightIv1.setImageResource(weatherNightId1);
        mDaysForecastWeaTypeNightIv2.setImageResource(weatherNightId2);
        mDaysForecastWeaTypeNightIv3.setImageResource(weatherNightId3);
        mDaysForecastWeaTypeNightIv4.setImageResource(weatherNightId4);
        mDaysForecastWeaTypeNightIv5.setImageResource(weatherNightId5);
        mDaysForecastWeaTypeNightIv6.setImageResource(weatherNightId6);

        // 设置风向
        if (weather != null) {
            mDaysForecastWindDirectionTv1.setText(weather1.getWindDirectionDay());
        } else {
            mDaysForecastWindDirectionTv1.setText(R.string.dash);
        }
        mDaysForecastWindDirectionTv2.setText(weather2.getWindDirectionDay());
        mDaysForecastWindDirectionTv3.setText(weather3.getWindDirectionDay());
        mDaysForecastWindDirectionTv4.setText(weather4.getWindDirectionDay());
        mDaysForecastWindDirectionTv5.setText(weather5.getWindDirectionDay());
        mDaysForecastWindDirectionTv6.setText(weather6.getWindDirectionDay());

        // 设置风力
        if (weather != null) {
            mDaysForecastWindPowerTv1.setText(weather1.getWindPowerDay());
        } else {
            mDaysForecastWindPowerTv1.setText(R.string.dash);
        }
        mDaysForecastWindPowerTv2.setText(weather2.getWindPowerDay());
        mDaysForecastWindPowerTv3.setText(weather3.getWindPowerDay());
        mDaysForecastWindPowerTv4.setText(weather4.getWindPowerDay());
        mDaysForecastWindPowerTv5.setText(weather5.getWindPowerDay());
        mDaysForecastWindPowerTv6.setText(weather6.getWindPowerDay());
    }

    /**
     * 取得温度
     *
     * @param temp 温度信息
     * @return 温度
     */
    private int getTemp(String temp) {
        String temperature;
        if (!temp.contains("-")) {
            if (temp.length() == 6) {
                temperature = temp.substring(3, 5);
            } else {
                temperature = temp.substring(3, 4);
            }
        } else {
            if (temp.length() == 7) {
                temperature = temp.substring(3, 6);
            } else {
                temperature = temp.substring(3, 5);
            }
        }
        return Integer.parseInt(temperature);
    }

    /**
     * 转换周的标题
     *
     * @param week 需要转换的周标题
     * @return 周的标题
     */
    private String getWeek(String week) {
        String week1;
        switch (week) {
            case "星期一":
                week1 = getString(R.string.monday);
                break;
            case "星期二":
                week1 = getString(R.string.tuesday);
                break;
            case "星期三":
                week1 = getString(R.string.wednesday);
                break;
            case "星期四":
                week1 = getString(R.string.thursday);
                break;
            case "星期五":
                week1 = getString(R.string.friday);
                break;
            case "星期六":
                week1 = getString(R.string.saturday);
                break;
            case "星期天":
            case "星期日":
                week1 = getString(R.string.sunday);
                break;
            default:
                week1 = week;
                break;
        }
        return week1;
    }

    /**
     * 截取日期和星期
     *
     * @param date 日期信息
     * @return 包含日期和星期的数组
     */
    private String[] getDay(String date) {
        String[] date1 = new String[2];
        if (date.length() == 5) {
            date1[0] = date.substring(0, 2);
            date1[1] = date.substring(2);
        } else {
            date1[0] = date.substring(0, 3);
            date1[1] = date.substring(3);
        }
        return date1;
    }

    /**
     * 生活指数信息
     */
    private void setLifeIndex(WeatherInfo weatherInfo) {
        List<WeatherLifeIndex> weatherLifeIndexes = weatherInfo.getWeatherLifeIndex();
        // 设置生活指数
        for (WeatherLifeIndex index : weatherLifeIndexes) {
            setLifeIndex(index);
        }
    }

    /**
     * 设置生活指数
     *
     * @param index 生活指数信息
     */

    private void setLifeIndex(WeatherLifeIndex index) {
        switch (index.getIndexName()) {
            case "雨伞指数":
                mLifeIndexUmbrellaTv.setText(index.getIndexValue());
                mLifeIndexUmbrellaDetail = index.getIndexDetail();
                break;
            case "紫外线强度":
                mLifeIndexUltravioletRaysTv.setText(index.getIndexValue());
                mLifeIndexUltravioletRaysDetail = index.getIndexDetail();
                break;
            case "穿衣指数":
                mLifeIndexDressTv.setText(index.getIndexValue());
                mLifeIndexDressDetail = index.getIndexDetail();
                break;
            case "感冒指数":
                mLifeIndexColdTv.setText(index.getIndexValue());
                mLifeIndexColdDetail = index.getIndexDetail();
                break;
            case "晨练指数":
                mLifeIndexMorningExerciseTv.setText(index.getIndexValue());
                mLifeIndexMorningExerciseDetail = index.getIndexDetail();
                break;
            case "运动指数":
                mLifeIndexSportTv.setText(index.getIndexValue());
                mLifeIndexSportDetail = index.getIndexDetail();
                break;
            case "洗车指数":
                mLifeIndexCarWashTv.setText(index.getIndexValue());
                mLifeIndexCarWashDetail = index.getIndexDetail();
                break;
            case "晾晒指数":
                mLifeIndexAirCureTv.setText(index.getIndexValue());
                mLifeIndexAirCureDetail = index.getIndexDetail();
                break;

        }
    }

    /**
     * 设置今天，明天，后天大概天气
     *
     * @param weather2 今天
     * @param weather3 明天
     * @param weather4 后天
     * @param hour     当前小时
     */
    private void setThreeDaysWeather(WeatherDaysForecast weather2, WeatherDaysForecast weather3,
                                     WeatherDaysForecast weather4, int hour) {
        // 天气类型图片id
        int weatherId;

        // 设置今天天气信息
        // 当前为凌晨
        if (hour >= 0 && hour < 6) {
            weatherId = WeatherUtils.getWeatherTypeImageID(weather2.getTypeDay(), false);
            // 当前为白天时
        } else if (hour >= 6 && hour < 18) {
            weatherId = WeatherUtils.getWeatherTypeImageID(weather2.getTypeDay(), true);
            // 当前为夜间
        } else {
            weatherId = WeatherUtils.getWeatherTypeImageID(weather2.getTypeNight(), false);
        }
        mWeatherTypeIvToday.setImageResource(weatherId);
        mTempHighTvToday.setText(weather2.getHigh().substring(3));
        mTempLowTvToday.setText(weather2.getLow().substring(3));
        mWeatherTypeTvToday.setText(WeatherUtils.getWeatherType
                (getActivity(), weather2.getTypeDay(), weather2.getTypeNight()));

        // 设置明天天气信息
        weatherId = WeatherUtils.getWeatherTypeImageID(weather3.getTypeDay(), true);
        mWeatherTypeIvTomorrow.setImageResource(weatherId);
        mTempHighTvTomorrow.setText(weather3.getHigh().substring(3));
        mTempLowTvTomorrow.setText(weather3.getLow().substring(3));
        mWeatherTypeTvTomorrow.setText(WeatherUtils.getWeatherType
                (getActivity(), weather3.getTypeDay(), weather3.getTypeNight()));

        // 设置后天天气信息
        weatherId = WeatherUtils.getWeatherTypeImageID(weather4.getTypeDay(), true);
        mWeatherTypeIvDayAfterTomorrow.setImageResource(weatherId);
        mTempHighTvDayAfterTomorrow.setText(weather4.getHigh().substring(3));
        mTempLowTvDayAfterTomorrow.setText(weather4.getLow().substring(3));
        mWeatherTypeTvDayAfterTomorrow.setText(WeatherUtils.getWeatherType
                (getActivity(), weather4.getTypeDay(), weather4.getTypeNight()));
    }

    /**
     * 设置风向、风力
     */
    private void setWind(WeatherInfo weatherInfo) {
        if (weatherInfo.getWindDirection() != null && weatherInfo.getWindPower() != null) {
            // 设置风向图片
            setImage(mWindTv, getWindImageId(weatherInfo.getWindDirection()));
            // 设置风向、风力
            mWindTv.setText(String.format(getString(R.string.aqi)
                    , weatherInfo.getWindDirection(), weatherInfo.getWindPower()));
        } else {
            setImage(mWindTv, R.drawable.ic_wind);
            mWindTv.setText(R.string.no);
        }
    }

    /**
     * 设置湿度
     */
    private void setHumidity(WeatherInfo weatherInfo) {
        if (weatherInfo.getHumidity() != null) {
            // 设置湿度图片
//            setImage(mHumidityTv, getHumidityImageId(weatherInfo.getHumidity()));
            // 设置湿度
            mHumidityTv.setText(String.format(getString(R.string.humidity),
                    weatherInfo.getHumidity()));
        } else {
//            setImage(mHumidityTv, R.drawable.ic_humidity20);
            mHumidityTv.setText(R.string.no);
        }
    }

    /**
     * 设置aqi
     */
    private void setAQI(WeatherInfo weatherInfo) {
        if (weatherInfo.getQuality() != null && weatherInfo.getAQI() != null) {
            mAqiTv.setVisibility(View.VISIBLE);
            // 设置空气质量图片
            setImage(mAqiTv, getQualityImageId(weatherInfo.getQuality()));
            // 设置空气质量
            mAqiTv.setText(String.format(getString(R.string.aqi),
                    weatherInfo.getQuality(), weatherInfo.getAQI()));
        } else {
            mAqiTv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置天气类型
     *
     * @param weatherToday 今天天气信息
     * @param hour         当前小时
     */
    private void setWeatherType(WeatherDaysForecast weatherToday, int hour) {
        if (hour < 18) {
            // 白天天气
            mWeatherTypeTv.setText(weatherToday.getTypeDay());
        } else {
            // 夜间天气
            mWeatherTypeTv.setText(weatherToday.getTypeNight());
        }
    }

    /**
     * 设置温度
     *
     * @param weatherInfo weatherInfo
     */
    private void setTemperature(WeatherInfo weatherInfo) {
        String temp = weatherInfo.getTemperature();
        mTemperature1Iv.setVisibility(View.VISIBLE);
        mTemperature2Iv.setVisibility(View.VISIBLE);
        mTemperature3Iv.setVisibility(View.VISIBLE);
        if (temp != null) {
            // 两位正数
            if (temp.length() == 2 && !temp.contains("-")) {
                int temp1 = Integer.parseInt(temp.substring(0, 1));
                setTemperatureImage(temp1, mTemperature1Iv);
                int temp2 = Integer.parseInt(temp.substring(1));
                setTemperatureImage(temp2, mTemperature2Iv);
                mTemperature3Iv.setVisibility(View.GONE);
                // 一位
            } else if (temp.length() == 1 && !temp.contains("-")) {
                int temp1 = Integer.parseInt(temp);
                setTemperatureImage(temp1, mTemperature1Iv);
                mTemperature2Iv.setVisibility(View.GONE);
                mTemperature3Iv.setVisibility(View.GONE);
                // 两位负数
            } else if (temp.length() == 2 && temp.contains("-")) {
                mTemperature1Iv.setImageResource(R.drawable.ic_minus);
                int temp2 = Integer.parseInt(temp.substring(1));
                setTemperatureImage(temp2, mTemperature2Iv);
                mTemperature3Iv.setVisibility(View.GONE);
                // 三位负数
            } else if (temp.length() == 3 && temp.contains("-")) {
                mTemperature1Iv.setImageResource(R.drawable.ic_minus);
                int temp2 = Integer.parseInt(temp.substring(1, 2));
                setTemperatureImage(temp2, mTemperature2Iv);
                int temp3 = Integer.parseInt(temp.substring(2));
                setTemperatureImage(temp3, mTemperature3Iv);
            } else {
                mTemperature1Iv.setImageResource(R.drawable.number_0);
                mTemperature2Iv.setImageResource(R.drawable.number_0);
                mTemperature3Iv.setImageResource(R.drawable.number_0);
            }
        } else {
            mTemperature1Iv.setImageResource(R.drawable.number_0);
            mTemperature2Iv.setImageResource(R.drawable.number_0);
            mTemperature3Iv.setImageResource(R.drawable.number_0);
        }
    }

    /**
     * 设置更新时间
     *
     * @param weatherInfo weatherInfo
     */
    @SuppressWarnings("deprecation")
    private void setUpdateTime(WeatherInfo weatherInfo) {
        if (weatherInfo.getUpdateTime() != null) {
            long now = System.currentTimeMillis();
            // 最近一次天气更新时间
            long lastTime = SharedPreferencesUtils.getLong(getActivity(), getString(R.string.city_weather_update_time,
                    weatherInfo.getCity()), 0L);
            // 更新间隔时间（小时）
            long minuteD = (now - lastTime) / 1000 / 60 / 60;
            // 更新时间
            String updateTime;
            if (minuteD < 24) {
                updateTime = String.format(getString(R.string.update_time), weatherInfo.getUpdateTime());
            } else if (minuteD >= 24 && minuteD < 48) {
                updateTime = String.format(getString(R.string.update_time2), weatherInfo.getUpdateTime());
            } else if (minuteD >= 48 && minuteD < 72) {
                updateTime = String.format(getString(R.string.update_time3), weatherInfo.getUpdateTime());
            } else if (minuteD >= 72 && minuteD < 96) {
                updateTime = String.format(getString(R.string.update_time4), 3);
            } else if (minuteD >= 96 && minuteD < 120) {
                updateTime = String.format(getString(R.string.update_time4), 4);
            } else if (minuteD >= 120 && minuteD < 144) {
                updateTime = String.format(getString(R.string.update_time4), 5);
            } else if (minuteD >= 144 && minuteD < 168) {
                updateTime = String.format(getString(R.string.update_time4), 6);
            } else {
                updateTime = getString(R.string.data_void);
            }
            mUpdateTimeTv.setText(updateTime);
            // 当不是数据过期
            if (!updateTime.equals(getString(R.string.data_void))) {
                mUpdateTimeTv.setTextColor(getResources().getColor(R.color.white_trans60));
            } else {
                mUpdateTimeTv.setTextColor(getResources().getColor(R.color.red));
            }
        } else {
            mUpdateTimeTv.setText(getString(R.string.dash));
            mUpdateTimeTv.setTextColor(getResources().getColor(R.color.white_trans60));
        }
    }

    /**
     * 设置城市名
     *
     * @param weatherInfo weatherInfo
     */
    @SuppressWarnings("deprecation")
    private void setCityName(WeatherInfo weatherInfo) {
        if (weatherInfo.getCity() != null) {
            mCityNameTv.setText(weatherInfo.getCity());
            // 不是自动定位
//            if (!getString(R.string.auto_location).equals(mCityWeatherCode)) {
//                mCityNameTv.setCompoundDrawables(null, null, null, null);
//            } else {
//                Drawable drawable = getResources().getDrawable(R.drawable.ic_gps);
//                if (drawable != null) {
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                            drawable.getMinimumHeight());
//                    // 设置图标
//                    mCityNameTv.setCompoundDrawables(drawable, null, null, null);
//                }
//            }
        } else {
            mCityNameTv.setText(getString(R.string.dash));
            mCityNameTv.setCompoundDrawables(null, null, null, null);
        }
    }

    /**
     * 设置预警信息
     *
     * @param weatherInfo weatherInfo
     */
    private void setAlarmInfo(final WeatherInfo weatherInfo) {
        if (weatherInfo.getAlarmType() != null) {
            mAlarmTv.setVisibility(View.VISIBLE);
            mAlarmTv.setText(getString(R.string.alarm, weatherInfo.getAlarmType()));
            mAlarmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 警报详情
                    String detail = weatherInfo.getAlarmDetail();
                    // 替换换行"\r\n"  \\\：转义字符
                    detail = detail.replaceAll("\\\\r\\\\n", "");
                    String format;
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .parse(weatherInfo.getAlarmTime());
                        format = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault()).format(date);
                    } catch (ParseException e) {
                        format = weatherInfo.getAlarmTime();
                    }
                    String time = getString(R.string.release_time, format);

                  /*  Intent intent = new Intent(getActivity(), WeatherAlarmActivity.class);
                    intent.putExtra(WeacConstants.TITLE, getString(R.string.alarm_title,
                            weatherInfo.getAlarmType(), weatherInfo.getAlarmDegree()));
                    intent.putExtra(WeacConstants.DETAIL, detail);
                    intent.putExtra(WeacConstants.TIME, time);
                    startActivity(intent);*/
                }
            });
        } else {
            mAlarmTv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置温度图片
     *
     * @param temp1     温度
     * @param imageView imageView控件
     */
    private void setTemperatureImage(int temp1, ImageView imageView) {
        switch (temp1) {
            case 0:
                imageView.setImageResource(R.drawable.number_0);
                break;
            case 1:
                imageView.setImageResource(R.drawable.number_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.number_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.number_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.number_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.number_5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.number_6);
                break;
            case 7:
                imageView.setImageResource(R.drawable.number_7);
                break;
            case 8:
                imageView.setImageResource(R.drawable.number_8);
                break;
            case 9:
                imageView.setImageResource(R.drawable.number_9);
                break;
            default:
                imageView.setImageResource(R.drawable.number_0);
                break;
        }
    }

    /**
     * 设置左侧图片
     *
     * @param tv      textView
     * @param imageId 图片id
     */
    private void setImage(TextView tv, int imageId) {
        @SuppressWarnings("deprecation") Drawable drawable = getResources().getDrawable(imageId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            // 设置图片
            tv.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 取得aqi图片id
     *
     * @param quality 大气质量
     * @return aqi图片id
     */
    private int getQualityImageId(String quality) {
        int imgId;
        switch (quality) {
            case "优":
                imgId = R.drawable.ic_quality_nice;
                break;
            case "良":
                imgId = R.drawable.ic_quality_good;
                break;
            case "轻度污染":
                imgId = R.drawable.ic_quality_little;
                break;
            case "中度污染":
                imgId = R.drawable.ic_quality_medium;
                break;
            case "重度污染":
                imgId = R.drawable.ic_quality_serious;
                break;
            case "严重污染":
                imgId = R.drawable.ic_quality_terrible;
                break;
            default:
                imgId = R.drawable.ic_quality_nice;
                break;
        }
        return imgId;
    }

    /**
     * 取得风向图片id
     *
     * @param windDirection 风向
     * @return 风向图片id
     */
    private int getWindImageId(String windDirection) {
        int imgId;
        switch (windDirection) {
            case "南风":
                imgId = R.drawable.ic_wind_1;
                break;
            case "西南风":
                imgId = R.drawable.ic_wind_2;
                break;
            case "西风":
                imgId = R.drawable.ic_wind_3;
                break;
            case "西北风":
                imgId = R.drawable.ic_wind_4;
                break;
            case "北风":
                imgId = R.drawable.ic_wind_5;
                break;
            case "东北风":
                imgId = R.drawable.ic_wind_6;
                break;
            case "东风":
                imgId = R.drawable.ic_wind_7;
                break;
            case "东南风":
                imgId = R.drawable.ic_wind_8;
                break;
            default:
                imgId = R.drawable.ic_wind;
                break;
        }
        return imgId;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 取消返回或者滑动返回
        if (resultCode != Activity.RESULT_OK) {
            if (mDefaultCityName != null && getDefaultCityName() != null) {
                // 当改变了默认城市后滑动返回
                if (!mDefaultCityName.equals(getDefaultCityName()) ) {
                    changeCityWeatherInfo(getDefaultCityName());
                } else {

                    int number = WeatherDBOperate.getInstance().queryCityManage(mCityName);
                    // 删除当前城市后滑动返回
                    if (number == 0) {
                        changeCityWeatherInfo(getDefaultCityName());
                    }
                }
                // 初次添加城市后滑动返回
            } else if (mDefaultCityName == null && getDefaultCityName() != null) {
                changeCityWeatherInfo(getDefaultCityName());
            }
            return;
        }
        if (requestCode == REQUEST_WEA) {
            String cityName = data.getStringExtra(Constants.CITY_NAME);
          //  String weatherCode = data.getStringExtra(Constants.WEATHER_CODE);
            changeCityWeatherInfo(cityName);
        }
    }

    /**
     * 当更改默认城市或者删除当前城市时更新城市天气信息
     */
    private void changeCityWeatherInfo(String cityName) {
        if (!TextUtils.isEmpty(cityName)) {
            mCityName = cityName;
         //   mCityWeatherCode = cityWeatherCode;

            // 滚动到顶端
            mPullRefreshScrollView.getRefreshableView().scrollTo(0, 0);
            WeatherInfo weatherInfo = WeatherUtils.readWeatherInfo(getActivity(), mCityName);
            if (weatherInfo != null) {
                showWeatherMessage(weatherInfo);
            }

            long now = System.currentTimeMillis();

            // 最近一次天气更新时间
            long lastTime = SharedPreferencesUtils.getLong(getActivity(), getString(R.string.city_weather_update_time,
                    mCityName), 0L);
            long minuteD = (now - lastTime) / 1000 / 60;
            // 更新间隔大于10分钟自动下拉刷新
            if (minuteD > 10) {
                // 自动定位
                if (TextUtils.isEmpty(mCityName)) {
                    locationPromptRefresh();
                } else {
                    mPullRefreshScrollView.setRefreshing();
                }
            }
        }
    }
}
