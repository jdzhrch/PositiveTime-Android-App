package com.sjtu.se2017.positivetime.view.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.controller.MyWindowManager;
import com.sjtu.se2017.positivetime.model.MenuItem;
import com.sjtu.se2017.positivetime.model.Share.Print.Print;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.service.FloatWindowService;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Administrator on 2017/7/7.
 */

public class MenuActivity extends Activity {
    Context context;
    List<MenuItem> adapterDatas;
    ListView listView;
    String MenuName;
    private MenuItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;
        MenuName = getIntent().getStringExtra("MenuName");
        ImageView menu_icon = (ImageView)findViewById(R.id.menu_icon);

        adapterDatas = new ArrayList<MenuItem>();
        //add statistics
        if(MenuName.equals("statistics")){
            menu_icon.setImageResource(R.drawable.chart);
            adapterDatas = new ArrayList<MenuItem>();
            adapterDatas.add(new MenuItem(R.drawable.chart_circle,"查看数据", Color.parseColor("#212121"), AppStatisticsList.class));
            adapterDatas.add(new MenuItem(R.drawable.chart,"AT折线图", Color.parseColor("#212121"), LineChartActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.chart,"分享", Color.parseColor("#212121"), Print.class));
        }else if(MenuName.equals("account")){
            menu_icon.setImageResource(R.drawable.accounts);
            adapterDatas = new ArrayList<MenuItem>();
            adapterDatas.add(new MenuItem(R.drawable.accounts,"登录/注册", Color.parseColor("#212121"), LoginActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.accounts,"个人中心", Color.parseColor("#212121"), MyinfoActivity.class));
        }else if(MenuName.equals("social")){
            menu_icon.setImageResource(R.drawable.socials);
            adapterDatas = new ArrayList<MenuItem>();
            adapterDatas.add(new MenuItem(R.drawable.apps,"app 搜索", Color.parseColor("#212121"), AppActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.socials,"相似用户", Color.parseColor("#212121"), UserActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.ranklist,"app排行榜", Color.parseColor("#212121"), RankActivity.class));
        }else if(MenuName.equals("settings")){
            menu_icon.setImageResource(R.drawable.setting);
            adapterDatas = new ArrayList<MenuItem>();
            adapterDatas.add(new MenuItem(R.drawable.setting,"设置权重", Color.parseColor("#212121"), SetWeightActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.floating_window,"悬浮窗", Color.parseColor("#212121"), MenuActivity.class));
            adapterDatas.add(new MenuItem(R.drawable.plan,"计划", Color.parseColor("#212121"), PlanActivity.class));
        }
        listView = (ListView) findViewById(R.id.MenuList);
        adapter = new MenuActivity.MenuItemAdapter(adapterDatas,context);
        listView.setAdapter(adapter);
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


    private class MenuItemAdapter extends BaseAdapter {
        private List<MenuItem> menuItems;
        private LayoutInflater inflater;
        ImageView menu_item_icon;
        TextView menu_item_name;
        RelativeLayout layout;

        public MenuItemAdapter() {}

        public MenuItemAdapter(List<MenuItem> menuItems,Context context) {
            this.menuItems = menuItems;
            this.inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return adapterDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MenuItem menuItem = adapterDatas.get(position);
            if(menuItem.getMenu_item_name().equals("悬浮窗")){
                convertView = inflater.inflate(R.layout.menu_listitem_floatingwindow, null);
                SwitchCompat mySwitch = (SwitchCompat) convertView.findViewById(R.id.CustomSwitchCompat);
                mySwitch.setChecked(ATapplicaion.getInstance().getIfFloatingWindow());
                mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            ATapplicaion.getInstance().setIfFloatingWindow(true);
                            startService(new Intent(MenuActivity.this, FloatWindowService.class));
                        } else {
                            MyWindowManager.getInstance().removeAllWindow(getApplicationContext());
                            ATapplicaion.getInstance().setIfFloatingWindow(false);
                        }
                    }
                });
            }else {
                convertView = inflater.inflate(R.layout.menu_listitem, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MenuActivity.this,menuItem.getTargetActivity()));
                    }
                });
            }
            menu_item_icon = (ImageView) convertView.findViewById(R.id.menu_item_icon);
            menu_item_name = (TextView) convertView.findViewById(R.id.menu_item_name);
            layout = (RelativeLayout) convertView.findViewById(R.id.layout);

            menu_item_icon.setImageResource(menuItem.getMenu_item_icon());
            menu_item_name.setText(menuItem.getMenu_item_name());
            layout.setBackgroundColor(menuItem.getBackgroundcolor());

            return convertView;
        }
    }

}
