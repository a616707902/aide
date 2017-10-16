package com.aide.chenpan.myaide.mvp.view;

import com.baidu.mapapi.search.poi.PoiDetailResult;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface ShowNearByResultView extends BaseView {
    void setPOIListsToFace(List<PoiDetailResult> poiInfos);
}
