package com.checkout.Model;

import java.util.ArrayList;

/**
 * Created by Abe on 11/15/2017.
 */

public class Items {
    private static final Items ourInstance = new Items();
    private static ArrayList<Item> items;

    public static Items getInstance() {
        if(items == null) {
            items = new ArrayList<Item>();
        }
        return ourInstance;
    }

    private Items() {
    }

    public void addItem(Item i) {
        items.add(i);
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void removeItem(int i) {
        items.remove(i);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
