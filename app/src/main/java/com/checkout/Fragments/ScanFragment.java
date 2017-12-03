package com.checkout.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.checkout.CameraPreview;
import com.checkout.Model.Card;
import com.checkout.Model.Item;
import com.checkout.Model.Items;
import com.checkout.R;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.google.android.gms.wearable.DataMap.TAG;

public class ScanFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Camera c;
    private Item focusedItem;
    private int quantity;
    private RequestQueue requestQueue;
    private Items cart;

    private FloatingActionButton cameraFAB, plusButton, minusButton;
    private Button addToCartButton;
    private TextView quantityTV, itemTitleTV, itemPriceTV;
    private ImageView itemIV;

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
        quantity = 0;
        focusedItem = null;
        requestQueue = Volley.newRequestQueue(getActivity());
        cart = Items.getInstance();

        // Initialize card to saved if present
        Card card = Card.getInstance();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = "";
        String number = "";
        String exp = "";
        String cvv = "";
        name = sharedPref.getString("name", name);
        if(!name.isEmpty()) {
            Log.d(TAG, "Found saved card");
            number = sharedPref.getString("num", number);
            exp = sharedPref.getString("exp", exp);
            cvv = sharedPref.getString("cvv", cvv);
            card.editCard(number, name, exp, cvv);
        } else {
            Log.d(TAG, "No saved card found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        cameraFAB = (FloatingActionButton) rootView.findViewById(R.id.captureButton);
        plusButton = (FloatingActionButton) rootView.findViewById(R.id.plusButton);
        minusButton = (FloatingActionButton) rootView.findViewById(R.id.minusButton);
        addToCartButton = (Button) rootView.findViewById(R.id.addToCartButton);
        quantityTV = (TextView) rootView.findViewById(R.id.quantityEditTV);
        itemIV = (ImageView) rootView.findViewById(R.id.itemIV);
        itemTitleTV = (TextView) rootView.findViewById(R.id.itemTitleTV);
        itemPriceTV = (TextView) rootView.findViewById(R.id.itemPriceTV);

        //init camera
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            c = null;
            try {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    c = Camera.open();
                }else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.CAMERA}, 0);
                    return rootView;
                }

                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
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
        initListeners();
        return rootView;
    }

    private void initListeners() {
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                updateQuantity();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity > 0) {
                    quantity--;
                    updateQuantity();
                }
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusedItem.setQuantity(quantity);
                cart.addItem(focusedItem);
                Toast.makeText(getActivity(), "Added item(s) to cart.", Toast.LENGTH_LONG).show();
                quantity = 0;
                focusedItem = null;
                updateQuantity();
                updateFocusedItem();
            }
        });

        cameraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.takePicture(null, null, mPicture);
            }
        });
    }

    private void updateQuantity() {
        quantityTV.setText("" + quantity);
        if(quantity == 0 || focusedItem == null) {
            addToCartButton.setVisibility(View.INVISIBLE);
        } else {
            addToCartButton.setVisibility(View.VISIBLE);
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            Log.d(TAG, "got pic file: " + pictureFile.getAbsolutePath());
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
                return;
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
                return;
            }

            Bitmap scanBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            if (scanBitmap == null) {
                Log.d(TAG, "Bitmap creation failed");
                return;
            }

            BarcodeDetector detector =
                    new BarcodeDetector.Builder(getActivity().getApplicationContext())
                            .setBarcodeFormats(Barcode.UPC_A)
                            .build();
            if(!detector.isOperational()){
                Log.d(TAG, "Barcode detector not operational");
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(scanBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if(barcodes.size() == 0) {
                // No barcode detected
                Log.d(TAG, "No barcode detected");
                Toast.makeText(getActivity(), "No barcode detected", Toast.LENGTH_SHORT).show();
                return;
            }
            Barcode code = barcodes.valueAt(0);
            Log.d(TAG, "Detected barcode: " + code.rawValue);
            Toast.makeText(getActivity(), "Barcode detected", Toast.LENGTH_SHORT).show();
            //Request barcode info
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                            "https://api.upcitemdb.com/prod/trial/lookup?upc=" + code.rawValue,
                            null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d(TAG, "Received UPC details response: \n" + response.toString());
                                JSONArray items = response.getJSONArray("items");
                                JSONObject item = items.getJSONObject(0);
                                String title = item.getString("title");
                                JSONArray offers = item.getJSONArray("offers");
                                double price = 0;
                                if(offers.length() > 0) {
                                    JSONObject offer = offers.getJSONObject(0);
                                    price = offer.getDouble("price");
                                }
                                JSONArray images = item.getJSONArray("images");
                                String image = "";
                                if(images.length() > 0) {
                                    image = images.getString(0);
                                }
                                focusedItem = new Item(title, image, price, quantity);
                                updateFocusedItem();
                            } catch (JSONException e) {
                                Log.d(TAG, "Error reading JSON response");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Product lookup failed");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }
    };

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Checkout");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Checkout", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    //Updates to the most recently scanned item
    private void updateFocusedItem() {
        if(focusedItem == null) {
            itemTitleTV.setText(R.string.defaultItem);
            itemPriceTV.setText("$0.00");
            itemIV.setImageDrawable(null);
            addToCartButton.setVisibility(View.INVISIBLE);
        } else {
            itemTitleTV.setText(focusedItem.getName().substring(0,
                    Math.min(focusedItem.getName().length(), 14)) + "...");
            if(focusedItem.getPrice() > 0) {
                String priceS = String.format("$%.2f", focusedItem.getPrice());
                itemPriceTV.setText(priceS);
            } else {
                itemPriceTV.setText("No offers found");
            }
            if(focusedItem.hasImage()) {
                Log.d(TAG, "Getting image at URL: " + focusedItem.getImageURL());
                new DownloadImageTask(itemIV).execute(focusedItem.getImageURL());
            }
        }
    }

    //Asynch image downloader for item images
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String[] urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            focusedItem.setImage(result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(c != null)
            c.release();
    }

}
