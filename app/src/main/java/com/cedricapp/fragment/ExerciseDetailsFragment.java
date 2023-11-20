
package com.cedricapp.fragment;


import static android.os.Looper.getMainLooper;
import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.InternetSpeedInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.CoachesDataModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.WorkoutDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.activity.HomeActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ExerciseDetailsFragment extends Fragment implements InternetSpeedInterface {
    private MaterialTextView materialTextViewExerciseName, mVideoDescription,
            mTextViewExerciseCategoryName, materialTextViewExerciseDuration;
    private ImageView mCategoryIcon;
    MaterialCardView mExerciseDetailsCardView;

    String exerciseName, description, token;
    //VideoView videoView;
    String position;
    Resources resources;
    private int listSize, coach_id, dayNumber, weekNumber, workOutId, currentUserId;
    int index, i = 0;
    private ImageButton backArrow;

    ProgressBar loadingPB;
    ArrayList<WorkoutDataModel.Data.Workout> exerciseList;
    // ArrayList<CoachesDataModel.Warmup> exerciseList1;

    private MaterialButton mNextVideoButton;
    private View view1;

    //Minimum Video you want to buffer while Playing
    private int MIN_BUFFER_DURATION = 2000;
    //Max Video you want to buffer during PlayBack
    private int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    private int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    private int MIN_PLAYBACK_RESUME_BUFFER = 2000;

    StyledPlayerView exoPlayerView;


    protected ExoPlayer exoPlayer;
    ArrayList<String> WatchedVideoIdList = new ArrayList<String>();


    private SegmentedProgressBar segmentedProgressBar;
    private boolean watched_status;
    private int watched_count;
    private boolean clicked = false;
    private ProgressiveMediaSource mediaSource;
    private DefaultMediaSourceFactory mediaSourceFactory;
    private String duration, categoryName, categoryIcon;
    private Call<CoachesDataModel> call;
    BlurView blurView;
    LottieAnimationView loading_lav;
    private FirebaseAnalytics firebaseAnalytics;
    private SwipeRefreshLayout swipeRefreshLayout;
    long lastPosition;
    DBHelper dbHelper;
    private String message;
    boolean isButtonEnabled = true;

    String TAG = "EXERCISE_DETAILS_FRAGMENT_TAG";


    public ExerciseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.hideBottomNav();
        SharedData.redirectToDashboard = false;
        loadingPB.setVisibility(View.VISIBLE);
        if (exoPlayer != null) {
            initVideoPlayer();
        }
    }

    @Override
    public void onStop() {
        /*Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null)
                    exoPlayer.setPlayWhenReady(false);
                if (Util.SDK_INT > 23) releasePlayer();
            }
        });*/

        HomeActivity.showBottomNav();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        //inialize
        init();

        segmentedProgressBar.setCompletedSegments(index + 1);
        //TODO Swipe to refresh layout is commited because its crash with progress bar
     /*   swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isAdded() && getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        getDataFromServer(workOutId);
                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                        showToast("No Internet, Please turn ON Internet");

                    }
                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);


                    }
                }
            }

        });*/
        //initVideoPlayer();

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        //listener for back button
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getContext() != null) {
                        /*  if (ConnectionDetector.isConnectedWithInternet(getContext())) {*/
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
                            getFragmentManager().popBackStack();
                        }
                       /* } else {
                            Toast.makeText(getContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                }
            }
        });
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
       /* mNextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNextVideoButton.startAnimation(myAnim);
                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            clicked = true;
                            nextWorkOut();
                        } else {
                            Toast.makeText(getContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });*/
        mNextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonEnabled) {
                    mNextVideoButton.startAnimation(myAnim);
                    if (isAdded()) {
                        if (getContext() != null) {
                            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                isButtonEnabled = false;
                                clicked = true;
                                nextWorkOut();

                                // Delay the re-enabling of the button by 2 seconds
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Re-enable the button
                                        isButtonEnabled = true;
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
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


    private void getDataFromBundle() {
        exerciseList = (ArrayList<WorkoutDataModel.Data.Workout>) getArguments().getSerializable("exerciseList");
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Workout List: " + exerciseList);
        }
        exerciseName = getArguments().getString("exercise");
        listSize = getArguments().getInt("size");
        workOutId = getArguments().getInt("workOutId");

        currentUserId = getArguments().getInt("user_id", 0);
        dayNumber = getArguments().getInt("dayNumber", 0);
        weekNumber = getArguments().getInt("weekNumber", 0);
        coach_id = getArguments().getInt("coachId", 0);
        index = getArguments().getInt("index");
        if (Common.isLoggingEnabled) {
            Log.d(TAG, listSize + "size" + watched_count + "count");
        }
        checkVideosPlayedListProgress();
        blurrBackground();
        startLoading();
        ConnectionDetector.checkInternetSpeed(getContext(), this);

        if (SharedData.token != null) {
            token = SharedData.token;
        }

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Exercise Name : " + exerciseName);
            Log.d(TAG, listSize + " list size" + exerciseList.size() + "size" + index + "size");
        }
        if (index == listSize - 1 || index == exerciseList.size() - 1/*|| index == exerciseList1.size() - 1*/) {
            mNextVideoButton.setText(resources.getString(R.string.finish));
        }

        materialTextViewExerciseName.setText(exerciseName);
        /*description = getArguments().getString("videoDescription");
        duration = getArguments().getString("videoDuration");
        categoryName = getArguments().getString("categoryName");
        categoryIcon = getArguments().getString("categoryIcon");
        setCategoryIcon(categoryIcon);


        mVideoDescription.setText(description);
        materialTextViewExerciseDuration.setText(duration);
        mTextViewExerciseCategoryName.setText(categoryName);*/

    }

    private void setCategoryIcon(String categoryIcon) {
        if (/*isAdded() &&*/ getContext() != null) {
            Glide.with(getContext()).load(categoryIcon).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException
                                                    e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(mCategoryIcon);

        }
    }

    private void init() {

        //swipe to refresh
        // swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshExercise);
        exoPlayerView = view1.findViewById(R.id.idExoPlayerVIew);
        //videoView = (VideoView) view1.findViewById(R.id.videoPlayer);
        mExerciseDetailsCardView = view1.findViewById(R.id.cardViewExerciseDetails);
        materialTextViewExerciseName = view1.findViewById(R.id.textViewExerciseName);
        materialTextViewExerciseDuration = view1.findViewById(R.id.textViewVideoDuration);
        mTextViewExerciseCategoryName = view1.findViewById(R.id.mTextViewCategoryName);
        mCategoryIcon = view1.findViewById(R.id.imageCategoryIcon);

        mVideoDescription = view1.findViewById(R.id.textViewExerciseDescriptionText);
        mNextVideoButton = view1.findViewById(R.id.btnNextWorkout);
        loadingPB = view1.findViewById(R.id.progress);
        backArrow = view1.findViewById(R.id.backArrow);
        segmentedProgressBar = (SegmentedProgressBar) view1.findViewById(R.id.segmented_progressbar);
        //segmentedProgressBar.setCompletedSegments(1);
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        dbHelper = new DBHelper(getContext());

        setlanguageToWidget();
        getDataFromBundle();
    }

    private void setlanguageToWidget() {
        mNextVideoButton.setText(resources.getString(R.string.next_workout));
    }

    void getDataFromServer(int workoutID) {
        if (isAdded() && getContext() != null) {
            call = ApiClient.getService().getVideoByWorkoutID("Bearer " + SessionUtil.getAccessToken(getContext()), workoutID);
            call.enqueue(new Callback<CoachesDataModel>() {
                @Override
                public void onResponse(Call<CoachesDataModel> call, Response<CoachesDataModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Response Status " + message.toString());
                            }
                            //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                            CoachesDataModel coachesDataModel = response.body();
                            if (coachesDataModel != null) {
                                if (coachesDataModel.getData() != null) {
                                    if (coachesDataModel.getData().getWorkouts() != null) {
                                        List<CoachesDataModel.Workout> workouts = coachesDataModel.getData().getWorkouts();
                                        if (workouts != null) {
                                            if (workouts.size() > 0) {
                                                CoachesDataModel.Workout workout = workouts.get(0);
                                                WatchedVideoIdList.clear();
                                                initializeVideoListFromLastArray();
                                                if (workout != null) {
                                                    if (workout.getDescription() != null) {
                                                        mVideoDescription.setText(workout.getDescription());
                                                    }
                                                    if (workout.getDuration() != null) {
                                                        materialTextViewExerciseDuration.setText(workout.getDuration());
                                                    }
                                                    if (workout.getCategory() != null) {
                                                        if (workout.getCategory().getIcon() != null) {
                                                            setCategoryIcon(workout.getCategory().getIcon());
                                                        }
                                                        if (workout.getCategory().getCategoryName() != null) {
                                                            mTextViewExerciseCategoryName.setText(workout.getCategory().getCategoryName());
                                                        }
                                                    }

                                                    if (workout.getVideos() != null) {
                                                        if (workout.getVideos().size() > 0) {
                                                            //if (isAdded() && getContext() != null) {
                                                            position = getVideoURLByInternetSpeed(workout);
                                                            if (position != null && !position.matches("")) {
                                                                initVideoPlayer();
                                                            }
                                                            /*} else {
                                                                if (Common.isLoggingEnabled) {
                                                                    Log.e(TAG, "Fragment is not attached with activity or context is null in ExerciseDetailsFragment");
                                                                }
                                                            }*/
                                                        } else {
                                                            if (Common.isLoggingEnabled) {
                                                                Log.e(TAG, "Videos URLs Array is Empty (Size = 0)");
                                                            }
                                                            if (isAdded() && getContext() != null) {
                                                                Toast.makeText(getContext(), resources.getString(R.string.video_unavailable), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        if (Common.isLoggingEnabled) {
                                                            Log.e(TAG, "Videos URLs Array is null");
                                                        }
                                                        if (isAdded() && getContext() != null) {
                                                            Toast.makeText(getContext(), resources.getString(R.string.video_unavailable), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } else {
                                                    if (Common.isLoggingEnabled) {
                                                        Log.e(TAG, "Workout is null");
                                                    }
                                                }
                                            } else {
                                                if (Common.isLoggingEnabled) {
                                                    Log.e(TAG, "Workouts size is zero");
                                                }
                                            }
                                        } else {
                                            if (Common.isLoggingEnabled) {
                                                Log.e(TAG, "Workouts is null");
                                            }
                                        }
                                    } else {
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "coachesDataModel.getData().getWorkouts() == null");
                                        }
                                    }
                                } else {
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "coachesDataModel.getData() == null");
                                    }
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "coachesDataModel == null");
                                }
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
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            if (getContext() != null) {
                                if (message != null) {
                                    Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_getDataFomServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                    }
                    stopLoading();
                }

                @Override
                public void onFailure(Call<CoachesDataModel> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_getDataFromServer",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                    }
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    stopLoading();

                }
            });
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragment is not attached with activity or getContext() returning null");
            }
        }
    }

    private String getVideoURLByInternetSpeed(CoachesDataModel.Workout workout) {

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Internet Speed is " + SharedData.networkSpeed);
        }
        if (SharedData.networkSpeed <= ConnectionDetector.AVERAGE_BANDWIDTH) {
            //Poor speed
            if (Common.isLoggingEnabled) {
                Log.d("NET_SPEED", "Play 480p video");
            }
            return getURL("480", workout);
        } else if (SharedData.networkSpeed > ConnectionDetector.AVERAGE_BANDWIDTH &&
                SharedData.networkSpeed < ConnectionDetector.GOOD_BANDWIDTH) {
            if (Common.isLoggingEnabled) {
                Log.d("NET_SPEED", "Play 720p video");
            }
            //Average Speed
            return getURL("720", workout);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d("NET_SPEED", "Play 1080p video");
            }
            //Good Speed
            return getURL("1080", workout);
        }
    }

    private String getURL(String resolution, CoachesDataModel.Workout workout) {
        if (workout != null) {
            if (workout.getVideos() != null) {
                for (int i = 0; i < workout.getVideos().size(); i++) {
                    if (workout.getVideos().get(i).getResolution() != null) {
                        if (workout.getVideos().get(i).getResolution().equals(resolution)) {
                            return workout.getVideos().get(i).getUrl();
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "workout.getVideos().get(i).getResolution() is null");
                        }
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "workout.getVideos() is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "workout is null");
            }
        }
        return "";
    }

    private void checkVideosPlayedListProgress() {
        segmentedProgressBar.setCompletedSegments(index);
        // segmentedProgressBar.playSegment(5000);
        if (listSize != 0) {
            segmentedProgressBar.setSegmentCount(listSize);
            segmentedProgressBar.incrementCompletedSegments();
        }
    }

    void initVideoPlayer() {
        try {
            // bandwisthmeter is used for
            // getting default bandwidth
            if (isAdded() && getContext() != null) {
                DataSource.Factory mediaDataSourceFactory = new DefaultDataSource.Factory(getContext());
                mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(position));

                mediaSourceFactory = new DefaultMediaSourceFactory(mediaDataSourceFactory);

                exoPlayer = new ExoPlayer.Builder(getContext())
                        .setMediaSourceFactory(mediaSourceFactory)
                        .build();

                exoPlayer.addMediaSource(mediaSource);
                // we are setting our exoplayer
                // when it is ready.
                exoPlayer.setPlayWhenReady(true);
                exoPlayerView.setPlayer(exoPlayer);
                // we are preparing our exoplayer
                // with media source.
                exoPlayer.prepare(mediaSource);

                exoPlayerView.requestFocus();

                playVideo();
            }


        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_initVideoPlayer",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    private void nextWorkOut() {
       /* onPause();
        onStop();*/
        lastPosition = 0;
        if (exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
            }
            exoPlayer.stop();
            //exoPlayer.setPlayWhenReady(false);

            // onStop();
            //exoPlayer.seekTo(0);
        } else {
            initVideoPlayer();
        }
        //exoPlayer.stop();
        // onPause();
        if (clicked == true) {
            if (exerciseList != null) {
                if (listSize == exerciseList.size()) {
                    // workOutId=exerciseList.get(index).getId();
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, exerciseList.size() + "list55555555555 " + listSize);
                    }
                    sendWatchedVideoStatusToServer(currentUserId, coach_id, workOutId, dayNumber, weekNumber);
                }
            }
        }
        clicked = false;
        try {
            if (mNextVideoButton.getText().toString().matches("Finish") || mNextVideoButton.getText().toString().matches("Avsluta")) {
                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
            }


            index += 1;
            checkVideosPlayedListProgress();

            if (index < exerciseList.size()/*|| index < exerciseList1.size()*/) {
                if (exerciseList.size() != 0) {
                    // position = getVideoURLByInternetSpeed(workout);
                    //position = exerciseList.get(index).getVideoUrl();
                    workOutId = exerciseList.get(index).getId();
                    watched_status = exerciseList.get(index).getWatched();
                    materialTextViewExerciseName.setText(exerciseList.get(index).getName());
                    if (isAdded() && getContext() != null) {
                        blurrBackground();
                        startLoading();
                        ConnectionDetector.checkInternetSpeed(getContext(), this);
                    }
                    //getDataFromServer(workOutId);
                   /* mVideoDescription.setText(exerciseList.get(index).getDescription());
                    materialTextViewExerciseDuration.setText(exerciseList.get(index).getDuration());
                    mTextViewExerciseCategoryName.setText(exerciseList.get(index).getCategory().getCategoryName());

                    categoryIcon = exerciseList.get(index).getCategory().getIcon();
                    setCategoryIcon(categoryIcon);

                    System.out.println(watched_count + "watched");
                    *//*playVideo();*//*
                    initVideoPlayer();*/
                } else {

               /*materialTextViewExerciseName.setText(exerciseList1.get(index).name);
                mVideoDescription.setText(exerciseList1.get(index).description);
                position = exerciseList1.get(index).videoUrl;*/


                    //  playVideo();
                }

                if (index == listSize - 1 || index == exerciseList.size() - 1 /*|| index == exerciseList1.size() - 1*/) {
                    // mNextVideoButton.setVisibility(View.GONE);
                    mNextVideoButton.setText(resources.getString(R.string.finish));
                }
            }

        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_NextWorkout",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    private void playVideo() {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        if (loadingPB.getVisibility() == View.INVISIBLE || loadingPB.getVisibility()
                                == View.GONE) {
                            loadingPB.setVisibility(View.VISIBLE);
                            //ToDo
                            mNextVideoButton.setVisibility(View.INVISIBLE);
                        }




                   /*MediaController mediaController = new MediaController(getContext());
                    mediaController.setAnchorView(videoView);*/

                        if (Common.isLoggingEnabled) {
                            if (Uri.parse(position) != null) {
                                Log.d(TAG, "" + Uri.parse(position));
                            }
                        }
                        try {
                            // we are parsing a video url
                            // and parsing its video uri.
                            // Uri videouri = Uri.parse(position);

                            // we are creating a variable for datasource factory
                            // and setting its user agent as 'exoplayer_view'
                            //DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");

                            // we are creating a variable for extractor factory
                            // and setting it to default extractor factory.
                            //  ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                            // we are creating a media source with above variables
                            // and passing our event handler as null,
                            //MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

                            // inside our exoplayer view
                            // we are setting our player
                            // exoPlayerView.setPlayer(exoPlayer);

                            // we are preparing our exoplayer
                            // with media source.
                            //  exoPlayer.prepare(mediaSource);

                            // we are setting our exoplayer
                            // when it is ready.
                            // exoPlayer.setPlayWhenReady(true);
                            if (exoPlayer != null && lastPosition != 0) {
                                //exoPlayer.setPlayWhenReady(true);
                                exoPlayer.seekTo(lastPosition);
                            }
                            exoPlayer.addListener(new Player.Listener() {
                                @Override
                                public void onEvents(Player player, Player.Events events) {
                                    // Player.Listener.super.onEvents(player, events);
                                }

                                @Override
                                public void onTimelineChanged(Timeline timeline, int reason) {
                                    // Player.Listener.super.onTimelineChanged(timeline, reason);
                                }

                                @Override
                                public void onIsLoadingChanged(boolean isLoading) {
                                    Player.Listener.super.onIsLoadingChanged(isLoading);
                                }

                                @Override
                                public void onLoadingChanged(boolean isLoading) {
                                    Player.Listener.super.onLoadingChanged(isLoading);
                                    if (isLoading) {
                                        loadingPB.setVisibility(View.VISIBLE);
                                        mNextVideoButton.setVisibility(View.INVISIBLE);
                                    } else {
                                        mNextVideoButton.setVisibility(View.VISIBLE);
                                        loadingPB.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                    // Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);


                                    if (playbackState == ExoPlayer.STATE_ENDED) {
                                        if (token != null && currentUserId != 0 && coach_id != 0 && workOutId != 0 && dayNumber != 0 && weekNumber != 0) {

                                            loadingPB.setVisibility(View.INVISIBLE);
                                            mNextVideoButton.setVisibility(View.VISIBLE);

                                           /* if (watched_count < listSize && watched_status == false) {
                                                watched_count = watched_count + 1;
                                            } else if (watched_count == listSize) {
                                                watched_count = listSize;
                                            }*/

                                            sendWatchedVideoStatusToServer(currentUserId, coach_id, workOutId, dayNumber, weekNumber);

                                            checkVideosPlayedListProgress();
                                            // System.out.println(watched_count+"count");
                                            nextWorkOut();
                                        }
                                        if (playbackState == ExoPlayer.STATE_BUFFERING) {
                                            loadingPB.setVisibility(View.VISIBLE);

                                        } else if (playbackState == ExoPlayer.STATE_READY) {
                                            loadingPB.setVisibility(View.INVISIBLE);
                                            // storeWatchedWorkoutIdSP(workOutId);
                                            // sendWatchedVideoStatusToServer(currentUserId, coach_id, workOutId, dayNumber, weekNumber);
                                            sendEventToAnalytics(workOutId);


                                            checkVideosPlayedListProgress();
                                            //player back ended
                                            // mNextVideoButton.setVisibility(View.VISIBLE);

                                        }

                                    }
                                }

                                @Override
                                public void onPlaybackStateChanged(int playbackState) {
                                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                                }

                                @Override
                                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                                    Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
                                }

                                @Override
                                public void onIsPlayingChanged(boolean isPlaying) {
                                    Player.Listener.super.onIsPlayingChanged(isPlaying);
                                }
                            });

                           /* exoPlayer.addListener(new ExoPlayer.EventListener() {


                                @Override
                                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                                }

                                @Override
                                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                                }

                                @Override
                                public void onLoadingChanged(boolean isLoading) {
                                    if (isLoading) {
                                        loadingPB.setVisibility(View.VISIBLE);
                                    } else {
                                        mNextVideoButton.setVisibility(View.VISIBLE);
                                        loadingPB.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                                        if (token != null && currentUserId != 0 && coach_id != 0 && workOutId != 0 && dayNumber != 0 && weekNumber != 0) {
                                            if (playbackState == ExoPlayer.STATE_ENDED) {


                                           *//*  if (watched_count < listSize && watched_status == false) {
                                                    watched_count = watched_count + 1;
                                                } else if (watched_count == listSize) {
                                                    watched_count = listSize;
                                                }*//*

                                                sendWatchedVideoStatusToServer(currentUserId, coach_id, workOutId, dayNumber, weekNumber);
                                                checkVideosPlayedListProgress();
                                               // System.out.println(watched_count+"count");
                                                nextWorkOut();
                                            }
                                            if (playbackState == ExoPlayer.STATE_BUFFERING) {

                                                loadingPB.setVisibility(View.VISIBLE);
                                            } else if (playbackState == ExoPlayer.STATE_READY){
                                                loadingPB.setVisibility(View.INVISIBLE);
                                             storeWatchedWorkoutIdSP(workOutId);
                                               // sendWatchedVideoStatusToServer(currentUserId, coach_id, workOutId, dayNumber, weekNumber);
                                            checkVideosPlayedListProgress();
                                            //player back ended

                                        }
                                    }
                                }

                                @Override
                                public void onRepeatModeChanged(int repeatMode) {

                                }

                                @Override
                                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                                }

                                @Override
                                public void onPlayerError(ExoPlaybackException error) {
                                    if (Common.isLoggingEnabled)
                                        Log.e(TAG, "Exo Player error: " + error.toString());
                                }

                                @Override
                                public void onPositionDiscontinuity(int reason) {

                                }

                                @Override
                                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                                }

                                @Override
                                public void onSeekProcessed() {

                                }
                            });*/


                        } catch (Exception ex) {
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            if (getContext() != null) {
                                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_playVideo",
                                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                            }
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                        }


                  /*videoView.setMediaController(mediaController);
                    videoView.setVideoURI(Uri.parse(position));
                    videoView.requestFocus();

                    Time duration = new Time(videoView.getDuration());

                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            loadingPB.setVisibility(View.GONE);
                            mNextVideoButton.setVisibility(View.VISIBLE);
                            videoView.start();
                        }
                    });

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            System.out.println(position + 1 + "===============");
                            try {
                                videoView.setVideoURI(Uri.parse(String.valueOf((DashboardFragment
                                        .fileArrayList.get(Integer.parseInt(position + 1))))));
                                System.out.println(position + "===========++++++++++++=======");
                                videoView.start();
                            } catch (NumberFormatException ex) {
                                FirebaseCrashlytics.getInstance().recordException(ex);
                                ex.printStackTrace();
                                System.out.println("===========+++++++=========");
                            }
                        }
                    });
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            loadingPB.setVisibility(View.GONE);
                            mNextVideoButton.setVisibility(View.INVISIBLE);
                            if (isAdded())
                                if (getContext() != null)
                                    Toast.makeText(getContext(), "Cannot play this video", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });*/

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "No internet connection while playing video");
                        }
                    }

                }
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_playVideoMethod",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    private void sendEventToAnalytics(int workOutId) {
       /* Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(this.workOutId));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/
        Bundle params = new Bundle();
        params.putInt("work id", workOutId);
        firebaseAnalytics.logEvent("watchVideo", params);
        firebaseAnalytics.setDefaultEventParameters(params);
    }

    public void initializeVideoListFromLastArray() {
        try {
            if (isAdded() && getContext() != null) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

                // creating a variable for gson.
                Gson gson = new Gson();

                // below line is to get to string present from our
                // shared prefs if not present setting it as null.

                String json = sharedPreferences.getString("watchedVideoIdList", "null");

                // below line is to get the type of our array list.
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();

                // in below line we are getting data from gson
                // and saving it to our array list
                if (gson.fromJson(json, type) != null) {
                    WatchedVideoIdList.clear();
                    WatchedVideoIdList = gson.fromJson(json, type);
                }

                // checking below if the array list is empty or not
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    private void sendWatchedVideoStatusToServer(int currentUserId, int coach_id,
                                                int workOutId, int dayNumber, int weekNumber) {
        Call<SignupResponse> call = ApiClient.getService().sendWatchedVideoToServer("Bearer " + token, currentUserId, coach_id, workOutId, dayNumber, weekNumber);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isStatus()) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, response.body().getMessage().toString());
                        }
                        dbHelper.setCoachVideoWatchedStatus(coach_id, workOutId, 1, weekNumber, dayNumber);
                    } else {
                        if (Common.isLoggingEnabled) {
                            if (response.body() != null && response.body().getMessage() != null)
                                Log.d(TAG, response.body().getMessage().toString());
                        }
                        dbHelper.setCoachVideoWatchedStatus(coach_id, workOutId, 0, weekNumber, dayNumber);
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_sendWatchedVideoToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }


    private void storeWatchedWorkoutIdSP(int workOutId) {
        try {
            if (isAdded() && getContext() != null) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();


                if (WatchedVideoIdList.isEmpty()) {
                    WatchedVideoIdList.add(String.valueOf(workOutId));
                    System.out.println(WatchedVideoIdList.toString() + " watchedvideoList");
                } else if (!WatchedVideoIdList.contains(String.valueOf(workOutId))) {
                    WatchedVideoIdList.add(String.valueOf(workOutId));
                    System.out.println(WatchedVideoIdList.toString() + " watchedvideoList");
                }

                // getting data from gson and storing it in a string.
                String json = gson.toJson(WatchedVideoIdList);
                //  Log.d("mylist",json);

                // below line is to save data in shared
                // prefs in the form of string.

                editor.putString("watchedVideoIdList", json);
                // editor.putBoolean("checkPlayed", checkCompleted);


                // below line is to apply changes
                // and save data in shared prefs.

                Log.d("idLoist", WatchedVideoIdList.toString());
                Log.d("idLosts", String.valueOf(exerciseList.size()));
                //Log.d("idLoists", String.valueOf(exerciseList1.size()));

                editor.commit();

            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ExerciseDetailsFragment_StoreWatchedWorkoutID",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {


        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (Util.SDK_INT > 23) {
                    releasePlayer();
                }
                if (exoPlayer != null) {
                    exoPlayer.stop();
                    lastPosition = exoPlayer.getCurrentPosition();
                    //exoPlayer.setPlayWhenReady(false);
                }
            }
        });
        super.onPause();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        /*if (Util.SDK_INT > 23) initVideoPlayer();*/

    }

    private void blurrBackground() {
        try {
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
        } catch (Exception ex) {

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
        try {
            blurView.setVisibility(View.INVISIBLE);
            blurView.setVisibility(View.GONE);
            //Enable user interaction

            Activity activity = getActivity();
            try {
                if (isAdded() && activity != null) {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            } catch (ActivityNotFoundException e) {

                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                }
            }
            loading_lav.setVisibility(View.GONE);
            loading_lav.pauseAnimation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void internetSpeed(int speed) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Internet Speed: " + speed);
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //do stuff like remove view etc
                        getDataFromServer(workOutId);
                    }
                });

            } else {
                stopLoading();
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "No Internet Connection");
                }
                Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            stopLoading();
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragment is not attached with activity or context is null");
            }
        }
    }
}