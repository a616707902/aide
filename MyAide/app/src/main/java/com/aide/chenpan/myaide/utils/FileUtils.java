package com.aide.chenpan.myaide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.aide.chenpan.myaide.common.Constants;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/4/24.
 * 文件工具类
 */
public class FileUtils {

    /**
     * 转换文件大小
     *
     * @param fileLength file
     * @param pattern    匹配模板 "#.00","0.0"...
     * @return 格式化后的大小
     */
    public static String formatFileSize(long fileLength, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        String fileSizeString;
        if (fileLength < 1024) {
//            fileSizeString = df.format((double) fileLength) + "B";
            fileSizeString = "0KB";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "G";
        }
        return fileSizeString;
    }
    /***
     * 转换文件时长
     *
     * @param ms 毫秒数
     * @return mm:ss
     */
    public static String formatFileDuration(int ms) {
        // 单位秒
        int ss = 1000;
        // 单位分
        int mm = ss * 60;

        // 剩余分钟
        int remainMinute = ms / mm;
        // 剩余秒
        int remainSecond = (ms - remainMinute * mm) / ss;

        return addZero(remainMinute) + ":"
                + addZero(remainSecond);

    }
    /**
     * 时间补零
     *
     * @param time 需要补零的时间
     * @return 补零后的时间
     */
    public static String addZero(int time) {
        if (String.valueOf(time).length() == 1) {
            return "0" + time;
        }

        return String.valueOf(time);
    }


    /**
     * 去掉文件扩展名
     *
     * @param fileName 文件名
     * @return 没有扩展名的文件名
     */
    public static String removeEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < fileName.length())) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }

    /**
     * Returns specified directory(/mnt/sdcard/...).
     * directory will be created on SD card by defined path if card
     * is mounted. Else - Android defines files directory on device's
     * files（/data/data/<application package>/files） system.
     *
     * @param context context
     * @param path    file path (e.g.: "/AppDir/a.mp3", "/AppDir/files/images/a.jp")
     * @return File {@link File directory}
     */
    public static File getFileDirectory(Context context, String path) {
        File file = null;
        if (isHasSDCard()) {
            file = new File(Environment.getExternalStorageDirectory(), path);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    file = null;
                }
            }
        }
        if (file == null) {
            // 使用内部缓存[MediaStore.EXTRA_OUTPUT ("output")]是无法正确写入裁切后的图片的。
            // 系统是用全局的ContentResolver来做这个过程的文件io操作，app内部的存储被忽略。（猜测）
            file = new File(context.getFilesDir(), path);
        }
        return file;
    }

    /**
     * Returns specified directory(/mnt/sdcard/Android/data/<application package>/files/...).
     * directory will be created on SD card by defined path if card
     * is mounted. Else - Android defines files directory on device's
     * files（/data/data/<application package>/files） system.
     *
     * @param context context
     * @param path    file  path (e.g.: "/music/a.mp3", "/pictures/a.jpg")
     * @return File {@link File directory}
     */
    public static File getExternalFileDirectory(Context context, String path) {
        File file = null;
        if (isHasSDCard()) {
            file = new File(context.getExternalFilesDir(null), path);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    file = null;
                }
            }
        }
        if (file == null) {
            // 使用内部缓存[MediaStore.EXTRA_OUTPUT ("output")]是无法正确写入裁切后的图片的。
            // 系统是用全局的ContentResolver来做这个过程的文件io操作，app内部的存储被忽略。（猜测）
            file = new File(context.getFilesDir(), path);
        }
        return file;
    }

    public static boolean isHasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Returns directory absolutePath.
     *
     * @param context context
     * @param path    file path (e.g.: "/AppDir/a.mp3", "/AppDir/files/images/a.jpg")
     * @return /mnt/sdcard/Android/data/<application package>/files/....
     */
    public static String getFilePath(Context context, String path) {
        return getExternalFileDirectory(context, path).getAbsolutePath();
    }

    /**
     * set intent options
     *
     * @param context  context
     * @param uri      image path uri
     * @param filePath save path (e.g.: "/AppDir/a.mp3", "/AppDir/files/images/a.jpg")
     * @param type     0，截取壁纸/拍照；1，截取Logo
     * @return Intent
     */
    public static Intent getCropImageOptions(Context context, Uri uri, String filePath, int type) {
        int width;
        int height;
        // 截取壁纸/拍照
        if (type == 0) {
            width = context.getResources().getDisplayMetrics().widthPixels;
            height = context.getResources().getDisplayMetrics().heightPixels;
        } else { // 截取logo
            width = height =ScreenUtils.dip2px(context, 30);
        }

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        // 保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getExternalFileDirectory
                (context, filePath)));
        // 是否去除面部检测
        intent.putExtra("noFaceDetection", true);
        // 是否保留比例
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 裁剪区的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        // 是否将数据保留在Bitmap中返回
        intent.putExtra("return-data", false);
        return intent;
    }


    /**
     * 保存自定义二维码logo地址
     */
    public static void saveQRcodeLogoPath(Context context, String logoPath) {
        SharedPreferences share = context.getSharedPreferences(
                Constants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit();
        edit.putString(Constants.QRCODE_LOGO_PATH, logoPath);
        edit.apply();
    }

}
