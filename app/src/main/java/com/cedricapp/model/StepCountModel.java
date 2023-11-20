package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class StepCountModel {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StepCountModel{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public static class Data{
        int stepCountID;
        int userID;
        String api_synced_at;
        String is_day_api_synced;
        String createdAt;
        String updatedAt;
        @SerializedName("steps_count")
        @Expose
        private String stepsCount;
        @SerializedName("water_count")
        @Expose
        private String waterCount;
        @SerializedName("distance")
        @Expose
        private String distance;
        @SerializedName("user_time_zone")
        @Expose
        private String userTimeZone;
        @SerializedName("user_activity_date")
        @Expose
        private String userActivityDate;
        @SerializedName("calories")
        @Expose
        private String calories;
        @SerializedName("activity_lat")
        @Expose
        private String activityLat;
        @SerializedName("activity_long")
        @Expose
        private String activityLong;
        @SerializedName("activity_location")
        @Expose
        private String activityLocation;

        public String getStepsCount() {
            return stepsCount;
        }

        public void setStepsCount(String stepsCount) {
            this.stepsCount = stepsCount;
        }

        public String getWaterCount() {
            return waterCount;
        }

        public void setWaterCount(String waterCount) {
            this.waterCount = waterCount;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
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

        public String getCalories() {
            return calories;
        }

        public void setCalories(String calories) {
            this.calories = calories;
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

        @Override
        public String toString() {
            return "Data{" +
                    "stepCountID=" + stepCountID +
                    ", userID=" + userID +
                    ", api_synced_at='" + api_synced_at + '\'' +
                    ", is_day_api_synced='" + is_day_api_synced + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", stepsCount='" + stepsCount + '\'' +
                    ", waterCount='" + waterCount + '\'' +
                    ", distance='" + distance + '\'' +
                    ", userTimeZone='" + userTimeZone + '\'' +
                    ", userActivityDate='" + userActivityDate + '\'' +
                    ", calories='" + calories + '\'' +
                    ", activityLat='" + activityLat + '\'' +
                    ", activityLong='" + activityLong + '\'' +
                    ", activityLocation='" + activityLocation + '\'' +
                    '}';
        }
    }
}
