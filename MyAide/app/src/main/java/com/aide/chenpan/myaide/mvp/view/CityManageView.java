package com.aide.chenpan.myaide.mvp.view;

import com.aide.chenpan.myaide.bean.WeatherInfo;

/**
* Created by Administrator on 2017/07/21
*/

public interface CityManageView extends BaseView{
    /**
     * 逐个显示各item的信息
     * @param bean
     * @param position
     */
    void showWeatherData(WeatherInfo bean, int position);

    /**
     * 失败提示在界面上
     * @param string
     * @param position
     */
    void runOnUiForFailed(String string, int position);
}