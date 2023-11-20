package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.R.string.username_error;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.ConnectionReceiver;
import com.cedricapp.common.JWebToken;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.ToastUtil;
import com.cedricapp.utils.ValidationsHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.Stripe;

import java.util.Base64;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class SignupActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
    private MaterialButton mSignUpButton, mGotoLoginActivityButton;
    Animation slideDown;
    LinearLayout linearLayout;
    private MaterialTextView mAcceptPolicy;
    private TextInputEditText mEditTextUsername, mEditTextEmail, mEditTextPassword, mEditTextConfirmPassword;
    TextInputLayout textInputLayoutUserName, textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPass;
    boolean isEmailValid, isPasswordValid;
    //private SweetAlertDialog pDialog;
    LottieAnimationView loading_lav;
    private static final String BACKEND_URL = "https://obscure-refuge-36000.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret, message;
    private Stripe stripe;
    String status = "", id;
    int signupKey = 1;
    BlurView blurView;
    JWebToken incomingToken;
    SignupResponse signupResponse;
    public final static String SHARED_PREF_NAME = "log_user_info";
    private CrashlyticsCore Crashlytics;
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
    private String token, refresh_token;
    private DBHelper dbHelper;
    private String responseCodeMessage;

    private MaterialTextView existingUserMTV, signUpWithMTV;

    Resources resources;

    String TAG = "SIGN_UP_TAG";

    ConnectionReceiver connectionReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

    }

    void init(){
        SessionUtil.setlangCode(getApplicationContext(),"");
        //resources = Localization.setLanguage(SignupActivity.this,getResources());
        resources = getResources();
        //set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {

                }
            });
        }
        //test token
        // String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        // JWT jwt = new JWT(token) ;
        dbHelper = new DBHelper(SignupActivity.this);
        dbHelper.clearUsers();

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

    private void initiate() {
        Intent intent = getIntent();

        mSignUpButton = findViewById(R.id.btnSigUp);
        linearLayout = findViewById(R.id.linearLayout1);

        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        mEditTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        mGotoLoginActivityButton = findViewById(R.id.btnLoginSignUpActivity);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);
        textInputLayoutUserName = findViewById(R.id.textInputLayoutUsername);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPass = findViewById(R.id.textInputLayoutCpassword);
        mAcceptPolicy = findViewById(R.id.TextViewAcceptPolicy);
        textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
        textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);
        existingUserMTV = findViewById(R.id.existingUserMTV);
        signUpWithMTV = findViewById(R.id.signUpWithMTV);

        mAcceptPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        mAcceptPolicy.setLinkTextColor(Color.parseColor("#D5A243"));
        setLanguageToWidget();
        checkValidationLive();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initiate();
        checkConnection();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        //listener for signup button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSignUpButton.startAnimation(myAnim);
                if (ConnectionDetector.isConnectedWithInternet(SignupActivity.this)) {
                    setValidation();

                } else {
                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT);
                    // Toast.makeText(getApplicationContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //listener for Login button
        mGotoLoginActivityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGotoLoginActivityButton.startAnimation(myAnim);
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                //linearLayout.startAnimation(slideDown);
                finish();


            }
        });
    }

    @Override
    protected void onStop() {
        unregisterReceiver(connectionReceiver);
        super.onStop();
    }

    void setLanguageToWidget(){
        existingUserMTV.setText(resources.getString(R.string.existing_user));
        mGotoLoginActivityButton.setText(resources.getString(R.string.log_in));
        signUpWithMTV.setText(resources.getString(R.string.sign_up_with));
        mAcceptPolicy.setText(resources.getString(R.string.hyperlink));
        mSignUpButton.setText(resources.getString(R.string.sign_up_txt));
        textInputLayoutUserName.setHint(resources.getString(R.string.full_name));
        textInputLayoutEmail.setHint(resources.getString(R.string.email));
        textInputLayoutPassword.setHint(resources.getString(R.string.password));
        textInputLayoutConfirmPass.setHint(resources.getString(R.string.confirm_password));
    }

    private void checkValidationLive() {
        // Regex to check valid email.


        String regexEmail = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,64}+$";
        Pattern emailPattern = Pattern.compile(regexEmail);

        // Regex to check valid username.
        String regex = "^[a-zA-Z\\s]+";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextUsername.getText().length() > 0) {
                    if (!p.matcher(mEditTextUsername.getText().toString()).matches()) {

                        mEditTextUsername.setError(resources.getString(R.string.username_valid_error));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //  =======================email check==========
        mEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextEmail.getText().length() > 0) {
                    if (!emailPattern.matcher(mEditTextEmail.getText().toString()).matches()) {
                        mEditTextEmail.setError(resources.getString(R.string.error_invalid_email));
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //============check password========
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO needs to change
                if (mEditTextPassword.getText().length() > 0) {
                    textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
                    if (mEditTextPassword.getText().length() < 8) {
                        textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
                        mEditTextPassword.setError(resources.getString(R.string.error_password));
                        isPasswordValid = false;
                    } else if (!PASSWORD_PATTERN.matcher(mEditTextPassword.getText().toString()).matches()) {
                        textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);

                        mEditTextPassword.setError(resources.getString(R.string.error_password));
                    }
                }
                //mEditTextPassword.setSelection(mEditTextPassword.getText().length());
                checkConfirmPasswordField();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //=== check Confirm Password=======
        mEditTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO needs to change
                // mEditTextConfirmPassword.setSelection(mEditTextConfirmPassword.getText().length());
                checkConfirmPasswordField();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void checkConfirmPasswordField() {
        if (mEditTextConfirmPassword.getText().length() > 0) {
            textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);

            if (!(mEditTextPassword.getText().toString().equals(mEditTextConfirmPassword.getText().toString()))) {
                textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);

                mEditTextConfirmPassword.setError(resources.getString(R.string.error_match_password));
                //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
                isPasswordValid = false;
            } else {
                mEditTextConfirmPassword.setError(null);
                isPasswordValid = true;
            }
        }
    }


    private void setValidation() {
        ValidationsHelper validationsHelper = new ValidationsHelper();

        String name = mEditTextUsername.getText().toString().trim();
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();


        if (validationsHelper.isNullOrEmpty(name)) {

            mEditTextUsername.setError(resources.getString(username_error));
            mEditTextUsername.requestFocus();

        } else if (!validationsHelper.isValidName(name)) {

            mEditTextUsername.setError(getString(R.string.username_valid_error));
            mEditTextUsername.requestFocus();
        }
        // Check for a valid email address.
        else if (validationsHelper.isNullOrEmpty(email)) {

            mEditTextEmail.setError(resources.getString(R.string.email_error));
            mEditTextEmail.requestFocus();
            isEmailValid = false;
        } else if (!validationsHelper.isValidEmail(email)) {

            mEditTextEmail.setError(resources.getString(R.string.error_invalid_email));
            mEditTextEmail.requestFocus();
            isEmailValid = false;
        }

        // Check for a valid password.
        else if (validationsHelper.isNullOrEmpty(password)) {
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);

            mEditTextPassword.setError(resources.getString(R.string.password_error));
            mEditTextPassword.requestFocus();
            isPasswordValid = false;
        } else if (!validationsHelper.isValidPassword(password)) {

            mEditTextPassword.setError(resources.getString(R.string.error_password));
            mEditTextPassword.requestFocus();
            isPasswordValid = false;
        } else if (validationsHelper.isNullOrEmpty(mEditTextConfirmPassword.getText().toString())) {
            textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);

            mEditTextConfirmPassword.setError(resources.getString(R.string.error_cpassword));
            mEditTextConfirmPassword.requestFocus();
            //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        } else if (!(password.equals(mEditTextConfirmPassword.getText().toString()))) {
            textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);

            mEditTextConfirmPassword.setError(resources.getString(R.string.error_match_password));
            mEditTextConfirmPassword.requestFocus();
            //Toast.makeText(SignupActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            isEmailValid = true;
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
            textInputLayoutConfirmPass.setPasswordVisibilityToggleEnabled(true);

            String language = Localization.getLang(getApplicationContext());
            registerUser(name, email, password, language);

            /*String savedLanguage = SessionUtil.getlangCode(this);

            Locale current = getResources().getConfiguration().locale;
            String language = current.getLanguage();

            if (!savedLanguage.matches("")) {
                Localization.setLocale(this, savedLanguage).getResources();
                //SessionUtil.setlangCode(getApplicationContext(), languageToLoad);
                registerUser(name, email, password, savedLanguage);
            } else {
                if (language.matches("sv")) {
                    if (Common.isLoggingEnabled) {
                        Log.d("language", "System Language2: " + language);
                        Log.d("language", "Shared Preference Lnaguage2: " + savedLanguage);
                    }
                    SessionUtil.setlangCode(getApplicationContext(), "sv");
                    Localization.setLocale(this, "sv").getResources();


                    //SessionUtil.getlangCode(getApplicationContext());
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d("language", "ELSE System Language2: " + language);
                        Log.d("language", "ELSE Shared Preference Lnaguage2: " + savedLanguage);
                    }
                    SessionUtil.setlangCode(getApplicationContext(), "en");
                    Localization.setLocale(this, "en").getResources();
                    registerUser(name, email, password, "en");

                }

            }*/
            // SignupResponse data = new SignupResponse(name, email, password);


        }
    }


    //register user
    public void registerUser(String name, String email, String password, String lang) {
        // loadingPB.setVisibility(View.VISIBLE);
        //  sweetAlertDialog();
        blurrBackground();
        StartLoading();


        Call<SignupResponse> call = ApiClient.getService().createPost(name, email, password, lang);

        // on below line we are executing our method.
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                // this method is called when we get response from our api.

                StopLoading();
                if (response.isSuccessful()) {
                    signupResponse = response.body();
                    if (signupResponse != null) {
                        responseCodeMessage = ResponseStatus.getResponseCodeMessage(response.code(),resources);

                        if (signupResponse.getData() != null) {
                            if (signupResponse.getData().getEmail() != null
                                    && signupResponse.getData().getName() != null) {
                                if (!dbHelper.checkUser(signupResponse.getData().getEmail())) {
                                    //dbHelper.addUser(signupResponse);
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "User already exists in local database");
                                    }
                                }
                                SharedData.statuss = signupResponse.isStatus();

                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "User ID: " + SharedData.id);
                                    Log.d(TAG, "Message: " + signupResponse.getMessage().toString());
                                    Log.d(TAG, "Status: " + SharedData.statuss);
                                    Log.d(TAG, "Status: " + signupResponse.isStatus());
                                    Log.d(TAG, "Profile Activation: " + SharedData.profileActivation);
                                }
                                message = signupResponse.getMessage();

                                /* SharedData.profileActivation = signupResponse.isProfileActivated();*/

                                token = signupResponse.data.getAccess_token();
                                refresh_token = signupResponse.data.getRefresh_token();
                                SharedData.token = token;
                                SharedData.refresh_token = refresh_token;
                                SharedData.email = signupResponse.data.getEmail();
                                SharedData.id = signupResponse.data.getId();
                                SharedData.username = signupResponse.data.getName();
                                // Log.d("sub", token.toString());
                                //  SharedData.username = signupResponse.data.getName();

                                getFCMDeviceToken();
                                ToastUtil.showToastForFragment(getApplicationContext(), true, false, "" + signupResponse.getMessage().toString(), Toast.LENGTH_SHORT);
                                /*Toast.makeText(getApplicationContext(), signupResponse.getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();*/
                                Intent intent = new Intent(SignupActivity.this, VerifyEmailActivity.class);
                                intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                                intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                                intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                                intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                                intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
                                StopLoading();
                                startActivity(intent);


                            } else {
                                StopLoading();
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("SignupActivity_register", SharedData.email
                                                , "Information", "Email or Username is null");
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Email or Username is null");
                                    Log.e(TAG, "Sign Up data response: " + response.body().toString());
                                }
                                ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                //Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            StopLoading();
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("SignupActivity_register", SharedData.email
                                            , "Information", "Data is null");
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Data is null");
                            }
                            ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                            //Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        StopLoading();
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("SignupActivity_register", SharedData.email
                                        , "Information", "Sign Up response is null");

                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Sign Up response is null");
                            if(response.body()!=null)
                            Log.e(TAG, "Sign Up response: " + response.body().toString());
                        }
                        ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                        //Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    try {
                        Gson gson = new GsonBuilder().create();
                        SignupResponse signupJSON_Response = new SignupResponse();
                        signupJSON_Response = gson.fromJson(response.errorBody().string(), SignupResponse.class);
                        if (response.code() == 400) {
                            if (signupJSON_Response != null) {
                                if (signupJSON_Response.getErrors() != null) {
                                    if (signupJSON_Response.getErrors().getEmail() != null
                                            && signupJSON_Response.getErrors().getEmail().size() > 0) {
                                        getFCMDeviceToken();
                                        //ToastUtil.showToastForFragment(getApplicationContext(), true, false, signupJSON_Response.getErrors().getEmail().get(0).toString(), Toast.LENGTH_SHORT);
                                        /*Toast.makeText(getApplicationContext(), signupJSON_Response.getErrors().getEmail().get(0).toString(),
                                                Toast.LENGTH_SHORT).show();*/
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),  signupJSON_Response.getErrors().getEmail().get(0).toString(), Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                        snackbar.setTextColor(getResources().getColor(R.color.white));
                                     /*   snackbar.setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Perform action when the "OK" button is clicked
                                            }
                                        });*/
                                        snackbar.show();

                                    }
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "signupJSON_Response.getErrors() is null");
                                    }
                                    if (signupJSON_Response != null) {
                                        if (signupJSON_Response.getMessage() != null) {
                                            getFCMDeviceToken();
                                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), signupJSON_Response.getMessage().toString(), Snackbar.LENGTH_SHORT);
                                            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                            snackbar.setTextColor(getResources().getColor(R.color.white));
                                          /*  snackbar.setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // Perform action when the "OK" button is clicked
                                                }
                                            });*/
                                            snackbar.show();
                                          //  ToastUtil.showToastForFragment(getApplicationContext(), true, false, signupJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT);
                                            /*Toast.makeText(getApplicationContext(), signupJSON_Response.getMessage().toString(),
                                                    Toast.LENGTH_LONG).show();*/
                                        } else {
                                            ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);

                                            new LogsHandlersUtils(getApplicationContext())
                                                    .getLogsDetails("SignupActivity_register", SharedData.email
                                                            , "Information", "Error msg is null");/*Toast.makeText(getApplicationContext(), "Something Went Wrong",
                                                    Toast.LENGTH_SHORT).show();*/
                                        }
                                    } else {

                                        ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                        /*Toast.makeText(getApplicationContext(), "Something Went Wrong",
                                                Toast.LENGTH_SHORT).show();*/
                                    }
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "signupJSON_Response is null");
                                }
                                ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                /*Toast.makeText(getApplicationContext(), "Something Went Wrong",
                                        Toast.LENGTH_SHORT).show();*/
                            }
                        } else {

                            responseCodeMessage = ResponseStatus.getResponseCodeMessage(response.code(),resources);

                            ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);

                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("SignupActivity_register", SharedData.email
                                            , "Information", String.valueOf(response.code()));
                            if (Common.isLoggingEnabled) {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "Registration Activity: Response code is " + response.code() + ", and error body is " + response.errorBody().string());
                                } else {
                                    Log.e(TAG, "Registration Activity: Response code is " + response.code() + ", and error body is null");
                                }
                            }
                        }
                    } catch (Exception ex) {
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        FirebaseCrashlytics.getInstance().recordException(ex);

                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("SignupActivity_register", SharedData.email
                                        , EXCEPTION, SharedData.caughtException(ex));
                    }
                    StopLoading();

                    /*StopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Reponse is not successfull");
                    }
                    Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();*/
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                try {
                    StopLoading();
                    //  Toast.makeText(SignupActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Crashlytics.logException(t);
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("SignupActivity_register", SharedData.email
                                    , EXCEPTION, SharedData.throwableObject(t));
                } catch (Exception e) {
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("SignupActivity_register", SharedData.email
                                    , EXCEPTION, SharedData.caughtException(e));
                }

            }
        });

    }

    private void getInfoFromToken(String token) {
        Call<SignupResponse> call = ApiClient.getService().infoFromToken("Bearer " + token);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    responseCodeMessage = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    signupResponse = response.body();
                    if (signupResponse != null) {
                        if (signupResponse.getEmail() != null && signupResponse.getId() != null && signupResponse.getName() != null) {
                            SharedData.email = signupResponse.getEmail();
                            SharedData.id = signupResponse.getId();
                            SharedData.username = signupResponse.getName();
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, SharedData.email + "from token" + SharedData.id + "from id token");
                            }

                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("SignupActivity_get_Token API", SharedData.email
                                , EXCEPTION, SharedData.throwableObject(t));
            }
        });
    }

    private void StartLoading() {
        //dissable user interaction
        SignupActivity.this.getWindow().setFlags(
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
        SignupActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    @Override
    public void onPause() {
        super.onPause();
        // isOnline();
        StopLoading();
    }

    private void getFCMDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            if (Common.isLoggingEnabled)
                                Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "FCM TOken: " + token);
                        }
                        if (SharedData.id != null) {
                            FirebaseDatabase.getInstance().getReference("token").child(SharedData.id).setValue(token);
                        }// Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /* private boolean isOnline() {
         ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
         return (networkInfo != null && networkInfo.isConnected());
     }*/
    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        // MyApplication.getInstance().setConnectivityListener(this);
       /* if(isOnline()){
          //  Toast.makeText(SignupActivity.this, "Online", Toast.LENGTH_SHORT).show();
        }
        else {
            SharedData.showMessage(getApplicationContext());
        }*/
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void checkConnection() {
        connectionReceiver = new ConnectionReceiver();

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(connectionReceiver, intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected);
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {

        // initialize color and message
        String message = "";
        int color;

        // check condition
        if (isConnected) {

            // when internet is connected
            // set message
            // message = "Connected to Internet";

            // set text color
            color = Color.WHITE;

        } else {

            // when internet
            // is disconnected
            // set message
            message = "Not Connected to Internet";

            // set text color
            color = Color.RED;
            // initialize snack bar
            Snackbar snackbar = Snackbar.make(findViewById(R.id.btnSigUp), message, Snackbar.LENGTH_LONG);

            // initialize view
            View view = snackbar.getView();

            // Assign variable
            TextView textView = view.findViewById(R.id.snackbar_text);

            // set text color
            textView.setTextColor(color);

            // show snack bar
            snackbar.show();
        }


    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}