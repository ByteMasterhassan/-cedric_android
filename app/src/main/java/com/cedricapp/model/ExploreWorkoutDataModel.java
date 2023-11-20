package com.cedricapp.model;

public class ExploreWorkoutDataModel {

    String WorkoutMainName;
    String NumberOfWorkout;
    int mainWorkoutImage;
    int workout1Image;
    int workout2Image;
    int workout3Image;
    String Workout1Name;
    String workout2Name;
    String workout3Name;
    private int id_;

    public ExploreWorkoutDataModel(String workoutMainName, String numberOfWorkout, int mainWorkoutImage, int workout1Image, int workout2Image,
                                   int workout3Image, String workout1Name, String workout2Name, String workout3Name, int id_) {
        this.WorkoutMainName = workoutMainName;
        this.NumberOfWorkout = numberOfWorkout;
        this.mainWorkoutImage = mainWorkoutImage;
        this.workout1Image = workout1Image;
        this.workout2Image = workout2Image;
        this.workout3Image = workout3Image;
        this.Workout1Name = workout1Name;
        this.workout2Name = workout2Name;
        this.workout3Name = workout3Name;
        this.id_ = id_;
    }

    public String getWorkoutMainName() {
        return WorkoutMainName;
    }

    public void setWorkoutMainName(String workoutMainName) {
        WorkoutMainName = workoutMainName;
    }

    public String getNumberOfWorkout() {
        return NumberOfWorkout;
    }

    public void setNumberOfWorkout(String numberOfWorkout) {
        NumberOfWorkout = numberOfWorkout;
    }

    public int getMainWorkoutImage() {
        return mainWorkoutImage;
    }

    public void setMainWorkoutImage(int mainWorkoutImage) {
        this.mainWorkoutImage = mainWorkoutImage;
    }

    public int getWorkout1Image() {
        return workout1Image;
    }

    public void setWorkout1Image(int workout1Image) {
        this.workout1Image = workout1Image;
    }

    public int getWorkout2Image() {
        return workout2Image;
    }

    public void setWorkout2Image(int workout2Image) {
        this.workout2Image = workout2Image;
    }

    public int getWorkout3Image() {
        return workout3Image;
    }

    public void setWorkout3Image(int workout3Image) {
        this.workout3Image = workout3Image;
    }

    public String getWorkout1Name() {
        return Workout1Name;
    }

    public void setWorkout1Name(String workout1Name) {
        Workout1Name = workout1Name;
    }

    public String getWorkout2Name() {
        return workout2Name;
    }

    public void setWorkout2Name(String workout2Name) {
        this.workout2Name = workout2Name;
    }

    public String getWorkout3Name() {
        return workout3Name;
    }

    public void setWorkout3Name(String workout3Name) {
        this.workout3Name = workout3Name;
    }

    public int getId_() {
        return id_;
    }

    public void setId_(int id_) {
        this.id_ = id_;
    }
}
