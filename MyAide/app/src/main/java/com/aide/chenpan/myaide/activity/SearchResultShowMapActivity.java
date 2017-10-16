package com.aide.chenpan.myaide.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.common.Common;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.SearchResultShowMapPresenter;
import com.aide.chenpan.myaide.mvp.view.SearchResultShowMapView;
import com.aide.chenpan.myaide.other.SonicJavaScriptInterface;
import com.aide.chenpan.myaide.utils.LocationUtils;
import com.aide.chenpan.myaide.utils.ScreenUtils;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.aide.chenpan.myaide.widget.AlertDialog;
import com.aide.chenpan.myaide.widget.ContentScrollView;
import com.aide.chenpan.myaide.widget.ScrollLayout;
import com.aide.chenpan.myaide.widget.TagLayout;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.deanguo.ratingview.RatingBar;
import com.deanguo.ratingview.RatingView;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/8/3.
 * 搜索结果显示在地图上的界面,单一结果显示界面，从搜索结果列表跳转而来
 */
public class SearchResultShowMapActivity extends BaseActivity implements SearchResultShowMapView {
    @Bind(R.id.action_return)
    ImageView actionReturn;
    @Bind(R.id.clear_btn)
    ImageView clearBtn;
    @Bind(R.id.search_city_edit_tv)
    TextView searchCityEditTv;
    @Bind(R.id.float_title_layout)
    RelativeLayout floatTitleLayout;
    @Bind(R.id.baidu_map)
    MapView mMapView;
    @Bind(R.id.content_view)
    ContentScrollView mContentScrollView;
    @Bind(R.id.scroll_down_layout)
    ScrollLayout mScrollLayout;
    @Bind(R.id.view_stub)
    ViewStub viewStub;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.linear_info)
    LinearLayout linearInfo;
    @Bind(R.id.fab_to_where)
    FloatingActionButton fabToWhere;
    RecyclerView recyclerView;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc;
    //    private SensorManager mSensorManager;
//    private LocationService locationService;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    /**
     * 是否是附近的POI，如果是，显示定位信息，显示列表界面，不是显示传过来的POI位置信息，显示POI内容界面，并且定位到POI位置
     */
    //private boolean isNearBy = false;
    private Marker mMarker;
    private LinearLayout markLayout;
    private TextView markName;
    private TextView markText;
    private BitmapDescriptor bitmapDescriptor;
    private PoiInfo poiInfo;
    private TextView mCallPhone;
    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result_show;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        setScrollLayout();
        Intent intent = getIntent();
        searchCityEditTv.setText(intent.getStringExtra("TITLENAME"));
        mBaiduMap = mMapView.getMap();//获取地图实例
        mMapView.showZoomControls(false);
        //隐藏百度地图logo
        mMapView.removeViewAt(1);
        // 开启定位图层，一定不要少了这句，否则对在地图的设置、绘制定位点将无效
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        // 初始化定位管理，监听
        //   initLocationManager();
        //这里显示POI位置信息，显示POI内容界面
        poiInfo = intent.getExtras().getParcelable("POIINFO");
        if (poiInfo != null) {
/**
 * 绘制Marker，地图上常见的类似气球形状的图层
 */
            MarkerOptions markerOptions = new MarkerOptions();//参数设置类
            markerOptions.position(poiInfo.location);//marker坐标位置
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.poi);
            markerOptions.icon(icon);//marker图标，可以自定义
            markerOptions.draggable(false);//是否可拖拽，默认不可拖拽
            markerOptions.anchor(0.5f, 1.0f);//设置 marker覆盖物与位置点的位置关系，默认（0.5f, 1.0f）水平居中，垂直下对齐
            markerOptions.alpha(0.8f);//marker图标透明度，0~1.0，默认为1.0
            markerOptions.animateType(MarkerOptions.MarkerAnimateType.grow);//marker出现的方式，从天上掉下
            markerOptions.flat(false);//marker突变是否平贴地面
            markerOptions.zIndex(1);//index
//Marker动画效果
//                markerOptions.icons(bitmapList);//如果需要显示动画，可以设置多张图片轮番显示
//                markerOptions.period(10);//每个10ms显示bitmapList里面的图片
            mMarker = (Marker) mBaiduMap.addOverlay(markerOptions);//在地图上增加mMarker图层
            markLayout = (LinearLayout) LinearLayout.inflate(this, R.layout.mark_top_layout, null);
            markName = (TextView) markLayout.findViewById(R.id.mark_name);
            markText = (TextView) markLayout.findViewById(R.id.mark_text);
            markName.setText(poiInfo.name);
            markText.setText(poiInfo.address);
            bitmapDescriptor = BitmapDescriptorFactory.fromView(markLayout);
//infowindow位置
            LatLng latLng = new LatLng(poiInfo.location.latitude, poiInfo.location.longitude);
//infowindow点击事件
            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    //隐藏infowindow
                    //  mBaiduMap.hideInfoWindow();
                    Intent intent = new Intent(SearchResultShowMapActivity.this, BrowserActivity.class);
                    intent.putExtra(BrowserActivity.PARAM_URL, url);
                    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
                    startActivityForResult(intent, -1);
                }
            };
//显示infowindow，-47是偏移量，使infowindow向上偏移，不会挡住marker
            InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -50, listener);
            mBaiduMap.showInfoWindow(infoWindow);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(poiInfo.location).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }

    }

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
        linearInfo.setOnClickListener(new View.OnClickListener() {
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
           /* if (textFoot.getVisibility() == View.VISIBLE)
                textFoot.setVisibility(View.GONE);*/
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                // textFoot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
    }

    @Override
    protected void initEventAndData() {
//获取详细的poi信息
        ((SearchResultShowMapPresenter) mPresenter).getPOIDetailMessage(this, poiInfo);

        actionReturn.setOnClickListener(returnClickListener);
        clearBtn.setOnClickListener(returnClickListener);
    }

    View.OnClickListener returnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public BasePresenter getPresenter() {
        return new SearchResultShowMapPresenter();
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
     * 显示POI的详细信息
     * 在上划菜单中显示
     *
     * @param poiDetailResult
     */
    @Override
    public void showPOIDetailMessageLayout(PoiDetailResult poiDetailResult) {

        url = poiDetailResult.getDetailUrl();
        //这里显示界面布局，并设置数据
        tvName.setText(poiDetailResult.getName());
        tvAddress.setText(poiDetailResult.getAddress());
        double distance = LocationUtils.getDistance(Constants.bdLocation.getLatitude(), Constants.bdLocation.getLongitude(), poiDetailResult.getLocation().latitude, poiDetailResult.getLocation().longitude);
        if (1000 < distance) {
            tvDistance.setText(getResources().getString(R.string.distance_km, distance / 1000.0f));
        } else {
            tvDistance.setText(getResources().getString(R.string.distance_m, distance));
        }
        fabToWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //显示上滑布局
        //这里根据不同的类型加载不同的布局
        mContentScrollView.setBackgroundDrawable(WallpaperUtils.getWallPaperBlurDrawable(this));
        viewStub.setLayoutResource(R.layout.viewstub_poidetail_hotel);
        viewStub.inflate();
        setLayoutHotelMessage(poiDetailResult);


    }

    /**
     * @param poiDetailResult
     */
    private void setLayoutHotelMessage(final PoiDetailResult poiDetailResult) {
        RatingView mRatingView = (RatingView) mScrollLayout.findViewById(R.id.rating_view);
        TagLayout mTagLayout = (TagLayout) mScrollLayout.findViewById(R.id.taglayout);
        TextView mRatingTv = (TextView) mScrollLayout.findViewById(R.id.center_tv);
        TextView mMoreMessage = (TextView) mScrollLayout.findViewById(R.id.more_message);
        TextView hotelPrice = (TextView) mScrollLayout.findViewById(R.id.hotel_price);
        TextView hotelHours = (TextView) mScrollLayout.findViewById(R.id.hotel_hours);
        LinearLayout detail_layout = (LinearLayout) mScrollLayout.findViewById(R.id.detail_layout);
        RecyclerView otherHotel = (RecyclerView) mScrollLayout.findViewById(R.id.nearby_recycle);
        TextView mNearby = (TextView) mScrollLayout.findViewById(R.id.nearby);
        TextView mNavigation = (TextView) mScrollLayout.findViewById(R.id.navigation);
        mCallPhone = (TextView) mScrollLayout.findViewById(R.id.call_phone);
        if (!TextUtils.isEmpty(poiDetailResult.telephone) )
        {
        }else{
            mCallPhone.setVisibility(View.INVISIBLE);
        }
      //  return html;
        final RatingBar bar3 = new RatingBar((int) poiDetailResult.getEnvironmentRating() * 2, "环境");
        final RatingBar bar2 = new RatingBar((int) poiDetailResult.getFacilityRating() * 2, "设施");
        final RatingBar bar1 = new RatingBar((int) poiDetailResult.getHygieneRating() * 2, "卫生");
        final RatingBar bar4 = new RatingBar((int) poiDetailResult.getServiceRating() * 2, "服务");
        mRatingTv.setText(getResources().getString(R.string.rating_all, poiDetailResult.getOverallRating()));
        hotelPrice.setText(poiDetailResult.getPrice() + "元起");
        hotelHours.setText("营业时间：" + poiDetailResult.getShopHours());
        mRatingView.addRatingBar(bar1);
        mRatingView.addRatingBar(bar2);
        mRatingView.addRatingBar(bar3);
        mRatingView.addRatingBar(bar4);
        mRatingView.show();
        if (!TextUtils.isEmpty(poiDetailResult.getTag())) {
            String[] tags = poiDetailResult.getTag().split(";");
            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    TextView view = new TextView(this);
                    view.setText(tag);
                    view.setBackgroundResource(R.drawable.tag_bg);
                    view.setTextColor(Color.WHITE);
                    view.setPadding(5, 5, 5, 5);
                    view.setGravity(Gravity.CENTER);
                    view.setTextSize(15);
                    mTagLayout.addView(view);
                }
            }
        }
        if (poiDetailResult.getDetailUrl().length() > 20) {
            mMoreMessage.setVisibility(View.VISIBLE);
        }
        if (poiDetailResult.getOverallRating() <= 0.5) {
            detail_layout.setVisibility(View.INVISIBLE);
        }
        mMoreMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultShowMapActivity.this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.PARAM_URL, poiDetailResult.detailUrl);
                intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
                startActivityForResult(intent, -1);
            }
        });
        /**
         * 周边
         */
        mNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到周边详情界面
                NearByDetailActivity.starNearByActivity(SearchResultShowMapActivity.this, poiDetailResult.getName(), poiDetailResult.getLocation());

            }
        });
        /**
         * 跳转到导航界面
         */
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultShowMapActivity.this, NavigationActivity.class);
                startActivity(intent);

            }
        });
        /**
         * 拨打电话
         */
        mCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + poiDetailResult.telephone);
                intent.setData(data);
                if (ActivityCompat.checkSelfPermission(SearchResultShowMapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    MPermissions.requestPermissions(SearchResultShowMapActivity.this, Common.REQUECT_CODE_CALLPHONE, Manifest.permission.CALL_PHONE);
                    return;
                } else {
                    if (!TextUtils.isEmpty(poiDetailResult.telephone)) {
                        startActivity(intent);
                    } else {
                        new AlertDialog(SearchResultShowMapActivity.this).builder().setTitle("提示").setMsg("无法获取电话号码").setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }

                }

            }
        });


    }

    @PermissionGrant(Common.REQUECT_CODE_CALLPHONE)
    public void requestCallphoneSuccess() {
        mCallPhone.performClick();
    }

    @PermissionDenied(Common.REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        //提示需要的权限
    }

    /**
     * @param context
     * @param poiInfo
     */
    public static void starResultShowMapActivity(Context context, PoiInfo poiInfo) {
        Intent intent = new Intent(context, SearchResultShowMapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("POIINFO", poiInfo);
        intent.putExtras(bundle);
        intent.putExtra("TITLENAME", poiInfo.name);
        intent.putExtra("NEARBY", false);
        context.startActivity(intent);
    }
}
