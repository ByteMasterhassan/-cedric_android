package com.cedricapp.broadcastreceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cedricapp.utils.SessionUtil;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;

import java.util.Calendar;



public class MyBroadcastReceiver extends GCoreWakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean isLogIn = SessionUtil.getLoggedStatus(context);
        if(isLogIn){
            /*PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":MyWakelockTag");
            wakeLock.acquire(60 * 1000L);
            Intent i = new Intent(context,DailyActivitySyncService.class);
            startWakefulService(context,i);
            startAlarmReset(context);
            wakeLock.release();*/
        }else{

        }


    }

    @SuppressLint("ObsoleteSdkInt")
    public void startAlarmReset(Context context){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context,MyBroadcastReceiver.class);
        myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        //TODO change FLAG to IMMutabLe
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,myIntent, PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);


//todo removed SDK_InT check on M and kitkat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent),pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }


    }
    public void cancelAlarmIfExists(Context mContext,int requestCode){
        try {
            Intent myIntent = new Intent(mContext,MyBroadcastReceiver.class);
            //TODO change FLAG to IMMutabLe
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, myIntent, PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            pendingIntent.cancel();
            am.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
