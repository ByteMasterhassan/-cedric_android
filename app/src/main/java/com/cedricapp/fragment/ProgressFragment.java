package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.cedricapp.adapters.ProgressAdapter;
import com.cedricapp.common.APIToken;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.DaysCheckedListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.DaysUnlockModel;
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.activity.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ProgressFragment extends Fragment implements DaysCheckedListInterface {
    private ProgressBar progressBar;
    //private MaterialTextView mTextViewProgressbar;
    //private CircularProgressIndicator circularProgress;
    int i = 0;

    public static View.OnClickListener myOnClickListener;
    private static RecyclerView mProgressRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ProgressDataModel> weekWiseList = new ArrayList<>();
    private static ProgressAdapter adapter;
    private ConstraintLayout mConstraintLayout;
    private ImageButton backArrow;
    CircleImageView userProfile;
    MaterialTextView materialTextViewLevel, mProfileName, materialTextViewTilte, mTextViewProgress, materialTextViewGoal;
    MaterialCardView mCardViewDay1, mCardViewDay2, mCardViewDay3, mCardViewDay4,
            mCardViewDay5, mCardViewDay6, mCardViewDay7, mCardViewDay8;
    String programName, totalWeeks, description;
    SharedPreferences preferences;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private View view1;
    ArrayList<String> watchedVideoIdList = new ArrayList<String>();
    private String token;
    ArrayList<String> daysCheckedlist = new ArrayList<>();
    private ArrayList<String> selectedWeeklist = new ArrayList<>();
    HashMap<String, String> weekDaysCompleted = new HashMap<String, String>();
    private int listsSize = 0;
    int week, currentUserId, programId;
    int level_id;
    float watchedvideos, totalVideos;
    int goal_id;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private MaterialTextView mTextViewProgressbar, textViewProgress, textViewGoalLabel, textViewLevelLabel;
    private DecimalFormat df;
    DBHelper dbHelper;
    private List<DaysUnlockModel.Data> daysUnlockModelList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private String message;

    Resources resources;

    String TAG = "PROGRESS_TAG";


    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
        // setProgressBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        df = new DecimalFormat("0.00");

        getDataFromBundle();

        init();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    Fragment fragment = new UpdateProfileFragment();
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    //changes
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    if (isAdded()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //check connection
        checkConnectionAndGetData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            if (SharedData.token != null && currentUserId != 0 && programId != 0) {
                                stopLoading();
                                getUnlockDays(token, currentUserId, programId);
                            }
                        } else {
                            if (swipeRefreshLayout != null) {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                            showToast(resources.getString(R.string.no_internet_connection));
                            //StoptShimmer();
                        }
                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        // showToast("No Internet, Please turn ON Internet");
                    }
                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        //   Populate data of first horizontal cardview
        weekWiseList = new ArrayList<ProgressDataModel>();
    }

    private void checkConnectionAndGetData() {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if (SessionUtil.isWorkoutProgressNeedToLoad(getContext())) {
                    if (SharedData.token != null && currentUserId != 0 && programId != 0) {
                        blurrBackground();
                        startLoading();
                        getUnlockDays(token, currentUserId, programId);
                    }
                } else {
                    daysUnlockModelList = getDataFromDB();
                    if (daysUnlockModelList != null && daysUnlockModelList.size() > 0) {
                        setDataToUI(daysUnlockModelList);
                    } else {
                        if (SharedData.token != null && currentUserId != 0 && programId != 0) {
                            blurrBackground();
                            startLoading();
                            getUnlockDays(token, currentUserId, programId);
                        }
                    }
                }

            } else {
                daysUnlockModelList = getDataFromDB();
                if (daysUnlockModelList != null && daysUnlockModelList.size() > 0) {
                    setDataToUI(daysUnlockModelList);
                } else {
                    showToast(resources.getString(R.string.no_internet_connection_no_data_available));
                }
            }
        }
    }

    private void getUnlockDays(String token, int currentUserId, int programId) {
        /*blurrBackground();
        startLoading();*/
        //dbHelper.deleteAllProgramDetailData();
        Call<DaysUnlockModel> call = ApiClient.getService().getUnlockDaysFromApi("Bearer " + token, currentUserId, programId);
        call.enqueue(new Callback<DaysUnlockModel>() {
            @Override
            public void onResponse(Call<DaysUnlockModel> call, Response<DaysUnlockModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();

                    DaysUnlockModel daysUnlockModel = response.body();
                    stopLoading();
                    if (response.code() == 200) {
                        if (daysUnlockModel != null) {
                            if (daysUnlockModel.getData() != null) {
                                SessionUtil.setWorkoutProgressLoad(false, getContext());
                                if (!dbHelper.isProgramDetailAvailable(daysUnlockModel.getData().getProgram())) {
                                    dbHelper.addProgramDetails(daysUnlockModel);
                                } else {
                                    dbHelper.updateProgramDetails(daysUnlockModel);
                                }
                                setDataToUI(getDataFromDB());
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "No Data available in ProgressFragment-getUnlockDaysFromApi");
                                }
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 404) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    stopLoading();
                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    if (message != null)
                        Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    stopLoading();
                }
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<DaysUnlockModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });

    }

    private List<DaysUnlockModel.Data> getDataFromDB() {
        List<DaysUnlockModel.Data> dayUnlockModelList = dbHelper.getProgramDetails(programName);
        return dayUnlockModelList;
    }

    private void setDataToUI(List<DaysUnlockModel.Data> dayUnlockModelList) {
        if (dayUnlockModelList != null && dayUnlockModelList.size() > 0) {
            materialTextViewTilte.setText(dayUnlockModelList.get(0).getProgram());
            int unlockDays = dayUnlockModelList.get(0).getUnlockDay();
            int totalWeeks = dayUnlockModelList.get(0).getTotalWeeks();
            int unLockWeeks = dayUnlockModelList.get(0).getUnlockWeek();
            totalVideos = dayUnlockModelList.get(0).getTotalVideos();
            watchedvideos = dayUnlockModelList.get(0).getWatchedVideos();
            setProgressBar(totalVideos, watchedvideos);
            stopLoading();
            setAdapter(totalWeeks, unlockDays, unLockWeeks, programId, currentUserId, programName);
        }
    }

    private void setAdapter(int totalWeeks, int unlockDays, int unLockWeeks, int programId,
                            int currentUserId, String programName) {
        adapter = new ProgressAdapter(getContext(), totalWeeks, unlockDays, unLockWeeks, programId, currentUserId, programName);
        mProgressRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getDataFromBundle() {
        try {
            token = SharedData.token;
            week = 1;
            if (SharedData.id == null) {
                SharedData.id = SessionUtil.getUserID(getContext());
            }
            currentUserId = Integer.parseInt(SharedData.id);
            level_id = Integer.parseInt(SharedData.level_id);
            goal_id = Integer.parseInt(SharedData.goal_id);
            programName = getArguments().getString("ProgramName");
            programId = getArguments().getInt("ProgramId", 0);
            totalWeeks = getArguments().getString("noOfWeeks");
            description = getArguments().getString("description");
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "level_id: " + level_id + "User ID: " + SharedData.id + ",\nProgram Name: " + programName + ",\nWeeks: " + totalWeeks + ",\nDescription: " + description);
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    private void init() {
        dbHelper = new DBHelper(getContext());
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshProgress);
        progressBar = view1.findViewById(R.id.progress_bar);
        mTextViewProgressbar = view1.findViewById(R.id.text_view_progress);
        textViewProgress = view1.findViewById(R.id.textViewProgress);
        //circularProgress = view1.findViewById(R.id.progress);
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        //set TextView Id and set level
        materialTextViewLevel = view1.findViewById(R.id.textViewUserLevel);
        materialTextViewGoal = view1.findViewById(R.id.textViewUserGoal);
        materialTextViewTilte = view1.findViewById(R.id.textViewProgramTitle);
        mProfileName = view1.findViewById(R.id.profileName);
        userProfile = view1.findViewById(R.id.userProfile);
        textViewGoalLabel = view1.findViewById(R.id.textViewGoal);
        textViewLevelLabel = view1.findViewById(R.id.textViewLevel);
        String name = "";
        if (SharedData.username != null) {
            name = getFirstWord(SharedData.username);
        }
        if (name != null && name != "") {
            String FirstNameCap = name.substring(0, 1).toUpperCase() + name.substring(1);
            mProfileName.setText(FirstNameCap);
        }
        if (isAdded() && getContext() != null) {
            materialTextViewLevel.setText(SessionUtil.getUserLevel(getContext()));
            materialTextViewGoal.setText(SessionUtil.getUserGoal(getContext()));

        }
        //   materialTextViewGoal.setText(SessionUtil.getUserGoal(getContext()));
        //materialTextViewTilte.setText(programName);
        backArrow = view1.findViewById(R.id.backArrow);

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Current User ID: " + currentUserId);
        }

        //initializeDaysListFromLastArray();

        mProgressRecyclerView = view1.findViewById(R.id.recyclerViewProgress);
        //   recyclerview for vertical cardView
        mProgressRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mProgressRecyclerView.setLayoutManager(layoutManager);

//TODO
        //setProgressBar(totalVideos, watchedvideos);
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if (SharedData.token != null) {
                    if (SharedData.token != "") {
                        // getWeeklyProgramData(SharedData.token, "" + programId, SharedData.level_id, SharedData.goal_id, "" + getCurrentWeekNumber(), "" + getCurrentDayNumberInWeek());
                    } else {
                        if (isAdded() && getContext() != null) {
                            APIToken.getToken(getContext());
                            //   getWeeklyProgramData(SharedData.token, "" + programId, SharedData.level_id, SharedData.goal_id, "" + getCurrentWeekNumber(), "" + getCurrentDayNumberInWeek());
                        }
                    }
                } else {
                    if (isAdded() && getContext() != null) {
                        APIToken.getToken(getContext());
                    }
                }
                getImageFB();

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "No Internet Connection");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragement is not attatched to activity");
            }
        }


        textViewProgress.setText(resources.getString(R.string.progress));
        textViewGoalLabel.setText(resources.getString(R.string.goal));
        textViewLevelLabel.setText(resources.getString(R.string.level));


    }

    private String getFirstWord(String firstname) {
        int index = firstname.indexOf(' ');
        if (index > -1) {
            // Check if there is more than one word.
            return firstname.substring(0, index).trim(); // Extract first word.

        } else {
            return firstname; // Text is the first word itself.
        }
    }

    public void setProgressBar(float totalVideos, float watchedvideos) {
        progressBar.setMax(100);
        df = new DecimalFormat("0.00");
        if (isAdded() && getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

            // creating a variable for gson.
            Gson gson = new Gson();

            // below line is to get to string present from our
            // shared prefs if not present setting it as null.

            String json = sharedPreferences.getString("videoIdList", "null");
            // listsSize=sharedPreferences.getInt("size",0);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "" + listsSize + "list ka size");
            }

            // below line is to get the type of our array list.
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();

            // in below line we are getting data from gson
            // and saving it to our array list
            if (gson.fromJson(json, type) != null) {
                watchedVideoIdList.clear();
                watchedVideoIdList = gson.fromJson(json, type);
            }

            if (totalVideos != 0.0 && watchedvideos != 0.0) {
                int progress;
                double percent = (watchedvideos / totalVideos);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "" + percent + "percent");
                    Log.d(TAG, "" + watchedvideos + "percent");
                    Log.d(TAG, "" + totalVideos + "percent");
                }
                progress = (int) (percent * 100);

                progressBar.setProgress(progress);
                // circularProgress.setProgress(Float.(progress));
                mTextViewProgressbar.setText(String.valueOf(progress) + "%");
                // Toast.makeText(getContext(), String.valueOf(progress), Toast.LENGTH_LONG).show();
            } else {
                progressBar.setProgress(0);
                mTextViewProgressbar.setText("0%");
            }
        }

    }


    @Override
    public void onDaysCheckedListener(String dayNumber, String weekNumber, boolean checkedState) {
        if (weekDaysCompleted.isEmpty() && (checkedState != false)) {
            weekDaysCompleted.put(dayNumber + weekNumber, String.valueOf(checkedState));
        } else if (!weekDaysCompleted.containsKey(dayNumber + weekNumber) || !weekDaysCompleted.containsValue(String.valueOf(checkedState))) {
            weekDaysCompleted.put(dayNumber + weekNumber, String.valueOf(checkedState));
        } /*else if (!weekDaysCompleted.containsKey(dayNumber) && weekDaysCompleted.containsValue(weekNumber) && (checkedState != false)) {
            weekDaysCompleted.put(dayNumber, weekNumber);
        } else if (weekDaysCompleted.containsKey(dayNumber) && weekDaysCompleted.containsValue(weekNumber) && (checkedState != false)) {
            weekDaysCompleted.replace(dayNumber, weekNumber);
        }*/ else if (weekDaysCompleted.isEmpty() && checkedState == false) {

            weekDaysCompleted.put("0week 0", "false");
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        // getting data from gson and storing it in a string.
        String json = gson.toJson(daysCheckedlist);
        String json1 = gson.toJson(selectedWeeklist);
        String jjson = gson.toJson(weekDaysCompleted);
        //  Log.d("mylist",json);

        // below line is to save data in shared
        // prefs in the form of string.

        editor.putString("daysCheckedList", json);
        editor.putString("weekDaysList", json1);
        editor.putString("weekDaysCompleted", jjson);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.commit();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "mylist" + daysCheckedlist.toString());
            Log.d(TAG, "mylist" + selectedWeeklist.toString());
            Log.d(TAG, "mylist2" + weekDaysCompleted.toString());
        }
    }

    public void initializeDaysListFromLastArray() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.

        String json = sharedPreferences.getString("daysCheckedList", "null");
        String json1 = sharedPreferences.getString("weekDaysList", "null");
        String json2 = sharedPreferences.getString("weekDaysCompleted", "null");

        // below line is to get the type of our array list.
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        /*if ((gson.fromJson(json, type)!=null) &&  (gson.fromJson(json1,type) !=null)) {
            //ingridentsCheckedlist.clear();
            daysCheckedlist = gson.fromJson(json, type);
            selectedWeeklist = gson.fromJson(json1, type);
        }*/
        if ((gson.fromJson(json2, type) != null)) {
            //ingridentsCheckedlist.clear();
            weekDaysCompleted = gson.fromJson(json2, type);

        }

        // checking below if the array list is empty or not
    }

    private void StartLoading() {
        //dissable user interaction
        try {
            requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loading_lav.setVisibility(View.VISIBLE);
            loading_lav.playAnimation();
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }


    void showToast(String message) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Toast: Fragement is not added to activity");
                }
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
    }

    int getCurrentWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_MONTH);
        //Toast.makeText(getContext(),"My current week of the month "+calendar.get(Calendar.WEEK_OF_MONTH),Toast.LENGTH_SHORT);
    }

    int getCurrentDayNumberInWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
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
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    private void getImageFB() {
        try {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    File localFile = File.createTempFile("profile_images", "jpg");

                    storageRef.getReference().child("profile_images/" + currentUserId).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "Firebase storage successfully retrieved");
                                }
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                /*imageUserProfile.setImageBitmap(bitmap);*/
                                Glide.with(getContext()).load(bitmap).into(userProfile);
                            } catch (Exception ex) {
                                if (Common.isLoggingEnabled)
                                    ex.printStackTrace();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            if (Common.isLoggingEnabled) {
                                e.printStackTrace();
                            }
                            if (getContext() != null) {
                                new LogsHandlersUtils(getContext()).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
                Log.e(TAG, "Dashboard fragment exception - Get Image from Firebase" + e.toString());
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("Dashboard_Profie_Image", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
        }
    }
}





