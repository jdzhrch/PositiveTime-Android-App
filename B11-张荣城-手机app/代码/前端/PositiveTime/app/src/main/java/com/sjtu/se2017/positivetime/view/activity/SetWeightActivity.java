package com.sjtu.se2017.positivetime.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.pm.PackageManager;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.AppInfoDao;
import com.sjtu.se2017.positivetime.model.AppInfo;
import com.xw.repo.BubbleSeekBar;

import java.util.List;

public class SetWeightActivity extends AppCompatActivity {

    private ListView listView;
    private Context context;
    private AppInfoDao appInfoDao;
    private List<AppInfo> adapterDatas;
    private AppAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.weight_set);
        //show all apps
        appInfoDao = new AppInfoDao();
        try {
            adapterDatas = appInfoDao.getAllApps(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.AppInfoList);
        adapter = new AppAdapter(adapterDatas,context);
        listView.setAdapter(adapter);

    }

    private class AppAdapter extends BaseAdapter {
        private List<AppInfo> appInfos;
        private LayoutInflater inflater;
        public AppAdapter() {}

        public AppAdapter(List<AppInfo> appInfos,Context context) {
            this.appInfos = appInfos;
            this.inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            appInfoDao = new AppInfoDao(context);
            final AppInfo appInfo = adapterDatas.get(position);
            convertView = inflater.inflate(R.layout.appinfo_listitem, null);
            ImageView app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
            TextView app_name = (TextView) convertView.findViewById(R.id.app_name);
            app_icon.setImageDrawable(appInfo.getImage());
            app_name.setText(appInfo.getAppName());
            /*//通过滑动条设置weight
            BubbleSeekBar app_weight_seekbar = (BubbleSeekBar) convertView.findViewById(R.id.app_weight_seekbar);
            //init seekbar
            appInfo.setWeight(appInfoDao.checkweight(appInfo.getAppName()));
            app_weight_seekbar.setProgress(appInfo.getWeight());

            app_weight_seekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
                @Override
                public void getProgressOnActionUp(int progress, float progressFloat) {
                    appInfo.setWeight(progress);
                    appInfoDao.insertOrUpdate(appInfo.getAppName(),appInfo.getWeight());
                    //should close when activity changes
                    //db.close();
                }
            });*/
            //通过滑动条设置weight
            final TextView app_weight_textview = (TextView) convertView.findViewById(R.id.app_weight_textview);
            SeekBar app_weight_seekbar = (SeekBar) convertView.findViewById(R.id.app_weight_seekbar);
            //init seekbar
            appInfo.setWeight(appInfoDao.checkweight(appInfo.getAppName()));
            app_weight_seekbar.setProgress(appInfo.getWeight());
            app_weight_textview.setText(""+app_weight_seekbar.getProgress());

            app_weight_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    app_weight_textview.setText(""+i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    appInfo.setWeight(seekBar.getProgress());
                    appInfoDao.insertOrUpdate(appInfo.getAppName(),appInfo.getWeight());
                    //should close when activity changes
                    //db.close();
                }
            });
            return convertView;
        }
    }

}
