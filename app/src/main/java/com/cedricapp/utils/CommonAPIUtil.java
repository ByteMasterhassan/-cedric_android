package com.cedricapp.utils;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.LevelListener;
import com.cedricapp.interfaces.PlanListener;
import com.cedricapp.model.GoalModel;
import com.cedricapp.model.LevelModel;
import com.cedricapp.retrofit.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonAPIUtil {
    static String TAG = "COMMON_API_TAG";
    public  static void getPlans(String auth, PlanListener planListener) {
        Call<GoalModel> call = ApiClient.getService().getGoals("Bearer " + auth);
        call.enqueue(new Callback<GoalModel>() {
            @Override
            public void onResponse(Call<GoalModel> call, Response<GoalModel> response) {
                try {
                    if (response.isSuccessful()) {
                        planListener.planOnSuccess(response.body());
                    } else {
                        planListener.planOnUnSuccess();
                    }
                }catch (Exception ex){
                    if(Common.isLoggingEnabled){
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GoalModel> call, Throwable t) {
                planListener.planOnFailure(t);
            }
        });
    }

    public static void getLevel(String auth, String goalID, LevelListener levelListener) {
        Call<LevelModel> call = ApiClient.getService().getUserLevel("Bearer " + auth, goalID);
        call.enqueue(new Callback<LevelModel>() {
            @Override
            public void onResponse(Call<LevelModel> call, Response<LevelModel> response) {
                try {
                    if (response.isSuccessful()) {
                        levelListener.levelOnSuccess(response.body());
                    } else {
                        levelListener.levelOnUnSuccess();
                    }
                }catch (Exception ex){
                    if(Common.isLoggingEnabled){
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LevelModel> call, Throwable t) {
                levelListener.levelOnFailure(t);
            }
        });
    }

}
