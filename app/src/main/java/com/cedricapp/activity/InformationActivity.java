package com.cedricapp.activity;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.R.string.weight_error;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.ProfileAPI_Callback;
import com.cedricapp.model.ErrorMessageModel;
import com.cedricapp.model.ProfileActivation;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ProfileUpdateUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class InformationActivity extends AppCompatActivity implements ProfileAPI_Callback {

    private MaterialButton mInformationButton;
    private TextInputEditText mEditTextWeight, mEditTextAge;
    /*  private AutoCompleteTextView gender;*/
    private static TextInputEditText mEditTextHeight;
    private RadioGroup mGenderGroupButton;
    private ImageButton back_btn;
    private RadioButton mGenderMale, mGenderFemale, mSelectedRadioButton;
    ArrayList<String> arrayList_gender;
    ArrayAdapter<String> arrayAdapter_gender;
    private TextInputLayout mTextInputLayoutGender;
    private MaterialCardView cardView;
    MaterialTextView materialTextViewLayout;
    private String id, unitType, email, name, refresh_token, token;
    private CrashlyticsCore Crashlytics;
    String gender, numberD;
    private SegmentedProgressBar segmentedProgressBar;
    private long mLastClickTime = 0;
    int dotCount = 0;

    BlurView blurView;
    LottieAnimationView loading_lav;

    MaterialTextView textViewInformation;

    Resources resources;

    String TAG = "INFORMATION_ACTIVITY_TAG";

    boolean isvalidate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //resources = Localization.setLanguage(InformationActivity.this, getResources());
        resources = getResources();
        getWindow().setBackgroundDrawableResource(R.drawable.ic_information_bg);
        //for background image
        getWindow().setBackgroundDrawableResource(R.drawable.ic_information_bg);
        //for hidding keypad on activity Start
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

        }

        // get intent data
        getIntentData();
        // initiaze all id's and set
        initialize();
        // check unit type
        checkType();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);


        //For Dropdown icon
        // genderDropDown();

        //listener for Informaion Button
        mInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInformationButton.startAnimation(myAnim);

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isvalidate)
                    saveUserData();
            }
        });
    }


   /* private void genderDropDown() {
        gender.setDropDownBackgroundDrawable(Drawable.createFromPath("#FFC153"));
        // gender.setText("Male");
        arrayList_gender = new ArrayList<>();
        arrayList_gender.add("Male");
        arrayList_gender.add("Female");
        arrayAdapter_gender = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item
                , arrayList_gender);
        gender.setAdapter(arrayAdapter_gender);
        //   gender.setThreshold();
    }*/


    private void initialize() {

        materialTextViewLayout = findViewById(R.id.textView);
        mEditTextHeight = findViewById(R.id.editTextHeight);
        mEditTextWeight = findViewById(R.id.editTextWeight);
        mEditTextAge = findViewById(R.id.editTextAge);
        mGenderGroupButton = findViewById(R.id.radio_Group);
        mGenderFemale = findViewById(R.id.rb_option_female);
        mGenderMale = findViewById(R.id.rb_option_male);
        mInformationButton = findViewById(R.id.btnInformationNext);
        back_btn = findViewById(R.id.backBtn);
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(2);

        blurView = findViewById(R.id.blurViewInfo);
        loading_lav = findViewById(R.id.loading_lavInfo);

        textViewInformation = findViewById(R.id.textViewInformation);

        setlanguageToWidget();

        mEditTextHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextHeight.setHint("");
                } else {
                    checkType();
                }
            }
        });

        mEditTextWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextWeight.setHint("");
                } else {
                    checkType();
                }
            }
        });

        mEditTextAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextAge.setHint("");
                } else {
                    mEditTextAge.setHint(resources.getString(R.string.age));
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
       /* mTextInputLayoutGender = findViewById(R.id.textInputLayoutGender);
        gender = findViewById(R.id.gender);*/
        checkValidationsLive();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }*/
    }

    void setlanguageToWidget() {
        textViewInformation.setText(resources.getString(R.string.information));
        mGenderMale.setText(resources.getString(R.string.male));
        mGenderFemale.setText(resources.getString(R.string.female));
        mEditTextHeight.setHint(resources.getString(R.string.height));
        mEditTextWeight.setHint(resources.getString(R.string.weight));
        mEditTextAge.setHint(resources.getString(R.string.age));
        mInformationButton.setText(resources.getString(R.string.next));
    }

    private void checkValidationsLive() {
        // mEditTextWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 0)});
        mEditTextAge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 0)});
        mEditTextHeight.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (unitType != null) {
                    switch (unitType) {
                        case "Imperial":
                            checkImperial();
                            break;
                        case "Kejserlig":
                            checkImperial();
                            break;

                        case "Metric":
                            checkMetric();
                            break;
                        case "Metrisk":
                            checkMetric();
                            break;
                        default:
                            break;
                    }
                }
                /*if (mEditTextHeight.getText().length() > 3) {


                    System.out.println(numberD + "this is digit after decimal");


                } else if (mEditTextHeight.getText().length() > 2) {

                    if ((unitType.matches("Imperial")) && ((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3.0)
                            || (Float.parseFloat(mEditTextHeight.getText().toString()) > 8.0))) {
                        mEditTextHeight.setError(getResources().getString(R.string.invalid_height));
                    } else if ((unitType.matches("Metric")) && ((Float.parseFloat(mEditTextHeight.getText().toString()) < 100.0)
                            || (Float.parseFloat(mEditTextHeight.getText().toString()) > 272.0))) {
                        mEditTextHeight.setError(getResources().getString(R.string.invalid_height_cm));


                    }
                } else if (mEditTextHeight.getText().length() == 2) {
                    if ((unitType.matches("Imperial"))) {
                        mEditTextHeight.setError(getResources().getString(R.string.invalid_height));
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (unitType.equals(getString(R.string.imperial))) {
                    if (editable.length() == 1) {
                        if (dotCount == 0) {
                            dotCount++;
                            mEditTextHeight.append(".");
                        }
                    } else if (editable.length() == 0) {
                        dotCount = 0;
                    }
                    mEditTextHeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(1, 2)});

                } else {
                    mEditTextHeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
                }
            }
        });

        // =======validation for weight

        mEditTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (unitType != null) {
                    if (unitType.matches(resources.getString(R.string.imperial))) {
                        mEditTextWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    } else if (unitType.matches(resources.getString(R.string.metric))) {
                        mEditTextWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextWeight.getText().length() > 0) {
                    if (unitType.matches(resources.getString(R.string.imperial))) {
                        if ((Float.parseFloat(mEditTextWeight.getText().toString()) <= 66.1)
                                || (Float.parseFloat(mEditTextWeight.getText().toString()) >= 881.8)) {
                            mEditTextWeight.setError(resources.getString(R.string.invalid_weight_Metric));
                            mEditTextWeight.requestFocus();
                        }
                    } else if (unitType.matches(resources.getString(R.string.metric))) {
                        if ((Float.parseFloat(mEditTextWeight.getText().toString()) <= 30)
                                || (Float.parseFloat(mEditTextWeight.getText().toString()) >= 400)) {
                            mEditTextWeight.setError(resources.getString(R.string.invalid_weight));
                            mEditTextWeight.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (unitType.matches(resources.getString(R.string.imperial))) {
                    mEditTextWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2 | 3, 2)});
                } else {
                    if (unitType.matches(resources.getString(R.string.metric))) {
                        mEditTextWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 0)});
                    }
                }/* else {
                        mEditTextWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

                    }*/
                // }
            }
        });

        // =======validation for age

        mEditTextAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mEditTextAge.getText().length() > 0) {
                    if (Integer.parseInt(mEditTextAge.getText().toString()) <= Integer.parseInt("12") ||
                            (Integer.parseInt(mEditTextAge.getText().toString()) >= Integer.parseInt("90"))) {
                        mEditTextAge.setError(resources.getString(R.string.invalid_age));
                        //System.out.println("Hello are youdddddddddddd there");

                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // =======validation for gender

      /*  gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTextInputLayoutGender.setEndIconVisible(true);
                if (gender.getText().toString().isEmpty()) {
                    gender.setError(getResources().getString(R.string.gender_error));

                    mTextInputLayoutGender.setEndIconVisible(false);
                    System.out.println("Hello are you thejjjjjjjjjre");
                } else if (gender.getText().toString().length() > 0) {
                    mTextInputLayoutGender.setEndIconVisible(true);
                    gender.setError(null);

                   // mTextInputLayoutGender.setErrorEnabled(false);

                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
    }

    void checkImperial() {
        if (mEditTextHeight.getText().length() > 3) {
            numberD = String.valueOf(mEditTextHeight.getText().toString());
            numberD = numberD.substring(numberD.indexOf(".")).substring(1);
            //mEditTextHeight.setError(getResources().getString(R.string.invalid_height));

            if ((Integer.parseInt(numberD) == 12)) {

                Double h = Double.valueOf(mEditTextHeight.getText().toString());
                int a = h.intValue();
                String hh = String.valueOf(a + 1);
                mEditTextHeight.setText(hh);

                mEditTextHeight.append("." + "0");
                if (!mEditTextHeight.getText().toString().matches("")) {
                    if (((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3.0)
                            || ((Float.parseFloat(mEditTextHeight.getText().toString()) >= 8.0))
                            || ((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3)
                            || (Float.parseFloat(mEditTextHeight.getText().toString()) >= 8)))) {
                        mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                        isvalidate = false;

                    } else {
                        isvalidate = true;
                    }
                } else {
                    mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                    isvalidate = false;
                }
            } else if (Integer.parseInt(numberD) > 12) {
                String seperated[] = mEditTextHeight.getText().toString().split("\\.");
                if (seperated.length > 0) {
                    String firstDigit = seperated[0];
                    mEditTextHeight.setText(firstDigit + "." + (numberD.charAt(0)));
                    mEditTextHeight.setSelection(mEditTextHeight.getText().length());
                    isvalidate = false;
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "seperated.length==0");
                        Log.e(TAG, "Height: " + mEditTextHeight.getText().toString());
                        Log.e(TAG, "seperated: " + seperated.toString());
                    }
                    isvalidate = true;
                }
                return;
            }
        } else if (mEditTextHeight.getText().length() > 2) {
            if (((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3.0)
                    || ((Float.parseFloat(mEditTextHeight.getText().toString()) >= 8.0)))) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                isvalidate = false;
            } else {
                isvalidate = true;
            }
        } else if (mEditTextHeight.getText().length() <= 2) {
            if (!mEditTextHeight.getText().toString().matches("") &&
                    !mEditTextHeight.getText().toString().matches(".") &&
                    !mEditTextHeight.getText().toString().matches(",")) {
                if ((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3.0) || (Float.parseFloat(mEditTextHeight.getText().toString()) >= 8)) {
                    mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                    isvalidate = false;
                }
                int heightLength = mEditTextHeight.getText().toString().length();
                if (heightLength > 0)
                    if (mEditTextHeight.getText().toString().charAt(heightLength - 1) == '.') {
                        mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                        isvalidate = false;
                    } else {
                        isvalidate = true;
                    }
            } else {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                isvalidate = false;
            }
        }
    }

    void checkMetric() {
        if (mEditTextHeight.getText().length() > 3) {
            if (((Float.parseFloat(mEditTextHeight.getText().toString()) <= 100.0)
                    || (Float.parseFloat(mEditTextHeight.getText().toString()) >= 272.0))) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height_cm));
                isvalidate = false;
            } else {
                isvalidate = true;
            }
        } else if (mEditTextHeight.getText().length() > 2) {
            if (((Float.parseFloat(mEditTextHeight.getText().toString()) <= 100.0)
                    || (Float.parseFloat(mEditTextHeight.getText().toString()) >= 272.0))) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height_cm));
                isvalidate = false;
            } else {
                isvalidate = true;
            }
        } else if (mEditTextHeight.getText().length() <= 2) {
            mEditTextHeight.setError(resources.getString(R.string.invalid_height_cm));
            isvalidate = false;
        } else {
            isvalidate = true;
        }
    }


    private void getIntentData() {
        try {
            //getIntent
            Intent intent = getIntent();
            id = intent.getStringExtra(Common.SESSION_USER_ID);
            name = intent.getStringExtra(Common.SESSION_USERNAME);
            email = intent.getStringExtra(Common.SESSION_EMAIL);
            unitType = intent.getStringExtra(Common.SESSION_UNIT_TYPE);
            token = intent.getStringExtra(Common.SESSION_ACCESS_TOKEN);
            refresh_token = intent.getStringExtra(Common.SESSION_REFRESH_TOKEN);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "User ID: " + id + ", Username: " + name + ", email: " + email + ", Unit Type: " + unitType + "\ntoken: " + token + "\nRefresh Token: " + refresh_token);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            new LogsHandlersUtils(getApplicationContext())
                    .getLogsDetails("Infromation_Activiy_getIntentData", email
                            , "Information", SharedData.caughtException(e));
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }


    }

    private void checkType() {
        if (unitType.equals(resources.getString(R.string.metric))) {
            mEditTextHeight.setHint("CM");
            mEditTextWeight.setHint("KG");
        } else if (unitType.equals(resources.getString(R.string.imperial))) {
            mEditTextHeight.setHint(resources.getString(R.string.ft_inches));
            mEditTextWeight.setHint("LB");
        }

    }


    private void saveUserData() {
        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
            if (mEditTextHeight.getText().length() > 3) {
                numberD = String.valueOf(mEditTextHeight.getText().toString());
                numberD = numberD.substring(numberD.indexOf(".")).substring(1);
            } else {
                numberD = "0";
            }

            if (mEditTextHeight.getText().toString().isEmpty() || mEditTextHeight.getText().toString().equals(".")) {
                mEditTextHeight.setError(resources.getString(R.string.height_error));
                mEditTextHeight.requestFocus();

            }
            // Check for a valid email address.
            else if (mEditTextWeight.getText().toString().isEmpty()) {
                mEditTextWeight.setError(resources.getString(weight_error));
                mEditTextWeight.requestFocus();

            } else if ((unitType.matches(resources.getString(R.string.imperial))) && ((Float.parseFloat(mEditTextHeight.getText().toString()) <= 3.0)
                    || (Float.parseFloat(mEditTextHeight.getText().toString()) > 8.0))) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height));
                mEditTextHeight.requestFocus();
            } else if ((unitType.matches(resources.getString(R.string.imperial))) && (Integer.parseInt(numberD) > 12)) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height_ft));
                mEditTextHeight.requestFocus();
                //mEditTextHeight.setText("");
            } else if ((unitType.matches(resources.getString(R.string.metric))) && ((Float.parseFloat(mEditTextHeight.getText().toString()) <= 100.0)
                    || (Float.parseFloat(mEditTextHeight.getText().toString()) >= 272.0))) {
                mEditTextHeight.setError(resources.getString(R.string.invalid_height_cm));
                mEditTextHeight.requestFocus();

            } else if (unitType.matches(resources.getString(R.string.imperial)) &&
                    ((Float.parseFloat(mEditTextWeight.getText().toString()) <= 66.1)
                            || (Float.parseFloat(mEditTextWeight.getText().toString()) >= 881.8))) {
                mEditTextWeight.setError(resources.getString(R.string.invalid_weight_Metric));
                mEditTextWeight.requestFocus();
            } else if (unitType.matches(resources.getString(R.string.metric)) && ((Integer.parseInt(mEditTextWeight.getText().toString()) <= 30)
                    || (Integer.parseInt(mEditTextWeight.getText().toString()) >= 400))) {
                mEditTextWeight.setError(resources.getString(R.string.invalid_weight));
                mEditTextWeight.requestFocus();
            } else if (mEditTextAge.getText().toString().isEmpty()) {
                mEditTextAge.setError(resources.getString(R.string.Age_error));
                mEditTextAge.requestFocus();
                //System.out.println("Hello are you there  222");
            } else if (Integer.parseInt(mEditTextAge.getText().toString()) <= 12 ||
                    (Integer.parseInt(mEditTextAge.getText().toString()) >= 90)) {
                mEditTextAge.setError(resources.getString(R.string.invalid_age));
                mEditTextAge.requestFocus();
                //System.out.println("Hello are youdddddddddddd there");

            } else if (!mGenderFemale.isChecked() && !mGenderMale.isChecked()) {
                //gender.setError(getResources().getString(R.string.gender_error));
                // mTextInputLayoutGender.setEndIconVisible(false);
                Toast.makeText(InformationActivity.this, resources.getString(R.string.select_gender),
                        Toast.LENGTH_SHORT).show();

                // System.out.println("Hello are you thejjjjjjjjjre");
            } else {
                if (mGenderMale.isChecked() || mGenderFemale.isChecked()) {
                    int checkedButton = mGenderGroupButton.getCheckedRadioButtonId();
                    mSelectedRadioButton = findViewById(checkedButton);
                    gender = mSelectedRadioButton.getText().toString();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "gender" + gender);
                    }
                }
                ProfileActivation profileModel = new ProfileActivation(id, mEditTextWeight.getText().toString(), mEditTextHeight.getText().toString(), mEditTextAge.getText().toString(), gender, unitType, name);
                if (Common.isLoggingEnabled) {
                    if (profileModel != null)
                        Log.d(TAG, "Profile Model in Information Activity: " + profileModel.toString());
                }
                blurrBackground();
                startLoading();
                ProfileUpdateUtil.updateUserProfile(getApplicationContext(), token, profileModel, this);

            }
        } else {
            if (getApplicationContext() != null) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void profileResponse(Response<SignupResponse> response) {

        if (response.isSuccessful()) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Profile Response in Information Actiivty: " + response.body().toString());
            }
            Intent intent = new Intent(InformationActivity.this,
                    GoalsActivity.class);
            intent.putExtra(Common.SESSION_USER_HEIGHT, mEditTextHeight.getText().toString());
            intent.putExtra(Common.SESSION_USER_WEIGHT, mEditTextWeight.getText().toString());

            intent.putExtra(Common.SESSION_USER_AGE, mEditTextAge.getText().toString());
            intent.putExtra(Common.SESSION_USER_GENDER, gender);
            intent.putExtra(Common.SESSION_UNIT_TYPE, unitType);
            intent.putExtra(Common.SESSION_USER_ID, id);
            intent.putExtra(Common.SESSION_EMAIL, email);
            intent.putExtra(Common.SESSION_USERNAME, name);
            intent.putExtra(Common.SESSION_ACCESS_TOKEN, token);
            intent.putExtra(Common.SESSION_REFRESH_TOKEN, refresh_token);
            //segmentedProgressBar.incrementCompletedSegments();
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "------------In Infromation Activity------------");
                Log.d(TAG, Common.SESSION_USER_ID + ": " + id);
                Log.d(TAG, Common.SESSION_EMAIL + ": " + email);
                Log.d(TAG, Common.SESSION_USERNAME + ": " + name);
                Log.d(TAG, Common.SESSION_USER_HEIGHT + ": " + mEditTextHeight.getText().toString());
                Log.d(TAG, Common.SESSION_USER_WEIGHT + ": " + mEditTextWeight.getText().toString());
                Log.d(TAG, Common.SESSION_USER_AGE + ": " + mEditTextAge.getText().toString());
                Log.d(TAG, Common.SESSION_USER_GENDER + ": " + gender);
                Log.d(TAG, Common.SESSION_UNIT_TYPE + ": " + unitType);
                Log.d(TAG, Common.SESSION_ACCESS_TOKEN + ": " + token);
                Log.d(TAG, Common.SESSION_REFRESH_TOKEN + ": " + refresh_token);

            }
            stopLoading();
            startActivity(intent);
        } else {
            stopLoading();
            try {
                Gson gson = new GsonBuilder().create();
                ErrorMessageModel profileJSON_Response = new ErrorMessageModel();

                profileJSON_Response = gson.fromJson(response.errorBody().string(), ErrorMessageModel.class);
                if (response.code() == 400) {
                    if (profileJSON_Response.getErrors() != null) {
                        if (profileJSON_Response.getErrors().getHeight() != null
                                && profileJSON_Response.getErrors().getHeight().size() > 0) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getErrors().getHeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else if (profileJSON_Response.getErrors().getWeight() != null
                                && profileJSON_Response.getErrors().getWeight().size() > 0) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getErrors().getWeight().get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else if (profileJSON_Response.getErrors().getGender() != null
                                && profileJSON_Response.getErrors().getGender().size() > 0) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getErrors().getGender().get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else if (profileJSON_Response.getErrors().getAge() != null
                                && profileJSON_Response.getErrors().getAge().size() > 0) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getErrors().getAge().get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else if (profileJSON_Response.getErrors().getUnit() != null
                                && profileJSON_Response.getErrors().getUnit().size() > 0) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getErrors().getUnit().get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (profileJSON_Response.getMessage() != null) {
                                Toast.makeText(getApplicationContext(), profileJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    LogoutUtil.redirectToLogin(InformationActivity.this);
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                } else {
                    if (profileJSON_Response != null) {
                        if (profileJSON_Response.getMessage() != null) {
                            Toast.makeText(getApplicationContext(), profileJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            } catch (Exception ex) {
                FirebaseCrashlytics.getInstance().recordException(ex);
                new LogsHandlersUtils(getApplicationContext())
                        .getLogsDetails("Food_Preference_activate_API", email
                                , EXCEPTION, SharedData.caughtException(ex));
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void profileResponseFailure(Throwable t) {
        stopLoading();
        if (Common.isLoggingEnabled) {
            t.printStackTrace();
        }

    }


    public static class DecimalDigitsInputFilter implements InputFilter {
        Pattern pattern;

        public DecimalDigitsInputFilter(int digitsBeforeDecimal, int digitsAfterDecimal) {
            pattern = Pattern.compile("(([1-9]{1}[0-9]{0," + (digitsBeforeDecimal - 1) + "})?||[0]{1})((\\.[0-9]{0,"
                    + digitsAfterDecimal + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int sourceStart, int sourceEnd, Spanned destination, int destinationStart, int destinationEnd) {
            // Remove the string out of destination that is to be replaced.
            String newString = destination.toString().substring(0, destinationStart) + destination.toString().substring(destinationEnd, destination.toString().length());

            // Add the new string in.
            newString = newString.substring(0, destinationStart) + source.toString() + newString.substring(destinationStart, newString.length());

            // Now check if the new string is valid.
            Matcher matcher = pattern.matcher(newString);

            if (matcher.matches()) {
                // Returning null indicates that the input is valid.
                return null;
            }

            // Returning the empty string indicates the input is invalid.
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void blurrBackground() {
        blurView.setVisibility(View.VISIBLE);

        float radius = 1f;


        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
    }

    private void startLoading() {
        //dissable user interaction
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}