package com.wty.app.phonegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.wty.app.phonegame.event.RefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CheckKeyActivity extends AppCompatActivity {

    TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_checkkey);
        tv_result = (TextView) findViewById(R.id.tv_key);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        Log.d("Main wutingyou:",event.getMsg());
        tv_result.setText(event.getMsg());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
