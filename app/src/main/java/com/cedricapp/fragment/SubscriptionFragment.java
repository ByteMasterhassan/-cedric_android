package com.cedricapp.fragment;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.adapters.SubscriptionAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.GetUserDetailsBack;
import com.cedricapp.interfaces.SubscriptionClickListener;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.PlansDataModel;
import com.cedricapp.model.StripeIntent;
import com.cedricapp.model.SubscriptionModel;
import com.cedricapp.model.UserStatusModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.ToastUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.activity.LoginActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.addresselement.AddressDetails;
import com.stripe.android.paymentsheet.addresselement.AddressLauncher;
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult;

import java.io.IOException;
import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class SubscriptionFragment extends Fragment implements UserStatusInterface, SubscriptionClickListener, GetUserDetailsBack {
    View view1;
    private ImageButton backArrow;
    private static ArrayList<PlansDataModel> plansDataArrayList = new ArrayList<>();
    private PlansDataModel subscriptionPlansDataModel;
    private SubscriptionAdapter subscriptionAdapter;
    private RecyclerView subscriptionRV;
    private ProgressBar progress;
    private TextView unsubscribeTv, mTextViewPlansTitle, mTextViewPlansDetails, newSubscriptionDate, txt_yourpackages, txt_otherpackages, txt_agreement, txt_terms;
    private OkHttpClient httpClient = new OkHttpClient();
    Resources resources;
    private String user_id, token, subscription_id;
    Context context;
    TextView goalTV, levelTV;
    boolean isSubscribed;
    LinearLayout subscribedLL, mLinearLayoutYourPackage;
    private ShimmerFrameLayout mFrameLayout, mFrameLayoutPackakge;
    private String message;
    PaymentSheet.CustomerConfiguration customerConfig;
    private MaterialTextView btn_Cancel, btn_Continue, btn_Ok;
    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;
    private AddressLauncher addressLauncher;
    private AddressDetails shippingDetails;

    SubscriptionModel subscribedPlanData;

    BlurView blurView;

    LottieAnimationView loading_lav;


    public SubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        mFrameLayoutPackakge.startShimmerAnimation();
        mFrameLayout.startShimmerAnimation();
        HomeActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();

        HomeActivity.showBottomNav();
    }

    @Override
    public void onPause() {
        mFrameLayout.stopShimmerAnimation();
        mFrameLayoutPackakge.stopShimmerAnimation();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        initialize();
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        addressLauncher = new AddressLauncher(this, this::onAddressLauncherResult);


//TODO needs to be change after resolve issue
        getAllPackages();
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
            Log.d(Common.LOG, "Payment Sheet Result: " + paymentSheetResult.toString());
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast(resources.getString(R.string.paymentcomplet));

            getUserDetails(token);
            //stripePayment(name,email,"4242424242424242",12,26,"123",planId);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.i(TAG, "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            /*Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();*/
            showAlert(resources.getString(R.string.paymentfaild), /*error.getLocalizedMessage()*/resources.getString(R.string.something_went_wrong));
        }
    }

    private void getUserDetails(String authorization) {
       /* blurrBackground();
        StartLoading();*/
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

                            SessionUtil.saveSubscriptionID(getContext(), SharedData.subscription_id);
                            SharedData.start_date = loginResponse.getData().getUser().getSubscription().getStarts_at();
                            SharedData.end_date = loginResponse.getData().getUser().getSubscription().getEnds_at();
                            SharedData.trail_ends = loginResponse.getData().getUser().getSubscription().getTrial_ends_at();
                            token = loginResponse.getData().getAccess_token();
                            SessionUtil.setAccessToken(getContext(), token);

                            //goBack();
                            loadDashboardFragment();

                            //refresh_token = loginResponse.getData().getRefresh_token();

                           /* String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            new UserDetailsUtil(getApplicationContext()).sendUserDetail("Bearer " + token, DeviceUtil.getUserDeviceModel(), DeviceUtil.getOS(),
                                    deviceID, "NA", WeekDaysHelper.getUTC_Time(), fcm_token, false, false, BuildConfig.VERSION_NAME, "" + BuildConfig.VERSION_CODE, "android","registration");*/
                            //saveDataAndCheckFragement(loginResponse);

                        }

                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Toast.makeText(getContext(), response.code() + "From Server", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //StopLoading();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // StopLoading();
                t.printStackTrace();
                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initialize() {
        context = context;

        plansDataArrayList.clear();

        mFrameLayout = view1.findViewById(R.id.shimmerLayoutItemPackage);
        mFrameLayoutPackakge = view1.findViewById(R.id.shimmerLayoutYourPackage);
        mLinearLayoutYourPackage = view1.findViewById(R.id.linearLayoutYourPackage);
        subscribedLL = view1.findViewById(R.id.subscribedLL);
        newSubscriptionDate = view1.findViewById(R.id.textViewNewSubscription);
        subscriptionRV = view1.findViewById(R.id.subscriptionRV);
        backArrow = view1.findViewById(R.id.backArrow);
        progress = view1.findViewById(R.id.progress);
        goalTV = view1.findViewById(R.id.goalTV);
        levelTV = view1.findViewById(R.id.levelTV);
        unsubscribeTv = view1.findViewById(R.id.unsubscribeTv);
        mTextViewPlansDetails = view1.findViewById(R.id.textViewPlansDetails);
        mTextViewPlansTitle = view1.findViewById(R.id.textViewPlansTitle);
        txt_yourpackages = view1.findViewById(R.id.txt_yourpackage);
        txt_otherpackages = view1.findViewById(R.id.txt_otherpackages);
        txt_agreement = view1.findViewById(R.id.txt_agreement);
        txt_terms = view1.findViewById(R.id.txt_terms);

        setlanguageToWidget();
        newSubscriptionDate.setText(resources.getString(R.string.new_subscription_is) + SharedData.end_date);
        token = SharedData.token;
        user_id = SharedData.id;

        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);

        goalTV.setText("Goal: " + SessionUtil.getUserGoal(getContext()));
        levelTV.setText("Level: " + SessionUtil.getUserLevel(getContext()));

        /*new UserStatusUtil(context, SubscriptionFragment.this)
                .getUserStatus("Bearer " + SharedData.token);*/
        //Log.d("sub_id", SharedData.subscription_id.toString());

        subscription_id = SessionUtil.getSubscriptionID_FromSession(getContext());
        SharedData.subscription_id = subscription_id;
        System.out.println(SharedData.subscription_status + "sub Status");
        System.out.println(subscription_id + "sub Status");
        if (SharedData.subscription_status != null) {
            if (SharedData.subscription_status.matches("cancel")) {
                isSubscribed = false;
                subscribedLL.setVisibility(GONE);
                Toast.makeText(getContext(), resources.getString(R.string.cancelsubscription), Toast.LENGTH_SHORT).show();
                if (token != null) {
                    getUserStatus(token);
                }

            } else {
                if (SessionUtil.getSubscriptionID_FromSession(getContext()) != null) {
                    Log.d("sub_id", SessionUtil.getSubscriptionID_FromSession(getContext()));
                    subscription_id = SessionUtil.getSubscriptionID_FromSession(getContext());
                }

                if (subscription_id == null) {
                    unsubscribeTv.setTextColor(Color.parseColor("#808080"));
                }
                if (subscription_id != null) {
                    Log.d("Subscription_id", subscription_id);
                    // subscription_id = SharedData.subscription_id;
                    getCurrentSubscribedPlan(SharedData.token, subscription_id);
                }
            }
        } else {
            Toast.makeText(getContext(), resources.getString(R.string.statusnull), Toast.LENGTH_SHORT).show();
        }


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        unsubscribeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    /*  Toast.makeText(requireContext(),"Not Implemented yet..!",Toast.LENGTH_SHORT).show();*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("Cancel Subscription").setMessage("You will be able to access Cedric till " + SharedData.end_date + " and won't be charged for next billing month.\nAre you sure you want to cancel subscription?")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                        cancelSubscription(token, user_id, subscription_id, null, 0, "");
                                    } else {
                                        Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //  holder.deleteIcon.setVisibility(View.INVISIBLE);

                                }
                            });
                    builder.show();

                } else {
                    Toast.makeText(getActivity(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setlanguageToWidget() {
        txt_yourpackages.setText(resources.getString(R.string.your_package));
        txt_otherpackages.setText(resources.getString(R.string.other_packages));
        txt_agreement.setText(resources.getString(R.string.by_continuing_you_agree_to_our));
        txt_terms.setText(resources.getString(R.string.terms_amp_conditions));
    }

    private void getCurrentSubscribedPlan(String token, String subscription_id) {
        Call<SubscriptionModel> call = ApiClient.getService().currentSubscriptionPlan("Bearer " + SharedData.token, subscription_id);
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    if (response.isSuccessful()) {
                        subscribedPlanData = response.body();
                        if (response.body().getStatus()) {
                            isSubscribed = true;

                            subscribedLL.setVisibility(View.VISIBLE);
                            mFrameLayoutPackakge.stopShimmerAnimation();
                            mFrameLayoutPackakge.setVisibility(GONE);
                            mLinearLayoutYourPackage.setVisibility(View.VISIBLE);
                            if (subscribedPlanData.getData() != null) {
                                if (subscribedPlanData.getData().getAmount() != null) {
                                    mTextViewPlansDetails.setText(subscribedPlanData.getData().getCurrency() + " "
                                            + (subscribedPlanData.getData().getAmount() / 100)/* + "/Month"*/);
                                }
                                if (subscribedPlanData.getData().getPackage() != null) {
                                    mTextViewPlansTitle.setText(/*"STANDARD"*/subscribedPlanData.getData().getPackage());
                                }
                            }
                        } else {
                            isSubscribed = false;
                            subscribedLL.setVisibility(GONE);
                            Toast.makeText(getContext(), "Subscribed plan not available", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_getCurrentplan",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_getCurrentPlan",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void cancelSubscription(String token, String user_id, String subscription_id, PlansDataModel plansDataModel, int index, String planId) {
        startLoading();
        blurrBackground();

        Call<SubscriptionModel> call = ApiClient.getService().cancelSubscriptionPlan("Bearer " + SharedData.token, user_id, subscription_id);
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    if (plansDataModel == null) {
                        stopLoading();
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus()) {
                                Log.d("Subscription", response.body().getData().getStatus().toString());
                                SharedData.subscription_status = "cancel";
                                SessionUtil.saveSubscriptionID(getContext(), "");
                                subscribedLL.setVisibility(GONE);
                                Toast.makeText(getContext(), response.body().getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                                unsubscribeTv.setTextColor(Color.parseColor("#808080"));
                                if (plansDataModel != null) {
                                    createPaymentIntent((Integer.parseInt(plansDataModel.getData().get(index).getAmount()) * 100),
                                            plansDataModel.getData().get(index).getCurrency(), "card", planId);
                                }
                            } else {
                                Toast.makeText(getContext(), response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                // unsubscribeTv.setTextColor(Integer.parseInt("#808080"));
                                unsubscribeTv.setTextColor(Color.parseColor("#808080"));
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "Response body is null");
                            }
                        }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    stopLoading();
                    //startloading();
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_cancelSubscription",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                stopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_cancelSubscription",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }

            }
        });


    }

    private void goBack() {
        if (ConnectionDetector.isConnectedWithInternet(getContext())) {

            if (getFragmentManager().getBackStackEntryCount() != 0) {
                if (isAdded()) {
                    getFragmentManager().popBackStack();
                    stopLoading();
                }
            }
        } else {
            Toast.makeText(getContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startloading() {
        if (isAdded()) {
            progress.setVisibility(View.VISIBLE);
            //dissable user interaction
            requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        mFrameLayout.startShimmerAnimation();
    }


    private void getAllPackages() {
        startloading();
        // on below line we are calling a method to get all the courses from API.
        System.out.println(SessionUtil.getProductID(getContext()) + "product Id");
        Call<PlansDataModel> call = ApiClient.getService().getAllPackages("Bearer " + SharedData.token, SessionUtil.getProductID(getContext()));

        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        call.enqueue(new Callback<PlansDataModel>() {
            @Override
            public void onResponse(Call<PlansDataModel> call, Response<PlansDataModel> response) {
                try {
                    // inside on response method we are checking
                    // if the response is success or not.
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        Log.d(Common.LOG, "Response Status " + message.toString());
                        //  Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        //ToastUtil.showToastForFragment(context, false ,isAdded(), "Sucessful", Toast.LENGTH_SHORT);

                        // below line is to add our data from api to our array list.
                        subscriptionPlansDataModel = response.body();
                        if (subscriptionPlansDataModel != null) {
                            if (subscriptionPlansDataModel.getData() != null) {
                                if (subscriptionPlansDataModel.getData().size() > 0) {
                                    if (isAdded() && getContext() != null) {
                                        mFrameLayout.stopShimmerAnimation();
                                        mFrameLayout.setVisibility(View.GONE);
                                        subscriptionRV.setVisibility(View.VISIBLE);
                                        subscriptionAdapter = new SubscriptionAdapter(subscriptionPlansDataModel, getContext(), isSubscribed, SubscriptionFragment.this, SharedData.subscription_status);
                                        subscriptionRV.setAdapter(subscriptionAdapter);
                                        subscriptionAdapter.notifyDataSetChanged();
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(Common.LOG, "Fragment is not attached with activity or getContext is null");
                                        }
                                    }
                                } else {
                                    ToastUtil.showToastForFragment(getContext(), false, isAdded(), "Subscriptions unavailable", Toast.LENGTH_SHORT);
                                    if (Common.isLoggingEnabled) {
                                        Log.e(Common.LOG, "subscriptionPlansDataModel.getData() size is zero");
                                    }
                                }
                            } else {
                                ToastUtil.showToastForFragment(getContext(), false, isAdded(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                                if (Common.isLoggingEnabled) {
                                    Log.e(Common.LOG, "subscriptionPlansDataModel.getData() is null");
                                }
                            }
                        } else {
                            ToastUtil.showToastForFragment(getContext(), false, isAdded(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "subscriptionPlansDataModel is null");
                            }
                        }
                        stopLoading();
                        //Toast.makeText(requireContext(),String.valueOf(plansDataArrayList.size()), Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (message != null) {
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Response Status " + message.toString());
                            }
                        }
                        // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        ToastUtil.showToastForFragment(getContext(), false, isAdded(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                        stopLoading();
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_getAllPackage",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    stopLoading();
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlansDataModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
                // toast message for fail to get data.
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SubscriptionFragment_getAllPackage",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                ToastUtil.showToastForFragment(getContext(), false, isAdded(), "Failed To Get Data", Toast.LENGTH_SHORT);
                //Toast.makeText(requireContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUserStatus(String accessToken) {
        Call<UserStatusModel> call = ApiClient.getService().getUserStatus("Bearer " + accessToken);
        call.enqueue(new Callback<UserStatusModel>() {
            @Override
            public void onResponse(Call<UserStatusModel> call, Response<UserStatusModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    Log.d(Common.LOG, "Response Status " + message.toString());
                    //Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                    UserStatusModel userStatusModel = response.body();
                    if (userStatusModel.getData() != null) {

                        if (userStatusModel.getData().getStatus() != null) {
                            SharedData.userStatus = userStatusModel.getData().getStatus();
                            Log.d(Common.LOG, "user Status " + SharedData.userStatus);
                            SessionUtil.setUserStatus(getContext(), SharedData.userStatus);

                            if (userStatusModel.getData().getStatus().matches("active")) {
                                if (userStatusModel.getData().getSubscriptionStatus() != null) {

                                    SharedData.subscription_status = userStatusModel.getData().getSubscriptionStatus();
                                    SharedData.is_dev_mode = userStatusModel.getData().getIsDev();
                                    SharedData.subscription_id = userStatusModel.getData().getSubscriptionId();
                                    SessionUtil.saveSubscriptionID(getContext(), SharedData.subscription_id);
                                    SessionUtil.setIsDevStatus(getContext(), SharedData.is_dev_mode);
                                    // SharedData.subscription_status = "blocked";

                                    /*if (SharedData.subscription_status.equals("cancel")) {
                                        showDialogBox();
                                    }*//* else if (SharedData.subscription_status.equals("blocked")) {
                                        showStandardDialog();
                                    }*/
                                    if (userStatusModel.getData().getSubscriptionEndsAt() != null) {
                                        //String subscriptionStartsAt = userStatusModel.getData().getSubscriptionStartsAt();
                                        String subscriptionEndsAt = userStatusModel.getData().getSubscriptionEndsAt();
                                        if (!WeekDaysHelper.isSubscriptionAvailable(WeekDaysHelper.getDateTimeNow(), subscriptionEndsAt)) {
                                            showDialogBox();
                                            //count++;
                                        }
                                    }

                                    //TODO
                                    if (userStatusModel.getData().getStatus() != null) {
                                       /* userStatusListener.getStatus(userStatusModel.getData().getStatus(),
                                                userStatusModel.getData().getSubscriptionStatus());*/
                                    }
                                }
                            } else if (userStatusModel.getData().getStatus().matches("blocked")) {
                                showStandardDialog();
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "User Status Util is not successful");
                    }
                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                        if (Common.isLoggingEnabled) {
                            if (errorMessageModel != null) {
                                Log.e(Common.LOG, "" + errorMessageModel);
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            Log.d(Common.LOG, "Response Status " + message.toString());
                            // Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("UserStatusUtil_getUserDetails",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserStatusModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("UserStatusUtil_getUserDetails_failure",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void showDialogBox() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_subscription);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_Cancel = dialog.findViewById(R.id.btn_left);
        btn_Continue = dialog.findViewById(R.id.btn_right);


        btn_Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);

                btn_Cancel.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                settingObject.activityDataSync(getContext(), "dashboard");
                SessionUtil.logout(getContext());

                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                /*  if (isAdded()) {*/
               /* if (context != null) {
                    Fragment fragment = new SubscriptionFragment();
                    FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    //changes
//
                    ft.addToBackStack("SubscriptionFragment");
                    ft.commit();
                }*/
                //}
                dialog.dismiss();
            }


        });
        dialog.show();
    }

    private void showStandardDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_blocked_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_Ok = dialog.findViewById(R.id.btn_Ok);
        //btn_Continue = dialog.findViewById(R.id.btn_right);
        TextView textView = dialog.findViewById(R.id.dialog_blocked_description);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        btn_Ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);

                btn_Ok.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.btn_background_dialog_blocked));
                settingObject.activityDataSync(getContext(), "dashboard");
                SessionUtil.logout(getContext());
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(intent);


                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        /*btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_background_dialog_right_click));

                if (context != null) {
                    Fragment fragment = new SubscriptionFragment();
                    FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    //changes
//
                    ft.addToBackStack("SubscriptionFragment");
                    ft.commit();
                }
                //}
                dialog.dismiss();
            }


        });*/
        dialog.show();

      /* String text = "You are not authorized to use the application. Please contact to the administration and send email to ";
        String linkText = "https://info@cedrics.se";
        SpannableString message = new SpannableString(text + linkText);
        int linkStartIndex = text.length();
        int linkEndIndex = linkStartIndex + linkText.length();
        message.setSpan(new URLSpan(linkText), linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



      AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        MaterialTextView textView = new MaterialTextView(context);
        textView.findViewById(R.id.dialog_blocked_description);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView.setText(message);
       // builder.setView(textView);


      //  builder.setView(textView);

       // builder.setMessage("You are not authorized to use the application.\n Please contact to the administration and send email to");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do something when the OK button is clicked
                SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();
                settingObject.activityDataSync(context, "dashboard");
                SessionUtil.logout(context);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "In Dashboard, User Status: " + userStatus + "\nSubscription Status: " + subscriptionStatus);
        }
    }


    private void createPaymentIntent(int amount, String currency, String payementMethod,
                                     String planID) {
        // blurrBackground();
        // StartLoading();
        Call<StripeIntent> call = ApiClient.getService().createPaymentIntent("Bearer " + token, amount, currency, payementMethod, planID, true);
        call.enqueue(new Callback<StripeIntent>() {
            @Override
            public void onResponse(Call<StripeIntent> call, Response<StripeIntent> response) {
                // StopLoading();
                stopLoading();
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    Log.d(Common.LOG, "Response Status: " + message.toString());
                    StripeIntent stripePaymentIntent = response.body();
                    if (stripePaymentIntent != null && stripePaymentIntent.getData() != null
                            && stripePaymentIntent.getData().getCustomer() != null
                            && stripePaymentIntent.getData().getEphemeralKey() != null) {
                        customerConfig = new PaymentSheet.CustomerConfiguration(
                                stripePaymentIntent.getData().getCustomer(),
                                stripePaymentIntent.getData().getEphemeralKey()
                        );
                        PaymentConfiguration.init(getContext(), stripePaymentIntent.getData().getStripe_key());
                        paymentIntentClientSecret = stripePaymentIntent.getData().getPaymentIntent();
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Retrieved PaymentIntent: " + paymentIntentClientSecret);
                        }
                        onPayClicked(getActivity().getWindow().getDecorView().getRootView());

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Response Code is " + response.code() + " and stripePaymentIntent == null && stripePaymentIntent.getData() == null");
                        }
                        showAlert(resources.getString(R.string.something_went_wrong), resources.getString(R.string.paymentunable));
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Response Code is " + response.code());
                    }
                    showAlert(resources.getString(R.string.something_went_wrong), resources.getString(R.string.pleasetryagian));
                }

            }

            @Override
            public void onFailure(Call<StripeIntent> call, Throwable t) {
                //StopLoading();
                stopLoading();
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                showAlert(resources.getString(R.string.fialedtoload), resources.getString(R.string.paymentunable));
            }
        });
    }

    private void onPayClicked(View view) {
        PaymentSheet.Configuration config = new PaymentSheet.Configuration.Builder("Cedrics, Inc.")
                .allowsDelayedPaymentMethods(false).
                googlePay(new PaymentSheet.GooglePayConfiguration(
                        PaymentSheet.GooglePayConfiguration.Environment.Test,
                        "SE"))
                .build();


        /*config.setAllowsDelayedPaymentMethods(true);
        config.setPrimaryButtonColor(ColorStateList.valueOf(Color.rgb(248, 72, 94)));
        config.setGooglePay(new PaymentSheet.GooglePayConfiguration(
                PaymentSheet.GooglePayConfiguration.Environment.Test,
                "SE"
        ));*/

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, config);
        //onAddressClicked(view);
    }

    private void showAlert(String title, @Nullable String message) {
        getActivity().runOnUiThread(() -> {
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(resources.getString(R.string.ok), null)
                    .create();
            dialog.show();
        });
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
    }

    @Override
    public void openPaymentSheet1(PlansDataModel plansDataModel, int index, String subscriptionStatus) {
        String planId = plansDataModel.getData().get(index).getPlanId();
        if (subscribedPlanData != null && subscribedPlanData.getData() != null
                && subscribedPlanData.getData().getPlanID() != null) {
            if (subscribedPlanData.getData().getPlanID().equals(planId)) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.alreadysubscribe), Toast.LENGTH_SHORT).show();
                }
            } else {
                resubscribe(plansDataModel, index, subscriptionStatus, planId);
            }
        } else {
            resubscribe(plansDataModel, index, subscriptionStatus, planId);
        }


    }

    void resubscribe(PlansDataModel plansDataModel, int index, String subscriptionStatus, String planId) {
        try {
            System.out.println(subscriptionStatus + "statusss");
            System.out.println(SharedData.subscription_status + "statusss");
            System.out.println(planId + "statusss");
            //dummyPaymentIntentForTesting();
            if (SharedData.subscription_status.equals("cancel") || SharedData.subscription_status.equals("null")) {
                startLoading();
                blurrBackground();
                createPaymentIntent((Integer.parseInt(plansDataModel.getData().get(index).getAmount()) * 100),
                        plansDataModel.getData().get(index).getCurrency(), "card", planId);

            } else if (SharedData.subscription_status.equals("active") ||
                    SharedData.subscription_status.equals("trialing")) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    /*  Toast.makeText(requireContext(),"Not Implemented yet..!",Toast.LENGTH_SHORT).show();*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle(resources.getString(R.string.cancelsubscription)).setMessage(resources.getString(R.string.accesscedric) + SharedData.end_date + resources.getString(R.string.nextmonth))
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                        cancelSubscription(token, user_id, subscription_id, plansDataModel, index, planId);
                                    } else {
                                        Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }


                                    // getUserStatus(token);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //  holder.deleteIcon.setVisibility(View.INVISIBLE);

                                }
                            });
                    builder.show();

                } else {
                    Toast.makeText(getActivity(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void getUserDataBack(LoginResponse loginResponse) {

    }

    private void loadDashboardFragment() {
        Fragment fragment = new DashboardFragment();
        //replacing the fragment
        if (fragment != null) {
            if (isAdded()) {
                if (getContext() != null) {
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    ft.disallowAddToBackStack();
                    ft.commit();
                }
            }

//            Toast toast = Toast.makeText(getContext(), "Data updated.", Toast.LENGTH_SHORT);
//            // toast.getView().setBackgroundResource(R.color.yellow);
//            toast.show();
        }
        //  pDialog.hide();
        //StopLoading();

    }
    /*private void StartLoading() {
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
    }*/

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

        if (isAdded()) {
            progress.setVisibility(GONE);
        }
        if (blurView != null) {
            blurView.setVisibility(View.INVISIBLE);
            blurView.setVisibility(View.GONE);
        }
        //Enable user interaction

        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_stopLoading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        if (loading_lav != null) {
            loading_lav.setVisibility(View.GONE);
            loading_lav.pauseAnimation();
        }
    }
}