package com.cedricapp.fragment;


import static android.content.Context.POWER_SERVICE;
import static com.cedricapp.common.Common.ANALYTICS_FOR;
import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.INFORMATION;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.CoachesAdapter;
import com.cedricapp.adapters.DemoRecyclerViewAdapter;
import com.cedricapp.adapters.NutritionViewPagerAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.common.UserActivity;
import com.cedricapp.interfaces.CalendarInterface;
import com.cedricapp.interfaces.PermissionDialogInterface;
import com.cedricapp.interfaces.PermissionRequestInterface;
import com.cedricapp.interfaces.UserActivityInterface;
import com.cedricapp.interfaces.UserDetailsListener;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.CoachesProfileDataModel;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.ProfileActivationResponse;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.model.VideoModel;
import com.cedricapp.model.VisualizationModel;
import com.cedricapp.model.VisualizationResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.AutoDismissalDialogUtil;
import com.cedricapp.utils.CalculatorUtil;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.HorizontalCalendarUtil;
import com.cedricapp.utils.ImageUtil;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.PermissionUtil;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.utils.UserDetailsUtil;
import com.cedricapp.utils.UserStatusUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.activity.NotificationCenterActivity;
import com.cedricapp.activity.StepAnalyticsActivity;
import com.cedricapp.broadcastreceiver.DateChangeReceiver;
import com.cedricapp.service.StepCounterDataSync;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.techisfun.onelinecalendar.DateSelectionListener;
import com.github.techisfun.onelinecalendar.OneLineCalendarPresenter;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


@SuppressWarnings("ALL")
public class DashboardFragment extends Fragment implements View.OnClickListener, UserStatusInterface, LocationListener, UserDetailsListener, UserActivityInterface, PermissionDialogInterface, PermissionRequestInterface, CalendarInterface {
    private static final int ALL_PERMISSIONS_RESULT = 101;
    public static View.OnClickListener myOnClickListener;
    private RecyclerView.LayoutManager layoutManager;
    OneLineCalendarView calendarView;

    // HorizontalCalendar horizontalCalendarView;
    Calendar calendar;
    boolean dateSelected = false;
    public static FrameLayout frameLayout;
    private ImageView /*mAvatar,*/ imageSleepVisualization;

    private ShapeableImageView mAvatar;
    private static RecyclerView recyclerView;
    private MaterialTextView textViewCoaches;
    Fragment fragment;
    SwipeRefreshLayout swipeRefreshLayout;

    int currentDateDifference;

    private static List<CoachesProfileDataModel.Data> dataList = new ArrayList<>();
    public static ArrayList<VideoModel> fileArrayList = new ArrayList<>();
    public static List<NutritionDataModel> nutritionList = new ArrayList<>();
    public static List<VisualizationModel.Data> visualizationList = new ArrayList<>();
    private static List<SignupResponse> userDataFromDb = new ArrayList<>();
    public static List<CoachesProfileDataModel> data;

    private static RecyclerView.Adapter adapter;
    MaterialCardView calmCardView;
    private CardView mSleepVisualizationCardView, mBreakFastCardview, mLunchCardView, mNightCardView;
    private LinearLayout mLinearLayoutDashboard, mLinearLayoutTextViewMAE, mLinearLayoutForMorning, mLinearLayoutForAfternoon, mLinearLayoutForNight;
    private MaterialTextView mTextViewBreakFast, mTextViewLunch, mTextViewDinner, mTextViewMorning, mTextViewAfternoon, mTextViewNight, selectedDateTw, mTextViewUsername, mTextViewWelcomingMessage, textViewCalm, textViewHealth,
            txt_visualization, txt_journeytostar;
    //MyAdapter myAdapter;
    private DBHelper dbHelper;
    VisualizationResponse visualizationResponse;
    ProfileActivationResponse profileActivationResponse;
    LinearLayout calenderDayLayout;
    TextView itemdayName, itemDayNumber, stepsTV, caloriesTV, distanceTV;
    SharedPreferences sharedPreferences;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    String audioUrl, dayTime, currentDate, nutritionDay, nutritionTime, name, userImageURL, email, foodPreferenceID, foodPreference;
    private ShimmerFrameLayout mFrameLayout, shimmerForAvatar, visualizationShimmerLayout, shimmerMorningImgLayout, shimmerAfternoonImgLayout, shimmerNightImgLayout, nutritionShimmerLayout;
    //private String selectedDay;
    private View view1;
    private String FirstName, FirstNameCap;
    private SharedPreferences sharedPreferences1;
    private String currentUserId;
    private SharedPreferences sharedPrefCurrent;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private String toDate;
    private int signupDay, signupYear, signupMonth;
    ArrayList<String> weekDaysList;
    private WeekDaysHelper weekDaysHelper;
    private String todaysDate;
    //private int weekNumber;     //not in use yet
    //private int dayNumber;      //not in use yet
    private int myDay;
    private int myWeek;
    CircleImageView imageMorning, imageAfternoon, imageNight;
    Call<SignupResponse> profileDataCall;
    Call<SignupResponse> tokenCall;
    Call<CoachesProfileDataModel> coachesCall;
    CoachesProfileDataModel coachProfileDataModel;
    Call<VisualizationModel> visualizationCall;
    VisualizationModel visualizationModel;
    CoachesProfileDataModel coachesProfileDataModel;
    Call<DashboardNutritionPagerModel> nutritionsCall;
    DashboardNutritionPagerModel dashboardNutrition;
    MaterialCardView breakfastCardView, LunchCardView, DinnerCardView;
    ImageView imageBreakfast, imageLunch, imageDinner;
    MaterialTextView textViewBreakfast, textViewLunch, textViewDinner, textViewNutrition, mTextViewSelectedDate;
    LinearLayout nutritionLL;
    private List<CoachesProfileDataModel.Data> coachesProfilesList;
    int statusCode;
    private HorizontalCalendar horizontalCalendar;
    //private List<DashboardNutrition.Data.Recipe> dashboardRecipesList = new ArrayList<>();
    private NutritionViewPagerAdapter viewPagerAdapter;
    private ViewPager2 pager2;
    DemoRecyclerViewAdapter pagerAdapter2;
    ScrollingPagerIndicator pagerIndicator2;
    private TabLayout tabLayout;
    private int count;
    DashboardNutritionPagerModel.Data.Recipes dashboardRecipeList;
    private List<DashboardNutritionPagerModel.Page1> pager1List = new ArrayList<>();
    private List<DashboardNutritionPagerModel.Page2> pager2List = new ArrayList<>();
    private List<DashboardNutritionPagerModel.Page3> pager3List = new ArrayList<>();
    /*boolean isBroadCastRecieverRunning;*/
    private WorkManager mWorkManager;
    private ImageButton minus_btn_cv, plus_btn_cv;
    private TextView waterCountTV, drinkCountTV;
    boolean isUserActivitySynced;
    Dialog dialog;
    TextView dialog_title;
    TextView dialog_description;
    MaterialTextView btn_Cancel;
    MaterialTextView btn_Continue;
    MaterialTextView textViewHello;
    TextView dialog_Timer, stepsLabelTV, calLabelTV, distanceLabelTV, waterLabelTV, perCupLblTV;
    protected LocationManager locationManager;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private String gpsLocation;
    private String previousLocation;
    private int backPressCount = 0;
    private String message;
    ProgressBar waterCountProgress;

    String TAG = "DASHBOARD_TAG";
    //private MaterialCardView mStepCountCardView;
    private MaterialCardView allowPermissionCardView;
    private MaterialTextView mTextViewAllow, mTextViewPermissionNotAallowed;

    MaterialCardView stepCountCardView, caloriesCardView, distanceCardView, waterCardView;

    UserStatusUtil userStatusUtil;

    Resources resources;

    ConstraintLayout bellRL;

    public static TextView notificationTV;

    Context mContext;

    List<StepCountModel.Data> userActivityListFromDB;

    String levelID, goalID;

    String token;

    String tokenType;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_CREATE");
        }
        OneLineCalendarPresenter.appLang = SessionUtil.getlangCode(mContext);
        setdatesForCalendar();
    }

    void setdatesForCalendar() {
        String date1 = SessionUtil.getSubscriptionStartDate(mContext);
        String date2 = SessionUtil.getSubscriptionEndDate(mContext);

        Date todayDate = Calendar.getInstance().getTime();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Subscription Start Date: " + date1 + "\nSubscription End Date: " + date2 + "\nToday date: " + todayDate);
        }
        int diffDays = 0;
        if (!date2.matches("")) {
            Date startDate = new Date();
            try {
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                if (!date1.matches("")) {
                    //subscription start date
                    startDate = dateFormater.parse(date1);
                }
                //subscription end date
                Date endDate = dateFormater.parse(date2);

                //today's date
                String todaydateString = dateFormater.format(todayDate);
                todayDate = dateFormater.parse(todaydateString);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Formatted Subscription End Date: " + endDate + "\nFormatted Today date: " + todayDate);
                }
                if (!todayDate.after(endDate)) {
                    long diff = endDate.getTime() - todayDate.getTime();
                    diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                }

                OneLineCalendarPresenter.subscriptionEndDate = endDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (diffDays > 0) {
            OneLineCalendarPresenter.MAX_DAYS = 365;
        } else {
            OneLineCalendarPresenter.MAX_DAYS = 1;
        }

        this.weekDaysHelper = new WeekDaysHelper();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_CREATE_VIEW");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_VIEW_CREATED");
        }
        view1 = view;


        getUserdetailsFromSharedPreferences();
        setDailYDateInSharedPreference();

        //intialize
        init();
        startAlarmForDateChange();
        initDialog();


    }

    @Override
    public void onStart() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_START");
        }
        super.onStart();
        startBroadCastingForSensor();
        onStartStuff();

        /*if (!isForegroundServiceRunning()) {
            startStepCounterService();
        }*/
    }

    void onStartStuff() {
        showStepsCounterView();
        /*checkNotificationPermission();
        checkStepCounterPermission();*/
        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(mContext))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (mContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    activityDataSync();
                } else {
                    PermissionUtil.checkNotificationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                    if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                        userActivityListFromDB = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                        if (userActivityListFromDB.size() == 0) {
                            if (!SessionUtil.getActivityDownloadedDate(mContext).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                new UserActivity(mContext, this).downloadUserActivity(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                            }
                        }
                    }
                }
            } else {
                activityDataSync();
            }

        if (SessionUtil.showPermissionDialogAgain(mContext) == false) {
            allowPermissionCardView.setStrokeColor(ContextCompat.getColor(mContext, R.color.red));
            allowPermissionCardView.setStrokeWidth(2);
            allowPermissionCardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_RESUME");
        }

        super.onResume();
        onResumeStuff();
    }

    void onResumeStuff() {
        if (mFrameLayout != null) {
            mFrameLayout.startShimmerAnimation();
        }
        if (!SessionUtil.getSelectedDate(mContext).matches("")) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "SElECTED_DATE Date in onResume Dashboard: " + SessionUtil.getSelectedDate(mContext));
            }
            OneLineCalendarView.count = 0;
            OneLineCalendarView.selectedDate = SessionUtil.getSelectedDate(mContext);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "SElECTED_DATE Date in onResume Dashboard After assign: " + SessionUtil.getSelectedDate(mContext));
            }
        }
        SharedData.canToastShow = true;
        SharedData.redirectToDashboard = false;
        SharedData.isDashboardVisible = true;

        /*blurrBackground();
        StartLoading();*/

        if (dialog != null) {
            if (isAdded()) {
                if (dialog.isShowing()) dialog.dismiss();
            }
        }
        dateSelected = true;
        try {
            //    if (isAdded() /*&& activity != null*/) {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // }
        } catch (ActivityNotFoundException e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        /*if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(mContext)) {
            //startBroadCastingForSensor();

            //below code is implemented due to mismatch of step counts in fragment UI and notification
            if (StepCountServiceUtil.isMyServiceRunning(StepCountingService.class, mContext)) {
                if(userActivityListFromDB == null){
                    userActivityListFromDB = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                }

                if (userActivityListFromDB.size() > 0) {
                    Intent intent = new Intent(mContext, StepCountingService.class);
                    intent.putExtra("STEP_COUNT_FROM_DB_ON_RESUME", userActivityListFromDB.get(0).getStepsCount());
                    mContext.startService(intent);
                }
            }
        }*/
    }


    @Override
    public void onPause() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_PAUSE");
        }

        super.onPause();
        onPauseStuff();
    }

    void onPauseStuff() {
        //if (isBatterySavorPermissionActive())

        if (mFrameLayout != null) {
            mFrameLayout.stopShimmerAnimation();
        }


    }

    @Override
    public void onStop() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_STOP");
        }
        onStopStuff();
        // if (isBroadCastRecieverRunning)
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(syncActivityBR);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiverSteps);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateChangeReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(m_timeChangedReceiver);
        super.onStop();

    }

    void onStopStuff() {
        if (mContext != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (mContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    activityDataSync();
                } else {
                    PermissionUtil.checkNotificationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                }
            } else {
                activityDataSync();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Get Context is null in onStop()");
            }
        }
        SharedData.isDashboardVisible = false;
    }

    @Override
    public void onDestroy() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "DASHBOARD_ON_DESTROY");
        }
        onDestroyStuff();
        super.onDestroy();
    }

    void onDestroyStuff() {
        /*if (mContext != null) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(mContext, ServiceRestarter.class);
            mContext.sendBroadcast(broadcastIntent);
        }*/
    }

    private void init() {
        // resources = Localization.setLanguage(mContext, getResources());
        tokenType = "Bearer ";
        resources = getResources();
        this.weekDaysHelper = new WeekDaysHelper();

        dbHelper = new DBHelper(mContext);
        currentUserId = SharedData.id;


        if (currentUserId == null) {
            currentUserId = SessionUtil.getUserID(mContext);
            SharedData.id = currentUserId;
        }

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "User ID is " + currentUserId);
        }


        //calendarInit();
        // Setup ViewPager2 with indicator
        pager2 = view1.findViewById(R.id.pager_view);
        pagerIndicator2 = view1.findViewById(R.id.pager_indicator2);
        dashboardNutrition = new DashboardNutritionPagerModel();
        SessionUtil.setLoggedIn(mContext, true);
        // DemoRecyclerViewAdapter pagerAdapter2 = new DemoRecyclerViewAdapter(3, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, dashboardRecipeList);

        textViewCoaches = view1.findViewById(R.id.textViewCoaches);
        mTextViewAllow = view1.findViewById(R.id.mTextViewAllow);
        mTextViewPermissionNotAallowed = view1.findViewById(R.id.mTextViewPermissionNotAallowed);

        //Avatar id
        mAvatar = view1.findViewById(R.id.Avatar);
        // mStepCountCardView = view1.findViewById(R.id.stepCounterCardView);
        allowPermissionCardView = view1.findViewById(R.id.allowPermissionCardView);
        //Linear id's
        mLinearLayoutDashboard = view1.findViewById(R.id.linearlayoutMain);
        mLinearLayoutTextViewMAE = view1.findViewById(R.id.linearLayoutForMAEView);
        mLinearLayoutForMorning = view1.findViewById(R.id.linearLayoutForMorning);
        mLinearLayoutForAfternoon = view1.findViewById(R.id.linearLayoutForAfternoon);
        mLinearLayoutForNight = view1.findViewById(R.id.linearLayoutForNight);
        calenderDayLayout = view1.findViewById(R.id.dayLayout);
        shimmerForAvatar = view1.findViewById(R.id.shimmerForDashboardProfile);
        visualizationShimmerLayout = view1.findViewById(R.id.visualizationShimmerLayout);
        shimmerMorningImgLayout = view1.findViewById(R.id.shimmerMorningImgLayout);
        shimmerAfternoonImgLayout = view1.findViewById(R.id.shimmerAfternoonImgLayout);
        shimmerNightImgLayout = view1.findViewById(R.id.shimmerNightImgLayout);
        imageMorning = view1.findViewById(R.id.imageMorning);
        imageAfternoon = view1.findViewById(R.id.imageAfternoon);
        imageNight = view1.findViewById(R.id.imageNight);
        mTextViewSelectedDate = view1.findViewById(R.id.selectedDate);

        stepsTV = view1.findViewById(R.id.stepsTV);
        caloriesTV = view1.findViewById(R.id.caloriesTV);
        distanceTV = view1.findViewById(R.id.distanceTV);

        //CardView's Id
        mSleepVisualizationCardView = view1.findViewById(R.id.sleepVisualCardView);
        mBreakFastCardview = view1.findViewById(R.id.breakfastCardView);
        mLunchCardView = view1.findViewById(R.id.LunchCardView);
        mNightCardView = view1.findViewById(R.id.DinnerCardView);

        mFrameLayout = view1.findViewById(R.id.shimmerLayout);

        //TextView's Id's
        itemdayName = view1.findViewById(R.id.item_day_name);
        itemDayNumber = view1.findViewById(R.id.item_day_number);
        mTextViewWelcomingMessage = view1.findViewById(R.id.textViewWelcoming);
        mTextViewMorning = view1.findViewById(R.id.textViewMorning);
        mTextViewAfternoon = view1.findViewById(R.id.textViewAfternoon);
        mTextViewNight = view1.findViewById(R.id.textViewNight);
        mTextViewBreakFast = view1.findViewById(R.id.textViewBreakfast);
        txt_visualization = view1.findViewById(R.id.textView1SleepViewCard);
        txt_journeytostar = view1.findViewById(R.id.textView2SleepViewCard);
        mTextViewLunch = view1.findViewById(R.id.textViewLunch);
        mTextViewDinner = view1.findViewById(R.id.textViewDinner);
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefresh);
        calmCardView = view1.findViewById(R.id.calmCardView);
        textViewCalm = view1.findViewById(R.id.textViewCalm);
        imageSleepVisualization = view1.findViewById(R.id.imageSleepVisualization);

        //--------------nutritions widgets
        //nutritions cardviews
        breakfastCardView = view1.findViewById(R.id.breakfastCardView);
        LunchCardView = view1.findViewById(R.id.LunchCardView);
        DinnerCardView = view1.findViewById(R.id.DinnerCardView);
        //nutritions imageViews
        imageBreakfast = view1.findViewById(R.id.imageBreakfast);
        imageLunch = view1.findViewById(R.id.imageLunch);
        imageDinner = view1.findViewById(R.id.imageDinner);

        textViewHello = view1.findViewById(R.id.textViewHello);

        //nutritions text views
        textViewBreakfast = view1.findViewById(R.id.textViewBreakfast);
        textViewLunch = view1.findViewById(R.id.textViewLunch);
        textViewDinner = view1.findViewById(R.id.textViewDinner);

        //nutritions shimmer
        nutritionShimmerLayout = view1.findViewById(R.id.nutritionShimmer);
        pager2.setVisibility(View.GONE);
        nutritionShimmerLayout.setVisibility(View.VISIBLE);
        nutritionShimmerLayout.startShimmerAnimation();

        //nutrtition Linear Layout
        /*nutritionLL = view1.findViewById(R.id.nutritionLL);
        nutritionLL.setVisibility(View.GONE);*/
        textViewNutrition = view1.findViewById(R.id.textViewNutrition);
        plus_btn_cv = view1.findViewById(R.id.plus_btn_cv);
        minus_btn_cv = view1.findViewById(R.id.minus_btn_cv);
        waterCountTV = view1.findViewById(R.id.waterCountTV);
        drinkCountTV = view1.findViewById(R.id.drinkCountTV);
        stepsLabelTV = view1.findViewById(R.id.stepsLabelTV);
        calLabelTV = view1.findViewById(R.id.calLabelTV);
        distanceLabelTV = view1.findViewById(R.id.distanceLabelTV);
        waterLabelTV = view1.findViewById(R.id.waterLabelTV);
        perCupLblTV = view1.findViewById(R.id.perCupLblTV);

        waterCountProgress = view1.findViewById(R.id.waterCountProgress);

        stepCountCardView = view1.findViewById(R.id.stepCountCardView);
        caloriesCardView = view1.findViewById(R.id.caloriesCardView);
        distanceCardView = view1.findViewById(R.id.distanceCardView);
        textViewHealth = view1.findViewById(R.id.textViewHealth);
        waterCardView = view1.findViewById(R.id.waterCardView);

        mLinearLayoutForMorning.setOnClickListener(this);
        mLinearLayoutForAfternoon.setOnClickListener(this);
        mLinearLayoutForNight.setOnClickListener(this);

        //   recyclerview for first horizontal cardView coaches
        recyclerView = view1.findViewById(R.id.recyclerviewForCoaches);
        mFrameLayout.startShimmerAnimation();
        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        dataList.clear();

        /*bottomNavigationView = view1.findViewById(R.id.navigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_plans_Home);*/

        if (HomeActivity.navigation != null) {
            HomeActivity.navigation.setSelectedItemId(R.id.navigation_plans_Home);
        }


        bellRL = view1.findViewById(R.id.bellRL);

        notificationTV = view1.findViewById(R.id.notificationTV);
        notificationTV.setText("" + dbHelper.getNotifications(SessionUtil.getUserID(mContext)).size());

        if (getArguments() != null && getArguments().getBoolean("show_success_dialog")) {
            if (getArguments().getBoolean("subscription_changed")) {
                AutoDismissalDialogUtil.showDialog(mContext, resources.getString(R.string.subscription_upgraded));
            } else {
                AutoDismissalDialogUtil.showDialog(mContext, resources.getString(R.string.update_completed_explore_dashboard));
            }
        }

        String date1 = SessionUtil.getSubscriptionStartDate(mContext);
        String date2 = SessionUtil.getSubscriptionEndDate(mContext);
        new HorizontalCalendarUtil(mContext, date1, date2, view1, DashboardFragment.this);

        //blurrBackground();
        //StartLoading();
        //getting image from firebase storage
        if (userImageURL != null) {
            if (!userImageURL.matches("")) {
                getImageFromFirebase();
            } else {
                StoptShimmer();
            }
        } else {
            StoptShimmer();
        }

        if ((levelID.matches("0") && goalID.matches("0"))
                || SessionUtil.getUserHeight(mContext).matches("0")) {
            getUserProfileData();
        } else {
            checkNetworkAndGetCoachesData();
        }


        //Avatar listener
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new UpdateProfileFragment();
                loadFragment(fragment);
            }
        });


       /* int day;
        int week;
        day=1;
        week=1;*/
        //  getNutrition(day, week);

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Unit Type from shared data variable: " + SharedData.unitType);
            Log.d(TAG, "First Name: " + FirstName);
        }


        // mTextViewUsername.setText(userDataFromDb.get());
        SharedData.setWelcomeMessage(mTextViewWelcomingMessage, resources);


        setLanguageToWidgets();


        mTextViewAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "check Permision on allow button click");
                }
                SessionUtil.setShowPermissionDialogAgain(mContext, true);
                PermissionUtil.checkLocationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                allowPermissionCardView.setVisibility(View.GONE);
                allowPermissionCardView.setStrokeWidth(0);

            }
        });

        bellRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NotificationCenterActivity.class);
                startActivity(intent);
            }
        });

        //StartShimmer();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mContext != null) {
                    if (currentUserId != null) {
                        if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                            dbHelper.clearCoaches();
                            dbHelper.clearVisualizations();
                            dbHelper.clearDashboardNutrition();
                            checkNetworkAndGetNewDataFromServer(SharedData.level_id, SharedData.goal_id);
                            if (userImageURL != null) {
                                if (!userImageURL.matches("")) {
                                    getImageFromFirebase();
                                } else {
                                    StoptShimmer();
                                }
                            } else {
                                StoptShimmer();
                            }
                        } else {
                            if (swipeRefreshLayout != null) {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                            showToast(resources.getString(R.string.no_internet_connection));
                        }
                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }

                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
                activityDataSync();

            }
        });

        plus_btn_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        int count = Integer.parseInt(waterCountTV.getText().toString());
                        if (count < 16) {
                            count++;
                            waterCountTV.setText("" + count);
                            drinkCountTV.setText("" + count);
                            waterCountProgress.setProgress(count);
                            SessionUtil.setWaterIntake(mContext, count);
                            if (dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                dbHelper.updateWaterIntake(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd(), "" + count);
                            } else {
                                dbHelper.addWaterIntake(SessionUtil.getUserID(mContext), "" + count, WeekDaysHelper.getTimeZoneID(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                            }
                        }
                    } else {

                        DialogUtil.showSubscriptionEndDialogBox(mContext, resources);
                    }
            }
        });

        minus_btn_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        int count = Integer.parseInt(waterCountTV.getText().toString());
                        if (count > 0) {
                            count--;
                            waterCountTV.setText("" + count);
                            drinkCountTV.setText("" + count);
                            waterCountProgress.setProgress(count);
                            SessionUtil.setWaterIntake(mContext, count);
                            //dbHelper.updateWaterIntake(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd(), "" + count);
                            if (dbHelper.isUserStepCountAvailable(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                dbHelper.updateWaterIntake(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd(), "" + count);
                            } else {
                                dbHelper.addWaterIntake(SessionUtil.getUserID(mContext), "" + count, WeekDaysHelper.getTimeZoneID(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                            }
                        }
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, resources);
                    }
            }
        });


        //showToast(""+ConnectionDetector.getLocalIpAddress());

        stepCountCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        Intent intent = new Intent(mContext, StepAnalyticsActivity.class);
                        intent.putExtra(ANALYTICS_FOR, "steps");
                        startActivity(intent);
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, resources);
                    }
            }
        });
        caloriesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        Intent intent = new Intent(mContext, StepAnalyticsActivity.class);
                        intent.putExtra(ANALYTICS_FOR, "calories");
                        startActivity(intent);
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, resources);
                    }
            }
        });
        distanceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        Intent intent = new Intent(mContext, StepAnalyticsActivity.class);
                        intent.putExtra(ANALYTICS_FOR, "distance");
                        startActivity(intent);
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, resources);
                    }
            }
        });

        if (mContext != null)
            if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                userStatusUtil = new UserStatusUtil(mContext, DashboardFragment.this, resources);
                userStatusUtil.getUserStatus(tokenType + token);
            }


    }

    void calendarInit() {
        //getCurrent date
        if (SessionUtil.getSelectedDate(mContext).matches("")) {
            /*toDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());*/
            toDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
            SessionUtil.setSelectedDate(mContext, toDate);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Today date in dashboard: " + toDate);
                String fcmToken = SessionUtil.getFcmToken(mContext);
                String deviceToken = SessionUtil.getDeviceId(mContext);
                Log.d(TAG, "Device ID: " + deviceToken + "\nFCM Token: " + fcmToken);
            }
        } else {
            toDate = SessionUtil.getSelectedDate(mContext);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Today date in dashboard selection from selected date preference: " + toDate);
                String fcmToken = SessionUtil.getFcmToken(mContext);
                String deviceToken = SessionUtil.getDeviceId(mContext);
                Log.d(TAG, "Device ID: " + deviceToken + "\nFCM Token: " + fcmToken);
            }
        }

        calendarView = (OneLineCalendarView) view1.findViewById(R.id.calendar_view);

        if (!SessionUtil.getSelectedDate(mContext).matches("")) {
            OneLineCalendarView.selectedDate = SessionUtil.getSelectedDate(mContext);
        } else {
            OneLineCalendarView.count = 0;
            OneLineCalendarView.selectedDate = toDate;
        }


        calendarView.setOnDateClickListener(new DateSelectionListener() {
            @Override
            public boolean onDateSelected(@NonNull Date date) {
                // selectedDateTw.setText(SimpleDateFormat.getDateInstance().format(date));
                //if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    toDate = sdf.format(date);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Calendar selected date is " + toDate);
                    }
                    SessionUtil.setSelectedDate(mContext, toDate);
                    OneLineCalendarView.selectedDate = toDate;

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "toDate: " + toDate);
                    }

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "--BEFORE--");
                        Log.d(TAG, "--Day and week on Selceted Calender View--");
                        Log.d(TAG, "Selected date in calendar View is " + toDate);
                        Log.d(TAG, "calendarView, My day number is " + myDay);
                        Log.d(TAG, "calendarView, My week number is " + myWeek);
                        Log.d(TAG, "-------------------------------------------");
                    }
                    myDay = weekDaysHelper.getMyDay(mContext, toDate);
                    myWeek = weekDaysHelper.getMyWeek(mContext, toDate);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "--AFTER--");
                        Log.d(TAG, "--Day and week on Selceted Calender View--");
                        Log.d(TAG, "Selected date in calendar View is " + toDate);
                        Log.d(TAG, "calendarView, My day number is " + myDay);
                        Log.d(TAG, "calendarView, My week number is " + myWeek);
                        Log.d(TAG, "-------------------------------------------");
                    }
                    if (levelID != null && goalID != null) {
                        if (!levelID.matches("") && !goalID.matches("")) {
                            //getCoachesData(Integer.parseInt(SharedData.level_id), Integer.parseInt(SharedData.goal_id), myDay, myWeek);
                            checkNetworkAndGetCoachesData();
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Level ID or Goal ID is empty");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Level ID or Goal ID is null");
                        }
                    }

                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    if (mContext != null) {
                        new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_DateSlectionListener", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));

                    }
                }

               /* } else {
                    if (mContext != null) {
                        showToast(resources.getString(R.string.no_internet_connection));
                    }
                }*/
                return true;
            }

            @Override
            public boolean onDateUnselected() {
                // selectedDateTw.setText(R.string.no_selection);
                return false;
            }
        });

        String date1 = SessionUtil.getSubscriptionStartDate(mContext);
        String date2 = SessionUtil.getSubscriptionEndDate(mContext);
        Calendar startSubscriptionDate = Calendar.getInstance();
        Calendar endSubscriptionDate = Calendar.getInstance();
        String datePatternRegex = "([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})";
        SimpleDateFormat sdf, sdf2;
        String subscriptionStartDate = SessionUtil.getSubscriptionStartDate(mContext);
        String subscriptionEndDate = SessionUtil.getSubscriptionEndDate(mContext);
        if (subscriptionStartDate.matches(datePatternRegex)) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        }
        if (subscriptionEndDate.matches(datePatternRegex)) {
            sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        } else {
            sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        }
        try {
            startSubscriptionDate.setTime(sdf.parse(subscriptionStartDate));
            endSubscriptionDate.setTime(sdf2.parse(subscriptionEndDate));
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        /*horizontalCalendarView = new HorizontalCalendar.Builder(getActivity(), R.id.calendarView)
                .range(startSubscriptionDate, endSubscriptionDate)
                .datesNumberOnScreen(7)
                .configure()
                .formatBottomText("MMM")
                .formatBottomText("dd")
                //.selectedDateBackground(getResources().getDrawable(R.drawable.calender_background_color))
                .end()
                .defaultSelectedDate(Calendar.getInstance())
                .build();

        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

            }
        });*/

    }

    void setLanguageToWidgets() {
        textViewHello.setText(resources.getString(R.string.hi));
        txt_visualization.setText(resources.getString(R.string.sleep_visualization));
        txt_journeytostar.setText(resources.getString(R.string.journey_to_star));
        textViewCoaches.setText(resources.getString(R.string.coaches));
        textViewCalm.setText(resources.getString(R.string.calm));
        stepsLabelTV.setText(resources.getString(R.string.steps));
        calLabelTV.setText(resources.getString(R.string.calories));
        distanceLabelTV.setText(resources.getString(R.string.distance));
        waterLabelTV.setText(resources.getString(R.string.water));
        perCupLblTV.setText(resources.getString(R.string.per_cup));
        //textViewBreakfast.setText(resources.getString(R.string.breakfast));
        //textViewLunch.setText(resources.getString(R.string.lunch_time));
        mTextViewAllow.setText(resources.getString(R.string.allow));
        mTextViewPermissionNotAallowed.setText(resources.getString(R.string.permission_not_allowed));
        // textViewDinner.setText(resources.getString(R.string.dinner_time));
        textViewNutrition.setText(resources.getString(R.string.nutrition));
        textViewHealth.setText(resources.getString(R.string.health));
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        Intent dataSyncServiceIntent = new Intent(mContext, StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Start Data Sync Service. Request for: " + requestFor + " and Request from: " + requestFrom);
        }
        mContext.startService(dataSyncServiceIntent);
    }


    private void setDailYDateInSharedPreference() {
        if (SessionUtil.getDailyDate(mContext).matches("")) {
            SessionUtil.setDailyCoachLoad(true, mContext);
            SessionUtil.setDailyDate(mContext, weekDaysHelper.getDateTimeNow_yyyyMMdd());
        } else if (weekDaysHelper.getCountOfDays(SessionUtil.getDailyDate(mContext), weekDaysHelper.getDateTimeNow_yyyyMMdd()) > 1) {
            SessionUtil.setDailyCoachLoad(true, mContext);
            SessionUtil.setDailyDate(mContext, weekDaysHelper.getDateTimeNow_yyyyMMdd());
        } else {
            SessionUtil.setDailyCoachLoad(false, mContext);
            SessionUtil.setDailyDate(mContext, weekDaysHelper.getDateTimeNow_yyyyMMdd());
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }


    void getUserdetailsFromSharedPreferences() {
        if (mContext != null) {
            SharedData.email = SessionUtil.getUserEmailFromSession(mContext);
            SharedData.username = SessionUtil.getUsernameFromSession(mContext);
            SharedData.id = SessionUtil.getUserID(mContext);
            SharedData.token = SessionUtil.getAccessToken(mContext);
            token = SharedData.token;
            //SharedData.refresh_token = SessionUtil.getRefreshToken(mContext);
            SharedData.subscription_id = SessionUtil.getSubscriptionID_FromSession(mContext);
            SharedData.start_date = SessionUtil.getSubscriptionStartDate(mContext);
            SharedData.end_date = SessionUtil.getSubscriptionEndDate(mContext);
            SharedData.trail_ends = SessionUtil.getTrailEnds(mContext);
            //getting profile data from shared preferences

            SharedData.weight = SessionUtil.getUserWeight(mContext);
            SharedData.height = SessionUtil.getUserHeight(mContext);
            SharedData.age = SessionUtil.getUserAge(mContext);
            SharedData.gender = SessionUtil.getUserGender(mContext);
            SharedData.goal = SessionUtil.getUserGoal(mContext);
            SharedData.level = SessionUtil.getUserLevel(mContext);
            SharedData.unitType = SessionUtil.getUserUnitType(mContext);
            SharedData.level_id = SessionUtil.getUserLevelID(mContext);
            levelID = SharedData.level_id;
            SharedData.goal_id = SessionUtil.getUserGoalID(mContext);
            goalID = SharedData.goal_id;
            userImageURL = SessionUtil.getUserImgURL(mContext);
            foodPreferenceID = SessionUtil.getFoodPreferenceID(mContext);
            // setTrialAndSubscriptionDates(SharedData.trail_ends);
            showUserName();
            setDayAndWeek();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Dashboard: Retrieved data from shared preferences------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + SharedData.id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + SharedData.email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + SharedData.username);
                Log.d(TAG, Common.SUBSCRIPTION_END_DATE + ": " + SharedData.end_date);
                Log.d(TAG, Common.SUBSCRIPTION_START_DATE + ": " + SharedData.start_date);
                Log.d(TAG, Common.TRAIL_ENDS + ": " + SharedData.trail_ends);
                Log.d(TAG, Common.SUBSCRIPTION_ID + ": " + SharedData.subscription_id);
                Log.d(TAG, Common.SESSION_USER_GOAL_ID + ": " + SharedData.goal_id);
                Log.d(TAG, Common.SESSION_USER_LEVEL_ID + ": " + SharedData.level_id);
                Log.d(TAG, Common.SESSION_USER_FOOD_PREFERENCE_ID + ": " + foodPreferenceID);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + SharedData.token);
                Log.d(TAG, "energy id" + ": " + SessionUtil.getAllergiesId(mContext));
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + SharedData.refresh_token);
                Log.d(TAG, "---------------------------------------------------------------------------------");
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragement is not attached with activity while getting shared preferences");
            }
        }
    }

    void showUserName() {
        mTextViewUsername = view1.findViewById(R.id.textViewUsername);
        FirstName = getFirstWord(SharedData.username);
        if (FirstName != null && FirstName.trim() != "") {
            if (FirstName.length() > 0) {
                FirstNameCap = FirstName.trim().substring(0, 1).toUpperCase() + FirstName.trim().substring(1);
            } else {
                FirstNameCap = "";
            }
        } else FirstNameCap = "";
        mTextViewUsername.setText(FirstNameCap);

    }

    void setDayAndWeek() {
        if (SessionUtil.getSelectedDate(mContext).matches("")) {
            myDay = weekDaysHelper.getMyDay(mContext, weekDaysHelper.getCurrentDateLikeServer());
            myWeek = weekDaysHelper.getMyWeek(mContext, weekDaysHelper.getCurrentDateLikeServer());
        } else {
            myDay = weekDaysHelper.getMyDay(mContext, SessionUtil.getSelectedDate(mContext));
            myWeek = weekDaysHelper.getMyWeek(mContext, SessionUtil.getSelectedDate(mContext));
        }
    }


    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    private void getUserProfileData() {

        profileDataCall = ApiClient.getService().getProfileData(tokenType + token, currentUserId);

        profileDataCall.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                try {
                    if (Common.isLoggingEnabled) {
                        if (response.body() != null) {
                            Log.d(TAG, "Get User Profile response data: " + response.body().toString());
                        }
                    }
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }

                        SignupResponse profileData = response.body();

                        if (profileData != null) {
                            if (profileData.getData() != null) {
                                /*SharedData.id = profileData.getData().user_id;*/
                                SharedData.weight = profileData.getData().weight;
                                SharedData.height = profileData.getData().height;
                                SharedData.age = profileData.getData().age;
                                SharedData.gender = profileData.getData().gender;
                                SharedData.goal = profileData.getData().goal;
                                SharedData.level = profileData.getData().level;
                                SharedData.unitType = profileData.getData().unit;
                                SharedData.level_id = profileData.getData().level_id;
                                levelID = SharedData.level_id;
                                SharedData.goal_id = profileData.getData().goal_id;
                                goalID = SharedData.goal_id;

                                SessionUtil.setUserWeight(mContext, SharedData.weight);
                                SessionUtil.setUserHeight(mContext, SharedData.height);
                                SessionUtil.setUserAge(mContext, SharedData.age);
                                SessionUtil.setUserGender(mContext, SharedData.gender);
                                SessionUtil.setUserLevelID(mContext, levelID);
                                SessionUtil.setUserGoalID(mContext, goalID);
                                SessionUtil.setUserUnitType(mContext, SharedData.unitType);


                                FirstName = getFirstWord(SharedData.username);
                                if (FirstName != null && FirstName.trim() != "") {
                                    if (FirstName.length() > 0) {
                                        FirstNameCap = FirstName.trim().substring(0, 1).toUpperCase() + FirstName.trim().substring(1);
                                    } else {
                                        FirstNameCap = "";
                                    }
                                } else {
                                    FirstNameCap = "";
                                }
                                if (mTextViewUsername != null) {
                                    mTextViewUsername.setText(FirstNameCap);
                                }

                                checkNetworkAndGetCoachesData();

                            } else {
                                message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                                if (Common.isLoggingEnabled) {
                                    if (message != null) {
                                        Log.e(TAG, "Response Status " + message.toString());
                                    }
                                    Log.e(TAG, "profileData.Data is null and response");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "profileData is null and response");
                                Log.e(TAG, "Trial ends date is null");
                            }
                            if (mContext != null) {
                                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, "profileData is null and response");
                            }
                        }

                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        //Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.e(TAG, "Response Status " + message.toString());
                            }
                            Log.e(TAG, "profile unsuccessfull");
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (mContext != null) {
                        new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_API_failure", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                        Log.e(TAG, "Dashboard Fragment Exception while getting user profile data: " + e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (mContext != null) {
                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    public void checkNetworkAndGetCoachesData() {
        if (mContext != null) {
            //currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);

            if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                if (!SessionUtil.isReloadDashboardDataRequired(mContext) || !SessionUtil.isLoadHomeData(mContext)) {

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "CurrentDateDifference: " + currentDateDifference);
                    }
                    /*if (currentDateDifference == 1) {*/
                    dataList = dbHelper.getAllCoaches(myWeek, myDay);
                    //visualizationList = dbHelper.getAllVisualization(String.valueOf(myDay), String.valueOf(myWeek));
                    if (Common.isLoggingEnabled) {
                        Log.i(TAG, "Coaches Data in DB is " + dataList.toString());
                    }
                    if (dataList != null && dataList.size() > 0) {
                        mFrameLayout.stopShimmerAnimation();
                        mFrameLayout.setVisibility(View.GONE);
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "--------------Coaches in Local DB--------------");
                            Log.d(TAG, dataList.toString());
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewCoaches.setVisibility(View.VISIBLE);
                        setCoachesDataToAdpter(dataList, 200);
                        visualizationList = dbHelper.getAllVisualization(String.valueOf(myDay), String.valueOf(myWeek));
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "--------------Visualization in Local DB--------------");
                            Log.i(TAG, "visualization List in DB is " + visualizationList.toString());
                        }
                        if (visualizationList != null && visualizationList.size() > 0) {
                            setVisualizationData(visualizationList);
                            //get data from local db for recipe
                            //checkAndGePagerRecipesDataFromDb();
                            getNutritionDataFromDbOrServer();
                        } else {
                            getVisualizationFromServer();
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Datalist for coaches is null or empty so coaches data will retrieve from server");
                        }
                        getCoachesDataFromServer();
                    }

                   /* } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Call data from server because date difference is not 1");
                        }
                        //loadDataFromAPI(level_id, goal_id);
                        getCoachesDataFromServer();
                    }*/
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Call data from server because refresh data required");
                    }
                    getCoachesDataFromServer();
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "No Internet so, we are check data in local db first!");
                }

                mFrameLayout.stopShimmerAnimation();
                mFrameLayout.setVisibility(View.GONE);


                //get data from local Db
                dataList = dbHelper.getAllCoaches(myWeek, myDay);


                if (dataList != null && dataList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewCoaches.setVisibility(View.VISIBLE);
                    setCoachesDataToAdpter(dataList, 200);
                    visualizationList = dbHelper.getAllVisualization(String.valueOf(myDay), String.valueOf(myWeek));
                    //set visualization Data if no internet connection
                    if (visualizationList != null && visualizationList.size() > 0) {
                        setVisualizationData(visualizationList);
                    }
                    //check pager recipes data in local db
                    /*checkAndGePagerRecipesDataFromDb();*/
                    getNutritionDataFromDbOrServer();
                } else {
                    //blurrBackground();
                    // StartLoading();
                    showNoInternetConnectionDialog();
                    showToast(resources.getString(R.string.no_internet_connection));
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "checkNetworkAndGetCoachesData::isAdded is false or getContext is null");
            }
        }
    }

    private void getImageFromFirebase() {
        try {
            if (mContext != null) {
                if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                    File localFile = File.createTempFile("profile_images", "jpg");

                    storageRef.getReference().child("profile_images/" + currentUserId).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Firebase storage successfully retrieved");
                                }
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                //mAvatar.setImageBitmap(bitmap);
                                // Glide.with(mContext).load(bitmap).into(mAvatar);
                                if (mContext != null) {
                                    Glide.with(mContext).load(ImageUtil.rotateImage(localFile.getAbsolutePath(), bitmap)).into(mAvatar);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.e(TAG, "Context is null while showing profile image at dashboard");
                                }
                                StoptShimmer();
                            } catch (Exception ex) {
                                if (Common.isLoggingEnabled) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                            if (mContext != null) {
                                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                            }
                            StoptShimmer();
                        }
                    });
                }
            }
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
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
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));
            }
            StoptShimmer();
        }
    }

    private void checkNetworkAndGetNewDataFromServer(String level_id, String goal_id) {
        if (mContext != null) {
            if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                if ((level_id != null && !level_id.matches("")) || (goal_id != null && !goal_id.matches(""))) {

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "checkNetworkAndRefreshCoachesData, My day number is " + myDay);
                        Log.d(TAG, "checkNetworkAndRefreshCoachesData, My week number is " + myWeek);
                    }
                    getCoachesDataFromServer();
                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Level id and goal ID is null");
                    }
                }
            } else {

                mFrameLayout.stopShimmerAnimation();
                mFrameLayout.setVisibility(View.GONE);


                //get data from local Db
                dataList = dbHelper.getAllCoaches(myWeek, myDay);
                visualizationList = dbHelper.getAllVisualization(String.valueOf(myDay), String.valueOf(myWeek));
                //TODO
                //dashboardRecipesList = dbHelper.getAllNutrition();


                if (dataList != null && dataList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewCoaches.setVisibility(View.VISIBLE);
                    setCoachesDataToAdpter(dataList, 200);
                    //set visualization Data if no internet connection
                    if (visualizationList != null && visualizationList.size() > 0) {
                        setVisualizationData(visualizationList);
                    }
                        /*if (dashboardRecipesList != null && dashboardRecipesList.size() > 0) {
                            // populateNutrition(dashboardRecipesList);
                        }*/
                } else {
                    //blurrBackground();
                    // StartLoading();
                    showNoInternetConnectionDialog();
                    showToast(resources.getString(R.string.no_internet_connection));
                }
            }
        }
    }


    //Extract first name From user name
    private String getFirstWord(String firstname) {

        int index = firstname.indexOf(' ');

        if (index > -1) { // Check if there is more than one word.

            return firstname.substring(0, index).trim(); // Extract first word.

        } else {
            return firstname; // Text is the first word itself.
        }
    }

    private void getCoachesDataFromServer() {
        mFrameLayout.setVisibility(View.VISIBLE);
        mFrameLayout.startShimmerAnimation();
        recyclerView.setVisibility(View.GONE);

        visualizationShimmerLayout.startShimmerAnimation();
        visualizationShimmerLayout.setVisibility(View.VISIBLE);
        calmCardView.setVisibility(View.GONE);
        mSleepVisualizationCardView.setVisibility(View.VISIBLE);
        mLinearLayoutForMorning.setVisibility(View.VISIBLE);
        mLinearLayoutForAfternoon.setVisibility(View.VISIBLE);
        mLinearLayoutForNight.setVisibility(View.VISIBLE);

        nutritionShimmerLayout.startShimmerAnimation();
        nutritionShimmerLayout.setVisibility(View.VISIBLE);
        pager2.setVisibility(View.GONE);
        //nutritionLL.setVisibility(View.GONE);
        textViewNutrition.setVisibility(View.VISIBLE);

        /*dbHelper.clearCoaches();
        dbHelper.clearVisualizations();
        dbHelper.clearDashboardNutrition();*/


        coachesCall = ApiClient.getService().getCoachesProfileData(tokenType + token, currentUserId, Integer.parseInt(goalID), Integer.parseInt(levelID), myWeek, myDay);

        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        coachesCall.enqueue(new Callback<CoachesProfileDataModel>() {
            @Override
            public void onResponse(Call<CoachesProfileDataModel> call, Response<CoachesProfileDataModel> response) {
                // inside on response method we are checking
                // if the response is success or not.

                try {
                    // Log.d(TAG, "Coaches Data response: " + response.errorBody().toString());
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Response Status " + message.toString());

                    }
                    if (mContext != null) {
                        if (SessionUtil.getAPP_Environment(mContext).matches("testing")) {
                            showToast("Coaches::" + message);
                        }
                    }

                    if (response.isSuccessful()) {
                        // Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();
                        if (mContext != null) {
                            if (SessionUtil.isLoadHomeData(mContext)) {
                                SessionUtil.setLoadHomeData(false, mContext);
                            }
                            SessionUtil.setDashboardReloadData(mContext, false);
                        }

                        coachProfileDataModel = response.body();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Coaches Data response: " + response.body().getData().get(0).getName());
                            Log.d(TAG, "Coaches Data response: " + coachProfileDataModel);
                        }
                        if (coachProfileDataModel != null) {
                            if (coachProfileDataModel.getData().size() > 0) {
                                if (toDate != null) {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, " coachesCall difference: " + currentDateDifference);
                                    }
                                    // add data to local db
                                    // if (currentDateDifference == 1) {
                                    //dbHelper.addCoachesProfilesData(coachProfileDataModel);
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "getCoachesDataFromServer:: coaches data retireved from server and saving in db");
                                    }
                                    saveCoachInDBAndShow(coachProfileDataModel.getData());

                                   /* } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "getCoachesDataFromServer:: coaches data retireved from server and set to adapter without saving in db");
                                        }
                                        mFrameLayout.setVisibility(View.GONE);
                                        mFrameLayout.stopShimmerAnimation();
                                        recyclerView.setVisibility(View.VISIBLE);
                                        textViewCoaches.setVisibility(View.VISIBLE);
                                        // if (textViewCoaches.getVisibility() == View.GONE && recyclerView.getVisibility() == View.GONE) {
                                        textViewCoaches.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        //}
                                        setCoachesDataToAdpter(coachProfileDataModel.getData(), statusCode);

                                    }*/
                                } else {
                                    mFrameLayout.setVisibility(View.GONE);
                                    mFrameLayout.stopShimmerAnimation();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    textViewCoaches.setVisibility(View.VISIBLE);
                                    //if (textViewCoaches.getVisibility() == View.GONE && recyclerView.getVisibility() == View.GONE) {
                                    textViewCoaches.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    // }
                                    setCoachesDataToAdpter(coachProfileDataModel.getData(), statusCode);
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "getCoachesDataFromServer:: todate is null");
                                    }
                                }
                            } else {
                                //-------------------Coaches not available on server-----------------
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "getCoachesDataFromServer:: Coaches not available on server");
                                }
                                mFrameLayout.setVisibility(View.GONE);
                                mFrameLayout.stopShimmerAnimation();
                                recyclerView.setVisibility(View.VISIBLE);
                                //Coaches not available
                                //if (textViewCoaches.getVisibility() == View.VISIBLE && recyclerView.getVisibility() == View.VISIBLE) {
                                textViewCoaches.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                //}
                            }

                        } else {
                            mFrameLayout.setVisibility(View.GONE);
                            mFrameLayout.stopShimmerAnimation();
                            recyclerView.setVisibility(View.VISIBLE);
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "getCoachesDataFromServer :: coachProfileDataModel == null");
                            }
                        }

                    } else if (response.code() == 401) {
                        if (mContext != null) {
                            LogoutUtil.redirectToLogin(mContext);
                            Toast.makeText(mContext, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 404) {
                        mFrameLayout.setVisibility(View.GONE);
                        mFrameLayout.stopShimmerAnimation();
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewCoaches.setVisibility(View.VISIBLE);

                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.d(TAG, "Response Status " + message.toString());
                            } else {
                                Log.e(TAG, "Message is null in coaches response " + response.code());
                            }
                        }
                        if (message != null) {
                            showToast(message.toString());
                            Toast.makeText(mContext, message.toString(), Toast.LENGTH_SHORT).show();
                        }
                        mFrameLayout.setVisibility(View.GONE);
                        mFrameLayout.stopShimmerAnimation();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (mContext != null) {
                        if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                            getVisualizationFromServer();
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "No Internet Connection");
                            }
                        }
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (mContext != null) {
                        new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_coaches_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));
                    }

                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Dashboard Fragment exception while geeting coaches data : " + ex.toString());
                        ex.printStackTrace();
                    }


                    if (textViewCoaches.getVisibility() == View.VISIBLE && recyclerView.getVisibility() == View.VISIBLE) {
                        textViewCoaches.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<CoachesProfileDataModel> call, Throwable t) {


                if (textViewCoaches.getVisibility() == View.VISIBLE && recyclerView.getVisibility() == View.VISIBLE) {
                    textViewCoaches.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }

                FirebaseCrashlytics.getInstance().recordException(t);
                if (mContext != null) {
                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_coaches_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    void saveCoachInDBAndShow(List<CoachesProfileDataModel.Data> coachesProfileDataModel) {
        if (coachesProfileDataModel != null) {
            for (int i = 0; i < coachesProfileDataModel.size(); i++) {
                if (coachesProfileDataModel.get(i).getId() != null) {
                    if (!dbHelper.checkCoachId(String.valueOf(coachesProfileDataModel.get(i).getId()), myWeek, myDay)) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "saveCoachInDB:: coach saved in db: " + coachesProfileDataModel.get(i).getId());
                        }
                        dbHelper.addSingleCoachData(coachesProfileDataModel.get(i), myWeek, myDay);
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "saveCoachInDBAndShow:: saveCoachInDB:: coach already in db: " + coachesProfileDataModel.get(i).getId());
                        }
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "saveCoachInDB::coachesProfileDataModel.get(i).getId() is null");
                    }
                }
            }
            /*dataList = dbHelper.getAllCoaches();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "saveCoachInDBAndShow :: coachesCall Coaches from db: " + coachesProfileDataModel.toString());
            }
            dataList = coachesProfilesList;*/
            setCoachesDataToAdpter(coachesProfileDataModel, statusCode);
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "saveCoachInDBAndShow:: coachesProfileDataModel.getData() is null");
            }
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_coaches_LocalDb", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, "coachesProfileDataModel.getData() is null");
            }

        }
    }


    void getVisualizationFromServer() {

        visualizationCall = ApiClient.getService().getVisualizationForDashboard(tokenType + token, myDay, Integer.parseInt(levelID), Integer.parseInt(goalID), myWeek);
        visualizationCall.enqueue(new Callback<VisualizationModel>() {
            @Override
            public void onResponse(Call<VisualizationModel> call, Response<VisualizationModel> response) {
                try {
                    visualizationModel = response.body();
                    if (mContext != null) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Visualization Response Status " + message.toString());
                        }

                        if (SessionUtil.getAPP_Environment(mContext).matches("testing"))
                            showToast("Visualization response :: " + message);
                    }
                    if (response.isSuccessful()) {

                        //Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();
                        if (visualizationModel != null) {
                            if (visualizationModel.getStatus() != null) {
                                if (visualizationModel.getStatus()) {
                                    if (visualizationModel.getData() != null) {
                                        if (visualizationModel.getData().size() > 0) {
                                            //dbHelper.clearVisualizations();
                                            //check data in local db
                                            //add data to local db
                                            checkVisualizationDataInDb(visualizationModel);

                                            visualizationShimmerLayout.stopShimmerAnimation();
                                            visualizationShimmerLayout.setVisibility(View.GONE);
                                            calmCardView.setVisibility(View.VISIBLE);

                                            //get data from db if exist else from server
                                            setVisualizationData(visualizationModel.getData());

                                        } else {
                                            visualizationShimmerLayout.stopShimmerAnimation();
                                            visualizationShimmerLayout.setVisibility(View.GONE);
                                            calmCardView.setVisibility(View.VISIBLE);
                                            if (Common.isLoggingEnabled) {
                                                Log.e(TAG, "Visualization Data list size from server is zero");
                                            }
                                            showToast(resources.getString(R.string.visualization_not_available));
                                        }

                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "Visualization Data is null");
                                        }
                                    }

                                } else {
                                    if (visualizationModel != null && visualizationModel.getData() != null && visualizationModel.getData().size() > 0) {
                                        visualizationShimmerLayout.stopShimmerAnimation();
                                        visualizationShimmerLayout.setVisibility(View.GONE);
                                        calmCardView.setVisibility(View.VISIBLE);
                                        if (visualizationModel.getData().get(0).getTime().matches("not available")) {
                                            mLinearLayoutForMorning.setVisibility(View.VISIBLE);
                                            shimmerMorningImgLayout.stopShimmerAnimation();
                                            shimmerMorningImgLayout.setVisibility(View.GONE);
                                            imageMorning.setVisibility(View.VISIBLE);

                                            mLinearLayoutForAfternoon.setVisibility(View.GONE);
                                            mLinearLayoutForNight.setVisibility(View.GONE);

                                            mTextViewMorning.setText(visualizationModel.getData().get(0).getDay());
                                            if (isAdded() && mContext != null) {
                                                Glide.with(mContext).load(visualizationModel.getData().get(0).getImageURL()).listener(new RequestListener<Drawable>() {
                                                    @Override
                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                        //FirebaseCrashlytics.getInstance().recordException(e);
                                                        if (Common.isLoggingEnabled) {
                                                            Log.e(TAG, "Image load failed: Image URL is "/* + Common.IMG_BASE_URL + */ + visualizationModel.getData().get(0).getImageURL());
                                                            e.printStackTrace();
                                                        }
                                                        FirebaseCrashlytics.getInstance().recordException(e);
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        if (Common.isLoggingEnabled) {
                                                            Log.d(TAG, "Visualization image banner is loaded");
                                                        }
                                                                    /*shimmerMorningImgLayout.stopShimmerAnimation();
                                                                    shimmerMorningImgLayout.setVisibility(View.GONE);
                                                                    imageMorning.setVisibility(View.VISIBLE);*/
                                                        return false;
                                                    }
                                                }).into(imageMorning);
                                            }
                                        }

                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "Visualization is null or Visualization data is null or Visualization size is greater is zero");
                                        }
                                    }
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Visualization status is null");
                                }
                                showToast(resources.getString(R.string.something_went_wrong));

                            }

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Visualization response is null");
                            }

                            showToast(resources.getString(R.string.something_went_wrong));

                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Visualization response unsuccessful");
                        }
                        if (response.code() == 401) {
                            if (mContext != null) {
                                showToast(resources.getString(R.string.unauthorized));
                                LogoutUtil.redirectToLogin(mContext);
                            }
                        } else {
                            showToast(resources.getString(R.string.something_went_wrong));

                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Response Status " + message.toString());
                            }
                            if (SessionUtil.getAPP_Environment(mContext).matches("testing")) {
                                showToast("Visualization Response: " + message);
                            }
                        }
                        //Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();
                    }
                    if (/*isAdded() &&*/ mContext != null) {
                        if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                            getNutritionFromServer();
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "No Internet Connection");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Fragment not attached with activity");
                        }
                        if (mContext != null) {
                            new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, "Fragment not attached with activity");
                        }
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (mContext != null) {
                        new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));

                    }
                }
            }

            @Override
            public void onFailure(Call<VisualizationModel> call, Throwable t) {
                visualizationShimmerLayout.stopShimmerAnimation();
                visualizationShimmerLayout.setVisibility(View.GONE);
                calmCardView.setVisibility(View.GONE);
                textViewCalm.setVisibility(View.GONE);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                if (mContext != null) {
                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.throwableObject(t));

                }
            }
        });
    }


    private void setVisualizationData(List<VisualizationModel.Data> visualizationModelList) {

        visualizationShimmerLayout.stopShimmerAnimation();
        visualizationShimmerLayout.setVisibility(View.GONE);
        calmCardView.setVisibility(View.VISIBLE);
        if (visualizationModelList.get(0).getPlaylistImage() != null) {
            if (isAdded() && mContext != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Visualization banner image url: " + visualizationModelList.get(0).getPlaylistImage());
                }
                Glide.with(mContext).load(visualizationModelList.get(0).getPlaylistImage()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        if (mContext != null) {
                            new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Visualization banner image error");
                            e.printStackTrace();
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Visualization banner image ready");
                        }
                                                                    /*shimmerMorningImgLayout.stopShimmerAnimation();
                                                                    shimmerMorningImgLayout.setVisibility(View.GONE);
                                                                    imageMorning.setVisibility(View.VISIBLE);*/
                        return false;
                    }
                }).into(imageSleepVisualization);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "isAdded() is false OR mContext == null in visualization banner");
                }
            }
        } else {
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_Image_Banner", SessionUtil.getUserEmailFromSession(mContext), INFORMATION, "Visualization banner image URL is null");
            }
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Visualization banner image URL is null");
            }
        }
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "visualizationModelList.size() = " + visualizationModelList.size());
        }
        for (int i = 0; i < visualizationModelList.size(); i++) {
            final int index = i;

            if (mContext != null)
                if (visualizationModelList.get(i).getTime().matches("Morning") || visualizationModelList.get(i).getTime().matches("Morgon")) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Morning Image URL at " + i + " is " + visualizationModelList.get(i).getImageURL());
                    }
                    mLinearLayoutForMorning.setVisibility(View.VISIBLE);
                    shimmerMorningImgLayout.stopShimmerAnimation();
                    shimmerMorningImgLayout.setVisibility(View.GONE);
                    imageMorning.setVisibility(View.VISIBLE);
                    mTextViewMorning.setText(visualizationModelList.get(i).getTime());
                    if (isAdded() && mContext != null) {
                        Glide.with(mContext).load(visualizationModelList.get(i).getImageURL()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //FirebaseCrashlytics.getInstance().recordException(e);
                                if (Common.isLoggingEnabled) {
                                    if (visualizationModelList.get(index) != null && visualizationModelList.get(index).getImageURL() != null) {
                                        Log.e(TAG, "Image load failed: Image URL is "/* + Common.IMG_BASE_URL +*/ + visualizationModelList.get(index).getImageURL());
                                    } else {
                                        Log.e(TAG, "Image load failed: Image URL is null");
                                    }
                                    e.printStackTrace();
                                }
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (mContext != null) {
                                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Visualization image banner is loaded");

                                }
                                                                    /*shimmerMorningImgLayout.stopShimmerAnimation();
                                                                    shimmerMorningImgLayout.setVisibility(View.GONE);
                                                                    imageMorning.setVisibility(View.VISIBLE);*/
                                return false;
                            }
                        }).into(imageMorning);
                    }

                } else if (visualizationModelList.get(i).getTime().matches("Afternoon") || visualizationModelList.get(i).getTime().matches("Eftermiddag")) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Afternoon Image URL at " + i + " is " + visualizationModelList.get(i).getImageURL());
                    }
                    mLinearLayoutForAfternoon.setVisibility(View.VISIBLE);
                    shimmerAfternoonImgLayout.stopShimmerAnimation();
                    shimmerAfternoonImgLayout.setVisibility(View.GONE);
                    imageAfternoon.setVisibility(View.VISIBLE);
                    mTextViewAfternoon.setText(visualizationModelList.get(i).getTime());
                    if (isAdded() && mContext != null) {
                        Glide.with(mContext).load(visualizationModelList.get(i).getImageURL()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //FirebaseCrashlytics.getInstance().recordException(e);
                                if (Common.isLoggingEnabled) {
                                    if (visualizationModelList.get(index) != null && visualizationModelList.get(index).getImageURL() != null) {
                                        Log.e(TAG, "Image load failed: Image URL is "/* + Common.IMG_BASE_URL +*/ + visualizationModelList.get(index).getImageURL());
                                    } else {
                                        Log.e(TAG, "Image load failed: Image URL is null");
                                    }
                                    e.printStackTrace();
                                }
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (mContext != null) {
                                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Visualization image banner is loaded");

                                }
                                                                    /*shimmerAfternoonImgLayout.stopShimmerAnimation();
                                                                    shimmerAfternoonImgLayout.setVisibility(View.GONE);
                                                                    imageAfternoon.setVisibility(View.VISIBLE);*/
                                return false;
                            }
                        }).into(imageAfternoon);
                    }
                } else if (visualizationModelList.get(i).getTime().matches("Night") || visualizationModelList.get(i).getTime().matches("Natt")) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Night Image URL at " + i + " is " + visualizationModelList.get(i).getImageURL());
                    }
                    mLinearLayoutForNight.setVisibility(View.VISIBLE);
                    shimmerNightImgLayout.stopShimmerAnimation();
                    shimmerNightImgLayout.setVisibility(View.GONE);
                    imageNight.setVisibility(View.VISIBLE);
                    mTextViewNight.setText(visualizationModelList.get(i).getTime());
                    if (isAdded() && mContext != null) {
                        Glide.with(mContext).load(visualizationModelList.get(i).getImageURL()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //FirebaseCrashlytics.getInstance().recordException(e);
                                if (Common.isLoggingEnabled) {
                                    if (visualizationModelList.get(index) != null && visualizationModelList.get(index).getImageURL() != null) {
                                        Log.e(TAG, "Image load failed: Image URL is "/* + Common.IMG_BASE_URL +*/ + visualizationModelList.get(index).getImageURL());
                                    } else {
                                        Log.e(TAG, "Image load failed: Image URL is null");
                                    }
                                    e.printStackTrace();
                                }
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (mContext != null) {
                                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Visualization_Image", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(e));
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (Common.isLoggingEnabled) {
                                    //Log.d(TAG, visualizationModel.getData().get(index).getName() + " is loaded");

                                }
                                                                    /*shimmerNightImgLayout.stopShimmerAnimation();
                                                                    shimmerNightImgLayout.setVisibility(View.GONE);
                                                                    imageNight.setVisibility(View.VISIBLE);*/
                                return false;
                            }
                        }).into(imageNight);
                    }
                }
        }

    }

    private void checkVisualizationDataInDb(VisualizationModel visualizationModel) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if (currentDateDifference == 1) {
                for (int i = 0; i < visualizationModel.getData().size(); i++) {
                    String day = visualizationModel.getData().get(i).getDay();
                    String week = visualizationModel.getData().get(i).getWeek();
                    String time = visualizationModel.getData().get(i).getTime();

                    if (!dbHelper.checkVisualizationByDayWeekAndTitle(day, week, time)) {
                        dbHelper.addSleepVisualizationData(visualizationModel);
                    }
                }
                //}
            }
        }, 0);

    }

    void getNutritionFromServer() {
        if (SessionUtil.isAPI_V3(mContext)) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "V3");
            }
            nutritionsCall = ApiClient.getService().dashboardPagerNutritionV3(tokenType + token, myDay, myWeek);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "V2");
            }
            nutritionsCall = ApiClient.getService().dashboardPagerNutrition(tokenType + token, myDay, myWeek);
        }
        nutritionsCall.enqueue(new Callback<DashboardNutritionPagerModel>() {
            @Override
            public void onResponse(Call<DashboardNutritionPagerModel> call, Response<DashboardNutritionPagerModel> response) {
                if (mContext != null) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Recipes Status " + message.toString());
                    }

                    if (SessionUtil.getAPP_Environment(mContext).matches("testing"))
                        showToast("Recipes response :: " + message);
                }
                if (response.isSuccessful()) {
                    //  Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();

                    dashboardNutrition = response.body();
                    if (Common.isLoggingEnabled) {
                        if (dashboardNutrition != null) {
                            Log.d(TAG, "Nutrition response is " + dashboardNutrition.toString());
                        } else {
                            Log.e(TAG, "Nutrition response is null");
                        }
                    }
                    if (dashboardNutrition != null) {
                        //if (dashboardNutrition.getStatus()) {
                        if (dashboardNutrition.getData() != null) {
                            if (dashboardNutrition.getData().getRecipes() != null) {
                                //delete all nutritions from local db before save again
                                // if (currentDateDifference == 1) {
                                //add current day data to db
                                //dbHelper.clearDashboardNutrition();
                                dbHelper.addPagerRecipes(dashboardNutrition.getData().getRecipes(), myWeek, myDay);

                                // saveNutritionInDB(dashboardNutrition.getData().getRecipes());
                                // }

                                dashboardRecipeList = dashboardNutrition.getData().getRecipes();
                                populateNutrition(dashboardRecipeList);
                                //get Data from DB or Server

                                //getNutritionDataFromDbOrServer();
                            }
                        }

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Nutrition Dashboard object is null");
                        }
                        showToast(resources.getString(R.string.something_went_wrong));
                    }

                } else {
                    if (response.code() == 401) {
                        if (mContext != null) {
                            LogoutUtil.redirectToLogin(mContext);
                            Toast.makeText(mContext, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Nutrition Dashboard API not successful");
                        }
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response Status " + message.toString());
                        }
                        if (SessionUtil.getAPP_Environment(mContext).matches("testing")) {
                            showToast("Nutritions Reponse : " + message);
                        }
                        //Toast.makeText(mContext,message.toString(),Toast.LENGTH_SHORT).show();

                        if (mContext != null) {
                            new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Nutrition_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, " Nutrition Dashboard API not successful");
                        }
                        // showToast(getString(R.string.nutrition_not_available));
                    }
                }

            }

            @Override
            public void onFailure(Call<DashboardNutritionPagerModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                nutritionShimmerLayout.setVisibility(View.VISIBLE);
                pager2.setVisibility(View.GONE);
                if (mContext != null) {
                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Nutrition_API", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.throwableObject(t));
                }
                //nutritionLL.setVisibility(View.GONE);

                // showToast(getActivity().getResources().getString(R.string.something_went_wrong));
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });

        if (/*isAdded() && */mContext != null) {
            String deviceId = SessionUtil.getDeviceId(mContext);
            String fcm_token = SessionUtil.getFcmToken(mContext);
            if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                if (previousLocation != null) {
                    if (!previousLocation.matches("")) {
                        String loggedLat = SessionUtil.getLoggedLatitude(mContext);
                        String loggedLng = SessionUtil.getLoggedLongitude(mContext);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Logged Lat is " + loggedLat);
                            Log.d(TAG, "Logged Lng is " + loggedLng);
                            Log.d(TAG, "Current Lat is " + SharedData.currentLatitude);
                            Log.d(TAG, "Current Lng is " + SharedData.currentLongitude);
                        }
                        if (!loggedLat.matches("") && !loggedLng.matches("")) {
                            double doubleLoggedLat = Double.parseDouble(loggedLat);
                            double doubleLoggedLng = Double.parseDouble(loggedLng);
                            float[] distance = new float[1];
                            Location.distanceBetween(doubleLoggedLat, doubleLoggedLng, SharedData.currentLatitude, SharedData.currentLongitude, distance);

                            if (distance.length > 0) {
                                if (distance[0] > 1000) {
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "Distance b/w two latitude is " + distance[0]);
                                    }
                                    if (SharedData.location != null && !SharedData.location.matches("")) {
                                        new UserDetailsUtil(mContext).updateUserDetails(tokenType + token, SharedData.location, WeekDaysHelper.getUTC_Time(), DashboardFragment.this);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "Distance is with in 1 KM radius");
                                    }
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Distance float variable length is zero");
                                }
                            }
                        }
                    }
                } else {
                    if (SharedData.location != null && !SharedData.location.matches("")) {
                        new UserDetailsUtil(mContext).updateUserDetails(tokenType + token, SharedData.location, WeekDaysHelper.getUTC_Time(), DashboardFragment.this);
                    }else{
                        new UserDetailsUtil(mContext).updateUserDetails(tokenType + token, "Location not available", WeekDaysHelper.getUTC_Time(), DashboardFragment.this);
                    }
                }
            }
        }
    }


    // get Data from db or Server
    private void getNutritionDataFromDbOrServer() {
        // dashboardRecipesList.clear();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if (dashboardNutrition != null && dashboardNutrition.getData() != null && dashboardNutrition.getData().getRecipes() != null) {
                //if (toDate != null) {
                //if (currentDateDifference == 1) {
                dashboardRecipeList = null;
                dashboardRecipeList = new DashboardNutritionPagerModel.Data.Recipes();
                boolean isPage1AvailableInDB = false;

                pager1List = dbHelper.getPager1Data("page1", myWeek, myDay);
                if (pager1List != null && pager1List.size() > 0) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "pager1" + pager1List.toString());
                    }
                    isPage1AvailableInDB = true;
                    dashboardRecipeList.setPage1(pager1List);

                }
                pager2List = dbHelper.getPager2Data("page2", myWeek, myDay);
                if (pager2List != null && pager2List.size() > 0) {
                    dashboardRecipeList.setPage2(pager2List);
                }

                pager3List = dbHelper.getPager3Data("page3", myWeek, myDay);
                if (pager3List != null && pager3List.size() > 0) {
                    dashboardRecipeList.setPage3(pager3List);
                }

                if (isPage1AvailableInDB) {
                    populateNutrition(dashboardRecipeList);
                } else {
                    if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                        getNutritionFromServer();
                    }
                }

                   /* } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "currentDateDifference != 1 in DashbaordFragment:getNutritionDataFromDbOrServer()");
                        }
                        populateNutrition(dashboardRecipeList);
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "toDate() is null in DashbaordFragment:getNutritionDataFromDbOrServer()");
                    }
                }*/
            }
        }, 0);

    }


    void populateNutrition(DashboardNutritionPagerModel.Data.Recipes dashboardRecipeList) {
        //if (dashboardRecipeList != null) {
        if (dashboardRecipeList != null) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Nutrition for adapter: " + dashboardRecipeList.toString());
            }
            if (dashboardRecipeList.getPage1() != null && dashboardRecipeList.getPage1().size() > 0) {
                count = 1;
            }
            if (dashboardRecipeList.getPage2() != null && dashboardRecipeList.getPage2().size() > 0) {
                count = 2;
            }
            if (dashboardRecipeList.getPage3() != null && dashboardRecipeList.getPage3().size() > 0) {
                count = 3;
            }

            if (dashboardRecipeList.getPage1() != null && dashboardRecipeList.getPage1().size() > 0) {
                nutritionShimmerLayout.stopShimmerAnimation();
                nutritionShimmerLayout.setVisibility(View.GONE);
                pager2.setVisibility(View.VISIBLE);
                pagerAdapter2 = new DemoRecyclerViewAdapter(count, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, dashboardRecipeList, mContext, myDay, myWeek);
                pager2.setAdapter(pagerAdapter2);
                pagerIndicator2.attachToPager(pager2);
            }

        }

    }


    @Override
    public void onClick(View v) {
        String selectedLang = SessionUtil.getlangCode(mContext);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Selected langauge of app is " + selectedLang);
        }
        if (v.getId() == R.id.linearLayoutForMorning) {
            if (selectedLang.matches("sv")) {
                dayTime = "Morgon";
            } else {
                dayTime = "Morning";
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadVisualizationFragment(getVisualizationByTime(dayTime));
                }
            }, 0);

        }
        if (v.getId() == R.id.linearLayoutForAfternoon) {
            if (selectedLang.matches("sv")) {
                dayTime = "Eftermiddag";
            } else {
                dayTime = "Afternoon";
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadVisualizationFragment(getVisualizationByTime(dayTime));
                }
            }, 0);

        }
        if (v.getId() == R.id.linearLayoutForNight) {
            if (selectedLang.matches("sv")) {
                dayTime = "Natt";
            } else {
                dayTime = "Night";
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadVisualizationFragment(getVisualizationByTime(dayTime));
                }
            }, 0);

        }
    }


    private void loadVisualizationFragment(VisualizationModel.Data data) {
        if (data != null) {
            //if (ConnectionDetector.isConnectedWithInternet(mContext)) {
            if (!data.getName().matches("not available")) {
                fragment = new SleepVisualizationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("imgURL", /*Common.IMG_BASE_URL +*/ data.getImageURL());
                bundle.putString("title", data.getName());
                bundle.putString("dayTime", data.getTime());
                bundle.putString("description", data.getDescription());
                bundle.putString("audioURL", data.getAudio());
                fragment.setArguments(bundle);
                loadFragment(fragment);
            } else {
                fragment = new SleepVisualizationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("imgURL", /*Common.IMG_BASE_URL +*/ data.getImageURL());
                bundle.putString("title", data.getName());
                bundle.putString("dayTime", data.getTime());
                bundle.putString("description", data.getDescription());
                bundle.putString("audioURL", data.getAudio());
                fragment.setArguments(bundle);
                loadFragment(fragment);
            }

        } else {

            showToast(resources.getString(R.string.no_data_available));

        }
    }

    private VisualizationModel.Data getVisualizationByTime(String time) {
        if (visualizationModel != null) {
            if (visualizationModel.getData() != null) {
                if (visualizationModel.getData().size() > 0) {
                    for (int i = 0; i < visualizationModel.getData().size(); i++) {
                        if (visualizationModel.getData().get(i).getTime().equals(time)) {
                            return visualizationModel.getData().get(i);
                        } else if (visualizationModel.getData().get(i).getTime().matches("not available")) {
                            return visualizationModel.getData().get(i);
                        }
                    }
                }
            }
        } else if (visualizationList.size() > 0) {
            for (int i = 0; i < visualizationList.size(); i++) {
                if (visualizationList.get(i).getTime().equals(time)) {
                    return visualizationList.get(i);
                } else if (visualizationList.get(i).getTime().matches("not available")) {
                    return visualizationList.get(i);
                }
            }
        }
        return null;
    }

    private void loadFragment(Fragment fragment) {
        // if (ConnectionDetector.isConnectedWithInternet(getActivity())) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.navigation_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }


    public void setCoachesDataToAdpter(List<CoachesProfileDataModel.Data> coachListApi, int statusCode) {
        if (coachListApi != null) {
            //coachListApi.clear();
            if (myDay != 0 && myWeek != 0) {
                //  coachesProfilesList.add(coachListApi.get(i).getData().get(j));
                if (/*isAdded() && */mContext != null) {
                    adapter = new CoachesAdapter(mContext, coachListApi, myDay, myWeek, Integer.parseInt(currentUserId), Integer.parseInt(levelID), Integer.parseInt(goalID), statusCode, isAdded());
                    adapter.notifyDataSetChanged();
                    layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    mFrameLayout.setVisibility(View.GONE);
                    mFrameLayout.stopShimmerAnimation();
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewCoaches.setVisibility(View.VISIBLE);
                    // if (textViewCoaches.getVisibility() == View.GONE && recyclerView.getVisibility() == View.GONE) {
                    textViewCoaches.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    // }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "setCoachesDataToAdpter :: not attached with fragment or getcontext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "setCoachesDataToAdpter :: DAY " + myDay + " and week " + myWeek);
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "setCoachesDataToAdpter :: coachListApi is null");
            }
        }
    }


    //Showing the No internet connection Custom Dialog =)
    public void showNoInternetConnectionDialog() {
        if (isAdded()) {
            if (mContext != null) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Entering showNoInternetConnectionDialog Method");
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(resources.getString(R.string.internet_issue)).setTitle(resources.getString(R.string.no_internet_connection)).setCancelable(false).setNeutralButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // getActivity().finish();
                    }
                });
                final AlertDialog alert = builder.create();
                alert.show();
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Showed No Intenet Connection Dialog");
                }
            }
        }
    }

    private void StartShimmer() {
        shimmerForAvatar.startShimmerAnimation();
        shimmerForAvatar.setVisibility(View.VISIBLE);
        mAvatar.setVisibility(View.INVISIBLE);

        //visualization shimmer
        visualizationShimmerLayout.startShimmerAnimation();
        visualizationShimmerLayout.setVisibility(View.VISIBLE);
        if (textViewCalm != null) {
            if (textViewCalm.getVisibility() == View.GONE) {
                textViewCalm.setVisibility(View.VISIBLE);
            }
        }

        //Visualization Images Shimmer
        shimmerMorningImgLayout.startShimmerAnimation();
        shimmerMorningImgLayout.setVisibility(View.VISIBLE);
        imageMorning.setVisibility(View.GONE);

        shimmerAfternoonImgLayout.startShimmerAnimation();
        shimmerAfternoonImgLayout.setVisibility(View.VISIBLE);
        imageAfternoon.setVisibility(View.GONE);

        shimmerNightImgLayout.startShimmerAnimation();
        shimmerNightImgLayout.setVisibility(View.VISIBLE);
        imageNight.setVisibility(View.GONE);

        nutritionShimmerLayout.startShimmerAnimation();
        nutritionShimmerLayout.setVisibility(View.VISIBLE);
    }

    private void StoptShimmer() {
        shimmerForAvatar.stopShimmerAnimation();
        shimmerForAvatar.setVisibility(View.GONE);
        mAvatar.setVisibility(View.VISIBLE);
    }

    private void blurrBackground() {
        if (isAdded()) {
            if (mContext != null) {
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

                blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurAlgorithm(new RenderScriptBlur(requireContext())).setBlurRadius(radius).setBlurAutoUpdate(true).setHasFixedTransformationMatrix(false);
            }
        }

    }

    public void StartLoading() {
        if (isAdded()) {
            if (mContext != null) {
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
            e.printStackTrace();
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    void showToast(String message) {
        try {
            if (isAdded()) {
                if (mContext != null) {
                    Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Fragement is not added to activity");
                }
            }
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    private final GCoreWakefulBroadcastReceiver mBroadcastReceiverSteps = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mContext != null) {
                            int steps = intent.getIntExtra("Steps", 0);
                            if (steps >= 0) {
                                if (steps < 1000) {
                                    stepsTV.setText("(" + steps + ")");
                                } else if (steps < 1000000) {
                                    double stepsValue = Math.abs(steps) / 1000.0;
                                    //  stepsCountValue.setText(String.format("%.2f",stepsValue)+""+"K");
                                    stepsTV.setText("(" + Double.toString(stepsValue).substring(0, Double.toString(stepsValue).length() - 1) + "" + "K)");
                                } else if (steps < 100000000) {
                                    double stepsValue = Math.abs(steps) / 1000000.0;
                                    //  stepsCountValue.setText(String.format("%.2f",stepsValue)+""+"M");
                                    stepsTV.setText("(" + Double.toString(stepsValue).substring(0, Double.toString(stepsValue).length() - 4) + "" + "M)");
                                } else {
                                    double stepsValue = Math.abs(steps) / 100000000.0;
                                    stepsTV.setText("(" + String.format("%.2f", stepsValue) + "" + "B)");
                                }
                                double height_in_cm = 0.0;
                                double weight_in_kg = 0.0;
                                height_in_cm = Double.parseDouble(SessionUtil.getUserHeight(mContext));
                                weight_in_kg = Double.parseDouble(SessionUtil.getUserWeight(mContext));
                                String CalCount = String.format("%.0f", Math.abs(CalculatorUtil.caloriesFromSteps(context, height_in_cm, weight_in_kg, steps)));
                                int CalValue = Integer.parseInt(CalCount);
                                if (CalValue > 1000) {
                                    double CaloriesValue = CalValue / 1000.0;
                                    caloriesTV.setText("(" + String.format("%.2f", CaloriesValue) + "" + "K)");
                                    //Log.i("calories K ",String.format("%.2f",CaloriesValue)+""+"K" );
                                    //Toast.makeText(mContext,String.format("%.2f",CaloriesValue)+""+"K" , Toast.LENGTH_SHORT).show();
                                } else {
                                    caloriesTV.setText("(" + CalValue + ")");
                                }
                                String DisCount = String.format("%.0f", Math.abs(CalculatorUtil.getDistanceNow(context, steps, height_in_cm)));
                                int DisValue = Integer.parseInt(DisCount);
                                if (DisValue > 1000) {
                                    double DistanceValue = DisValue / 1000.0;
                                    distanceTV.setText("(" + String.format("%.2f", DistanceValue) + "" + "K)");
                                    //SaveSharedPreference.setDistance(mContext, String.format("%.1f",DistanceValue)+"");
                                } else {
                                    double DistanceValue = DisValue / 1000.0;
                                    distanceTV.setText("(" + String.format("%.2f", DistanceValue) + ")");
                                }
                            }
                        }
                    }
                }, 0);

            } catch (Exception exception) {
                FirebaseCrashlytics.getInstance().recordException(exception);
                if (mContext != null) {
                    new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Step_counter_broadcastReceiver", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(exception));
                }
                if (Common.isLoggingEnabled) {
                    exception.printStackTrace();
                }

            }
        }
    };


    static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

    void startBroadCastingForSensor() {
        if (mContext != null) {
            Intent intent = new Intent();
            intent.setAction(mContext.getPackageName() + ".CUSTOM_INTENT_STEPS_SENSOR_LISTENER");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

            LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiverSteps, new IntentFilter(mContext.getPackageName() + ".CUSTOM_INTENT_STEPS"));

            mContext.registerReceiver(m_timeChangedReceiver, s_intentFilter);

            LocalBroadcastManager.getInstance(mContext).registerReceiver(dateChangeReceiver, new IntentFilter(mContext.getPackageName() + ".CUSTOM_INTENT_ACTIVITY_DATE_CHANGE"));

            LocalBroadcastManager.getInstance(mContext).registerReceiver(syncActivityBR, new IntentFilter(mContext.getPackageName() + ".SYNC_ACTIVITY_SERVICE"));
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "getContext is null");
            }
        }
    }

    private final BroadcastReceiver syncActivityBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In syncActivityBR: received Broadcast");
                }
                String requestFor = "";
                if (intent.hasExtra("requestFor")) {
                    requestFor = intent.getStringExtra("requestFor");
                    if (requestFor.matches("home")) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "In syncActivityBR to start service");
                        }
                        showStepsCounterView();
                    }
                }
                PermissionUtil.checkLocationPermission(context, DashboardFragment.this, DashboardFragment.this);

            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
    };

    private final GCoreWakefulBroadcastReceiver dateChangeReceiver = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Now you can call all your fragments method here
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "dateChangeReceiver");
            }

            //Toast.makeText(context, "DateChanged BraodCast Lives UI", Toast.LENGTH_LONG).show();
            stepsResetDateChangedUI();

            //updateNotify();
            //updateNotifyDiary();

        }
    };

    private final GCoreWakefulBroadcastReceiver m_timeChangedReceiver = new GCoreWakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_DATE_CHANGED)) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "m_timeChangedReceiver");
                }
               /*SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
                String NextDayDateAndTime = sdf.format(new Date());
                String TodayDateAndTime = SaveSharedPreference.getUserLogInDate(context);
                if(!TodayDateAndTime.equals(NextDayDateAndTime)){

                }*/
                stepsResetDateChangedUI();
                // updateNotify();
            }
        }
    };

    public void stepsResetDateChangedUI() {

        showStepsCounterView();
        /*stepsTV.setText("("+tempSteps+")");
        caloriesTV.setText("(0)");
        distanceTV.setText("(0)");*/
        if (mContext != null) {
            SimpleDateFormat sdf2 = new SimpleDateFormat(Common.DATE_FORMAT);
            String currentDateandTime1 = sdf2.format(new Date());
            //SessionUtil.setStepDaySessionDate(mContext, currentDateandTime1);
        }

    }

    public void showStepsCounterView() {
        try {
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {*/
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In showStepsCounterView()");
            }
            if (mContext != null) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In showStepsCounterView(), added to fragment and context not equal to null");
                }
                int todaySteps = 0;

                List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In showStepsCounterView(), Activity data in DB: " + list.toString());
                }
                for (int i = 0; i < list.size(); i++) {
                    waterCountTV.setText("" + list.get(i).getWaterCount());
                    drinkCountTV.setText("" + list.get(i).getWaterCount());
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Water Count = " + list.get(i).getWaterCount());
                    }
                    if (list.get(i).getWaterCount() != null) {
                        if (list.get(i).getWaterCount().matches("")) {
                            waterCountProgress.setProgress(0);
                        } else {
                            waterCountProgress.setProgress(Integer.parseInt(list.get(i).getWaterCount()));
                        }
                    } else {
                        waterCountProgress.setProgress(0);
                    }
                    todaySteps = Integer.parseInt(list.get(i).getStepsCount());
                }
                /*stepsTV.setText("(" + Math.abs(todaySteps) + ")");*/
                stepsTV.setText("(" + String.valueOf(Math.abs(todaySteps)) + ")");
                if (todaySteps < 10000) {
                    stepsTV.setText("(" + String.valueOf(todaySteps) + ")");
                } else if (todaySteps < 1000000) {
                    // Log.i("StepValue",todaySteps+"");
                    double stepsValue = Math.abs(todaySteps) / 1000.0;
                    stepsTV.setText("(" + Double.toString(stepsValue).substring(0, Double.toString(stepsValue).length() - 1) + "" + "K)");
                    //  stepsCountValue.setText(String.format("%.2f",stepsValue)+""+"K");

                } else if (todaySteps < 100000000) {
                    double stepsValue = Math.abs(todaySteps) / 1000000.0;
                    //   stepsCountValue.setText(String.format("%.2f",stepsValue)+""+"M");
                    stepsTV.setText("(" + Double.toString(stepsValue).substring(0, Double.toString(stepsValue).length() - 4) + "" + "M)");
                } else {
                    double stepsValue = Math.abs(todaySteps) / 100000000.0;
                    stepsTV.setText("(" + String.format("%.2f", stepsValue) + "" + "B)");
                }

                double height_in_cm = 0.0;
                double weight_in_kg = 0.0;
                height_in_cm = Double.parseDouble(SessionUtil.getUserHeight(mContext));
                weight_in_kg = Double.parseDouble(SessionUtil.getUserWeight(mContext));

                String CalCount = String.format("%.0f", Math.abs(CalculatorUtil.caloriesFromSteps(mContext, height_in_cm, weight_in_kg, todaySteps)));
                double CalValue = Integer.parseInt(CalCount);
                if (CalValue > 999) {
                    double CaloriesValue = CalValue / 1000.0;
                    caloriesTV.setText("(" + String.format("%.2f", CaloriesValue) + ")");
                } else {
                    caloriesTV.setText("(" + String.valueOf(CalValue) + ")");
                }
                String DisCount = String.format("%.0f", Math.abs(CalculatorUtil.getDistanceNow(mContext, todaySteps, height_in_cm)));
                int DisValue = Integer.parseInt(DisCount);
                if (DisValue > 999) {
                    double DistanceValue = DisValue / 1000.0;
                    distanceTV.setText("(" + String.format("%.2f", DistanceValue) + ")");
                } else {
                    //distanceUnit.setText("(Km)");
                    double DistanceValue = DisValue / 1000.0;
                    distanceTV.setText("(" + String.format("%.2f", DistanceValue) + ")");
                }
                SessionUtil.setDistance(mContext, DisCount + "");
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "showStepsCounterView() fragment not added or context is null");
                }
            }
                /*}
            }, 0);*/

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
                Log.e(TAG, "StepsView: " + e.toString());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                //permission for ACCESS_FINE_LOCATION
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mContext != null) {
                            SessionUtil.setLocationPermissionForeground(mContext, true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                PermissionUtil.checkNotificationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                            } else {
                                PermissionUtil.checkStepCounterPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                            }
                        }
                        //checkAccessBackgroundPermission();
                    } else {
                        if (mContext != null) {
                            SessionUtil.setLocationPermissionForeground(mContext, false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                PermissionUtil.checkNotificationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                            } else {
                                PermissionUtil.checkStepCounterPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                            }
                        }
                    }
                }
                break;
            case 2:
                //permission for Camera
                if (grantResults.length > 0) {
                    if (mContext != null) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SessionUtil.setCameraPermission(mContext, true);
                        } else {
                            SessionUtil.setCameraPermission(mContext, false);
                        }
                    }
                }
                break;

            case 4:
                //notification permission for android tiramisu and above
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (isAdded() && isVisible() && mContext != null) {
                            PermissionUtil.checkStepCounterPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                        }
                    }
                }
                break;
            case 5:
                //permission for Step Counter
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mContext != null) {
                            if (!StepCountServiceUtil.isMyServiceRunning(StepsService.class, mContext)) {
                                StepCountServiceUtil.startStepCountService(mContext);
                            }

                            SessionUtil.setStepCounterPermission(mContext, true);
                            if (!PermissionUtil.isBatterySavorPermissionActive(mContext)) {
                                PermissionUtil.checkBatteryUsageRestrictionPermission(mContext, DashboardFragment.this);
                            }
                        }
                    } else {
                        if (mContext != null) {
                            SessionUtil.setStepCounterPermission(mContext, false);
                        }
                    }
                }
                break;

            default:
                break;
        }

    }


    /**
     * @param context
     * @Description Stop ForeGroundService for stepCounting
     */


    public static void stopDataSyncService(Context context) {
        try {
            Intent startIntent = new Intent(context, StepCounterDataSync.class);
            context.startService(startIntent);
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }


    private void initDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_description = dialog.findViewById(R.id.dialog_description);
        btn_Cancel = dialog.findViewById(R.id.btn_left);
        btn_Cancel.setText(resources.getString(R.string.btn_cancel));
        btn_Continue = dialog.findViewById(R.id.btn_right);
        btn_Continue.setText(resources.getString(R.string.btn_continue));
        dialog_Timer = dialog.findViewById(R.id.dialog_Timer);
        dialog_Timer.setVisibility(View.GONE);
    }

    private void showDialogBox(String title, String description, String dialogFor) {
        try {
            if (mContext != null) {
                if (mContext.getResources() != null) {
                    btn_Continue.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.selected_bg));
                    btn_Cancel.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_background_dialog_left));
                }

                dialog_title.setText(title);
                dialog_description.setText(description);


                btn_Cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                        if (mContext != null) {
                            btn_Cancel.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                        }
                        SessionUtil.setShowPermissionDialogAgain(mContext, false);
                        // lllll
                        allowPermissionCardView.setStrokeColor(ContextCompat.getColor(mContext, R.color.red));
                        allowPermissionCardView.setStrokeWidth(2);
                        allowPermissionCardView.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                    // dialog.dismiss();
                });

                btn_Continue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogFor != null) {
                            if (dialogFor.matches("battery_setting")) {
                                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                                if (mContext != null) {
                                    allowPermissionCardView.setVisibility(View.GONE);
                                    btn_Continue.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.selected_bg));
                                }
                                //requestPermissions(new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, 5);
                                dialog.dismiss();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent();
                                    String packageName = mContext.getPackageName();
                                    PowerManager pm = (PowerManager) mContext.getSystemService(POWER_SERVICE);
                                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                        intent.setData(Uri.parse("package:" + packageName));
                                        startActivity(intent);
                                    }
                                }
                            } else if (dialogFor.matches("location_permission")) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                if (mContext != null) {
                                    if (mContext.getResources() != null) {
                                        btn_Continue.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                                        dialog.dismiss();
                                        btn_Continue.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_background_dialog_right));
                                        //requestPermissions(new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, 5);
                                    }
                                }

                            }

                        }
                    }
                });
                if (dialogFor.matches("battery_setting")) {
                    if (!PermissionUtil.isBatterySavorPermissionActive(mContext)) {
                        if (isAdded()) {
                            if (isVisible()) {
                                if (dialog != null) {
                                    if (!dialog.isShowing()) {
                                        dialog.show();
                                    }
                                }
                            }
                        }
                    } else {
                        if (dialog != null) {
                            if (isAdded()) {
                                if (isVisible()) {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }
                    }
                } else if (dialogFor.matches("location_permission")) {

                    if (dialog != null) {
                        if (isAdded()) {
                            if (isVisible()) {
                                if (!dialog.isShowing()) {
                                    dialog.show();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_Dialog_Opened_Failed", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        location.getLongitude();
        location.getLatitude();
    }

    @Override
    public void response(boolean isSuccessful, String message) {

    }

    @Override
    public void responseCode(int responseCode) {
        if (responseCode == 401) {
            if (mContext != null) {
                showToast(resources.getString(R.string.unauthorized));
                LogoutUtil.redirectToLogin(mContext);
            }
        }
    }

    @Override
    public void responseError(Throwable t) {
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
        }

    }

    @Override
    public void userActivitySync() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In DASHBOARD_FRAGMENT::userActivitySync()");
        }
        showStepsCounterView();
        PermissionUtil.checkLocationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In Dashboard, User Status: " + userStatus + "\nSubscription Status: " + subscriptionStatus);
        }
    }

    void activityDataSync() {
        if (mContext != null) {
            if (userActivityListFromDB == null) {
                userActivityListFromDB = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(mContext), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
            }
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "User activity in db: " + userActivityListFromDB.toString());
            }
            if (userActivityListFromDB.size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Activity list is greater than zero in DB");
                }
                if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, mContext)) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "In Dashboard fragment, uploading activity");
                    }
                    startSyncDataService("upload", "home");

                    //showStepsCounterView();

                }/*else{
                    stopDataSyncService(mContext);
                }*/
                //checkLocationPermission();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Activity list is zero in DB");
                }
                if (!SessionUtil.getActivityDownloadedDate(mContext).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "In Dashboard fragment, actiivty downloaded date not available in shared preference so, acitivty is going to upload");
                    }
                    if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, mContext)) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "In Dashboard fragment, downloading activity");
                        }
                        startSyncDataService("download", "home");
                        // checkLocationPermission();
                        //startBroadCastingForSensor();
                        //  showStepsCounterView();
                    }
                } else {
                    // stopDataSyncService(mContext);
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Today activity already downloaded");
                    }
                    showStepsCounterView();
                    PermissionUtil.checkLocationPermission(mContext, DashboardFragment.this, DashboardFragment.this);
                }
                //checkLocationPermission();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "getContext is null in activityDataSync method");
            }
        }
    }

    @Override
    public void showDialog(String dialogName) {
        if (dialogName.matches("location")) {
            showDialogBox(resources.getString(R.string.step_counter_work_properly_message), resources.getString(R.string.dialog_location_message), "location_permission");
        } else if (dialogName.matches("battery")) {
            showDialogBox(resources.getString(R.string.step_counter_work_properly_message), resources.getString(R.string.battery_restriction_dialog_msg), "battery_setting");
        }
    }

    @Override
    public void permissionRequest(int i) {
        if (i == 2) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        } else if (i == 4) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 4);
        } else if (i == 5) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 5);
        }
    }

    public void startAlarmForDateChange() {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(mContext, DateChangeReceiver.class);
        myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, myIntent, pendingFlags);
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.add(java.util.Calendar.DAY_OF_MONTH, 1);
        c.set(java.util.Calendar.HOUR_OF_DAY, 23);
        c.set(java.util.Calendar.MINUTE, 59);
        c.set(java.util.Calendar.SECOND, 59);
        c.set(java.util.Calendar.MILLISECOND, 999);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pendingIntent), pendingIntent);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Dashboard::next alarm set to " + alarmManager.getNextAlarmClock().getTriggerTime());
        }
    }

    @Override
    public void onDateSelection(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            toDate = sdf.format(date);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Calendar selected date is " + toDate);
            }
            SessionUtil.setSelectedDate(mContext, toDate);
            OneLineCalendarView.selectedDate = toDate;

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "toDate: " + toDate);
            }

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "--BEFORE--");
                Log.d(TAG, "--Day and week on Selceted Calender View--");
                Log.d(TAG, "Selected date in calendar View is " + toDate);
                Log.d(TAG, "calendarView, My day number is " + myDay);
                Log.d(TAG, "calendarView, My week number is " + myWeek);
                Log.d(TAG, "-------------------------------------------");
            }
            myDay = weekDaysHelper.getMyDay(mContext, toDate);
            myWeek = weekDaysHelper.getMyWeek(mContext, toDate);
            if (SessionUtil.getAPP_Environment(mContext).matches("testing")) {
                if (isAdded() && mContext != null) {
                    showToast("Week = " + myWeek + " and day = " + myDay);
                }
            }
            if (Common.isLoggingEnabled) {

                Log.d(TAG, "--AFTER--");
                Log.d(TAG, "--Day and week on Selceted Calender View--");
                Log.d(TAG, "Selected date in calendar View is " + toDate);
                Log.d(TAG, "calendarView, My day number is " + myDay);
                Log.d(TAG, "calendarView, My week number is " + myWeek);
                Log.d(TAG, "-------------------------------------------");
            }
            if (levelID != null && goalID != null) {
                if (!levelID.matches("") && !goalID.matches("")) {
                    //getCoachesData(Integer.parseInt(SharedData.level_id), Integer.parseInt(SharedData.goal_id), myDay, myWeek);
                    checkNetworkAndGetCoachesData();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Level ID or Goal ID is empty");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Level ID or Goal ID is null");
                }
            }

        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            if (mContext != null) {
                new LogsHandlersUtils(mContext).getLogsDetails("Dashboard_DateSlectionListener", SessionUtil.getUserEmailFromSession(mContext), EXCEPTION, SharedData.caughtException(ex));

            }
        }

    }
}