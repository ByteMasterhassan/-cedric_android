package com.cedricapp.fragment;

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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.InternetSpeedInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.CoachesDataModel;
import com.cedricapp.model.ProgramWorkout;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class BestExerciseDetailsFragment extends Fragment implements InternetSpeedInterface {
    private MaterialTextView materialTextViewExerciseName, mVideoDescription,
            mTextViewExerciseCategoryName, materialTextViewExerciseDuration;
    private ImageView mCategoryIcon;
    String exerciseName, description, token, duration, categoryName, categoryIcon;
    //VideoView videoView;
    String position;
    private MaterialButton mNextVideoButton;
    int listSize, index, i = 0;
    List<ProgramWorkout> exerciseList;
    //ArrayList<BestProgramModel.Warmup> exerciseList1;
    ArrayList<String> videoIdList = new ArrayList<String>();
    ArrayList<Integer> indexList = new ArrayList<Integer>();
    ProgressBar loadingPB;
    private int currentUserId, level_Id, goal_Id, programId, day, week, workoutId;
    private boolean checkCompleted, watched_status;
    private View view1;
    private ImageButton backArrow;
    StyledPlayerView exoPlayerView;
    private Call<CoachesDataModel> call;


    protected ExoPlayer exoPlayer;
    private SegmentedProgressBar segmentedProgressBar;
    private int watched_count;
    private boolean clicked = false;
    private ProgressiveMediaSource mediaSource;
    private DefaultMediaSourceFactory mediaSourceFactory;
    BlurView blurView;
    LottieAnimationView loading_lav;
    private FirebaseAnalytics firebaseAnalytics;
    DBHelper dbHelper;
    int previousWorkoutID;
    private String message;

    private boolean moveToBackFragment;
    Resources resources;

    String TAG = "BEST_EXERCISE_DETAIL_FRAGMENT_TAG";


    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
        HomeActivity.showBottomNav();
    }

    public BestExerciseDetailsFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_best_exercise_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        init();


        getIntentData();

        initializeVideoListFromLastArray();

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Exercise Name: " + exerciseName);
        }

        //checkPlayedVideosList();
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        mNextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNextVideoButton.startAnimation(myAnim);
                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            clicked = true;
                            nextWorkOut();
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
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


        // getSupportActionBar().hide();
        //initVideoPlayer();
        /*playVideo();*/
        checkVideosPlayedListProgress();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //  exoPlayer.setPlayWhenReady(true);
        if (Util.SDK_INT > 23) {
            initVideoPlayer("onStart");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    private void checkVideosPlayedListProgress() {
        segmentedProgressBar.setCompletedSegments(index);
        // segmentedProgressBar.playSegment(5000);

        if (listSize != 0) {
            segmentedProgressBar.setSegmentCount(listSize);
            segmentedProgressBar.incrementCompletedSegments();
        }
    }

    private void init() {
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        exoPlayerView = view1.findViewById(R.id.idExoPlayerVIew);
        checkCompleted = false;
        //videoView = (VideoView) view1.findViewById(R.id.videoPlayer);
        materialTextViewExerciseName = view1.findViewById(R.id.textViewExerciseName);
        materialTextViewExerciseDuration = view1.findViewById(R.id.textViewVideoDuration);
        mTextViewExerciseCategoryName = view1.findViewById(R.id.mTextViewCategoryName);
        mCategoryIcon = view1.findViewById(R.id.imageCategoryIcon);
        mVideoDescription = view1.findViewById(R.id.textViewExerciseDescriptionText);
        mNextVideoButton = view1.findViewById(R.id.btnNextWorkout);
        loadingPB = view1.findViewById(R.id.progress);
        backArrow = view1.findViewById(R.id.backArrow);
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);

        segmentedProgressBar = (SegmentedProgressBar) view1.findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setCompletedSegments(1);

        setlanguageToWidget();
      /*  currentUserId = SharedData.id;
        Log.d("iiddd", currentUserId);*/
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Day: " + SharedData.day);
        }
        // initVideoPlayer();
        videoIdList.clear();
        dbHelper = new DBHelper(getContext());

        moveToBackFragment = false;
    }

    private void setlanguageToWidget() {
        mNextVideoButton.setText(resources.getString(R.string.next_workout));
    }

    private void getIntentData() {

        assert getArguments() != null;

        token = SharedData.token;

        watched_status = getArguments().getBoolean("watched_status", false);
        currentUserId = getArguments().getInt(Common.SESSION_USER_ID, 0);
        workoutId = getArguments().getInt("workoutId", 0);
        previousWorkoutID = workoutId;
        programId = getArguments().getInt("program_id", 0);
        day = getArguments().getInt("dayNumber", 0);
        week = getArguments().getInt("weekNumber", 0);
        watched_count = getArguments().getInt("count", 0);
        level_Id = getArguments().getInt(Common.SESSION_USER_LEVEL_ID, 0);
        goal_Id = getArguments().getInt(Common.SESSION_USER_GOAL_ID, 0);
        exerciseList = (List<ProgramWorkout>) getArguments().getSerializable("exerciseList");
        //exerciseList1 = (ArrayList<BestProgramModel.Warmup>) getArguments().getSerializable("exerciseList");
        System.out.println(exerciseList + ",,,,,,,,,,,,,,,,,,,,,workoutList");
        exerciseName = getArguments().getString("exercise");
        materialTextViewExerciseName.setText(exerciseName);
        position = getArguments().getString("position");
        // position = "https://firebasestorage.googleapis.com/v0/b/cedric-8cb7d.appspot.com/o/Windmill.mp4?alt=media&token=d890a9e1-7102-446d-8814-92afd19bcc74";
        //description = getArguments().getString("videoDescription");
        duration = getArguments().getString("videoDuration");
        /*categoryName = getArguments().getString("categoryName");
        categoryIcon = getArguments().getString("categoryIcon");*/

        listSize = getArguments().getInt("size");
        System.out.println(listSize + "list size" + watched_count + "count");
        index = getArguments().getInt("index");

        //mVideoDescription.setText(description);
        materialTextViewExerciseDuration.setText(duration);
        //mTextViewExerciseCategoryName.setText(categoryName);
        //setCategoryIcon(categoryIcon);
        blurrBackground();
        startLoading();
        ConnectionDetector.checkInternetSpeed(getContext(), this);

        if (index == listSize - 1 || index == exerciseList.size() - 1 /*|| index == exerciseList1.size() - 1*/) {
            mNextVideoButton.setText(resources.getString(R.string.finish));
        }
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
                                if (message != null)
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
                                                //WatchedVideoIdList.clear();
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
                                                            if (isAdded() && getContext() != null) {
                                                                position = getVideoURLByInternetSpeed(workout);
                                                                if (position != null && !position.matches("")) {
                                                                    initVideoPlayer("getDataFromServer");
                                                                }
                                                            } else {
                                                                if (Common.isLoggingEnabled) {
                                                                    Log.e(TAG, "Fragment is not attached with activity or context is null in ExerciseDetailsFragment");
                                                                }
                                                            }
                                                        } else {
                                                            if (Common.isLoggingEnabled) {
                                                                Log.e(TAG, "Videos URLs Array is Empty (Size = 0)");
                                                            }
                                                            if (isAdded() && getContext() != null) {
                                                                //Toast.makeText(getContext(), "Videos Not Available", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(getContext(), "Video is not available on this url,play next video", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        if (Common.isLoggingEnabled) {
                                                            Log.e(TAG, "Videos URLs Array is null");
                                                        }
                                                        if (isAdded() && getContext() != null) {
                                                            // Toast.makeText(getContext(), "Videos Not Available", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(getContext(), "Video is not available on this url,play next video", Toast.LENGTH_SHORT).show();
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
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.e(TAG, "Response Status " + message.toString());
                            }
                            if (getContext() != null) {
                                if (message != null)
                                    Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }


                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext())
                                    .getLogsDetails("BestExerciseDetailsFragment_Api_Call",
                                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION,
                                            SharedData.caughtException(ex));
                        }
                    }
                    stopLoading();
                }

                @Override
                public void onFailure(Call<CoachesDataModel> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (Common.isLoggingEnabled) {
                        t.printStackTrace();
                    }
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext())
                                .getLogsDetails("BestExerciseDetailsFragment_Api_Call",
                                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION,
                                        SharedData.throwableObject(t));

                    }
                    stopLoading();
                }
            });
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Fragment is not attached with activity or ");
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
                Log.d(TAG, "Play 480p video");
            }
            return getURL("480", workout);
        } else if (SharedData.networkSpeed > ConnectionDetector.AVERAGE_BANDWIDTH &&
                SharedData.networkSpeed < ConnectionDetector.GOOD_BANDWIDTH) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Play 720p video");
            }
            //Average Speed
            return getURL("720", workout);
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Play 1080p video");
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

    void initVideoPlayer(String comingFrom) {
        try {
            // bandwisthmeter is used for
            // getting default bandwidth
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if (position != null && !position.matches("")) {
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
                } else {
                    if (getContext() != null) {
                        // Toast.makeText(getContext(),getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                        new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExerciseDetail_playVideoMethod",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, "Video URL is null or empty string in init method and coming from " + comingFrom);

                    }
                }

            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }


    /*private void checkPlayedVideosList() {
        if (index == listSize - 1 || index == exerciseList.size() - 1 || index == exerciseList1.size() - 1) {

            if ((index == exerciseList.size() - 1) && index == exerciseList1.size() - 1) {
                checkCompleted = true;
            }
            mNextVideoButton.setText("Finish");
        }
    }*/

    private void storeWatchedWorkoutIdSP(int workoutId, int index) {
        if (isAdded() && getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            if (videoIdList.isEmpty() && indexList.isEmpty()) {
                videoIdList.add(String.valueOf(workoutId));
                indexList.add(index);
            } else if (!videoIdList.contains(String.valueOf(workoutId))) {
                videoIdList.add(String.valueOf(workoutId));
            } else if (!indexList.contains(index)) {
                for (int i = 0; i < indexList.size(); i++) {
                    if (indexList.get(i) < index && indexList.get(i) + 1 == index) {
                        indexList.add(index);
                    }
                }
            }

            // getting data from gson and storing it in a string.
            String json = gson.toJson(videoIdList);
            String integerJson = gson.toJson(indexList);
            //  Log.d("mylist",json);

            // below line is to save data in shared
            // prefs in the form of string.

            editor.putString("videoIdList", json);
            editor.putString("indexList", integerJson);
            editor.putBoolean("checkPlayed", checkCompleted);


            // below line is to apply changes
            // and save data in shared prefs.
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "idLoist" + videoIdList.toString());
                Log.d(TAG, "idLoist" + indexList.toString());
                Log.d(TAG, "idLoist" + String.valueOf(exerciseList.size()));
            }
            //Log.d("idLoists", String.valueOf(exerciseList1.size()));

            editor.commit();

        }
    }

    private void nextWorkOut() {
        if (getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) {
                        exoPlayer.setPlayWhenReady(false);
                    }
                    exoPlayer.stop();
                }

                if (clicked == true) {
                    if (watched_count < listSize && watched_status == false) {
                        watched_count = watched_count + 1;
                    } else if (watched_count == listSize) {
                        watched_count = listSize;
                    }
                    if (mNextVideoButton.getText().toString().matches(resources.getString(R.string.finish))) {
                        moveToBackFragment = true;
                        mNextVideoButton.setVisibility(View.GONE);
                    } else {
                        moveToBackFragment = false;
                        mNextVideoButton.setVisibility(View.GONE);
                    }
                    sendWatchedVideoStatusToServer(token, currentUserId, programId, level_Id,
                            goal_Id, day, week, watched_count);

                }

                index += 1;

                checkVideosPlayedListProgress();

                if (index < exerciseList.size() /*|| index < exerciseList1.size()*/) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Video list Index is " + index);
                    }
                    if (exerciseList.size() != 0) {
                        materialTextViewExerciseName.setText(exerciseList.get(index).getWorkoutName());
                        mVideoDescription.setText("");
                        materialTextViewExerciseDuration.setText(exerciseList.get(index).getWorkoutDuration());
               /* mTextViewExerciseCategoryName.setText(exerciseList.get(index).getCategory().getCategoryName());
                categoryIcon = exerciseList.get(index).getCategory().getIcon();*/

                        //position = exerciseList.get(index).getVideoUrl();
                        previousWorkoutID = workoutId;
                        workoutId = exerciseList.get(index).getWorkoutId();
                        watched_status = exerciseList.get(index).isWorkoutIsWatched();
                        setCategoryIcon(categoryIcon);
                        blurrBackground();
                        startLoading();
                        ConnectionDetector.checkInternetSpeed(getContext(), this);
                        //getDataFromServer(workoutId);
             /*   if(watched_count<listSize && watched_status==false){
                    watched_count =watched_count+1 ;
                }
                else if(watched_count==listSize){
                    watched_count=listSize;
                }*/

                        if (loadingPB.getVisibility() == View.GONE || loadingPB.getVisibility() == View.INVISIBLE) {
                            loadingPB.setVisibility(View.VISIBLE);
                            mNextVideoButton.setVisibility(View.INVISIBLE);
                        }

                        /* playVideo();*/
                        // initVideoPlayer();
                    } /*else {
                materialTextViewExerciseName.setText(exerciseList1.get(index).name);
                mVideoDescription.setText(exerciseList1.get(index).description);
                position = exerciseList1.get(index).videoUrl;

                playVideo();
            }*/

                    if (index == listSize - 1 || index == exerciseList.size() - 1) {
                        mNextVideoButton.setVisibility(View.GONE);
                        mNextVideoButton.setText(resources.getString(R.string.finish));
                    }
                }


//        if (getFragmentManager().getBackStackEntryCount() != 0) {
//            getFragmentManager().popBackStack();
//        }
            }
        }
    }

    private void playVideo() {

        try {
            if (isAdded()) {
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        if (loadingPB.getVisibility() == View.INVISIBLE ||
                                loadingPB.getVisibility() == View.GONE) {
                            loadingPB.setVisibility(View.VISIBLE);

                        }
                        mNextVideoButton.setVisibility(View.INVISIBLE);
                        //ToDO
                        if (Common.isLoggingEnabled) {
                            if (Uri.parse(position) != null) {
                                if (Common.isLoggingEnabled) {
                                    Log.d(TAG, "" + Uri.parse(position));
                                }
                            } else {
                                // Toast.makeText(getContext(), "Video is not available on this url,play next video", Toast.LENGTH_SHORT).show();
                            }
                        }
                        try {
                            // we are parsing a video url
                            // and parsing its video uri.
                            // Uri videouri = Uri.parse(position);

                            // we are creating a variable for datasource factory
                            // and setting its user agent as 'exoplayer_view'
                            // DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");

                            // we are creating a variable for extractor factory
                            // and setting it to default extractor factory.
                            //  ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                            // we are creating a media source with above variables
                            // and passing our event handler as null,
                            //MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

                            // inside our exoplayer view
                            // we are setting our player
                            //  exoPlayerView.setPlayer(exoPlayer);

                            // we are preparing our exoplayer
                            // with media source.
                            //exoPlayer.prepare(mediaSource);

                            // we are setting our exoplayer
                            // when it is ready.
                            //   exoPlayer.setPlayWhenReady(true);

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
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "ExoPlayer.STATE_ENDED");
                                        }
                                        if (token != null && currentUserId != 0 && programId != 0 &&
                                                day != 0 && week != 0 && level_Id != 0 && goal_Id != 0) {

                                            storeWatchedWorkoutIdSP(workoutId, index);
                                            if (watched_count < listSize && watched_status == false) {
                                                watched_count = watched_count + 1;
                                            } else if (watched_count == listSize) {
                                                watched_count = listSize;
                                            }
                                            /*loadingPB.setVisibility(View.INVISIBLE);
                                            mNextVideoButton.setVisibility(View.VISIBLE);*/
                                            checkVideosPlayedListProgress();
                                            sendWatchedVideoStatusToServer(token, currentUserId, programId, level_Id, goal_Id, day, week, watched_count);
                                            nextWorkOut();
                                        }
                                    }
                                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "ExoPlayer.STATE_BUFFERING");
                                        }
                                        loadingPB.setVisibility(View.VISIBLE);
                                        mNextVideoButton.setVisibility(View.INVISIBLE);
                                    } else if (playbackState == ExoPlayer.STATE_READY) {
                                        if (Common.isLoggingEnabled) {
                                            Log.d(TAG, "ExoPlayer.STATE_READY");
                                        }
                                        loadingPB.setVisibility(View.INVISIBLE);
                                        mNextVideoButton.setVisibility(View.VISIBLE);
                                        sendEventToAnalytics(workoutId);
                                    }


                                    checkVideosPlayedListProgress();
                                      /*if (watched_count < listSize && watched_status == false) {
                                            watched_count = watched_count + 1;
                                        } else if (watched_count == listSize) {
                                            watched_count = listSize;
                                        }*/
                                    // sendWatchedVideoStatusToServer(token, currentUserId, programId, level_Id, goal_Id, day, week, watched_count);
                                    checkVideosPlayedListProgress();
                                }

                                @Override
                                public void onPlaybackStateChanged(int playbackState) {
                                    //  Player.Listener.super.onPlaybackStateChanged(playbackState);
                                }

                                @Override
                                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                                    //  Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
                                }

                                @Override
                                public void onIsPlayingChanged(boolean isPlaying) {
                                    // Player.Listener.super.onIsPlayingChanged(isPlaying);
                                }
                            });

                         /*   exoPlayer.addListener(new ExoPlayer.EventListener() {


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

                                    if (playbackState == ExoPlayer.STATE_ENDED) {
                                        if (token != null && currentUserId != 0 && programId != 0 &&
                                                day != 0 && week != 0 && level_Id != 0 && goal_Id != 0) {
                                            storeWatchedWorkoutIdSP(workoutId, index);
                                            if (watched_count < listSize && watched_status == false) {
                                                watched_count = watched_count + 1;
                                            } else if (watched_count == listSize) {
                                                watched_count = listSize;
                                            }
                                            checkVideosPlayedListProgress();
                                            System.out.println("count" + watched_count);
                                            sendWatchedVideoStatusToServer(token, currentUserId, programId, level_Id, goal_Id, day, week, watched_count);
                                            nextWorkOut();
                                        }
                                    }
                                        if (playbackState == ExoPlayer.STATE_BUFFERING)
                                            loadingPB.setVisibility(View.VISIBLE);
                                        else if (playbackState == ExoPlayer.STATE_READY )
                                            loadingPB.setVisibility(View.INVISIBLE);
                                      *//*if (watched_count < listSize && watched_status == false) {
                                            watched_count = watched_count + 1;
                                        } else if (watched_count == listSize) {
                                            watched_count = listSize;
                                        }*//*
                                       // sendWatchedVideoStatusToServer(token, currentUserId, programId, level_Id, goal_Id, day, week, watched_count);
                                        checkVideosPlayedListProgress();

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
                            if (Common.isLoggingEnabled) {
                                ex.printStackTrace();
                            }
                            FirebaseCrashlytics.getInstance().recordException(ex);
                            if (getContext() != null) {
                                new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExerciseDetail_playVideoMethod",
                                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                            }
                        }




               /*MediaController mediaController = new MediaController(requireContext());
                mediaController.setAnchorView(videoView);

                videoView.setMediaController(mediaController);
                // videoView.setVideoPath(String.valueOf(MainActivity.fileArrayList.get(Integer.parseInt(position))));
                videoView.setVideoURI(Uri.parse(position));
                videoView.requestFocus();
                Time duration = new Time(videoView.getDuration());
                // Toast.makeText(getContext(), "" + duration, Toast.LENGTH_SHORT).show();

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        loadingPB.setVisibility(View.GONE);
                        videoView.start();
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, String.valueOf(videoView.getDuration()));
                            Log.d(TAG, String.valueOf(videoView.getCurrentPosition()));
                        }

                        //  if (videoView.getCurrentPosition() == 100) {
                        try {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, String.valueOf(videoView.getCurrentPosition()));
                                System.out.println("Video View" + String.valueOf(videoView.getCurrentPosition()));
                            }
                            storeWatchedWorkoutIdSP();
                            nextWorkOut();
                        } catch (Exception ex) {

                            FirebaseCrashlytics.getInstance().recordException(ex);
                            ex.printStackTrace();
                            System.out.println("===========++++++++++++=============");
                        }

                        // }
                        System.out.println(position + 1 + "===========jjjjj=============");
              *//**//*  try {
                  //  videoView.setVideoURI(Uri.parse(String.valueOf((DashboardFragment.fileArrayList.get(Integer.parseInt(position + 1))))));
                    System.out.println(position + "===========+++++++]]]]]]]]]+++++=============");
                    //videoView.start();
                }*//**//*
                        //  nextWorkOut();
                        // requireActivity().onBackPressed();
                    }
                });*//*
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(TAG, "URL is empty");
                        if (isAdded() && getContext() != null) {
                            // Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(TAG, "URL is null");
                    if (isAdded() && getContext() != null) {
                        //  Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }*/
                    }
                }
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExerciseDetail_playVideoMethod",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
        }
    }

    private void sendEventToAnalytics(int workoutId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(this.workoutId));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void sendWatchedVideoStatusToServer(String token, int currentUserId, int programId,
                                                int level_id,
                                                int goal_id, int day, int week, int watchedWorkouts) {
        Call<SignupResponse> call = ApiClient.getService().sendWatchedVideosToServer("Bearer " + token, currentUserId, programId, level_id, goal_id, day, week, watchedWorkouts);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    if (Common.isLoggingEnabled) {
                        if (response.body().getMessage() != null) {
                            Log.d(TAG, "Watched status response in BestExerciseDetailsFragment" + response.body().getMessage().toString());
                        }
                    }
                    dbHelper.updateProgramWorkoutWatchedStatus(programId, previousWorkoutID, String.valueOf(week), String.valueOf(day), true);
                    previousWorkoutID = workoutId;
                    if (moveToBackFragment) {
                        if (isAdded()) {
                            if (getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    //dbHelper.updateProgramWorkoutWatchedStatus(programId, previousWorkoutID, String.valueOf(week), String.valueOf(day), true);

                                    SessionUtil.setWorkoutProgressLoad(true, getContext());

                                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            }
                        }
                    }

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dbHelper.updateProgramWorkoutWatchedStatus(programId, previousWorkoutID, String.valueOf(week), String.valueOf(day), false);
                    previousWorkoutID = workoutId;
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "BestExerciseDetailsFragment: sendWatchedVideoStatusToServer reponse code is " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExerciseDetail_sendWatchedVideoStatusToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
            }
        });
    }


    public void initializeVideoListFromLastArray() {
        if (isAdded() && getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

            // creating a variable for gson.
            Gson gson = new Gson();

            // below line is to get to string present from our
            // shared prefs if not present setting it as null.

            String json = sharedPreferences.getString("videoIdList", "null");
            String iJson = sharedPreferences.getString("indexList", "null");

            // below line is to get the type of our array list.
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();

            Type iType = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            // in below line we are getting data from gson
            // and saving it to our array list
            if (gson.fromJson(json, type) != null && gson.fromJson(iJson, iType) != null) {
                videoIdList.clear();
                indexList.clear();
                videoIdList = gson.fromJson(json, type);
                indexList = gson.fromJson(iJson, iType);
            }

            // checking below if the array list is empty or not
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
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("BestProgramExerciseDetail_stopLoading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));

            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }

    @Override
    public void internetSpeed(int speed) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        getDataFromServer(workoutId);
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