package com.cedricapp.activity;


import static android.content.ContentValues.TAG;
import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.SESSION_USER_ALLERGY_IDS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.PaymentsUtil;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.collect.ImmutableList;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.stripe.android.Stripe;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class CheckoutActivityJava extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "https://lit-springs-66219.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private TextView amountTextView;
    MaterialTextView txt_paymentMethod;
    private CardInputWidget cardInputWidget;
    Resources resources;
    Button payButton;
    FrameLayout prvivacyPolicy;
    private ImageButton back_btn;
    LottieAnimationView loading_lav;
    private static String height, email, name, price, weight, gender, age, goals, duration, refresh_token, token,
            orderId, orderRef, level, unitType,
            orderStatus, profileImage, paymentMethod, transactionDate, comments;
    int levelID, goalID, foodPreferenceID;
    public final static String SHARED_PREF_NAME = "log_user_info";
    private String id, currentUserId, foodPreference, allergyIDs, allergyNames, product_id;
    DBHelper dbHelper;
    BlurView blurView;
    SignupResponse profileResponse;
    private CrashlyticsCore Crashlytics;
    private String signupDate;
    private SegmentedProgressBar segmentedProgressBar;
    private int exp_year, fragmentKey;
    private int exp_month;
    private String planId;
    private String cvc, number = "";
    private String postalCode;
    private WeekDaysHelper weekDaysHelper;
    private MaterialButton payWithStripeBtn;

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private static final long SHIPPING_COST_CENTS = 90 * PaymentsUtil.CENTS_IN_A_UNIT.longValue();

    // A client for interacting with the Google Pay API.
    private PaymentsClient paymentsClient;

    private View googlePayButton;

    private JSONArray garmentList;
    private JSONObject selectedGarment;

    private MaterialTextView textViewCreditDebit;

    /*private static final String TAG = "InAppBilling";*/
    private String inAppItem1;

    private BillingClient billingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private ProductDetails productDetails;
    private Purchase purchase;
    private int planPosition;
    private String message;

    /**
     * Initialize the Google Pay API on creation of the activity
     *
     * @see Activity#onCreate(android.os.Bundle)
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(CheckoutActivityJava.this);


        // ser status icon bar color
        setContentView(R.layout.activity_checkout_java);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }


        //inialize id's and set
        intialize();


        StopLoading();
        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
       /* stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51JjIAXLx3yBYLT8ENQmVoSrg7XT4lEfVofVWkdkMjd91CqNowWApV9vBtAFpIiCgiF2vM7gl79XK4QcSzV6exicv00L2yjglv0")
        );*/

        //startCheckout();

        //initializeUi();
        billingSetup();

        // Set up the mock information for our item in the UI.
       /* try {
            selectedGarment = fetchRandomGarment();
            displayGarment(selectedGarment);
        } catch (JSONException e) {
            throw new RuntimeException("The list of garments cannot be loaded");
        }*/

        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        paymentsClient = PaymentsUtil.createPaymentsClient(this);
        //possiblyShowGooglePayButton();
    }

    private void billingSetup() {

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(
                    @NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "OnBillingSetupFinish connected");
                    queryProduct();

                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost");
            }
        });
    }

    public void makePurchase() {
        // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
// Get the offerToken of the selected offer
        String offerToken = productDetails
                .getSubscriptionOfferDetails()
                .get(planPosition)
                .getOfferToken();

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails)
                                                .setOfferToken(offerToken)
                                                .build()
                                )
                        )
                        .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult,
                                       List<Purchase> purchases) {

            if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    completePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.i(TAG, "onPurchasesUpdated: Purchase Canceled");
            } else {
                Log.i(TAG, "onPurchasesUpdated: Error");
            }
        }
    };

    private void completePurchase(Purchase item) {

        purchase = item;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            runOnUiThread(() -> {
                //binding.consumeButton.setEnabled(true);
                //binding.statusText.setText("Purchase Complete");
                saveDataAndCheckFragement();
                startNextActivity();
            });
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            id = intent.getStringExtra(Common.SESSION_USER_ID);
            fragmentKey = intent.getIntExtra("fragmentKey", 0);

            email = intent.getStringExtra(Common.SESSION_EMAIL);
            SharedData.username = intent.getStringExtra(Common.SESSION_USERNAME);
            level = intent.getStringExtra(Common.SESSION_USER_LEVEL);
            levelID = intent.getIntExtra(Common.SESSION_USER_LEVEL_ID, 0);
            height = intent.getStringExtra(Common.SESSION_USER_HEIGHT);
            weight = intent.getStringExtra(Common.SESSION_USER_WEIGHT);
            //System.out.println(weight + "oooooo");
            age = intent.getStringExtra(Common.SESSION_USER_AGE);
            gender = intent.getStringExtra(Common.SESSION_USER_GENDER);
            goals = intent.getStringExtra(Common.SESSION_USER_GOAL);
            goalID = intent.getIntExtra(Common.SESSION_USER_GOAL_ID, 0);
            //duration = SharedData.plan;
            price = intent.getStringExtra(Common.SESSION_USER_PLAN_PRICE);
            unitType = intent.getStringExtra(Common.SESSION_UNIT_TYPE);
            planId = intent.getStringExtra(Common.SESSION_USER_PLAN_ID);
            planPosition = intent.getIntExtra(Common.SESSION_USER_PLAN_POSITION, 0);
            token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
            SharedData.unitType = unitType;
            SharedData.token = token;

            foodPreferenceID = intent.getIntExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, 0);
            foodPreference = intent.getStringExtra(Common.SESSION_USER_FOOD_PREFERENCE);
            allergyIDs = intent.getStringExtra(SESSION_USER_ALLERGY_IDS);
            allergyNames = intent.getStringExtra(Common.SESSION_USER_ALLERGIES);
            product_id = intent.getStringExtra(Common.SESSION_USER_PRODUCT_ID);

            if (goalID == 1) {
                inAppItem1 = "lose_weight";
            } else if (goalID == 2) {
                inAppItem1 = "muscles_gain";
            } else if (goalID == 3) {
                inAppItem1 = "get_fitted_and_tone";
            } else {
                inAppItem1 = "lose_weight";
            }

            //System.out.println(price + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
            //System.out.println(SharedData.planPrice + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
            //id = SharedData.id;
            currentUserId = id;
            //System.out.println(currentUserId + ";;;;;;;;ID;;;;;;" + fragmentKey + ";;;;;;;;ID;;;;;;");
            //set amount in textview
            // ToDo change amount
            amountTextView.setText("" + 100);

            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "------------In Checkout Activity: retrieving data from intent------------");
                Log.d(Common.LOG, Common.SESSION_USER_ID + ": " + currentUserId);
                Log.d(Common.LOG, Common.SESSION_EMAIL + ": " + email);
                Log.d(Common.LOG, Common.SESSION_USERNAME + ": " + SharedData.username);
                Log.d(Common.LOG, Common.SESSION_USER_HEIGHT + ": " + height);
                Log.d(Common.LOG, Common.SESSION_USER_WEIGHT + ": " + weight);
                Log.d(Common.LOG, Common.SESSION_USER_LEVEL + ": " + level);
                Log.d(Common.LOG, Common.SESSION_USER_LEVEL_ID + ": " + levelID);
                Log.d(Common.LOG, Common.SESSION_USER_AGE + ": " + age);
                Log.d(Common.LOG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(Common.LOG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(Common.LOG, Common.SESSION_USER_GOAL + ": " + goals);
                Log.d(Common.LOG, Common.SESSION_USER_GOAL_ID + ": " + goalID);
                Log.d(Common.LOG, Common.SESSION_USER_PLAN_POSITION + ": " + planPosition);
                Log.d(Common.LOG, Common.SESSION_USER_PLAN_ID + ": " + planId);
                Log.d(Common.LOG, Common.SESSION_USER_PLAN_PRICE + ": " + price);
                Log.d(Common.LOG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(Common.LOG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);

                Log.d(Common.LOG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreferenceID);
                Log.d(Common.LOG, Common.SESSION_USER_FOOD_PREFERENCE + ": " + foodPreference);
                Log.d(Common.LOG, SESSION_USER_ALLERGY_IDS + ": " + allergyIDs);
                Log.d(Common.LOG, Common.SESSION_USER_ALLERGIES + ": " + allergyNames);
                Log.d(Common.LOG,Common.SESSION_USER_ALLERGIES+ ": "+SessionUtil.getAllergiesId(CheckoutActivityJava.this));
                Log.d(Common.LOG, Common.SESSION_USER_PRODUCT_ID + ": " + product_id);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("CheckoutActivity",email
                            , EXCEPTION, SharedData.caughtException(e));
            e.printStackTrace();
        }

    }

    private void intialize() {

        amountTextView = findViewById(R.id.amountTextView);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        //cardInputWidget.setPostalCodeEnabled(true);
        cardInputWidget.setPostalCodeRequired(true);
        loading_lav = findViewById(R.id.loading_lav);
        // Hook up the pay button to the card widget and stripe instance
        payButton = findViewById(R.id.payButton);
        blurView = findViewById(R.id.blurView);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        // prvivacyPolicy=findViewById(R.id.navigation_container1);
        weekDaysHelper = new WeekDaysHelper();

        textViewCreditDebit = findViewById(R.id.textViewCreditDebit);
        payWithStripeBtn = findViewById(R.id.payWithStripeBtn);
        txt_paymentMethod=findViewById(R.id.textViewPaymentMethod);


        /*PaymentConfiguration.init(
                getApplicationContext(),
                Common.STRIPE_KEY
        );*/
        //get intent data
        getIntentData();
        resources = Localization.setLanguage(CheckoutActivityJava.this,getResources());
        setlanguageToWidget();

        back_btn = findViewById(R.id.backBtn);
        if (fragmentKey == 0) {

            segmentedProgressBar.setCompletedSegments(8);
        } else if (fragmentKey == 1) {
            segmentedProgressBar.setVisibility(View.INVISIBLE);

        }


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payButton.startAnimation(myAnim);

                completePayment();
            }
        });

        payWithStripeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payButton.setVisibility(View.VISIBLE);
                textViewCreditDebit.setVisibility(View.VISIBLE);
                cardInputWidget.setVisibility(View.VISIBLE);
                payWithStripeBtn.setVisibility(View.GONE);
                googlePayButton.setVisibility(View.GONE);
            }
        });

        // The Google Pay button is a layout file – take the root view
        googlePayButton = findViewById(R.id.googlePayButton);
        googlePayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // requestPayment(view);
                        makePurchase();
                    }
                });


    }

    private void setlanguageToWidget() {
        txt_paymentMethod.setText(resources.getString(R.string.payment_method));
    }

    private boolean isValidated() {

        return false;
    }

    private boolean isCardFieldValidated() {
        //  Pattern p = Pattern.compile("[$&+,:;=?@#|'<>.^*()%!-]");
        try {

            if (cardInputWidget != null) {
                if (cardInputWidget.getCardParams() != null) {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Card Input Widget Data: " + cardInputWidget.getCardParams().toString());
                    if (cardInputWidget.getCardParams().getAddress() != null) {
                        if (cardInputWidget.getCardParams().getAddress().getPostalCode() != null) {
                            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                            Matcher matcher = pattern.matcher(cardInputWidget.getCardParams().getAddress().getPostalCode().toString());
                            boolean isStringContainsSpecialCharacter = matcher.find();
                            if (isStringContainsSpecialCharacter) {
                                Toast.makeText(this, getResources().getString(R.string.valid_postal_code), Toast.LENGTH_SHORT).show();
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Postal Code not entered");
                            return true;
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(Common.LOG, "Get Address is returning null");
                        return true;
                    }

                } else {

                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Card Input Widget getCardParms method is returning null");
                    Toast.makeText(this, getResources().getString(R.string.enter_correct_card_detail), Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {

                if (Common.isLoggingEnabled)
                    Log.d(Common.LOG, "Card Input Widget is null");
                Toast.makeText(this, getResources().getString(R.string.enter_correct_card_detail), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception ex) {
            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("CheckoutActivity",email
                            , EXCEPTION, SharedData.caughtException(ex));
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        return true;
    }

    private void completePayment() {

        if (isCardFieldValidated()) {
            if (ConnectionDetector.isConnectedWithInternet(CheckoutActivityJava.this)) {
                blurrBackground();
                StartLoading();

                if (cardInputWidget != null) {
                    if (cardInputWidget.getCardParams() != null) {
                        if (cardInputWidget.getCardParams().getNumber$payments_core_release() != null) {
                            number = cardInputWidget.getCardParams().getNumber$payments_core_release();
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Card number is null");
                        }
                        if (cardInputWidget.getCardParams().getCvc$payments_core_release() != null) {
                            cvc = cardInputWidget.getCardParams().getCvc$payments_core_release();
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "CVC number is null");
                        }
                        if (cardInputWidget.getCardParams().getExpMonth$payments_core_release() != 0) {
                            exp_month = cardInputWidget.getCardParams().getExpMonth$payments_core_release();
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Expiry month is null");
                        }
                        if (cardInputWidget.getCardParams().getExpYear$payments_core_release() != 0) {
                            exp_year = cardInputWidget.getCardParams().getExpYear$payments_core_release();
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Expiry Year is null");
                        }

                        if (cardInputWidget.getCardParams().getAddress() != null) {
                            if (cardInputWidget.getCardParams().getAddress().getPostalCode() != null) {
                                postalCode = cardInputWidget.getCardParams().getAddress().getPostalCode();
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "Postal code is null");
                            }

                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Address is null");
                        }

                        //plan="price_1KeWmkLx3yBYLT8ED9vBQ5Bz";
                        if (Common.isLoggingEnabled) {
                            if (number != null)
                                System.out.println(number.toString() + "number kkk");
                            System.out.println(cvc.toString() + "number cvc");
                            System.out.println(exp_month + "number month");
                            System.out.println(exp_year + "number year");
                            System.out.println(planId + "number year");
                        }


                        if (cardInputWidget.getPaymentMethodCreateParams() != null) {
                            if (Common.isLoggingEnabled)
                                System.out.println("hello to futur");
                            try {
                /*ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);*/
                                //call Stripe Payment API
                                back_btn.setClickable(false);
                                stripePayment(SharedData.username, email, number, exp_month, exp_year, cvc, planId);

                                // segmentedProgressBar.incrementCompletedSegments();
                            } catch (IllegalArgumentException i) {
                                FirebaseCrashlytics.getInstance().recordException(i);
                                if (Common.isLoggingEnabled)
                                    System.out.println("hello working dangeor;;;;;;;;;;;;;;;;;;,,,,,,,");
                                Log.e(i.getLocalizedMessage(), "null");

                            }
                            if (Common.isLoggingEnabled)
                                System.out.println("out of catch '''''''''''''''''");
                        } else {
                            if (Common.isLoggingEnabled)
                                System.out.println("out of if ;;;;;;;;;;;;;;;;;;;;");
                            // pDialog.hide();
                            StopLoading();
                            Toast.makeText(this, getResources().getString(R.string.enter_correct_card_number), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(Common.LOG, "cardInputWidget.getCardParams() is null");
                    }
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "cardInputWidget object is null");
                }

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void stripePayment(String name, String email, String number, int exp_month, int exp_year, String cvc, String plan) {
        retrofit2.Call<SignupResponse> call = ApiClient.getService().paymentCall("Bearer " + token, SharedData.username, email, number, exp_month, exp_year,
                cvc, planId);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                back_btn.setClickable(true);
                try {
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Response Status: " + message.toString());
                        profileResponse = response.body();
                        if (profileResponse.isStatus() == true) {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Checkout response: " + profileResponse.toString());

                            Toast.makeText(CheckoutActivityJava.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            SharedData.subscription_id = profileResponse.getData().getId();
                            SharedData.start_date = profileResponse.getData().getCurrent_period_start();
                            SharedData.end_date = profileResponse.getData().getCurrent_period_end();
                            SharedData.trail_ends = profileResponse.getData().getTrial_end();


                            //addEmailAndIdToSharedPref();
                            saveDataAndCheckFragement();


                            startNextActivity();


                        } else if (profileResponse.isStatus() == false) {
                            Toast.makeText(CheckoutActivityJava.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            StopLoading();
                        }
                    }
                    else{
                        message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                        Toast.makeText(CheckoutActivityJava.this, message.toString(), Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Response Status: " + message.toString());
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    new LogsHandlersUtils(getApplicationContext())
                            .getLogsDetails("CheckoutActivity",email
                                    , EXCEPTION, SharedData.caughtException(ex));
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                        if (Common.isLoggingEnabled)
                        Log.e(Common.LOG, "Checkout Activity exception: " + ex.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("CheckoutActivity",email
                                ,EXCEPTION, SharedData.throwableObject(t));
                t.printStackTrace();
                StopLoading();
                back_btn.setClickable(true);
            }
        });

    }

    private void saveDataAndCheckFragement() {
        if (fragmentKey == 0) {
            SessionUtil.saveUserSession(getApplicationContext(), id, email, SharedData.username, height, weight,
                    level, "" + levelID, age, gender, unitType, goals,
                    "" + goalID, token, refresh_token, SharedData.subscription_id, SharedData.start_date,
                    SharedData.end_date,SharedData.start_date/* weekDaysHelper.getPrevious7thDayDate(SharedData.trail_ends)*/,
                    SharedData.trail_ends, "" + foodPreferenceID, foodPreference, allergyIDs,
                    allergyNames, product_id);
            SessionUtil.setLoggedInStepsFirstTime(getApplicationContext(), true);

            SessionUtil.setLoggedIn(getApplicationContext(), true);

            SimpleDateFormat sdf2 = new SimpleDateFormat(Common.DATE_FORMAT);
            String currentDateandTime1 = sdf2.format(new Date());
            SessionUtil.setStepDaySessionDate(getApplicationContext(), currentDateandTime1);

            //This date is saving for the step counter
            SessionUtil.setUserLogInDate(getApplicationContext(), currentDateandTime1);
        } else if (fragmentKey == 1) {
            SessionUtil.saveSubscriptionID(getApplicationContext(), SharedData.subscription_id);
            SessionUtil.saveSubscription(getApplicationContext(), SharedData.start_date, SharedData.end_date);
            SessionUtil.saveStartTrialDate(getApplicationContext(), SharedData.start_date);/*weekDaysHelper.getPrevious7thDayDate(SharedData.trail_ends))*/
            SessionUtil.saveEndTrialDate(getApplicationContext(), SharedData.trail_ends);
        }
    }

    private void startNextActivity() {
        Intent intent1 = new Intent(CheckoutActivityJava.this,
                HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra(Common.SESSION_USERNAME, SharedData.username);
        startActivity(intent1);
        StopLoading();
    }

  /*  private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");


        double amount = Double.valueOf(amountTextView.getText().toString()) * 100;


        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", "eur"); //dont change currency in testing phase otherwise it won't work
        itemMap.put("id", "photo_subscription");
        itemMap.put("amount", amount);
        itemList.add(itemMap);
        payMap.put("items", itemList);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));

        payButton.setOnClickListener((View view) -> {

            blurrBackground();
            StartLoading();

            // CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            number = cardInputWidget.getCardParams().getNumber$stripe_release();
            cvc = cardInputWidget.getCardParams().getCvc$stripe_release();
            month = cardInputWidget.getCardParams().getExpMonth$stripe_release();
            year = cardInputWidget.getCardParams().getExpYear$stripe_release();
            System.out.println(number.toString() + "number kkk");
            System.out.println(cvc.toString() + "number cvc");
            System.out.println(month + "number month");
            System.out.println(year + "number year");

            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();


            if (params != null) {
                System.out.println("hello to futur");
                try {
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    stripe.confirmPayment(this, confirmParams);
                    //fgfg

                    // segmentedProgressBar.incrementCompletedSegments();
                } catch (IllegalArgumentException i) {
                    System.out.println("hello working dangeor;;;;;;;;;;;;;;;;;;,,,,,,,");
                    Log.e(i.getLocalizedMessage(), "null");

                }
                System.out.println("out of catch '''''''''''''''''");
            } else {
                System.out.println("out of if ;;;;;;;;;;;;;;;;;;;;");
                // pDialog.hide();
                StopLoading();
                Toast.makeText(this, "Please enter correct card number", Toast.LENGTH_LONG).show();
            }

        });


        System.out.println("almost out of  fun");
    }*/


    /*private void displayAlert(@NonNull String title, @Nullable String message) {

     *//* blurrBackground();
        StartLoading();*//*
        if (title == "Payment Completed") {
            Toast.makeText(CheckoutActivityJava.this, "Payment Completed Successfully..", Toast.LENGTH_SHORT).show();

            activateProfile(weight, height, age, gender, goals, level, profileImage, paymentMethod, comments,
                    orderId, orderRef, orderStatus, transactionDate, price, duration);

        } else {

            //   pDialog.hide();
            StopLoading();
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            builder.create().show();
        }
    }*/


 /*   private void activateProfile(String weight, String height, String age,
                                 String gender, String goals, String level,
                                 String profileImage, String paymentMethod,
                                 String comments, String orderId, String orderRef,
                                 String orderStatus, String transactionDate, String price,
                                 String duration) {
        //   pDialog.show();
        retrofit2.Call<LoginResponse> call = ApiClient.getService().profileActivate(id, weight, height, age, gender, goals, level, profileImage, paymentMethod, comments,
                orderId, orderRef, orderStatus, transactionDate, price, duration);

        // on below line we are executing our method.
        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull retrofit2.Call<LoginResponse> call, @NotNull retrofit2.Response<LoginResponse> response) {
                // this method is called when we get response from our api.


                if (response.isSuccessful()) {

                    *//* Toast.makeText(CheckoutActivityJava.this, "Data added to API", Toast.LENGTH_SHORT).show();*//*
                    try {
                        LocalDate now = LocalDate.now();
                        signupDate = now.format(DateTimeFormatter.ofPattern("MMM dd YYYY"));
                        Log.d("date", signupDate);
                        profileResponse = response.body();
                        assert profileResponse != null;
                        //SharedData.profileActivation = profileResponse.getProfileActivated();

                        //dbHelper.addUser(profileResponse);
                        //Use loged in shared preference for successfull login session

                        addDataToSharedPreference();
                        addUnitTosharedPrefrence();

                        SharedData.id = profileResponse.getId();
                        //  SharedData.status = profileResponse.getStatus();
                        SharedData.username = profileResponse.getName();
                        SharedData.email = profileResponse.getEmail();
                        SharedData.gender = profileResponse.getGender();
                        SharedData.age = profileResponse.getAge();
                        SharedData.height = profileResponse.getHeight();
                        SharedData.weight = profileResponse.getWeight();
                        SharedData.level = profileResponse.getLevel();
                        SharedData.signupDate = signupDate;

                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", SharedData.email);
                        editor.putString("id", SharedData.id);
                        editor.apply();
//                    SharedData.imageUrl=profileResponse.getProfileImage();

                        System.out.println("Profile Activated" + SharedData.username + "ppppppppppppppppppppppppppp");
                  *//*  Toast.makeText(CheckoutActivityJava.this,
                            SharedData.level + "ppppppppppppppppppppppppppp", Toast.LENGTH_SHORT).show();*//*
                        System.out.println("Profile Activated" + SharedData.level + "ppppppppppppppppppppppppppp");

                        //loadPrivacyFragment(SharedData.email,SharedData.id);

                        Intent intent1 = new Intent(CheckoutActivityJava.this,
                                HomeActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent1.putExtra("Name", SharedData.username);

                        //  StopLoading();
                        startActivity(intent1);
                    } catch (Exception e) {
                        // Crashlytics.logException(e);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();

                    }
                } else {
                    StopLoading();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                StopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("fail Api");
            }
        });
    }*/

    private void addEmailAndIdToSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("name", SharedData.username);
        editor.putString("id", id);
        editor.putString("token", token);
        editor.putString("refresh_token", refresh_token);
        editor.putString("subscription_id", SharedData.subscription_id);
        editor.putString("start_date", SharedData.start_date);
        editor.putString("end_date", SharedData.end_date);
        editor.putString("trail_ends", makeTrialEndDay(weekDaysHelper.getCurrentDateLikeServer()));
        editor.apply();
    }

    private void addDataToSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(currentUserId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("unitType", unitType);
        editor.putString("age", age);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("gender", gender);
        editor.putString("level", level);
        editor.putString("signupDate", signupDate);
        Log.d("date", signupDate.toString());
        System.out.println(profileResponse.getLevel() + "pppppppppppppppppppppppppp");

        editor.apply();
    }

    String makeTrialEndDay(String trialStartDate) {
        String trailEndDate = weekDaysHelper.get7thDayDate(trialStartDate);
        return trailEndDate;
    }

    private void addUnitTosharedPrefrence() {
        SharedPreferences sharedPreferences = getSharedPreferences(currentUserId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("unitType", unitType);
        editor.putString("level", profileResponse.getLevel());
        System.out.println(profileResponse.getLevel() + "newppppppppppp");
        editor.apply();
    }

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }*/

    /*private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }*/

   /* private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PayCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Error: " + e.toString(), Toast.LENGTH_LONG).show());
        }


        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->

                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }*/

    /*private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();

            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent)
//
//                );
                //for new dialog
                activity.displayAlert("Payment Completed", "Payment completed successfully");

                orderId = paymentIntent.getId();
                orderRef = paymentIntent.getPaymentMethodId();
                orderStatus = String.valueOf(paymentIntent.getStatus());
                comments = paymentIntent.getDescription();
                transactionDate = SimpleDateFormat.getDateInstance().format(new Date());
                paymentMethod = String.valueOf(paymentIntent.getPaymentMethod());


            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }


        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }*/

    private void StartLoading() {
        //dissable user interaction
        CheckoutActivityJava.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void StopLoading() {

        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        CheckoutActivityJava.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    private void blurrBackground() {
        blurView.setVisibility(View.VISIBLE);

        float radius = 1f;

        System.out.println("your in blurr");

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
    public void onBackPressed() {
        super.onBackPressed();
    }


    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode  Result code returned by the Google Pay API.
     * @param data        Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        //handlePaymentSuccess(paymentData);
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                }

                // Re-enables the Google Pay payment button.
                googlePayButton.setClickable(true);
        }
    }


   /* private void initializeUi() {

        billingClient = BillingClient.newBuilder(this.getApplicationContext())
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull @NotNull BillingResult billingResult,
                                                   @Nullable @org.jetbrains.annotations.Nullable List<Purchase> list) {
                        //here you can process the new purchases.
                    }
                }).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                //you can restart your client here.
            }

            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //here billing Client is ready to be used.

                    querySkuDetails();
                }
            }
        });

       /* Use view binding to access the UI elements
        layoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());


    }*/

    private void queryProduct() {

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(inAppItem1)
                                                .setProductType(
                                                        BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(
                            @NonNull BillingResult billingResult,
                            @NonNull List<ProductDetails> productDetailsList) {

                        if (!productDetailsList.isEmpty()) {
                            productDetails = productDetailsList.get(0);
                            runOnUiThread(() -> {
                                payButton.setEnabled(true);
                                productDetails.getName();
                            });
                        } else {
                            Log.i(TAG, "onProductDetailsResponse: No products");
                        }
                    }
                }
        );
    }

    /*private void querySkuDetails() {

        SkuDetailsParams.Builder skuDetailsParamsBuilder
                = SkuDetailsParams.newBuilder();
        ArrayList<String> list = new ArrayList<>();
        list.add(inAppItem1);
        skuDetailsParamsBuilder.setSkusList(list);
        skuDetailsParamsBuilder.setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull @NotNull BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (SkuDetails skuDetails : list) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                    }
                }
            }
        });

    }*/

   /* private void startPurchase() {
        if (mSkuDetailsMap.size() > 0) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(mSkuDetailsMap.get(inAppItem1))
                    .build();

            billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }

    protected void RestoringPurchases() {
        //To Query you have to provide skuType which is "BillingClient.SkuType.INAPP" or "BillingClient.SkuType.SUBS"
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull @NotNull BillingResult billingResult, @NonNull @NotNull List<Purchase> list) {
                //here you can process your purchases.
            }
        });
    }*/




   /* QueryProductDetailsParams queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                    .setProductList(
                            ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("product_id_example")
                                            .setProductType(BillingClient.ProductType.SUBS)
                                            .build()))
                    .build();*/



    /* private void displayGarment(JSONObject garment) throws JSONException {
     *//*layoutBinding.detailTitle.setText(garment.getString("title"));
        layoutBinding.detailPrice.setText(
                String.format(Locale.getDefault(), "$%.2f", garment.getDouble("price")));

        final String escapedHtmlText = Html.fromHtml(
                garment.getString("description"), Html.FROM_HTML_MODE_COMPACT).toString();
        layoutBinding.detailDescription.setText(Html.fromHtml(
                escapedHtmlText, Html.FROM_HTML_MODE_COMPACT));

        final String imageUri = String.format("@drawable/%s", garment.getString("image"));
        final int imageResource = getResources().getIdentifier(imageUri, null, getPackageName());
        layoutBinding.detailImage.setImageResource(imageResource);*//*
    }*/

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.
     * IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    /*private void possiblyShowGooglePayButton() {

        final Optional<JSONObject> isReadyToPayJson = Optional.ofNullable(PaymentsUtil.getIsReadyToPayRequest());
        if (!isReadyToPayJson.isPresent()) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    setGooglePayAvailable(task.getResult());
                } else {
                    Log.w("isReadyToPay failed", task.getException());
                }
            }
        });

        *//*task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {

                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });*//*
    }*/

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns {@code
     * false}.
     *
     * @param available isReadyToPay API response.
     */
   /* private void setGooglePayAvailable(boolean available) {
        if (available) {
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }*/

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
   /* private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, getString(R.string.payments_show_name, billingName),
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token: ", token);

        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }*/

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     *                   WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

   /* public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        googlePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        try {
            double garmentPrice = selectedGarment.getDouble("price");
            long garmentPriceCents = Math.round(garmentPrice * PaymentsUtil.CENTS_IN_A_UNIT.longValue());
            long priceCents = garmentPriceCents + SHIPPING_COST_CENTS;

            Optional<JSONObject> paymentDataRequestJson = Optional.ofNullable(PaymentsUtil.getPaymentDataRequest(priceCents));
            if (!paymentDataRequestJson.isPresent()) {
                return;
            }

            PaymentDataRequest request =
                    PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient.loadPaymentData(request),
                        this, LOAD_PAYMENT_DATA_REQUEST_CODE);
            }

        } catch (JSONException e) {
            throw new RuntimeException("The price cannot be deserialized from the JSON object.");
        }
    }*/

   /* private JSONObject fetchRandomGarment() {

        // Only load the list of items if it has not been loaded before
        if (garmentList == null) {
            garmentList = Json.readFromResources(this, R.raw.tshirts);
        }

        // Take a random element from the list
        int randomIndex = Math.toIntExact(Math.round(Math.random() * (garmentList.length() - 1)));
        try {
            return garmentList.getJSONObject(randomIndex);
        } catch (JSONException e) {
            throw new RuntimeException("The index specified is out of bounds.");
        }
    }*/
}