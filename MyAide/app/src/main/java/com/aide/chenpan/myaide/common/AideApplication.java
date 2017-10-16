package com.aide.chenpan.myaide.common;

import com.aide.chenpan.myaide.service.LocationService;
import com.baidu.mapapi.SDKInitializer;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/6/26.
 */
public class AideApplication  extends LitePalApplication{
    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }
}
