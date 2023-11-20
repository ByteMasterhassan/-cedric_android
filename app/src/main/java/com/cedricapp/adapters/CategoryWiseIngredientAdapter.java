package com.cedricapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.IngredientListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeUpdateModel;
import com.cedricapp.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryWiseIngredientAdapter extends RecyclerView.Adapter<CategoryWiseIngredientAdapter.IngredientHolder> {
    Context context;
    List<NutritionDataModel.Category> categories;
    List<NutritionDataModel.Ingredient> ingredientList;
    ArrayList<String> ingredientsCheckedList;
    IngredientListInterface ingredientListInterface;
    ArrayList<RecipeUpdateModel> recipes;

    String requestFrom;

    int recipeID;
    DBHelper dbHelper;
    Resources resources;

    public CategoryWiseIngredientAdapter(Context context, List<NutritionDataModel.Category> categories, /*List<NutritionDataModel.Ingredient> ingredientList,*/ IngredientListInterface callback, String requestFrom, int recipeID,Resources resources) {
        this.context = context;
        this.categories = categories;
        /*this.ingredientList = ingredientList;*/
        this.ingredientListInterface = callback;
        dbHelper = new DBHelper(context);
        this.requestFrom = requestFrom;
        this.recipeID = recipeID;
        this.resources=resources;
    }

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list_categories, parent, false);
        return new IngredientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        recipes = new ArrayList<>();
        holder.categoryNameMTV.setText(categories.get(position).getName());
        if (categories.get(position).getName() != null) {
            List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
            if (requestFrom.matches("cart")) {
                ingredientList = dbHelper.getIngredientsByCategory(categories.get(position).getName());
                populateRecipeModelList(dbHelper.getIngredientsByCategoryWithoutMerging(categories.get(position).getName()));
                /*if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Ingredients Gotten from DB in Shopping List: " + ingredientList.toString());
                }*/
            } else if (requestFrom.matches("specific")) {
                ingredientList = dbHelper.getIngredientsByCategoryAndRecipe(categories.get(position).getName(), recipeID);
                populateRecipeModelList(dbHelper.getIngredientsByCategoryWithoutMerging(categories.get(position).getName()));
                /*if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Ingredients Gotten from DB in against Recipe ID: "+recipeID+": "+ ingredientList.toString());
                }*/
            }
            //List<NutritionDataModel.Ingredient> ingredientList = getAddedQtyList(getIngredientListByCategoryName(categories.get(position).getName()));
            if (ingredientList.size() == 0) {
                holder.categoryNameMTV.setVisibility(View.GONE);
                holder.ingredientsDetailRV.setVisibility(View.GONE);
            } else {
                IngredientByCategoryAdapter adapter = new IngredientByCategoryAdapter(context, ingredientList, ingredientListInterface, recipes, requestFrom,resources);
                RecyclerView.LayoutManager ingredientLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.ingredientsDetailRV.setHasFixedSize(true);
                holder.ingredientsDetailRV.setLayoutManager(ingredientLayoutManager);
                holder.ingredientsDetailRV.setAdapter(adapter);
            }
        }
    }

    void populateRecipeModelList(List < NutritionDataModel.Ingredient > ingredientList) {
        for (int i = 0; i < ingredientList.size(); i++) {
            ArrayList<Integer> ingredient = new ArrayList<>();
            ingredient.add(ingredientList.get(i).getId());
            recipes.add(new RecipeUpdateModel(ingredientList.get(i).getRecipeID(), ingredient, ingredientList.get(i).getServing()));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    List<NutritionDataModel.Ingredient> getIngredientListByCategoryName(String categoryName) {
        List<NutritionDataModel.Ingredient> filteredIngredientList = new ArrayList<>();
        for (int i = 0; i < ingredientList.size(); i++) {
            if (categoryName.equals(ingredientList.get(i).getCategory())) {
                filteredIngredientList.add(ingredientList.get(i));
                ArrayList<Integer> ingredient = new ArrayList<>();
                ingredient.add(ingredientList.get(i).getId());
                if (ingredientList.get(i).getServing() == null) {
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "Serving is null so add serving as 1");
                    }
                    ingredientList.get(i).setServing("1");
                } else {
                    ingredientList.get(i).setQuantity(ingredientList.get(i).getQuantity() * Integer.parseInt(ingredientList.get(i).getServing()));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Serving is not null so serving is " + ingredientList.get(i).getServing());
                    }
                }
                //TODO
                recipes.add(new RecipeUpdateModel(ingredientList.get(i).getRecipeID(), ingredient, ingredientList.get(i).getServing()));
            }
        }
        return filteredIngredientList;

    }

    List<NutritionDataModel.Ingredient> getAddedQtyList(List<NutritionDataModel.Ingredient> ingredientListByCategory) {
        List<NutritionDataModel.Ingredient> tempList = new ArrayList();
        // sort list to maintain order
        Collections.sort(ingredientListByCategory);
        String ingredientName = null;
        int id = 0;
        String category = null, unit = null, fats = null, calories = null, protein = null, carbs = null;
        double quantity = 0.0;
        int recipeID = 0;
        String serving = "";
        String status = "";
        NutritionDataModel.Ingredient itemIngredientObj = null;
        for (int i = 0; i < ingredientListByCategory.size(); i++) {
            if (ingredientName == null || ingredientName.equals(ingredientListByCategory.get(i).getIngredient())) {
                quantity = quantity + ingredientListByCategory.get(i).getQuantity();
                /*ArrayList <Integer> ingredient = new ArrayList<>();
                ingredient.add(ingredientListByCategory.get(i).getId());
                recipes.add(new RecipeUpdateModel(ingredientListByCategory.get(i).getRecipeID(),ingredient,ingredientListByCategory.get(i).getServing()));*/
            } else {
                itemIngredientObj = new NutritionDataModel.Ingredient();
                itemIngredientObj.setId(id);
                itemIngredientObj.setIngredient(ingredientName);
                itemIngredientObj.setCategory(category);
                itemIngredientObj.setUnit(unit);
                itemIngredientObj.setFats(fats);
                itemIngredientObj.setCalories(calories);
                itemIngredientObj.setProtein(protein);
                itemIngredientObj.setCarbs(carbs);
                itemIngredientObj.setQuantity(quantity);
                itemIngredientObj.setRecipeID(recipeID);
                itemIngredientObj.setServing(serving);
                itemIngredientObj.setStatus(status);
                tempList.remove(itemIngredientObj);
                tempList.add(itemIngredientObj);
                quantity = 0;
                quantity = quantity + ingredientListByCategory.get(i).getQuantity();
            }

            ingredientName = ingredientListByCategory.get(i).getIngredient();
            id = ingredientListByCategory.get(i).getId();
            category = ingredientListByCategory.get(i).getCategory();
            unit = ingredientListByCategory.get(i).getUnit();
            fats = ingredientListByCategory.get(i).getFats();
            calories = ingredientListByCategory.get(i).getCalories();
            protein = ingredientListByCategory.get(i).getProtein();
            carbs = ingredientListByCategory.get(i).getCarbs();
            recipeID = ingredientListByCategory.get(i).getRecipeID();
            serving = ingredientListByCategory.get(i).getServing();
            status = ingredientListByCategory.get(i).getStatus();

            if (i == ingredientListByCategory.size() - 1) {
                itemIngredientObj = new NutritionDataModel.Ingredient();
                itemIngredientObj.setId(id);
                itemIngredientObj.setIngredient(ingredientName);
                itemIngredientObj.setCategory(category);
                itemIngredientObj.setUnit(unit);
                itemIngredientObj.setFats(fats);
                itemIngredientObj.setCalories(calories);
                itemIngredientObj.setProtein(protein);
                itemIngredientObj.setCarbs(carbs);
                itemIngredientObj.setQuantity(quantity);
                itemIngredientObj.setRecipeID(recipeID);
                itemIngredientObj.setServing(serving);
                itemIngredientObj.setStatus(status);
                tempList.add(itemIngredientObj);
            }
        }

        return tempList;
    }

    class IngredientHolder extends RecyclerView.ViewHolder {
        MaterialTextView categoryNameMTV;
        RecyclerView ingredientsDetailRV;
        MaterialCheckBox nutrientDetails;

        public IngredientHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameMTV = itemView.findViewById(R.id.categoryNameMTV);
            ingredientsDetailRV = itemView.findViewById(R.id.ingredientsDetailRV);
            this.nutrientDetails = itemView.findViewById(R.id.checkboxForNutritionShopping);
        }
    }

}
