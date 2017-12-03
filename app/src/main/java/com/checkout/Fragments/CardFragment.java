package com.checkout.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.checkout.CameraPreview;
import com.checkout.Model.Card;
import com.checkout.R;

import org.w3c.dom.Text;

public class CardFragment extends Fragment {

    private Card card;

    private EditText numberET, nameET, expirationET, cvvET;
    private TextView activeCardTV;
    private Button updateCardButton;

    public CardFragment() {
        // Required empty public constructor
    }

    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        card = Card.getInstance();

        numberET = (EditText) rootView.findViewById(R.id.cardNumberET);
        nameET = (EditText) rootView.findViewById(R.id.nameOnCardET);
        expirationET = (EditText) rootView.findViewById(R.id.expirationDateET);
        cvvET = (EditText) rootView.findViewById(R.id.cvvET);
        activeCardTV = (TextView) rootView.findViewById(R.id.activeCardTV);
        updateCardButton = (Button) rootView.findViewById(R.id.updateCardButton);

        updateCardTV();

        initListeners();
        return rootView;
    }

    private void initListeners() {
        updateCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberET.getText().toString();
                String name = nameET.getText().toString();
                String exp = expirationET.getText().toString();
                String cvv = cvvET.getText().toString();
                card.editCard(number, name, exp, cvv);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("num", number);
                editor.putString("exp", exp);
                editor.putString("name", name);
                editor.putString("cvv", cvv);
                editor.commit();

                Toast.makeText(getActivity(), getString(R.string.updatedCard), Toast.LENGTH_LONG).show();
                updateCardTV();
            }
        });
    }

    private void updateCardTV() {
        if(!card.isCardSet()) {
            activeCardTV.setText(getString(R.string.activeCard) + " " +
                    getString(R.string.noActiveCard));
        } else {
            String number = card.getNumber();
            activeCardTV.setText(getString(R.string.activeCard) + " **** **** **** "
                    + number.substring(number.length() - 4));
        }
    }
}
