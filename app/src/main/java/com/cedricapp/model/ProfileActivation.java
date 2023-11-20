package com.cedricapp.model;

public class ProfileActivation {
    private String userID, weight,height,age,gender,goal_id,level_id,unit, userImage, username, food_preference;

    public ProfileActivation() {
    }

    public ProfileActivation(String userID, String weight, String height, String age, String gender, String unit, String username) {
        this.userID = userID;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.unit = unit;
        this.username = username;
    }

    public ProfileActivation(String userID, String weight, String height, String age, String gender, String goal_id, String level_id, String unit, String username) {
        this.userID = userID;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.goal_id = goal_id;
        this.level_id = level_id;
        this.unit = unit;
        this.username = username;
    }

    public ProfileActivation(String userID, String weight, String height, String age, String gender, String goal_id, String level_id, String unit, String username, String food_preference) {
        this.userID = userID;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.goal_id = goal_id;
        this.level_id = level_id;
        this.unit = unit;
        this.username = username;
        this.food_preference = food_preference;
    }

    public ProfileActivation(String userID, String weight, String height, String age, String gender, String goal_id, String level_id, String unit, String userImage, String username, String food_preference) {
        this.userID = userID;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.goal_id = goal_id;
        this.level_id = level_id;
        this.unit = unit;
        this.userImage = userImage;
        this.username = username;
        this.food_preference = food_preference;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFood_preference() {
        return food_preference;
    }

    public void setFood_preference(String food_preference) {
        this.food_preference = food_preference;
    }

    @Override
    public String toString() {
        return "ProfileActivation{" +
                "userID='" + userID + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", goal_id='" + goal_id + '\'' +
                ", level_id='" + level_id + '\'' +
                ", unit='" + unit + '\'' +
                ", userImage='" + userImage + '\'' +
                ", username='" + username + '\'' +
                ", food_preference='" + food_preference + '\'' +
                '}';
    }
}
