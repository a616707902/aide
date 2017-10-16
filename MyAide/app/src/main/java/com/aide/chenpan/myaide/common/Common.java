package com.aide.chenpan.myaide.common;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;

/**
 * 作者：chenpan
 * 时间：2016/11/15 16:33
 * 邮箱：616707902@qq.com
 * 描述：
 */

public class Common {



    private static LayoutInflater inflater;

    public static View inflate(Context context, int res) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        return inflater.inflate(res, null);


    }

    /**
     * 手机存储路径
     * Environment.getExternalStorageDirectory();
     */
    public static final String PHONE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator+"myaide"+ File.separator;
    public static final String SAVEFOLDER=PHONE_PATH+"/Error";


//权限申请code
    public final static int  REQUECT_CODE_LOCATION=1;
    public final static int  REQUECT_CODE_SDCARD=2;
    public final static int  REQUECT_CODE_CAMERA=3;
    public final static int  REQUECT_CODE_CALLPHONE=4;

}
