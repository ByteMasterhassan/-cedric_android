package com.cedricapp.model;

public class CheckUncheckDbModel {
    int recipe_id,ingredient_id,serving;
    String checked_state;
    String server_checked_state;
    boolean is_api_synced;

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public String getServer_checked_state() {
        return server_checked_state;
    }

    public void setServer_checked_state(String server_checked_state) {
        this.server_checked_state = server_checked_state;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public int getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public String getChecked_state() {
        return checked_state;
    }

    public void setChecked_state(String checked_state) {
        this.checked_state = checked_state;
    }

    public boolean isIs_api_synced() {
        return is_api_synced;
    }

    public void setIs_api_synced(boolean is_api_synced) {
        this.is_api_synced = is_api_synced;
    }

    @Override
    public String toString() {
        return "CheckUncheckDbModel{" +
                "recipe_id=" + recipe_id +
                ", ingredient_id=" + ingredient_id +
                ", serving=" + serving +
                ", checked_state='" + checked_state + '\'' +
                ", server_checked_state='" + server_checked_state + '\'' +
                ", is_api_synced=" + is_api_synced +
                '}';
    }
}
