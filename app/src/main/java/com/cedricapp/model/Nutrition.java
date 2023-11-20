package com.cedricapp.model;

public class Nutrition {
    NutritionDataModel.Recipe recipe;
    NutritionDataModel.Ingredient ingredient;
    NutritionDataModel.Category category;


    public Nutrition(NutritionDataModel.Recipe recipe, NutritionDataModel.Ingredient ingredient, NutritionDataModel.Category category) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.category = category;
    }

    public Nutrition() {
    }

    public NutritionDataModel.Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(NutritionDataModel.Recipe recipe) {
        this.recipe = recipe;
    }

    public NutritionDataModel.Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(NutritionDataModel.Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public NutritionDataModel.Category getCategory() {
        return category;
    }

    public void setCategory(NutritionDataModel.Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Nutrition{" +
                "recipe=" + recipe +
                ", ingredient=" + ingredient +
                ", category=" + category +
                '}';
    }
}
