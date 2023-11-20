package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChecklistModel {
    @SerializedName("checked_items")
    @Expose
    private List<List<Integer>> checkedItems;
    @SerializedName("unchecked_items")
    @Expose
    private List<List<Integer>> uncheckedItems;

    public List<List<Integer>> getUncheckedItems() {
        return uncheckedItems;
    }

    public void setUncheckedItems(List<List<Integer>> uncheckedItems) {
        this.uncheckedItems = uncheckedItems;
    }

    public List<List<Integer>> getCheckedItems() {
        return checkedItems;
    }

    public void setCheckedItems(List<List<Integer>> checkedItems) {
        this.checkedItems = checkedItems;
    }

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private Errors errors;

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @SerializedName("user_id")
    @Expose
    private ArrayList<String> userId = null;
    @SerializedName("recipe")
    @Expose
    private ArrayList<String> recipe = null;

    public ArrayList<String> getUserId() {
        return userId;
    }

    public void setUserId(ArrayList<String> userId) {
        this.userId = userId;
    }

    public ArrayList<String> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<String> recipe) {
        this.recipe = recipe;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class Errors {

        @SerializedName("user_id")
        @Expose
        private ArrayList<String> userId = null;
        @SerializedName("recipe")
        @Expose
        private ArrayList<String> recipe = null;

        public ArrayList<String> getUserId() {
            return userId;
        }

        public void setUserId(ArrayList<String> userId) {
            this.userId = userId;
        }

        public ArrayList<String> getRecipe() {
            return recipe;
        }

        public void setRecipe(ArrayList<String> recipe) {
            this.recipe = recipe;
        }

    }
}
