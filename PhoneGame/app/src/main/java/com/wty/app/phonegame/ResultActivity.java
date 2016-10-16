package com.wty.app.phonegame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_end;
    ImageView icon;
    TextView tv_result;
    TextView tv_result_notice;
    int success;
    int failed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        btn_end = (Button) findViewById(R.id.btn_finish);
        icon = (ImageView) findViewById(R.id.img_icon);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_result_notice = (TextView) findViewById(R.id.tv_result_notice);
        btn_end.setOnClickListener(this);

        success = getIntent().getIntExtra("Success",0);
        failed = getIntent().getIntExtra("Fail",0);

        String result = String.format("您答对了%s道，答错了%s道!",success,failed);
        tv_result.setText(result);
        if(success>=failed){
            tv_result_notice.setText("通关!");
            icon.setImageResource(R.mipmap.ic_success);
        }else{
            icon.setImageResource(R.mipmap.ic_failed);
            tv_result_notice.setText("失败!");
        }
        countDowntimer.start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_finish){
            finish();
        }
    }

    /***
     * @Decription  倒计时
     */
    CountDownTimer countDowntimer = new CountDownTimer(5000,200) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            finish();
        }
    };

}
