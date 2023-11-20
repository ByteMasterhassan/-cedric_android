package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.UpdateLanguage;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class ChooseUnitTypeActivity extends AppCompatActivity {
    private MaterialButton mChooseTypeButton;
    private MaterialTextView mTextViewImperial, mTextViewMetric, textViewChooseUnitType;
    private Boolean mStateChanged = true;
    private ImageButton back_btn;
    private long pressedTime;
    private long mLastClickTime = 0;

    String unitType, id, email, name, refresh_token, token;
    int clickCounter = 0;
    private SegmentedProgressBar segmentedProgressBar;

    Resources resources;

    String TAG = "UNIT_TYPE_TAG";


    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_unit_type);
        //resources = Localization.setLanguage(ChooseUnitTypeActivity.this, getResources());
        resources = getResources();
        if (Build.VERSION.SDK_INT >= 24) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        getIntentData();

        mTextViewImperial = findViewById(R.id.textViewImperial);
        mTextViewMetric = findViewById(R.id.textViewMetric);
        mChooseTypeButton = findViewById(R.id.btnChooseUnitNext);
        textViewChooseUnitType = findViewById(R.id.textViewChooseUnitType);
        // back_btn = findViewById(R.id.backBtn);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(1);

        textViewChooseUnitType.setText(resources.getString(R.string.choose_one));
        mTextViewImperial.setText(resources.getString(R.string.imperial));
        mTextViewMetric.setText(resources.getString(R.string.metric));
        mChooseTypeButton.setText(resources.getString(R.string.next));

       /* back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/


        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        mChooseTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChooseTypeButton.startAnimation(myAnim);
                clickCounter = clickCounter + 1;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                // do your magic here
                if (unitType == null || unitType.equalsIgnoreCase(null)) {
                    if (clickCounter <= 3) {
                        Toast.makeText(ChooseUnitTypeActivity.this, resources.getString(R.string.please_select_one),
                                Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        //listener for textViews
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textViewImperial) {
                    if (mStateChanged) {
                        mTextViewImperial.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.drawable.textview_after_click);

                        mTextViewMetric.setTextColor(Color.WHITE);
                        mTextViewMetric.setBackgroundResource(R.drawable.textview_outline_style);

                        mChooseTypeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unitType = mTextViewImperial.getText().toString();
                                gotoNextActivity();
                            }
                        });
                    }
                }
                if (v.getId() == R.id.textViewMetric) {
                    if (mStateChanged) {

                        mTextViewMetric.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.drawable.textview_after_click);

                        mTextViewImperial.setTextColor(Color.WHITE);
                        mTextViewImperial.setBackgroundResource(R.drawable.textview_outline_style);

                        mChooseTypeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                unitType = mTextViewMetric.getText().toString();
                                gotoNextActivity();

//
                            }
                        });

                    }
                }

            }
        };
        mTextViewImperial.setOnClickListener(listener);
        mTextViewMetric.setOnClickListener(listener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }


    }

    private void getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra(Common.SESSION_USER_ID);
        email = intent.getStringExtra(Common.SESSION_EMAIL);
        name = intent.getStringExtra(Common.SESSION_USERNAME);
        token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
        refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In Choose Unit Type Activity: ID: " + id + ", token: " + token + ", email  " + email + ", Name: " + name);
        }
        /*String savedLanguage = SessionUtil.getlangCode(this);
        if(savedLanguage.matches("")){
            Locale current = getResources().getConfiguration().locale;
            String systemLanguage = current.getLanguage();
            if(ConnectionDetector.isConnectedWithInternet(getApplicationContext())){
                updateLanguage(token,systemLanguage);
            }
        }else{
            if(ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                updateLanguage(token, savedLanguage);
            }
        }*/
    }

    private void gotoNextActivity() {

        Intent intent = new Intent(ChooseUnitTypeActivity.this,
                InformationActivity.class);
        intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
        intent.putExtra(Common.SESSION_USER_ID, id);
        intent.putExtra(Common.SESSION_EMAIL, email);
        intent.putExtra(Common.SESSION_USERNAME, name);
        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
        intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);
        //segmentedProgressBar.incrementCompletedSegments();

        startActivity(intent);


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void updateLanguage(String token, String language) {
        Call<UpdateLanguage> call = ApiClient.getService()
                .changeLanguage("Bearer " + token, language);
        call.enqueue(new Callback<UpdateLanguage>() {
            @Override
            public void onResponse(Call<UpdateLanguage> call, Response<UpdateLanguage> response) {
                if (response.isSuccessful()) {
                    String message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }

                    //SessionUtil.setlangCode(getApplication(), language);

                } else {
                    Gson gson = new GsonBuilder().create();
                    UpdateLanguage updateLanguage = new UpdateLanguage();
                    try {
                        if (response.errorBody() != null) {
                            updateLanguage = gson.fromJson(response.errorBody().string(), UpdateLanguage.class);
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);

                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("language_fragment_update_language_api__in_CHOOSE_UNIT_TYPE_ACTIVITY", SessionUtil.getUserEmailFromSession(getApplicationContext())
                                        , EXCEPTION, SharedData.caughtException(e));

                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }

                    if (response.code() == 400) {

                        if (updateLanguage != null && updateLanguage.getMessage() != null) {
                            Toast.makeText(getApplicationContext(), updateLanguage.getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        String message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateLanguage> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);

                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Choose_unit_type_activity__language_update", SessionUtil.getUserEmailFromSession(getApplicationContext())
                                , EXCEPTION, SharedData.throwableObject(t));

                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });

    }
}
