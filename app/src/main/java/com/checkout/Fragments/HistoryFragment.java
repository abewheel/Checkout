package com.checkout.Fragments;

import android.Manifest;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.checkout.CameraPreview;
import com.checkout.Model.Purchase;
import com.checkout.Model.Purchases;
import com.checkout.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private Purchases purchases;
    private HistoryListAdapter hla;

    private ListView purchasesLV;
    private TextView emptyHistoryTV;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        purchases = Purchases.getInstance();
        hla = new HistoryListAdapter(getActivity(),
                R.layout.layout_list_history, purchases.getPurchases());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        purchasesLV = (ListView) rootView.findViewById(R.id.historyLV);
        emptyHistoryTV = (TextView) rootView.findViewById(R.id.emptyHistoryTV);
        purchasesLV.setEmptyView(emptyHistoryTV);
        purchasesLV.setAdapter(hla);
        return rootView;
    }
}

class HistoryListAdapter extends ArrayAdapter<Purchase> {

    private Context c;
    private ArrayList<Purchase> purchases;

    public HistoryListAdapter(Context c, int resId, ArrayList<Purchase> purchases) {
        super(c, resId, purchases);
        this.c = c;
        this.purchases = purchases;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.layout_list_history, null);
        }
        Purchase p = purchases.get(position);

        TextView purchaseDateTV = (TextView) convertView.findViewById(R.id.purchaseDateTV);
        TextView purchaseItemsTV = (TextView) convertView.findViewById(R.id.purchaseItemsTV);
        TextView purchaseCostTV = (TextView) convertView.findViewById(R.id.purchaseCostTV);

        purchaseDateTV.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(p.getDate()));
        purchaseItemsTV.setText("" + p.getNumItems() + " items purchased");
        String priceS = String.format("Total: $%.2f", p.getTotal());
        purchaseCostTV.setText(priceS);

        return convertView;
    }
}
