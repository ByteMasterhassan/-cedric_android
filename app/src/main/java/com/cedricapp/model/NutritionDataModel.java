package com.cedricapp.model;

import java.util.ArrayList;
import java.util.List;

public class NutritionDataModel  {

    public Data data;
    public String message;
    public boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NutritionDataModel{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

    public static class Data{
        public ArrayList<Category> categories;
        public ArrayList<Recipe> recipes;

        public ArrayList<Category> getCategories() {
            return categories;
        }

        public void setCategories(ArrayList<Category> categories) {
            this.categories = categories;
        }

        public List<Recipe> getRecipes() {
            return recipes;
        }
        public void setRecipes(List<Recipe> recipes) {
            this.recipes = (ArrayList<Recipe>) recipes;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "categories=" + categories +
                    ", recipes=" + recipes +
                    '}';
        }
    }

    //category class

    public static class Category{
        public int id;
        public String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    //Recipe class
    public static class Recipe{
        public int id;
        public String day;
        public String name;
        public String title;
        public String imageURL;
        public String duration;
        public String cook;
        public String methods;
        public String serving;
        public List<Ingredient> ingredients;


        public List<Ingredient> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        public Recipe(int id, String day, String name, String title, String imageURL, String duration, String cook, String methods) {
            this.id = id;
            this.day = day;
            this.name = name;
            this.title = title;
            this.imageURL = imageURL;
            this.duration = duration;
            this.cook = cook;
            this.methods = methods;
        }

        public Recipe() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
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


        public String getServing() {
            return serving;
        }

        public void setServing(String serving) {
            this.serving = serving;
        }



        @Override
        public String toString() {
            return "Recipe{" +
                    "id=" + id +
                    ", day='" + day + '\'' +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", duration='" + duration + '\'' +
                    ", cook='" + cook + '\'' +
                    ", methods='" + methods + '\'' +
                    ", serving='" + serving + '\'' +

                    ", ingredients=" + ingredients +
                    '}';
        }
    }

    public static class Ingredient implements Comparable<Ingredient>{
        public int id;
        public String category;
        public String ingredient;
        public String unit;
        public double quantity;
        public String fats;
        public String calories;
        public String protein;
        public String carbs;
        public int recipeID;
        public String serving;
        public String status;
        public int total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
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
        public int compareTo(Ingredient ingredient) {
            return this.getIngredient().compareTo(ingredient.getIngredient());
        }

        public int getRecipeID() {
            return recipeID;
        }

        public void setRecipeID(int recipeID) {
            this.recipeID = recipeID;
        }

        public String getServing() {
            return serving;
        }

        public void setServing(String serving) {
            this.serving = serving;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
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
                    ", recipeID=" + recipeID +
                    ", serving='" + serving + '\'' +
                    ", status='" + status + '\'' +
                    ", total=" + total +
                    '}';
        }
    }

}
