package com.aide.chenpan.myaide.activity;

import android.os.Bundle;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.view.RoutePlanView;

/**
 * 线路界面
 */
public class RoutePlanActivity extends BaseActivity implements RoutePlanView {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_route_plan;
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

    @Override
    public void showLoadProgressDialog(String str) {

    }

    @Override
    public void dissDialog() {

    }
}
