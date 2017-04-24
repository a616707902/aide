package com.aide.chenpan.myaide.activity;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.utils.CommonUtils;
import com.aide.chenpan.myaide.utils.SharedPreferencesUtils;
import com.aide.chenpan.myaide.utils.WallpaperUtils;

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
        if (SharedPreferencesUtils.isFirst(this)) {
            CommonUtils.startActivity(this, MainActivity.class);
        } else {
            CommonUtils.startActivity(this, MainActivity.class);

        }
        overridePendingTransition(0, android.R.anim.fade_out);
        finish();
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
}
