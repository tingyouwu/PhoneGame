package com.wty.app.phonegame.service;

import android.content.Context;
import android.content.Intent;

public class MusicServiceManager {

    public static void startService(Context context){
        Intent intent = new Intent();
        intent.setAction("com.wty.Android.MUSIC");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    public static void stopService(Context context){
        Intent intent = new Intent(context,MusicService.class);
        context.stopService(intent);
    }
}
