package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WorkoutDataModel implements Serializable {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WorkoutDataModel{" +
                "data=" + data +
                '}';
    }

    public static class Data implements Serializable {

        @SerializedName("workouts")
        @Expose
        private List<Workout> workouts = null;

        public List<Workout> getWorkouts() {
            return workouts;
        }

        public void setWorkouts(List<Workout> workouts) {
            this.workouts = workouts;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "workouts=" + workouts +
                    '}';
        }


        public static class Workout implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            private Integer coachId;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("thumbnail")
            @Expose
            private String thumbnail;
            @SerializedName("reps")
            @Expose
            private String reps;
            @SerializedName("sets")
            @Expose
            private String sets;
            @SerializedName("duration")
            @Expose
            private String duration;
            @SerializedName("isWatched")
            @Expose
            private Boolean isWatched;

            private String loadDate;

            public String getLoadDate() {
                return loadDate;
            }

            public void setLoadDate(String loadDate) {
                this.loadDate = loadDate;
            }

            public Integer getCoachId() {
                return coachId;
            }

            public void setCoachId(Integer coachId) {
                this.coachId = coachId;
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

            public String getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(String thumbnail) {
                this.thumbnail = thumbnail;
            }

            public String getReps() {
                return reps;
            }

            public void setReps(String reps) {
                this.reps = reps;
            }

            public String getSets() {
                return sets;
            }

            public void setSets(String sets) {
                this.sets = sets;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public Boolean getWatched() {
                return isWatched;
            }

            public void setWatched(Boolean watched) {
                isWatched = watched;
            }

            @Override
            public String toString() {
                return "Workout{" +
                        "id=" + id +
                        ", coachId=" + coachId +
                        ", name='" + name + '\'' +
                        ", thumbnail='" + thumbnail + '\'' +
                        ", reps='" + reps + '\'' +
                        ", sets='" + sets + '\'' +
                        ", duration='" + duration + '\'' +
                        ", isWatched=" + isWatched +
                        ", loadDate='" + loadDate + '\'' +
                        '}';
            }
        }


    }

        /*public class Category {

            @SerializedName("category_name")
            @Expose
            private String categoryName;
            @SerializedName("icon")
            @Expose
            private String icon;

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

        }*/




}
