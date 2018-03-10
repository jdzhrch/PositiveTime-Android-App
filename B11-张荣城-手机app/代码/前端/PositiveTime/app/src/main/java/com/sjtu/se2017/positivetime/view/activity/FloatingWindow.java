package com.sjtu.se2017.positivetime.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.model.application.Constants;
import com.sjtu.se2017.positivetime.controller.MyWindowManager;
import com.sjtu.se2017.positivetime.service.FloatWindowService;
import com.sjtu.se2017.positivetime.service.util.PreferenceUtil;

import java.lang.reflect.Field;

public class FloatingWindow extends AppCompatActivity implements Constants{
    private Button btnOpen;
    private Button btnClose;
    private PreferenceUtil preUtil;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceUtil.getSingleton(getApplicationContext()).getBoolean(SP_BG, false)) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floatingwindow);
        preUtil = PreferenceUtil.getSingleton(getApplicationContext());
        intent = new Intent(FloatingWindow.this, FloatWindowService.class);
        btnOpen = (Button) findViewById(R.id.buttonOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startService(intent);
            }
        });
        btnClose = (Button) findViewById(R.id.buttonClose);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Intent intent = new Intent(FloatingWindow.this,
                 //       FloatWindowService.class);
                //stopService(intent);
                MyWindowManager.getInstance().removeAllWindow(getApplicationContext());
            }

        });
        Log.v("activity", ATapplicaion.getInstance().getAT()+"");
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveStatusBarHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setChecked(preUtil.getBoolean(SP_BOOT, true));
        menu.getItem(1).setChecked(preUtil.getBoolean(SP_BG, false));
        menu.getItem(2).setChecked(preUtil.getBoolean(SP_LOC, false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_boot:
                item.setChecked(!item.isChecked());
                preUtil.saveBoolean(SP_BOOT, item.isChecked());
                break;
            case R.id.action_bg:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        MyWindowManager.getInstance().setViewBg(getDrawable(R.drawable.trans_bg));
                    } else {
                        MyWindowManager.getInstance().setViewBg(getResources().getDrawable(R.drawable.trans_bg));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        MyWindowManager.getInstance().setViewBg(getDrawable(R.drawable.float_bg));
                    } else {
                        MyWindowManager.getInstance().setViewBg(getResources().getDrawable(R.drawable.float_bg));
                    }
                }
                preUtil.saveBoolean(SP_BG, item.isChecked());
                recreate();
                break;
            case R.id.action_loc:
                if (MyWindowManager.getInstance().isWindowShowing()) {
                    item.setChecked(!item.isChecked());
                    MyWindowManager.getInstance().fixWindow(FloatingWindow.this, item.isChecked());
                    preUtil.saveBoolean(SP_LOC, item.isChecked());
                    if (item.isChecked()) {
                        preUtil.saveInt(SP_X, MyWindowManager.getInstance().getWindowX());
                        preUtil.saveInt(SP_Y, MyWindowManager.getInstance().getWindowY());
                    }
                } else {
                    Toast.makeText(this, R.string.string_setting_fix, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_donate:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_ALIPAY_URL));
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_result), Toast.LENGTH_SHORT).show();
            } else {
                startService(intent);
            }
        }
    }

    private void saveStatusBarHeight() {
        if (preUtil.getInt(SP_STATUSBAR_HEIGHT, 0) == 0) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            Log.d("gjy", "statusBarHeight1=" + statusBarHeight);
            if (statusBarHeight == 0) {
                Class<?> c;
                Object obj;
                Field field;
                int x;
                try {
                    c = Class.forName("com.android.internal.R$dimen");
                    obj = c.newInstance();
                    field = c.getField("status_bar_height");
                    x = Integer.parseInt(field.get(obj).toString());
                    statusBarHeight = getResources().getDimensionPixelSize(x);
                    Log.d("gjy", "statusBarHeight2=" + statusBarHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            preUtil.saveInt(SP_STATUSBAR_HEIGHT, statusBarHeight);
        }
    }
}
