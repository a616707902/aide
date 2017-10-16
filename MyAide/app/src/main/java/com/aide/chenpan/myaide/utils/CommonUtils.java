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

    /**
     * 验证手机号码
     *
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param cellphone
     * @return
     */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        return check(cellphone, regex);
    }

    private static boolean check(String cellphone, String regex) {
        return cellphone.matches(regex);
    }

    /**
     * 验证固话号码
     *
     * @param telephone
     * @return
     */
    public static boolean checkTelephone(String telephone) {
        String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
        return check(telephone, regex);
    }
}
