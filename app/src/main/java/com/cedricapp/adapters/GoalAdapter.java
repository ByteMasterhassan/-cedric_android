package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.interfaces.GoalClickListener;
import com.cedricapp.model.GoalModel;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalAdapterViewHolder> {
    ArrayList<GoalModel.Datum> goals;
    Context context;
    GoalClickListener goalClickListener;
    int index;

    public GoalAdapter(Context context, ArrayList<GoalModel.Datum> goals, GoalClickListener goalClickListener) {
        this.context = context;
        this.goals = goals;
        this.goalClickListener = goalClickListener;
        index = -1;
    }

    @NonNull
    @Override
    public GoalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalAdapterViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull GoalAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GoalModel.Datum goalData = goals.get(position);
        String savedLanguage = SessionUtil.getlangCode(context);
        Locale current = context.getResources().getConfiguration().locale;
        String language = current.getLanguage();
        if (goalData != null) {
            if (goalData.getStripeProduct() != null && goalData.getStripeProduct().getName() != null &&
                    goalData.getStripeProduct().getNameSV() != null) {
                if (savedLanguage.matches("")) {
                    if (language.matches("sv")) {
                        holder.productNameTV.setText(goalData.getStripeProduct().getNameSV());
                    } else {
                        holder.productNameTV.setText(goalData.getStripeProduct().getName());
                    }
                } else {
                    if (savedLanguage.matches("sv")) {
                        holder.productNameTV.setText(goalData.getStripeProduct().getNameSV());
                    } else {
                        holder.productNameTV.setText(goalData.getStripeProduct().getName());
                    }
                }
            }

            if (goalData != null && goalData.getName() != null && goalData.getNameSv() != null) {
                if (savedLanguage.matches("")) {
                    if (language.matches("sv")) {
                        holder.productNameBriefTV.setText(goalData.getNameSv());
                    } else {
                        holder.productNameBriefTV.setText(goalData.getName());
                    }
                }else{
                    if (savedLanguage.matches("sv")) {
                        holder.productNameBriefTV.setText(goalData.getNameSv());
                    } else {
                        holder.productNameBriefTV.setText(goalData.getName());
                    }
                }
            }

        }
        holder.productNameBriefTV.setOnClickListener(view -> {
            index = position;
            goalClickListener.goalItemOnClickListener(goalData);
            /*setActiveSelection(holder, position, index);*/
            notifyDataSetChanged();
        });
        setActiveSelection(holder, position, index);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class GoalAdapterViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView productNameTV;
        MaterialTextView productNameBriefTV;


        public GoalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.productNameTV = itemView.findViewById(R.id.productNameTV);
            this.productNameBriefTV = itemView.findViewById(R.id.productNameBriefTV);
        }
    }

    void setActiveSelection(GoalAdapterViewHolder holder, int position, int i) {
        if (position == i) {
            holder.productNameBriefTV.setBackgroundResource(R.drawable.textview_after_click);
            holder.productNameBriefTV.setTextColor(Color.BLACK);
        } else {
            holder.productNameBriefTV.setBackgroundResource(R.drawable.textview_outline_style);
            holder.productNameBriefTV.setTextColor(Color.WHITE);
        }

    }
}
