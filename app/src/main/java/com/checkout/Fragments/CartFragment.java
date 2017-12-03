package com.checkout.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkout.CameraPreview;
import com.checkout.Model.Card;
import com.checkout.Model.Item;
import com.checkout.Model.Items;
import com.checkout.Model.Purchase;
import com.checkout.Model.Purchases;
import com.checkout.R;

import java.util.ArrayList;
import java.util.Date;

public class CartFragment extends Fragment {

    private Items items;
    private Card card;

    private ListView cartItemsLV;
    private TextView emptyCartTextView;
    private ItemListAdapter ila;
    private Button checkoutButton;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = Items.getInstance();
        ila = new ItemListAdapter(getActivity(),
                R.layout.layout_list_cart, items.getItems());
        card = Card.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        cartItemsLV = (ListView) rootView.findViewById(R.id.cartLV);
        emptyCartTextView = (TextView) rootView.findViewById(R.id.emptyCartTV);
        checkoutButton = (Button) rootView.findViewById(R.id.checkoutButton);

        cartItemsLV.setEmptyView(emptyCartTextView);
        cartItemsLV.setAdapter(ila);

        if(!items.isEmpty()) {
            checkoutButton.setVisibility(View.VISIBLE);
        }

        initListeners();
        return rootView;
    }

    private void initListeners() {
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!card.isCardSet()) {
                    Toast.makeText(getActivity(), getString(R.string.noCard),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                double total = 0.0;
                int totalItems = 0;
                for(int i = 0; i < items.size(); ++i) {
                    Item curr = items.getItem(i);
                    totalItems += curr.getQuantity();
                    total += curr.getQuantity() * curr.getPrice();
                }
                Purchase purchase = new Purchase(new Date(), totalItems, total);
                Purchases.getInstance().addPurchase(purchase);
                Toast.makeText(getActivity(), "Purchase success", Toast.LENGTH_LONG).show();
                ila.removeAll();
            }
        });
    }
}

class ItemListAdapter extends ArrayAdapter<Item> {

    private Context c;
    private Items items;

    public ItemListAdapter(Context c, int resId, ArrayList<Item> items) {
        super(c, resId, items);
        this.c = c;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.layout_list_cart, null);
        }
        items = Items.getInstance();
        Item i = items.getItem(position);

        ImageView itemIV = (ImageView) convertView.findViewById(R.id.itemIV);
        TextView itemTitleTV = (TextView) convertView.findViewById(R.id.itemTitleTV);
        TextView itemPriceTV = (TextView) convertView.findViewById(R.id.itemPriceTV);
        TextView itemQuantityTV = (TextView) convertView.findViewById(R.id.itemQuantityTV);
        ImageButton removeItemIB = (ImageButton) convertView.findViewById(R.id.removeItemIB);

        itemIV.setImageBitmap(i.getImage());
        itemTitleTV.setText(i.getName().substring(0,
                Math.min(i.getName().length(), 14)) + "...");
        String priceS = String.format("$%.2f", i.getPrice());
        itemPriceTV.setText(priceS);
        itemQuantityTV.setText("x" + i.getQuantity());

        removeItemIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });

        return convertView;
    }

    public void removeAll() {
        while(!items.isEmpty()) {
            deleteItem(items.size() - 1);
        }
    }

    private void deleteItem(int position) {
        remove(items.getItem(position));
    }
}