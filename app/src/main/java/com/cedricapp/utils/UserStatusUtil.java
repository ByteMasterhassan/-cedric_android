package com.cedricapp.utils;

import static com.cedricapp.common.Common.EXCEPTION;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.LogoutInterface;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.UserStatusModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class UserStatusUtil implements LogoutInterface {
    Context context;
    UserStatusInterface userStatusListener;
    MaterialTextView btn_Cancel, btn_Continue, btn_Ok;
    Resources resources;
    // private String message;

    String TAG = "USER_STATUS_TAG";


    public UserStatusUtil(Context context, UserStatusInterface userStatusListener, Resources resources) {
        this.context = context;
        this.userStatusListener = userStatusListener;
        this.resources = resources;
    }

    public void getUserStatus(String accessToken) {
        Call<UserStatusModel> call = ApiClient.getService().getUserStatus(accessToken);
        call.enqueue(new Callback<UserStatusModel>() {
            @Override
            public void onResponse(Call<UserStatusModel> call, Response<UserStatusModel> response) {
                if (response.isSuccessful()) {
                    int count = 0;
                    // message = ResponseStatus.getResponseCodeMessage(response.code());
                    //Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    UserStatusModel userStatusModel = response.body();
                    if (userStatusModel != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "User Status Model: " + userStatusModel.toString());
                            //Log.d(TAG, "Response Status " + message.toString());
                        }
                        if (userStatusModel.getData() != null) {

                            if (userStatusModel.getData().getStatus() != null) {

                                SharedData.userStatus = userStatusModel.getData().getStatus();
                                Log.d(TAG, "user Status " + SharedData.userStatus);
                                SessionUtil.setUserStatus(context, SharedData.userStatus);

                                if (userStatusModel.getData().getStatus().matches("active")) {
                                    if (userStatusModel.getData().getSubscriptionStatus() != null) {
                                        SessionUtil.saveSubscription(context, userStatusModel.getData().getSubscriptionStartsAt(), userStatusModel.getData().getSubscriptionEndsAt());
                                        SharedData.subscription_status = userStatusModel.getData().getSubscriptionStatus();
                                        SharedData.is_dev_mode = userStatusModel.getData().getIsDev();
                                        SharedData.subscription_id = userStatusModel.getData().getSubscriptionId();
                                        SessionUtil.setIsDevStatus(context, SharedData.is_dev_mode);
                                        if (userStatusModel.getData().getSubscriptionEndsAt() != null) {
                                            //String subscriptionStartsAt = userStatusModel.getData().getSubscriptionStartsAt();
                                            String subscriptionEndsAt = userStatusModel.getData().getSubscriptionEndsAt();
                                            if (WeekDaysHelper.isSubscriptionAvailable(WeekDaysHelper.getDateTimeNow(), subscriptionEndsAt)) {
                                                SessionUtil.setSubscriptionAvailabilityStatus(context,true);
                                                SharedData.isSubscriptionAvailable = true;
                                               // DialogUtil.showSubscriptionEndDialogBox(context,resources);
                                                //count++;
                                            }else{
                                                SessionUtil.setSubscriptionAvailabilityStatus(context,false);
                                                SharedData.isSubscriptionAvailable = false;
                                            }
                                        }
                                        // SharedData.subscription_status = "blocked";

                                   /* if (count==0 && SharedData.subscription_status.matches("cancel")) {
                                        showDialogBox();
                                    }*//* else if (SharedData.subscription_status.equals("blocked")) {
                                        showStandardDialog();
                                    }*/

                                        if (userStatusModel.getData().getStatus() != null) {
                                            userStatusListener.getStatus(userStatusModel.getData().getStatus(),
                                                    userStatusModel.getData().getSubscriptionStatus());
                                        }
                                    } else {
                                        DialogUtil.showSubscriptionEndDialogBox(context,resources);
                                    }
                                } else if (userStatusModel.getData().getStatus().matches("blocked")) {
                                    DialogUtil.showUserBlockDialog(context,resources);
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "User Status Model status is null");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "User Status Model get data is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "User Status Model is null");
                        }
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(context, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    LogoutUtil.redirectToLogin(context);

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "User Status Util is not successful");
                    }
                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    //errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                    if (Common.isLoggingEnabled) {
                        if (errorMessageModel != null) {
                           // Log.e(TAG, "" + errorMessageModel);
                        }
                    } else {
                        //message = ResponseStatus.getResponseCodeMessage(response.code());
                        /*if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.e(TAG, "Response Status " + message.toString());
                            }
                        }*/
                        // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserStatusModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (context != null) {
                    new LogsHandlersUtils(context).getLogsDetails("UserStatusUtil_getUserDetails_failure",
                            SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(t));
                }

                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }





    @Override
    public void isLogout(boolean isLoggedOut) {
        if (isLoggedOut) {
            /*SettingFragment settingFragment = new SettingFragment();
            if (context != null) {
                settingFragment.redirectToLogin(context, "dashboard");
            }*/
            if (context != null)
                LogoutUtil.redirectToLogin(context);
        } else {
            // stopLoading();
            if (context != null)
                Toast.makeText(context, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void logoutResponse(String message) {

    }

    @Override
    public void logoutReponseCode(int responseCode) {
        if (responseCode == 401) {
            if (context != null) {
                LogoutUtil.redirectToLogin(context);
                Toast.makeText(context, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void logoutError(Throwable t) {
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
        }
        if (getContext() != null) {
            Toast.makeText(context, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }

    }
}
