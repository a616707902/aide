package com.aide.chenpan.myaide.activity;

import android.os.Bundle;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;

/**
 * 引导页
 */
public class GuidanceActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guidance;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {

    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }
}
