package com.cedricapp.localdatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.model.CheckUncheckDbModel;
import com.cedricapp.model.CoachesProfileDataModel;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.model.DaysUnlockModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.NotificationModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.ProgramWorkout;
import com.cedricapp.model.ProgramsDataModel;
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.SingleRecipeDataModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.model.VisualizationModel;
import com.cedricapp.model.WorkoutDataModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {


    Gson gson = new Gson();
    Type type;


    public static final String DATABASE_NAME = "Cedric.db";
    public static final int DATABASE_VERSION = 55;

    private String TAG = "DB_TAG";

    // User table name
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_PROFILE = "userProfiles";
    private static final String TABLE_COACHES = "coachesProfiles";
    private static final String TABLE_SLEEP_VISUALIZATION = "sleep_visualization";
    private static final String TABLE_DASHBOARD_RECIPES = "dashboard_recipes";
    private static final String TABLE_USER_SHOPPING_LIST = "User_Shopping_List";
    private static final String TABLE_INGREDIENT_CATEGORIES = "ingredientCategories";
    private static final String TABLE_PROGRAM = "program";
    private static final String TABLE_PROGRESS_FRAGMENT = "program_detail";
    private static final String TABLE_PROGRAM_EXERCISES = "exercise";
    private static final String TABLE_COACHES_EXERCISES = "coaches_exercise";
    private static final String TABLE_SHOPPING_LIST = "shoppingList";
    private static final String TABLE_NUTRITION = "nutrition";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_INGREDIENTS = "ingredients";
    private static final String TABLE_SHOPPING_INGREDIENTS = "shopping_ingredients";
    private static final String TABLE_CHECK_UNCHECK = "check_uncheck";

    private static final String TABLE_NOTIFICATION = "notifications_tbl";
    private static final String TABLE_PIVOT_RECIPE = "pivotRecipes";
    private static final String TABLE_STEP_COUNTS = "step_counts";


    //========================================== User Table Columns names===============================================
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_ACCESS_TOKEN = "access_token";
    private static final String COLUMN_USER_REFRESH_TOKEN = "refresh_token";
    private static final String COLUMN_USER_TOKEN_EXPIRE_DATE = "token_expires_at";
    private static final String COLUMN_USER_IS_PROFILE_COMPLETED = "is_profile_completed";
    private static final String COLUMN_USER_IMAGE = "user_image";
    private static final String COLUMN_USER_WEIGHT = "weight";
    private static final String COLUMN_USER_HEIGHT = "height";
    private static final String COLUMN_USER_AGE = "age";
    private static final String COLUMN_USER_GENDER = "gender";
    private static final String COLUMN_USER_GOAL_ID = "goal_id";
    private static final String COLUMN_USER_GOAL = "goal";
    private static final String COLUMN_USER_LEVEL_ID = "level_id";
    private static final String COLUMN_USER_LEVEL = "level";
    private static final String COLUMN_USER_UNIT = "unit";
    private static final String COLUMN_USER_STRIPE_PRODUCT_ID = "product_id";
    private static final String COLUMN_USER_FOOD_PREFERENCE_ID = "food_preference_id";
    private static final String COLUMN_USER_FOOD_PREFERENCE = "food_preference";
    private static final String COLUMN_USER_ALLERGIES_IDS = "allergies_ids";
    private static final String COLUMN_USER_ALLERGIES = "allergies";
    private static final String COLUMN_USER_STRIPE_ID = "stripe_id";
    private static final String COLUMN_USER_STRIPE_STATUS = "stripe_status";
    private static final String COLUMN_USER_STRIPE_PRICE = "stripe_price";
    private static final String COLUMN_USER_SUBSCRIPTION_QUANTITY = "quantity";
    private static final String COLUMN_USER_TRIAL_ENDS_AT = "trial_ends_at";
    private static final String COLUMN_USER_SUBSCRIPTION_ENDS_AT = "ends_at";
    private static final String COLUMN_USER_MESSAGE = "message";


    //=============================================coachesProfiles Table Columns names=============================
    private static final String COLUMN_COACH_ID = "coach_id";
    private static final String COLUMN_COACH_NAME = "coach_name";
    private static final String COLUMN_COACH_IMAGE = "coach_image";
    private static final String COLUMN_COACH_DESCRIPTION = "coach_description";
    private static final String COLUMN_COACH_ROLE = "coach_role";
    private static final String COLUMN_COACH_WORKOUT_COUNT = "workout_count";
    private static final String COLUMN_COACH_LIMIT = "offset_limit";
    private static final String COLUMN_STATUS_CODE = "status_code";

    /*private static final String COLUMN_COACH_WARMUP = "coach_warmup";*/
    private static final String COLUMN_COACH_WORKOUT = "coach_workout";
    private static final String COLUMN_COACH_WORKOUT_ID = "coach_workout_id";

    private static final String COLUMN_COACH_DAY = "coach_workout_day";

    private static final String COLUMN_COACH_WEEK = "coach_workout_week";


    private static final String COLUMN_DAY = "day";

    private static final String COLUMN_WEEK = "week";
    private static final String WORKOUT_ID = "id";
    private static final String COLUMN_COACH_WORKOUT_NAME = "coach_workout_Name";
    private static final String COLUMN_COACH_WORKOUT_DESCRIPTION = "coach_workout_description";
    private static final String COLUMN_COACH_WORKOUT_EXERCISE_TYPE = "coach_workout_exercise_type";
    private static final String COLUMN_COACH_WORKOUT_EXERCISE_CATEGORY = "coach_workout_category";
    private static final String COLUMN_COACH_WORKOUT_VIDEO_URL = "coach_workout_video";
    private static final String COLUMN_COACH_WORKOUT_THUMBNAIL = "coach_workout_thumbnail";
    private static final String COLUMN_COACH_WORKOUT_REPS = "coach_workout_reps";
    private static final String COLUMN_COACH_WORKOUT_SETS = "coach_workout_sets";
    private static final String COLUMN_COACH_WORKOUT_DURATION = "coach_workout_duration";
    private static final String COLUMN_COACH_WORKOUT_IS_WATCHED = "coach_workout_watched_status";
    private static final String COLUMN_lOAD_DATE = "load_date";


    //========================================sleep visualization Table Columns names=================================
    private static final String COLUMN_SLEEP_VISUALIZATION_DAY = "visualization_day";
    private static final String COLUMN_SLEEP_VISUALIZATION_WEEK = "visualization_week";
    private static final String COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE = "visualization_playlist_image";
    private static final String COLUMN_SLEEP_VISUALIZATION_NAME = "visualization_name";
    private static final String COLUMN_SLEEP_VISUALIZATION_TIME = "visualization_time";
    private static final String COLUMN_SLEEP_VISUALIZATION_IMAGE = "visualization_imageUrl";
    private static final String COLUMN_SLEEP_VISUALIZATION_MUSIC = "visualization_audio";
    private static final String COLUMN_SLEEP_VISUALIZATION_DESCRIPTION = "visualization_description";


    //=========================================Dashboard recipes Table Columns names===================================

    //below static variables for recipes
    private static final String COLUMN_RECIPE_ID = "recipe_id";
    private static final String COLUMN_RECIPE_AUTO_ID = "auto_recipe_id";
    private static final String COLUMN_RECIPE_NAME = "recipe_name";
    private static final String COLUMN_RECIPE_TITLE = "recipe_title";
    private static final String COLUMN_RECIPE_IMAGE = "recipe_image_url";
    private static final String COLUMN_RECIPE_PAGE = "recipe_page_number";
    /*private static final String COLUMN_RECIPE_DURATION = "duration";
    private static final String COLUMN_RECIPE_COOK = "cook";*/
    /*private static final String COLUMN_RECIPE_METHODS = "methods";
    private static final String COLUMN_RECIPE_INGRIDENTS = "ingredients";*/
    private static final String COLUMN_RECIPE_DAY = "day";

    private static final String COLUMN_RECIPE_ADDED_IN_SHOPPING_LIST = "is_recipe_added_in_shopping_list";


    //=========================================User Shopping List Table Columns names===================================

    private static final String COLUMN_USER_SHOPPING_ID = "user_shopping_id";
    private static final String COLUMN_RECIPE_DURATION = "recipe_duration";
    private static final String COLUMN_RECIPE_COOK = "recipe_cook";
    private static final String COLUMN_RECIPE_INDEX = "recipe_index";

    private static final String COLUMN_RECIPE_TOTAL_CALORIES = "recipe_total_calories";
    private static final String COLUMN_RECIPE_METHOD = "recipe_method";

    private static final String COLUMN_RECIPE_METHOD_JSON = "recipe_method_JSON";


    private static final String COLUMN_RECIPE_SERVING = "recipe_serving";
    private static final String COLUMN_RECIPE_QUANTITY_TOTAL = "recipe_quantity_total";
    private static final String COLUMN_INGREDIENT_TOTAL = "total";
    /*private static final String COLUMN_RECIPE_INGREDIENTS = "recipe_ingrdients";*/
    private static final String COLUMN_RECIPE_CATEGORY_ID = "category_id";
    private static final String COLUMN_RECIPE_CATEGORY_NAME = "category_name";

   /* private static final String COLUMN_RECIPE_INGREDIENT_ID = "recipe_ingrdient_id";
    private static final String COLUMN_RECIPE_INGREDIENT_CATEGORY_NAME = "recipe_ingredient_category";
    private static final String COLUMN_RECIPE_INGREDIENT = "recipe_ingredient_name";
    private static final String COLUMN_RECIPE_INGREDIENT_UNIT = "ingredient_unit";
    private static final String COLUMN_RECIPE_INGREDIENT_QUANTITY = "ingredient_quantity";
    private static final String COLUMN_RECIPE_INGREDIENT_FATS = "ingredient_fats";
    private static final String COLUMN_RECIPE_INGREDIENT_CALORIES = "ingredient_calories";
    private static final String COLUMN_RECIPE_INGREDIENT_PROTIENS = "ingredient_protiens";
    private static final String COLUMN_RECIPE_INGREDIENT_CARBS = "ingredient_carbs";*/


    private static final String COLUMN_SHOPPING_ID = "shopping_id";

    //below variables have been created for ingredients
    private static final String COLUMN_INGREDIENTS_TABLE_ID = "ingredient_table_id";
    private static final String COLUMN_INGREDIENTS_ID = "ingredient_id";
    private static final String COLUMN_INGREDIENTS_CATEGORY = "category";
    private static final String COLUMN_INGREDIENTS_INGREDIENT = "ingredient";
    private static final String COLUMN_INGREDIENTS_UNIT = "unit";
    private static final String COLUMN_INGREDIENTS_QUANTITY = "quantity";
    private static final String COLUMN_INGREDIENTS_FATS = "fats";
    private static final String COLUMN_INGREDIENTS_CALORIES = "calories";
    private static final String COLUMN_INGREDIENTS_PROTEIN = "protein";
    private static final String COLUMN_INGREDIENTS_CARBS = "carbs";
    private static final String COLUMN_INGREDIENTS_RECIPE_ID = "recipe_id";
    private static final String COLUMN_INGREDIENTS_CATEGORY_ID = "category_id";
    private static final String COLUMN_RECIPE_INGREDIENT_STATUS = "ingredient_status";
    private static final String COLUMN_INGREDIENT_IS_CHECKED = "is_checked";
    private static final String COLUMN_INGREDIENT_IS_API_SYNCED = "is_api_synced";
    private static final String COLUMN_SERVER_CHECKED = "is_server_checked";

    //=============================== Program Table Columns names====================================================

    //program Table Columns names
    private static final String COLUMN_PROGRAM_ID = "program_id";
    private static final String COLUMN_PROGRAM_NAME = "program_name";
    private static final String COLUMN_PROGRAM_DESCRIPTION = "program_description";
    private static final String COLUMN_PROGRAM_THUMBNAIL = "program_thumbnail";
    private static final String COLUMN_PROGRAM_TOTAL_WEEKS = "total_weeks";


    //=============================== Progress Table Columns names====================================================

    //program Table Columns names
    private static final String COLUMN_PROGRAM_WATCHED_VIDEOS = "watched_videos";
    private static final String COLUMN_PROGRAM_TOTAL_VIDEOS = "total_videos";
    private static final String COLUMN_PROGRAM_UNLOCK_DAY = "unlock_day";
    private static final String COLUMN_PROGRAM_UNLOCK_WEEK = "unlock_week";
    private static final String COLUMN_PROGRAM_WORKOUT_NAME = "workout_name";
    private static final String COLUMN_PROGRAM_WORKOUT_ID = "workout_id";
    private static final String COLUMN_PROGRAM_DURATION = "workout_duration";
    private static final String COLUMN_PROGRAM_IS_WATCHED = "is_watched";
    private static final String COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL = "thumbnail_url";

    private static final String COLUMN_PROGRAM_DAY = "day";
    private static final String COLUMN_PROGRAM_WEEK = "week";
    private static final String COLUMN_PROGRAM = "selected_program_name";

    //=============================== Step count Table Columns names====================================================
    private static final String COLUMN_STEP_COUNTS_ID = "step_count_id";
    private static final String COLUMN_STEP_USER_ID = "user_id";
    private static final String COLUMN_STEPS_COUNT = "steps_count";
    private static final String COLUMN_WATER_INTAKE = "water_intake";
    private static final String COLUMN_STEPS_DISTANCE = "distance";
    private static final String COLUMN_STEPS_CALORIES = "calories";
    private static final String COLUMN_USER_TIME_ZONE = "user_time_zone";
    private static final String COLUMN_API_SYNCED_AT = "api_synced_at";
    private static final String COLUMN_IS_DAY_API_SYNCED = "is_day_api_synced";
    private static final String COLUMN_STEPS_LATITUDE = "lat";
    private static final String COLUMN_STEPS_LONGITUDE = "lng";
    private static final String COLUMN_STEPS_LOCATION = "location";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String COLUMN_ACTIVITY_DATE = "activity_date";

    //=============================== Notification Table Columns names====================================================

    private static final String COLUMN_NOTIFICATION_ID = "notification_id";

    private static final String COLUMN_NOTIFICATION_TITLE = "title";

    private static final String COLUMN_NOTIFICATION_DESCRIPTION = "description";

    private static final String COLUMN_NOTIFICATION_TIME = "time";

    private static final String COLUMN_NOTIFICATION_IS_READ = "is_read";

    //=============================== create users table sql query====================================================

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_ACCESS_TOKEN + " TEXT,"
            + COLUMN_USER_REFRESH_TOKEN + " TEXT," + COLUMN_USER_TOKEN_EXPIRE_DATE + " TEXT," +
            COLUMN_USER_IS_PROFILE_COMPLETED + " TEXT," + COLUMN_USER_IMAGE + " TEXT,"
            + COLUMN_USER_WEIGHT + " TEXT," + COLUMN_USER_HEIGHT + " TEXT,"
            + COLUMN_USER_AGE + " TEXT," + COLUMN_USER_GENDER + " TEXT,"
            + COLUMN_USER_GOAL_ID + " TEXT," + COLUMN_USER_GOAL + " TEXT," +
            COLUMN_USER_LEVEL_ID + " TEXT," + COLUMN_USER_LEVEL + " TEXT,"
            + COLUMN_USER_FOOD_PREFERENCE_ID + " TEXT," + COLUMN_USER_FOOD_PREFERENCE + " TEXT,"
            + COLUMN_USER_ALLERGIES_IDS + " TEXT," + COLUMN_USER_ALLERGIES + " TEXT,"
            + COLUMN_USER_UNIT + " TEXT," + COLUMN_USER_STRIPE_ID + " TEXT,"
            + COLUMN_USER_STRIPE_PRODUCT_ID + " TEXT,"
            + COLUMN_USER_STRIPE_STATUS + " TEXT," + COLUMN_USER_STRIPE_PRICE + " TEXT," +
            COLUMN_USER_SUBSCRIPTION_QUANTITY + " TEXT," + COLUMN_USER_TRIAL_ENDS_AT + " TEXT,"
            + COLUMN_USER_SUBSCRIPTION_ENDS_AT + " TEXT," + COLUMN_USER_MESSAGE + " TEXT" + ")";

    //======================================create coachesProfiles table Sql Query========================================

    private String CREATE_COACHES_TABLE = "CREATE TABLE " + TABLE_COACHES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COACH_ID + " INTEGER,"
            + COLUMN_COACH_NAME + " TEXT," + COLUMN_COACH_IMAGE + " TEXT,"
            + COLUMN_COACH_DESCRIPTION + " TEXT," + COLUMN_COACH_ROLE + " TEXT," + COLUMN_STATUS_CODE + " TEXT," + COLUMN_COACH_WORKOUT_COUNT + " INTEGER," + COLUMN_COACH_LIMIT + " INTEGER, " + COLUMN_CREATED_AT + " TEXT," +
            COLUMN_WEEK + " TEXT," + COLUMN_DAY + " TEXT)";

    private String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + "("
            + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTIFICATION_TITLE + " TEXT," + COLUMN_NOTIFICATION_DESCRIPTION + " TEXT," + COLUMN_NOTIFICATION_IS_READ + " TEXT," + COLUMN_USER_ID + " TEXT,"
            + COLUMN_NOTIFICATION_TIME + " TEXT" + ")";

    private String CREATE_COACHES_EXERCISE_TABLE = "CREATE TABLE " + TABLE_COACHES_EXERCISES + "("
            + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COACH_WORKOUT_ID + " INTEGER," + COLUMN_COACH_ID + " INTEGER,"
            + COLUMN_COACH_WORKOUT_NAME + " TEXT," + COLUMN_COACH_WORKOUT_DESCRIPTION + " TEXT,"
            + COLUMN_COACH_WORKOUT_EXERCISE_TYPE + " TEXT," + COLUMN_COACH_WORKOUT_EXERCISE_CATEGORY + " TEXT,"
            + COLUMN_COACH_WORKOUT_VIDEO_URL + " TEXT," + COLUMN_COACH_WORKOUT_THUMBNAIL + " TEXT,"
            + COLUMN_COACH_WORKOUT_REPS + " TEXT," + COLUMN_COACH_WORKOUT_SETS + " TEXT,"
            + COLUMN_COACH_WORKOUT_DURATION + " TEXT," + COLUMN_COACH_WORKOUT_IS_WATCHED + " INTEGER,"
            + COLUMN_COACH_WEEK + " INTEGER," + COLUMN_COACH_DAY + " INTEGER,"
            + COLUMN_lOAD_DATE + " TEXT,"
            + COLUMN_CREATED_AT + " TEXT,"
            + COLUMN_UPDATED_AT + " TEXT" + ")";


    //======================================create visualization table Sql Query========================================

    private String CREATE_SLEEP_VISUALIZATION_TABLE = "CREATE TABLE " + TABLE_SLEEP_VISUALIZATION + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SLEEP_VISUALIZATION_DAY + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_WEEK + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_NAME + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_TIME + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_IMAGE + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_MUSIC + " TEXT,"
            + COLUMN_SLEEP_VISUALIZATION_DESCRIPTION + " TEXT" + ")";


    //======================================create recipes_dashboard table Sql Query========================================

    private String CREATE_RECIPES_DASHBOARD_TABLE = "CREATE TABLE " + TABLE_DASHBOARD_RECIPES + "("
            + COLUMN_RECIPE_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_RECIPE_ID + " INTEGER ," + COLUMN_RECIPE_INDEX + " INTEGER,"
            + COLUMN_RECIPE_PAGE + " TEXT," + COLUMN_RECIPE_NAME + " TEXT,"
            + COLUMN_RECIPE_TITLE + " TEXT," + COLUMN_RECIPE_TOTAL_CALORIES + " TEXT,"
            + COLUMN_RECIPE_IMAGE + " TEXT," + COLUMN_WEEK + " TEXT," + COLUMN_DAY + " TEXT," + COLUMN_RECIPE_ADDED_IN_SHOPPING_LIST + " TEXT)";

    private String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
            + COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY," + COLUMN_RECIPE_TITLE + " TEXT,"
            + COLUMN_RECIPE_NAME + " TEXT," + COLUMN_RECIPE_DAY + " TEXT," + COLUMN_RECIPE_IMAGE + " TEXT," +
            COLUMN_RECIPE_DURATION + " TEXT," + COLUMN_RECIPE_METHOD_JSON + " TEXT,"
            + COLUMN_RECIPE_METHOD + " TEXT," + COLUMN_RECIPE_COOK + " TEXT,"
            + COLUMN_CREATED_AT + " TEXT," + COLUMN_UPDATED_AT + " TEXT" + ")";
         /*
            + COLUMN_RECIPE_INGRIDENTS + " TEXT" +*/

    //=========================================create Shopping List table Sql Query=======================================

    //create user shoppingList table
    private String CREATE_USER_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_USER_SHOPPING_LIST + "("
            + COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RECIPE_NAME + " TEXT,"
            + COLUMN_RECIPE_TITLE + " TEXT,"
            + COLUMN_RECIPE_IMAGE + " TEXT,"
            + COLUMN_RECIPE_DURATION + " TEXT,"
            + COLUMN_RECIPE_COOK + " TEXT,"
            + COLUMN_RECIPE_METHOD + " TEXT,"
            + COLUMN_RECIPE_SERVING + " TEXT" + ")";
          /*  + COLUMN_RECIPE_INGREDIENT_ID + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_CATEGORY_NAME + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_UNIT + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_QUANTITY + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_FATS + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_CALORIES + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_PROTIENS + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_CARBS + " TEXT,"
            + COLUMN_RECIPE_INGREDIENT_STATUS + " TEXT" + ")";
            */


    //create Category  table Sql Query
    private String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_INGREDIENT_CATEGORIES + "("
            + COLUMN_RECIPE_CATEGORY_ID + " INTEGER PRIMARY KEY," + COLUMN_RECIPE_CATEGORY_NAME + " TEXT" + ")";


    //create shoppingList table
    private String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + "("
            + COLUMN_SHOPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RECIPE_ID + " INTEGER,"
            + COLUMN_USER_ID + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";


    //exercise Table Columns names
    private static final String COLUMN_BEST_PROGRAM_WORKOUT = "best_program_workout";
    private static final String COLUMN_BEST_PROGRAM_WARMUP = "best_program_warmup";

    //Pivot table Column names
    private static final String COLUMN_PIVOT_ID = "pivot_id";
    private static final String COLUMN_PIVOT_CATEGORY_ID = "category_id";
    private static final String COLUMN_PIVOT_RECIPE_ID = "recipe_id";
    private static final String COLUMN_PIVOT_INGREDIENTS_ID = "ingredient_id";


    //below variables have been created for categories
    private static final String COLUMN_CATEGORIES_ID = "category_id";
    private static final String COLUMN_CATEGORIES_NAME = "category_name";


    //===============================================create program table Sql Query===========================================

    private String CREATE_PROGRAM_TABLE = "CREATE TABLE " + TABLE_PROGRAM + "("
            + COLUMN_PROGRAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PROGRAM_NAME + " TEXT,"
            + COLUMN_PROGRAM_THUMBNAIL + " TEXT,"
            + COLUMN_PROGRAM_DESCRIPTION + " TEXT" + ")";


    //===============================================create program details table Sql Query===========================================

    private String CREATE_PROGRESS_TABLE = "CREATE TABLE " + TABLE_PROGRESS_FRAGMENT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROGRAM + " TEXT," + COLUMN_PROGRAM_TOTAL_WEEKS + " TEXT,"
            + COLUMN_PROGRAM_TOTAL_VIDEOS + " TEXT," + COLUMN_PROGRAM_WATCHED_VIDEOS + " TEXT,"
            + COLUMN_PROGRAM_UNLOCK_WEEK + " TEXT,"
            + COLUMN_PROGRAM_UNLOCK_DAY + " TEXT" + ")";


    //===============================================create program EXERCISE table Sql Query===========================================
    //create exercise table
    private String CREATE_PROGRAM_EXERCISES_TABLE = "CREATE TABLE " + TABLE_PROGRAM_EXERCISES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROGRAM_ID + " INTEGER,"
            + COLUMN_PROGRAM + " TEXT,"
            + COLUMN_PROGRAM_DESCRIPTION + " TEXT,"
            + COLUMN_PROGRAM_WORKOUT_NAME + " TEXT,"
            + COLUMN_PROGRAM_WORKOUT_ID + " INTEGER,"
            + COLUMN_PROGRAM_DURATION + " TEXT,"
            + COLUMN_PROGRAM_IS_WATCHED + " TEXT,"
            + COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL + " TEXT,"
            + COLUMN_PROGRAM_WEEK + " TEXT,"
            + COLUMN_PROGRAM_DAY + " TEXT" + ")";


    //===============================================create check uncheck table Sql Query===========================================
    //create exercise table
    private String CREATE_CHECK_UNCHECK_TABLE = "CREATE TABLE " + TABLE_CHECK_UNCHECK + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INGREDIENTS_ID + " INTEGER,"
            + COLUMN_RECIPE_ID + " INTEGER,"
            + COLUMN_RECIPE_SERVING + " INTEGER,"
            + COLUMN_INGREDIENT_IS_CHECKED + " TEXT,"
            + COLUMN_SERVER_CHECKED + " TEXT,"
            + COLUMN_INGREDIENT_IS_API_SYNCED + " TEXT" + ")";


    //private static final Boolean COLUMN_USER_STATUS = Boolean.valueOf("user_status");
    private static final String COLUMN_USER_STATUS = "user_status";
    //private static final String COLUMN_USER_CVC = "user_cvc";


    private static final String COLUMN_USER_SUBSCRIPTION = "subscription_id";
    //private static final String COLUMN_USER_STRIPE_ID = "stripe_id";

     /*private static final Boolean COLUMN_PROFILE_ACTIVATION = Boolean.valueOf("isProfileActivated");
    private static final String COLUMN_EXPIRY_DATE = "expiryDate";
    private static final String COLUMN_CARD_NUMBER = "cardNumber";
    private static final String COLUMN_PROFILE_IMAGE = "profileImage";*/



    /*//create nutrition table
    private String CREATE_NUTRITION_TABLE = "CREATE TABLE " + TABLE_NUTRITION + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NUTRITION_ID + " INTEGER,"
            + COLUMN_NUTRITION_NAME + " TEXT," + COLUMN_NUTRITION_TIME + " TEXT,"
            + COLUMN_NUTRITION_DAY + " TEXT," + COLUMN_NUTRITION_IMAGE + " TEXT," + COLUMN_NUTRITION_METHOD + " TEXT,"
            + COLUMN_NUTRITION_INGREDIENTS + " TEXT" + ")";*/

    //create Pivot table for recipe
    private String CREATE_PIVOT_RECIPE_TABLE = "CREATE TABLE " + TABLE_PIVOT_RECIPE + "("
            + COLUMN_PIVOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PIVOT_RECIPE_ID + " INTEGER,"
            + COLUMN_PIVOT_CATEGORY_ID + " INTEGER ," + COLUMN_PIVOT_INGREDIENTS_ID + " INTEGER ,"
            + "FOREIGN KEY(" + COLUMN_PIVOT_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + " ), "
            + "FOREIGN KEY(" + COLUMN_PIVOT_CATEGORY_ID + ") REFERENCES " + TABLE_INGREDIENT_CATEGORIES + "(" + COLUMN_CATEGORIES_ID + " ), "
            + "FOREIGN KEY(" + COLUMN_PIVOT_INGREDIENTS_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENTS_ID + ") )";

    /*//create shoppingList table
    private String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + "("
            + COLUMN_SHOPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RECIPE_ID + " INTEGER,"
            + COLUMN_USER_ID + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";*/


    // create user profiles table sql query
  /*  private String CREATE_USER_PROFILE_TABLE = "CREATE TABLE " + TABLE_USER_PROFILE + "("
            + COLUMN_USER_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_WEIGHT + " TEXT,"
            + COLUMN_USER_HEIGHT + " TEXT," + COLUMN_USER_AGE + " TEXT," + COLUMN_USER_GENDER + " TEXT,"
            + COLUMN_USER_GOAL_ID + " TEXT," + COLUMN_USER_LEVEL_ID + " TEXT," + COLUMN_USER_UNIT_TYPE + " TEXT,"
            + COLUMN_USER_GOAL + " TEXT," + COLUMN_USER_LEVEL + " TEXT,"
            + COLUMN_USER_AVATAR + " TEXT," + COLUMN_USER_ID + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ") )";*/


    private String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENTS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INGREDIENTS_ID + " INTEGER," + COLUMN_INGREDIENTS_CATEGORY + " TEXT,"
            + COLUMN_INGREDIENTS_INGREDIENT + " TEXT,"
            + COLUMN_INGREDIENTS_UNIT + " TEXT," + COLUMN_INGREDIENTS_QUANTITY + " TEXT,"
            + COLUMN_INGREDIENTS_FATS + " TEXT,"
            + COLUMN_INGREDIENTS_CALORIES + " TEXT,"
            + COLUMN_INGREDIENTS_PROTEIN + " TEXT,"
            + COLUMN_INGREDIENTS_CARBS + " TEXT ,"
            + COLUMN_INGREDIENTS_RECIPE_ID + " INTEGER ,"
            + COLUMN_RECIPE_SERVING + " TEXT ,"
            + COLUMN_RECIPE_INGREDIENT_STATUS + " TEXT" + ")";


    private String CREATE_SHOPPING_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_SHOPPING_INGREDIENTS + "(" + COLUMN_INGREDIENTS_TABLE_ID + " TEXT,"
            + COLUMN_INGREDIENTS_ID + " INTEGER," + COLUMN_INGREDIENTS_CATEGORY + " TEXT,"
            + COLUMN_INGREDIENTS_INGREDIENT + " TEXT,"
            + COLUMN_INGREDIENTS_UNIT + " TEXT," + COLUMN_INGREDIENTS_QUANTITY + " TEXT,"
            + COLUMN_INGREDIENTS_FATS + " TEXT,"
            + COLUMN_INGREDIENTS_CALORIES + " TEXT,"
            + COLUMN_INGREDIENTS_PROTEIN + " TEXT,"
            + COLUMN_INGREDIENTS_CARBS + " TEXT ,"
            + COLUMN_INGREDIENTS_RECIPE_ID + " INTEGER ,"
            + COLUMN_RECIPE_SERVING + " TEXT ,"
            + COLUMN_RECIPE_QUANTITY_TOTAL + " TEXT ,"
            + COLUMN_RECIPE_INGREDIENT_STATUS + " TEXT" + ")";
    //===============================================create program EXERCISE table Sql Query===========================================
    //create exercise table
    private String CREATE_STEP_COUNTS = "CREATE TABLE " + TABLE_STEP_COUNTS + "("
            + COLUMN_STEP_COUNTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STEP_USER_ID + " INTEGER,"
            + COLUMN_STEPS_COUNT + " TEXT,"
            + COLUMN_WATER_INTAKE + " TEXT,"
            + COLUMN_STEPS_DISTANCE + " TEXT,"
            + COLUMN_STEPS_CALORIES + " TEXT,"
            + COLUMN_USER_TIME_ZONE + " TEXT,"
            + COLUMN_API_SYNCED_AT + " TEXT,"
            + COLUMN_IS_DAY_API_SYNCED + " TEXT,"
            + COLUMN_STEPS_LATITUDE + " TEXT,"
            + COLUMN_STEPS_LONGITUDE + " TEXT,"
            + COLUMN_STEPS_LOCATION + " TEXT,"
            + COLUMN_ACTIVITY_DATE + " TEXT,"
            + COLUMN_CREATED_AT + " TEXT,"
            + COLUMN_UPDATED_AT + " TEXT" + ")";


    // drop tables sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_USER_PROFILE_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER_PROFILE;
    private String DROP_SLEEP_VISUALIZATION_TABLE = "DROP TABLE IF EXISTS " + TABLE_SLEEP_VISUALIZATION;
    private String DROP_RECIPE_DASHBOARD_TABLE = "DROP TABLE IF EXISTS " + TABLE_DASHBOARD_RECIPES;
    private String DROP_USER_SHOPPING_LIST_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER_SHOPPING_LIST;
    private String DROP_RECIPE_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECIPES;
    private String DROP_INGREDIENTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_INGREDIENTS;
    private String DROP_SHOPPING_INGREDIENTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_INGREDIENTS;
    private String DROP_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + TABLE_INGREDIENT_CATEGORIES;
    private String DROP_COACHES_TABLE = "DROP TABLE IF EXISTS " + TABLE_COACHES;
    private String DROP_COACHES_EXERCISE_TABLE = "DROP TABLE IF EXISTS " + TABLE_COACHES_EXERCISES;
    private String DROP_NUTRITION_TABLE = "DROP TABLE IF EXISTS " + TABLE_NUTRITION;
    private String DROP_SHOPPING_LIST_TABLE = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST;
    private String DROP_PROGRAM_TABLE = "DROP TABLE IF EXISTS " + TABLE_PROGRAM;
    private String DROP_PROGRESS_TABLE = "DROP TABLE IF EXISTS " + TABLE_PROGRESS_FRAGMENT;
    private String DROP_PROGRAM_EXERCISES_TABLE = "DROP TABLE IF EXISTS " + TABLE_PROGRAM_EXERCISES;
    private String DROP_CHECK_UNCHECK_TABLE = "DROP TABLE IF EXISTS " + TABLE_CHECK_UNCHECK;
    private String DROP_PIVOT_RECIPE_TABLE = "DROP TABLE IF EXISTS " + TABLE_PIVOT_RECIPE;
    private String DROP_STEP_COUNTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_STEP_COUNTS;

    private String DROP_NOTIFICATION_TABLE = "DROP TABLE IF EXISTS " + TABLE_NOTIFICATION;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        //  db.execSQL(CREATE_USER_PROFILE_TABLE);
        db.execSQL(CREATE_COACHES_TABLE);
        db.execSQL(CREATE_COACHES_EXERCISE_TABLE);
        db.execSQL(CREATE_SLEEP_VISUALIZATION_TABLE);
        db.execSQL(CREATE_RECIPES_DASHBOARD_TABLE);
        db.execSQL(CREATE_USER_SHOPPING_LIST_TABLE);
        db.execSQL(CREATE_PROGRAM_TABLE);
        db.execSQL(CREATE_PROGRESS_TABLE);
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_SHOPPING_INGREDIENTS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        /*db.execSQL(CREATE_NUTRITION_TABLE);*/
        db.execSQL(CREATE_SHOPPING_LIST_TABLE);

        db.execSQL(CREATE_PROGRAM_EXERCISES_TABLE);
        db.execSQL(CREATE_CHECK_UNCHECK_TABLE);


        db.execSQL(CREATE_PIVOT_RECIPE_TABLE);
        db.execSQL(CREATE_STEP_COUNTS);
        db.execSQL(CREATE_NOTIFICATION_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_USER_PROFILE_TABLE);
        db.execSQL(DROP_SLEEP_VISUALIZATION_TABLE);
        db.execSQL(DROP_RECIPE_DASHBOARD_TABLE);
        db.execSQL(DROP_USER_SHOPPING_LIST_TABLE);
        db.execSQL(DROP_RECIPE_TABLE);
        db.execSQL(DROP_INGREDIENTS_TABLE);
        db.execSQL(DROP_CATEGORIES_TABLE);
        db.execSQL(DROP_COACHES_TABLE);
        db.execSQL(DROP_COACHES_EXERCISE_TABLE);
        db.execSQL(DROP_NUTRITION_TABLE);
        db.execSQL(DROP_SHOPPING_LIST_TABLE);
        db.execSQL(DROP_PROGRAM_TABLE);
        db.execSQL(DROP_PROGRESS_TABLE);
        db.execSQL(DROP_PROGRAM_EXERCISES_TABLE);
        db.execSQL(DROP_CHECK_UNCHECK_TABLE);
        db.execSQL(DROP_PIVOT_RECIPE_TABLE);
        db.execSQL(DROP_STEP_COUNTS_TABLE);
        db.execSQL(DROP_SHOPPING_INGREDIENTS_TABLE);
        db.execSQL(DROP_NOTIFICATION_TABLE);
        // Create table
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_USER_PROFILE_TABLE);
        db.execSQL(DROP_RECIPE_TABLE);
        db.execSQL(DROP_INGREDIENTS_TABLE);
        db.execSQL(DROP_CATEGORIES_TABLE);
        db.execSQL(DROP_COACHES_TABLE);
        db.execSQL(DROP_NUTRITION_TABLE);
        db.execSQL(DROP_SHOPPING_LIST_TABLE);
        db.execSQL(DROP_PROGRAM_TABLE);
        db.execSQL(DROP_PROGRAM_EXERCISES_TABLE);
        db.execSQL(DROP_SLEEP_VISUALIZATION_TABLE);
        db.execSQL(DROP_PIVOT_RECIPE_TABLE);*/
        super.onDowngrade(db, oldVersion, newVersion);
    }


    /**
     * This method is to create user record
     *
     * @param user
     */
    //==================================================METHODS FOR USERS TABLE=====================================
    //insert User data into Table
    public void addUser(LoginResponse user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //User
        values.put(COLUMN_USER_ID, user.getData().getUser().getId());
        values.put(COLUMN_USER_NAME, user.getData().getUser().getName());
        values.put(COLUMN_USER_EMAIL, user.getData().getUser().getEmail());
        values.put(COLUMN_USER_IS_PROFILE_COMPLETED, user.getData().getUser().getIs_profile_completed());
        //Data
        values.put(COLUMN_USER_ACCESS_TOKEN, user.getData().getAccess_token());
        values.put(COLUMN_USER_REFRESH_TOKEN, user.getData().getRefresh_token());
        values.put(COLUMN_USER_TOKEN_EXPIRE_DATE, user.getData().getToken_expires_at());
        //Profile
        if (user.getData().getUser().getProfile() != null) {
            values.put(COLUMN_USER_IMAGE, "" + user.getData().getUser().getProfile().getUser_image());
            values.put(COLUMN_USER_WEIGHT, user.getData().getUser().getProfile().getWeight());
            values.put(COLUMN_USER_HEIGHT, user.getData().getUser().getProfile().getHeight());
            values.put(COLUMN_USER_AGE, user.getData().getUser().getProfile().getAge());
            values.put(COLUMN_USER_GENDER, user.getData().getUser().getProfile().getGender());
            values.put(COLUMN_USER_GOAL_ID, user.getData().getUser().getProfile().getGoal_id());
            values.put(COLUMN_USER_GOAL, user.getData().getUser().getProfile().getGoal());
            values.put(COLUMN_USER_LEVEL_ID, user.getData().getUser().getProfile().getLevel_id());
            values.put(COLUMN_USER_LEVEL, user.getData().getUser().getProfile().getLevel());
            values.put(COLUMN_USER_UNIT, user.getData().getUser().getProfile().getUnit());

            /*if (user.getData().getUser().getProfile().getFood_preference_id() != null)
                values.put(COLUMN_USER_FOOD_PREFERENCE_ID, user.getData().getUser().getProfile().getFood_preference_id());
            if (user.getData().getUser().getProfile().getFood_preference() != null)
                values.put(COLUMN_USER_FOOD_PREFERENCE, user.getData().getUser().getProfile().getFood_preferences());

            if (user.getData().getUser().getProfile().getAllergy_ids() != null)
                values.put(COLUMN_USER_ALLERGIES_IDS, user.getData().getUser().getProfile().getAllergy_ids());
            if (user.getData().getUser().getProfile().getAllergie() != null && user.getData().getUser().getProfile().getAllergies().size()>0)
                values.put(COLUMN_USER_ALLERGIES,   TextUtils.join(",",user.getData().getUser().getProfile().getAllergies()));*/

        }
        //Subscription
        if (user.getData().getUser().getSubscription() != null) {
            values.put(COLUMN_USER_STRIPE_ID, user.getData().getUser().getSubscription().getStripe_id());
            values.put(COLUMN_USER_STRIPE_STATUS, user.getData().getUser().getSubscription().getStripe_status());
            values.put(COLUMN_USER_STRIPE_PRICE, user.getData().getUser().getSubscription().getStripe_price());
            values.put(COLUMN_USER_SUBSCRIPTION_QUANTITY, user.getData().getUser().getSubscription().getQuantity());
            values.put(COLUMN_USER_TRIAL_ENDS_AT, user.getData().getUser().getSubscription().getTrial_ends_at());
            values.put(COLUMN_USER_SUBSCRIPTION_ENDS_AT, user.getData().getUser().getSubscription().getEnds_at());


        }
        values.put(COLUMN_USER_MESSAGE, user.getMessage());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    //get user data from USER Table
    @SuppressLint("Range")
    public LoginResponse getUserData(String id) {

        String sqlQuery = "SELECT * FROM " + TABLE_USER +
                " WHERE " + COLUMN_USER_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sqlQuery, null);
        LoginResponse loginResponse = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                loginResponse = new LoginResponse();
                // loginResponse data = new SignupResponse.Data();
                // User Class
                loginResponse.getData().getUser().setId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                loginResponse.getData().getUser().setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                loginResponse.getData().getUser().setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                loginResponse.getData().getUser().setIs_profile_completed(cursor.getString(cursor.getColumnIndex(COLUMN_USER_IS_PROFILE_COMPLETED)));
                //Data Class
                loginResponse.getData().setAccess_token(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ACCESS_TOKEN)));
                loginResponse.getData().setRefresh_token(cursor.getString(cursor.getColumnIndex(COLUMN_USER_REFRESH_TOKEN)));
                loginResponse.getData().setToken_expires_at(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TOKEN_EXPIRE_DATE)));

                //Profile
                loginResponse.getData().getUser().getProfile().setUser_image(cursor.getString(cursor.getColumnIndex(COLUMN_USER_IMAGE)));
                loginResponse.getData().getUser().getProfile().setWeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_WEIGHT)));
                loginResponse.getData().getUser().getProfile().setHeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEIGHT)));
                loginResponse.getData().getUser().getProfile().setAge(cursor.getString(cursor.getColumnIndex(COLUMN_USER_AGE)));
                loginResponse.getData().getUser().getProfile().setGender(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GENDER)));
                loginResponse.getData().getUser().getProfile().setGoal_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL_ID)));
                loginResponse.getData().getUser().getProfile().setGoal(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL)));
                loginResponse.getData().getUser().getProfile().setLevel_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL_ID)));
                loginResponse.getData().getUser().getProfile().setLevel(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL)));
                loginResponse.getData().getUser().getProfile().setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_USER_UNIT)));

                //Subscription
                loginResponse.getData().getUser().getSubscription().setStripe_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_STRIPE_ID)));
                loginResponse.getData().getUser().getSubscription().setStripe_status(cursor.getString(cursor.getColumnIndex(COLUMN_USER_STRIPE_STATUS)));
                loginResponse.getData().getUser().getSubscription().setStripe_price(cursor.getString(cursor.getColumnIndex(COLUMN_USER_STRIPE_PRICE)));
                loginResponse.getData().getUser().getSubscription().setQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_USER_SUBSCRIPTION_QUANTITY)));
                loginResponse.getData().getUser().getSubscription().setTrial_ends_at(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TRIAL_ENDS_AT)));
                loginResponse.getData().getUser().getSubscription().setEnds_at(cursor.getString(cursor.getColumnIndex(COLUMN_USER_SUBSCRIPTION_ENDS_AT)));
                loginResponse.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_MESSAGE)));
            }
        }
        return loginResponse;

    }

    //Update User Data
    public void updateUserProfile(SignupResponse user) {
        SQLiteDatabase db = this.getWritableDatabase();

        //For User table
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getData().getId());
        values.put(COLUMN_USER_NAME, user.getData().getName());
        values.put(COLUMN_USER_IMAGE, user.getData().getAvatar());
        values.put(COLUMN_USER_WEIGHT, user.getData().getWeight());
        values.put(COLUMN_USER_HEIGHT, user.getData().getHeight());
        values.put(COLUMN_USER_AGE, user.getData().getAge());
        values.put(COLUMN_USER_GENDER, user.getData().getGender());
        values.put(COLUMN_USER_GOAL_ID, user.getData().getGoal_id());
        values.put(COLUMN_USER_GOAL, user.getData().getGoal());
        values.put(COLUMN_USER_LEVEL_ID, user.getData().getLevel_id());
        values.put(COLUMN_USER_LEVEL, user.getData().getLevel());
        values.put(COLUMN_USER_UNIT, user.getData().getUnit());
        values.put(COLUMN_USER_MESSAGE, user.getMessage());

        if (user.getData().getFood_preference_id() != null)
            values.put(COLUMN_USER_FOOD_PREFERENCE_ID, user.getData().getFood_preference_id());
        /*if (user.getData().getFood_preference() != null)
        values.put(COLUMN_USER_FOOD_PREFERENCE, user.getData().getFood_preference());

        if (user.getData().getAllergy_ids() != null)
        values.put(COLUMN_USER_ALLERGIES_IDS, user.getData().getAllergy_ids());*/
        if (user.getData().getAllergies() != null && user.getData().getAllergies().size() > 0) {
            values.put(COLUMN_USER_ALLERGIES, TextUtils.join(",", user.getData().getAllergies()));
        }

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getData().getId())});


        db.close();
    }

    //  Delete User Details
    public void deleteUser(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userID)});
        db.close();
    }


    //  ============================USER TABLE METHODS ENDS==========================================================

    //==============================METHODS For COACHES PROFILES TABLE================================================

    //add coaches prodile data to table
    public void addCoachesProfilesData(CoachesProfileDataModel coach) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        for (CoachesProfileDataModel.Data c : coach.getData()) {
            ContentValues values = new ContentValues();

            // convert warmup list to json

          /*  List<CoachesDataModel.Warmup> warmUpList = c.getWarmup();
            gson = new Gson();
            String inputWarmup = gson.toJson(warmUpList);

            System.out.println(inputWarmup + " warmup list==========================lll=======================");
            System.out.println(c.getWarmup() + " warmup ==========================jjjj===================================");

            // convert workout list to json
            List<CoachesDataModel.Workout> workUpList = c.workouts;
            String inputWorkout = gson.toJson(workUpList);*/

            values.put(COLUMN_COACH_ID, c.getId());
            values.put(COLUMN_COACH_NAME, c.getName());
            values.put(COLUMN_COACH_IMAGE, c.getImageURL());
            values.put(COLUMN_COACH_DESCRIPTION, c.getDescription());
            values.put(COLUMN_COACH_ROLE, c.getRole());
            values.put(COLUMN_COACH_WORKOUT_COUNT, c.getWorkoutCount());
            values.put(COLUMN_COACH_LIMIT, c.getLimit());
            //  values.put(COLUMN_STATUS_CODE,code);

           /* values.put(COLUMN_COACH_WARMUP, inputWarmup);
            values.put(COLUMN_COACH_WORKOUT, inputWorkout);*/

            db.insert(TABLE_COACHES, null, values);
        }
        db.close();

    }

    public void addStepCount(StepCountModel.Data stepCountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_STEP_USER_ID, stepCountModel.getUserID());
        values.put(COLUMN_STEPS_COUNT, stepCountModel.getStepsCount());
        values.put(COLUMN_WATER_INTAKE, stepCountModel.getWaterCount());
        values.put(COLUMN_STEPS_DISTANCE, stepCountModel.getDistance());
        values.put(COLUMN_STEPS_CALORIES, stepCountModel.getCalories());
        values.put(COLUMN_USER_TIME_ZONE, stepCountModel.getUserTimeZone());
        values.put(COLUMN_API_SYNCED_AT, stepCountModel.getApi_synced_at());
        values.put(COLUMN_IS_DAY_API_SYNCED, stepCountModel.getIs_day_api_synced());
        values.put(COLUMN_STEPS_LATITUDE, stepCountModel.getActivityLat());
        values.put(COLUMN_STEPS_LONGITUDE, stepCountModel.getActivityLong());
        values.put(COLUMN_STEPS_LOCATION, stepCountModel.getActivityLocation());
        values.put(COLUMN_ACTIVITY_DATE, stepCountModel.getUserActivityDate());
        values.put(COLUMN_CREATED_AT, dateFormat.format(date));
        values.put(COLUMN_UPDATED_AT, "");

        db.insert(TABLE_STEP_COUNTS, null, values);

        db.close();
    }

    public int updateStepCount(StepCountModel.Data stepCountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_STEPS_COUNT, stepCountModel.getStepsCount());
        //values.put(COLUMN_WATER_INTAKE, stepCountModel.getWaterCount());
        values.put(COLUMN_STEPS_DISTANCE, stepCountModel.getDistance());
        values.put(COLUMN_STEPS_CALORIES, stepCountModel.getCalories());
        values.put(COLUMN_USER_TIME_ZONE, stepCountModel.getUserTimeZone());
        values.put(COLUMN_API_SYNCED_AT, stepCountModel.getApi_synced_at());
        values.put(COLUMN_IS_DAY_API_SYNCED, stepCountModel.getIs_day_api_synced());
        values.put(COLUMN_STEPS_LATITUDE, stepCountModel.getActivityLat());
        values.put(COLUMN_STEPS_LONGITUDE, stepCountModel.getActivityLong());
        values.put(COLUMN_STEPS_LOCATION, stepCountModel.getActivityLocation());
        values.put(COLUMN_ACTIVITY_DATE, stepCountModel.getUserActivityDate());
        values.put(COLUMN_UPDATED_AT, dateFormat.format(date));

        int rowsUpdated = 0;
        rowsUpdated = db.update(TABLE_STEP_COUNTS, values, COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?", new String[]{"" + stepCountModel.getUserID(), stepCountModel.getUserActivityDate()});

        db.close();

        return rowsUpdated;
    }

    public int updateActivity(StepCountModel.Data stepCountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_STEPS_COUNT, stepCountModel.getStepsCount());
        values.put(COLUMN_WATER_INTAKE, stepCountModel.getWaterCount());
        values.put(COLUMN_STEPS_DISTANCE, stepCountModel.getDistance());
        values.put(COLUMN_STEPS_CALORIES, stepCountModel.getCalories());
        values.put(COLUMN_USER_TIME_ZONE, stepCountModel.getUserTimeZone());
        values.put(COLUMN_API_SYNCED_AT, stepCountModel.getApi_synced_at());
        values.put(COLUMN_IS_DAY_API_SYNCED, stepCountModel.getIs_day_api_synced());
        values.put(COLUMN_STEPS_LATITUDE, stepCountModel.getActivityLat());
        values.put(COLUMN_STEPS_LONGITUDE, stepCountModel.getActivityLong());
        values.put(COLUMN_STEPS_LOCATION, stepCountModel.getActivityLocation());
        values.put(COLUMN_ACTIVITY_DATE, stepCountModel.getUserActivityDate());
        values.put(COLUMN_UPDATED_AT, dateFormat.format(date));

        int rowsUpdated = 0;
        rowsUpdated = db.update(TABLE_STEP_COUNTS, values, COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?", new String[]{"" + stepCountModel.getUserID(), stepCountModel.getUserActivityDate()});

        db.close();

        return rowsUpdated;
    }

    public void addWaterIntake(String userID, String waterIntake, String timeZone, String activityDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_STEP_USER_ID, userID);
        values.put(COLUMN_STEPS_COUNT, "0");
        values.put(COLUMN_WATER_INTAKE, waterIntake);
        values.put(COLUMN_STEPS_DISTANCE, "0.0");
        values.put(COLUMN_STEPS_CALORIES, "0.0");
        values.put(COLUMN_USER_TIME_ZONE, timeZone);
        values.put(COLUMN_API_SYNCED_AT, "");
        values.put(COLUMN_IS_DAY_API_SYNCED, "");
        values.put(COLUMN_STEPS_LATITUDE, "0.0");
        values.put(COLUMN_STEPS_LONGITUDE, "0.0");
        values.put(COLUMN_STEPS_LOCATION, "");
        values.put(COLUMN_ACTIVITY_DATE, activityDate);
        values.put(COLUMN_CREATED_AT, dateFormat.format(date));
        values.put(COLUMN_UPDATED_AT, "");

        db.insert(TABLE_STEP_COUNTS, null, values);

        db.close();
    }

    public int updateWaterIntake(String userID, String activityDate, String waterIntake) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_WATER_INTAKE, waterIntake);
        values.put(COLUMN_API_SYNCED_AT, "");
        values.put(COLUMN_IS_DAY_API_SYNCED, "");
        values.put(COLUMN_UPDATED_AT, dateFormat.format(date));

        int rowsUpdated = 0;
        rowsUpdated = db.update(TABLE_STEP_COUNTS, values, COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?", new String[]{"" + userID, activityDate});

        db.close();

        return rowsUpdated;
    }

    public int updateAPI_Sync(String userID, String activityDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_API_SYNCED_AT, dateFormat.format(date));
        values.put(COLUMN_IS_DAY_API_SYNCED, dateFormat.format(date));
        values.put(COLUMN_UPDATED_AT, dateFormat.format(date));

        int rowsUpdated = 0;
        rowsUpdated = db.update(TABLE_STEP_COUNTS, values, COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?", new String[]{"" + userID, activityDate});

        db.close();

        return rowsUpdated;
    }

    public boolean isUserStepCountAvailable(String userID, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*String sqlQuery = "SELECT * FROM " + TABLE_STEP_COUNTS +
                " WHERE " + COLUMN_STEP_USER_ID + " = ? AND "
                + COLUMN_ACTIVITY_DATE + " = ?";*/
        String[] column = {COLUMN_STEP_COUNTS_ID};
        String selection = COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?";
        Cursor cursor = db.query(TABLE_STEP_COUNTS, column, selection, new String[]{userID, date}, null, null, null);
        //Cursor cursor = db.rawQuery(sqlQuery, new String[]{userID, date});
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    @SuppressLint("Range")
    public List<StepCountModel.Data> getUserActivityByUserID_ByAPI_SyncedAt(String userID) {
        String[] columns = {
                COLUMN_STEP_COUNTS_ID,
                COLUMN_STEP_USER_ID,
                COLUMN_STEPS_COUNT,
                COLUMN_WATER_INTAKE,
                COLUMN_STEPS_DISTANCE,
                COLUMN_STEPS_CALORIES,
                COLUMN_USER_TIME_ZONE,
                COLUMN_API_SYNCED_AT,
                COLUMN_IS_DAY_API_SYNCED,
                COLUMN_STEPS_LATITUDE,
                COLUMN_STEPS_LONGITUDE,
                COLUMN_STEPS_LOCATION,
                COLUMN_ACTIVITY_DATE,
                COLUMN_CREATED_AT,
                COLUMN_UPDATED_AT

        };
        String selection = COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_API_SYNCED_AT + " = ?";
        String[] selectionArgs = {userID, ""};
        String sortOrder =
                COLUMN_STEPS_COUNT + " ASC";
        List<StepCountModel.Data> activities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        Cursor cursor = db.query(TABLE_STEP_COUNTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        if (cursor.moveToFirst()) {
            do {
                StepCountModel.Data activity = new StepCountModel.Data();
                activity.setStepCountID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_COUNTS_ID)));
                activity.setUserID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_USER_ID)));
                activity.setStepsCount(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_COUNT)));
                activity.setWaterCount(cursor.getString(cursor.getColumnIndex(COLUMN_WATER_INTAKE)));
                activity.setDistance(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_DISTANCE)));
                activity.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_CALORIES)));
                activity.setUserTimeZone(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TIME_ZONE)));
                activity.setApi_synced_at(cursor.getString(cursor.getColumnIndex(COLUMN_API_SYNCED_AT)));
                activity.setIs_day_api_synced(cursor.getString(cursor.getColumnIndex(COLUMN_IS_DAY_API_SYNCED)));
                activity.setActivityLat(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LATITUDE)));
                activity.setActivityLong(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LONGITUDE)));
                activity.setActivityLocation(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LOCATION)));
                activity.setUserActivityDate(cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DATE)));
                activity.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                activity.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;

    }

    @SuppressLint("Range")
    public List<StepCountModel.Data> getUserActivityByUserID_ByAPI_SyncedAt_Distinct(String userID) {

        String[] columns = {
                COLUMN_STEP_COUNTS_ID,
                COLUMN_STEP_USER_ID,
                COLUMN_STEPS_COUNT,
                COLUMN_WATER_INTAKE,
                COLUMN_STEPS_DISTANCE,
                COLUMN_STEPS_CALORIES,
                COLUMN_USER_TIME_ZONE,
                COLUMN_API_SYNCED_AT,
                COLUMN_IS_DAY_API_SYNCED,
                COLUMN_STEPS_LATITUDE,
                COLUMN_STEPS_LONGITUDE,
                COLUMN_STEPS_LOCATION,
                COLUMN_ACTIVITY_DATE,
                COLUMN_CREATED_AT,
                COLUMN_UPDATED_AT

        };
        List<StepCountModel.Data> activities = new ArrayList<>();
        String selection = COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_API_SYNCED_AT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {userID, ""};
        Cursor cursor = db.query(true, TABLE_STEP_COUNTS, //Table to query
                columns,            //columns to return
                selection,          //columns for the WHERE clause
                selectionArgs,      //The values for the WHERE clause
                null,               //group the rows
                null,               //filter by row groups
                COLUMN_ACTIVITY_DATE,  //sortBy
                null);              //limit

        //Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                StepCountModel.Data activity = new StepCountModel.Data();
                activity.setStepCountID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_COUNTS_ID)));
                activity.setUserID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_USER_ID)));
                activity.setStepsCount(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_COUNT)));
                activity.setWaterCount(cursor.getString(cursor.getColumnIndex(COLUMN_WATER_INTAKE)));
                activity.setDistance(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_DISTANCE)));
                activity.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_CALORIES)));
                activity.setUserTimeZone(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TIME_ZONE)));
                activity.setApi_synced_at(cursor.getString(cursor.getColumnIndex(COLUMN_API_SYNCED_AT)));
                activity.setIs_day_api_synced(cursor.getString(cursor.getColumnIndex(COLUMN_IS_DAY_API_SYNCED)));
                activity.setActivityLat(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LATITUDE)));
                activity.setActivityLong(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LONGITUDE)));
                activity.setActivityLocation(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LOCATION)));
                activity.setUserActivityDate(cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DATE)));
                activity.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                activity.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;

    }

    @SuppressLint("Range")
    public List<StepCountModel.Data> getUserActivityByUserID_ActivityDate(String userID, String todayDate) {
        String[] columns = {
                COLUMN_STEP_COUNTS_ID,
                COLUMN_STEP_USER_ID,
                COLUMN_STEPS_COUNT,
                COLUMN_WATER_INTAKE,
                COLUMN_STEPS_DISTANCE,
                COLUMN_STEPS_CALORIES,
                COLUMN_USER_TIME_ZONE,
                COLUMN_API_SYNCED_AT,
                COLUMN_IS_DAY_API_SYNCED,
                COLUMN_STEPS_LATITUDE,
                COLUMN_STEPS_LONGITUDE,
                COLUMN_STEPS_LOCATION,
                COLUMN_ACTIVITY_DATE,
                COLUMN_CREATED_AT,
                COLUMN_UPDATED_AT

        };
        String selection = COLUMN_STEP_USER_ID + " = ? AND " + COLUMN_ACTIVITY_DATE + " = ?";
        String[] selectionArgs = {userID, todayDate};
        String sortOrder =
                COLUMN_STEPS_COUNT + " ASC";
        List<StepCountModel.Data> activities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        Cursor cursor = db.query(TABLE_STEP_COUNTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        if (cursor.moveToFirst()) {
            do {
                StepCountModel.Data activity = new StepCountModel.Data();
                activity.setStepCountID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_COUNTS_ID)));
                activity.setUserID(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP_USER_ID)));
                activity.setStepsCount(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_COUNT)));
                activity.setWaterCount(cursor.getString(cursor.getColumnIndex(COLUMN_WATER_INTAKE)));
                activity.setDistance(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_DISTANCE)));
                activity.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_CALORIES)));
                activity.setUserTimeZone(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TIME_ZONE)));
                activity.setApi_synced_at(cursor.getString(cursor.getColumnIndex(COLUMN_API_SYNCED_AT)));
                activity.setIs_day_api_synced(cursor.getString(cursor.getColumnIndex(COLUMN_IS_DAY_API_SYNCED)));
                activity.setActivityLat(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LATITUDE)));
                activity.setActivityLong(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LONGITUDE)));
                activity.setActivityLocation(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_LOCATION)));
                activity.setUserActivityDate(cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DATE)));
                activity.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                activity.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;

    }

    public void addSingleCoachData(CoachesProfileDataModel.Data coach, int week, int day) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        ContentValues values = new ContentValues();

        values.put(COLUMN_COACH_ID, coach.getId());
        values.put(COLUMN_COACH_NAME, coach.getName());
        values.put(COLUMN_COACH_IMAGE, coach.getImageURL());
        values.put(COLUMN_COACH_DESCRIPTION, coach.getDescription());
        values.put(COLUMN_COACH_ROLE, coach.getRole());
        values.put(COLUMN_COACH_WORKOUT_COUNT, coach.getWorkoutCount());
        values.put(COLUMN_COACH_LIMIT, coach.getLimit());
        values.put(COLUMN_WEEK, String.valueOf(week));
        values.put(COLUMN_DAY, String.valueOf(day));
        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.LO);
        Date dt = new Date();*/
        values.put(COLUMN_CREATED_AT, "" + System.currentTimeMillis());

        db.insert(TABLE_COACHES, null, values);

        db.close();
    }

    /**
     * This method is to fetch all user and return the list of coach records
     *
     * @return list
     */
    @SuppressLint("Range")
    public List<CoachesProfileDataModel.Data> getAllCoaches(int week, int day) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_COACH_ID,
                COLUMN_COACH_NAME,
                COLUMN_COACH_IMAGE,
                COLUMN_COACH_DESCRIPTION,
                COLUMN_COACH_ROLE,
                COLUMN_COACH_WORKOUT_COUNT,
                COLUMN_COACH_LIMIT,
                COLUMN_WEEK,
                COLUMN_DAY

                /*  COLUMN_COACH_WARMUP,
                  COLUMN_COACH_WORKOUT,*/
        };
        // sorting orders
        String sortOrder =
                COLUMN_CREATED_AT + " ASC";
        List<CoachesProfileDataModel.Data> coachProfileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?";
        String[] selectionArgs = {String.valueOf(week), String.valueOf(day)};
        // query the user table

        Cursor cursor = db.query(TABLE_COACHES, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                CoachesProfileDataModel.Data coach = new CoachesProfileDataModel.Data();
                coach.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_COACH_ID)));
                coach.setName(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_NAME)));
                coach.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_IMAGE)));
                coach.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_DESCRIPTION)));
                coach.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_ROLE)));
                coach.setWorkoutCount(cursor.getInt(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_COUNT)));
                coach.setLimit(cursor.getInt(cursor.getColumnIndex(COLUMN_COACH_LIMIT)));

                // Adding user record to list
                coachProfileList.add(coach);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return coachProfileList;
    }


    public boolean checkCoachId(String coachId, int week, int day) {
        String[] columns = {
                COLUMN_COACH_ID,
                COLUMN_WEEK,
                COLUMN_DAY
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_COACH_ID + " = ? AND " + COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?";
        String[] selectionArgs = {coachId, String.valueOf(week), String.valueOf(day)};
        Cursor cursor = db.query(TABLE_COACHES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    //=====================================================METHODS FOR VISUALIZATION TABLE======================

    // add Visualization data to table
    public void addSleepVisualizationData(VisualizationModel visualizationResponse) {
        SQLiteDatabase db = this.getWritableDatabase();


        for (VisualizationModel.Data v : visualizationResponse.getData()) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_SLEEP_VISUALIZATION_DAY, v.getDay());
            values.put(COLUMN_SLEEP_VISUALIZATION_WEEK, v.getWeek());
            values.put(COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE, v.getPlaylistImage());
            values.put(COLUMN_SLEEP_VISUALIZATION_NAME, v.getName());
            values.put(COLUMN_SLEEP_VISUALIZATION_TIME, v.getTime());
            values.put(COLUMN_SLEEP_VISUALIZATION_IMAGE, v.getImageURL());
            values.put(COLUMN_SLEEP_VISUALIZATION_MUSIC, v.getAudio());
            values.put(COLUMN_SLEEP_VISUALIZATION_DESCRIPTION, v.getDescription());

            // Inserting Row
            db.insert(TABLE_SLEEP_VISUALIZATION, null, values);
        }
        db.close();
    }


    // get All visualization Data
    @SuppressLint("Range")
    public List<VisualizationModel.Data> getAllVisualization(String day, String week, String time) {
        // array of columns to fetch

        String selection = COLUMN_SLEEP_VISUALIZATION_DAY + " = ? AND " + COLUMN_SLEEP_VISUALIZATION_WEEK + " = ? AND " + COLUMN_SLEEP_VISUALIZATION_TIME + " = ?";
        String[] selectionArgs = {day, week, time};
        String[] columns = {

                COLUMN_SLEEP_VISUALIZATION_DAY,
                COLUMN_SLEEP_VISUALIZATION_WEEK,
                COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE,
                COLUMN_SLEEP_VISUALIZATION_NAME,
                COLUMN_SLEEP_VISUALIZATION_TIME,
                COLUMN_SLEEP_VISUALIZATION_IMAGE,
                COLUMN_SLEEP_VISUALIZATION_MUSIC,
                COLUMN_SLEEP_VISUALIZATION_DESCRIPTION,
        };
        // sorting orders
        String sortOrder =
                COLUMN_SLEEP_VISUALIZATION_NAME + " ASC";
        List<VisualizationModel.Data> visualizationResponses = new ArrayList<VisualizationModel.Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_SLEEP_VISUALIZATION, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                VisualizationModel.Data sleep = new VisualizationModel.Data();
                sleep.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_DAY)));
                sleep.setWeek(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_WEEK)));
                sleep.setPlaylistImage(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE)));
                sleep.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_NAME)));
                sleep.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_TIME)));
                sleep.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_IMAGE)));
                sleep.setAudio(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_MUSIC)));
                sleep.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_DESCRIPTION)));


                // Adding user record to list
                visualizationResponses.add(sleep);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return visualizationResponses;
    }

    @SuppressLint("Range")
    public List<VisualizationModel.Data> getAllVisualization(String day, String week) {
        // array of columns to fetch

        String selection = COLUMN_SLEEP_VISUALIZATION_DAY + " = ? AND " + COLUMN_SLEEP_VISUALIZATION_WEEK + " = ? ";
        String[] selectionArgs = {day, week};

        String[] columns = {
                COLUMN_SLEEP_VISUALIZATION_DAY,
                COLUMN_SLEEP_VISUALIZATION_WEEK,
                COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE,
                COLUMN_SLEEP_VISUALIZATION_NAME,
                COLUMN_SLEEP_VISUALIZATION_TIME,
                COLUMN_SLEEP_VISUALIZATION_IMAGE,
                COLUMN_SLEEP_VISUALIZATION_MUSIC,
                COLUMN_SLEEP_VISUALIZATION_DESCRIPTION,
        };
        // sorting orders
        String sortOrder =
                COLUMN_SLEEP_VISUALIZATION_NAME + " ASC";
        List<VisualizationModel.Data> visualizationResponses = new ArrayList<VisualizationModel.Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_SLEEP_VISUALIZATION, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                VisualizationModel.Data sleep = new VisualizationModel.Data();
                sleep.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_DAY)));
                sleep.setWeek(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_WEEK)));
                sleep.setPlaylistImage(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_PLAYLIST_IMAGE)));
                sleep.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_NAME)));
                sleep.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_TIME)));
                sleep.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_IMAGE)));
                sleep.setAudio(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_MUSIC)));
                sleep.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_VISUALIZATION_DESCRIPTION)));


                // Adding user record to list
                visualizationResponses.add(sleep);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return visualizationResponses;
    }


    public boolean checkVisualizationByDayWeekAndTitle(String day, String week, String title) {
        String[] columns = {
                COLUMN_SLEEP_VISUALIZATION_DAY,
                COLUMN_SLEEP_VISUALIZATION_WEEK,
                COLUMN_SLEEP_VISUALIZATION_TIME

        };

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_SLEEP_VISUALIZATION_TIME + " = ? AND " + COLUMN_SLEEP_VISUALIZATION_DAY + "= ? AND " + COLUMN_SLEEP_VISUALIZATION_WEEK + " = ?";
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SLEEP_VISUALIZATION_TIME + " FROM " + TABLE_SLEEP_VISUALIZATION +
                " WHERE " + COLUMN_SLEEP_VISUALIZATION_DAY + " = " + day + " AND "
                + COLUMN_SLEEP_VISUALIZATION_WEEK + " = " + week + "", null);
        /*Cursor cursor = db.query(TABLE_SHOPPING_LIST, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);  */                    //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


//=========================================================METHODS FOR RECIPES DASHBOARD TABLE===========================

    //add recipe to db
    public void addRecipe(NutritionDataModel recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (NutritionDataModel.Recipe r : recipe.getData().getRecipes()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_ID, r.getId());
            values.put(COLUMN_RECIPE_NAME, r.getName());
            values.put(COLUMN_RECIPE_TITLE, r.getTitle());
            values.put(COLUMN_RECIPE_IMAGE, r.getImageURL());
            values.put(COLUMN_RECIPE_DAY, r.getDay());
            values.put(COLUMN_RECIPE_DURATION, r.getDuration());
            values.put(COLUMN_RECIPE_COOK, r.getCook());
            values.put(COLUMN_RECIPE_METHOD, r.getMethods());

            db.insert(TABLE_RECIPES, null, values);
        }

        db.close();
    }

    public void addIndividualRecipe(SingleRecipeDataModel.Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipe.getId());
        values.put(COLUMN_RECIPE_NAME, recipe.getName());
        values.put(COLUMN_RECIPE_TITLE, recipe.getTitle());
        values.put(COLUMN_RECIPE_IMAGE, recipe.getImageURL());
        values.put(COLUMN_RECIPE_DURATION, recipe.getDuration());
        values.put(COLUMN_RECIPE_COOK, recipe.getCook());
        if (recipe.getMethods() != null)
            values.put(COLUMN_RECIPE_METHOD, recipe.getMethods());
        if (recipe.getMethodArray() != null)
            values.put(COLUMN_RECIPE_METHOD_JSON, new JSONArray(recipe.getMethodArray()).toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dt = new Date();
        values.put(COLUMN_CREATED_AT, dateFormat.format(dt));
        db.insert(TABLE_RECIPES, null, values);
        db.close();
    }

    public void updateIndividualRecipe(SingleRecipeDataModel.Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        /*values.put(COLUMN_RECIPE_ID, recipe.getId());*/
        values.put(COLUMN_RECIPE_NAME, recipe.getName());
        values.put(COLUMN_RECIPE_TITLE, recipe.getTitle());
        values.put(COLUMN_RECIPE_IMAGE, recipe.getImageURL());
        values.put(COLUMN_RECIPE_DURATION, recipe.getDuration());
        values.put(COLUMN_RECIPE_COOK, recipe.getCook());
        if (recipe.getMethods() != null)
            values.put(COLUMN_RECIPE_METHOD, recipe.getMethods());
        if (recipe.getMethodArray() != null)
            values.put(COLUMN_RECIPE_METHOD_JSON, new JSONArray(recipe.getMethodArray()).toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dt = new Date();
        values.put(COLUMN_UPDATED_AT, dateFormat.format(dt));
        db.update(TABLE_RECIPES, values, COLUMN_RECIPE_ID + " = ?", new String[]{String.valueOf(recipe.getId())});
        db.close();
    }


    @SuppressLint("Range")
    public SingleRecipeDataModel.Recipe getRecipeByID(String recipeID) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_DAY,
                COLUMN_RECIPE_DURATION,
                COLUMN_RECIPE_COOK,
                COLUMN_RECIPE_METHOD,
                COLUMN_RECIPE_METHOD_JSON
        };
        String selection = COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {recipeID};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPES, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order

        SingleRecipeDataModel.Recipe recipe = new SingleRecipeDataModel.Recipe();

        if (cursor.moveToFirst()) {
            recipe.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
            recipe.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
            recipe.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
            recipe.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
            recipe.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
            recipe.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
            recipe.setMethods(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHOD)));
            String jsonString = cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHOD_JSON));
            ArrayList<String> methodArray = new ArrayList<>();
            if (jsonString != null && !jsonString.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        methodArray.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    if (Common.isLoggingEnabled)
                        e.printStackTrace();
                }
            }
            recipe.setMethodArray(methodArray);
            // Adding user record to list
        }

        cursor.close();
        db.close();
        // return user list
        return recipe;
    }

    // get All Nutritions Data
    @SuppressLint({"Range", "Range"})
    public List<NutritionDataModel.Recipe> getAllRecipes() {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_DAY,
                COLUMN_RECIPE_DURATION,
                COLUMN_RECIPE_COOK,
                COLUMN_RECIPE_METHOD,


        };
        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_NAME + " ASC";
        List<NutritionDataModel.Recipe> recipesDataModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_RECIPES, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Recipe recipe = new NutritionDataModel.Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                recipe.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                recipe.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                recipe.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                recipe.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DAY)));
                recipe.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                recipe.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
                recipe.setMethods(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHOD)));


                // String methodString = cursor.getString(cursor.getColumnIndex(COLUMN_NUTRITION_METHOD));
                //  String ingredientsString = cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGRIDENTS));

                //methods

                /*type = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> methodList = gson.fromJson(methodString, type);

                //ingredients
                type = new TypeToken<ArrayList<NutritionDataModel.Ingredient>>() {
                }.getType();

                List<NutritionDataModel.Ingredient> ingredientList = gson.fromJson(ingredientsString, type);
                try {

                     nutrition.setMethod(methodList);
                    nutrition.setIngredients(ingredientList);
                    System.out.println(ingredientList + " is it null---------------------------------");
                } catch (Exception e) {
                    System.out.println(e);

                }*/


                // Adding user record to list
                recipesDataModelList.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return recipesDataModelList;
    }

    public void addPagerRecipes(DashboardNutritionPagerModel.Data.Recipes recipe, int week, int day) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (recipe.getPage1() != null && recipe.getPage1().size() > 0) {
            //add page 1 data
            int size = recipe.getPage1().size();
            if (size > 3) {
                size = 3;
            }
            ContentValues values1 = new ContentValues();
            for (int i = 0; i < size; i++) {
                if (!isRecipeAvailable(String.valueOf(recipe.getPage1().get(i).getId()), "page1", week, day)) {
                    values1.put(COLUMN_RECIPE_ID, recipe.getPage1().get(i).getId());
                    values1.put(COLUMN_RECIPE_PAGE, "page1");
                    values1.put(COLUMN_RECIPE_NAME, recipe.getPage1().get(i).getName());
                    values1.put(COLUMN_RECIPE_TITLE, recipe.getPage1().get(i).getTitle());
                    values1.put(COLUMN_RECIPE_IMAGE, recipe.getPage1().get(i).getImageURL());
                    values1.put(COLUMN_RECIPE_INDEX, recipe.getPage1().get(i).getIndex());
                    if (recipe.getPage1().get(i).getTotalCalories() != null && !recipe.getPage1().get(i).getTotalCalories().matches("")) {
                        values1.put(COLUMN_RECIPE_TOTAL_CALORIES, recipe.getPage1().get(i).getTotalCalories());
                    } else {
                        values1.put(COLUMN_RECIPE_TOTAL_CALORIES, "");
                    }
                    values1.put(COLUMN_RECIPE_ADDED_IN_SHOPPING_LIST, recipe.getPage1().get(i).isIs_added_in_shoppingList());
                    values1.put(COLUMN_WEEK, String.valueOf(week));
                    values1.put(COLUMN_DAY, String.valueOf(day));
                    db.insert(TABLE_DASHBOARD_RECIPES, null, values1);
                }
            }
        }
        //add page 2 data
        if (recipe.getPage2() != null && recipe.getPage2().size() > 0) {
            int size = recipe.getPage2().size();
            if (size > 3) {
                size = 3;
            }
            ContentValues values2 = new ContentValues();
            for (int i = 0; i < size; i++) {
                if (!isRecipeAvailable(String.valueOf(recipe.getPage1().get(i).getId()), "page2", week, day)) {
                    values2.put(COLUMN_RECIPE_ID, recipe.getPage2().get(i).getId());
                    values2.put(COLUMN_RECIPE_PAGE, "page2");
                    values2.put(COLUMN_RECIPE_NAME, recipe.getPage2().get(i).getName());
                    values2.put(COLUMN_RECIPE_TITLE, recipe.getPage2().get(i).getTitle());
                    values2.put(COLUMN_RECIPE_IMAGE, recipe.getPage2().get(i).getImageURL());
                    values2.put(COLUMN_RECIPE_INDEX, recipe.getPage2().get(i).getIndex());
                    if (recipe.getPage2().get(i).getTotalCalories() != null && !recipe.getPage2().get(i).getTotalCalories().matches("")) {
                        values2.put(COLUMN_RECIPE_TOTAL_CALORIES, recipe.getPage2().get(i).getTotalCalories());
                    } else {
                        values2.put(COLUMN_RECIPE_TOTAL_CALORIES, "");
                    }
                    values2.put(COLUMN_RECIPE_ADDED_IN_SHOPPING_LIST, recipe.getPage2().get(i).isIs_added_in_shoppingList());
                    values2.put(COLUMN_WEEK, String.valueOf(week));
                    values2.put(COLUMN_DAY, String.valueOf(day));
                    db.insert(TABLE_DASHBOARD_RECIPES, null, values2);
                }
            }
        }
        //add page 3 data
        if (recipe.getPage3() != null && recipe.getPage3().size() > 0) {
            int size = recipe.getPage3().size();
            if (size > 3) {
                size = 3;
            }
            ContentValues values3 = new ContentValues();
            for (int i = 0; i < size; i++) {
                if (!isRecipeAvailable(String.valueOf(recipe.getPage1().get(i).getId()), "page3", week, day)) {
                    values3.put(COLUMN_RECIPE_ID, recipe.getPage3().get(i).getId());
                    values3.put(COLUMN_RECIPE_PAGE, "page3");
                    values3.put(COLUMN_RECIPE_NAME, recipe.getPage3().get(i).getName());
                    values3.put(COLUMN_RECIPE_TITLE, recipe.getPage3().get(i).getTitle());
                    values3.put(COLUMN_RECIPE_IMAGE, recipe.getPage3().get(i).getImageURL());
                    values3.put(COLUMN_RECIPE_INDEX, recipe.getPage3().get(i).getIndex());
                    if (recipe.getPage3().get(i).getTotalCalories() != null && !recipe.getPage3().get(i).getTotalCalories().matches("")) {
                        values3.put(COLUMN_RECIPE_TOTAL_CALORIES, recipe.getPage3().get(i).getTotalCalories());
                    } else {
                        values3.put(COLUMN_RECIPE_TOTAL_CALORIES, "");
                    }
                    values3.put(COLUMN_RECIPE_ADDED_IN_SHOPPING_LIST, recipe.getPage3().get(i).isIs_added_in_shoppingList());
                    values3.put(COLUMN_WEEK, String.valueOf(week));
                    values3.put(COLUMN_DAY, String.valueOf(day));
                    db.insert(TABLE_DASHBOARD_RECIPES, null, values3);
                }
            }
        }


        //  db.insert(TABLE_DASHBOARD_RECIPES, null, values);
        db.close();
    }


    // get All Nutritions Data
    @SuppressLint({"Range", "Range"})
    public List<DashboardNutritionPagerModel.Page2> getPager2Data(String pagerNumber, int week, int day) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_INDEX,
                COLUMN_RECIPE_PAGE,
                COLUMN_RECIPE_TOTAL_CALORIES


        };
        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_INDEX + " ASC";
        List<DashboardNutritionPagerModel.Page2> page2List = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_DASHBOARD_RECIPES, //Table to query
                columns,    //columns to return
                COLUMN_RECIPE_PAGE + " = ? AND " + COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?",        //columns for the WHERE clause
                new String[]{pagerNumber, String.valueOf(week), String.valueOf(day)},
                null,       //group the rows
                null,       //filter by row groups
                COLUMN_RECIPE_INDEX);
        // Traversing through all rows and adding to list


        if (cursor.moveToFirst()) {
            do {

                DashboardNutritionPagerModel.Page2 page2 = new DashboardNutritionPagerModel.Page2();

                if (cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)).matches("page2")) {
                    page2.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                    page2.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                    page2.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                    page2.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                    page2.setPageNumber(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)));
                    page2.setIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_INDEX)));
                    page2.setTotalCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TOTAL_CALORIES)));
                    // page1List.add(page1);


                }
                page2List.add(page2);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return page2List;
    }

    @SuppressLint({"Range", "Range"})
    public List<DashboardNutritionPagerModel.Page1> getPager1Data(String pagerNumber, int week, int day) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_INDEX,
                COLUMN_RECIPE_PAGE,
                COLUMN_RECIPE_TOTAL_CALORIES


        };
        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_INDEX + " ASC";
        List<DashboardNutritionPagerModel.Page1> page1List = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_DASHBOARD_RECIPES, //Table to query
                columns,    //columns to return
                COLUMN_RECIPE_PAGE + " = ? AND " + COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?",        //columns for the WHERE clause
                new String[]{pagerNumber, String.valueOf(week), String.valueOf(day)},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                COLUMN_RECIPE_INDEX); //The sort order
        // Traversing through all rows and adding to list


        if (cursor.moveToFirst()) {
            do {

                DashboardNutritionPagerModel.Page1 page1 = new DashboardNutritionPagerModel.Page1();


                if (cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)).matches("page1")) {
                    page1.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                    page1.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                    page1.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                    page1.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                    page1.setTotalCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TOTAL_CALORIES)));
                    page1.setPageNumber(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)));
                    page1.setIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_INDEX)));

                    // page1List.add(page1);

                }
                page1List.add(page1);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return page1List;
    }

    @SuppressLint({"Range", "Range"})
    public List<DashboardNutritionPagerModel.Page3> getPager3Data(String pagerNumber, int week, int day) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_INDEX,
                COLUMN_RECIPE_PAGE,
                COLUMN_RECIPE_TOTAL_CALORIES


        };
        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_INDEX + " ASC";
        List<DashboardNutritionPagerModel.Page3> page3List = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_DASHBOARD_RECIPES, //Table to query
                columns,    //columns to return
                COLUMN_RECIPE_PAGE + " = ? AND " + COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?",        //columns for the WHERE clause
                new String[]{pagerNumber, String.valueOf(week), String.valueOf(day)},
                null,       //group the rows
                null,       //filter by row groups
                COLUMN_RECIPE_INDEX);//The sort order
        // Traversing through all rows and adding to list


        if (cursor.moveToFirst()) {
            do {

                DashboardNutritionPagerModel.Page3 page3 = new DashboardNutritionPagerModel.Page3();


                if (cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)).matches("page3")) {
                    page3.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                    page3.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                    page3.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                    page3.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                    page3.setPageNumber(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_PAGE)));
                    page3.setIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_INDEX)));
                    page3.setTotalCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TOTAL_CALORIES)));
                    // page1List.add(page1);


                }
                page3List.add(page3);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return page3List;
    }


    // String methodString = cursor.getString(cursor.getColumnIndex(COLUMN_NUTRITION_METHOD));
    //  String ingredientsString = cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGRIDENTS));

    //methods

               /*type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> methodList = gson.fromJson(methodString, type);

            //ingredients
            type = new TypeToken<ArrayList<NutritionDataModel.Ingredient>>() {
            }.getType();

            List<NutritionDataModel.Ingredient> ingredientList = gson.fromJson(ingredientsString, type);
            try {

                nutrition.setMethod(methodList);
                nutrition.setIngredients(ingredientList);
                System.out.println(ingredientList + " is it null---------------------------------");
            } catch (Exception e) {
                System.out.println(e);

            }*/


           /* // Adding user record to list
            recipesDataModelList.add(recipe);
        }
        while (cursor.moveToNext()) ;
        cursor.close();
        db.close();
        // return user list
        return recipesDataModelList;*/
    /*}*/


    //check recipe id in db
    public boolean isRecipeAvailable(String recipe_id, String pageNumber, int week, int day) {
        String[] columns = {
                COLUMN_RECIPE_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_RECIPE_PAGE + " = ? AND " + COLUMN_WEEK + " = ? AND " + COLUMN_DAY + " = ?";
        String[] selectionArgs = {recipe_id, pageNumber, String.valueOf(week), String.valueOf(day)};
        Cursor cursor = db.query(TABLE_DASHBOARD_RECIPES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        //cursor.close();
        //db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    //check recipe id in db
    public boolean isRecipeAvailable(String recipe_id) {
        String[] columns = {COLUMN_RECIPE_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {recipe_id};
        Cursor cursor = db.query(TABLE_RECIPES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    //=================================================================USER SHOPPING LIST METHODS==================================

    //add Shopping profile data to table
    public void addUserShoppingList(NutritionDataModel shopping) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        for (NutritionDataModel.Recipe s : shopping.getData().getRecipes()) {
            ContentValues values = new ContentValues();
            // convert warmup list to json

       /* List<NutritionDataModel.Recipe> recipesList = shopping.getData().getRecipes();
        gson = new Gson();
        String inputRecipes = gson.toJson(recipesList);*/

            // System.out.println(inputWarmup + " warmup list==========================lll=======================");
            // System.out.println(shopping.re + " warmup ==========================jjjj===================================");

            // convert workout list to json
     /*   List<NutritionDataModel.Category> categoriesList = shopping.data.getCategories();
        String inputCategories = gson.toJson(categoriesList);*/


            values.put(COLUMN_RECIPE_ID, s.getId());
            values.put(COLUMN_RECIPE_NAME, s.getName());
            values.put(COLUMN_RECIPE_TITLE, s.getTitle());
            values.put(COLUMN_RECIPE_IMAGE, s.getImageURL());
            values.put(COLUMN_RECIPE_DURATION, s.getDuration());
            values.put(COLUMN_RECIPE_COOK, s.getCook());
            values.put(COLUMN_RECIPE_METHOD, s.getMethods());
            values.put(COLUMN_RECIPE_SERVING, s.getServing());
            //Ingredients List


            db.insert(TABLE_USER_SHOPPING_LIST, null, values);
        }
        db.close();
    }

    //add ingredient in table for check and uncheck
    public void addCheckUncheckIngredient(int ingredient_id, int recipe_id, int serving, String isChecked,
                                          String server_isChecked
            , boolean is_api_synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        values.put(COLUMN_RECIPE_ID, recipe_id);
        values.put(COLUMN_RECIPE_SERVING, serving);
        values.put(COLUMN_INGREDIENT_IS_CHECKED, isChecked);
        values.put(COLUMN_SERVER_CHECKED, server_isChecked);
        values.put(COLUMN_INGREDIENT_IS_API_SYNCED, is_api_synced);
        db.insert(TABLE_CHECK_UNCHECK, null, values);

        db.close();


    }

    public boolean updateCheckUncheck(int recipeID, int ingredient_id, int serving, String isChecked, String server_isChecked,
                                      boolean is_api_synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_RECIPE_SERVING, serving);
        args.put(COLUMN_INGREDIENT_IS_CHECKED, isChecked);
        args.put(COLUMN_SERVER_CHECKED, server_isChecked);
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, is_api_synced);
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_ID + " = ?", new String[]{String.valueOf(ingredient_id), String.valueOf(recipeID)});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean updateCheckUncheckInIngredientTable(int recipeID, String ingredient_id, String isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_RECIPE_INGREDIENT_STATUS, isChecked);
        int rowCount = db.update(TABLE_SHOPPING_INGREDIENTS, args, COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_ID + " = ?", new String[]{ingredient_id, String.valueOf(recipeID)});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean updateAllCheckUncheckInIngredientTable(String ingredient_id, String isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_RECIPE_INGREDIENT_STATUS, isChecked);
        int rowCount = db.update(TABLE_SHOPPING_INGREDIENTS, args, COLUMN_INGREDIENTS_ID + " = ?", new String[]{ingredient_id});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean updateCheckUncheckOnly(int recipeID, String ingredient_id, String isChecked, String server_isChecked,
                                          boolean is_api_synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_INGREDIENT_IS_CHECKED, isChecked);
        args.put(COLUMN_SERVER_CHECKED, server_isChecked);
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, is_api_synced);
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_ID + " = ?", new String[]{ingredient_id, String.valueOf(recipeID)});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean updateAllCheckUncheckOnly(String ingredient_id, String isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_INGREDIENT_IS_CHECKED, isChecked);
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, "0");
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_INGREDIENTS_ID + " = ?", new String[]{ingredient_id});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean updateServingCheckUnCheck(int recipeID, int serving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_RECIPE_ID, String.valueOf(recipeID));
        args.put(COLUMN_RECIPE_SERVING, serving);
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, false);
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeID)});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }


    public boolean updateServing(int recipeID, int serving) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*ContentValues args = new ContentValues();
        args.put(COLUMN_RECIPE_ID, String.valueOf(recipeID));
        args.put(COLUMN_RECIPE_SERVING, serving);
        args.put(COLUMN_RECIPE_QUANTITY_TOTAL, serving*);*/
        String QUERY = "UPDATE " + TABLE_SHOPPING_INGREDIENTS + " SET " + COLUMN_RECIPE_SERVING + " = ? ," + COLUMN_RECIPE_QUANTITY_TOTAL + " = " + COLUMN_INGREDIENTS_QUANTITY + " * " + String.valueOf(serving) + " WHERE " + COLUMN_RECIPE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(serving), String.valueOf(recipeID)};
        Cursor cursor = db.rawQuery(QUERY, selectionArgs);
        int count = cursor.getCount();
        cursor.close();
        //int rowCount = db.update(TABLE_SHOPPING_INGREDIENTS, args, COLUMN_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeID)});
        if (db.isOpen()) {
            db.close();
        }
        return count > 0;
    }


    public boolean updateIngredientCheckStatus(int ingredient_id, String isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENTS_ID, ingredient_id);
        args.put(COLUMN_INGREDIENT_IS_CHECKED, isChecked);
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, false);
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_INGREDIENTS_ID + " = ?", new String[]{String.valueOf(ingredient_id)});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    public boolean uncheckAllCheckedIngredients() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_INGREDIENT_IS_CHECKED, "un-checked");
        args.put(COLUMN_INGREDIENT_IS_API_SYNCED, "0");
        int rowCount = db.update(TABLE_CHECK_UNCHECK, args, COLUMN_INGREDIENT_IS_CHECKED + " = ?", new String[]{"checked"});
        if (db.isOpen()) {
            db.close();
        }
        return rowCount > 0;
    }

    @SuppressLint("Range")
    public boolean isIngredientChecked(int ingredient_id) {
        String[] columns = {
                COLUMN_INGREDIENT_IS_CHECKED
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_INGREDIENTS_ID + " = ?";
        /*Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_CHECK_UNCHECK +
                " WHERE " + COLUMN_INGREDIENTS_ID + " = " + ingredient_id + " AND " + COLUMN_RECIPE_ID + " = " + recipeID + "", null);*/
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                new String[]{String.valueOf(ingredient_id)},              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                     //The sort order
        int cursorCount = 0;
        int totalRecords = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                String checkedStatus = "";
                checkedStatus = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED));
                if (checkedStatus.matches("checked")) {
                    cursorCount++;
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (totalRecords == cursorCount) {
            return true;
        } else if (totalRecords > cursorCount) {
            return false;
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public boolean isIngredientCheckedByIngredientAndRecipeID(int recipeID, int ingredient_id) {
        String[] columns = {
                COLUMN_INGREDIENT_IS_CHECKED
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_INGREDIENTS_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENTS_ID + " = ?";
        /*Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_CHECK_UNCHECK +
                " WHERE " + COLUMN_INGREDIENTS_ID + " = " + ingredient_id + " AND " + COLUMN_RECIPE_ID + " = " + recipeID + "", null);*/
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                new String[]{String.valueOf(recipeID), String.valueOf(ingredient_id)},              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                     //The sort order

        String checkedStatus = "";
        if (cursor.moveToFirst()) {
            checkedStatus = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED));
        }
        cursor.close();
        db.close();
        if (checkedStatus.matches("checked")) {
            return true;
        } else {
            return false;
        }
    }

    //get data on recipe_id and ingredient_id
    @SuppressLint("Range")
    public CheckUncheckDbModel getServerCheckedUncheckedItems(int ingredient_id, int recipe_id) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENTS_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_INGREDIENT_IS_CHECKED,
                COLUMN_SERVER_CHECKED,
                COLUMN_INGREDIENT_IS_API_SYNCED
        };
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENT_IS_CHECKED + " ASC";
        CheckUncheckDbModel checkUncheckDbModel = new CheckUncheckDbModel();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENTS_ID + " = ?";


        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,    //columns to return
                selection,
                new String[]{String.valueOf(recipe_id), String.valueOf(ingredient_id)},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            /*  do {*/
            // CheckUncheckDbModel checkUncheckDbModel = new CheckUncheckDbModel();
            //checkUncheckDbModel.setRecipe_id(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
            // checkUncheckDbModel.setIngredient_id(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
            checkUncheckDbModel.setChecked_state(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED)));
            checkUncheckDbModel.setServer_checked_state(cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_CHECKED)));
            // checkUncheckDbModel.setIs_api_synced(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_API_SYNCED))));
            // Adding user record to list
            // checkUncheckDbModelList.add(checkUncheckDbModel);
            /*       } while (cursor.moveToNext());*/
        }
        cursor.close();
        db.close();
        // return user list
        return checkUncheckDbModel;
    }

    @SuppressLint("Range")
    public boolean getServerCheckedUncheckedItemsIDs(int ingredient_id, int recipe_id) {
        // array of columns to fetch

        String[] columns = {

                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENTS_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_INGREDIENT_IS_CHECKED,
                COLUMN_SERVER_CHECKED,
                COLUMN_INGREDIENT_IS_API_SYNCED,


        };
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENT_IS_CHECKED + " ASC";
        CheckUncheckDbModel checkUncheckDbModel = new CheckUncheckDbModel();
        String serverChecked = null;
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,    //columns to return
                COLUMN_RECIPE_ID + " = ? AND " +
                        COLUMN_INGREDIENTS_ID + " = ?",
                new String[]{String.valueOf(recipe_id), String.valueOf(ingredient_id)},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            /*  do {*/
            // CheckUncheckDbModel checkUncheckDbModel = new CheckUncheckDbModel();
            //checkUncheckDbModel.setRecipe_id(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
            // checkUncheckDbModel.setIngredient_id(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
            checkUncheckDbModel.setChecked_state(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED)));
            checkUncheckDbModel.setServer_checked_state(cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_CHECKED)));
            // checkUncheckDbModel.setIs_api_synced(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_API_SYNCED))));
            // Adding user record to list
            // checkUncheckDbModelList.add(checkUncheckDbModel);
            /*       } while (cursor.moveToNext());*/
        }
        cursor.close();
        db.close();
        // return user list

        return true;
    }


    @SuppressLint("Range")
    public List<CheckUncheckDbModel> getAllCheckedUncheckedItems() {
        // array of columns to fetch

        String[] columns = {

                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENTS_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_INGREDIENT_IS_CHECKED,
                COLUMN_SERVER_CHECKED,
                COLUMN_INGREDIENT_IS_API_SYNCED,


        };
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENT_IS_CHECKED + " ASC";
        List<CheckUncheckDbModel> checkUncheckDbModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,    //columns to return
                COLUMN_INGREDIENT_IS_API_SYNCED + " = 0 ",
                null,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CheckUncheckDbModel checkUncheckDbModel = new CheckUncheckDbModel();
                checkUncheckDbModel.setRecipe_id(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                checkUncheckDbModel.setIngredient_id(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                checkUncheckDbModel.setServing(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                checkUncheckDbModel.setChecked_state(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED)));
                checkUncheckDbModel.setServer_checked_state(cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_CHECKED)));
                checkUncheckDbModel.setIs_api_synced(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_API_SYNCED))));
                // Adding user record to list
                checkUncheckDbModelList.add(checkUncheckDbModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return checkUncheckDbModelList;
    }

    @SuppressLint("Range")
    public List<CheckUncheckDbModel> getAllCheckedIngredientByNutritionID(int nutritionID) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENTS_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_INGREDIENT_IS_CHECKED,
                COLUMN_SERVER_CHECKED,
                COLUMN_INGREDIENT_IS_API_SYNCED,
        };

        List<CheckUncheckDbModel> checkedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENT_IS_CHECKED + " = ?";

        String[] selectionArgs = {String.valueOf(nutritionID), "checked"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CheckUncheckDbModel checkedModel = new CheckUncheckDbModel();
                checkedModel.setRecipe_id(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                checkedModel.setIngredient_id(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                checkedModel.setServing(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                checkedModel.setChecked_state(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED)));
                checkedModel.setServer_checked_state(cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_CHECKED)));
                checkedModel.setIs_api_synced(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_API_SYNCED))));
                // Adding user record to list
                checkedList.add(checkedModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return checkedList;
    }

    @SuppressLint("Range")
    public int areCheckedAvailableForUploading(int nutritionID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENT_IS_CHECKED + " = ? AND " +
                COLUMN_INGREDIENT_IS_API_SYNCED + " = ?";

        String[] selectionArgs = {String.valueOf(nutritionID), "checked", "0"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                null,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        int checkedCount = cursor.getCount();
        cursor.close();
        db.close();
        // return user list
        return checkedCount;
    }


    @SuppressLint("Range")
    public List<CheckUncheckDbModel> getAllUnCheckedIngredientByNutritionID(int nutritionID) {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENTS_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_INGREDIENT_IS_CHECKED,
                COLUMN_SERVER_CHECKED,
                COLUMN_INGREDIENT_IS_API_SYNCED,
        };

        List<CheckUncheckDbModel> checkedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENT_IS_CHECKED + " = ?";

        String[] selectionArgs = {String.valueOf(nutritionID), "un-checked"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CheckUncheckDbModel checkedModel = new CheckUncheckDbModel();
                checkedModel.setRecipe_id(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                checkedModel.setIngredient_id(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                checkedModel.setServing(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                checkedModel.setChecked_state(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_CHECKED)));
                checkedModel.setServer_checked_state(cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_CHECKED)));
                checkedModel.setIs_api_synced(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENT_IS_API_SYNCED))));
                // Adding user record to list
                checkedList.add(checkedModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return checkedList;
    }

    @SuppressLint("Range")
    public int areUnCheckedAvailableForUploading(int nutritionID) {


        List<CheckUncheckDbModel> checkedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENT_IS_CHECKED + " = ? AND " +
                COLUMN_INGREDIENT_IS_API_SYNCED + " = ?";

        String[] selectionArgs = {String.valueOf(nutritionID), "un-checked", "0"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                null,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        int uncheckedCount = cursor.getCount();
        cursor.close();
        db.close();
        // return user list
        return uncheckedCount;
    }


    @SuppressLint("Range")
    public int areCheckedUnCheckedAvailableForUploading() {


        List<CheckUncheckDbModel> checkedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENT_IS_API_SYNCED + " = ?";

        String[] selectionArgs = {"0"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                null,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        int uncheckedCount = cursor.getCount();
        cursor.close();
        db.close();
        // return user list
        return uncheckedCount;
    }

    @SuppressLint("Range")
    public int areCheckedIngredientAvailableInDB() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENT_IS_CHECKED + " = ?";

        String[] selectionArgs = {"checked"};

        // query the user table
        Cursor cursor = db.query(TABLE_CHECK_UNCHECK, //Table to query
                null,    //columns to return
                selection,
                selectionArgs,
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order
        // Traversing through all rows and adding to list
        int uncheckedCount = cursor.getCount();
        cursor.close();
        db.close();
        // return user list
        return uncheckedCount;
    }


    public boolean checkShoppingRecipeId(String recipe_id) {
        String[] columns = {
                COLUMN_RECIPE_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {recipe_id};
        Cursor cursor = db.query(TABLE_USER_SHOPPING_LIST, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public void logout() {
        clearStepCounter();
        clearShoppingListItems();
        clearRecipes();
        clearShoppingRecipesIngredients();
        clearRecipesIngredients();
        clearShoppingRecipesCategories();
        clearCoaches();
        clearVisualizations();
        clearDashboardNutrition();
        clearUsers();
        deleteAllCoachesExercisesData();
        clearAllShoppingList();
        clearCheckUncheck();
        clearProgramExercises();
        deleteAllProgramsData();
        deleteAllProgramDetailData();
        clearPiviotData();
    }

    public void clearDB() {
        clearShoppingListItems();
        clearRecipes();
        clearShoppingRecipesIngredients();
        clearRecipesIngredients();
        clearShoppingRecipesCategories();
        clearCoaches();
        clearVisualizations();
        clearDashboardNutrition();
        deleteAllCoachesExercisesData();
        clearAllShoppingList();
        clearCheckUncheck();
        clearProgramExercises();
        deleteAllProgramsData();
        deleteAllProgramDetailData();
        clearPiviotData();
    }

    public void clearStepCounter() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STEP_COUNTS, null, null);
        db.close();
    }

    public void clearProgramExercises() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROGRAM_EXERCISES, null, null);
        db.close();
    }

    public void clearShoppingListItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_SHOPPING_LIST, null, null);
        db.close();
    }

    public void clearRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPES, null, null);
        db.close();
    }

    public void clearCheckUncheck() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHECK_UNCHECK, null, null);
        db.close();
    }

    public void clearRecipesIngredients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INGREDIENTS, null, null);
        db.close();
    }

    public int clearRecipesIngredientsByRecipeID(String recipeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_INGREDIENTS_RECIPE_ID + " = ?";
        int affectedRows = 0;
        affectedRows = db.delete(TABLE_INGREDIENTS, whereClause, new String[]{recipeID});
        db.close();
        return affectedRows;
    }

    public void clearShoppingRecipesIngredients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING_INGREDIENTS, null, null);
        db.close();
    }

    public void clearShoppingRecipesCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INGREDIENT_CATEGORIES, null, null);
        db.close();
    }

    public void clearCoaches() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COACHES, null, null);
        db.close();
    }

    public void clearVisualizations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SLEEP_VISUALIZATION, null, null);
        db.close();
    }

    public void clearDashboardNutrition() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DASHBOARD_RECIPES, null, null);
        db.close();
    }

    public void clearUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
    }

    public void clearPiviotData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PIVOT_RECIPE, null, null);
        db.close();
    }

    public void addOrUpdateCategory(ArrayList<NutritionDataModel.Category> categories) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < categories.size(); i++) {
            if (!checkCategoryByID("" + categories.get(i).getId())) {
                if (!db.isOpen())
                    db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_RECIPE_CATEGORY_ID, categories.get(i).getId());
                values.put(COLUMN_RECIPE_CATEGORY_NAME, categories.get(i).getName());
                db.insert(TABLE_INGREDIENT_CATEGORIES, null, values);
            } else {
                if (!db.isOpen())
                    db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_RECIPE_CATEGORY_ID, categories.get(i).getId());
                values.put(COLUMN_RECIPE_CATEGORY_NAME, categories.get(i).getName());
                db.update(TABLE_INGREDIENT_CATEGORIES, values, COLUMN_RECIPE_CATEGORY_ID + " = ?",
                        new String[]{String.valueOf(categories.get(i).getId())});
            }
        }
        if (db.isOpen())
            db.close();
    }

    public void addOrUpdateIngredient(List<NutritionDataModel.Ingredient> ingredient) {
        try {
            for (int i = 0; i < ingredient.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_INGREDIENTS_ID, ingredient.get(i).getId());
                values.put(COLUMN_INGREDIENTS_CATEGORY, ingredient.get(i).getCategory());
                values.put(COLUMN_INGREDIENTS_INGREDIENT, ingredient.get(i).getIngredient());
                values.put(COLUMN_INGREDIENTS_UNIT, ingredient.get(i).getUnit());
                values.put(COLUMN_INGREDIENTS_QUANTITY, ingredient.get(i).getQuantity());
                values.put(COLUMN_INGREDIENTS_FATS, ingredient.get(i).getFats());
                values.put(COLUMN_INGREDIENTS_CALORIES, ingredient.get(i).getCalories());
                values.put(COLUMN_INGREDIENTS_PROTEIN, ingredient.get(i).getProtein());
                values.put(COLUMN_INGREDIENTS_CARBS, ingredient.get(i).getCarbs());
                values.put(COLUMN_RECIPE_INGREDIENT_STATUS, ingredient.get(i).getStatus());
                values.put(COLUMN_INGREDIENTS_RECIPE_ID, ingredient.get(i).getRecipeID());
                values.put(COLUMN_RECIPE_SERVING, ingredient.get(i).getServing());
                //db.insert(TABLE_INGREDIENTS, null, values);
                SQLiteDatabase db = null;
                if (!isIngredientAvailableInDB(String.valueOf(ingredient.get(i).getId()), String.valueOf(ingredient.get(i).getRecipeID()))) {
                    if (db == null) {
                        db = this.getWritableDatabase();
                    } else if (!db.isOpen()) {
                        db = this.getWritableDatabase();
                    }
                    db.insert(TABLE_INGREDIENTS, null, values);
                    if (db.isOpen())
                        db.close();
                } else {
                    if (db == null) {
                        db = this.getWritableDatabase();
                    } else if (!db.isOpen()) {
                        db = this.getWritableDatabase();
                    }
                    db.update(TABLE_INGREDIENTS, values, COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_INGREDIENTS_RECIPE_ID + " = ?", new String[]{String.valueOf(ingredient.get(i).getId()), String.valueOf(ingredient.get(i).getRecipeID())});
                    if (db.isOpen())
                        db.close();
                }

            }

        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }

    }

    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getIngredientsByCategory(String categoryName) {
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_RECIPE_QUANTITY_TOTAL
        };

        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENTS_CATEGORY + " = ?";

        String[] selectionArgs = {categoryName};

        // query the user table
        /*Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                COLUMN_INGREDIENTS_INGREDIENT,       //group the rows
                null,       //filter by row groups
                null); //The sort order*/
        String QUERY = "SELECT " + COLUMN_INGREDIENTS_ID + ", " + COLUMN_INGREDIENTS_CATEGORY + ", " + COLUMN_INGREDIENTS_INGREDIENT +
                ", " + COLUMN_INGREDIENTS_UNIT + ", " + COLUMN_INGREDIENTS_QUANTITY + ", " + COLUMN_INGREDIENTS_FATS + ", "
                + COLUMN_INGREDIENTS_CALORIES + ", " + COLUMN_INGREDIENTS_PROTEIN + ", " + COLUMN_INGREDIENTS_CARBS + ", "
                + COLUMN_RECIPE_INGREDIENT_STATUS + ", " + COLUMN_INGREDIENTS_RECIPE_ID + ", " + COLUMN_RECIPE_SERVING +
                ", sum(" + COLUMN_RECIPE_QUANTITY_TOTAL + ") " +
                "FROM " + TABLE_SHOPPING_INGREDIENTS + " WHERE " + COLUMN_INGREDIENTS_CATEGORY + " = ?" + "  GROUP BY " + COLUMN_INGREDIENTS_INGREDIENT;
        Cursor cursor = db.rawQuery(QUERY, selectionArgs);
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                ingredient.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                ingredient.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY)));
                ingredient.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                ingredient.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                ingredient.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                ingredient.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                ingredient.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                ingredient.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                ingredient.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                ingredient.setTotal(cursor.getInt(12));

                // Adding user record to list
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredientList;
    }

    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getIngredientsByCategoryWithoutMerging(String categoryName) {
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_RECIPE_QUANTITY_TOTAL
        };

        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String[] selectionArgs = {categoryName};

        // query the user table
        /*Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                COLUMN_INGREDIENTS_INGREDIENT,       //group the rows
                null,       //filter by row groups
                null); //The sort order*/
        String QUERY = "SELECT " + COLUMN_INGREDIENTS_ID + ", " + COLUMN_INGREDIENTS_CATEGORY + ", " + COLUMN_INGREDIENTS_INGREDIENT +
                ", " + COLUMN_INGREDIENTS_UNIT + ", " + COLUMN_INGREDIENTS_QUANTITY + ", " + COLUMN_INGREDIENTS_FATS + ", "
                + COLUMN_INGREDIENTS_CALORIES + ", " + COLUMN_INGREDIENTS_PROTEIN + ", " + COLUMN_INGREDIENTS_CARBS + ", "
                + COLUMN_RECIPE_INGREDIENT_STATUS + ", " + COLUMN_INGREDIENTS_RECIPE_ID + ", " + COLUMN_RECIPE_SERVING + ", " + COLUMN_RECIPE_QUANTITY_TOTAL +
                " FROM " + TABLE_SHOPPING_INGREDIENTS + " WHERE " + COLUMN_INGREDIENTS_CATEGORY + " = ?";
        Cursor cursor = db.rawQuery(QUERY, selectionArgs);
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                ingredient.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                ingredient.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY)));
                ingredient.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                ingredient.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                ingredient.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                ingredient.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                ingredient.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                ingredient.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                ingredient.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                ingredient.setTotal(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_QUANTITY_TOTAL)));

                // Adding user record to list
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredientList;
    }

    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getUncheckedIngredientsByIngredientID(int ingredientID) {
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_RECIPE_QUANTITY_TOTAL
        };

        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_INGREDIENT_STATUS + " = ?";

        String[] selectionArgs = {String.valueOf(ingredientID), "un-checked"};

        // query the user table
        /*Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                COLUMN_INGREDIENTS_INGREDIENT,       //group the rows
                null,       //filter by row groups
                null); //The sort order*/
        String QUERY = "SELECT " + COLUMN_INGREDIENTS_ID + ", " + COLUMN_INGREDIENTS_CATEGORY + ", " + COLUMN_INGREDIENTS_INGREDIENT +
                ", " + COLUMN_INGREDIENTS_UNIT + ", " + COLUMN_INGREDIENTS_QUANTITY + ", " + COLUMN_INGREDIENTS_FATS + ", "
                + COLUMN_INGREDIENTS_CALORIES + ", " + COLUMN_INGREDIENTS_PROTEIN + ", " + COLUMN_INGREDIENTS_CARBS + ", "
                + COLUMN_RECIPE_INGREDIENT_STATUS + ", " + COLUMN_INGREDIENTS_RECIPE_ID + ", " + COLUMN_RECIPE_SERVING +
                ", sum(" + COLUMN_RECIPE_QUANTITY_TOTAL + ") " +
                "FROM " + TABLE_SHOPPING_INGREDIENTS + " WHERE " + COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_INGREDIENT_STATUS + " = ? GROUP BY " + COLUMN_INGREDIENTS_ID;
        Cursor cursor = db.rawQuery(QUERY, selectionArgs);
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                ingredient.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                ingredient.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY)));
                ingredient.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                ingredient.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                ingredient.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                ingredient.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                ingredient.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                ingredient.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                ingredient.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                ingredient.setTotal(cursor.getInt(12));

                // Adding user record to list
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredientList;
    }

    @SuppressLint("Range")
    public int getAllCheckedIngredientsCount(int ingredientID) {
        String[] columns = {
                COLUMN_RECIPE_INGREDIENT_STATUS
        };

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_RECIPE_INGREDIENT_STATUS + " = ?";

        String[] selectionArgs = {String.valueOf(ingredientID), "checked"};

        // query the user table
        Cursor cursor = db.query(TABLE_SHOPPING_INGREDIENTS, columns, selection, selectionArgs, null, null, null);
        // Traversing through all rows and adding to list
        int totalIngredients = cursor.getCount();
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return totalIngredients;
    }

    @SuppressLint("Range")
    public int getTotalNumberOfIngredients(int ingredientID) {
        String[] columns = {
                COLUMN_RECIPE_INGREDIENT_STATUS
        };

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENTS_ID + " = ?";

        String[] selectionArgs = {String.valueOf(ingredientID)};

        // query the user table
        Cursor cursor = db.query(TABLE_SHOPPING_INGREDIENTS, columns, selection, selectionArgs, null, null, null);
        // Traversing through all rows and adding to list
        int totalIngredients = cursor.getCount();
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return totalIngredients;
    }

    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getIngredientsByCategoryAndRecipe(String categoryName, int recipeID) {
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING,
                COLUMN_RECIPE_QUANTITY_TOTAL
        };

        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_INGREDIENTS_CATEGORY + " = ?";

        String[] selectionArgs = {categoryName, String.valueOf(recipeID)};

        // query the user table
        /*Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,
                selectionArgs,
                COLUMN_INGREDIENTS_INGREDIENT,       //group the rows
                null,       //filter by row groups
                null); //The sort order*/
        String QUERY = "SELECT " + COLUMN_INGREDIENTS_ID + ", " + COLUMN_INGREDIENTS_CATEGORY + ", " + COLUMN_INGREDIENTS_INGREDIENT +
                ", " + COLUMN_INGREDIENTS_UNIT + ", " + COLUMN_INGREDIENTS_QUANTITY + ", " + COLUMN_INGREDIENTS_FATS + ", "
                + COLUMN_INGREDIENTS_CALORIES + ", " + COLUMN_INGREDIENTS_PROTEIN + ", " + COLUMN_INGREDIENTS_CARBS + ", "
                + COLUMN_RECIPE_INGREDIENT_STATUS + ", " + COLUMN_INGREDIENTS_RECIPE_ID + ", " + COLUMN_RECIPE_SERVING +
                ", sum(" + COLUMN_RECIPE_QUANTITY_TOTAL + ") " +
                "FROM " + TABLE_SHOPPING_INGREDIENTS + " WHERE " + COLUMN_INGREDIENTS_CATEGORY + " = ? AND " + COLUMN_INGREDIENTS_RECIPE_ID + " = ? GROUP BY " + COLUMN_INGREDIENTS_INGREDIENT;
        Cursor cursor = db.rawQuery(QUERY, selectionArgs);
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                ingredient.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                ingredient.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY)));
                ingredient.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                ingredient.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                ingredient.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                ingredient.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                ingredient.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                ingredient.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                ingredient.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                ingredient.setTotal(cursor.getInt(12));

                // Adding user record to list
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredientList;
    }

    public boolean isIngredientAvailableInDB(String ingredientID, String recipeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_INGREDIENTS +
                " WHERE " + COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_INGREDIENTS_RECIPE_ID + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{ingredientID, recipeID});
        int cursorCount = cursor.getCount();
        cursor.close();
        if (db.isOpen())
            db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public void addOrUpdateShoppingIngredient(List<NutritionDataModel.Ingredient> ingredient) {
        try {
            for (int i = 0; i < ingredient.size(); i++) {
                SQLiteDatabase db = null;


                String category;
                if (ingredient.get(i).getCategory() == null) {
                    category = "Other";
                } else {
                    category = ingredient.get(i).getCategory();
                }
                String query = "INSERT OR REPLACE INTO " + TABLE_SHOPPING_INGREDIENTS + " (" + COLUMN_INGREDIENTS_TABLE_ID + ", " + COLUMN_INGREDIENTS_ID + ", " +
                        COLUMN_INGREDIENTS_CATEGORY + ", " + COLUMN_INGREDIENTS_INGREDIENT + ", " + COLUMN_INGREDIENTS_UNIT + ", " + COLUMN_INGREDIENTS_QUANTITY + ", " + COLUMN_INGREDIENTS_FATS
                        + ", " + COLUMN_INGREDIENTS_CALORIES + ", " + COLUMN_INGREDIENTS_PROTEIN + ", " + COLUMN_INGREDIENTS_CARBS + ", " + COLUMN_INGREDIENTS_RECIPE_ID +
                        ", " + COLUMN_RECIPE_SERVING + ", " + COLUMN_RECIPE_QUANTITY_TOTAL + ", " + COLUMN_RECIPE_INGREDIENT_STATUS + ") VALUES ('" + String.valueOf(ingredient.get(i).getRecipeID()).concat(String.valueOf(ingredient.get(i).getId())) + "', " + ingredient.get(i).getId() + ", '" + category + "', '" +
                        ingredient.get(i).getIngredient() + "', '" + ingredient.get(i).getUnit() + "', '" + ingredient.get(i).getQuantity() + "', '" + ingredient.get(i).getFats() + "', '" +
                        ingredient.get(i).getCalories() + "', '" + ingredient.get(i).getProtein() + "', '" + ingredient.get(i).getCarbs() + "', " + ingredient.get(i).getRecipeID() + ", '" +
                        ingredient.get(i).getServing() + "', '" + String.valueOf(Integer.parseInt(ingredient.get(i).getServing()) * ingredient.get(i).getQuantity()) + "', '" +
                        ingredient.get(i).getStatus() + "');";
                if (db == null) {
                    db = this.getWritableDatabase();
                } else if (!db.isOpen()) {
                    db = this.getWritableDatabase();
                }
                db.execSQL(query);
                if (db.isOpen())
                    db.close();


            }

        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }

    }

    public boolean isIngredientAvailableInShoppingDB(String ingredientID, String recipeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_SHOPPING_INGREDIENTS +
                " WHERE " + COLUMN_INGREDIENTS_ID + " = ? AND " + COLUMN_INGREDIENTS_RECIPE_ID + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{ingredientID, recipeID});
        int cursorCount = cursor.getCount();
        cursor.close();
        if (db.isOpen())
            db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getIngredients(String recipeID) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING
        };
        String selection = COLUMN_INGREDIENTS_RECIPE_ID + " = ?";
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENTS_INGREDIENT + " ASC";
        List<NutritionDataModel.Ingredient> ingredientsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT category_id,category_name, FROM category Model ORDER BY category_name;
         */
        Cursor cursor = db.query(TABLE_SHOPPING_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                new String[]{recipeID},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NutritionDataModel.Ingredient ingredientsDataModel = new NutritionDataModel.Ingredient();
                    ingredientsDataModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                    ingredientsDataModel.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                    ingredientsDataModel.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                    ingredientsDataModel.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                    ingredientsDataModel.setQuantity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY))));
                    ingredientsDataModel.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                    ingredientsDataModel.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                    ingredientsDataModel.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                    ingredientsDataModel.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                    ingredientsDataModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                    ingredientsDataModel.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                    ingredientsDataModel.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                    // Adding user record to list
                    ingredientsList.add(ingredientsDataModel);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        // return user list
        return ingredientsList;
    }


    @SuppressLint("Range")
    public List<NutritionDataModel.Ingredient> getShoppingIngredients(String recipeID) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING
        };
        String selection = COLUMN_INGREDIENTS_RECIPE_ID + " = ?";
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENTS_INGREDIENT + " ASC";
        List<NutritionDataModel.Ingredient> ingredientsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT category_id,category_name, FROM category Model ORDER BY category_name;
         */
        Cursor cursor = db.query(TABLE_SHOPPING_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                new String[]{recipeID},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NutritionDataModel.Ingredient ingredientsDataModel = new NutritionDataModel.Ingredient();
                    ingredientsDataModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                    ingredientsDataModel.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                    ingredientsDataModel.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                    ingredientsDataModel.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                    ingredientsDataModel.setQuantity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY))));
                    ingredientsDataModel.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                    ingredientsDataModel.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                    ingredientsDataModel.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                    ingredientsDataModel.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                    ingredientsDataModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                    ingredientsDataModel.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));
                    ingredientsDataModel.setRecipeID(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_RECIPE_ID)));
                    // Adding user record to list
                    ingredientsList.add(ingredientsDataModel);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        // return user list
        return ingredientsList;
    }

    @SuppressLint("Range")
    public List<SingleRecipeDataModel.Ingredient> getIngredientsByRecipeId(String recipeID) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INGREDIENTS_ID,
                COLUMN_INGREDIENTS_CATEGORY,
                COLUMN_INGREDIENTS_INGREDIENT,
                COLUMN_INGREDIENTS_UNIT,
                COLUMN_INGREDIENTS_QUANTITY,
                COLUMN_INGREDIENTS_FATS,
                COLUMN_INGREDIENTS_CALORIES,
                COLUMN_INGREDIENTS_PROTEIN,
                COLUMN_INGREDIENTS_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,
                COLUMN_INGREDIENTS_RECIPE_ID,
                COLUMN_RECIPE_SERVING
        };
        // sorting orders
        String sortOrder =
                COLUMN_INGREDIENTS_INGREDIENT + " ASC";
        List<SingleRecipeDataModel.Ingredient> ingredientsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_INGREDIENTS_RECIPE_ID + " = ?";

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT category_id,category_name, FROM category Model ORDER BY category_name;
         */
        Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                new String[]{recipeID},        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    SingleRecipeDataModel.Ingredient ingredientsDataModel = new SingleRecipeDataModel.Ingredient();
                    ingredientsDataModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                    ingredientsDataModel.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CATEGORY)));
                    ingredientsDataModel.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                    ingredientsDataModel.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                    ingredientsDataModel.setQuantity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY))));
                    ingredientsDataModel.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                    ingredientsDataModel.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                    ingredientsDataModel.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                    ingredientsDataModel.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));
                    // Adding user record to list
                    ingredientsList.add(ingredientsDataModel);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        // return user list
        return ingredientsList;
    }


    //get category from db
    @SuppressLint("Range")
    public ArrayList<NutritionDataModel.Category> getCategories() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_CATEGORIES_ID,
                COLUMN_CATEGORIES_NAME,
        };
        // sorting orders
        String sortOrder =
                COLUMN_CATEGORIES_NAME + " ASC";
        ArrayList<NutritionDataModel.Category> ingredientsCategoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT category_id,category_name, FROM category Model ORDER BY category_name;
         */
        Cursor cursor = db.query(TABLE_INGREDIENT_CATEGORIES, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NutritionDataModel.Category category = new NutritionDataModel.Category();
                    category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORIES_ID)));
                    category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORIES_NAME)));
                    // Adding user record to list
                    ingredientsCategoryList.add(category);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        // return user list
        return ingredientsCategoryList;
    }

    public boolean checkCategoryByID(String categoryID) {
        String[] columns = {
                COLUMN_CATEGORIES_NAME
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CATEGORIES_ID + " = ?";
        String[] selectionArgs = {categoryID};
        Cursor cursor = db.query(TABLE_INGREDIENT_CATEGORIES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    /*public boolean checkIngredientID(String ingredientId) {
        String[] columns = {
                COLUMN_INGREDIENTS_INGREDIENT
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_INGREDIENTS_ID + " = ?";
        String[] selectionArgs = {ingredientId};
        Cursor cursor = db.query(TABLE_INGREDIENTS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }*/

    public boolean isRecipeByIdInShoppingCart(String userID, String recipeID) {
        String[] columns = {
                COLUMN_SHOPPING_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SHOPPING_ID + " FROM " + TABLE_SHOPPING_LIST +
                " WHERE " + COLUMN_RECIPE_ID + " = " + recipeID + " AND " + COLUMN_USER_ID + " = " + userID + "", null);
        /*Cursor cursor = db.query(TABLE_SHOPPING_LIST, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);  */                    //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    public boolean isCheckUncheckExist(int ingredient_id, int recipeID) {
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ? AND " + COLUMN_INGREDIENTS_ID + " = ?";
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_CHECK_UNCHECK +
                " WHERE " + COLUMN_INGREDIENTS_ID + " = " + ingredient_id + " AND " + COLUMN_RECIPE_ID + " = " + recipeID + "", null);
      /* Cursor cursor = db.query(TABLE_SHOPPING_LIST, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);   */                  //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public boolean checkUserRecipeId(String recipe_id) {
        String[] columns = {
                COLUMN_RECIPE_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {recipe_id};
        //TODO TABLE NAME need to be CHANGED if.......
        Cursor cursor = db.query(TABLE_DASHBOARD_RECIPES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    // add method for pivot table
    public void addPivoteTableEntries(int recipe_id, int ingrident_id, int category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PIVOT_RECIPE_ID, recipe_id);
        values.put(COLUMN_PIVOT_CATEGORY_ID, category_id);
        values.put(COLUMN_PIVOT_INGREDIENTS_ID, ingrident_id);
        db.insert(TABLE_PIVOT_RECIPE, null, values);
        db.close();
    }

    public int getCategoryIdFromName(String categoryName) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + COLUMN_CATEGORIES_ID + " FROM " + TABLE_INGREDIENT_CATEGORIES +
                " WHERE " + COLUMN_CATEGORIES_NAME + " = ?", new String[]{categoryName});
        int id = -1;
        if (cursor.moveToFirst()) id = cursor.getInt(0);
        cursor.close();
        sqLiteDatabase.close();
        return id;
    }

    public void addShoppingList(int user_id, int recipe_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipe_id);
        values.put(COLUMN_USER_ID, user_id);
        db.insert(TABLE_SHOPPING_LIST, null, values);
        db.close();
    }

    //Delete All shoppinglist
    public void deleteAllShoppingListData(int user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING_LIST, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user_id)});
        db.execSQL("delete  from " + TABLE_SHOPPING_LIST);
        db.close();
    }

    public void deleteSpecificShoppingListData(int recipe_id, int user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING_LIST, COLUMN_RECIPE_ID + " = ? AND " +
                        COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(recipe_id), String.valueOf(user_id)});
    }

   /* public List<NutritionDataModel.Data> getAllShoppingList() {
        db.execSQL("delete  from " + TABLE_SHOPPING_LIST);
        db.close();
    }*/

    @SuppressLint("Range")
    public List<NutritionDataModel.Recipe> getAllShoppingList() {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_DURATION,
                COLUMN_RECIPE_COOK,
                COLUMN_RECIPE_METHOD,
                COLUMN_RECIPE_SERVING,
                //Ingredients List
                /*COLUMN_RECIPE_INGREDIENT_ID,
                COLUMN_RECIPE_INGREDIENT_CATEGORY_NAME,
                COLUMN_RECIPE_INGREDIENT,
                COLUMN_RECIPE_INGREDIENT_UNIT,
                COLUMN_RECIPE_INGREDIENT_QUANTITY,
                COLUMN_RECIPE_INGREDIENT_FATS,
                COLUMN_RECIPE_INGREDIENT_CALORIES,
                COLUMN_RECIPE_INGREDIENT_PROTIENS,
                COLUMN_RECIPE_INGREDIENT_CARBS,
                COLUMN_RECIPE_INGREDIENT_STATUS,*/
        };

        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_NAME + " ASC";
        List<NutritionDataModel.Recipe> shoppingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER_SHOPPING_LIST, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                NutritionDataModel.Recipe getShopping = new NutritionDataModel.Recipe();
                getShopping.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                getShopping.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                getShopping.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                getShopping.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                getShopping.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                getShopping.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
                getShopping.setMethods(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHOD)));
                getShopping.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));

              /*  for(int i=0; i<getShopping.getIngredients().size();i++){
                    getShopping.getIngredients().get(i).setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_ID)));
                    getShopping.getIngredients().get(i).setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_CATEGORY_NAME)));
                    getShopping.getIngredients().get(i).setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT)));
                    getShopping.getIngredients().get(i).setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_UNIT)));
                    getShopping.getIngredients().get(i).setQuantity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_QUANTITY))));
                    getShopping.getIngredients().get(i).setFats(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_FATS)));
                    getShopping.getIngredients().get(i).setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_CALORIES)));
                    getShopping.getIngredients().get(i).setFats(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_PROTIENS)));
                    getShopping.getIngredients().get(i).setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_CARBS)));
                    getShopping.getIngredients().get(i).setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGREDIENT_STATUS)));
                }*/



                /*@SuppressLint("Range") String recipeListString = cursor.getString(cursor.getColumnIndex(COLUMN_SHOPPING_RECIPE));
                @SuppressLint("Range") String categoryListString = cursor.getString(cursor.getColumnIndex(COLUMN_SHOPPING_CATEGORY));*/

                //warmup

              /*  type = new TypeToken<ArrayList<NutritionDataModel.Recipe>>() {
                }.getType();
                ArrayList<NutritionDataModel.Recipe> recipeArrayList = gson.fromJson(recipeListString, type);

                //workouts
                type = new TypeToken<ArrayList<NutritionDataModel.Category>>() {
                }.getType();
                ArrayList<NutritionDataModel.Category> categoryArrayList = gson.fromJson(categoryListString, type);
                try {

                    getShopping.setRecipes(recipeArrayList);
                    getShopping.setCategories(categoryArrayList);
                    //System.out.println(workoutList + " is it null---------------------------------");
                } catch (Exception e) {
                    System.out.println(e);

                }*/


                // Adding user record to list
                shoppingList.add(getShopping);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return shoppingList;
    }

    @SuppressLint("Range")
    public NutritionDataModel.Recipe getNutritionByID(String recipeID) {
        // array of columns to fetch
        String selection = COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {recipeID};

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_IMAGE,
                COLUMN_RECIPE_DURATION,
                COLUMN_RECIPE_COOK,
                COLUMN_RECIPE_METHOD,
                COLUMN_RECIPE_SERVING
        };

        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table


        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER_SHOPPING_LIST, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        NutritionDataModel.Recipe getShopping = new NutritionDataModel.Recipe();
        if (cursor.moveToFirst()) {
            do {
                getShopping.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                getShopping.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                getShopping.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                getShopping.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                getShopping.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                getShopping.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
                getShopping.setMethods(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHOD)));
                getShopping.setServing(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_SERVING)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return getShopping;
    }

    // ================================================================ Methods for Program Table===============================

    public void addProgram(ProgramsDataModel.Datum programsDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRAM_ID, programsDataModel.getId());
        values.put(COLUMN_PROGRAM_NAME, programsDataModel.getName());
        values.put(COLUMN_PROGRAM_THUMBNAIL, programsDataModel.getThumbnail());
        values.put(COLUMN_PROGRAM_DESCRIPTION, programsDataModel.getDescription());
        // Inserting Row
        db.insert(TABLE_PROGRAM, null, values);

        db.close();
    }

    public void updateProgram(ProgramsDataModel.Datum programsDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selection = COLUMN_PROGRAM_ID + " = ?";
        String selectionArgs[] = {String.valueOf(programsDataModel.getId())};
        values.put(COLUMN_PROGRAM_ID, programsDataModel.getId());
        values.put(COLUMN_PROGRAM_NAME, programsDataModel.getName());
        values.put(COLUMN_PROGRAM_THUMBNAIL, programsDataModel.getThumbnail());
        values.put(COLUMN_PROGRAM_DESCRIPTION, programsDataModel.getDescription());
        // Inserting Row
        db.update(TABLE_PROGRAM, values, selection, selectionArgs);
        db.close();
    }

    // delete all coaches

    public void deleteAllProgramsData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete  from " + TABLE_PROGRAM);
        db.close();
    }

    void clearAllShoppingList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete  from " + TABLE_SHOPPING_LIST);
        db.close();
    }


    //method for get all Program Data
    @SuppressLint("Range")
    public List<ProgramsDataModel.Datum> getAllProgram() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PROGRAM_ID,
                COLUMN_PROGRAM_NAME,
                COLUMN_PROGRAM_THUMBNAIL,
                COLUMN_PROGRAM_DESCRIPTION,

        };
        // sorting orders
        String sortOrder =
                COLUMN_PROGRAM_ID + " ASC";
        List<ProgramsDataModel.Datum> programList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_PROGRAM, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProgramsDataModel.Datum programs = new ProgramsDataModel.Datum();
                programs.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRAM_ID)));
                programs.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_NAME)));
                programs.setThumbnail(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_THUMBNAIL)));
                programs.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_DESCRIPTION)));

                // Adding user record to list
                programList.add(programs);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return programList;
    }

    public boolean checkProgramId(String program_id) {
        String[] columns = {
                COLUMN_PROGRAM_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PROGRAM_ID + " = ?";
        String[] selectionArgs = {program_id};

        Cursor cursor = db.query(TABLE_PROGRAM, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    // ================================================================ Methods for Progress Table===============================

    public void addProgramDetails(DaysUnlockModel programDetail) {
        SQLiteDatabase db = this.getWritableDatabase();

        /* for (DaysUnlockModel programDetail : daysUnlockModels) {*/
        ContentValues values = new ContentValues();

        values.put(COLUMN_PROGRAM, programDetail.getData().getProgram());
        values.put(COLUMN_PROGRAM_TOTAL_WEEKS, programDetail.getData().getTotalWeeks());
        values.put(COLUMN_PROGRAM_TOTAL_VIDEOS, programDetail.getData().getTotalVideos());
        values.put(COLUMN_PROGRAM_WATCHED_VIDEOS, programDetail.getData().getWatchedVideos());
        values.put(COLUMN_PROGRAM_UNLOCK_WEEK, programDetail.getData().getUnlockWeek());
        values.put(COLUMN_PROGRAM_UNLOCK_DAY, programDetail.getData().getUnlockDay());

        // Inserting Row
        db.insert(TABLE_PROGRESS_FRAGMENT, null, values);
        //  }
        db.close();
    }

    public void updateProgramDetails(DaysUnlockModel programDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selection = COLUMN_PROGRAM + " = ?";
        String[] selectionArgs = {programDetail.getData().getProgram()};
        values.put(COLUMN_PROGRAM, programDetail.getData().getProgram());
        values.put(COLUMN_PROGRAM_TOTAL_WEEKS, programDetail.getData().getTotalWeeks());
        values.put(COLUMN_PROGRAM_TOTAL_VIDEOS, programDetail.getData().getTotalVideos());
        values.put(COLUMN_PROGRAM_WATCHED_VIDEOS, programDetail.getData().getWatchedVideos());
        values.put(COLUMN_PROGRAM_UNLOCK_WEEK, programDetail.getData().getUnlockWeek());
        values.put(COLUMN_PROGRAM_UNLOCK_DAY, programDetail.getData().getUnlockDay());
        // Inserting Row
        db.update(TABLE_PROGRESS_FRAGMENT, values, selection, selectionArgs);
        db.close();
    }

    //get program Details
    @SuppressLint("Range")
    public List<DaysUnlockModel.Data> getProgramDetails(String programName) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PROGRAM,
                COLUMN_PROGRAM_TOTAL_WEEKS,
                COLUMN_PROGRAM_TOTAL_VIDEOS,
                COLUMN_PROGRAM_WATCHED_VIDEOS,
                COLUMN_PROGRAM_UNLOCK_WEEK,
                COLUMN_PROGRAM_UNLOCK_DAY,
        };
        // sorting orders
        String sortOrder = COLUMN_PROGRAM + " ASC";
        List<DaysUnlockModel.Data> programDetailList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PROGRAM + " = ?";
        String[] selectionArgs = {programName};
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_PROGRESS_FRAGMENT, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        //  DaysUnlockModel programDetail = new DaysUnlockModel();
        if (cursor.moveToFirst()) {
            do {
                DaysUnlockModel.Data programDetail = new DaysUnlockModel.Data();
                programDetail.setProgram(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM)));
                programDetail.setTotalWeeks(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_TOTAL_WEEKS))));
                programDetail.setTotalVideos(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_TOTAL_VIDEOS))));
                programDetail.setWatchedVideos(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_WATCHED_VIDEOS))));
                programDetail.setUnlockWeek(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_UNLOCK_WEEK))));
                programDetail.setUnlockDay(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_UNLOCK_DAY))));
                // Adding user record to list
                programDetailList.add(programDetail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return program list
        return programDetailList;
    }

    //check program added
    public boolean isProgramDetailAvailable(String program_name) {
        String[] columns = {
                COLUMN_PROGRAM
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PROGRAM + " = ?";
        String[] selectionArgs = {program_name};

        Cursor cursor = db.query(TABLE_PROGRESS_FRAGMENT, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    //Delete program detail
    public void deleteAllProgramDetailData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete  from " + TABLE_PROGRESS_FRAGMENT);
        db.close();
    }


    // =================================================== methods for exercise table=============================================

    //add program Exercise

    public void addProgramExercises(int programID, String programName, String programDescription, ProgressDataModel.Data.Workouts.Workout workout, String week, String day) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRAM_ID, programID);
        values.put(COLUMN_PROGRAM, programName);
        values.put(COLUMN_PROGRAM_DESCRIPTION, programDescription);
        values.put(COLUMN_PROGRAM_WORKOUT_NAME, workout.getName());
        values.put(COLUMN_PROGRAM_WORKOUT_ID, workout.getId());
        values.put(COLUMN_PROGRAM_DURATION, workout.getDuration());
        if (workout.getIsWatched()) {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "true");
        } else {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "false");
        }
        values.put(COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL, workout.getThumbnail());
        values.put(COLUMN_PROGRAM_WEEK, week);
        values.put(COLUMN_PROGRAM_DAY, day);
        db.insert(TABLE_PROGRAM_EXERCISES, null, values);
        db.close();

    }

    public boolean isProgramExerciseAvailable(String programID, String workoutID, String week, String day) {
        String[] columns = {
                COLUMN_PROGRAM
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PROGRAM_ID + " = ? AND " + COLUMN_PROGRAM_WORKOUT_ID + " = ? AND " + COLUMN_PROGRAM_WEEK + " = ? AND " + COLUMN_PROGRAM_DAY + " = ?";
        String[] selectionArgs = {programID, workoutID, week, day};

        Cursor cursor = db.query(TABLE_PROGRAM_EXERCISES, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public void updateProgramExercises(int programID, String programName, String programDescription, ProgressDataModel.Data.Workouts.Workout workout, String week, String day) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selection = COLUMN_PROGRAM_ID + " = ? AND " + COLUMN_PROGRAM_WORKOUT_ID + " = ? AND " + COLUMN_PROGRAM_WEEK + " = ? AND " + COLUMN_PROGRAM_DAY + " = ?";
        String[] selectionArgs = {String.valueOf(programID), String.valueOf(workout.getId()), week, day};
        values.put(COLUMN_PROGRAM_ID, programID);
        values.put(COLUMN_PROGRAM, programName);
        values.put(COLUMN_PROGRAM_DESCRIPTION, programDescription);
        values.put(COLUMN_PROGRAM_WORKOUT_NAME, workout.getName());
        values.put(COLUMN_PROGRAM_WORKOUT_ID, workout.getId());
        values.put(COLUMN_PROGRAM_DURATION, workout.getDuration());
        if (workout.getIsWatched()) {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "true");
        } else {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "false");
        }
        values.put(COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL, workout.getThumbnail());
        values.put(COLUMN_PROGRAM_WEEK, week);
        values.put(COLUMN_PROGRAM_DAY, day);

        db.update(TABLE_PROGRAM_EXERCISES, values, selection, selectionArgs);
        db.close();

    }

    public void updateProgramWorkoutWatchedStatus(int programID, int workoutID, String week, String day, boolean isWatched) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selection = COLUMN_PROGRAM_ID + " = ? AND " + COLUMN_PROGRAM_WORKOUT_ID + " = ? AND " + COLUMN_PROGRAM_WEEK + " = ? AND " + COLUMN_PROGRAM_DAY + " = ?";
        String[] selectionArgs = {String.valueOf(programID), String.valueOf(workoutID), week, day};
        if (isWatched) {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "true");
        } else {
            values.put(COLUMN_PROGRAM_IS_WATCHED, "false");
        }

        db.update(TABLE_PROGRAM_EXERCISES, values, selection, selectionArgs);
        db.close();

    }


    // get All program_exercises Data
    @SuppressLint("Range")
    public List<ProgramWorkout> getAllProgramExercise(String programID, String week, String day) {

        String[] columns = {
                COLUMN_PROGRAM_ID,
                COLUMN_PROGRAM,
                COLUMN_PROGRAM_DESCRIPTION,
                COLUMN_PROGRAM_WORKOUT_NAME,
                COLUMN_PROGRAM_WORKOUT_ID,
                COLUMN_PROGRAM_DURATION,
                COLUMN_PROGRAM_IS_WATCHED,
                COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL,
                COLUMN_PROGRAM_WEEK,
                COLUMN_PROGRAM_DAY
        };
        // sorting orders
        String sortOrder =
                COLUMN_PROGRAM_WORKOUT_ID + " ASC";
        List<ProgramWorkout> programWorkouts = new ArrayList<ProgramWorkout>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PROGRAM_ID + " = ? AND " + COLUMN_PROGRAM_WEEK + " = ? AND " + COLUMN_PROGRAM_DAY + " = ?";
        String[] selectionArgs = {programID, week, day};
        // query the user table

        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_PROGRAM_EXERCISES, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProgramWorkout programWorkout = new ProgramWorkout();
                try {

                    programWorkout.setProgramID(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRAM_ID)));
                    programWorkout.setProgramName(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM)));
                    programWorkout.setProgramDescription(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_DESCRIPTION)));
                    programWorkout.setWorkoutId(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRAM_WORKOUT_ID)));
                    programWorkout.setWorkoutName(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_WORKOUT_NAME)));
                    programWorkout.setWorkoutDuration(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_DURATION)));
                    String status = cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_IS_WATCHED));
                    if (status.matches("true")) {
                        programWorkout.setWorkoutIsWatched(true);
                    } else {
                        programWorkout.setWorkoutIsWatched(false);
                    }
                    programWorkout.setWorkoutThumbnail(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_WORKOUT_THUMBNAIL_URL)));
                    programWorkout.setWeek(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_WEEK)));
                    programWorkout.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM_DAY)));
                    programWorkouts.add(programWorkout);

                    //  bestProgram.setWarmup(warmupList);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return programWorkouts;
    }


    //Delete program detail
    public void deleteAllProgramExercisesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_SHOPPINGLIST,null,null);
        db.execSQL("delete  from " + TABLE_PROGRAM_EXERCISES);
        db.close();
    }

    // =================================================== methods for coaches exercise table=============================================


    //add program Exercise
    public void addOrUpdateCoachesExercises(List<WorkoutDataModel.Data.Workout> workoutDataModel, int coach_id, int week, int day) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        for (WorkoutDataModel.Data.Workout workout : workoutDataModel) {

            //covert EXerciseTYpe list to json
           /* List<String> ExerciseTypeList = workout.getExerciseTypes();
            String inputExerciseTypeString = gson.toJson(ExerciseTypeList);*/

            //covert ExerciseCategory list to json
            /*List<WorkoutDataModel.Data.Workout> CategoryTypeList = workout.getCategory();
            String inputCategoryString = gson.toJson(CategoryTypeList);*/

            if (!isCoachAndWorkoutAvailable(String.valueOf(coach_id), String.valueOf(workout.getId()))) {
                if (!db.isOpen())
                    db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                Date date = new Date();
                long timeMilli = date.getTime();
                values.put(COLUMN_COACH_WORKOUT_ID, workout.getId());
                values.put(COLUMN_COACH_ID, coach_id);
                values.put(COLUMN_COACH_WORKOUT_NAME, workout.getName());
                values.put(COLUMN_COACH_WORKOUT_DESCRIPTION, workout.getThumbnail());
            /*values.put(COLUMN_COACH_WORKOUT_EXERCISE_TYPE, inputExerciseTypeString);
            values.put(COLUMN_COACH_WORKOUT_EXERCISE_CATEGORY, inputCategoryString);*/
                // values.put(COLUMN_COACH_WORKOUT_VIDEO_URL, workout.getVideoUrl());
                values.put(COLUMN_COACH_WORKOUT_THUMBNAIL, workout.getThumbnail());
                values.put(COLUMN_COACH_WORKOUT_REPS, workout.getReps());
                values.put(COLUMN_COACH_WORKOUT_SETS, workout.getSets());
                values.put(COLUMN_COACH_WORKOUT_DURATION, workout.getDuration());
                if (workout.getWatched())
                    values.put(COLUMN_COACH_WORKOUT_IS_WATCHED, 1);
                else
                    values.put(COLUMN_COACH_WORKOUT_IS_WATCHED, 0);
                values.put(COLUMN_COACH_WEEK, week);
                values.put(COLUMN_COACH_DAY, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date dt = new Date();
                values.put(COLUMN_CREATED_AT, dateFormat.format(dt));
                values.put(COLUMN_UPDATED_AT, "");
                values.put(COLUMN_lOAD_DATE, String.valueOf(timeMilli));

                // Inserting Row
                db.insert(TABLE_COACHES_EXERCISES, null, values);
            } else {
                if (!db.isOpen())
                    db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                Date date = new Date();
                long timeMilli = date.getTime();
                values.put(COLUMN_COACH_WORKOUT_ID, workout.getId());
                values.put(COLUMN_COACH_ID, coach_id);
                values.put(COLUMN_COACH_WORKOUT_NAME, workout.getName());
                values.put(COLUMN_COACH_WORKOUT_DESCRIPTION, workout.getThumbnail());
            /*values.put(COLUMN_COACH_WORKOUT_EXERCISE_TYPE, inputExerciseTypeString);
            values.put(COLUMN_COACH_WORKOUT_EXERCISE_CATEGORY, inputCategoryString);*/
                // values.put(COLUMN_COACH_WORKOUT_VIDEO_URL, workout.getVideoUrl());
                values.put(COLUMN_COACH_WORKOUT_THUMBNAIL, workout.getThumbnail());
                values.put(COLUMN_COACH_WORKOUT_REPS, workout.getReps());
                values.put(COLUMN_COACH_WORKOUT_SETS, workout.getSets());
                values.put(COLUMN_COACH_WORKOUT_DURATION, workout.getDuration());
                values.put(COLUMN_COACH_WORKOUT_IS_WATCHED, workout.getWatched());
                values.put(COLUMN_lOAD_DATE, String.valueOf(timeMilli));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date dt = new Date();
                values.put(COLUMN_UPDATED_AT, dateFormat.format(dt));
                db.update(TABLE_COACHES_EXERCISES, values, COLUMN_COACH_ID + " = ? AND " + COLUMN_COACH_WORKOUT_ID + " = ? AND " + COLUMN_COACH_WEEK + " = ? AND " + COLUMN_COACH_DAY + " = ?",
                        new String[]{String.valueOf(coach_id), String.valueOf(workout.getId()), String.valueOf(week), String.valueOf(day)});

            }
        }
        if (db.isOpen())
            db.close();

    }

    public boolean isCoachAndWorkoutAvailable(String coachID, String workoutID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_COACHES_EXERCISES +
                " WHERE " + COLUMN_COACH_ID + " = ? AND " + COLUMN_COACH_WORKOUT_ID + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{coachID, workoutID});
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    // get All program_exercises Data
    @SuppressLint("Range")
    public List<WorkoutDataModel.Data.Workout> getAllCoachesExercise(String coachID, int week, int day) {
        // array of columns to fetch
        String selection = COLUMN_COACH_ID + " = ? AND " + COLUMN_COACH_WEEK + " = ? AND " + COLUMN_COACH_DAY + " = ?";

        String[] columns = {

                COLUMN_COACH_WORKOUT_ID,
                COLUMN_COACH_ID,
                COLUMN_COACH_WORKOUT_NAME,
                COLUMN_COACH_WORKOUT_DESCRIPTION,
                /*COLUMN_COACH_WORKOUT_EXERCISE_TYPE,
                COLUMN_COACH_WORKOUT_EXERCISE_CATEGORY,
                COLUMN_COACH_WORKOUT_VIDEO_URL,*/
                COLUMN_COACH_WORKOUT_THUMBNAIL,
                COLUMN_COACH_WORKOUT_REPS,
                COLUMN_COACH_WORKOUT_SETS,
                COLUMN_COACH_WORKOUT_DURATION,
                COLUMN_COACH_WORKOUT_IS_WATCHED,
                COLUMN_lOAD_DATE,
                COLUMN_COACH_WEEK,
                COLUMN_COACH_DAY

        };
        // sorting orders
        String sortOrder =
                COLUMN_COACH_WORKOUT_ID + " ASC";

        String[] selectionArgs = {coachID, String.valueOf(week), String.valueOf(day)};

        List<WorkoutDataModel.Data.Workout> workoutModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table

        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_COACHES_EXERCISES, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WorkoutDataModel.Data.Workout workout = new WorkoutDataModel.Data.Workout();
                try {
                    workout.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_ID))));
                    workout.setCoachId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_ID))));
                    workout.setName(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_NAME)));
                    //  workout.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_DESCRIPTION)));
                    //  workout.setVideoUrl(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_VIDEO_URL)));
                    workout.setThumbnail(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_THUMBNAIL)));
                    workout.setReps(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_REPS)));
                    workout.setSets(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_SETS)));
                    workout.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_DURATION)));
                    if (cursor.getInt(cursor.getColumnIndex(COLUMN_COACH_WORKOUT_IS_WATCHED)) == 1) {
                        workout.setWatched(true);
                    } else {
                        workout.setWatched(false);
                    }
                    workout.setLoadDate(String.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_lOAD_DATE))));
                    //  workout.setCategory(categoryList);
                    //  workout.setExerciseTypes(exerciseTypeList);

                    //  bestProgram.setWarmup(warmupList);
                    // System.out.println(categoryList + " is it null---------------------------------");
                } catch (Exception e) {
                    //System.out.println(e);
                    if (Common.isLoggingEnabled)
                        e.printStackTrace();

                }


                // Adding user record to list
                workoutModelList.add(workout);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return workoutModelList;
    }


    public void setCoachVideoWatchedStatus(int coachID, int workOutID, int videoWatchedStatus, int week, int day) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isOpen())
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COACH_WORKOUT_IS_WATCHED, videoWatchedStatus);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dt = new Date();
        values.put(COLUMN_UPDATED_AT, dateFormat.format(dt));
        db.update(TABLE_COACHES_EXERCISES, values, COLUMN_COACH_ID + " = ? AND " + COLUMN_COACH_WORKOUT_ID + " = ? AND " + COLUMN_COACH_WEEK + " = ? AND " + COLUMN_COACH_DAY + " = ?",
                new String[]{String.valueOf(coachID), String.valueOf(workOutID), String.valueOf(week), String.valueOf(day)});
        if (db.isOpen())
            db.close();
    }


    //Delete program detail
    public void deleteAllCoachesExercisesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_SHOPPINGLIST,null,null);
        db.execSQL("delete  from " + TABLE_COACHES_EXERCISES);
        db.close();
    }




 /*   @SuppressLint("Range")
    public SignupResponse getUserProfileData(String userID) {
        String sqlQuery = "SELECT * FROM " + TABLE_USER_PROFILE + " INNER JOIN " + TABLE_USER +
                " ON " + TABLE_USER_PROFILE + "." + COLUMN_USER_ID + " = " + TABLE_USER + "." + COLUMN_USER_ID
                + " WHERE " + TABLE_USER_PROFILE + "." + COLUMN_USER_ID + " = " + userID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        SignupResponse signupResponse = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                signupResponse = new SignupResponse();
                SignupResponse.Data data = new SignupResponse.Data();
                data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_ID)));
                data.setUser_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                data.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                data.setWeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_WEIGHT)));
                data.setAge(cursor.getString(cursor.getColumnIndex(COLUMN_USER_AGE)));
                data.setGoal(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL)));
                data.setLevel(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL)));
                data.setHeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEIGHT)));
                data.setGender(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GENDER)));
                data.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_USER_UNIT_TYPE)));
                data.setGoal_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL_ID)));
                data.setLevel_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL_ID)));

            }
        }
        return signupResponse;
    }*/


/*    public void addUserProfile(SignupResponse user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_WEIGHT, user.getData().getWeight());
        values.put(COLUMN_USER_HEIGHT, user.getData().getHeight());
        values.put(COLUMN_USER_AGE, user.getData().getAge());
        values.put(COLUMN_USER_GENDER, user.getData().getGender());
        values.put(COLUMN_USER_GOAL_ID, user.getData().getGoal_id());
        values.put(COLUMN_USER_LEVEL_ID, user.getData().getLevel_id());
        values.put(COLUMN_USER_UNIT_TYPE, user.getData().getUnit());
        values.put(COLUMN_USER_GOAL, user.getData().getGoal());
        values.put(COLUMN_USER_LEVEL, user.getData().getLevel());
        values.put(COLUMN_USER_AVATAR, user.getData().getAvatar());
        values.put(COLUMN_USER_ID, user.getData().getUser_id());
        db.insert(TABLE_USER_PROFILE, null, values);
        db.close();
    }*/


   /* @SuppressLint("Range")
    public SignupResponse getUserProfileData(String userID) {
        String sqlQuery = "SELECT * FROM " + TABLE_USER_PROFILE + " INNER JOIN " + TABLE_USER +
                " ON " + TABLE_USER_PROFILE + "." + COLUMN_USER_ID + " = " + TABLE_USER + "." + COLUMN_USER_ID
                + " WHERE " + TABLE_USER_PROFILE + "." + COLUMN_USER_ID + " = " + userID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        SignupResponse signupResponse = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                signupResponse = new SignupResponse();
                SignupResponse.Data data = new SignupResponse.Data();
                data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_ID)));
                data.setUser_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                data.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                data.setWeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_WEIGHT)));
                data.setAge(cursor.getString(cursor.getColumnIndex(COLUMN_USER_AGE)));
                data.setGoal(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL)));
                data.setLevel(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL)));
                data.setHeight(cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEIGHT)));
                data.setGender(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GENDER)));
                data.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_USER_UNIT_TYPE)));
                data.setGoal_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GOAL_ID)));
                data.setLevel_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LEVEL_ID)));

            }
        }
        return signupResponse;
    }*/


   /* public void addRecipe(List<NutritionDataModel.Recipe> recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < recipe.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_ID, recipe.get(i).getId());
            values.put(COLUMN_RECIPE_TITLE, recipe.get(i).getTitle());
            values.put(COLUMN_RECIPE_NAME, recipe.get(i).getName());
            values.put(COLUMN_RECIPE_DAY, recipe.get(i).getDay());
            values.put(COLUMN_RECIPE_IMAGE, recipe.get(i).getImageURL());
            values.put(COLUMN_RECIPE_DURATION, recipe.get(i).getDuration());
            values.put(COLUMN_RECIPE_COOK, recipe.get(i).getCook());
            values.put(COLUMN_RECIPE_METHODS, recipe.get(i).getMethods());
            db.insert(TABLE_RECIPES, null, values);
        }
        db.close();
    }*/








/*    public Cart getAllRecipesFromCart(String userID) {
        String sqlQuery = "SELECT * FROM " + TABLE_SHOPPING_LIST + " INNER JOIN " + TABLE_RECIPES +
                " ON " + TABLE_SHOPPING_LIST + "." + COLUMN_RECIPE_ID + " = " + TABLE_RECIPES + "." + COLUMN_RECIPE_ID
                + " INNER JOIN " + TABLE_USER + " ON " + TABLE_SHOPPING_LIST + "." + COLUMN_USER_ID + " = " + TABLE_USER + "." + COLUMN_USER_ID
                + " WHERE " + TABLE_SHOPPING_LIST + "." + COLUMN_USER_ID + " = " + userID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        Cart cart = null;
        if (cursor != null) {
            ArrayList<CartItem> cartItems = new ArrayList<>();

            while (cursor.moveToNext()) {

                *//*if (cursor.moveToFirst()) {
                @SuppressLint("Range") CartItem cartItem = new CartItem(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_SHOPPING_ID)),
                        new Users(
                                cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL))
                        ), new NutritionDataModel.Recipe(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)),
                        //cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DAY)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)),
                       cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)),
                       cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)), ""

                        cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHODS))
                )
                );
                cartItems.add(cartItem);
                *//*}

            } while (cursor.moveToNext());*//*
            cart = new Cart(cartItems);
        }
        return cart;
    }*/

    @SuppressLint("Range")
  /*  public ArrayList<Nutrition> getAllIngredientsByRecipeAndCategory(String recipeID, String categoryID)
    {
        String sqlQuery = "SELECT * FROM " + TABLE_PIVOT_RECIPE + " INNER JOIN " + TABLE_RECIPES +
                " ON " + TABLE_PIVOT_RECIPE + "." + COLUMN_PIVOT_RECIPE_ID + " = " + TABLE_RECIPES + "." + COLUMN_RECIPE_ID
                + " INNER JOIN " + TABLE_INGREDIENTS + " ON " + TABLE_PIVOT_RECIPE + "." + COLUMN_PIVOT_INGREDIENTS_ID + " = " + TABLE_INGREDIENTS + "." + COLUMN_INGREDIENTS_ID
                + " INNER JOIN " + TABLE_INGREDIENT_CATEGORIES + " ON " + TABLE_PIVOT_RECIPE + "." + COLUMN_PIVOT_CATEGORY_ID + " = " + TABLE_INGREDIENT_CATEGORIES + "." + COLUMN_CATEGORIES_ID
                + " WHERE " + TABLE_PIVOT_RECIPE + "." + COLUMN_PIVOT_RECIPE_ID + " = " + recipeID + " AND " +
                TABLE_PIVOT_RECIPE + "." + COLUMN_PIVOT_CATEGORY_ID + " = " + categoryID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        ArrayList<Nutrition> nutritions = new ArrayList<>();
        if (cursor != null) {

            do {

                if (cursor.moveToFirst()) {

                    NutritionDataModel.Recipe recipe = new NutritionDataModel.Recipe();
                    recipe.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                    recipe.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                    recipe.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                    recipe.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DAY)));
                    recipe.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
                    recipe.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                    recipe.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
                    recipe.setMethods(""*//*cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHODS))*//*);

                    NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                    ingredient.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_INGREDIENTS_ID)));
                    ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_INGREDIENT)));
                    ingredient.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_UNIT)));
                    ingredient.setQuantity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_QUANTITY))));
                    ingredient.setFats(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_FATS)));
                    ingredient.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CALORIES)));
                    ingredient.setProtein(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_PROTEIN)));
                    ingredient.setCarbs(cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS_CARBS)));

                    NutritionDataModel.Category category = new NutritionDataModel.Category();
                    category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORIES_ID)));
                    category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORIES_NAME)));

                    nutritions.add(new Nutrition(recipe, ingredient, category));
                }

            } while (cursor.moveToNext());

        }
        return nutritions;
    }*/


    //Delete All shoppinglist
/*    public void deleteSpecificShoppingListData(int NUTRITION_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING_LIST, "nutrition_id=?", new String[]{String.valueOf(NUTRITION_ID)});
        //  db.execSQL("delete  from "+ TABLE_SHOPPINGLIST);
        db.close();
    }*/


    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";
        // selection argument
        String[] selectionArgs = {email};
        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

   /* public boolean checkUserProfile(String userID) {
        String[] columns = {
                COLUMN_USER_PROFILE_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {userID};
        Cursor cursor = db.query(TABLE_USER_PROFILE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }*/


    //add coach
    /*public void addCoach(CoachesProfileDataModel coach) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        for (CoachesDataModel c : coach) {
            ContentValues values = new ContentValues();

            // convert warmup list to json

            List<CoachesDataModel.Warmup> warmUpList = c.getWarmup();
            gson = new Gson();
            String inputWarmup = gson.toJson(warmUpList);

            System.out.println(inputWarmup + " warmup list==========================lll=======================");
            System.out.println(c.getWarmup() + " warmup ==========================jjjj===================================");

            // convert workout list to json
            List<CoachesDataModel.Workout> workUpList = c.workouts;
            String inputWorkout = gson.toJson(workUpList);
            values.put(COLUMN_COACH_ID, c.getCoachNumber());
            values.put(COLUMN_COACH_NAME, c.getName());
            values.put(COLUMN_COACH_DESCRIPTION, c.getDescription());
            values.put(COLUMN_COACH_IMAGE, c.getImageURL());
            values.put(COLUMN_COACH_ROLE, c.getRole());
            values.put(COLUMN_COACH_WARMUP, inputWarmup);
            values.put(COLUMN_COACH_WORKOUT, inputWorkout);

            db.insert(TABLE_COACHES, null, values);
        }
        db.close();

    }
*/

    // delete all coaches

    public void deleteAllCoachesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_SHOPPINGLIST,null,null);
        db.execSQL("delete  from " + TABLE_COACHES);
        db.close();
    }


    public void addNotification(NotificationModel notificationModel, String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();

        values.put(COLUMN_USER_ID, userID);
        values.put(COLUMN_NOTIFICATION_TITLE, notificationModel.getTitle());
        values.put(COLUMN_NOTIFICATION_DESCRIPTION, notificationModel.getDescription());
        values.put(COLUMN_NOTIFICATION_TIME, dateFormat.format(date));
        values.put(COLUMN_NOTIFICATION_IS_READ, false);
        db.insert(TABLE_NOTIFICATION, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<NotificationModel> getNotifications(String userID) {
        // array of columns to fetch
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {userID};

        String[] columns = {
                COLUMN_NOTIFICATION_ID,
                COLUMN_NOTIFICATION_TITLE,
                COLUMN_NOTIFICATION_DESCRIPTION,
                COLUMN_NOTIFICATION_IS_READ,
                COLUMN_NOTIFICATION_TIME
        };

        // sorting orders
        String sortOrder =
                COLUMN_NOTIFICATION_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table

        Cursor cursor = db.query(TABLE_NOTIFICATION, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        ArrayList<NotificationModel> notifications = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                NotificationModel notificationModel = new NotificationModel(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATION_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TIME)),
                        Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_IS_READ))));
                notifications.add(notificationModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return notifications;
    }

    public void deleteAllNotifications(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete  from " + TABLE_NOTIFICATION);
        db.delete(TABLE_NOTIFICATION, COLUMN_USER_ID + " = ?",
                new String[]{userID});
        db.close();
    }

    public void readAllNotifications(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isOpen())
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_IS_READ, "true");
        int updateCount = db.update(TABLE_NOTIFICATION, values, COLUMN_USER_ID + " = ?",
                new String[]{userID});
        if (Common.isLoggingEnabled) {
            Log.d("NOTIFICATION_TAG", "Update count on read All Notification: " + updateCount);
        }
        if (db.isOpen())
            db.close();
    }

    public void readNotificationByID(String userID, int notificationID) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isOpen())
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_IS_READ, "true");
        int updateCount = db.update(TABLE_NOTIFICATION, values, COLUMN_USER_ID + " = ? AND " + COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{userID, String.valueOf(notificationID)});
        if (Common.isLoggingEnabled) {
            Log.d("NOTIFICATION_TAG", "readNotificationByID: Update count on read All Notification: " + updateCount);
        }
        if (db.isOpen())
            db.close();
    }


    // ===================================================Nutrition Table Methods========================================

    //add coach
    /*public void addNutrition(NutritionDataModel nutritionDataModel) {
        //covert ingredients list to json
       *//* List<NutritionDataModel.Ingredient> ingredientsList =nutritionDataModel.getData().recipes.;
        gson = new Gson();
        String inputIngredients = gson.toJson(ingredientsList);*//*

     *//*  // convert methods list to json
        List<String> methodList = nutritionDataModel.method;
        String inputMethods = gson.toJson(methodList);*//*

        // convert category list to json
        List<NutritionDataModel.Category> categoryList = nutritionDataModel.getData().categories;
        String inputMethods = gson.toJson(categoryList);

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < nutritionDataModel.getData().recipes.size(); i++) {
            //covert ingredients list to json
            ContentValues values = new ContentValues();
            List<NutritionDataModel.Ingredient> ingredientsList = nutritionDataModel.getData().recipes.get(i).getIngredients();
            gson = new Gson();
            String inputIngredients = gson.toJson(ingredientsList);

            values.put(COLUMN_RECIPE_ID, nutritionDataModel.getData().recipes.get(i).getId());
            values.put(COLUMN_RECIPE_TITLE, nutritionDataModel.getData().recipes.get(i).getTitle());
            values.put(COLUMN_RECIPE_NAME, nutritionDataModel.getData().recipes.get(i).getName());
            values.put(COLUMN_RECIPE_DAY, nutritionDataModel.getData().recipes.get(i).getDay());
            values.put(COLUMN_RECIPE_IMAGE, nutritionDataModel.getData().recipes.get(i).getImageURL());
            values.put(COLUMN_RECIPE_DURATION, nutritionDataModel.getData().recipes.get(i).getDuration());
            values.put(COLUMN_RECIPE_COOK, nutritionDataModel.getData().recipes.get(i).getCook());
            values.put(COLUMN_RECIPE_METHODS, nutritionDataModel.getData().recipes.get(i).getMethods());
            values.put(COLUMN_RECIPE_INGRIDENTS, inputIngredients);
            db.insert(TABLE_RECIPES, null, values);

        }
        db.close();

    }*/

    // get All Nutritions Data
    //@SuppressLint({"Range", "Range"})
   /* public List<NutritionDataModel.Recipe> getAllNutrition() {
        // array of columns to fetch

        String[] columns = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_NAME,
                //COLUMN_RECIPE_DAY,
                COLUMN_RECIPE_IMAGE,
                *//*COLUMN_RECIPE_DURATION,
                COLUMN_RECIPE_COOK,
                COLUMN_RECIPE_METHODS,
                COLUMN_RECIPE_INGRIDENTS,*//*
        };
        // sorting orders
        String sortOrder =
                COLUMN_RECIPE_NAME + " ASC";
        List<NutritionDataModel.Recipe> nutritionDataModelList = new ArrayList<NutritionDataModel.Recipe>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table

    *//*Here query function is used to fetch records from user table this function works like we
        use sql query.
                * SQL query equivalent to this query function is
                * SELECT user_id, user_name, user_email, user_password FROM user ORDER BY user_name;*//*

        Cursor cursor = db.query(TABLE_RECIPES, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NutritionDataModel.Recipe nutrition = new NutritionDataModel.Recipe();
                nutrition.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECIPE_ID)));
                nutrition.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_TITLE)));
                nutrition.setName(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_NAME)));
                *//*nutrition.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                nutrition.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DAY)));*//*
                nutrition.setImageURL(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_IMAGE)));
               *//* nutrition.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_DURATION)));
                nutrition.setCook(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_COOK)));
                nutrition.setMethods(cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_METHODS)));*//*

                // String methodString = cursor.getString(cursor.getColumnIndex(COLUMN_NUTRITION_METHOD));
              //  String ingredientsString = cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_INGRIDENTS));

                //methods

          *//*      type = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> methodList = gson.fromJson(methodString, type);*//*

                //ingredients
                type = new TypeToken<ArrayList<NutritionDataModel.Ingredient>>() {
                }.getType();

                List<NutritionDataModel.Ingredient> ingredientList = gson.fromJson(ingredientsString, type);
                try {

                    *//*   nutrition.setMethod(methodList);*//*
                    nutrition.setIngredients(ingredientList);
                    System.out.println(ingredientList + " is it null---------------------------------");
                } catch (Exception e) {
                    System.out.println(e);

                }


                // Adding user record to list
                nutritionDataModelList.add(nutrition);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return nutritionDataModelList;
    }*/


    //=========================================================methods for Sleep Visualization=====================================


    // ================================================== Methods for Shoppinglist table================
   /*public void addToShoppingList(NutritionDataModel nutritionDataModel) {
       //covert ingredients list to json
       List<NutritionDataModel.Ingredients> ingredientsList = nutritionDataModel.ingredients;
       gson = new Gson();
       String inputIngredients = gson.toJson(ingredientsList);

       // convert methods list to json
       List<String> methodList = nutritionDataModel.method;
       String inputMethods = gson.toJson(methodList);

       SQLiteDatabase db = this.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put(NUTRITION_ID, nutritionDataModel.nutritionId);
       values.put(NUTRITION_NAME, nutritionDataModel.name);
       values.put(NUTRITION_TIME, nutritionDataModel.time);
       values.put(NUTRITION_INTAKE, "1");
       values.put(NUTRITION_DAY, nutritionDataModel.day);
       values.put(NUTRITION_IMAGE, nutritionDataModel.imageURL);
       values.put(NUTRITION_METHOD, inputMethods);
       values.put(NUTRITION_INGREDIENTS, inputIngredients);

       db.insert(TABLE_SHOPPINGLIST, null, values);
       db.close();

   }*/

    //Item is a class representing any item with id, name and description
   /* public void updateNutritionIntake(NutritionDataModel nutritionDataModel) {

        //covert ingredients list to json
        List<NutritionDataModel.Ingredients> ingredientsList = nutritionDataModel.ingredients;
        gson = new Gson();
        String inputIngredients = gson.toJson(ingredientsList);

        // convert methods list to json
        List<String> methodList = nutritionDataModel.method;
        String inputMethods = gson.toJson(methodList);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUTRITION_ID, nutritionDataModel.nutritionId);
        values.put(NUTRITION_NAME, nutritionDataModel.name);
        values.put(NUTRITION_TIME, nutritionDataModel.time);
        values.put(NUTRITION_INTAKE, nutritionDataModel.intake);
        values.put(NUTRITION_DAY, nutritionDataModel.day);
        values.put(NUTRITION_IMAGE, nutritionDataModel.imageURL);
        values.put(NUTRITION_METHOD, inputMethods);
        values.put(NUTRITION_INGREDIENTS, inputIngredients);

        String whereClause = "nutrition_id=?";
        String whereArgs[] = {String.valueOf(nutritionDataModel.nutritionId)};
        db.update(TABLE_SHOPPINGLIST, values, whereClause, whereArgs);
    }   */

    // get All ShoppingList Data
    /*  @SuppressLint("Range")*/
  /*  public List<NutritionDataModel> getAllShoppingList() {
        // array of columns to fetch

        String[] columns = {
                NUTRITION_ID,
               NUTRITION_NAME,
                NUTRITION_TIME,
               NUTRITION_DAY,
               NUTRITION_IMAGE,
                NUTRITION_METHOD,
               NUTRITION_INGREDIENTS,
               NUTRITION_INTAKE,
        };
        // sorting orders
        String sortOrder =
               NUTRITION_NAME + " ASC";
        List<NutritionDataModel> nutritionDataModelList = new ArrayList<NutritionDataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        // query the user table
        *//**
     * Here query function is used to fetch records from user table this function works like we use sql query.
     * SQL query equivalent to this query function is
     * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
     *//*
        Cursor cursor = db.query(TABLE_SHOPPINGLIST, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                NutritionDataModel nutrition = new NutritionDataModel();
                nutrition.setNutritionId(cursor.getInt(cursor.getColumnIndex(NUTRITION_ID)));
                nutrition.setName(cursor.getString(cursor.getColumnIndex(NUTRITION_NAME)));
                nutrition.setIntake(cursor.getString(cursor.getColumnIndex(NUTRITION_INTAKE)));
                nutrition.setTime(cursor.getString(cursor.getColumnIndex(NUTRITION_TIME)));
                nutrition.setDay(cursor.getString(cursor.getColumnIndex(NUTRITION_DAY)));
                nutrition.setImageURL(cursor.getString(cursor.getColumnIndex(NUTRITION_IMAGE)));

                String methodString = cursor.getString(cursor.getColumnIndex(NUTRITION_METHOD));
                String ingredientsString = cursor.getString(cursor.getColumnIndex(NUTRITION_INGREDIENTS));

                //methods

                type = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> methodList = gson.fromJson(methodString, type);

                //ingredients
                type = new TypeToken<ArrayList<NutritionDataModel.Ingredients>>() {
                }.getType();
                ArrayList<NutritionDataModel.Ingredients> ingredientList = gson.fromJson(ingredientsString, type);
                try {

                    nutrition.setMethod(methodList);
                    nutrition.setIngredients(ingredientList);
                    System.out.println(ingredientList + " is it null---------------------------------");
                } catch (Exception e) {
                    System.out.println(e);

                }


                // Adding user record to list
                nutritionDataModelList.add(nutrition);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return nutritionDataModelList;
    }*/


}
