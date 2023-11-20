package com.cedricapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class PlanItemsDetailsAdapter extends RecyclerView.Adapter<PlanItemsDetailsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> dataList;

    public PlanItemsDetailsAdapter(Context context, ArrayList<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.planItemDescription.setText(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       MaterialTextView planItemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            planItemDescription=itemView.findViewById(R.id.planDetailTextView);

        }
    }
}

