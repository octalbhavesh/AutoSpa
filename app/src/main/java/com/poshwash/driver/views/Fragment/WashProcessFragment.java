package com.poshwash.driver.views.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.databinding.RatingDialogBinding;
import com.poshwash.driver.databinding.WashProcessFragmentBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.OnSwipeTouchListener;
import com.poshwash.driver.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WashProcessFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    Context context;
    public static Object object, object1;
    JSONObject jsonObject;
    public GoogleMap googleMap;
    MyBookingChildModal myBookingChildModal = new MyBookingChildModal();
    String bookingId = "";
    String bookingDriverId = "", vehicleTypeId = "";
    MyProgressDialog progressDialog;
    long mLastViewDetailTvClickTime = 0, mLastReachedButtonClickTime = 0;
    WashProcessFragmentBinding binding;
    private Runnable runnable = null;
    Handler handler = null;

    public static WashProcessFragment newInstance(Object obj, Object obj1) {

        WashProcessFragment f = new WashProcessFragment();
        object = obj;
        object1 = obj1;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.wash_process_fragment, container, false);
        context = getActivity();
        initView();
        checkRequestPermission();
        if (object != null) {
            jsonObject = (JSONObject) object;
            setData();
        }
        if (object1 != null) {
            myBookingChildModal = (MyBookingChildModal) object1;
            SetSPWash(myBookingChildModal);
        }
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        binding.viewDetailTv.setOnClickListener(this);
        binding.reachedButton.setOnClickListener(this);

        progressDialog = new MyProgressDialog(context);
        progressDialog.setCancelable(false);

        binding.startButton.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {

                binding.startButton.setVisibility(View.GONE);
                binding.completeButton.setVisibility(View.VISIBLE);
//                paymentButton.setVisibility(View.GONE);
                binding.reachedButton.setVisibility(View.GONE);
                binding.llTime.setVisibility(View.VISIBLE);
                callWebServiceStartBooking();
            }

        });

        binding.completeButton.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeLeft() {
                binding.completeButton.setVisibility(View.GONE);
                binding.reachedButton.setVisibility(View.GONE);
                washCompleteWebService();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_detail_tv:

                if (SystemClock.elapsedRealtime() - mLastViewDetailTvClickTime < 1000) {
                    return;
                }
                mLastViewDetailTvClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(context, BookingDetail.class);
                intent.putExtra("bookingId", bookingId);
                intent.putExtra("bookingDriverId", bookingDriverId);
                context.startActivity(intent);

                break;
            case R.id.reachedButton:

                if (SystemClock.elapsedRealtime() - mLastReachedButtonClickTime < 1000) {
                    return;
                }
                mLastReachedButtonClickTime = SystemClock.elapsedRealtime();

                callWebReachedDriver();

                break;
        }
    }

    private void checkRequestPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
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

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {

        googleMap = Map;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);

        if (googleMap != null) {
            try {
                LatLng sydney = new LatLng(Double.parseDouble(myBookingChildModal.getBooking_lat()), Double.parseDouble(myBookingChildModal.getBooking_long()));
                googleMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title(myBookingChildModal.getOwner_name()));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12), 1000, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setData() {
        try {

            if (jsonObject != null) {
                JSONObject bookingObj = jsonObject.getJSONObject("booking_data");
                JSONObject customerObj = jsonObject.getJSONArray("customer_data").getJSONObject(0);

                myBookingChildModal.setId(bookingObj.getString("booking_id"));
                myBookingChildModal.setBooking_Sp_id(bookingObj.getString("booking_driver_id"));
                myBookingChildModal.setOwner_name(customerObj.getString("first_name") + " " + customerObj.getString("last_name"));
                myBookingChildModal.setRating_count(bookingObj.getString("user_rating"));
                myBookingChildModal.setPrice(bookingObj.getString("charges"));
                myBookingChildModal.setBooking_type(bookingObj.getString("booking_type"));
                myBookingChildModal.setStatus(bookingObj.getString("status"));
                myBookingChildModal.setBooking_lat(bookingObj.getString("latitude"));
                myBookingChildModal.setBooking_long(bookingObj.getString("longitude"));
                myBookingChildModal.setUser_lat(customerObj.getString("latitude"));
                myBookingChildModal.setUser_lon(customerObj.getString("longitude"));
                myBookingChildModal.setUser_phone(customerObj.getString("mobile"));
                myBookingChildModal.setProfile_image_url(customerObj.getString("img"));
                myBookingChildModal.setBooking_date(bookingObj.getString("booking_date"));
                myBookingChildModal.setBooking_time(bookingObj.getString("booking_time"));
                myBookingChildModal.setVehicle_type_id(bookingObj.optString("vehicle_type_id"));

                SetSPWash(myBookingChildModal);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SetSPWash(MyBookingChildModal myBookingChildModal) {

        this.bookingId = myBookingChildModal.getId();
        this.bookingDriverId = myBookingChildModal.getBooking_Sp_id();
        this.vehicleTypeId = myBookingChildModal.getVehicle_type_id();


        binding.userRatingBar.setVisibility(View.VISIBLE);
        Log.e("rating : ", "rating : " + myBookingChildModal.getRating_count());
        binding.userRatingBar.setRating(Float.parseFloat(myBookingChildModal.getRating_count()));

        if (myBookingChildModal.getStatus().compareTo("9") == 0) // Reached
        {
            try {

                binding.customerNameTv.setText(myBookingChildModal.getOwner_name());
//                customerRatingBar.setVisibility(View.GONE);

//                if (!myBookingChildModal.getProfile_image_url().equals("")) {
                ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getProfile_image_url(), binding.customerPicIv, AutoSpaApplication.options);
//                } else {
//                    binding.customerPicIv.setImageResource(R.drawable.defalut_bg);
//                }

                if (googleMap != null) {
                    LatLng sydney = new LatLng(Double.parseDouble(myBookingChildModal.getBooking_lat()), Double.parseDouble(myBookingChildModal.getBooking_long()));
                    googleMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title(myBookingChildModal.getOwner_name()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12), 1000, null);
                }

                binding.reachedButton.setVisibility(View.GONE);
                binding.startButton.setVisibility(View.VISIBLE);
                binding.completeButton.setVisibility(View.GONE);
//                paymentButton.setVisibility(View.GONE);
                binding.llTime.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        }
        if (myBookingChildModal.getStatus().compareTo("1") == 0) // Start Wash
        {
            try {

                binding.customerNameTv.setText(myBookingChildModal.getOwner_name());
//                customerRatingBar.setVisibility(View.GONE);

                if (!myBookingChildModal.getProfile_image_url().equals("")) {
                    ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getProfile_image_url(), binding.customerPicIv, AutoSpaApplication.options);
                } else {
                    binding.customerPicIv.setImageResource(R.drawable.defalut_bg);
                }

                if (googleMap != null) {
                    LatLng sydney = new LatLng(Double.parseDouble(myBookingChildModal.getBooking_lat()), Double.parseDouble(myBookingChildModal.getBooking_long()));
                    googleMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title(myBookingChildModal.getOwner_name()));

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12), 1000, null);
                }

                binding.reachedButton.setVisibility(View.VISIBLE);
                binding.startButton.setVisibility(View.GONE);
                binding.completeButton.setVisibility(View.GONE);
//                paymentButton.setVisibility(View.GONE);
                binding.llTime.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        }
        if (myBookingChildModal.getStatus().compareTo("5") == 0) // Inprocess Wash
        {
            try {
                binding.customerNameTv.setText(myBookingChildModal.getOwner_name());
//                customerRatingBar.setVisibility(View.GONE);

                if (!myBookingChildModal.getProfile_image_url().equals("")) {
                    ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getProfile_image_url(), binding.customerPicIv, AutoSpaApplication.options);
                } else {
                    binding.customerPicIv.setImageResource(R.drawable.defalut_bg);
                }

                if (googleMap != null) {
                    LatLng sydney = new LatLng(Double.parseDouble(myBookingChildModal.getBooking_lat()), Double.parseDouble(myBookingChildModal.getBooking_long()));
                    googleMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title(myBookingChildModal.getOwner_name()));

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12), 1000, null);
                }
                binding.reachedButton.setVisibility(View.GONE);
                binding.startButton.setVisibility(View.GONE);
                binding.completeButton.setVisibility(View.VISIBLE);
//                paymentButton.setVisibility(View.GONE);
                binding.llTime.setVisibility(View.VISIBLE);
                String start_wash_time = myBookingChildModal.getStart_wash_time();
                Date date = null;
                try {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = dt.parse(start_wash_time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (date != null) {
                    ShowTimer(date.getTime());
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        }

        if (myBookingChildModal.getStatus().compareTo("2") == 0) // Inprocess Wash
        {
            try {
                binding.customerNameTv.setText(myBookingChildModal.getOwner_name());
//                customerRatingBar.setVisibility(View.GONE);

                if (!myBookingChildModal.getProfile_image_url().equals("")) {
                    ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getProfile_image_url(), binding.customerPicIv, AutoSpaApplication.options);
                } else {
                    binding.customerPicIv.setImageResource(R.drawable.defalut_bg);
                }
                if (googleMap != null) {
                    LatLng sydney = new LatLng(Double.parseDouble(myBookingChildModal.getBooking_lat()), Double.parseDouble(myBookingChildModal.getBooking_long()));
                    googleMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title(myBookingChildModal.getOwner_name()));

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12), 1000, null);
                }
                binding.startButton.setVisibility(View.GONE);
//                paymentButton.setVisibility(View.VISIBLE);
                binding.completeButton.setVisibility(View.GONE);
                binding.reachedButton.setVisibility(View.GONE);
                binding.llTime.setVisibility(View.GONE);
//                paymentButton.setText(getString(R.string.recive_payment) + " " + myBookingChildModal.getPrice());

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        }
    }

    public void ShowTimer(final long dateTime) {
        handler = new Handler();
        final int delay = 1000; //milliseconds
        runnable = new Runnable() {
            public void run() {
//                long score = System.currentTimeMillis() - (Long.parseLong(MySharedPreferences.getbookingTimer(context)));
                long score = System.currentTimeMillis() - dateTime;
                long hours = TimeUnit.MILLISECONDS.toHours(score);
                score -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(score);
                score -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(score);
                if (hours > 0) {
                    binding.timeTv.setText(pad(hours) + ":" + pad(minutes) + ":" + pad(seconds));
                    binding.timeTypeTv.setText(R.string.hour);
                } else {
                    binding.timeTv.setText(pad(minutes) + ":" + pad(seconds));
                    binding.timeTypeTv.setText(R.string.minute);
                }
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);
    }

    public String pad(long time) {
        NumberFormat f = new DecimalFormat("00");
        String timea = (f.format(time) + "");

        return timea;
    }

    public void getNoticationData(String notification_data) {
        try {
            JSONObject jsonObject = new JSONObject(notification_data);
            int notification_type = 0;
            notification_type = Integer.parseInt(jsonObject.getString("type"));
            MySharedPreferences.setNotification(context, "");

            if (notification_type == 2)// Request For Accept
            {
                //showAcceptDialog(notification_data);
            } else if (notification_type == 4) // Cancel your request
            {
                ((MainActivity) getActivity()).displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void showRatingDialog() {

        final Dialog dialog = new Dialog(context);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        final RatingDialogBinding alertInputBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.rating_dialog, null, false);
        dialog.setContentView(alertInputBinding.getRoot());

        ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getProfile_image_url(), alertInputBinding.imgUserImage, AutoSpaApplication.options);
        alertInputBinding.txtCustomerName.setText(myBookingChildModal.getOwner_name());

        alertInputBinding.txtCompleteRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int rating = (int) alertInputBinding.ratingBar.getRating();
                if (rating == 0) {
                    Util.errorToast(context, "Please select rating.");
                    return;
                }
                callCustomerRating("" + rating, dialog);

                bookingId = "";
                bookingDriverId = "";
                vehicleTypeId = "";
                myBookingChildModal = null;

            }
        });

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();

    }

    /*APIs*/
    private void callWebReachedDriver() {

        if (Util.hasInternet(getActivity())) {
            Call<ResponseBody> call = null;
            JSONObject jsonObject1 = null;
            progressDialog.show();

            try {
                jsonObject1 = new JSONObject();
                jsonObject1.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject1.put("booking_id", bookingId);
                jsonObject1.put("booking_driver_id", bookingDriverId);
                jsonObject1.put("language", "eng");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.start_moving_on(new ConvertJsonToMap().jsonToMap(jsonObject1));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject jsonObject11 = new JSONObject(response.body().string());
                            if (jsonObject11.getJSONObject("response").getBoolean("status")) {
                                binding.startButton.setVisibility(View.VISIBLE);
                                binding.reachedButton.setVisibility(View.GONE);
                                binding.completeButton.setVisibility(View.GONE);
                                binding.llTime.setVisibility(View.GONE);
                            } else {
                                Util.errorToast(getActivity(), jsonObject11.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }

    private void callWebServiceStartBooking() {

        if (Util.hasInternet(getActivity())) {
            Call<ResponseBody> call = null;
            JSONObject jsonObject1 = null;
            progressDialog.show();

            try {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                jsonObject1 = new JSONObject();
                jsonObject1.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject1.put("booking_id", bookingId);
                jsonObject1.put("booking_driver_id", bookingDriverId);
                jsonObject1.put("start_wash_time", currentDateandTime);
                jsonObject1.put("language", "eng");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.startWash(new ConvertJsonToMap().jsonToMap(jsonObject1));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject jsonObject11 = new JSONObject(response.body().string());
                            if (jsonObject11.getJSONObject("response").getBoolean("status")) {
                                long time = System.currentTimeMillis();

                                Log.e("time : ", "time : " + time);

                                MySharedPreferences.setbookingTimer(context, "" + time);
                                binding.startButton.setVisibility(View.GONE);
                                binding.reachedButton.setVisibility(View.GONE);
                                binding.completeButton.setVisibility(View.VISIBLE);
//                            paymentButton.setVisibility(View.GONE);
                                binding.llTime.setVisibility(View.VISIBLE);
                                ShowTimer(System.currentTimeMillis());
                            } else {
                                Util.errorToast(getActivity(), jsonObject11.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }

    private void washCompleteWebService() {

        if (Util.hasInternet(getActivity())) {
            String wash_complete_duration = binding.timeTv.getText().toString();

            progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("booking_id", bookingId);
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("complete_wash_duration", wash_complete_duration);
                jsonObject.put("booking_driver_id", bookingDriverId);
                jsonObject.put("vehicle_type_id", vehicleTypeId);
                jsonObject.put("language", "eng");
                Log.e("req : ", "req : " + jsonObject);
                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.complete_wash(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(response.body().string());
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                MySharedPreferences.setbookingTimer(context, "0");

                                if (handler != null && runnable != null) {
                                    handler.removeCallbacks(runnable);
                                    handler = null;
                                    runnable = null;
                                }

                                showRatingDialog();

                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }

                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Util.dismissPrograssDialog(progressDialog);
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Util.dismissPrograssDialog(progressDialog);
                Log.e("error", e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }

    private void callCustomerRating(String rating, final Dialog dialog) {

        if (Util.hasInternet(getActivity())) {

            progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("booking_driver_id", bookingDriverId);
                jsonObject.put("booking_id", bookingId);
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("review", "");
                jsonObject.put("rating", rating);
                jsonObject.put("language", "eng");
                Log.e("req : ", "req : " + jsonObject);

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.customer_rating(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(response.body().string());
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }

                                Util.successToast(getActivity(), mJsonObj.getJSONObject("response").getString("message"));
                                ((MainActivity) getActivity()).displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);

                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }

                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Util.dismissPrograssDialog(progressDialog);
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Util.dismissPrograssDialog(progressDialog);
                Log.e("error", e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }
}