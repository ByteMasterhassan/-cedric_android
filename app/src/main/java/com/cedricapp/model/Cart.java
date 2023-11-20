package com.cedricapp.model;

import java.util.ArrayList;

public class Cart {
    ArrayList <CartItem> cartItems;


    public Cart(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Cart() {
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartItems=" + cartItems +
                '}';
    }
}
