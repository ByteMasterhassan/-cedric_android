package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.LevelListener;
import com.cedricapp.interfaces.PlanListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.GoalModel;
import com.cedricapp.model.LevelModel;
import com.cedricapp.model.UpdateLanguage;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.CommonAPIUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import worker8.com.github.radiogroupplus.RadioGroupPlus;


@SuppressWarnings({"ALL", "deprecation"})
public class LanguageFragment extends Fragment implements PlanListener, LevelListener {
    RadioGroupPlus radioGroupPlus;
    MaterialTextView languagetxt;
    TextView txt_english, txt_swedish;
    ImageView backArrow;
    RadioButton english_btn, swedish_btn;
    View view;
    LocaleListCompat appLocale;
    LinearLayout EnglsihLL, SwedishLL;
    String token;

    String checkRadiobtn;
    boolean backPress = true;
    private String message;
    DBHelper dbHelper;

    BlurView blurView;
    LottieAnimationView loading_lav;

    Resources resources;

    String TAG = "LANGUAGE_FRAGMENT_TAG";

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
        if(Common.isLoggingEnabled){
            Log.d(TAG,"LANGUAGE_ON_RESUME");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Common.isLoggingEnabled){
            Log.d(TAG,"LANGUAGE_ON_CREATE");
        }


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (backPress) {
                    onBackPress();
                } else {
                    //Toast.makeText(getContext(), "wait little while", Toast.LENGTH_SHORT).show();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(Common.isLoggingEnabled){
            Log.d(TAG,"LANGUAGE_ON_CREATE_VIEW");
        }

        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_language, container, false);
        loading_lav = view.findViewById(R.id.loading_lav);
        blurView = view.findViewById(R.id.blurView);
        radioGroupPlus = view.findViewById(R.id.radio_GroupPlus);
        languagetxt = view.findViewById(R.id.title);
        txt_english = view.findViewById(R.id.txt_english);
        txt_swedish = view.findViewById(R.id.swedishtxt);
        backArrow = view.findViewById(R.id.backArrow);
        EnglsihLL = view.findViewById(R.id.englishLL);
        SwedishLL = view.findViewById(R.id.swedishLL);
        english_btn = view.findViewById(R.id.english_button);
        swedish_btn = view.findViewById(R.id.swedish_button);
        /*if (!lang.matches("")) {
            if (lang.matches("sv")) {
                Resources resources = Localization.setLocale(getContext(), "sv").getResources();
                setlanguageToWidget(resources);
            } else {
                Resources resources = Localization.setLocale(getContext(), "en").getResources();
                setlanguageToWidget(resources);
            }
        } else {
            Resources resources = Localization.setLocale(getContext(), "en").getResources();
            setlanguageToWidget(resources);
        }*/

        setlanguageToWidget();

        token = SessionUtil.getAccessToken(getContext());
        checkRadiobtn = SessionUtil.getlangCode(getContext());
        dbHelper = new DBHelper(getContext());
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "lannguage1" + checkRadiobtn);
        }


        if (checkRadiobtn.matches("en")) {
            english_btn.setChecked(true);
        } else if (checkRadiobtn.matches("sv")) {
            swedish_btn.setChecked(true);
        } else {
            Locale current = getResources().getConfiguration().locale;
            String language = current.getLanguage();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "lannguage" + language);
            }
            if (language.matches("sv")) {
                swedish_btn.setChecked(true);
                // SessionUtil.setlangCode(getContext(),"sv");
            } else {
                english_btn.setChecked(true);
                // SessionUtil.setlangCode(getContext(),"en");
            }

        }

        EnglsihLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                english_btn.setChecked(true);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "EnglishLL pressed");
                }
                updateLanguage(token, "en");
                backArrow.setEnabled(false);
                backPress = false;
            }
        });

        SwedishLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swedish_btn.setChecked(true);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "SwedishLL pressed");
                }
                updateLanguage(token, "sv");
                backArrow.setEnabled(false);
                backPress = false;
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        radioGroupPlus.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroupPlus group, int checkedId) {
                switch (checkedId) {
                    case R.id.english_button:
                        // updateLanguage(token, "en");
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "English Radio pressed");
                        }
                        backArrow.setEnabled(false);
                        backPress = false;
                        break;
                    case R.id.swedish_button:
                        //updateLanguage(token, "sv");
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Swedish radio pressed");
                        }
                        backArrow.setEnabled(false);
                        backPress = false;
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    private void setlanguageToWidget() {
        languagetxt.setText(resources.getString(R.string.Fragment_language_title));
        txt_english.setText(resources.getString(R.string.english_language));
        txt_swedish.setText(resources.getString(R.string.swedish_language));
    }

    private void onBackPress() {

        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                if (isAdded()) {
                    getFragmentManager().popBackStack();
                }
            }
        } else {
            Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLanguage(String token, String language) {
        StartLoading();
        blurrBackground();
        Call<UpdateLanguage> call = ApiClient.getService()
                .changeLanguage("Bearer " + token, language);
        call.enqueue(new Callback<UpdateLanguage>() {
            @Override
            public void onResponse(Call<UpdateLanguage> call, Response<UpdateLanguage> response) {
                if (response.isSuccessful()) {
                    try {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        UpdateLanguage updateLanguage = response.body();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Update Language Model: " + updateLanguage.toString());
                        }
                        if (isAdded() && getContext() != null) {
                            if (updateLanguage != null && updateLanguage.getMessage() != null)
                                Toast.makeText(getContext(), updateLanguage.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (getContext() != null) {
                            SessionUtil.setlangCode(getContext(), language);
                            Localization.setLanguage(getContext(),getResources());
                            SessionUtil.setReloadData(getContext(), true);
                            SessionUtil.setDashboardReloadData(getContext(), true);
                        }
                        dbHelper.clearDB();
                        backArrow.setEnabled(true);
                        backPress = true;
                        if (getActivity() != null && getActivity().getBaseContext() != null) {
                            SessionUtil.setLoadHomeData(true, getActivity().getBaseContext());
                        }
                        if (getContext() != null) {
                            CommonAPIUtil.getPlans(token, LanguageFragment.this);

                        }

                    } catch (Exception ex) {

                        StopLoading();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Loader stopped when exception occurred");
                            ex.printStackTrace();
                            Log.e(TAG, ex.getMessage());
                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Update lang on unsuccessful");
                    }
                    Log.e(TAG, "Loader stopped when successfull false");
                    StopLoading();
                    Gson gson = new GsonBuilder().create();
                    UpdateLanguage updateLanguage = new UpdateLanguage();
                    try {
                        if (response.errorBody() != null) {
                            updateLanguage = gson.fromJson(response.errorBody().string(), UpdateLanguage.class);
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext())
                                    .getLogsDetails("language_fragment_update_language_api", SessionUtil.getUserEmailFromSession(getContext())
                                            , EXCEPTION, SharedData.caughtException(e));
                        }
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (response.code() == 400) {
                            if (getContext() != null) {
                                if (updateLanguage != null && updateLanguage.getMessage() != null) {
                                    Toast.makeText(getContext(), updateLanguage.getMessage().toString(),
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
                    } catch (Exception ex) {
                        if (Common.isLoggingEnabled)
                            ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateLanguage> call, Throwable t) {
                if (Common.isLoggingEnabled)
                    Log.e(TAG, "Loader stopped when language on Failure");
                StopLoading();
                try {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext())
                                .getLogsDetails("language_update_response", SessionUtil.getUserEmailFromSession(getContext())
                                        , EXCEPTION, SharedData.throwableObject(t));
                    }
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                        Log.e(TAG, t.getMessage());
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled)
                        ex.printStackTrace();
                }
            }
        });

    }

    public void StartLoading() {
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    private void StopLoading() {
        try {
            blurView.setVisibility(View.INVISIBLE);
            blurView.setVisibility(View.GONE);
            //Enable user interaction
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Stop loader");
            }

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
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }

    }

    private void blurrBackground() {
        try {
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

                    blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurAlgorithm(new RenderScriptBlur(requireContext())).setBlurRadius(radius).setBlurAutoUpdate(true).setHasFixedTransformationMatrix(false);
                }
            }
        }catch (Exception er){
            if(Common.isLoggingEnabled)
                er.printStackTrace();
        }

    }

    @Override
    public void levelOnSuccess(LevelModel levelModel) {
        try {
            if (levelModel != null && levelModel.getData() != null && levelModel.getData().size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Level model in Language Model: " + levelModel.toString());
                }
                if (getContext() != null) {
                    String lang = SessionUtil.getlangCode(getContext());
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Lang code in level: " + lang);
                    }
                    for (int i = 0; i < levelModel.getData().size(); i++) {
                        String levelID = SessionUtil.getUserLevelID(getContext());
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Level ID in shared preference is " + levelID);
                        }
                        if (levelID.equals(String.valueOf(levelModel.getData().get(i).getId()))) {

                            if (lang.matches("sv")) {
                                SessionUtil.setUserLevel(getContext(), levelModel.getData().get(i).getNameSv());
                            } else {
                                SessionUtil.setUserLevel(getContext(), levelModel.getData().get(i).getName());
                            }
                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "level getContext is null");
                    }
                    StopLoading();
                }
                if (getActivity() != null) {
                    /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,
                            new DashboardFragment()).commit();*/
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    if (Common.isLoggingEnabled)
                        Log.e(TAG, "Loader stopped when redirecing to Bottom Navigation Bar");
                    StopLoading();
                    startActivity(intent);
                    /*Intent intent = new Intent(getContext(), HomeActivity.class);
                    if (Common.isLoggingEnabled)
                        Log.e(TAG, "Loader stopped when redirecing to Bottom Navigation Bar");
                    StopLoading();
                    startActivity(intent);
                    if (getActivity() != null)
                        getActivity().finish();*/
                    /*if(Common.isLoggingEnabled){
                        Log.d(TAG,"CALL_DASHBOARD_FRAGEMENT_FROM_LANGUAGE");
                    }*/

                       /* getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,
                                new DashboardFragment()).commit();*/
                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StopLoading();
                            *//*getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,
                                    new DashboardFragment()).commit();*//*
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                    },5000);*/
                        /*if(getActivity()!=null) {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }*/

                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DashboardFragment dashboardFragment = new DashboardFragment();
                            FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.navigation_container, dashboardFragment);
                            //changes
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }).start();*/

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "level getContext2 is null");
                    }
                    if (Common.isLoggingEnabled)
                        Log.e(TAG, "Loader stopped when getContext is null while redirecting");
                    StopLoading();
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "levelModel != null || levelModel.getData() != null || levelModel.getData().size() > 0");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void levelOnUnSuccess() {
        if (Common.isLoggingEnabled) {
            Log.e(TAG, "LEVEL on unsuccessfull");
        }

    }

    @Override
    public void levelOnFailure(Throwable throwable) {
        if (Common.isLoggingEnabled) {
            throwable.printStackTrace();
        }

    }

    @Override
    public void planOnSuccess(GoalModel planModel) {
        try {
            if (planModel != null && planModel.getData() != null && planModel.getData().size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Plan model in Language Model: " + planModel.toString());
                }
                if (getContext() != null) {
                    String lang = SessionUtil.getlangCode(getContext());
                    for (int i = 0; i < planModel.getData().size(); i++) {
                        String planID = SessionUtil.getUserGoalID(getContext());
                        if (planModel.getData().get(i).getStripeProduct() != null) {
                            if (planModel.getData().get(i).getStripeProduct().getGoalId().equals(planID)) {
                                if (lang.matches("sv")) {
                                    SessionUtil.setUserGoal(getContext(), planModel.getData().get(i).getNameSv());
                                } else {
                                    SessionUtil.setUserGoal(getContext(), planModel.getData().get(i).getName());
                                }
                            }
                        }
                    }
                    CommonAPIUtil.getLevel(token, SessionUtil.getUserGoalID(getContext()), LanguageFragment.this);
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "plan getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "planModel!=null || planModel.getData()!=null || planModel.getData().size()>0");
                }
            }

        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void planOnUnSuccess() {
        if (Common.isLoggingEnabled)
            Log.e(TAG, "Plan on unsuccessfull");
    }

    @Override
    public void planOnFailure(Throwable t) {
        if (Common.isLoggingEnabled)
            t.printStackTrace();

    }
}