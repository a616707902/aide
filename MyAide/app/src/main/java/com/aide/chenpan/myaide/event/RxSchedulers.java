package com.aide.chenpan.myaide.event;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Rxjava线程调度
 * Created by cp on 16/8/11.
 * 用法：.compose(RxSchedulers.schedulersTransformer)
 *
 * 如：_apiService.login(mobile, verifyCode)
 .compose(RxSchedulers.schedulersTransformer)
 .//省略
 */
public class RxSchedulers {

    public static Observable.Transformer schedulersTransformer = new Observable.Transformer(){

        @Override
        public Object call(Object observable) {
            return ((Observable)observable).subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
}