package com.aide.chenpan.myaide.mvp.model;


import android.content.Context;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.SearchTripBean;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.other.SearchHistoyDBOperate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/07/31
 */

public class TripSearchModelImpl implements TripSearchModel {

    @Override
    public void searchHistoyData(Context context, MVPCallBack<List<SearchTripBean>> mvpCallBack) {
        List<SearchTripBean> list = SearchHistoyDBOperate.getInstance().loadHistoryData();
        if (list != null && list.size() > 0) {
            mvpCallBack.succeed(list);
        } else {
            mvpCallBack.failed("未查询到历史记录数据");
        }
    }

    @Override
    public void searchConventionData(Context context, MVPCallBack<List<SearchTripBean>> mvpCallBack) {
        List<SearchTripBean> list = new ArrayList<>();
        list.add(new SearchTripBean(R.drawable.icon_search_food, "美食"));
        list.add(new SearchTripBean(R.drawable.icon_search_hotel, "酒店"));
        list.add(new SearchTripBean(R.drawable.icon_search_bus, "公交站"));
        list.add(new SearchTripBean(R.drawable.icon_search_bank, "银行"));
        list.add(new SearchTripBean(R.drawable.icon_search_scenic_spot, "景点"));
        list.add(new SearchTripBean(R.drawable.icon_search_petrol_station, "加油站"));
        list.add(new SearchTripBean(R.drawable.icon_search_market, "超市"));
        list.add(new SearchTripBean(R.drawable.icon_search_hosptal, "医院"));
        mvpCallBack.succeed(list);

    }
}