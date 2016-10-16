package com.wty.app.phonegame;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wty.app.phonegame.data.PhoneNum;
import com.wty.app.phonegame.event.RefreshEvent;
import com.wty.app.phonegame.service.MusicServiceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static int ONE_TIME = 1000;
    public static int MAX_RECORD_TIME = 33*ONE_TIME;
    public static int EACH_TIME = 3*ONE_TIME;
    TextView tv_mobile,tv_notice,tv_time;
    TextView tv_start;
    TextView tv_check;
    RelativeLayout contentLayout;
    LinearLayout ll_refuse,ll_accept;
    String currentmobile;
    boolean isNeedtoRefuse;
    boolean isClickRefuse;
    boolean isClickAccept;
    List<Boolean> result = new ArrayList<>();
    int MobileSelection = 0;//记录当前是第几个电话号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll_refuse = (LinearLayout) findViewById(R.id.ll_refuse);
        ll_accept = (LinearLayout) findViewById(R.id.ll_accept);
        ll_refuse.setOnClickListener(this);
        ll_accept.setOnClickListener(this);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile_num);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_start = (TextView) findViewById(R.id.start_time);
        tv_check = (TextView) findViewById(R.id.tv_check);
        contentLayout = (RelativeLayout) findViewById(R.id.rl_content);
        isClickRefuse = false;
        isClickAccept = false;
        EventBus.getDefault().register(this);
        countDowntimerForStart.start();
    }

    /***
     * @Decription  倒计时
     */
    CountDownTimer countDowntimerForShow = new CountDownTimer(MAX_RECORD_TIME,200) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(millisUntilFinished-EACH_TIME<0)return;
            int untilfinish = (int)((millisUntilFinished-EACH_TIME)/1000);
            tv_time.setText(""+untilfinish);
        }

        @Override
        public void onFinish() {
            int success_mobile = 0;
            int failed_mobile = 0;

            for(Boolean item:result){
                if(item){
                    success_mobile++;
                }else{
                    failed_mobile++;
                }
            }
            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
            intent.putExtra("Success",success_mobile);
            intent.putExtra("Fail",failed_mobile);
            MainActivity.this.startActivity(intent);
            //倒计时10s到了
            finish();
        }
    };


    /***
     * @Decription  倒计时
     */
    CountDownTimer countDowntimerForStart = new CountDownTimer(4000,500) {
        @Override
        public void onTick(long millisUntilFinished) {
            int untilfinish = (int)(millisUntilFinished/1000);
            tv_start.setText(untilfinish == 0?"开始":""+untilfinish);
        }

        @Override
        public void onFinish() {
            MusicServiceManager.startService(MainActivity.this);
            contentLayout.setVisibility(View.VISIBLE);
            tv_start.setVisibility(View.GONE);
            countDowntimerForShow.start();
            countDowntimer.start();
        }
    };


    /***
     * @Decription  倒计时
     */
    CountDownTimer countDowntimer = new CountDownTimer(MAX_RECORD_TIME,EACH_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(MobileSelection != 0){
                if(MobileSelection > result.size()){
                    result.add(false);
                }
            }
            MobileSelection ++;
            isClickAccept = false;
            isClickRefuse = false;
            currentmobile = PhoneNum.getInstance().getRandomMoblie();
            isNeedtoRefuse = PhoneNum.getInstance().isNeedtoRefuse(currentmobile);
            tv_mobile.setText(currentmobile);
            tv_notice.setText(isNeedtoRefuse?"骚扰电话":"");
            tv_check.setText("");
        }

        @Override
        public void onFinish() {
            if(MobileSelection > result.size()){
                    result.add(false);
            }
        }
    };

    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        Log.d("Main wutingyou:",event.getMsg());
        tv_check.setText(event.getMsg());
        if(event.getMsg().equals("2")){
            //接收
            if(MobileSelection > result.size()){
                result.add(!isNeedtoRefuse?true:false);
            }
        }else if(event.getMsg().equals("1")){
            //拒绝
            if(MobileSelection > result.size()){
                result.add(isNeedtoRefuse?true:false);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicServiceManager.stopService(this);
        EventBus.getDefault().unregister(this);
        if(countDowntimer != null)
            countDowntimer.cancel();

        if(countDowntimerForShow != null)
            countDowntimerForShow.cancel();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ll_accept){
            if(MobileSelection > result.size()){
                result.add(!isNeedtoRefuse?true:false);
            }
        }else if (v.getId()==R.id.ll_refuse){
            if(MobileSelection > result.size()){
                result.add(isNeedtoRefuse?true:false);
            }
        }
    }
}
