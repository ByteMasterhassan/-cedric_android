package com.cedricapp.utils;


import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.LOCATION_API_URL;
import static com.cedricapp.fragment.WeekWiseNutritonFragment.context;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserService;
import com.cedricapp.model.LocationModel;

import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationUtil {

    public static void getLocationByGeoLocationAPI(Context context) {
        if (ConnectionDetector.isConnectedWithInternet(context)) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            if (Common.isLoggingEnabled)
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            else
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LOCATION_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            UserService userService = retrofit.create(UserService.class);

            Call<LocationModel> call = userService.getLocation(BuildConfig.place_api_key);
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                    if (response.isSuccessful()) {
                        /*String message = ResponseStatus.getResponseCodeMessage(response.code());
                        Log.d(Common.LOG, "Response Status " + message.toString());*/
                        //  Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        LocationModel locationModel = response.body();
                        if (locationModel != null) {
                            if (locationModel.getLocation() != null) {
                                if (locationModel.getLocation().getLat() != null &&
                                        locationModel.getLocation().getLng() != null) {
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "Location API: " + locationModel.toString());
                                    }
                                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(locationModel.getLocation().getLat(), locationModel.getLocation().getLng(), 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            String fullDetail = addresses.get(0).getAddressLine(0);
                                            String city = addresses.get(0).getLocality();
                                            SharedData.countryCode = addresses.get(0).getCountryCode();
                                           /* String stateName = addresses.get(0).getAddressLine(1);
                                            String countryName = addresses.get(0).getAddressLine(2);*/
                                            SharedData.location = addresses.get(0).getAddressLine(0);
                                            if (Common.isLoggingEnabled) {
                                                Log.d(Common.LOG, "Location Detail: " + fullDetail);
                                                Log.d(Common.LOG, "City: " + city);
                                                Log.d(Common.LOG, "Time: " + WeekDaysHelper.getUTC_Time());
                                                Log.d(Common.LOG, "Country Code: " + SharedData.countryCode);
                                            }
                                        }
                                    } catch (Exception e) {
                                        if (context != null) {
                                            new LogsHandlersUtils(context).getLogsDetails("LocationUtils_getLocationByGeoLocationAPI",
                                                    SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
                                        }
                                        if (Common.isLoggingEnabled)
                                            e.printStackTrace();
                                    }


                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.e(Common.LOG, "locationModel.getLocation().getLat() or locationModel.getLocation().getLng() is null");
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.e(Common.LOG, "locationModel.getLocation() is null");
                            }
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.e(Common.LOG, "locationModel is null");
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.e(Common.LOG, "Geo Location API is not successful");
                        new LogsHandlersUtils(context).getLogsDetails("LocationUtils_getLocation",
                                SessionUtil.getUserEmailFromSession(context), EXCEPTION, "Geo Location API is not successful");

                        if (response.errorBody() != null) {
                            if (Common.isLoggingEnabled)
                                Log.e(Common.LOG, response.errorBody().toString());
                        } else {
                          /* String message = ResponseStatus.getResponseCodeMessage(response.code());
                            Log.e(Common.LOG, "Response Status " + message.toString());
                            Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();*/
                        }
                    }
                }

                @Override
                public void onFailure(Call<LocationModel> call, Throwable t) {
                    if (context != null) {
                        new LogsHandlersUtils(context).getLogsDetails("LocationUtils_getLocation_method",
                                SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(t));
                    }

                    if (Common.isLoggingEnabled)
                        t.printStackTrace();
                }
            });

        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Internet is not available");

        }
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {

            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        if (gps_enabled && network_enabled) {
            return true;
        }
        return false;
    }

    public static String getCompleteAddressString(double LATITUDE, double LONGITUDE, Context applicationContext) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(applicationContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                if (addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("My Current loction address", strReturnedAddress.toString());
                }
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Cannot get Address!");
            if (context != null) {
                new LogsHandlersUtils(context).getLogsDetails("LocationUtils_getCompleteStringAddress_method",
                        SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.caughtException(e));
            }

        }
        return strAdd;
    }


    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountryFromSim(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
