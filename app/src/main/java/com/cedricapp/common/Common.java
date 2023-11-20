package com.cedricapp.common;

import android.content.ComponentName;
import android.content.Intent;

import com.cedricapp.model.Users;

public interface Common {
    //TESTING
    //public static final String TESTING_BASE_URL = "https://cedric.apit.techozon.com/v2/";
    String TESTING_BASE_URL = "https://apit.cedrics.se/v2/";
    String PRODUCTION_BASE_URL = "https://api.cedrics.se/v2/";
    String TESTING_BETA_BASE_URL = "https://apibeta.cedrics.se/v2/";
    //String PRODUCTION_BASE_URL = "https://apib.cedrics.se/v2/";
    public static final String STRIPE_BASE_URL = "https://api.stripe.com";
    //public static final String STRIPE_BASE_URL = "https://cedric.apit.techozon.com/v2/";

    //STAGING
    //public static final String STAGING_BASE_URL = "https://cedric.apis.techozon.com/v2/";
    public static final String STAGING_BASE_URL = "https://apis.cedrics.se/v2/";
    //public static final String BETA_BASE_URL = "https://cedric.apit.techozon.com/v2/";
    public static final String BETA_BASE_URL = "https://apib.cedrics.se/v2/";

    //public static final String IMAGE_URL_INTERNET_TEST = "https://cedricp.techozon.com/images/programs/upper_body_program.jpg";
    //public static final String IMAGE_URL_INTERNET_TEST = "https://cedricp.techozon.com/workouts/August2019/W8.png";
    public static final String IMAGE_URL_INTERNET_TEST = "https://crm.cedrics.se/workouts/August2019/W8.png";

    public static final String LOCATION_API_URL = "https://www.googleapis.com/geolocation/";

    //BASE_URL
    //public static final String IMG_BASE_URL = "http://cedricp.techozon.com/"

    public static final String TERMS_AND_POLICY_URL = "https://cedrics.se/terms?locale=";
    public static final String ABOUT_US_URL = "https://cedrics.se/about?locale=";
    public static final String PRIVACY_POLICY_URL = "https://cedrics.se/policy?locale=";
    public static final String FAQS_URL = "https://cedrics.se/faqs?locale=";
    public static final String IOS_CHANGE_SUBSCRIPTION_URL = "https://support.apple.com/en-us/HT204939";
    public static final String IOS_CANCEL_SUBSCRIPTION_URL = "https://support.apple.com/en-ae/HT202039";

    //public static final String TERMS_AND_POLICY_URL = "https://cedric.montagesvets.se/policy.html";
    //public static final String ABOUT_US_URL = "https://cedric.montagesvets.se/about.html";
    //public static final String PRIVACY_POLICY_URL = "https://cedric.montagesvets.se/policy.html";
    // public static final String FAQS_URL = "https://cedric.montagesvets.se/about.html";


    public static final Users currentUser = null;
    public static final boolean isLoggingEnabled = false;
    public static final String LOG = "CEDRIC_LOGs";
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final int SHOPPING_DELAY_TIME = 500;

    //------------------------SESSION KEYS--------------------------
    public static final String SESSION_USER_ID = "Id";
    public static final String SESSION_EMAIL = "email";
    public static final String SESSION_USERNAME = "name";
    public static final String SESSION_ACCESS_TOKEN = "token";
    public static final String SESSION_REFRESH_TOKEN = "refresh_token";
    public static final String SESSION_FCM_TOKEN = "fcm_token";
    public static final String SESSION_UNIT_TYPE = "unit_type";
    public static final String SESSION_USER_HEIGHT = "userHeight";
    public static final String SESSION_USER_WEIGHT = "userWeight";
    public static final String SESSION_USER_AGE = "Age";
    public static final String SESSION_USER_GENDER = "gender";
    public static final String SESSION_USER_LEVEL = "Level";
    public static final String SESSION_USER_LEVEL_ID = "level_id";
    public static final String SESSION_USER_GOAL = "goals";
    public static final String SESSION_USER_GOAL_ID = "goal_id";
    public static final String SESSION_USER_FOOD_PREFERENCE_ID = "food_preference_id";
    public static final String SESSION_USER_FOOD_PREFERENCE = "food_preference";
    public static final String SESSION_USER_ALLERGY_IDS = "allergy_ids";
    public static final String SESSION_USER_ALLERGIES = "allergies";
    public static final String SESSION_USER_PRODUCT_ID = "product_id";
    public static final String SESSION_USER_PLAN_ID = "plan_id";
    public static final String SESSION_USER_PLAN_PRICE = "plan_price";
    public static final String SESSION_USER_IMAGE = "user_image";
    public static final String SESSION_SUBSCRIPTION_ID = "subscriptionID";
    public static final String SESSION_COMING_FROM = "comingFrom";
    public final static String TRAIL_STARTS = "trail_starts";
    public final static String TRAIL_ENDS = "trail_ends";
    public final static String SUBSCRIPTION_ID = "subscription_id";
    public final static String SUBSCRIPTION_START_DATE = "subscription_start_date";
    public final static String SUBSCRIPTION_END_DATE = "subscription_end_date";
    public final static String SUBSCRIPTION_AVAILABILITY = "is_subscription_available";
    public final static String COACH_NAME = "coachName";
    public final static String COACH_ID = "coach_Id";
    public final static String ENVIRONMENT = "environment";
    public final static String API_URL = "API_URL";
    public final static String IS_DEV_STATUS = "is_dev";
    public final static String SESSION_DEVICE_ID = "device_id";
    public final static String INTEGERI = "i";
    public final static String lang = "en";
    String SELECTED_DATE = "selected_date";
    String SIGNED_UP_DEVICE_PLATFORM = "signed_up_device_platform";

    //from rikskampen app
    public static final String APP_STATE_PREF = "app_sate_ref";
    public static final String LOGGED_IN_PREF = "logged_in_status";
    public static final String LOGGED_IN_FIRST_TIME = "logged_in_first_time";
    public static final String STOP_SERVICE_REQUIRED = "stop_service_required";
    public static final String LOGGED_TESTER_USER = "logged_tester";
    public static final String LOGGED_IN_STEPS_FIRST_TIME = "logged_in_steps_first_time";
    public static final String LOGGED_IN_SPLASH = "logged_in_splash";
    public static final String LOCATION_PERMISSION_FOREGROUND = "location_permission_foreground";
    public static final String LOCATION_PERMISSION_BACKGROUND = "location_permission_background";
    public static final String BATTERY_PERMISSION_BACKGROUND = "battery_permission_background";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String CAMERA_PERMISSION = "camera_permission";
    public static final String STEP_COUNTER_PERMISSION = "step_counter_permission";
    public static final String LOGOUT_CHECK = "logout";
    public static final String STEP_FOREGROUND_SERVICE = "step_foreground_service";
    public static final String STEP_FOREGROUND_SERVICE_DESTROY_STATUS = "step_foreground_service_destroy_status";
    public static final String STEP_COUNT_STOP_NOTIFY = "step_count_stop_notify";
    public static final String STEP_COUNT_PERMISSION_POP_UP = "step_count_permission_pop_up";
    public static final String GOOGLE_API = "google_api";
    public static final String DRAW_OVER_OTHER_APPS_PERMISSION = "draw_over_other_apps_permission";
    public static final String POST_ACTIVITY_NOTIFICATION = "post_activity_notification";
    public static final String IS_LIVE_VIDEO_CALL_AVAILABLE = "is_Live_Video_Available";
    public static final String STOP_WATCH_TIME = "stop_watch_time";
    public static final String ENCRYPTION_KEY = "encryption_key_db";
    public static final String LOGGED_IN_FCM = "logged_in_fcm";
    public static final String SYNCED_API_FIRST_TIME = "synced_api_first_time";
    public static final String LOGGED_IN_PREF_Email = "logged_in_email";
    public static final String LOGGED_IN_PREF_pass = "logged_in_pass";
    public static final String LOGGED_IN_DATE = "date";
    public static final String STEP_SESSION_DATE = "step_session_date";
    public static final String NOTIFICATION_PERMISSION = "notification_permission";
    public static final String LOGGED_IN_DATE_STEPS = "logged_in_date_steps";
    public static final String LOGGED_IN_STEPS = "logged_in_steps";
    public static final String ACTIVITY_UPLOADED= "activity_uploaded";
    public static final String ACTIVITY_UPLOADING_DATE= "activity_uploading_date";
    public static final String ACTIVITY_DOWNLOADING_DATE= "activity_downloading_date";
    public static final String TODAY_STEPS_COUNT = "today_steps_count";
    public static final String SHOW_PERMISSION_DIALOG = "SHOW_PERMISSION_DIALOG";
    public static final String GOOGLE_FIT_TODAY_STEPS = "google_fit_today_steps";
    public static final String GOOGLE_FIT_TODAY_STEPS_ADD = "google_fit_today_steps_add";
    public static final String SENSOR_STEPS = "sensor_steps";
    public static final String DEVICE_STATUS = "device_status";
    public static final String GOOGLE_FIT_STATUS = "google_fit_status";
    public static final String SENSOR_STATIC_STEPS = "sensor_Static_steps";
    public static final String DISTANCE = "distance";
    public static final String NOTMESSAGE = "notmessage";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String STOP_WATCH_ON_PAUSE = "pause_time";
    public static final String CHASE_STAR_STEPS = "starsteps";
    public static final String CHASE_STAR = "star_count";
    public static final String JOURNEY_CHASE_STAR = "star_count_journey";
    public static final String REQUEST_CODE = "request_code";
    public static final String CHASE_STAR_STEPS_CURRENT = "starstepscurrent";
    public static final String TODAY_STEPS = "todaysteps";
    public static final String LAST_SCHDULE_SLOT_ID = "last_schdule_slot_id";
    public static final String USER_STAR_CHASE_TRACK = "user_star_chase_track";
    public static final String JOURNEY_STEPS = "journeysteps";
    public static final String JOURNEY_DISTANCE = "journeydistance";
    public static final String JOURNEY_CALORIES = "journeycalories";
    public static final String LAST_DAY_STEPS = "lastdaysteps";
    public static final String TODAY_STEPS_DATE = "todaystepsdate";
    public static final String STEPS_DATE_TIME_TO_FETECH_HISTORY_FROM_GOOGLE_FIT = "Steps_Date_Time_To_Fetech_History_From_GoogleFit";
    public static final String APP_STATUS = "appstatus";
    public static final String LIFE_CYCLE_STATUS = "life_cycle_status";
    public static final String CHAT_ACTIVTY_STATUS = "chat_activity_status";
    public static final String CHAT_COUNT = "chatcount";
    public static final String CHAT_COUNT_CURRENT = "chatcountCurrent";
    public static final String CHAT_COUNT_ZERO = "chatcountzero";
    public static final String FCM_TOKEN = "token";
    public static final String APP_VERSION = "app_version";
    public static final String NUTRITION_PDF_URL = "nutrition_pdf_url";
    public static final String DAILY_IMAGE = "daily_image";
    public static final String DAILY_LOCATION = "daily_location";
    public static final String DAILY_DATE = "daily_date";
    public static final String WEIGHT_TODAY = "today_weight";
    public static final String WATER_TODAY = "today_water";
    public static final String WAIST_TODAY = "today_waist";
    public static final String REMMBER_ME = "remmber";
    public static final String NEED_TO_UPDATE = "is_need_to_update";
    public static final String LIVE_VIDEO_CALL_SESSION_END_TIME = "live_video_call_session_end_time";
    public static final String LIVE_VIDEO_CALL_SESSION_TRAINER_ID = "live_video_call_session_trainer_id";
    public static final String LIVE_VIDEO_CALL_SESSION_CONTESTANT_ID = "live_video_call_session_contestant_id";
    public static final String LIVE_VIDEO_CALL_SESSION_PARTICIPANT_NAME = "live_video_call_session_participant_name";
    public static final String LIVE_VIDEO_CALL_SESSION_PARTICIPANT_IMAGE = "live_video_call_session_participant_image";
    public static final String LIVE_VIDEO_CALL_SESSION_HOURS = "live_video_call_session_hours";
    public static final String LIVE_VIDEO_CALL_SESSION_MINIUTES = "live_video_call_session_miniutes";
    public static final String LIVE_VIDEO_CALL_SESSION_SECONDS = "live_video_call_session_seconds";

    public static final String LAT_IN_PREF = "lat";
    public static final String LAN_IN_PREF = "lan";


    public static final String TOKEN_PREF = "token_ref";
    public static final String CURRENT_DATE_COMPITION = "current_date_compitition";
    public static final String BASE_URL_SESSION = "base_url_session";
    public static final String API_SYNCED_DATE = "api_synced_date";


    public static final String USER_ID = "user_id";


    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String START_ACTION = "service.action.start";
    public static String STOP_ACTION = "service.action.stop";
    public static String RESTART_SERVICE = "restartservice";

    public static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    };
    public final static String SESSION_USER_PLAN_POSITION = "plan_position";
    public final static String  SESSION_USER_LOGGED_IN_STATUS ="user_logged_status" ;
    public final static String  USER_STATUS = "user_staus";
    public final static String   LOGGED_LOCATION ="logged_location" ;
    public final static String   LOGGED_Latitude="logged_lat" ;
    public final static String   REQUEST_LOAD="request_load" ;

    public final static String   REQUEST_LOAD_SHOPPING="request_load_shopping" ;
    public final static String   LOGGED_Longitude="logged_lng" ;
    public final static String   EXCEPTION ="exception" ;
    public final static String   ERROR ="error" ;
    public final static String   INFORMATION = "Information";
    public final static String LOGGED_EMAIL = "logged_email";

    public final static String REQUEST_LOAD_DAILY_COACH = "request_load_daily_coach" ;

    public final static String REQUEST_LOAD_WORKOUT_PROGRESS = "request_load_workout_progress" ;
    public final static String REQUEST_RELOAD_DATA = "request_reload_data" ;
    public final static String REQUEST_DASHBOARD_RELOAD_DATA = "request_dashboard_reload_data" ;

    public final static String IS_SUBSCRIPTION_UNSUBSCRIBED = "is_subscription_unsubscribed" ;

    public final static String UNSUBSCRIBED_PLAN_ID = "UNSUBSCRIBED_PLAN_ID" ;

    public final static String ANALYTICS_FOR = "ANALYTICS_FOR";

    public final static String ANALYTICS_TYPE = "ANALYTICS_TYPE";

    public final static String EXCEPTION_TAG = "exception";
    public final static String EXCEPT_TAG = "ex";
    public final static String EMAIL_TAG = "user_email";

    public final static String PREFERENCE_API_V3 = "PREFERENCE_API_V3";

}
