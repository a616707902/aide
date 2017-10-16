package com.aide.chenpan.myaide.mvp.view;

import com.baidu.mapapi.search.poi.PoiDetailResult;

/**
* Created by Administrator on 2017/08/17
*/

public interface SearchResultShowMapView extends BaseView{
 void   showPOIDetailMessageLayout(PoiDetailResult poiDetailResult);
}