package com.aide.chenpan.myaide.mvp.model;


import android.content.Context;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.network.DataCallback;
import com.aide.chenpan.myaide.network.NetWorking;
import com.aide.chenpan.myaide.utils.WeatherUtils;

import java.io.ByteArrayInputStream;

import okhttp3.Call;

/**
* Created by Administrator on 2017/07/21
*/

public class CityManageModelImpl implements CityManageModel{

    @Override
    public void getWeatherDataByCityName(final Context context, String cityName, final MVPCallBack<WeatherInfo> mvpCallBack) {
        String url=String.format(context.getResources().getString(R.string.address_weather_city),cityName);
        NetWorking.requstNetDataByGet(url, url, new DataCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mvpCallBack.failed("onError");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!response.contains("error")) {
                    WeatherInfo weatherInfo = WeatherUtils.handleWeatherResponse(
                            new ByteArrayInputStream(response.getBytes()));
                    // 保存天气信息
                    WeatherUtils.saveWeatherInfo(weatherInfo, context);
                    mvpCallBack.succeed(weatherInfo);
                } else {
                    mvpCallBack.failed("onError");
                }

            }
        });
    }
}