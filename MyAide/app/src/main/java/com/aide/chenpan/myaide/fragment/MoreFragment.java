package com.aide.chenpan.myaide.fragment;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseFragment;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;

/**
 * Created by Administrator on 2017/4/25.
 */
public class MoreFragment extends BaseFragment {
    @Override
    protected int getlayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected void lazyLoadData() {

    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }
}
