package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CheckedUncheckedResponseModel {
    @SerializedName("recipe")
    @Expose
    private Integer recipe;
    @SerializedName("checked_items")
    @Expose
    private List<String> checkedItems;
    @SerializedName("unchecked_items")
    @Expose
    private List<String> uncheckedItems;
    @SerializedName("servings")
    @Expose
    private Integer servings;

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private Errors errors;

    public Integer getRecipe() {
        return recipe;
    }

    public void setRecipe(Integer recipe) {
        this.recipe = recipe;
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public void setCheckedItems(List<String> checkedItems) {
        this.checkedItems = checkedItems;
    }

    public List<String> getUncheckedItems() {
        return uncheckedItems;
    }

    public void setUncheckedItems(List<String> uncheckedItems) {
        this.uncheckedItems = uncheckedItems;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "CheckedUncheckedResponseModel{" +
                "recipe=" + recipe +
                ", checkedItems=" + checkedItems +
                ", uncheckedItems=" + uncheckedItems +
                ", servings=" + servings +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                '}';
    }

    public class Errors {

        @SerializedName("user_id")
        @Expose
        private List<String> userId;
        @SerializedName("recipe")
        @Expose
        private List<String> recipe;

        public List<String> getUserId() {
            return userId;
        }

        public void setUserId(List<String> userId) {
            this.userId = userId;
        }

        public List<String> getRecipe() {
            return recipe;
        }

        public void setRecipe(List<String> recipe) {
            this.recipe = recipe;
        }

        @Override
        public String toString() {
            return "Errors{" +
                    "userId=" + userId +
                    ", recipe=" + recipe +
                    '}';
        }
    }
}
