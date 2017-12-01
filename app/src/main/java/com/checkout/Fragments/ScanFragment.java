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
import android.widget.FrameLayout;

import com.checkout.CameraPreview;
import com.checkout.R;

public class ScanFragment extends Fragment {

    public ScanFragment() {
        // Required empty public constructor
    }

    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.captureButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //init camera
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Camera c = null;
            try {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    c = Camera.open();
                }else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.CAMERA}, 0);
                }

                // Create our Preview view and set it as the content of our activity.
                CameraPreview mPreview = new CameraPreview(getActivity(), c);
                FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
                preview.addView(mPreview);
            }
            catch (Exception e){
                Log.e("ScanActivity", "Cannot open camera");
            }
        } else {
            Log.e("ScanActivity", "Cannot access camera");
        }
        return rootView;
    }
}
