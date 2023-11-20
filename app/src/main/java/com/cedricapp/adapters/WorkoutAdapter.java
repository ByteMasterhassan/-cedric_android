package com.cedricapp.adapters;

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
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
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
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.fragment.ExerciseDetailsFragment;
import com.cedricapp.model.WorkoutDataModel;
import com.cedricapp.R;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutVideoHolder> {
    private final Context context;
    String exercise, exerciseVideo, exerciseDescription;
    boolean watchedStatus;
    private List<WorkoutDataModel.Data.Workout> workOutUploads;
    private ImageView imageThumbnail;
    private Bitmap bitmap;
    private int listSize, coach_id, dayNumber, weekNumber, currentUserId;
    private List<String> watchedVideosList = null;
    private int watched_count, position_count;
    ArrayList<WorkoutDataModel.Data.Workout> workoutList;
    private FirebaseAnalytics firebaseAnalytics;
    boolean isAdded;
    boolean next = false;
    int item_position;
    int count;
    Resources resources;

    public WorkoutAdapter(Context context, List<WorkoutDataModel.Data.Workout> uploads, int currentUserId, int coach_id, int dayNumber, int weekNumber, boolean isAdded,   Resources resources) {
        this.context = context;
        this.dayNumber = dayNumber;
        this.coach_id = coach_id;
        this.weekNumber = weekNumber;
        // this.currentUserId=currentUserId;
        workOutUploads = uploads;
        currentUserId = Integer.parseInt(SharedData.id);
        System.out.println(currentUserId + " ooooid");
        this.isAdded = isAdded;
        this.resources=resources;
    }

    @Override
    public WorkoutVideoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coaches_workout, viewGroup, false);
        return new WorkoutVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkoutVideoHolder workoutVideoHolder, @SuppressLint("RecyclerView") int position) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        try {
            WorkoutDataModel.Data.Workout uploadCurrent = workOutUploads.get(position);//.get(position);
            //workoutVideoHolder.shimmerLayout.startShimmerAnimation();
            int index = position;
            currentUserId = Integer.parseInt(SharedData.id);
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Index of workout adapter: " + String.valueOf(position));
            workoutList = (ArrayList<WorkoutDataModel.Data.Workout>) workOutUploads;
            Integer workoutCoachId = uploadCurrent.getId();
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Workout Couch ID: " + workoutCoachId);
                Log.d(Common.LOG, "Workout Couch ID in shared variable: " + SharedData.coachId);
            }


// set Check bos design
            // if (workOutUploads.size() != 0) {
            //for(position=0;i<workouts.getWorkout().size();i++){
            if (position == 0) {
                workoutVideoHolder.firstBar.setVisibility(View.INVISIBLE);
            } else if (position == workOutUploads.size() - 1) {
                workoutVideoHolder.lastBar.setVisibility(View.INVISIBLE);
            } else {
                workoutVideoHolder.firstBar.setVisibility(View.VISIBLE);
                workoutVideoHolder.lastBar.setVisibility(View.VISIBLE);
            }

            //}

            workoutVideoHolder.txtFileName.setText(uploadCurrent.getName());
            workoutVideoHolder.textVideoDuration.setText(uploadCurrent.getDuration());
            exercise = workoutVideoHolder.txtFileName.getText().toString();
            watchedStatus = uploadCurrent.getWatched();


            //TODO API Needs to be change
            /*checkForWatchedVideosCheckList();

            if(watchedVideosList!=null){
                if(watchedVideosList.contains(String.valueOf(uploadCurrent.getId()))){
                    workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(true);
                }
                else{
                    workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(false);
                }

            }*/

            if (watchedStatus) {
                workoutVideoHolder.firstBar.setBackgroundColor(context.getColor(R.color.app_logo));
                workoutVideoHolder.lastBar.setBackgroundColor(context.getColor(R.color.app_logo));
                workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(true);
                watched_count = watched_count + 1;
                workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(context.getDrawable(R.drawable.custom_radio_btn_timeline));
                //  SharedData.coachWatchCount++;
                workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#363C69"));
                workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#363C69"));
                next = true;

            } else {
                // Toast.makeText(context, String.valueOf(SharedData.coachWatchCount), Toast.LENGTH_SHORT).show();

                if (position != 0 && !next) {
                    count = 1;
                }

                workoutVideoHolder.mRadioBtnWatchedStatus.setChecked(false);
                if (count == 0) {
                    count++;
                    workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_radio_btn_timeline));
                    workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#363C69"));
                    workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#363C69"));
                } else {
                    workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_light_radio_btn_timeline));
                    workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#C5C5C5"));
                    workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#C5C5C5"));
                }


                if (item_position == position) {
                    Log.d(Common.LOG, "Item position: " + item_position);
                    workoutVideoHolder.firstBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                    workoutVideoHolder.lastBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                    if (position > 0) {
                        workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_light_radio_btn_timeline));
                    }
                    workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#363C69"));
                    workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#363C69"));
                }
                if (position != 0) {
                    if ((workOutUploads.get(position - 1).getWatched()) &&
                            (!workOutUploads.get(position).getWatched())) {
                        workoutVideoHolder.firstBar.setBackgroundColor(context.getColor(R.color.app_logo));
                        workoutVideoHolder.lastBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                        workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_radio_btn_timeline));
                        workoutVideoHolder.txtFileName.setTextColor(Color.parseColor("#363C69"));
                        workoutVideoHolder.textVideoDuration.setTextColor(Color.parseColor("#363C69"));
                    } else {

                        workoutVideoHolder.firstBar.setBackgroundColor(Color.parseColor("#C9C9C9"));
                        workoutVideoHolder.lastBar.setBackgroundColor(Color.parseColor("#C9C9C9"));

                    }
                }

                if (position == 0) {
                    workoutVideoHolder.mRadioBtnWatchedStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_radio_btn_timeline));
                }

            }

            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "watched count: " + watched_count);
            //System.out.println(watched_count +"watched count");

            //Picasso.get().load(uploadCurrent.getImgThumbnail()).into(WorkoutVideoHolder.imageThumbnail);
            if (isAdded && context != null) {
                Glide.with(context).load(/*Common.IMG_BASE_URL+*/uploadCurrent.getThumbnail())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException
                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                if (Common.isLoggingEnabled)
                                    e.printStackTrace();
                                return false;

                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                /*workoutVideoHolder.shimmerLayout.stopShimmerAnimation();
                                workoutVideoHolder.shimmerLayout.setVisibility(View.GONE);*/
                                //workoutVideoHolder.mWarmUpLinearLayout.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(workoutVideoHolder.imageThumbnail);
            }

            try {
                // bitmap = uploadCurrent.getImgThumbnail();
                //if (bitmap != null) {
                //WorkoutVideoHolder.imageThumbnail.setImageBitmap(bitmap);

                // }
            } catch (Throwable throwable) {
                FirebaseCrashlytics.getInstance().recordException(throwable);
                throwable.printStackTrace();
                System.out.println("Are you there in thumbnail");
            }


            workoutVideoHolder.mWarmUpLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (watched_count != 0) {
                        if (workOutUploads.get(position).getWatched()) {
                            startFragment(position);
                        } else if (position != 0) {
                            if ((workOutUploads.get(position - 1).getWatched()) &&
                                    (!workOutUploads.get(position).getWatched())) {
                                startFragment(position);
                            }
                        } else if ((position == 0) && (!workOutUploads.get(position).getWatched())) {
                            startFragment(position);
                        } else {
                            Toast.makeText(context, resources.getString(R.string.please_select_in_seq), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (position == 0) {
                            startFragment(position);
                        } else if (workOutUploads.get(position).getWatched()) {
                            startFragment(position);
                        }


                    }
                }


            });


        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
                if (Common.isLoggingEnabled)
                    Log.e(Common.LOG, "Workout Adapter Exception: " + ex.toString());
            }
        }
    }


    private void startFragment(int position) {
        if(SessionUtil.isSubscriptionAvailable(context)) {
            Fragment fragment = new ExerciseDetailsFragment();
            FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                    .getSupportFragmentManager().beginTransaction();
            Bundle param = new Bundle();
            param.putInt("workoutID", workOutUploads.get(position).getId());
            firebaseAnalytics.logEvent("watchVideo", param);
            firebaseAnalytics.setDefaultEventParameters(param);
            Bundle bundle = new Bundle();
            bundle.putInt("index", position);
            bundle.putSerializable("exerciseList", (Serializable) workOutUploads);
            bundle.putInt("size", listSize);
            bundle.putInt("count", watched_count);
            bundle.putBoolean("watched_status", workOutUploads.get(position).getWatched());
            bundle.putInt("workOutId", workOutUploads.get(position).getId());
            bundle.putInt("user_id", currentUserId);
            bundle.putInt("dayNumber", dayNumber);
            bundle.putInt("weekNumber", weekNumber);
            bundle.putInt("coachId", coach_id);

            bundle.putString("exercise", workOutUploads.get(position).getName()); //key and value
            fragment.setArguments(bundle);
            mFragmentTransaction.replace(R.id.navigation_container, fragment);
            mFragmentTransaction.addToBackStack("CoachesExercisesFragment");
            mFragmentTransaction.commit();
        }else{
            DialogUtil.showSubscriptionEndDialogBox(context,context.getResources());
        }

    }

    private void checkForWatchedVideosCheckList() {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

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
            watchedVideosList = gson.fromJson(json, type);

            // checking below if the array list is empty or not
            if (Common.isLoggingEnabled) {
                if (watchedVideosList != null) {
                    Log.d("mylist1", watchedVideosList.toString());
                }
            }
        }


    }


    @Override
    public int getItemCount() {
        if (workOutUploads != null) {
            // if (workOutUploads.!= null) {
            if (workOutUploads.size() != 0) {
                listSize = workOutUploads.size();
                System.out.println(listSize + "list size ye hy   ////////////////////////////////     ");
                return listSize;

            }

            // }

        }
        return 0;
    }

}

class WorkoutVideoHolder extends RecyclerView.ViewHolder {

    MaterialTextView txtFileName;
    ImageView imageThumbnail;
    MaterialTextView textVideoDuration;
    ConstraintLayout mWarmUpLinearLayout;
    //ShimmerFrameLayout shimmerLayout;
    RadioButton mRadioBtnWatchedStatus;
    View lastBar, firstBar;


    WorkoutVideoHolder(View view) {
        super(view);

        txtFileName = view.findViewById(R.id.textViewWorkout);
        textVideoDuration = view.findViewById(R.id.textViewWorkoutTime);
        imageThumbnail = view.findViewById(R.id.imageWorkout);
        mWarmUpLinearLayout = view.findViewById(R.id.workoutLL);
        //shimmerLayout = view.findViewById(R.id.shimmerLayout);
        mRadioBtnWatchedStatus = view.findViewById(R.id.radioButtonWatchedStatus);
        lastBar = view.findViewById(R.id.lastLine);
        firstBar = view.findViewById(R.id.firstLine);

    }

}