package com.cedricapp.model;

public class FiltersWorkoutModel {
    String workoutsName;
    boolean checkedState;

    public FiltersWorkoutModel(String workoutsName, boolean checkedState) {
        this.workoutsName = workoutsName;
        this.checkedState = checkedState;
    }

    public String getWorkoutsName() {
        return workoutsName;
    }

    public void setWorkoutsName(String workoutsName) {
        this.workoutsName = workoutsName;
    }

    public boolean isCheckedState() {
        return checkedState;
    }

    public void setCheckedState(boolean checkedState) {
        this.checkedState = checkedState;
    }
}
