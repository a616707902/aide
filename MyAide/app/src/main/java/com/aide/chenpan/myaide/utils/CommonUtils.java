package com.aide.chenpan.myaide.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.aide.chenpan.myaide.R;

/**
 * Created by Administrator on 2017/4/24.
 */
public class CommonUtils {
    /**
     * 跳转界面
     * @param context
     * @param cls  需要跳转的界面
     */
    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    /**
     * 带参跳转的方法
     * @param context
     * @param cls
     * @param bundle
     */
    public static void startActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 获取应用版本号
     *
     * @param context context
     * @return 版本号
     */
    public static String getVersion(Context context) {
        String version;
        try {
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = context.getString(R.string.version);
        }
        return version;
    }
}
