package com.cedricapp.model;

import java.util.List;

public class CheckedUncheckedIngredientModel {
    Integer recipe_id;
    List<String> checked;
    List<String> un_checked;
    Integer servings;

    public CheckedUncheckedIngredientModel(Integer recipe_id, List<String> checked, List<String> un_checked, Integer servings) {
        this.recipe_id = recipe_id;
        this.checked = checked;
        this.un_checked = un_checked;
        this.servings = servings;
    }

    public Integer getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(Integer recipe_id) {
        this.recipe_id = recipe_id;
    }

    public List<String> getChecked() {
        return checked;
    }

    public void setChecked(List<String> checked) {
        this.checked = checked;
    }

    public List<String> getUn_checked() {
        return un_checked;
    }

    public void setUn_checked(List<String> un_checked) {
        this.un_checked = un_checked;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "CheckedUncheckedIngredientModel{" +
                "recipe_id=" + recipe_id +
                ", checked=" + checked +
                ", un_checked=" + un_checked +
                ", servings=" + servings +
                '}';
    }
}
