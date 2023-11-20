package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.BestProgramAdapter;
import com.cedricapp.adapters.BestProgramWorkoutAdapter;
import com.cedricapp.common.APIToken;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.BestProgramModel;
import com.cedricapp.model.ProgramWorkout;
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.UserStatusUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class BestProgramExercises extends Fragment implements UserStatusInterface {
    //
    public static View.OnClickListener myOnClickListener;
    private ImageButton backArrow;
    private RecyclerView.LayoutManager layoutManager;
    //private static RecyclerView bestProgramWarmuprecyclerView;
    private static RecyclerView bestProgramWorkoutrecyclerView;
    private MaterialTextView programTitle, textViewDescription;

    BestProgramAdapter myAdapter;
    BestProgramWorkoutAdapter bestProgramWorkoutAdapter;
    //BestProgramModel bestProgramModel = new BestProgramModel();
    public static List<BestProgramModel> bestProgramList;
    MaterialTextView mTextViewWarmup, mTextViewWorkout;
    //private ShimmerFrameLayout mFrameLayout;
    private ShimmerFrameLayout mFrameLayout1;

    //    private DatabaseReference mDatabaseRef;

    String day;
    int week, level_id, goal_id, programId, videoListSize, currentUserId;
    ProgressDataModel.Data.Workouts workouts;
    String programName, programDescription;
    private DBHelper dbHelper;
    private List<ProgressDataModel.Data> workoutList;
    SwipeRefreshLayout swipeRefreshLayout;
    View view1;
    private String message;

    Resources resources;

    String TAG = "BEST_PROGRAM_EXERCISES_TAG";

    UserStatusUtil userStatusUtil;


    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    @Override
    public void onResume() {
        //mFrameLayout.startShimmerAnimation();
        mFrameLayout1.startShimmerAnimation();
        HomeActivity.hideBottomNav();
        SharedData.redirectToDashboard = false;
        super.onResume();

        //swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshProgressExercise);

        // Programmatically trigger the refresh action
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                // Call onRefresh() method here
                onRefresh();
            }
        });
    }

    private void onRefresh() {
        if (isAdded()) {
            if (getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {

                    if (SharedData.token != null) {
                        if (SharedData.token != "") {
                            getWeeklyProgramData(SharedData.token, currentUserId, programId, SharedData.level_id, SharedData.goal_id, SharedData.day, SharedData.week);
                            if (bestProgramWorkoutAdapter != null) {
                                bestProgramWorkoutAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (isAdded() && getContext() != null) {
                                APIToken.getToken(getContext());
                                getWeeklyProgramData(SharedData.token, currentUserId, programId, SharedData.level_id, SharedData.goal_id, SharedData.day, SharedData.week);
                                if (bestProgramWorkoutAdapter != null) {
                                    bestProgramWorkoutAdapter.notifyDataSetChanged();
                                }
                            }
                        }
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


    @Override
    public void onPause() {
        //mFrameLayout.stopShimmerAnimation();
        mFrameLayout1.stopShimmerAnimation();
        super.onPause();
    }

    public BestProgramExercises() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_best_program_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        getBundleData();

        dbHelper = new DBHelper(getContext());
        init();

        checkConnectionAndGetData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {

                            if (SharedData.token != null) {
                                if (SharedData.token != "") {
                                    getWeeklyProgramData(SharedData.token, currentUserId, programId, SharedData.level_id, SharedData.goal_id, SharedData.day, SharedData.week);
                                    if (bestProgramWorkoutAdapter != null) {
                                        bestProgramWorkoutAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (isAdded() && getContext() != null) {
                                        APIToken.getToken(getContext());
                                        getWeeklyProgramData(SharedData.token, currentUserId, programId, SharedData.level_id, SharedData.goal_id, SharedData.day, SharedData.week);
                                        if (bestProgramWorkoutAdapter != null) {
                                            bestProgramWorkoutAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
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


    }

    private void checkConnectionAndGetData() {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                List<ProgramWorkout> dataList = getDataFromDB();
                if (dataList != null && dataList.size() > 0) {
                    //populateData(dataList);
                    if (level_id != 0 && goal_id != 0) {
                        showData(dataList, currentUserId, programId, level_id, goal_id, SharedData.day, SharedData.week);
                    }
                } else {
                    if (isAdded() && getContext() != null) {
                        getWeeklyProgramData(SessionUtil.getAccessToken(getContext()), currentUserId, programId, SharedData.level_id, SharedData.goal_id, SharedData.day, SharedData.week);
                    }
                }
            } else {
                List<ProgramWorkout> dataList = getDataFromDB();
                if (dataList != null && dataList.size() > 0) {
                    /*populateData(dataList);*/
                    if (level_id != 0 && goal_id != 0) {
                        showData(dataList, currentUserId, programId, level_id, goal_id, SharedData.day, SharedData.week);
                    }
                } else {

                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "dataList == null && dataList.size() == 0");
                    }
                }
                showToast(resources.getString(R.string.no_internet_connection));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragement is not attatched to activity");
            }
        }

    }

    private void init() {
        programTitle = view1.findViewById(R.id.textViewProgramNameProgress);
        textViewDescription = view1.findViewById(R.id.textViewDescription);
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshProgressExercise);
        // programTitle.setText(SharedData.programName);
        // programId = SharedData.bestProgramId;
        mTextViewWarmup = view1.findViewById(R.id.textViewWarmUP);
        mTextViewWorkout = view1.findViewById(R.id.textViewWorkouts);
        //mFrameLayout = view.findViewById(R.id.shimmerLayout);
        mFrameLayout1 = view1.findViewById(R.id.shimmerLayout1);
        backArrow = view1.findViewById(R.id.backArrow);
        videoListSize = 0;
        //myOnClickListener = new MyItemOnClickListener(this);
        //programTitle.setText(programName);
        textViewDescription.setText(programDescription);
        //for warmup Exercises
        /*bestProgramWarmuprecyclerView = view.findViewById(R.id.recyclerviewBestProgramWarmUp);

        bestProgramWarmuprecyclerView.setHasFixedSize(true);*/
        //for workout Exercises
        bestProgramWorkoutrecyclerView = view1.findViewById(R.id.recyclerviewBestProgramWorkout);
        bestProgramWorkoutrecyclerView.setHasFixedSize(true);
        programTitle.setText(programName);
        textViewDescription.setText(programDescription);

        userStatusUtil = new UserStatusUtil(getContext(), BestProgramExercises.this, resources);
        if (ConnectionDetector.isConnectedWithInternet(getContext()))
            userStatusUtil.getUserStatus("Bearer " + SessionUtil.getAccessToken(getContext()));
    }


    private void getBundleData() {
        currentUserId = Integer.parseInt(SharedData.id);
        SharedData.day = getArguments().getInt("selectedDay", 0);
        SharedData.week = getArguments().getInt("selectedWeek", 0);
        programId = getArguments().getInt("programId", 0);
        currentUserId = getArguments().getInt("user_id", 0);
        level_id = getArguments().getInt("level_id", 0);
        goal_id = getArguments().getInt("goal_id", 0);
        // workouts = getArguments().getParcelable("workout");
        programName = getArguments().getString("programName");
        programDescription = getArguments().getString("programDescription");

    }

    void showData(List<ProgramWorkout> workoutList, int currentUserId, int programId,
                  int level_id, int goal_id, int day, int week) {
        if (isAdded() && getContext() != null) {
            mFrameLayout1.setVisibility(View.GONE);
            mFrameLayout1.stopShimmerAnimation();
            mTextViewWorkout.setVisibility(View.VISIBLE);
            bestProgramWorkoutrecyclerView.setVisibility(View.VISIBLE);
            if (currentUserId != 0 && programId != 0 && level_id != 0 && goal_id != 0 && day != 0 && week != 0) {
                bestProgramWorkoutAdapter = new BestProgramWorkoutAdapter(getContext(), workoutList, currentUserId,
                        programId, level_id, goal_id, day, week, resources);
                bestProgramWorkoutrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                bestProgramWorkoutrecyclerView.setAdapter(bestProgramWorkoutAdapter);
                bestProgramWorkoutAdapter.notifyDataSetChanged();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Please Check Parameters");
                }
            }

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragment is not added to activity");
            }
        }
    }


    /*  private void bestProgramExerciseDataPost() {
          Call<BestProgramModel> call = ApiClient.getService().programProgressDataPost(programId, SharedData.week, String.valueOf(SharedData.day));

          // on below line we are calling method to enqueue and calling
          // all the data from array list.
          call.enqueue(new Callback<BestProgramModel>() {
              @Override
              public void onResponse(Call<BestProgramModel> call, Response<BestProgramModel> response) {
                  if (response.isSuccessful()) {

                      //bestProgramModel = response.body();
                      //shimmer
                      *//*mFrameLayout.setVisibility(View.GONE);
                    mFrameLayout.stopShimmerAnimation();*//*

                    // mFrameLayout1.setVisibility(View.GONE);
                    //mFrameLayout1.stopShimmerAnimation();

                    //dbHelper.addProgramExercises(bestProgramModel);

                    //getBestProgramDataFromApi(bestProgramModel);


                }

            }

            @Override
            public void onFailure(Call<BestProgramModel> call, Throwable t) {
                // in the method of on failure we are displaying a
                // toast message for fail to get data.
                FirebaseCrashlytics.getInstance().recordException(t);
                //Toast.makeText(getContext(), "Fail to get  Best Program  data", Toast.LENGTH_SHORT).show();
            }
        });

    }*/
    private void getWeeklyProgramData(String token, int currentUserId, int programId, String
            level_id, String goal_id, int day, int week) {
        mFrameLayout1.startShimmerAnimation();
        mFrameLayout1.setVisibility(View.VISIBLE);
        bestProgramWorkoutrecyclerView.setVisibility(View.GONE);
        // on below line we are calling a method to get all the weekly programs from API.
        //dbHelper.deleteAllProgramExercisesData();
        Call<ProgressDataModel> call = ApiClient.getService().getWeeklyPrograms("Bearer " + token, currentUserId, programId, Integer.parseInt(level_id), Integer.parseInt(goal_id), week, day);
        call.enqueue(new Callback<ProgressDataModel>() {
            @Override
            public void onResponse(Call<ProgressDataModel> call, Response<ProgressDataModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status getWeeklyProgramData: " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    ProgressDataModel progressDataModel = response.body();
                    if (Common.isLoggingEnabled) {
                        if (progressDataModel != null) {
                            Log.d(TAG, progressDataModel.toString());
                        } else {
                            Log.e(TAG, "Progress Data Model is null");
                        }
                    }
                    if (progressDataModel != null) {
                        if (response.code() == 200) {
                            if (progressDataModel.getData() != null) {
                                //   if (progressDataModel.getData().getWeeks() > 0) {
                                programTitle.setText(progressDataModel.getData().getProgram());
                                textViewDescription.setText(progressDataModel.getData().getDescription());
                                if (progressDataModel.getData().getWorkouts().size() > 0) {
                                    //add to db
                                    for (int i = 0; i < progressDataModel.getData().getWorkouts().size(); i++) {
                                        for (int j = 0; j < progressDataModel.getData().getWorkouts().get(i).getWorkout().size(); j++) {
                                            if (Common.isLoggingEnabled) {
                                                Log.d(TAG, "Program ID is " + programId);
                                                Log.d(TAG, "Week " + SharedData.week);
                                                Log.d(TAG, "Day " + SharedData.week);
                                            }
                                            if (!dbHelper.isProgramExerciseAvailable(String.valueOf(programId), String.valueOf(progressDataModel.getData().getWorkouts().get(i).getWorkout().get(j).getId()), String.valueOf(SharedData.week), String.valueOf(SharedData.day))) {
                                                dbHelper.addProgramExercises(programId, progressDataModel.getData().getProgram(), progressDataModel.getData().getDescription(), progressDataModel.getData().getWorkouts().get(i).getWorkout().get(j), String.valueOf(SharedData.week), String.valueOf(SharedData.day));
                                            } else {
                                                dbHelper.updateProgramExercises(programId, progressDataModel.getData().getProgram(), progressDataModel.getData().getDescription(), progressDataModel.getData().getWorkouts().get(i).getWorkout().get(j), String.valueOf(SharedData.week), String.valueOf(SharedData.day));
                                            }
                                        }
                                    }
                                    //get data from db
                                    List<ProgramWorkout> dataList = getDataFromDB();

                                    if (dataList != null && dataList.size() > 0) {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "List size of program workout " + dataList.size());
                                        }
                                        /*populateData(dataList);*/
                                        showData(dataList, currentUserId, programId, Integer.parseInt(level_id), Integer.parseInt(goal_id), SharedData.day, SharedData.week);

                                    } else {
                                        mFrameLayout1.setVisibility(View.GONE);
                                        mFrameLayout1.stopShimmerAnimation();
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "List size is null or zero of program workout ");
                                        }
                                    }

                                } else {
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), resources.getString(R.string.workout_unavailable), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Progress Data Model\'s Data is null");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Status is false");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response is null");
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
                        ProgressDataModel progressJSON_Response = new ProgressDataModel();
                        progressJSON_Response = gson.fromJson(response.errorBody().string(), ProgressDataModel.class);
                        if (progressJSON_Response != null) {
                            if (progressJSON_Response.getMessage() != null) {
                                if (isAdded() && getContext() != null) {
                                    Toast.makeText(getContext(), "" + progressJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            if (message != null)
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (isAdded() && getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExercise_getweeklyData_API",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                            Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response is unsuccessful");
                    }
                }
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProgressDataModel> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExercise_getWeeklyData",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                    showToast("Programs not retrieved");
                }
            }
        });


    }

    /*private void populateData(List<ProgramWorkout> workoutList) {
        for (int i = 0; i < workoutList.size(); i++) {
            if (level_id != 0 && goal_id != 0) {
                showData(workoutList.get(i), currentUserId, programId, level_id, goal_id, SharedData.day, SharedData.week);

            }
        }
    }*/

    private List<ProgramWorkout> getDataFromDB() {
        return dbHelper.getAllProgramExercise(String.valueOf(programId), String.valueOf(SharedData.week), String.valueOf(SharedData.day));
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
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExercise_ShowToast",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(exception));
            }
        }
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {

    }

}