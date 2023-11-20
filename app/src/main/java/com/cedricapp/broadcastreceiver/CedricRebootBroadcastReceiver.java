package com.cedricapp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cedricapp.common.Common;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.SessionUtil;

import java.util.Objects;

public class CedricRebootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(context)) {
                SessionUtil.setNotificationMessage(context, "Your device is rebooted");
                boolean isLogIn = !SessionUtil.getUserEmailFromSession(context).isEmpty();
                SessionUtil.setSensorStaticSteps(context, 0);
                if (isLogIn) {
                    Intent serviceIntent = new Intent(context, StepsService.class);
                    serviceIntent.setAction(Common.START_ACTION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
            }
        }
    }
}
