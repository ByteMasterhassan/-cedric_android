package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.SharedData;
import com.cedricapp.R;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.model.OtpResponseModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.retrofit.ApiClient;
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
public class OtpFragment extends Fragment {


    private MaterialButton mButtonOtpRest;
    private OtpTextView otpTextView;
    private MaterialTextView mTextViewResendOtp, mTextViewTimer;
    private ImageButton backArrow;
    NavController navController;
    private SignupResponse verifactionResponse;
    private String email;
    Date currentTime, targetTime;
    private View view1;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private String message;
    private CountDownTimer mCountDownTimer;
    private boolean timerWasRunning = false;
    private long timeRemaining = 0;

    MaterialTextView textViewResetPasswordTitle, textViewPutOtp;

    Resources resources;

    String TAG = "OTP_FRAGMENT_TAG";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            //   timeRemaining = mCountDownTimer.;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timeRemaining > 0) {
            mCountDownTimer = new CountDownTimer(timeRemaining, 1000) {
                // onTick() and onFinish() methods here
                public void onTick(long millisUntilFinished) {

                    //mTextViewResendOtp.setClickable(false);
                    mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    /* long hour = (millisUntilFinished / 3600000) % 24;*/
                    timeRemaining = millisUntilFinished;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    mTextViewTimer.setText(f.format(min) + ":" + f.format(sec));
                }

                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    timeRemaining = 0;
                    mTextViewTimer.setText(" ");
                    mTextViewResendOtp.setClickable(true);
                    mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));

                }
            }.start();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        Init();

        //set Timer for OTP
        if (timeRemaining == 0) {
            setOtpTimer();
        }


        //getTimeInterval();
        changeResendOtpColor();

        mTextViewResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    if (mTextViewTimer.getText().toString().equals(" ")) {
                        generateOtp(email);

                    } else {

                        //  Toast.makeText(getContext(), "An OTP  has been sent to your email. If you do not receive please try again after 3 minutes. ", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //  navController.navigate(R.id.action_otpFragment2_to_LoginActivity);
                if (getFragmentManager() != null)
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        //getFragmentManager().popBackStack();
                        navController.popBackStack(R.id.forgetPasswordFragment3, false);
                    }
            }
        });
        //navController.navigate(R.id.action_otpFragment2_to_resetPasswordFragment2);

        mButtonOtpRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurrBackground();
                StartLoading();
                String otp = otpTextView.getOTP();
                if (Objects.requireNonNull(otp.isEmpty())) {
                    Toast.makeText(getContext(), resources.getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
                    StopLoading();
                } else {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        verifyEmail(email, otp);
                        StopLoading();
                        // Toast.makeText(VerifyEmailActivity.this, "Enter correct OTP Code ", Toast.LENGTH_LONG).show();
                    } else {
                        StopLoading();
                        Toast.makeText(getActivity(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void changeResendOtpColor() {
        if (!mTextViewTimer.getText().toString().equals(" ")) {
            mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
        } else {
            mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));

        }
    }

    private void Init() {
        email = getArguments().getString("email");
        System.out.println(email + " email   ");
        backArrow = view1.findViewById(R.id.backBtn);
        navController = Navigation.findNavController(view1);
        mButtonOtpRest = view1.findViewById(R.id.btnOtpResetPassword);
        otpTextView = view1.findViewById(R.id.otp_view);
        mTextViewResendOtp = view1.findViewById(R.id.textViewResendOtp);
        mTextViewTimer = view1.findViewById(R.id.textViewTimer);
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        mTextViewResendOtp.setClickable(false);
        otpTextView.setFocusable(true);
        textViewResetPasswordTitle = view1.findViewById(R.id.textViewResetPasswordTitle);
        textViewPutOtp = view1.findViewById(R.id.textViewPutOtp);


        textViewResetPasswordTitle.setText(resources.getString(R.string.reset_password));
        mTextViewResendOtp.setText(resources.getString(R.string.resend_otp));
        textViewPutOtp.setText(resources.getString(R.string.put_otp_code_to_reset_your_password));
        mButtonOtpRest.setText(resources.getString(R.string.btn_reset));

    }

    private void setOtpTimer() {
        //timeRemaining = 0;
        mCountDownTimer = new CountDownTimer(180000, 1000) {
            public void onTick(long millisUntilFinished) {
                //mTextViewResendOtp.setClickable(false);
                mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                /* long hour = (millisUntilFinished / 3600000) % 24;*/
                timeRemaining = millisUntilFinished;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                mTextViewTimer.setText(f.format(min) + ":" + f.format(sec));
            }

            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                timeRemaining = 0;
                mTextViewTimer.setText(" ");
                mTextViewResendOtp.setClickable(true);
                mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));

            }
        }.start();
    }

    private void getTimeInterval() {
        Calendar currentTimeNow = Calendar.getInstance();
        currentTime = currentTimeNow.getTime();
        System.out.println("Current time now : " + currentTimeNow.getTime());
        System.out.println("Current time now : " + currentTime);
        currentTimeNow.add(Calendar.MINUTE, 5);
        targetTime = currentTimeNow.getTime();
        System.out.println("After adding 10 mins with Caleder add() method : " + targetTime);
    }

    private void verifyEmail(String email, String otp) {
        Call<SignupResponse> call = ApiClient.getService().emailVerification(email, otp);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                try {
                    verifactionResponse = response.body();
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        verifactionResponse = response.body();

                        if (response.code() == 202) {
                            if (getContext() != null)
                                Toast.makeText(getContext(), verifactionResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            navController.navigate(R.id.action_otpFragment2_to_resetPasswordFragment2, bundle);
                            StopLoading();
                        } else {
                            StopLoading();
                            Toast.makeText(getContext(), verifactionResponse.getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Gson gson = new GsonBuilder().create();
                        SignupResponse verifyOTP_JSON_Reponse = new SignupResponse();
                        verifyOTP_JSON_Reponse = gson.fromJson(response.errorBody().string(), SignupResponse.class);

                        if (response.code() == 401) {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                           /* Toast.makeText(getContext(),  response.message().toString(),
                                    Toast.LENGTH_SHORT).show();*/
                        } else if (response.code() == 404) {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                           /* Toast.makeText(getContext(), response.message().toString(),
                                    Toast.LENGTH_SHORT).show();*/
                        } else if (response.code() == 403) {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                           /* Toast.makeText(getContext(), response.message().toString(),
                                    Toast.LENGTH_SHORT).show();*/
                        } else if (response.code() == 410) {
                            StopLoading();
                            showMessage(verifyOTP_JSON_Reponse);
                          /*  Toast.makeText(getContext(), response.message().toString(),
                                    Toast.LENGTH_SHORT).show();*/
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.e(TAG, "Response Status " + message.toString());
                            }
                            if (message != null)
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    new LogsHandlersUtils(getContext())
                            .getLogsDetails("Otp_Fragment_Verify_API", email
                                    , EXCEPTION, SharedData.caughtException(ex));
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    StopLoading();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {

                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getContext())
                        .getLogsDetails("Otp_fragment_Verify_API_Failure", email
                                , EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                //System.out.println("failed Api");
                StopLoading();
            }
        });
    }

    private void generateOtp(String email) {
        Call<OtpResponseModel> call = ApiClient.getService().createOtp(email, true);

        call.enqueue(new Callback<OtpResponseModel>() {
            @Override
            public void onResponse(Call<OtpResponseModel> call, Response<OtpResponseModel> response) {
                try {
                    if (response.isSuccessful()) {
                        OtpResponseModel otpResponseModel = response.body();
                        assert otpResponseModel != null;

                        if (response.code() == 201) {
                            // StopLoading();
                            if (otpResponseModel != null && otpResponseModel.getMessage() != null) {
                                if (getContext() != null)
                                    Toast.makeText(getContext(), otpResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            setOtpTimer();
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);

                        } else {
                            //StopLoading();
                            if (otpResponseModel != null && otpResponseModel.getMessage() != null) {
                                if (getContext() != null)
                                    Toast.makeText(getContext(), otpResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        try {
                            Gson gson = new GsonBuilder().create();
                            OtpResponseModel otpResponseModel = new OtpResponseModel();
                            otpResponseModel = gson.fromJson(response.errorBody().string(), OtpResponseModel.class);
                            if (otpResponseModel != null && otpResponseModel.getMessage() != null) {
                                if (getContext() != null)
                                    if (response.code() == 400) {
                                        // StopLoading();
                                        Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    } else if (response.code() == 404) {
                                        //  StopLoading();
                                        Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // StopLoading();
                                        Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                            }
                        } catch (Exception ex) {
                            //StopLoading();
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            if (getContext() != null)
                                new LogsHandlersUtils(getContext())
                                        .getLogsDetails("PaymentCategory_plans_API_EXception", email
                                                , EXCEPTION, SharedData.throwableObject(ex));
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                            if (getContext() != null)
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null)
                        new LogsHandlersUtils(getContext())
                                .getLogsDetails("PaymentCategory_plans_API_Failure", email
                                        , EXCEPTION, SharedData.caughtException(ex));
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<OtpResponseModel> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), resources.getString(R.string.failed_to_send_otp), Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null)
                    new LogsHandlersUtils(getContext())
                            .getLogsDetails("Otp_fragment_generate_otp_API_Failure", email
                                    , EXCEPTION, SharedData.throwableObject(t));
                // System.out.println("fail Api");
                //StopLoading();
            }
        });

    }

    private void blurrBackground() {

        if (isAdded()) {
            if (requireActivity() != null) {
                blurView.setVisibility(View.VISIBLE);
                float radius = 1f;

                //======================add disable button when load
        /*this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();*/
                this.getView().setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            return true;
                        }
                        return false;
                    }
                });

                View decorView = requireActivity().getWindow().getDecorView();
                ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

                Drawable windowBackground = decorView.getBackground();

                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(new RenderScriptBlur(requireContext()))
                        .setBlurRadius(radius)
                        .setBlurAutoUpdate(true)
                        .setHasFixedTransformationMatrix(false);
            }
        }

    }

    private void StartLoading() {
        if (isAdded()) {
            if (requireActivity() != null) {
                //dissable user interaction
                disableUserInteraction();

      /*  this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();*/
                this.getView().setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            return true;
                        }
                        return false;
                    }
                });

                loading_lav.setVisibility(View.VISIBLE);
                loading_lav.playAnimation();
            }
        }
    }

    private void StopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction

        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    void showMessage(SignupResponse verifyOTP_JSON_Reponse) {
        if (verifyOTP_JSON_Reponse != null) {
            if (verifyOTP_JSON_Reponse.getMessage() != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "verifyOTP_JSON_Reponse getMessage(): " + verifyOTP_JSON_Reponse.getMessage().toString());
                }
                Toast.makeText(getContext(), verifyOTP_JSON_Reponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "verifyOTP_JSON_Reponse getMessage() is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "verifyOTP_JSON_Reponse is null");
            }
            if (getContext() != null)
                new LogsHandlersUtils(getContext())
                        .getLogsDetails("Otp_fragment_response", email
                                , EXCEPTION, "verifyOTP_JSON_Reponse is null");
        }
    }
}
