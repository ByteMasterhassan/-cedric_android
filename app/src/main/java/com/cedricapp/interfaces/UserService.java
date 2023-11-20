package com.cedricapp.interfaces;

import com.cedricapp.model.ActivitiesModel;
import com.cedricapp.model.AllergyModel;
import com.cedricapp.model.AllergyModelForRegistration;
import com.cedricapp.model.AnalyticsModel;
import com.cedricapp.model.ApplicationDetailsModel;
import com.cedricapp.model.BestProgramModel;
import com.cedricapp.model.ChangePlanModel;
import com.cedricapp.model.CheckedUncheckedResponseModel;
import com.cedricapp.model.ChecklistModel;
import com.cedricapp.model.CoachesDataModel;
import com.cedricapp.model.CoachesProfileDataModel;
import com.cedricapp.model.DashboardNutrition;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.model.DaysUnlockModel;
import com.cedricapp.model.FoodPreferencesModel;
import com.cedricapp.model.GoalModel;
import com.cedricapp.model.LevelModel;
import com.cedricapp.model.LocationModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.LogsDataModel;
import com.cedricapp.model.LogoutModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.OtpResponseModel;
import com.cedricapp.model.PlanModel;
import com.cedricapp.model.PlansDataModel;
import com.cedricapp.model.ProductModel;
import com.cedricapp.model.ProgramsDataModel;
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.model.ResubscribeModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.SingleRecipeDataModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.model.StripeCustomerModel;
import com.cedricapp.model.StripeIntent;
import com.cedricapp.model.ChangeSubscriptionModel;
import com.cedricapp.model.SubscriptionModel;
import com.cedricapp.model.UnsubscribeLaterModel;
import com.cedricapp.model.UpdateLanguage;
import com.cedricapp.model.UpgradeDowngradeSubscriptionModel;
import com.cedricapp.model.UserDetailModel;
import com.cedricapp.model.UserStatusModel;
import com.cedricapp.model.VisualizationModel;
import com.cedricapp.model.VisualizationResponse;
import com.cedricapp.model.WorkoutDataModel;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UserService {
//post user Signup credentials
    /*@POST("/user/signup")*/

    @GET("application-details")
        //on below line we are creating a method to post our data.
    Call<ApplicationDetailsModel> checkUpdates(@Query("platform") String platform);

    @POST("user/register")
        //on below line we are creating a method to post our data.
    Call<SignupResponse> createPost(@Query("name") String name, @Query("email") String email, @Query("password") String password, @Query("lang") String lang);

    //   @Query("name") String name, @Query("email") String email, @Query("password") String password);

    @POST("user/save-feedback")
    Call<SignupResponse> feedbackAboutApp(@Header("Authorization") String access_token,
                                          @Query("user_id") String id, @Query("rating") String rating, @Query("type") String type,
                                          @Query("description") String description);


    //OTP
    @POST("user/forgot-password")
    //on below line we are creating a method to post our data.
    Call<OtpResponseModel> createOtp(
            @Query("email") String email, @Query("forgot_password") boolean isForgotPassword);


    //reset Password
    @POST("user/reset-password")
    //on below line we are creating a method to post our data.
    Call<OtpResponseModel> resetPassword(@Query("email") String email, @Query("isVerified") Boolean isVerified,
                                         @Query("old_password") String old_password, @Query("new_password") String new_password);
    /*Call <JsonObject>resetPassword(@Path("id") String id,
                                   @Query("password") String password, @Query("cpassword") String cpassword);*/


    //post user Login credentials
    @POST("user/login")
    //on below line we are creating a method to post our data.
    Call<LoginResponse> loginDataPost(
            @Query("email") String email, @Query("password") String password, @Query("device_id") String deviceId);

    @GET("user/user_details")
        //on below line we are creating a method to post our data.
    Call<LoginResponse> getUserDetails(@Header("Authorization") String access_token);

    //email Verification
    @POST("user/email-verify")
    Call<SignupResponse> emailVerification(
            @Query("email") String email, @Query("otp") String otp);

    //ProfileActivation
    @PUT("/user/activateProfile/{id}")
    //on below line we are creating a method to post our data.
    Call<LoginResponse> profileActivate(@Path("id") String id,
                                        @Query("weight") String weight, @Query("height") String height, @Query("age") String age, @Query("gender") String gender, @Query("goals") String goals,
                                        @Query("level") String level, @Query("profileImage") String profileImage, @Query("paymentMethod") String paymentMethod,
                                        @Query("comments") String comments, @Query("orderId") String orderId, @Query("orderRef") String orderRef,
                                        @Query("orderStatus") String orderStatus, @Query("transactionDate") String transactionDate, @Query("price") String price, @Query("duration") String duration);

    //get packages
    @GET("stripe/get-plans")
    Call<PlansDataModel> getAllPackages(@Header("Authorization") String access_token, @Query("product_id") String productID);

    //get Current Subscription
    @POST("stripe/cancel-subscription")
    Call<SubscriptionModel> cancelSubscriptionPlan(@Header("Authorization") String access_token, @Query("user_id") String id,
                                                   @Query("subscription_id") String subscription_id);

    //Cancel Current Subscription
    @POST("stripe/get-current-plan")
    Call<SubscriptionModel> currentSubscriptionPlan(@Header("Authorization") String access_token,
                                                    @Query("subscription_id") String subscription_id);

    //getProfile
//    @GET("/user/getProfile/{id}")
//    Call<getProfileResponseModel> getProfileData(@Path("id")String id);

    //Update Profile Data

    @POST("user/updateProfile")
    Call<SignupResponse> updateProfileData(@Header("Authorization") String access_token, @Query("user_id") String id, @Query("weight") String weight,
                                           @Query("height") String height, @Query("age") String age,
                                           @Query("gender") String gender,
                                           @Query("goal_id") int goal_id, @Query("level_id") int level_id,
                                           @Query("unit") String unitType, @Query("username") String name,
                                           @Query("email") String email, @Query("user_image") String profileImage, @Query("food_preference") String foodPreference);

    @POST("user/signup/updateProfile")
    Call<SignupResponse> updateProfileAtSignUp(@Header("Authorization") String access_token, @Query("user_id") String id, @Query("weight") String weight,
                                           @Query("height") String height, @Query("age") String age,
                                           @Query("gender") String gender,
                                           @Query("goal_id") String goal_id, @Query("level_id") String level_id,
                                           @Query("unit") String unitType, @Query("username") String name,
                                           @Query("user_image") String profileImage, @Query("food_preference") String foodPreference);

    //Activate Profile Call
    @POST("user/updateProfile")
    Call<SignupResponse> profileActivation(@Header("Authorization") String access_token, @Query("user_id") String id,
                                           @Query("weight") String weight, @Query("height") String height,
                                           @Query("age") String age,
                                           @Query("gender") String gender, @Query("goal_id") int goal_id,
                                           @Query("level_id") int level_id,
                                           @Query("unit") String unitType, @Query("user_image") String profileImage,
                                           @Query("food_preference") String foodPreference);
/*    @PUT("/user/updateProfile/{id}")
    Call<LoginResponse> updateProfileData(@Path("id")String id,
                                          @Query("weight") String weight,
                                          @Query("name") String name,
                                          @Query("email") String email,
                                          @Query("height") String height,
                                          @Query("age") String age,
                                          @Query("profileImage") String profileImage);*/

    //coaches and their related Daily workouts API's are list Below
    //Coaches profile Data API
    @GET("coaches/daily")
    Call<CoachesProfileDataModel> getCoachesProfileData(@Header("Authorization") String access_token, @Query("user_id")
            String userID, @Query("goal_id") Integer goal_id, @Query("level_id") Integer level_id,
                                                        @Query("week") Integer week, @Query("day") Integer day);


    @GET("workouts/daily-workouts")
    Call<WorkoutDataModel> getAllWorkoutsByCoach(@Header("Authorization") String access_token, @Query("coach_id") Integer coach_id, @Query("user_id") String userID,
                                                 @Query("day") Integer day,
                                                 @Query("week") Integer week,
                                                 @Query("offset") String offset);

    @GET("workouts/workout-details")
    Call<CoachesDataModel> getVideoByWorkoutID(@Header("Authorization") String access_token, @Query("workout_id") Integer workoutID);

    @PUT("shopping-list/un-check/all")
    Call<ChecklistModel> unCheckAllCheckedItems(@Header("Authorization") String access_token);

    @PUT("user/update-language")
    Call<UpdateLanguage> changeLanguage(@Header("Authorization") String access_token, @Query("lang") String language);

    //generate New Token
    @POST("user/token")
    Call<SignupResponse> getNewToken(@Header("Authorization") String refresh_token, @Query("user_id") String id);

    //get Profile Data
    @POST("user/getProfile")
    Call<SignupResponse> getProfileData(@Header("Authorization") String access_token, @Query("user_id") String id);

    @POST("visualizations")
        //on below line we are creating a method to post our data.
    Call<VisualizationResponse> VisualizationDataPost(
            @Header("Authorization") String access_token,
            @Query("day") Integer day, @Query("level_id") Integer level_id,
            @Query("goal_id") Integer goal_id, @Query("week") Integer week);

    @POST("visualizations")
        //on below line we are creating a method to post our data.
    Call<VisualizationModel> getVisualizationForDashboard(
            @Header("Authorization") String access_token,
            @Query("day") Integer day, @Query("level_id") Integer level_id,
            @Query("goal_id") Integer goal_id, @Query("week") Integer week);

    @POST("recipes")
    Call<NutritionDataModel> nutritionDataPost(@Header("Authorization") String access_token,
                                               @Query("day") Integer day, @Query("level_id") Integer level_id,
                                               @Query("goal_id") Integer goal_id, @Query("week") Integer week);

    @Headers({"Accept: application/vnd.cedrics.v3+json"})
    @POST("recipe")
    Call<SingleRecipeDataModel> nutritionSingleRecipeDataV3(@Header("Authorization") String access_token,
                                                          @Query("recipe_id") Integer recipe_id);

    @POST("recipe")
    Call<SingleRecipeDataModel> nutritionSingleRecipeData(@Header("Authorization") String access_token,
                                                          @Query("recipe_id") Integer recipe_id);

    @POST("recipes/dashboard")
    Call<DashboardNutrition> dashboardNutrition(@Header("Authorization") String access_token, @Query("level_id") Integer level_id,
                                                @Query("goal_id") Integer goal_id, @Query("day") Integer day, @Query("week") Integer week, @Query("food_preference_id") String foodPreferenceID);

    @Headers({"Accept: application/vnd.cedrics.v3+json"})
    @GET("recipe-list")
    Call<DashboardNutritionPagerModel> dashboardPagerNutritionV3(@Header("Authorization") String access_token,
                                                               @Query("day") Integer day, @Query("week") Integer week);

    @GET("recipe-list")
    Call<DashboardNutritionPagerModel> dashboardPagerNutrition(@Header("Authorization") String access_token,
                                                                 @Query("day") Integer day, @Query("week") Integer week);


    //get Programs
    @GET("workouts/programs")
    Call<ProgramsDataModel> getAllPrograms(@Header("Authorization") String access_token);

    //get Weekly programs
    @GET("workouts/weekly-workouts")
    Call<ProgressDataModel> getWeeklyPrograms(@Header("Authorization") String access_token,
                                              @Query("user_id") Integer userID, @Query("program_id") Integer programID,
                                              @Query("level_id") Integer levelID, @Query("goal_id") Integer goalID,
                                              @Query("week") Integer week, @Query("day") Integer day);

    //Programs Progress
    @POST("programInformation/getData")
    //on below line we are creating a method to post our data.
    Call<BestProgramModel> programProgressDataPost(
            @Query("programId") Integer programId, @Query("week") Integer week, @Query("day") String day);

    //Decode Token
    @GET("user")
    Call<SignupResponse> infoFromToken(@Header("Authorization") String access_token);

    @GET("goals")
    Call<GoalModel> getGoals(@Header("Authorization") String access_token);

    @GET("food-preferences")
    Call<FoodPreferencesModel> getFoodPreferences(@Header("Authorization") String access_token);

    @GET("allergies")
    Call<AllergyModel> getAllergies(@Header("Authorization") String access_token, @Query("food_preference_id") int foodPreferenceID);


    // Stripe Payment Call
    @POST("stripe/create-subscription")
    Call<SignupResponse> paymentCall(@Header("Authorization") String access_token, @Query("name") String name,
                                     @Query("email") String email, @Query("number") String number,
                                     @Query("exp_month") Integer month, @Query("exp_year") Integer year,
                                     @Query("cvc") String cvc, @Query("plan") String planId);

    //Save recipe into Shopping list
    @POST("shopping-list/save")
    Call<NutritionDataModel> saveRecipeToList(@Header("Authorization") String access_token,
                                              @Query("user_id") String id, @Query("recipe") String recipe_ids);

    //Get Shopping list
    @GET("shopping-list/getList")
    Call<NutritionDataModel> getShoppingList(@Header("Authorization") String access_token, @Query("user_id") String userID);
    /* @Query("user_id") String id)*/

    @PUT("shopping-list/update")
    Call<ChecklistModel> updateShoppingList(@Header("Authorization") String access_token, @Query("user_id") String userID,
                                            @Query("recipe") String recipe);


    //check item list APi
    @PUT("shopping-list/check")
    Call<ChecklistModel> checkShoppingListItem(@Header("Authorization") String access_token, @Query("user_id") String userID,
                                               @Query("recipe") String recipe);

    @PUT("shopping-list/check-uncheck")
    Call<CheckedUncheckedResponseModel> checkedUncheckedShoppingListItem(@Header("Authorization") String access_token, @Query("user_id") String userID,
                                                                         @Query("recipe") String recipe);

    //uncheck item list APi
    @PUT("shopping-list/un-check")
    Call<ChecklistModel> unCheckShoppingListItem(@Header("Authorization") String access_token, @Query("user_id") String userID,
                                                 @Query("recipe") String recipe);

    //Delete Shopping list
    @DELETE("shopping-list/delete")
    Call<NutritionDataModel> deleteShoppingList(@Header("Authorization") String access_token, @Query("recipe_ids") String recipe_ids);

    @DELETE("shopping-list/delete/all")
    Call<ChecklistModel> deleteAllShoppingList(@Header("Authorization") String access_token);

    @POST("workouts/daily/save-progress")
    Call<SignupResponse> sendWatchedVideoToServer(@Header("Authorization") String access_token,
                                                  @Query("user_id") Integer id, @Query("coach_id") Integer coach_id,
                                                  @Query("workout_id") Integer workout_id, @Query("day") Integer day,
                                                  @Query("week") Integer week);

    @GET("workouts/program/details")
    Call<DaysUnlockModel> getUnlockDaysFromApi(@Header("Authorization") String access_token,
                                               @Query("user_id") Integer id,
                                               @Query("program_id") Integer program_id);


    @POST("workouts/weekly/save-progress")
    Call<SignupResponse> sendWatchedVideosToServer(@Header("Authorization") String access_token,
                                                   @Query("user_id") Integer id, @Query("program_id") Integer program_id,
                                                   @Query("level_id") Integer level_id, @Query("goal_id") Integer goal_id, @Query("day") Integer day,
                                                   @Query("week") Integer week, @Query("watched_count") Integer watched_count);

    @POST("v1/geolocate")
    Call<LocationModel> getLocation(@Query("key") String googleAPI_Key);

    @POST("user/login-details")
    Call<UserDetailModel> createUserDetails(@Header("Authorization") String access_token,
                                            @Query("agent") String agent, @Query("os") String OS, @Query("device_id") String device_id,
                                            @Query("location") String location, @Query("timezone") String timezone, @Query("fcm_id")
                                            String fcm_id, @Query("is_cancel") Boolean is_cancel,
                                            @Query("version_no") String versionNo, @Query("build_no") String buildNo, @Query("os_type") String os_type);

    @PUT("user/login-details")
    Call<UserDetailModel> updateUserDetails(@Header("Authorization") String access_token, @Query("location") String location, @Query("timezone") String timezone);

    @GET("user/user-status")
    Call<UserStatusModel> getUserStatus(@Header("Authorization") String access_token);

    @POST("user/user-allergies")
    Call<AllergyModelForRegistration> saveAllergies(@Header("Authorization") String access_token, @Query("allergies") String allergies);

    @GET("/v2/user/get-activities")
    Call<ActivitiesModel> getUserActivity(@Header("Authorization") String access_token, @Query("sw") String startDate);

    @GET("/v2/user/get-activities")
    Call<ActivitiesModel> getUserActivities(@Header("Authorization") String access_token, @Query("sw") String startDate, @Query("ew") String endDate);

    @GET("user/get-analytics")
    Call<AnalyticsModel> getUserAnalytic(@Header("Authorization") String access_token, @Query("sw") String startDate, @Query("ew") String endDate, @Query("activity_type") String activityType);

    @GET("user/get-analytics")
    Call<AnalyticsModel> getUserAnalytic(@Header("Authorization") String access_token, @Query("activity_type") String activityType, @Query("year") String year);

    @POST("/v2/user/create-activity")
    Call<StepCountModel> createNewUserActivity(@Header("Authorization") String access_token, @Query("steps_count") String stepCounts, @Query("water_count") String waterIntake,
                                               @Query("distance") String distance, @Query("user_time_zone") String timeZone,
                                               @Query("calories") String calories, @Query("activity_lat") String latitude,
                                               @Query("activity_long") String longitude, @Query("activity_location") String location,
                                               @Query("user_activity_date") String activityDate);

    @POST("/v2/user/create-activities")
    Call<StepCountModel> createPastActivities(@Header("Authorization") String access_token, @Query("no_of_days") String noOfDays, @Query("steps_count[]") String[] stepCounts,
                                              @Query("water_count[]") String[] waterCounts, @Query("distance[]") String[] distance, @Query("user_time_zone[]") String[] timeZones,
                                              @Query("calories[]") String[] calories, @Query("activity_lat[]") String[] latitude, @Query("activity_long[]") String[] lng,
                                              @Query("activity_location[]") String[] locations, @Query("user_activity_date[]") String[] activityDates);

    @PUT("/v2/user/update-activity")
    Call<StepCountModel> updateUserActivity(@Header("Authorization") String access_token, @Query("steps_count") String stepCounts, @Query("water_count") String waterIntake,
                                               @Query("distance") String distance, @Query("user_time_zone") String timeZone,
                                               @Query("calories") String calories,@Query("activity_lat") String latitude,
                                               @Query("activity_long") String longitude, @Query("activity_location") String location,
                                               @Query("user_activity_date") String activityDate);


    @POST("/v2/stripe/create-payment")
    Call <StripeIntent> createPaymentIntent(@Header("Authorization") String accessToken,
    @Query("amount") int amount, @Query("currency") String currency, @Query("payment_method") String paymentMethod, @Query("plan_id") String planID, @Query("is_plan_changed") boolean isSelectedPlanChanged);


    @POST("/v2/stripe/create-payment-setup")
    Call <StripeIntent> createSetupIntent(@Header("Authorization") String accessToken,
                                          @Query("amount") int amount, @Query("currency") String currency, @Query("payment_method") String paymentMethod, @Query("plan_id") String planID, @Query("is_plan_changed") boolean isSelectedPlanChanged);

    @POST("/v1/customers")
    Call <StripeCustomerModel> createStripeCustomer(@Header("Authorization") String token,
                                                     @Query("email") String email, @Query("name") String name);

    @PUT("/v2/user/logout")
    Call<LogoutModel> logout(@Header("Authorization") String token);
                                            /*@Query("distance") String distance, @Query("user_time_zone") String timeZone,
                                            @Query("calories") String calories, @Query("activity_lat") String latitude,
                                            @Query("activity_long") String longitude, @Query("activity_location") String location,
                                            @Query("user_activity_date") String activityDate);*/


    @POST("create-logs")
    Call<LogsDataModel>getLogsDetailsLogIssue(@Query("title") String title, @Query("email") String email, @Query("type") String type, @Query("message") String message);
    @DELETE("/v2/user/delete")
    Call<LogoutModel> deleteAccount(@Header("Authorization") String token);

    @POST("/v2/stripe/get-active-product-plans")
    Call<ProductModel> getAllProductsBySubscriptionIDAndProductID(@Header("Authorization") String token,@Query("subscription_id") String subscriptionID, @Query("product_id") String productID);

    @POST("/v2/stripe/choose-plan")
    Call<ChangeSubscriptionModel> getChosenPlanDetails(@Header("Authorization") String token, @Query("plan_id") String planID);

    @PUT("/v2/stripe/upgrade-subscription")
    Call<UpgradeDowngradeSubscriptionModel> upgradeOrDowngradeSubscription(@Header("Authorization") String token,@Query("subscription_id") String subscriptionID, @Query("plan_id") String planID/*, @Query("goal_id") String goalID*/);

    @PUT("/v2/stripe/update-to-reactivate")
    Call<UnsubscribeLaterModel> unsubscribeLaterSubscription(@Header("Authorization") String token, @Query("subscription_id") String subscriptionID, @Query("plan_id") String planID);

    @PUT("stripe/re-subscribe")
    Call<ResubscribeModel> resubscribe(@Header("Authorization") String token, @Query("subscription_id") String subscriptionID, @Query("plan_id") String planID);

    @GET("user/get-goals")
    Call<PlanModel> getPlans(@Header("Authorization") String access_token);

    @PUT("stripe/change-goal")
    Call<ChangePlanModel> changePlan(@Header("Authorization") String access_token, @Query("subscription_id") String subscriptionID, @Query("product_id") String productID, @Query("goal_id") String planID);

    @POST("user/get-levels")
    Call<LevelModel> getUserLevel(@Header("Authorization") String access_token, @Query("goal_id") String goalID);

}
