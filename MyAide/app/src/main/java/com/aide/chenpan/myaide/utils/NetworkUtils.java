package com.aide.chenpan.myaide.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/24.
 * 网络工具类
 */
public class NetworkUtils {


    /**
     * 检查当前网络是否可用
     *
     * @param context context
     * @return 是否连接到网络
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 网址验证
     *
     * @param url 需要验证的内容
     */
    public static boolean checkWebSite(String url) {
        //http://www.163.com
//        String format = "^(http)\\://(\\w+\\.\\w+\\.\\w+|\\w+\\.\\w+)";
        //TODO: 正则表达式理解
        String format = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+" +
                "([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        return startCheck(format, url);
    }

    /**
     * 网址路径（无协议部分）验证
     *
     * @param url 需要验证的路径
     */
    public static boolean checkWebSitePath(String url) {
        String format = "[\\w\\-_]+(\\.[\\w\\-_]+)+" +
                "([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        return startCheck(format, url);
    }

    /**
     * 匹配正则表达式
     *
     * @param format 匹配格式
     * @param str    匹配内容
     * @return 是否匹配成功
     */
    private static boolean startCheck(String format, String str) {
        boolean tem;
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(str);

        tem = matcher.matches();
        return tem;
    }
}
