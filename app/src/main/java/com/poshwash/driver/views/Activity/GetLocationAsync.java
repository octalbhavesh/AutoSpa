package com.poshwash.driver.views.Activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;
    AppCompatActivity appCompatActivity;
    List<Address> addresses;
        public GetLocationAsync(AppCompatActivity appCompatActivity,double latitude, double longitude) {
            // TODO Auto-generated constructor stub

            x = latitude;
            y = longitude;
            this.appCompatActivity = appCompatActivity;
        }

        @Override
        protected void onPreExecute() {
            Log.e(" Getting location ","");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Geocoder geocoder = new Geocoder(appCompatActivity, Locale.ENGLISH);
                 addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();

                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");

                } else {
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.e("onPostExecute"," "+addresses.get(0).getAddressLine(0)+ addresses.get(0).getAddressLine(1) );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }


