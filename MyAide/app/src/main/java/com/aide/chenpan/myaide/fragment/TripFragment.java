package com.aide.chenpan.myaide.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.activity.TripSearchActivity;
import com.aide.chenpan.myaide.base.BaseFragment;
import com.aide.chenpan.myaide.common.AideApplication;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.service.LocationService;
import com.aide.chenpan.myaide.utils.NetworkUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/4/25.
 */
public class TripFragment extends BaseFragment {
    @Bind(R.id.mapview)
    MapView mMapView;
    @Bind(R.id.search_city_edit_tv)
    TextView searchCityEditTv;
    @Bind(R.id.clear_btn)
    ImageView clearBtn;
    @Bind(R.id.search_layout)
    LinearLayout searchLayout;
    @Bind(R.id.nearby)
    TextView nearby;
    @Bind(R.id.gowhere)
    TextView gowhere;
    @Bind(R.id.navigate)
    TextView navigate;
    @Bind(R.id.show_menue)
    LinearLayout showMenue;
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private View.OnClickListener menueClickListener;
    private Animation translateAnimation;


    @Override
    protected int getlayoutId() {
        return R.layout.fragment_trip;
    }

    @Override
    protected void initInjector() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);// 获取传感器管理服务
        mBaiduMap = mMapView.getMap();//获取地图实例
        //隐藏百度地图logo
        mMapView.removeViewAt(1);
        // 开启定位图层，一定不要少了这句，否则对在地图的设置、绘制定位点将无效
        mBaiduMap.setMyLocationEnabled(true);
//普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

//卫星地图
        //   mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

//空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        //  mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        /**
         * 对定位的图标进行配置，需要MyLocationConfiguration实例，这个类是用设置定位图标的显示方式的
         */
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        /*if (Constants.bdLocation != null) {
            mCurrentLat=Constants.bdLocation.getLatitude();
            mCurrentLon=Constants.bdLocation.getLongitude();
            MyLocationData locData = new MyLocationData.Builder().accuracy(Constants.bdLocation.getRadius())
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(Constants.bdLocation.getLatitude(), Constants.bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }*/
        mBaiduMap.setOnMapStatusChangeListener(listener);
        translateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_list_translate_out);

    }

    BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status) {

            showMenue.startAnimation(translateAnimation);
            showMenue.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    showMenue.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //放大缩小控件
            mMapView.showZoomControls(false);

        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {
        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            translateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_list_tranlate_in);
            showMenue.setVisibility(View.VISIBLE);

            showMenue.startAnimation(translateAnimation);
            showMenue.setLayoutAnimationListener(null);
            mMapView.showZoomControls(true);
        }
    };

    /**
     * 开始定位
     */
    private void startLocation() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            ToastUtil.showShortToast(getActivity(), getString(R.string.internet_error));
            return;
        }

        // 初始化定位管理，监听
        initLocationManager();

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(mySensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 初始化定位管理监听
     */
    private void initLocationManager() {

        locationService = ((AideApplication) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mTripListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

    }


    @Override
    protected void initEventAndData() {
        menueClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nearby:
                        break;
                    case R.id.gowhere:
                        break;
                    case R.id.navigate:
                        break;
                }


            }
        };

    }

    @Override
    protected void lazyLoadData() {
        //TODO :查找
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareAnimation(searchLayout);
            }
        });
        //TODO :去哪里收索
        gowhere.setOnClickListener(menueClickListener);
        nearby.setOnClickListener(menueClickListener);
        navigate.setOnClickListener(menueClickListener);
        if (Constants.bdLocation == null) {
            startLocation();
        } else {
            mCurrentLat = Constants.bdLocation.getLatitude();
            mCurrentLon = Constants.bdLocation.getLongitude();
            MyLocationData locData = new MyLocationData.Builder().accuracy(Constants.bdLocation.getRadius())
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(Constants.bdLocation.getLatitude(), Constants.bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mTripListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mCurrentLat = location.getLatitude();
                mCurrentLon = location.getLongitude();
                mCurrentAccracy = location.getRadius();
                Constants.bdLocation = location;
                if (Constants.bdLocation.getLatitude() - mCurrentLat > 0.0005) {
                    isFirstLoc = true;
                }
                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                        .direction(mCurrentDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了
                //这里是移动到当前的定位位置
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


            } else {
                ToastUtil.showShortToast(getActivity(), getString(R.string.auto_location_error_retry));
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };
    public SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //每次方向改变，重新给地图设置定位数据，用上一次onReceiveLocation得到的经纬度、精度
            double x = sensorEvent.values[SensorManager.DATA_X];
            if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
                mCurrentDirection = (int) x;
                locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
                mBaiduMap.setMyLocationData(locData);
                LatLng ll = new LatLng(mCurrentLat, mCurrentLon);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
            lastX = x;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        // 取消注册传感器监听
        mSensorManager.unregisterListener(mySensorEventListener);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        if (locationService != null) {
            locationService.unregisterListener(mTripListener);
            locationService.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void showShareAnimation(View view) {
        Intent intent = new Intent(getActivity(), TripSearchActivity.class);

        //创建一个rect 对象来存储共享元素的位置信息
        Rect rect = new Rect();
        //获取元素的位置信息
        view.getGlobalVisibleRect(rect);
        //将位置信息附加到intent 上
        intent.setSourceBounds(rect);
        intent.putExtra(Constants.EXTRA_SEARCH_SHAREIMAGE, createViewInfoBundle(searchLayout));
        startActivity(intent);
        //用于屏蔽 activity 默认的转场动画效果
        getActivity().overridePendingTransition(0, 0);
    }

    private Bundle createViewInfoBundle(View view) {
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        Bundle b = new Bundle();
        int left = screenLocation[0];
        int top = screenLocation[1];
        int width = view.getWidth();
        int height = view.getHeight();
        b.putInt("left", left);
        b.putInt("top", top);
        b.putInt("width", width);
        b.putInt("height", height);
        return b;
    }

}
