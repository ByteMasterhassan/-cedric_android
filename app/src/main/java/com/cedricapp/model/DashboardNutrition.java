package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DashboardNutrition {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("errors")
    @Expose
    private Errors errors;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "DashboardNutrition{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", errors=" + errors +
                '}';
    }

    public static class Data {

        @SerializedName("recipes")
        @Expose
        private ArrayList<Recipe> recipes = null;

        public ArrayList<Recipe> getRecipes() {
            return recipes;
        }

        public void setRecipes(ArrayList<Recipe> recipes) {
            this.recipes = recipes;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "recipes=" + recipes +
                    '}';
        }

        public static class Recipe {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("imageURL")
            @Expose
            private String imageURL;
            @SerializedName("index")
            @Expose
            private int index;

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

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImageURL() {
                return imageURL;
            }

            public void setImageURL(String imageURL) {
                this.imageURL = imageURL;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            @Override
            public String toString() {
                return "Recipe{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", title='" + title + '\'' +
                        ", imageURL='" + imageURL + '\'' +
                        ", index='" + index + '\'' +
                        '}';
            }
        }


    }

    public class Errors {

        @SerializedName("goal_id")
        @Expose
        private ArrayList<String> goalId = null;
        @SerializedName("level_id")
        @Expose
        private ArrayList<String> levelId = null;
        @SerializedName("day")
        @Expose
        private ArrayList<String> day = null;
        @SerializedName("week")
        @Expose
        private ArrayList<String> week = null;

        public ArrayList<String> getGoalId() {
            return goalId;
        }

        public void setGoalId(ArrayList<String> goalId) {
            this.goalId = goalId;
        }

        public ArrayList<String> getLevelId() {
            return levelId;
        }

        public void setLevelId(ArrayList<String> levelId) {
            this.levelId = levelId;
        }

        public ArrayList<String> getDay() {
            return day;
        }

        public void setDay(ArrayList<String> day) {
            this.day = day;
        }

        public ArrayList<String> getWeek() {
            return week;
        }

        public void setWeek(ArrayList<String> week) {
            this.week = week;
        }

        @Override
        public String toString() {
            return "Errors{" +
                    "goalId=" + goalId +
                    ", levelId=" + levelId +
                    ", day=" + day +
                    ", week=" + week +
                    '}';
        }
    }


}
