package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.OtpResponseModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPasswordFragment extends Fragment {
    private TextInputEditText mEditTextForgetPasswordEmail;
    private MaterialButton mButtonRest;
    boolean isEmailValid;
    String email;
    NavController navController;
    private ImageButton backArrow;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private String message;

    Resources resources;

    MaterialTextView textViewForgetPasswordTitle, checkEmailMTV;

    String TAG = "FORGOT_PASSWORD_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        mButtonRest = view.findViewById(R.id.btnResetPassword);
        backArrow = view.findViewById(R.id.backBtn);
        loading_lav = view.findViewById(R.id.loading_lav);
        blurView = view.findViewById(R.id.blurView);
        textViewForgetPasswordTitle = view.findViewById(R.id.textViewForgetPasswordTitle);
        mEditTextForgetPasswordEmail = view.findViewById(R.id.editTextEmailForgetPassword);
        checkEmailMTV = view.findViewById(R.id.checkEmailMTV);
        StopLoading();
        navController = Navigation.findNavController(view);
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);

        backArrow.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                startActivity(intent);
            requireActivity().onBackPressed();
        });
        mButtonRest.setOnClickListener(v -> {
            mButtonRest.startAnimation(myAnim);

            //EmailValidation();
            blurrBackground();
            StartLoading();
            email = mEditTextForgetPasswordEmail.getText().toString().trim();

            // Check for a valid email address.
            if (Objects.requireNonNull(email.isEmpty())) {
                mEditTextForgetPasswordEmail.setError(resources.getString(R.string.email_reset_error));
                isEmailValid = false;
                StopLoading();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEditTextForgetPasswordEmail.setError(resources.getString(R.string.error_invalid_email));
                isEmailValid = false;
                StopLoading();
            } else {
                isEmailValid = true;
                email = mEditTextForgetPasswordEmail.getText().toString();
                generateOtp(email);

                // navController.navigate(R.id.action_forgetPasswordFragment3_to_otpFragment2);
            }


        });


        textViewForgetPasswordTitle.setText(resources.getString(R.string.reset_password));
        mEditTextForgetPasswordEmail.setHint(resources.getString(R.string.email));
        checkEmailMTV.setText(resources.getString(R.string.check_email_you_will_get_the_otp_code));
        mButtonRest.setText(resources.getString(R.string.reset));


    }

    private void generateOtp(String email) {
        Call<OtpResponseModel> call = ApiClient.getService().createOtp(email, true);

        call.enqueue(new Callback<OtpResponseModel>() {
            @Override
            public void onResponse(Call<OtpResponseModel> call, Response<OtpResponseModel> response) {
                try {
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        OtpResponseModel otpResponseModel = response.body();
                        //assert otpResponseModel != null;
                        StopLoading();
                        if (otpResponseModel != null) {
                            if (otpResponseModel.getMessage() != null) {
                                Toast.makeText(getContext(), otpResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("email", email);
                                navController.navigate(R.id.action_forgetPasswordFragment3_to_otpFragment2, bundle);
                                mEditTextForgetPasswordEmail.setText("");
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "otpResponse message is null");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "otpResponse Model is null");
                            }
                        }
                    } else {
                        try {
                            Gson gson = new GsonBuilder().create();
                            OtpResponseModel otpJSON_Response = new OtpResponseModel();
                            otpJSON_Response = gson.fromJson(response.errorBody().string(), OtpResponseModel.class);
                            if (response.code() == 404) {
                                showMessage(otpJSON_Response);
                                mEditTextForgetPasswordEmail.setText("");
                            } else {
                                showMessage(otpJSON_Response);
                                message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                                if (Common.isLoggingEnabled) {
                                    if (message != null)
                                        Log.d(TAG, "Response Status " + message.toString());
                                }
                                // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            new LogsHandlersUtils(getContext())
                                    .getLogsDetails("Otp_fragment_response", email
                                            , EXCEPTION, SharedData.caughtException(ex));
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                        }
                        StopLoading();
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpResponseModel> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.failed_to_send_otp), Toast.LENGTH_SHORT).show();
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getContext())
                        .getLogsDetails("Otp_fragment_response", email
                                , EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "ForgetPasswordFragement: onFailure");
                    t.printStackTrace();
                }
                StopLoading();
            }
        });

    }

    void showMessage(OtpResponseModel otpJSON_Response) {
        if (otpJSON_Response != null) {
            if (otpJSON_Response.getMessage() != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "otpJSON_Response getMessage(): " + otpJSON_Response.getMessage().toString());
                }
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), otpJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "otpJSON_Response getMessage() is null");
                }
            }
        } else {
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ForgetPassword_fragment_response", email
                        , EXCEPTION, "otpJSON_Response is null");
            }
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "otpJSON_Response is null");

            }
        }
    }

    private void StartLoading() {
        //dissable user interaction
        requireActivity().getWindow().setFlags(
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
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    private void blurrBackground() {
        blurView.setVisibility(View.VISIBLE);
        float radius = 1f;

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

    @Override
    public void onPause() {
        super.onPause();
        StopLoading();
    }
}
