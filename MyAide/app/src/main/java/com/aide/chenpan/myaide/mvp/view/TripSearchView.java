package com.aide.chenpan.myaide.mvp.view;

import com.aide.chenpan.myaide.bean.SearchTripBean;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiAddrInfo;

import java.util.List;

/**
* Created by Administrator on 2017/07/31
*/

public interface TripSearchView extends BaseView{
    /**
     * 设置历史记录布局
     * @param bean
     */
    void setHistoryUI(List<SearchTripBean> bean);

    /**
     * 隐藏布局
     */
    void goneHistoryUI();

    /**
     * 显示常规搜索布局
     * @param beans
     */
    void setSearchUser(List<SearchTripBean> beans);

    /**
     * 设置检索数据
     * @param poiAddrInfos
     */
    void setSearchResult(List<PoiInfo> poiAddrInfos);
}