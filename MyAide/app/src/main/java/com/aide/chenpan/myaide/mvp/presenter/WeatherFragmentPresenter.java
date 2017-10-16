package com.aide.chenpan.myaide.mvp.presenter;

import android.content.Context;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.ConstellationBean;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.mvp.model.WeatherFragmentModel;
import com.aide.chenpan.myaide.mvp.model.WeatherFragmentModelImpl;
import com.aide.chenpan.myaide.mvp.view.WeatherFragmentView;

/**
 * Created by Administrator on 2017/5/16.
 */
public class WeatherFragmentPresenter extends BasePresenter<WeatherFragmentView> {
    WeatherFragmentModel model = new WeatherFragmentModelImpl();

    /**
     * 刷新天气
     *
     * @param context
     * @param mCityName
     */
    public void refreshWeather(final Context context, String mCityName) {

        model.refreshWeather(context, mCityName, new MVPCallBack<WeatherInfo>() {
            @Override
            public void succeed(WeatherInfo bean) {
                if (bean != null && mView != null) {
                    mView.dissDialog();
                    mView.showWeatherMessage(bean);
                } else {
                    mView.dissDialog();
                    mView.showToastMessage(context.getString(R.string.get_weather_message_fail));
                }
            }

            @Override
            public void failed(String message) {
                if (mView != null)
                    mView.dissDialog();
                mView.showToastMessage(message);
            }
        });
    }

    /**
     * 获取星座详情
     *
     * @param context
     * @param name
     */
    public void getConstellation(final Context context, String name) {
        model.getConstellation(context,name, new MVPCallBack<ConstellationBean>() {
            @Override
            public void succeed(ConstellationBean bean) {
                if (bean != null && mView != null) {
                    mView.showConstellation(bean);
                } else {
                  //  mView.showToastMessage(context.getString(R.string.get_weather_message_fail));
                }
            }

            @Override
            public void failed(String message) {

            }
        });
    }
}
