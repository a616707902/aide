package com.aide.chenpan.myaide.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/10/10.
 */
public class AssetsUtils {
    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 包括后缀
     * @return
     */
    public static String readAssetsFile(Context context,String fileName){
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }
}
