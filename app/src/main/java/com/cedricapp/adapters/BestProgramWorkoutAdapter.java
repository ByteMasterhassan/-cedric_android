package com.cedricapp.adapters;

import static com.cedricapp.common.Common.EXCEPTION;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.fragment.BestExerciseDetailsFragment;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.ProgramWorkout;
import com.cedricapp.R;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class BestProgramWorkoutAdapter extends RecyclerView.Adapter<BestProgramWorkoutVideoHolder> {

    private final Context context;
    private int currentUserId, programId, level_Id, goal_Id, dayNumber, weekNumber;
    String exercise, videoUrl;
    private ImageView imageThumbnail;
    private Bitmap bitmap;
    private String exerciseVideo, exerciseDescription;
    private int listSize;
    List<ProgramWorkout> workouts;
    int watched_count = 0;
    private FirebaseAnalytics firebaseAnalytics;
    private ArrayList<String> watchedVideosList = null;
    private ArrayList<Integer> watchedVideosIndexList;
    private int count;
    Resources resources;

    public BestProgramWorkoutAdapter(Context context, List<ProgramWorkout> programWorkoutList, int currentUserId,
                                     int programId, int level_Id, int goal_id, int day, int week, Resources resources) {

        this.context = context;
        this.currentUserId = currentUserId;
        this.programId = programId;
        this.level_Id = level_Id;
        this.goal_Id = goal_id;
        this.dayNumber = day;
        this.weekNumber = week;
        this.workouts = programWorkoutList;
        this.resources=resources;
        if (Common.isLoggingEnabled) {
            System.out.println(SharedData.id + " currentUserId");
        }
        if (SharedData.id != null) {
            currentUserId = Integer.parseInt(SharedData.id);
        }

    }


    @Override
    public BestProgramWorkoutVideoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.best_program_workout, viewGroup, false);
        return new BestProgramWorkoutVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BestProgramWorkoutVideoHolder workoutVideoHolder, @SuppressLint("RecyclerView") int position) {
        /*BestProgramModel uploadCurrent = mUploads;
        ArrayList<BestProgramModel.Workout> bestWorkoutList= (ArrayList<BestProgramModel.Workout>) uploadCurrent.getWorkout();
        int index = position;*/
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        setCheckBoxes();
        if (workouts.size() != 0) {
            //for(position=0;i<workouts.getWorkout().size();i++){
            if (position == 0) {
                workoutVideoHolder.firstBar.setVisibility(View.INVISIBLE);
            }
            if (position == getItemCount() - 1) {
                workoutVideoHolder.lastBar.setVisibility(View.INVISIBLE);
            }
            //}
        }


        if (workouts.get(position).isWorkoutIsWatched() == false) {
            workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(false);
            if (count == 0) {
                count++;
                workoutVideoHolder.firstBar.setBackgroundColor(context.getColor(R.color.app_logo));
                //workoutVideoHolder.lastBar.setBackgroundColor(context.getColor(R.color.app_logo));
                workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(context.getDrawable(R.drawable.custom_radio_btn_timeline));
                workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#363C69"));
                workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#363C69"));
            } else {
                workoutVideoHolder.firstBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                workoutVideoHolder.lastBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#C5C5C5"));
                workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#C5C5C5"));
                workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(context.getDrawable(R.drawable.custom_light_radio_btn_timeline));

            }

        } else if (workouts.get(position).isWorkoutIsWatched() == true) {
            workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(true);
            workoutVideoHolder.firstBar.setBackgroundColor(context.getColor(R.color.app_logo));
            workoutVideoHolder.lastBar.setBackgroundColor(context.getColor(R.color.app_logo));
            workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(context.getDrawable(R.drawable.custom_radio_btn_timeline));
            watched_count = watched_count + 1;
        }
        System.out.println(watched_count + "watched count");
        //TODO API Needs to be change
        checkForWatchedVideosCheckList();


      /*if(watchedVideosIndexList.isEmpty()){
          clearList();



        }*/
       /* if (watchedVideosList != null) {
            if (watchedVideosList.contains(String.valueOf(workouts.getWorkout().get(position).getId()))) {
                workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(true);
            } else {
                workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(false);
            }

        }*/


        workoutVideoHolder.txtFileName.setText(workouts.get(position).getWorkoutName());
        workoutVideoHolder.textVideoDuration.setText(workouts.get(position).getWorkoutDuration());
        //videoUrl=uploadCurrent.getUrl();
        /* Glide.with(context).load(uploadCurrent.workout.get(position).thumbnail).into(workoutVideoHolder.imageThumbnail);*/
        //Picasso.get().load(uploadCurrent.getImgThumbnail()).into(videoHolder.imageThumbnail);


        try {
            Glide.with(context).load(/*Common.IMG_BASE_URL + */workouts.get(position).getWorkoutThumbnail())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException
                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            workoutVideoHolder.shimmerFrameLayout.stopShimmerAnimation();
                            workoutVideoHolder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            workoutVideoHolder.mWorkoutUpLinearLayout.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(workoutVideoHolder.imageThumbnail);
        } catch (Throwable throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable);
            if (context != null) {
                new LogsHandlersUtils(context).getLogsDetails("BestProgramWorkoutHolder_imageLoadingException",
                        SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(throwable));
            }

            throwable.printStackTrace();
        }


        workoutVideoHolder.workoutParentLayout.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                exercise = workoutVideoHolder.txtFileName.getText().toString();

                if (watched_count != 0) {
                    if (workouts.get(position).isWorkoutIsWatched() == true) {
                        startFragment(position);
                    } else if (position != 0) {
                        if ((workouts.get(position - 1).isWorkoutIsWatched() == true) &&
                                (workouts.get(position).isWorkoutIsWatched() == false)) {
                            startFragment(position);
                        }
                    } else if ((position == 0) && (workouts.get(position).isWorkoutIsWatched() == false)) {
                        startFragment(position);
                    } else {
                        Toast.makeText(context, resources.getString(R.string.please_select_in_seq), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (position == 0) {
                        startFragment(position);
                    } else if (workouts.get(position).isWorkoutIsWatched() == true) {
                        startFragment(position);
                    }
                    //int position=0;


                }


            }
        });


    }

    private void setCheckBoxes() {

    }

    private void clearList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("indexList");

        editor.apply();
    }

    private void startFragment(int position) {
        if(context!=null)
        if(SessionUtil.isSubscriptionAvailable(context)) {
            Fragment fragment = new BestExerciseDetailsFragment();
            FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                    .getSupportFragmentManager().beginTransaction();

            Bundle param = new Bundle();
            param.putInt("workoutID", workouts.get(position).getWorkoutId());
            firebaseAnalytics.logEvent("watchVideo", param);
            firebaseAnalytics.setDefaultEventParameters(param);

            Bundle bundle = new Bundle();
            bundle.putInt("size", listSize);
            bundle.putInt("count", watched_count);
            bundle.putBoolean("watched_status", workouts.get(position).isWorkoutIsWatched());
            bundle.putSerializable("exerciseList", (Serializable) workouts);
            bundle.putInt("index", position);
            bundle.putInt("workoutId", workouts.get(position).getWorkoutId());
            bundle.putInt(Common.SESSION_USER_ID, currentUserId);
            bundle.putInt(Common.SESSION_USER_LEVEL_ID, level_Id);
            bundle.putInt(Common.SESSION_USER_GOAL_ID, goal_Id);
            bundle.putInt("program_id", programId);
            bundle.putInt("dayNumber", dayNumber);
            bundle.putInt("weekNumber", weekNumber);
            bundle.putString("videoDuration", workouts.get(position).getWorkoutDuration());
       /* bundle.putString("categoryIcon", workouts.getWorkout().get(position).getCategory().getIcon());
        bundle.putString("categoryName", workouts.getWorkout().get(position).getCategory().getCategoryName());
        bundle.putString("videoDuration", workouts.getWorkout().get(position).getDuration());
        bundle.putString("position", workouts.getWorkout().get(position).getVideoUrl());*/
            /*bundle.putString("videoDescription", workouts.getWorkout().get(position).getDescription());*/
            bundle.putString("exercise", workouts.get(position).getWorkoutName()); //key and value
            fragment.setArguments(bundle);
            mFragmentTransaction.replace(R.id.navigation_container, fragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }else{
            DialogUtil.showSubscriptionEndDialogBox(context,context.getResources());
        }
    }


   /* boolean isVideoEnabled(int position, int watched_count, boolean previousWatched,
                           boolean isWatched) {
        if (watched_count != 0) {
            if (isWatched) {
                return true;
            } else if (position != 0) {
                if (previousWatched && isWatched) {
                    return true;
                }
            } else if (position == 0 && !isWatched) {
                return true;
            }
        } else {
            if (position == 0) {
                return true;
            } else if (isWatched) {
                return true;
            }
        }
        return false;
    }*/

    private void checkForWatchedVideosCheckList() {
        if (getContext() != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

            // creating a variable for gson.
            Gson gson = new Gson();

            // below line is to get to string present from our
            // shared prefs if not present setting it as null.

            String json = sharedPreferences.getString("videoIdList", "null");
            String intJson = sharedPreferences.getString("indexList", "null");

            // below line is to get the type of our array list.
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();

            Type iType = new TypeToken<ArrayList<Integer>>() {
            }.getType();

            // in below line we are getting data from gson
            // and saving it to our array list
            if (gson.fromJson(json, type) != null) {
                watchedVideosList = gson.fromJson(json, type);
            }
            if (gson.fromJson(intJson, iType) != null) {

                watchedVideosIndexList = gson.fromJson(intJson, iType);
            }


            // checking below if the array list is empty or not
            if (Common.isLoggingEnabled) {
                if (watchedVideosList != null) {
                    Log.d("mylist1", watchedVideosList.toString());
                    Log.d("mylist1", watchedVideosIndexList.toString());
                }
            }
        }

    }


    @Override
    public int getItemCount() {
        //   return mUploads.workout.size();


        if (workouts.size() != 0) {
            listSize = workouts.size();
            return workouts.size();
        } else {
            return 0;
        }

    }
}

class BestProgramWorkoutVideoHolder extends RecyclerView.ViewHolder {

    MaterialTextView txtFileName;
    ImageView imageThumbnail;
    MaterialTextView textVideoDuration;
    LinearLayout mWorkoutUpLinearLayout;
    ConstraintLayout workoutParentLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    RadioButton mRadioBtnWatchedStatus;
    View lastBar, firstBar;

    BestProgramWorkoutVideoHolder(View view) {
        super(view);

        txtFileName = view.findViewById(R.id.textViewBestProgramWorkout);
        textVideoDuration = view.findViewById(R.id.textViewBestProgramWorkoutTime);
        imageThumbnail = view.findViewById(R.id.bestProgramImageWorkout);
        mWorkoutUpLinearLayout = view.findViewById(R.id.bestProgramWorkoutLinearLayout);
        shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayoutBestWorkout);
        mRadioBtnWatchedStatus = view.findViewById(R.id.radioButtonWatchedStatus);
        workoutParentLayout = view.findViewById(R.id.workoutParentLayout);
        lastBar = view.findViewById(R.id.lastLine);
        firstBar = view.findViewById(R.id.firstLine);

    }

}
