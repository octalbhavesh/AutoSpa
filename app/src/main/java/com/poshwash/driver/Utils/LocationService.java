package com.poshwash.driver.Utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.poshwash.driver.Delegate.ServiceCallbacks;

import static com.poshwash.driver.Constant.AutoSpaConstant.CUREENTLOCATION;

/**
 * Created by Anand on 16/09/15.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationService";
    public final static String LATEST_LOC = "LATEST_LOC";
    public static Location LATEST_LOC_OBJ = null;
    private ServiceCallbacks serviceCallbacks;


    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 3;
    //displacment is given priority over
    //intervals
    private static final long MIN_DISPLACMENT = 5;
    //min distance to mark a destination as arriwed in meters
    private static final long MIN_DIST_FOR_ARRIWED = 50;
    private Context mContext;
    private LocationRequest locationRequest;
    public static GoogleApiClient googleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi;
    public static final String
            BROADCAST_ACTION = " com.octalsoftaware.okwasch";


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        /*mContext = this;
        getLocation();*/
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mContext = this;
        getLocation();

        // showPrograss();

    }



   /* @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
      //  Toast.makeText(activityContext, "ondestroy", Toast.LENGTH_SHORT).show();
        try {
            if (googleApiClient != null) {
                googleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    private void getLocation() {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);
//            locationRequest.setSmallestDisplacement(MIN_DISPLACMENT);
            fusedLocationProviderApi = LocationServices.FusedLocationApi;
        }
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "Location onConnected ..............: ");
//  Location location = fusedLocationProviderApi.getLastLocation(googleApiClient);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }


    @Override
    public void onLocationChanged(Location location) {
        // Log.d(TAG, "Location update started ..............: ");
        //  Log.d(TAG,location.getLatitude()+" "+location.getLongitude());
        LocationService.LATEST_LOC_OBJ = location;

        if (MySharedPreferences.getUserId(mContext).compareTo("") != 0) {

            Util.sendUpdatedLocationMessage(getApplicationContext(), location);
            Util.callWebserviceUpdateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), getApplicationContext());

        }
        if (!CUREENTLOCATION) {
            Intent intent = new Intent();
            intent.setAction(LATEST_LOC);
            sendBroadcast(intent);
        }
        //   stopService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }


}