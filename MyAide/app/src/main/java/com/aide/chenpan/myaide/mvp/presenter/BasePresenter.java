package com.aide.chenpan.myaide.mvp.presenter;


import com.aide.chenpan.myaide.mvp.view.BaseView;

public abstract class BasePresenter<T extends BaseView> {
    public T mView;

    public void attach(T mView) {
        this.mView = mView;
    }

    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }
}