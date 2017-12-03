package com.checkout.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.checkout.R;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by Abe on 12/2/2017.
 */

public class Card {

    private static Card ourInstance;

    private static String number, name, exp, cvv;

    public static Card getInstance() {
        if(ourInstance == null) {
            ourInstance = new Card();
            number = "";
            name = "";
            exp = "";
            cvv = "";
        }
        return ourInstance;
    }

    private Card() {
    }

    public void editCard(String number, String name, String exp, String cvv) {
        this.number = number;
        this.name = name;
        this.exp = exp;
        this.cvv = cvv;
    }

    public String getNumber() {
        return number;
    }

    public String getExp() {
        return exp;
    }

    public String getName() {
        return name;
    }

    public String getCvv() {
        return cvv;
    }

    public boolean isCardSet() {
        return !name.isEmpty();
    }
}

