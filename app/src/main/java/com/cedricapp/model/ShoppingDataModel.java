package com.cedricapp.model;

public class ShoppingDataModel {
    String nutritionName;
    String nutritionImage;
    int _id;



    public ShoppingDataModel(String nutritionName, String nutritionImage, int _id) {
        this.nutritionName = nutritionName;
        this.nutritionImage = nutritionImage;
        this._id = _id;
    }

    public String getNutritionName() {
        return nutritionName;
    }

    public void setNutritionName(String nutritionName) {
        this.nutritionName = nutritionName;
    }

    public String getNutritionImage() {
        return nutritionImage;
    }

    public void setNutritionImage(String nutritionImage) {
        this.nutritionImage = nutritionImage;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
