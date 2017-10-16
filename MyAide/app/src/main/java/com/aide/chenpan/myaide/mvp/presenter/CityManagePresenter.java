package com.aide.chenpan.myaide.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.mvp.model.CityManageModel;
import com.aide.chenpan.myaide.mvp.model.CityManageModelImpl;
import com.aide.chenpan.myaide.mvp.view.CityManageView;

/**
 * Created by Administrator on 2017/7/21.
 */
public class CityManagePresenter extends BasePresenter<CityManageView> {

    CityManageModel cityManageModel = new CityManageModelImpl();

    public void getServerData(final Context context, final int position, final String cityName) {
        cityManageModel.getWeatherDataByCityName(context, cityName, new MVPCallBack<WeatherInfo>() {
            @Override
            public void succeed(WeatherInfo bean) {
                if (mView != null) {
                    mView.showWeatherData(bean, position);
                }
            }

            @Override
            public void failed(String message) {
                if (position == -1) {
                   mView.runOnUiForFailed(context.getString(R.string.add_city_fail), position);
                    // 添加定位
                } else if (position == -2) {
                    mView.runOnUiForFailed(context.getString(R.string.add_location_fail), position);
                } else {
                    if (TextUtils.isEmpty(cityName)) {
                        mView.runOnUiForFailed(String.format(context.getString(R.string.refresh_fail), cityName), position);
                        // 自动定位
                    } else {
                        mView.runOnUiForFailed(String.format(context.getString(R.string.refresh_fail), context.getString(
                                R.string.auto_location)), position);
                    }
                }
            }
        });
    }

}
