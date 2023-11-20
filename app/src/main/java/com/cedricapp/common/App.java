package com.cedricapp.common;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.AsyncNotedAppOp;
import android.app.SyncNotedAppOp;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bugsnag.android.Bugsnag;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;

public class App extends Application {
    private static Application application;
    public static FirebaseAnalytics mFirebaseAnalytics;

    public static Application getApplication() {
        return application;
    }

    public static Context getContext(){
        return getApplication().getApplicationContext();
    }

    private String TAG = "CEDRIC_APPLICATION_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Bugsnag.start(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            AppOpsManager.OnOpNotedCallback appOpsCallback = new AppOpsManager.OnOpNotedCallback() {

                private void logPrivateDataAccess(String opCode, String trace) {
                    Log.i(TAG, "Private data accessed. " +
                            "Operation: $opCode\nStack Trace:\n$trace");
                }
                @Override
                public void onNoted(@NonNull SyncNotedAppOp syncNotedAppOp) {
                    logPrivateDataAccess(syncNotedAppOp.getOp(),
                            Arrays.toString(new Throwable().getStackTrace()));

                }

                @Override
                public void onSelfNoted(@NonNull SyncNotedAppOp syncNotedAppOp) {
                    logPrivateDataAccess(syncNotedAppOp.getOp(),
                            Arrays.toString(new Throwable().getStackTrace()));

                }

                @Override
                public void onAsyncNoted(@NonNull AsyncNotedAppOp asyncNotedAppOp) {
                    logPrivateDataAccess(asyncNotedAppOp.getOp(),
                            asyncNotedAppOp.getMessage());
                }
            };
            AppOpsManager appOpsManager = getSystemService(AppOpsManager.class);
            if (appOpsManager != null) {
                appOpsManager.setOnOpNotedCallback(getMainExecutor(), appOpsCallback);
            }
        }
    }
}
