package com.checkout.Model;

import android.graphics.Bitmap;

/**
 * Created by Abe on 11/15/2017.
 */

public class Item {

    private String name, imageURL;
    private double price;
    private int quantity;
    private Bitmap image;

    public Item(String name, String imageURL, double price, int quantity) {
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.quantity = quantity;
    }

    public boolean hasImage() {
        return !imageURL.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Bitmap getImage() {
        return image;
    }
}
