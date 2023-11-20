package com.cedricapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.model.ExploreWorkoutDataModel;
import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ExploreWorkoutAdapter extends RecyclerView.Adapter<ExploreWorkoutAdapter.MyViewHolder> {
    private static ArrayList<ExploreWorkoutDataModel> dataSet;
   // private Fragment context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView mainWorkoutName;
        MaterialTextView numberOfWorkouts;
        MaterialTextView workout1Name;
        MaterialTextView workout2Name;
        MaterialTextView workout3Name;
        ImageView mainWorkoutImage;
        ImageView workout1Image;
        ImageView workout2Image;
        ImageView workout3Image;



        public MyViewHolder(View itemView) {
            super(itemView);

            this.mainWorkoutName = (MaterialTextView) itemView.findViewById(R.id.textViewMainWorkOutName);
            this.numberOfWorkouts = (MaterialTextView) itemView.findViewById(R.id.textViewNumberOfWorkouts);
//            this.workout1Name = (MaterialTextView) itemView.findViewById(R.id.textViewWorkout1Name);
//            this.workout2Name = (MaterialTextView) itemView.findViewById(R.id.textViewWorkout2Name);
//            this.workout3Name = (MaterialTextView) itemView.findViewById(R.id.textViewWorkout3Name);
//            this.mainWorkoutImage = (ImageView) itemView.findViewById(R.id.imageMainWorkout);
//            this.workout1Image = (ImageView) itemView.findViewById(R.id.imageWorkout1);
//            this.workout2Image = (ImageView) itemView.findViewById(R.id.imageWorkout2);
//            this.workout3Image = (ImageView) itemView.findViewById(R.id.imageWorkout3);


//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Fragment fragment = new ProgressFragment();
//                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//
//                    fragmentTransaction.replace(R.id.progress_fragment, fragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//
//
//
//
//
//                }
//            });


        }
    }
    public ExploreWorkoutAdapter(ArrayList<ExploreWorkoutDataModel> data) {
        this.dataSet = data;
    }

    @Override
    public ExploreWorkoutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_cardview, parent, false);

       // view.setOnClickListener(ProgramsFragment.myOnClickListener);

        ExploreWorkoutAdapter.MyViewHolder myViewHolder = new ExploreWorkoutAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ExploreWorkoutAdapter.MyViewHolder holder, final int listPosition) {

        MaterialTextView textViewMainWorkoutName = holder.mainWorkoutName;
        MaterialTextView textViewNumberOfWorkouts = holder.numberOfWorkouts;
        MaterialTextView textViewWorkout1 = holder.workout1Name;
        MaterialTextView textViewWorkout2 = holder.workout2Name;
        MaterialTextView textViewWorkout3 = holder.workout3Name;
        ImageView imageViewMainWorkout = holder.mainWorkoutImage;
        ImageView imageViewWorkout1 = holder.workout1Image;
        ImageView imageViewWorkout2 = holder.workout2Image;
        ImageView imageViewWorkout3 = holder.workout3Image;



        textViewMainWorkoutName.setText(dataSet.get(listPosition).getWorkoutMainName());
        textViewNumberOfWorkouts.setText(dataSet.get(listPosition).getNumberOfWorkout());
//        textViewWorkout1.setText(dataSet.get(listPosition).getWorkout1Name());
//        textViewWorkout2.setText(dataSet.get(listPosition).getWorkout2Name());
//        textViewWorkout3.setText(dataSet.get(listPosition).getWorkout3Name());
//        imageViewMainWorkout.setImageResource(dataSet.get(listPosition).getMainWorkoutImage());
//        imageViewWorkout1.setImageResource(dataSet.get(listPosition).getWorkout1Image());
//        imageViewWorkout2.setImageResource(dataSet.get(listPosition).getWorkout2Image());
//        imageViewWorkout3.setImageResource(dataSet.get(listPosition).getWorkout3Image());



    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
