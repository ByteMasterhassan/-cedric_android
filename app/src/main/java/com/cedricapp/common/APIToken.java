package com.cedricapp.common;

import static com.cedricapp.activity.LoginActivity.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cedricapp.model.SignupResponse;
import com.cedricapp.retrofit.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIToken {
    private static Call<SignupResponse> tokenCall;
    private static boolean isTokenGotten = false;

    public static boolean getToken(Context context, String refreshToken, String userID) {

        tokenCall = ApiClient.getService().getNewToken("Bearer " + refreshToken, userID);
        tokenCall.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        SignupResponse responseToken = response.body();

                        if (responseToken != null) {
                            if (responseToken.status == true) {

                                if (context != null) {
                                    if (responseToken.getData() != null) {
                                        isTokenGotten = true;
                                        addTokenInSharedPreference(context, responseToken.getData().getAccess_token(), responseToken.getData().getRefresh_token());
                                    } else {
                                        isTokenGotten = false;
                                        if (Common.isLoggingEnabled) {
                                            Log.d(Common.LOG, "Response Token is null");
                                        }
                                    }
                                }else{
                                    isTokenGotten = false;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "Context is null");
                                    }
                                }

                            } else if (responseToken.status == false) {
                                isTokenGotten = false;
                                if (responseToken.getMessage() != null)
                                    Log.d(Common.LOG, "Response Message: " + responseToken.getMessage().toString());

                            }
                        } else {
                            isTokenGotten = false;
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Response Token is null");
                            }
                        }
                    } else {
                        isTokenGotten = false;
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Response is unsuccessful");
                        }
                    }
                } catch (Exception e) {
                    isTokenGotten = false;
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                        Log.e(Common.LOG, "API_Token: Exception while getting user token data: " + e.toString());

                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                isTokenGotten = false;
            }
        });
        return isTokenGotten;
    }

    public static void addTokenInSharedPreference(Context context, String token, String refreshToken) {
        SharedData.token = token;
        SharedData.refresh_token = refreshToken;
        //update tokens
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       // editor.putString(Common.SESSION_TOKEN, SharedData.token);
        editor.putString(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      //  SharedData.token = sharedPreferences.getString(Common.SESSION_TOKEN, "");
        return SharedData.token;
    }
}
