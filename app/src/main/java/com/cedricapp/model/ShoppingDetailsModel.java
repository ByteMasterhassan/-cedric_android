package com.cedricapp.model;

public class ShoppingDetailsModel {
    int id;
    String nutritionShoppingDetails;
    String nutritionShoppingQuantity;
    String nutritionShoppingCategoryId;
    boolean checkedState;

    public boolean isCheckedState() {
        return checkedState;
    }

    public void setCheckedState(boolean checkedState) {
        this.checkedState = checkedState;
    }

    public String getNutritionShoppingCategoryId() {
        return nutritionShoppingCategoryId;
    }

    public void setNutritionShoppingCategoryId(String nutritionShoppingCategoryId) {
        this.nutritionShoppingCategoryId = nutritionShoppingCategoryId;
    }

    public String getNutritionShoppingQuantity() {
        return nutritionShoppingQuantity;
    }

    public void setNutritionShoppingQuantity(String nutritionShoppingQuantity) {
        this.nutritionShoppingQuantity = nutritionShoppingQuantity;
    }

    public ShoppingDetailsModel(String nutritionShoppingDetails, int _id) {
        this.id = _id;
        this.nutritionShoppingDetails = nutritionShoppingDetails;
    }

    public ShoppingDetailsModel(String nutritionShoppingDetails, int _id,String nutritionShoppingQuantity, String nutritionShoppingCategoryId) {
        this.id = _id;
        this.nutritionShoppingDetails = nutritionShoppingDetails;
        this.nutritionShoppingQuantity = nutritionShoppingQuantity;
        this.nutritionShoppingCategoryId = nutritionShoppingCategoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNutritionShoppingDetails() {
        return nutritionShoppingDetails;
    }

    public void setNutritionShoppingDetails(String nutritionShoppingDetails) {
        this.nutritionShoppingDetails = nutritionShoppingDetails;
    }
}
