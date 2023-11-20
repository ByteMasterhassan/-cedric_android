package com.cedricapp.retrofit;

import android.util.Log;
import com.cedricapp.common.App;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserService;
import com.cedricapp.utils.SessionUtil;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getRetrofit() {
        if (SharedData.BASE_URL == null) {
            /*SharedData.BASE_URL =null;// Common.PRODUCTION_BASE_URL;*/
            String env = SessionUtil.getAPP_Environment(App.getContext());
            switch (env){
                case "testing":
                    SharedData.BASE_URL = Common.TESTING_BASE_URL;
                    break;
                case "stagging":
                    SharedData.BASE_URL = Common.STAGING_BASE_URL;
                    break;
                case "beta":
                    SharedData.BASE_URL = Common.BETA_BASE_URL;
                    break;
                case "testing_beta":
                    SharedData.BASE_URL = Common.TESTING_BETA_BASE_URL;
                    break;
                default:
                    SharedData.BASE_URL = Common.PRODUCTION_BASE_URL;
                    break;
            }
        }
        if (Common.isLoggingEnabled) {
            Log.d("BASE_URL", SharedData.BASE_URL);
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (Common.isLoggingEnabled)
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                /*.addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                       *//* try {
                            Request request;
                            okhttp3.Response response = null;
                            request = chain.request();
                            response = chain.proceed(request);
                            if (!response.isSuccessful()) {
                                if (Common.isLoggingEnabled) {
                                    Log.e("APP_URL", response.toString());
                                }
                                //FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                                if (SharedData.email != null) {
                                    FirebaseCrashlytics.getInstance().log("API_UNSUCCESSFUL:  USER EMAIL: " + SharedData.email + ". RESPONSE CODE: " + response.code() + ". API_RESPONSE: " + response.toString());
                                }else{
                                    FirebaseCrashlytics.getInstance().log("API_UNSUCCESSFUL: USER EMAIL: Not Available. RESPONSE CODE: " + response.code() + ". API_RESPONSE: " + response.toString());
                                }

                                *//**//*if (response.code() != 414) {
                                    if (SharedData.email != null) {
                                        new LogsHandlersUtils().getLogsDetails("Exception",
                                                SharedData.email, EXCEPTION, "response: " + response.toString());
                                    } else {
                                        new LogsHandlersUtils().getLogsDetails("Exception",
                                                "no-email@email.com", EXCEPTION, "response: " + response.toString());
                                    }
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.e("APP_URL", "Response code is " + response.code());
                                    }
                                }*//**//*
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.i("APP_URL", response.toString());
                                }
                            }
                            return response;
                        } catch (Exception ex) {
                            if (Common.isLoggingEnabled)
                                ex.printStackTrace();
                            return null;
                        }*//*
                        return null;
                    }
                })*/
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(httpLoggingInterceptor)
                .build();

        // below line is to create an instance for our retrofit api class.


        return new Retrofit.Builder()
                // .baseUrl("https://us-central1-cedric-8cb7d.cloudfunctions.net/")
                .baseUrl(SharedData.BASE_URL)
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                // at last we are building our retrofit builder.
                .build();
    }

    public static UserService getService() {
        return getRetrofit().create(UserService.class);
    }

}
