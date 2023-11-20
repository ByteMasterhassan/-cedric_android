package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeIdAndServingJsonModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("servings")
    @Expose
    private Integer servings;

    public RecipeIdAndServingJsonModel(Integer id, Integer servings) {
      this.servings=servings;
      this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "RecipeIdAndServingJsonModel{" +
                "id=" + id +
                ", servings=" + servings +
                '}';
    }
}

