package com.cedricapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private MaterialButton mStarFreeTrialButton;
    private MaterialButton mLoginActivityButton;
    private MaterialTextView mdummy, seven_dayTV;
    boolean isLoggedIn;
    private Boolean mStateChanged = false;
    private MaterialTextView mTextviewAppTitle;
    private FirebaseAnalytics firebaseAnalytics;
    private String userEmail;
    Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //resources = Localization.setLanguageOnLogin(MainActivity.this, getResources());
        resources = getResources();

        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            //SessionUtil.saveAPI_Environment(getApplicationContext(),false);
            setAPIEnvironment();
            isLoggedIn();
        } else {
            Toast.makeText(this, resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            setAPIEnvironment();
            isLoggedIn();
        }


        //set Full Screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set Button id's
        mStarFreeTrialButton = findViewById(R.id.btnFreeTrials);
        mLoginActivityButton = findViewById(R.id.btnLoginMainActivity);
        mTextviewAppTitle = findViewById(R.id.mTextViewAppTitle);
        seven_dayTV = findViewById(R.id.seven_dayTV);


        //setLanguage();
        //set Gradient on text
        Shader shader = new LinearGradient(0, 0, 0,
                mTextviewAppTitle.getTextSize(), getColor(R.color.gradient1),
                getColor(R.color.gradient2),
                Shader.TileMode.CLAMP);
        mTextviewAppTitle.getPaint().setShader(shader);

        if (!mStateChanged) {
            mStarFreeTrialButton.setBackgroundResource(R.drawable.textview_circular_gradient_shape);
            mStarFreeTrialButton.setTextColor(Color.parseColor("#FFFFFF"));

            mLoginActivityButton.setBackgroundResource(R.drawable.textview_outline_style);
            mLoginActivityButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
        setLanguageToWidget();


        // mdummy=findViewById(R.id.textViewBeginner);

        mStarFreeTrialButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                mStateChanged = true;
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                // mStarFreeTrialButton.setTextColor(R.color.black);
                if (mStateChanged) {
                    mStarFreeTrialButton.setBackgroundResource(R.drawable.textview_circular_gradient_shape);
                    mStarFreeTrialButton.setTextColor(Color.BLACK);
                }
                mStateChanged = false;
                startActivity(intent);
            }
        });

        // Listener for login Button
        mLoginActivityButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                mStarFreeTrialButton.setBackgroundResource(R.drawable.textview_outline_style);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                mLoginActivityButton.setBackgroundResource(R.drawable.gradient_drawable_button);
                mLoginActivityButton.setTextColor(Color.BLACK);
                startActivity(intent);
            }
        });

    }


    void setLanguageToWidget() {
        mStarFreeTrialButton.setText(resources.getString(R.string.sign_up_txt));
        mLoginActivityButton.setText(resources.getString(R.string.log_in));
        seven_dayTV.setText(resources.getString(R.string.your_first_7_days_are_free));
    }

    void setAPIEnvironment() {
        if (SessionUtil.getAPI_URL(getApplicationContext()).matches("")) {
            String environment = SessionUtil.getAPP_Environment(MainActivity.this);
            if (environment.matches("testing")) {
                SharedData.BASE_URL = Common.TESTING_BASE_URL;
                /* environmentSwitch.setChecked(true);*/
            } else if (environment.matches("stagging")) {
                SharedData.BASE_URL = Common.STAGING_BASE_URL;

            } else if (environment.matches("beta")) {
                SharedData.BASE_URL = Common.BETA_BASE_URL;

            } else if (environment.matches("testing_beta")) {
                SharedData.BASE_URL = Common.TESTING_BETA_BASE_URL;

            } else {
                SharedData.BASE_URL = Common.PRODUCTION_BASE_URL;
                /* environmentSwitch.setChecked(false);*/
            }
        } else {
            SharedData.BASE_URL = SessionUtil.getAPI_URL(getApplicationContext());
        }
    }


    private void isLoggedIn() {
        if (!SessionUtil.getUserEmailFromSession(getApplicationContext()).isEmpty()) {
            if (!SessionUtil.getLoggedStatus(getApplicationContext())) {
                SessionUtil.setLoggedIn(getApplicationContext(), true);
            }
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStarFreeTrialButton != null) {
            mStarFreeTrialButton.setBackgroundResource(R.drawable.textview_circular_gradient_shape);
            mStarFreeTrialButton.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (mLoginActivityButton != null) {
            mLoginActivityButton.setBackgroundResource(R.drawable.textview_outline_style);
            mLoginActivityButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
}