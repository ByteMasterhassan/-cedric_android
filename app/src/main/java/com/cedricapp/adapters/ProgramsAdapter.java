package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cedricapp.common.Common;
import com.cedricapp.model.ProgramsDataModel;
import com.cedricapp.fragment.ProgressFragment;
import com.cedricapp.R;

import java.util.List;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.MyViewHolder> {
    private List<ProgramsDataModel.Datum> programsDataModel;
    private final Context context;
    int height;
    int width;
    float density;



    public ProgramsAdapter(Context context, List<ProgramsDataModel.Datum> programsDataModel) {
        this.context = context;
        this.programsDataModel = programsDataModel;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        density = displayMetrics.density;
        if(Common.isLoggingEnabled){
            Log.d(Common.LOG,"Screen width: "+width+", height is "+height+", density is "+density+"\n width in dp: "+(width/density)+", height in dp"+(height/density));
        }
    }

    @NonNull
    @Override
    public ProgramsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.programs_cardview, parent, false);

        // view.setOnClickListener(ProgramsFragment.myOnClickListener);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        holder.programShimmer.startShimmerAnimation();

        //ProgramsDataModel uploadCurrent = programsList.get(listPosition);
        //System.out.println(uploadCurrent + "programs Data");
        if(programsDataModel.get(listPosition).getName()!=null && programsDataModel.get(listPosition).getName().length()>1) {
            holder.programName.setText(programsDataModel.get(listPosition).getName().substring(0, 1).toUpperCase()+""+programsDataModel.get(listPosition).getName().toString().substring(1).toLowerCase());
        }

        if(width>1599){
            holder.programImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        //holder.programName.setText(programsDataModel.get(listPosition).getName());
        //System.out.println(uploadCurrent.name + "program name");
        holder.plan.setText(R.string.day_plans );
        // Glide.with(context).asBitmap().load(uploadCurrent.getThumbnail()).into(holder.programImage);

        if (context != null) {
            Glide.with(context).load(/*Common.IMG_BASE_URL+*/programsDataModel.get(listPosition).getThumbnail())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException
                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, e.toString());
                                e.printStackTrace();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.programShimmer.stopShimmerAnimation();
                            holder.programShimmer.setVisibility(View.INVISIBLE);
                            holder.programsConstraintLLayout.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.programImage);
        }


        holder.programImage.setOnClickListener(v -> {
            //ProgramName = holder.programName.getText().toString();
            //SharedData.bestProgramId = uploadCurrent.getProgramId();
            //System.out.println(SharedData.bestProgramId + "pId");
            //SharedData.programName = uploadCurrent.getName();

            Fragment fragment = new ProgressFragment();
            if (context != null) {
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("ProgramName", programsDataModel.get(listPosition).getName()); //key and value
                bundle.putInt("ProgramId", (programsDataModel.get(listPosition).getId()));
                bundle.putString("noOfWeeks", programsDataModel.get(listPosition).getWeeks());
                bundle.putString("description", programsDataModel.get(listPosition).getDescription());
                //bundle.putString("id",post_key);
                fragment.setArguments(bundle);
                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            }


        });
    }

    @Override
    public int getItemCount() {
        return programsDataModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView programName;
        ImageView programImage;
        MaterialTextView plan;
        ConstraintLayout programsConstraintLLayout;
        ShimmerFrameLayout programShimmer;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.programName = itemView.findViewById(R.id.textViewProgramName);
            this.programImage = itemView.findViewById(R.id.bodyProgramImg);
            this.plan = itemView.findViewById(R.id.textViewProgramPlan);
            this.programShimmer = itemView.findViewById(R.id.programs_shimmer);
            this.programsConstraintLLayout = itemView.findViewById(R.id.programs_layout);


        }
    }
}
