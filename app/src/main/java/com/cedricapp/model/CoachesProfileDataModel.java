package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoachesProfileDataModel {
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CoachesProfileDataModel{" +
                "data=" + data +
                '}';
    }

    public static class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("imageURL")
        @Expose
        private String imageURL;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("workout_count")
        @Expose
        private Integer workoutCount;
        @SerializedName("limit")
        @Expose
        private Integer limit;

        public Integer getWorkoutCount() {
            return workoutCount;
        }

        public void setWorkoutCount(Integer workoutCount) {
            this.workoutCount = workoutCount;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", description='" + description + '\'' +
                    ", role='" + role + '\'' +
                    ", workoutCount=" + workoutCount +
                    ", limit=" + limit +
                    '}';
        }
    }
}
