package com.sjtu.se2017.positivetime.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.AppInfoDao;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.xw.repo.BubbleSeekBar;

import java.util.Locale;


/**
 * Created by Administrator on 2017/7/7.
 */

public class PlanActivity extends Activity {
    AppInfoDao appInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        //appInfoDao = new AppInfoDao(this);

        final BubbleSeekBar bubbleSeekBar = (BubbleSeekBar) findViewById(R.id.bubbleSeekBar);
        float nTotalWeight = (float)ATapplicaion.getInstance().getNTotalWeight();
        bubbleSeekBar.setProgress(nTotalWeight);

        final TextView textView = (TextView)findViewById(R.id.pweight);
        textView.setText("正负时间比 "+(int)nTotalWeight+":"+(int)(100-nTotalWeight));
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                String s = String.format(Locale.CHINA, "onFinally int:%d, float:%.1f", progress, progressFloat);
                ATapplicaion.getInstance().setPTotalWeight(100-progress);
                ATapplicaion.getInstance().setNTotalWeight(progress);
                textView.setText("正负时间比 "+progress+":"+(100-progress));
                //appInfoDao.insertOrUpdate("NTotalWeight",progress);
                //Log.v("test",ATapplicaion.getInstance().getNTotalWeight()+"");
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
}
