package com.cedricapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.AddCardsItemClickListener;
import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class AddedCardsAdapter extends RecyclerView.Adapter<AddedCardsAdapter.ViewHolder> {


    private Context context;
    private ArrayList<String> dataList;
    private AddCardsItemClickListener addCardsItemClickListener;
    private int selectedItem = -1; // Initially, no item is selected


    public AddedCardsAdapter(Context context, ArrayList<String> dataList, AddCardsItemClickListener addCardsItemClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.addCardsItemClickListener = addCardsItemClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_cards_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.mCardName.setText(data);




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView mCardName;
        private ConstraintLayout mCardsConstraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardName = itemView.findViewById(R.id.cardName);
            mCardsConstraintLayout = itemView.findViewById(R.id.mCardsConstraintLayout);

          // Set a click listener on the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Call onItemClick on the listener when the item is clicked
                    int position = getAbsoluteAdapterPosition();
                    selectedItem = position;
                    if (position != RecyclerView.NO_POSITION) {
                        SharedData.cardPosition=position;
                        addCardsItemClickListener.onItemClick(view,position);
                    }
                }
            });


        }
    }
}



