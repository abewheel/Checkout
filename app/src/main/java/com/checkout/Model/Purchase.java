package com.checkout.Model;

import java.util.Date;

/**
 * Created by Abe on 11/15/2017.
 */

public class Purchase {

    private Date d;
    private int numItems;
    private double total;

    public Purchase(Date d, int numItems, double total) {
        this.d = d;
        this.numItems = numItems;
        this.total = total;
    }

    public Date getDate() {
        return d;
    }

    public int getNumItems() {
        return numItems;
    }

    public double getTotal() {
        return total;
    }
}
