package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.adapters.AllergyAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.AllergyClickListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.AllergyModel;
import com.cedricapp.model.AllergyModelForRegistration;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.fragment.PaymentCategory;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllergiesActivity extends AppCompatActivity implements AllergyClickListener {
    ImageButton allergyBackBtn;
    ImageView background;
    SegmentedProgressBar allergySegmentedProgressbar;
    RecyclerView allergyRecyclerview;
    String userLevel, userGoals, height, weight, foodPreference,
            gender, age, unitType, id, email, name, refresh_token, token, product_id;
    int level_id, goal_id, preferenceID;
    BlurView blurView;
    LottieAnimationView loading_lav;
    MaterialButton btnAllergiesNext;
    ArrayList<Integer> allergyIDs;
    ArrayList<String> allergyNames;
    AllergyModel.Datum allergyModel;
    Call<SignupResponse> updateAllergiesCall;
    Intent intent;
    private String message;

    public static String comeFrom;

    String subscriptionID;

    MaterialTextView textViewAllergies, loader_mtv;

    Resources resources;

    String TAG = "ALLERGIES_TAG";

    int DELAY = 2000;

    boolean isAllergiesUpdated;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            //showing dialog and then closing the application..
            // showDialog();
            if (intent.hasExtra("From_setting")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    SessionUtil.setLoadHomeData(true, getApplicationContext());
                    Intent intent1 = new Intent(AllergiesActivity.this,
                            HomeActivity.class);
                    intent1.putExtra("coming_from", "settings");
                    startActivity(intent1);
                }
            } else {
                // onBackPressed();
                finish();
            }/*
            if(!isAllergiesUpdated){
                //when user not pressed updated button and move back to screen then share preference allergies will be removed
                SessionUtil.setAllergiesId(getApplicationContext(),"");
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);
        init();

        // adding onbackpressed callback listener.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }

    }

    void init() {
        try {
            //resources = Localization.setLanguage(AllergiesActivity.this, getResources());
            resources = getResources();
            //initialize backBtn
            allergyBackBtn = findViewById(R.id.allergyBackBtn);
            allergyRecyclerview = findViewById(R.id.allergiesRecyclerview);
            allergySegmentedProgressbar = (SegmentedProgressBar) findViewById(R.id.allergySegmented_progressbar);
            btnAllergiesNext = findViewById(R.id.btnAllergiesNext);
            blurView = findViewById(R.id.blurView);
            loading_lav = findViewById(R.id.loading_lav);
            background = findViewById(R.id.allergiesbackground);
            textViewAllergies = findViewById(R.id.textViewAllergies);
            loader_mtv = findViewById(R.id.loader_mtv);
            allergyIDs = new ArrayList<>();
            allergyNames = new ArrayList<>();

            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));


            textViewAllergies.setText(resources.getString(R.string.allergies_you_have));

            allergySegmentedProgressbar.setCompletedSegments(6);
            try {
                if (!SessionUtil.getAllergiesId(getApplicationContext()).matches("")) {
                    String[] ids = SessionUtil.getAllergiesId(getApplicationContext()).split(",");
                    for (int i = 0; i < ids.length; i++) {
                        allergyIDs.add(Integer.parseInt(ids[i]));
                    }

                }
            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }

            allergyBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getIntentData();
            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            btnAllergiesNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnAllergiesNext.startAnimation(myAnim);
                    if (btnAllergiesNext.getText().toString().equals(resources.getString(R.string.update))) {
                        loader_mtv.setText(resources.getString(R.string.clearing_previous_data));
                        String allergyIDsString = TextUtils.join(",", allergyIDs);
                        String allergyNamesString = TextUtils.join(",", allergyNames);
                        Log.d(TAG, "Allergy IDs: " + allergyIDsString);
                        Log.d(TAG, "Allergy : " + allergyNamesString);

                        saveAllergies(allergyIDsString, allergyNamesString);
                        /*if (SessionUtil.getIntegerI(getApplicationContext()) == 0) {
                            Toast.makeText(AllergiesActivity.this, getResources().getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                        } else {
                            saveAllergies(allergyIDsString, allergyNamesString);
                            int i = 0;
                            SessionUtil.setIntegerI(getApplicationContext(), i);
                        }*/

                    } else {

                        //loadNextActivity(allergiesString);
                        //loadNextActivity(allergiesString);
                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                            //activeProfile(id, weight, height, age, gender, goal_id, level_id, unitType, "" + preferenceID, allergyIDsString, allergyNamesString);
                            /*if (allergyIDsString.matches("")) {
                                loadNextActivity("", "");
                            } else {
                                saveAllergies(allergyIDsString, allergyNamesString);
                            }*/
                            activeProfile(id, weight, height, age, gender, goal_id, level_id, unitType, name, "" + preferenceID);
                        } else {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {zz
                    if (intent.hasExtra("From_setting")) {
                        SessionUtil.setLoadHomeData(true, getApplicationContext());
                        startActivity(new Intent(AllergiesActivity.this, HomeActivity.class));
                    } *//*else {
                        AllergiesActivity.super.onBackPressed();
                    }*//*
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getIntentData() {
        try {
            intent = getIntent();
            comeFrom = "";
            if (intent.hasExtra("From_setting")) {
                btnAllergiesNext.setText(resources.getString(R.string.update));
                comeFrom = "From_setting";
                allergySegmentedProgressbar.setVisibility(View.INVISIBLE);
                //background.setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.allergies_bkg_2));
                //background.setBackgroundResource(R.drawable.allergies_bkg_2);
                background.setBackgroundResource(R.drawable.allergies_bkg_2);
                background.setScaleType(ImageView.ScaleType.CENTER);
                token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
                preferenceID = intent.getIntExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, 0);
            } else {
                btnAllergiesNext.setText(resources.getString(R.string.next));
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
            userGoals = intent.getStringExtra(Common.SESSION_USER_GOAL);
            product_id = intent.getStringExtra(Common.SESSION_USER_PRODUCT_ID);
            preferenceID = intent.getIntExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, 0);
            foodPreference = intent.getStringExtra(Common.SESSION_USER_FOOD_PREFERENCE);
            if (intent.hasExtra(Common.SESSION_SUBSCRIPTION_ID)) {
                subscriptionID = intent.getStringExtra(Common.SESSION_SUBSCRIPTION_ID);
            }
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------Allergies: Get data from bundle------------");
                //Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + allergies);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + preferenceID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + preferenceID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoals);
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goal_id);
                Log.d(TAG, Common.SESSION_USER_LEVEL_ID + ": " + level_id);
                Log.d(TAG, Common.SESSION_USER_LEVEL + ": " + userLevel);
                Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);

            }
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                getAllergies();
            } else {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
    }

    void loadNextActivity(String allergyIDs, String allergies) {
        try {
            Intent intent = new Intent(AllergiesActivity.this,
                    PaymentCategory.class);
            SessionUtil.setAllergiesId(getApplicationContext(), allergyIDs);
            intent.putExtra(Common.SESSION_USER_ALLERGY_IDS, allergyIDs);
            intent.putExtra(Common.SESSION_USER_ALLERGIES, allergies);
            intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, preferenceID);
            intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE, foodPreference);
            intent.putExtra(Common.SESSION_USER_GOAL_ID, goal_id);
            intent.putExtra(Common.SESSION_USER_GOAL, userGoals);
            intent.putExtra(Common.SESSION_USER_LEVEL_ID, level_id);
            intent.putExtra(Common.SESSION_USER_LEVEL, userLevel);
            intent.putExtra(Common.SESSION_USER_HEIGHT, height);
            intent.putExtra(Common.SESSION_USER_WEIGHT, weight);
            intent.putExtra(Common.SESSION_USER_AGE, age);
            intent.putExtra(Common.SESSION_USER_GENDER, gender);
            intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
            intent.putExtra(Common.SESSION_USER_ID, id);
            intent.putExtra(Common.SESSION_EMAIL, email);
            intent.putExtra(Common.SESSION_USER_PRODUCT_ID, product_id);
            intent.putExtra(Common.SESSION_USERNAME, name);
            intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
            intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);
            if (subscriptionID != null) {
                intent.putExtra(Common.SESSION_SUBSCRIPTION_ID, subscriptionID);
            }
            //segmentedProgressBar.incrementCompletedSegments();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Allergies Activity Add to bundle to pass activity------------");
                Log.d(TAG, Common.SESSION_USER_ALLERGY_IDS + ": " + allergyIDs);
                Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + allergies);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + preferenceID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + preferenceID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoals);
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goal_id);
                Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
                Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + SessionUtil.getAllergiesId(AllergiesActivity.this));
            }
            startActivity(intent);
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    void getAllergies() {
        blurBackground();
        startLoading();
        Call<AllergyModel> foodPreferencesModelCall = ApiClient.getService().getAllergies("Bearer " + token, preferenceID);
        foodPreferencesModelCall.enqueue(new Callback<AllergyModel>() {
            @Override
            public void onResponse(@NonNull Call<AllergyModel> call, @NonNull Response<AllergyModel> response) {
                try {
                    stopLoading();
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status: " + message.toString());
                        }
                        if (response.body() != null) {
                            setAdapter(response.body());
                        }
                    } else if (response.code() == 401) {
                        LogoutUtil.redirectToLogin(AllergiesActivity.this);
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new GsonBuilder().create();
                        ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Response Status: " + message.toString());
                        }
                        try {
                            if (response.errorBody() != null) {
                                errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                                if (errorMessageModel.getError() != null) {
                                    Toast.makeText(getApplicationContext(), errorMessageModel.getError(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                                if (message != null)
                                    Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            if (getApplicationContext() != null) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("AllergiesActivity",
                                                email, EXCEPTION,
                                                SharedData.caughtException(ex));
                            }
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AllergyModel> call, @NonNull Throwable t) {
                try {
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (getApplicationContext() != null) {
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("AllergiesActivity",
                                        email, EXCEPTION,
                                        SharedData.throwableObject(t));

                    }

                    stopLoading();
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    if (Common.isLoggingEnabled) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private void setAdapter(AllergyModel allergyModel) {
        try {
            if (allergyModel != null) {
                if (allergyModel.getData() != null) {
                    allergyRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    for (int i = 0; i < allergyModel.getData().size(); i++) {
                        if (allergyIDs != null) {
                            if (allergyIDs.contains(allergyModel.getData().get(i).getId())) {
                                allergyModel.getData().get(i).setChecked(true);
                            }
                        }

                    }
                    AllergyAdapter allergyAdapter = new AllergyAdapter(getApplicationContext(), allergyModel.getData(), AllergiesActivity.this);
                    allergyRecyclerview.setAdapter(allergyAdapter);
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    void saveAllergies(String allergyIDs, String allergyNames) {
        blurBackground();
        startLoading();
        Call<AllergyModelForRegistration> allergyModelCall = ApiClient.getService().saveAllergies("Bearer " + token, allergyIDs);
        allergyModelCall.enqueue(new Callback<AllergyModelForRegistration>() {
            @Override
            public void onResponse(Call<AllergyModelForRegistration> call, Response<AllergyModelForRegistration> response) {
                try {
                    if (response.isSuccessful()) {
                        AllergyModelForRegistration allergyModel = response.body();
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Response Status: " + message.toString());
                        }
                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        if (intent.hasExtra("From_setting")) {
                            new Handler().postDelayed(() -> {
                                loader_mtv.setText(resources.getString(R.string.adding_your_updated_diet_plan));
                                SessionUtil.setAllergiesId(getApplicationContext(), allergyIDs);
                                SessionUtil.setAllergiesName(getApplicationContext(), allergyNames);

                                new Handler().postDelayed(() -> {
                                    dbHelper.clearDB();
                                    loader_mtv.setText(resources.getString(R.string.revamped_diet_plan));
                                    new Handler().postDelayed(() -> {
                                        loader_mtv.setText(resources.getString(R.string.update_completed_explore_dashboard));
                                        // Toast.makeText(getApplicationContext(), resources.getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(() -> {
                                            Intent intent = new Intent(AllergiesActivity.this, HomeActivity.class);
                                            if (Common.isLoggingEnabled)
                                                Log.e(TAG, "Loader stopped when redirecting to Bottom Navigation Bar");
                                            stopLoading();
                                            intent.putExtra("show_success_dialog",true);
                                            startActivity(intent);
                                        }, DELAY);

                                    }, DELAY);

                                }, DELAY);

                            }, DELAY);


                        } else {
                            if (allergyModel != null) {
                                if (allergyModel.getData() != null) {
                                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    loadNextActivity(allergyIDs, allergyNames);
                               /*   if (allergyModel.getData(). > 0) {

                              } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                }*/
                                } else {
                                    Log.d(TAG, "Response Status: " + message.toString());
                                    if (message != null)
                                        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (message != null)
                                    Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            }
                            stopLoading();
                        }
                    } else if (response.code() == 401) {
                        stopLoading();
                        LogoutUtil.redirectToLogin(AllergiesActivity.this);
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new GsonBuilder().create();
                        ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Response Status: " + message.toString());
                        }
                        try {
                            errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                            if (errorMessageModel.getError() != null) {
                                Toast.makeText(getApplicationContext(), "" + errorMessageModel.getError(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            if (getApplicationContext() != null) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("AllergiesActivity_save_API",
                                                email, EXCEPTION,
                                                SharedData.caughtException(e));

                            }
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                        }
                        stopLoading();
                    }

                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllergyModelForRegistration> call, Throwable t) {
                try {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (getApplicationContext() != null) {
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("AllergiesActivity_save_API_failure",
                                        email, EXCEPTION,
                                        SharedData.throwableObject(t));
                    }
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    stopLoading();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (intent.hasExtra("From_setting")) {
            Intent intent1 = new Intent(AllergiesActivity.this,
                    HomeActivity.class);
            intent1.putExtra("coming_from", "settings");
            startActivity(intent1);
        } else {
            super.onBackPressed();
        }
    }

    private void blurBackground() {
        try {
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
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    private void startLoading() {
        try {
            //disable user interaction
            AllergiesActivity.this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loading_lav.setVisibility(View.VISIBLE);
            loading_lav.playAnimation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopLoading() {
        try {
            blurView.setVisibility(View.INVISIBLE);
            blurView.setVisibility(View.GONE);
            //Enable user interaction
            AllergiesActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loading_lav.setVisibility(View.GONE);
            loading_lav.pauseAnimation();
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void allergyClickListener(Integer allergyID, String allergyName, boolean isChecked) {
        try {
            if (isChecked) {
                allergyIDs.add(allergyID);
                //SessionUtil.setAllergiesId(getApplicationContext(),TextUtils.join(",", allergyIDs));
                allergyNames.add(allergyName);
            } else {
                //SessionUtil.RemoveAllergiesId(getApplicationContext());
                removeAllergyFromList(allergyID, allergyName);

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void removeAllergyFromList(int allergyID, String allergyName) {
        try {
            for (int i = 0; i < allergyIDs.size(); i++) {
                if (allergyID == allergyIDs.get(i)) {
                    allergyIDs.remove(i);
                    if (i <= allergyNames.size() - 1) {
                        allergyNames.remove(i);
                    }
                }
            }
            //SessionUtil.setAllergiesId(getApplicationContext(),TextUtils.join(",", allergyIDs));
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
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
                SignupResponse activationResponse;
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    Log.d(TAG, "Response Status: " + message.toString());
                    activationResponse = response.body();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Allergies Activity: " + activationResponse.toString());
                    }
                    //dbHelper.addUser(activationResponse);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.updateUserProfile(activationResponse);
                    /*Toast.makeText(FoodPreferencesActivity.this, activationResponse.getMessage().toString(),
                            Toast.LENGTH_SHORT).show();*/

                    String allergyIDsString = TextUtils.join(",", allergyIDs);
                    String allergyNamesString = TextUtils.join(",", allergyNames);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Allergy IDs: " + allergyIDsString);
                        Log.d(TAG, "Allergy : " + allergyNamesString);
                    }
                    if (allergyIDsString.matches("")) {
                        stopLoading();
                        loadNextActivity("", "");
                    } else {
                        saveAllergies(allergyIDsString, allergyNamesString);
                    }


                } else {
                    try {
                        Gson gson = new GsonBuilder().create();
                        SignupResponse profileJSON_Response = new SignupResponse();
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);

                        if (message != null)
                            Log.e(TAG, "Response Status: " + message.toString());

                        profileJSON_Response = gson.fromJson(response.errorBody().string(), SignupResponse.class);
                        if (response.code() == 400) {
                            showMessage(profileJSON_Response);

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

    void showMessage(SignupResponse profileJSON_Response) {
        if (profileJSON_Response != null) {
            if (profileJSON_Response.getErrors() != null) {
                if (profileJSON_Response.getErrors().getAge() != null) {
                    if (profileJSON_Response.getErrors().getAge().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getAge().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getHeight() != null) {
                    if (profileJSON_Response.getErrors().getHeight().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getHeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getWeight() != null) {
                    if (profileJSON_Response.getErrors().getWeight().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getWeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getGender() != null) {
                    if (profileJSON_Response.getErrors().getGender().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getGender().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getUnit() != null) {
                    if (profileJSON_Response.getErrors().getUnit().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getUnit().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getGoal_id() != null) {
                    if (profileJSON_Response.getErrors().getGoal_id().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getGoal_id().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else if (profileJSON_Response.getErrors().getLevel_id() != null) {
                    if (profileJSON_Response.getErrors().getLevel_id().size() > 0) {
                        Toast.makeText(this, profileJSON_Response.getErrors().getLevel_id().get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (profileJSON_Response.getMessage() != null)
                        Toast.makeText(this, profileJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                if (Common.isLoggingEnabled) {
                    if (profileJSON_Response.getMessage() != null) {
                        Log.d(TAG, "profileJSON_Response getMessage(): " + profileJSON_Response.getMessage().toString());
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
                    Toast.makeText(this, profileJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "profileJSON_Response is null");
            }
        }
    }
}