package com.cedricapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class NotificationUtil {
    private String CHANNEL_ID = "1111";
    private String CHANNEL_NAME = "Cedric Step Counter Channel";
    private String CHANNEL_DESCRIPTION = "Getting step counting and location";

    @RequiresApi(Build.VERSION_CODES.O)
    public void createMainNotificationChannel(Context c) {
        String id = CHANNEL_ID;
        String name = CHANNEL_NAME;
        String description = CHANNEL_DESCRIPTION;
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription (description);
        mChannel.setShowBadge(false);
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public String getMainNotificationId() {
        return CHANNEL_ID;
    }

}