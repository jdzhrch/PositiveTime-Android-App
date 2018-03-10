package com.sjtu.se2017.positivetime.view.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.Appsta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Featureapp extends AppCompatActivity {

    //private Drawable Icon;
    private String label,packageName,viewemail;
    private ArrayList<Appsta> ShowList;
    private int style;
    private long totalTime;
    private int totalTimes;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_featureapp);
        //email = aTapplicaion.getEmail();
        viewemail = getIntent().getStringExtra("email");//email是从相似用户列表传输过来的 用户点击了哪个就传输哪个

        onResume();



    }



    //每次重新进入界面的时候加载listView
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        List<Map<String,Object>> datalist = null;



        datalist = getDataList(ShowList);

        ListView listView = (ListView)findViewById(R.id.AppStatisticsList);
        SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.inner_list,
                new String[]{"label","info"},
                new int[]{R.id.label,R.id.info});
        listView.setAdapter(adapter);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Drawable){

                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)o);
                    return true;
                }
                else return false;
            }
        });

//        TextView textView = (TextView)findViewById(R.id.text1);
//        textView.setText("运行总时间: " + DateUtils.formatElapsedTime(totalTime / 1000));
    }

    private List<Map<String,Object>> getDataList(ArrayList<Appsta> ShowList) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        Map<String,Object> map = new HashMap<String,Object>();


        dataList.add(map);
        int size = ShowList.size();

        for(int i=0;i<size;i++) {
            map = new HashMap<String,Object>();
            map.put("label",ShowList.get(i).getAppName());
            map.put("info","运行时间: " + ShowList.get(i).getTime());

            dataList.add(map);
        }

        return dataList;
    }



}
