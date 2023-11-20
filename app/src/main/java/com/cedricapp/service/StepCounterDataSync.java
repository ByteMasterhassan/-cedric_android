package com.cedricapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.PastActivities;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ActivitiesModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepCounterDataSync extends Service {
    Context context;
    Call<ActivitiesModel> getActivityForUploadingCall;
    Call<ActivitiesModel> downloadActivityCall;
    StepCountModel fetchedActivity;

    Call<StepCountModel> createActivityCall;
    StepCountModel createdActivity;
    Call<StepCountModel> createActivitiesCall;
    StepCountModel createdActivities;
    Call<StepCountModel> updateActivityCall;
    StepCountModel updatedActivity;
    String requestFor;
    String requestFrom;

    String TAG = "DATA_SYNC_TAG";

    public StepCounterDataSync() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In StepCounterDataSync onCreate");
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In StepCounterDataSync onStartCommand");
        }
        this.context = getApplicationContext();
        if(intent!=null) {
            if (intent.hasExtra("requestFor") && intent.hasExtra("requestFrom")) {
                this.requestFor = intent.getExtras().getString("requestFor");
                this.requestFrom = intent.getExtras().getString("requestFrom");
            }
            //this.userActivityInterface = userActivityInterface;
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In StepCounterDataSync: REQUEST_FOR " + requestFor);
            }
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                if (requestFor.matches("download") && !SessionUtil.getActivityDownloadedDate(getApplicationContext()).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "-------------Download activity from server-------------");
                    }
                    downloadUserActivity(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                } else if (requestFor.matches("upload")) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "-------------Upload activity from server-------------");
                    }
                    List<StepCountModel.Data> dates = getDistinctDatesByAPI_SyncedAt();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Activity Dates available in DB are " + dates.toString());
                    }
                    if (dates.size() == 1) {
                        getUserActivityForUploading(dates.get(0).getUserActivityDate(), "");
                    } else if (dates.size() > 1) {
                        getUserActivityForUploading(dates.get(0).getUserActivityDate(), dates.get(dates.size() - 1).getUserActivityDate());
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Sending broadcast screen and stop the service");
                        }
                        //if (requestFrom.matches("home"))
                        sendMyBroadCast(requestFrom);
                        stopService();
                    }
                }

            } else {
                if (Common.isLoggingEnabled)
                    Log.d(TAG, "StepCounterDataSync: No internet connection");
                //if (requestFrom.matches("home"))
                sendMyBroadCast(requestFrom);
                stopService();

            }
        }else{
            if (Common.isLoggingEnabled)
                Log.d(TAG, "StepCounterDataSync: Intent is null");
            stopService();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "CEDRIC_01";
            String channelName = "Cedric Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_app_launch)
                    .setContentTitle("Cedric App uploading step counts")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(100, notification);
        }
    }

    void downloadUserActivity(String activityDate) {
        downloadActivityCall = ApiClient.getService().getUserActivity("Bearer " + SessionUtil.getAccessToken(context), activityDate);
        downloadActivityCall.enqueue(new Callback<ActivitiesModel>() {
            @Override
            public void onResponse(Call<ActivitiesModel> call, Response<ActivitiesModel> response) {
                if (response.isSuccessful()) {
                    ActivitiesModel downloadModel = response.body();
                    if (downloadModel != null && downloadModel.getData() != null && downloadModel.getData().size() > 0) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "DOWNLOADED_USER_ACTIVITY: " + downloadModel.toString());
                        }
                        saveDownloadedActivityDataInDB(downloadModel, activityDate);
                    } else {
                        if (Common.isLoggingEnabled) {
                            if (downloadModel != null) {
                                Log.e(TAG, "DOWNLOADED_USER_ACTIVITY is empty : " + downloadModel.toString());
                            } else {
                                Log.e(TAG, "DOWNLOADED_USER_ACTIVITY is downloadModel is null");
                            }
                        }
                        saveDownloadedNullActivityDataInDB(activityDate);
                    }
                } else {
                    stopService();
                }
            }

            @Override
            public void onFailure(Call<ActivitiesModel> call, Throwable t) {
                if (requestFrom.matches("home"))
                    sendMyBroadCast("home");
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                stopService();
            }
        });
    }


    void saveDownloadedActivityDataInDB(ActivitiesModel downloadedData, String date) {
        if (!SessionUtil.getUserID(getApplicationContext()).matches("")) {
            SessionUtil.setActivityDownloadedDate(getApplicationContext(), date);
            int userID = Integer.parseInt(SessionUtil.getUserID(getApplicationContext()));

            StepCountModel.Data activityData = new StepCountModel.Data();
            activityData.setUserID(userID);
            activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
            activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
            //String currentDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd().trim();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "saveDownloadedActivityDataInDB: Current date from android app is " + date);
            }
            int size = downloadedData.getData().size();
            int stepCount = Integer.parseInt(downloadedData.getData().get(size - 1).getStepsCount());
            int waterCount = Integer.parseInt(downloadedData.getData().get(size - 1).getWaterCount());
            double calories = Double.parseDouble(downloadedData.getData().get(size - 1).getCalories());
            double distance = Double.parseDouble(downloadedData.getData().get(size - 1).getDistance());

            activityData.setWaterCount("" + waterCount);
            activityData.setStepsCount("" + stepCount);
            activityData.setCalories("" + calories);
            activityData.setDistance("" + distance);
            activityData.setUserActivityDate(downloadedData.getData().get(size - 1).getUserActivityDate());
            if (downloadedData.getData().get(size - 1).getActivityLat() != null)
                activityData.setActivityLat(downloadedData.getData().get(size - 1).getActivityLat());
            else
                activityData.setActivityLat("0");

            if(downloadedData.getData().get(size - 1).getActivityLong()!=null)
            activityData.setActivityLong(downloadedData.getData().get(size - 1).getActivityLong());
            else
                activityData.setActivityLong("0");
            activityData.setActivityLocation(downloadedData.getData().get(size - 1).getActivityLocation());
            activityData.setUserTimeZone(downloadedData.getData().get(size - 1).getUserTimeZone());

            StepCountModel stepCountModel = new StepCountModel();
            stepCountModel.setData(activityData);

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Downloaded activity data which made self: " + stepCountModel.toString());
            }
            //List<StepCountModel.Data> activities = getActivityDataFromLocalDB_ByActivityDate();
            if (downloadedData.getData().get(size - 1).getUserActivityDate() != null) {
                if (isDataAvailableInDB(downloadedData.getData().get(size - 1).getUserActivityDate())) {
                    List<StepCountModel.Data> dbDataList = getActivityDataByDateFromLocalDB(downloadedData.getData().get(size - 1).getUserActivityDate());
                    if (dbDataList != null) {
                        if (dbDataList.size() > 0) {
                            String dbStepCount = dbDataList.get(dbDataList.size() - 1).getStepsCount();
                            String dbWaterCount = dbDataList.get(dbDataList.size() - 1).getWaterCount();
                            String dbCalories = dbDataList.get(dbDataList.size() - 1).getCalories();
                            String dbDistance = dbDataList.get(dbDataList.size() - 1).getDistance();
                            if (!stepCountModel.getData().getStepsCount().matches("")
                                    && !dbStepCount.matches("")) {
                                int addedStepCount = Integer.parseInt(stepCountModel.getData().getStepsCount()) + Integer.parseInt(dbStepCount);
                                stepCount = addedStepCount;
                                stepCountModel.getData().setStepsCount("" + addedStepCount);
                            }
                            if (!stepCountModel.getData().getWaterCount().matches("")
                                    && !dbWaterCount.matches("")) {
                                int addedWaterCount = Integer.parseInt(stepCountModel.getData().getWaterCount()) + Integer.parseInt(dbWaterCount);
                                if (addedWaterCount >= 16) {
                                    stepCountModel.getData().setWaterCount("16");
                                    waterCount = 16;
                                } else {
                                    stepCountModel.getData().setWaterCount("" + addedWaterCount);
                                    waterCount = addedWaterCount;
                                }

                            }
                            if (!stepCountModel.getData().getCalories().matches("")
                                    && !dbCalories.matches("")) {
                                double addedCalories = Double.parseDouble(stepCountModel.getData().getCalories()) + Double.parseDouble(dbCalories);
                                stepCountModel.getData().setCalories("" + addedCalories);
                            }
                            if (!stepCountModel.getData().getDistance().matches("")
                                    && !dbDistance.matches("")) {
                                double addedDistance = Double.parseDouble(stepCountModel.getData().getDistance()) + Double.parseDouble(dbDistance);
                                stepCountModel.getData().setDistance("" + addedDistance);
                            }
                        }
                    }
                    if (stepCount > 0 && calories > 0 && distance > 0) {
                        SessionUtil.setUserLogInSteps(context, stepCount);
                        SessionUtil.setUsertodaySteps(context, stepCount);
                        SessionUtil.setWaterIntake(context, waterCount);
                        updateUserActivityInLocalDB(stepCountModel);
                    }
                } else {
                    SessionUtil.setUserLogInSteps(context, stepCount);
                    SessionUtil.setUsertodaySteps(context, stepCount);
                    SessionUtil.setWaterIntake(context, waterCount);
                    createUserActivityInLocalDB(stepCountModel);
                }
            }
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Downloaded data from server and saved values in session preference getUserLogInSteps: " + SessionUtil.getUserLogInSteps(context));
                Log.d(TAG, "Downloaded data from server and saved values in session preference getUserTodaySteps: " + SessionUtil.getUserTodaySteps(context));
                Log.d(TAG, "Downloaded data from server and saved values in session preference getWaterIntake: " + SessionUtil.getWaterIntake(context));
            }
            if (requestFrom.matches("home"))
                sendMyBroadCast("home");
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "StepCountDataSync: User ID is empty while saving in local db that was downloaded");
            }
            if (requestFrom.matches("home"))
                sendMyBroadCast("home");
            stopService();
        }
    }

    void saveDownloadedNullActivityDataInDB(String date) {
        if (!SessionUtil.getUserID(getApplicationContext()).matches("")) {
            StepCountModel.Data activityData = new StepCountModel.Data();
            activityData.setUserID(Integer.parseInt(SessionUtil.getUserID(getApplicationContext())));
            activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
            activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
            //String currentDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd().trim();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "saveDownloadedNullActivityDataInDB: Current date from android app is " + date);
            }

            activityData.setWaterCount("0");
            activityData.setStepsCount("0");
            activityData.setCalories("0.0");
            activityData.setDistance("0.0");
            activityData.setUserActivityDate(date);
            activityData.setActivityLat("0.0");
            activityData.setActivityLong("0.0");
            activityData.setActivityLocation("");
            activityData.setUserTimeZone("");
            StepCountModel stepCountModel = new StepCountModel();
            stepCountModel.setData(activityData);

            if (!isDataAvailableInDB(date)) {
                SessionUtil.setUserLogInSteps(context, 0);
                SessionUtil.setUsertodaySteps(context, 0);
                SessionUtil.setWaterIntake(context, 0);
                createUserActivityInLocalDB(stepCountModel);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Activity data of " + date + ": data is already available in DB. So, set shared preference data for step counts to zero");
                }
                SessionUtil.setUserLogInSteps(context, 0);
                SessionUtil.setUsertodaySteps(context, 0);
                SessionUtil.setWaterIntake(context, 0);
            }


        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "StepCountDataSync::saveDownloadedNullActivityDataInDB: User ID is empty while saving in local db that was downloaded");
            }

            stopService();
        }
        if (requestFrom.matches("home"))
            sendMyBroadCast("home");
        stopService();

    }

    void sendMyBroadCast(String requestFor) {
        Intent intent = new Intent();
        //intent.putExtra("activityData",fetchedActivity.getData());
        intent.putExtra("requestFor", requestFor);
        intent.setAction(getPackageName() + ".SYNC_ACTIVITY_SERVICE");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        //stopService();
    }

    void getUserActivityForUploading(String startWith, String endWith) {
        if (Common.isLoggingEnabled)
            Log.d(TAG, "Access Token in getUserActivity: " + SessionUtil.getAccessToken(context));
        if (endWith.matches("")) {
            getActivityForUploadingCall = ApiClient.getService().getUserActivity("Bearer " + SessionUtil.getAccessToken(context), startWith);
        } else {
            getActivityForUploadingCall = ApiClient.getService().getUserActivities("Bearer " + SessionUtil.getAccessToken(context), startWith, endWith);
        }
        getActivityForUploadingCall.enqueue(new Callback<ActivitiesModel>() {
            @Override
            public void onResponse(Call<ActivitiesModel> call, Response<ActivitiesModel> response) {
                if (response.isSuccessful()) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "getUserActivity isSuccessful()");
                    }
                    ActivitiesModel fetchedActivityModel = response.body();
                    if (fetchedActivityModel != null) {
                        Log.d(TAG, "User Activity from server " + fetchedActivityModel.toString());
                    } else {
                        Log.e(TAG, "StepCounterDataSync: fetchedActivityModel: User Activity from server is null");
                    }
                    if (requestFor.matches("upload")) {
                        List<StepCountModel.Data> activities = getDistinctDatesByAPI_SyncedAt();

                        // List<String> datesInDB = getDistinctDatesByAPI_SyncedAt();
                        if (activities.size() == 1) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "database has only one date for data uploading");
                            }
                            if (fetchedActivityModel != null && fetchedActivityModel.getData() != null && fetchedActivityModel.getData().size() > 0) {
                                updateUserActivityOnServer(activities.get(0), true);

                            } else {
                                createUserActivityOnServer(activities.get(0));
                            }
                        } else if (activities.size() > 1) {

                            List<String> activitiesAvailableOnServer = new ArrayList<>();
                            //List<String> activitiesNotAvailableOnServer = new ArrayList<>();
                            if (fetchedActivityModel != null
                                    && fetchedActivityModel.getData() != null
                                    && fetchedActivityModel.getData().size() > 0) {
                                int days = WeekDaysHelper.getCountOfDaysBtwTwoDates(startWith, endWith);

                                for (int i = 0; i < fetchedActivityModel.getData().size(); i++) {
                                    for (int j = 0; j < activities.size(); j++) {
                                        if (fetchedActivityModel.getData().get(i).getUserActivityDate().trim().equals(activities.get(j).getUserActivityDate().trim())) {
                                            if (!activitiesAvailableOnServer.contains(activities.get(j).getUserActivityDate().trim()))
                                                activitiesAvailableOnServer.add(activities.get(j).getUserActivityDate());
                                        }
                                    }
                                }
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Days b/w " + startWith + " and " + endWith + ": " + days);
                                    Log.d(TAG, "Available on server: " + activitiesAvailableOnServer.toString());
                                    // Log.d(TAG, "Un Available on server: " + activitiesNotAvailableOnServer.toString());

                                }
                                if (activitiesAvailableOnServer.size() == days) {
                                    for (int i = 0; i < activitiesAvailableOnServer.size(); i++) {
                                        if (i == activitiesAvailableOnServer.size() - 1) {
                                            updateUserActivityOnServer(activities.get(0), true);
                                        } else {
                                            updateUserActivityOnServer(activities.get(0), false);
                                        }
                                    }
                                } else if (activitiesAvailableOnServer.size() < days) {
                                    int dayCount = days - activitiesAvailableOnServer.size();
                                    String[] stepCounts = new String[dayCount];
                                    String[] waterCounts = new String[dayCount];
                                    String[] distances = new String[dayCount];
                                    String[] timeZones = new String[dayCount];
                                    String[] calories = new String[dayCount];
                                    String[] latitudes = new String[dayCount];
                                    String[] longitudes = new String[dayCount];
                                    String[] locations = new String[dayCount];
                                    String[] activityDates = new String[dayCount];
                                    List<String> dataNotAvailableOnServer = new ArrayList<>();
                                    List<StepCountModel.Data> steps = new ArrayList<>();
                                    for (int i = 0; i < activities.size(); i++) {
                                        if (!activitiesAvailableOnServer.contains(activities.get(i).getUserActivityDate().trim())) {
                                            if (!dataNotAvailableOnServer.contains(activities.get(i).getUserActivityDate().trim())) {
                                                dataNotAvailableOnServer.add(activities.get(i).getUserActivityDate().trim());
                                                steps.add(activities.get(i));
                                            }
                                        }
                                    }
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "Data not available on Server are " + dataNotAvailableOnServer.toString());
                                    }
                                    if (dataNotAvailableOnServer.size() == dayCount) {
                                        for (int i = 0; i < dayCount; i++) {

                                            stepCounts[i] = steps.get(i).getStepsCount();
                                            waterCounts[i] = steps.get(i).getWaterCount();
                                            distances[i] = steps.get(i).getDistance();
                                            timeZones[i] = steps.get(i).getUserTimeZone();
                                            calories[i] = steps.get(i).getCalories();
                                            latitudes[i] = steps.get(i).getActivityLat();
                                            longitudes[i] = steps.get(i).getActivityLong();
                                            locations[i] = steps.get(i).getActivityLocation();
                                            activityDates[i] = steps.get(i).getUserActivityDate();
                                        }
                                        PastActivities pastActivities = new PastActivities("" + dayCount, stepCounts, waterCounts, distances, timeZones, calories, latitudes, longitudes, locations, activityDates);
                                        createUserActivities(pastActivities);
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "dataNotAvailableOnServer.size(): " + dataNotAvailableOnServer.size() + " and day count(s) is/are " + dayCount);
                                        }
                                        stopService();
                                    }

                                } else {
                                    stopService();
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Response is null on server when uploading data activities.size() > 1");
                                }
                                //if (requestFrom.matches("home"))
                                sendMyBroadCast(requestFrom);
                                stopService();
                            }
                        } else {
                            //saveActivityDataInDB(fetchedActivityModel, startWith);
                            stopService();
                        }
                    } else {
                        stopService();
                    }

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "User Activity fetched from server: " + fetchedActivityModel.toString());
                    }
                } else {

                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getUserActivityForUpload: Request is not successful and response code is " + response.code());
                    }

                    stopService();
                    /*if(Common.isLoggingEnabled)
                        Log.d(TAG,"getUserActivity: Request is not successful");*/
                }

            }

            @Override
            public void onFailure(Call<ActivitiesModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                //if (requestFor.matches("download"))
                //stopService();
            }
        });
    }


    void saveActivityDataInDB(ActivitiesModel fetchedActivityModel, String startWith) {
        if (!SessionUtil.getUserID(context).matches("")) {
            if (!SessionUtil.getActivityUploadedDate(getApplicationContext()).equals(startWith)) {
                int userID = Integer.parseInt(SessionUtil.getUserID(context));
                if (fetchedActivityModel != null && fetchedActivityModel.getData() != null
                        && fetchedActivityModel.getData().size() > 0) {
                    SessionUtil.setActivityUploadedDate(getApplicationContext(), startWith);

                    int temp = 0;
                    int stepCount = 0;
                    int waterCount = 0;
                    double distance = 0.0;
                    double calories = 0.0;
                    fetchedActivity = new StepCountModel();
                    fetchedActivity.setMessage(fetchedActivityModel.getMessage());

                    StepCountModel.Data activityData = new StepCountModel.Data();
                    activityData.setUserID(userID);
                    activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
                    activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
                    //String currentDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd().trim();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Current date from android app is " + startWith);
                    }

                            /*for (int i = 0; i < fetchedActivityModel.getData().size(); i++) {
                                if (fetchedActivityModel.getData().get(i).getUserActivityDate() != null) {
                                    if (fetchedActivityModel.getData().get(i).getUserActivityDate().trim().equals(startWith)) {
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "Activity date from server is " + fetchedActivityModel.getData().get(i).getUserActivityDate());
                                        }*//*fetchedActivityModel.getData().get(i).setUserID(userID);
                                        fetchedActivityModel.getData().get(i).setApi_synced_at(WeekDaysHelper.getDateTimeNow());
                                        fetchedActivityModel.getData().get(i).setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());*//*
                                        stepCount = stepCount + Integer.parseInt(fetchedActivityModel.getData().get(i).getStepsCount());
                                        waterCount = waterCount + Integer.parseInt(fetchedActivityModel.getData().get(i).getWaterCount());
                                        distance = distance + Double.parseDouble(fetchedActivityModel.getData().get(i).getDistance());
                                        calories = calories + Double.parseDouble(fetchedActivityModel.getData().get(i).getCalories());

                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "------Details from server-----\nStep Counts: " + fetchedActivityModel.getData().get(i).getStepsCount() + "\nWater Count: " + fetchedActivityModel.getData().get(i).getWaterCount() + "\ndistance: " + fetchedActivityModel.getData().get(i).getDistance() + "\nCalories: " + fetchedActivityModel.getData().get(i).getCalories());

                                            Log.d(TAG, "Step Counts: " + stepCount + "\nWater Count: " + waterCount + "\ndistance: " + distance + "\nCalories: " + calories);
                                        }
                                        if (temp == 0) {
                                            temp++;
                                            activityData.setUserTimeZone(fetchedActivityModel.getData().get(i).getUserTimeZone());
                                            activityData.setUserActivityDate(fetchedActivityModel.getData().get(i).getUserActivityDate());
                                            activityData.setActivityLat(fetchedActivityModel.getData().get(i).getActivityLat());
                                            activityData.setActivityLong(fetchedActivityModel.getData().get(i).getActivityLong());
                                            activityData.setActivityLocation(fetchedActivityModel.getData().get(i).getActivityLocation());
                                        }

                                    }
                                }
                            }
                            activityData.setWaterCount("" + waterCount);
                            activityData.setStepsCount("" + stepCount);
                            activityData.setCalories("" + calories);
                            activityData.setDistance("" + distance);*/

                    stepCount = Integer.parseInt(fetchedActivityModel.getData().get(0).getStepsCount());
                    waterCount = Integer.parseInt(fetchedActivityModel.getData().get(0).getWaterCount());
                    calories = Double.parseDouble(fetchedActivityModel.getData().get(0).getCalories());
                    distance = Double.parseDouble(fetchedActivityModel.getData().get(0).getDistance());

                    activityData.setWaterCount("" + waterCount);
                    activityData.setStepsCount("" + stepCount);
                    activityData.setCalories("" + calories);
                    activityData.setDistance("" + distance);
                    activityData.setUserActivityDate(fetchedActivityModel.getData().get(0).getUserActivityDate());
                    activityData.setActivityLat(fetchedActivityModel.getData().get(0).getActivityLat());
                    activityData.setActivityLong(fetchedActivityModel.getData().get(0).getActivityLong());
                    activityData.setActivityLocation(fetchedActivityModel.getData().get(0).getActivityLocation());
                    activityData.setUserTimeZone(fetchedActivityModel.getData().get(0).getUserTimeZone());

                    fetchedActivity.setData(activityData);

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Fetched activity data which made self: " + fetchedActivity.toString());
                    }
                    //List<StepCountModel.Data> activities = getActivityDataFromLocalDB_ByActivityDate();
                    if (isDataAvailableInDB(startWith)) {
                        if (stepCount > 0 && calories > 0 && distance > 0) {
                            SessionUtil.setUserLogInSteps(context, stepCount);
                            SessionUtil.setUsertodaySteps(context, stepCount);
                            SessionUtil.setWaterIntake(context, waterCount);
                            updateUserActivityInLocalDB(fetchedActivity);
                        }
                    } else {
                        SessionUtil.setUserLogInSteps(context, stepCount);
                        SessionUtil.setUsertodaySteps(context, stepCount);
                        SessionUtil.setWaterIntake(context, waterCount);
                        createUserActivityInLocalDB(fetchedActivity);
                    }
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "User Log In Steps gotten from server: " + SessionUtil.getUserLogInSteps(context));
                        Log.d(TAG, "User today Steps gotten from server: " + SessionUtil.getUserTodaySteps(context));
                        Log.d(TAG, "Water Intake gotten from server: " + SessionUtil.getWaterIntake(context));
                    }
                    sendMyBroadCast(requestFrom);

                } else {
                    if (!isDataAvailableInDB(startWith)) {
                        SessionUtil.setUserLogInSteps(context, 0);
                        SessionUtil.setUsertodaySteps(context, 0);
                        SessionUtil.setWaterIntake(context, 0);
                        fetchedActivity = new StepCountModel();
                        fetchedActivity.setMessage("");
                        StepCountModel.Data activityData = new StepCountModel.Data();
                        activityData.setUserID(userID);
                        activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
                        activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
                        activityData.setWaterCount("0");
                        activityData.setStepsCount("0");
                        activityData.setCalories("0");
                        activityData.setDistance("0");
                        activityData.setUserActivityDate(startWith);
                        activityData.setActivityLat("0");
                        activityData.setActivityLong("0");
                        activityData.setActivityLocation("");
                        activityData.setUserTimeZone(WeekDaysHelper.getTimeZoneID());
                        fetchedActivity.setData(activityData);
                        createUserActivityInLocalDB(fetchedActivity);
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "StepCountRequestManager: fetchedActivity == null OR fetchedActivity.getData() == null or fetchedActivity.getData() size is zero");
                    }
                }
                sendMyBroadCast(requestFrom);
                stopService();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "StepCountRequestManager: User activity date is not same with uploading date and system date");
                }
                stopService();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "StepCountRequestManager: User ID is empty while saving in local db");
            }
            stopService();
        }
    }

    void stopService() {
        //stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "onDestroy called");
        }
    }

    void createUserActivityOnServer(StepCountModel.Data activity) {
        createActivityCall = ApiClient.getService().createNewUserActivity("Bearer " + SessionUtil.getAccessToken(context), activity.getStepsCount(), activity.getWaterCount(), activity.getDistance(), WeekDaysHelper.getTimeZoneID(), activity.getCalories(), activity.getActivityLat(), activity.getActivityLong(), activity.getActivityLocation(), activity.getUserActivityDate());
        createActivityCall.enqueue(new Callback<StepCountModel>() {
            @Override
            public void onResponse(Call<StepCountModel> call, Response<StepCountModel> response) {
                if (response.isSuccessful()) {
                    createdActivity = response.body();
                    if (createdActivity != null) {
                        StepCountModel.Data data = activity;
                        data.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
                        data.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
                        createdActivity.setData(data);
                        updateUserActivityInLocalDB(createdActivity);
                        // userActivityInterface.userActivitySync(fetchedActivity.getData(), "downloaded");
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "User Activity response is null on created");
                        }
                    }
                    if (Common.isLoggingEnabled) {
                        if (createdActivity != null)
                            Log.d(TAG, "User Activity created on server: " + createdActivity.toString());
                    }
                } else {
                    if (response.code() == 404) {
                        try {
                            Gson gson = new GsonBuilder().create();
                            StepCountModel stepCountModel = new StepCountModel();
                            stepCountModel = gson.fromJson(response.errorBody().string(), StepCountModel.class);
                            createdActivity = stepCountModel;
                        } catch (Exception exception) {
                            if (Common.isLoggingEnabled)
                                exception.printStackTrace();
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "createUserActivity: Request is not successful");
                        }
                    }

                }
                stopService();
            }

            @Override
            public void onFailure(Call<StepCountModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                stopService();
            }
        });
    }

    void createUserActivities(PastActivities pastActivities) {
        createActivitiesCall = ApiClient.getService().createPastActivities("Bearer " + SessionUtil.getAccessToken(context), pastActivities.getNoOfDays(), pastActivities.getStepCounts(), pastActivities.getWaterCounts(), pastActivities.getDistances(), pastActivities.getTimeZones(), pastActivities.getCalories(), pastActivities.getLatitudes(), pastActivities.getLongitudes(), pastActivities.getLocations(), pastActivities.getActivityDates());
        createActivitiesCall.enqueue(new Callback<StepCountModel>() {
            @Override
            public void onResponse(Call<StepCountModel> call, Response<StepCountModel> response) {
                if (response.isSuccessful()) {
                    createdActivities = response.body();
                    if (createdActivities != null) {
                        if (createdActivities.getMessage() != null) {
                            int size = Integer.parseInt(pastActivities.getNoOfDays());
                            for (int i = 0; i < size; i++) {
                                if (i < (size - 1)) {
                                    updateUserActivityInLocalDB_ByActivityDate(pastActivities.getActivityDates()[i], false);
                                } else {
                                    updateUserActivityInLocalDB_ByActivityDate(pastActivities.getActivityDates()[i], true);
                                }
                            }

                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Create Activities response: " + createdActivities.getMessage());
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Create Activities response createdActivities.getMessage() is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Create Activities response createdActivities is null");
                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Create Activities API not successful and response code is " + response.code());
                    }
                }
                //stopService();
            }

            @Override
            public void onFailure(Call<StepCountModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                stopService();
            }
        });
    }

    void updateUserActivityOnServer(StepCountModel.Data activity, boolean serviceNeedToBeStopped) {
        if(activity.getActivityLat()==null)
            activity.setActivityLat("0");
        if(activity.getActivityLong()==null)
            activity.setActivityLong("0");
        updateActivityCall = ApiClient.getService().updateUserActivity("Bearer " + SessionUtil.getAccessToken(context), activity.getStepsCount(), activity.getWaterCount(), activity.getDistance(), WeekDaysHelper.getTimeZoneID(), activity.getCalories(), activity.getActivityLat(), activity.getActivityLong(), activity.getActivityLocation(), activity.getUserActivityDate());
        updateActivityCall.enqueue(new Callback<StepCountModel>() {
            @Override
            public void onResponse(Call<StepCountModel> call, Response<StepCountModel> response) {
                if (response.isSuccessful()) {
                    updatedActivity = response.body();

                    if (updatedActivity != null) {
                        /*StepCountModel.Data data = activity;
                        data.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
                        data.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
                        updatedActivity.setData(data);
                        updateUserActivityInLocalDB(updatedActivity);*/
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "updatedActivity response is " + updatedActivity.toString());
                        }
                        updateUserActivityInLocalDB_ByActivityDate(activity.getUserActivityDate(), serviceNeedToBeStopped);
                        if (updatedActivity.getMessage() != null) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "UpdateActivity Message is " + updatedActivity.getMessage());
                            }

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "UpdateActivity Message is null on Update Activity");
                            }
                        }

                        //userActivityInterface.userActivitySync(updatedActivity.getData(), "uploaded");
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "User Activity response is null on update");
                        }
                    }
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "User Activity updated from server: " + updatedActivity.toString());
                    }
                } else {
                    if (response.code() == 404) {
                        try {
                            Gson gson = new GsonBuilder().create();
                            StepCountModel stepCountModel = new StepCountModel();
                            stepCountModel = gson.fromJson(response.errorBody().string(), StepCountModel.class);
                            updatedActivity = stepCountModel;
                        } catch (Exception exception) {
                            if (Common.isLoggingEnabled)
                                exception.printStackTrace();
                            stopService();
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "updateUserActivity: Request is not successful");
                        }
                    }
                    stopService();
                    /*if(Common.isLoggingEnabled)
                        Log.d(TAG,"updateUserActivity: Request is not successful");*/
                }
                //stopService();
            }

            @Override
            public void onFailure(Call<StepCountModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                stopService();
            }
        });
    }

    List<StepCountModel.Data> getActivityFromLocalDB_ByAPI_Synced() {
        DBHelper dbHelper = new DBHelper(context);
        List<StepCountModel.Data> activities = new ArrayList<>();
        activities = dbHelper.getUserActivityByUserID_ByAPI_SyncedAt(SessionUtil.getUserID(context));
        return activities;
        /*for(int i=0; i<activities.size();i++){
            createUserActivity(activities.get(i));
        }*/
    }

    List<StepCountModel.Data> getDistinctDatesByAPI_SyncedAt() {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getUserActivityByUserID_ByAPI_SyncedAt_Distinct(SessionUtil.getUserID(context));
    }

    List<StepCountModel.Data> getActivityDataFromLocalDB_ByActivityDate() {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(context), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
    }

    void updateUserActivityInLocalDB(StepCountModel stepCountModel) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateActivity(stepCountModel.getData());
        if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        stopService();

    }

    void updateUserActivityInLocalDB_ByActivityDate(String activityDate, boolean serviceNeedToBeStopped) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateAPI_Sync(SessionUtil.getUserID(context), activityDate);

        if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        else if (requestFrom.matches("step_count_service"))
            sendMyBroadCast("step_count_service");
        if (serviceNeedToBeStopped) {
            stopService();
        }
    }

    void createUserActivityInLocalDB(StepCountModel stepCountModel) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.addStepCount(stepCountModel.getData());
        if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        stopService();
    }

    List<StepCountModel.Data> getActivityDataByDateFromLocalDB(String date) {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(context), date);
    }

    boolean isDataAvailableInDB(String date) {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(context), date);
    }
}
