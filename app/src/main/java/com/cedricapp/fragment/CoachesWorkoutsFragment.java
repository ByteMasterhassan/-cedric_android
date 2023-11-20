package com.cedricapp.fragment;


import static com.cedricapp.common.Common.EXCEPTION;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.ShimmerAdapter;
import com.cedricapp.adapters.WorkoutAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.CoachesDataModel;
import com.cedricapp.model.WorkoutDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.UserStatusUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class CoachesWorkoutsFragment extends Fragment implements UserStatusInterface {

    public static View.OnClickListener myOnClickListener;
    private MaterialTextView materialTextViewCoachName, materialTextViewCoachRole, coaches_txt,
            materialTextViewCoachDescription, mTextViewWarmup, mTextViewWorkout;
    private ImageButton backArrow;
    private ImageView mCoachImage;
    String coachName, role, description;
    private MaterialButton monBtn, tueBtn, wedBtn, thuBtn, friBtn, satBtn, sunBtn;

    private RecyclerView.LayoutManager layoutManager;
    private Boolean dbclear = true;

    RecyclerView myRecyclerView, workoutRecyclerview, shimmerRecyclerview;
    //MyAdapter myAdapter;
    WorkoutAdapter workoutAdapter;
    ShimmerAdapter shimmerWorkoutAdapter;
    WorkoutDataModel coachData;
    public static ArrayList<CoachesDataModel> coachesDataArrayList = new ArrayList<CoachesDataModel>();
    private Context context;
    private int currentUserId, dayNumber, weekNumber, coach_id, level_id, goal_id;
    String vid, token;
    private View view1;
    //private ShimmerFrameLayout mFrameLayout;
    private Context mContext;
    private Call<WorkoutDataModel> coachesDataCall;
    private List<WorkoutDataModel.Data.Workout> coachWorkoutsData;
    private DBHelper dbHelper;
    int count;
    int workout_count = 0;
    int limit;
    int offset;
    ArrayList<Boolean> shimmerStatusList;
    private WeekDaysHelper weekDaysHelper;
    private String toDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout loadMoreLL;
    static boolean loadmore;
    boolean isCallFromLoadMore;
    boolean isMoreDataAvailable;
    private String message;

    String TAG = "COACHES_WORKOUT_FRAGMENT_TAG";

    Resources resources;

    UserStatusUtil userStatusUtil;

    public CoachesWorkoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.canToastShow = false;
        SharedData.redirectToDashboard = true;
        int currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);
        if (currentDateDifference > 1) {
            if (coachWorkoutsData != null) {
                coachWorkoutsData.clear();
            }
        }
        HomeActivity.hideBottomNav();
    }


    @Override
    public void onPause() {
        super.onPause();
        //mFrameLayout.stopShimmerAnimation();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weekDaysHelper = new WeekDaysHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_coaches_exercises, container, false);

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        //  initialize
        init();
        offset = 0;
        isMoreDataAvailable = true;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currentUserId != 0) {
                    if (isAdded()) {
                        if (getContext() != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                userStatusUtil.getUserStatus("Bearer " + token);
                                if (coachWorkoutsData != null) {
                                    setShimmerAdapter();
                                    setShimmerStatusList(true);
                                    setWorkout(coachWorkoutsData, false);

                                }
                                //StartLoading();
                                //getUserProfileData(currentUserId);

                                coachWorkoutsData = null;
                                offset = 0;
                                getCoachesWorkoutDataFromAPI(token, currentUserId, coach_id, dayNumber, weekNumber, level_id, goal_id, offset);
                                if (workoutAdapter != null) {
                                    workoutAdapter.notifyDataSetChanged();
                                }

                            } else {
                                if (swipeRefreshLayout != null) {
                                    if (swipeRefreshLayout.isRefreshing()) {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                                showToast(resources.getString(R.string.turn_on_your_internet));
                            }
                        } else {
                            if (swipeRefreshLayout != null) {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }

                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
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


        if (isAdded() && getContext() != null) {
            toDate = SessionUtil.getSelectedDate(getContext());
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Selected Date in CoachesWorkoutFragement class is " + toDate);
            }
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                int currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);
                if (currentDateDifference == 1) {
                    coachWorkoutsData = dbHelper.getAllCoachesExercise("" + coach_id, weekNumber, dayNumber);
                    if (SessionUtil.isDailyCoachLoad(getContext())) {
                        dbHelper.deleteAllCoachesExercisesData();
                        coachWorkoutsData = null;
                    }
                    if (coachWorkoutsData != null && coachWorkoutsData.size() > 0) {
                        setWorkout(coachWorkoutsData, true);
                    } else {
                        coachWorkoutsData = null;
                        if (getContext() != null) {
                            if (WeekDaysHelper.getCountOfDays(SessionUtil.getDailyDate(getContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd()) > 1) {
                                dbHelper.deleteAllCoachesExercisesData();
                            }
                            getCoachesWorkoutDataFromAPI(SessionUtil.getAccessToken(getContext()), currentUserId, coach_id, dayNumber, weekNumber, level_id, goal_id, 0);

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "2: getContext is null in CoachesWorkoutFragment, onViewCreated");
                            }
                        }

                    }

                } else {
                    /* if (SharedData.token != null) {*/
                    /*if (SharedData.token != "") {*/
                    if (getContext() != null) {
                        // if (workout_count < 10) {
                        getCoachesWorkoutDataFromAPI(SessionUtil.getAccessToken(getContext()), currentUserId, coach_id, dayNumber, weekNumber, level_id, goal_id, 0);
                       /* } else {
                            getCoachesWorkoutDataFromAPI(SessionUtil.getAccessToken(getContext()), currentUserId, coach_id, dayNumber, weekNumber, level_id, goal_id, offset);
                        }*/
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "3: getContext is null in CoachesWorkoutFragment, onViewCreated");
                        }
                    }

                }
            } else {
                coachWorkoutsData = dbHelper.getAllCoachesExercise("" + coach_id, weekNumber, dayNumber);
                if (coachWorkoutsData != null && coachWorkoutsData.size() > 0) {
                    for (int i = 0; i < coachWorkoutsData.size(); i++) {
                        if (coachWorkoutsData.get(i).getCoachId().equals(coach_id)) {
                            setWorkout(coachWorkoutsData, true);
                        }
                    }
                } else {
                    showToast(resources.getString(R.string.no_internet_connection_no_data_available));
                }
            }

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragement is not addted on activity or getContext is null in CoachesWorkoutFragment, onViewCreated");
            }
        }

        //get recyclerview data
        // getWarmUpData(coachWorkoutsData);

        //listener for back button
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getActivity() != null) {
                        /* if (ConnectionDetector.isConnectedWithInternet(getActivity())) {*/
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
                            getFragmentManager().popBackStack();
                        }
                       /* } else {
                            Toast.makeText(getActivity(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }*/

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "getActivity is returning null");
                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Fragment is not added to activity");
                    }
                }

            }
        });
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
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    private void getCoachesWorkoutDataFromAPI(String token, int currentUserId, int coach_id, int dayNumber, int weekNumber, int level_id, int goal_id, int offset) {
       /* if (dbclear) {
            dbHelper.deleteAllCoachesExercisesData();
        }*/

        coachesDataCall = ApiClient.getService().getAllWorkoutsByCoach("Bearer " + SharedData.token, coach_id, "" + SharedData.id, dayNumber, weekNumber, "" + offset);

        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        coachesDataCall.enqueue(new Callback<WorkoutDataModel>() {
            @Override
            public void onResponse(Call<WorkoutDataModel> call, Response<WorkoutDataModel> response) {
                // inside on response method we are checking
                // if the response is success or not.
                try {
                    if (loadmore) {
                        loadmore = false;
                        loadMoreLL.setVisibility(View.GONE);
                    }
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        coachData = response.body();
                        if (coachData != null && coachData.getData() != null && coachData.getData().getWorkouts() != null && coachData.getData().getWorkouts().size() > 0) {
                            if (coachWorkoutsData == null) {
                                if (getContext() != null) {
                                    if (SessionUtil.isDailyCoachLoad(getContext())) {
                                        SessionUtil.setDailyCoachLoad(false, getContext());
                                    }
                                }
                                coachWorkoutsData = coachData.getData().getWorkouts();
                                int currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);
                                if (currentDateDifference == 1) {
                                    dbHelper.addOrUpdateCoachesExercises(coachWorkoutsData, coach_id, weekNumber, dayNumber);
                                    coachWorkoutsData = dbHelper.getAllCoachesExercise("" + coach_id, weekNumber, dayNumber);
                                    setWorkout(coachWorkoutsData, true);
                                    return;
                                }

                                // coachWorkoutsData= dbHelper.getAllCoachesExercise();
                                setWorkout(coachWorkoutsData, false);
                                // workoutRecyclerview.scrollToPosition(coachWorkoutsData.size() - 5);
                            } else {
                                if (getContext() != null) {
                                    if (SessionUtil.isDailyCoachLoad(getContext())) {
                                        SessionUtil.setDailyCoachLoad(false, getContext());
                                    }
                                }
                                isCallFromLoadMore = true;
                                List<WorkoutDataModel.Data.Workout> workoutData = coachWorkoutsData;
                                coachWorkoutsData.addAll(coachData.getData().getWorkouts());
                                int currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);
                                if (currentDateDifference == 1) {
                                    dbHelper.addOrUpdateCoachesExercises(workoutData, coach_id, weekNumber, dayNumber);
                                    coachWorkoutsData = dbHelper.getAllCoachesExercise("" + coach_id, weekNumber, dayNumber);
                                    setWorkout(coachWorkoutsData, true);
                                    return;
                                }
                                // workoutRecyclerview.scrollToPosition(coachWorkoutsData.size() - 5);

                                // coachWorkoutsData= dbHelper.getAllCoachesExercise();
                                setWorkout(coachWorkoutsData, false);
                            }

                        }


                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                            LogoutUtil.redirectToLogin(getContext());
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Response Status " + message.toString());
                        }
                        if (getContext() != null)
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        /*mFrameLayout.setVisibility(View.GONE);
                        mFrameLayout.stopShimmerAnimation();*/
                        isMoreDataAvailable = false;

                    }

                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("CoachesWorkoutFragment_workoutAPICall",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Dashboard Fragment exception while geeting coaches data : " + ex.toString());
                        ex.printStackTrace();
                    }
                    /*if (mFrameLayout != null) {
                        if (mFrameLayout.isAnimationStarted()) {
                            mFrameLayout.setVisibility(View.GONE);
                            mFrameLayout.stopShimmerAnimation();
                        }
                    }*/

                  /*  if (textViewCoaches.getVisibility() == View.VISIBLE && recyclerView.getVisibility() == View.VISIBLE) {
                        textViewCoaches.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    showToast("Something went wrong");*/
                }

                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<WorkoutDataModel> call, Throwable t) {

                /*if (mFrameLayout != null) {
                    if (mFrameLayout.isAnimationStarted()) {
                        mFrameLayout.setVisibility(View.GONE);
                        mFrameLayout.stopShimmerAnimation();
                    }
                }*/

              /*  if (textViewCoaches.getVisibility() == View.VISIBLE && recyclerView.getVisibility() == View.VISIBLE) {
                    textViewCoaches.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }*/

                //  showToast("Something went wrong");

                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("CoachesWorkoutFragment_getCoachesWorkoutDataMethod",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
            }
        });

    }

    private void getDataFromBundle() {

        try {
            //coachData = getArguments().getParcelable("coach");
            //System.out.println(coachData.name);
            /**
             *below shared id code is written due to crash occurred
             * When SharedData.id was null then app crashed
             */
            if (SharedData.id != null) {
                currentUserId = Integer.parseInt(SharedData.id);
            } else {
                if (getContext() != null) {
                    currentUserId = Integer.parseInt(SessionUtil.getUserID(getContext()));
                }
            }
            if (SharedData.token != null) {
                token = SharedData.token;
            } else {
                if (getContext() != null) {
                    token = SessionUtil.getAccessToken(getContext());
                }
            }
            if (currentUserId == 0) {
                currentUserId = getArguments().getInt(Common.SESSION_USER_ID, 0);
            }
            coach_id = getArguments().getInt(Common.COACH_ID, 0);
            dayNumber = getArguments().getInt("dayNumber", 0);
            weekNumber = getArguments().getInt("weekNumber", 0);
            level_id = getArguments().getInt(Common.SESSION_USER_LEVEL_ID, 0);
            goal_id = getArguments().getInt(Common.SESSION_USER_GOAL_ID, 0);
            coachName = getArguments().getString(Common.COACH_NAME, "");
            workout_count = getArguments().getInt("workout_count", 0);
            limit = getArguments().getInt("offset_limit", 0);
            role = getArguments().getString("role");
            description = getArguments().getString("description");

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Workout Count: " + workout_count + "\n offset: " + offset);
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

        //set data

        materialTextViewCoachName.setText(coachName);
        if (role != null) {
            materialTextViewCoachRole.setText(role);
        }

        materialTextViewCoachDescription.setText(description);
        //mCoachImage.setImageResource(Integer.parseInt(String.valueOf((getArguments().getInt("profileImage")))));
        try {
            String img = getArguments().getString("profileImage");
            if (isAdded()) {
                if (mContext != null) {
                    Glide.with(mContext).load(img).into(mCoachImage);
                }
            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("CoachesWorkoutFragment_GetBundleData",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
                Log.e(TAG, "Exception while downloading coach image");
            }
        }
        setShimmerAdapter();
    }

    private void init() {
        mContext = getContext();
        dbHelper = new DBHelper(getContext());

       /* mFrameLayout.startShimmerAnimation();
        mFrameLayout.setVisibility(View.VISIBLE);*/
        //System.out.print("token" + token.toString());
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshCoaches);
        backArrow = view1.findViewById(R.id.backArrow);
        coaches_txt = view1.findViewById(R.id.coaches);
        materialTextViewCoachName = view1.findViewById(R.id.coachName);
        materialTextViewCoachDescription = view1.findViewById(R.id.textViewDescription);
        materialTextViewCoachRole = view1.findViewById(R.id.textViewCoachRole);
        mTextViewWarmup = view1.findViewById(R.id.textViewWarmUP);
        mTextViewWorkout = view1.findViewById(R.id.textViewWorkouts);
        mCoachImage = view1.findViewById(R.id.coachDp);
        //mFrameLayout = view1.findViewById(R.id.shimmerLayout);
        shimmerRecyclerview = view1.findViewById(R.id.recyclerviewShimmerWorkout);
        shimmerRecyclerview.setHasFixedSize(true);
        shimmerStatusList = new ArrayList<>();
      /*  monBtn = view1.findViewById(R.id.monBtn);
        tueBtn = view1.findViewById(R.id.tuesBtn);
        wedBtn = view1.findViewById(R.id.wedBtn);
        thuBtn = view1.findViewById(R.id.thurBtn);
        friBtn = view1.findViewById(R.id.friBtn);
        satBtn = view1.findViewById(R.id.satBtn);
        sunBtn = view1.findViewById(R.id.sunBtn);*/

        //      recyclerview for warmup
        myRecyclerView = view1.findViewById(R.id.recyclerviewWarmUp);
        myRecyclerView.setHasFixedSize(true);

        //      recyclerview for workout
        workoutRecyclerview = view1.findViewById(R.id.recyclerviewWorkout);
        workoutRecyclerview.setHasFixedSize(true);

        loadMoreLL = view1.findViewById(R.id.loadMoreLL);


        //Toast.makeText(getContext(),"Internet Speed: "+ConnectionDetector.getInternetSpeed(getContext()),Toast.LENGTH_SHORT).show();

        //setWeekbtn();

        // get data from bundle
        getDataFromBundle();
        setLanguageToWidgets();

        userStatusUtil = new UserStatusUtil(getContext(), CoachesWorkoutsFragment.this, resources);
        if (ConnectionDetector.isConnectedWithInternet(getContext()))
            userStatusUtil.getUserStatus("Bearer " + token);

        //LinearLayoutManager layoutManager = (LinearLayoutManager) workoutRecyclerview.getLayoutManager();

        workoutRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // super.onScrolled(recyclerView, dx, dy);
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!workoutRecyclerview.canScrollVertically(1)/*lastVisiblePosition == workoutRecyclerview.getChildCount()*/) {
                    /*if(loadmore)
                        loadmore = false;
                    loadmore = true;*/
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        if (workout_count >= 9/*&&offset<=workout_count*/) {
                            if (isMoreDataAvailable) {
                                loadmore = true;
                                offset = offset + 9;
                                if (Common.isLoggingEnabled) {
                                    Log.d("offset1", String.valueOf(offset));
                                }
                                dbclear = false;
                                loadMoreLL.setVisibility(View.VISIBLE);
                                getCoachesWorkoutDataFromAPI(token, currentUserId, coach_id, dayNumber, weekNumber, level_id, goal_id, offset);
                            }
                            //workoutAdapter.notifyDataSetChanged();
                            /* workoutRecyclerview.smoothScrollToPosition(workoutRecyclerview.c);*/
                        }
                    } else {
                        if (getContext() != null)
                            showToast(resources.getString(R.string.no_internet_connection));
                    }
                }
            }
        });


    }

    private void setLanguageToWidgets() {
        coaches_txt.setText(resources.getString(R.string.coaches));
        mTextViewWorkout.setText(resources.getString(R.string.workout));
        mTextViewWarmup.setText(resources.getString(R.string.warm_up));
    }

    private void setShimmerAdapter() {
        if (isAdded() && getContext() != null) {
            if (workout_count < 10) {
                addShimmerStatus(true, workout_count);
            } else {
                addShimmerStatus(true, 10);
            }
        }
    }

    void addShimmerStatus(boolean isStarted, int count) {
        for (int i = 0; i < count; i++) {
            shimmerStatusList.add(isStarted);
        }
        shimmerWorkoutAdapter = new ShimmerAdapter(mContext, isAdded(), shimmerStatusList);
        shimmerRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerview.setAdapter(shimmerWorkoutAdapter);
    }

    void setShimmerStatusList(boolean isStarted) {
        for (int i = 0; i < shimmerStatusList.size(); i++) {
            shimmerStatusList.set(i, isStarted);
        }
        shimmerWorkoutAdapter.notifyDataSetChanged();
        shimmerRecyclerview.setVisibility(View.GONE);
    }

    private void setWorkout(List<WorkoutDataModel.Data.Workout> coachWorkoutsData, boolean isDataForDB) {
        if (isDataForDB) {
            setShimmerStatusList(false);
            swipeRefreshLayout.setRefreshing(false);
            if (isAdded()) {
                if (getContext() != null) {
                    mTextViewWorkout.setVisibility(View.VISIBLE);
                    workoutRecyclerview.setVisibility(View.VISIBLE);
                    if (coachWorkoutsData.size() > 0) {
                        if (SharedData.id != null && coach_id != 0 && dayNumber != 0 && weekNumber != 0) {
                            if (coachWorkoutsData != null) {
                                //  List<WorkoutDataModel.Data.Workout> List=dbHelper.getAllCoachesExercise();
                                workoutAdapter = new WorkoutAdapter(mContext, coachWorkoutsData, Integer.parseInt(SharedData.id), coach_id, dayNumber, weekNumber, isAdded(), resources);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                // linearLayoutManager.setReverseLayout(true);
                                //linearLayoutManager.setStackFromEnd(true);
                                //linearLayoutManager.setReverseLayout(true);
                                workoutRecyclerview.setLayoutManager(linearLayoutManager);
                                workoutRecyclerview.setAdapter(workoutAdapter);
                                workoutAdapter.notifyDataSetChanged();
                                if (isCallFromLoadMore) {
                                    workoutRecyclerview.scrollToPosition(workoutAdapter.getItemCount() - 10);
                                    isCallFromLoadMore = false;
                                }

                            }

                        }
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is returning null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Fragment is not added to activity");
                }
            }
        } else {
            if (coachWorkoutsData != null) {
                if (coachWorkoutsData.isEmpty()) {
                        /*mFrameLayout.startShimmerAnimation();
                        mFrameLayout.setVisibility(View.VISIBLE);*/

                    mTextViewWorkout.setVisibility(View.GONE);
                    workoutRecyclerview.setVisibility(View.GONE);
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Workout list is empty");
                    }

                } else {
                        /*mFrameLayout.stopShimmerAnimation();
                        mFrameLayout.setVisibility(View.GONE);*/
                    // shimmerRecyclerview.setVisibility(View.GONE);
                    setShimmerStatusList(false);

                    //shimmerWorkoutAdapter.stopShimmer();
                    if (isAdded()) {
                        if (getContext() != null) {
                            mTextViewWorkout.setVisibility(View.VISIBLE);
                            workoutRecyclerview.setVisibility(View.VISIBLE);
                            if (isAdded() && getContext() != null) {
                                if (SharedData.id != null && coach_id != 0 && dayNumber != 0 && weekNumber != 0) {
                                    workoutAdapter = new WorkoutAdapter(mContext, coachWorkoutsData, Integer.parseInt(SharedData.id), coach_id, dayNumber, weekNumber, isAdded(), resources);
                                    // workoutRecyclerview.setLayoutManager(linearLayoutManager);
                                    workoutRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                                    workoutRecyclerview.setAdapter(workoutAdapter);
                                    workoutAdapter.notifyDataSetChanged();
                                    workoutRecyclerview.scrollToPosition(workoutAdapter.getItemCount() - 10);
                                }
                            }

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "getContext is returning null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Fragment is not added to activity");
                        }
                    }
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Workout list is null");
                }
            }
        }
    }


    // warmup Adapter setting
//ToDo needs to be change
      /*  if (coachData.warmup.size() == 0) {
            mTextViewWarmup.setVisibility(View.GONE);
            myRecyclerView.setVisibility(View.GONE);
//            mFrameLayout.startShimmerAnimation();
//            mFrameLayout.setVisibility(View.VISIBLE);

        } else {
//            mFrameLayout.stopShimmerAnimation();
//            mFrameLayout.setVisibility(View.GONE);
            mTextViewWarmup.setVisibility(View.VISIBLE);
            myRecyclerView.setVisibility(View.VISIBLE);
            myAdapter = new MyAdapter(getContext(), coachData);
            myRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            myRecyclerView.setAdapter(myAdapter);
        }*/


    // from db
    // if (dbHelper.getAllCoachesExercise() != null && dbHelper.getAllCoachesExercise().size() > 0) {
    //TODO
    //  coachWorkoutsData=dbHelper.getAllCoachesExercise();

            /*mFrameLayout.stopShimmerAnimation();
            mFrameLayout.setVisibility(View.GONE);*/
    //shimmerRecyclerview.setVisibility(View.GONE);
    //shimmerWorkoutAdapter.setStopShimmer(true);
    //shimmerWorkoutAdapter = new ShimmerAdapter(dataAvailable);
            /*if(workout_count<10) {
                for (int i = 0; i < workout_count; i++) {
                    RecyclerView.ViewHolder viewHolderAdapter = shimmerRecyclerview.findViewHolderForAdapterPosition(i);
                    ShimmerFrameLayout shimmerFrameLayout = viewHolderAdapter.itemView.findViewById(R.id.shimmerLayout);
                    shimmerFrameLayout.stopShimmerAnimation();

                }
            }else{
                for (int i = 0; i < 10; i++) {
                    RecyclerView.ViewHolder viewHolderAdapter = shimmerRecyclerview.findViewHolderForAdapterPosition(i);
                    ShimmerFrameLayout shimmerFrameLayout = viewHolderAdapter.itemView.findViewById(R.id.shimmerLayout);
                    shimmerFrameLayout.stopShimmerAnimation();

                }
            }*/


    //shimmerRecyclerview.setVisibility(View.GONE);
    // for(int i=0;i<count;i++)
    //shimmerWorkoutAdapter.stopShimmer(shimmerRecyclerview.getChildAdapterPosition(shimmerRecyclerview.getChildAt(i)));


    //} else {
            /*if(workout_count<10) {
                for (int i = 0; i < workout_count; i++) {
                    RecyclerView.ViewHolder viewHolderAdapter = shimmerRecyclerview.findViewHolderForAdapterPosition(i);
                    ShimmerFrameLayout shimmerFrameLayout = viewHolderAdapter.itemView.findViewById(R.id.shimmerLayout);
                    shimmerFrameLayout.stopShimmerAnimation();

                }
            }else{
                for (int i = 0; i < 10; i++) {
                    RecyclerView.ViewHolder viewHolderAdapter = shimmerRecyclerview.findViewHolderForAdapterPosition(i);
                    ShimmerFrameLayout shimmerFrameLayout = viewHolderAdapter.itemView.findViewById(R.id.shimmerLayout);
                    shimmerFrameLayout.stopShimmerAnimation();

                }
            }*/

    //shimmerRecyclerview.setVisibility(View.GONE);
    //  if (coachData != null) {

    //}
        /* else {
                if (Common.isLoggingEnabled)
                    Log.d(TAG, "Coaches data is null");
            }*/
    // }


    // }


    void setWeekbtn() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                monBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                monBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.TUESDAY:
                tueBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                tueBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.WEDNESDAY:
                wedBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                wedBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.THURSDAY:
                thuBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                thuBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.FRIDAY:
                friBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                friBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.SATURDAY:
                satBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                satBtn.setTextColor(Color.WHITE);
                break;
            case Calendar.SUNDAY:
                sunBtn.setBackgroundResource(R.drawable.gradient_drawable_button);
                sunBtn.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {

    }
}



