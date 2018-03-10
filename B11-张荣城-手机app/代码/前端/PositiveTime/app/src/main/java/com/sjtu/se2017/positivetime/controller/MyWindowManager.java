package com.sjtu.se2017.positivetime.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.model.application.Constants;
import com.sjtu.se2017.positivetime.service.util.PreferenceUtil;
import com.sjtu.se2017.positivetime.view.SmallWindowView;
import com.sjtu.se2017.positivetime.view.WindowView;

import java.text.DecimalFormat;

public class MyWindowManager implements Constants {

    private static MyWindowManager instance;
    private WindowManager mWindowManager;
    private WindowView mSmallWindowView;
    private LayoutParams windowParams;
    private TextView tvSum;
    private long rxtxTotal = 0;
    private long mobileRecvSum = 0;
    private long mobileSendSum = 0;
    private long wlanRecvSum = 0;
    private long wlanSendSum = 0;
    private long exitTime = 0;


    private DecimalFormat showFloatFormat = new DecimalFormat("0.00");

    public static MyWindowManager getInstance() {
        if (instance == null) {
            instance = new MyWindowManager();
        }
        return instance;
    }

    public void createWindow(final Context context) {
        createWindow(context, SMALL_WINDOW_TYPE);
    }

    private void createWindow(final Context context, int type) {
        final WindowManager windowManager = getWindowManager(context);
        if (windowParams == null) {
            windowParams = getWindowParams(context);
        }
        if (mSmallWindowView == null) {
            mSmallWindowView = new SmallWindowView(context);
            Drawable background = getCurrentBgDrawable(context);
            setViewBg(background);
            if (PreferenceUtil.getSingleton(context).getBoolean(SP_LOC)) {
                setOnTouchListener(context, mSmallWindowView, BIG_WINDOW_TYPE);
            } else {
                setOnTouchListener(windowManager, context, mSmallWindowView, BIG_WINDOW_TYPE);
            }
            try{
                windowManager.addView(mSmallWindowView, windowParams);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        tvSum = (TextView) mSmallWindowView.findViewById(R.id.tvSum);


    }

    private Drawable getCurrentBgDrawable(Context context) {
        Drawable background;
        int bgId;
        if (PreferenceUtil.getSingleton(context).getBoolean(SP_BG, false)) {
            bgId = R.drawable.trans_bg;
        } else {
            bgId = R.drawable.float_bg;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            background = context.getDrawable(bgId);
        } else {
            background = context.getResources().getDrawable(bgId);
        }
        return background;
    }

    public void initData() {
        mobileRecvSum = TrafficStats.getMobileRxBytes();
        mobileSendSum = TrafficStats.getMobileTxBytes();
        wlanRecvSum = TrafficStats.getTotalRxBytes() - mobileRecvSum;
        wlanSendSum = TrafficStats.getTotalTxBytes() - mobileSendSum;
        rxtxTotal = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();
    }

    private LayoutParams getWindowParams(Context context) {
        final WindowManager windowManager = getWindowManager(context);
        Point sizePoint = new Point();
        windowManager.getDefaultDisplay().getSize(sizePoint);
        int screenWidth = sizePoint.x;
        int screenHeight = sizePoint.y;
        LayoutParams windowParams = new LayoutParams();
        windowParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        windowParams.format = PixelFormat.RGBA_8888;
        windowParams.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        windowParams.gravity = Gravity.START | Gravity.TOP;
        windowParams.width = LayoutParams.WRAP_CONTENT;
        windowParams.height = LayoutParams.WRAP_CONTENT;
        int x = PreferenceUtil.getSingleton(context).getInt(SP_X, -1);
        int y = PreferenceUtil.getSingleton(context).getInt(SP_Y, -1);
        if (x == -1 || y == -1) {
            x = screenWidth;
            y = screenHeight / 2;
        }
        windowParams.x = x;
        windowParams.y = y;
        return windowParams;
    }

    private void setOnTouchListener(final WindowManager windowManager, final Context context, final WindowView windowView, final int type) {
        windowView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = windowParams.x;
                        paramY = windowParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        windowParams.x = paramX + dx;
                        windowParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(windowView, windowParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((System.currentTimeMillis() - exitTime) < CHANGE_DELAY) {
                            createWindow(context, type);
                            return true;
                        } else {
                            exitTime = System.currentTimeMillis();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void setOnTouchListener(final Context context, final WindowView windowView, final int type) {
        windowView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if ((System.currentTimeMillis() - exitTime) < CHANGE_DELAY) {
                            createWindow(context, type);
                            return true;
                        } else {
                            exitTime = System.currentTimeMillis();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void setViewBg(Drawable background) {
        if (mSmallWindowView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mSmallWindowView.setBackground(background);
            } else {
                mSmallWindowView.setBackgroundDrawable(background);
            }
        }
    }

    private void removeWindow(Context context, WindowView windowView) {
        if (windowView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(windowView);
        }
    }

    public void removeAllWindow(Context context) {
        removeWindow(context, mSmallWindowView);
        mSmallWindowView = null;
    }

    public void updateViewData(Context context) {

        /*long tempSum = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();
        long rxtxLast = tempSum - rxtxTotal;
        double totalSpeed = rxtxLast * 1000 / TIME_SPAN;
        rxtxTotal = tempSum;
        long tempMobileRx = TrafficStats.getMobileRxBytes();
        long tempMobileTx = TrafficStats.getMobileTxBytes();
        long tempWlanRx = TrafficStats.getTotalRxBytes() - tempMobileRx;
        long tempWlanTx = TrafficStats.getTotalTxBytes() - tempMobileTx;
        long mobileLastRecv = tempMobileRx - mobileRecvSum;
        long mobileLastSend = tempMobileTx - mobileSendSum;
        long wlanLastRecv = tempWlanRx - wlanRecvSum;
        long wlanLastSend = tempWlanTx - wlanSendSum;
        mobileRecvSum = tempMobileRx;
        mobileSendSum = tempMobileTx;
        wlanRecvSum = tempWlanRx;
        wlanSendSum = tempWlanTx;*/
        //String tmp = String.valueOf(AT);



        ATapplicaion aTapplicaion = ATapplicaion.getInstance();
        long AT = aTapplicaion.getAT();
        AT = AT/1000;
        //AT = AT/100;
        long h = AT/3600;
        long m = (AT-h*3600)/60;
        long s = (AT-h*3600) % 60;
        String t = h+":"+m+":"+s ;
        tvSum.setText(t);


    }

    private String showSpeed(double speed) {
        String speedString;
        if (speed >= 1048576d) {
            speedString = showFloatFormat.format(speed / 1048576d) + "MB/s";
        } else {
            speedString = showFloatFormat.format(speed / 1024d) + "KB/s";
        }
        return speedString;
    }

    public boolean isWindowShowing() {
        return  mSmallWindowView != null;
    }

    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public int getWindowX() {
        return windowParams.x;
    }

    public int getWindowY() {
        return windowParams.y;
    }

    public void fixWindow(Context context, boolean yes) {
        if (yes) {
            setOnTouchListener(context, mSmallWindowView, mSmallWindowView == null ? SMALL_WINDOW_TYPE : BIG_WINDOW_TYPE);
        } else {
            setOnTouchListener(getWindowManager(context), context, mSmallWindowView, mSmallWindowView == null ? SMALL_WINDOW_TYPE : BIG_WINDOW_TYPE);
        }
    }

}
