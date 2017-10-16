package com.aide.chenpan.myaide.network;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;

/**
 * 类名: NetWorking
 * <br/>功能描述:
 * <br/>作者: 陈渝金
 * <br/>时间: 2017/3/9
 * <br/>最后修改者:
 * <br/>最后修改内容:
 */


public class NetWorking {
    /**
     *OkHttp的GET请求
     * @param url 拼装好的url  如：http://wthrcdn.etouch.cn/WeatherApi?city=成都
     * @param tag 用于取消用的标志
     * @param myCallBack 回调接口
     */
    public synchronized static void requstNetDataByGet(String url, Object tag, DataCallback myCallBack) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(tag)
                .build()
                .execute(myCallBack);
    }

    /**
     * OkHttp的Post请求
     * @param url 如：http://wthrcdn.etouch.cn/WeatherApi
     * @param tag 用于取消用的标志
     * @param params 添加的参数信息
     * @param myCallBack 回调接口
     */
    public synchronized static void requstNetDataByPost(String url, Object tag, Map<String ,String > params, DataCallback myCallBack) {
        OkHttpUtils
                .post()
                .url(url)
                .params(params)
                .tag(tag)
                .build()
                .execute(myCallBack);
    }

    /**
     * 取消单个请求
     * @param url  OkHttpUtils.cancelTag(this);//取消以Activity.this作为tag的请求
     */
    public synchronized static void cancelRequestByUrl(String url) {
        RequestCall call = OkHttpUtils.get().url(url).build();
        call.cancel();
    }
    /**
     * 根据tag取消请求
     * @param tag
     */
    public synchronized static void cancelRequestByTag(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);//取消以Activity.this作为tag的请求
    }
}
