package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.AllergyClickListener;
import com.cedricapp.model.AllergyModel;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;

import java.util.ArrayList;

public class AllergyAdapter extends RecyclerView.Adapter<AllergyAdapter.MyViewHolder> {
    Context context;
    ArrayList<AllergyModel.Datum> allergies;
    AllergyClickListener allergyClickListener;
    public int i;

    public AllergyAdapter(Context context, ArrayList<AllergyModel.Datum> allergies, AllergyClickListener allergyClickListener) {
        this.context = context;
        this.allergies = allergies;
        this.allergyClickListener = allergyClickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AllergyModel.Datum allergy = allergies.get(position);
        String ischeck = SessionUtil.getAllergiesId(context);
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Allegies IDs: " + ischeck);
        }
        i = 0;
        SessionUtil.setIntegerI(context, i);
        //if (/*allergies.get(position).isChecked()*/ ischeck.contains(String.valueOf(allergies.get(position).getId()))) {
           /* holder.allergyCheckBox.setChecked(true);
        }else{
            holder.allergyCheckBox.setChecked(false);
        }*/

        //if (AllergiesActivity.comeFrom != null && AllergiesActivity.comeFrom.matches("")) {
        if (allergies.get(position).isChecked()) {
            holder.allergyCheckBox.setChecked(true);
        } else {
            holder.allergyCheckBox.setChecked(false);
        }
        //}

        if (allergy != null) {
            if (allergy.getName() != null) {
                holder.allergyCheckBox.setText(allergy.getName());

            }
        }

        holder.allergyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    if (allergy.getId() != 0 && allergy.getName() != null) {
                        allergies.get(position).setChecked(true);
                        allergy.setChecked(true);
                        allergyClickListener.allergyClickListener(allergy.getId(), allergy.getName(), true);
                    }
                } else {
                    if (allergy.getId() != 0) {
                        allergies.get(position).setChecked(false);
                        allergy.setChecked(false);
                        allergyClickListener.allergyClickListener(allergy.getId(), allergy.getName(), false);
                    }
                }
            }
        });
        /*holder.allergyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                i = 1;
                SessionUtil.setIntegerI(context, i);
                if (b) {
                    if (allergy.getId() != 0 && allergy.getName() != null) {
                        allergies.get(position).setChecked(b);
                        allergy.setChecked(b);
                        allergyClickListener.allergyClickListener(allergy.getId(), allergy.getName(), b);
                    }
                } else {
                    if (allergy.getId() != 0) {
                        allergies.get(position).setChecked(b);
                        allergy.setChecked(b);
                        allergyClickListener.allergyClickListener(allergy.getId(), allergy.getName(), b);
                    }
                }
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return allergies.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox allergyCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            allergyCheckBox = itemView.findViewById(R.id.allergyCheckBox);
        }
    }
}
