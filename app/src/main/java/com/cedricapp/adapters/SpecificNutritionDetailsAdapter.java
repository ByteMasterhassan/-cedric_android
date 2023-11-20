package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.cedricapp.model.ShoppingDetailsModel;
import com.cedricapp.R;

import java.util.ArrayList;
import java.util.List;

public class SpecificNutritionDetailsAdapter extends RecyclerView.Adapter<SpecificNutritionDetailsAdapter.ShoppingDetailViewHolder> {

    private final List<ShoppingDetailsModel> dataArrayList;
    ArrayList<ShoppingDetailsModel> checkedItems = new ArrayList<ShoppingDetailsModel>();
    Context context;
    int categoryId;
    private int getCatId;

    public SpecificNutritionDetailsAdapter(List<ShoppingDetailsModel> dataArrayList, Context context) {
        this.dataArrayList = dataArrayList;
        this.context = context;
    }

    @Override
    public ShoppingDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.specific_shopping_details, parent, false);

        ShoppingDetailViewHolder myViewHolder = new ShoppingDetailViewHolder(view);
        return myViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ShoppingDetailViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ShoppingDetailsModel myListData = dataArrayList.get(position);

        String quantity = myListData.getNutritionShoppingQuantity();
        //  int catId = myListData.getNutritionShoppingCategoryId();
        String space = "    ";
        if (Integer.parseInt(quantity) >= 1000) {
            quantity = String.valueOf(Float.parseFloat(quantity) / 1000);
            holder.nutrientDetails.setText(space + quantity + "kg" + " " + myListData.getNutritionShoppingDetails());
        } else {
            holder.nutrientDetails.setText(space + quantity + "g" + " " + myListData.getNutritionShoppingDetails());
        }

      /*  boolean isChecked = getCheckedState(position);
       //int getSharedCatId=getCategoryId(categoryId);
        if(isChecked==true ){
            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.nutrientDetails.setChecked(isChecked);
        }*/

       /* holder.nutrientDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                boolean checked = ((CheckBox) compoundButton).isChecked();


                if (isChecked) {
                     //checkedItems.add(dataArrayList.get(position).getNutritionShoppingDetails());
                    int pos=position;
                     categoryId=  myListData.getNutritionShoppingCategoryId();

                     Toast.makeText(compoundButton.getContext(), checkedItems +" check status",Toast.LENGTH_LONG).show();
                    Log.d("Ingredient data",String.valueOf(myListData.getId()));

                    holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    putCheckedState(isChecked, position,categoryId);
                } else {
                    categoryId=  myListData.getNutritionShoppingCategoryId();
                    holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    putCheckedState(isChecked, position,categoryId);
                }
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ShoppingDetailViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox nutrientDetails;

        public ShoppingDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nutrientDetails = (MaterialCheckBox) itemView.findViewById(R.id.checkboxForNutritionShopping);
        }
    }
}
