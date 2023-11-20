package com.cedricapp.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cedricapp.common.Common;
import com.cedricapp.utils.StepCountServiceUtil;

public class DataSyncWorker extends Worker {
    private final Context context;

    String TAG = "DataSyncWorker_TAG";

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams, Context context1) {
        super(context, workerParams);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DataSyncWorker constructor");
            /*Log.d(Common.LOG, "Service Running: " + StepCountServiceUtil.isMyServiceRunning(StepsService.class, context));*/
        }
        this.context = context1;
    }


    @NonNull
    @Override
    public Result doWork() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DataSyncWorker::doWork called for: " + this.getId());
            /*Log.d(Common.LOG, "Service Running: " + StepCountServiceUtil.isMyServiceRunning(StepsService.class, context));*/
        }
        startSyncDataService("upload", "step_count_service");


        return Result.success();
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, context)) {
            Intent dataSyncServiceIntent = new Intent(getApplicationContext(), StepCounterDataSync.class);
            dataSyncServiceIntent.putExtra("requestFor", requestFor);
            dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
            context.startService(dataSyncServiceIntent);
        }
    }

    @Override
    public void onStopped() {
        if (Common.isLoggingEnabled)
            Log.e(TAG, "DataSyncWorker::onStopped called for: " + this.getId());
        super.onStopped();
    }


}
