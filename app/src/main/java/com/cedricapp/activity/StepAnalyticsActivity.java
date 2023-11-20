package com.cedricapp.activity;

import static com.cedricapp.common.Common.ANALYTICS_FOR;
import static com.cedricapp.common.Common.ANALYTICS_TYPE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.R;
import com.cedricapp.fragment.StepAnalyticsFragment;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class StepAnalyticsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private RadioGroup radioGroup;
    private RadioButton tab1, tab2, tab3;

    private ImageButton backArrowBtn;

    private String analyticsFor;

    TextView titleTV;

    CoordinatorLayout coordinatorLayout;

    Resources resources;

    LottieAnimationView loading_lav;

    BlurView blurView;
    String TAG = "STEP_ANALYTICS_ACTIVITY_TAG";

    ConstraintLayout parentCL;

    RadioButton dailyRB, weeklyRB, monthlyRB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_analytics);


        init();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    void init() {
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //resources = Localization.setLanguage(StepAnalyticsActivity.this,getResources());
        resources = getResources();
        frameLayout = findViewById(R.id.frameLayout);
        radioGroup = findViewById(R.id.radioGroup);
        backArrowBtn = findViewById(R.id.back_arrow);
        titleTV = findViewById(R.id.titleTV);
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);
        parentCL = findViewById(R.id.parentCL);
        dailyRB = findViewById(R.id.dailyRB);
        weeklyRB = findViewById(R.id.weeklyRB);
        monthlyRB = findViewById(R.id.monthlyRB);

        LocalBroadcastManager.getInstance(StepAnalyticsActivity.this).registerReceiver(stepAnalyticsBroadcastReciever,
                new IntentFilter("analytics_loader"));
        Intent intent = getIntent();
        if (intent.hasExtra(ANALYTICS_FOR)) {
            String savedLanguage = SessionUtil.getlangCode(this);

            Locale current = getResources().getConfiguration().locale;
            String language = current.getLanguage();
            analyticsFor = intent.getStringExtra(ANALYTICS_FOR);

            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(analyticsFor.matches("water")){
                window.setStatusBarColor(this.getResources().getColor(R.color.water));
                parentCL.setBackgroundColor(this.getResources().getColor(R.color.water));
                radioGroup.setBackground(getDrawable(R.drawable.water_tabbkg));
                dailyRB.setBackground(getDrawable(R.drawable.water_analytics_tab_selected_bkg));
                weeklyRB.setBackground(getDrawable(R.drawable.water_analytics_tab_selected_bkg));
                monthlyRB.setBackground(getDrawable(R.drawable.water_analytics_tab_selected_bkg));
            }else {
                window.setStatusBarColor(this.getResources().getColor(R.color.cedric_base_color));
            }
            /*if (analyticsFor != null && analyticsFor.length()>0)
                titleTV.setText(analyticsFor.substring(0,1).toUpperCase()+analyticsFor.substring(1));*/

            setLanguageToWidget();
            /*if (!savedLanguage.matches("")) {
                resources = Localization.setLocale(this, savedLanguage).getResources();
                setLanguageToWidget();
                //SessionUtil.setlangCode(getApplicationContext(), languageToLoad);
            } else {
                if (language.matches("sv")) {
                    if (Common.isLoggingEnabled) {
                        Log.d("language", "System Language2: " + language);
                        Log.d("language", "Shared Preference Lnaguage2: " + savedLanguage);
                    }
                    SessionUtil.setlangCode(getApplicationContext(), "sv");
                    resources = Localization.setLocale(this, "sv").getResources();
                    setLanguageToWidget();
                    //SessionUtil.getlangCode(getApplicationContext());
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d("language", "ELSE System Language2: " + language);
                        Log.d("language", "ELSE Shared Preference Lnaguage2: " + savedLanguage);
                    }
                    SessionUtil.setlangCode(getApplicationContext(), "en");
                    resources = Localization.setLocale(this, "en").getResources();
                    setLanguageToWidget();
                }

            }*/

        }
        if (analyticsFor != null) {
            replaceFragment(new StepAnalyticsFragment(), analyticsFor, "daily");
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Fragment selectedFragment;
                switch (checkedId) {
                    case R.id.dailyRB:
                        if (analyticsFor != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                replaceFragment(new StepAnalyticsFragment(), analyticsFor, "daily");
                            } else {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Snackbar.make(coordinatorLayout, "www.journaldev.com", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.weeklyRB:
                        if (analyticsFor != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                replaceFragment(new StepAnalyticsFragment(), analyticsFor, "weekly");
                            } else {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.monthlyRB:
                        if (analyticsFor != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                replaceFragment(new StepAnalyticsFragment(), analyticsFor, "yearly");
                            } else {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    default:
                        selectedFragment = null;
                }
            }
        });
        backArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setLanguageToWidget(){
        if(analyticsFor.matches("steps")){
            titleTV.setText(resources.getString(R.string.steps));
        }else if(analyticsFor.matches("calories")){
            titleTV.setText(resources.getString(R.string.calories));
        }else if(analyticsFor.matches("distance")){
            titleTV.setText(resources.getString(R.string.distance));
        }else if(analyticsFor.matches("water")){
            titleTV.setText(resources.getString(R.string.water));
        }
        dailyRB.setText(resources.getString(R.string.daily));
        weeklyRB.setText(resources.getString(R.string.weekly));
        monthlyRB.setText(resources.getString(R.string.yearly));
    }

    private void replaceFragment(Fragment fragment, String requestFor, String duration) {
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ANALYTICS_FOR, requestFor);
            bundle.putString(ANALYTICS_TYPE, duration);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
        }
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

        //disbale bluur view
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

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

    private final BroadcastReceiver stepAnalyticsBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Common.isLoggingEnabled){
                Log.d(TAG,"In On received broadcast");
            }
            if(intent.hasExtra("load")){
                if(Common.isLoggingEnabled){
                    Log.d(TAG,"Intent has loading");
                }
                boolean isLoad = intent.getBooleanExtra("load",false);
                if(isLoad){
                    if(Common.isLoggingEnabled){
                        Log.d(TAG,"load Loader");
                    }
                    blurrBackground();
                    startLoading();
                }else{
                    if(Common.isLoggingEnabled){
                        Log.d(TAG,"Stop Loader");
                    }
                    stopLoading();
                }
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(Common.isLoggingEnabled){
            Log.d(TAG,"In On pause");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.isLoggingEnabled){
            Log.d(TAG,"In On resume");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(StepAnalyticsActivity.this).unregisterReceiver(stepAnalyticsBroadcastReciever);
    }
}