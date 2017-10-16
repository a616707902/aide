package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.CityManage;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.other.DBObserverListener;
import com.aide.chenpan.myaide.other.NotifyListener;
import com.aide.chenpan.myaide.other.WeatherDBOperate;
import com.aide.chenpan.myaide.utils.SharedPreferencesUtils;
import com.aide.chenpan.myaide.utils.WeatherUtils;

import java.util.Calendar;
import java.util.List;

public class CityManageAdapter extends ArrayAdapter<CityManage> {

    private final Context mContext;
    private List<CityManage> mList;

    /**
     * 进度条显示位置
     */
    private int mPosition = -1;

    /**
     * 显示进度条
     *
     * @param position 位置
     */
    public void displayProgressBar(int position) {
        mPosition = position;
    }

    /**
     * db数据观察者
     */
    private DBObserverListener mDBObserverListener;

    public void setDBObserverListener(DBObserverListener dbObserverListener) {
        mDBObserverListener = dbObserverListener;
    }

    /**
     * 默认城市改动
     */
    private NotifyListener mNotifyListener;

    public void setNotifyListener(NotifyListener notifyListener) {
        mNotifyListener = notifyListener;
    }

    /**
     * 删除城市按钮状态
     */
    private boolean mIsVisible;

    public void setDefaultCity(String defaultCity) {
        mDefaultCity = defaultCity;
    }

    /**
     * 默认城市位置
     */
    private String mDefaultCity;

    /**
     * 城市管理适配器构造方法
     *
     * @param context context
     * @param list    城市管理列表
     */
    public CityManageAdapter(Context context, List<CityManage> list) {
        super(context, 0, list);
        mContext = context;
        mList = list;


        mDefaultCity = SharedPreferencesUtils.getString(context, Constants.DEFAULT_CITY, "");
    }

    /**
     * 更新删除城市按钮状态
     *
     * @param isVisible 删除按钮是否可见
     */
    public void setCityDeleteButton(boolean isVisible) {
        mIsVisible = isVisible;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CityManage cityManage = getItem(position);
        final String cityName = cityManage.getCityName();
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.gv_city_manage, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.background = (ViewGroup) convertView.findViewById(R.id.background);
            viewHolder.cityWeather = (ViewGroup) convertView
                    .findViewById(R.id.city_weather);
            viewHolder.cityName = (TextView) convertView
                    .findViewById(R.id.city_name);
            viewHolder.weatherTypeIv = (ImageView) convertView
                    .findViewById(R.id.weather_type_iv);
            viewHolder.tempHigh = (TextView) convertView
                    .findViewById(R.id.temp_high);
            viewHolder.tempLow = (TextView) convertView
                    .findViewById(R.id.temp_low);
            viewHolder.weatherTypeTv = (TextView) convertView
                    .findViewById(R.id.weather_type_tv);
            viewHolder.setDefaultTv = (TextView) convertView
                    .findViewById(R.id.set_default);
            viewHolder.addCityIv = (ImageView) convertView
                    .findViewById(R.id.add_city);
            convertView.setTag(viewHolder);
            viewHolder.deleteCityBtn = (ImageView) convertView.findViewById(R.id.city_delete_btn);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置默认按钮
        viewHolder.setDefaultTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 重复设置默认城市
                if (cityName != null && SharedPreferencesUtils.getString(mContext, Constants.DEFAULT_CITY, "").equals(cityName)) {
                    return;
                }

                String cityName;
                // 不是自定定位
                if (cityManage.getLocationCity() == null) {
                    // 保存默认的城市名
                    cityName = cityManage.getCityName();
                    // 定位城市
                } else {
                    cityName = cityManage.getLocationCity();
                }

                // 保存城市管理的默认城市
                SharedPreferencesUtils.saveString(mContext, Constants.DEFAULT_CITY, cityManage.getCityName());
                // 保存默认的城市名
                SharedPreferencesUtils.saveString(mContext, Constants.DEFAULT_CITY_NAME, cityName);
                // 保存默认的天气代码
              //  SharedPreferencesUtils.saveString(mContext, Constants.DEFAULT_WEATHER_CODE, weatherCode);

                mDefaultCity = cityManage.getCityName();
                notifyDataSetChanged();
                // 通知默认城市改动
                mNotifyListener.onChanged();
            }
        });
        // 默认城市
        if (cityName != null && mDefaultCity.equals(cityName)) {
            viewHolder.setDefaultTv.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.bg_gv_city_manage_default));
            viewHolder.setDefaultTv.setText(R.string.my_default);
        } else {
            viewHolder.setDefaultTv.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.bg_gv_city_manage_set_default));
            viewHolder.setDefaultTv.setText(R.string.set_default);
        }
        // 当显示删除按钮并且不是添加城市按钮
        if (mIsVisible && (position != mList.size() - 1)) {
            viewHolder.deleteCityBtn.setVisibility(View.VISIBLE);
            Animation scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.city_item_delete_image);
            viewHolder.deleteCityBtn.startAnimation(scaleAnimation);
            viewHolder.deleteCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 默认城市不可删除
                    if (cityName != null && mDefaultCity.equals(cityName)) {
                        return;
                    }
                    WeatherDBOperate.getInstance().deleteCityManage(cityManage);
                    mList.remove(cityManage);
                    notifyDataSetChanged();
                }
            });
            // 不加这个编辑状态会错乱
            viewHolder.background.setVisibility(View.VISIBLE);
            // 当显示删除按钮并且是添加城市按钮
        } else if (mIsVisible && (position == mList.size() - 1) && mList.size() != 1) {
            // 隐藏添加城市按钮
            viewHolder.background.setVisibility(View.GONE);
            // 当不显示删除按钮并且不是添加城市按钮
        } else if (!mIsVisible && (position != mList.size() - 1)) {
            // 隐藏删除按钮
            viewHolder.deleteCityBtn.setVisibility(View.GONE);
            // 当不显示删除按钮并且是添加城市按钮
        } else if (!mIsVisible && (position == mList.size() - 1)) {
            // 显示添加城市按钮
            viewHolder.background.setVisibility(View.VISIBLE);

        }

        // 当为最后一项（添加城市按钮）
        if (position == mList.size() - 1) {
            viewHolder.addCityIv.setVisibility(View.VISIBLE);
            viewHolder.cityWeather.setVisibility(View.INVISIBLE);
            viewHolder.deleteCityBtn.setVisibility(View.GONE);

            viewHolder.progressBar.setVisibility(View.GONE);
        } else {
            viewHolder.addCityIv.setVisibility(View.GONE);
            viewHolder.cityWeather.setVisibility(View.VISIBLE);

            // 新加城市临时加载为空
            if (cityManage.getCityName() != null) {
                viewHolder.cityName.setText(cityManage.getCityName());

                // 天气类型图片id
                int weatherId;
                Calendar calendar = Calendar.getInstance();
                // 现在小时
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                // 当前为凌晨
                if (currentHour >= 0 && currentHour < 6) {
                    weatherId = WeatherUtils.getWeatherTypeImageID(cityManage.getWeatherTypeDay(), false);
                    // 当前为白天时
                } else if (currentHour >= 6 && currentHour < 18) {
                    weatherId = WeatherUtils.getWeatherTypeImageID(cityManage.getWeatherTypeDay(), true);
                    // 当前为夜间
                } else {
                    weatherId = WeatherUtils.getWeatherTypeImageID(cityManage.getWeatherTypeNight(), false);
                }
                viewHolder.weatherTypeIv.setImageResource(weatherId);

                viewHolder.tempHigh.setText(cityManage.getTempHigh());
                viewHolder.tempLow.setText(cityManage.getTempLow());
                viewHolder.weatherTypeTv.setText(cityManage.getWeatherType());
            }
        }

        // 当列表为空（仅有添加按钮）
        if (mIsVisible && (mList.size() == 1)) {
            mIsVisible = false;
            mDBObserverListener.onDBDataChanged();
        }

        // 当显示进度条并且不是添加按钮
        if ((position == mPosition) && !(position == mList.size() - 1)) {
            viewHolder.cityWeather.setVisibility(View.INVISIBLE);
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            // 当不显示进度条并且不是添加按钮
        } else if ((position != mPosition) && !(position == mList.size() - 1)) {
            viewHolder.cityWeather.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setVisibility(View.GONE);
        }

        // 不是自动定位
        if (cityManage.getLocationCity() == null) {
            viewHolder.cityName.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_gps);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                // 设置定位图标
                viewHolder.cityName.setCompoundDrawables(drawable, null, null, null);
            }
        }
        return convertView;
    }

    /**
     * 保存控件实例
     */
    private final class ViewHolder {
        // 城市天气控件
        ViewGroup cityWeather;
        // 城市名
        TextView cityName;
        // 天气类型图片
        ImageView weatherTypeIv;
        // 高温
        TextView tempHigh;
        // 低温
        TextView tempLow;
        // 天气类型文字
        TextView weatherTypeTv;
        // 设置默认
        TextView setDefaultTv;
        // 添加城市按钮
        ImageView addCityIv;
        // 删除城市按钮
        ImageView deleteCityBtn;
        // 控件布局
        ViewGroup background;
        // 进度条
        ProgressBar progressBar;
    }
}