package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.PlanItemsDetailsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.ProductModel;
import com.cedricapp.model.ResubscribeModel;
import com.cedricapp.model.SubscriptionModel;
import com.cedricapp.model.UnsubscribeLaterModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StripeStatusUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class NewSelectedSubscriptionFragment extends Fragment {

    private MaterialTextView mTextViewTermsAndCondition, mTextViewplanPriceCardView1,
            mTextViewplanPriceCardView2, mTextViewplanPriceCardView3, mTextViewPerOFFCardView1, mTextViewPerOFFCardView2,
            mTextViewPerOFFCardView3, mTextViewPlanDurationCardView1, mTextViewPlanDurationCardView2, mTextViewPlanDurationCardView3,
            mTextViewPlanDurationPriceCardView1, mTextViewPlanDurationPriceCardView2, mTextViewPlanDurationPriceCardView3,
            mTextViewActualAmountCardView1, mTextViewActualAmountCardView2,
            mTextViewActualAmountCardView3, mTextViewSubscribedPlanExpireDate, mTextViewToggleTextView, mTextViewGoal, mTextViewLevel;
    private RecyclerView mPlanDescriptionRecyclerView;
    private View view1;
    private PlanItemsDetailsAdapter adapter;
    MaterialButton mSubscriptionButton1, mSubscriptionButton2, mSubscriptionButton3;
    private ArrayList<String> planDetailsList = new ArrayList<>();
    private MaterialTextView btn_No, btn_yes;
    private MaterialCardView mCardView1, mCardView2, mCardView3;
    private SwitchCompat mToggleButton;
    private Boolean cheeckedStatus = false;
    private ImageButton backArrow;
    boolean backPress = true;

    String sessionToken;

    String subscriptionID;

    String productID;

    String TAG = "NewSelectedSubscriptionFragment_TAG";

    List<ProductModel.Plan> planList;

    SubscriptionModel subscriptionModel;

    LottieAnimationView loading_lav;

    BlurView blurView;

    ProductModel.Plan frontCardPlanData, middleCardPlanData, backCardPlanData;

    String planID;

    Resources resources;

    MaterialTextView textViewsSubscription;

    public NewSelectedSubscriptionFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_selected_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        resources = Localization.setLanguage(getContext(), getResources());
        init();

    }

    private void init() {

        backArrow = view1.findViewById(R.id.backBtn);
        mTextViewActualAmountCardView1 = view1.findViewById(R.id.mTextViewActualAmountCardView1);
        mTextViewActualAmountCardView2 = view1.findViewById(R.id.mTextViewActualAmountCardView2);
        mTextViewActualAmountCardView3 = view1.findViewById(R.id.mTextViewActualAmountCardView3);
        mTextViewTermsAndCondition = view1.findViewById(R.id.mTextViewTermsAndCondition);
        mTextViewTermsAndCondition.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewTermsAndCondition.setLinkTextColor(Color.parseColor("#000000"));

        mPlanDescriptionRecyclerView = view1.findViewById(R.id.planItemDetailsRecyclerview);

        mCardView1 = view1.findViewById(R.id.subPlanCardView1);
        mCardView2 = view1.findViewById(R.id.subPlanCardView2);
        mCardView3 = view1.findViewById(R.id.subPlanCardView3);
        mSubscriptionButton1 = view1.findViewById(R.id.btnSubscribeCardView1);
        mSubscriptionButton2 = view1.findViewById(R.id.btnSubscribeCardView2);
        mSubscriptionButton3 = view1.findViewById(R.id.btnSubscribeCardView3);

        mTextViewplanPriceCardView1 = view1.findViewById(R.id.planPriceCardView1);
        mTextViewplanPriceCardView2 = view1.findViewById(R.id.planPriceCardView2);
        mTextViewplanPriceCardView3 = view1.findViewById(R.id.planPriceCardView3);

        mTextViewPerOFFCardView1 = view1.findViewById(R.id.mTextViewPerOFFCardView1);
        mTextViewPerOFFCardView2 = view1.findViewById(R.id.mTextViewPerOFFCardView2);
        mTextViewPerOFFCardView3 = view1.findViewById(R.id.mTextViewPerOFFCardView3);

        mTextViewPlanDurationCardView1 = view1.findViewById(R.id.mTextViewPlanDurationCardView1);
        mTextViewPlanDurationCardView2 = view1.findViewById(R.id.mTextViewPlanDurationCardView2);
        mTextViewPlanDurationCardView3 = view1.findViewById(R.id.mTextViewPlanDurationCardView3);

        mTextViewPlanDurationPriceCardView1 = view1.findViewById(R.id.mTextViewPlanDurationPriceCardView1);
        mTextViewPlanDurationPriceCardView2 = view1.findViewById(R.id.mTextViewPlanDurationPriceCardView2);
        mTextViewPlanDurationPriceCardView3 = view1.findViewById(R.id.mTextViewPlanDurationPriceCardView3);
        mTextViewSubscribedPlanExpireDate = view1.findViewById(R.id.mTextViewExpireDateCardView3);

        mTextViewGoal = view1.findViewById(R.id.mTextViewGoal);
        mTextViewLevel = view1.findViewById(R.id.mTextViewLevel);

        mTextViewGoal.setText(SessionUtil.getUserGoal(getContext()));
        mTextViewLevel.setText(" (" + SessionUtil.getUserLevel(getContext()) + ")");

        mToggleButton = view1.findViewById(R.id.toggleButton);
        mTextViewToggleTextView = view1.findViewById(R.id.toggleTextView);
        mPlanDescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCardView1.setClickable(true);
        mCardView2.setClickable(true);

        // Add data to the data list
        planDetailsList.clear();

        textViewsSubscription = view1.findViewById(R.id.textViewsSubscription);
        textViewsSubscription.setText(resources.getString(R.string.subscription_new));
        mTextViewTermsAndCondition.setText(resources.getString(R.string.hyperlink1));
        mTextViewToggleTextView.setText(resources.getString(R.string.pause_subscription_toggle));

        /*planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");*/

        //Strike through on text
        mTextViewActualAmountCardView1.setPaintFlags(mTextViewActualAmountCardView1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mTextViewActualAmountCardView2.setPaintFlags(mTextViewActualAmountCardView2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mTextViewActualAmountCardView3.setPaintFlags(mTextViewActualAmountCardView3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        sessionToken = SessionUtil.getAccessToken(getContext());
        //sessionToken = SharedData.token;

        subscriptionID = SessionUtil.getSubscriptionID_FromSession(getContext());

        productID = SessionUtil.getProductID(getContext());

        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);


        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Session Token: " + sessionToken + "\nSubscription ID: " + subscriptionID + "\nProduct ID: " + productID);
        }

        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            blurrBackground();
            StartLoading();
            getCurrentSubscribedPlan();

        } else {
            Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        if (getContext() != null) {
            adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
        }
        mPlanDescriptionRecyclerView.setAdapter(adapter);

        mSubscriptionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionButton3.getText().toString().matches(resources.getString(R.string.unsubscribe))) {
                    openDialogBox();
                } else if (mSubscriptionButton3.getText().toString().matches(resources.getString(R.string.resubscribe))) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                if (subscriptionID != null && planID != null) {
                                    resubscribe();
                                } else {
                                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else if (mSubscriptionButton3.getText().toString().matches(resources.getString(R.string.subscribe))) {
                    if (frontCardPlanData != null && frontCardPlanData.getPlanId() != null) {
                        changeSubscriptionFragment(frontCardPlanData.getPlanId());
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mSubscriptionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionButton1.getText().toString().matches(resources.getString(R.string.unsubscribe))) {
                    openDialogBox();
                } else if (mSubscriptionButton1.getText().toString().matches(resources.getString(R.string.subscribe))) {
                    if (backCardPlanData != null && backCardPlanData.getPlanId() != null) {
                        changeSubscriptionFragment(backCardPlanData.getPlanId());
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mSubscriptionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionButton2.getText().toString().matches(resources.getString(R.string.unsubscribe))) {
                    openDialogBox();
                } else if (mSubscriptionButton2.getText().toString().matches(resources.getString(R.string.subscribe))) {
                    if (middleCardPlanData != null && middleCardPlanData.getPlanId() != null) {
                        changeSubscriptionFragment(middleCardPlanData.getPlanId());
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        // Assuming toggleButton is an instance of SwitchCompat

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the toggle button state change
                if (isChecked) {
                    if (cheeckedStatus == false) {
                        showPauseSubDialog();
                    }

                } else {
                    // Toggle button is unchecked
                    // Perform your action here
                    if (cheeckedStatus == true) {
                        showResumeSubscription();
                    }
                }
            }
        });

        mCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });
        mCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCardsData(v);
            }
        });

        //listener for back button
        backArrow.setOnClickListener(v -> {
            if (getFragmentManager() != null)
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
        });


    }

    private void changeSubscriptionFragment(String planID) {
        if (isAdded()) {
            if (getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    Fragment fragment = new ChangeSubscriptionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("planID", planID);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void swapCardsData(View v) {
        switch (v.getId()) {
            case R.id.subPlanCardView1:
                ProductModel.Plan backCardTempData;
                if (frontCardPlanData != null && backCardPlanData != null) {
                    backCardTempData = backCardPlanData;
                    backCardPlanData = frontCardPlanData;
                    frontCardPlanData = backCardTempData;
                    setDataToFrontCard(frontCardPlanData);
                    setDataToBackCard(backCardPlanData);
                }
                break;
            case R.id.subPlanCardView2:
                ProductModel.Plan middleCardTempData;
                if (frontCardPlanData != null && middleCardPlanData != null) {
                    middleCardTempData = middleCardPlanData;
                    middleCardPlanData = frontCardPlanData;
                    frontCardPlanData = middleCardTempData;
                    setDataToFrontCard(frontCardPlanData);
                    setDataToMiddleCard(middleCardPlanData);
                }
                break;
        }

    }

    private void swapCardsData2(View v) {
        if (v.getId() == R.id.subPlanCardView1) {
            // Swap data between clicked card and third card

            String planPrice = mTextViewplanPriceCardView1.getText().toString();
            String offOnAmount = mTextViewPerOFFCardView1.getText().toString();
            /*String planDuration = mTextViewPlanDurationCardView1.getText().toString();*/
            String planDurationPrice = mTextViewPlanDurationPriceCardView1.getText().toString();
            String btn1Text = mSubscriptionButton1.getText().toString();
            String btn3Text = mSubscriptionButton3.getText().toString();
            String originalPrice = mTextViewActualAmountCardView1.getText().toString();


            mTextViewplanPriceCardView1.setText(mTextViewplanPriceCardView3.getText().toString());
            mTextViewPerOFFCardView1.setText(mTextViewPerOFFCardView3.getText().toString());
            /*mTextViewPlanDurationCardView1.setText(mTextViewPlanDurationCardView3.getText().toString());*/
            mSubscriptionButton1.setText(btn3Text);

            if (mSubscriptionButton1.getText().toString().equals(resources.getString(R.string.unsubscribe))) {
                mSubscriptionButton1.setBackgroundColor(Color.RED);
                //mSubscriptionButton1.setTextColor(R.color.white);
                mSubscriptionButton1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else {
                mSubscriptionButton1.setBackgroundColor(Color.WHITE);
                //mSubscriptionButton1.setTextColor(R.color.white);
                mSubscriptionButton1.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
            }
            //mTextViewPlanDurationPriceCardView1.setText(mTextViewPlanDurationPriceCardView3.getText().toString());

            mTextViewplanPriceCardView3.setText(planPrice);
            mTextViewPerOFFCardView3.setText(offOnAmount);
            /* mTextViewPlanDurationCardView3.setText(planDuration);*/
            mTextViewPlanDurationPriceCardView3.setText(planDurationPrice);
            mSubscriptionButton3.setText(btn1Text);
            if (!originalPrice.matches("")) {
                mTextViewActualAmountCardView3.setVisibility(View.VISIBLE);
                mTextViewActualAmountCardView3.setText(originalPrice);
            } else {
                mTextViewActualAmountCardView3.setVisibility(View.INVISIBLE);
            }
            if (mSubscriptionButton3.getText().toString().equals(resources.getString(R.string.subscribe))) {
                mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                mSubscriptionButton3.setTextColor(R.color.yellow);
                mTextViewSubscribedPlanExpireDate.setVisibility(View.GONE);
                mToggleButton.setVisibility(View.INVISIBLE);
                mTextViewToggleTextView.setVisibility(View.INVISIBLE);
                // mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else {
                mSubscriptionButton3.setBackgroundColor(Color.RED);
                // mSubscriptionButton3.setTextColor(R.color.white);
                mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                mTextViewSubscribedPlanExpireDate.setVisibility(View.VISIBLE);
                mToggleButton.setVisibility(View.VISIBLE);
                mTextViewToggleTextView.setVisibility(View.VISIBLE);
            }


        } else if (v.getId() == R.id.subPlanCardView2) {
            // Swap data between clicked card and third card
            String planPrice = mTextViewplanPriceCardView2.getText().toString();
            String offOnAmount = mTextViewPerOFFCardView2.getText().toString();
            /* String planDuration = mTextViewPlanDurationCardView2.getText().toString();*/
            String planDurationPrice = mTextViewPlanDurationPriceCardView2.getText().toString();
            String btn2Text = mSubscriptionButton2.getText().toString();
            String btn3Text = mSubscriptionButton3.getText().toString();
            String originalPrice = mTextViewActualAmountCardView2.getText().toString();

            mTextViewplanPriceCardView2.setText(mTextViewplanPriceCardView3.getText().toString());
            mTextViewPerOFFCardView2.setText(mTextViewPerOFFCardView3.getText().toString());
            /* mTextViewPlanDurationCardView2.setText(mTextViewPlanDurationCardView3.getText().toString());*/
            mSubscriptionButton2.setText(btn3Text);

            if (mSubscriptionButton2.getText().toString().equals(resources.getString(R.string.unsubscribe))) {
                mSubscriptionButton2.setBackgroundColor(Color.RED);
                //mSubscriptionButton1.setTextColor(R.color.white);
                mSubscriptionButton2.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else {
                mSubscriptionButton2.setBackgroundColor(Color.WHITE);
                //mSubscriptionButton1.setTextColor(R.color.white);
                mSubscriptionButton2.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
            }
            // mTextViewPlanDurationPriceCardView2.setText(mTextViewPlanDurationPriceCardView3.getText().toString());

            mTextViewplanPriceCardView3.setText(planPrice.toString());
            mTextViewPerOFFCardView3.setText(offOnAmount.toString());
            /* mTextViewPlanDurationCardView3.setText(planDuration.toString());*/
            mTextViewPlanDurationPriceCardView3.setText(planDurationPrice.toString());
            mSubscriptionButton3.setText(btn2Text);
            if (!originalPrice.matches("")) {
                mTextViewActualAmountCardView3.setVisibility(View.VISIBLE);
                mTextViewActualAmountCardView3.setText(originalPrice);
            } else {
                mTextViewActualAmountCardView3.setVisibility(View.INVISIBLE);
            }
            if (mSubscriptionButton3.getText().toString().equals(resources.getString(R.string.subscribe))) {
                mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                mSubscriptionButton3.setTextColor(R.color.yellow);
                mTextViewSubscribedPlanExpireDate.setVisibility(View.GONE);
                mToggleButton.setVisibility(View.INVISIBLE);
                mTextViewToggleTextView.setVisibility(View.INVISIBLE);
                // mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else {
                mSubscriptionButton3.setBackgroundColor(Color.RED);
                // mSubscriptionButton3.setTextColor(R.color.white);
                mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                mTextViewSubscribedPlanExpireDate.setVisibility(View.VISIBLE);
                mToggleButton.setVisibility(View.VISIBLE);
                mTextViewToggleTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showResumeSubscription() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box_resume_subscription);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));
        dialog_description.setText(resources.getString(R.string.dialog_text_resume_subscription));

        btn_No = dialog.findViewById(R.id.btn_left);
        btn_No.setText(resources.getString(R.string.btn_no));

        btn_yes = dialog.findViewById(R.id.btn_right);
        btn_yes.setText(resources.getString(R.string.btn_yes));


        btn_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                // cheeckedStatus=false;
                btn_No.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                mToggleButton.setChecked(true);
                dialog.dismiss();
            }

        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                cheeckedStatus = false;
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_yes.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }*/
                /*mSubscriptionButton3.setText("Resubscribe");
                mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                mSubscriptionButton3.setTextColor(R.color.yellow);
                mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));*/
                dialog.dismiss();
            }


        });
        dialog.show();
    }

    private void showPauseSubDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box_pause_subscription);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(resources.getString(R.string.dialog_text_pause_subscription));

        btn_No = dialog.findViewById(R.id.btn_left);
        btn_No.setText(resources.getString(R.string.btn_no));
        btn_yes = dialog.findViewById(R.id.btn_right);
        btn_yes.setText(resources.getString(R.string.btn_yes));

        RadioButton radioBtn_pause_1 = dialog.findViewById(R.id.radioBtn_pause_1);
        radioBtn_pause_1.setText(resources.getString(R.string.pause_for_min_days));

        RadioButton radioBtn_pause_2 = dialog.findViewById(R.id.radioBtn_pause_2);
        radioBtn_pause_2.setText(resources.getString(R.string.pause_for_med_days));

        RadioButton radioBtn_pause_3 = dialog.findViewById(R.id.radioBtn_pause_3);
        radioBtn_pause_3.setText(resources.getString(R.string.pause_for_max_days));


        btn_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                cheeckedStatus = false;
                btn_No.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                mToggleButton.setChecked(false);
                dialog.dismiss();
            }

        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                cheeckedStatus = true;
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_yes.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }*/
                /*mSubscriptionButton3.setText("Resubscribe");
                mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                mSubscriptionButton3.setTextColor(R.color.yellow);
                mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));*/
                dialog.dismiss();
            }


        });
        dialog.show();
    }

    private void openDialogBox() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box_unsubscribe);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.subscription_expiration_date) + " " + WeekDaysHelper.parseDateToYYYYmmDD(SessionUtil.getSubscriptionEndDate(getContext())));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(resources.getString(R.string.unsubscribe_package_text));

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
            // dialog.dismiss();
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_yes.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }*/
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    if (subscriptionID != null && planID != null) {
                        unsubscribeSubscription();
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }

                dialog.dismiss();
            }


        });
        dialog.show();
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

    void getProductsFromServer() {
        Call<ProductModel> call = ApiClient.getService().getAllProductsBySubscriptionIDAndProductID("Bearer " + sessionToken, subscriptionID, productID);
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (Common.isLoggingEnabled) {
                    if (response.raw() != null)
                        Log.d(TAG, "Raw Response: " + response.raw() + "\n" + response.errorBody());
                }
                if (response.isSuccessful()) {

                    ProductModel productModel = response.body();
                    if (productModel != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Products From Server: " + productModel.toString());
                        }
                        if (productModel.getPlans() != null) {
                            if (productModel.getPlans().size() > 0) {
                                planList = productModel.getPlans();
                                showDataToCards();
                            } else {
                                if (isAdded() && getContext() != null) {
                                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                }
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "productModel.getPlans() size is zero in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                                }
                                if (getContext() != null) {
                                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                        new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getProductsFromServer()",
                                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, "productModel.getPlans() size is zero in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                                    }
                                }
                            }

                        } else {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "productModel.getPlans() is null in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                            }
                            if (getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getProductsFromServer()",
                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, "productModel.getPlans() is null in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                                }
                            }
                        }

                    } else {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "productModel is null in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                        }
                        if (getContext() != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getProductsFromServer()",
                                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, "productModel is null in NewSelectedSubscriptionFragment::getProductsFromServer() ");
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Gson gson = new GsonBuilder().create();
                        ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                        errorMessageModel = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                        if (isAdded() && getContext() != null) {
                            if (errorMessageModel != null && errorMessageModel.getError() != null)
                                Toast.makeText(getContext(), errorMessageModel.getError(), Toast.LENGTH_SHORT).show();
                            else if (errorMessageModel != null && errorMessageModel.getMessage() != null) {
                                Toast.makeText(getContext(), errorMessageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                StopLoading();
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                StopLoading();
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getProductsFromServer()",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                    }
                }
            }
        });
    }

    void showDataToCards() {
        int firstCardIndex = 0;
        boolean isBackcard = false;
        boolean isMiddleCard = false;
        for (int i = 0; i < planList.size(); i++) {
            ProductModel.Plan planData = planList.get(i);
            if (planData.getIsCurrentPlan()) {
                firstCardIndex = i;
                frontCardPlanData = planData;
                setDataToFrontCard(frontCardPlanData);
            } else {
                if (!isBackcard) {
                    backCardPlanData = planData;
                    setDataToBackCard(backCardPlanData);
                    isBackcard = true;
                } else if (!isMiddleCard) {
                    middleCardPlanData = planData;
                    setDataToMiddleCard(middleCardPlanData);
                    isMiddleCard = true;
                }
            }
        }
    }

    void setDataToFrontCard(ProductModel.Plan planData) {
        if (planData != null) {

            if (planData.getDiscount() != null && planData.getDiscount() != 0) {
                mTextViewPerOFFCardView3.setVisibility(View.VISIBLE);
                mTextViewPerOFFCardView3.setText("" + planData.getDiscount() + "% " + resources.getString(R.string.off));
            } else {
                mTextViewPerOFFCardView3.setVisibility(View.INVISIBLE);
            }
            if (planData.getInterval() != null && !planData.getInterval().matches("")
                    && planData.getAmount() != null && !planData.getAmount().matches("")
                    && planData.getInterval_count() != null) {
                mTextViewplanPriceCardView3.setText("(" + planData.getInterval_count() + " " + getSubscritpionIntervalName(planData.getInterval_count()) + " " + resources.getString(R.string.at) + " " + planData.getCurrency().toUpperCase() + " " + (Integer.parseInt(planData.getAmount()) / Integer.parseInt(planData.getInterval_count())) + "/" + resources.getString(R.string.month) + ")");
            }
            if (planData.getOriginalPrice() != null && planData.getOriginalPrice() > 0) {
                mTextViewActualAmountCardView3.setVisibility(View.VISIBLE);
                mTextViewActualAmountCardView3.setText("(" + planData.getCurrency().toUpperCase() + " " + planData.getOriginalPrice() + ")");
            } else {
                mTextViewActualAmountCardView3.setVisibility(View.INVISIBLE);
            }
            if (planData.getCurrency() != null && planData.getAmount() != null) {

                mTextViewPlanDurationPriceCardView3.setText(planData.getCurrency().toUpperCase() + " " + planData.getAmount() + "/" + getSubscritpionNameByIntervalCount(planData.getInterval_count()));
            }
            /*if (planData.getIntervalName() != null) {
                mTextViewPlanDurationCardView3.setText(planData.getIntervalName());

            }*/

            if (planData.getDescription() != null) {
                planDetailsList.clear();
                planDetailsList.addAll(planData.getDescription());
                if (getContext() != null) {
                    adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
                }
                mPlanDescriptionRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if (planData.getIsCurrentPlan()) {
                boolean isSubscriptionAvailable = false;

                if (subscriptionModel != null && subscriptionModel.getData() != null && subscriptionModel.getData().getExpiryDate() != null) {
                    mTextViewSubscribedPlanExpireDate.setVisibility(View.VISIBLE);
                    String expiryDate = WeekDaysHelper.parseDateToYYYYmmDD(subscriptionModel.getData().getExpiryDate());
                    mTextViewSubscribedPlanExpireDate.setText(resources.getString(R.string.expires_at) + " " + expiryDate);
                    if (WeekDaysHelper.isSubscriptionAvailable(WeekDaysHelper.getDateTimeNow(), subscriptionModel.getData().getExpiryDate())) {
                        isSubscriptionAvailable = true;
                    } else {
                        isSubscriptionAvailable = false;
                    }
                }

                //set unsubscribe button to resubscribe
                if (SessionUtil.isSubscriptionUnsubscribed(getContext()) && SessionUtil.getUnsubscribedPlanID(getContext()).equals(planData.getPlanId())) {
                    mSubscriptionButton3.setText(resources.getString(R.string.resubscribe));
                    mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                    mSubscriptionButton3.setTextColor(R.color.yellow);
                    mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                    mToggleButton.setVisibility(View.INVISIBLE);
                    mTextViewToggleTextView.setVisibility(View.INVISIBLE);
                } else {
                    if (isSubscriptionAvailable) {
                        mToggleButton.setVisibility(View.VISIBLE);
                        mTextViewToggleTextView.setVisibility(View.VISIBLE);
                        mSubscriptionButton3.setText(resources.getString(R.string.unsubscribe));
                        mSubscriptionButton3.setBackgroundColor(Color.RED);
                        mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    } else {
                        mSubscriptionButton3.setText(resources.getString(R.string.resubscribe));
                        mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                        mSubscriptionButton3.setTextColor(R.color.yellow);
                        mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                    }
                }

            } else {
                mTextViewSubscribedPlanExpireDate.setVisibility(View.INVISIBLE);
                mTextViewSubscribedPlanExpireDate.setText("");
                mToggleButton.setVisibility(View.INVISIBLE);
                mTextViewToggleTextView.setVisibility(View.INVISIBLE);
                mSubscriptionButton3.setText(resources.getString(R.string.subscribe));

                mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                mSubscriptionButton3.setTextColor(R.color.yellow);
            }
        }
    }

    String getSubscritpionNameByIntervalCount(String intervalCount) {
        String name = "";
        int interval = 0;
        if (intervalCount != null) {
            interval = Integer.parseInt(intervalCount);
            switch (intervalCount) {
                case "1":
                    name = resources.getString(R.string.month);
                    break;
                case "6":
                    name = resources.getString(R.string.half_yearly_textView);
                    break;
                case "12":
                    name = resources.getString(R.string.yearly);
                    break;
                default:
                    name = "";
            }
        }
        return name;
    }

    String getSubscritpionIntervalName(String intervalCount) {
        String name = "";
        int interval = 0;
        if (intervalCount != null) {
            interval = Integer.parseInt(intervalCount);
            if (interval == 1) {
                name = resources.getString(R.string.month);
            } else if (interval > 1) {
                name = resources.getString(R.string.months);
            }
        }
        return name;
    }

    void setDataToBackCard(ProductModel.Plan planData) {
        if (planData != null) {
            if (planData.getDiscount() != null && planData.getDiscount() != 0) {
                mTextViewPerOFFCardView1.setVisibility(View.VISIBLE);
                mTextViewPerOFFCardView1.setText("" + planData.getDiscount() + "% " + resources.getString(R.string.off));
            } else {
                mTextViewPerOFFCardView1.setVisibility(View.INVISIBLE);
            }
            if (planData.getInterval() != null && !planData.getInterval().matches("")
                    && planData.getAmount() != null && !planData.getAmount().matches("")
                    && planData.getInterval_count() != null) {
                mTextViewplanPriceCardView1.setText("(" + planData.getInterval_count() + " " + getSubscritpionIntervalName(planData.getInterval_count()) + " " + resources.getString(R.string.at) + " " + planData.getCurrency().toUpperCase() + " " + (Integer.parseInt(planData.getAmount()) / Integer.parseInt(planData.getInterval_count())) + "/" + resources.getString(R.string.month) + ")");
            }
            if (planData.getOriginalPrice() != null && planData.getOriginalPrice() > 0) {
                mTextViewActualAmountCardView1.setVisibility(View.VISIBLE);
                mTextViewActualAmountCardView1.setText("(" + planData.getCurrency().toUpperCase() + " " + planData.getOriginalPrice() + ")");
            } else {
                mTextViewActualAmountCardView1.setVisibility(View.INVISIBLE);
            }

            mTextViewPlanDurationPriceCardView1.setText(planData.getCurrency().toUpperCase() + " " + planData.getAmount() + "/" + getSubscritpionNameByIntervalCount(planData.getInterval_count()));
            /*mTextViewPlanDurationCardView1.setText(planData.getIntervalName());*/
            if (planData.getIsCurrentPlan()) {
                if (SessionUtil.isSubscriptionUnsubscribed(getContext()) && SessionUtil.getUnsubscribedPlanID(getContext()).equals(planData.getPlanId())) {
                    mSubscriptionButton1.setText(resources.getString(R.string.resubscribe));
                    mSubscriptionButton1.setBackgroundColor(Color.WHITE);
                    mSubscriptionButton1.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                } else {
                    mSubscriptionButton1.setText(resources.getString(R.string.unsubscribe));
                    mSubscriptionButton1.setBackgroundColor(Color.RED);
                    mSubscriptionButton1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                }
            } else {
                mSubscriptionButton1.setText(resources.getString(R.string.subscribe));
                mSubscriptionButton1.setBackgroundColor(Color.WHITE);
                mSubscriptionButton1.setTextColor(R.color.yellow);
            }
        }

    }

    void setDataToMiddleCard(ProductModel.Plan planData) {
        if (planData != null) {
            if (planData.getDiscount() != null && planData.getDiscount() != 0) {
                mTextViewPerOFFCardView2.setVisibility(View.VISIBLE);
                mTextViewPerOFFCardView2.setText("" + planData.getDiscount() + "% " + resources.getString(R.string.off));
            } else {
                mTextViewPerOFFCardView2.setVisibility(View.INVISIBLE);
            }
            if (planData.getInterval() != null && !planData.getInterval().matches("")
                    && planData.getAmount() != null && !planData.getAmount().matches("")
                    && planData.getInterval_count() != null) {
                mTextViewplanPriceCardView2.setText("(" + planData.getInterval_count() + " " + getSubscritpionIntervalName(planData.getInterval_count()) + " " + resources.getString(R.string.at) + " " + planData.getCurrency().toUpperCase() + " " + (Integer.parseInt(planData.getAmount()) / Integer.parseInt(planData.getInterval_count())) + "/" + resources.getString(R.string.month) + ")");
            }
            if (planData.getOriginalPrice() != null && planData.getOriginalPrice() > 0) {
                mTextViewActualAmountCardView2.setVisibility(View.VISIBLE);
                mTextViewActualAmountCardView2.setText("(" + planData.getCurrency().toUpperCase() + " " + planData.getOriginalPrice() + ")");
            } else {
                mTextViewActualAmountCardView2.setVisibility(View.INVISIBLE);
            }
            mTextViewPlanDurationPriceCardView2.setText(planData.getCurrency().toUpperCase() + " " + planData.getAmount() + "/" + getSubscritpionNameByIntervalCount(planData.getInterval_count()));
            /*mTextViewPlanDurationCardView2.setText(planData.getIntervalName());*/
            if (planData.getIsCurrentPlan()) {
                if (SessionUtil.isSubscriptionUnsubscribed(getContext()) && SessionUtil.getUnsubscribedPlanID(getContext()).equals(planData.getPlanId())) {
                    mSubscriptionButton2.setText(resources.getString(R.string.resubscribe));
                    mSubscriptionButton2.setBackgroundColor(Color.WHITE);
                    mSubscriptionButton2.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                } else {
                    mSubscriptionButton2.setText(resources.getString(R.string.unsubscribe));
                    mSubscriptionButton2.setBackgroundColor(Color.RED);
                    mSubscriptionButton2.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                }
            } else {
                mSubscriptionButton2.setText(resources.getString(R.string.subscribe));
                mSubscriptionButton2.setBackgroundColor(Color.WHITE);
                mSubscriptionButton2.setTextColor(R.color.yellow);
            }
        }

    }

    private void getCurrentSubscribedPlan() {
        Call<SubscriptionModel> call = ApiClient.getService().currentSubscriptionPlan("Bearer " + sessionToken, subscriptionID);
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    if (response.isSuccessful()) {
                        subscriptionModel = response.body();
                        if (subscriptionModel != null && subscriptionModel.getData() != null && subscriptionModel.getData().getPlanID() != null) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Subscription List: " + subscriptionModel.toString());
                            }
                            planID = subscriptionModel.getData().getPlanID();
                            if(subscriptionModel.getData().getUnsubscribed()!=null) {
                                if (subscriptionModel.getData().getUnsubscribed()) {
                                    if (getContext() != null) {
                                        SessionUtil.setUnsubscribeStatus(getContext(), true);
                                        SessionUtil.setUnsubscribedPlanID(getContext(), planID);
                                    }
                                } else {
                                    SessionUtil.setUnsubscribeStatus(getContext(), false);
                                    SessionUtil.setUnsubscribedPlanID(getContext(), "");
                                }
                            }else{
                                SessionUtil.setUnsubscribeStatus(getContext(), false);
                                SessionUtil.setUnsubscribedPlanID(getContext(), "");
                            }
                        }
                    } else {
                        try {
                            Gson gson = new GsonBuilder().create();
                            ErrorMessageModel errorMessageModel = new ErrorMessageModel();
                            errorMessageModel = gson.fromJson(response.errorBody().toString(), ErrorMessageModel.class);
                            if (getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getCurrentSubscribedPlan",
                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, "Call request not successfull, response raw is  " + response.raw() + " and response is " + errorMessageModel.toString());
                                }
                            }
                        } catch (Exception ex) {
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getCurrentSubscribedPlan()",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }
                getProductsFromServer();
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("NewSelectedSubscriptionFragment::getCurrentSubscribedPlan()",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                getProductsFromServer();
            }
        });
    }

    void unsubscribeSubscription() {
        blurrBackground();
        StartLoading();
        Call<UnsubscribeLaterModel> call = ApiClient.getService().unsubscribeLaterSubscription("Bearer " + sessionToken, subscriptionID, planID);
        call.enqueue(new Callback<UnsubscribeLaterModel>() {
            @Override
            public void onResponse(Call<UnsubscribeLaterModel> call, Response<UnsubscribeLaterModel> response) {
                if (response.isSuccessful()) {
                    UnsubscribeLaterModel unsubscribeLaterModel = response.body();
                    if (unsubscribeLaterModel != null) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.unsubscribe_successfully), Toast.LENGTH_SHORT).show();
                        }
                        if (getContext() != null) {
                            SessionUtil.setUnsubscribeStatus(getContext(), true);
                            SessionUtil.setUnsubscribedPlanID(getContext(), planID);
                            mSubscriptionButton3.setText(resources.getString(R.string.resubscribe));
                        }
                        mSubscriptionButton3.setBackgroundColor(Color.WHITE);
                        mSubscriptionButton3.setTextColor(R.color.yellow);
                        mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                        mToggleButton.setVisibility(View.INVISIBLE);
                        mTextViewToggleTextView.setVisibility(View.INVISIBLE);
                    } else {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "NewSelectedSubscriptionFragment::unsubscribeSubscription(), unsubscribeLaterModel is null from server");
                        }
                    }

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //TODO implement later
                }
                StopLoading();
            }

            @Override
            public void onFailure(Call<UnsubscribeLaterModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                StopLoading();
            }
        });
    }

    void resubscribe() {
        blurrBackground();
        StartLoading();
        Call<ResubscribeModel> call = ApiClient.getService().resubscribe("Bearer " + sessionToken, subscriptionID, planID);
        call.enqueue(new Callback<ResubscribeModel>() {
            @Override
            public void onResponse(Call<ResubscribeModel> call, Response<ResubscribeModel> response) {
                if (response.isSuccessful()) {
                    ResubscribeModel resubscribeModel = response.body();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Resubscribe Model: " + resubscribeModel.toString());
                    }
                    if (resubscribeModel != null && resubscribeModel.getData() != null) {
                        if (getContext() != null) {
                            if (resubscribeModel.getData().getPeriodStarts() != null
                                    && resubscribeModel.getData().getPeriodEnds() != null
                                    && resubscribeModel.getData().getStatus() != null) {
                                if (StripeStatusUtil.isUserAllowToUseApp(resubscribeModel.getData().getStatus())) {
                                    SessionUtil.saveSubscription(getContext(), resubscribeModel.getData().getPeriodStarts(), resubscribeModel.getData().getPeriodEnds());
                                    SessionUtil.setUnsubscribeStatus(getContext(), false);
                                    SessionUtil.setUnsubscribedPlanID(getContext(), "");
                                    mToggleButton.setVisibility(View.VISIBLE);
                                    mTextViewToggleTextView.setVisibility(View.VISIBLE);
                                    mSubscriptionButton3.setText(resources.getString(R.string.unsubscribe));
                                    mSubscriptionButton3.setBackgroundColor(Color.RED);
                                    mSubscriptionButton3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                    if (getContext() != null)
                                        Toast.makeText(getContext(), resources.getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                                } else {
                                    if (getContext() != null)
                                        Toast.makeText(getContext(), resources.getString(R.string.your_subscription_is) + " " + resubscribeModel.getData().getStatus(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "getContext()==null in resubscribe()");
                            }
                        }
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "resubscribeModel == null && resubscribeModel.getData() == null in resubscribe()");
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response is unsuccessful in resubscribe()");
                    }
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                StopLoading();
            }

            @Override
            public void onFailure(Call<ResubscribeModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                StopLoading();
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

    public void StartLoading() {
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

    }

}