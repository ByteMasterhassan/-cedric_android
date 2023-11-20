package com.cedricapp.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cedricapp.R;

import java.util.Calendar;
import java.util.Date;

public class SharedData {
    public static String BASE_URL;
    public static String plan;
    public static String id;
    public static String planPrice;
    public static double dPlanPrice;
    public static String planMonthlyPrice;
    public static String planDiscount;
    public static Boolean profileActivation;
    public static String username;
    public static String gender;
    public static String age;
    public static String height;
    public static String weight;
    public static String email;
    public static String imageUrl = "oooo";
    public static Integer coachId;
    public static Integer warmupId;
    public static String programName;
    public static int programId;
    public static int bestProgramId;
    public static String otp;
    public static String otpGeneratedId;
    public static String password;
    public static String level;
    public static String coachName;
    public static String coachImage;
    public static int day;
    public static int week;
    public static String unitType;
    public static boolean statuss;
    public static String status;
    public static String today;
    public static String signupDate;
    public static String stripe_id;
    public static String isCompleted;
    public static String planId;
    public static String goal;
    public static Date created_Date;
    public static Date updated_Date;
    public static String level_id, goal_id;
    public static String token;
    public static String refresh_token;
    public static String subscribed_id;
    public static String subscription_id;
    public static String start_date;
    public static String end_date;
    public static String trail_ends;
    public static Calendar selectedDate;
    public static String subscription_status;
    public static Boolean is_dev_mode;
    public static String fcm_token;
    public static String device_id;
    public static int planPosition;
    public static String userStatus;
    public static String previousGpsLocation;

    public static double currentLatitude;

    public static double currentLongitude;
    public static int cardPosition;

    static Date currentTime;
    static Date targetTime;
    public static int networkSpeed;
    public static String location;
    public static int coachWatchCount;

    public static Boolean canToastShow;

    public static Boolean redirectToDashboard;
    public static Boolean isDashboardVisible;

    public static String countryCode;

    public static boolean isSubscriptionAvailable;
    public static boolean isLoginScreen = false;

    public static void setWelcomeMessage(TextView mTextWelcome, Resources resources) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 1 && hour <= 11)
            mTextWelcome.setText(resources.getString(R.string.good_morning));
        else if (hour >= 12 && hour <= 16)
            mTextWelcome.setText(resources.getString(R.string.good_afternoon));
        else if (hour >= 17 && hour <= 19)
            mTextWelcome.setText(resources.getString(R.string.good_evening));
        else
            mTextWelcome.setText(resources.getString(R.string.good_night));
    }

    public static void showMessage(Context applicationContext) {
        Toast.makeText(applicationContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }


    public static String throwableObject(Throwable t) {
        //return Log.getStackTraceString(t);
        return t.toString();
    }

    public static String caughtException(Exception e) {
        if(Common.isLoggingEnabled){
            e.printStackTrace();
        }
        return Log.getStackTraceString(e);
        //return e.toString();
    }
}
