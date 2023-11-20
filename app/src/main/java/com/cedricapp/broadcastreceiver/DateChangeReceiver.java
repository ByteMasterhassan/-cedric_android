package com.cedricapp.broadcastreceiver;

import static android.content.Context.POWER_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cedricapp.common.Common;
import com.cedricapp.utils.SessionUtil;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "DateChangeReceiver:onReceive() and intent is "+intent.getAction());
        }
        if (Objects.equals(intent.getAction(), Intent.ACTION_DATE_CHANGED)
        || Objects.equals(intent.getAction(), "android.intent.action.TIME_SET")) {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "DateChangeReceiver:onReceive():in ACTION_DATE_CHANGED");
            }
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":MyWakelockTag");
            wakeLock.acquire(60 * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMAT, Locale.US);
            String NextDayDateAndTime = sdf.format(new Date());
            String TodayDateAndTime = SessionUtil.getStepDaySessionDate(context);
            if (!TodayDateAndTime.equals(NextDayDateAndTime)) {
                if(Common.isLoggingEnabled){
                    Log.d(Common.LOG,"DateChangeReceiver::onReceive(): date changed");
                }
                Intent intent1 = new Intent();
                intent1.setAction(context.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                Intent intent2 = new Intent();
                intent2.setAction(context.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE_SERVICE");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                startAlarmReset(context);
                SessionUtil.setStepDaySessionDate(context, NextDayDateAndTime);
            }else{
                if(Common.isLoggingEnabled){
                    Log.e(Common.LOG,"DateChangeReceiver::onReceive(): date not changed");
                }
            }
            wakeLock.release();
        }
    }

    public void startAlarmReset(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Intent myIntent = new Intent(context, DateChangeReceiver.class);
                myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, myIntent, pendingFlags);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                c.set(Calendar.MILLISECOND, 999);
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pendingIntent), pendingIntent);
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                //alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pendingIntent), pendingIntent);
            }else{
                if(Common.isLoggingEnabled){
                    Log.e(Common.LOG,"DateChangeReceiver::startAlarmReset(): alarmManager is null");
                }
            }
        } catch (SecurityException ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
    }
}
