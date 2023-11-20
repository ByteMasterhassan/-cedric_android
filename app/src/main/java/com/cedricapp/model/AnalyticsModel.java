package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnalyticsModel {
    @SerializedName("total_steps")
    @Expose
    Integer totalSteps;

    @SerializedName("total_calories")
    @Expose
    Double totalCalories;

    @SerializedName("total_distance")
    @Expose
    Double totalDistance;

    @SerializedName("data")
    @Expose
    private ArrayList<Data> data;
    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }


    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "AnalyticsModel{" +
                "totalSteps=" + totalSteps +
                ", totalCalories=" + totalCalories +
                ", totalDistance=" + totalDistance +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }


    public static class Data {
        int stepCountID;
        int userID;
        String api_synced_at;
        String is_day_api_synced;
        String createdAt;
        String updatedAt;
        @SerializedName("steps_count")
        @Expose
        private Integer stepsCount;
        @SerializedName("water_count")
        @Expose
        private Integer waterCount;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("user_time_zone")
        @Expose
        private String userTimeZone;
        @SerializedName("user_activity_date")
        @Expose
        private String userActivityDate;
        @SerializedName("calories")
        @Expose
        private Double calories;
        @SerializedName("activity_lat")
        @Expose
        private String activityLat;
        @SerializedName("activity_long")
        @Expose
        private String activityLong;
        @SerializedName("activity_location")
        @Expose
        private String activityLocation;


        public int getStepCountID() {
            return stepCountID;
        }

        public void setStepCountID(int stepCountID) {
            this.stepCountID = stepCountID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getApi_synced_at() {
            return api_synced_at;
        }

        public void setApi_synced_at(String api_synced_at) {
            this.api_synced_at = api_synced_at;
        }

        public String getIs_day_api_synced() {
            return is_day_api_synced;
        }

        public void setIs_day_api_synced(String is_day_api_synced) {
            this.is_day_api_synced = is_day_api_synced;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }



        public String getUserTimeZone() {
            return userTimeZone;
        }

        public void setUserTimeZone(String userTimeZone) {
            this.userTimeZone = userTimeZone;
        }

        public String getUserActivityDate() {
            return userActivityDate;
        }

        public void setUserActivityDate(String userActivityDate) {
            this.userActivityDate = userActivityDate;
        }


        public String getActivityLat() {
            return activityLat;
        }

        public void setActivityLat(String activityLat) {
            this.activityLat = activityLat;
        }

        public String getActivityLong() {
            return activityLong;
        }

        public void setActivityLong(String activityLong) {
            this.activityLong = activityLong;
        }

        public String getActivityLocation() {
            return activityLocation;
        }

        public void setActivityLocation(String activityLocation) {
            this.activityLocation = activityLocation;
        }

        public Integer getStepsCount() {
            return stepsCount;
        }

        public void setStepsCount(Integer stepsCount) {
            this.stepsCount = stepsCount;
        }

        public Integer getWaterCount() {
            return waterCount;
        }

        public void setWaterCount(Integer waterCount) {
            this.waterCount = waterCount;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getCalories() {
            return calories;
        }

        public void setCalories(Double calories) {
            this.calories = calories;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "stepCountID=" + stepCountID +
                    ", userID=" + userID +
                    ", api_synced_at='" + api_synced_at + '\'' +
                    ", is_day_api_synced='" + is_day_api_synced + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", stepsCount=" + stepsCount +
                    ", waterCount=" + waterCount +
                    ", distance=" + distance +
                    ", userTimeZone='" + userTimeZone + '\'' +
                    ", userActivityDate='" + userActivityDate + '\'' +
                    ", calories=" + calories +
                    ", activityLat='" + activityLat + '\'' +
                    ", activityLong='" + activityLong + '\'' +
                    ", activityLocation='" + activityLocation + '\'' +
                    '}';
        }
    }
}
