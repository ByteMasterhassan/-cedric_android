package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.cedricapp.model.FiltersWorkoutModel;
import com.cedricapp.model.ShoppingDetailsModel;
import com.cedricapp.R;

import java.util.ArrayList;
import java.util.List;

public class FiltersWorkoutListAdapter extends RecyclerView.Adapter<FiltersWorkoutListAdapter.ShoppingDetailViewHolder> {

    private final List<FiltersWorkoutModel> dataArrayList;
    ArrayList<ShoppingDetailsModel> checkedItems = new ArrayList<ShoppingDetailsModel>();
    Context context;
    int categoryId;
    private int getCatId;

    public FiltersWorkoutListAdapter(List<FiltersWorkoutModel> dataArrayList, Context context) {
        this.dataArrayList = dataArrayList;
        this.context = context;
    }

    @Override
    public ShoppingDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filters_checkbox_layout, parent, false);

        FiltersWorkoutListAdapter.ShoppingDetailViewHolder myViewHolder = new FiltersWorkoutListAdapter.ShoppingDetailViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingDetailViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final FiltersWorkoutModel myListData = dataArrayList.get(position);

        holder.checkBoxList.setText(myListData.getWorkoutsName());


    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ShoppingDetailViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox checkBoxList;

        public ShoppingDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            this.checkBoxList = (MaterialCheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}
