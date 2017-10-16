package com.aide.chenpan.myaide.mvp.presenter;

import android.content.Context;

import com.aide.chenpan.myaide.mvp.model.SearchResultShowMapModel;
import com.aide.chenpan.myaide.mvp.model.SearchResultShowMapModelImpl;
import com.aide.chenpan.myaide.mvp.view.SearchResultShowMapView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

/**
 * Created by Administrator on 2017/8/17.
 */
public class SearchResultShowMapPresenter extends BasePresenter<SearchResultShowMapView>{
    SearchResultShowMapModel mSearchResultShowMapModel=new SearchResultShowMapModelImpl();
    /**
     * 获取详细的POI信息
     * @param context
     * @param poiInfo
     */
    public void getPOIDetailMessage(Context context, PoiInfo poiInfo) {
        PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poiInfo.uid));

    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        //获取POI检索结果
        @Override
        public void onGetPoiResult(PoiResult poiResult) {

        }
        //获取Place详情页检索结果,获取某个Poi详细信息

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
/**
 * 当执行以下请求时，此方法回调
 * PoiDetailSearchOption detailSearchOption = new PoiDetailSearchOption();
 detailSearchOption.poiUid(poiInfo.uid);//设置要查询的poi的uid
 mPoiSearch.searchPoiDetail(detailSearchOption);//查询poi详细信息
 */
            //poiDetailResult里面包含poi的巨多信息，你想要的都有，这里不列举了

            if (null!=mView){
                mView.showPOIDetailMessageLayout(poiDetailResult);
            }


        }
        //室内检索
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
/**
 * 当执行以下请求时，此方法回调
 * PoiIndoorOption indoorOption = new PoiIndoorOption();
 indoorOption.poiFloor(floor);//楼层
 mPoiSearch.searchPoiDetail(indoorOption);
 */

        }
    };
}
