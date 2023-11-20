package com.cedricapp.utils;

import android.content.Context;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.model.LogsDataModel;
import com.cedricapp.retrofit.ApiClient;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogsHandlersUtils {
    Context context;

    public LogsHandlersUtils(Context context) {
        this.context = context;

    }

    public LogsHandlersUtils() {

    }

    public void getLogsDetails(String title, String email, String type, String msg) {
        Call<LogsDataModel> call = ApiClient.getService().getLogsDetailsLogIssue(title, email, type, msg);
        call.enqueue(new Callback<LogsDataModel>() {
            @Override
            public void onResponse(Call<LogsDataModel> call, Response<LogsDataModel> response) {
                try {
                    if (response.isSuccessful()) {
                        /*message = ResponseStatus.getResponseCodeMessage(response.code());
                        if (message != null) {
                           // Log.d(Common.LOG, "Response Status " + message.toString());
                        }*/
                        // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        if (response != null) {
                            if (response.body() != null) {
                                if(Common.isLoggingEnabled)
                                    Log.d(Common.LOG,"Logs response code: "+response.code());
                                // Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               // Log.d("logs", response.body().getMessage().toString());

                            } else {
                                //Toast.makeText(context, "Response is null", Toast.LENGTH_SHORT).show();
                                Log.e("logs", "Response is null");
                                if(Common.isLoggingEnabled)
                                    Log.d(Common.LOG,"Logs response code: "+response.code());
                            }
                        }

                    } else {
                        /*message = ResponseStatus.getResponseCodeMessage(response.code());
                        if (message != null) {
                            //Log.d(Common.LOG, "Response Status " + message.toString());
                        }*/
                        //Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LogsDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }
}
