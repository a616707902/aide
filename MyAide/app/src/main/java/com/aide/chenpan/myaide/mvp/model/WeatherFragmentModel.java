package com.aide.chenpan.myaide.mvp.model;

import android.content.Context;

import com.aide.chenpan.myaide.bean.ConstellationBean;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.event.MVPCallBack;

/**
* Created by Administrator on 2017/05/16
*/

public interface WeatherFragmentModel{

    void refreshWeather(Context context, String mCityName, MVPCallBack<WeatherInfo> mvpCallBack);

    void getConstellation(Context context, String name, MVPCallBack<ConstellationBean> mvpCallBack);

}