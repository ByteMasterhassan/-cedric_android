package com.cedricapp.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.activity.FoodPreferencesActivity;
import com.cedricapp.adapters.AddedCardsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.AddCardsItemClickListener;
import com.cedricapp.model.UpgradeDowngradeSubscriptionModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.activity.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ChangeSubscriptionPaymentFragment extends Fragment implements AddCardsItemClickListener {

    private View view1;
    private RecyclerView mAddedCardsRecyclerView;
    private AddedCardsAdapter adapter;
    private ArrayList<String> addCardList = new ArrayList<>();
    private ImageButton backArrow;
    boolean backPress = true;
    private int selectedItemPosition = -1;

    private UpgradeDowngradeSubscriptionModel upgradeDowngradeSubscriptionModel;

    private MaterialButton btnCheckoutTransaction;

    private String token, oldSubscriptionID, newPlanID;

    private String TAG = "ChangeSubscriptionPaymentFragment_TAG";

    LottieAnimationView loading_lav;

    BlurView blurView;

    Resources resources;

    MaterialTextView blurBkgTxt;

    int DELAY = 2000;


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

    public ChangeSubscriptionPaymentFragment() {
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
        return inflater.inflate(R.layout.fragment_change_subscription_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        init();
    }

    private void init() {
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        mAddedCardsRecyclerView = view1.findViewById(R.id.addedCardRecyclerView);
        mAddedCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        backArrow = view1.findViewById(R.id.backBtn);
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        btnCheckoutTransaction = view1.findViewById(R.id.btnCheckoutTransaction);
        blurBkgTxt = view1.findViewById(R.id.blurBkgTxt);
        if (SharedData.token != null) {
            token = SharedData.token;
        } else {
            token = SessionUtil.getAccessToken(getContext());
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            oldSubscriptionID = bundle.getString("subscription_id");
            newPlanID = bundle.getString("plan_id");
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Old SubscriptionID: " + oldSubscriptionID + "\nNew Plan ID: " + newPlanID);
            }
        }



        /*addCardList.add("MasterCard");
        addCardList.add("Visa");
        addCardList.add("MasterCard");*/


        //listener for back button
        backArrow.setOnClickListener(v -> {
            /*if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }*/
            onBackPress();
        });

        if (getContext() != null) {
            adapter = new AddedCardsAdapter(getContext(), addCardList, this);
        }
        mAddedCardsRecyclerView.setAdapter(adapter);

        btnCheckoutTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    if (oldSubscriptionID != null && newPlanID != null) {
                        blurrBackground();
                        StartLoading();
                        upgradeOrDowngrade();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void upgradeOrDowngrade() {
        Call<UpgradeDowngradeSubscriptionModel> call = ApiClient.getService().upgradeOrDowngradeSubscription("Bearer " + token, oldSubscriptionID, newPlanID);
        call.enqueue(new Callback<UpgradeDowngradeSubscriptionModel>() {
            @Override
            public void onResponse(Call<UpgradeDowngradeSubscriptionModel> call, Response<UpgradeDowngradeSubscriptionModel> response) {
                try {
                    if (response.isSuccessful()) {
                        upgradeDowngradeSubscriptionModel = response.body();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Response UpgradeDowngradeSubscriptionModel:  " + upgradeDowngradeSubscriptionModel.toString());
                        }
                        if (upgradeDowngradeSubscriptionModel != null) {
                            if (upgradeDowngradeSubscriptionModel.getData() != null) {
                                if (upgradeDowngradeSubscriptionModel.getData().getSubscriptionId() != null) {
                                    if (isAdded() && getContext() != null) {
                                        SessionUtil.saveSubscriptionID(getContext(), upgradeDowngradeSubscriptionModel.getData().getSubscriptionId());
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "!isAdded() && getContext() == null");
                                        }
                                    }
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "upgradeDowngradeSubscriptionModel.getData().getSubscriptionId()==null");
                                    }
                                }
                                if (upgradeDowngradeSubscriptionModel.getData().getPeriodStarts() != null &&
                                        upgradeDowngradeSubscriptionModel.getData().getPeriodEnds() != null &&
                                        upgradeDowngradeSubscriptionModel.getData().getTrialEndsAt() != null) {
                                    if (isAdded() && getContext() != null) {
                                        SessionUtil.saveSubscription(getContext(), upgradeDowngradeSubscriptionModel.getData().getPeriodStarts(), upgradeDowngradeSubscriptionModel.getData().getPeriodEnds());
                                        SessionUtil.saveEndTrialDate(getContext(), upgradeDowngradeSubscriptionModel.getData().getTrialEndsAt());
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
                                                                        intent.putExtra("subscription_changed",true);
                                                                        StopLoading();
                                                                        startActivity(intent);
                                                                    }
                                                                }, DELAY);
                                                            }
                                                        }, DELAY);

                                                    }
                                                },DELAY);

                                            }
                                        }, DELAY);
                                        /*startActivity(new Intent(getContext(), HomeActivity.class));*/

                                    }else{
                                        StopLoading();
                                    }
                                }else{
                                    StopLoading();
                                }
                            } else {
                                StopLoading();
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "upgradeDowngradeSubscriptionModel.getData() == null");
                                }
                            }
                        } else {
                            StopLoading();
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "upgradeDowngradeSubscriptionModel == null");
                            }
                        }
                    } else if (response.code() == 401) {
                        StopLoading();
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        StopLoading();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response is unsuccessfull ");
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpgradeDowngradeSubscriptionModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                StopLoading();
                try {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void onBackPress() {

        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                if (isAdded()) {
                    getFragmentManager().popBackStack();
                }
            }
        } else {
            Toast.makeText(getContext(), getActivity().getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if (getContext() != null) {
            // Change the stroke color of the clicked item
            //System.out.println("position" + position);
            //  view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_item_background));
            View itemView = mAddedCardsRecyclerView.getChildAt(position);

            // Get the ConstraintLayout by its ID
            ConstraintLayout layout = itemView.findViewById(R.id.mCardsConstraintLayout);
            MaterialTextView mCardName = itemView.findViewById(R.id.cardName);


            Drawable background = layout.getBackground();
            if (selectedItemPosition == position) {
                // If the clicked item is already selected, deselect it
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_color));
                selectedItemPosition = -1;

            } else {
                // Otherwise, select the new item and deselect the previous selection
                if (selectedItemPosition != -1) {
                    View previousItemView = mAddedCardsRecyclerView.getChildAt(selectedItemPosition);
                    ConstraintLayout previousLayout = previousItemView.findViewById(R.id.mCardsConstraintLayout);
                    previousLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_color));
                }

                GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.selected_item_background);
                int newStrokeColor = ContextCompat.getColor(view.getContext(), R.color.yellow);
                shapeDrawable.setStroke(4 /* stroke width */, newStrokeColor /* stroke color */);
                layout.setBackground(shapeDrawable);

                selectedItemPosition = position;
            }
            adapter.notifyDataSetChanged();
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