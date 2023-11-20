package com.cedricapp.model;

import java.io.Serializable;

public class ProgramWorkout implements Serializable {
    Integer programID;
    String programName;
    String programDescription;
    Integer workoutId;
    String workoutName;
    String workoutDuration;
    boolean workoutIsWatched;
    String  workoutThumbnail;
    String week;
    String day;

    public Integer getProgramID() {
        return programID;
    }

    public void setProgramID(Integer programID) {
        this.programID = programID;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(String workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    public boolean isWorkoutIsWatched() {
        return workoutIsWatched;
    }

    public void setWorkoutIsWatched(boolean workoutIsWatched) {
        this.workoutIsWatched = workoutIsWatched;
    }

    public String getWorkoutThumbnail() {
        return workoutThumbnail;
    }

    public void setWorkoutThumbnail(String workoutThumbnail) {
        this.workoutThumbnail = workoutThumbnail;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "ProgramWorkouts{" +
                "programID=" + programID +
                ", programName='" + programName + '\'' +
                ", programDescription='" + programDescription + '\'' +
                ", workoutId=" + workoutId +
                ", workoutName='" + workoutName + '\'' +
                ", workoutDuration='" + workoutDuration + '\'' +
                ", workoutIsWatched=" + workoutIsWatched +
                ", workoutThumbnail='" + workoutThumbnail + '\'' +
                ", week='" + week + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
