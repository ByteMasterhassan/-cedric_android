package com.cedricapp.common;

import android.content.Context;
import android.util.Log;

import com.cedricapp.interfaces.StepsAnalyticsInterface;
import com.cedricapp.model.AnalyticsModel;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.SessionUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepsAnalytics {
    static String TAG = "STEPS_ANALYTICS_TAG";

    public static void getAnalytics(Context context, String startDate, String endDate, String activityType, String year, StepsAnalyticsInterface stepsAnalyticsInterface) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Access Token in getDailySteps: " + SessionUtil.getAccessToken(context));
        }
        Call<AnalyticsModel> call;
        if (activityType.matches("yearly"))
            call = ApiClient.getService().getUserAnalytic("Bearer " + SessionUtil.getAccessToken(context), activityType, year);
        else
            call = ApiClient.getService().getUserAnalytic("Bearer " + SessionUtil.getAccessToken(context), startDate, endDate, activityType);

        call.enqueue(new Callback<AnalyticsModel>() {
            @Override
            public void onResponse(Call<AnalyticsModel> call, Response<AnalyticsModel> response) {
                if (response.isSuccessful()) {
                    AnalyticsModel analytics = response.body();
                    if (analytics != null && analytics.getData() != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "getDailySteps(): " + analytics.toString());
                        }
                        stepsAnalyticsInterface.dailyAnalytics(analytics);
                    } else {
                        if (Common.isLoggingEnabled) {
                            if (analytics != null) {
                                Log.e(TAG, "getDailySteps() is empty : " + analytics.toString());
                            } else {
                                Log.e(TAG, "getDailySteps() is downloaded Model is null");
                            }
                        }
                        stepsAnalyticsInterface.stepAnalyticsFailed(response.code(), "Something went wrong");

                    }
                } else if (response.code() == 401) {
                    if (context != null) {
                        LogoutUtil.redirectToLogin(context);
                        //Toast.makeText(context, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    stepsAnalyticsInterface.stepAnalyticsFailed(response.code(), "Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<AnalyticsModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stepsAnalyticsInterface.stepAnalyticsFailed(0, t.getMessage());
            }
        });
    }
}
