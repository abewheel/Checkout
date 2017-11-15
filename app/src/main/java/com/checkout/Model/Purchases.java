package com.checkout.Model;

import java.util.ArrayList;

/**
 * Created by Abe on 11/15/2017.
 */

public class Purchases {
    private static final Purchases ourInstance = new Purchases();
    private static ArrayList<Purchase> purchases;

    public static Purchases getInstance() {
        if(purchases == null) {
            purchases = new ArrayList<Purchase>();
        }
        return ourInstance;
    }

    private Purchases() {
    }

    public void addPurchase(Purchase i) {
        purchases.add(i);
    }

    public Purchase getPurchase(int i) {
        return purchases.get(i);
    }

    public int size() {
        return purchases.size();
    }
}
