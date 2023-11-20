package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.LocaleListCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.LevelListener;
import com.cedricapp.interfaces.PlanListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.GoalModel;
import com.cedricapp.model.LevelModel;
import com.cedricapp.model.UpdateLanguage;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.CommonAPIUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class LanguageActivity extends AppCompatActivity implements PlanListener, LevelListener {

    RadioGroupPlus radioGroupPlus;
    MaterialTextView languagetxt;
    TextView txt_english, txt_swedish;
    ImageView backArrow;
    RadioButton english_btn, swedish_btn;
    View view;
    LocaleListCompat appLocale;
    LinearLayout EnglsihLL, SwedishLL;
    String token;

    String checkRadiobtn;
    boolean backPress = true;
    private String message;
    DBHelper dbHelper;

    BlurView blurView;
    LottieAnimationView loading_lav;

    Resources resources;

    String TAG = "LANGUAGE_FRAGMENT_TAG";

    String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_CREATE");
            if (savedInstanceState == null) {
                Log.d(TAG, "savedInstanceState == null");
            } else {
                Log.d(TAG, "savedInstanceState != null");
            }
        }
        init(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_START");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_RESUME");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_STOP");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_PAUSE");
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_DESTROY");
        }
    }

    void init(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "LANGUAGE_ON_CREATE_VIEW");
        }


        //resources = Localization.setLanguage(getApplicationContext(), getResources());
        resources = getResources();
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);
        radioGroupPlus = findViewById(R.id.radio_GroupPlus);
        languagetxt = findViewById(R.id.title);
        txt_english = findViewById(R.id.txt_english);
        txt_swedish = findViewById(R.id.swedishtxt);
        backArrow = findViewById(R.id.backArrow);
        EnglsihLL = findViewById(R.id.englishLL);
        SwedishLL = findViewById(R.id.swedishLL);
        english_btn = findViewById(R.id.english_button);
        swedish_btn = findViewById(R.id.swedish_button);
        /*if (!lang.matches("")) {
            if (lang.matches("sv")) {
                Resources resources = Localization.setLocale(getApplicationContext(), "sv").getResources();
                setlanguageToWidget(resources);
            } else {
                Resources resources = Localization.setLocale(getApplicationContext(), "en").getResources();
                setlanguageToWidget(resources);
            }
        } else {
            Resources resources = Localization.setLocale(getApplicationContext(), "en").getResources();
            setlanguageToWidget(resources);
        }*/

        setlanguageToWidget();

        token = SessionUtil.getAccessToken(getApplicationContext());
        checkRadiobtn = Localization.getLang(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Language in shared preference: " + checkRadiobtn);
        }


        if (checkRadiobtn.matches("en")) {
            english_btn.setChecked(true);
        } else if (checkRadiobtn.matches("sv")) {
            swedish_btn.setChecked(true);
        } else {
            Locale current = getResources().getConfiguration().locale;
            String language = current.getLanguage();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "lannguage" + language);
            }
            if (language.matches("sv")) {
                swedish_btn.setChecked(true);
                // SessionUtil.setlangCode(getApplicationContext(),"sv");
            } else {
                english_btn.setChecked(true);
                // SessionUtil.setlangCode(getApplicationContext(),"en");
            }

        }

        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

        if (savedInstanceState != null) {
            StartLoading();
            blurrBackground();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    StopLoading();
                    finish();
                }
            }, 2000);

        }

        EnglsihLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    english_btn.setChecked(true);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "EnglishLL pressed");
                    }
                    //updateLanguage(token, "en");
                    backArrow.setEnabled(false);
                    backPress = false;
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        SwedishLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    swedish_btn.setChecked(true);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "SwedishLL pressed");
                    }
                    //updateLanguage(token, "sv");
                    backArrow.setEnabled(false);
                    backPress = false;
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        radioGroupPlus.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroupPlus group, int checkedId) {
                switch (checkedId) {
                    case R.id.english_button:
                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "English Radio pressed");
                            }
                            backArrow.setEnabled(false);
                            backPress = false;
                            updateLanguage(token, "en");
                        } else {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.swedish_button:

                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Swedish radio pressed");
                            }
                            backArrow.setEnabled(false);
                            backPress = false;
                            updateLanguage(token, "sv");
                        } else {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setlanguageToWidget() {
        languagetxt.setText(resources.getString(R.string.Fragment_language_title));
        txt_english.setText(resources.getString(R.string.english_language));
        txt_swedish.setText(resources.getString(R.string.swedish_language));
    }

    private void onBackPress() {

        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLanguage(String token, String language) {
        StartLoading();
        blurrBackground();
        Call<UpdateLanguage> call = ApiClient.getService()
                .changeLanguage("Bearer " + token, language);
        call.enqueue(new Callback<UpdateLanguage>() {
            @Override
            public void onResponse(Call<UpdateLanguage> call, Response<UpdateLanguage> response) {
                if (response.isSuccessful()) {
                    try {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        UpdateLanguage updateLanguage = response.body();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Update Language Model: " + updateLanguage.toString());
                        }

                        if (updateLanguage != null && updateLanguage.getMessage() != null)
                            Toast.makeText(getApplicationContext(), updateLanguage.getMessage(), Toast.LENGTH_SHORT).show();

                        /*Locale locale = new Locale(language);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;

                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());*/
                        lang = language;


                        SessionUtil.setReloadData(getApplicationContext(), true);
                        SessionUtil.setDashboardReloadData(getApplicationContext(), true);

                        dbHelper.clearDB();
                        backArrow.setEnabled(true);
                        backPress = true;
                        SessionUtil.setLoadHomeData(true, getBaseContext());

                        if (getApplicationContext() != null) {
                            CommonAPIUtil.getPlans(token, LanguageActivity.this);

                        }

                       /* if (getApplicationContext() != null) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            StopLoading();
                            startActivity(intent);
                            if (getActivity() != null)
                                getActivity().finish();
                        } else {
                            StopLoading();
                        }*/
                    } catch (Exception ex) {

                        StopLoading();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Loader stopped when exception occurred");
                            ex.printStackTrace();
                            Log.e(TAG, ex.getMessage());
                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Update lang on unsuccessful");
                    }
                    Log.e(TAG, "Loader stopped when successfull false");
                    StopLoading();
                    Gson gson = new GsonBuilder().create();
                    UpdateLanguage updateLanguage = new UpdateLanguage();
                    try {
                        if (response.errorBody() != null) {
                            updateLanguage = gson.fromJson(response.errorBody().string(), UpdateLanguage.class);
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        if (getApplicationContext() != null) {
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("language_fragment_update_language_api", SessionUtil.getUserEmailFromSession(getApplicationContext())
                                            , EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (response.code() == 400) {
                            if (getApplicationContext() != null) {
                                if (updateLanguage != null && updateLanguage.getMessage() != null) {
                                    Toast.makeText(getApplicationContext(), updateLanguage.getMessage().toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        if (Common.isLoggingEnabled)
                            ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateLanguage> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    Log.e(TAG, "Loader stopped when language on Failure");
                StopLoading();
                try {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (getApplicationContext() != null) {
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("language_update_response", SessionUtil.getUserEmailFromSession(getApplicationContext())
                                        , EXCEPTION, SharedData.throwableObject(t));
                    }
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                        Log.e(TAG, t.getMessage());
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled)
                        ex.printStackTrace();
                }
            }
        });

    }

    private void StartLoading() {
        try {
            //disable user interaction
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loading_lav.setVisibility(View.VISIBLE);
            loading_lav.playAnimation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void StopLoading() {
        try {
            blurView.setVisibility(View.INVISIBLE);
            blurView.setVisibility(View.GONE);
            //Enable user interaction
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loading_lav.setVisibility(View.GONE);
            loading_lav.pauseAnimation();
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    private void blurrBackground() {
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

    @Override
    public void levelOnSuccess(LevelModel levelModel) {
        try {
            if (levelModel != null && levelModel.getData() != null && levelModel.getData().size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Level model in Language Model: " + levelModel.toString());
                }
                if (getApplicationContext() != null) {

                    for (int i = 0; i < levelModel.getData().size(); i++) {
                        String levelID = SessionUtil.getUserLevelID(getApplicationContext());
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Level ID in shared preference is " + levelID);
                        }
                        if (levelID.equals(String.valueOf(levelModel.getData().get(i).getId()))) {

                            if (lang.matches("sv")) {
                                SessionUtil.setUserLevel(getApplicationContext(), levelModel.getData().get(i).getNameSv());
                            } else {
                                SessionUtil.setUserLevel(getApplicationContext(), levelModel.getData().get(i).getName());
                            }
                        }
                    }
                    StopLoading();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "level getContext is null");
                    }
                    StopLoading();
                }
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "CALL_DASHBOARD_ACTIVITY_FROM_LANGUAGE");
                }
                /*Localization.setLanguage(LanguageActivity.this,getResources());*/
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Language in lang variable is " + lang);
                }
                SessionUtil.setlangCode(getApplicationContext(), lang);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Language in shared preference is " + SessionUtil.getlangCode(getApplicationContext()));
                }
                StopLoading();
                Localization.setLocale(getApplicationContext(), lang);
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);*/


            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "levelModel != null || levelModel.getData() != null || levelModel.getData().size() > 0");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void levelOnUnSuccess() {
        if (Common.isLoggingEnabled) {
            Log.e(TAG, "LEVEL on unsuccessful");
        }

    }

    @Override
    public void levelOnFailure(Throwable throwable) {
        if (Common.isLoggingEnabled) {
            throwable.printStackTrace();
        }

    }

    @Override
    public void planOnSuccess(GoalModel planModel) {
        try {
            if (planModel != null && planModel.getData() != null && planModel.getData().size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Plan model in Language Model: " + planModel.toString());
                }
                if (getApplicationContext() != null) {
                    for (int i = 0; i < planModel.getData().size(); i++) {
                        String planID = SessionUtil.getUserGoalID(getApplicationContext());
                        if (planModel.getData().get(i).getStripeProduct() != null) {
                            if (planModel.getData().get(i).getStripeProduct().getGoalId().equals(planID)) {
                                if (lang.matches("sv")) {
                                    SessionUtil.setUserGoal(getApplicationContext(), planModel.getData().get(i).getNameSv());
                                } else {
                                    SessionUtil.setUserGoal(getApplicationContext(), planModel.getData().get(i).getName());
                                }
                            }
                        }
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "plan getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "planModel!=null || planModel.getData()!=null || planModel.getData().size()>0");
                }
            }
            CommonAPIUtil.getLevel(token, SessionUtil.getUserGoalID(getApplicationContext()), LanguageActivity.this);
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void planOnUnSuccess() {
        if (Common.isLoggingEnabled)
            Log.e(TAG, "Plan on unsuccessfull");
    }

    @Override
    public void planOnFailure(Throwable t) {
        if (Common.isLoggingEnabled)
            t.printStackTrace();

    }
}