package com.cedricapp.broadcastreceiver;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Common.isLoggingEnabled)
            Log.d(Common.LOG, "Service restarter onReceive call");
        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(context)) {
            startStepCountService(context);
        }

    }

    public void startStepCountService(Context context) {
        try {
            Intent startIntent = new Intent(context, StepsService.class);
            startIntent.setAction(Common.START_ACTION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Step counter service start foreground Service in service restarter broadcast reciever");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        context.startForegroundService(startIntent);
                    }
                } else {
                    context.startForegroundService(startIntent);
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Step counter service startService in dashboard");
                }
                context.startService(startIntent);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (context != null) {
                if (ConnectionDetector.isConnectedWithInternet(context)) {
                    new LogsHandlersUtils(context).getLogsDetails("ServiceRestarter", SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
                }
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }
}
