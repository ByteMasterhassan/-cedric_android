package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SingleRecipeDataModel {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

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

    public class Data {

        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;
        @SerializedName("recipe")
        @Expose
        private Recipe recipe;

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public void setRecipe(Recipe recipe) {
            this.recipe = recipe;
        }

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
        @SerializedName("duration")
        @Expose
        private String duration;
        @SerializedName("cook")
        @Expose
        private String cook;
        @SerializedName("methods")
        @Expose
        private String methods;

        @SerializedName("method_array")
        @Expose
        private ArrayList<String> methodArray;

        @SerializedName("total_calories")
        @Expose
        private String totalCalories;
        @SerializedName("ingredients")
        @Expose
        private List<Ingredient> ingredients = null;

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

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getCook() {
            return cook;
        }

        public void setCook(String cook) {
            this.cook = cook;
        }

        public String getMethods() {
            return methods;
        }

        public void setMethods(String methods) {
            this.methods = methods;
        }

        public List<Ingredient> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        public String getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(String totalCalories) {
            this.totalCalories = totalCalories;
        }

        public ArrayList<String> getMethodArray() {
            return methodArray;
        }

        public void setMethodArray(ArrayList<String> methodArray) {
            this.methodArray = methodArray;
        }

        @Override
        public String toString() {
            return "Recipe{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", duration='" + duration + '\'' +
                    ", cook='" + cook + '\'' +
                    ", methods='" + methods + '\'' +
                    ", methodArray=" + methodArray +
                    ", totalCalories='" + totalCalories + '\'' +
                    ", ingredients=" + ingredients +
                    '}';
        }
    }

    public static class Ingredient {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("ingredient")
        @Expose
        private String ingredient;
        @SerializedName("unit")
        @Expose
        private String unit;
        @SerializedName("quantity")
        @Expose
        private Double quantity;
        @SerializedName("fats")
        @Expose
        private String fats;
        @SerializedName("calories")
        @Expose
        private String calories;
        @SerializedName("protein")
        @Expose
        private String protein;
        @SerializedName("carbs")
        @Expose
        private String carbs;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getIngredient() {
            return ingredient;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public String getFats() {
            return fats;
        }

        public void setFats(String fats) {
            this.fats = fats;
        }

        public String getCalories() {
            return calories;
        }

        public void setCalories(String calories) {
            this.calories = calories;
        }

        public String getProtein() {
            return protein;
        }

        public void setProtein(String protein) {
            this.protein = protein;
        }

        public String getCarbs() {
            return carbs;
        }

        public void setCarbs(String carbs) {
            this.carbs = carbs;
        }

        @Override
        public String toString() {
            return "Ingredient{" +
                    "id=" + id +
                    ", category='" + category + '\'' +
                    ", ingredient='" + ingredient + '\'' +
                    ", unit='" + unit + '\'' +
                    ", quantity=" + quantity +
                    ", fats='" + fats + '\'' +
                    ", calories='" + calories + '\'' +
                    ", protein='" + protein + '\'' +
                    ", carbs='" + carbs + '\'' +
                    '}';
        }
    }

    public class Category {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

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

    }

}
