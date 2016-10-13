package com.wty.app.phonegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_start;
    Button btn_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_end = (Button) findViewById(R.id.btn_endgame);
        btn_start = (Button) findViewById(R.id.btn_startgame);
        btn_start.setOnClickListener(this);
        btn_end.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_endgame){
            finish();
        }else if(v.getId()==R.id.btn_startgame){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
