package com.cedricapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {
    private Context context;
    String exercise, exerciseVideo, exerciseDescription;

    boolean isAdded;
    private ShimmerViewHolder viewHolder;
    boolean startShimmer;
    ArrayList<Boolean> shimmerList;

    public ShimmerAdapter(Context mContext, boolean added, ArrayList<Boolean> startShimmer) {
        this.context = mContext;
        this.isAdded = added;
        this.shimmerList = startShimmer;

    }

    /*public ShimmerAdapter(boolean dataAvailable) {
        this.dataAvailable=dataAvailable;
    }*/

    @NonNull
    @Override
    public ShimmerAdapter.ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_workout_exercises_recyclerview, parent, false);
        return new ShimmerAdapter.ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerAdapter.ShimmerViewHolder holder, int position) {
        viewHolder = holder;
        if (shimmerList.get(position))
            holder.shimmerFrameLayout.startShimmerAnimation();
        else
            holder.shimmerFrameLayout.stopShimmerAnimation();

    }

    @Override
    public int getItemCount() {
        return shimmerList.size();
    }

    public class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }


}
