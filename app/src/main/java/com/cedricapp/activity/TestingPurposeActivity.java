package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.common.Common.currentUser;
import static com.cedricapp.activity.LoginActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.LogoutInterface;
import com.cedricapp.interfaces.UserDetailsListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.R;
import com.cedricapp.service.StepsService;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.service.StepCounterDataSync;

import java.util.List;

public class TestingPurposeActivity extends AppCompatActivity implements UserDetailsListener, LogoutInterface {
    SwitchCompat environmentSwitch;
    RadioGroup apiGroup;
    RadioButton testing, S_testing_beta, stagging, beta, production;
    SharedPreferences sharedPreferences, sharedPreferences2;
    private final static String SHARED_PREF_NAME2 = "log_user_info";
    String lang;
    private DBHelper dbHelper;
    boolean isStaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_purpose);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(syncActivityBR,
                new IntentFilter(getPackageName() + ".SYNC_ACTIVITY_SERVICE"));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(syncActivityBR);
        super.onPause();
    }

    private final BroadcastReceiver syncActivityBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String requestFor = "";
                if (intent.hasExtra("requestFor")) {
                    requestFor = intent.getStringExtra("requestFor");
                    if (requestFor.matches("logout")) {
                        //redirectToLogin();
                        LogoutUtil.performLogout(SessionUtil.getAccessToken(getApplicationContext()), TestingPurposeActivity.this);
                        // new UserDetailsUtil(getApplicationContext()).updateUserDetails("Bearer " + SharedData.token, SharedData.location, WeekDaysHelper.getUTC_Time(), TestingPurposeActivity.this);
                    }
                }

            } catch (Exception ex) {
                if (getApplicationContext() != null) {
                    new LogsHandlersUtils(getApplicationContext()).getLogsDetails("TestingPurposeActivity_BroadcastReceiver",
                            SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.caughtException(ex));
                }
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("ObsoleteSdkInt")
    void init() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        dbHelper = new DBHelper(getApplicationContext());
        lang = SessionUtil.getlangCode(getApplicationContext());
        /*environmentSwitch = findViewById(R.id.environmentSwitch);*/
        apiGroup = findViewById(R.id.S_apiGroup);
        testing = findViewById(R.id.S_testing);
        S_testing_beta = findViewById(R.id.S_testing_beta);
        stagging = findViewById(R.id.S_staging);
        beta = findViewById(R.id.S_beta);
        production = findViewById(R.id.S_production);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        sharedPreferences2 = getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        checkEnvironment();
        String environment = SessionUtil.getAPP_Environment(TestingPurposeActivity.this);
        if (environment.matches("testing")) {
            testing.setChecked(true);
        } else if (environment.matches("stagging")) {
            stagging.setChecked(true);
        } else if (environment.matches("beta")) {
            beta.setChecked(true);
        }else if (environment.matches("testing_beta")) {
            S_testing_beta.setChecked(true);
        } else {
            production.setChecked(true);
        }
        apiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.S_testing:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("testing");
                        }
                        break;
                    case R.id.S_testing_beta:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("testing_beta");
                        }
                        break;
                    case R.id.S_staging:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("stagging");
                        }
                        break;
                    case R.id.S_beta:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("beta");
                        }
                        break;
                    case R.id.S_production:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("production");
                        }
                        break;


                }
            }
        });

    }

    void checkEnvironment() {
        String environment = SessionUtil.getAPP_Environment(TestingPurposeActivity.this);

        if (environment.matches("testing")) {
            SharedData.BASE_URL = Common.TESTING_BASE_URL;
            if (testing != null) {
                testing.setChecked(true);
            }
            SessionUtil.saveAPI_URL(getApplicationContext(),"");
            /* environmentSwitch.setChecked(true);*/
        } else if (environment.matches("stagging")) {
            SharedData.BASE_URL = Common.STAGING_BASE_URL;
            if (stagging != null) {
                stagging.setChecked(true);
            }
            SessionUtil.saveAPI_URL(getApplicationContext(),"");
            //  Toast.makeText(this, "beta is under development", Toast.LENGTH_SHORT).show();
        }
        else if (environment.matches("beta")) {
            if (beta != null) {
                beta.setChecked(true);
            }
            SharedData.BASE_URL = Common.BETA_BASE_URL;
            SessionUtil.saveAPI_URL(getApplicationContext(),"");
            /* environmentSwitch.setChecked(false);*/
            //  Toast.makeText(this, "beta is under development", Toast.LENGTH_SHORT).show();
        }else if (environment.matches("testing_beta")) {
            if (S_testing_beta != null) {
                S_testing_beta.setChecked(true);
            }
            SharedData.BASE_URL = Common.TESTING_BETA_BASE_URL;
            SessionUtil.saveAPI_URL(getApplicationContext(),"");
            /* environmentSwitch.setChecked(false);*/
            //  Toast.makeText(this, "beta is under development", Toast.LENGTH_SHORT).show();
        }else {
            if (production != null) {
                production.setChecked(true);
            }
            SharedData.BASE_URL = Common.PRODUCTION_BASE_URL;
            SessionUtil.saveAPI_URL(getApplicationContext(),"");
            /* environmentSwitch.setChecked(false);*/
        }

        //  switchListener();
    }


    void switchListener() {
        apiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.S_testing:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("testing");
                        }
                        break;
                    case R.id.S_staging:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("stagging");
                        }
                        break;
                    case R.id.S_beta:
                        if (SessionUtil.logout(TestingPurposeActivity.this)) {
                            //redirectToLogin();
                            setApiEnv("beta");
                        }
                        break;


                }
            }
        });
        /*environmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isStaging = true;
                    if (SessionUtil.logout(TestingPurposeActivity.this)) {
                        //redirectToLogin();
                        activityDataSync();
                    }
                } else {
                    isStaging = false;
                    if (SessionUtil.logout(TestingPurposeActivity.this)) {
                        //redirectToLogin();
                        activityDataSync();
                    }
                }
            }
        });*/
    }

    private void setApiEnv(String apiEnv) {
        activityDataSync();
        SessionUtil.saveAPI_Environment(getApplicationContext(), apiEnv);
    }

  /*  void redirectToLogin() {
        Intent intent = new Intent(TestingPurposeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();

        Toast toast = Toast.makeText(getApplicationContext(), "Successfully Logged Out!", Toast.LENGTH_LONG);
        // toast.getView().setBackgroundResource(R.color.yellow);
        toast.show();

        startActivity(intent);
    }*/

    void redirectToLogin() {

        sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        SessionUtil.setlangCode(getApplicationContext(), "");

        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.clear();
        editor2.apply();
        SessionUtil.setSensorStaticSteps(getApplicationContext(), 0);
        SessionUtil.setUserLogInSteps(getApplicationContext(), 0);
        SessionUtil.setUsertodaySteps(getApplicationContext(), 0);
        SessionUtil.setLoggedIn(getApplicationContext(), false);
        SessionUtil.SetFoodPreferenceID(getApplicationContext(), "");
        dbHelper.logout();
        dbHelper.deleteUser(String.valueOf(currentUser));
        if (StepCountServiceUtil.isMyServiceRunning(StepsService.class, getApplicationContext()))
            StepCountServiceUtil.stopStepCountService(getApplicationContext());
        //dbHelper.deleteAllShoppingListData();
        //  SessionUtil.saveAPI_Environment(getApplicationContext(), " "/*isStaging*/);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Toast toast = Toast.makeText(getApplicationContext(), "Successfully Logged Out!", Toast.LENGTH_LONG);
        toast.show();
        finish();
        startActivity(intent);
    }

    void activityDataSync() {
        if (getApplicationContext() != null) {
            if (dbHelper == null)
                dbHelper = new DBHelper(getApplicationContext());
            List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ByAPI_SyncedAt(SessionUtil.getUserID(getApplicationContext()));
            if (list.size() > 0) {
                if (!StepCountServiceUtil.isMyServiceRunning(StepCounterDataSync.class, getApplicationContext())) {
                    startSyncDataService("upload", "logout");
                }
            } else {

                //List<StepCountModel.Data> list = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(getContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                //redirectToLogin();
                LogoutUtil.performLogout(SessionUtil.getAccessToken(getApplicationContext()), TestingPurposeActivity.this);
                //new UserDetailsUtil(getApplicationContext()).updateUserDetails("Bearer " + SharedData.token, SharedData.location, WeekDaysHelper.getUTC_Time(), TestingPurposeActivity.this);
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "getContext is null in activityDataSync method");
            }
        }
    }

    void startSyncDataService(String requestFor, String requestFrom) {
        Intent dataSyncServiceIntent = new Intent(getApplicationContext(), StepCounterDataSync.class);
        dataSyncServiceIntent.putExtra("requestFor", requestFor);
        dataSyncServiceIntent.putExtra("requestFrom", requestFrom);
        startService(dataSyncServiceIntent);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(dataSyncServiceIntent);
        } else {
            startService(dataSyncServiceIntent);
        }*/
    }

    @Override
    public void response(boolean isSuccessful, String message) {
        if (isSuccessful) {
            LogoutUtil.performLogout(SessionUtil.getAccessToken(getApplicationContext()), TestingPurposeActivity.this);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void responseCode(int responseCode) {

    }

    @Override
    public void responseError(Throwable t) {

    }

    @Override
    public void isLogout(boolean isLoggedOut) {
        if (isLoggedOut) {
            redirectToLogin();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void logoutResponse(String message) {

    }

    @Override
    public void logoutReponseCode(int responseCode) {

    }

    @Override
    public void logoutError(Throwable t) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
    }
}