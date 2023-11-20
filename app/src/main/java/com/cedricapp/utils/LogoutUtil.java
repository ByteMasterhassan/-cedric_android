package com.cedricapp.utils;

import static android.content.Context.MODE_PRIVATE;
import static com.cedricapp.common.Common.currentUser;
import static com.cedricapp.common.SharedData.isLoginScreen;
import static com.cedricapp.activity.LoginActivity.SHARED_PREF_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.LogoutInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.activity.LoginActivity;
import com.cedricapp.model.LogoutModel;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.service.StepsService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutUtil {

    static String TAG = "LOGOUT_TAG";

    public static void performLogout(String token, LogoutInterface logoutInterface) {
        Call<LogoutModel> logoutModelCall = ApiClient.getService().logout("Bearer " + token);
        logoutModelCall.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Logout response code is " + response.code());
                }
                if (response.isSuccessful()) {
                   /*String message = ResponseStatus.getResponseCodeMessage(response.code());
                   if(Common.isLoggingEnabled){
                       Log.d(TAG,response.raw().message());
                       Log.d(TAG, "Response Status " + message.toString());
                       if(response.body()!=null){
                           Log.d(TAG, "Logout API response: " + response.body().toString());
                       }
                   }*/

                    // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();

                    logoutInterface.logoutReponseCode(response.code());
                    if (response.body() != null && response.body().getMessage() != null) {
                        logoutInterface.isLogout(true);
                        logoutInterface.logoutResponse(response.body().getMessage());
                    } else {
                        logoutInterface.isLogout(false);
                    }

                } else {
                    logoutInterface.logoutReponseCode(response.code());
                    logoutInterface.isLogout(false);
                    if (response.body() != null && response.body().getError() != null) {
                        logoutInterface.isLogout(true);
                        logoutInterface.logoutResponse(response.body().getError());
                    } else {
                        /*String message = ResponseStatus.getResponseCodeMessage(response.code());
                        Log.d(TAG, "Response Status " + message.toString());*/
                        // Toast.makeText(get, message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                logoutInterface.isLogout(false);
                logoutInterface.logoutError(t);
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "onFailure while Logout" + t.getMessage());
                }
            }
        });
    }

    public static void redirectToLogin(Context context) {
        try {
            SharedPreferences sharedPreferences, sharedPreferences2;
            String SHARED_PREF_NAME2 = "log_user_info";
            DBHelper dbHelper = new DBHelper(context);

            if (StepCountServiceUtil.isMyServiceRunning(StepsService.class, context)) {
                StepCountServiceUtil.stopStepCountService(context);
            }

            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            sharedPreferences2 = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
            editor2.clear();
            editor2.apply();

            SessionUtil.setSensorStaticSteps(context, 0);
            SessionUtil.setSelectedDate(context, "");
            SessionUtil.setUserLogInSteps(context, 0);
            SessionUtil.setUsertodaySteps(context, 0);
            SessionUtil.setLoggedEmail(context, "");
            SessionUtil.setShowPermissionDialogAgain(context, true);
            SessionUtil.setLoggedIn(context, false);
            SessionUtil.setlangCode(context, "");
            SessionUtil.SetFoodPreferenceID(context, "");
            SessionUtil.setActivityUploadedDate(context, "");
            SessionUtil.setUnsubscribedPlanID(context, "");
            SessionUtil.setUnsubscribeStatus(context, false);
            SessionUtil.setStepDaySessionDate(context,"");
            dbHelper.logout();
            dbHelper.deleteUser(String.valueOf(currentUser));


            //dbHelper.deleteAllShoppingListData();
            if (!isLoginScreen) {
                Intent intent = new Intent(context, LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }

        } catch (Exception exception) {
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }


    }
}
