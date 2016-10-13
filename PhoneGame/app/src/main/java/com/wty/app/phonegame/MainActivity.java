package com.wty.app.phonegame;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wty.app.phonegame.data.PhoneNum;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static int MAX_RECORD_TIME = 10*1000;

    TextView tv_mobile,tv_notice,tv_time;
    LinearLayout ll_refuse,ll_accept;
    String currentmobile;
    boolean isNeedtoRefuse;
    boolean isClickRefuse;
    boolean isClickAccept;
    List<Boolean> result = new ArrayList<>();


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
        currentmobile = PhoneNum.getInstance().getRandomMoblie();
        isNeedtoRefuse = PhoneNum.getInstance().isNeedtoRefuse(currentmobile);
        tv_mobile.setText(currentmobile);
        tv_notice.setText(isNeedtoRefuse?"骚扰电话":"");
        tv_time.setText("10");
        isClickRefuse = false;
        isClickAccept = false;
        countDowntimer.start();
    }

    /***
     * @Decription  十秒倒计时
     */
    CountDownTimer countDowntimer = new CountDownTimer(MAX_RECORD_TIME,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            //每隔1s判断一次按键点击事件
            if(isClickRefuse){
                //点击拒绝 并且是骚扰电话
                result.add(isNeedtoRefuse?true:false);
                Log.d("wutingyou isClickRefuse",isNeedtoRefuse+"");
            }else if(isClickAccept){
                result.add(!isNeedtoRefuse?true:false);
                Log.d("wutingyou isClickAccept",isNeedtoRefuse+"");
            }else{
                Log.d("wutingyou ........",isNeedtoRefuse+"");
                result.add(false);
            }
            //重置两个按钮
            isClickRefuse = false;
            isClickAccept = false;

            int untilfinish = (int)(millisUntilFinished/1000);
            currentmobile = PhoneNum.getInstance().getRandomMoblie();
            isNeedtoRefuse = PhoneNum.getInstance().isNeedtoRefuse(currentmobile);
            Log.d("wutingyou",currentmobile+"  " +isNeedtoRefuse);
            tv_mobile.setText(currentmobile);
            tv_notice.setText(isNeedtoRefuse?"骚扰电话":"");
            tv_time.setText(""+untilfinish);
        }

        @Override
        public void onFinish() {
            //每隔1s判断一次按键点击事件
            if(isClickRefuse){
                //点击拒绝 并且是骚扰电话
                result.add(isNeedtoRefuse?true:false);
            }else if(isClickAccept){
                result.add(!isNeedtoRefuse?true:false);
            }else{
                result.add(false);
            }

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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ll_accept){

            if(isClickRefuse){
                //已经先点击了拒绝
                isClickAccept = false;
            }else{
                isClickAccept = true;
            }

        }else if (v.getId()==R.id.ll_refuse){
            if(isClickAccept){
                //已经先点击了接收
                isClickRefuse =false;
            }else{
                isClickRefuse = true;
            }
        }
    }
}
