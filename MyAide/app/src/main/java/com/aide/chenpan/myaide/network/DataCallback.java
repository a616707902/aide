package com.aide.chenpan.myaide.network;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 *自定义CallBack

 目前内部包含StringCallBack,FileCallBack,BitmapCallback，可以根据自己的需求去自定义Callback，例如希望回调User对象：

 public abstract class UserCallback extends Callback<User>
 {
 @Override
 public User parseNetworkResponse(Response response) throws IOException
 {
 String string = response.body().string();
 User user = new Gson().fromJson(string, User.class);
 return user;
 }
 }

 OkHttpUtils
 .get()//
 .url(url)//
 .addParams("username", "hyman")//
 .addParams("password", "123")//
 .build()//
 .execute(new UserCallback()
 {
 @Override
 public void onError(Request request, Exception e)
 {
 mTv.setText("onError:" + e.getMessage());
 }

 @Override
 public void onResponse(User response)
 {
 mTv.setText("onResponse:" + response.username);
 }
 });
 通过parseNetworkResponse回调的response进行解析，该方法运行在子线程，所以可以进行任何耗时操作，详细参见sample。
 */



public abstract class DataCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        return string;
    }
}
