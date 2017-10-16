package com.aide.chenpan.myaide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.SearchNearByAdapter;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.bean.NearByMenuBean;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.ShowNearByResultPresenter;
import com.aide.chenpan.myaide.mvp.view.ShowNearByResultView;
import com.aide.chenpan.myaide.utils.ScreenUtils;
import com.aide.chenpan.myaide.widget.ContentListView;
import com.aide.chenpan.myaide.widget.ScrollLayout;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiDetailResult;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/10/11.
 */
public class ShowNearBySearchResultActivity extends BaseActivity implements ShowNearByResultView {


    @Bind(R.id.baidu_map)
    MapView mMapView;
    @Bind(R.id.action_return)
    ImageView actionReturn;
    @Bind(R.id.clear_btn)
    ImageView clearBtn;
    @Bind(R.id.search_city_edit_tv)
    TextView searchCityEditTv;
    @Bind(R.id.float_title_layout)
    RelativeLayout floatTitleLayout;
    @Bind(R.id.list_view)
    ContentListView listView;
    @Bind(R.id.text_foot)
    TextView textFoot;
    @Bind(R.id.scroll_down_layout)
    ScrollLayout mScrollLayout;
    /**
     * 目标的经纬度
     */
    private LatLng targetLatlng;
    /**
     * 目标名字
     */
    private String targetName;
    /**
     * 搜索的内容
     */
    private NearByMenuBean.AllTagsListBean.TagInfoListBean tagInfoListBean;
    /**
     * 初始搜索距离5000米
     */
    int distance = 5000;
    private BaiduMap mBaiduMap;
    private SearchNearByAdapter searchNearByAdapter;
    private int pageNum=0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nearby_search_result;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        setScrollLayout();
        tagInfoListBean = (NearByMenuBean.AllTagsListBean.TagInfoListBean) getIntent().getExtras().getSerializable("SEARCH");
        targetLatlng = getIntent().getExtras().getParcelable("LATLNG");
        targetName = getIntent().getStringExtra("LACALNAME");
        searchCityEditTv.setText(tagInfoListBean.getTagName());
        mBaiduMap = mMapView.getMap();//获取地图实例
        mMapView.showZoomControls(false);
        //隐藏百度地图logo
        mMapView.removeViewAt(1);
        // 开启定位图层，一定不要少了这句，否则对在地图的设置、绘制定位点将无效
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        if (targetLatlng != null) {
            /**
             * 绘制Marker，地图上常见的类似气球形状的图层
             */
            MarkerOptions markerOptions = new MarkerOptions();//参数设置类
            markerOptions.position(targetLatlng);//marker坐标位置
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.poi);
            markerOptions.icon(icon);//marker图标，可以自定义
            markerOptions.draggable(false);//是否可拖拽，默认不可拖拽
            markerOptions.anchor(0.5f, 1.0f);//设置 marker覆盖物与位置点的位置关系，默认（0.5f, 1.0f）水平居中，垂直下对齐
            markerOptions.alpha(0.8f);//marker图标透明度，0~1.0，默认为1.0
            markerOptions.animateType(MarkerOptions.MarkerAnimateType.grow);//marker出现的方式，从天上掉下
            markerOptions.flat(false);//marker突变是否平贴地面
            markerOptions.zIndex(1);//index
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(targetLatlng).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    protected void initEventAndData() {

        actionReturn.setOnClickListener(returnClickListener);
        clearBtn.setOnClickListener(returnClickListener);
        ((ShowNearByResultPresenter) mPresenter).getPOIbyLatlng(this, targetLatlng, distance, tagInfoListBean.getTagName());
    }

    View.OnClickListener returnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };
    private void setScrollLayout() {
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset((int) (ScreenUtils.getScreenHeight(this) * 0.5));
        mScrollLayout.setExitOffset(ScreenUtils.dip2px(this, 50));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        mScrollLayout.setToExit();
        mScrollLayout.getBackground().setAlpha(0);
        mMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.scrollToExit();
            }
        });
        textFoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.setToOpen();
            }
        });
    }
    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
            }
            if (textFoot.getVisibility() == View.VISIBLE)
                textFoot.setVisibility(View.GONE);
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                 textFoot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };
    @Override
    public BasePresenter getPresenter() {
        return new ShowNearByResultPresenter();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
    }

    /**
     * 唯一的跳转接口
     *
     * @param context
     * @param localName 地点名称
     * @param latLng    经纬度
     */
    public static void starNearBySearchResultActivity(Context context, String localName, LatLng latLng, NearByMenuBean.AllTagsListBean.TagInfoListBean tagInfoListBean) {
        Intent intent = new Intent(context, ShowNearBySearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("LATLNG", latLng);
        bundle.putSerializable("SEARCH", tagInfoListBean);
        intent.putExtras(bundle);
        intent.putExtra("LACALNAME", localName);
        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void showLoadProgressDialog(String str) {

    }

    @Override
    public void dissDialog() {

    }

    /**
     * 显示poi列表信息
     *
     * @param poiDetailResults
     */
    @Override
    public void setPOIListsToFace(List<PoiDetailResult> poiDetailResults) {

        if (pageNum==0){
            searchNearByAdapter = new SearchNearByAdapter(poiDetailResults, this, targetLatlng);
            listView.setAdapter(searchNearByAdapter);
        }else{
            searchNearByAdapter.addMoreData(poiDetailResults);
        }
    }

}
