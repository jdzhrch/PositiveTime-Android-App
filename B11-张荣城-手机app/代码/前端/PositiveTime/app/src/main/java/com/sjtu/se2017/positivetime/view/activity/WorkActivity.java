package com.sjtu.se2017.positivetime.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dualcores.swagpoints.SwagPoints;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.service.UpdateUIService;
import com.sjtu.se2017.positivetime.service.WatchDogService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/7.
 */

public class WorkActivity extends Activity {
    private CountDownTimer countDownTimer;
    private TextView tvTimer;
    SwagPoints swagPoints;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomatos);

        //init
        swagPoints = (SwagPoints)findViewById(R.id.seekbar_point);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        button = (Button)findViewById(R.id.button);
        button.setText("开始工作");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText() == "完成"){
                    finish();
                } else {
                    swagPoints.setVisibility(View.INVISIBLE);
                    //button.setEnabled(false);
                    button.setVisibility(View.INVISIBLE);
                    button.setText("完成");
                    countDownTimer = new CountDownTimer(swagPoints.getPoints() * 1000 * 60, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = new Date(millisUntilFinished);
                            String dateStr = simpleDateFormat.format(date);
                            tvTimer.setText(dateStr);
                        }

                        @Override
                        public void onFinish() {
                            ATapplicaion aTapplicaion = ATapplicaion.getInstance();
                            tvTimer.setText("干得好!");
                            aTapplicaion.setPTime(aTapplicaion.getPTime() + swagPoints.getPoints() * 1000 * 60 * 100);//100是权重
                            button.setVisibility(View.VISIBLE);
                            stopService(new Intent(WorkActivity.this, WatchDogService.class));
                            startService(new Intent(WorkActivity.this, UpdateUIService.class));
                        }
                    };
                    countDownTimer.start();
                    //java 字符串比较相等要用equals！！！
                    stopService(new Intent(WorkActivity.this, UpdateUIService.class));//防止再关闭watchdogservice
                    startService(new Intent(WorkActivity.this, WatchDogService.class));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    /*
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event)
        {
            if(ifLock) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(this, "you should be working", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, RelaxActivity.class));
                }
            }
            return super.onKeyDown(keyCode, event);
        }*/
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
