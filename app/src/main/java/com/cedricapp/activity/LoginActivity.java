package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.SharedData.isLoginScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.ConnectionReceiver;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.GetUserDetailsBack;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.AllergyStringModel;
import com.cedricapp.model.CredentialsModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.fragment.PaymentCategory;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.DeviceUtil;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SecureSessionUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.ToastUtil;
import com.cedricapp.utils.UserDetailsUtil;
import com.cedricapp.utils.ValidationsHelper;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.service.LocationTrack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener, GetUserDetailsBack {
    private MaterialButton mLoginButton, mGotoSigUpActivityButton;
    Animation slideUp, slideDown;
    private TextInputEditText mEditTextLoginUsernameOrEmail, mEditTextLoginPassword;
    private MaterialTextView mForgetPasswordTextView;
    boolean isEmailValid, isPasswordValid;
    LinearLayout linearLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    private CrashlyticsCore Crashlytics;
    private DBHelper dbHelper;
    private ScrollView scrollLayout;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    Boolean profileActivate;
    String emailLogin, passwordLogin;
    // LoginResponse loginResponse;
    LoginResponse loginResponse;
    // SweetAlertDialog pDialog;
    LottieAnimationView loading_lav;
    Toast toast;
    public final static String SHARED_PREF_NAME = "log_user_info";

    BlurView blurView;
    private TextInputLayout textInputLayoutLoginPassword, textInputLayoutLoginEmail;
    private String token, refresh_token;
    private String tokenExpiryDate;
    String m_androidId;
    Switch environmentSwitch;
    LinearLayout environmentSwitchLL;
    public String fcm_token;
    MaterialTextView btn_Ok, btn_No, btn_Yes, textViewTimer;
    RadioGroup ApiGroup;
    RadioButton testing, staging, beta, production, testingBeta;
    private boolean is_logged_in;
    FirebaseMessaging firebaseMessaging;

    //For Location
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private String gpsLocation;
    private Dialog loggedDialog;
    private float versionCode;
    private String versionName;
    private boolean stop;

    LocationTrack mLocationService;

    boolean mBound;
    private String message;
    boolean isUserDetailsCalled = false;

    private TextView envET;

    private Button submitEnvBtn;

    MaterialTextView loginMsgTV, textViewOr;

    String TAG = "LOGIN_TAG";

    Resources resources;

    MaterialCheckBox rememberMeCheckBox;

    SecureSessionUtil secureSessionUtil;

    MaterialTextView appVersionMTV, envMTV;

    ConnectionReceiver connectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isLoginScreen = true;

        // set status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.yellow));

        initWidgets();

    }

    void initWidgets(){
        /* environmentSwitch = findViewById(R.id.environmentSwitch);*/
        environmentSwitchLL = findViewById(R.id.environmentSwitchLL);
        ApiGroup = findViewById(R.id.apiGroup);
        testing = findViewById(R.id.testing);
        testingBeta = findViewById(R.id.testingBeta);
        staging = findViewById(R.id.staging);
        beta = findViewById(R.id.beta);
        envET = findViewById(R.id.envET);
        submitEnvBtn = findViewById(R.id.submitEnvBtn);
        production = findViewById(R.id.production);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        appVersionMTV = findViewById(R.id.appVersionMTV);
        envMTV = findViewById(R.id.envMTV);

        linearLayout = findViewById(R.id.linearLayout2);
        mEditTextLoginUsernameOrEmail = findViewById(R.id.editTextLoginUsernameOrEmail);
        mEditTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        mGotoSigUpActivityButton = findViewById(R.id.btnSigUpLoginActivity);
        mForgetPasswordTextView = findViewById(R.id.textViewForgetPassword);
        mLoginButton = findViewById(R.id.btnLogin);
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);

        textInputLayoutLoginPassword = findViewById(R.id.textInputLayoutLoginPassword);
        textInputLayoutLoginEmail = findViewById(R.id.textInputLayoutLoginEmail);
        textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(true);
        loginMsgTV = findViewById(R.id.loginMsgTV);
        textViewOr = findViewById(R.id.textViewOr);

    }



    private void showCredentials() {
        CredentialsModel credentialsModel = secureSessionUtil.getCredentials();
        if ((credentialsModel != null && credentialsModel.getEmail() != null && credentialsModel.getPassword() != null)
                && (!credentialsModel.getEmail().isEmpty() && !credentialsModel.getPassword().isEmpty())) {
            mEditTextLoginUsernameOrEmail.setText(credentialsModel.getEmail());
            mEditTextLoginPassword.setText(credentialsModel.getPassword());
            rememberMeCheckBox.setChecked(true);
        } else {
            mEditTextLoginUsernameOrEmail.setText("");
            mEditTextLoginPassword.setText("");
            rememberMeCheckBox.setChecked(false);
        }
        StopLoading();
    }

    private void checkIsLocationAvailable(LocationTrack locationTrack) {
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            //get address
            gpsLocation = LocationUtil.getCompleteAddressString(latitude, longitude, getApplicationContext());
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Latitude from GPS: " + latitude + "\nLongitude from GPS: " + longitude + "\nGPS Location: " + gpsLocation.toString());
                // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            }

            //Log.d("loc", gpsLocation.toString());
            if (gpsLocation != null && !gpsLocation.matches("")) {
                SharedData.previousGpsLocation = gpsLocation;
                SessionUtil.setLoggedLocation(SharedData.previousGpsLocation, getApplicationContext());
                SessionUtil.setLoggedLongitude("" + longitude, getApplicationContext());
                SessionUtil.setLoggedLatitude("" + latitude, getApplicationContext());
            } else {
                SharedData.previousGpsLocation = " ";
            }
            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            DialogUtil.showSettingsAlert(LoginActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if (locationTrack != null)
            locationTrack.stopListener();*/
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

    void init(){
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out);
        SessionUtil.setlangCode(getApplicationContext(), "");
        //resources = Localization.setLanguage(LoginActivity.this, getResources());
        resources = Localization.setLanguageOnLogin(LoginActivity.this, getResources());
        secureSessionUtil = new SecureSessionUtil(getApplicationContext());
        dbHelper = new DBHelper(LoginActivity.this);
        dbHelper.clearUsers();

        // for Analytics
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseMessaging = FirebaseMessaging.getInstance();

        setAPIEnvironment();

        versionCode = BuildConfig.VERSION_CODE;
        versionName = BuildConfig.VERSION_NAME;
        m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Android Device ID " + m_androidId);
        }

        //setStringToWidgets();
        showCredentials();

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.startAnimation(myAnim);
                if (ConnectionDetector.isConnectedWithInternet(LoginActivity.this)) {
                    setLoginValidation();
                    // checkConnection();
                } else {
                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT);
                    //Toast.makeText(getApplicationContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mForgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                isLoginScreen = false;
                startActivity(intent);
            }
        });

        //listener for Login button
        mGotoSigUpActivityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGotoSigUpActivityButton.startAnimation(myAnim);
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                isLoginScreen = false;
                startActivity(intent);
                linearLayout.startAnimation(slideUp);
            }
        });

        submitEnvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!envET.getText().toString().isEmpty()) {
                    if ((envET.getText().toString().contains("https://") ||
                            envET.getText().toString().contains("http://") ||
                            envET.getText().toString().contains("https") ||
                            envET.getText().toString().contains("http")) ||
                            envET.getText().toString().contains("/")) {
                        envET.setError("Please do not add https:// or http:// or avoid /");
                    } else {
                        String url = "https://";
                        url = url + envET.getText().toString() + "/v2/";
                        //url.concat(envET.getText().toString()).concat("/v2/");
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "URL after concatination is " + url);
                        }
                        SessionUtil.saveAPI_URL(getApplicationContext(), url);
                        Toast.makeText(getApplicationContext(), "URL Saved", Toast.LENGTH_SHORT).show();
                        environmentSwitchLL.setVisibility(View.GONE);
                        setAPIEnvironment();
                        mEditTextLoginUsernameOrEmail.setText("");
                        mEditTextLoginPassword.setText("");

                    }
                } else {
                    envET.setError("Field is empty");
                }
            }
        });
    }


    void setAPIEnvironment() {
        String environment = SessionUtil.getAPP_Environment(LoginActivity.this);
        if (SessionUtil.getAPI_URL(getApplicationContext()).matches("")) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "API enviroment is " + environment);
            }

            if (environment.matches("testing")) {
                SharedData.BASE_URL = Common.TESTING_BASE_URL;
                testing.setChecked(true);
                /* environmentSwitch.setChecked(true);*/
            } else if (environment.matches("stagging")) {
                SharedData.BASE_URL = Common.STAGING_BASE_URL;
                staging.setChecked(true);

            } else if (environment.matches("beta")) {
                SharedData.BASE_URL = Common.BETA_BASE_URL;
                beta.setChecked(true);
            } else if (environment.matches("testing_beta")) {
                SharedData.BASE_URL = Common.TESTING_BETA_BASE_URL;
                testingBeta.setChecked(true);
            } else {
                SharedData.BASE_URL = Common.PRODUCTION_BASE_URL;
                production.setChecked(true);
            }
        } else {
            envET.setText(SessionUtil.getAPI_URL(getApplicationContext()));
            SharedData.BASE_URL = SessionUtil.getAPI_URL(getApplicationContext());
        }/* else {
            SharedData.BASE_URL = Common.TESTING_BASE_URL;
            environmentSwitch.setChecked(false);

           *//* environmentSwitch.setChecked(false);*//*
        }*/
    }

    private void checkValidationLive() {
        String regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}";
        Pattern emailPattern = Pattern.compile(regex);

        //  =======================email check==========
        mEditTextLoginUsernameOrEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextLoginUsernameOrEmail.getText().length() > 0) {
                    if (!emailPattern.matcher(mEditTextLoginUsernameOrEmail.getText().toString()).matches()) {

                        mEditTextLoginUsernameOrEmail.setError(getResources().getString(R.string.error_invalid_email));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //============check password========
        mEditTextLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextLoginPassword.getText().length() > 0) {
                    textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(true);
                    if (mEditTextLoginPassword.getText().length() < 4) {

                        // mEditTextLoginPassword.setError(getResources().getString(R.string.error_weak_password));
                        //textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(false);
                        isPasswordValid = false;
                    } else if (mEditTextLoginPassword.getText().length() < 6) {

                        // textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(false);
                        //  mEditTextLoginPassword.setError(getResources().getString(R.string.error_medium_password));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        StartLoading();
        blurrBackground();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                LocationUtil.getLocationByGeoLocationAPI(getApplicationContext());

                appVersionMTV.setText("V " + versionName);
                if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                    envMTV.setVisibility(View.VISIBLE);
                    envMTV.setText("Env: " + SessionUtil.getAPP_Environment(getApplicationContext()));
                } else {
                    envMTV.setVisibility(View.GONE);
                }
                checkConnection();
                checkValidationLive();
            }
        },800);

    }


    @Override
    protected void onStop() {
        StopLoading();
        unregisterReceiver(connectionReceiver);
        super.onStop();

    }


    private void setLoginValidation() {
        ValidationsHelper validationsHelper = new ValidationsHelper();

        emailLogin = Objects.requireNonNull(mEditTextLoginUsernameOrEmail.getText()).toString().trim();
        passwordLogin = Objects.requireNonNull(mEditTextLoginPassword.getText()).toString().trim();
        // Check for a valid email address.


        if (validationsHelper.isNullOrEmpty(emailLogin)) {

            mEditTextLoginUsernameOrEmail.setError(getResources().getString(R.string.email_error));
            mEditTextLoginUsernameOrEmail.requestFocus();
            isEmailValid = false;
        } else if (!validationsHelper.isValidEmail(emailLogin)) {

            mEditTextLoginUsernameOrEmail.setError(getResources().getString(R.string.error_invalid_email));
            mEditTextLoginUsernameOrEmail.requestFocus();
            isEmailValid = false;
        }

        // Check for a valid password.
        else if (validationsHelper.isNullOrEmpty(passwordLogin)) {

            mEditTextLoginPassword.setError(getResources().getString(R.string.password_error));
            mEditTextLoginPassword.requestFocus();
            isPasswordValid = false;
            /* textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(false);*/
        } else {
            textInputLayoutLoginPassword.setPasswordVisibilityToggleEnabled(true);
            isPasswordValid = true;
            isEmailValid = true;

            if (emailLogin.equals(BuildConfig.email) && passwordLogin.equals(BuildConfig.password)) {
                //environmentSwitch.setVisibility(View.VISIBLE);
                String enviroment = SessionUtil.getAPP_Environment(LoginActivity.this);
                if (enviroment.matches("testing")) {
                    testing.setChecked(true);
                } else if (enviroment.matches("stagging")) {
                    staging.setChecked(true);

                } else if (enviroment.matches("beta")) {
                    beta.setChecked(true);
                } else if (enviroment.matches("testing_beta")) {
                    testingBeta.setChecked(true);
                } else {
                    production.setChecked(true);
                }/* else {
                    beta.setChecked(true);
                }*/
                environmentSwitchLL.setVisibility(View.VISIBLE);
                ApiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.testing:
                                SessionUtil.saveAPI_Environment(getApplicationContext(), "testing");
                                environmentSwitchLL.setVisibility(View.GONE);
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                setAPIEnvironment();
                                mEditTextLoginUsernameOrEmail.setText("");
                                mEditTextLoginPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Now, you are in testing evironment", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.testingBeta:
                                SessionUtil.saveAPI_Environment(getApplicationContext(), "testing_beta");
                                environmentSwitchLL.setVisibility(View.GONE);
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                setAPIEnvironment();
                                mEditTextLoginUsernameOrEmail.setText("");
                                mEditTextLoginPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Now, you are in testing beta evironment", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.staging:
                                SessionUtil.saveAPI_Environment(getApplicationContext(), "stagging");
                                environmentSwitchLL.setVisibility(View.GONE);
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                setAPIEnvironment();
                                mEditTextLoginUsernameOrEmail.setText("");
                                mEditTextLoginPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Now, you are in staging evironment", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.beta:
                                SessionUtil.saveAPI_Environment(getApplicationContext(), "beta");
                                environmentSwitchLL.setVisibility(View.GONE);
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                setAPIEnvironment();
                                mEditTextLoginUsernameOrEmail.setText("");
                                mEditTextLoginPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Now, you are in beta evironment", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.production:
                                SessionUtil.saveAPI_Environment(getApplicationContext(), "production");
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                environmentSwitchLL.setVisibility(View.GONE);
                                SessionUtil.saveAPI_URL(getApplicationContext(), "");
                                setAPIEnvironment();
                                mEditTextLoginUsernameOrEmail.setText("");
                                mEditTextLoginPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Now, you are in production evironment", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

            } else {
                loginUser(emailLogin, passwordLogin);
            }
        }

    }


    public void loginUser(String email, String password) {
        blurrBackground();
        StartLoading();

        Call<LoginResponse> call = ApiClient.getService().loginDataPost(email, password, m_androidId);

        // on below line we are executing our method.
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                // this method is called when we get response from our api.

                try {
                    if (response.isSuccessful()) {
                        dbHelper.clearUsers();
                        loginResponse = response.body();
                        SessionUtil.setActivityDownloadedDate(getApplicationContext(), "");
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Login Response: " + message.toString());
                        }
                        if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                        if (loginResponse != null) {
                            if (email != null) {
                                SharedData.email = email;
                            }
                            // SessionUtil.setLoggedEmail(getApplicationContext(), emailLogin);
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Login Response: " + loginResponse.toString());
                            }
                            if (loginResponse.getData() != null) {
                                is_logged_in = loginResponse.getData().getLoggedIn();
                                //if user logged in other device then this condition will be run
                                if (is_logged_in) {
                                    if (loginResponse.getData().getUser() != null && loginResponse.getData().getUser().getId() != null) {
                                        SharedData.id = loginResponse.getData().getUser().getId();
                                        SharedData.token = loginResponse.getData().getAccess_token();
                                        getFCMDeviceToken(null, null, null);
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "User is null or User ID is null");
                                        }
                                        Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    redirectToSignUpFlow(loginResponse);
                                }

                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Login response data is null");
                                }
                                StopLoading();
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                                , "Information", "Login response data is null");

                            }

                            /*else if (loginResponse.getEmail().isEmpty() || (!loginResponse.getEmail().matches(emailLogin))) {

                                StopLoading();
                                Toast.makeText(LoginActivity.this, "Email is not exist!",
                                        Toast.LENGTH_SHORT).show();
                            }*/

                        } else {
                            StopLoading();
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Login Response object is null");
                            }
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                            , "Information", "Login Response object is null");
                            ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                            //Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }


                        //postDataToSQLite();


                        // dbHelper.addUser(loginResponse);
                        // userDatafromApi(loginResponse);

                        // pDialog.hide();


                    } else {
                        StopLoading();

                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Login Response: " + message.toString());
                        }
                        if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        //StopLoading();
                        /*System.out.println(" Some thing not working fine========");
                        Toast.makeText(LoginActivity.this, "Either your email or password is wrong!", Toast.LENGTH_SHORT).show();*/

                        Gson gson = new GsonBuilder().create();
                        LoginResponse loginJSON_Response = new LoginResponse();
                        loginJSON_Response = gson.fromJson(response.errorBody().string(),
                                LoginResponse.class);
                        if (response.code() == 401) {
                            if (loginJSON_Response != null) {
                                if (loginJSON_Response.getMessage() != null) {
                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, "" + loginJSON_Response.getMessage(), Toast.LENGTH_SHORT);
                                    if (loginJSON_Response.getMessage().matches(resources.getString(R.string.account_not_active))) {
                                        showStandardDialog();
                                    }
                                } else {
                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                    /*Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();*/
                                    if (loginJSON_Response.getMessage().matches(resources.getString(R.string.email_not_verified))) {
                                        if (loginResponse.getData() != null) {
                                            if (loginResponse.getData().getPlatform() != null) {
                                                SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                                            } else {
                                                SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                                            }
                                        }
                                        Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                                        SharedData.email = mEditTextLoginUsernameOrEmail.getText().toString();
                                        intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                                        isLoginScreen = false;
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            } else {
                                ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.code() == 404) {
                            if (loginJSON_Response != null) {
                                if (loginJSON_Response.getMessage() != null) {
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "Login JSON Repsonse: " + loginJSON_Response.toString());
                                        if (loginJSON_Response.getMessage() != null) {
                                            Log.e(TAG, "Message Login JSON Repsonse: " + loginJSON_Response.getMessage());
                                        } else {
                                            Log.e(TAG, "Login JSON Repsonse message is null");
                                        }
                                    }
                                    new LogsHandlersUtils(getApplicationContext())
                                            .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                                    , "Information", "Login JSON Repsonse message is null");

                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, "" + loginJSON_Response.getMessage(), Toast.LENGTH_SHORT);
                                    //Toast.makeText(getApplicationContext(), "" + loginJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                    //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Login JSON Repsonse is null");
                                }
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                                , "Information", "Login JSON Repsonse is null");
                            }
                        } else {
                            if (loginJSON_Response != null) {
                                if (loginJSON_Response.getMessage() != null) {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "Login JSON Repsonse in else: " + loginJSON_Response.toString());
                                        if (loginJSON_Response.getMessage() != null) {
                                            Log.e(TAG, "Message Login JSON Repsonse: " + loginJSON_Response.getMessage());
                                        } else {
                                            Log.e(TAG, "Login JSON Repsonse message in else is null");
                                        }
                                    }
                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, "" + loginJSON_Response.getMessage(), Toast.LENGTH_SHORT);
                                    //Toast.makeText(getApplicationContext(), "" + loginJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                }//Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();

                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Login JSON Repsonse in else is null");
                                }
                                ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        StopLoading();

                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                        //Log.e(TAG, "Login Actvity exception: " + Log.getStackTraceString(ex));
                    }
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                    , EXCEPTION, SharedData.caughtException(ex));
                }

            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                try {
                    StopLoading();

                    // Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    //Crashlytics.logException(t);
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                    , EXCEPTION, SharedData.throwableObject(t));
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    //  Toast.makeText(SignupActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("loginActiviy_LoginAPI", emailLogin
                                    , EXCEPTION, SharedData.caughtException(e));
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }


    void redirectForSubscription(LoginResponse.Data.User user, LoginResponse.Data.User.Profile profile, LoginResponse.Data.User.Subscription subscription) {
        if (loginResponse.getData() != null) {
            if (loginResponse.getData().getPlatform() != null) {
                SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
            } else {
                SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
            }
        }
        if (profile != null) {
            ToastUtil.showToastForFragment(getApplicationContext(), true, false, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT);
                                                /*Toast.makeText(LoginActivity.this, loginResponse.getMessage(),
                                                        Toast.LENGTH_SHORT).show();*/
            Intent intent = null;
            if (user != null && user.getAllergies() != null) {
                intent = new Intent(LoginActivity.this, PaymentCategory.class);
                String allergyIDs = getAllergyIDs(user.getAllergies()).getAllergyID();
                String allergyName = getAllergyIDs(user.getAllergies()).getAllergyName();

                intent.putExtra(Common.SESSION_USER_ALLERGY_IDS, allergyIDs);
                intent.putExtra(Common.SESSION_USER_ALLERGIES, allergyName);

            } else {
                if (profile.getFood_preference_id() == null) {
                    intent = new Intent(LoginActivity.this, FoodPreferencesActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, PaymentCategory.class);
                }
                intent.putExtra(Common.SESSION_USER_ALLERGY_IDS, "");
                intent.putExtra(Common.SESSION_USER_ALLERGIES, "");

            }
            if (rememberMeCheckBox.isChecked()) {
                secureSessionUtil.saveCredentials(new CredentialsModel(SharedData.email, passwordLogin));
            } else {
                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
            }
            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
            if (subscription != null && subscription.getStripe_id() != null) {
                intent.putExtra(Common.SESSION_SUBSCRIPTION_ID, subscription.getStripe_id());
            }

            if (profile.getProduct_id() != null) {
                intent.putExtra(Common.SESSION_USER_PRODUCT_ID, profile.getProduct_id());
            }
            if (profile.getFood_preference_id() != null) {
                intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, profile.getFood_preference_id());
            }
            if (profile.getFood_preference() != null) {
                intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE, profile.getFood_preference());
            }
            if (profile.getWeight() != null) {
                intent.putExtra(Common.SESSION_USER_WEIGHT, profile.getWeight());
            }
            if (profile.getHeight() != null) {
                intent.putExtra(Common.SESSION_USER_HEIGHT, profile.getHeight());
            }
            if (profile.getAge() != null) {
                intent.putExtra(Common.SESSION_USER_AGE, profile.getAge());
            }
            if (profile.getGender() != null) {
                intent.putExtra(Common.SESSION_USER_GENDER, profile.getGender());
            }
            if (profile.getGoal_id() != null) {
                intent.putExtra(Common.SESSION_USER_GOAL_ID, profile.getGoal_id());
            }
            if (profile.getGoal() != null) {
                intent.putExtra(Common.SESSION_USER_GOAL, profile.getGoal());
            }
            if (profile.getLevel_id() != null) {
                intent.putExtra(Common.SESSION_USER_LEVEL_ID, Integer.parseInt(profile.getLevel_id()));
            }
            if (profile.getGoal_id() != null) {
                intent.putExtra(Common.SESSION_USER_GOAL_ID, Integer.parseInt(profile.getGoal_id()));
            }
            if (profile.getLevel() != null) {
                intent.putExtra(Common.SESSION_USER_LEVEL, profile.getLevel());
            }
            if (profile.getUnit() != null) {
                intent.putExtra(Common.SESSION_UNIT_TYPE, profile.getUnit());
            }
            /*String allergyIDs = "";
            String allergyName = "";*/
            if (profile.getLang() != null) {
                SessionUtil.setlangCode(getApplicationContext(), profile.getLang());
            }
                                                /*if (user != null) {
                                                    allergyIDs = getAllergyIDs(user.getAllergies()).getAllergyID();
                                                    allergyName = getAllergyIDs(user.getAllergies()).getAllergyName();
                                                    if (user.getAllergies() != null) {
                                                        intent.putExtra(Common.SESSION_USER_ALLERGY_IDS, allergyIDs);
                                                        intent.putExtra(Common.SESSION_USER_ALLERGIES, allergyName);
                                                    }
                                                }*/

            StopLoading();
            isLoginScreen = false;
            startActivity(intent);
            finish();

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Profile is null");
            }
            StopLoading();
            ToastUtil.showToastForFragment(getApplicationContext(), true, false, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
            /*Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();*/
        }
    }

    private void showLoggedDialog(String fcm_token, boolean is_logged_in) {
        loggedDialog = new Dialog(LoginActivity.this);
        loggedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loggedDialog.setCancelable(false);
        loggedDialog.setContentView(R.layout.custom_alert_dialog_box);
        loggedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView alertMTV = loggedDialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = loggedDialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.you_have_already_logged_in_on_another_device));

        TextView textView = loggedDialog.findViewById(R.id.dialog_description);
        textView.setText(resources.getString(R.string.do_you_want_to_logout_from_n_that_device));


        btn_No = loggedDialog.findViewById(R.id.btn_left);
        btn_No.setText(resources.getString(R.string.btn_no));
        btn_Yes = loggedDialog.findViewById(R.id.btn_right);
        btn_Yes.setText(resources.getString(R.string.btn_yes));
        textViewTimer = loggedDialog.findViewById(R.id.dialog_Timer);
        stop = false;
        setOtpTimer();
        SharedData.device_id = m_androidId;

        btn_No.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                btn_No.setBackgroundDrawable(getApplicationContext().getResources()
                        .getDrawable(R.drawable.btn_background_dialog_left_click));
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    setUserDetailsAPICall(fcm_token, SharedData.device_id, false, true);
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                stop = true;
                loggedDialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Yes.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "FCM Token is on click yes in dialog box " + fcm_token);
                }
                StartLoading();
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    setUserDetailsAPICall(fcm_token, SharedData.device_id, is_logged_in, false);
                } else {
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                loggedDialog.dismiss();
            }


        });
        loggedDialog.show();

    }

    private void showStandardDialog() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_blocked_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SessionUtil.setAccessToken(getApplicationContext(), "");
        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.you_are_not_authorized_to_use_the_application));

        btn_Ok = dialog.findViewById(R.id.btn_Ok);
        btn_Ok.setText(resources.getString(R.string.ok_btn));
        //btn_Continue = dialog.findViewById(R.id.btn_right);
        TextView textView = dialog.findViewById(R.id.dialog_blocked_description);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(resources.getString(R.string.admin_email));


        btn_Ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();*/
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);

                btn_Ok.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.btn_background_dialog_blocked));
                //settingObject.activityDataSync(getApplicationContext(), "splash");
                SessionUtil.logout(getApplicationContext());
                //finish();
                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        dialog.show();


    }

    private void setUserDetailsAPICall(String fcm_token, String device_id, boolean is_logged_in, boolean is_cancel) {

        if (SharedData.token == null) {
            //SharedData.token=SessionUtil.getAccessToken(getApplicationContext());
            if (Common.isLoggingEnabled) {
                Log.e("token  in setUserDetailsAPICall method in LoginActivity class: ", SharedData.token);
            }
            if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                Toast.makeText(getApplicationContext(), "Access Token is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            /*if (gpsLocation != null && !gpsLocation.matches("")) {
                SharedData.location = gpsLocation;
            }*/
            if (SharedData.location != null && !SharedData.location.matches("")) {

                isUserDetailsCalled = true;
                new UserDetailsUtil(getApplicationContext(), LoginActivity.this).sendUserDetail("Bearer " +
                                SharedData.token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(), device_id, SharedData.location,
                        WeekDaysHelper.getUTC_Time(), fcm_token, is_logged_in, is_cancel, versionName, String.valueOf(versionCode), "android", "login");

            } else {

                isUserDetailsCalled = true;
                new UserDetailsUtil(getApplicationContext(), LoginActivity.this).sendUserDetail("Bearer " +
                                SharedData.token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(), device_id, "UNKNOWN",
                        WeekDaysHelper.getUTC_Time(), fcm_token, is_logged_in, is_cancel, versionName, String.valueOf(versionCode), "android", "login");
                Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_location), Toast.LENGTH_LONG);

                if (Common.isLoggingEnabled) {
                    Log.e(TAG, SharedData.location + "location not Called");
                }
            }
        }
    }

    AllergyStringModel getAllergyIDs(ArrayList<LoginResponse.Data.User.Allergies> allergies) {
        String allergyIDs = "";
        String allergyNames = "";
        AllergyStringModel allergyStringModel = new AllergyStringModel();
        if (allergies != null) {
            ArrayList<String> allergyIdList = new ArrayList<>();
            ArrayList<String> allergyNameList = new ArrayList<>();
            for (int i = 0; i < allergies.size(); i++) {
                if (allergies.get(i).getId() != null && allergies.get(i).getName() != null) {
                    allergyIdList.add(allergies.get(i).getId());
                    allergyNameList.add(allergies.get(i).getName());
                }
            }
            allergyIDs = TextUtils.join(",", allergyIdList);
            allergyNames = TextUtils.join(",", allergyNameList);
            allergyStringModel.setAllergyID(allergyIDs);
            allergyStringModel.setAllergyName(allergyNames);
        }
        return allergyStringModel;
    }


    private void StartLoading() {
        //dissable user interaction

        LoginActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void StopLoading() {
        //Enable user interaction
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);

        LoginActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    @Override
    public void onPause() {

        //StopLoading();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        // MyApplication.getInstance().setConnectivityListener(this);
        /*if (!isOnline()) {
            SharedData.showMessage(getApplicationContext());
        }*/
    }

    @Override
    public void getUserDataBack(LoginResponse loginResponse) {
        is_logged_in = true;
        redirectToSignUpFlow(loginResponse);
    }

    void redirectToSignUpFlow(LoginResponse loginResponse) {
        if (loginResponse.getData().getAccess_token() != null) {
            token = loginResponse.getData().getAccess_token();
            SharedData.token = token;
            SessionUtil.setAccessToken(getApplicationContext(), SharedData.token);
            tokenExpiryDate = loginResponse.getData().getToken_expires_at();

            dbHelper.addUser(loginResponse);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Token Expiry date: " + tokenExpiryDate);
                Log.d(TAG, "Access token: " + SharedData.token);
            }
            if (loginResponse.getData().getUser() != null) {
                SharedData.id = loginResponse.getData().getUser().getId();

                SharedData.username = loginResponse.getData().getUser().getName();
                SharedData.email = loginResponse.getData().getUser().getEmail();
                SharedData.isCompleted = loginResponse.getData().getUser().getIs_profile_completed();
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "User ID is  " + SharedData.id);
                    Log.d(TAG, "User name is   " + SharedData.username);
                    Log.d(TAG, "User email is   " + SharedData.email);
                    Log.d(TAG, "User profile completed status is  " + SharedData.isCompleted);
                }
                if (SharedData.isCompleted != null) {
                    //Data
                    LoginResponse.Data data = loginResponse.getData();
                    //User
                    LoginResponse.Data.User user = data.getUser();
                    //User profile
                    LoginResponse.Data.User.Profile profile = user.getProfile();
                    //User Subscription
                    LoginResponse.Data.User.Subscription subscription = user.getSubscription();

                    if (SharedData.isCompleted.matches("0") && profile != null && subscription == null) {
                        if (user.getIs_email_verified() != null && !user.getIs_email_verified()) {
                            if (loginResponse.getData().getPlatform() != null) {
                                SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                            } else {
                                SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                            }
                            if (rememberMeCheckBox.isChecked()) {
                                secureSessionUtil.saveCredentials(new CredentialsModel(emailLogin, passwordLogin));
                            } else {
                                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
                            }
                            StopLoading();
                            Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                            SharedData.email = mEditTextLoginUsernameOrEmail.getText().toString();
                            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
                            isLoginScreen = false;

                            startActivity(intent);
                            finish();
                        } else if (profile.getUnit() == null && profile.getGender() == null && profile.getWeight() == null && profile.getHeight() == null && profile.getAge() == null) {
                            if (loginResponse.getData() != null) {
                                if (loginResponse.getData().getPlatform() != null) {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                                } else {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                                }
                            }
                            if (rememberMeCheckBox.isChecked()) {
                                secureSessionUtil.saveCredentials(new CredentialsModel(emailLogin, passwordLogin));
                            } else {
                                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
                            }
                            Intent intent = new Intent(LoginActivity.this, ChooseUnitTypeActivity.class);
                            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
                            StopLoading();
                            isLoginScreen = false;
                            startActivity(intent);
                            finish();
                        }
                    } else if (SharedData.isCompleted.matches("1") && profile != null && subscription == null) {
                        if (profile.getUnit() == null && profile.getGender() == null && profile.getWeight() == null && profile.getHeight() == null && profile.getAge() == null) {
                            if (loginResponse.getData() != null) {
                                if (loginResponse.getData().getPlatform() != null) {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                                } else {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                                }
                            }
                            if (rememberMeCheckBox.isChecked()) {
                                secureSessionUtil.saveCredentials(new CredentialsModel(emailLogin, passwordLogin));
                            } else {
                                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
                            }
                            StopLoading();
                            Intent intent = new Intent(LoginActivity.this, ChooseUnitTypeActivity.class);
                            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
                            isLoginScreen = false;
                            startActivity(intent);
                            finish();
                        } else if (profile.getGoal_id() == null && profile.getLevel_id() == null && profile.getGoal() == null && profile.getLevel() == null) {
                            if (loginResponse.getData() != null) {
                                if (loginResponse.getData().getPlatform() != null) {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                                } else {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                                }
                            }
                            if (rememberMeCheckBox.isChecked()) {
                                secureSessionUtil.saveCredentials(new CredentialsModel(emailLogin, passwordLogin));
                            } else {
                                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
                            }
                            StopLoading();
                            Intent intent = new Intent(LoginActivity.this, GoalsActivity.class);
                            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);

                            intent.putExtra(Common.SESSION_USER_HEIGHT, profile.getHeight());
                            intent.putExtra(Common.SESSION_USER_WEIGHT, profile.getWeight());
                            intent.putExtra(Common.SESSION_USER_AGE, profile.getAge());
                            intent.putExtra(Common.SESSION_USER_GENDER, profile.getGender());
                            intent.putExtra(Common.SESSION_UNIT_TYPE, profile.getUnit());

                            intent.putExtra(Common.SESSION_COMING_FROM, "login");


                            isLoginScreen = false;
                            startActivity(intent);
                            finish();
                        } else if (profile.getFood_preference_id() == null && profile.getFood_preference() == null) {
                            if (loginResponse.getData() != null) {
                                if (loginResponse.getData().getPlatform() != null) {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
                                } else {
                                    SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
                                }
                            }
                            if (rememberMeCheckBox.isChecked()) {
                                secureSessionUtil.saveCredentials(new CredentialsModel(emailLogin, passwordLogin));
                            } else {
                                secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
                            }
                            StopLoading();
                            Intent intent = new Intent(LoginActivity.this, FoodPreferencesActivity.class);
                            intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
                            intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
                            intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
                            intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
                            intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);

                            intent.putExtra(Common.SESSION_USER_HEIGHT, profile.getHeight());
                            intent.putExtra(Common.SESSION_USER_WEIGHT, profile.getWeight());
                            intent.putExtra(Common.SESSION_USER_AGE, profile.getAge());
                            intent.putExtra(Common.SESSION_USER_GENDER, profile.getGender());
                            intent.putExtra(Common.SESSION_UNIT_TYPE, profile.getUnit());

                            if (!profile.getGoal_id().matches("")) {
                                intent.putExtra(Common.SESSION_USER_GOAL_ID, Integer.parseInt(profile.getGoal_id()));
                            } else {
                                intent.putExtra(Common.SESSION_USER_GOAL_ID, 0);
                            }
                            intent.putExtra(Common.SESSION_USER_GOAL, profile.getGoal());
                            intent.putExtra(Common.SESSION_USER_PRODUCT_ID, profile.getProduct_id());
                            intent.putExtra(Common.SESSION_USER_LEVEL, profile.getLevel());
                            if (!profile.getLevel_id().matches("")) {
                                intent.putExtra(Common.SESSION_USER_LEVEL_ID, Integer.parseInt(profile.getLevel_id()));
                            } else {
                                intent.putExtra(Common.SESSION_USER_LEVEL_ID, 0);
                            }
                            intent.putExtra(Common.SESSION_COMING_FROM, "login");

                            isLoginScreen = false;
                            startActivity(intent);
                            finish();
                        } else {
                            //get Device Token
                            redirectForSubscription(user, profile, null);
                        }

                    } else if ((SharedData.isCompleted.matches("1")) && profile != null && (subscription != null) &&
                            (subscription.getStripe_id() != null)) {
                        if (is_logged_in) {
                            //if user already logged in on other device
                            redirectToDashboard(user, profile, subscription);
                        } else {
                            //for normal login
                            getFCMDeviceToken(user, profile, subscription);
                        }
                    }
                } else {
                    StopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "is_profile_completed is null");
                    }
                    if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                        Toast.makeText(getApplicationContext(), "profile completed is null", Toast.LENGTH_SHORT);
                    }
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "User is null");
                }
                if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                    Toast.makeText(getApplicationContext(), "User is null", Toast.LENGTH_SHORT);
                }
                StopLoading();
            }
            // }

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Access token or Refresh token is null");
            }
            if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                Toast.makeText(getApplicationContext(), "Access Token is null in response", Toast.LENGTH_SHORT);
            }
            StopLoading();
        }
    }


    // Watcher Classs
    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextLoginUsernameOrEmail:
                    validateLoginEmail();
                    break;
                case R.id.editTextLoginPassword:
                    validateLoginPassword();
                    break;
            }
        }
    }


    private boolean validateLoginPassword() {
        // Check for a valid password.
        if (mEditTextLoginPassword.getText().toString().isEmpty()) {
            //StopLoading();
            mEditTextLoginPassword.setError(resources.getString(R.string.password_error));
            return false;
        } /*else if (mEditTextLoginPassword.getText().length() < 4) {
//            StopLoading();
            mEditTextLoginPassword.setError(getResources().getString(R.string.error_weak_password));
            return false;
        } else if (mEditTextLoginPassword.getText().length() < 6) {
//            StopLoading();
            mEditTextLoginPassword.setError(getResources().getString(R.string.error_medium_password));
            return false;
        }*/ else {
            textInputLayoutLoginPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLoginEmail() {
        if (mEditTextLoginUsernameOrEmail.getText().toString().isEmpty()) {
            // StopLoading();
            mEditTextLoginUsernameOrEmail.setError(resources.getString(R.string.email_error));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEditTextLoginUsernameOrEmail.getText().toString()).matches()) {
            //StopLoading();
            mEditTextLoginUsernameOrEmail.setError(resources.getString(R.string.error_invalid_email));
            return false;
        } else {
            textInputLayoutLoginEmail.setErrorEnabled(true);
        }
        return true;
    }


    public void getFCMDeviceToken(LoginResponse.Data.User user, LoginResponse.Data.User.Profile profile, LoginResponse.Data.User.Subscription subscription) {
        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    StopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Login Actiivty: getFCMDeviceToken2: Fetching FCM registration token failed", task.getException());
                    }
                    if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("testing")) {
                        Toast.makeText(getApplicationContext(), "Error while fetching FCM token", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("loginActivity_FCMFailure", emailLogin
                                        , EXCEPTION, task.getException().toString());
                    }

                    //redirectToDashboard(user, profile, subscription);
                    return;
                } else {
                    // Get new FCM registration token
                    fcm_token = task.getResult();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "FCM is " + fcm_token);
                    }

                    SessionUtil.setFcmToken(getApplicationContext(), fcm_token);
                    SharedData.fcm_token = fcm_token;
                    Log.d(TAG, "Login Actiivty: getFCMDeviceToken2: FCM Token retrieved from Shared Variable is " + SharedData.fcm_token);

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Login Actiivty: getFCMDeviceToken2: FCM TOKEN STRING from server: " + msg);
                    }
                    FirebaseDatabase.getInstance().getReference("token").child(SharedData.id).setValue(fcm_token);
                    // Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();
                    if (is_logged_in) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "LoginActivtiy: getFCMDeviceToken(boolean is_logged_in): FCM Token isLogin=True " + fcm_token);
                        }
                        showLoggedDialog(fcm_token, is_logged_in);
                    } else {
                        if (subscription.getStripe_status() != null && subscription.getStripe_status().matches("inactive")) {
                            redirectToDashboard(user, profile, subscription);
                        } else if (subscription.getStripe_status() != null && subscription.getStripe_status().matches("cancel")) {
                            redirectForSubscription(user, profile, subscription);
                        } else if (subscription.getPayment_status() != null) {
                            if (subscription.getPayment_status().matches("requires_payment_method")
                                    || subscription.getPayment_status().matches("requires_action")) {
                                redirectForSubscription(user, profile, subscription);
                            } else if (subscription.getPayment_status().matches("succeeded")) {
                                redirectToDashboard(user, profile, subscription);
                            } else {
                                //redirectForSubscription(user, profile, subscription);
                                redirectToDashboard(user, profile, subscription);
                            }
                        } else {
                            redirectForSubscription(user, profile, subscription);
                            /*redirectToDashboard(user, profile, subscription);*/
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                }
                FirebaseCrashlytics.getInstance().toString();
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("loginActivity_FCMFailure", emailLogin
                                , EXCEPTION, SharedData.caughtException(e));

            }
        });
    }

    void redirectToDashboard(LoginResponse.Data.User user, LoginResponse.Data.User.Profile profile,
                             LoginResponse.Data.User.Subscription subscription) {
        //get Device Token
        // getFCMDeviceToken();
        StopLoading();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DEVICE_ID TOKEN: " + m_androidId);
            Log.d(TAG, "FCM Token is " + fcm_token);
        }

        SharedData.device_id = m_androidId;
        SessionUtil.setDeviceToken(getApplicationContext(), m_androidId);


        SharedData.subscription_id = subscription.getStripe_id();
        SharedData.trail_ends = subscription.getTrial_ends_at();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Subscription ID is " + SharedData.subscription_id);
            Log.d(TAG, "trail_ends in login: " + subscription.getTrial_ends_at() + " and shared data variable data is " + SharedData.trail_ends);
        }

        //getInfoFromToken(token);
        WeekDaysHelper weekDaysHelper = new WeekDaysHelper();

        String allergyIDs = "";
        String allergyNames = "";
        if (user != null) {
            allergyIDs = getAllergyIDs(user.getAllergies()).getAllergyID();
            allergyNames = getAllergyIDs(user.getAllergies()).getAllergyName();
        }
        if (loginResponse.getData() != null) {
            if (loginResponse.getData().getPlatform() != null) {
                SessionUtil.setSignedUpPlatform(getApplicationContext(), loginResponse.getData().getPlatform());
            } else {
                SessionUtil.setSignedUpPlatform(getApplicationContext(), "android");
            }
        }

        //addToSharedPref();
        if (Common.isLoggingEnabled) {
            if (profile.getProduct_id() != null)
                Log.d(TAG, "Product ID: " + profile.getProduct_id());
            else
                Log.e(TAG, "Product ID is null ");
        }
        if (rememberMeCheckBox.isChecked()) {
            secureSessionUtil.saveCredentials(new CredentialsModel(SharedData.email, passwordLogin));
        } else {
            secureSessionUtil.saveCredentials(new CredentialsModel("", ""));
        }
        SessionUtil.saveUserSession(getApplicationContext(), SharedData.id, SharedData.email, SharedData.username,
                profile.getHeight(), profile.getWeight(), profile.getLevel(), profile.getLevel_id(), profile.getAge(), profile.getGender(),
                profile.getUnit(), profile.getGoal(), profile.getGoal_id(), SharedData.token, SharedData.refresh_token,
                subscription.getStripe_id(), "" + subscription.getStarts_at(),
                subscription.getEnds_at(), "" + subscription.getStarts_at(),
                subscription.getTrial_ends_at(), "" + profile.getFood_preference_id(), profile.getFood_preference(), allergyIDs,
                allergyNames, profile.getProduct_id());

        if (subscription.getStripe_key() != null) {
            secureSessionUtil.saveStripeKey(subscription.getStripe_key());
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "subscription.getStripe_key() == null");
            }
        }

        if (subscription.getEphemeral_key() != null) {
            secureSessionUtil.saveStripeEphemeral(subscription.getEphemeral_key());
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "subscription.getEphemeral_key() == null");
            }
        }

        if (subscription.getCustomer() != null) {
            secureSessionUtil.saveStripeCustomer(subscription.getCustomer());
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "subscription.getCustomer() == null");
            }
        }

        if (subscription.getPayment_intent() != null) {
            secureSessionUtil.savePaymentIntent(subscription.getPayment_intent());
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "subscription.getPayment_intent() == null");
            }
        }

        SessionUtil.setLoggedInStepsFirstTime(getApplicationContext(), true);

        SessionUtil.setLoggedIn(getApplicationContext(), true);

        String todayDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
        SessionUtil.setStepDaySessionDate(getApplicationContext(), todayDate);

        //This date is saving for the step counter
        SessionUtil.setUserLogInDate(getApplicationContext(), todayDate);


        if (profile.getUser_image() != null) {
            SessionUtil.setUserImgURL(getApplicationContext(), profile.getUser_image());
        }

        if (profile.getLang() != null) {
            SessionUtil.setlangCode(getApplicationContext(), profile.getLang());
        }
                                                        /*//get Device Token
                                                        getFCMDeviceToken();
                                                        if (Common.isLoggingEnabled)
                                                            Log.d(TAG, "DEVICE_ID TOKEN: " + m_androidId);
                                                        //Log.d(TAG, "FCM.." + fcm_token);
                                                        SharedData.device_id = m_androidId;
                                                        SharedData.fcm_token = fcm_token;

                                                        //save data to shared preference
                                                        //SessionUtil.setFcmToken(getApplicationContext(), SharedData.fcm_token);
                                                        SessionUtil.setDeviceToken(getApplicationContext(), m_androidId);*/
        if (Common.isLoggingEnabled) {
            if (SharedData.fcm_token != null) {
                Log.d(TAG, "FCM.. in redirect to Dashbaord in Login Activity: " + SharedData.fcm_token.toString());
            }
        }

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Common.SESSION_USER_ID, SharedData.id);
        intent.putExtra(Common.SESSION_EMAIL, SharedData.email);
        intent.putExtra(Common.SESSION_USERNAME, SharedData.username);
        intent.putExtra(Common.SESSION_ACCESS_TOKEN, SharedData.token);
        intent.putExtra(Common.SESSION_REFRESH_TOKEN, SharedData.refresh_token);
        intent.putExtra(Common.SESSION_FCM_TOKEN, SharedData.fcm_token);
        intent.putExtra(Common.SESSION_DEVICE_ID, SharedData.device_id);

        ToastUtil.showToastForFragment(getApplicationContext(),
                true, false, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT);

        isLoginScreen = false;
        startActivity(intent);
        finish();
    }


    private void checkConnection() {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        connectionReceiver = new ConnectionReceiver();

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
            message = resources.getString(R.string.no_internet_connection);

            // set text color
            color = Color.RED;
            // initialize snack bar
            Snackbar snackbar = Snackbar.make(findViewById(R.id.btnLogin), message, Snackbar.LENGTH_LONG);

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

    private void addToSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", SharedData.email);
        editor.putString("id", SharedData.id);
        editor.putString("name", SharedData.username);
        editor.putString("token", SharedData.token);
        editor.putString("refresh_token", SharedData.refresh_token);
        editor.putString("subscription_id", SharedData.subscription_id);
        editor.putString("trail_ends", SharedData.trail_ends);
//                        editor.putBoolean("isLoggedIn",true);
        editor.apply();

    }


    private void setOtpTimer() {

        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                //mTextViewResendOtp.setClickable(false);
                // mTextViewResendOtp.setTextColor(Color.parseColor("#80FFFFFF"));
                // Used for formatting digit to be in 2 digits only
                if (stop) {
                    cancel(); // cancel the countdown timer
                }
                NumberFormat f = new DecimalFormat("00");
                /* long hour = (millisUntilFinished / 3600000) % 24;*/
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                textViewTimer.setText(f.format(min) + ":" + f.format(sec));
            }


            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                textViewTimer.setText(" ");
                StopLoading();
                loggedDialog.dismiss();
                /*mTextViewResendOtp.setClickable(true);
                mTextViewResendOtp.setTextColor(Color.parseColor("#FFC153"));*/

            }

        }.start();
    }

    void setStringToWidgets() {

        loginMsgTV.setText(resources.getString(R.string.login_message));
        mEditTextLoginUsernameOrEmail.setHint(resources.getString(R.string.email));
        mEditTextLoginPassword.setHint(resources.getString(R.string.password));
        mForgetPasswordTextView.setText(resources.getString(R.string.forgot_password));
        mLoginButton.setText(resources.getString(R.string.log_in));
        textViewOr.setText(resources.getString(R.string.or));
        mGotoSigUpActivityButton.setText(resources.getString(R.string.sign_up_txt));
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}