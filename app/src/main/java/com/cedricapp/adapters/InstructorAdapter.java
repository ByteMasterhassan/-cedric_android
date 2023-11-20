package com.cedricapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.cedricapp.fragment.DashboardFragment;
import com.cedricapp.model.InstructorDataModel;
import com.cedricapp.R;

import java.util.List;

@SuppressWarnings("deprecation")
public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.MyViewHolder> {
    private final List<InstructorDataModel> dataArrayList;
    private final Context context;
    String mCoachName;

    private Bitmap bitmap;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView name, noOfVideos;
        ImageView imageViewIcon, cameraIcon;
        ConstraintLayout coaches_conslay;
        ShimmerFrameLayout coaches_shimmer;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.textViewInstructorName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.InstructorImage);
            this.noOfVideos = itemView.findViewById(R.id.textViewNoOfVideos);
            this.cameraIcon = (ImageView) itemView.findViewById(R.id.cameraIcon);

            this.coaches_conslay = (ConstraintLayout) itemView.findViewById(R.id.coaches_conslay);
            this.coaches_shimmer = (ShimmerFrameLayout) itemView.findViewById(R.id.coaches_adapter_shimmer);


        }
    }

    public InstructorAdapter(Context context, List<InstructorDataModel> uploads) {
        this.context = context;
        dataArrayList = uploads;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_instructors_layout, parent, false);

        view.setOnClickListener(DashboardFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        InstructorDataModel uploadCurrent = dataArrayList.get(listPosition);

        //add to local db;


        // CoachesDataModel data = (CoachesDataModel) dbHelper.getAllCoaches();


        MaterialTextView textViewName = (MaterialTextView) holder.name;
        String instructorName = uploadCurrent.getInstructorName();
        String instructorImage = String.valueOf(uploadCurrent.getInstructorImage());

        holder.noOfVideos.setText(uploadCurrent.getNoOfVideos());
        Glide.with(context).load(uploadCurrent.getInstructorImage()).into(holder.imageViewIcon);
        Glide.with(context).load(uploadCurrent.getCameraIcon()).into(holder.cameraIcon);

        //holder.name.setText(uploadCurrent.name);
        textViewName.setText(instructorName);


       /* Glide.with(context).load(instructorImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException
                                                        e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                     *//*   holder.coaches_shimmer.stopShimmerAnimation();
                        holder.coaches_shimmer.setVisibility(View.INVISIBLE);
                        holder.coaches_conslay.setVisibility(View.VISIBLE);*//*
                        return false;
                    }
                })
                .into(holder.imageViewIcon);*/




     /*   holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoachName = holder.name.getText().toString();
                //  SharedData.coachId=uploadCurrent.coachNumber;

                System.out.println(SharedData.coachId + "id");

                Fragment fragment = new CoachesExercisesFragment();
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable("coach", uploadCurrent);
                bundle.putString("profileImage", String.valueOf(uploadCurrent.imageURL));
                bundle.putString("CoachName", uploadCurrent.name);
                bundle.putString("description", uploadCurrent.description);
                bundle.putString("role", uploadCurrent.role);//key and value
                fragment.setArguments(bundle);
                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();


            }
        });*/


    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }
}
