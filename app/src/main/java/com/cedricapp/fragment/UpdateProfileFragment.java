package com.cedricapp.fragment;

import static android.app.Activity.RESULT_OK;
import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.R.string.height_error;
import static com.cedricapp.R.string.username_error;
import static com.cedricapp.R.string.weight_error;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.CompressUtil;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.StepCountServiceUtil;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.activity.InformationActivity;
import com.cedricapp.service.UploadImageService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class UpdateProfileFragment extends Fragment {

    private static final long SPLASH_SCREEN_TIME_OUT = 2000;
    private ImageButton backArrow;
    private MaterialButton mUpdateProfileButton;
    private TextInputEditText mUpdateUsername, mUpdateEmail, mUpdateHeight, mUpdateWeight, mUpdateAge;
    String updateUsername, updateEmail, updateHeight, updateWeight, updateAge,
            updateImage, numberD;
    TextView weight_tv, height_tv;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private DBHelper dbHelper;
    //  SweetAlertDialog pDialog;
    ShimmerFrameLayout shimmerForProfile;

    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    MaterialTextView btn_Cancel, btn_Continue;


    private FirebaseStorage storage;


    private String uid;
    // private String imgUrl;
    // private ImageView mUserProfileImage;
    private ShapeableImageView mUserProfileImage;
    // Uri indicates, where the image will be picked from
    private Uri imageUri;
    String name, email, height, weight, age, gender, level, unit, goal, img;
    int goal_id, level_id;
    Bitmap bitmap;
    LottieAnimationView loading_lav;

    private View view1;
    BlurView blurView;
    //private Context mContext;
    Call<SignupResponse> updateProfileCall;
    boolean isLoading = false;
    boolean isPhotoTaken = false;
    int dotCount = 0;
    private ProgressBar progressBar;
    private MaterialTextView mProgressBarTextView;
    private Snackbar snackbar;
    private String message;

    boolean canDotAdd = false;

    MaterialTextView textViewUsername, emailLabelTV, ageLabelTV, userNameTV;

    Uri cam_uri;
    String cam_ImagePath;

    Resources resources;


    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onResume");
        }
        HomeActivity.hideBottomNav();
        SharedData.canToastShow = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this,
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            // Handle back button press here
                            if (!isPhotoTaken) {
                                // showCustomDialog();
                                Log.d("photoTaken ", String.valueOf(isPhotoTaken));
                                if (getFragmentManager().getBackStackEntryCount() != 0) {
                                    getFragmentManager().popBackStack();
                                }
                            } else {
                                Log.d("photoTaken 1", String.valueOf(isPhotoTaken));
                                showCustomDialog();
                            }

                            // Toast.makeText(getActivity(), "Your profile image not saved, please save before leaving", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onStop");
        }
    }


    /*private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (!isPhotoTaken) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            } else {
                // requireActivity().onBackPressed();
                showCustomDialog();
            }

        }
    };*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onAttach");
        }
        // Enable back button in the fragment
       /* requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (getChildFragmentManager().getBackStackEntryCount() != 0) {
                        getChildFragmentManager().popBackStack();
                    } else {
                        // requireActivity().onBackPressed();
                        showCustomDialog();
                    }
                }

                // requireActivity().onBackPressed();
            }
        });*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onCreate");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onCreateView");
        }
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_update_profile, container, false);
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        resources = getResources();
        //resources = Localization.setLanguage(getContext(), getResources());
        // Register the OnBackPressedCallback with the parent Fragment's OnBackPressedDispatcher
        Log.d("isAdded", String.valueOf(isAdded()));


        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onViewCreated");
        }
        view1 = view;
        dbHelper = new DBHelper(getContext());
        init();
        // Add the callback to the OnBackPressedDispatcher


        SetUnityType();
        setUpdateProfileLiveValidation();


        mUpdateHeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(3, 2)});
//        mUpdateWeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(3, 0)});
//        mUpdateAge.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(3, 0)});


        StartShimmer();

        //  imageUri = Uri.parse(mUserProfileImage.getDrawable().toString()) ;
        imageUri = Uri.parse("Null");

        getUsersData();

        //StopLoading();
        oSBackButton();


        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (ConnectionDetector.isConnectedWithInternet(getActivity())) {
                if (!isPhotoTaken) {
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        getFragmentManager().popBackStack();

                    }
                } else {
                    showCustomDialog();
                    // Toast.makeText(getActivity(), "Your profile image not saved, please save before leaving", Toast.LENGTH_LONG).show();
                }
               /* } else {
                    Toast.makeText(getActivity(), getString(R.string.turn_on_your_internet), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        //new work
        mUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        mUpdateProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUpdateProfileButton.startAnimation(myAnim);
                try {

                    // Regex to check valid username.
                    String regex = "^[a-zA-Z\\s]+";

                    // Compile the ReGex
                    Pattern p = Pattern.compile(regex);

                    name = mUpdateUsername.getText().toString().trim();
                    System.out.println(name + "username");
                    email = mUpdateEmail.getText().toString();
                    height = mUpdateHeight.getText().toString();
                    weight = mUpdateWeight.getText().toString();
                    age = mUpdateAge.getText().toString();


                    if (mUpdateHeight.getText().length() > 3) {
                        numberD = String.valueOf(mUpdateHeight.getText().toString());
                        numberD = numberD.substring(numberD.indexOf(".")).substring(1);
                    } else {
                        numberD = "0";
                    }

                    if (Objects.requireNonNull(email.isEmpty())) {
                        //StopLoading();
                        mUpdateEmail.setError(resources.getString(R.string.email_error));


                    } else if (name.isEmpty()) {
                        mUpdateUsername.setError(resources.getString(R.string.username_valid_error));
                    } else if (!p.matcher(name).matches()) {
                        //StopLoading();
                        mUpdateUsername.setError(resources.getString(R.string.username_valid_error));
                        mUpdateUsername.requestFocus();

                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        //StopLoading();
                        mUpdateEmail.setError(resources.getString(R.string.error_invalid_email));
                    } else if (Objects.requireNonNull(name.isEmpty())) {
                        //StopLoading();
                        mUpdateUsername.setError(resources.getString(username_error));
                        mUpdateUsername.requestFocus();

                    } else if ((Objects.requireNonNull(age.isEmpty())) || (age.equals("0"))) {
                        //StopLoading();
                        mUpdateAge.setError(resources.getString(R.string.enter_age));
                        mUpdateAge.requestFocus();
                    } else if (Integer.parseInt(mUpdateAge.getText().toString()) <= 12 ||
                            (Integer.parseInt(mUpdateAge.getText().toString()) >= 90)) {
                        mUpdateAge.setError(resources.getString(R.string.invalid_age));
                        //StopLoading();
                        mUpdateAge.requestFocus();

                    } else if ((Objects.requireNonNull(height.isEmpty())) || (height.equals("0")) || (height.equals("."))) {
                        //StopLoading();
                        mUpdateHeight.setError(resources.getString(height_error));
                        mUpdateHeight.requestFocus();
                    } else if ((SharedData.unitType.matches("Imperial")) && ((Float.parseFloat(mUpdateHeight.getText().toString()) <= 3.0)
                            || (Float.parseFloat(mUpdateHeight.getText().toString()) > 8.0))) {
                        mUpdateHeight.setError(resources.getString(R.string.invalid_height));
                        //StopLoading();
                        mUpdateHeight.requestFocus();

                    } else if ((SharedData.unitType.matches("Imperial")) && (Integer.parseInt(numberD) > 12)) {
                        mUpdateHeight.setError(resources.getString(R.string.invalid_height_ft));
                        //StopLoading();
                        mUpdateHeight.requestFocus();
                        //mUpdateHeight.setText("");
                    } else if ((SharedData.unitType.matches("Metric")) && ((Float.parseFloat(mUpdateHeight.getText().toString()) < 100.0)
                            || (Float.parseFloat(mUpdateHeight.getText().toString()) > 272.0))) {
                        mUpdateHeight.setError(resources.getString(R.string.invalid_height_cm));
                        //StopLoading();
                        mUpdateHeight.requestFocus();

                    } else if ((Objects.requireNonNull(weight.isEmpty())) || (weight.matches("0"))) {
                        //StopLoading();
                        mUpdateWeight.setError(resources.getString(weight_error));
                        mUpdateWeight.requestFocus();
                    } else if (SharedData.unitType.matches("Imperial") &&
                            ((Float.parseFloat(mUpdateWeight.getText().toString()) <= 66.1)
                                    || (Float.parseFloat(mUpdateWeight.getText().toString()) >= 881.8))) {
                        mUpdateWeight.setError(resources.getString(R.string.invalid_weight_Metric));
                        mUpdateWeight.requestFocus();
                    } else if (SharedData.unitType.matches("Metric") && ((Integer.parseInt(mUpdateWeight.getText().toString()) <= 30)
                            || (Integer.parseInt(mUpdateWeight.getText().toString()) >= 400))) {
                        mUpdateWeight.setError(resources.getString(R.string.invalid_weight));

                        mUpdateWeight.requestFocus();
                    } else {
                        if (ConnectionDetector.isConnectedWithInternet(getActivity())) {
                            img = imageUri.toString();
                            Log.d("imagePath", imageUri.toString());
                            Log.d("uid", uid.toString());

                            /*blurrBackground();
                            StartLoading();*/
                            if (!imageUri.toString().equalsIgnoreCase("Null")) {
                                /*final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                progressDialog.setTitle("Uploading...");
                                progressDialog.show();*/

                                startServiceForProfileImageUpload();

                                /*//uploading image to firebase storage
                                storageRef.getReference("profile_images/" + uid).putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                progressDialog.dismiss();
                                                //Toast.makeText(requireContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                                                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        isPhotoTaken = false;
                                                        snackSet();
                                                        if (Common.isLoggingEnabled) {
                                                            Log.d(Common.LOG, "Firebase Stored path is " + uri.toString());
                                                        }
                                                        updateProfileButtonListener(name, email, mUpdateHeight.getText().toString(), weight, age, uri.toString());
                                                    }
                                                });

                                                *//*if (downloadUri.isSuccessful()) {
                                                    isPhotoTaken = false;
                                                    //loadFragment();
                                                    //loadDashboardFragment();
                                                    String generatedFilePath = downloadUri.getResult().toString();
                                                    if(Common.isLoggingEnabled)
                                                        Log.d(Common.LOG,"Firebase Stored path is "+ generatedFilePath);
                                                    updateProfileButtonListener(name, email, height, weight, age, generatedFilePath);

                                                } else {
                                                    Uri downloadedUri = taskSnapshot.getMetadata().get
                                                    String generatedFilePath = downloadUri.getResult().toString();
                                                    if(Common.isLoggingEnabled)
                                                        Log.d(Common.LOG,"In Unsuccessfull: Firebase Stored path is "+ generatedFilePath);
                                                    updateProfileButtonListener(name, email, height, weight, age, generatedFilePath);
                                                    //loadFragment();
                                                    //loadDashboardFragment();
                                                }*//*
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (getContext() != null) {
                                                    new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_ImageUploadingFailure",
                                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                                                }
                                                if (Common.isLoggingEnabled) {
                                                    Log.e(Common.LOG, "Error Message: " + e.getMessage());
                                                }
                                                updateProfileButtonListener(name, email, mUpdateHeight.getText().toString(), weight, age, updateImage);
                                                *//* loadDashboardFragment();*//*
                                                // loadFragment();
                                            }
                                        });*/

                            } else {
                                //img = SessionUtil.getUserImgURL(getContext());
                                updateProfileButtonListener(name, email, mUpdateHeight.getText().toString(), weight, age, updateImage);
                                //loadDashboardFragment();
                                //loadFragment();
                            }
                        } else {
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), resources.getString(R.string.turn_on_your_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_uploadImageOnFirbase",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                        Log.e(Common.LOG, "Update Profile Fragment Exception: " + e.toString());
                    }
                }
            }
        });

    }

    void startServiceForProfileImageUpload() {
        if (getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if (!StepCountServiceUtil.isMyServiceRunning(UploadImageService.class, getContext())) {
                    Intent mIntent = new Intent(getContext(), UploadImageService.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("imageURI", imageUri.toString());
                    mBundle.putString("name", name);
                    mBundle.putString("height", mUpdateHeight.getText().toString());
                    mBundle.putString("weight", weight);
                    mBundle.putString("age", age);
                    mIntent.putExtras(mBundle);
                    getContext().startService(mIntent);
                    isPhotoTaken = false;
                }
            }
        }
    }


    private void oSBackButton() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!isPhotoTaken) {
                                if (getFragmentManager().getBackStackEntryCount() != 0) {
                                    getFragmentManager().popBackStack();
                                }
                            } else {
                                showCustomDialog();
                                // Toast.makeText(getActivity(), "Your profile image not saved, please save before leaving", Toast.LENGTH_LONG).show();
                            }
                        } else {*/

                        if (!isPhotoTaken) {
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }
                        } else {
                            showCustomDialog();
                            // Toast.makeText(getActivity(), "Your profile image not saved, please save before leaving", Toast.LENGTH_LONG).show();
                        }
                        /* }*/

                        return true;
                    }
                }
                // Toast.makeText(getActivity(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void loadFragment() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //This method will be executed once the timer is over
                // Start your app main activity
                StopLoading();
                // Toast.makeText(getContext(), "Your profile is updated", Toast.LENGTH_SHORT).show();
                if (getContext() != null)
                    if (getFragmentManager() != null /*&& getFragmentManager().isStateSaved()*/) {
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
                            getFragmentManager().popBackStack();
                        }
                    }
                // blurrBackground();
                //StartLoading();
            }
        }, 2000);


        //StopLoading();
    }

  /*  private void handler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //StopLoading();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }*/

    private void init() {
        //mContext = getContext();
        //button id
        mUpdateProfileButton = view1.findViewById(R.id.updateProfileBtn);
        loading_lav = view1.findViewById(R.id.loading_lav);
        /*progressBar = view1.findViewById(R.id.progressBar);
        mProgressBarTextView = view1.findViewById(R.id.progressTextView);*/

        //EditText id's
        uid = SessionUtil.getUserID(getContext());
        backArrow = view1.findViewById(R.id.backArrow);
        mUserProfileImage = view1.findViewById(R.id.userImageFromGallery);
        mUpdateUsername = view1.findViewById(R.id.editTextUpdateUsername);
        mUpdateUsername.getText().toString();
        mUpdateEmail = view1.findViewById(R.id.editTextUpdateEmail);
        mUpdateAge = view1.findViewById(R.id.editTextUpdateAge);
        mUpdateHeight = view1.findViewById(R.id.editTextHeightUpdate);
        mUpdateWeight = view1.findViewById(R.id.editTextWeightUpdate);
        shimmerForProfile = view1.findViewById(R.id.shimmerForProfile);
        height_tv = view1.findViewById(R.id.height_tv);
        weight_tv = view1.findViewById(R.id.weight_tv);
        blurView = view1.findViewById(R.id.blurView);
        textViewUsername = view1.findViewById(R.id.textViewUsername);
        emailLabelTV = view1.findViewById(R.id.emailLabelTV);
        ageLabelTV = view1.findViewById(R.id.ageLabelTV);
        userNameTV = view1.findViewById(R.id.userName);

        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            FirebaseApp.initializeApp(/*context=*/ getContext());
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
        }
        setLanguageToWidgets();


    }

    void setLanguageToWidgets() {
        textViewUsername.setText(resources.getString(R.string.name));
        emailLabelTV.setText(resources.getString(R.string.email));
        ageLabelTV.setText(resources.getString(R.string.age));
        height_tv.setText(resources.getString(R.string.height));
        weight_tv.setText(resources.getString(R.string.weight));
        mUpdateProfileButton.setText(resources.getString(R.string.update_profile));
        userNameTV.setText(resources.getString(R.string.upload_your_profile_image));
    }

    private void setUpdateProfileLiveValidation() {
        try {

            // mUpdateWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 0)});
            mUpdateAge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 0)});

            // Regex to check valid username.
            String regex = "^[a-zA-Z\\s]+";

            // Compile the ReGex
            Pattern p = Pattern.compile(regex);
            mUpdateUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (mUpdateUsername.getText().length() > 0) {
                        if (!p.matcher(mUpdateUsername.getText().toString()).matches()) {
                            //StopLoading();
                            mUpdateUsername.setError(resources.getString(R.string.username_valid_error));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //  =======================email check==========
            mUpdateEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (mUpdateEmail.getText().length() > 0) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(mUpdateEmail.getText().toString()).matches()) {
                            //StopLoading();
                            mUpdateEmail.setError(resources.getString(R.string.error_invalid_email));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            //  =======================height check==========
            mUpdateHeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (mUpdateHeight.getText().length() > 3) {

                        numberD = String.valueOf(mUpdateHeight.getText().toString());
                        numberD = numberD.substring(numberD.indexOf(".")).substring(1);

                        System.out.println(numberD + "this is digit after decimal");
                        if (SharedData.unitType != null) {
                            if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))
                                    && ((Float.parseFloat(mUpdateHeight.getText().toString()) <= 3.0)
                                    || ((Float.parseFloat(mUpdateHeight.getText().toString()) > 8.0)) || ((Float.parseFloat(mUpdateHeight.getText().toString()) <= 3)
                                    || (Float.parseFloat(mUpdateHeight.getText().toString()) > 8)))) {
                                mUpdateHeight.setError(resources.getString(R.string.invalid_height));

                            } else if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))
                                    && (Integer.parseInt(numberD) >= 12)) {
                                if (Integer.parseInt(numberD) == 12) {
                                    Double h = Double.valueOf(mUpdateHeight.getText().toString());
                                    int a = h.intValue();
                                    String hh = String.valueOf(a + 1);
                                    mUpdateHeight.setText(hh);
                                    mUpdateHeight.append("." + "0");
                                    canDotAdd = false;
                                    if (Common.isLoggingEnabled) {
                                        System.out.println(hh + ".........................");
                                    }
                                } else if ((Integer.parseInt(numberD) > 12)) {
                                    String seperated[] = mUpdateHeight.getText().toString().split("\\.");
                                    if (seperated.length > 0) {
                                        String firstDigit = seperated[0];
                                        mUpdateHeight.setText(firstDigit + "." + (numberD.charAt(0)));
                                        mUpdateHeight.setSelection(mUpdateHeight.getText().length());
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(Common.LOG, "seperated.length==0");
                                            Log.e(Common.LOG, "Height: " + mUpdateHeight.getText().toString());
                                            Log.e(Common.LOG, "seperated: " + seperated.toString());
                                        }
                                    }
                                    return;
                                }
                            } else if ((SharedData.unitType.equals(resources.getString(R.string.metric))) && ((Float.parseFloat(mUpdateHeight.getText().toString()) < 100.0)
                                    || (Float.parseFloat(mUpdateHeight.getText().toString()) > 272.0))) {
                                mUpdateHeight.setError(resources.getString(R.string.invalid_height_cm));
                            }
                        } else {
                            mUpdateHeight.setError(resources.getString(R.string.something_went_wrong));
                        }
                    } else if (mUpdateHeight.getText().length() > 2) {
                        if (SharedData.unitType != null) {

                            if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))
                                    && ((Float.parseFloat(mUpdateHeight.getText().toString()) <= 3.0)
                                    || (Float.parseFloat(mUpdateHeight.getText().toString()) > 8.0))) {
                                mUpdateHeight.setError(resources.getString(R.string.invalid_height));
                            } else if ((SharedData.unitType.equals(resources.getString(R.string.metric))) && ((Float.parseFloat(mUpdateHeight.getText().toString()) < 100.0)
                                    || (Float.parseFloat(mUpdateHeight.getText().toString()) > 272.0))) {
                                mUpdateHeight.setError(resources.getString(R.string.invalid_height_cm));

                            }
                        } else {
                            mUpdateHeight.setError(resources.getString(R.string.something_went_wrong));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (SharedData.unitType != null) {
                        if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))) {
                            mUpdateHeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(1, 2)});
                            if (editable.length() == 1) {
                                if (dotCount == 0) {
                                    dotCount++;
                                    if (canDotAdd) {
                                        mUpdateHeight.append(".");
                                    }
                                }
                            } else if (editable.length() == 0) {
                                dotCount = 0;
                                canDotAdd = true;
                            }


                        } else {
                            mUpdateHeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(3, 2)});

                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "SharedData.unitType is null in profile");
                        }
                    }
                }
            });
            // =======validation for weight

            mUpdateWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (mUpdateWeight.getText().length() > 0) {
                        if (SharedData.unitType != null) {
                            if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))) {
                                mUpdateWeight.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                if ((Float.parseFloat(mUpdateWeight.getText().toString()) <= 66.1)
                                        || (Float.parseFloat(mUpdateWeight.getText().toString()) >= 881.8)) {
                                    mUpdateWeight.setError(resources.getString(R.string.invalid_weight_Metric));
                                    mUpdateWeight.requestFocus();
                                }

                            } else if ((SharedData.unitType.equals(resources.getString(R.string.metric)))) {
                                mUpdateWeight.setInputType(InputType.TYPE_CLASS_NUMBER);

                                if ((Integer.parseInt(mUpdateWeight.getText().toString()) <= 30)
                                        || (Integer.parseInt(mUpdateWeight.getText().toString()) >= 400)) {
                                    mUpdateWeight.setError(resources.getString(R.string.invalid_weight));
                                    mUpdateWeight.requestFocus();
                                }
                            }
                        } else {
                            mUpdateWeight.setError(resources.getString(R.string.something_went_wrong));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (SharedData.unitType != null) {
                        if ((SharedData.unitType.matches(resources.getString(R.string.imperial)))) {
                            mUpdateWeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(2 | 3, 2)});

                        } else {
                            if ((SharedData.unitType.equals(resources.getString(R.string.metric)))) {
                                mUpdateWeight.setFilters(new InputFilter[]{new InformationActivity.DecimalDigitsInputFilter(3, 0)});
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "SharedData.unitType is null in profile");
                        }
                    }/* else {
                        mUpdateWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

                    }*/
                    // }
                }
            });

            // =======validation for age

            mUpdateAge.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (mUpdateAge.getText().length() > 0) {
                        if (Integer.parseInt(mUpdateAge.getText().toString()) <= Integer.parseInt("12") ||
                                (Integer.parseInt(mUpdateAge.getText().toString()) >= Integer.parseInt("90"))) {
                            mUpdateAge.setError(resources.getString(R.string.invalid_age));
                            //System.out.println("Hello are youdddddddddddd there");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_Weight",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        try {
            if (isAdded()) {
                if (requireActivity() != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        askPermission();
                    } else {
                        customDialogForImage();
                    }

                    /*ImagePicker.with(requireActivity())
                            .crop()                    //Crop image(Optional), Check Customization for more option
                            .compress(1024)            //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .createIntent(intent -> {
                                startForProfileImageResult.launch(intent);
                                return null;
                            });*/
                }
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_selectImage",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> startForProfileImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        if (result.getResultCode() == RESULT_OK) {
                            isPhotoTaken = true;
                            // There are no request codes
                            Intent data = result.getData();
                            // Get the Uri of data
                            imageUri = data.getData();
                            try {
                                // Setting image on image view using Bitmap
                                if (isAdded() && getContext() != null) {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                                    mUserProfileImage.setImageBitmap(bitmap);
                                }
                            } catch (IOException e) {
                                // Log the exception
                                if (Common.isLoggingEnabled) {
                                    e.printStackTrace();
                                }
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (getContext() != null) {
                                    new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_uploadImageOnFirbase",
                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));

                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Result Code after image selection: " + result.getResultCode());
                            }
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_uploadImageOnFirbase",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

    private String convertToString(String s) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }


    private void updateProfileButtonListener(String name, String email,
                                             String height, String weight, String age, String img) {
        //    sweetAlertDialog();
        //snackSet();

        updateProfileCall = ApiClient.getService().updateProfileData("Bearer " + SharedData.token, uid, weight, height, age, gender, goal_id, level_id, SharedData.unitType, name, email, img, SessionUtil.getFoodPreferenceID(getContext()));


        // on below line we are executing our method.
        updateProfileCall.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(@NotNull Call<SignupResponse> call, @NotNull Response<SignupResponse> response) {
                // this method is called when we get response from our api.
                SignupResponse updateProfileModel = response.body();
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(Common.LOG, "Response Status " + message.toString());
                    }
                    // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    try {
                        if (Common.isLoggingEnabled) {
                            if (response.body() != null)
                                Log.d(Common.LOG, "Profile reponse after update: " + response.body().toString());
                        }
                        if (updateProfileModel != null) {
                            /*if (updateProfileModel.isStatus() == true) {*/
                            dbHelper.updateUserProfile(updateProfileModel);
                            SharedData.id = updateProfileModel.getData().user_id;
                            //SharedData.username = updateProfileModel.getData().getName();
                            // System.out.println("username ;;;;;;;;;;;;;;;;;;;" + SharedData.username);

                            //update username in seesion sharedpreference

                            if (isAdded() && getContext() != null) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "--------------save updated profile details in shared preferences------------");
                                    Log.d(Common.LOG, "Usernmae: " + SharedData.username);
                                    Log.d(Common.LOG, "Age: " + SharedData.age);
                                    Log.d(Common.LOG, "Height: " + SharedData.height);
                                    Log.d(Common.LOG, "Weight: " + SharedData.weight);
                                }
                                SharedData.username = updateProfileModel.getData().getName();
                                SessionUtil.setUsername(getContext(), SharedData.username);

                                SharedData.age = updateProfileModel.getData().age;
                                SessionUtil.setUserAge(getContext(), SharedData.age);

                                SharedData.height = updateProfileModel.getData().getHeight();
                                SessionUtil.setUserHeight(getContext(), SharedData.height);

                                SharedData.weight = updateProfileModel.getData().getWeight();
                                SessionUtil.setUserWeight(getContext(), SharedData.weight);

                                SessionUtil.setUserImgURL(getContext(), updateProfileModel.getData().getAvatar());
                                loadFragment();


                                        /*SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        //editor.putString("name", SharedData.username);
                                        editor.putString("name", SharedData.username);
                                        editor.apply();*/
                                //SharedData.username = username;
                                // mUsername.setText(SharedData.username);
                                // mAge.setText(SharedData.age);

                                //mHeight.setText(SharedData.height);

                                //mWeight.setText(SharedData.weight);
                                // SharedData.email = updateProfileModel.getData().getEmail();
                                //mGender.setText(SharedData.gender);
                                //  SharedData.imageUrl = updateProfileModel.getProfileImage();
                                /*System.out.println(SharedData.age + "image from APi");*/
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(Common.LOG, "Profile Fragment is not attached with activity");
                                }
                            }

                            // Glide.with(getContext()).asBitmap().load(SharedData.imageUrl).into(SharedData.imageUrl);
                            /*} else if (updateProfileModel.isStatus() == false) {

                                if (isAdded() && getContext() != null)
                                    Toast.makeText(getContext(), updateProfileModel.getMessage().toString(), Toast.LENGTH_LONG).show();
                                if (updateProfileModel.getMessage().toString().equals("Access token expires!")) {
                                    generateNewToken(uid);
                                }
                            }*/
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Update Profile model is null");
                            }
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                            }
                            //StopLoading();
                        }

                    } catch (Exception e) {
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "Update Profile Fragment exception: " + e.toString());
                            e.printStackTrace();

                        }
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_Update_Profile Fragment exception",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                        }
                        //StopLoading();
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    //StopLoading();
                    // loadDashboardFragment();
                    //loadFragment();
                    //snackbar.dismiss();
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // StopLoading();
                    // snackbar.dismiss();
                    if (isAdded() && getContext() != null) {
                        if (updateProfileModel != null && updateProfileModel.getMessage() != null) {
                            Toast.makeText(getContext(), updateProfileModel.getMessage().toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "Response is unsuccessful");
                    }
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                // snackbar.dismiss();
                if (isAdded()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_updateProfileAPICall",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                StopLoading();

            }
        });
    }

    /*private void snackSet() {


        snackbar = Snackbar.make(view1, "", Snackbar.LENGTH_LONG);

// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

// Inflate a linear layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

// Inflate the text view
        TextView textView = new TextView(getContext());
        textView.setText("Uploading...");
        textView.setTextSize(13);


        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

// Inflate the progress bar
        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
// Set the height of the progress bar
        LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(5)
        );
        progressBar.setLayoutParams(progressBarParams);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_color);
        progressBar.setProgressDrawable(drawable);

// Add the text view and progress bar to the linear layout
        linearLayout.addView(textView);
        linearLayout.addView(progressBar);

// Add the linear layout to the snackbar's layout view
        layout.addView(linearLayout, 0);

// Show the snackbar
        snackbar.show();


    }*/

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void generateNewToken(String uid) {
        Call<SignupResponse> call = ApiClient.getService().getNewToken("Bearer " + SharedData.refresh_token, uid);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                try {
                    if (response.isSuccessful()) {

                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();

                        SignupResponse responseToken = response.body();
                        if (responseToken != null) {
                            if (responseToken.status) {
                                SharedData.token = responseToken.data.getAccess_token();
                                SharedData.refresh_token = responseToken.data.getRefresh_token();

                                //update tokens
                                SessionUtil.setAccessToken(getContext(), SharedData.token);
                                SessionUtil.setRefreshToken(getContext(), SharedData.refresh_token);

                                updateProfileButtonListener(name, email, height, weight, age, updateImage);
                            } else {
                                if (isAdded()) {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), responseToken.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "Update Profile token is null");
                            }
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "Response Status " + message.toString());
                        }
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_generateNewToken",
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
                    new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_generateNewToken",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });


    }

    private void loadDashboardFragment() {
        Fragment fragment = new DashboardFragment();
        //replacing the fragment
        if (fragment != null) {
            if (isAdded()) {
                if (getContext() != null) {
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    ft.disallowAddToBackStack();
                    ft.commit();
                }
            }

//            Toast toast = Toast.makeText(getContext(), "Data updated.", Toast.LENGTH_SHORT);
//            // toast.getView().setBackgroundResource(R.color.yellow);
//            toast.show();
        }
        //  pDialog.hide();
        //StopLoading();

    }


    private void getUsersData() {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "" + SessionUtil.getUserEmailFromSession(getContext()));
        }

        mUpdateUsername.setText(SessionUtil.getUsernameFromSession(getContext()));
        updateUsername = mUpdateUsername.getText().toString();
        mUpdateUsername.setSelection(updateUsername.length());
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Username: " + updateUsername);
        }
        /*if (Common.isLoggingEnabled)
            Log.d(Common.LOG, "Saved Email is " + SharedData.email);*/
        mUpdateEmail.setText(SessionUtil.getUserEmailFromSession(getContext()));
        updateEmail = mUpdateEmail.getText().toString();
        mUpdateEmail.setSelection(updateEmail.length());
        mUpdateHeight.setText(SessionUtil.getUserHeight(getContext()));
        updateHeight = mUpdateHeight.getText().toString();
        mUpdateHeight.setSelection(updateHeight.length());
        mUpdateWeight.setText(SessionUtil.getUserWeight(getContext()));
        updateWeight = mUpdateWeight.getText().toString();
        mUpdateWeight.setSelection(updateWeight.length());
        mUpdateAge.setText(SessionUtil.getUserAge(getContext()));
        updateAge = mUpdateAge.getText().toString();
        mUpdateAge.setSelection(updateAge.length());
        gender = SessionUtil.getUserGender(getContext());
        goal = SessionUtil.getUserGoal(getContext());
        level = SessionUtil.getUserLevel(getContext());
        SharedData.token = SessionUtil.getAccessToken(getContext());
        updateImage = SessionUtil.getUserImgURL(getContext());
        if (!SessionUtil.getUserGoalID(getContext()).matches("")) {
            goal_id = Integer.parseInt(SessionUtil.getUserGoalID(getContext()));
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Goal ID is null or empty which retrieved from shared preferences");
            }
        }
        if (!SessionUtil.getUserLevelID(getContext()).matches("")) {
            level_id = Integer.parseInt(SessionUtil.getUserLevelID(getContext()));
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Level ID is null or empty which retrieved from shared preferences");
            }
        }
        // SharedData.unitType=SharedData.unitType;

        //getting image from firebase storage
        try {
            if (!updateImage.matches("")) {
                File localFile = File.createTempFile("profile_images", "jpg");

                storageRef.getReference().child("profile_images/" + uid)
                        .getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                if (isAdded()) {
                                    if (getContext() != null) {
                                        try {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            // mUserProfileImage.setImageBitmap(bitmap);
                                            Glide.with(getContext()).load(bitmap).into(mUserProfileImage);

                                            StoptShimmer();
                                        } catch (Exception ex) {
                                            if (Common.isLoggingEnabled)
                                                ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (getContext() != null) {
                                    new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_imageUploading",
                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                                }
                                StoptShimmer();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                //snackSet();
                            }
                        });
            } else {
                StoptShimmer();
            }
        } catch (IOException e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_ImageUploading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        //StopLoading();
    }


    private void StartLoading() {

        //dissable user interaction
        if (isAdded()) {
            if (requireActivity() != null) {

                //dissable user interaction
                disableUserInteraction();
                this.getView().setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {

                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
                });
            }
            loading_lav.setVisibility(View.VISIBLE);
            loading_lav.playAnimation();
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
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_Stoploading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        isLoading = false;
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    @Override
    public void onPause() {
        if (isLoading) {
            StopLoading();
        }
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile Fragment: onPause");
        }
        super.onPause();

    }

    private void StartShimmer() {
        shimmerForProfile.startShimmerAnimation();
        shimmerForProfile.setVisibility(View.VISIBLE);
        mUserProfileImage.setVisibility(View.INVISIBLE);
    }

    private void StoptShimmer() {
        shimmerForProfile.stopShimmerAnimation();
        shimmerForProfile.setVisibility(View.GONE);
        mUserProfileImage.setVisibility(View.VISIBLE);
    }

    private void blurrBackground() {
        if (isAdded()) {
            if (getContext() != null) {
                blurView.setVisibility(View.VISIBLE);
                float radius = 1f;


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

    private void SetUnityType() {
        try {
            // Toast.makeText(requireContext(),SharedData.SharedData.unitType , Toast.LENGTH_SHORT).show();
            if (SharedData.unitType != null) {
                if (SharedData.unitType.toString().trim().equalsIgnoreCase("Metric") ||
                        SharedData.unitType.toString().trim().equalsIgnoreCase("Metrisk")) {
                    height_tv.append(" (CM)");
                    weight_tv.append(" (Kg)");
                } else {
                    height_tv.append(" (ft/in)");
                    weight_tv.append(" (lb)");
                }
            } else {
                //SharedData.SharedData.unitType="Imperial";
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("UpdateProfileFragment_SetUNitType",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
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


    void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.subscription_expiration_date));

        TextView textView = dialog.findViewById(R.id.dialog_description);
        textView.setText(resources.getString(R.string.unsubscribe_package_text));

        btn_Cancel = dialog.findViewById(R.id.btn_left);
        btn_Cancel.setText(resources.getString(R.string.cancel));
        btn_Continue = dialog.findViewById(R.id.btn_right);
        btn_Continue.setText(resources.getString(R.string.btn_continue));


        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);
                btn_Cancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_left_click));

                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
                dialog.dismiss();
            }


        });
        dialog.show();
    }

    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if (o == null) {
                if (isAdded() && getContext() != null)
                    Toast.makeText(getContext(), resources.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
            } else {
                imageUri = o;
                Glide.with(getContext()).load(o).centerInside().into(mUserProfileImage);
            }
        }
    });

    void customDialogForImage() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_camera_photo_selection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout cameraLL = dialog.findViewById(R.id.openCameraLL);
        LinearLayout galleryLL = dialog.findViewById(R.id.openGalleryLL);

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.choose_one));

        TextView textView4 = dialog.findViewById(R.id.textView4);
        textView4.setText(resources.getString(R.string.choose_one));

        TextView camera = dialog.findViewById(R.id.textView2);
        camera.setText(resources.getString(R.string.camera));

        TextView gallery = dialog.findViewById(R.id.textView3);
        gallery.setText(resources.getString(R.string.gallery));


        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
                dialog.dismiss();
            }
        });

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1231);
    }

    void checkCameraPermission() {
        try {
            if (getContext() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    } else {
                        SessionUtil.setCameraPermission(getContext(), true);
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                //permission for Camera
                if (grantResults.length > 0) {
                    if (isAdded() && isVisible() && getContext() != null) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SessionUtil.setCameraPermission(getContext(), true);
                            openCamera();
                        } else {
                            SessionUtil.setCameraPermission(getContext(), false);
                        }
                    }
                }
                break;
            case 60:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // If permissions are granted we call the setView Method which prompts the user to pick
                        // an Image either by the clicking it now or picking from the gallery

                        customDialogForImage();

                    }
                }
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1231 && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (isAdded() && getContext() != null) {
                    imageUri = CompressUtil.bitmapToUriConverter(getContext(), photo);
                    //mUserProfileImage.setImageBitmap(photo);
                    isPhotoTaken = true;
                    Glide.with(getContext()).load(imageUri).centerInside().into(mUserProfileImage);
                    //mUserProfileImage.setImageURI(imageUri);
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Context is null in onActivityResult::Camera intent result");
                    }
                }

            } catch (Exception ex) {
                if (Common.isLoggingEnabled) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Asking user for storage permission
    public void askPermission() {
        // Checking if the permissions are not granted.
        if (
                ContextCompat.checkSelfPermission(
                        getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                                getContext(),
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If not granted requesting Read and  Write storage
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, 60);
        } else {
            // If permissions are granted we proceed by setting an OnClickListener for the button
            // which helps the user pick the image
            customDialogForImage();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Common.isLoggingEnabled) {
            Log.e(Common.LOG, "UpdateProfile: OnDestroy");
        }
    }

}