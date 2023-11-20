package com.cedricapp.service;

import static android.util.Log.i;
import static com.cedricapp.common.Common.DATE_TIME_FORMAT;
import static com.cedricapp.common.Common.EMAIL_TAG;
import static com.cedricapp.common.Common.EXCEPTION_TAG;
import static com.cedricapp.common.Common.EXCEPT_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ActivitiesModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.activity.SplashActivity;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.NotificationUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.broadcastreceiver.ServiceRestarter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// _________ Extend Service class & implement Service lifecycle callback methods. _________ //
public class StepCountingService extends /*IntentService*/ Service implements SensorEventListener {

    int currentStepsDetected;
    private FirebaseAnalytics mFirebaseAnalytics;
    int stepCounter;
    int newStepCounter;
    Context c = this;
    boolean serviceStopped; // Boolean variable to control the repeating timer.

    private final int mNotificationId = 1;
    NotificationManager notificationManager;
    NotificationUtil nc = new NotificationUtil();
    int countSteps1;
    boolean stepCounterSensorstatus, stepAccelerometerSensorStatus;
    /*private BroadcastReceiver shutdownReceiver;*/

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;

    //peak detection variables
    private double lastXPoint = 1d;
    double stepThreshold = 1.0d;
    double noiseThreshold = 2d;
    private final int windowSize = 10;
    public static float mStepCounter = 0;

    private static final int SMOOTHING_WINDOW_SIZE = 20;
    private final float[] mRawAccelValues = new float[3];
    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    // smoothing accelerometer signal variables
    private final float[][] mAccelValueHistory = new float[3][SMOOTHING_WINDOW_SIZE];
    private final float[] mRunningAccelTotal = new float[3];
    private final float[] mCurAccelAvg = new float[3];
    private int mCurReadIndex = 0;
    private double mGraph1LastXValue = 0d;
    private double mGraph2LastXValue = 0d;
    private double lastMag = 0d;
    private double avgMag = 0d;
    private double netMag = 0d;
    private static Location loc;
    DBHelper dbHelper;

    final static double walkingFactor = 0.57;
    static double strip;
    static double stepCountMile; // step/mile
    static double conversationFactor;
    static double caloriesBurnedPerMile;

    Exception mGlobalEx;

    private FusedLocationProviderClient fusedLocationClient;
    boolean isTimeChangeReceiverRegistered, isDateChangedRecieverRegistered;
    boolean isDateChanged = false;

    long millisecondForRequest;

    long minuteAfter;

    long timeDuration;

    boolean isServiceNeedToBeStopped = false;

    Resources resources;

    String TAG = "STEP_COUNT_SERVICE_TAG";

    String userAddress = "";

    String latitude = "";
    String longitude = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */
    public StepCountingService() {
        // super("StepCountingService");

    }

    // ___________________________________________________________________________ \\
    static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    static IntentFilter s_intentFilter1;

    static {
        s_intentFilter1 = new IntentFilter();
        s_intentFilter1.addAction(Intent.ACTION_SCREEN_ON);
    }

    //Realm_User userGlobal;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        resources = Localization.setLanguage(getApplicationContext(), getResources());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Step counter service on create");
        }


        ForegroundServiceInitialize();
        dbHelper = new DBHelper(this);

        millisecondForRequest = System.currentTimeMillis();

        minuteAfter = 0;

        timeDuration = 300000;

        /* shutdownReceiver = new ShutdownRecevier();*/


        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // --------------------------------------------------------------------------- \\
        // ___ (2) create/instantiate intent. ___ \\
        // Instantiate the intent declared globally, and pass "BROADCAST_ACTION" to the constructor of the intent.
        /* intent = new Intent(Constants.START_ACTION);*/

        /*LocalBroadcastManager.getInstance(c).*/
        registerReceiver(m_timeChangedReceiver, s_intentFilter);
        isTimeChangeReceiverRegistered = true;

        /* LocalBroadcastManager.getInstance(c).*/
        registerReceiver(DateChangeReceiver,
                new IntentFilter(c.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE_SERVICE"));
        isDateChangedRecieverRegistered = true;
        LocalBroadcastManager.getInstance(c).registerReceiver(StepSensorListner,
                new IntentFilter(c.getPackageName() + ".CUSTOM_INTENT_STEPS_SENSOR_LISTENER"));

        // ___________________________________________________________________________ \\

        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (LocationUtil.isLocationEnabled(getApplicationContext())) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    getLocationFromGPS();
                }
            }
        }


    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensorManager = null;
        Sensor stepCounterSensor = null;
        Sensor stepAccelrometerSensor = null;
        int sticky = 1;
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Step counter service onStartCommand");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Activity Recognition permission allowed");
                }
                sticky = checkStepCountOrAccelerometerSensors(intent, sensorManager, stepCounterSensor, stepAccelrometerSensor);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "step counter service on command start, Activity Recognization checked permission and permission denied");
                }
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stopSelf called from onStartCommand");
                }
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Android SDK is lower than Android Q");
            }
            sticky = checkStepCountOrAccelerometerSensors(intent, sensorManager, stepCounterSensor, stepAccelrometerSensor);
        }
        return sticky;
    }

    int checkStepCountOrAccelerometerSensors(Intent intent, SensorManager sensorManager,
                                             Sensor stepCounterSensor, Sensor stepAccelrometerSensor) {
        if (intent != null) {

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


            if (sensorManager != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Sensor Manager Initialized: " + sensorManager.toString());
                }
                stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                if (Common.isLoggingEnabled) {
                    if (stepCounterSensor != null)
                        Log.d(TAG, "Step counter (pedometer) is not null: " + stepCounterSensor.toString());
                }
                if (stepCounterSensor == null) {
                    stepAccelrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Sensor Manager != null and Step counter (pedometer) is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled)
                    Log.e(TAG, "sensorManager is null");
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();
                return START_NOT_STICKY;
            }


            if (Objects.equals(intent.getAction(), Common.START_ACTION)) {
                isServiceNeedToBeStopped = false;
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Step counter service onStartCommand START_ACTION");
                }
                //ForegroundServiceInitialize();

                stepCounterSensorstatus = sensorManager.registerListener(StepCountingService.this, stepCounterSensor, SensorManager.SENSOR_DELAY_GAME);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Mobile has step counter sensor and registered");
                }


                if (stepCounterSensor == null) {
                    stepAccelerometerSensorStatus = sensorManager.registerListener(StepCountingService.this, stepAccelrometerSensor, SensorManager.SENSOR_DELAY_GAME);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Mobile has Accelerometer sensor and registered");
                    }
                }
            } else if (Objects.equals(intent.getAction(), Common.STOP_ACTION)) {

                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "checkStepCountOrAccelerometerSensors in else");
                }
                //unregisterBroadcastReceiver();
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Step counter service onStartCommand in not start action");
                }
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stepCounterSensor unregistered");
                }
                sensorManager.unregisterListener(StepCountingService.this, stepCounterSensor);


                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stepCounterSensor stepAccelrometerSensor");
                }
                sensorManager.unregisterListener(StepCountingService.this, stepAccelrometerSensor);

                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stopSelf called from checkStepCountOrAccelerometerSensors");
                }
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();

                return START_NOT_STICKY;
            }/*else if (intent.hasExtra("STEP_COUNT_FROM_DB_ON_RESUME")) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "STEP_COUNT_FROM_DB_ON_RESUME");
                }
                String stepCountInDB = intent.getStringExtra("STEP_COUNT_FROM_DB_ON_RESUME");
                if (!stepCountInDB.matches("")) {
                    int stepCountFromDB = Integer.parseInt(stepCountInDB);
                    if (stepCountFromDB != SessionUtil.getUserTodaySteps(c)) {
                        updateStepCounter(stepCountFromDB);
                    } else {
                        updateStepCounter(SessionUtil.getUserTodaySteps(c));
                    }
                } else {
                    updateStepCounter(SessionUtil.getUserTodaySteps(c));
                }
            } */

            mSeries1 = new LineGraphSeries<>();
            mSeries2 = new LineGraphSeries<>();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "stepCounterSensorstatus: " + stepCounterSensorstatus + " and stepAccelerometerSensorStatus: " + stepAccelerometerSensorStatus);
            }

            if (stepCounterSensorstatus || stepAccelerometerSensorStatus) {
                SessionUtil.setStepCounterPermission(c, true);
            } else {
                SessionUtil.setStepCounterPermission(c, false);
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stopSelf called from checkStepCountOrAccelerometerSensors::stepCounterSensorstatus");
                }
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();

                return START_NOT_STICKY;
            }
            /*Intent i = new Intent(c, StepCountingStop.class);
            startWakefulService(c, i);*/
            currentStepsDetected = 0;
            stepCounter = 0;
            newStepCounter = 0;
            serviceStopped = false;
            return START_STICKY;
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "intent is null in step counter service on command start");
            }
            isServiceNeedToBeStopped = true;
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
    }


    public class LocalBinder extends Binder {
        public StepCountingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return StepCountingService.this;
        }
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   /* @Override
    protected void onHandleIntent(Intent intent) {

    }*/


    /**
     * Called when the overall system is running low on memory, and actively running processes should trim their memory usage.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /////////////////__________________ Sensor Event. __________________//////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {

        /*if (isDateChanged) {
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                getUserActivityOnDateChanged();
            }
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stopSelf called from onSensorChanged permission not allowed");
                }
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();
            }
        }
        // STEP_COUNTER Sensor.
        // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
        try {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_STEP_COUNTER:
                    countSteps1 = (int) event.values[0];
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "TYPE_STEP_COUNTER: counter Step from pedometer sensor: " + countSteps1);
                    }
                    if (countSteps1 == 0) {
                        int todaySteps = SessionUtil.getUserTodaySteps(c);
                        if (Common.isLoggingEnabled)
                            Log.d(TAG, "TYPE_STEP_COUNTER: Today Steps: " + todaySteps);
                        if (todaySteps != 0) {
                            SessionUtil.setUserLogInSteps(c, todaySteps);
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "step count is zero in getUserTodaySteps");
                            }
                        }
                        SessionUtil.setSensorStaticSteps(c, 0);
                    }
                    updateStepsFromSensor(countSteps1);

                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    mRawAccelValues[0] = event.values[0];
                    mRawAccelValues[1] = event.values[1];
                    mRawAccelValues[2] = event.values[2];


                    lastMag = Math.sqrt(Math.pow(mRawAccelValues[0], 2) + Math.pow(mRawAccelValues[1], 2) + Math.pow(mRawAccelValues[2], 2));

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

                    stepAccelrometer();
            }


        } catch (Exception e) {
            //i(TAG, e.toString());
            if (Common.isLoggingEnabled)
                e.printStackTrace();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
            SessionUtil.setStepCounterPermission(c, false);
            /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, SharedData.caughtException(e));
            }*/
            // ToDo: User Id Expection
            //   CrashLyticsExpLogs(e);
            isServiceNeedToBeStopped = true;
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "stopSelf called from onSensorChanged exception");
            }
            stopForeground(true);
            stopSelf();
        }

    }

    void getLocationFromGPS() {

        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        if (locationManager != null) {
            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Exception on gpsEnabled");
                    ex.printStackTrace();
                }
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
            }

            try {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Exception on networkEnabled");
                    ex.printStackTrace();
                }
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
            }
        }
        if (gpsEnabled && networkEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                loc = location;
                                latitude = "" + loc.getLatitude();
                                longitude = "" + loc.getLongitude();
                                SessionUtil.setLastKnownLocation(c, loc.getLatitude(), loc.getLongitude());
                                Geocoder geocoder = new Geocoder(c, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                                    addresses.get(0).getAddressLine(0);
                                    SessionUtil.setLocation(c, addresses.get(0).getAddressLine(0));
                                    userAddress = addresses.get(0).getAddressLine(0);
                                    //stepCountModelData.setActivityLocation("" + addresses.get(0).getAddressLine(0));
                                } catch (Exception ex) {
                                    if (Common.isLoggingEnabled) {
                                        ex.printStackTrace();
                                    }
                                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
                                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                                    bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                                    mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                                }
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Location toString: " + loc.toString() + "\nLat: " + loc.getLatitude() + " Lng: " + loc.getLongitude());
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.e(TAG, "Location is null");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                            Bundle bundle = new Bundle();
                            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                        }
                    });
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "GPS or network is not enabled");
            }
        }
    }


    private final GCoreWakefulBroadcastReceiver StepSensorListner = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor stepCounterSensor = null;
                if (sensorManager != null) {
                    stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                }
                Sensor stepDetectorSensor = null;
                if (sensorManager != null) {
                    stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                }
                Sensor stepAccelrometerSensor = null;
                if (sensorManager != null && stepCounterSensor == null) {
                    stepAccelrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                }
                boolean stepCounterSensorstatus, stepDetectorSensorstatus;
                if (sensorManager != null) {
                    stepCounterSensorstatus = sensorManager.registerListener(StepCountingService.this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                }
                if (sensorManager != null) {
                    stepDetectorSensorstatus = sensorManager.registerListener(StepCountingService.this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
                }
                if (sensorManager != null && stepCounterSensor == null) {
                    stepAccelerometerSensorStatus = sensorManager.registerListener(StepCountingService.this, stepAccelrometerSensor, SensorManager.SENSOR_DELAY_UI);
                }
                if (stepCounterSensorstatus = true) {
                    SessionUtil.setStepCounterPermission(c, true);
                } else {

                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
                    String currentDateandTime = sdf.format(new Date());

                    serviceStopped = true;
                    notificationManager.cancel(mNotificationId);

                    //sendNotificationCongratulations(c);
                    SessionUtil.setStepCounterPermission(c, false);
                    SessionUtil.setStepsForeGroundService(c, false);
                    SessionUtil.setStepsForeGroundServiceDestroyStatus(c, true);
                    // SessionUtil.setStepsDateTimeToFetechHistoryFromGoogleFit(c, currentDateandTime);
                    SessionUtil.setStepCountingStopNotification(c, true);
                    Intent i = new Intent(c, StepCountingStop.class);
                    startWakefulService(c, i);
                    isServiceNeedToBeStopped = true;
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "stopSelf called from StepSensorListner");
                    }
                    stopForeground(true);
                    stopSelf();

                }
            } catch (Exception ex) {
                i("StepSensorListnerEX", ex.toString());
                //Constants.CrashLyticsExpLogs(ex);
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }

            }
        }
    };


    private final GCoreWakefulBroadcastReceiver DateChangeReceiver = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Now you can call all your fragments method here
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "GCoreWakefulBroadcastReceiver DateChangeReceiver: onReceive");
            }

            try {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wakeLock = null;
                if (powerManager != null) {
                    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":MyWakelockTag");
                    wakeLock.acquire(60 * 1000L);
                }
                isDateChanged = true;
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    getUserActivityOnDateChanged();
                }

                if (wakeLock != null) {
                    wakeLock.release();
                }
            } catch (Exception e) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                SessionUtil.setStepCounterPermission(c, false);
                // Constants.CrashLyticsExpLogs(e);
                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                }
                /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                    , EXCEPTION, SharedData.caughtException(e));
                }*/

            }
        }
    };


    private final GCoreWakefulBroadcastReceiver m_timeChangedReceiver = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String action = intent.getAction();
                if (/*action.equals(Intent.ACTION_DATE_CHANGED)*/action.equals(Intent.ACTION_TIME_CHANGED) ||
                        action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":MyWakelockTag");
                    wakeLock.acquire(60 * 1000L);
                    isDateChanged = true;
                    if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                        getUserActivityOnDateChanged();
                    }
                    wakeLock.release();
                }
            } catch (Exception e) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                SessionUtil.setStepCounterPermission(c, false);
                /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                    , EXCEPTION, SharedData.caughtException(e));
                }*/
//Constants.CrashLyticsExpLogs(e);
                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }
// ___________________________________________________________________________ \\


// --------------------------------------------------------------------------- \\


    public void ForegroundServiceInitialize() {
        Notification notification = getNotification(SessionUtil.getUserTodaySteps(c));
        if (notification != null) {
            startForeground(mNotificationId, notification);
            SessionUtil.setStepsForeGroundService(c, true);
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Notification is null");
            }
            /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, "Notification is null for Foreground at the time of initialization");
            }*/
        }

    }

    private void updateStepCounter(int StepCount) {

        //  startForeground(mNotificationId, getNotification(StepCount));
        notificationManager.notify(mNotificationId, getNotification(StepCount));

    }


    @SuppressLint({"ObsoleteSdkInt", "IconColors"})
    public Notification getNotification(int stepCount) {

        try {
            Boolean isLogIn = SessionUtil.getLoggedStatus(c);
            if (isLogIn) {
                Intent notificationIntent = new Intent(StepCountingService.this, SplashActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("From", "service");
                notificationIntent.putExtras(bundle);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pIntent;
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                pIntent = PendingIntent.getActivity(StepCountingService.this, 0,
                        notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                /*else
                    pIntent = PendingIntent.getActivity(StepCountingService.this, 0,
                            notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);*/

                Notification notification = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    //Creating Channel
                    nc.createMainNotificationChannel(this);
                    notification = new Notification.Builder(c, nc.getMainNotificationId())
                            //.setContentTitle(getResources().getString(R.string.general_today_steps)+" "+SessionUtil.getUserTodaySteps(c)+"")
                            .setContentTitle(resources.getString(R.string.general_today_steps) + " " + stepCount + "")
                            .setContentText(resources.getString(R.string.general_pedometer_session))
                            .setSmallIcon(R.drawable.ic_app_launch)
                            .setContentIntent(pIntent)
                            .setOngoing(true).build();

                } else {
                    //for devices less than API Level 26
                    notification = new Notification.Builder(c)
                            .setPriority(Notification.PRIORITY_MAX)
                            //.setContentTitle(getResources().getString(R.string.general_today_steps)+" "+SessionUtil.getUserTodaySteps(c)+"")
                            .setContentTitle(getResources().getString(R.string.general_today_steps) + " " + stepCount + "")
                            .setContentText(getResources().getString(R.string.general_pedometer_session))
                            .setSmallIcon(R.drawable.ic_app_launch)
                            .setContentIntent(pIntent)
                            .setOngoing(true).build();
                }


                return notification;
            }
        } catch (Exception e) {
            i("StepsCountNotEx", e.toString());
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
            //Constants.CrashLyticsExpLogs(e);
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Exception getNotification()", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, SharedData.caughtException(e));
            }*/

        }
        return null;
    }


    public interface StepsCountResult {
        void onStepsService(int steps);

    }


    /**
     * @param steps updateing steps in local DB and in UI from Step Counter Sensor
     */
    public void updateStepsFromSensor(int steps) {
        try {
            if (!SessionUtil.getUserEmailFromSession(getApplicationContext()).matches("")) {
                int stepCountFromSensor = steps;
                SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMAT, Locale.US);
                String currentDateandTime = sdf.format(new Date());
                String UserLogInDate = SessionUtil.getUserLogInDate(c);
                if (SessionUtil.getLoggedInStepsFirstTime(c)) {
                    SessionUtil.setLoggedInStepsFirstTime(c, false);
                    SessionUtil.setSensorStaticSteps(c, steps);
                }
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "User Login Date : " + UserLogInDate);
                    Log.d(TAG, "Current Date and Time : " + currentDateandTime);
                    Log.d(TAG, "Logged In Steps First Time : " + SessionUtil.getLoggedInStepsFirstTime(c));
                    Log.d(TAG, "Sensor Static Steps : " + SessionUtil.getSensorStaticSteps(c));
                    Log.d(TAG, "User Log In Steps : " + SessionUtil.getUserLogInSteps(c));
                    Log.d(TAG, "User Today Steps : " + SessionUtil.getUserTodaySteps(c));
                }


                //If anyone need to 0 step counter then uncomment below code
                //SessionUtil.setSensorStaticSteps(c, 3583);
                if (UserLogInDate.equals(currentDateandTime)) {
                    int loginSteps = Math.abs(SessionUtil.getUserLogInSteps(c));
                    steps = loginSteps + Math.abs(steps - SessionUtil.getSensorStaticSteps(c));
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "---updateStepsFromSensor: UserLogInDate.equals(currentDateandTime)---");
                        Log.d(TAG, "Steps in updateStepsFromSensor method: " + steps);
                    }
                    if (steps >= 0) {
                        try {
                            updateStepCounter(steps);

                            if (steps == SessionUtil.getUserTodaySteps(c)) {
                                return;
                            }
                            //if (SessionUtil.getLifeCyleStatus(c)) {
                            if (Common.isLoggingEnabled)
                                Log.d(TAG, c.getPackageName() + ".CUSTOM_INTENT_STEPS");
                            Intent intent2 = new Intent();
                            intent2.putExtra("Steps", steps);
                            intent2.setAction(c.getPackageName() + ".CUSTOM_INTENT_STEPS");
                            LocalBroadcastManager.getInstance(c).sendBroadcast(intent2);
                            //}


                        } catch (Exception e) {
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                            Bundle bundle = new Bundle();
                            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                          /*  if (ConnectionDetector.isConnectedWithInternet(c)) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                                , EXCEPTION, SharedData.caughtException(e));
                            }*/
                        }

                        StepCountModel.Data stepCountModelData = new StepCountModel.Data();
                        stepCountModelData.setUserID(Integer.parseInt(SessionUtil.getUserID(c)));
                        stepCountModelData.setStepsCount("" + Math.abs(steps));
                        stepCountModelData.setWaterCount("" + SessionUtil.getWaterIntake(getApplicationContext()));
                        stepCountModelData.setUserActivityDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                        stepCountModelData.setCalories("" + String.format(Locale.US, "%.0f", Math.abs(caloriesCalculatorFromSteps(getHeightInCM(), Double.parseDouble(SessionUtil.getUserWeight(c)), steps))));
                        stepCountModelData.setDistance("" + String.format(Locale.US, "%.0f", Math.abs(getDistanceNow(steps, getHeightInCM()))));
                        stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
                        stepCountModelData.setApi_synced_at("");
                        stepCountModelData.setIs_day_api_synced("");
                        if (loc != null) {
                            stepCountModelData.setActivityLat("" + loc.getLatitude());
                            stepCountModelData.setActivityLong("" + loc.getLongitude());
                            stepCountModelData.setActivityLocation(userAddress);

                        } else {
                            stepCountModelData.setActivityLat(latitude);
                            stepCountModelData.setActivityLong(longitude);
                            stepCountModelData.setActivityLocation(userAddress);
                        }
                        if (!SessionUtil.getUserID(c).matches("")) {
                            if (dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                dbHelper.updateStepCount(stepCountModelData);
                            } else {
                                //stepCountModelData.setCreatedAt("");
                                dbHelper.addStepCount(stepCountModelData);
                            }
                        }

                        SessionUtil.setUsertodaySteps(c, Math.abs(steps));

                        /**
                         *Below code for uploading logs on server after 5 minutes
                         */
                        if (System.currentTimeMillis() > minuteAfter) {
                            if (ConnectionDetector.isConnectedWithInternet(c)) {
                                long currentTime = System.currentTimeMillis();
                                minuteAfter = currentTime + timeDuration;
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "minuteAfter: " + minuteAfter);
                                    Log.d(TAG, "Current time: " + currentTime);
                                }
                                startSyncDataService("upload", "step_count_service");
                                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                                Bundle bundle = new Bundle();
                                bundle.putString("email", SessionUtil.getUserEmailFromSession(c));
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "steps");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT, "Pedometer::User Logged In Date: " + UserLogInDate + ", Step counts that showing to " + SessionUtil.getUserEmailFromSession(c) + ": " + steps + ", Step counts from login shared preference: " + SessionUtil.getUserLogInSteps(c) + ", Step Counts from static step counts shared preference: " + SessionUtil.getSensorStaticSteps(c) + ", Step count from pedometer sensor: " + stepCountFromSensor + ", Today Steps from shared preference: " + SessionUtil.getUserTodaySteps(c));
                                mFirebaseAnalytics.logEvent("steps_count", bundle);
                                /*new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("Step Counter Service", SessionUtil.getUserEmailFromSession(c)
                                                , INFORMATION, "Pedometer::User Logged In Date: " + UserLogInDate + ", Step counts that showing to " + SessionUtil.getUserEmailFromSession(c) + ": " + steps + ", Step counts from login shared preference: " + SessionUtil.getUserLogInSteps(c) + ", Step Counts from static step counts shared preference: " + SessionUtil.getSensorStaticSteps(c) + ", Step count from pedometer sensor: " + stepCountFromSensor + ", Today Steps from shared preference: " + SessionUtil.getUserTodaySteps(c));*/
                            }
                        }

                    }
                    //dbHelper.addStepCount();
                } else {
                    if (ConnectionDetector.isConnectedWithInternet(c)) {
                        getUserActivityOnDateChanged();
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "User is logged out");
                }
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "stopSelf called from updateStepsFromSensor");
                }
                isServiceNeedToBeStopped = true;
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
           /* if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, SharedData.caughtException(ex));
            }*/
        }
    }


    double getHeightInCM() {
        double height_in_cm = 0.0;
        //if (SessionUtil.getUserUnitType(c).matches("Metric"))
        //height_in_cm = Double.parseDouble(SessionUtil.getUserHeight(c));
        //else {
        height_in_cm = Double.parseDouble(SessionUtil.getUserHeight(c));
        //height_in_cm = height_in_cm * 30.48;
        /*}*/
        return height_in_cm;
    }


    /**
     * @param steps
     * @Des updateing steps in local DB and in UI from Accelerometer
     */
    public void updateStepsFromAccelerometerSensor(int steps) {
        try {
            int stepCountFromSensor = steps;
            SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMAT, Locale.US);
            String currentDateandTime = sdf.format(new Date());
            String UserLogInDate = SessionUtil.getUserLogInDate(c);
            if (SessionUtil.getLoggedInStepsFirstTime(c)) {
                SessionUtil.setLoggedInStepsFirstTime(c, false);
                SessionUtil.setSensorStaticSteps(c, steps);
            }
            if (UserLogInDate.equals(currentDateandTime)) {
                int loginSteps = Math.abs(SessionUtil.getUserLogInSteps(c));
                if (Common.isLoggingEnabled)
                    Log.d(TAG, "LoginSteps in updateStepsFromAccelerometerSensor method: " + loginSteps);
                steps = loginSteps + Math.abs(steps);
                if (Common.isLoggingEnabled)
                    Log.d(TAG, "Steps in updateStepsFromAccelerometerSensor method: " + steps);
                if (steps >= 0) {
                    try {

                        updateStepCounter(steps);
                        if (steps == SessionUtil.getUserTodaySteps(c)) {
                            return;
                        }
                        //if (SessionUtil.getLifeCyleStatus(c)) {
                        Intent intent2 = new Intent();
                        intent2.putExtra("Steps", steps);
                        intent2.setAction(c.getPackageName() + ".CUSTOM_INTENT_STEPS");
                        LocalBroadcastManager.getInstance(c).sendBroadcast(intent2);
                        // }

                    } catch (Exception e) {
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                        Bundle bundle = new Bundle();
                        bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                        bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                        mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                        /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                            startSyncDataService("upload", "step_count_service");*/

                            /*new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                            , EXCEPTION, SharedData.caughtException(e));*/
                        //}
                    }

                    StepCountModel.Data stepCountModelData = new StepCountModel.Data();
                    stepCountModelData.setUserID(Integer.parseInt(SessionUtil.getUserID(c)));
                    stepCountModelData.setStepsCount("" + Math.abs(steps));
                    stepCountModelData.setWaterCount("" + SessionUtil.getWaterIntake(getApplicationContext()));
                    stepCountModelData.setUserActivityDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                    stepCountModelData.setCalories("" + String.format(Locale.US, "%.0f", Math.abs(caloriesCalculatorFromSteps(getHeightInCM(), Double.parseDouble(SessionUtil.getUserWeight(c)), steps))));
                    stepCountModelData.setDistance("" + String.format(Locale.US, "%.0f", Math.abs(getDistanceNow(steps, getHeightInCM()))));
                    stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
                    stepCountModelData.setApi_synced_at("");
                    stepCountModelData.setIs_day_api_synced("");
                    if (loc != null) {
                        stepCountModelData.setActivityLat("" + loc.getLatitude());
                        stepCountModelData.setActivityLong("" + loc.getLongitude());
                        stepCountModelData.setActivityLocation(userAddress);

                    } else {
                        stepCountModelData.setActivityLat(latitude);
                        stepCountModelData.setActivityLong(longitude);
                        stepCountModelData.setActivityLocation(userAddress);
                    }
                    //stepCountModelData.setCreatedAt("");
                    if (!SessionUtil.getUserID(c).matches("")) {
                        if (dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                            dbHelper.updateStepCount(stepCountModelData);
                        } else {
                            //stepCountModelData.setCreatedAt("");
                            dbHelper.addStepCount(stepCountModelData);
                        }
                    }

                    SessionUtil.setUsertodaySteps(c, Math.abs(steps));
                    if (System.currentTimeMillis() > minuteAfter) {
                        if (ConnectionDetector.isConnectedWithInternet(c)) {
                            long currentTime = System.currentTimeMillis();
                            minuteAfter = currentTime + timeDuration;
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "minuteAfter: " + minuteAfter);
                                Log.d(TAG, "Current time: " + currentTime);
                            }
                            startSyncDataService("upload", "step_count_service");
                            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", SessionUtil.getUserEmailFromSession(c));
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "steps");
                            bundle.putString(FirebaseAnalytics.Param.CONTENT, "Pedometer::User Logged In Date: " + UserLogInDate + ", Step counts that showing to " + SessionUtil.getUserEmailFromSession(c) + ": " + steps + ", Step counts from login shared preference: " + SessionUtil.getUserLogInSteps(c) + ", Step Counts from static step counts shared preference: " + SessionUtil.getSensorStaticSteps(c) + ", Step count from pedometer sensor: " + stepCountFromSensor + ", Today Steps from shared preference: " + SessionUtil.getUserTodaySteps(c));
                            mFirebaseAnalytics.logEvent("steps_count", bundle);
                            /*new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("Step Counter Service", SessionUtil.getUserEmailFromSession(c)
                                            , INFORMATION, "Accelerometer::User Logged In Date: " + UserLogInDate + ", Step counts that showing to " + SessionUtil.getUserEmailFromSession(c) + ": " + steps + ", Step counts from login shared preference: " + SessionUtil.getUserLogInSteps(c) + ", Step Counts from static step counts shared preference: " + SessionUtil.getSensorStaticSteps(c) + ", Step count from accelerometer sensor: " + stepCountFromSensor + ", Today Steps from shared preference: " + SessionUtil.getUserTodaySteps(c));*/
                        }
                    }
                }
            } else {
                if (ConnectionDetector.isConnectedWithInternet(c)) {
                    getUserActivityOnDateChanged();
                }

            }
        } catch (Exception ex) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(ex.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
            SessionUtil.setStepCounterPermission(c, false);
            //Constants.CrashLyticsExpLogs(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
           /* if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Exception", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, SharedData.caughtException(ex));
            }*/

        }
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        Intent dataSyncServiceIntent = new Intent(getApplicationContext(), StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
        c.startService(dataSyncServiceIntent);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(dataSyncServiceIntent);
        } else {
            getApplicationContext().startService(dataSyncServiceIntent);
        }*/
    }

    public float getDistanceNow(long steps, double height) {
        double stepLength = 0.0;
        if (SessionUtil.getUserUnitType(getApplicationContext()).matches(resources.getString(R.string.imperial))) {
            double heightInCM = height * 30.48;
            stepLength = heightInCM * 0.415;
        } else {
            //Height already in CM
            stepLength = height * 0.415;
        }
        //double stepLenght = (hightCM * 0.3619);
        float Distance = (float) (stepLength * steps);
        return Distance / 100;
    }

    public double caloriesCalculatorFromSteps(double height, double weight, double stepsCount) {

        if (SessionUtil.getUserUnitType(getApplicationContext()).matches(resources.getString(R.string.imperial))) {
            caloriesBurnedPerMile = walkingFactor * weight;
            //strip = height * 0.415;
            double heightinCM = height * 30.48;
            strip = heightinCM * 0.415;
        } else {
            caloriesBurnedPerMile = walkingFactor * (weight * 2.20462);
            strip = height * 0.415;
        }

        stepCountMile = 160934.4 / strip;

        conversationFactor = caloriesBurnedPerMile / stepCountMile;

        caloriesBurnedPerMile = stepsCount * conversationFactor;

        return caloriesBurnedPerMile;
    }

    /**
     * @des Counting steps from Accereometer Data
     */
    private void stepAccelrometer() {

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
         *Threshold, stepThreshold was derived by observing people's step graph
         * ASSUMPTIONS:
         * Phone is held vertically in portrait orientation for better results
         */
        if (SessionUtil.getLoggedStatus(getApplicationContext())) {

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
                        mStepCounter += 1;
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "TYPE_ACCELEROMETER: counter Step: " + mStepCounter);
                        }
                        updateStepsFromAccelerometerSensor((int) mStepCounter);

                    }
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "User is logged out");
            }
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "stopSelf called from stepAccelrometer");
            }
            isServiceNeedToBeStopped = true;
            stopForeground(true);
            stopSelf();
        }
    }

    void getUserActivityOnDateChanged() {
        Call<ActivitiesModel> getActivityCall = ApiClient.getService().getUserActivity("Bearer " + SessionUtil.getAccessToken(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        getActivityCall.enqueue(new Callback<ActivitiesModel>() {
            @Override
            public void onResponse(Call<ActivitiesModel> call, Response<ActivitiesModel> response) {
                try {
                    /*LocalBroadcastManager.getInstance(c).registerReceiver(DateChangeReceiver,
                            new IntentFilter(c.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE"));*/
                    if (response.isSuccessful()) {
                        SessionUtil.setUserLogInDate(getApplicationContext(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                        SessionUtil.setActivityDownloadedDate(getApplicationContext(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                        isDateChanged = false;
                    /*if (!dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                        dbHelper.addStepCount(stepCountModelData);
                    }*/
                        ActivitiesModel activitiesModel = response.body();
                        if (activitiesModel != null && activitiesModel.getData() != null
                                && activitiesModel.getData().size() > 0) {


                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Date Changed Data from server: " + activitiesModel.toString());
                            }

                            int size = activitiesModel.getData().size();
                            //SessionUtil.setStepDaySessionDate(c, activitiesModel.getData().get(0).getUserActivityDate());
                            SessionUtil.setWaterIntake(getApplicationContext(), Integer.parseInt(activitiesModel.getData().get(0).getWaterCount()));
                            StepCountModel.Data stepCountModelData = new StepCountModel.Data();
                            stepCountModelData.setUserID(Integer.parseInt(SessionUtil.getUserID(c)));
                            stepCountModelData.setStepsCount(activitiesModel.getData().get(size - 1).getStepsCount());
                            stepCountModelData.setWaterCount(activitiesModel.getData().get(size - 1).getWaterCount());
                            stepCountModelData.setUserActivityDate(activitiesModel.getData().get(size - 1).getUserActivityDate());
                            stepCountModelData.setCalories(activitiesModel.getData().get(size - 1).getCalories());
                            stepCountModelData.setDistance(activitiesModel.getData().get(size - 1).getDistance());
                            stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
                            stepCountModelData.setApi_synced_at("");
                            stepCountModelData.setIs_day_api_synced("");

                            stepCountModelData.setActivityLat(activitiesModel.getData().get(size - 1).getActivityLat());
                            stepCountModelData.setActivityLong(activitiesModel.getData().get(size - 1).getActivityLong());
                            stepCountModelData.setActivityLocation(activitiesModel.getData().get(size - 1).getActivityLocation());

                            //stepCountModelData.setCreatedAt("");
                            if (dbHelper == null) {
                                dbHelper = new DBHelper(c);
                            }
                           /* if (ConnectionDetector.isConnectedWithInternet(c)) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("Step Counter Service Date Changed get Data from server", SessionUtil.getUserEmailFromSession(c)
                                                , INFORMATION, stepCountModelData.toString());
                            }*/
                            if (!SessionUtil.getUserID(c).matches("")) {
                                if (WeekDaysHelper.getDateTimeNow_yyyyMMdd().equals(activitiesModel.getData().get(size - 1).getUserActivityDate())) {
                                    if (dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                        dbHelper.updateActivity(stepCountModelData);
                                    } else {
                                        dbHelper.addStepCount(stepCountModelData);
                                    }
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "User ID is not available from shared preference");
                                }
                            }
                            SessionUtil.setSensorSteps(c, countSteps1);
                            /*SessionUtil.setGoogleFitTodayStepsSteps(c, 0);*/
                            int StaticSensorSteps = SessionUtil.getSensorStaticSteps(c);

                            if (countSteps1 == 0) {
                                SessionUtil.setSensorStaticSteps(c, 0);
                            }
                            if (countSteps1 > StaticSensorSteps) {
                                SessionUtil.setSensorStaticSteps(c, countSteps1);
                            }
                            try {
                                //SessionUtil.setPostActivityNotificationStatus(c, false);
                                if (!activitiesModel.getData().get(0).getStepsCount().matches("")) {
                                    SessionUtil.setUsertodaySteps(c, Integer.parseInt(activitiesModel.getData().get(0).getStepsCount()));
                                    SessionUtil.setUserLogInSteps(c, Integer.parseInt(activitiesModel.getData().get(0).getStepsCount()));
                                }
                                SessionUtil.setLocation(c, activitiesModel.getData().get(0).getActivityLocation());
                            } catch (Exception e) {
                                if (Common.isLoggingEnabled) {
                                    e.printStackTrace();
                                }
                            }
                            if (!activitiesModel.getData().get(0).getStepsCount().matches("")) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "update step count to notification");
                                }
                                updateStepCounter(Integer.parseInt(activitiesModel.getData().get(0).getStepsCount()));
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "update step count to notification as zero");
                                }
                                updateStepCounter(0);
                            }
                        } else {
                            /*if (ConnectionDetector.isConnectedWithInternet(c)) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("Step Counter Service Date Changed", SessionUtil.getUserEmailFromSession(c)
                                                , INFORMATION, "Activity retrieved from server is null");
                            }*/
                            setStepCountToZero();
                        }
                    } else {
                       /* if (ConnectionDetector.isConnectedWithInternet(c)) {
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("Step Counter Service Date Changed", SessionUtil.getUserEmailFromSession(c)
                                            , INFORMATION, "Activity retrieved from server is null and request unsuccessfull");
                        }*/
                        //setStepCountToZero();
                    }
                } catch (Exception e) {
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                    Bundle bundle = new Bundle();
                    bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                    bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                    mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
                }
            }

            @Override
            public void onFailure(Call<ActivitiesModel> call, Throwable t) {
                //setStepCountToZero();
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                Bundle bundle = new Bundle();
                bundle.putString(EXCEPT_TAG, String.valueOf(t.getMessage()));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
                bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
                mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);

               /* try {
                    if (ConnectionDetector.isConnectedWithInternet(c)) {
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("Step Counter Service Date Changed", SessionUtil.getUserEmailFromSession(c)
                                        , EXCEPTION, SharedData.caughtException(new Exception(t)));
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }*/
            }
        });
    }

    void setStepCountToZero() {
        // if (SessionUtil.getLoggedInFirstTime(c)) {
        mStepCounter = 0;
        SessionUtil.setWaterIntake(getApplicationContext(), 0);
        StepCountModel.Data stepCountModelData = new StepCountModel.Data();
        //SessionUtil.setStepDaySessionDate(c, WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        stepCountModelData.setUserID(Integer.parseInt(SessionUtil.getUserID(c)));
        stepCountModelData.setStepsCount("0");
        stepCountModelData.setWaterCount("0");
        stepCountModelData.setUserActivityDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        stepCountModelData.setCalories("0");
        stepCountModelData.setDistance("0");
        stepCountModelData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
        stepCountModelData.setApi_synced_at("");
        stepCountModelData.setIs_day_api_synced("");
        if (loc != null) {
            stepCountModelData.setActivityLat("" + loc.getLatitude());
            stepCountModelData.setActivityLong("" + loc.getLongitude());
            stepCountModelData.setActivityLocation(userAddress);

        } else {
            stepCountModelData.setActivityLat(latitude);
            stepCountModelData.setActivityLong(longitude);
            stepCountModelData.setActivityLocation(userAddress);
        }
        //stepCountModelData.setCreatedAt("");
        if (!dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(c), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
            dbHelper.addStepCount(stepCountModelData);
        }
        //else
        //dbHelper.updateActivity(stepCountModelData);
        SimpleDateFormat sdf1 = new SimpleDateFormat(Common.DATE_FORMAT, Locale.US);
        String currentDateandTime1 = sdf1.format(new Date());
        SessionUtil.setSensorSteps(c, countSteps1);
        /*SessionUtil.setGoogleFitTodayStepsSteps(c, 0);*/
        int StaticSensorSteps = SessionUtil.getSensorStaticSteps(c);

        if (countSteps1 == 0) {
            SessionUtil.setSensorStaticSteps(c, 0);
        }
        if (countSteps1 > StaticSensorSteps) {
            SessionUtil.setSensorStaticSteps(c, countSteps1);
        }
        try {
            //SessionUtil.setPostActivityNotificationStatus(c, false);

            SessionUtil.setUsertodaySteps(c, 0);
            SessionUtil.setUserLogInSteps(c, 0);
            SessionUtil.setLocation(c, "");
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);

           /* if (ConnectionDetector.isConnectedWithInternet(c)) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Step Counter Service Date Changed", SessionUtil.getUserEmailFromSession(c)
                                , EXCEPTION, SharedData.caughtException(e));
            }*/

        }
        updateStepCounter(0);
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        try {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Step count service has been destroyed");
            }

            serviceStopped = true;
            notificationManager.cancel(mNotificationId);
            SessionUtil.setStepsForeGroundService(c, false);
            SessionUtil.setStepsForeGroundServiceDestroyStatus(c, true);
            SessionUtil.setStepCountingStopNotification(c, true);
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "stopSelf called from onDestroy");
            }

            if (!isServiceNeedToBeStopped)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
                            if (!SessionUtil.getUserEmailFromSession(c).isEmpty()) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, ">=Android Q::Restart Step count Service");
                                }
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction("restartservice");
                                broadcastIntent.setClass(c, ServiceRestarter.class);
                                c.sendBroadcast(broadcastIntent);
                            }
                        }
                    }
                } else {
                    if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
                        if (!SessionUtil.getUserEmailFromSession(c).isEmpty()) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "< Android Q::Restart Step count Service");
                            }
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("restartservice");
                            broadcastIntent.setClass(c, ServiceRestarter.class);
                            c.sendBroadcast(broadcastIntent);
                        }

                    }
                }

        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
        }

        try {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "m_timeChangedReceiver and DateChangeReceiver broadcast receivers unregister");
            }
            unregisterReceiver(m_timeChangedReceiver);
            unregisterReceiver(DateChangeReceiver);
            isTimeChangeReceiverRegistered = false;
            isDateChangedRecieverRegistered = false;
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "m_timeChangedReceiver and DateChangeReceiver broadcast receivers unregistered");
            }
            //Register or UnRegister your broadcast receiver here

        } catch (Exception e) {
            e.printStackTrace();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
            Bundle bundle = new Bundle();
            bundle.putString(EXCEPT_TAG, String.valueOf(e.toString()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exception");
            bundle.putString(EMAIL_TAG, SessionUtil.getUserEmailFromSession(c));
            mFirebaseAnalytics.logEvent(EXCEPTION_TAG, bundle);
        }

        super.onDestroy();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void sendNotificationCongratulations(Context context) {

        String title = SessionUtil.getNotificationMessage(c);
        // String title = getResources().getString(R.string.General_Step_Count_Stop_Notification);
        String messageBody = "Cedric Team";
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("From", "service");
        notificationIntent.putExtras(bundle);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        pIntent = PendingIntent.getActivity(StepCountingService.this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        /*else
            pIntent = PendingIntent.getActivity(StepCountingService.this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);*/

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            int NOTIFICATION_ID = 234;
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            @SuppressLint("WrongConstant")
            NotificationChannel mChannel = new NotificationChannel("006", "Cedric Congratulation", importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "006")
                .setSmallIcon(R.drawable.ic_app_launch)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(006, builder.build());
        }
    }

}
