package com.cedricapp.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.cedricapp.model.ChangeSubscriptionModel;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ChangeSubscriptionFragment extends Fragment {
    private View view1;
    private ImageButton backArrow;
    boolean backPress = true;
    private MaterialButton btnCheckout, btnCancel;
    private RecyclerView mPlanDescriptionRecyclerView;
    private ArrayList<String> planDetailsList = new ArrayList<>();
    private PlanItemsDetailsAdapter adapter;

    private MaterialTextView currentPlanPricePerMonth, mTextViewCurrentPlanOFF, mTextViewCurrentPlanDuration, mTextViewCurrentPlanPrice, mTextViewNewPlanOFF, newPlanPricePerMonth, mTextViewNewPlanDuration, mTextViewNewPlanPrice, mTextViewPlanDurationPriceCardView2, cardMTV;

    private MaterialTextView mTextViewOrderPlanPrice, newPlanItemDetailsRecyclerview, mTextViewOrderReimbursmentPrice, mTextViewOrderAmountExcludedPrice, mTextViewOrderAmountIncludePrice, mTextViewOrderTotalAmountPrice;

    private MaterialTextView mTextViewCurrentPlan, mTextViewNewPlan, mTextViewTransactionDetails, mTextViewOrderSummary,
            mTextViewOrderPlanType, mTextViewOrderReimbursment, mTextViewOrderAmountExcluded, mTextViewOrderAmountInclude, mTextViewOrderTotalAmount;
    LottieAnimationView loading_lav;

    BlurView blurView;

    String planID;

    String TAG = "ChangeSubscriptionFragment_TAG";

    ChangeSubscriptionModel changeSubscriptionModel;

    Resources resources;

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.hideBottomNav();
        //requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }


    public ChangeSubscriptionFragment() {
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
                    Toast.makeText(getContext(), "wait little while", Toast.LENGTH_SHORT).show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        init();


        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getContext() != null) {
                        if (changeSubscriptionModel != null) {
                            String oldSubscriptionID = SessionUtil.getSubscriptionID_FromSession(getContext());
                            if (changeSubscriptionModel.getNewPlan().getPlanId() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("subscription_id", oldSubscriptionID);
                                String newPlanID = changeSubscriptionModel.getNewPlan().getPlanId();
                                bundle.putString("plan_id", newPlanID);
                                Fragment fragment = new ChangeSubscriptionPaymentFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.navigation_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        }
                    }
                }
            }

        });
    }

    private void init() {
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        backArrow = view1.findViewById(R.id.backBtn);
        btnCheckout = view1.findViewById(R.id.btnCheckoutTransaction);
        btnCancel = view1.findViewById(R.id.btnCancelTransaction);

        currentPlanPricePerMonth = view1.findViewById(R.id.currentPlanPricePerMonth);
        mTextViewCurrentPlanOFF = view1.findViewById(R.id.mTextViewCurrentPlanOFF);
        mTextViewCurrentPlanDuration = view1.findViewById(R.id.mTextViewCurrentPlanDuration);
        mTextViewCurrentPlanPrice = view1.findViewById(R.id.mTextViewCurrentPlanPrice);
        cardMTV = view1.findViewById(R.id.cardMTV);

        mTextViewNewPlanOFF = view1.findViewById(R.id.mTextViewNewPlanOFF);
        newPlanPricePerMonth = view1.findViewById(R.id.newPlanPricePerMonth);
        mTextViewNewPlanDuration = view1.findViewById(R.id.mTextViewNewPlanDuration);
        mTextViewNewPlanPrice = view1.findViewById(R.id.mTextViewNewPlanPrice);
        mTextViewPlanDurationPriceCardView2 = view1.findViewById(R.id.mTextViewPlanDurationPriceCardView2);

        mTextViewPlanDurationPriceCardView2.setPaintFlags(mTextViewPlanDurationPriceCardView2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mTextViewOrderPlanPrice = view1.findViewById(R.id.mTextViewOrderPlanPrice);
        mTextViewOrderReimbursmentPrice = view1.findViewById(R.id.mTextViewOrderReimbursmentPrice);
        mTextViewOrderAmountExcludedPrice = view1.findViewById(R.id.mTextViewOrderAmountExcludedPrice);
        mTextViewOrderAmountIncludePrice = view1.findViewById(R.id.mTextViewOrderAmountIncludePrice);
        mTextViewOrderTotalAmountPrice = view1.findViewById(R.id.mTextViewOrderTotalAmountPrice);

        /*textViewChoosePlan = view1.findViewById(R.id.textViewChoosePlan);*/
        mTextViewCurrentPlan = view1.findViewById(R.id.mTextViewCurrentPlan);
        mTextViewNewPlan = view1.findViewById(R.id.mTextViewNewPlan);
        mTextViewTransactionDetails = view1.findViewById(R.id.mTextViewTransactionDetails);
        mTextViewOrderSummary = view1.findViewById(R.id.mTextViewOrderSummary);
        mTextViewOrderPlanType = view1.findViewById(R.id.mTextViewOrderPlanType);
        mTextViewOrderReimbursment = view1.findViewById(R.id.mTextViewOrderReimbursment);
        mTextViewOrderAmountExcluded = view1.findViewById(R.id.mTextViewOrderAmountExcluded);
        mTextViewOrderAmountInclude = view1.findViewById(R.id.mTextViewOrderAmountInclude);
        mTextViewOrderTotalAmount = view1.findViewById(R.id.mTextViewOrderTotalAmount);


        setLanguageToWidget();

        Bundle bundle = getArguments();
        planID = bundle.getString("planID");

        if (SharedData.token == null) {
            SharedData.token = SessionUtil.getAccessToken(getContext());
        }

        planDetailsList.clear();

        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);

        /*planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");
        planDetailsList.add("Hassale free for 1 months");*/
        mPlanDescriptionRecyclerView = view1.findViewById(R.id.newPlanItemDetailsRecyclerview);
        mPlanDescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getContext() != null) {
            adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
        }
        mPlanDescriptionRecyclerView.setAdapter(adapter);
        //listener for back button
        backArrow.setOnClickListener(v -> {
            /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }*/
            onBackPress();
        });
        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            blurrBackground();
            StartLoading();
            getChosenPlanDetailsFromServer();
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

    void getChosenPlanDetailsFromServer() {
        Call<ChangeSubscriptionModel> call = ApiClient.getService().getChosenPlanDetails("Bearer " + SharedData.token, planID);
        call.enqueue(new Callback<ChangeSubscriptionModel>() {
            @Override
            public void onResponse(Call<ChangeSubscriptionModel> call, Response<ChangeSubscriptionModel> response) {
                if (response.isSuccessful()) {
                    changeSubscriptionModel = response.body();
                    showDataToUI();
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
                            if (errorMessageModel != null && errorMessageModel.getError() != null) {
                                Toast.makeText(getContext(), errorMessageModel.getError(), Toast.LENGTH_SHORT).show();
                            } else if (errorMessageModel != null && errorMessageModel.getErrors() != null &&
                                    errorMessageModel.getErrors().getPlanID() != null && errorMessageModel.getErrors().getPlanID().size() > 0) {
                                Toast.makeText(getContext(), errorMessageModel.getErrors().getPlanID().get(0), Toast.LENGTH_SHORT).show();
                            } else if (errorMessageModel != null && errorMessageModel.getMessage() != null) {
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
                    StopLoading();
                }

            }

            @Override
            public void onFailure(Call<ChangeSubscriptionModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                StopLoading();

            }
        });
    }

    void showDataToUI() {
        if (changeSubscriptionModel != null) {
            if (changeSubscriptionModel.getCurrentPlan() != null) {
                if (changeSubscriptionModel.getCurrentPlan().getDiscount() != null && changeSubscriptionModel.getCurrentPlan().getDiscount() > 0) {
                    mTextViewCurrentPlanOFF.setVisibility(View.VISIBLE);
                    mTextViewCurrentPlanOFF.setText("" + changeSubscriptionModel.getCurrentPlan().getDiscount() + "% " + resources.getString(R.string.off));
                } else {
                    mTextViewCurrentPlanOFF.setVisibility(View.INVISIBLE);
                }
                String intervalCount = changeSubscriptionModel.getCurrentPlan().getIntervalCount() == null ? "1" : String.valueOf(changeSubscriptionModel.getCurrentPlan().getIntervalCount());
                currentPlanPricePerMonth.setText(changeSubscriptionModel.getCurrentPlan().getCurrency().toUpperCase() + " " + (Integer.parseInt(changeSubscriptionModel.getCurrentPlan().getAmount()) / Integer.parseInt(intervalCount)) + "/" + resources.getString(R.string.month));
                mTextViewCurrentPlanDuration.setText(/*changeSubscriptionModel.getCurrentPlan()*/"");
                mTextViewCurrentPlanPrice.setText(changeSubscriptionModel.getCurrentPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getCurrentPlan().getAmount());
                String cardText = cardMTV.getText().toString();
                String cardNo = (changeSubscriptionModel.getCurrentPlan().getCardNo() == null) ? "**** **** **** 1234" : changeSubscriptionModel.getCurrentPlan().getCardNo();
                cardMTV.setText(cardText + " " + cardNo);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "changeSubscriptionModel.getCurrentPlan() is null");
                }
            }
            if (changeSubscriptionModel.getNewPlan() != null) {
                if (changeSubscriptionModel.getNewPlan().getDiscount() != null && changeSubscriptionModel.getNewPlan().getDiscount() > 0) {
                    mTextViewNewPlanOFF.setVisibility(View.VISIBLE);
                    mTextViewNewPlanOFF.setText("" + changeSubscriptionModel.getNewPlan().getDiscount() + "% " + resources.getString(R.string.off));
                } else {
                    mTextViewNewPlanOFF.setVisibility(View.INVISIBLE);
                }
                if (changeSubscriptionModel.getNewPlan().getAmount() != null && changeSubscriptionModel.getNewPlan().getCurrency() != null) {
                    String intervalCount = changeSubscriptionModel.getNewPlan().getIntervalCount() == null ? "1" : String.valueOf(changeSubscriptionModel.getNewPlan().getIntervalCount());
                    newPlanPricePerMonth.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + (Integer.parseInt(changeSubscriptionModel.getNewPlan().getAmount()) / Integer.parseInt(intervalCount)) + "/" + resources.getString(R.string.month));
                    mTextViewNewPlanPrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getNewPlan().getAmount());

                    mTextViewOrderPlanPrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getNewPlan().getAmount());
                }

                String planDuration = changeSubscriptionModel.getNewPlan().getIntervalName() == null ? "" : changeSubscriptionModel.getNewPlan().getIntervalName();
                mTextViewNewPlanDuration.setText(planDuration);
                if (changeSubscriptionModel.getNewPlan().getCurrency() != null && changeSubscriptionModel.getNewPlan().getOriginalPrice() != null) {
                    mTextViewPlanDurationPriceCardView2.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getNewPlan().getOriginalPrice());
                }

                if (changeSubscriptionModel.getNewPlan().getDescription() != null) {
                    planDetailsList.addAll(changeSubscriptionModel.getNewPlan().getDescription());
                    if (getContext() != null) {
                        adapter = new PlanItemsDetailsAdapter(getContext(), planDetailsList); // Pass the activity context and data list to the adapter
                    }
                    mPlanDescriptionRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                if (changeSubscriptionModel.getNewPlan().getCurrency() != null && changeSubscriptionModel.getNewPlan().getExclTax() != null
                        && changeSubscriptionModel.getNewPlan().getInclTax() != null && changeSubscriptionModel.getNewPlan().getAmount() != null
                        && !changeSubscriptionModel.getNewPlan().getAmount().matches("")) {
                    Integer reimbursment = changeSubscriptionModel.getNewPlan().getReimbursement() == null ? 0 : changeSubscriptionModel.getNewPlan().getReimbursement();
                    mTextViewOrderReimbursmentPrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + reimbursment);
                    mTextViewOrderAmountExcludedPrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getNewPlan().getExclTax());
                    mTextViewOrderAmountIncludePrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + changeSubscriptionModel.getNewPlan().getInclTax());
                    int totalPrice = Integer.parseInt(changeSubscriptionModel.getNewPlan().getAmount()) + changeSubscriptionModel.getNewPlan().getExclTax() + changeSubscriptionModel.getNewPlan().getInclTax();
                    mTextViewOrderTotalAmountPrice.setText(changeSubscriptionModel.getNewPlan().getCurrency().toUpperCase() + " " + totalPrice);
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "changeSubscriptionModel.getNewPlan() is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "changeSubscriptionModel is null");
            }
        }
        StopLoading();
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

    void setLanguageToWidget() {
        if (getContext() != null) {
            //textViewChoosePlan.setText(resources.getString(R.string.change_subscription_subtitle));
            mTextViewCurrentPlan.setText(resources.getString(R.string.current_plan_text));
            mTextViewNewPlan.setText(resources.getString(R.string.new_plan_text));
            mTextViewTransactionDetails.setText(resources.getString(R.string.transaction_details_text));
            mTextViewOrderSummary.setText(resources.getString(R.string.order_summery_text));
            mTextViewOrderPlanType.setText(resources.getString(R.string.new_order_plan_type));
            mTextViewOrderReimbursment.setText(resources.getString(R.string.reimbursment_of_previous_plan_text));
            mTextViewOrderAmountExcluded.setText(resources.getString(R.string.total_payable_amount_excl_of_taxes_text));
            mTextViewOrderAmountInclude.setText(resources.getString(R.string.total_payable_amount_incl_of_taxes_text));
            mTextViewOrderTotalAmount.setText(resources.getString(R.string.total_payment_text));
            btnCancel.setText(resources.getString(R.string.btn_text_transaction_cancel));
            btnCheckout.setText(resources.getString(R.string.btn_text_checkout));
        }
    }
}