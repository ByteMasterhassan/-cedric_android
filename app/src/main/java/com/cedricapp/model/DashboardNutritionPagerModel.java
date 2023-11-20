package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardNutritionPagerModel {
    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("status")
    @Expose
    private Boolean status;


    @NotNull
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DashboardNutritionPagerModel{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }

    /*@Override
    public String toString() {
        return "DashboardNutrition{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", errors=" + errors +
                '}';
    }*/

    public static class Data {


        @SerializedName("recipes")
        @Expose
        public Recipes recipes;

        public Recipes getRecipes() {
            return recipes;
        }

        public void setRecipes(Recipes recipes) {
            this.recipes = recipes;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "recipes=" + recipes +
                    '}';
        }

        public static class Recipes {


            @SerializedName("page_1")
            @Expose
            public List<Page1> page1 = null;
            @SerializedName("page_2")
            @Expose
            public List<Page2> page2 = null;
            @SerializedName("page_3")
            @Expose
            public List<Page3> page3 = null;

            public List<Page1> getPage1() {
                return page1;
            }

            public void setPage1(List<Page1> page1) {
                this.page1 = page1;
            }

            public List<Page2> getPage2() {
                return page2;
            }

            public void setPage2(List<Page2> page2) {
                this.page2 = page2;
            }

            public List<Page3> getPage3() {
                return page3;
            }

            public void setPage3(List<Page3> page3) {
                this.page3 = page3;
            }

            @Override
            public String toString() {
                return "Recipes{" +
                        "page1=" + page1 +
                        ", page2=" + page2 +
                        ", page3=" + page3 +
                        '}';
            }
        }


    }


    public static class Page1 {

        @SerializedName("index")
        @Expose
        private Integer index;
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

        @SerializedName("total_calories")
        @Expose
        private String totalCalories;

        @SerializedName("is_added_in_shoppingList")
        @Expose
        private Boolean is_added_in_shoppingList;
        private String pageNumber;

        public String getPageNumber() {
            return pageNumber;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
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

        public void setPageNumber(String pageNumber) {
            this.pageNumber = pageNumber;
        }

        public String getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(String totalCalories) {
            this.totalCalories = totalCalories;
        }

        public Boolean isIs_added_in_shoppingList() {
            return is_added_in_shoppingList;
        }

        public void setIs_added_in_shoppingList(Boolean is_added_in_shoppingList) {
            this.is_added_in_shoppingList = is_added_in_shoppingList;
        }

        @Override
        public String toString() {
            return "Page1{" +
                    "index=" + index +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", totalCalories='" + totalCalories + '\'' +
                    ", is_added_in_shoppingList=" + is_added_in_shoppingList +
                    ", pageNumber='" + pageNumber + '\'' +
                    '}';
        }
    }

    public static class Page2 {

        @SerializedName("index")
        @Expose
        private Integer index;
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

        @SerializedName("total_calories")
        @Expose
        private String totalCalories;

        @SerializedName("is_added_in_shoppingList")
        @Expose
        private Boolean is_added_in_shoppingList;
        private String pageNumber;

        public String getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(String pageNumber) {
            this.pageNumber = pageNumber;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
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

        public String getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(String totalCalories) {
            this.totalCalories = totalCalories;
        }

        public Boolean isIs_added_in_shoppingList() {
            return is_added_in_shoppingList;
        }

        public void setIs_added_in_shoppingList(Boolean is_added_in_shoppingList) {
            this.is_added_in_shoppingList = is_added_in_shoppingList;
        }

        @Override
        public String toString() {
            return "Page2{" +
                    "index=" + index +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", totalCalories='" + totalCalories + '\'' +
                    ", is_added_in_shoppingList=" + is_added_in_shoppingList +
                    '}';
        }
    }

    public static class Page3 {

        @SerializedName("index")
        @Expose
        private Integer index;
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

        @SerializedName("total_calories")
        @Expose
        private String totalCalories;

        @SerializedName("is_added_in_shoppingList")
        @Expose
        private Boolean is_added_in_shoppingList;
        private String pageNumber;

        public String getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(String pageNumber) {
            this.pageNumber = pageNumber;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
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

        public String getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(String totalCalories) {
            this.totalCalories = totalCalories;
        }

        public Boolean isIs_added_in_shoppingList() {
            return is_added_in_shoppingList;
        }

        public void setIs_added_in_shoppingList(Boolean is_added_in_shoppingList) {
            this.is_added_in_shoppingList = is_added_in_shoppingList;
        }

        @Override
        public String toString() {
            return "Page3{" +
                    "index=" + index +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", totalCalories='" + totalCalories + '\'' +
                    ", is_added_in_shoppingList=" + is_added_in_shoppingList +
                    '}';
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
