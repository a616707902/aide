package com.aide.chenpan.myaide.mvp.view;

import com.aide.chenpan.myaide.bean.ConstellationBean;
import com.aide.chenpan.myaide.bean.WeatherInfo;

/**
* Created by Administrator on 2017/05/16
*/

public interface WeatherFragmentView extends BaseView{

    void showWeatherMessage(WeatherInfo bean);
    void showToastMessage(String msg);

    void showConstellation(ConstellationBean bean);
}