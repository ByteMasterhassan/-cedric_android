package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.model.SingleRecipeDataModel;
import com.google.android.material.textview.MaterialTextView;
import com.cedricapp.R;

import java.util.ArrayList;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsHolder> {


    @NonNull
    private final Context context;
    private ArrayList<SingleRecipeDataModel.Ingredient> nutritionIngredients;

    public IngredientsAdapter(@NonNull Context context, ArrayList<SingleRecipeDataModel.Ingredient> nutritionIngredients) {
        this.context = context;
        this.nutritionIngredients = nutritionIngredients;
    }

    @Override
    public IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingerdient_recyclerview, parent, false);
        return new IngredientsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull IngredientsHolder holder, int position) {
        ArrayList<SingleRecipeDataModel.Ingredient> uploadCurrentData = nutritionIngredients;

        holder.ingredientName.setText(uploadCurrentData.get(position).getIngredient());
     /*   if (Integer.parseInt(uploadCurrentData.get(position).getQuantity()) < 1000) {
            holder.ingredientQuantity.setText(uploadCurrentData.get(position).getQuantity() + "g");
        } else {
            String quantity = uploadCurrentData.get(position).getQuantity();
            quantity = String.valueOf(Float.parseFloat(quantity) / 1000);
            holder.ingredientQuantity.setText(quantity + "kg");
        }*/

        holder.ingredientQuantity.setText(uploadCurrentData.get(position).getQuantity()+""+uploadCurrentData.get(position).getUnit());
        holder.ingredientFats.setText(uploadCurrentData.get(position).getFats() +""+uploadCurrentData.get(position).getUnit());
        holder.ingredientCarbs.setText(uploadCurrentData.get(position).getCarbs() +""+uploadCurrentData.get(position).getUnit());
        holder.ingredientKcal.setText(uploadCurrentData.get(position).getCalories() +""+uploadCurrentData.get(position).getUnit());
        holder.ingredientProteins.setText(uploadCurrentData.get(position).getProtein() +""+uploadCurrentData.get(position).getUnit());

    }

    @Override
    public int getItemCount() {
        if (nutritionIngredients != null)
            return nutritionIngredients.size();
        return 0;
    }

    class IngredientsHolder extends RecyclerView.ViewHolder {
        MaterialTextView ingredientName, ingredientQuantity, ingredientFats, ingredientProteins, ingredientCarbs, ingredientKcal;

        public IngredientsHolder(@NonNull View itemView) {
            super(itemView);

            ingredientName = itemView.findViewById(R.id.textViewIngredientName);
            ingredientQuantity = itemView.findViewById(R.id.ingredQnty);
            ingredientCarbs = itemView.findViewById(R.id.ingredCarbs);
            ingredientFats = itemView.findViewById(R.id.ingredFats);
            ingredientKcal = itemView.findViewById(R.id.ingredKcal);
            ingredientProteins = itemView.findViewById(R.id.ingredProteins);

        }
    }
}
