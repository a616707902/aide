package com.aide.chenpan.myaide.mvp.presenter;

import android.content.Context;

import com.aide.chenpan.myaide.bean.SearchTripBean;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.event.MVPCallBack;
import com.aide.chenpan.myaide.mvp.model.TripSearchModel;
import com.aide.chenpan.myaide.mvp.model.TripSearchModelImpl;
import com.aide.chenpan.myaide.mvp.view.TripSearchView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */
public class TripSearchPresenter extends BasePresenter<TripSearchView> {

    TripSearchModel tripModel = new TripSearchModelImpl();
    private int pageIndex = 0;
    private int pageCapacity=10;

    /**
     * 查询数据
     *
     * @param context
     */
    public void searchShowRecycleData(Context context) {
        searchConventionData(context);
        searchHistoyData(context);
    }

    /**
     * 查询历史
     *
     * @param context
     */
    private void searchHistoyData(Context context) {
        tripModel.searchHistoyData(context, new MVPCallBack<List<SearchTripBean>>() {
            @Override
            public void succeed(List<SearchTripBean> bean) {
                if (mView != null) {
                    mView.setHistoryUI(bean);
                }
            }

            @Override
            public void failed(String message) {
                if (mView != null) {
                    mView.goneHistoryUI();
                }
            }
        });
    }

    /**
     * 查询匹配数据
     *
     * @param context
     */
    private void searchConventionData(Context context) {
        tripModel.searchConventionData(context, new MVPCallBack<List<SearchTripBean>>() {
            @Override
            public void succeed(List<SearchTripBean> beans) {
                if (mView != null) {
                    mView.setSearchUser(beans);
                }
            }

            @Override
            public void failed(String message) {

            }
        });
    }
public void searchCityPOIByString(Context context, String keyword,int pageNum){
    PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
    mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听
    String citystr = "北京";
    if (Constants.bdLocation != null) {
        citystr = Constants.bdLocation.getCity();
    }
    PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
    citySearchOption.city(citystr);//城市名称,最小到区级单位
    citySearchOption.keyword(keyword);
    citySearchOption.isReturnAddr(true);//是否返回门址类信息：xx区xx路xx号
    citySearchOption.pageNum(pageNum);
    mPoiSearch.searchInCity(citySearchOption);//查询

}
    /**
     * 搜索POI
     *
     * @param context
     * @param keyword
     */
    public void searchPOIByString(Context context, String keyword) {

        PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听

        /**
         * 搜索位置点周边POI
         */
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption(); //POI附近检索参数设置类
        nearbySearchOption.keyword(keyword);//搜索关键字，比如：银行、网吧、餐厅等
        nearbySearchOption.location(new LatLng(Constants.bdLocation.getLatitude(),Constants.bdLocation.getLongitude()));//搜索的位置点
        nearbySearchOption.radius(10000);//搜索覆盖半径
        nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);//搜索类型，从近至远
        nearbySearchOption.pageNum(pageIndex);//查询第几页：POI量可能会很多，会有分页查询;
        nearbySearchOption.pageCapacity(pageCapacity);//设置每页查询的个数，默认10个
        mPoiSearch.searchNearby(nearbySearchOption);//查询

        /**
         * 搜索指定区域POI
         */
     //   PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();

/**
 * LatLngBounds searchbound：地理范围数据结构，两个点就可以确定一个矩形
 * southwest：西南点
 * northeast：东北点
 */
//        LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();
//        boundSearchOption.bound(searchbound);//设置边界
//        boundSearchOption.keyword(keyword);
//        boundSearchOption.pageNum(pageIndex);
//        mPoiSearch.searchInBound(boundSearchOption);//查询

/**
 * 搜索城市内POI
 */
      /*  PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(city);//城市名称,最小到区级单位
        citySearchOption.keyword(keyword);
        citySearchOption.isReturnAddr(true);//是否返回门址类信息：xx区xx路xx号
        citySearchOption.pageNum(pageIndex);
        mPoiSearch.searchInCity(citySearchOption);//查询*/


    }
public void getPOIDetail(Context context,PoiInfo poiInfo){
    PoiSearch mPoiSearch = PoiSearch.newInstance();//初始化POI检索
    mPoiSearch.setOnGetPoiSearchResultListener(poiListener);//注册搜索事件监听
    PoiDetailSearchOption detailSearchOption = new PoiDetailSearchOption();
    detailSearchOption.poiUid(poiInfo.uid);//设置要查询的poi的uid
    mPoiSearch.searchPoiDetail(detailSearchOption);//查询poi详细信息
}


    public void getSuggestionPOI(Context context,String keyword){
        String citystr = "北京";
        if (Constants.bdLocation != null) {
            citystr = Constants.bdLocation.getCity();
        }
        //再介绍一个推荐查询，应用场景：百度地图搜索输入：银行，百度会给你推荐各大银行供你选择，点击后显示具体位置及POI信息
        SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();//初始化
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionlistener);//设置监听

        SuggestionSearchOption sSption = new SuggestionSearchOption();
        sSption.city(citystr);//指定城市
        sSption.keyword(keyword);//关键字
        sSption.location(new LatLng(Constants.bdLocation.getLatitude(),Constants.bdLocation.getLongitude()));//指定位置，选填，设置位置之后，返回结果按距离该位置的远近进行排序
        sSption.citylimit(true);//设置限制城市范围，仅返回指定城市检索结果，默认为false
        mSuggestionSearch.requestSuggestion(sSption);



    }

    OnGetSuggestionResultListener suggestionlistener = new OnGetSuggestionResultListener() {

        @Override
        public void onGetSuggestionResult(SuggestionResult result) {//获取推荐POI列表
           /*
           * SuggestionInfo中包含的信息有限，只包含：联想词，坐标点，POI的uid等
             如果想要POI的详细信息，还得利用uid通过mPoiSearch.searchPoiDetail获取
           */
            List<SuggestionResult.SuggestionInfo> suggestionInfos = result.getAllSuggestions();
        }
    };
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
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
            if (mView!=null){
                mView.setSearchResult(poiInfos);
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
