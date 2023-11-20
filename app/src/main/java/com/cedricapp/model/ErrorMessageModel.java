package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorMessageModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private Errors errors;

    @SerializedName("error")
    @Expose
    private String error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorMessageModel{" +
                "message='" + message + '\'' +
                ", errors=" + errors +
                ", error='" + error + '\'' +
                '}';
    }

    public class Errors {

        @SerializedName("user_id")
        @Expose
        private List<String> userId = null;
        @SerializedName("coach_id")
        @Expose
        private List<String> coachId = null;
        @SerializedName("day")
        @Expose
        private List<String> day = null;
        @SerializedName("week")
        @Expose
        private List<String> week = null;

        @SerializedName("location")
        @Expose
        private List<String> location = null;

        @SerializedName("timezone")
        @Expose
        private List<String> timezone = null;

        @SerializedName("agent")
        @Expose
        private List<String> agent = null;

        @SerializedName("os")
        @Expose
        private List<String> os = null;

        @SerializedName("height")
        @Expose
        private List<String> height = null;

        @SerializedName("weight")
        @Expose
        private List<String> weight = null;

        @SerializedName("age")
        @Expose
        private List<String> age = null;

        @SerializedName("gender")
        @Expose
        private List<String> gender = null;

        @SerializedName("unit")
        @Expose
        private List<String> unit = null;

        @SerializedName("username")
        @Expose
        private List<String> username = null;

        @SerializedName("product_id")
        @Expose
        private List<String> productID = null;

        @SerializedName("subscription_id")
        @Expose
        private List<String> subscriptionID = null;

        @SerializedName("plan_id")
        @Expose
        private List<String> planID = null;

        @SerializedName("goal_id")
        @Expose
        private List<String> goalID = null;

        public List<String> getUserId() {
            return userId;
        }

        public void setUserId(List<String> userId) {
            this.userId = userId;
        }

        public List<String> getCoachId() {
            return coachId;
        }

        public void setCoachId(List<String> coachId) {
            this.coachId = coachId;
        }

        public List<String> getDay() {
            return day;
        }

        public void setDay(List<String> day) {
            this.day = day;
        }

        public List<String> getWeek() {
            return week;
        }

        public void setWeek(List<String> week) {
            this.week = week;
        }

        public List<String> getLocation() {
            return location;
        }

        public void setLocation(List<String> location) {
            this.location = location;
        }

        public List<String> getTimezone() {
            return timezone;
        }

        public void setTimezone(List<String> timezone) {
            this.timezone = timezone;
        }

        public List<String> getAgent() {
            return agent;
        }

        public void setAgent(List<String> agent) {
            this.agent = agent;
        }

        public List<String> getOs() {
            return os;
        }

        public void setOs(List<String> os) {
            this.os = os;
        }

        public List<String> getHeight() {
            return height;
        }

        public void setHeight(List<String> height) {
            this.height = height;
        }

        public List<String> getWeight() {
            return weight;
        }

        public void setWeight(List<String> weight) {
            this.weight = weight;
        }

        public List<String> getAge() {
            return age;
        }

        public void setAge(List<String> age) {
            this.age = age;
        }

        public List<String> getGender() {
            return gender;
        }

        public void setGender(List<String> gender) {
            this.gender = gender;
        }

        public List<String> getUnit() {
            return unit;
        }

        public void setUnit(List<String> unit) {
            this.unit = unit;
        }

        public List<String> getUsername() {
            return username;
        }

        public void setUsername(List<String> username) {
            this.username = username;
        }

        public List<String> getProductID() {
            return productID;
        }

        public void setProductID(List<String> productID) {
            this.productID = productID;
        }

        public List<String> getSubscriptionID() {
            return subscriptionID;
        }

        public void setSubscriptionID(List<String> subscriptionID) {
            this.subscriptionID = subscriptionID;
        }

        public List<String> getPlanID() {
            return planID;
        }

        public void setPlanID(List<String> planID) {
            this.planID = planID;
        }

        public List<String> getGoalID() {
            return goalID;
        }

        public void setGoalID(List<String> goalID) {
            this.goalID = goalID;
        }

        @Override
        public String toString() {
            return "Errors{" +
                    "userId=" + userId +
                    ", coachId=" + coachId +
                    ", day=" + day +
                    ", week=" + week +
                    ", location=" + location +
                    ", timezone=" + timezone +
                    ", agent=" + agent +
                    ", os=" + os +
                    ", height=" + height +
                    ", weight=" + weight +
                    ", age=" + age +
                    ", gender=" + gender +
                    ", unit=" + unit +
                    ", username=" + username +
                    ", productID=" + productID +
                    ", subscriptionID=" + subscriptionID +
                    ", planID=" + planID +
                    ", goalID=" + goalID +
                    '}';
        }
    }
}
