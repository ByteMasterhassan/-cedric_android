package com.cedricapp.model;

public class AllergyStringModel {
    String allergyID;
    String allergyName;

    public String getAllergyID() {
        return allergyID;
    }

    public void setAllergyID(String allergyID) {
        this.allergyID = allergyID;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    @Override
    public String toString() {
        return "AllergyStringModel{" +
                "allergyID='" + allergyID + '\'' +
                ", allergyName='" + allergyName + '\'' +
                '}';
    }
}
