package com.sjtu.se2017.positivetime.model.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sjtu.se2017.positivetime.service.FloatWindowService;
import com.sjtu.se2017.positivetime.service.util.PreferenceUtil;

public class BootReceiver extends BroadcastReceiver implements Constants {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceUtil.getSingleton(context).getBoolean(SP_BOOT, true)) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                context.startService(new Intent(context, FloatWindowService.class));
            }
        }
    }
}
