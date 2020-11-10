package com.poshwash.driver.views.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.poshwash.driver.databinding.MapviewActivityBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poshwash.driver.R;

public class MapViewActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    Context context;
    String booking_lat = "", booking_lng = "";
    MapviewActivityBinding binding;
    GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.mapview_activity);
        context = this;
        binding.toggleIcon.setOnClickListener(this);

        booking_lat = getIntent().getStringExtra("booking_lat");
        booking_lng = getIntent().getStringExtra("booking_lng");

        checkRequestPermission();


    }

    private void checkRequestPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            }, 1);
        } else {

            initilizeMap();
            // GoNext();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        int i = 0;
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                checkRequestPermission();

            } else {
            }
        }
    }

    private void initilizeMap() {
        try {
            //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);

        if (googleMap != null) {

            try {

                LatLng sydney = new LatLng(Double.parseDouble(booking_lat), Double.parseDouble(booking_lng));
                googleMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title(""));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15), 1000, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.toggleIcon) {
            finish();
        }
    }
}
