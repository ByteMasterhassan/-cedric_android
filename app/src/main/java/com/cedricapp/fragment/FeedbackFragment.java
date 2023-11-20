package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class FeedbackFragment extends Fragment {


    private TextInputLayout mTextInputLayoutFeedbackType;
    private Boolean mStateChanged = true;
    private ImageButton backArrow;
    private AutoCompleteTextView mFeedbackType;
    ArrayList<String> arrayList_feedbackType;
    ArrayAdapter<String> arrayAdapter_feedbackType;
    private Button mFeedBackSubmitButton;
    private MaterialTextView mTextViewRate1, mTextViewRate2, mTextViewRate3, mTextViewRate4, mTextViewRate5, mTextViewRate6,
            mTextViewRate7, mTextViewRate8, mTextViewRate9, mTextViewRate10;
    private TextInputEditText mFeedbackDescription;
    private View view1;
    private String rating;
    private String suggestionType, description, token, user_id;
    private Context mContext;
    BlurView blurView;
    LottieAnimationView loading_lav;
    Resources resources;
    private String message;

    String TAG = "FEEDBACK_FRAGMENT_TAG";

    MaterialTextView feedbackTitle, textViewImprove, textViewSatisfaction;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        Init();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });


        //listener for textViews
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textViewRate1) {
                    if (mStateChanged) {
                        // mTextViewBeginner.setTextColor(Color.BLACK);
                        v.setBackgroundResource(R.color.yellow);

                        //mTextViewAdvance.setTextColor(Color.WHITE);
                        mTextViewRate2.setBackgroundResource(R.color.transparent);
                        mTextViewRate3.setBackgroundResource(R.color.transparent);
                        mTextViewRate4.setBackgroundResource(R.color.transparent);
                        mTextViewRate5.setBackgroundResource(R.color.transparent);
                        mTextViewRate6.setBackgroundResource(R.color.transparent);
                        mTextViewRate7.setBackgroundResource(R.color.transparent);
                        mTextViewRate8.setBackgroundResource(R.color.transparent);
                        mTextViewRate9.setBackgroundResource(R.color.transparent);
                        mTextViewRate10.setBackgroundResource(R.color.transparent);
                        rating = mTextViewRate1.getText().toString();


                    }
                }
                if (v.getId() == R.id.textViewRate2) {
                    if (mStateChanged) {
                        v.setBackgroundResource(R.color.yellow);
                        mTextViewRate1.setBackgroundResource(R.color.transparent);
                        mTextViewRate3.setBackgroundResource(R.color.transparent);
                        mTextViewRate4.setBackgroundResource(R.color.transparent);
                        mTextViewRate5.setBackgroundResource(R.color.transparent);
                        mTextViewRate6.setBackgroundResource(R.color.transparent);
                        mTextViewRate7.setBackgroundResource(R.color.transparent);
                        mTextViewRate8.setBackgroundResource(R.color.transparent);
                        mTextViewRate9.setBackgroundResource(R.color.transparent);
                        mTextViewRate10.setBackgroundResource(R.color.transparent);
                        rating = mTextViewRate2.getText().toString();
                    }
                }
                if (v.getId() == R.id.textViewRate3) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate3.getText().toString();
                }
                if (v.getId() == R.id.textViewRate4) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate4.getText().toString();
                }
                if (v.getId() == R.id.textViewRate5) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate5.getText().toString();
                }
                if (v.getId() == R.id.textViewRate6) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate6.getText().toString();
                }
                if (v.getId() == R.id.textViewRate7) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate7.getText().toString();
                }
                if (v.getId() == R.id.textViewRate8) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate8.getText().toString();
                }
                if (v.getId() == R.id.textViewRate9) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);
                    mTextViewRate10.setBackgroundResource(R.color.transparent);
                    rating = mTextViewRate9.getText().toString();
                }
                if (v.getId() == R.id.textViewRate10) {
                    v.setBackgroundResource(R.color.yellow);
                    mTextViewRate1.setBackgroundResource(R.color.transparent);
                    mTextViewRate2.setBackgroundResource(R.color.transparent);
                    mTextViewRate4.setBackgroundResource(R.color.transparent);
                    mTextViewRate5.setBackgroundResource(R.color.transparent);
                    mTextViewRate6.setBackgroundResource(R.color.transparent);
                    mTextViewRate7.setBackgroundResource(R.color.transparent);
                    mTextViewRate8.setBackgroundResource(R.color.transparent);
                    mTextViewRate9.setBackgroundResource(R.color.transparent);
                    mTextViewRate3.setBackgroundResource(R.color.transparent);

                    rating = mTextViewRate10.getText().toString();
                }
            }
        };
        mTextViewRate1.setOnClickListener(listener);
        mTextViewRate2.setOnClickListener(listener);
        mTextViewRate3.setOnClickListener(listener);
        mTextViewRate4.setOnClickListener(listener);
        mTextViewRate5.setOnClickListener(listener);
        mTextViewRate6.setOnClickListener(listener);
        mTextViewRate7.setOnClickListener(listener);
        mTextViewRate8.setOnClickListener(listener);
        mTextViewRate9.setOnClickListener(listener);
        mTextViewRate10.setOnClickListener(listener);
    }

    private void Init() {
        mContext = getContext();
        user_id = SharedData.id;
        token = SharedData.token;
        backArrow = view1.findViewById(R.id.backArrow);
        mTextViewRate1 = view1.findViewById(R.id.textViewRate1);
        mTextViewRate2 = view1.findViewById(R.id.textViewRate2);
        mTextViewRate3 = view1.findViewById(R.id.textViewRate3);
        mTextViewRate4 = view1.findViewById(R.id.textViewRate4);
        mTextViewRate5 = view1.findViewById(R.id.textViewRate5);
        mTextViewRate6 = view1.findViewById(R.id.textViewRate6);
        mTextViewRate7 = view1.findViewById(R.id.textViewRate7);
        mTextViewRate8 = view1.findViewById(R.id.textViewRate8);
        mTextViewRate9 = view1.findViewById(R.id.textViewRate9);
        mTextViewRate10 = view1.findViewById(R.id.textViewRate10);
        mFeedbackDescription = view1.findViewById(R.id.editTextDescription);
        mFeedBackSubmitButton = view1.findViewById(R.id.btnFeedbackSubmit);

        feedbackTitle = view1.findViewById(R.id.feedbackTitle);
        textViewImprove = view1.findViewById(R.id.textViewImprove);
        textViewSatisfaction = view1.findViewById(R.id.textViewSatisfaction);


        feedbackTitle.setText(resources.getString(R.string.feedback));
        textViewImprove.setText(resources.getString(R.string.your_opinion_is_important_to_us_this_way_we_n_can_keep_improving_our_app));
        textViewSatisfaction.setText(resources.getString(R.string.your_overall_satisfaction_of_the_app));


        mTextInputLayoutFeedbackType = view1.findViewById(R.id.textInputLayoutFeedback);
        mFeedbackType = view1.findViewById(R.id.feedbackType);
        genderDropDown();

        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        resources = Localization.setLanguage(getContext(), getResources());
        mFeedBackSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnectedWithInternet(mContext)) {
                    suggestionType = mFeedbackType.getText().toString();
                    description = mFeedbackDescription.getText().toString();

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Suggestion Type: " + suggestionType);
                        Log.d(TAG, "Rating: " + rating);
                        Log.d(TAG, "Description: " + description);
                        Log.d(TAG, "User ID: " + user_id);
                    }

                    if (rating != null && !description.equals("") && !suggestionType.equals("")) {
                        sendFeedback(token, user_id, rating, suggestionType, description);
                    } else {
                        if (resources != null)
                            Toast.makeText(getContext(), resources.getString(R.string.please_filled_field), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (resources != null)
                        ToastUtil.showToastForFragment(getContext(), false, isAdded(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);

                }
            }
        });
    }

    private void sendFeedback(String token, String user_id, String rating, String suggestionType, String description) {
        blurrBackground();
        startLoading();

        Call<SignupResponse> call = ApiClient.getService().feedbackAboutApp("Bearer " + token, user_id, rating, suggestionType, description);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                stopLoading();
                redirectToThankYouFragment();
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null)
                            if (response.body().isStatus() == true) {
                                if (Common.isLoggingEnabled) {
                                    if (response.body().getMessage() != null) {
                                        Log.d(TAG, "" + response.body().getMessage().toString());
                                    }
                                }
                                //redirectToThankYouFragment();
                                //ToastUtil.showToastForFragment(getContext(),false,isAdded(),""+response.body().getMessage().toString(),Toast.LENGTH_SHORT);

                                //  Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            } else if (response.body().isStatus() == false) {
                                ToastUtil.showToastForFragment(getContext(), false, isAdded(), "" + response.body().getMessage().toString(), Toast.LENGTH_SHORT);
                                //Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("FeedbackFragment_sendFeedback_API",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("FeedbackFragment_sendFeedback_API",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();

            }
        });
    }

    void redirectToThankYouFragment() {
        Fragment fragment = new FeedbackThankYouFragment();
        FragmentTransaction mFragmentTransaction = ((FragmentActivity) getContext())
                .getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.navigation_container, fragment);
        //  mFragmentTransaction.addToBackStack();
        mFragmentTransaction.commit();
    }


    private void genderDropDown() {
        //gender.setDropDownBackgroundDrawable(Drawable.createFromPath("#FFC153"));
        // gender.setText("Male");
        arrayList_feedbackType = new ArrayList<>();
        if (resources != null) {
            arrayList_feedbackType.add(resources.getString(R.string.question));
            arrayList_feedbackType.add(resources.getString(R.string.suggestion));
            arrayList_feedbackType.add(resources.getString(R.string.others));
        } else {
            arrayList_feedbackType.add(getString(R.string.question));
            arrayList_feedbackType.add(getString(R.string.suggestion));
            arrayList_feedbackType.add(getString(R.string.others));
        }
        arrayAdapter_feedbackType = new ArrayAdapter<>(mContext, R.layout.dropdown_item
                , arrayList_feedbackType);
        mFeedbackType.setAdapter(arrayAdapter_feedbackType);
        mFeedbackType.setEnabled(false);
        //   gender.setThreshold();

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
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction

        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("FeedbackFragment_ActivityNotBounnd",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }
}