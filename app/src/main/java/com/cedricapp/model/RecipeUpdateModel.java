package com.cedricapp.model;

import java.util.ArrayList;

public class RecipeUpdateModel implements Comparable<RecipeUpdateModel> {
    int recipe_id;
    ArrayList<Integer> ingredient_ids;
    String servings;


    public RecipeUpdateModel(int recipe_id, ArrayList<Integer> ingredients, String serving) {
        this.recipe_id = recipe_id;
        this.ingredient_ids = ingredients;
        this.servings = serving;
    }


    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public ArrayList<Integer> getIngredient_ids() {
        return ingredient_ids;
    }

    public void setIngredient_ids(ArrayList<Integer> ingredient_ids) {
        this.ingredient_ids = ingredient_ids;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "RecipeUpdateModel{" +
                "recipe_id=" + recipe_id +
                ", ingredient_ids=" + ingredient_ids +
                ", servings='" + servings + '\'' +
                '}';
    }

    @Override
    public int compareTo(RecipeUpdateModel recipeUpdateModel) {
        String recipeID = Integer.toString(this.getRecipe_id());
        return recipeID.compareTo(Integer.toString(recipeUpdateModel.getRecipe_id()));
    }
}
