package com.cedricapp.common;

import android.content.Context;
import android.util.Log;

import com.cedricapp.interfaces.UserActivityInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ActivitiesModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity {
    private String TAG = "USER_ACTIVITY";
    Context context;
    UserActivityInterface userActivityInterface;

    public UserActivity(Context context, UserActivityInterface userActivityInterface){
        this.context = context;
        this.userActivityInterface = userActivityInterface;
    }

    private Call<ActivitiesModel> downloadActivityCall;

    public void downloadUserActivity( String activityDate) {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Access Token in getUserActivity: " + SessionUtil.getAccessToken(context));
        }
        downloadActivityCall = ApiClient.getService().getUserActivity("Bearer " + SessionUtil.getAccessToken(context), activityDate);
        downloadActivityCall.enqueue(new Callback<ActivitiesModel>() {
            @Override
            public void onResponse(Call<ActivitiesModel> call, Response<ActivitiesModel> response) {
                if (response.isSuccessful()) {
                    ActivitiesModel downloadModel = response.body();
                    if (downloadModel != null && downloadModel.getData() != null && downloadModel.getData().size() > 0) {
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "DOWNLOADED_USER_ACTIVITY: " + downloadModel.toString());
                        }
                        saveDownloadedActivityDataInDB( downloadModel, activityDate);
                    } else {
                        if (Common.isLoggingEnabled) {
                            if (downloadModel != null) {
                                Log.e(Common.LOG, "DOWNLOADED_USER_ACTIVITY is empty : " + downloadModel.toString());
                            } else {
                                Log.e(Common.LOG, "DOWNLOADED_USER_ACTIVITY is downloadModel is null");
                            }
                        }
                        saveDownloadedNullActivityDataInDB( activityDate);
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivitiesModel> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    void saveDownloadedActivityDataInDB(ActivitiesModel downloadedData, String date) {
        if (!SessionUtil.getUserID(context).matches("")) {
            SessionUtil.setActivityDownloadedDate(context, date);
            int userID = Integer.parseInt(SessionUtil.getUserID(context));

            StepCountModel.Data activityData = new StepCountModel.Data();
            activityData.setUserID(userID);
            activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
            activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
            //String currentDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd().trim();
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "saveDownloadedActivityDataInDB: Current date from android app is " + date);
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
            activityData.setActivityLat(downloadedData.getData().get(size - 1).getActivityLat());
            activityData.setActivityLong(downloadedData.getData().get(size - 1).getActivityLong());
            activityData.setActivityLocation(downloadedData.getData().get(size - 1).getActivityLocation());
            activityData.setUserTimeZone(downloadedData.getData().get(size - 1).getUserTimeZone());

            StepCountModel stepCountModel = new StepCountModel();
            stepCountModel.setData(activityData);

            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Downloaded activity data which made self: " + stepCountModel.toString());
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
                                stepCountModel.getData().setStepsCount(""+addedStepCount);
                            }
                            if (!stepCountModel.getData().getWaterCount().matches("")
                                    && !dbWaterCount.matches("")) {
                                int addedWaterCount = Integer.parseInt(stepCountModel.getData().getWaterCount()) + Integer.parseInt(dbWaterCount);
                                if(addedWaterCount>=16){
                                    stepCountModel.getData().setWaterCount("16");
                                    waterCount = 16;
                                }else{
                                    stepCountModel.getData().setWaterCount(""+addedWaterCount);
                                    waterCount = addedWaterCount;
                                }

                            }
                            if (!stepCountModel.getData().getCalories().matches("")
                                    && !dbCalories.matches("")) {
                                double addedCalories = Double.parseDouble(stepCountModel.getData().getCalories()) + Double.parseDouble(dbCalories);
                                stepCountModel.getData().setCalories(""+addedCalories);
                            }
                            if (!stepCountModel.getData().getDistance().matches("")
                                    && !dbDistance.matches("")) {
                                double addedDistance = Double.parseDouble(stepCountModel.getData().getDistance()) + Double.parseDouble(dbDistance);
                                stepCountModel.getData().setDistance(""+addedDistance);
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
                Log.d(Common.LOG, "Downloaded data from server and saved values in session preference getUserLogInSteps: " + SessionUtil.getUserLogInSteps(context));
                Log.d(Common.LOG, "Downloaded data from server and saved values in session preference getUserTodaySteps: " + SessionUtil.getUserTodaySteps(context));
                Log.d(Common.LOG, "Downloaded data from server and saved values in session preference getWaterIntake: " + SessionUtil.getWaterIntake(context));
            }
            /*if (requestFrom.matches("home"))
                sendMyBroadCast("home");*/
            userActivityInterface.userActivitySync();
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "StepCountDataSync: User ID is empty while saving in local db that was downloaded");
            }
            /*if (requestFrom.matches("home"))
                sendMyBroadCast("home");
            stopService();*/
            userActivityInterface.userActivitySync();
        }
    }

    void saveDownloadedNullActivityDataInDB(String date) {
        if (!SessionUtil.getUserID(context).matches("")) {
            StepCountModel.Data activityData = new StepCountModel.Data();
            activityData.setUserID(Integer.parseInt(SessionUtil.getUserID(context)));
            activityData.setApi_synced_at(WeekDaysHelper.getDateTimeNow());
            activityData.setIs_day_api_synced(WeekDaysHelper.getDateTimeNow());
            //String currentDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd().trim();
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "saveDownloadedNullActivityDataInDB: Current date from android app is " + date);
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
            }


        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "StepCountDataSync::saveDownloadedNullActivityDataInDB: User ID is empty while saving in local db that was downloaded");
            }

            //stopService();
        }
        /*if (requestFrom.matches("home"))
            sendMyBroadCast("home");
        stopService();*/
        userActivityInterface.userActivitySync();

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
        /*if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        stopService();*/
        userActivityInterface.userActivitySync();

    }

    void updateUserActivityInLocalDB_ByActivityDate(String activityDate, boolean serviceNeedToBeStopped) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateAPI_Sync(SessionUtil.getUserID(context), activityDate);

        /*if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        if (serviceNeedToBeStopped) {
            stopService();
        }*/
        userActivityInterface.userActivitySync();
    }

    void createUserActivityInLocalDB(StepCountModel stepCountModel) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.addStepCount(stepCountModel.getData());
        /*if (requestFrom.matches("logout"))
            sendMyBroadCast("logout");
        stopService();*/
        userActivityInterface.userActivitySync();
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
