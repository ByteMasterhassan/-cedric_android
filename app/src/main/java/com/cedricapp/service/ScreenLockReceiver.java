package com.cedricapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cedricapp.common.Common;

public class ScreenLockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "onReceive called: screen on");
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "onReceive called: screen off");
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "onReceive called: screen unlocked");
            //new Util().setRandomWallpaper(context);
        }
    }
}
