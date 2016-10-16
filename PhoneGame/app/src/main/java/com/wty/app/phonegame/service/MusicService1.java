package com.wty.app.phonegame.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wty.app.phonegame.R;

public class MusicService1 extends Service {

    private MediaPlayer mp;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        // TODO Auto-generated method stub

        if(mp==null){
           // R.raw.mmp是资源文件，MP3格式的
            mp = MediaPlayer.create(this, R.raw.ringtone3);
            mp.start();

        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // 循环播放
                try {
                    mp.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        // 播放音乐时发生错误的事件处理
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                // 释放资源
                try {
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        // 服务停止时停止播放音乐并释放资源
        if(mp != null){
            mp.stop();
            mp.release();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
