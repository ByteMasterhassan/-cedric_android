package com.cedricapp.common;

import java.util.Arrays;

public class PastActivities {
    String noOfDays;
    String []stepCounts;
    String []waterCounts;
    String []distances;
    String []timeZones;
    String []calories;
    String []latitudes;
    String []longitudes;
    String []locations;
    String []activityDates;

    public PastActivities(String noOfDays, String[] stepCounts, String[] waterCounts, String[] distances, String[] timeZones, String[] calories, String[] latitudes, String[] longitudes, String[] locations, String[] activityDates) {
        this.noOfDays = noOfDays;
        this.stepCounts = stepCounts;
        this.waterCounts = waterCounts;
        this.distances = distances;
        this.timeZones = timeZones;
        this.calories = calories;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.locations = locations;
        this.activityDates = activityDates;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public String[] getStepCounts() {
        return stepCounts;
    }

    public void setStepCounts(String[] stepCounts) {
        this.stepCounts = stepCounts;
    }

    public String[] getWaterCounts() {
        return waterCounts;
    }

    public void setWaterCounts(String[] waterCounts) {
        this.waterCounts = waterCounts;
    }

    public String[] getDistances() {
        return distances;
    }

    public void setDistances(String[] distances) {
        this.distances = distances;
    }

    public String[] getTimeZones() {
        return timeZones;
    }

    public void setTimeZones(String[] timeZones) {
        this.timeZones = timeZones;
    }

    public String[] getCalories() {
        return calories;
    }

    public void setCalories(String[] calories) {
        this.calories = calories;
    }

    public String[] getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(String[] latitudes) {
        this.latitudes = latitudes;
    }

    public String[] getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(String[] longitudes) {
        this.longitudes = longitudes;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String[] getActivityDates() {
        return activityDates;
    }

    public void setActivityDates(String[] activityDates) {
        this.activityDates = activityDates;
    }

    @Override
    public String toString() {
        return "PastActivities{" +
                "noOfDays='" + noOfDays + '\'' +
                ", stepCounts=" + Arrays.toString(stepCounts) +
                ", waterCounts=" + Arrays.toString(waterCounts) +
                ", distances=" + Arrays.toString(distances) +
                ", timeZones=" + Arrays.toString(timeZones) +
                ", calories=" + Arrays.toString(calories) +
                ", latitudes=" + Arrays.toString(latitudes) +
                ", longitudes=" + Arrays.toString(longitudes) +
                ", locations=" + Arrays.toString(locations) +
                ", activityDates=" + Arrays.toString(activityDates) +
                '}';
    }
}
