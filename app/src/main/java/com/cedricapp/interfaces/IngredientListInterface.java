package com.cedricapp.interfaces;

import com.cedricapp.model.RecipeUpdateModel;

public interface IngredientListInterface {
    public void onCheckedListener(String ingredientId, boolean checkedState, RecipeUpdateModel recipeUpdateModel);

    public void onUnCheckedListener(String ingredientId, boolean checkedState, RecipeUpdateModel recipeUpdateModel);

}
