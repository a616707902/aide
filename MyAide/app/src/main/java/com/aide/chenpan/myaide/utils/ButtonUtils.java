package com.aide.chenpan.myaide.utils;

import android.os.SystemClock;

/**
 * Created by Administrator on 2017/7/18.
 */
public class ButtonUtils {
    private static long mLastClickTime = 0;             // 按钮最后一次点击时间
    private static final int SPACE_TIME = 500;          // 空闲时间

    /**
     * 是否连续点击按钮多次
     *
     * @return 是否快速多次点击
     */
    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - mLastClickTime <= SPACE_TIME) {
            return true;
        } else {
            mLastClickTime = time;
            return false;
        }
    }
}
