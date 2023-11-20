package com.cedricapp.model;

public class WeekWiseNutritionModel {
    int nutritionId;
    String nutritionName;
    String nutritionImage;
    String quantity,day;


    /*public WeekWiseNutritionModel(String nutritionName, String nutritionImage, String quantity) {
        this.nutritionName=nutritionName;
        this.nutritionImage=nutritionImage;
        this.quantity=quantity;
    }*/

    public WeekWiseNutritionModel(int nutritionId, String nutritionName, String nutritionImage, String quantity, String day) {
        this.nutritionId = nutritionId;
        this.nutritionName = nutritionName;
        this.nutritionImage = nutritionImage;
        this.quantity = quantity;
        this.day=day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(int nutritionId) {
        this.nutritionId = nutritionId;
    }
}
