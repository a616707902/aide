package com.aide.chenpan.myaide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aide.chenpan.myaide.base.BaseActivity;


public class SharedPreferencesUtils {
	public static void saveString(Context mActivity, String key,
			String value) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	public static void saveLong(Context mActivity, String key,
								  Long value) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().putLong(key, value).commit();
	}

	public static String getString(Context mActivity, String key,
			String defValue) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	public static Long getLong(Context mActivity, String key,
								   Long defValue) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getLong(key, defValue);
	}

	public static void setBoolean(BaseActivity mActivity, String key,
								  boolean value) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static boolean isFirst(BaseActivity mActivity) {
		return getBoolean(mActivity, "isfirst", true);
	}

	public static boolean getBoolean(Context mActivity, String key,
			boolean defValue) {
		SharedPreferences sp = mActivity.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}


}
