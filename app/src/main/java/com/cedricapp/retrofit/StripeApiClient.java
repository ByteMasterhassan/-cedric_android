package com.cedricapp.retrofit;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.UserService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StripeApiClient {
    public static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient okHttpClient = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();

        // below line is to create an instance for our retrofit api class.


        return new Retrofit.Builder()
               // .baseUrl("https://us-central1-cedric-8cb7d.cloudfunctions.net/")
                .baseUrl(Common.STRIPE_BASE_URL)
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                // at last we are building our retrofit builder.
                .build();
    }
    public static UserService getService(){
        return getRetrofit().create(UserService.class);
    }

}
