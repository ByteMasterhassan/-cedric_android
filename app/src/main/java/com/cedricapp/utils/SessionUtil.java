package com.cedricapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;

public class SessionUtil {
    static SharedPreferences sharedPreferences;
    private final static String SHARED_PREF_NAME = "log_user_info";
    private final static String SHARED_API_ENVIRONMENT = "api_environment";


    public SessionUtil() {

    }

    public static void saveUserSession(Context context, String userID, String email, String username, String height, String weight,
                                       String level, String levelID, String age, String gender, String unitType, String goals, String goalID,
                                       String accessToken, String refreshToken, String subscriptionID, String subscriptionStartDate,
                                       String subscriptionEndDate, String trialStarts, String trialEnds, String foodPreferenceID, String foodPreference,
                                       String allergyIDs, String allergies, String productID) {

        if (context != null) {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "------------In SessionUtil: Save session in shared preference------------");
                Log.d(Common.LOG, Common.SESSION_USER_ID + ": " + userID);
                Log.d(Common.LOG, Common.SESSION_EMAIL + ": " + email);
                Log.d(Common.LOG, Common.SESSION_USERNAME + ": " + SharedData.username);
                Log.d(Common.LOG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(Common.LOG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(Common.LOG, Common.SESSION_USER_LEVEL + ": " + level);
                Log.d(Common.LOG, Common.SESSION_USER_LEVEL_ID + ": " + levelID);
                Log.d(Common.LOG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(Common.LOG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(Common.LOG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(Common.LOG, Common.SESSION_USER_GOAL + ": " + goals);
                Log.d(Common.LOG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
                Log.d(Common.LOG, Common.SESSION_ACCESS_TOKEN + ": " + accessToken);
                Log.d(Common.LOG, Common.SESSION_REFRESH_TOKEN + ": " + refreshToken);
                Log.d(Common.LOG, Common.TRAIL_ENDS + ": " + trialEnds);
                Log.d(Common.LOG, Common.TRAIL_STARTS + ": " + trialStarts);

                Log.d(Common.LOG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreferenceID);
                Log.d(Common.LOG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(Common.LOG, Common.SESSION_USER_ALLERGY_IDS + ": " + allergyIDs);
                Log.d(Common.LOG, Common.SESSION_USER_ALLERGIES + ": " + allergies);
                Log.d(Common.LOG, Common.SESSION_USER_PRODUCT_ID + ": " + productID);
            }

            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_ID, userID);
            editor.putString(Common.SESSION_EMAIL, email);
            editor.putString(Common.SESSION_USERNAME, username);
            editor.putString(Common.SESSION_USER_HEIGHT, height);
            editor.putString(Common.SESSION_USER_WEIGHT, weight);
            editor.putString(Common.SESSION_USER_LEVEL_ID, levelID);
            editor.putString(Common.SESSION_USER_LEVEL, level);
            editor.putString(Common.SESSION_USER_AGE, age);
            editor.putString(Common.SESSION_USER_GENDER, gender);
            editor.putString(Common.SESSION_UNIT_TYPE, unitType);
            editor.putString(Common.SESSION_USER_GOAL, goals);
            editor.putString(Common.SESSION_USER_GOAL_ID, goalID);
            editor.putString(Common.SESSION_ACCESS_TOKEN, accessToken);
            editor.putString(Common.SESSION_REFRESH_TOKEN, refreshToken);
            editor.putString(Common.SUBSCRIPTION_ID, subscriptionID);
            editor.putString(Common.SUBSCRIPTION_START_DATE, subscriptionStartDate);
            editor.putString(Common.SUBSCRIPTION_END_DATE, subscriptionEndDate);
            //editor.putString(Common.TRAIL_STARTS, trialStarts);
            editor.putString(Common.TRAIL_ENDS, trialEnds);
            editor.putString(Common.TRAIL_STARTS, trialStarts);
            editor.putBoolean(Common.SUBSCRIPTION_AVAILABILITY, true);
            editor.putString(Common.SESSION_USER_FOOD_PREFERENCE_ID, foodPreferenceID);
            editor.putString(Common.SESSION_USER_FOOD_PREFERENCE, foodPreference);
            editor.putString(Common.SESSION_USER_ALLERGY_IDS, allergyIDs);
            editor.putString(Common.SESSION_USER_ALLERGIES, allergies);
            editor.putString(Common.SESSION_USER_PRODUCT_ID, productID);
            //editor.putBoolean(Common.LOGGED_IN_PREF, true);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class");
            }
        }

    }

    public static void setlangCode(Context context, String en) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.lang, en);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static String getlangCode(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.lang, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: langCode  method");
            }
            return "";
        }

    }

    public static void setNumberCode(Context context, String en) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("number", en);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static String getNumberCode(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString("number", "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getNumberCode  method");
            }
            return "";
        }

    }


    public static void setIntegerI(Context context, Integer i) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Common.INTEGERI, i);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static int getIntegerI(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Common.INTEGERI, 0);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getAllergiesId  method");
            }
            return 0;
        }

    }

    public static void setAllergiesId(Context context, String allergiesId) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_ALLERGY_IDS, allergiesId);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static void RemoveAllergiesId(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Common.SESSION_USER_ALLERGY_IDS);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static String getAllergiesId(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_ALLERGY_IDS, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getAllergiesId  method");
            }
            return "";
        }

    }

    public static void saveSubscription(Context context, String startSubscription, String endSubscription) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SUBSCRIPTION_START_DATE, startSubscription);
            editor.putString(Common.SUBSCRIPTION_END_DATE, endSubscription);
            //editor.putBoolean(Common.SUBSCRIPTION_AVAILABILITY, true);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session saveSubscription");
            }
        }
    }

    public static void saveStartTrialDate(Context context, String startTrial) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.TRAIL_STARTS, startTrial);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session SaveTrailDate");
            }
        }
    }

    public static void saveEndTrialDate(Context context, String endTrial) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.TRAIL_ENDS, endTrial);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session SaveTrailEndDate");
            }
        }
    }

    public static void saveSubscriptionID(Context context, String id) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SUBSCRIPTION_ID, id);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session SaveSubscriptionID");
            }
        }
    }


    public static void saveAPI_Environment(Context context, String env) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_API_ENVIRONMENT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.ENVIRONMENT, env);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session saveAPI_Environment");
            }
        }
    }

    public static String getAPP_Environment(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_API_ENVIRONMENT, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.ENVIRONMENT, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: isStaging method");
            }
            return "";
        }
    }

    public static void setAPI_V3(Context context, boolean isAPI_V3) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Common.PREFERENCE_API_V3, isAPI_V3);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAPI_v3");
            }
        }
    }

    public static boolean isAPI_V3(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(Common.PREFERENCE_API_V3, true);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: isAPI_V3 method");
            }
            return false;
        }
    }

    public static void saveAPI_URL(Context context, String url) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_API_ENVIRONMENT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.API_URL, url);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session saveAPI_URL");
            }
        }
    }

    public static String getAPI_URL(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_API_ENVIRONMENT, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.API_URL, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getAPI_URL method");
            }
            return "";
        }
    }


    public static void setSubscriptionAvailabilityStatus(Context context, Boolean isSubscriptionAvailable) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Common.SUBSCRIPTION_AVAILABILITY, isSubscriptionAvailable);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session saveAPI_URL");
            }
        }
    }

    public static Boolean isSubscriptionAvailable(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(Common.SUBSCRIPTION_AVAILABILITY, false);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: isSubscriptionAvailable method");
            }
            return false;
        }
    }

    public static String getSubscriptionStartDate(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SUBSCRIPTION_START_DATE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSubscriptionStartDate method");
            }
            return "";
        }
    }

    public static String getSubscriptionEndDate(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SUBSCRIPTION_END_DATE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSubscriptionEndDate method");
            }
            return "";
        }
    }

    public static String getTrailStarts(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.TRAIL_STARTS, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getTrailStarts method");
            }
            return "";
        }
    }

    public static String getTrailEnds(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.TRAIL_ENDS, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getTrailEnds method");
            }
            return "";
        }
    }

    public static void setUsername(Context context, String username) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USERNAME, username);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setUsername");
        }
    }


    public static String getUsernameFromSession(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USERNAME, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUsernameFromSession method");
            }
            return "";
        }

    }

    public static void setUserImgURL(Context context, String userImgURL) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_IMAGE, userImgURL);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setUserImgURL");
        }
    }

    public static String getUserImgURL(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_IMAGE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserImgURL method");
            }
            return "";
        }

    }


    public static void setAccessToken(Context context, String accessToken) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_ACCESS_TOKEN, accessToken);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setAccessToken method");
        }
    }


    public static String getAccessToken(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_ACCESS_TOKEN, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserTokenSession method");
            }
            return "";
        }
    }

    public static void setRefreshToken(Context context, String refreshToken) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_REFRESH_TOKEN, refreshToken);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setRefreshToken method");
        }
    }

    public static String getRefreshToken(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_REFRESH_TOKEN, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getRefreshTokenFromSession method");
            }
            return "";
        }

    }

    public static String getSubscriptionID_FromSession(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SUBSCRIPTION_ID, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSubscriptionID_FromSession method");
            }
            return "";
        }

    }

    public static String getSubscriptionStartDateFromSession(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SUBSCRIPTION_START_DATE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSubscriptionStartDateFromSession method");
            }
            return "";
        }

    }

    public static String getSubscriptionEndDateFromSession(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SUBSCRIPTION_END_DATE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSubscriptionEndDateFromSession method");
            }
            return "";
        }

    }

    public static String getUserID(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_ID, "0");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserID method");
            }
            return "";
        }

    }

    public static void setUserWeight(Context context, String weight) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_WEIGHT, weight);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setUserHeight");
        }
    }

    public static String getUserWeight(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_WEIGHT, "0");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserWeight method");
            }
            return "";
        }

    }

    public static void setUserHeight(Context context, String height) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_HEIGHT, height);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null in Session setUserHeight");
        }
    }

    public static String getUserHeight(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_HEIGHT, "0");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserHeight method");
            }
            return "";
        }
    }

    public static String getUserAge(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_AGE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserAge method");
            }
            return "";
        }
    }

    public static void setUserAge(Context context, String age) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_AGE, age);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserAge method");
            }
        }
    }

    public static void setUserGender(Context context, String gender) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_GENDER, gender);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserGender method");
            }
        }
    }

    public static String getUserGender(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_GENDER, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGender method");
            }
            return "";
        }
    }

    public static void setUserLevel(Context context, String level) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_LEVEL, level);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserLevel method");
            }
        }
    }

    public static String getUserLevel(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_LEVEL, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserLevel method");
            }
            return "";
        }
    }

    public static void setUserUnitType(Context context, String level) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_UNIT_TYPE, level);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }
    }

    public static String getUserUnitType(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_UNIT_TYPE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserUnitType method");
            }
            return "";
        }
    }

    public static String getFoodPreferenceID(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_FOOD_PREFERENCE_ID, "0");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserUnitType method");
            }
            return "";
        }
    }

    public static void SetFoodPreferenceID(Context context, String foodPrefId) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_FOOD_PREFERENCE_ID, foodPrefId);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }
    }

    public static void setUserLevelID(Context context, String levelID) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_LEVEL_ID, levelID);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserLevelID method");
            }
        }
    }

    public static String getUserLevelID(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_LEVEL_ID, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserLevelID method");
            }
            return "";
        }
    }

    public static void setUserGoalID(Context context, String goalID) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_GOAL_ID, goalID);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserGoalID method");
            }
        }
    }

    public static void setUserGoal(Context context, String goal) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_GOAL, goal);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserGoal method");
            }
        }
    }

    public static String getUserGoalID(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_GOAL_ID, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoalID method");
            }
            return "";
        }
    }

    public static String getProductID(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_PRODUCT_ID, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserProductID method");
            }
            return "";
        }
    }

    public static void setProductID(Context applicationContext, String productID) {
        if (applicationContext != null) {
            sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_PRODUCT_ID, productID);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setProductID method");
            }
        }
    }

    public static String getUserGoal(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_GOAL, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoal method");
            }
            return "";
        }
    }

    public static boolean logout(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            return true;
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoal method");
            }
            return false;
        }
    }

    public static void setIsDevStatus(Context context, Boolean is_dev_mode) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Common.IS_DEV_STATUS, is_dev_mode);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }
    }

    public static Boolean getIsDevStatus(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(Common.IS_DEV_STATUS, false);
        }
        return false;
    }

    public static void setFcmToken(Context context, String fcm_token) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_FCM_TOKEN, fcm_token);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }

    }

    public static void setDeviceToken(Context applicationContext, String device_id) {
        if (applicationContext != null) {
            sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_DEVICE_ID, device_id);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }
    }

    public static String getFcmToken(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_FCM_TOKEN, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoal method");
            }
            return "";
        }
    }

    public static String getDeviceId(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_DEVICE_ID, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoal method");
            }
            return "";
        }
    }

    public static void setSelectedDate(Context context, String toDate) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SELECTED_DATE, toDate);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserUnitType method");
            }
        }
    }

    public static String getSelectedDate(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SELECTED_DATE, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserGoal method");
            }
            return "";
        }
    }

    public static void setSignedUpPlatform(Context context, String platform) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SIGNED_UP_DEVICE_PLATFORM, platform);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setSignedUpPlatform method");
            }
        }
    }

    public static String getSignedUpPlatform(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SIGNED_UP_DEVICE_PLATFORM, "android");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getSignedUpPlatform method");
            }
            return "";
        }
    }


    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static void setLocationPermissionBackground(Context context, boolean locationStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LOCATION_PERMISSION_BACKGROUND, locationStatus);
        editor.apply();
    }

    public static boolean getLocationPermissionBackground(Context context) {
        return getPreferences(context).getBoolean(Common.LOCATION_PERMISSION_BACKGROUND, false);
    }

    public static void setBatteryConsumptionPermissionBackground(Context context, boolean locationStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.BATTERY_PERMISSION_BACKGROUND, locationStatus);
        editor.apply();
    }

    public static boolean getBatteryConsumptionPermissionBackground(Context context) {
        return getPreferences(context).getBoolean(Common.BATTERY_PERMISSION_BACKGROUND, false);
    }


    public static void setLocationPermissionForeground(Context context, boolean locationStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LOCATION_PERMISSION_FOREGROUND, locationStatus);
        editor.apply();
    }


    public static boolean getLocationPermissionForeground(Context context) {
        return getPreferences(context).getBoolean(Common.LOCATION_PERMISSION_FOREGROUND, false);
    }

    public static void setStepCounterPermission(Context context, boolean isStepCounterStarted) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.STEP_COUNTER_PERMISSION, isStepCounterStarted);
        editor.apply();
    }

    public static boolean getStepCounterPermission(Context context) {
        return getPreferences(context).getBoolean(Common.STEP_COUNTER_PERMISSION, false);
    }

    public static void setStepsForeGroundService(Context context, boolean CameraStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.STEP_FOREGROUND_SERVICE, CameraStatus);
        editor.apply();
    }

    public static boolean getStepsForeGroundService(Context context) {
        return getPreferences(context).getBoolean(Common.STEP_FOREGROUND_SERVICE, false);
    }

    public static void setStepsForeGroundServiceDestroyStatus(Context context, boolean CameraStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.STEP_FOREGROUND_SERVICE_DESTROY_STATUS, CameraStatus);
        editor.apply();
    }

    public static boolean getStepsForeGroundServiceDestroyStatus(Context context) {
        return getPreferences(context).getBoolean(Common.STEP_FOREGROUND_SERVICE_DESTROY_STATUS, false);
    }


    public static void setStepCountingStopNotification(Context context, boolean CameraStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.STEP_COUNT_STOP_NOTIFY, CameraStatus);
        editor.apply();
    }

    public static boolean getStepCountingStopNotification(Context context) {
        return getPreferences(context).getBoolean(Common.STEP_COUNT_STOP_NOTIFY, false);
    }


    public static void setCameraPermission(Context context, boolean CameraStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.CAMERA_PERMISSION, CameraStatus);
        editor.apply();
    }


    public static boolean getCameraPermission(Context context) {
        return getPreferences(context).getBoolean(Common.CAMERA_PERMISSION, false);
    }

    public static void setNotificationPermission(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.NOTIFICATION_PERMISSION, isEnabled);
        editor.apply();
    }


    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }


    public static void setNotificationMessage(Context context, String message) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.NOTIFICATION_MESSAGE, message);
        editor.apply();
    }

    public static String getNotificationMessage(Context context) {
        return getPreferences(context).getString(Common.NOTIFICATION_MESSAGE, "");
    }


    public static void setUsertodaySteps(Context context, int Steps) {

        try {

            SharedPreferences.Editor editor = getPreferences(context).edit();
            editor.putInt(Common.TODAY_STEPS, Steps);
            editor.apply();
        } catch (Exception ex) {

        }
    }

    public static int getUserTodaySteps(Context context) {
        return getPreferences(context).getInt(Common.TODAY_STEPS, 0);
    }


    public static Boolean getLifeCyleStatus(Context context) {
        return getPreferences(context).getBoolean(Common.LIFE_CYCLE_STATUS, false);
    }

    public static void setLifeCyleStatus(Context context, Boolean appStatus) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LIFE_CYCLE_STATUS, appStatus);
        editor.apply();
    }


    public static void setUserLogInSteps(Context context, int steps) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(Common.LOGGED_IN_STEPS, steps);
        editor.apply();
    }

    public static int getUserLogInSteps(Context context) {
        return getPreferences(context).getInt(Common.LOGGED_IN_STEPS, 0);
    }

    public static void setActivityUploaded(Context context, boolean isUploaded) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.ACTIVITY_UPLOADED, isUploaded);
        editor.apply();
    }

    public static boolean isTodayActivityUploaded(Context context) {
        return getPreferences(context).getBoolean(Common.ACTIVITY_UPLOADED, false);
    }

    public static void setActivityUploadedDate(Context context, String uploadingDate) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.ACTIVITY_UPLOADING_DATE, uploadingDate);
        editor.apply();
    }

    public static String getActivityUploadedDate(Context context) {
        return getPreferences(context).getString(Common.ACTIVITY_UPLOADING_DATE, "");
    }

    public static void setActivityDownloadedDate(Context context, String uploadingDate) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.ACTIVITY_DOWNLOADING_DATE, uploadingDate);
        editor.apply();
    }

    public static String getActivityDownloadedDate(Context context) {
        return getPreferences(context).getString(Common.ACTIVITY_DOWNLOADING_DATE, "");
    }

    public static void setUserStarCount(Context context, int stars) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(Common.CHASE_STAR, stars);
        editor.apply();
    }

    public static void setShowPermissionDialogAgain(Context context, boolean isShowAgain) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.SHOW_PERMISSION_DIALOG, isShowAgain);
        editor.apply();
    }

    public static Boolean showPermissionDialogAgain(Context context) {
        return getPreferences(context).getBoolean(Common.SHOW_PERMISSION_DIALOG, true);
    }


    public static void setLocation(Context context, String Location) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.DAILY_LOCATION, Location);
        editor.apply();
    }

    public static String getLocation(Context context) {
        return getPreferences(context).getString(Common.DAILY_LOCATION, "");
    }


    public static void setDailyDate(Context context, String Date) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.DAILY_DATE, Date);
        editor.apply();
    }

    public static String getDailyDate(Context context) {
        return getPreferences(context).getString(Common.DAILY_DATE, "");
    }


    public static void setWeightToday(Context context, String weight) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.WEIGHT_TODAY, weight);
        editor.apply();
    }

    public static String getWeightToday(Context context) {
        return getPreferences(context).getString(Common.WEIGHT_TODAY, "N/A");
    }

    public static void setWaterIntake(Context context, int waterIntake) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(Common.WATER_TODAY, waterIntake);
        editor.apply();
    }

    public static int getWaterIntake(Context context) {
        return getPreferences(context).getInt(Common.WATER_TODAY, 0);
    }


    public static void setUserLogInDate(Context context, String date) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.LOGGED_IN_DATE, date);
        editor.apply();
    }

    public static String getUserLogInDate(Context context) {
        return getPreferences(context).getString(Common.LOGGED_IN_DATE, "");
    }


    public static void setStepDaySessionDate(Context context, String date) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.STEP_SESSION_DATE, date);
        editor.apply();
    }

    public static String getStepDaySessionDate(Context context) {
        return getPreferences(context).getString(Common.STEP_SESSION_DATE, "");
    }


    public static void setSensorSteps(Context context, int steps) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(Common.SENSOR_STEPS, steps);
        editor.apply();
    }

    public static int getSensorSteps(Context context) {
        return getPreferences(context).getInt(Common.SENSOR_STEPS, 0);
    }


    public static void setSensorStaticSteps(Context context, int steps) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(Common.SENSOR_STATIC_STEPS, steps);
        editor.apply();
    }

    public static int getSensorStaticSteps(Context context) {
        return getPreferences(context).getInt(Common.SENSOR_STATIC_STEPS, 0);
    }


    public static void setDistance(Context context, String distance) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.DISTANCE, distance);
        editor.apply();
    }

    public static String getDistance(Context context) {
        return getPreferences(context).getString(Common.DISTANCE, "");
    }


    public static void setLoggedIn(Context context, boolean loggedIn, String email, String pass) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LOGGED_IN_PREF, loggedIn);
        editor.putString(Common.LOGGED_IN_PREF_Email, email);
        editor.putString(Common.LOGGED_IN_PREF_pass, pass);
        editor.apply();
    }


    public static void setLoggedInStepsFirstTime(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(Common.LOGGED_IN_STEPS_FIRST_TIME, loggedIn);
        editor.apply();
    }


    public static boolean getLoggedInStepsFirstTime(Context context) {
        return getPreferences(context).getBoolean(Common.LOGGED_IN_STEPS_FIRST_TIME, false);
    }

    public static void setLastKnownLocation(Context context, double lat, double lan) {

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.LAT_IN_PREF, lat + "");
        editor.putString(Common.LAN_IN_PREF, lan + "");
        editor.apply();

    }

    public static String getLongitude(Context context) {
        return getPreferences(context).getString(Common.LAT_IN_PREF, "0.0");
    }

    public static String getLatitude(Context context) {
        return getPreferences(context).getString(Common.LAN_IN_PREF, "0.0");
    }


    /**
     * Get the Login Status
     *
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(Common.LOGGED_IN_PREF, false);
    }

    public static String[] getLoggedParams(Context context) {
        String email = getPreferences(context).getString(Common.LOGGED_IN_PREF_Email, "");
        String pass = getPreferences(context).getString(Common.LOGGED_IN_PREF_pass, "");

        return new String[]{email, pass};
    }


    public static void setUserStatus(Context applicationContext, String userStatus) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putString(Common.USER_STATUS, userStatus);
        editor.apply();
    }

    public static String getUserStatus(Context context) {
        return getPreferences(context).getString(Common.USER_STATUS, "");
    }

    public static void setLoggedLocation(String location, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putString(Common.LOGGED_LOCATION, location);
        editor.apply();
    }

    public static String getLoggedLocation(Context context) {
        return getPreferences(context).getString(Common.LOGGED_LOCATION, "");
    }

    public static void setLoggedLatitude(String latitude, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putString(Common.LOGGED_Latitude, latitude);
        editor.apply();
    }

    public static String getLoggedLatitude(Context context) {
        return getPreferences(context).getString(Common.LOGGED_Latitude, "");
    }

    public static void setLoggedLongitude(String longitude, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putString(Common.LOGGED_Longitude, longitude);
        editor.apply();
    }

    public static String getLoggedLongitude(Context context) {
        return getPreferences(context).getString(Common.LOGGED_Longitude, "");
    }

    public static void setLoggedEmail(Context applicationContext, String emailLogin) {
        if (applicationContext != null) {
            sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_EMAIL, emailLogin);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: setUserLevelID method");
            }
        }
    }


    public static String getUserEmailFromSession(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_EMAIL, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getUserEmailFromSession method");
            }
            return "";
        }

    }

    public static void setLoadHomeData(boolean isLoadData, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_LOAD, isLoadData);
        editor.apply();
    }

    public static boolean isLoadHomeData(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_LOAD, false);
    }

    public static void setShoppingLoading(boolean isLoadData, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_LOAD_SHOPPING, isLoadData);
        editor.apply();
    }

    public static boolean isShoppingLoad(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_LOAD_SHOPPING, false);
    }


    public static void setAllergiesName(Context context, String allergyNames) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SESSION_USER_ALLERGIES, allergyNames);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static void removeAllergiesNames(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Common.SESSION_USER_ALLERGIES);
            editor.apply();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }

    public static String getAllergiesName(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Common.SESSION_USER_ALLERGIES, "");
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session Util class: getAllergiesId  method");
            }
            return "";
        }

    }

    public static boolean isDailyCoachLoad(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_LOAD_DAILY_COACH, false);
    }

    public static void setDailyCoachLoad(boolean isLoadData, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_LOAD_DAILY_COACH, isLoadData);
        editor.apply();
    }

    public static void setWorkoutProgressLoad(boolean isLoadData, Context applicationContext) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_LOAD_WORKOUT_PROGRESS, isLoadData);
        editor.apply();
    }

    public static boolean isWorkoutProgressNeedToLoad(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_LOAD_WORKOUT_PROGRESS, false);
    }

    public static void setReloadData(Context applicationContext, boolean isLoadData) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_RELOAD_DATA, isLoadData);
        editor.apply();
    }

    public static boolean isReloadDataRequired(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_RELOAD_DATA, false);
    }

    public static void setDashboardReloadData(Context applicationContext, boolean isLoadData) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.REQUEST_DASHBOARD_RELOAD_DATA, isLoadData);
        editor.apply();
    }

    public static boolean isReloadDashboardDataRequired(Context context) {
        return getPreferences(context).getBoolean(Common.REQUEST_DASHBOARD_RELOAD_DATA, false);
    }

    public static void setUnsubscribeStatus(Context applicationContext, boolean isSubscriptionUnsubscribe) {
        SharedPreferences.Editor editor = getPreferences(applicationContext).edit();
        editor.putBoolean(Common.IS_SUBSCRIPTION_UNSUBSCRIBED, isSubscriptionUnsubscribe);
        editor.apply();
    }

    public static boolean isSubscriptionUnsubscribed(Context context) {
        return getPreferences(context).getBoolean(Common.IS_SUBSCRIPTION_UNSUBSCRIBED, false);
    }

    public static void setUnsubscribedPlanID(Context context, String unsubscribedPlanID) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(Common.UNSUBSCRIBED_PLAN_ID, unsubscribedPlanID);
        editor.apply();
    }

    public static String getUnsubscribedPlanID(Context context) {
        return getPreferences(context).getString(Common.UNSUBSCRIBED_PLAN_ID, "");
    }


   /* public static void setSubscriptionID(Context context, String subscription_id) {
        if (context!=null){
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Common.SUBSCRIPTION_ID, subscription_id);
            editor.apply();
        }
        else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Context is null in Session setAllergiesId");
            }
        }
    }*/
}
