package com.aide.chenpan.myaide.mvp.model;

import android.content.Context;

import com.aide.chenpan.myaide.bean.SearchTripBean;
import com.aide.chenpan.myaide.event.MVPCallBack;

import java.util.List;

/**
* Created by Administrator on 2017/07/31
*/

public interface TripSearchModel{

    void searchHistoyData(Context context, MVPCallBack<List<SearchTripBean>> mvpCallBack);

    void searchConventionData(Context context, MVPCallBack<List<SearchTripBean>> mvpCallBack);
}