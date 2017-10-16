package com.aide.chenpan.myaide.mvp.model;


import android.content.Context;
import android.text.TextUtils;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.ConstellationBean;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.event.RxSchedulers;
import com.aide.chenpan.myaide.network.DataCallback;
import com.aide.chenpan.myaide.network.NetWorking;
import com.aide.chenpan.myaide.utils.WeatherUtils;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;

import okhttp3.Call;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/05/16
 */

public class WeatherFragmentModelImpl implements WeatherFragmentModel {

    @Override
    public void refreshWeather(final Context context, String mCityName, final MVPCallBack<WeatherInfo> mvpCallBack) {
        String url = context.getString(R.string.address_weather_city, mCityName);
        NetWorking.requstNetDataByGet(url, url, new DataCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mvpCallBack.failed(context.getString(R.string.internet_error));
            }

            @Override
            public void onResponse(String response, int id) {
                Observable.just(response).map(new Func1<String, WeatherInfo>() {
                    @Override
                    public WeatherInfo call(String resString) {
                        WeatherInfo weatherInfo = null;
                        try {
                            if (!resString.contains("error")) {
                                weatherInfo = WeatherUtils.handleWeatherResponse(
                                        new ByteArrayInputStream(resString.getBytes()));
                                // 保存天气信息
                                WeatherUtils.saveWeatherInfo(weatherInfo, context);
                                // 无法解析当前位置
                            } else {

                            }
                        } catch (Exception e) {
                        }
                        return weatherInfo;
                    }
                }).compose(RxSchedulers.schedulersTransformer).subscribe(new Action1<WeatherInfo>() {
                    @Override
                    public void call(WeatherInfo weather) {
                        mvpCallBack.succeed(weather);
                    }
                });
            }
        });
    }

    @Override
    public void getConstellation(final Context context, String name, final MVPCallBack<ConstellationBean> mvpCallBack) {
        String url = "http://web.juhe.cn:8080/constellation/getAll";
        String urls = url + "?consName=" + name + "&type=today&key="
                + Constants.CONSTELLATIONKEY;
        NetWorking.requstNetDataByGet(urls, urls, new DataCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mvpCallBack.failed(context.getString(R.string.internet_error));
            }

            @Override
            public void onResponse(String response, int id) {
                Observable.just(response).map(new Func1<String, ConstellationBean>() {
                    @Override
                    public ConstellationBean call(String resString) {
                        ConstellationBean constellationBean = null;
                        if (!TextUtils.isEmpty(resString)) {
                            Gson gson = new Gson();
                            constellationBean = gson.fromJson(resString, ConstellationBean.class);
                        }
                        return constellationBean;
                    }
                }).compose(RxSchedulers.schedulersTransformer).subscribe(new Action1<ConstellationBean>() {
                    @Override
                    public void call(ConstellationBean weather) {
                        mvpCallBack.succeed(weather);
                    }
                });
            }
        });
    }
}