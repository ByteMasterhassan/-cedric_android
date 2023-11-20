package com.cedricapp.fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.cedricapp.common.Common.ABOUT_US_URL;
import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.FAQS_URL;
import static com.cedricapp.common.Common.IOS_CHANGE_SUBSCRIPTION_URL;
import static com.cedricapp.common.Common.PRIVACY_POLICY_URL;
import static com.cedricapp.common.Common.TERMS_AND_POLICY_URL;
import static com.cedricapp.common.Common.currentUser;
import static com.cedricapp.activity.LoginActivity.SHARED_PREF_NAME;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.LogoutInterface;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.LoginResponse;
import com.cedricapp.model.LogoutModel;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.utils.UserStatusUtil;
import com.cedricapp.activity.AllergiesActivity;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.activity.FoodPreferencesActivity;
import com.cedricapp.activity.LanguageActivity;
import com.cedricapp.activity.LoginActivity;
import com.cedricapp.activity.TestingPurposeActivity;
import com.cedricapp.service.StepCounterDataSync;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class SettingFragment extends Fragment implements View.OnClickListener, LogoutInterface, UserStatusInterface {
    private ImageButton backArrow;
    // private CircleImageView imageUserProfile;
    private ShapeableImageView imageUserProfile;
    SharedPreferences sharedPreferences, sharedPreferences2;
    private DBHelper dbHelper;
    LoginResponse loginResponse;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    LinearLayout apiVersionLL, profileLL, subscriptionLL, feedbackLL, aboutLL, faqLL, privacyLL, foodPrefLL, allenergiesLL,
            termsLL, testingLL, languageLL, changePlanLL, changeLevelLL, deleteAccountLL;
    CardView logoutCV;
    private View view1, line6;
    TextView appVersionTV;
    private final static String SHARED_PREF_NAME2 = "log_user_info";
    String token;
    int prefId;
    String lang;
    private static SettingFragment settingFragment;
    private String checkActivity;

    View changeLL_View, changePlanView;

    BlurView blurView;
    LottieAnimationView loading_lav;

    MaterialTextView textViewProfileSettings, subscriptionTV, feedbackTV,
            languageTV, foodPreferenceTV, textViewAllergies, aboutTV, faqTV, termsAndConditionTV,
            testingPurposeOnlyTV, logoutTV, copyRightTV, mTextViewUserName, deleteAccountTV, mTextViewUserEmail, textViewChangePlan, textViewChangeLevel, privacyPolicyTV;
    private String userImageURL;
    private String currentUserId;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private ShimmerFrameLayout shimmerForAvatar;

    private boolean isProfileImageLoaded = false;


    public static SettingFragment getInstance() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        // returns the singleton object
        return settingFragment;
    }

    Activity activity;

    Resources resources;

    String TAG = "SETTING_TAG";

    UserStatusUtil userStatusUtil;

    SwitchCompat apiSwitch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = context instanceof Activity ? (Activity) context : null;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = true;
        try {
            //    if (isAdded() /*&& activity != null*/) {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SettingFragment_onResume",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        HomeActivity.showBottomNav();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(syncActivityBR,
                    new IntentFilter(getContext().getPackageName() + ".SYNC_ACTIVITY_SERVICE"));
        }
        if (userImageURL != null) {
            if (!userImageURL.matches("")) {
                if (!isProfileImageLoaded) {
                    GetImageFB();
                }
            } else {
                StoptShimmer();
            }
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(syncActivityBR);
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
        token = SessionUtil.getAccessToken(getContext());
        try {
            prefId = Integer.parseInt(SessionUtil.getFoodPreferenceID(getContext()));
        } catch (Exception exception) {
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
        return inflater.inflate(R.layout.fragment_new_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(getContext())) {
                LocationUtil.getLocationByGeoLocationAPI(getContext());
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Google plays service is not available in the device");
                }
            }
        }
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        lang = SessionUtil.getlangCode(getContext());
        view1 = view;
        dbHelper = new DBHelper(getContext());
        loginResponse = new LoginResponse();
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //initialize data
        init();

//listener for all textviewa

        /* // backArrow.setOnClickListener(this);*/
        logoutCV.setOnClickListener(this);
        termsLL.setOnClickListener(this);
        faqLL.setOnClickListener(this);
        aboutLL.setOnClickListener(this);
        privacyLL.setOnClickListener(this);
        subscriptionLL.setOnClickListener(this);
        feedbackLL.setOnClickListener(this);
        foodPrefLL.setOnClickListener(this);
        allenergiesLL.setOnClickListener(this);
        languageLL.setOnClickListener(this);
        changePlanLL.setOnClickListener(this);
        changeLevelLL.setOnClickListener(this);
        deleteAccountLL.setOnClickListener(this);


        imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    isProfileImageLoaded = false;
                    Fragment fragment = new UpdateProfileFragment();
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    //changes
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    if (isAdded()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        if (ConnectionDetector.isConnectedWithInternet(getContext()))
            userStatusUtil.getUserStatus("Bearer " + token);
    }

    public void activityDataSync(Context context, String checkActivity) {
        if (context != null) {
            if (dbHelper == null) {
                dbHelper = new DBHelper(context);
            }
            List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ByAPI_SyncedAt(SessionUtil.getUserID(context));
            if (list.size() > 0) {
                if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, context)) {
                    startSyncDataService("upload", "logout");
                }
            } else {
                //List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(getContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                //redirectToLogin(context, checkActivity);
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        LogoutUtil.performLogout(SessionUtil.getAccessToken(getContext()), SettingFragment.this);
                        //new UserDetailsUtil(WeekWiseNutritonFragment.context).updateUserDetails("Bearer " + SharedData.token, SharedData.location, WeekDaysHelper.getUTC_Time(), SettingFragment.this);
                        /*LogoutUtil.performLogout(SharedData.token, SettingFragment.this);*/
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    stopLoading();
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is null in activityDataSync method");
                    }
                }
            }
        } else {
            stopLoading();
            if (getContext() != null) {
                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "getContext is null in activityDataSync method");
            }
        }
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        Intent dataSyncServiceIntent = new Intent(getContext(), StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
        if (getContext() != null)
            getContext().startService(dataSyncServiceIntent);
        else stopLoading();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(dataSyncServiceIntent);
        } else {
            getContext().startService(dataSyncServiceIntent);
        }*/
    }


    private void init() {
        // backArrow = view.findViewById(R.id.backArrow);
        mTextViewUserName = view1.findViewById(R.id.userNameSetting);
        mTextViewUserEmail = view1.findViewById(R.id.userEmailSetting);
        foodPrefLL = view1.findViewById(R.id.foodPrefLL);
        allenergiesLL = view1.findViewById(R.id.allergiesLL);
        imageUserProfile = view1.findViewById(R.id.avatarSetting);
        //profileLL = view1.findViewById(R.id.profileLL);
        languageLL = view1.findViewById(R.id.LanguageLL);
        subscriptionLL = view1.findViewById(R.id.subscriptionLL);
        feedbackLL = view1.findViewById(R.id.feedbackLL);
        aboutLL = view1.findViewById(R.id.aboutLL);
        faqLL = view1.findViewById(R.id.faqLL);
        privacyLL = view1.findViewById(R.id.privacyLL);
        termsLL = view1.findViewById(R.id.termsLL);
        testingLL = view1.findViewById(R.id.testingLL);
        logoutCV = view1.findViewById(R.id.logoutCV);
        appVersionTV = view1.findViewById(R.id.appVersionTV);
        line6 = view1.findViewById(R.id.line6);
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        changePlanLL = view1.findViewById(R.id.changePlanLL);
        changeLevelLL = view1.findViewById(R.id.changeLevelLL);
        deleteAccountLL = view1.findViewById(R.id.deleteAccountLL);
        changeLL_View = view1.findViewById(R.id.changeLL_View);
        changePlanView = view1.findViewById(R.id.changePlanView);
        privacyPolicyTV = view1.findViewById(R.id.privacyPolicyTV);
        deleteAccountTV = view1.findViewById(R.id.deleteAccountTV);
        apiVersionLL = view1.findViewById(R.id.apiVersionLL);
        apiSwitch = view1.findViewById(R.id.apiSwitch);

        userStatusUtil = new UserStatusUtil(getContext(), SettingFragment.this, resources);

       /* if (SessionUtil.isStaging(getContext()).matches("production")
                || SessionUtil.isStaging(getContext()).matches("beta")
                || SessionUtil.isStaging(getContext()).matches("")) {
            changePlanLL.setVisibility(View.GONE);
            changePlanView.setVisibility(View.GONE);
            changeLevelLL.setVisibility(View.GONE);
            changeLL_View.setVisibility(View.GONE);
        }*/

        //TODO needs to be changed
        SharedData.is_dev_mode = SessionUtil.getIsDevStatus(getContext());
        // SharedData.is_dev_mode=true;
        System.out.println("DEV" + SharedData.is_dev_mode);
        checkDevStatus(SharedData.is_dev_mode);

        textViewProfileSettings = view1.findViewById(R.id.textViewProfileSettings);
        subscriptionTV = view1.findViewById(R.id.changeSubscriptionTV);
        feedbackTV = view1.findViewById(R.id.feedbackTV);
        languageTV = view1.findViewById(R.id.languageTV);
        foodPreferenceTV = view1.findViewById(R.id.foodPreferenceTV);
        textViewAllergies = view1.findViewById(R.id.allergiesTV);
        aboutTV = view1.findViewById(R.id.aboutTV);
        faqTV = view1.findViewById(R.id.faqTV);
        termsAndConditionTV = view1.findViewById(R.id.termsAndConditionTV);
        testingPurposeOnlyTV = view1.findViewById(R.id.testingPurposeOnlyTV);
        logoutTV = view1.findViewById(R.id.logoutTV);
        copyRightTV = view1.findViewById(R.id.copyRightTV);
        shimmerForAvatar = view1.findViewById(R.id.shimmerForSettingProfile);

        textViewChangePlan = view1.findViewById(R.id.textViewChangePlan);
        textViewChangeLevel = view1.findViewById(R.id.textViewChangeLevel);

        mTextViewUserName.setText(SharedData.username);
        mTextViewUserEmail.setText(SharedData.email);
        userImageURL = SessionUtil.getUserImgURL(getContext());

        currentUserId = SharedData.id;


        //SharedData.username = "Tahir Shezad";

        if (currentUserId == null) {
            currentUserId = SessionUtil.getUserID(getContext());
            SharedData.id = currentUserId;
        }
        if (userImageURL != null) {
            if (!userImageURL.matches("")) {
                GetImageFB();
            } else {
                StoptShimmer();
            }
        }
        setLanguageToWidgets();

        if (SessionUtil.getAPP_Environment(getContext()).matches("testing")) {
            apiVersionLL.setVisibility(View.VISIBLE);
            if(SessionUtil.isAPI_V3(getContext())){
                apiSwitch.setChecked(true);
            }else{
                apiSwitch.setChecked(false);
            }
            apiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        if(Common.isLoggingEnabled){
                            Log.d(TAG,"V3 True");
                        }
                        SessionUtil.setAPI_V3(getContext(), true);
                        dbHelper.clearDB();
                        moveToHome();
                    }else{
                        if(Common.isLoggingEnabled){
                            Log.d(TAG,"V2 True");
                        }
                        SessionUtil.setAPI_V3(getContext(), false);
                        dbHelper.clearDB();
                        moveToHome();
                    }

                    //apiVersionLL.setVisibility(View.GONE);
                }
            });
        } else {
            apiVersionLL.setVisibility(View.GONE);
        }
    }

    void moveToHome(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment home = new DashboardFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.navigation_container, home);
                transaction.commit();
            }
        },300);
    }

    private void StoptShimmer() {
        shimmerForAvatar.stopShimmerAnimation();
        shimmerForAvatar.setVisibility(View.GONE);
        imageUserProfile.setVisibility(View.VISIBLE);
    }

    private void GetImageFB() {
        try {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    File localFile = File.createTempFile("profile_images", "jpg");

                    storageRef.getReference().child("profile_images/" + currentUserId).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Firebase storage successfully retrieved");
                                }
                                isProfileImageLoaded = true;
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                /*imageUserProfile.setImageBitmap(bitmap);*/
                                Glide.with(getContext()).load(bitmap).into(imageUserProfile);
                                StoptShimmer();
                            } catch (Exception ex) {
                                if (Common.isLoggingEnabled)
                                    ex.printStackTrace();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                            if (getContext() != null) {
                                new LogsHandlersUtils(getContext()).getLogsDetails("SETTING_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                            }
                            StoptShimmer();
                        }
                    });
                }
            }
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SETTING_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            StoptShimmer();
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
                Log.e(TAG, "Dashboard fragment exception - Get Image from Firebase" + e.toString());
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SETTING_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            StoptShimmer();
        }
    }

    void setLanguageToWidgets() {
        // textViewProfileSettings.setText(resources.getString(R.string.profile_settings));
        subscriptionTV.setText(resources.getString(R.string.subscription));
        //textViewProfileSettings.setText(resources.getString(R.string.profile_settings));
        feedbackTV.setText(resources.getString(R.string.feedback));
        languageTV.setText(resources.getString(R.string.languages));
        foodPreferenceTV.setText(resources.getString(R.string.food_preferences));
        textViewAllergies.setText(resources.getString(R.string.allergies));
        aboutTV.setText(resources.getString(R.string.about));
        faqTV.setText(resources.getString(R.string.faq));
        termsAndConditionTV.setText(resources.getString(R.string.terms_amp_conditions));
        testingPurposeOnlyTV.setText(resources.getString(R.string.for_testing_purpose_only));
        logoutTV.setText(resources.getString(R.string.logout));
        copyRightTV.setText(resources.getString(R.string.copyright_by_mauritz_jarl));
        textViewChangePlan.setText(resources.getString(R.string.change_plan));
        textViewChangeLevel.setText(resources.getString(R.string.change_level));
        privacyPolicyTV.setText(resources.getString(R.string.privacy_policy));
        deleteAccountTV.setText(resources.getString(R.string.delete_account_text));
    }

    private void checkDevStatus(Boolean is_dev_mode) {
        if (is_dev_mode == false) {
            testingLL.setVisibility(View.GONE);
            line6.setVisibility(View.GONE);
        } else {
            testingLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), TestingPurposeActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getActivity(),"Button Clicked",Toast.LENGTH_SHORT).show();
                }
            });
        }

        setAppVersion();
    }

    private void setAppVersion() {
        String version = BuildConfig.VERSION_NAME;
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        if (version != null) {
            appVersionTV.setText("V " + version + " (" + versionCode + ")");
        }
    }

    @Override
    public void onClick(View v) {
        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            if (v.getId() == R.id.termsLL) {
                String url = TERMS_AND_POLICY_URL;
                String lang = SessionUtil.getlangCode(getContext());
                if (lang.matches("sv")) {
                    url = url.concat("sv");
                } else {
                    url = url.concat("en");
                }
                if (Common.isLoggingEnabled) {
                    Log.d("SETTING_FRAGMENT", "Terms and Condition URL: " + url);
                }
                Fragment fragment = new WebViewFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("Url", url); //key and value
                            bundle.putString("title", resources.getString(R.string.terms_amp_conditions)); //key and value
                            fragment.setArguments(bundle);
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }

                }
            } else if (v.getId() == R.id.aboutLL) {
                String url = ABOUT_US_URL;
                String lang = SessionUtil.getlangCode(getContext());
                if (lang.matches("sv")) {
                    url = url.concat("sv");
                } else {
                    url = url.concat("en");
                }
                if (Common.isLoggingEnabled) {
                    Log.d("SETTING_FRAGMENT", "About URL: " + url);
                }
                Fragment fragment = new WebViewFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("Url", url); //key and value
                            bundle.putString("title", resources.getString(R.string.about));
                            fragment.setArguments(bundle);
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }

                }
            } else if (v.getId() == R.id.foodPrefLL) {
                if (getContext() != null)
                    if (SessionUtil.isSubscriptionAvailable(getContext())) {
                        Intent intent = new Intent(getContext(), FoodPreferencesActivity.class);
                        intent.putExtra("From_setting", "setting");
                        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                    }
            } else if (v.getId() == R.id.allergiesLL) {
                if (getContext() != null)
                    if (SessionUtil.isSubscriptionAvailable(getContext())) {
                        Intent intent = new Intent(getContext(), AllergiesActivity.class);
                        intent.putExtra("From_setting", "setting");
                        intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
                        intent.putExtra(Common.SESSION_USER_FOOD_PREFERENCE_ID, prefId);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                    }
            } else if (v.getId() == R.id.privacyLL) {
                String url = PRIVACY_POLICY_URL;
                String lang = SessionUtil.getlangCode(getContext());
                if (lang.matches("sv")) {
                    url = url.concat("sv");
                } else {
                    url = url.concat("en");
                }
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Privacy Policy URL: " + url);
                }
                Fragment fragment = new WebViewFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("Url", url); //key and value
                            bundle.putString("title", resources.getString(R.string.privacy_policy));
                            fragment.setArguments(bundle);
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }

                }
            } else if (v.getId() == R.id.faqLL) {
                String url = FAQS_URL;
                String lang = SessionUtil.getlangCode(getContext());
                if (lang.matches("sv")) {
                    url = url.concat("sv");
                } else {
                    url = url.concat("en");
                }
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "FAQs URL: " + url);
                }
                Fragment fragment = new WebViewFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("Url", url); //key and value
                            bundle.putString("title", resources.getString(R.string.faq));
                            fragment.setArguments(bundle);
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }
                }
            } else if (v.getId() == R.id.feedbackLL) {
                Fragment fragment = new FeedbackFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
               /* Bundle bundle = new Bundle();
                bundle.putString("Url", url); //key and value
                fragment.setArguments(bundle);*/
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }

                }
            } else if (v.getId() == R.id.subscriptionLL) {
                // SharedData.id.signOut();
                if (isAdded()) {
                    if (getContext() != null) {
                        if (SessionUtil.getSignedUpPlatform(getContext()).matches("ios")) {
                            Fragment fragment = new WebViewFragment();
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("Url", IOS_CHANGE_SUBSCRIPTION_URL); //key and value
                            bundle.putString("title", resources.getString(R.string.subscription));
                            fragment.setArguments(bundle);
                            ft.replace(R.id.navigation_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            Fragment fragment = new NewSelectedSubscriptionFragment();
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.navigation_container, fragment);
                            //changes//
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }
                }
            } else if (v.getId() == R.id.changePlanLL) {
                // SharedData.id.signOut();
                if (isAdded()) {
                    if (getContext() != null) {
                        if (SessionUtil.isSubscriptionAvailable(getContext())) {
                            Fragment fragment = new ChangePlanFragment();
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager()
                                    .beginTransaction();
                            ft.replace(R.id.navigation_container, fragment);
                            //changes//
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                        }
                    }
                }
            } else if (v.getId() == R.id.changeLevelLL) {

                Fragment fragment = new ChangeLevelFragment();
                //replacing the fragment
                if (fragment != null) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            if (SessionUtil.isSubscriptionAvailable(getContext())) {
                                FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
               /* Bundle bundle = new Bundle();
                bundle.putString("Url", url); //key and value
                fragment.setArguments(bundle);*/
                                ft.replace(R.id.navigation_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                            }
                        }
                    }
                }

            } else if (v.getId() == R.id.LanguageLL) {
                // SharedData.id.signOut();
                if (isAdded()) {
                    if (getContext() != null) {
                        if (SessionUtil.isSubscriptionAvailable(getContext())) {
                            Intent intent = new Intent(getActivity(), LanguageActivity.class);
                            getActivity().startActivity(intent);
                        } else {
                            DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                        }
                       /* Fragment fragment = new LanguageFragment();
                        FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.navigation_container, fragment);
                        //changes//
                        ft.addToBackStack("LanguageFragment");
                        ft.commit();*/
                    }
                }
            } else if (v.getId() == R.id.deleteAccountLL) {
                if (getContext() != null)
                    showDeleteDialogBox();


            } else if (v.getId() == R.id.logoutCV) {
                // SharedData.id.signOut();
                if (isAdded()) {
                    if (getContext() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setTitle(resources.getString(R.string.log_out)).setMessage(resources.getString(R.string.are_you_sure))
                                // .setIcon(R.drawable.ic_baseline_delete_24)
                                .setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (isAdded() && getContext() != null) {
                                            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                                //if (SharedData.location != null && !SharedData.location.matches("")) {
                                                checkActivity = "dashboard";
                                                startLoading();
                                                blurrBackground();
                                                //if(!dbHelper.isUserStepCountAvailable(Integer.parseInt(SessionUtil.getUserID(getContext())),WeekDaysHelper.getDateTimeNow_yyyyMMdd())){
                                                activityDataSync(getContext(), checkActivity);
                                                // }else{
                                                //   new StepCounterDataSync(getContext(),"upload", (UserActivityInterface) SettingFragment.this);
                                                // }
                                               /* } else {
                                                    if(Common.isLoggingEnabled){
                                                        Log.e(TAG,"Location is null so not able to logout");
                                                    }
                                                    Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                                                }*/
                                            }
                                        }
                                    }
                                }).setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  holder.deleteIcon.setVisibility(View.INVISIBLE);

                                    }
                                });
                        builder.show();
                    }
                }


            }
        } else {
            if (isAdded()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private final BroadcastReceiver syncActivityBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String requestFor = "";
                if (intent.hasExtra("requestFor")) {
                    requestFor = intent.getStringExtra("requestFor");
                    if (requestFor.matches("logout")) {
                        LogoutUtil.performLogout(SessionUtil.getAccessToken(getContext()), SettingFragment.this);
                        //redirectToLogin(context, checkActivity);
                        //new UserDetailsUtil(WeekWiseNutritonFragment.context).updateUserDetails("Bearer " + SharedData.token, SharedData.location, WeekDaysHelper.getUTC_Time(), SettingFragment.this);

                    }else{
                        stopLoading();
                        if(Common.isLoggingEnabled)
                            Log.e(TAG,"request received other than logout");
                    }
                }else{
                    stopLoading();
                    if(Common.isLoggingEnabled)
                        Log.e(TAG,"SettingFragment::requestFor not available");
                }

            } catch (Exception ex) {
                stopLoading();
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SettingFragment_broadCaster",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }
            }
        }
    };

    public void redirectToLogin(Context context, String checkActivity) {
        try {
            if (checkActivity == "setting") {
                stopLoading();
            } else {
                dbHelper = new DBHelper(context);
            }

            if (StepCountServiceUtil.isMyServiceRunning(StepsService.class, context)) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Stop service");
                }
                StepCountServiceUtil.stopStepCountService(context);
            }

            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            sharedPreferences2 = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
            editor2.clear();
            editor2.apply();

            SessionUtil.setSensorStaticSteps(context, 0);
            SessionUtil.setSelectedDate(context, "");
            SessionUtil.setUserLogInSteps(context, 0);
            SessionUtil.setUsertodaySteps(context, 0);
            SessionUtil.setLoggedEmail(context, "");
            SessionUtil.setShowPermissionDialogAgain(context, true);
            SessionUtil.setLoggedIn(context, false);
            SessionUtil.setlangCode(context, "");
            /*if (getContext() != null && getResources() != null)
                Localization.setLanguageOnLogout(getContext(),getResources());*/


            SessionUtil.SetFoodPreferenceID(context, "");
            SessionUtil.setActivityUploadedDate(context, "");
            SessionUtil.setUnsubscribedPlanID(context, "");
            SessionUtil.setUnsubscribeStatus(context, false);
            dbHelper.logout();
            dbHelper.deleteUser(String.valueOf(currentUser));


            //dbHelper.deleteAllShoppingListData();
            if (checkActivity == "dashboard" || checkActivity == "setting") {
                Intent intent = new Intent(context, LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // if(getActivity()!=null)
                // getActivity().finish();
                context.startActivity(intent);
                super.onDestroyView();

           /* if (isAdded()) {
                if (context != null) {

                    Toast toast = Toast.makeText(context, R.string.successfully_logged_out, Toast.LENGTH_LONG);
                    // toast.getView().setBackgroundResource(R.color.yellow);
                    toast.show();
                }
            }*/

            }
        } catch (Exception exception) {
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLoading();

    }

    @Override
    public void isLogout(boolean isLoggedOut) {
        if (isLoggedOut) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "logout");
            }
            if (isAdded() && getContext() != null) {
                redirectToLogin(getContext(), "setting");
            }
        } else {
            stopLoading();

        }

    }

    @Override
    public void logoutResponse(String message) {
        stopLoading();
        if (getContext() != null) {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Context is null on logoutResponse callback method");
            }
        }
    }

    @Override
    public void logoutReponseCode(int responseCode) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Logout API responce code: " + responseCode);
        }
        if (Common.isLoggingEnabled && getContext() != null) {
            //Toast.makeText(getContext(), "Response Code on Logout is " + responseCode, Toast.LENGTH_SHORT).show();
        } else if (responseCode == 401) {
            if (getContext() != null) {
                LogoutUtil.redirectToLogin(getContext());
                Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void logoutError(Throwable t) {
        stopLoading();
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
        }
        if (getContext() != null) {
            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public void response(boolean isSuccessful, String message) {
        stopLoading();
        if (isSuccessful) {
            LogoutUtil.performLogout(SessionUtil.getAccessToken(getContext()), SettingFragment.this);
        } else {
            if (getContext() != null) {
                Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void responseCode(int responseCode) {
        if(Common.isLoggingEnabled){
            Log.e(TAG,"Error Code on logout on updateUserDetails is "+responseCode);
        }

    }

    @Override
    public void responseError(Throwable t) {
        stopLoading();
        if (getContext() != null) {
            Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }

    }*/


   /* @Override
    public void userActivitySync(StepCountModel.Data activty, String requestFrom) {
        if (requestFrom.matches("uploaded")) {
            redirectToLogin();
        }
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

    private void showDeleteDialogBox() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box_delete_account);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView btn_Cancel = dialog.findViewById(R.id.btn_left);
        MaterialTextView btn_delete = dialog.findViewById(R.id.btn_right);
        MaterialCheckBox checkBoxDelete = dialog.findViewById(R.id.checkBoxDelete);
        MaterialTextView deleteAcoountDescription = dialog.findViewById(R.id.mTextViewDeleteAccountDescription);
        deleteAcoountDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        btn_delete.setEnabled(false);

        checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_delete.setClickable(true);
                    btn_delete.setEnabled(true);
                    btn_delete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_delete_dialog_right_click));
                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (getContext() != null)
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    checkActivity = "setting";
                                    startLoading();
                                    blurrBackground();
                                    deleteUserAccount();
                                } else {
                                    Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            dialog.dismiss();
                        }


                    });

                } else {
                    btn_delete.setClickable(false);
                    btn_delete.setEnabled(false);
                    btn_delete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_delete_dialog_right));
                }
            }
        });

        /*if (checkBoxDelete.isChecked()) {
            btn_delete.setClickable(true);
            btn_delete.setEnabled(true);
            btn_delete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_delete_dialog_right_click));
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                    // btn_delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
               *//* if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }*//*
                    dialog.dismiss();
                }


            });
        }else{
            btn_delete.setClickable(false);
            btn_delete.setEnabled(false);
            btn_delete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_delete_dialog_right));
        }*/


        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                //  btn_Cancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));

                dialog.dismiss();
            }
            // dialog.dismiss();
        });


        dialog.show();
    }

    private void deleteUserAccount() {
        Call<LogoutModel> deleteAccountCall = ApiClient.getService().deleteAccount("Bearer " + token);
        deleteAccountCall.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                if (response.isSuccessful()) {
                    stopLoading();
                    if (getContext() != null)
                        LogoutUtil.redirectToLogin(getContext());

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getContext() != null)
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }
                stopLoading();
            }

            @Override
            public void onFailure(Call<LogoutModel> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
                stopLoading();
                if (getContext() != null)
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                //logoutInterface.isLogout(false);
                //logoutInterface.logoutError(t);
            }
        });
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {

    }
}

