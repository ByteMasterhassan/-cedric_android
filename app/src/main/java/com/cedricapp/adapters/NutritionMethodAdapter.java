package com.cedricapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.cedricapp.R;

import java.util.ArrayList;

public class NutritionMethodAdapter extends RecyclerView.Adapter<NutritionMethodAdapter.MyViewHolder> {
    private ArrayList<String> methodArrayList;
    private Context context;
    int index = -1;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView stepNumber;
        MaterialTextView stepDetails;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.stepNumber = (MaterialTextView) itemView.findViewById(R.id.textViewSerialNo);
            this.stepDetails = (MaterialTextView) itemView.findViewById(R.id.textViewSteps);


        }
    }

    public NutritionMethodAdapter(Context context, ArrayList<String> uploads) {
        this.context = context;
        methodArrayList = uploads;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrition_method, parent, false);

        // view.setOnClickListener(DashboardFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int listPosition) {
        ArrayList<String> uploadCurrent = methodArrayList;
        int position = listPosition + 1;
        String serialNo = String.valueOf(position);

        System.out.println(uploadCurrent + "+++++llllllllllllllllllllllllllllllllllll");

        //holder.stepNumber.setText(listPosition);
        holder.stepDetails.setText(uploadCurrent.get(listPosition));
        holder.stepNumber.setText(serialNo);

    }

    @Override
    public int getItemCount() {

        if (methodArrayList.size() != 0) {
            return methodArrayList.size();
        } else {
            return 0;
        }

    }
}
