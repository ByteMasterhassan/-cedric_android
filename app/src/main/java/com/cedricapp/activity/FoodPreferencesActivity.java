package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.adapters.FoodPreferenceAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.DialogInterface;
import com.cedricapp.interfaces.FoodPreferenceClickListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.FoodPreferencesModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.CustomDialogUtil;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodPreferencesActivity extends AppCompatActivity implements FoodPreferenceClickListener, DialogInterface {
    ImageButton pfBackBtn;
    RecyclerView foodPreferenceRecyclerview;
    String userLevel, userGoals, height, weight,
            gender, age, unitType, id, email, name, refresh_token, token, product_id, profile_image;
    int level_id, goal_id;
    BlurView blurView;
    LottieAnimationView loading_lav;
    SegmentedProgressBar fpSegmented_progressbar;
    FoodPreferencesModel.Datum foodPreference;
    MaterialButton btnFoodPreferencesNext;
    Call<SignupResponse> updateFoodPrefCall;
    DBHelper dbHelper;
    Intent intent;
    private String message;

    boolean isComingFromLogin;

    MaterialTextView textViewFoodPreferences, loader_mtv;

    Resources resources;

    String TAG = "FOOD_PREFERENCE_ACTIVITY_TAG";

    int DELAY = 2000;


    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            //showing dialog and then closing the application..
            // showDialog();
            if (intent.hasExtra("From_setting")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    SessionUtil.setLoadHomeData(true, getApplicationContext());
                    Intent intent1 = new Intent(FoodPreferencesActivity.this,
                            HomeActivity.class);
                    intent1.putExtra("coming_from", "settings");
                    startActivity(intent1);
                }else{
                    finish();
                }
            } else {
                // onBackPressed();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_preferences);
        //resources = Localization.setLanguage(FoodPreferencesActivity.this, getResources());
        resources = getResources();
        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }


    }

    void init() {
        //initialize backBtn
        pfBackBtn = findViewById(R.id.pfBackBtn);
        foodPreferenceRecyclerview = findViewById(R.id.foodPreferenceRecyclerview);
        fpSegmented_progressbar = (SegmentedProgressBar) findViewById(R.id.fpSegmented_progressbar);
        btnFoodPreferencesNext = findViewById(R.id.btnFoodPreferencesNext);
        loader_mtv = findViewById(R.id.loader_mtv);
        blurView = findViewById(R.id.blurView);
        loading_lav = findViewById(R.id.loading_lav);
        textViewFoodPreferences = findViewById(R.id.textViewFoodPreferences);
        dbHelper = new DBHelper(getApplicationContext());
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        fpSegmented_progressbar.setCompletedSegments(5);

        textViewFoodPreferences.setText(resources.getString(R.string.which_meal_plan_do_nyou_want_to_follow));

        pfBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getIntentData();
        btnFoodPreferencesNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFoodPreferencesNext.startAnimation(myAnim);
                if (btnFoodPreferencesNext.getText().toString().equals(resources.getString(R.string.update))) {
                    if (foodPreference != null) {
                        intent = getIntent();
                        if (intent.hasExtra("From_setting")) {
                            CustomDialogUtil.showDialog(FoodPreferencesActivity.this, "", resources.getString(R.string.do_you_proceed), FoodPreferencesActivity.this);
                        } else {
                            activeProfile(SessionUtil.getUserID(getApplicationContext()),
                                    SessionUtil.getUserWeight(getApplicationContext()),
                                    SessionUtil.getUserHeight(getApplicationContext()),
                                    SessionUtil.getUserAge(getApplicationContext()),
                                    SessionUtil.getUserGender(getApplicationContext()),
                                    Integer.parseInt(SessionUtil.getUserGoalID(getApplicationContext())),
                                    Integer.parseInt(SessionUtil.getUserLevelID(getApplicationContext())),
                                    SessionUtil.getUserUnitType(getApplicationContext()), name,
                                    String.valueOf(foodPreference.getId()));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.select_your_favourite_food), Toast.LENGTH_SHORT).show();
                    }


                } else if (foodPreference != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {

                        SessionUtil.SetFoodPreferenceID(getApplicationContext(), String.valueOf(foodPreference.getId()));
                        SessionUtil.RemoveAllergiesId(getApplicationContext());
                        SessionUtil.removeAllergiesNames(getApplicationContext());

                        loadNextActivity();

                        /*activeProfile(id, weight, height, age, gender, goal_id, level_id, unitType, name, "" + foodPreference.getId());*/
                    } else {
                        if (getApplicationContext() != null) {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.select_your_favourite_food), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (intent.hasExtra("From_setting")) {
                        startActivity(new Intent(FoodPreferencesActivity.this, HomeActivity.class));
                    }
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/
    }

    private void getIntentData() {
        try {
            intent = getIntent();
            if (intent.hasExtra("From_setting")) {
                btnFoodPreferencesNext.setText(resources.getString(R.string.update));
                fpSegmented_progressbar.setVisibility(View.INVISIBLE);
                token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            } else {
                btnFoodPreferencesNext.setText(resources.getString(R.string.next));
            }
            userLevel = intent.getStringExtra(Common.SESSION_USER_LEVEL);
            height = intent.getStringExtra(Common.SESSION_USER_HEIGHT);
            weight = intent.getStringExtra(Common.SESSION_USER_WEIGHT);
            age = intent.getStringExtra(Common.SESSION_USER_AGE);
            gender = intent.getStringExtra(Common.SESSION_USER_GENDER);
            unitType = intent.getStringExtra(Common.SESSION_UNIT_TYPE);
            id = intent.getStringExtra(Common.SESSION_USER_ID);
            email = intent.getStringExtra(Common.SESSION_EMAIL);
            name = intent.getStringExtra(Common.SESSION_USERNAME);
            token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
            level_id = intent.getIntExtra(Common.SESSION_USER_LEVEL_ID, 0);
            goal_id = intent.getIntExtra(Common.SESSION_USER_GOAL_ID, 0);
            product_id = intent.getStringExtra(Common.SESSION_USER_PRODUCT_ID);
            userGoals = intent.getStringExtra(Common.SESSION_USER_GOAL);
            if (intent.hasExtra(Common.SESSION_COMING_FROM)) {
                pfBackBtn.setVisibility(View.GONE);
                isComingFromLogin = true;
            }
            if (!intent.hasExtra("From_setting")) {
                SessionUtil.SetFoodPreferenceID(getApplicationContext(), "");
            }
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                getFoodPreferences();
            } else {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }

    }

    private void activeProfile(String id, String weight, String height, String age, String gender, int goal_id, int level_id, String unitType, String name, String foodPreference) {
        blurBackground();
        startLoading();
        Call<SignupResponse> call = ApiClient.getService().updateProfileAtSignUp("Bearer " + token, id, weight, height, age, gender,
                "" + goal_id, "" + level_id, unitType, name, "", foodPreference);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if (response.isSuccessful()) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    final SignupResponse activationResponse = response.body();
                    if (intent.hasExtra("From_setting")) {

                        new Handler().postDelayed(() -> {
                            loader_mtv.setText(resources.getString(R.string.adding_your_updated_diet_plan));
                            dbHelper.clearDB();
                            new Handler().postDelayed(() -> {

                                loader_mtv.setText(resources.getString(R.string.revamped_diet_plan));
                                new Handler().postDelayed(() -> {

                                    if (Common.isLoggingEnabled) {
                                        if (activationResponse != null)
                                            Log.d(TAG, "Allergies Activity: " + activationResponse.toString());
                                        else
                                            Log.e(TAG, "Allergies are null ");
                                    }
                                    //dbHelper.addUser(activationResponse);
                                    if (activationResponse != null) {
                                        dbHelper.updateUserProfile(activationResponse);
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "Activation Response is null");
                                        }
                                    }

                                    SessionUtil.SetFoodPreferenceID(getApplicationContext(), foodPreference);
                                    loader_mtv.setText(resources.getString(R.string.update_completed_explore_dashboard));
                                    new Handler().postDelayed(() -> {

                                        Intent intent = new Intent(FoodPreferencesActivity.this, HomeActivity.class);
                                        if (Common.isLoggingEnabled)
                                            Log.e(TAG, "Loader stopped when redirecting to Bottom Navigation Bar");

                                        intent.putExtra("show_success_dialog",true);
                                        stopLoading();
                                        startActivity(intent);
                                    }, DELAY);

                                }, DELAY);
                            }, DELAY);
                        }, DELAY);

                    } else {
                   /* message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null) {
                            Log.d(TAG, "Response Status: " + message.toString());
                        }
                    }*/
                        if (Common.isLoggingEnabled) {
                            if (activationResponse != null)
                                Log.d(TAG, "Allergies Activity: " + activationResponse.toString());
                            else
                                Log.e(TAG, "Allergies are null ");
                        }
                        //dbHelper.addUser(activationResponse);
                        if (activationResponse != null) {
                            dbHelper.updateUserProfile(activationResponse);
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Activation Response is null");
                            }
                        }
                    }

                    /*Toast.makeText(FoodPreferencesActivity.this, activationResponse.getMessage().toString(),
                            Toast.LENGTH_SHORT).show();*/


                } else {
                    try {
                        Gson gson = new GsonBuilder().create();
                        SignupResponse profileJSON_Response = new SignupResponse();
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.d(TAG, "Response Status: " + message.toString());
                            }
                        }

                        profileJSON_Response = gson.fromJson(response.errorBody().string(), SignupResponse.class);
                        if (response.code() == 400) {
                            showMessage(profileJSON_Response);

                        } else if (response.code() == 401) {
                            LogoutUtil.redirectToLogin(FoodPreferencesActivity.this);
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        } else {
                            showMessage(profileJSON_Response);
                        }

                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("Food_Preference_activate_API", email
                                        , EXCEPTION, SharedData.caughtException(ex));
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                    }
                    stopLoading();
                }
            }


            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Food_Preference_activateAPI", email
                                , EXCEPTION, SharedData.throwableObject(t));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
    }

    void showMessage(SignupResponse profileJSON_Response) {
        if (profileJSON_Response != null) {
            if (profileJSON_Response.getErrors() != null) {
                if (profileJSON_Response.getErrors().getAge() != null) {
                    if (profileJSON_Response.getErrors().getAge().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getAge().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getHeight() != null) {
                    if (profileJSON_Response.getErrors().getHeight().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getHeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getWeight() != null) {
                    if (profileJSON_Response.getErrors().getWeight().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getWeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getGender() != null) {
                    if (profileJSON_Response.getErrors().getGender().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getGender().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getUnit() != null) {
                    if (profileJSON_Response.getErrors().getUnit().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getUnit().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getGoal_id() != null) {
                    if (profileJSON_Response.getErrors().getGoal_id().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getGoal_id().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getLevel_id() != null) {
                    if (profileJSON_Response.getErrors().getLevel_id().size() > 0) {
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getErrors().getLevel_id().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (profileJSON_Response.getMessage() != null)
                        Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                if (Common.isLoggingEnabled) {
                    if (profileJSON_Response.getMessage() != null) {
                        Log.e(TAG, "profileJSON_Response getMessage(): " + profileJSON_Response.getMessage().toString());
                    } else {
                        Log.e(TAG, "profileJSON_Response getMessage() is null ");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "profileJSON_Response getErrors() is null");
                }
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Food_Preference_Activate_API", email
                                , EXCEPTION, "profileJSON_Response getErrors() is null");
                if (profileJSON_Response.getMessage() != null) {
                    Toast.makeText(FoodPreferencesActivity.this, profileJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodPreferencesActivity.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(FoodPreferencesActivity.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "profileJSON_Response is null");
            }
        }
    }

    void loadNextActivity() {
        Intent intent = new Intent(FoodPreferencesActivity.this,
                AllergiesActivity.class);

        intent.putExtra(Common.SESSION_USER_LEVEL, userLevel);
        intent.putExtra(Common.SESSION_USER_LEVEL_ID, level_id);
        intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, foodPreference.getId());
        intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE, foodPreference.getTitle());
        intent.putExtra(Common.SESSION_USER_GOAL_ID, goal_id);
        intent.putExtra(Common.SESSION_USER_GOAL, userGoals);
        intent.putExtra(Common.SESSION_USER_HEIGHT, height);
        intent.putExtra(Common.SESSION_USER_WEIGHT, weight);
        intent.putExtra(Common.SESSION_USER_AGE, age);
        intent.putExtra(Common.SESSION_USER_GENDER, gender);
        intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
        intent.putExtra(Common.SESSION_USER_ID, id);
        intent.putExtra(Common.SESSION_EMAIL, email);
        intent.putExtra(Common.SESSION_USERNAME, name);
        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
        intent.putExtra(Common.SESSION_USER_PRODUCT_ID, product_id);
        intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);

        //segmentedProgressBar.incrementCompletedSegments();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "------------In Food Preference Activity Add to bundle to pass activity------------");
            Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
            Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
            Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
            Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
            Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
            Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreference.getId());
            Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference.getTitle());
            Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
            Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
            Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
            Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoals);
            Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goal_id);
            Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
            Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
            Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
        }
        startActivity(intent);
    }

    void getFoodPreferences() {
        blurBackground();
        startLoading();
        Call<FoodPreferencesModel> foodPreferencesModelCall = ApiClient.getService().getFoodPreferences("Bearer " + token);
        foodPreferencesModelCall.enqueue(new Callback<FoodPreferencesModel>() {
            @Override
            public void onResponse(@NonNull Call<FoodPreferencesModel> call, @NonNull Response<FoodPreferencesModel> response) {
                stopLoading();
                if (response.isSuccessful()) {
                    setAdapter(response.body());
                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(FoodPreferencesActivity.this);
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        if (response.errorBody() != null) {
                            errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                            if (errorMessageModel.getError() != null) {
                                Toast.makeText(getApplicationContext(), errorMessageModel.getError(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("Food_Preference_getFood_Preference", email
                                        , EXCEPTION, SharedData.caughtException(ex));
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FoodPreferencesModel> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Food_Preference_getFood_Preference_API", email
                                , EXCEPTION, SharedData.throwableObject(t));
                stopLoading();
                Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setAdapter(FoodPreferencesModel foodPreferencesModel) {
        if (foodPreferencesModel != null) {
            if (foodPreferencesModel.getData() != null) {
                foodPreferenceRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                FoodPreferenceAdapter foodPreferenceAdapter;
                if (intent.hasExtra("From_setting")) {
                    foodPreferenceAdapter = new FoodPreferenceAdapter(getApplicationContext(), (ArrayList<FoodPreferencesModel.Datum>) foodPreferencesModel.getData(), FoodPreferencesActivity.this, "From_setting");
                } else {
                    foodPreferenceAdapter = new FoodPreferenceAdapter(getApplicationContext(), (ArrayList<FoodPreferencesModel.Datum>) foodPreferencesModel.getData(), FoodPreferencesActivity.this, "registration");
                }
                foodPreferenceRecyclerview.setAdapter(foodPreferenceAdapter);
                // foodPreferenceAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (intent.hasExtra("From_setting")) {
            //SessionUtil.setLoadHomeData(true, getApplicationContext());
            Intent intent1 = new Intent(FoodPreferencesActivity.this,
                    HomeActivity.class);
            intent1.putExtra("coming_from", "settings");
            startActivity(intent1);

        } else {
            super.onBackPressed();
            /*if (!isComingFromLogin) {
                super.onBackPressed();
            }*/
        }


    }

    private void blurBackground() {
        blurView.setVisibility(View.VISIBLE);

        float radius = 1f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
    }

    private void startLoading() {
        //dissable user interaction
        FoodPreferencesActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        FoodPreferencesActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    @Override
    public void fPClickListener(FoodPreferencesModel.Datum foodPreference) {
        this.foodPreference = foodPreference;
    }

    @Override
    public void onClickedYes() {
        loader_mtv.setText(resources.getString(R.string.clearing_previous_data));
        activeProfile(SessionUtil.getUserID(getApplicationContext()),
                SessionUtil.getUserWeight(getApplicationContext()),
                SessionUtil.getUserHeight(getApplicationContext()),
                SessionUtil.getUserAge(getApplicationContext()),
                SessionUtil.getUserGender(getApplicationContext()),
                Integer.parseInt(SessionUtil.getUserGoalID(getApplicationContext())),
                Integer.parseInt(SessionUtil.getUserLevelID(getApplicationContext())),
                SessionUtil.getUserUnitType(getApplicationContext()), name,
                String.valueOf(foodPreference.getId()));

    }

    @Override
    public void onClickedNo() {

    }
}