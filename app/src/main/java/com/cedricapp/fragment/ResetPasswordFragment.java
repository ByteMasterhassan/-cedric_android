package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.OtpResponseModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.ValidationsHelper;
import com.cedricapp.activity.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ResetPasswordFragment extends Fragment {
    private MaterialButton mNewPasswordResetButton;
    boolean isPasswordValid;
    String new_password, cpassword;
    private ImageButton backArrow;
    int clickCounter = 0;
    private long mLastClickTime = 0;
    BlurView blurView;
    LottieAnimationView loading_lav;

    private TextInputEditText mEditTextNewPassword, mEditTextNewConfirmPassword;
    TextInputLayout textInputLayoutNewPassword, textInputLayoutConfirmPassword;
    private View view1;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[$&+,:;=?@#|'<>.^*()%!-])" + //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,30}" +               //at least 4 characters
                    "$");
    private String email;
    Boolean isVerified;
    private String old_password;
    private OtpResponseModel passwordResponse;
    private String message;

    MaterialTextView textViewNewPasswordTitle;

    Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        init();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    private void init() {
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        backArrow = view1.findViewById(R.id.backBtn);
        mNewPasswordResetButton = view1.findViewById(R.id.btnResetPasswordNew);
        mEditTextNewPassword = view1.findViewById(R.id.editTextNewPassword);
        mEditTextNewConfirmPassword = view1.findViewById(R.id.editTextNewConfirmPassword);
        textInputLayoutNewPassword = view1.findViewById(R.id.textInputLayoutNewPassword);
        textInputLayoutConfirmPassword = view1.findViewById(R.id.textInputLayoutConfirmPassword);
        textViewNewPasswordTitle = view1.findViewById(R.id.textViewNewPasswordTitle);

        textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
        textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);

        email = getArguments().getString("email");
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Email: " + email);
        }
        isVerified = true;
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);

        mNewPasswordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewPasswordResetButton.startAnimation(myAnim);
                /*clickCounter = clickCounter + 1;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();*/

                // do your magic here
                // if (unitType == null || unitType.equalsIgnoreCase(null)) {
                //if (clickCounter <= 1) {
                setValidations();
                // }

                // }

            }


        });

        mEditTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO needs to change
                if (mEditTextNewPassword.getText().length() > 0) {
                    textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
                    if (mEditTextNewPassword.getText().length() < 8) {
                        textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
                        mEditTextNewPassword.setError(resources.getString(R.string.error_password));
                        isPasswordValid = false;
                    } else if (!PASSWORD_PATTERN.matcher(mEditTextNewPassword.getText().toString()).matches()) {
                        textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);

                        mEditTextNewPassword.setError(resources.getString(R.string.error_password));
                    }
                }
                //mEditTextNewPassword.setSelection(mEditTextNewPassword.getText().length());
                checkConfirmPasswordField();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditTextNewConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO needs to change
                //mEditTextNewConfirmPassword.setSelection(mEditTextNewConfirmPassword.getText().length());
                checkConfirmPasswordField();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        textViewNewPasswordTitle.setText(resources.getString(R.string.new_password));
        textInputLayoutNewPassword.setHint(resources.getString(R.string.password));
        textInputLayoutConfirmPassword.setHint(resources.getString(R.string.confirm_password));
        mNewPasswordResetButton.setText(resources.getString(R.string.reset));

    }

    void checkConfirmPasswordField() {
        if (mEditTextNewConfirmPassword.getText().length() > 0) {
            textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);

            if (!(mEditTextNewPassword.getText().toString().equals(mEditTextNewConfirmPassword.getText().toString()))) {
                textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);

                mEditTextNewConfirmPassword.setError(resources.getString(R.string.error_match_password));
                //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
                isPasswordValid = false;
            } else {
                mEditTextNewConfirmPassword.setError(null);
                isPasswordValid = true;
            }
        }
    }


    private void resetPassword(String email, Boolean isVerified, String old_password, String new_password) {
        startLoading();
        blurrBackground();
        Call<OtpResponseModel> call = ApiClient.getService().resetPassword(email, isVerified, old_password, new_password);
        call.enqueue(new Callback<OtpResponseModel>() {
            @Override
            public void onResponse(Call<OtpResponseModel> call, Response<OtpResponseModel> response) {
                try {
                    stopLoading();
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(Common.LOG, "Response Status " + message.toString());
                        }
                        passwordResponse = response.body();
                        if (response.code() == 201) {
                            Toast.makeText(getContext(), passwordResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), passwordResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        try {
                            Gson gson = new GsonBuilder().create();
                            OtpResponseModel otpResponseModel = new OtpResponseModel();
                            otpResponseModel = gson.fromJson(response.errorBody().string(), OtpResponseModel.class);
                            if (response.code() == 400) {
                                // StopLoading();
                                if (otpResponseModel != null && otpResponseModel.getMessage() != null) {
                                    if (getContext() != null)
                                        Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else if (response.code() == 404) {
                                //  StopLoading();
                                if (otpResponseModel != null && otpResponseModel.getMessage() != null) {
                                    if(getContext()!=null)
                                    Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // StopLoading();
                                message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                                if (Common.isLoggingEnabled) {
                                    Log.e(Common.LOG, "Reset Password Fragment Response Status " + message.toString());
                                }
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                // Toast.makeText(getContext(), otpResponseModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            //StopLoading();
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpResponseModel> call, Throwable t) {
                stopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext())
                            .getLogsDetails("Reset_fragment_Fragment", email
                                    , EXCEPTION, SharedData.throwableObject(t));
                }
                Toast.makeText(getContext(), resources.getString(R.string.failed_update_password), Toast.LENGTH_SHORT).show();
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void setValidations() {
        ValidationsHelper validationsHelper = new ValidationsHelper();
        //  old_password="ooo";
        old_password = mEditTextNewConfirmPassword.getText().toString();
        new_password = mEditTextNewPassword.getText().toString();
     /*   if (Objects.requireNonNull(new_password.isEmpty())) {

            mEditTextNewPassword.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
            textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(false);
        } else*/
        if (validationsHelper.isNullOrEmpty(new_password)) {
            textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
            mEditTextNewPassword.setError(resources.getString(R.string.password_error));
            mEditTextNewPassword.requestFocus();
            isPasswordValid = false;
        } else if (!validationsHelper.isValidPassword(new_password)) {
            mEditTextNewPassword.setError(resources.getString(R.string.error_password));
            textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
            mEditTextNewPassword.requestFocus();
            isPasswordValid = false;
        } else if (validationsHelper.isNullOrEmpty(mEditTextNewConfirmPassword.getText().toString())) {
            textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);

            mEditTextNewConfirmPassword.setError(resources.getString(R.string.error_cpassword));
            mEditTextNewConfirmPassword.requestFocus();
            //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        } else if (!(new_password.equals(mEditTextNewConfirmPassword.getText().toString()))) {
            textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);

            mEditTextNewConfirmPassword.setError(resources.getString(R.string.error_match_password));
            mEditTextNewConfirmPassword.requestFocus();
            isPasswordValid = false;
            //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        }/* else if (!(Objects.equals(new_password, old_password))) {
            mEditTextNewConfirmPassword.setError(getResources().getString(R.string.error_match_password));
            Toast.makeText(getContext(), getResources().getString(R.string.error_match_password), Toast.LENGTH_LONG).show();

            isPasswordValid = false;
       } */ else {

            isPasswordValid = true;

            textInputLayoutNewPassword.setPasswordVisibilityToggleEnabled(true);
            textInputLayoutConfirmPassword.setPasswordVisibilityToggleEnabled(true);
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                isPasswordValid = true;
            /*email = "mubasharhassan@techozon.com";
            isVerified = true;
            old_password="ooo";*/
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, email + "-email-" + isVerified + "-isverfieid-" + new_password + "-newPassword-" + old_password + "-oldPassword-");
                }
                resetPassword(email, isVerified, old_password, new_password);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }

        }

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

    private void startLoading() {
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

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction

        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
}