package com.cedricapp.utils;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.GetUserDetailsBack;
import com.cedricapp.interfaces.UserDetailsListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.UserDetailModel;
import com.cedricapp.retrofit.ApiClient;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsUtil {
    Call<UserDetailModel> loginCall;
    Call<UserDetailModel> logoutCall;
    Call<LoginResponse> getUserDetailsCall;
    Context context;

    private DBHelper dbHelper;
    private String token, refresh_token;
    private Boolean is_logged_in;
    private String tokenExpiryDate;
    GetUserDetailsBack userDetails;
    /* private String message;*/

    public UserDetailsUtil(Context context) {
        this.context = context;


    }

    public UserDetailsUtil(Context context, GetUserDetailsBack getUserDetailsBack) {
        this.context = context;
        this.userDetails = getUserDetailsBack;

    }

    public void sendUserDetail(String authorization, String agent, String os, String deviceId, String location, String timezone, String fcm_id, boolean isLoggedIn, boolean is_cancel, String versionName, String versionCode, String os_type, String requestFrom) {
        loginCall = ApiClient.getService().createUserDetails(authorization, agent, os, deviceId, location, timezone, fcm_id, is_cancel, versionName, versionCode, os_type);
        loginCall.enqueue(new Callback<UserDetailModel>() {
            @Override
            public void onResponse(Call<UserDetailModel> call, Response<UserDetailModel> response) {
                if (response.isSuccessful()) {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "User Details successful Response: " + response.body().toString());
                    }
                    //message = ResponseStatus.getResponseCodeMessage(response.code());
                    //Log.d(Common.LOG, "Response Status " + message.toString());
                    //Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                    UserDetailModel userDetailModel = response.body();
                    if (userDetailModel != null && userDetailModel.getData() != null) {
                        if (userDetailModel.getMessage() != null) {
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "msg" + userDetailModel.getMessage());
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "userDetailModel.getMessage() is null");
                            }
                        }
                        if (isLoggedIn) {
                            getUserDetails(authorization, requestFrom);
                        }


                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "userDetailModel==null && userDetailModel.getData()==null");
                        }
                    }
                } else {

                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "User Details Util is not successful");
                    }
                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        errorMessageModel = gson.fromJson(String.valueOf(response.errorBody()), ErrorMessageModel.class);
                        if (Common.isLoggingEnabled) {
                            if (errorMessageModel != null) {
                                Log.e(Common.LOG, "" + errorMessageModel);
                            } else {
                                //message = ResponseStatus.getResponseCodeMessage(response.code());
                                // Log.e(Common.LOG, "Response Status " + message.toString());
                                //Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        if (context != null) {
                            new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_sendUserDetails",
                                    SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled)
                            e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<UserDetailModel> call, Throwable t) {
                new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_sendUserDetails_failure",
                        SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    public void getUserDetails(String authorization, String requestFrom) {
        getUserDetailsCall = ApiClient.getService().getUserDetails(authorization);
        getUserDetailsCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //StopLoading();
                try {
                    LoginResponse loginResponse = response.body();
                    if (response.isSuccessful()) {
                       /* message = ResponseStatus.getResponseCodeMessage(response.code());
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "getUserDetails in successful: " + loginResponse.toString());
                            Log.d(Common.LOG, "Response Status " + message.toString());
                        }*/

                        // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();

                        //add data to local Db
                        userDetails.getUserDataBack(loginResponse);
                        if (requestFrom.matches("registration")) {
                            List<String> allergyNamesList = new ArrayList<>();
                            List<String> allergyIDsList = new ArrayList<>();
                            String allergyNames = "", allergyIDs = "";
                            if (loginResponse.getData().getUser().getAllergies() != null) {
                                for (int i = 0; i < loginResponse.getData().getUser().getAllergies().size(); i++) {
                                    allergyIDsList.add("" + loginResponse.getData().getUser().getAllergies().get(i).getId());
                                    allergyIDsList.add("" + loginResponse.getData().getUser().getAllergies().get(i).getName());
                                }
                            }
                            if (allergyIDsList.size() > 0) {
                                allergyNames = String.join(",", allergyNamesList);
                                allergyIDs = String.join(",", allergyIDsList);
                            }
                            SessionUtil.saveUserSession(context, loginResponse.getData().getUser().getId(), loginResponse.getData().getUser().getEmail(), loginResponse.getData().getUser().getName(), loginResponse.getData().getUser().getProfile().getHeight(), loginResponse.getData().getUser().getProfile().getWeight(),
                                    loginResponse.getData().getUser().getProfile().getLevel(), "" + loginResponse.getData().getUser().getProfile().getLevel_id(), loginResponse.getData().getUser().getProfile().getAge(), loginResponse.getData().getUser().getProfile().getGender(), loginResponse.getData().getUser().getProfile().getUnit(), loginResponse.getData().getUser().getProfile().getGoal(),
                                    "" + loginResponse.getData().getUser().getProfile().getGoal_id(), loginResponse.getData().getAccess_token(), "", loginResponse.getData().getUser().getSubscription().getStripe_id(), "" + loginResponse.getData().getUser().getSubscription().getStarts_at(),
                                    "" + loginResponse.getData().getUser().getSubscription().getEnds_at(), "" + loginResponse.getData().getUser().getSubscription().getStarts_at(),
                                    "" + loginResponse.getData().getUser().getSubscription().getTrial_ends_at(), "" + loginResponse.getData().getUser().getProfile().getFood_preference_id(), "" + loginResponse.getData().getUser().getProfile().getFood_preference(), allergyIDs,
                                    allergyNames, loginResponse.getData().getUser().getProfile().getProduct_id());
                            if (loginResponse.getData().getUser().getProfile().getLang() != null) {
                                SessionUtil.setlangCode(context, loginResponse.getData().getUser().getProfile().getLang());
                            }
                        }


                    } else {
                        Toast.makeText(context, response.code() + "From Server", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (context != null) {
                        new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_getUserDetails",
                                SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (context != null) {
                    new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_getUserDetails",
                            SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(t));
                }
                t.printStackTrace();
            }
        });
    }

    public void updateUserDetails(String authorization, String location, String timezone, UserDetailsListener userDetailsListener) {
        logoutCall = ApiClient.getService().updateUserDetails(authorization, location, timezone);
        logoutCall.enqueue(new Callback<UserDetailModel>() {
            @Override
            public void onResponse(Call<UserDetailModel> call, Response<UserDetailModel> response) {
                if (response.isSuccessful()) {
                    /*message = ResponseStatus.getResponseCodeMessage(response.code());
                    Log.d(Common.LOG, "Response Status " + message.toString());*/
                    //Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();

                    UserDetailModel userDetailModel = response.body();
                    if (userDetailModel != null && userDetailModel.getData() != null) {
                        if (userDetailModel.getMessage() != null) {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "" + userDetailModel.getMessage());
                            userDetailsListener.response(response.isSuccessful(), userDetailModel.getMessage());
                            userDetailsListener.responseCode(response.code());
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "userDetailModel.getMessage() is null");
                            userDetailsListener.response(response.isSuccessful(), "Message not retrieved from server");
                            userDetailsListener.responseCode(response.code());
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(Common.LOG, "userDetailModel==null && userDetailModel.getData()==null");
                        userDetailsListener.response(response.isSuccessful(), "Something went wrong");
                        userDetailsListener.responseCode(response.code());
                    }
                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(context);
                    // Toast.makeText(getApplicationContext(),resources.getString(R.string.unauthorized),Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "User Update Details Util is not successful");
                    userDetailsListener.responseCode(response.code());

                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        //errorMessageModel = gson.fromJson(response.errorBody().toString(), ErrorMessageModel.class);
                        if (Common.isLoggingEnabled) {
                            if (errorMessageModel != null) {
                                Log.d(Common.LOG, "" + errorMessageModel);
                                userDetailsListener.response(response.isSuccessful(), errorMessageModel.toString());
                            }
                        } else {
                            /*message = ResponseStatus.getResponseCodeMessage(response.code());
                            Log.d(Common.LOG, "Response Status " + message.toString());*/
                            // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (context != null) {
                            new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_updateUserDetails",
                                    SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled)
                            e.printStackTrace();
                        userDetailsListener.responseError(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailModel> call, Throwable t) {
                if (context != null)
                    new LogsHandlersUtils(context).getLogsDetails("UserDetailsUtils_updateUserDetails_failure",
                            SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                userDetailsListener.responseError(t);
            }
        });
    }

}
