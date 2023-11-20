package com.cedricapp.service;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cedricapp.R;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.broadcastreceiver.DateChangeReceiver;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ActivitiesModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.CalculatorUtil;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.NotificationUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepsService extends Service implements SensorEventListener {
    Context context;

    Resources resources;

    final int notificationId = 1;

    NotificationUtil notificationChannel;

    NotificationManager notificationManager;

    String TAG = "STEPS_COUNT_SERVICE_LOG_TAG";

    boolean stepCounterSensorStatus, stepAccelerometerSensorStatus;

    private LineGraphSeries<DataPoint> mSeries1;

    private LineGraphSeries<DataPoint> mSeries2;

    private float[] rawAccelerometerValues;

    private float[] mRunningAccelTotal;

    private float[][] mAccelValueHistory;

    private float[] mRawAccelValues;

    private float[] mCurAccelAvg;

    private static final int SMOOTHING_WINDOW_SIZE = 20;

    private double lastMag;

    private double avgMag = 0d;

    private double netMag = 0d;

    private int mCurReadIndex = 0;

    private double mGraph1LastXValue = 0d;

    private double mGraph2LastXValue = 0d;

    private double lastXPoint = 1d;

    private final int windowSize = 10;

    double stepThreshold = 1.0d;

    double noiseThreshold = 2d;

    public static float accelerometerStepCounts = 0;

    int userID;

    DBHelper dbHelper;

    PowerManager.WakeLock mWakeLock;

    DateChangeReceiver globalDateChangeReceiver;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;

    Location location;

    private final String WORK_MANAGER_TAG = "DATA_SYNC_WORK_MANAGER";


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        resources = getResources();
        notificationChannel = new NotificationUtil();
        initForegroundService();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        rawAccelerometerValues = new float[3];
        mRunningAccelTotal = new float[3];
        mAccelValueHistory = new float[3][SMOOTHING_WINDOW_SIZE];
        mRawAccelValues = new float[3];
        mCurAccelAvg = new float[3];
        userID = Integer.parseInt(SessionUtil.getUserID(context));
        dbHelper = new DBHelper(context);
        globalDateChangeReceiver = new DateChangeReceiver();
        registerBroadcastReceiverListeners();
        startLocationUpdates();
        setupAPIScheduler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int STICKY = START_NOT_STICKY;
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "onStartCommand()::Mobile SDK is greater or equal than Q");
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "onStartCommand()::ACTIVITY_RECOGNITION is granted");
                    }
                    STICKY = initSensorForSteps(intent);
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "onStartCommand()::ACTIVITY_RECOGNITION is not granted\nStop Foreground service");
                    }
                    stopForeground(STOP_FOREGROUND_DETACH);
                    stopSelf();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "onStartCommand()::Mobile SDK is less than Q");
                }
                STICKY = initSensorForSteps(intent);
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "onStartCommand()::intent is null");
            }
            /*stopForeground(STOP_FOREGROUND_DETACH);
            stopSelf();*/
        }
        return STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "onSensorChanged called");
        }
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER -> {
                int stepCounts;
                stepCounts = (int) sensorEvent.values[0];
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "TYPE_STEP_COUNTER");
                }
                computeStepsCount(stepCounts);
            }
            case Sensor.TYPE_ACCELEROMETER -> {
                rawAccelerometerValues[0] = sensorEvent.values[0];
                rawAccelerometerValues[1] = sensorEvent.values[1];
                rawAccelerometerValues[2] = sensorEvent.values[2];
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "TYPE_ACCELEROMETER");
                }
                computeRawDataTakenFromAccelerometerSensor();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    void initForegroundService() {
        Notification notification = getNotification(SessionUtil.getUserTodaySteps(context));
        if (notification != null) {
            startForeground(notificationId, notification);
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Notification is null");
            }
            stopSelf();
        }

    }

    public Notification getNotification(int stepCount) {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("From", "service");
        notificationIntent.putExtras(bundle);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Creating Channel
            notificationChannel.createMainNotificationChannel(this);
            notification = new Notification.Builder(context, notificationChannel.getMainNotificationId())
                    .setContentTitle(resources.getString(R.string.general_today_steps) + " " + stepCount + "")
                    .setContentText(resources.getString(R.string.general_pedometer_session))
                    .setSmallIcon(R.drawable.ic_app_launch)
                    .setContentIntent(pIntent)
                    .setOngoing(true).build();
        } else {
            //for devices less than API Level 26
            notification = new Notification.Builder(context)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(getResources().getString(R.string.general_today_steps) + " " + stepCount + "")
                    .setContentText(getResources().getString(R.string.general_pedometer_session))
                    .setSmallIcon(R.drawable.ic_app_launch)
                    .setContentIntent(pIntent)
                    .setOngoing(true).build();
        }
        return notification;
    }

    private void updateStepCountToNotification(int StepCount) {
        notificationManager.notify(notificationId, getNotification(StepCount));
    }

    int initSensorForSteps(Intent intent) {
        SensorManager sensorManager;
        Sensor stepCounterSensor = null;
        Sensor stepAccelerometerSensor = null;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if (stepCounterSensor == null) {
                stepAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                if (Common.isLoggingEnabled) {
                    if (stepAccelerometerSensor == null)
                        Log.e(TAG, "ACCELEROMETER sensor is null");
                    else
                        Log.d(TAG, "ACCELEROMETER sensor is available");
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Step counter sensor is not null");
                }
            }

            if (Objects.equals(intent.getAction(), Common.START_ACTION)) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Step counter service onStartCommand START_ACTION");
                }
                if (stepCounterSensor != null) {
                    stepCounterSensorStatus = sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);

                    if (Common.isLoggingEnabled) {
                        if (stepCounterSensorStatus)
                            Log.d(TAG, "Step counter sensor is available in device");
                        else
                            Log.e(TAG, "Step counter sensor is not available in device");
                    }

                } else {
                    stepAccelerometerSensorStatus = sensorManager.registerListener(this, stepAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                    if (Common.isLoggingEnabled) {
                        if (stepAccelerometerSensorStatus)
                            Log.d(TAG, "Accelerometer sensor is available in device");
                        else
                            Log.e(TAG, "Accelerometer sensor is not available in device");
                    }
                }

                mSeries1 = new LineGraphSeries<>();

                mSeries2 = new LineGraphSeries<>();

                if (stepCounterSensorStatus || stepAccelerometerSensorStatus) {
                    return START_STICKY;
                } else {
                    stopForeground(STOP_FOREGROUND_DETACH);
                    stopSelf();
                    return START_NOT_STICKY;
                }

            } else if (Objects.equals(intent.getAction(), Common.STOP_ACTION)) {

                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Stop step count service");
                }

                if (stepCounterSensor != null) {
                    sensorManager.unregisterListener(this, stepCounterSensor);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Step count sensor unregistered listener");
                    }
                }

                if (stepAccelerometerSensor != null) {
                    sensorManager.unregisterListener(this, stepAccelerometerSensor);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Accelerometer sensor unregistered listener");
                    }
                }

                stopForeground(STOP_FOREGROUND_DETACH);
                stopSelf();
                return START_NOT_STICKY;
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "initSensorForSteps()::Step count sensor service in else statement");
                }
                return START_NOT_STICKY;
            }
        } else {
            if (Common.isLoggingEnabled)
                Log.e(TAG, "sensor manager is null");

            return START_NOT_STICKY;
        }

    }

    void computeRawDataTakenFromAccelerometerSensor() {
        try {
            lastMag = Math.sqrt(Math.pow(rawAccelerometerValues[0], 2) + Math.pow(rawAccelerometerValues[1], 2) + Math.pow(rawAccelerometerValues[2], 2));

            //Source: https://github.com/jonfroehlich/CSE590Sp2018
            for (int i = 0; i < 3; i++) {
                mRunningAccelTotal[i] = mRunningAccelTotal[i] - mAccelValueHistory[i][mCurReadIndex];
                mAccelValueHistory[i][mCurReadIndex] = mRawAccelValues[i];
                mRunningAccelTotal[i] = mRunningAccelTotal[i] + mAccelValueHistory[i][mCurReadIndex];
                mCurAccelAvg[i] = mRunningAccelTotal[i] / SMOOTHING_WINDOW_SIZE;
            }
            mCurReadIndex++;
            if (mCurReadIndex >= SMOOTHING_WINDOW_SIZE) {
                mCurReadIndex = 0;
            }

            avgMag = Math.sqrt(Math.pow(mCurAccelAvg[0], 2) + Math.pow(mCurAccelAvg[1], 2) + Math.pow(mCurAccelAvg[2], 2));

            netMag = lastMag - avgMag; //removes gravity effect

            //update graph data points
            mGraph1LastXValue += 1d;
            mSeries1.appendData(new DataPoint(mGraph1LastXValue, lastMag), true, 60);

            mGraph2LastXValue += 1d;
            mSeries2.appendData(new DataPoint(mGraph2LastXValue, netMag), true, 60);

            double highestValX = mSeries2.getHighestValueX();

            if (highestValX - lastXPoint < windowSize) {
                return;
            }

            Iterator<DataPoint> valuesInWindow = mSeries2.getValues(lastXPoint, highestValX);

            lastXPoint = highestValX;

            double forwardSlope = 0d;
            double downwardSlope = 0d;

            List<DataPoint> dataPointList = new ArrayList<DataPoint>();
            valuesInWindow.forEachRemaining(dataPointList::add); //This requires API 24 or higher

            for (int i = 0; i < dataPointList.size(); i++) {
                if (i == 0) continue;
                else if (i < dataPointList.size() - 1) {
                    forwardSlope = dataPointList.get(i + 1).getY() - dataPointList.get(i).getY();
                    downwardSlope = dataPointList.get(i).getY() - dataPointList.get(i - 1).getY();

                    if (forwardSlope < 0 && downwardSlope > 0 && dataPointList.get(i).getY() > stepThreshold && dataPointList.get(i).getY() < noiseThreshold) {
                        accelerometerStepCounts += 1;
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "TYPE_ACCELEROMETER: counter Step: " + accelerometerStepCounts);
                        }
                        computeStepsCount((int) accelerometerStepCounts);
                    }
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    void computeStepsCount(int stepsCount) {
        if (!SessionUtil.getUserEmailFromSession(context).isEmpty()) {
            if (SessionUtil.getUserLogInDate(context).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                if (SessionUtil.getLoggedInStepsFirstTime(context)) {
                    SessionUtil.setSensorStaticSteps(context, stepsCount);
                    SessionUtil.setLoggedInStepsFirstTime(context, false);
                }
                int stepsFromServer = SessionUtil.getUserLogInSteps(context);
                int steps;
                if (SessionUtil.getSensorStaticSteps(context) > stepsCount) {
                    steps = stepsFromServer + (SessionUtil.getSensorStaticSteps(context) - stepsCount);
                } else {
                    steps = stepsFromServer + (stepsCount - SessionUtil.getSensorStaticSteps(context));
                }
                SessionUtil.setUsertodaySteps(context, steps);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "---------------------\nSteps from sensor is " + stepsCount);
                    Log.d(TAG, "Steps from Sensor that retrieved from sensor first time after login (static steps) is " + SessionUtil.getSensorStaticSteps(context));
                    Log.d(TAG, "Steps at time of login are " + stepsFromServer);
                    Log.d(TAG, "Steps from server are " + SessionUtil.getUserTodaySteps(context));
                    Log.d(TAG, "Steps after computation are " + steps + "\n---------------------");
                }
                updateStepCountToNotification(steps);
                updateNotificationToUI(steps);
                addActivityDataIntoDB(makeActivityObject(steps), WeekDaysHelper.getDateTimeNow_yyyyMMdd());

            } else {
                //This condition will run when date changed
                //if date has been changed while walking then app will upload previous local db data to server
                //After uploading user's data, download new data from server for next date if available on server
                if (ConnectionDetector.isConnectedWithInternet(context))
                    startLocalDataSync("upload");
                else
                    Log.e(TAG, "No internet available at the time of date change. Data need to be uploaded  but failed due to No internet connection");
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "User not logged in. So, stop step count service");
            }
            stopForeground(STOP_FOREGROUND_DETACH);
            stopSelf();
        }
    }

    void updateNotificationToUI(int steps) {
        Intent intent = new Intent();
        intent.putExtra("Steps", steps);
        intent.setAction(context.getPackageName() + ".CUSTOM_INTENT_STEPS");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    StepCountModel.Data makeActivityObject(int steps) {
        double height_in_cm = 0.0;
        double weight_in_kg = 0.0;
        height_in_cm = Double.parseDouble(SessionUtil.getUserHeight(context));
        weight_in_kg = Double.parseDouble(SessionUtil.getUserWeight(context));
        StepCountModel.Data stepCountModelData = new StepCountModel.Data();
        stepCountModelData.setUserID(userID);
        stepCountModelData.setStepsCount("" + Math.abs(steps));
        stepCountModelData.setWaterCount("" + SessionUtil.getWaterIntake(getApplicationContext()));
        stepCountModelData.setUserActivityDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        stepCountModelData.setCalories("" + String.format(Locale.getDefault(), "%.0f", Math.abs(CalculatorUtil.caloriesFromSteps(context, height_in_cm, Double.parseDouble(SessionUtil.getUserWeight(context)), steps))));
        stepCountModelData.setDistance("" + String.format(Locale.getDefault(), "%.0f", Math.abs(CalculatorUtil.getDistanceNow(context, steps, weight_in_kg))));
        stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
        stepCountModelData.setApi_synced_at("");
        stepCountModelData.setIs_day_api_synced("");

        if (location != null) {
            stepCountModelData.setActivityLat(String.valueOf(location.getLatitude()));
            stepCountModelData.setActivityLong(String.valueOf(location.getLongitude()));
        } else {
            stepCountModelData.setActivityLat("0");
            stepCountModelData.setActivityLong("0");
        }
        stepCountModelData.setActivityLocation(makeLocationFromCoordinated());
        return stepCountModelData;
    }

    StepCountModel.Data makeActivityObject(ActivitiesModel activitiesModel) {
        StepCountModel.Data stepCountModelData = new StepCountModel.Data();
        stepCountModelData.setUserID(userID);
        stepCountModelData.setStepsCount("" + Math.abs(activitiesModel.getData().get(0).getStepCountID()));
        stepCountModelData.setWaterCount("" + activitiesModel.getData().get(0).getWaterCount());
        stepCountModelData.setUserActivityDate(activitiesModel.getData().get(0).getUserActivityDate());
        stepCountModelData.setCalories(activitiesModel.getData().get(0).getCalories());
        stepCountModelData.setDistance(activitiesModel.getData().get(0).getDistance());
        stepCountModelData.setUserTimeZone(activitiesModel.getData().get(0).getUserTimeZone());
        stepCountModelData.setApi_synced_at("");
        stepCountModelData.setIs_day_api_synced("");


        stepCountModelData.setActivityLat(activitiesModel.getData().get(0).getActivityLat());
        stepCountModelData.setActivityLong(activitiesModel.getData().get(0).getActivityLong());
        stepCountModelData.setActivityLocation(activitiesModel.getData().get(0).getActivityLocation());
        return stepCountModelData;
    }

    StepCountModel.Data makeActivityObject() {
        StepCountModel.Data stepCountModelData = new StepCountModel.Data();
        stepCountModelData.setUserID(userID);
        stepCountModelData.setStepsCount("0");
        stepCountModelData.setWaterCount("0");
        stepCountModelData.setUserActivityDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        stepCountModelData.setCalories("0");
        stepCountModelData.setDistance("0");
        stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZone());
        stepCountModelData.setApi_synced_at("");
        stepCountModelData.setIs_day_api_synced("");

        if (location != null) {
            stepCountModelData.setActivityLat(String.valueOf(location.getLatitude()));
            stepCountModelData.setActivityLong(String.valueOf(location.getLongitude()));
        } else {
            stepCountModelData.setActivityLat("0");
            stepCountModelData.setActivityLong("0");
        }
        stepCountModelData.setActivityLocation(makeLocationFromCoordinated());
        return stepCountModelData;
    }

    String makeLocationFromCoordinated() {
        if (location != null && location.getLongitude() > 0 && location.getLatitude() > 0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0)
                    return addresses.get(0).getAddressLine(0);
            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
        return "unknown";
    }

    void addActivityDataIntoDB(StepCountModel.Data stepCountModelData, String date) {
        if (dbHelper.isUserStepCountAvailable(String.valueOf(userID), date)) {
            dbHelper.updateActivity(stepCountModelData);
        } else {
            dbHelper.addStepCount(stepCountModelData);
        }
    }

    void getActivityFromServer() {
        Call<ActivitiesModel> getActivityCall = ApiClient.getService().getUserActivity("Bearer " + SessionUtil.getAccessToken(context), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        getActivityCall.enqueue(new Callback<ActivitiesModel>() {
            @Override
            public void onResponse(Call<ActivitiesModel> call, Response<ActivitiesModel> response) {
                try {
                    if (response.isSuccessful()) {
                        ActivitiesModel activitiesModel = response.body();
                        if (activitiesModel != null) {
                            if (activitiesModel.getData() != null) {
                                if (activitiesModel.getData().size() > 0) {
                                    if (activitiesModel.getData().get(0).getStepsCount() != null) {
                                        int downloadedSteps = Integer.parseInt(activitiesModel.getData().get(0).getStepsCount());
                                        SessionUtil.setUsertodaySteps(context, downloadedSteps);
                                        SessionUtil.setUserLogInSteps(context, downloadedSteps);
                                        SessionUtil.setLoggedInStepsFirstTime(context, true);
                                        updateStepCountToNotification(downloadedSteps);
                                    }
                                    addActivityDataIntoDB(makeActivityObject(activitiesModel), activitiesModel.getData().get(0).getUserActivityDate());
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "getActivityFromServer()::activity model data array list is empty");
                                    }
                                    SessionUtil.setUsertodaySteps(context, 0);
                                    SessionUtil.setUserLogInSteps(context, 0);
                                    SessionUtil.setLoggedInStepsFirstTime(context, true);
                                    updateStepCountToNotification(0);
                                    addActivityDataIntoDB(makeActivityObject(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "getActivityFromServer()::activity model data is null");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "getActivityFromServer()::activity model is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "getUserActivity call is not successful");
                        }
                    }
                    if (mWakeLock != null) {
                        mWakeLock.release();
                        mWakeLock = null;
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivitiesModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    void startLocalDataSync(String requestFor) {
        Intent dataSyncServiceIntent = new Intent(context, StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", "step_count_service");
        context.startService(dataSyncServiceIntent);
    }

    static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

    void registerBroadcastReceiverListeners() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Registering listeners");
        }
        // ContextCompat.registerReceiver(context,timerChangeReceiver, s_intentFilter,ContextCompat.RECEIVER_EXPORTED);
        ContextCompat.registerReceiver(context, globalDateChangeReceiver, s_intentFilter, ContextCompat.RECEIVER_EXPORTED);
        LocalBroadcastManager.getInstance(context).registerReceiver(dateChangeReceiver, new IntentFilter(context.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE_SERVICE"));
        LocalBroadcastManager.getInstance(context).registerReceiver(syncActivityBR, new IntentFilter(context.getPackageName() + ".SYNC_ACTIVITY_SERVICE"));
    }

    void unregisterBroadcastReceiverListeners() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Unregistering listeners");
        }
        context.unregisterReceiver(globalDateChangeReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(dateChangeReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(syncActivityBR);
    }

    //Below Broadcast Receiver is for listening uploaded data completion on date change then make retrieve call for next day data
    private final BroadcastReceiver syncActivityBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In syncActivityBR: received Broadcast");
                }
                String requestFor = "";
                if (intent.hasExtra("requestFor")) {
                    requestFor = intent.getStringExtra("requestFor");
                    if (requestFor != null && requestFor.matches("step_count_service")) {
                        if (ConnectionDetector.isConnectedWithInternet(context))
                            getActivityFromServer();
                        else {
                            if (mWakeLock != null) {
                                mWakeLock.release();
                                mWakeLock = null;
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
    };


    private final GCoreWakefulBroadcastReceiver dateChangeReceiver = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(context.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE_SERVICE")) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "StepService::dateChangeReceiver: ACTION_DATE_CHANGED");
                }
                mWakeLock = acquireWakeLock();
                if (ConnectionDetector.isConnectedWithInternet(context)) {
                    startLocalDataSync("upload");
                } else {
                    if (mWakeLock != null) {
                        mWakeLock.release();
                        mWakeLock = null;
                    }
                    Log.e(TAG, "dateChangeReceiver::No internet available at the time of date change. Data need to be uploaded  but failed due to No internet connection");
                }
            }
        }
    };

    PowerManager.WakeLock acquireWakeLock() {
        String WAKE_LOCK_TAG = "STEP_SERVICE:WAKE_LOCK";
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
        startAlarmReset();
        //1 minute is equal to 60*1000L
        wakeLock.acquire(60 * 1000L);
        return wakeLock;
    }

    public void startAlarmReset() {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "StepsService::next alarm set to " + alarmManager.getNextAlarmClock().getTriggerTime());
            }
        } catch (SecurityException se) {
            if (Common.isLoggingEnabled) {
                se.printStackTrace();
            }
        }
    }

    void startLocationUpdates() {
        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(context)) {
            int permission = 0;
            int priority = Priority.PRIORITY_LOW_POWER;

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission = 1;
                priority = Priority.PRIORITY_HIGH_ACCURACY;
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission = 2;
                priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
            }
            if (permission > 0) {
                if (LocationUtil.isLocationEnabled(this)) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Permission allowed and location is ON");
                    }
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location == null) {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "step service::getLastLocation() : location is null");
                                }
                                return;
                            }
                            StepsService.this.location = location;
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "step service::getLastLocation() : location is " + StepsService.this.location.toString());
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                        }
                    });
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationResult == null) {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "step service::LocationCallback() : location result is null");
                                }
                                return;
                            }
                            for (Location location : locationResult.getLocations()) {
                                StepsService.this.location = location;
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "step service::getLastLocation() : location is " + StepsService.this.location.toString());
                                }
                            }
                        }
                    };

                    fusedLocationClient.requestLocationUpdates(createLocationRequest(priority), locationCallback, Looper.getMainLooper());
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Location not enabled");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Location permission not allowed");
                }
            }
        } else {
            if (Common.isLoggingEnabled)
                Log.e(TAG, "Step Service::startLocationUpdates: GPS not available in device");
        }
    }

    private LocationRequest createLocationRequest(int priority) {
        return new LocationRequest.Builder(priority, 10000).build();
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, context)) {
            Intent dataSyncServiceIntent = new Intent(getApplicationContext(), StepCounterDataSync.class);
            dataSyncServiceIntent.putExtra("requestFor", requestFor);
            dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
            context.startService(dataSyncServiceIntent);
        }
    }

    private void setupAPIScheduler() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.scheduleAtFixedRate(() -> {
            handler.post(() -> {
                if(Common.isLoggingEnabled){
                    Log.d(TAG,"Send Step count data to server");
                }
                // Do your stuff here, It gets loop every 15 Minutes
                startSyncDataService("upload", "step_count_service");
            });
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void cancelAllWorkManagerByTag() {
        //WorkManager.getInstance(this).cancelAllWorkByTag(WORK_MANAGER_TAG);
        WorkManager.getInstance(this).cancelAllWork();
    }

    @Override
    public void onDestroy() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Unregistering listeners");
        }
        notificationManager.cancel(notificationId);
        SessionUtil.setStepCountingStopNotification(context, true);
        unregisterBroadcastReceiverListeners();
        stopLocationUpdates();
        super.onDestroy();
    }
}
