package com.cedricapp.adapters;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
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
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.R;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class BestProgramAdapter extends RecyclerView.Adapter<BestProgramAdapter.MyViewHolder> {
    String exercise, videoUrl;
    private final Context context;
    private final ProgressDataModel.Data.Workouts workouts;
    private ImageView imageThumbnail;
    private Bitmap bitmap;
    private String exerciseVideo, exerciseDescription;
    private int listSize;


    public BestProgramAdapter(Context context, ProgressDataModel.Data.Workouts workouts) {
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public BestProgramAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.best_program_warmup, parent, false);

        //view.setOnClickListener(DashboardFragment.myOnClickListener);

        BestProgramAdapter.MyViewHolder myViewHolder = new BestProgramAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final BestProgramAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        // BestProgramModel uploadCurrent = mUploads;
        holder.shimmerFrameLayout.startShimmerAnimation();

        //ArrayList<BestProgramModel.Warmup> bestWarmupList= (ArrayList<BestProgramModel.Warmup>) uploadCurrent.getWarmup();
        int index = listPosition;

        holder.txtFileName.setText(/*uploadCurrent.warmup.get(listPosition).name*/workouts.getWorkout().get(listPosition).getName());
        holder.textVideoDuration.setText(/*uploadCurrent.warmup.get(listPosition).duration*/"");
        //videoUrl=uploadCurrent.getUrl();
        /*Glide.with(context).load(uploadCurrent.warmup.get(listPosition).thumbnail)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.try_later)
                .into(holder.imageThumbnail);*/
        //Picasso.get().load(uploadCurrent.getImgThumbnail()).into(videoHolder.imageThumbnail);
        try {
            Glide.with(context).load(/*Common.IMG_BASE_URL+*/workouts.getWorkout().get(listPosition).getThumbnail())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException
                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            holder.mBestWarmUpLinearLayout.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.imageThumbnail);
        } catch (Throwable throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable);
            if (context != null) {
                new LogsHandlersUtils(context).getLogsDetails("BestProgramAdapter_imageLoadingException",
                        SessionUtil.getUserEmailFromSession(context), EXCEPTION, SharedData.throwableObject(throwable));
            }

            throwable.printStackTrace();
            if (Common.isLoggingEnabled) {
                System.out.println("Are you there in thumbnail");
            }
        }


        holder.mBestWarmUpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Fragment fragment = new BestExerciseDetailsFragment();
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("size", listSize);
                //bundle.putSerializable("exerciseList",  bestWarmupList);
                bundle.putInt("index", index);
                //bundle.putString("position", uploadCurrent.warmup.get(listPosition).videoUrl);
                // bundle.putString("videoDescription", uploadCurrent.warmup.get(listPosition).description);
                //bundle.putString("exercise",uploadCurrent.warmup.get(listPosition).name);
                //Log.d("msg",uploadCurrent.warmup.get(listPosition).duration);//key and value

                fragment.setArguments(bundle);
                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        // return mUploads.warmup.size();
        return workouts.getWorkout().size();

       /* if (mUploads.warmup.size() != 0) {
            listSize = mUploads.warmup.size();
            return listSize;

        } else {
            return 0;
        }*/
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView txtFileName;
        ImageView imageThumbnail;
        MaterialTextView textVideoDuration;
        LinearLayout mBestWarmUpLinearLayout;

        ShimmerFrameLayout shimmerFrameLayout;


        public MyViewHolder(View itemView) {
            super(itemView);

            txtFileName = itemView.findViewById(R.id.textViewBestWarmUP);
            textVideoDuration = itemView.findViewById(R.id.textViewBestWarmUpTime);
            imageThumbnail = itemView.findViewById(R.id.imageBestWarmUp);
            mBestWarmUpLinearLayout = itemView.findViewById(R.id.warmUpBestLinearLayout);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayoutBestWarmUp);

        }
    }
}

