package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.adapters.GoalAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.GoalClickListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.GoalModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class GoalsActivity extends AppCompatActivity implements GoalClickListener {
    private MaterialButton mGoalsButton;
    private Boolean mStateChanged = true;
    String userLevel, userGoals, height, weight, gender, age, unitType, id, email, name, refresh_token, token;
    int goal_id, level_id;
    BlurView blurView;
    LottieAnimationView loading_lav;
    private CrashlyticsCore Crashlytics;
    private ImageButton back_btn;
    private SegmentedProgressBar segmentedProgressBar;
    SignupResponse activationResponse;
    private DBHelper dbHelper;
    private long mLastClickTime = 0;
    private int clickCounter = 0;
    RecyclerView goalRecyclerView;
    GoalModel.Datum goalData;

    Resources resources;
    private String message;

    boolean isComingFromLogin;

    MaterialTextView textViewGoals;

    String TAG = "GOAL_ACTIVITY_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        //resources = Localization.setLanguage(GoalsActivity.this, getResources());
        resources = getResources();
        dbHelper = new DBHelper(GoalsActivity.this);

        //set status icon bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        //getIntent data
        getIntentData();

        //initialize all id's
        initialize();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // listener for goals buttton
        mGoalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoalsButton.startAnimation(myAnim);
                clickCounter = clickCounter + 1;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {


                    /*if (goalData == null*//* || userGoals.equalsIgnoreCase(null)*//*) {
                    if (clickCounter <= 3) {
                        Toast toast = Toast.makeText(GoalsActivity.this, "Please select your goal", Toast.LENGTH_SHORT);
                        //  toast.getView().setBackgroundResource(R.color.yellow);
                        toast.show();
                    }

                }*/
                    //uploadData();
                    loadNextActivity();
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //listener for textViews
        /*View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textViewLoseWeightSubtitle) {
                    mLoseWgt.setTextColor(Color.BLACK);
                    v.setBackgroundResource(R.drawable.textview_after_click);

                    mFittedToned.setTextColor(Color.WHITE);
                    mFittedToned.setBackgroundResource(R.drawable.textview_outline_style);
                    mBuildMuscle.setTextColor(Color.WHITE);
                    mBuildMuscle.setBackgroundResource(R.drawable.textview_outline_style);

                    mGoalsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            goal_id = 1;
                            userGoals = mLoseWgt.getText().toString();
                            saveDataInDatabase();
                        }
                    });

                } else if (v.getId() == R.id.textViewBuildMusclesSubtitle) {
                    mBuildMuscle.setTextColor(Color.BLACK);
                    v.setBackgroundResource(R.drawable.textview_after_click);


                    mLoseWgt.setTextColor(Color.WHITE);
                    mLoseWgt.setBackgroundResource(R.drawable.textview_outline_style);
                    mFittedToned.setTextColor(Color.WHITE);
                    mFittedToned.setBackgroundResource(R.drawable.textview_outline_style);


                    mGoalsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            goal_id = 2;
                            userGoals = mBuildMuscle.getText().toString();
                            saveDataInDatabase();

                        }
                    });
                } else if (v.getId() == R.id.textViewFittedAndTonedSubtitle) {
                    mFittedToned.setTextColor(Color.BLACK);
                    v.setBackgroundResource(R.drawable.textview_after_click);

                    mLoseWgt.setTextColor(Color.WHITE);
                    mLoseWgt.setBackgroundResource(R.drawable.textview_outline_style);
                    mBuildMuscle.setTextColor(Color.WHITE);
                    mBuildMuscle.setBackgroundResource(R.drawable.textview_outline_style);

                    mGoalsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                goal_id = 3;
                                userGoals = mFittedToned.getText().toString();
                                saveDataInDatabase();
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        };*/
    }

    private void uploadData() {

        if (goalData == null /*|| userGoals.equalsIgnoreCase(null)*/) {
            Toast toast = Toast.makeText(GoalsActivity.this, resources.getString(R.string.select_your_goal), Toast.LENGTH_SHORT);
            //  toast.getView().setBackgroundResource(R.color.yellow);
            toast.show();
        } else {
            blurrBackground();
            StartLoading();
            //activeProfile(id, weight, height, age, gender, goalData.getId(), level_id, unitType);


        }


    }

    private void loadNextActivity() {
        if (this.goalData != null && this.goalData.getStripeProduct() != null) {
            Intent intent = new Intent(GoalsActivity.this,
                    FitnessLevelActivity.class);

        /*intent.putExtra(Common.SESSION_USER_LEVEL, userLevel);
        intent.putExtra(Common.SESSION_USER_LEVEL_ID, level_id);*/

            intent.putExtra(Common.SESSION_USER_GOAL_ID, this.goalData.getId());
            intent.putExtra(Common.SESSION_USER_GOAL, this.goalData.getName());
            intent.putExtra(Common.SESSION_USER_HEIGHT, height);
            intent.putExtra(Common.SESSION_USER_WEIGHT, weight);
            intent.putExtra(Common.SESSION_USER_AGE, age);
            intent.putExtra(Common.SESSION_USER_GENDER, gender);
            intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
            intent.putExtra(Common.SESSION_USER_ID, id);
            intent.putExtra(Common.SESSION_EMAIL, email);
            intent.putExtra(Common.SESSION_USERNAME, name);
            intent.putExtra(Common.SESSION_USER_PRODUCT_ID, this.goalData.getStripeProduct().getProductId());
            intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
            intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);

            //segmentedProgressBar.incrementCompletedSegments();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Goal Activity Add to bundle to pass activity------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_USER_GOAL + ": " + goalData.getName());
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goalData.getId());
                Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + goalData.getStripeProduct().getProductId());
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
            }
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    void getGoalsFromServer() {
        blurrBackground();
        StartLoading();
        Call<GoalModel> call = new ApiClient().getService().getGoals("Bearer " + token);
        call.enqueue(new Callback<GoalModel>() {
            @Override
            public void onResponse(Call<GoalModel> call, Response<GoalModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status: " + message.toString());
                    }

                    StopLoading();
                    if (response.body() != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Goal Response: " + response.body().toString());
                        }
                        setAdapter(response.body());
                    }
                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(GoalsActivity.this);
                    Toast.makeText(GoalsActivity.this, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();

                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    StopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response is unsuccessfull while getting goals");
                    }
                }
            }

            @Override
            public void onFailure(Call<GoalModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                StopLoading();
            }
        });
    }

    void setAdapter(GoalModel goalModel) {
        if (goalModel != null) {
            if (goalModel.getData() != null) {
                if (goalModel.getData().size() > 0) {
                    goalRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    GoalAdapter goalAdapter = new GoalAdapter(getApplicationContext(), goalModel.getData(), GoalsActivity.this);
                    goalRecyclerView.setAdapter(goalAdapter);
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "goalModel.getData().size() is zero");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "goalModel.getData() is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "goalModel is null");
            }
        }
    }


    private void getIntentData() {
        try {
            Intent intent = getIntent();
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
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Goal Activity fetched data from previous activity------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
            }

        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("loginActiviy_LoginAPI", email
                            , EXCEPTION, SharedData.caughtException(exception));
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }

    }

    private void initialize() {

        loading_lav = findViewById(R.id.loading_lav);
        // Hook up the pay button to the card widget and stripe instance

        blurView = findViewById(R.id.blurView);
        goalRecyclerView = findViewById(R.id.goalRecyclerview);
        goalRecyclerView.setHasFixedSize(true);
        mGoalsButton = findViewById(R.id.btnGoalsNext);
        back_btn = findViewById(R.id.backBtn);
        textViewGoals = findViewById(R.id.textViewGoals);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(3);


        Intent intent = getIntent();
        if (intent.hasExtra(Common.SESSION_COMING_FROM)) {
            back_btn.setVisibility(View.GONE);
            isComingFromLogin = true;
        }


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        textViewGoals.setText(resources.getString(R.string.what_s_your_goal));
        mGoalsButton.setText(resources.getString(R.string.next));

        getGoalsFromServer();

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (!isComingFromLogin) {
            super.onBackPressed();
        }
    }

    private void blurrBackground() {
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

    private void StartLoading() {
        //dissable user interaction
        GoalsActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void StopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        GoalsActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    @Override
    public void goalItemOnClickListener(GoalModel.Datum goalData) {
        this.goalData = goalData;
        goal_id = goalData.getId();
        userGoals = goalData.getName();
    }
}






