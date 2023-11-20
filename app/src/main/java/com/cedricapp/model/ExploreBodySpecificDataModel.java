package com.cedricapp.model;

public class ExploreBodySpecificDataModel {
    int imageBodySpecific;
    int _id;
    String mealsName,mealsTime;


    public ExploreBodySpecificDataModel(int imageBodySpecific, int _id, String mealsName, String mealsTime) {
        this.imageBodySpecific = imageBodySpecific;
        this._id = _id;
        this.mealsName = mealsName;
        this.mealsTime = mealsTime;

    }

    public String getMealsName() {
        return mealsName;
    }

    public void setMealsName(String mealsName) {
        this.mealsName = mealsName;
    }

    public String getMealsTime() {
        return mealsTime;
    }

    public void setMealsTime(String mealsTime) {
        this.mealsTime = mealsTime;
    }

    public int getImageBodySpecific() {
        return imageBodySpecific;
    }

    public void setImageBodySpecific(int imageBodySpecific) {
        this.imageBodySpecific = imageBodySpecific;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
