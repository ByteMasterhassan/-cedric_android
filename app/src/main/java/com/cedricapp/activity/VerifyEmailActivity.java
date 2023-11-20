package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.OtpResponseModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class VerifyEmailActivity extends AppCompatActivity {
    private MaterialButton mButtonOtpRest;
    private OtpTextView otpTextView;
    private MaterialTextView mTextViewResendOtp, mTextViewTimer, textViewCheckOtp;
    private ImageButton backArrow;
    SignupResponse verifactionResponse;
    private String id, email, name, token, refresh_token;
    Date currentTime, targetTime;
    LottieAnimationView loading_lav;
    BlurView blurView;
    Resources resources;

    MaterialTextView textViewResetPasswordTitle;

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Back Button Pressed");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
       // resources = Localization.setLanguage(VerifyEmailActivity.this, getResources());
        resources = getResources();
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        onBackPressedCallback.setEnabled(false);
        // set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

        }
        getIntentData();
        Init();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra(Common.SESSION_USER_ID);
        email = intent.getStringExtra(Common.SESSION_EMAIL);
        name = intent.getStringExtra(Common.SESSION_USERNAME);
        //email = intent.getStringExtra("email");
        //name = intent.getStringExtra("name");
        token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
        refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "In Verify Email Activity: " + id + " id " + email + " email  " + name);
        } //    generateOtp(email);
    }


    private void Init() {
        getTimeInterval();
        //backArrow = findViewById(R.id.backBtn);
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);
        mButtonOtpRest = findViewById(R.id.btnOtpResetPassword);
        textViewCheckOtp = findViewById(R.id.textViewPutOtp);
        otpTextView = findViewById(R.id.otp_view);
        mTextViewResendOtp = findViewById(R.id.textViewResendOtp);
        mTextViewTimer = findViewById(R.id.textViewTimer);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        textViewResetPasswordTitle = findViewById(R.id.textViewResetPasswordTitle);


        textViewResetPasswordTitle.setText(resources.getString(R.string.email_verification));
        mTextViewResendOtp.setText(resources.getString(R.string.resend_otp));
        //textViewCheckOtp.setText(resources.getString(R.string.please_enter_the_otp_code_sent_to_your_email));
        textViewCheckOtp.setText(resources.getString(R.string.opt_confirmation) + " \"" + email + "\"");
        mButtonOtpRest.setText(resources.getString(R.string.verify));

        //set Timer for OTP
        setOtpTimer();
        changeResendOtpColor();

        mTextViewResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* blurrBackground();
                StartLoading();*/
                if (ConnectionDetector.isConnectedWithInternet(VerifyEmailActivity.this)) {
                    //if (mTextViewTimer.getText().toString().equals(" ")) {
                    blurrBackground();
                    StartLoading();
                    generateOtp(email);
                    /*} else {
                        // Toast.makeText(VerifyEmailActivity.this, "A OTP  has been sent to your email. If you do not receive please try again after 3 minutes. ", Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    Toast.makeText(getApplicationContext(), resources.getText(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

      /*  backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getActivity().onBackPressed();
                //  navController.navigate(R.id.action_otpFragment2_to_LoginActivity);
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });*/

        mButtonOtpRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonOtpRest.startAnimation(myAnim);
                blurrBackground();
                StartLoading();
                String otp = otpTextView.getOTP();
                email = SharedData.email;
                if (Objects.requireNonNull(otp.isEmpty())) {
                    Toast.makeText(VerifyEmailActivity.this, resources.getString(R.string.provide_opt), Toast.LENGTH_SHORT).show();
                    StopLoading();
                } else {
                    verifyEmail(email, otp);
                    //StopLoading();
                    // Toast.makeText(VerifyEmailActivity.this, "Enter correct OTP Code ", Toast.LENGTH_LONG).show();
                }
            }
        });

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        //  }
    }


    private void getTimeInterval() {
        Calendar currentTimeNow = Calendar.getInstance();
        currentTime = currentTimeNow.getTime();
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Current time now : " + currentTimeNow.getTime());
            Log.d(Common.LOG, "Current time now : " + currentTime);
        }
        currentTimeNow.add(Calendar.MINUTE, 5);
        targetTime = currentTimeNow.getTime();
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "After adding 10 mins with Caleder add() method : " + targetTime);
        }
    }

    private void verifyEmail(String email, String otp) {
        Call<SignupResponse> call = ApiClient.getService().emailVerification(email, otp);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    verifactionResponse = response.body();

                    if (response.code() == 202) {

                        Toast.makeText(VerifyEmailActivity.this, verifactionResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyEmailActivity.this, ChooseUnitTypeActivity.class);
                        intent.putExtra(Common.SESSION_USER_ID, id);
                        intent.putExtra(Common.SESSION_EMAIL, email);
                        intent.putExtra(Common.SESSION_USERNAME, name);
                        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
                        intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);
                        startActivity(intent);
                        StopLoading();

                    } else {
                        StopLoading();
                        Toast.makeText(VerifyEmailActivity.this, verifactionResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(VerifyEmailActivity.this);
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        Gson gson = new GsonBuilder().create();
                        SignupResponse verifyOTP_JSON_Reponse = new SignupResponse();
                        verifyOTP_JSON_Reponse = gson.fromJson(response.errorBody().string(), SignupResponse.class);
                        if (response.code() == 400) {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                        } else {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        StopLoading();
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {

                Toast.makeText(VerifyEmailActivity.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "failed Api");
                }
                StopLoading();
            }
        });
    }

    void showMessage(SignupResponse verifyOTP_JSON_Reponse) {
        if (verifyOTP_JSON_Reponse != null) {
            if (verifyOTP_JSON_Reponse.getMessage() != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "verifyOTP_JSON_Reponse getMessage(): " + verifyOTP_JSON_Reponse.getMessage().toString());
                }
                Toast.makeText(VerifyEmailActivity.this, verifyOTP_JSON_Reponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "verifyOTP_JSON_Reponse getMessage() is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "verifyOTP_JSON_Reponse is null");
            }
        }
    }

    private void changeResendOtpColor() {
        if (!mTextViewTimer.getText().toString().equals(" ")) {
            mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
        } else {
            mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));

        }
    }

    private void setOtpTimer() {


        new CountDownTimer(180000, 1000) {
            public void onTick(long millisUntilFinished) {
                mTextViewResendOtp.setClickable(false);
                mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                /* long hour = (millisUntilFinished / 3600000) % 24;*/
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                mTextViewTimer.setText(f.format(min) + ":" + f.format(sec));
            }

            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                mTextViewTimer.setText(" ");
                mTextViewResendOtp.setClickable(true);
                mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));

            }
        }.start();
    }

    private void generateOtp(String email) {
        Call<OtpResponseModel> call = ApiClient.getService().createOtp(email, false);

        call.enqueue(new Callback<OtpResponseModel>() {
            @Override
            public void onResponse(Call<OtpResponseModel> call, Response<OtpResponseModel> response) {
                try {
                    if (response.isSuccessful()) {
                        setOtpTimer();
                        OtpResponseModel otpResponseModel = response.body();
                        assert otpResponseModel != null;

                        if (response.code() == 201) {
                            // StopLoading();
                            Toast.makeText(VerifyEmailActivity.this, otpResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                            Bundle bundle = new Bundle();
                            bundle.putString(Common.SESSION_EMAIL, email);
                            //  StopLoading();
                        } else {
                            //  StopLoading();
                            Toast.makeText(VerifyEmailActivity.this, otpResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    } else if (response.code() == 401) {
                        LogoutUtil.redirectToLogin(VerifyEmailActivity.this);
                        Toast.makeText(VerifyEmailActivity.this, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();

                    } else {
                        try {
                            Gson gson = new GsonBuilder().create();
                            OtpResponseModel otpResponseModel = new OtpResponseModel();
                            otpResponseModel = gson.fromJson(response.errorBody().string(), OtpResponseModel.class);
                            if (response.code() == 400) {
                                //StopLoading();
                                Toast.makeText(VerifyEmailActivity.this, otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 404) {
                                //StopLoading();
                                Toast.makeText(VerifyEmailActivity.this, otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                // StopLoading();
                                Toast.makeText(VerifyEmailActivity.this, otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("VerifyEmailActivity", email
                                            , EXCEPTION, SharedData.caughtException(ex));
                            //StopLoading();
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("VerifyEmailActivity", email
                                    , EXCEPTION, SharedData.throwableObject(ex));
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    //   StopLoading();
                }
                StopLoading();

            }

            @Override
            public void onFailure(Call<OtpResponseModel> call, Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, resources.getString(R.string.fail_to_get_otp), Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("VerifyEmailActivity", email
                                , EXCEPTION, SharedData.throwableObject(t));

                StopLoading();
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                //  StopLoading();
            }
        });

    }

    private void StartLoading() {
        //dissable user interaction

        VerifyEmailActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void StopLoading() {
        //disbale bluur view
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);

        //Enable user interaction
        VerifyEmailActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
        }
    }

    /* @Override
    @MainThread
    public void onBackPressed() {
        try {
            if (onBackPressedCallback.hasEnabledCallbacks()) {
                // There's an active callback; let the fragment handle it
                super.onBackPressed();
            } else {
                // Do your activity's back press handling here
            }
        } catch (Exception exception) {
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
     *//* if (getCallingActivity().!= 0) {
            getFragmentManager().popBackStack();
        }*//*

    }*/
}