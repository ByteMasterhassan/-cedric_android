package com.cedricapp.fragment;

import static com.cedricapp.common.Common.ERROR;
import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.INFORMATION;
import static com.cedricapp.common.Common.SESSION_USER_ALLERGY_IDS;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.PlansAdapter;
import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.PlanClickListener;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.PlansDataModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.StripeIntent;
import com.cedricapp.model.SubscriptionModel;
import com.cedricapp.model.UpdateLanguage;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.DeviceUtil;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.UserDetailsUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.activity.CheckoutActivityJava;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentOptionCallback;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.PaymentSheetResultCallback;
import com.stripe.android.paymentsheet.addresselement.AddressDetails;
import com.stripe.android.paymentsheet.addresselement.AddressLauncher;
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult;
import com.stripe.android.paymentsheet.model.PaymentOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class PaymentCategory extends AppCompatActivity implements PlanClickListener {
    private MaterialCardView mCard1, mCard2, mCard3;
    private RecyclerView mPlansRecyclerView;
    private ArrayList<PlansDataModel> plansDataList;
    private PlansDataModel plansDataModels;
    private PlansAdapter plansAdapter;
    private ImageButton back_btn;
    static MaterialButton mPaymentCategoryNextButton;
    String cardType, cardNumber, exp_Date, cardCvv;
    public static boolean check = false;
    double monthPlan;
    String startDate;
    LocalDate endDate;
    String userLevel, userGoals, height, weight, gender, age, unitType, email, id, name, refresh_token, token, product_id, allergyIDs, allergyNames, foodPreference, subscriptionID;
    int goalID, levelID, foodPreferenceID;
    // SweetAlertDialog pDialog;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private CrashlyticsCore Crashlytics;
    private SegmentedProgressBar segmentedProgressBar;
    private String setupIntentClientSecret;

    //private PaymentSheet paymentSheet;
    private PaymentSheet.FlowController flowController;
    private AddressDetails shippingDetails;
    private AddressLauncher addressLauncher;
    PaymentSheet.CustomerConfiguration customerConfig;
    String planId;
    SignupResponse profileResponse;
    int fragmentKey;
    WeekDaysHelper weekDaysHelper;

    FirebaseMessaging firebaseMessaging;
    String fcm_token = "";

    boolean isPlannedChanged = false;

    SubscriptionModel subscribedPlanData;

    ArrayList<String> retreivedPaymentIntentPlanIds;
    String stripeKey;

    String countryCode;

    MaterialTextView textViewTitlePaymentCat, textView7daysfree, textView7daysNoCharges;

    Resources resources;

    String TAG = "PAYMENT_TAG";

    private final AddressLauncher.Configuration addressConfiguration =
            new AddressLauncher.Configuration.Builder()
                    .additionalFields(
                            new AddressLauncher.AdditionalFieldsConfiguration(
                                    AddressLauncher.AdditionalFieldsConfiguration.FieldConfiguration.REQUIRED
                            )
                    )
                    .allowedCountries(new HashSet<>(Arrays.asList("SE", "AE", "AU", "BE", "BR", "CA", "CH", "DE", "ES", "FR", "GB", "IE", "IT", "MX", "NO", "NL",
                            "PL", "RU", "TR", "US", "ZA", "PK")))
                    .title("Shipping Address")
                    .googlePlacesApiKey(BuildConfig.place_api_key)
                    .build();
    private String message;

   /* private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            //showing dialog and then closing the application..
            // showDialog();
            *//*if (intent.hasExtra("From_setting")) {
                SessionUtil.setLoadHomeData(true, getApplicationContext());
                startActivity(new Intent(FoodPreferencesActivity.this, HomeActivity.class));
            }*//*
            // onBackPressed();
        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_category);
        //resources = Localization.setLanguage(PaymentCategory.this, getResources());
        resources = getResources();
        /*PaymentConfiguration.init(
                getApplicationContext(),
                BuildConfig.stripe_api_key
        );*/

        //set status icon bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        check = false;
        loading_lav = findViewById(R.id.loading_lav);
        blurView = findViewById(R.id.blurView);

        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(PaymentCategory.this)) {
            LocationUtil.getLocationByGeoLocationAPI(getApplicationContext());
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Google plays service is not available in the device");
            }
        }

        //getIntent data
        getIntentData();


        //get id's and set
        initialize();
        // calling a method to all pakages
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            if (subscriptionID != null) {
                if (!subscriptionID.matches("")) {
                    blurrBackground();
                    StartLoading();
                    getCurrentSubscribedPlan(token, subscriptionID);
                } else {
                    blurrBackground();
                    StartLoading();
                    getAllPackages();
                }
            } else {
                blurrBackground();
                StartLoading();
                getAllPackages();
            }
        } else {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        mPaymentCategoryNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPaymentCategoryNextButton.startAnimation(myAnim);
                if (check == true) {
                    savePaymentPackageAndMove();
                } else {
                    check = false;
                    Toast toast = Toast.makeText(PaymentCategory.this, resources.getString(R.string.select_a_plan_to_proceed), Toast.LENGTH_SHORT);
                    // toast.getView().setBackgroundResource(R.color.yellow);
                    toast.show();
                }

            }
        });

        final PaymentOptionCallback paymentOptionCallback = paymentOption -> {
            onPaymentOption(paymentOption);
        };

        final PaymentSheetResultCallback paymentSheetResultCallback = paymentSheetResult -> {
            onPaymentSheetResult(paymentSheetResult);
        };


        //paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        flowController = PaymentSheet.FlowController.create(
                this,
                paymentOptionCallback,
                paymentSheetResultCallback
        );
        addressLauncher = new AddressLauncher(this, this::onAddressLauncherResult);
        // fetchPaymentIntent();
    }


    private void onPaymentOption(
            @Nullable PaymentOption paymentOption
    ) {

       /* Button paymentMethodButton = findViewById(R.id.customPaymentButton);
        if (paymentOption != null) {
            paymentMethodButton.setText(paymentOption.getLabel());
            paymentMethodButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    paymentOption.getDrawableResourceId(),
                    0,
                    0,
                    0
            );
        } else {
            paymentMethodButton.setText("Select");
            paymentMethodButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
            );
        }*/
        try {
            if (paymentOption != null)
                flowController.confirm();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            fragmentKey = intent.getIntExtra("fragmentKey", 0);
            foodPreferenceID = intent.getIntExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, 0);
            foodPreference = intent.getStringExtra(Common.SESSION_USER_FOOD_PREFERENCE);
            allergyIDs = intent.getStringExtra(Common.SESSION_USER_ALLERGIES);
            allergyNames = intent.getStringExtra(Common.SESSION_USER_ALLERGIES);
            product_id = intent.getStringExtra(Common.SESSION_USER_PRODUCT_ID);

            levelID = intent.getIntExtra(Common.SESSION_USER_LEVEL_ID, 0);
            userLevel = intent.getStringExtra(Common.SESSION_USER_LEVEL);
            height = intent.getStringExtra(Common.SESSION_USER_HEIGHT);
            weight = intent.getStringExtra(Common.SESSION_USER_WEIGHT);
            age = intent.getStringExtra(Common.SESSION_USER_AGE);
            gender = intent.getStringExtra(Common.SESSION_USER_GENDER);
            userGoals = intent.getStringExtra(Common.SESSION_USER_GOAL);
            goalID = intent.getIntExtra(Common.SESSION_USER_GOAL_ID, 0);
            unitType = intent.getStringExtra(Common.SESSION_UNIT_TYPE);
            name = intent.getStringExtra(Common.SESSION_USERNAME);
            id = intent.getStringExtra(Common.SESSION_USER_ID);
            email = intent.getStringExtra(Common.SESSION_EMAIL);
            token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
            if (intent.hasExtra(Common.SESSION_SUBSCRIPTION_ID)) {
                subscriptionID = intent.getStringExtra(Common.SESSION_SUBSCRIPTION_ID);
            } else {
                isPlannedChanged = true;
            }
            String language = SessionUtil.getlangCode(getApplicationContext());
            //updateLanguage(token, language);

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Payment Category get Intent------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(TAG, Common.SESSION_USER_LEVEL + ": " + userLevel);
                Log.d(TAG, Common.SESSION_USER_LEVEL_ID + ": " + levelID);
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoals);
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreferenceID);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(TAG, SESSION_USER_ALLERGY_IDS + ": " + allergyIDs);
                Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + allergyNames);
                Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + SessionUtil.getAllergiesId(PaymentCategory.this));
                Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }


    }

    private void initialize() {

        textViewTitlePaymentCat = findViewById(R.id.textViewTitlePaymentCat);
        textView7daysfree = findViewById(R.id.textView7daysfree);
        textView7daysNoCharges = findViewById(R.id.textView7daysNoCharges);
        if (token == null) {
            token = SharedData.token;
        }
        if (product_id == null) {
            product_id = SessionUtil.getProductID(getApplicationContext());
        }

        mPlansRecyclerView = findViewById(R.id.plansRecyclerview);
        //button id
        mPaymentCategoryNextButton = findViewById(R.id.btnPaymentMethodNext);
        //get date
        startDate = SimpleDateFormat.getDateInstance().format(new Date());
        if (Common.isLoggingEnabled) {
            System.out.println(startDate + "===date");
        }
        back_btn = findViewById(R.id.backBtn);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(7);
        if (fragmentKey == 1) {
            segmentedProgressBar.setVisibility(View.GONE);
        }

        retreivedPaymentIntentPlanIds = new ArrayList<>();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
            }
        });

        plansDataList = new ArrayList<>();
        weekDaysHelper = new WeekDaysHelper();
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            firebaseMessaging = FirebaseMessaging.getInstance();
            getFCMDeviceToken();
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/

        textViewTitlePaymentCat.setText(resources.getString(R.string.choose_subscription));
        textView7daysfree.setText(resources.getString(R.string.your_first_7_days_are_free));
        textView7daysNoCharges.setText(resources.getString(R.string.no_charges_until_your_7_day_trial_ends_ncancel_anytime));
        mPaymentCategoryNextButton.setText(resources.getString(R.string.next));
    }

    private void getCurrentSubscribedPlan(String token, String subscription_id) {
        Call<SubscriptionModel> call = ApiClient.getService().currentSubscriptionPlan("Bearer " + SharedData.token, subscription_id);
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    if (response.isSuccessful()) {
                        subscribedPlanData = response.body();
                        getAllPackages();
                    } else if (response.code() == 401) {
                        LogoutUtil.redirectToLogin(PaymentCategory.this);
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new GsonBuilder().create();
                        ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                        try {
                            errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                            if (errorMessageModel.getMessage() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                    new LogsHandlersUtils(getApplicationContext())
                                            .getLogsDetails("PaymentCategory getCurrentSubscribedPlan", email
                                                    , EXCEPTION, "" + errorMessageModel.getMessage());
                                }
                            } else {
                                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                    new LogsHandlersUtils(getApplicationContext())
                                            .getLogsDetails("PaymentCategory getCurrentSubscribedPlan", email
                                                    , EXCEPTION, "Some error occurred PaymentCategory getCurrentSubscribedPlan");
                                }
                            }

                        } catch (Exception ex) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("PaymentCategory getCurrentSubscribedPlan", email
                                                , EXCEPTION, SharedData.caughtException(ex));
                            }
                        }
                    }
                } catch (Exception e) {
                    StopLoading();
                    FirebaseCrashlytics.getInstance().recordException(e);

                    new LogsHandlersUtils(getApplicationContext()).getLogsDetails("PayementCategory_getCurrentSubscribedPlan",
                            SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.caughtException(e));

                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);

                new LogsHandlersUtils(getApplicationContext()).getLogsDetails("PayementCategory_getCurrentSubscribedPlan",
                        SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.throwableObject(t));

                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void getAllPackages() {

        // on below line we are calling a method to get all the courses from API.
        Call<PlansDataModel> call = ApiClient.getService().getAllPackages("Bearer " + token, product_id);

        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        call.enqueue(new Callback<PlansDataModel>() {
            @Override
            public void onResponse(Call<PlansDataModel> call, Response<PlansDataModel> response) {
                // inside on response method we are checking
                // if the response is success or not.
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status: " + message.toString());
                    }
                    // below line is to add our data from api to our array list.
                    plansDataModels = response.body();

                    // System.out.println(plansDataModels.getData().toString()) + "Plans");
                    if (plansDataModels != null) {
                        if (plansDataModels.getStatus()) {

                            // below line we are running a loop to add data to our adapter class.
                            //  for (int i = 0; i < plansDataModels.getData().plans.size(); i++) {
                            if (plansDataModels.getData() != null) {
                                if (plansDataModels.getData().size() > 0) {

                                    plansAdapter = new PlansAdapter(plansDataModels, PaymentCategory.this, PaymentCategory.this);

                                    // below line is to set layout manager for our recycler view.
                                    LinearLayoutManager manager = new LinearLayoutManager(PaymentCategory.this);

                                    // setting layout manager for our recycler view.
                                    mPlansRecyclerView.setLayoutManager(manager);

                                    // below line is to set adapter to our recycler view.
                                    mPlansRecyclerView.setAdapter(plansAdapter);

                                } else {

                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "Plans Array size is zero");
                                    }
                                    new LogsHandlersUtils(getApplicationContext())
                                            .getLogsDetails("PaymentCategory_plans_API", email
                                                    , EXCEPTION, "Plans Array size is zero");

                                    Toast.makeText(PaymentCategory.this, resources.getString(R.string.no_plan_for_you), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Plans Data is null");
                                }
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("PaymentCategory_plans_API", email
                                                , EXCEPTION, "Plans Data is null");


                                Toast.makeText(PaymentCategory.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }

                            StopLoading();

                        } else {

                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Plans Data model getStatus is false");
                            }

                            Toast.makeText(PaymentCategory.this, resources.getString(R.string.no_plan_for_you), Toast.LENGTH_SHORT).show();

                            StopLoading();
                        }
                    } else {

                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Plan Data is null");
                        }

                        Toast.makeText(PaymentCategory.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                        StopLoading();
                    }

                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(PaymentCategory.this);
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);

                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response Status: " + message.toString());
                        Log.e(TAG, "Request is unsuccessful and reponse code is " + response.code());
                    }
                    StopLoading();

                    Toast.makeText(PaymentCategory.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlansDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("PaymentCategory_plans_API_Failure", email
                                , EXCEPTION, SharedData.throwableObject(t));


                // toast message for fail to get data.
                StopLoading();

                Toast.makeText(PaymentCategory.this, resources.getString(R.string.failedtogetdata), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void savePaymentPackageAndMove() {
        Intent intent = new Intent(PaymentCategory.this,
                CheckoutActivityJava.class);

        intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, foodPreferenceID);
        intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE, foodPreference);
        intent.putExtra(Common.SESSION_USER_ALLERGY_IDS, allergyIDs);
        intent.putExtra(Common.SESSION_USER_ALLERGIES, allergyNames);
        intent.putExtra(Common.SESSION_USER_PRODUCT_ID, product_id);

        //System.out.println(SharedData.planPrice + "Share price");
        intent.putExtra(Common.SESSION_USER_LEVEL_ID, levelID);
        intent.putExtra(Common.SESSION_USER_LEVEL, userLevel);
        intent.putExtra(Common.SESSION_USER_GOAL_ID, goalID);
        intent.putExtra(Common.SESSION_USER_GOAL, userGoals);
        intent.putExtra(Common.SESSION_USER_HEIGHT, height);
        intent.putExtra(Common.SESSION_USER_WEIGHT, weight);
        intent.putExtra(Common.SESSION_USER_AGE, age);
        intent.putExtra(Common.SESSION_USER_GENDER, gender);
        intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
        intent.putExtra(Common.SESSION_USER_ID, id);
        intent.putExtra(Common.SESSION_EMAIL, email);
        intent.putExtra(Common.SESSION_USERNAME, name);
        intent.putExtra(Common.SESSION_USER_PLAN_PRICE, SharedData.planPrice);
        intent.putExtra(Common.SESSION_USER_PLAN_ID, SharedData.planId);
        intent.putExtra(Common.SESSION_USER_PLAN_POSITION, SharedData.planPosition);
        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
        intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);
        //segmentedProgressBar.incrementCompletedSegments();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "------------In Payment Category: passing bundle to another activity------------");
            Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
            Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
            Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
            Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + height);
            Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + weight);
            Log.d(TAG, Common.SESSION_USER_LEVEL + ": " + userLevel);
            Log.d(TAG, Common.SESSION_USER_LEVEL_ID + ": " + levelID);
            Log.d(TAG, Common.SESSION_USER_AGE + ": " + age);
            Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
            Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
            Log.d(TAG, Common.SESSION_USER_GOAL + ": " + userGoals);
            Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
            Log.d(TAG, Common.SESSION_USER_PLAN_ID + ": " + SharedData.planId);
            Log.d(TAG, Common.SESSION_USER_PLAN_PRICE + ": " + SharedData.planPrice);
            Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
            Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);

            Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreferenceID);
            Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
            Log.d(TAG, SESSION_USER_ALLERGY_IDS + ": " + allergyIDs);
            Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + allergyNames);
            Log.d(TAG, Common.SESSION_USER_ALLERGIES + ": " + SessionUtil.getAllergiesId(PaymentCategory.this));
            Log.d(TAG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
        }
        startActivity(intent);

    }

    private void StartLoading() {
        //dissable user interaction
        PaymentCategory.this.getWindow().setFlags(
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
        PaymentCategory.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
    public void onPause() {
        super.onPause();
        StopLoading();
    }


    @Override
    public void openPaymentSheet(PlansDataModel plansDataModel, int index) {
        // onPayClicked(getWindow().getDecorView().getRootView());
        planId = plansDataModel.getData().get(index).getPlanId();
       /* if (subscribedPlanData != null && subscribedPlanData.getData() != null
                && subscribedPlanData.getData().getPlanID() != null) {*/
        if ((subscribedPlanData != null && subscribedPlanData.getData() != null
                && subscribedPlanData.getData().getPlanID() != null && subscribedPlanData.getData().getPlanID().equals(planId))
                || retreivedPaymentIntentPlanIds.contains(planId)) {

            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Plan Not Changed get old payment intent");
            }
            isPlannedChanged = false;
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Plan Changed get new payment intent");
            }
            isPlannedChanged = true;
        }
       /* } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Plan Changed get new payment intent");
            }
            isPlannedChanged = true;
        }*/
        //dummyPaymentIntentForTesting();
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            createSetupIntent((Integer.parseInt(plansDataModel.getData().get(index).getAmount()) * 100), plansDataModel.getData().get(index).getCurrency(), "card", planId);
        } else {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(resources.getString(R.string.ok), null)
                    .create();
            dialog.show();
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void createSetupIntent(int amount, String currency, String payementMethod,
                                   String planID) {
        blurrBackground();
        StartLoading();
        Call<StripeIntent> call = ApiClient.getService().createSetupIntent("Bearer " + token, amount, currency, payementMethod, planID, /*isPlannedChanged*/true);
        call.enqueue(new Callback<StripeIntent>() {
            @Override
            public void onResponse(Call<StripeIntent> call, Response<StripeIntent> response) {

                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status: " + message.toString());
                    }
                    if (!retreivedPaymentIntentPlanIds.contains(planId)) {
                        retreivedPaymentIntentPlanIds.add(planID);
                    }
                    StripeIntent stripeIntent = response.body();
                    if (stripeIntent != null && stripeIntent.getData() != null &&
                            stripeIntent.getData().getSetupIntent() != null && stripeIntent.getData().getStripe_key() != null
                            && stripeIntent.getData().getCustomer() != null && stripeIntent.getData().getEphemeralKey() != null) {
                        customerConfig = new PaymentSheet.CustomerConfiguration(
                                stripeIntent.getData().getCustomer(),
                                stripeIntent.getData().getEphemeralKey()
                        );
                        PaymentConfiguration.init(getApplicationContext(), stripeIntent.getData().getStripe_key());
                        stripeKey = stripeIntent.getData().getStripe_key();
                        setupIntentClientSecret = stripeIntent.getData().getSetupIntent();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Retrieved PaymentIntent: " + setupIntentClientSecret);
                        }
                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("New User Payment Setup Intent", email
                                            , INFORMATION, "Retrieved Setup Intent Details: " + setupIntentClientSecret.toString());
                        }

                        onPayClicked(getWindow().getDecorView().getRootView());

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response Code is " + response.code() + " and stripeSetupIntent == null && stripePaymentIntent.getData() == null");
                        }
                        showAlert(resources.getString(R.string.something_went_wrong), resources.getString(R.string.payment_unable_to_proceed));
                    }
                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(PaymentCategory.this);
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                } else {
                    StopLoading();
                    /*Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                        if (errorMessageModel.getMessage() != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("New User Payment Failed", email
                                                , EXCEPTION, "" + errorMessageModel.getMessage());
                            }
                        } else {
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                new LogsHandlersUtils(getApplicationContext())
                                        .getLogsDetails("New User Payment Failed", email
                                                , EXCEPTION, "Some error occurred on payment intent creation");
                            }
                        }

                    } catch (Exception ex) {
                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                            new LogsHandlersUtils(getApplicationContext())
                                    .getLogsDetails("New User Payment Failed", email
                                            , EXCEPTION, SharedData.caughtException(ex));
                        }
                    }

                    */
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response Code is " + response.code());
                    }
                    showAlert(resources.getString(R.string.something_went_wrong), resources.getString(R.string.try_again_later));
                }

            }

            @Override
            public void onFailure(Call<StripeIntent> call, Throwable t) {
                StopLoading();

                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                showAlert(resources.getString(R.string.failedtogetdata), resources.getString(R.string.payment_unable_to_proceed));
            }
        });
    }

    /*private void fetchPaymentIntent() {
        final String shoppingCartContent = "{\"items\": [ {\"id\":\"xl-tshirt\"}]}";
        // final String shoppingCartContent = "";

        final okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(shoppingCartContent, MediaType.get("application/json; charset=utf-8"));

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://stripe-mobile-payment-sheet.glitch.me/checkout")
                .post(requestBody).build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                        showAlert("Failed to load data", "Error: " + e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            showAlert(
                                    "Failed to load page",
                                    "Error: " + response.toString()
                            );
                        } else {
                            final JSONObject responseJson = parseResponse(response.body());
                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    responseJson.optString("customer"),
                                    responseJson.optString("ephemeralKey")
                            );
                            paymentIntentClientSecret = responseJson.optString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), responseJson.optString("publishableKey"));
                            //runOnUiThread(() -> payButton.setEnabled(true));
                            Log.i(TAG, "Retrieved PaymentIntent: " + paymentIntentClientSecret);
                            *//*try {
                                final JSONObject result = new JSONObject(String.valueOf(response.body()));
                                customerConfig = new PaymentSheet.CustomerConfiguration(
                                        result.getString("customer"),
                                        result.getString("ephemeralKey")
                                );
                                paymentIntentClientSecret = result.getString("paymentIntentClientSecret");
                                PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));
                            } catch (JSONException e) { *//**//* handle error *//**//*
                            e.printStackTrace();
                            }*//*
                        }
                    }
                });

    }*/

    private JSONObject parseResponse(okhttp3.ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Error parsing response", e);
                }
            }
        }

        return new JSONObject();
    }

    private void onPayClicked(View view) {
        if (SharedData.countryCode != null) {
            countryCode = SharedData.countryCode;
        } else {
            countryCode = "SE";
        }
        PaymentSheet.GooglePayConfiguration googlePayConfiguration;
        if (SessionUtil.getAPP_Environment(getApplicationContext()).matches("") ||
                SessionUtil.getAPP_Environment(getApplicationContext()).matches("production")
                || SessionUtil.getAPP_Environment(getApplicationContext()).matches("beta")) {
            googlePayConfiguration =
                    new PaymentSheet.GooglePayConfiguration(
                            PaymentSheet.GooglePayConfiguration.Environment.Production,
                            countryCode,"SEK"
                    );
        } else {
            googlePayConfiguration =
                    new PaymentSheet.GooglePayConfiguration(
                            PaymentSheet.GooglePayConfiguration.Environment.Test,
                            countryCode, "SEK"
                    );
        }
        PaymentSheet.Address address = new PaymentSheet.Address.Builder()
                .country(countryCode)
                .build();
        PaymentSheet.BillingDetails billingDetails = new PaymentSheet.BillingDetails.Builder()
                .name(name)
                .email(email)
                .address(address)
                .build();

        PaymentSheet.Configuration config = new PaymentSheet.Configuration.Builder(resources.getString(R.string.cedric_fitness_app))
                .customer(customerConfig)
                .merchantDisplayName(resources.getString(R.string.cedric_fitness_app))
                .googlePay(googlePayConfiguration)
                .defaultBillingDetails(billingDetails)
                .allowsDelayedPaymentMethods(true)
                .build();

        flowController.configureWithSetupIntent(setupIntentClientSecret,
                config,
                (success, error) -> {
                    if (success) {
                        // Update your UI using `flowController.getPaymentOption()`
                        //flowController.getPaymentOption();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "In flow controller success");
                        }
                        StopLoading();
                        flowController.presentPaymentOptions();
                    } else {
                        StopLoading();
                        // handle FlowController configuration failure
                        if (error != null) {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, error.getMessage());
                                error.printStackTrace();
                            }
                        }
                    }
                }
        );


        /*if (Common.isLoggingEnabled) {
            Log.e(TAG, "Google Pay: " + config.getGooglePay().toString());
        }*/

        // Present Payment Sheet
        //paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, config);

        //paymentSheet.presentWithSetupIntent(setupIntentClientSecret, config);
        //flowController.presentPaymentOptions();

    }

    private void onAddressClicked(View view) {
        addressLauncher.present(
                stripeKey,
                addressConfiguration
        );
    }

    private void onAddressLauncherResult(AddressLauncherResult result) {
        // TODO: Handle result and update your UI
        if (result instanceof AddressLauncherResult.Succeeded) {
            shippingDetails = ((AddressLauncherResult.Succeeded) result).getAddress();
        } else if (result instanceof AddressLauncherResult.Canceled) {
            // TODO: Handle cancel
        }
    }

    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (Common.isLoggingEnabled) {
            if (paymentSheetResult != null)
                Log.d(TAG, "Payment Sheet Result: " + paymentSheetResult.toString());
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast(resources.getString(R.string.paymentcomplet));
            getUserDetails(token);

            //stripePayment(name,email,"4242424242424242",12,26,"123",planId);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Payment canceled!");
            }
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {

            /*Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();*/
            showAlert(resources.getString(R.string.paymentfaild), /*error.getLocalizedMessage()*/resources.getString(R.string.something_went_wrong));
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            }
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("New User Payment Failed", email
                                , ERROR, "" + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            }
        }
    }

    /*private void createCustomerOnStripe(){
        Call<StripeCustomerModel> call = StripeApiClient.getService().createStripeCustomer(BuildConfig.stripe_api_key,email,name);
        call.enqueue(new Callback<StripeCustomerModel>() {
            @Override
            public void onResponse(Call<StripeCustomerModel> call, Response<StripeCustomerModel> response) {
                if(response.isSuccessful()){
                    StripeCustomerModel customerModel = response.body();
                }
            }

            @Override
            public void onFailure(Call<StripeCustomerModel> call, Throwable t) {
                if(Common.isLoggingEnabled)
                    t.printStackTrace();
            }
        });
    }*/


    private void stripePayment(String name, String email, String number, int exp_month, int exp_year, String cvc, String plan) {
        blurrBackground();
        StartLoading();
        retrofit2.Call<SignupResponse> call = ApiClient.getService().paymentCall("Bearer " + token, name, email, number, exp_month, exp_year,
                cvc, planId);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                //back_btn.setClickable(true);
                StopLoading();
                try {
                    if (response.isSuccessful()) {
                        profileResponse = response.body();
                        if (profileResponse.isStatus()) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Checkout response: " + profileResponse.toString());
                            }
                            Toast.makeText(PaymentCategory.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            SharedData.subscription_id = profileResponse.getData().getId();
                            SharedData.start_date = profileResponse.getData().getCurrent_period_start();
                            SharedData.end_date = profileResponse.getData().getCurrent_period_end();
                            SharedData.trail_ends = profileResponse.getData().getTrial_end();
                            //addEmailAndIdToSharedPref();
                        } else {
                            Toast.makeText(PaymentCategory.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            StopLoading();
                        }
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                        Log.e(TAG, "Checkout Activity exception: " + ex.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                StopLoading();
                // back_btn.setClickable(true);
            }
        });

    }

    private void saveDataAndCheckFragement(LoginResponse loginResponse) {
        List<String> allergyNamesList = new ArrayList<>();
        List<String> allergyIDsList = new ArrayList<>();
        String allergyNames = "", allergyIDs = "";
        if (loginResponse.getData().getUser().getAllergies() != null) {
            for (int i = 0; i < loginResponse.getData().getUser().getAllergies().size(); i++) {
                allergyIDsList.add("" + loginResponse.getData().getUser().getAllergies().get(i).getId());
                allergyNamesList.add("" + loginResponse.getData().getUser().getAllergies().get(i).getName());
            }
        }
        if (allergyIDsList.size() > 0) {
            allergyNames = String.join(",", allergyNamesList);
            allergyIDs = String.join(",", allergyIDsList);
        }
        if (fragmentKey == 0) {
            SessionUtil.saveUserSession(getApplicationContext(), loginResponse.getData().getUser().getId(), loginResponse.getData().getUser().getEmail(), loginResponse.getData().getUser().getName(), loginResponse.getData().getUser().getProfile().getHeight(), loginResponse.getData().getUser().getProfile().getWeight(),
                    loginResponse.getData().getUser().getProfile().getLevel(), "" + loginResponse.getData().getUser().getProfile().getLevel_id(), loginResponse.getData().getUser().getProfile().getAge(), loginResponse.getData().getUser().getProfile().getGender(), loginResponse.getData().getUser().getProfile().getUnit(), loginResponse.getData().getUser().getProfile().getGoal(),
                    "" + loginResponse.getData().getUser().getProfile().getGoal_id(), loginResponse.getData().getAccess_token(), "", loginResponse.getData().getUser().getSubscription().getStripe_id(), loginResponse.getData().getUser().getSubscription().getStarts_at(),
                    loginResponse.getData().getUser().getSubscription().getEnds_at(), "" + weekDaysHelper.getPrevious7thDayDate(loginResponse.getData().getUser().getSubscription().getTrial_ends_at()),
                    loginResponse.getData().getUser().getSubscription().getTrial_ends_at(), "" + loginResponse.getData().getUser().getProfile().getFood_preference_id(), loginResponse.getData().getUser().getProfile().getFood_preference(), allergyIDs,
                    allergyNames, loginResponse.getData().getUser().getProfile().getProduct_id());
            if (loginResponse.getData().getUser().getProfile().getLang() != null) {
                SessionUtil.setlangCode(getApplicationContext(), loginResponse.getData().getUser().getProfile().getLang());
            }

            SessionUtil.setLoggedInStepsFirstTime(getApplicationContext(), true);

            SessionUtil.setLoggedIn(getApplicationContext(), true);

            SimpleDateFormat sdf2 = new SimpleDateFormat(Common.DATE_FORMAT);
            String currentDateandTime1 = sdf2.format(new Date());
            SessionUtil.setStepDaySessionDate(getApplicationContext(), currentDateandTime1);

            //This date is saving for the step counter
            SessionUtil.setUserLogInDate(getApplicationContext(), currentDateandTime1);
            SessionUtil.setAccessToken(getApplicationContext(), loginResponse.getData().getAccess_token());
            SharedData.token = loginResponse.getData().getAccess_token();
        } else if (fragmentKey == 1) {
            //segmentedProgressBar.setVisibility(View.GONE);
            /*SessionUtil.saveSubscriptionID(getApplicationContext(), SharedData.subscription_id);
            SessionUtil.saveSubscription(getApplicationContext(), SharedData.start_date, SharedData.end_date);
            SessionUtil.saveStartTrialDate(getApplicationContext(), weekDaysHelper.getPrevious7thDayDate(SharedData.trail_ends));
            SessionUtil.saveEndTrialDate(getApplicationContext(), SharedData.trail_ends);*/
            SessionUtil.saveUserSession(getApplicationContext(), loginResponse.getData().getUser().getId(), loginResponse.getData().getUser().getEmail(), loginResponse.getData().getUser().getName(), loginResponse.getData().getUser().getProfile().getHeight(), loginResponse.getData().getUser().getProfile().getWeight(),
                    loginResponse.getData().getUser().getProfile().getLevel(), "" + loginResponse.getData().getUser().getProfile().getLevel_id(), loginResponse.getData().getUser().getProfile().getAge(), loginResponse.getData().getUser().getProfile().getGender(), loginResponse.getData().getUser().getProfile().getUnit(), loginResponse.getData().getUser().getProfile().getGoal(),
                    "" + loginResponse.getData().getUser().getProfile().getGoal_id(), loginResponse.getData().getAccess_token(), "", loginResponse.getData().getUser().getSubscription().getStripe_id(), loginResponse.getData().getUser().getSubscription().getStarts_at(),
                    loginResponse.getData().getUser().getSubscription().getEnds_at(), "" + weekDaysHelper.getPrevious7thDayDate(loginResponse.getData().getUser().getSubscription().getTrial_ends_at()),
                    loginResponse.getData().getUser().getSubscription().getTrial_ends_at(), "" + loginResponse.getData().getUser().getProfile().getFood_preference_id(), loginResponse.getData().getUser().getProfile().getFood_preference(), allergyIDs,
                    allergyNames, loginResponse.getData().getUser().getProfile().getProduct_id());
            if (loginResponse.getData().getUser().getProfile().getLang() != null) {
                SessionUtil.setlangCode(getApplicationContext(), loginResponse.getData().getUser().getProfile().getLang());
            }
            //SessionUtil.setAccessToken(getApplicationContext(),loginResponse.getData().getAccess_token());
        }

        startNextActivity();
    }

    private void startNextActivity() {
        Intent intent1 = new Intent(PaymentCategory.this,
                HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra(Common.SESSION_USERNAME, SharedData.username);
        startActivity(intent1);
        StopLoading();
    }

    private void getUserDetails(String authorization) {
        blurrBackground();
        StartLoading();
        Call<LoginResponse> getUserDetailsCall = ApiClient.getService().getUserDetails("Bearer " + authorization);
        getUserDetailsCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //StopLoading();
                try {

                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();

                        if (loginResponse != null && loginResponse.getData() != null
                                && loginResponse.getData().getUser() != null
                                && loginResponse.getData().getUser().getProfile() != null
                                && loginResponse.getData().getUser().getSubscription() != null) {
                            SharedData.subscription_id = loginResponse.getData().getUser().getSubscription().getStripe_id();
                            SharedData.start_date = loginResponse.getData().getUser().getSubscription().getStarts_at();
                            SharedData.end_date = loginResponse.getData().getUser().getSubscription().getEnds_at();
                            SharedData.trail_ends = loginResponse.getData().getUser().getSubscription().getTrial_ends_at();
                            token = loginResponse.getData().getAccess_token();
                            refresh_token = loginResponse.getData().getRefresh_token();

                            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            if (SharedData.location != null && !SharedData.location.matches("")) {
                                new UserDetailsUtil(getApplicationContext()).sendUserDetail("Bearer " + token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(),
                                        deviceID, SharedData.location, WeekDaysHelper.getUTC_Time(), fcm_token, false, false, BuildConfig.VERSION_NAME, "" + BuildConfig.VERSION_CODE, "android", "registration");
                                saveDataAndCheckFragement(loginResponse);
                            } else {
                                new UserDetailsUtil(getApplicationContext()).sendUserDetail("Bearer " + token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(),
                                        deviceID, "UNKNOWN", WeekDaysHelper.getUTC_Time(), fcm_token, false, false, BuildConfig.VERSION_NAME, "" + BuildConfig.VERSION_CODE, "android", "registration");
                                saveDataAndCheckFragement(loginResponse);
                            }

                        }

                    } else {
                        if (Common.isLoggingEnabled)
                            Toast.makeText(PaymentCategory.this, response.code() + "From Server", Toast.LENGTH_SHORT).show();
                        /*if (response.code()==500) {
                            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            new UserDetailsUtil(getApplicationContext()).sendUserDetail("Bearer " + token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(),
                                    deviceID, "NA", WeekDaysHelper.getUTC_Time(), fcm_token, false, false, BuildConfig.VERSION_NAME, "" + BuildConfig.VERSION_CODE, "android", "registration");
                            startNextActivity();
                        }*/
                    }

                } catch (Exception e) {
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
                StopLoading();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                StopLoading();
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                Toast.makeText(PaymentCategory.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFCMDeviceToken() {
        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                    }
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("loginActivity_FCMFailure", email
                                    , EXCEPTION, task.getException().toString());
                    return;
                } else {
                    // Get new FCM registration token
                    fcm_token = task.getResult();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "FCM is " + fcm_token);
                    }

                    SessionUtil.setFcmToken(getApplicationContext(), fcm_token);
                    SharedData.fcm_token = fcm_token;
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "FCM Token iss " + SharedData.fcm_token);
                    }

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "FCM TOKEN STRING: " + msg);
                    }
                    String userId = SharedData.id;
                    if (userId != null) {
                        FirebaseDatabase.getInstance().getReference("token").child(userId).setValue(fcm_token);
                    } else {
                        // Handle the case where SharedData.id is null
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "User id is null");
                        }
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    }
                    // Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();

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
                        .getLogsDetails("loginActivity_FCMFailure", email
                                , EXCEPTION, SharedData.caughtException(e));
            }
        });
    }


    private void updateLanguage(String token, String language) {
        Call<UpdateLanguage> call = ApiClient.getService()
                .changeLanguage("Bearer " + token, language);
        call.enqueue(new Callback<UpdateLanguage>() {
            @Override
            public void onResponse(Call<UpdateLanguage> call, Response<UpdateLanguage> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    UpdateLanguage updateLanguage = response.body();
                    //  Toast.makeText(getApplicationContext(), updateLanguage.getMessage(), Toast.LENGTH_SHORT).show();

                    Locale locale = new Locale(language);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getApplicationContext().getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                    SessionUtil.setlangCode(getApplicationContext(), language);
                    SessionUtil.setReloadData(getApplicationContext(), true);
                    SessionUtil.setDashboardReloadData(getApplicationContext(), true);
                    //dbHelper.clearDB();
                    //backArrow.setEnabled(true);
                    // backPress = true;
                    if (getApplicationContext() != null && getApplicationContext() != null) {
                        SessionUtil.setLoadHomeData(true, getApplicationContext());
                    }
                    /*Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();*/


                } else {
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
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateLanguage> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getApplicationContext() != null) {
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("Otp_fragment_response", SessionUtil.getUserEmailFromSession(getApplicationContext())
                                    , EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });

    }


}