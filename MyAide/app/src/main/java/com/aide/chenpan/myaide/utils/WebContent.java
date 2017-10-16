package com.aide.chenpan.myaide.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebContent {
    public byte[] readStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public String testGetHtml(String urlpath) throws Exception {
        URL url = new URL(urlpath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(6 * 1000);

//   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());  
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());  
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = readStream(inputStream);
            Log.e("字节数组长度", data.length + "个字节");
            String html = new String(data);
            return html;
        }
        return null;
    }

    public String getTitle(String s) {
        String regex;
        String title = "";
        ArrayList<String> list = new ArrayList<String>();
        regex = "(?<=<img src=\").*(?=\" class=\"head-img\">)";
        Pattern pa = Pattern.compile(regex);
        Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        for (int i = 0; i < list.size(); i++) {
            title = title + list.get(i);
        }
        return (title);
    }
}