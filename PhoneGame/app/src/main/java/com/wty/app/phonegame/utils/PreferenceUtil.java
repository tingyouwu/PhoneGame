package com.wty.app.phonegame.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author wty
 * ShareP 工具类  请在Application oncreate初始化
 **/
public class PreferenceUtil {

	private static volatile PreferenceUtil sInstance = null;

	private static String PREFERENCES_NAME = "Preferences";//preference名字
	public static String LEVEL = "level";//等级
	public static String MAX = "max";//
	public static String EACH = "each";//

	private SharedPreferences mSharedPreferences;

	/**
	 * 单例模式，获取instance实例
	 * @return
	 */
	public synchronized static PreferenceUtil getInstance() {
		if (sInstance == null) {
			throw new RuntimeException("please init first!");
		}
		return sInstance;
	}

	/**
	 * context用AppContext
	 **/
	public static void init(Context context) {
		if (sInstance == null) {
			synchronized (PreferenceUtil.class) {
				if (sInstance == null) {
					sInstance = new PreferenceUtil(context);
				}
			}
		}
	}

	private PreferenceUtil(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public int getLevel(){
		return mSharedPreferences.getInt(LEVEL,1);
	}

	public int getMax(){
		return mSharedPreferences.getInt(MAX,12000);
	}

	public int getEach(){
		return mSharedPreferences.getInt(EACH,2000);
	}

	/**
	 *	功能描述:保存到sharedPreferences
	 */
	public void writePreferences(String key,String value){
		mSharedPreferences.edit().putString(key, value).commit();// 提交修改;
	}

	public void writePreferences(String key,Boolean value){
		mSharedPreferences.edit().putBoolean(key, value).commit();// 提交修改;
	}

	public void writePreferences(String key,int value){
		mSharedPreferences.edit().putInt(key, value).commit();// 提交修改;
	}

}
