package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.fragment.CoachesWorkoutsFragment;
import com.cedricapp.common.Common;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.cedricapp.fragment.DashboardFragment;

import com.cedricapp.model.CoachesProfileDataModel;
import com.cedricapp.R;
import com.cedricapp.common.SharedData;

import java.util.List;

public class CoachesAdapter extends RecyclerView.Adapter<CoachesAdapter.MyViewHolder> {
    private final List<CoachesProfileDataModel.Data> dataArrayList;
    private final Context context;
    private final int goal_id, level_id, userId;
    String mCoachName;
    int day, week;
    int statusCode;
    boolean isAdded;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageViewIcon;
        ConstraintLayout coaches_conslay;
        ShimmerFrameLayout coaches_shimmer;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.coachesName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.profileImage);
            this.coaches_conslay = (ConstraintLayout) itemView.findViewById(R.id.coaches_conslay);
            this.coaches_shimmer = (ShimmerFrameLayout) itemView.findViewById(R.id.coaches_adapter_shimmer);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String coachName = name.getText().toString();
//                    Intent intent = new Intent(v.getContext(), CoachesExercises.class);
//                    //Bundle bundle=new Bundle();
//                    // intent.putExtra("Image",  dataSet.get(getAdapterPosition()).getProfile_img());
//                    //intent.putExtras(bundle);
//
//                    //intent.putExtra("Name",coachName);
//                    v.getContext().startActivity(intent);
//
//
//                }
//            });
        }
    }

    public CoachesAdapter(Context context, List<CoachesProfileDataModel.Data> uploads, int day, int week,
                          int userId, int level_id, int goal_id, int statusCode, boolean isAdded) {
        this.context = context;
        dataArrayList = uploads;
        this.day = day;
        this.week = week;
        this.level_id = level_id;
        this.goal_id = goal_id;
        this.userId = userId;
        this.statusCode = statusCode;
        if(Common.isLoggingEnabled){
            Log.d(Common.LOG,"Status Code: "+this.statusCode);
        }
        this.isAdded = isAdded;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_coaches, parent, false);

        view.setOnClickListener(DashboardFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        CoachesProfileDataModel.Data uploadCurrent = dataArrayList.get(listPosition);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        //add to local db;
        holder.coaches_shimmer.startShimmerAnimation();
        holder.coaches_shimmer.setVisibility(View.VISIBLE);
        holder.coaches_conslay.setVisibility(View.GONE);
        holder.imageViewIcon.setVisibility(View.GONE);


        // CoachesDataModel data = (CoachesDataModel) dbHelper.getAllCoaches();


        MaterialTextView textViewName = (MaterialTextView) holder.name;
        SharedData.coachName = uploadCurrent.getName();
        SharedData.coachId = uploadCurrent.getId();
        System.out.println(SharedData.coachName + "jjjjjjjjjjjjj");

        SharedData.coachImage = uploadCurrent.getImageURL();
        System.out.println(SharedData.coachImage + "jjjjjjjjjjjjj");
        textViewName.setText(SharedData.coachName);


        //holder.name.setText(uploadCurrent.name);

        if (isAdded && context != null) {
            Glide.with(context).load(/*Common.IMG_BASE_URL +*/ uploadCurrent.getImageURL())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException
                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.coaches_shimmer.stopShimmerAnimation();
                            holder.coaches_shimmer.setVisibility(View.GONE);
                            holder.coaches_conslay.setVisibility(View.VISIBLE);
                            holder.imageViewIcon.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.imageViewIcon);
        }

        // Picasso.get().load(uploadCurrent.getImgurl()).into(holder.imageViewIcon);
//        Bitmap bitmapThumbnail = ThumbnailUtils.createImageThumbnail(uploadCurrent.getImgurl(),
//                MediaStore.Images.Thumbnails.MINI_KIND);
//        holder.imageViewIcon.setImageBitmap(bitmapThumbnail);

        //if (statusCode == 200) {
        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoachName = holder.name.getText().toString();
                if (isAdded && context != null) {
                    System.out.println(day + "day" + week + "week");
                    Bundle coachparam = new Bundle();
                    coachparam.putString("coach_name", uploadCurrent.getName());
                    mFirebaseAnalytics.logEvent("Coaches", coachparam);
                    mFirebaseAnalytics.setUserProperty("Gender", SessionUtil.getUserGender(context));
                    Fragment fragment = new CoachesWorkoutsFragment();
                    @SuppressLint("CommitTransaction") FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                            .getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    //   bundle.putParcelable("coach", uploadCurrent);
                    bundle.putString("profileImage", /*Common.IMG_BASE_URL +*/ uploadCurrent.getImageURL());
                    //bundle.putString("profileImage", String.valueOf(uploadCurrent.imageURL));
                    bundle.putString(Common.COACH_NAME, uploadCurrent.getName());
                    bundle.putInt(Common.COACH_ID, uploadCurrent.getId());
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    bundle.putInt(Common.SESSION_USER_LEVEL_ID, level_id);
                    bundle.putInt(Common.SESSION_USER_GOAL_ID, goal_id);
                    bundle.putInt(Common.SESSION_USER_ID, userId);
                    bundle.putInt("workout_count", dataArrayList.get(listPosition).getWorkoutCount());
                    bundle.putInt("offset_limit", dataArrayList.get(listPosition).getLimit());
                    bundle.putString("description", uploadCurrent.getDescription());
                    bundle.putString("role", uploadCurrent.getRole());//key and value
                    fragment.setArguments(bundle);


                    String backStateName = fragment.getClass().getName();

                    FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                    boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { //fragment not in back stack, create it.
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.navigation_container, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                        /*} else {
                            if (isAdded && context != null)
                                Toast.makeText(context, "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }*/
                }
            }
        });
        /*} else if (statusCode == 404) {
            if (context != null) {
                holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAdded && context != null)
                            Toast.makeText(context, "No data available against this!", Toast.LENGTH_SHORT).show();
                    }
                });

            }


        }else{
            if(Common.isLoggingEnabled){
                Log.e(Common.LOG,"Status code is "+statusCode);
            }
        }*/


    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }
}
