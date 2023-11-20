package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.interfaces.FoodPreferenceClickListener;
import com.cedricapp.model.FoodPreferencesModel;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class FoodPreferenceAdapter extends RecyclerView.Adapter<FoodPreferenceAdapter.MyViewHolder> {

    ArrayList<FoodPreferencesModel.Datum> foodPreferences;
    Context context;
    final static String TAG = "Number Exception";
    int index;
    FoodPreferenceClickListener foodPreferenceClickListener;
    int count;
    String comingFrom = "";

    public FoodPreferenceAdapter(Context context, ArrayList<FoodPreferencesModel.Datum> foodPreferences, FoodPreferenceClickListener foodPreferenceClickListener, String comingFrom) {
        this.context = context;
        this.foodPreferences = foodPreferences;
        this.foodPreferenceClickListener = foodPreferenceClickListener;
        index = -1;
        count = 0;
        this.comingFrom = comingFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_preference, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodPreferencesModel.Datum foodPreference = foodPreferences.get(position);
        if (foodPreference.getTitle() != null) {
            holder.fpNameTV.setText(foodPreference.getTitle());
            if (foodPreference.getTitle().matches("Regular")
            || foodPreference.getTitle().matches("Regelbunden")) {
                holder.fpImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cloud));

            } else if (foodPreference.getTitle().matches("Pescatarian")) {
                holder.fpImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fish));
                //holder.fpNameBriefTV.setText(R.string.plus_seafood);
            } else if (foodPreference.getTitle().matches("Vegetarian")) {
                holder.fpImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_carrot));
                //holder.fpNameBriefTV.setText(R.string.hold_the_meat);
            } else if (foodPreference.getTitle().matches("Vegan")
            || foodPreference.getTitle().matches("Vegansk")) {
                holder.fpImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
                //holder.fpNameBriefTV.setText(R.string.animal_product);
            }

        }
        if(foodPreference.getTagline()!=null){
            holder.fpNameBriefTV.setText(foodPreference.getTagline());
        }

        holder.cardMainLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                index = position;
                setActiveSelection(holder, position, index);
                foodPreferenceClickListener.fPClickListener(foodPreference);
                notifyDataSetChanged();
            }
        });
        setActiveSelection(holder, position, index);
    }

    @Override
    public int getItemCount() {
        return foodPreferences.size();
    }

    void setActiveSelection(FoodPreferenceAdapter.MyViewHolder holder, int position, int i) {
        if (position == i) {
            holder.cardMainLayout.setBackgroundResource(R.drawable.textview_after_click);
            holder.fpNameTV.setTextColor(Color.BLACK);
            holder.fpNameBriefTV.setTextColor(Color.BLACK);
        } else {
            holder.cardMainLayout.setBackgroundResource(R.drawable.textview_outline_style);
            holder.fpNameTV.setTextColor(Color.WHITE);
            holder.fpNameBriefTV.setTextColor(Color.WHITE);
        }
        try {
            if (comingFrom.matches("From_setting") && count == 0
                    && foodPreferences.get(position).getId() == Integer.parseInt(SessionUtil.getFoodPreferenceID(context))) {
                holder.cardMainLayout.setBackgroundResource(R.drawable.textview_after_click);
                holder.fpNameTV.setTextColor(Color.BLACK);
                holder.fpNameBriefTV.setTextColor(Color.BLACK);
                count++;
            }
        } catch (NumberFormatException numberFormatException) {
            Log.i(TAG, numberFormatException.getMessage());
        }
        /*else if (count ==0 && foodPreferences.get(position).getId()==Integer.parseInt(SessionUtil.getFoodPreferenceID(context))){
            holder.cardMainLayout.setBackgroundResource(R.drawable.textview_after_click);
            holder.fpNameTV.setTextColor(Color.BLACK);
            holder.fpNameBriefTV.setTextColor(Color.BLACK);
            count++;
        }*/

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView fpNameTV, fpNameBriefTV;
        LinearLayout cardMainLayout;
        ImageView fpImgView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fpImgView = itemView.findViewById(R.id.fpImg);
            fpNameTV = itemView.findViewById(R.id.fpNameTV);
            fpNameBriefTV = itemView.findViewById(R.id.fpNameBriefTV);
            cardMainLayout = itemView.findViewById(R.id.cardMainLayout);

        }
    }
}
