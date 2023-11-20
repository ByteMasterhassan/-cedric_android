package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.model.ApplicationDetailsModel;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.UserStatusModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.fragment.SettingFragment;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class SplashActivity extends AppCompatActivity implements UserStatusInterface {
    MaterialTextView mTextviewAppTitle;
    private MaterialTextView btn_update, btn_Remind, btn_update_soft;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;
    private AppUpdateManager appUpdateManager;
    private AppUpdateManager appUpdateManagerFlexible;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    int versionCode;
    private String VersionName;
    private boolean loggedInStatus;
    MaterialTextView btn_Cancel, btn_Continue, btn_Ok;
    private String userEmail;
    private FirebaseAnalytics firebaseAnalytics;
    private String message;


    boolean isAPI_Called;
    Handler handler;
    private Runnable myRunnable;

    Call<UserStatusModel> call;

    String TAG = "SPLASH_TAG";

    boolean isAppNeedToBeUpdated = false;

    Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = Localization.setLanguage(SplashActivity.this, getResources());
        OneLineCalendarView.isLogging = Common.isLoggingEnabled;
        setContentView(R.layout.activity_splash);
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            FirebaseApp.initializeApp(this);
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        setAPIEnvironment();
        versionCode = BuildConfig.VERSION_CODE;
        VersionName = BuildConfig.VERSION_NAME;
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "version Code " + versionCode);
        }


        appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
        appUpdateManagerFlexible = AppUpdateManagerFactory.create(SplashActivity.this);

        userEmail = SessionUtil.getUserEmailFromSession(getApplicationContext());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Email " + userEmail);
        }

        if (userEmail.isEmpty()) {
            loggedInStatus = false;
            if (StepCountServiceUtil.isMyServiceRunning(StepsService.class, getApplicationContext())) {
                StepCountServiceUtil.stopStepCountService(getApplicationContext());
            }
        } else {
            loggedInStatus = true;
        }

        //for Flexible
       /* installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
        checkForFlexibleUpdate();*/


        //checkUpdate();

        SharedData.token = SessionUtil.getAccessToken(getApplicationContext());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Access Token on Splash Screen: " + SharedData.token);
        }
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "loogedStatus = " + loggedInStatus);
        }

        //SessionUtil.saveAPI_Environment(getApplicationContext(),false);
        //set Full Screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mTextviewAppTitle = findViewById(R.id.mTextViewAppTitle);

        Shader shader = new LinearGradient(0, 0, 0,
                mTextviewAppTitle.getTextSize(), getColor(R.color.gradient1),
                getColor(R.color.gradient2),
                Shader.TileMode.CLAMP);

        mTextviewAppTitle.getPaint().setShader(shader);


        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            if (loggedInStatus) {
                handlerForCheckInternet();
                if (SharedData.token != null) {
                    userStatus(SharedData.token);
                }
            } else {
                handler();
            }

        } else {
            handler();
            Toast.makeText(this, resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }


        //handler();

    }

    private void userStatus(String token) {

        call = ApiClient.getService().getUserStatus("Bearer " + token);
        call.enqueue(new Callback<UserStatusModel>() {
            @Override
            public void onResponse(Call<UserStatusModel> call, Response<UserStatusModel> response) {
                isAPI_Called = true;
                if (handler != null) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "handler stopped");
                    }
                    handler.removeMessages(0);
                }
                if (response.isSuccessful()) {

                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "user Status1 " + message.toString());
                    }
                    UserStatusModel userStatusModel = response.body();
                    if (userStatusModel.getData() != null) {

                        if (userStatusModel.getData().getStatus() != null) {
                            SharedData.userStatus = userStatusModel.getData().getStatus();
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "user Status1 " + SharedData.userStatus);
                            }
                            SessionUtil.setUserStatus(getApplicationContext(), SharedData.userStatus);
                            if (userStatusModel.getData().getStatus().matches("active")) {
                                if (userStatusModel.getData().getSubscriptionStatus() != null) {
                                    SharedData.subscription_status = userStatusModel.getData().getSubscriptionStatus();
                                    SharedData.is_dev_mode = userStatusModel.getData().getIsDev();
                                    SessionUtil.setIsDevStatus(getApplicationContext(), SharedData.is_dev_mode);
                                    if (loggedInStatus) {
                                        if (userStatusModel.getData().getSubscriptionEndsAt() != null) {
                                            //String subscriptionStartsAt = userStatusModel.getData().getSubscriptionStartsAt();
                                            String subscriptionEndsAt = userStatusModel.getData().getSubscriptionEndsAt();
                                            /*if (!WeekDaysHelper.isSubscriptionAvailable(WeekDaysHelper.getDateTimeNow(), subscriptionEndsAt)) {
                                                showDialogBox();
                                                //count++;
                                            } else {
                                                handler();
                                            }*/
                                        }

                                        if (SharedData.subscription_status.matches("active") ||
                                                (SharedData.subscription_status.matches("trialing"))) {
                                            handler();
                                        }
                                        /*if (SharedData.subscription_status.matches("cancel") ||
                                                SharedData.subscription_status.matches("null")) {
                                            showDialogBox();
                                        } else if (SharedData.subscription_status.matches("active") ||
                                                (SharedData.subscription_status.matches("trialing"))) {
                                            handler();
                                        }*/
                                    } else {
                                        handler();
                                    }
                                } else {
                                    handler();
                                    if (userStatusModel != null && userStatusModel.getData() != null && userStatusModel.getData().getSubscriptionStatus() != null) {
                                        Toast.makeText(getApplicationContext(), userStatusModel.getData().getSubscriptionStatus().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else if (userStatusModel.getData().getStatus().matches("blocked")) {
                                showStandardDialog();
                            }

                        }
                    }
                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "User Status Util is not successful");
                    }
                    Gson gson = new GsonBuilder().create();
                    ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                    try {
                        errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                        if (Common.isLoggingEnabled) {
                            if (errorMessageModel != null) {
                                Log.e(TAG, "Error Message: " + errorMessageModel);
                            }
                        }
                        //Toast.makeText(getApplicationContext(), "Your Session is Expired,Please Login again! ", Toast.LENGTH_LONG).show();
                        if (message != null) {
                            Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
                        }

                        LogoutUtil.redirectToLogin(SplashActivity.this);
                        /*Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();*/
                    } catch (Exception e) {
                        handler();
                        FirebaseCrashlytics.getInstance().recordException(e);

                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }

                        new LogsHandlersUtils(getApplicationContext())
                                .getLogsDetails("splash_userStatus", userEmail
                                        , EXCEPTION, SharedData.caughtException(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserStatusModel> call, Throwable t) {
                if (handler != null) {
                    handler.removeMessages(0);
                }
                isAPI_Called = true;
                FirebaseCrashlytics.getInstance().recordException(t);
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("splash_userStatus_OnFailure", userEmail
                                , EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                handler();
            }
        });
    }

    private void showStandardDialog() {

        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_blocked_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.you_are_not_authorized_to_use_the_application));


        btn_Ok = dialog.findViewById(R.id.btn_Ok);
        btn_Ok.setText(resources.getString(R.string.ok_btn));
        //btn_Continue = dialog.findViewById(R.id.btn_right);
        TextView textView = dialog.findViewById(R.id.dialog_blocked_description);
        textView.setText(resources.getString(R.string.admin_email));
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        btn_Ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();*/
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);

                btn_Ok.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.btn_background_dialog_blocked));
                //settingObject.activityDataSync(getApplicationContext(), "splash");
                SessionUtil.logout(getApplicationContext());
                finish();
                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        /*btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_background_dialog_right_click));

                if (getContext() != null) {
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


    }

    private void showDialogBox() {

        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_subscription);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.your_subscription_is_ended));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(resources.getString(R.string.do_you_want_to_resubscribe));

        btn_Cancel = dialog.findViewById(R.id.btn_left);
        btn_Cancel.setText(resources.getString(R.string.btn_no));
        btn_Continue = dialog.findViewById(R.id.btn_right);
        btn_Continue.setText(resources.getString(R.string.btn_yes));


        btn_Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SettingFragment settingObject;
                settingObject = SettingFragment.getInstance();
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                btn_Cancel.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                settingObject.activityDataSync(getApplicationContext(), "splash");
                SessionUtil.logout(getApplicationContext());
                finish();
                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                // close this activity
                finish();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void checkForFlexibleUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManagerFlexible.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startFlexibleUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startFlexibleUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManagerFlexible.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, SplashActivity.FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            FirebaseCrashlytics.getInstance().recordException(e);
            new LogsHandlersUtils(getApplicationContext()).getLogsDetails("splash_FlexibleUpdate", userEmail
                    , EXCEPTION, SharedData.caughtException(e));
        }
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                isAppNeedToBeUpdated = false;
                handler();
            } else {
                isAppNeedToBeUpdated = false;
                handler();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, SplashActivity.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }

            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("splash_ImmediateUpdate_", userEmail
                            , EXCEPTION, SharedData.caughtException(e));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                isAppNeedToBeUpdated = false;
                // handler();
                Toast.makeText(getApplicationContext(), resources.getString(R.string.app_update_cancel) + " " + resultCode, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_OK) {
                isAppNeedToBeUpdated = false;
                handler();
                Toast.makeText(getApplicationContext(), resources.getString(R.string.update_successfully), Toast.LENGTH_LONG).show();
            } else {
                isAppNeedToBeUpdated = false;
                Toast.makeText(getApplicationContext(), resources.getString(R.string.update_failed) + ": " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        } else if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                isAppNeedToBeUpdated = false;

                Toast.makeText(getApplicationContext(), resources.getString(R.string.update_canceled) + ": " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                isAppNeedToBeUpdated = false;
                handler();
                Toast.makeText(getApplicationContext(), resources.getString(R.string.update_successfully) + " " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                isAppNeedToBeUpdated = false;
                Toast.makeText(getApplicationContext(), resources.getString(R.string.update_failed) + ": " + resultCode, Toast.LENGTH_LONG).show();
                checkForFlexibleUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                        resources.getString(R.string.new_app_ready), Snackbar.LENGTH_INDEFINITE)

                .setAction(resources.getString(R.string.installed), view -> {
                    if (appUpdateManagerFlexible != null) {
                        appUpdateManagerFlexible.completeUpdate();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.purple_500))
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManagerFlexible != null) {
            if (installStateUpdatedListener != null) {
                appUpdateManagerFlexible.unregisterListener(installStateUpdatedListener);
            }
        }
    }

    private void handler() {
        new Handler().postDelayed(() -> {
            //This method will be executed once the timer is over
            // Start your app main activity
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "OTHER HANDLER RAN");
            }
            if (!isAppNeedToBeUpdated) {
                Intent i;
                if(loggedInStatus){
                    i = new Intent(SplashActivity.this, HomeActivity.class);
                }else{
                    i = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3000);
    }

    private void handlerForCheckInternet() {
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAPI_Called) {
                    if (call != null)
                        if (call.isExecuted()) {
                            call.cancel();
                        }
                    Intent i;
                    if (loggedInStatus) {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Slow Net so redirecting to Main Activity");
                        }
                        i = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Slow Net so redirecting to Home Activity");
                        }
                        i = new Intent(SplashActivity.this, HomeActivity.class);
                    }
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        };
        handler.postDelayed(myRunnable, 5500);
        checkAppUpdates();
    }

    private void checkAppUpdates() {

        Call<ApplicationDetailsModel> call = ApiClient.getService().checkUpdates("android");
        call.enqueue(new Callback<ApplicationDetailsModel>() {
            @Override
            public void onResponse(Call<ApplicationDetailsModel> call, Response<ApplicationDetailsModel> response) {
                isAPI_Called = true;
                if (handler != null) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "handler stopped");
                    }
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                }
                if (response.isSuccessful()) {
                    ApplicationDetailsModel applicationDetailsModel = response.body();
                    if (applicationDetailsModel != null) {
                        if (applicationDetailsModel.getData() != null) {
                            if (response.code() == 200) {
                                if (/*!applicationDetailsModel.getData().getAppVersion().equals(VersionName) || */applicationDetailsModel.getData().getBuildCode() > versionCode) {

                                    if (applicationDetailsModel.getData().getStatus().matches("force_update")) {
                                        //showCustomDialog();
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "Version code from server is " + applicationDetailsModel.getData().getBuildCode() + " and in gradle is " + versionCode);
                                        }
                                        isAppNeedToBeUpdated = true;

                                        checkUpdate();


                                    } else if (applicationDetailsModel.getData().getStatus().matches("soft_update")) {
                                        //showCustomDialogForSoft();
                                        installStateUpdatedListener = state -> {
                                            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                popupSnackBarForCompleteUpdate();
                                            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                                                removeInstallStateUpdateListener();
                                            } else {
                                                if (Common.isLoggingEnabled) {
                                                    Log.d(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                                                }
                                                //Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
                                            }
                                        };
                                        appUpdateManagerFlexible.registerListener(installStateUpdatedListener);
                                        checkForFlexibleUpdate();

                                    } else if (applicationDetailsModel.getData().getStatus().matches("no_update")) {
                                        //handler();
                                    }
                                } else if (applicationDetailsModel.getData().getBuildCode() <= versionCode) {
                                    // handler();
                                }


                            } else {
                                if (Common.isLoggingEnabled) {
                                    // Toast.makeText(getApplicationContext(),ResponseStatus.getResponseCodeMessage(response.code()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                } else {
                    if (response.code() == 401 || response.code() == 404) {
                        Toast.makeText(getApplicationContext(),
                                ResponseStatus.getResponseCodeMessage(response.code(), resources).toString(), Toast.LENGTH_SHORT).show();
                        // handler();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                ResponseStatus.getResponseCodeMessage(response.code(), resources).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApplicationDetailsModel> call, Throwable t) {
                isAPI_Called = true;
                if (handler != null) {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "handler stopped");
                    }
                    handler.removeMessages(0);
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("splash_CheckForUpdates", userEmail
                                , EXCEPTION, SharedData.throwableObject(t));
                //handler();
            }
        });
    }

    private void showCustomDialogForSoft() {

        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_soft_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_Remind = dialog.findViewById(R.id.btn_left_soft);
        btn_Remind.setText(resources.getString(R.string.remind_me_later));
        btn_update_soft = dialog.findViewById(R.id.btn_right_soft);
        btn_update.setText(resources.getString(R.string.update_soft));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.latest_update_is_available));

        TextView textView = dialog.findViewById(R.id.dialog_description);
        textView.setText(resources.getString(R.string.do_you_want_to_update));


        btn_Remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                btn_Remind.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                handler();
                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_update_soft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_update_soft.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));

                installStateUpdatedListener = state -> {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackBarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        removeInstallStateUpdateListener();
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                        }
                        // Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
                    }
                };
                appUpdateManager.registerListener(installStateUpdatedListener);
                checkForFlexibleUpdate();

                //handler();
                dialog.dismiss();
            }


        });
        dialog.show();

    }

    void showCustomDialog() {
        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_app_updates);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));


        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.please_update_to_get_latest_features));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(R.string.to_update_please_press_button);

        btn_update = dialog.findViewById(R.id.btn_left);
        btn_update.setText(resources.getString(R.string.btn_update));
        // btn_Continue = dialog.findViewById(R.id.btn_right);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_update.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                checkUpdate();
                handler();

                dialog.dismiss();
            }


        });
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInstallStateUpdateListener();
    }


    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {

    }

    void setAPIEnvironment() {
        if (SessionUtil.getAPI_URL(getApplicationContext()).matches("")) {
            String environment = SessionUtil.getAPP_Environment(SplashActivity.this);

            if (environment.matches("testing")) {
                SharedData.BASE_URL = Common.TESTING_BASE_URL;
                /* environmentSwitch.setChecked(true);*/
            } else if (environment.matches("stagging")) {
                SharedData.BASE_URL = Common.STAGING_BASE_URL;

            } else if (environment.matches("beta")) {
                SharedData.BASE_URL = Common.BETA_BASE_URL;

            } else if (environment.matches("testing_beta")) {
                SharedData.BASE_URL = Common.TESTING_BETA_BASE_URL;

            } else {
                SharedData.BASE_URL = Common.PRODUCTION_BASE_URL;
                /* environmentSwitch.setChecked(false);*/
            }
        } else {
            SharedData.BASE_URL = SessionUtil.getAPI_URL(getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                IMMEDIATE,
                                                this,
                                                IMMEDIATE_APP_UPDATE_REQ_CODE);
                                    } catch (IntentSender.SendIntentException e) {
                                        if (Common.isLoggingEnabled) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
        }
    }
}