package com.cedricapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ChangePlanModel;
import com.cedricapp.model.GoalModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogoutUtil;
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
public class ChangePlanFragment extends Fragment {
    private View view1;
    private MaterialTextView mTextViewTermsAndCondition, mTextViewPlanCardView1,
            mTextViewPlanCardView2, mTextViewAllPlanCardView1, mTextViewAllLevelCardView2,
            mTextViewGoal, mTextViewLevel, textViewChangePlan, textViewYourCurrentPlan;

    private MaterialCardView mChangePlanCardView1, mChangePlanCardView2;
    private MaterialButton mChangePlanButton1, mChangePlanButton2;
    private MaterialTextView btn_No, btn_yes;
    private ImageButton backArrow;
    boolean backPress = true;
    private RecyclerView mPlanDescriptionRecyclerView;
    private ArrayList<String> planDetailsList = new ArrayList<>();
    private PlanItemsDetailsAdapter adapter;

    private String auth;

    private GoalModel planModel;

    private GoalModel.Datum firstPlan, secondPlan;

    LottieAnimationView loading_lav;

    BlurView blurView;

    private String TAG = "CHANGE_PLAN_TAG";

    Resources resources;

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

    public ChangePlanFragment() {
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
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        init();
    }

    private void init() {

        mPlanDescriptionRecyclerView = view1.findViewById(R.id.changePlanItemDetailsRecyclerview);
        planDetailsList.clear();
        mTextViewTermsAndCondition = view1.findViewById(R.id.mTextViewTermsAndCondition);
        mTextViewTermsAndCondition.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewTermsAndCondition.setLinkTextColor(Color.parseColor("#000000"));
        mChangePlanCardView1 = view1.findViewById(R.id.changePlanCardView1);
        mChangePlanCardView2 = view1.findViewById(R.id.changePlanCardView2);
        mTextViewPlanCardView1 = view1.findViewById(R.id.mTextViewPlanCardView1);
        mTextViewPlanCardView2 = view1.findViewById(R.id.mTextViewPlanCardView2);
        mChangePlanButton1 = view1.findViewById(R.id.btnChangePlanCardView1);
        mChangePlanButton2 = view1.findViewById(R.id.btnChangePlanCardView2);
        mTextViewAllPlanCardView1 = view1.findViewById(R.id.mTextViewAllPlanCardView1);
        mTextViewAllLevelCardView2 = view1.findViewById(R.id.mTextViewAllLevelCardView2);
        mPlanDescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTextViewGoal = view1.findViewById(R.id.mTextViewGoal);
        mTextViewLevel = view1.findViewById(R.id.mTextViewLevel);
        textViewChangePlan = view1.findViewById(R.id.textViewChangePlan);
        textViewYourCurrentPlan = view1.findViewById(R.id.textViewYourCurrentPlan);
        blurBkgTxt = view1.findViewById(R.id.blurBkgTxt);

        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);

        //mTextViewGoal.setText(SessionUtil.getUserGoal(getContext()));
        mTextViewLevel.setText("(" + SessionUtil.getUserLevel(getContext()) + ")");

        if (SharedData.token != null) {
            auth = SharedData.token;
        } else {
            auth = SessionUtil.getAccessToken(getContext());
        }

        if (Common.isLoggingEnabled) {
            Log.i(TAG, "BEARER TOKEN = " + auth);
        }

        setWidgetByLanguage();

        getPlansFromServer(true);

        // Add data to the data list

        //planDetailsList.add("Hassale free for 1 months");
        /*planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");*/

        /*if (getContext() != null) {
            adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
        }
        mPlanDescriptionRecyclerView.setAdapter(adapter);*/

        backArrow = view1.findViewById(R.id.backBtn);
        //listener for back button
        backArrow.setOnClickListener(v -> {
            /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }*/
            onBackPress();
        });


        mChangePlanButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (planModel != null && planModel.getData() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        showDialogBox(firstPlan);
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mChangePlanButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (planModel != null && planModel.getData() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        showDialogBox(secondPlan);
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


        mChangePlanCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });
        mChangePlanCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });
    }

    private void showDialogBox(GoalModel.Datum planModel) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box_change_plan);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialTextView titleMTV = dialog.findViewById(R.id.titleMTV);
        titleMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(resources.getString(R.string.dielog_box_text_change_plan));


        btn_No = dialog.findViewById(R.id.btn_left);
        btn_No.setText(resources.getString(R.string.btn_no));
        btn_yes = dialog.findViewById(R.id.btn_right);
        btn_yes.setText(resources.getString(R.string.btn_yes));


        btn_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                btn_No.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));

                dialog.dismiss();
            }

        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_yes.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                dialog.dismiss();
                changePlan(SessionUtil.getSubscriptionID_FromSession(getContext()), planModel);
            }


        });
        dialog.show();
    }


    private void swapCardsData(View v) {
        if (firstPlan != null && secondPlan != null) {
            GoalModel.Datum tempPlanModel;
            if (v.getId() == R.id.changePlanCardView1) {
                tempPlanModel = firstPlan;
                firstPlan = secondPlan;
                secondPlan = tempPlanModel;
                setDataToUI();
            } else if (v.getId() == R.id.changePlanCardView2) {
                tempPlanModel = secondPlan;
                secondPlan = firstPlan;
                firstPlan = tempPlanModel;
                setDataToUI();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "firstPlan or secondPlan null on swaping");
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


    void getPlansFromServer(boolean isStopDialog) {
        blurrBackground();
        startLoading();
        Call<GoalModel> call = ApiClient.getService().getGoals("Bearer " + auth);
        call.enqueue(new Callback<GoalModel>() {
            @Override
            public void onResponse(Call<GoalModel> call, Response<GoalModel> response) {
                if (response.isSuccessful()) {
                    planModel = response.body();
                    if (planModel != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Plan Model: " + planModel.toString());
                        }
                    }
                    setModel(isStopDialog);
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                    stopLoading();
                } else {
                    if (response.code() == 401) {
                        //Unauthorized
                        //Logout from app

                    }
                    if (getContext() != null) {
                        if (response.body() != null && response.body().getError() != null) {
                            Toast.makeText(getContext(), response.body().getError(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                    stopLoading();
                }
            }

            @Override
            public void onFailure(Call<GoalModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                stopLoading();
            }
        });
    }

    void setModel(boolean isStopDialog) {
        if (planModel != null) {
            if (planModel.getData() != null) {
                if (planModel.getData().size() > 1) {
                    if (firstPlan != null) {
                        firstPlan = null;
                    }
                    for (int i = 0; i < planModel.getData().size(); i++) {
                        if (!planModel.getData().get(i).getStripeProduct().getProductId().equals(SessionUtil.getProductID(getContext()))) {
                            if (firstPlan == null) {
                                firstPlan = planModel.getData().get(i);
                            } else {
                                secondPlan = planModel.getData().get(i);
                            }
                        } else {
                            if (planModel.getData().get(i) != null &&
                                    planModel.getData().get(i).getStripeProduct() != null
                                    && planModel.getData().get(i).getStripeProduct().getNameSV() != null
                                    && planModel.getData().get(i).getStripeProduct().getName() != null) {
                                if (Localization.getLang(getContext()).matches("sv")) {
                                    if (getContext() != null) {
                                        SessionUtil.setUserGoal(getContext(), planModel.getData().get(i).getNameSv());
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "getContext() == null while saving session");
                                        }
                                    }
                                    mTextViewGoal.setText(planModel.getData().get(i).getNameSv());
                                } else {
                                    if (getContext() != null) {
                                        SessionUtil.setUserGoal(getContext(), planModel.getData().get(i).getName());
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "getContext() == null while saving session");
                                        }
                                    }
                                    mTextViewGoal.setText(planModel.getData().get(i).getName());

                                }
                            }
                        }
                    }
                    if(isStopDialog)
                        stopLoading();
                    /*if (planModel.getData().get(0) != null) {
                        firstPlan = planModel.getData().get(0);
                    }
                    if (planModel.getData().get(1) != null) {
                        secondPlan = planModel.getData().get(1);
                    }*/
                    setDataToUI();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Plan data size is " + planModel.getData().size() + " in setModel()");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "planModel.getData() is null in setModel()");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "planModel is null in setModel()");
            }
        }
    }

    void setDataToUI() {
        if (isAdded() && getContext() != null) {
            if (firstPlan != null && secondPlan != null
                    && firstPlan.getStripeProduct() != null && secondPlan.getStripeProduct() != null) {

                planDetailsList.clear();
                if (Localization.getLang(getContext()).matches("sv")) {
                    mTextViewPlanCardView1.setText(firstPlan.getNameSv());
                    planDetailsList.add(firstPlan.getStripeProduct().getDescription_SV());
                } else {
                    mTextViewPlanCardView1.setText(firstPlan.getName());
                    planDetailsList.add(firstPlan.getStripeProduct().getDescription());
                }
                adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
                mPlanDescriptionRecyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                //mTextViewPlanCardView2.setText(secondPlan.getName());
                if (Localization.getLang(getContext()).matches("sv")) {
                    mTextViewPlanCardView2.setText(secondPlan.getNameSv());
                } else {
                    mTextViewPlanCardView2.setText(secondPlan.getName());
                }
                mTextViewAllPlanCardView1.setText(resources.getString(R.string.all_levels));
                mTextViewAllLevelCardView2.setText(resources.getString(R.string.all_levels));
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "firstPlan or secondPlan null in setDataToUI()");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragment not attached or getContext is null in setDataToUI()");
            }
        }
    }

    void changePlan(String subscriptionID, GoalModel.Datum planModel) {
        blurrBackground();
        startLoading();
        Call<ChangePlanModel> call = ApiClient.getService().changePlan("Bearer " + auth, subscriptionID, planModel.getStripeProduct().getProductId(), String.valueOf(planModel.getStripeProduct().getGoalId()));
        call.enqueue(new Callback<ChangePlanModel>() {
            @Override
            public void onResponse(Call<ChangePlanModel> call, Response<ChangePlanModel> response) {
                if (response.isSuccessful()) {
                    blurBkgTxt.setVisibility(View.VISIBLE);
                    ChangePlanModel changePlanModel = response.body();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "ChangePlanModel: " + changePlanModel.toString());
                    }
                    if (changePlanModel != null && changePlanModel.getData() != null) {
                        if (getContext() != null) {
                            SessionUtil.saveSubscriptionID(getContext(), changePlanModel.getData().getSubscriptionId());
                            if (planModel.getStripeProduct() != null) {
                                SessionUtil.setProductID(getContext(), planModel.getStripeProduct().getProductId());
                                SessionUtil.setUserGoalID(getContext(), String.valueOf(planModel.getStripeProduct().getGoalId()));
                            }
                            if (Localization.getLang(getContext()).matches("sv")) {
                                SessionUtil.setUserGoal(getContext(), planModel.getNameSv());
                                mTextViewGoal.setText(planModel.getNameSv());
                            } else {
                                SessionUtil.setUserGoal(getContext(), planModel.getName());
                                mTextViewGoal.setText(planModel.getName());
                            }
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

                            Toast.makeText(getContext(), resources.getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                        }
                        getPlansFromServer(false);
                    } else {
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
                    if (getContext() != null) {
                        if (response.body() != null && response.body().getError() != null) {
                            Toast.makeText(getContext(), response.body().getError(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
               // stopLoading();
            }

            @Override
            public void onFailure(Call<ChangePlanModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });
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

    void setWidgetByLanguage() {
        if (getContext() != null) {
            textViewChangePlan.setText(resources.getString(R.string.change_plan_title));
            textViewYourCurrentPlan.setText(resources.getString(R.string.text_view_your_current_plan_is));
            mChangePlanButton1.setText(resources.getString(R.string.btn_select_level));
            mChangePlanButton2.setText(resources.getString(R.string.btn_select_level));
            mTextViewTermsAndCondition.setText(resources.getString(R.string.hyperlink1));
        }
    }

}