package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.ProfileAPI_Callback;
import com.cedricapp.model.ProfileActivation;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ProfileUpdateUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class FitnessLevelActivity extends AppCompatActivity implements ProfileAPI_Callback {
    private MaterialTextView mTextViewBeginner, mTextViewIntermediate, mTextViewAdvance;
    private Boolean mStateChanged = true;
    private MaterialButton mFitnessLevelButton;
    String level, height, weight, gender, age, unitType, id, name, email, refresh_token, token, userGoal, product_id;
    private int level_id, goalID;
    private CrashlyticsCore Crashlytics;
    private ImageButton back_btn;
    private SegmentedProgressBar segmentedProgressBar;
    private long mLastClickTime = 0;
    private int clickCounter = 0;

    MaterialTextView textViewFitnessLevel;

    BlurView blurView;
    LottieAnimationView loading_lav;

    Resources resources;

    String TAG="LEVEL_ACTIVITY_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_level);
        //resources = Localization.setLanguage(FitnessLevelActivity.this,getResources());
        resources = getResources();

        //set status icon bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        // initialize all id's and set
        initialize();
        //get intent data from previous activity
        getIntentData();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        mFitnessLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCounter = clickCounter + 1;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (level == null || level.equalsIgnoreCase(null)) {
                    if (clickCounter <= 3) {
                        Toast.makeText(FitnessLevelActivity.this, getResources().getString(R.string.select_your_current_fitness_level),
                                Toast.LENGTH_SHORT).show();
                    }

                    //toast.getView().setBackgroundResource(R.color.yellow);

                }

            }
        });


        //listener for textViews
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textViewBeginner) {
                    if (mStateChanged) {
                        mTextViewBeginner.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.drawable.textview_after_click);

                        mTextViewAdvance.setTextColor(Color.WHITE);
                        mTextViewAdvance.setBackgroundResource(R.drawable.textview_outline_style);
                        mTextViewIntermediate.setTextColor(Color.WHITE);
                        mTextViewIntermediate.setBackgroundResource(R.drawable.textview_outline_style);
                        mFitnessLevelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFitnessLevelButton.startAnimation(myAnim);
                                level_id = 1;
                                level = mTextViewBeginner.getText().toString();
                                updateUserProfile("1");
                                //gotoNextActivity();

                            }
                        });
                    }
                }
                if (v.getId() == R.id.textViewIntermediate) {
                    if (mStateChanged) {

                        mTextViewIntermediate.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.drawable.textview_after_click);

                        mTextViewBeginner.setTextColor(Color.WHITE);
                        mTextViewBeginner.setBackgroundResource(R.drawable.textview_outline_style);
                        mTextViewAdvance.setTextColor(Color.WHITE);
                        mTextViewAdvance.setBackgroundResource(R.drawable.textview_outline_style);
                        mFitnessLevelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFitnessLevelButton.startAnimation(myAnim);
                                level_id = 2;
                                level = mTextViewIntermediate.getText().toString();
                                updateUserProfile("2");
                                //gotoNextActivity();

                            }
                        });

                    }
                }
                if (v.getId() == R.id.textViewAdvance) {
                    if (mStateChanged) {
                        mTextViewAdvance.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.drawable.textview_after_click);

                        mTextViewBeginner.setTextColor(Color.WHITE);
                        mTextViewBeginner.setBackgroundResource(R.drawable.textview_outline_style);
                        mTextViewIntermediate.setTextColor(Color.WHITE);
                        mTextViewIntermediate.setBackgroundResource(R.drawable.textview_outline_style);
                        mFitnessLevelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFitnessLevelButton.startAnimation(myAnim);
                                level_id = 3;
                                level = mTextViewAdvance.getText().toString();
                                updateUserProfile("3");
                                //gotoNextActivity();

                            }
                        });
                    }
                }
            }
        };
        mTextViewBeginner.setOnClickListener(listener);
        mTextViewIntermediate.setOnClickListener(listener);
        mTextViewAdvance.setOnClickListener(listener);

    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            goalID = intent.getIntExtra(Common.SESSION_USER_GOAL_ID, 0);
            userGoal = intent.getStringExtra(Common.SESSION_USER_GOAL);
            height = intent.getStringExtra(Common.SESSION_USER_HEIGHT);
            weight = intent.getStringExtra(Common.SESSION_USER_WEIGHT);
            age = intent.getStringExtra(Common.SESSION_USER_AGE);
            gender = intent.getStringExtra(Common.SESSION_USER_GENDER);
            unitType = intent.getStringExtra(Common.SESSION_UNIT_TYPE);
            id = intent.getStringExtra(Common.SESSION_USER_ID);
            email = intent.getStringExtra(Common.SESSION_EMAIL);
            name = intent.getStringExtra(Common.SESSION_USERNAME);
            product_id = intent.getStringExtra(Common.SESSION_USER_PRODUCT_ID);
            token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Fitness Activity GET INTENT ------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
                Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoal);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("Fitness_Level_Activity_getIntentData", email
                            , EXCEPTION, SharedData.caughtException(e));
        }


    }

    private void initialize() {
        mTextViewBeginner = findViewById(R.id.textViewBeginner);
        mTextViewIntermediate = findViewById(R.id.textViewIntermediate);
        mTextViewAdvance = findViewById(R.id.textViewAdvance);
        mFitnessLevelButton = findViewById(R.id.btnFitnessNext);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(4);
        textViewFitnessLevel = findViewById(R.id.textViewFitnessLevel);

        back_btn = findViewById(R.id.backBtn);

        blurView = findViewById(R.id.blurView);
        loading_lav = findViewById(R.id.loading_lav);

        
        textViewFitnessLevel.setText(resources.getString(R.string.what_s_your_current_n_fitness_level));
        mTextViewBeginner.setText(resources.getString(R.string.beginner));
        mTextViewIntermediate.setText(resources.getString(R.string.intermediate));
        mTextViewAdvance.setText(resources.getString(R.string.advanced));
        mFitnessLevelButton.setText(resources.getString(R.string.next));

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/

    }

    private void gotoNextActivity() {

        Intent intent = new Intent(FitnessLevelActivity.this,
                FoodPreferencesActivity.class);
        intent.putExtra(Common.SESSION_USER_GOAL_ID, goalID);
        intent.putExtra(Common.SESSION_USER_GOAL, userGoal);
        intent.putExtra(Common.SESSION_USER_LEVEL_ID, level_id);
        intent.putExtra(Common.SESSION_USER_LEVEL, level);
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
        //segmentedProgressBar.incrementCompletedSegments();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "------------In Fitness Activity Add to bundle to pass activity------------");
            Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
            Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
            Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
            Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
            Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
            Log.d(TAG, Common.SESSION_USER_LEVEL + ": " + level);
            Log.d(TAG, Common.SESSION_USER_LEVEL_ID + ": " + level_id);
            Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
            Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
            Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
            Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
            Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoal);
            Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
            Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
        }
        startActivity(intent);


    }

    void updateUserProfile(String levelID) {
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            ProfileActivation profileModel = new ProfileActivation(id, weight, height, age, gender, ""+goalID, levelID, unitType, name);
            blurrBackground();
            startLoading();
            ProfileUpdateUtil.updateUserProfile(getApplicationContext(), token, profileModel, this);
        }else{
            if (getApplicationContext() != null) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void profileResponse(Response<SignupResponse> response) {
        if (response.isSuccessful()) {
            stopLoading();
            gotoNextActivity();
        } else {
            stopLoading();
            if(response.code() == 401){
                LogoutUtil.redirectToLogin(FitnessLevelActivity.this);
                Toast.makeText(getApplicationContext(),resources.getString(R.string.unauthorized),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void profileResponseFailure(Throwable t) {
        stopLoading();
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
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

    private void startLoading() {
        //dissable user interaction
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}