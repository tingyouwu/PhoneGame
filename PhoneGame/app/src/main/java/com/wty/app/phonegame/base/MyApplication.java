package com.wty.app.phonegame.base;

import android.app.Application;

import com.wty.app.phonegame.utils.PreferenceUtil;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceUtil.init(this);
	}

}
