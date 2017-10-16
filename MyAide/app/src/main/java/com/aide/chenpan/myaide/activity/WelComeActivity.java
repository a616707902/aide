package com.aide.chenpan.myaide.activity;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.common.AideApplication;
import com.aide.chenpan.myaide.common.Common;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.service.LocationService;
import com.aide.chenpan.myaide.utils.CommonUtils;
import com.aide.chenpan.myaide.utils.SharedPreferencesUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.Calendar;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/4/24.
 * 欢迎界面:判断是否是第一次  是第一次进入系统，跳转到SplashActivity界面不是第一次 进入系统MainActivity
 */
public class WelComeActivity extends BaseActivity {


    @Bind(R.id.splash_iv)
    ImageView splashIv;
    @Bind(R.id.version_tv)
    TextView versionTv;
    @Bind(R.id.weac_slogan_tv)
    TextView weacSloganTv;
    @Bind(R.id.logo_rlyt)
    RelativeLayout logoRlyt;
    private LocationService locationService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        // 解决初次安装后打开后按home返回后重新打开重启问题。。。
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }

        overridePendingTransition(R.anim.zoomin, 0);
        // 禁止滑动后退
        setSwipeBackEnable(false);
        WallpaperUtils.setStatusBarTranslucent(this);

        initLocationManager();
    }

    @Override
    protected void initEventAndData() {
        assignViews();

    }


    private void assignViews() {
        //设置背景图
        setImageBack();
        // 设置版本号
        setVersion();
        // 设置标语
        setSlogan();
        // 开启欢迎动画
        startAnimation();
    }

    private void setImageBack() {
        splashIv.setImageResource(getBackgroundImageResID());

    }

    private void startAnimation() {

        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 1.0f, 1.2f);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                if (value != 1.2f) {
                    splashIv.setScaleX(value);
                    splashIv.setScaleY(value);
                } else {
                    goToActivity();
                }
            }


        });
        animator.start();
    }

    /**
     * 第一次走Splash
     */
    private void goToActivity() {

        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int targetSdkVersion = info.applicationInfo.targetSdkVersion;

        if (targetSdkVersion >= 23) {
            MPermissions.requestPermissions(WelComeActivity.this, Common.REQUECT_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                CommonUtils.startActivity(this, MainActivity.class);
                overridePendingTransition(0, android.R.anim.fade_out);
                finish();
            } else {

            }
        }

        //      if (SharedPreferencesUtils.isFirst(this)) {
//            CommonUtils.startActivity(this, GuidanceActivity.class);
//        } else {

        // CommonUtils.startActivity(this, MainActivity.class);

//        }


    }

    /**
     * 设置标语样式
     */
    private void setSlogan() {
        try {
            AssetManager mgr = getAssets();
            Typeface fontFace = Typeface.createFromAsset(mgr, "fonts/weac_slogan.ttf");
            TextView SloganTv = (TextView) findViewById(R.id.weac_slogan_tv);
            SloganTv.setTypeface(fontFace);
        } catch (Exception e) {
        }
    }

    /**
     * 设置版本号
     */
    private void setVersion() {
        TextView versionTv = (TextView) findViewById(R.id.version_tv);
        versionTv.setText(getString(R.string.aide_version, CommonUtils.getVersion(this)));
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    //屏蔽返回键
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public int getBackgroundImageResID() {
        int resId;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour <= 12) {
            resId = R.drawable.morning;
        } else if (hour > 12 && hour <= 18) {
            resId = R.drawable.afternoon;
        } else {
            resId = R.drawable.night;
        }
        return resId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(Common.REQUECT_CODE_LOCATION)
    public void requestLocationSuccess() {
        CommonUtils.startActivity(this, MainActivity.class);
        overridePendingTransition(0, android.R.anim.fade_out);
        finish();
    }

    @PermissionDenied(Common.REQUECT_CODE_LOCATION)
    public void requestLocationFailed() {
        //ToastUtil.showShortToast(this,"失败");
    }


    /**
     * 初始化定位管理监听
     */
    private void initLocationManager() {

        locationService = ((AideApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mWelcomListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mWelcomListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                locationService.stop();
                locationService.unregisterListener(mWelcomListener);
                //定位成功
                //实现BDLocationListener接口
                //BDLocation类，封装了定位SDK的定位结果，在BDLocationListener的onReceiveLocation方法中获取，
                // onReceiveLocation方法当返回的是网络类型定位时是在子线程中执行了，如果有UI操作，请注意。

                if (161 == location.getLocType()) {
                    Constants.bdLocation=location;
                    String cityName = location.getCity();
                    if (cityName != null) {
                        //   mCityWeatherCode = getString(R.string.auto_location);
                        //  String  mCityWeatherCode = location.getCityCode();
                        // 初次加载定位保存
                        SharedPreferencesUtils.saveString(WelComeActivity.this, Constants.LOCATION_CITY_NAME, cityName);
                        if (SharedPreferencesUtils.getString(WelComeActivity.this, Constants.DEFAULT_CITY_NAME, null) == null) {
                            SharedPreferencesUtils.saveString(WelComeActivity.this, Constants.DEFAULT_CITY_NAME, cityName);
                            // SharedPreferencesUtils.saveString(WelComeActivity.this, Constants.DEFAULT_WEATHER_CODE, mCityWeatherCode);

                        }
                        // 立刻更新
                    } else {
                        ToastUtil.showShortToast(WelComeActivity.this, getString(R.string.can_not_find_current_location));
                    }

                } else {
                    ToastUtil.showShortToast(WelComeActivity.this, getString(R.string.location_fail));
                }
            } else {
                ToastUtil.showShortToast(WelComeActivity.this, getString(R.string.auto_location_error_retry));
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    @Override
    protected void onDestroy() {
        locationService.stop();
        super.onDestroy();

    }
}
