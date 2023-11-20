package com.cedricapp.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.PlanItemsDetailsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.ProfileAPI_Callback;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.LevelModel;
import com.cedricapp.model.ProfileActivation;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.ProfileUpdateUtil;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ChangeLevelFragment extends Fragment implements ProfileAPI_Callback {
    MaterialButton btnChangeLevel1, btnChangeLevel2;
    private MaterialTextView mTextViewTermsAndCondition, textViewChangePlan, mTextViewLevelCardView1, mTextViewLevelCardView2;
    private View view1;
    private ImageButton backArrow;
    boolean backPress = true;
    private MaterialCardView mChangeLevelCardView1, mChangeLevelCardView2;

    private String auth;

    private String TAG = "CHANGE_LEVEL_TAG";

    LevelModel levelModel;

    LevelModel.Datum firstCardModel, secondCardModel;

    LottieAnimationView loading_lav;

    BlurView blurView;

    Resources resources;

    RecyclerView planItemDetailsRecyclerview;
    MaterialTextView textViewYourCurrentLevel;

    private PlanItemsDetailsAdapter adapter;

    MaterialTextView blurBkgTxt;

    int DELAY = 2000;

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
        //requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    @Override
    public void onPause() {
        super.onPause();
        // requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));
    }

    public ChangeLevelFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_level, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        init();
    }

    private void init() {
        if (getContext() != null && getResources() != null) {
            //resources = Localization.setLanguage(getContext(), getResources());
            resources= getResources();
        }

        mTextViewTermsAndCondition = view1.findViewById(R.id.mTextViewTermsAndCondition);
        mTextViewTermsAndCondition.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewTermsAndCondition.setLinkTextColor(Color.parseColor("#000000"));
        btnChangeLevel1 = view1.findViewById(R.id.btnChangeLevelCardView1);
        btnChangeLevel2 = view1.findViewById(R.id.btnChangeLevelCardView2);
        mTextViewLevelCardView1 = view1.findViewById(R.id.mTextViewLevelCardView1);
        mTextViewLevelCardView2 = view1.findViewById(R.id.mTextViewLevelCardView2);
        backArrow = view1.findViewById(R.id.backBtn);
        mChangeLevelCardView1 = view1.findViewById(R.id.changeLevelCardView1);
        mChangeLevelCardView2 = view1.findViewById(R.id.changeLevelCardView2);

        blurBkgTxt = view1.findViewById(R.id.blurBkgTxt);

        textViewChangePlan = view1.findViewById(R.id.textViewChangePlan);

        textViewYourCurrentLevel = view1.findViewById(R.id.textViewYourCurrentLevel);

        planItemDetailsRecyclerview = view1.findViewById(R.id.planItemDetailsRecyclerview);
        planItemDetailsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        textViewYourCurrentLevel.setText(resources.getString(R.string.text_view_your_current_level_is));
        btnChangeLevel1.setText(resources.getString(R.string.btn_select_level));
        btnChangeLevel2.setText(resources.getString(R.string.btn_select_level));
        mTextViewTermsAndCondition.setText(resources.getString(R.string.hyperlink1));

        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);

        if (SharedData.token != null) {
            auth = SharedData.token;
        } else {
            auth = SessionUtil.getAccessToken(getContext());
        }

        if (Common.isLoggingEnabled) {
            Log.i(TAG, "BEARER TOKEN = " + auth);
        }


        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            getLevelFromServer(SessionUtil.getUserGoalID(getContext()));
        }

        //listener for back button
        backArrow.setOnClickListener(v -> {
           /* if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }*/
            onBackPress();
        });
        btnChangeLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getContext() != null) {
                        /*Fragment fragment = new ChangeSubscriptionPaymentFragment();
                        FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.navigation_container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();*/
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            if (firstCardModel != null) {
                                blurrBackground();
                                startLoading();
                                ProfileActivation profileActivation = new ProfileActivation("" + SessionUtil.getUserID(getContext()), "" + SessionUtil.getUserWeight(getContext()), "" + SessionUtil.getUserHeight(getContext()), "" + SessionUtil.getUserAge(getContext()), "" + SessionUtil.getUserGender(getContext()), "" + SessionUtil.getUserGoalID(getContext()), "" + firstCardModel.getId(), "" + SessionUtil.getUserUnitType(getContext()), "" + SessionUtil.getUsernameFromSession(getContext()));
                                ProfileUpdateUtil.updateUserProfile(getContext(), auth, profileActivation, ChangeLevelFragment.this);
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "firstCardModel is null on button click");
                                }
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        });

        btnChangeLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getContext() != null) {
                        /*Fragment fragment = new ChangeSubscriptionPaymentFragment();
                        FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.navigation_container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();*/
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            if (secondCardModel != null) {
                                blurrBackground();
                                startLoading();
                                ProfileActivation profileActivation = new ProfileActivation("" + SessionUtil.getUserID(getContext()), "" + SessionUtil.getUserWeight(getContext()), "" + SessionUtil.getUserHeight(getContext()), "" + SessionUtil.getUserAge(getContext()), "" + SessionUtil.getUserGender(getContext()), "" + SessionUtil.getUserGoalID(getContext()), "" + secondCardModel.getId(), "" + SessionUtil.getUserUnitType(getContext()), "" + SessionUtil.getUsernameFromSession(getContext()));
                                ProfileUpdateUtil.updateUserProfile(getContext(), auth, profileActivation, ChangeLevelFragment.this);
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "secondCardModel is null on button click");
                                }
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        });

        mChangeLevelCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });
        mChangeLevelCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });
    }

    void getLevelFromServer(String goalID) {
        blurrBackground();
        startLoading();
        Call<LevelModel> call = ApiClient.getService().getUserLevel("Bearer " + auth, goalID);
        call.enqueue(new Callback<LevelModel>() {
            @Override
            public void onResponse(Call<LevelModel> call, Response<LevelModel> response) {
                if (response.isSuccessful()) {
                    levelModel = response.body();
                    if (levelModel != null && levelModel.getData().size() > 1) {
                        String currentLevel = SessionUtil.getUserLevelID(getContext());
                        firstCardModel = null;
                        secondCardModel = null;
                        for (int i = 0; i < levelModel.getData().size(); i++) {
                            if (!String.valueOf(levelModel.getData().get(i).getId()).matches(currentLevel)) {
                                if (firstCardModel == null) {
                                    firstCardModel = levelModel.getData().get(i);
                                } else {
                                    secondCardModel = levelModel.getData().get(i);
                                }
                            } else {
                                if (getContext() != null) {
                                    if (SessionUtil.getlangCode(getContext()).matches("sv")) {
                                        if (levelModel.getData().get(i).getNameSv() != null) {
                                            textViewChangePlan.setText(levelModel.getData().get(i).getNameSv());
                                            SessionUtil.setUserLevel(getContext(), levelModel.getData().get(i).getNameSv());
                                        }
                                    } else {
                                        if (levelModel.getData().get(i).getName() != null) {
                                            textViewChangePlan.setText(levelModel.getData().get(i).getName());
                                            SessionUtil.setUserLevel(getContext(), levelModel.getData().get(i).getName());
                                        }
                                    }
                                }
                            }
                        }
                        setLevelUI();
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "levelModel==null OR levelModel.getData().size()<1 in getLevelFromServer()");
                        }
                        if (getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        //Logout
                    }
                    if (getContext() != null) {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
                stopLoading();
            }

            @Override
            public void onFailure(Call<LevelModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                stopLoading();
            }
        });
    }

    void setLevelUI() {
        if (firstCardModel != null && secondCardModel != null) {
            if (SessionUtil.getlangCode(getContext()).matches("sv")) {
                if (firstCardModel.getNameSv() != null && secondCardModel.getNameSv() != null) {
                    mTextViewLevelCardView1.setText(firstCardModel.getNameSv());
                    mTextViewLevelCardView2.setText(secondCardModel.getNameSv());
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "firstCardModel.getNameSv() == null && secondCardModel.getNameSv() == null in setLevelUI()");
                    }
                }
                if (secondCardModel.getDescriptionSV() != null) {
                    ArrayList<String> List = new ArrayList<>();
                    List.add(secondCardModel.getDescriptionSV());
                    adapter = new PlanItemsDetailsAdapter(getContext(), List); // Pass the activity context and data list to the adapter
                    planItemDetailsRecyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "firstCardModel.getDescriptionSV() == null && secondCardModel.getDescriptionSV() == null in setLevelUI()");
                    }
                }
            } else {
                if (firstCardModel.getName() != null && secondCardModel.getName() != null) {
                    mTextViewLevelCardView1.setText(firstCardModel.getName());
                    mTextViewLevelCardView2.setText(secondCardModel.getName());
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "firstCardModel.getName() == null && secondCardModel.getName() == null in setLevelUI()");
                    }
                }

                if (secondCardModel.getDescriptionEN() != null) {
                    ArrayList<String> List = new ArrayList<>();
                    List.add(secondCardModel.getDescriptionEN());
                    adapter = new PlanItemsDetailsAdapter(getContext(), List); // Pass the activity context and data list to the adapter
                    planItemDetailsRecyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "firstCardModel.getDescriptionSV() == null && secondCardModel.getDescriptionSV() == null in setLevelUI()");
                    }
                }
            }

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "levelModel==null && levelModel.getData()==null");
            }
            if (getContext() != null) {
                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
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

    private void swapCardsData(View v) {
        if (firstCardModel != null && secondCardModel != null) {
            LevelModel.Datum tempCardLevelModel;
            if (v.getId() == R.id.changeLevelCardView1) {
                tempCardLevelModel = firstCardModel;
                firstCardModel = secondCardModel;
                secondCardModel = tempCardLevelModel;
                setLevelUI();
            } else if (v.getId() == R.id.changeLevelCardView2) {
                tempCardLevelModel = secondCardModel;
                secondCardModel = firstCardModel;
                firstCardModel = tempCardLevelModel;
                setLevelUI();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "firstCardModel == null && secondCardModel !=  in swapCardsData()");
            }
        }
    }

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

                blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurAlgorithm(new RenderScriptBlur(requireContext())).setBlurRadius(radius).setBlurAutoUpdate(true).setHasFixedTransformationMatrix(false);
            }
        }

    }

    public void startLoading() {
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
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    private void stopLoading() {
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

    }


    @Override
    public void profileResponse(Response<SignupResponse> response) {
        if (response.isSuccessful()) {
            SignupResponse signupResponse = response.body();
            if (signupResponse.getData() != null) {
                if (signupResponse.getData().getLevel_id() != null) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Update profile response: " + signupResponse.toString());
                    }
                    if (getContext() != null) {
                        SessionUtil.setUserLevelID(getContext(), signupResponse.getData().getLevel_id());
                        SessionUtil.setUserLevel(getContext(), signupResponse.getData().getLevel());
                        textViewChangePlan.setText(SessionUtil.getUserLevel(getContext()));
                        //reload all data
                        SessionUtil.setDashboardReloadData(getContext(), true);
                        SessionUtil.setReloadData(getContext(), true);
                        SessionUtil.setWorkoutProgressLoad(true, getContext());
                        SessionUtil.setDailyCoachLoad(true, getContext());
                        SessionUtil.setShoppingLoading(true, getContext());
                        SessionUtil.setLoadHomeData(true, getContext());
                        //clear DB
                        new DBHelper(getContext()).clearDB();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blurBkgTxt.setVisibility(View.VISIBLE);
                                blurBkgTxt.setText(resources.getString(R.string.adding_your_updated_diet_plan));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        blurBkgTxt.setText(resources.getString(R.string.revamped_diet_plan));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                blurBkgTxt.setText(resources.getString(R.string.update_completed_explore_dashboard));
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(getContext(), HomeActivity.class);
                                                        if (Common.isLoggingEnabled)
                                                            Log.e(TAG, "Loader stopped when redirecting to Bottom Navigation Bar");

                                                        intent.putExtra("show_success_dialog",true);
                                                        stopLoading();
                                                        blurBkgTxt.setVisibility(View.GONE);
                                                        startActivity(intent);
                                                    }
                                                }, DELAY);
                                            }
                                        }, DELAY);

                                    }
                                },DELAY);

                            }
                        }, DELAY);

                       // Toast.makeText(getContext(), getResources().getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                        /*if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            getLevelFromServer(SessionUtil.getUserGoalID(getContext()));
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "ChangeLevelFragment::profileResponse::no internet connection");
                            }
                        }*/
                    } else {
                        stopLoading();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "ChangeLevelFragment::profileResponse::getContext() == null");
                        }
                    }
                } else {
                    stopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "ChangeLevelFragment::profileResponse::signupResponse.getData().getLevel_id() == null");
                    }
                }
            } else {
                stopLoading();
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "ChangeLevelFragment::profileResponse::signupResponse.getData() == null");
                }
            }
        } else if (response.code() == 401) {
            if (getContext() != null) {
                LogoutUtil.redirectToLogin(getContext());
                Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
            }
            stopLoading();
        } else {
            if (response.code() == 401) {
                //logout
            }
            stopLoading();
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "ChangeLevelFragment::profileResponse::Request is not successfull and response code is " + response.code());
            }
        }
       // stopLoading();
    }

    @Override
    public void profileResponseFailure(Throwable t) {
        stopLoading();
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
        }

    }
}