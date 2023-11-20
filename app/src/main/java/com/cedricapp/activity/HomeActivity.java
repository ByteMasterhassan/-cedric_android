package com.cedricapp.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.common.UserActivity;
import com.cedricapp.fragment.DashboardFragment;
import com.cedricapp.fragment.ExploreFragment;
import com.cedricapp.interfaces.UserActivityInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.fragment.ProgramsFragment;
import com.cedricapp.R;
import com.cedricapp.fragment.SettingFragment;
import com.cedricapp.fragment.ShoppingFragment;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.GooglePlayServiceUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LocationUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.service.LocationTrack;
import com.cedricapp.service.StepCounterDataSync;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity implements UserActivityInterface {
    public static FrameLayout frameLayout;
    ConstraintLayout constraintLayout;
    View iconView;
    String nutritionTime;
    int clickCounter = 0;
    MaterialTextView mTextViewWelcomingMessage;
    public static BottomNavigationView navigation;
    private Fragment SettingFragment;
    Fragment currentFragment;
    private int id;
    private long pressedTime;

    LocationTrack mLocationService;

    boolean mBound;
    ProgressBar progressBar;

    private BroadcastReceiver mSnackBarReceiver = null;

    boolean isSnackBarReciever;

    Snackbar snackbar;

    long timeOfTap = 0;
    Resources resources;

    String TAG = "BOTTOM_NAVIGATION_TAG";

    String FRAGMENT_TAG = "MY_FRAGMENT_TAG";

    DBHelper dbHelper;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    FloatingActionButton floatingActionButton;

    View view;


    @Override
    protected void onStart() {
        super.onStart();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "On Start");
        }
        SharedData.redirectToDashboard = false;
        SessionUtil.setLifeCyleStatus(getApplicationContext(), true);
        // onStartStuff();

    }


    void startLocationService() {
        /*permissionsToRequest = findUnAskedPermissions(permissions);

        if (permissionsToRequest.size()==0) {*/

        Intent intent = new Intent(this, LocationTrack.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationTrack.LocalBinder binder = (LocationTrack.LocalBinder) iBinder;
            mLocationService = binder.getService();
            checkIsLocationAvailable(mLocationService);

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void checkIsLocationAvailable(LocationTrack locationTrack) {
        if (locationTrack.canGetLocation()) {
            SharedData.currentLongitude = locationTrack.getLongitude();
            SharedData.currentLatitude = locationTrack.getLatitude();

            //get address
            String gpsLocation = LocationUtil.getCompleteAddressString(SharedData.currentLatitude, SharedData.currentLongitude, getApplicationContext());
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Latitude from GPS: " + SharedData.currentLatitude + "\nLongitude from GPS: " + SharedData.currentLongitude + "\nGPS Location: " + gpsLocation.toString());
                //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(SharedData.currentLongitude) + "\nLatitude:" + Double.toString(SharedData.currentLatitude), Toast.LENGTH_SHORT).show();
            }

            SharedData.location = gpsLocation;

            //Log.d("loc", gpsLocation.toString());
            /*if (gpsLocation != null && !gpsLocation.matches("")) {
                SharedData.previousGpsLocation = gpsLocation;
                //SessionUtil.setLoggedLocation(SharedData.previousGpsLocation, getApplicationContext());
            } else {
                SharedData.previousGpsLocation = " ";
            }*/

            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(SharedData.currentLatitude), Toast.LENGTH_SHORT).show();
        } else {
            //DialogUtil.showSettingsAlert(HomeActivity.this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "On Stop");
        }
        try {
            SessionUtil.setLifeCyleStatus(getApplicationContext(), false);
            if (mBound) {
                unbindService(connection);
                mBound = false;
            }
            if (mSnackBarReceiver != null) {
                if (isSnackBarReciever) {
                    unregisterReceiver(mSnackBarReceiver);
                } else {
                    isSnackBarReciever = false;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "On Destroy");
        }
        SessionUtil.setLifeCyleStatus(getApplicationContext(), false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "On Pause");
        }
        SessionUtil.setLifeCyleStatus(getApplicationContext(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "On Resume");
        }
        SessionUtil.setLifeCyleStatus(getApplicationContext(), true);
        SharedData.redirectToDashboard = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(mSnackBarReceiver, new IntentFilter("EVENT_SNACKBAR"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        view = getWindow().getDecorView().findViewById(android.R.id.content);

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "ON_CREATE_BOTTOM_NAVIGATION_BAR");
        }

        dbHelper = new DBHelper(getApplicationContext());
        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        // resources = Localization.setLanguage(HomeActivity.this, getResources());
        resources = getResources();
        SessionUtil.setSelectedDate(getApplicationContext(), "");
        String languageToLoad = SessionUtil.getlangCode(this);
        Localization.setLocale(this, languageToLoad);

        constraintLayout = findViewById(R.id.mainlayout);
        SessionUtil.setNumberCode(this, "0");
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mTextViewWelcomingMessage = findViewById(R.id.textViewWelcome);

        //set status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        //bottom Navigation
        navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        frameLayout = (FrameLayout) findViewById(R.id.navigation_container);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        //setLanguageToWidgets(resources);

        mSnackBarReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isSnackBarReciever = true;
                if (intent != null) {
                    if (intent.hasExtra("message")
                            && intent.hasExtra("progress")) {
                        String message = intent.getStringExtra("message");
                        int progress = intent.getIntExtra("progress", 0);
                        if (message.matches("initSnackbar")) {
                            initSnackBarForProfileUpdate();
                        } else if (message.matches("progress")) {
                            setSnackBarProgress(intent.getIntExtra("progress", 0));
                        } else if (message.matches("Uploaded Successfully")) {
                            setSnackBarProgress(100);
                            if (snackbar != null) {
                                snackbar.dismiss();
                            }
                        } else {
                            if (snackbar != null) {
                                snackbar.dismiss();
                            }

                        }
                    }
                }
            }
        };
        /*Fragment fragment = null;
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("coming_from") && getIntent().getStringExtra("coming_from").matches("settings")) {
                fragment = new SettingFragment();
            }else{
                fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                if (getIntent().hasExtra("show_success_dialog")) {
                    bundle.putBoolean("show_success_dialog", true);
                    fragment.setArguments(bundle);
                }
            }
        }else{
            fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,fragment,FRAGMENT_TAG).commit();
*/
        if (getIntent().hasExtra("coming_from") && getIntent().getStringExtra("coming_from").matches("settings")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,
                    new SettingFragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_setting);
        } else {
            DashboardFragment dashboardFragment = new DashboardFragment();
            Bundle bundle = new Bundle();
            if (getIntent().hasExtra("show_success_dialog")) {
                bundle.putBoolean("show_success_dialog", true);
                if (getIntent().hasExtra("subscription_changed")) {
                    bundle.putBoolean("subscription_changed", true);
                }
                dashboardFragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction().add(R.id.navigation_container,
                    dashboardFragment).commit();
        }
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "-----------------Cedric Dashboard: Bottom Navigation bar----------------");
        }
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.navigation_plans_Home:

                        if (item.getItemId() != id) {
                            clickCounter = 0;
                        }
                        clickCounter = clickCounter + 1;
                        if (clickCounter < 2) {
                            if (timeOfTap == 0 || (((System.currentTimeMillis() - timeOfTap)) > 500)) {
                                timeOfTap = System.currentTimeMillis();
                                id = R.id.navigation_plans_Home;
                                fragment = new DashboardFragment();
                                loadFragment(fragment);
                                return true;
                            }
                            timeOfTap = System.currentTimeMillis();
                            return false;

                        } else {
                            timeOfTap = System.currentTimeMillis();
                            return false;
                        }
                            /*} else {
                                Toast.makeText(getApplicationContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                            }*/

                    case R.id.navigation_programs:
                        // if (ConnectionDetector.isConnectedWithInternet(HomeActivity.this)) {
                        /*if (timeOfTap == 0) {
                            timeOfTap = System.currentTimeMillis();
                        }*/

                        if (item.getItemId() != id) {
                            clickCounter = 0;
                        }
                        clickCounter = clickCounter + 1;
                        if (clickCounter < 2) {
                            if (timeOfTap == 0 || (((System.currentTimeMillis() - timeOfTap)) > 500)) {
                                timeOfTap = System.currentTimeMillis();
                                //ToDo needs to be
                                id = R.id.navigation_programs;
                                    /*Toast.makeText(HomeActivity.this,
                                            "This feature is comming soon..!", Toast.LENGTH_SHORT).show();*/
                                fragment = new ProgramsFragment();
                                loadFragment(fragment);
                                return true;
                            }
                            timeOfTap = System.currentTimeMillis();
                            return false;
                        } else {
                            timeOfTap = System.currentTimeMillis();
                            return false;
                        }
                        //  break;
                           /* } else {
                                Toast.makeText(getApplicationContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                            }*/

                    case R.id.navigation_explore:
                        if (ConnectionDetector.isConnectedWithInternet(HomeActivity.this)) {
                            if (item.getItemId() != id) {
                                clickCounter = 0;
                            }
                            /*if (timeOfTap == 0) {
                                timeOfTap = System.currentTimeMillis();
                            }*/
                            clickCounter = clickCounter + 1;
                            if (clickCounter < 2) {
                                if (timeOfTap == 0 || (((System.currentTimeMillis() - timeOfTap)) > 500)) {
                                    timeOfTap = System.currentTimeMillis();
                                    id = R.id.navigation_explore;
                                    fragment = new ExploreFragment();
                                    /*  loadFragment(fragment);*/
                                    Toast.makeText(HomeActivity.this,
                                            R.string.feature_common_soon, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                timeOfTap = System.currentTimeMillis();
                                return false;
                            } else {
                                timeOfTap = System.currentTimeMillis();
                                return false;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_your_internet), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.navigation_shopping:
                        // if (ConnectionDetector.isConnectedWithInternet(HomeActivity.this)) {
                        if (item.getItemId() != id) {
                            clickCounter = 0;
                        }
                       /* if (timeOfTap == 0) {
                            timeOfTap = System.currentTimeMillis();
                        }*/
                        clickCounter = clickCounter + 1;
                        if (clickCounter < 2) {
                            if (timeOfTap == 0 || (((System.currentTimeMillis() - timeOfTap)) > 500)) {
                                timeOfTap = System.currentTimeMillis();
                                id = R.id.navigation_shopping;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    getNutritionTime();
                                    String day = LocalDate.now().getDayOfWeek().name().toLowerCase();
                                    SharedData.today = day.substring(0, 1).toUpperCase() + day.substring(1);
                                    // today = "Monday";
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "Today: " + SharedData.today);
                                    }
                                    fragment = new ShoppingFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("nutritionTime", nutritionTime);
                                    bundle.putString("day", SharedData.today);
                                    fragment.setArguments(bundle);
                                    loadFragment(fragment);
                                } else {
                                    getNutritionTime();
                                    //  String today = "Monday";

                                    fragment = new ShoppingFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("nutritionTime", nutritionTime);
                                    bundle.putString("day", SharedData.today);
                                    fragment.setArguments(bundle);
                                    loadFragment(fragment);
                                }
                                return true;
                            }
                            timeOfTap = System.currentTimeMillis();
                            return false;
                        } else {
                            timeOfTap = System.currentTimeMillis();
                            return false;
                        }
                            /*} else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.turn_on_your_internet), Toast.LENGTH_SHORT).show();
                            }*/
                        //break;
                    case R.id.navigation_setting:
                        if (ConnectionDetector.isConnectedWithInternet(HomeActivity.this)) {
                            if (item.getItemId() != id) {
                                clickCounter = 0;
                            }
                            /*if (timeOfTap == 0) {
                                timeOfTap = System.currentTimeMillis();
                            }*/
                            clickCounter = clickCounter + 1;
                            if (clickCounter < 2) {
                                if (timeOfTap == 0 || (((System.currentTimeMillis() - timeOfTap)) > 500)) {
                                    timeOfTap = System.currentTimeMillis();
                                    id = R.id.navigation_setting;
                                    fragment = new SettingFragment();
                                    loadFragment(fragment);
                                    return true;
                                }
                                timeOfTap = System.currentTimeMillis();
                                return false;
                            } else {
                                timeOfTap = System.currentTimeMillis();
                                return false;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_your_internet), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;

                }
                return false;
            }

        });
        // }
        /*if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(HomeActivity.this))
            checkGPSAndStart();*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        //  Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                        try {
                            getSupportFragmentManager().popBackStack();
                            if (pressedTime + 2000 > System.currentTimeMillis()) {
                                //  Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            } else {
                                //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.press_back), Toast.LENGTH_SHORT).show();
                            }
                            pressedTime = System.currentTimeMillis();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                            Log.d("exception", e.toString());
                        }
                    } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        //Toast.makeText(this,"In second",Toast.LENGTH_SHORT).show();
                        frameLayout.removeAllViewsInLayout();
                        try {
                            //Toast.makeText(this,"In second",Toast.LENGTH_SHORT).show();
                            getSupportFragmentManager().popBackStack();
                        } catch (Exception e) {
                            // Toast.makeText(this,"In second",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            Log.d("exception", e.toString());
                        }

                    } else {
                        //Toast.makeText(this,"In third",Toast.LENGTH_SHORT).show();
                        // frameLayout.removeAllViewsInLayout();
                        onBackPressed();

                    }

                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/



       /* if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            new UserStatusUtil(getApplicationContext(), HomeActivity.this, resources).getUserStatus("Bearer " + SharedData.token);
        }*/
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    showBottomSheetDialof();
                } else {
                    Toast.makeText(getApplicationContext(), getText(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void showBottomSheetDialof() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.modal_bottom_sheet_webview);
        bottomSheetDialog.setCancelable(true);
        WebView chatBotWebView = bottomSheetDialog.findViewById(R.id.chatBotWebView);
        chatBotWebView.clearCache(true);
        chatBotWebView.setNestedScrollingEnabled(true);
        // chatBotWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = chatBotWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true);
        }
        //webSettings.setMediaPlaybackRequiresUserGesture(false);
        chatBotWebView.loadUrl("http://3.110.168.73/");
        bottomSheetDialog.show();

        //bottomSheetBehavior.setHideable(false);
        /*bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    if(!chatBotWebView.canScrollVertically(1)) {
                        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });*/
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            try {
                //Toast.makeText(getApplicationContext(),"On Back pressed",Toast.LENGTH_SHORT).show();
                if (SharedData.redirectToDashboard) {
                    //doubleBackToExitPressedOnce = false;
                    navigation.setSelectedItemId(R.id.navigation_plans_Home);
                    SharedData.redirectToDashboard = false;
                    loadFragment(new DashboardFragment());
                } else {
                    if (SharedData.isDashboardVisible) {
                        if (pressedTime + 2000 > System.currentTimeMillis()) {
                            //Toast.makeText(getApplicationContext(),"In First",Toast.LENGTH_SHORT).show();
                    /*super.onBackPressed();
                    finish();*/
                            SharedData.isDashboardVisible = false;
                            popBackStackTillEntry(0);
                            moveTaskToBack(true);
                            System.exit(0);
                            return;
                        } else {
                            //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.press_back),
                                    Toast.LENGTH_SHORT).show();
                        }
                        pressedTime = System.currentTimeMillis();
                    } else {
                        getSupportFragmentManager().popBackStack();
                    }
                }

            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }


        }
    };

    void setLanguageToWidgets(Resources resources) {
        navigation.getMenu().findItem(R.id.navigation_plans_Home).setTitle(resources.getString(R.string.nav_home));
        navigation.getMenu().findItem(R.id.navigation_programs).setTitle(resources.getString(R.string.nav_programs));
        navigation.getMenu().findItem(R.id.navigation_explore).setTitle(resources.getString(R.string.nav_explore));
        navigation.getMenu().findItem(R.id.navigation_shopping).setTitle(resources.getString(R.string.nav_shopping_list));
        navigation.getMenu().findItem(R.id.navigation_setting).setTitle(resources.getString(R.string.nav_setting));
    }

    void checkGPSAndStart() {
        try {
            //if (SessionUtil.getLocationPermissionBackground(getApplicationContext())) {

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DialogUtil.showSettingsAlert(HomeActivity.this);
            } else {
                startLocationService();

            }

        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
        // }
    }

    public static void hideBottomNav() {
        navigation.setVisibility(View.GONE);
    }

    public static void showBottomNav() {
        navigation.setVisibility(View.VISIBLE);
    }

    private void getNutritionTime() {
        SharedData.setWelcomeMessage(mTextViewWelcomingMessage, resources);
        String dayTime = mTextViewWelcomingMessage.getText().toString();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Bottom Navigation Bar-daytime: " + dayTime);
        }
        if (dayTime.equals(resources.getString(R.string.good_afternoon))) {
            mTextViewWelcomingMessage.setText(getString(R.string.breakfast));
            nutritionTime = mTextViewWelcomingMessage.getText().toString();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Bottom Navigation Bar-nutritionTime: " + nutritionTime);
            }
        } else if (dayTime.equals(resources.getString(R.string.good_morning))) {
            mTextViewWelcomingMessage.setText(getString(R.string.breakfast));
            nutritionTime = mTextViewWelcomingMessage.getText().toString();
            //remove this line if data comes from Api fully
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Bottom Navigation Bar-nutritionTime: " + nutritionTime);
            }
        } else if (dayTime.equals(resources.getString(R.string.good_evening))) {
            mTextViewWelcomingMessage.setText(getString(R.string.breakfast));
            nutritionTime = mTextViewWelcomingMessage.getText().toString();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Bottom Navigation Bar-nutritionTime: " + nutritionTime);
            }
        } else {
            mTextViewWelcomingMessage.setText(resources.getString(R.string.breakfast));
            nutritionTime = mTextViewWelcomingMessage.getText().toString();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Bottom Navigation Bar-nutritionTime: " + nutritionTime);
            }
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation_container, fragment);
        // transaction.addToBackStack(null);
        transaction.commit();
    }


    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            String numberCode = SessionUtil.getNumberCode(getApplicationContext());
            if (currentNetworkInfo.isConnected() && (numberCode.matches("1"))) {
                Snackbar snackbar = Snackbar.make(constraintLayout, resources.getString(R.string.back_online), Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(navigation);
                snackbar.show();
            } else if (!currentNetworkInfo.isConnected()) {
                Snackbar snackbar = Snackbar.make(constraintLayout, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(navigation);
                snackbar.show();
                try {
                    if (StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, getApplicationContext())) {
                        DashboardFragment.stopDataSyncService(getApplicationContext());
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }
            SessionUtil.setNumberCode(getApplicationContext(), "1");
        }
    };

    @Override
    public void onBackPressed() {
        try {
            if (SharedData.redirectToDashboard) {
                //doubleBackToExitPressedOnce = false;
                navigation.setSelectedItemId(R.id.navigation_plans_Home);
                SharedData.redirectToDashboard = false;
                loadFragment(new DashboardFragment());
            } else {
                if (SharedData.isDashboardVisible) {
                    if (pressedTime + 2000 > System.currentTimeMillis()) {
                        //Toast.makeText(getApplicationContext(),"In First",Toast.LENGTH_SHORT).show();
                    /*super.onBackPressed();
                    finish();*/
                        SharedData.isDashboardVisible = false;
                        popBackStackTillEntry(0);
                        moveTaskToBack(true);
                        System.exit(0);
                        return;
                    } else {
                        //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.press_back),
                                Toast.LENGTH_SHORT).show();
                    }
                    pressedTime = System.currentTimeMillis();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }

        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

        /* if (doubleBackToExitPressedOnce) {
            popBackStackTillEntry(0);
            moveTaskToBack(true);
            System.exit(0);
            return;

        } else {
            if (SharedData.canToastShow && navigation.getSelectedItemId() == R.id.navigation_plans_Home) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.press_back),
                        Toast.LENGTH_SHORT).show();
            }
        }
        doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }*/
        /*if (pressedTime + 2000 > System.currentTimeMillis()) {
            //Toast.makeText(getApplicationContext(),"In First",Toast.LENGTH_SHORT).show();
            // onBackPressed();
            //finish();
        } else {
            //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
            Toast.makeText(getBaseContext(), getResources().getString(R.string.press_back),
                    Toast.LENGTH_SHORT).show();
        }*/
        //pressedTime = System.currentTimeMillis();
       /* if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            //  Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
            try {
                getSupportFragmentManager().popBackStack();
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    Toast.makeText(this, "In First", Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                    // finish();
                } else {
                    //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.press_back),
                            Toast.LENGTH_SHORT).show();
                }
                pressedTime = System.currentTimeMillis();
            } catch (Exception e) {
                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                    //Toast.makeText(this,"In First",Toast.LENGTH_SHORT).show();
                    Log.d("exception", e.toString());
                }
            }
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (Common.isLoggingEnabled) {
                Toast.makeText(this, "In second", Toast.LENGTH_SHORT).show();
            }
            frameLayout.removeAllViewsInLayout();
            try {
                //Toast.makeText(this,"In second",Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                if (Common.isLoggingEnabled) {
                    Toast.makeText(this, "In second", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
            }

        } else {
            Toast.makeText(this, "In third", Toast.LENGTH_SHORT).show();
            //frameLayout.removeAllViewsInLayout();
            super.onBackPressed();
        }*/
    }


    void setAPIEnvironment() {
        if (SessionUtil.getAPI_URL(getApplicationContext()).matches("")) {
            String environment = SessionUtil.getAPP_Environment(HomeActivity.this);
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
        /*if (SessionUtil.isStaging(HomeActivity.this)) {
            SharedData.BASE_URL = Common.STAGING_BASE_URL;
        } else {
            SharedData.BASE_URL = Common.TESTING_BASE_URL;
        }*/
    }

    public void popBackStackTillEntry(int entryIndex) {

        if (getSupportFragmentManager() == null) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() <= entryIndex) {
            return;
        }
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                entryIndex);
        if (entry != null) {
            getSupportFragmentManager().popBackStackImmediate(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }

    private void initSnackBarForProfileUpdate() {

        snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);

// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

// Inflate a linear layout
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

// Inflate the text view
        String language = Localization.getLang(getApplicationContext());

        TextView textView = new TextView(getApplicationContext());
        String savedLanguage = SessionUtil.getlangCode(this);
        if (!savedLanguage.matches("")) {
            //resources = Localization.setLocale(this, savedLanguage).getResources();
            textView.setText(resources.getString(R.string.uploading));
        } else {
            if (language.matches("sv")) {
                //resources = Localization.setLocale(this, "sv").getResources();
                textView.setText(resources.getString(R.string.uploading));
            } else {
                //resources = Localization.setLocale(this, "en").getResources();
                textView.setText(resources.getString(R.string.uploading));
            }

        }
        textView.setTextSize(13);


        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

// Inflate the progress bar
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
// Set the height of the progress bar
        LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(5)
        );
        progressBar.setIndeterminate(false);
        progressBar.setLayoutParams(progressBarParams);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.progress_bar_color);
        progressBar.setProgressDrawable(drawable);

// Add the text view and progress bar to the linear layout
        linearLayout.addView(textView);
        linearLayout.addView(progressBar);

// Add the linear layout to the snackbar's layout view
        layout.addView(linearLayout, 0);
        progressBar.setProgress(0);

// Show the snackbar
        snackbar.show();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    void setSnackBarProgress(int progressBarProgress) {
        if (progressBar != null) {
            progressBar.setProgress(progressBarProgress);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());

        super.onSaveInstanceState(outState);
    }

    void onStartStuff() {
        //showStepsCounterView();
        /*checkNotificationPermission();
        checkStepCounterPermission();*/
        if (GooglePlayServiceUtil.isGooglePlayServicesAvailable(getApplicationContext()))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    activityDataSync();
                } else {
                    //PermissionUtil.checkNotificationPermission(getApplicationContext(), HomeActivity.this, HomeActivity.this);
                    if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                        List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(getApplicationContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                        if (list.size() == 0) {
                            if (!SessionUtil.getActivityDownloadedDate(getApplicationContext()).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                                new UserActivity(getApplicationContext(), this).downloadUserActivity(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                            }
                        }
                    }
                }
            } else {
                activityDataSync();
            }

    }

    void activityDataSync() {

        List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(getApplicationContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "User activity in db: " + list.toString());
        }
        if (list.size() > 0) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Activity list is greater than zero in DB");
            }
            if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, getApplicationContext())) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In Dashboard fragment, uploading activity");
                }
                startSyncDataService("upload", "home");
                    /*startBroadCastingForSensor();
                    showStepsCounterView();*/

            }/*else{
                    stopDataSyncService(mContext);
                }*/
            //checkLocationPermission();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Activity list is zero in DB");
            }
            if (!SessionUtil.getActivityDownloadedDate(getApplicationContext()).equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In Dashboard fragment, actiivty downloaded date not available in shared preference so, acitivty is going to upload");
                }
                if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, getApplicationContext())) {
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
                //showStepsCounterView();
                //PermissionUtil.checkLocationPermission(getApplicationContext(), HomeActivity.this, HomeActivity.this);
            }
            //checkLocationPermission();
        }

    }

    void startSyncDataService(String requestFor, String requestFrom) {
        Intent dataSyncServiceIntent = new Intent(getApplicationContext(), StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Start Data Sync Service. Request for: " + requestFor + " and Request from: " + requestFrom);
        }
        startService(dataSyncServiceIntent);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(dataSyncServiceIntent);
        } else {
            mContext.startService(dataSyncServiceIntent);
        }*/
    }

    @Override
    public void userActivitySync() {

    }
}