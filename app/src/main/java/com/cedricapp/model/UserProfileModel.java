package com.cedricapp.model;

public class UserProfileModel {
    public Data data;
    public String message;
    public boolean status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        public String user_id;
        public String weight;
        public String height;
        public String age;
        public String gender;
        public String goal_id;
        public String level_id;
        public String unit;
        public String goal;
        public String level;
        public String name;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
