package com.aide.chenpan.myaide.mvp.presenter;

import android.content.Context;

import com.aide.chenpan.myaide.mvp.view.ShowNearByResultView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */
public class ShowNearByResultPresenter extends BasePresenter<ShowNearByResultView> {

    private int pageIndex = 0;
    private int pageCapacity = 10;
    List<PoiDetailResult> poiDetailResults=new ArrayList<>();

    public void getPOIDetailMessage(PoiInfo poiInfo) {
        PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poiInfo.uid));

    }

    public void getPOIbyLatlng(Context mContext, LatLng targetLatlng, int distance, String keyword) {
        poiDetailResults.clear();
        PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听

        /**
         * 搜索位置点周边POI
         */
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption(); //POI附近检索参数设置类
        nearbySearchOption.keyword(keyword);//搜索关键字，比如：银行、网吧、餐厅等
        nearbySearchOption.location(targetLatlng);//搜索的位置点
        nearbySearchOption.radius(distance);//搜索覆盖半径
        nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);//搜索类型，从近至远
        nearbySearchOption.pageNum(pageIndex);//查询第几页：POI量可能会很多，会有分页查询;
        nearbySearchOption.pageCapacity(pageCapacity);//设置每页查询的个数，默认10个
        mPoiSearch.searchNearby(nearbySearchOption);//查询

    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        //获取POI检索结果
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            /**
             * PoiInfo中包含了经纬度、城市、地址信息、poi名称、uid、邮编、电话等等信息；
             有了这些，你是不是可以可以在这里画一个自定义的图层了，然后添加点击事件，做一些操作了呢
             */
             List<PoiInfo> poiInfos = poiResult.getAllPoi();//poi列表

            /**
             * PoiAddrInfo：只包含地址、经纬度、名称
             */
            List<PoiAddrInfo> PoiAddrInfos = poiResult.getAllAddr();
            for (PoiInfo poiInfo: poiInfos){
                getPOIDetailMessage(poiInfo);
            }

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

            poiDetailResults.add(poiDetailResult);
            if (poiDetailResults.size()==pageCapacity){
                if (mView!=null){
                    mView.setPOIListsToFace(poiDetailResults);
                }
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
