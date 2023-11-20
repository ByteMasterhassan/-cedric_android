package com.cedricapp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.service.StepsService;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


public class StepCountServiceUtil {

    static String TAG = "SERVICE_START_STOP_UTIL";

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
                //return false;
            }
        }
        return false;
    }

    public static void startStepCountService(Context context) {
        try {
            // if (SharedData.isSubscriptionAvailable) {
            if (context != null) {
                if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(context)) {
                    // Intent startIntent = new Intent(context, StepCountingService.class);
                    Intent startIntent = new Intent(context, StepsService.class);
                    startIntent.setAction(Common.START_ACTION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Step counter service start foreground Service");
                        }
                        if (!SessionUtil.getUserEmailFromSession(context).isEmpty())
                            context.startForegroundService(startIntent);

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Step counter service startService");
                        }
                        if (!SessionUtil.getUserEmailFromSession(context).isEmpty())
                            context.startService(startIntent);
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Context is null while starting step count service");
                }
            }
            // }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);

            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    public static void stopStepCountService(Context context) {
        try {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "stop service");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "stop service in tiramisu");
                }

                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    // Intent stopIntent = new Intent(context, StepCountingService.class);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "POST_NOTIFICATION GRANTED");
                    }
                    Intent stopIntent = new Intent(context, StepsService.class);
                    stopIntent.setAction(Common.STOP_ACTION);
                    context.startForegroundService(stopIntent);

                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "stop service less than tiramisu");
                }
                //Intent startIntent = new Intent(context, StepCountingService.class);
                Intent stopIntent = new Intent(context, StepsService.class);
                stopIntent.setAction(Common.STOP_ACTION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(stopIntent);
                } else {
                    context.startService(stopIntent);
                }
            }
            //getActivity().unbindService(mConnection);
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);

            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }
}
