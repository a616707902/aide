package com.aide.chenpan.myaide.mvp.model;

import android.content.Context;

import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.event.MVPCallBack;

/**
* Created by Administrator on 2017/07/21
*/

public interface CityManageModel{

    void getWeatherDataByCityName(Context context, String cityName, MVPCallBack<WeatherInfo> mvpCallBack);
}