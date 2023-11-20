package com.cedricapp.model;

public class CartItem {
    int ShoppingID;
    Users users;
    NutritionDataModel.Recipe recipe;

    public CartItem(int shoppingID, Users users, NutritionDataModel.Recipe recipe) {
        ShoppingID = shoppingID;
        this.users = users;
        this.recipe = recipe;
    }

    public CartItem() {
    }

    public int getShoppingID() {
        return ShoppingID;
    }

    public void setShoppingID(int shoppingID) {
        ShoppingID = shoppingID;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public NutritionDataModel.Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(NutritionDataModel.Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public String toString() {
        return "CartSingleItem{" +
                "ShoppingID=" + ShoppingID +
                ", users=" + users +
                ", recipe=" + recipe +
                '}';
    }
}
