package com.poshwash.driver.views.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.databinding.PendingrequestFragmentBinding;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.Adapter.PendingRequestAdapter;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.DividerItemDecoration;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingRequestFragment extends Fragment implements View.OnClickListener, PendingRequestAdapter.OnclickListner {

    Context context;
    MyProgressDialog myProgressDialog;
    JSONArray Pending_jsonArray = new JSONArray();
    PendingRequestAdapter pendingRequestAdapter;
    PendingRequestAdapter.OnclickListner onclickListner;
    PendingrequestFragmentBinding binding;

    public static PendingRequestFragment newInstance() {
        return new PendingRequestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pendingrequest_fragment, container, false);
        context = getActivity();
        onclickListner = this;
        initView();
        checkRequestPermission();

        if (AutoSpaApplication.mLastLocation != null) {
            Util.callWebserviceUpdateLocation(String.valueOf(AutoSpaApplication.mLastLocation.getLatitude()), String.valueOf(AutoSpaApplication.mLastLocation.getLongitude()), context);
        }
        if (!TextUtils.isEmpty(MySharedPreferences.getNotification(context))) {
            getNoticationData(MySharedPreferences.getNotification(context));
        }

        return binding.getRoot();
    }

    private void initView() {
        myProgressDialog = new MyProgressDialog(context);
        myProgressDialog.setCancelable(false);
        binding.menuIcon.setOnClickListener(this);

        pendingRequestAdapter = new PendingRequestAdapter(context, Pending_jsonArray, PendingRequestFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recylerview.setLayoutManager(mLayoutManager);
        binding.recylerview.setItemAnimator(new DefaultItemAnimator());
        binding.recylerview.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        binding.recylerview.setAdapter(pendingRequestAdapter);
        pendingRequestAdapter.SetonclickListenr(onclickListner);

        updateStatus();

    }

    public void updateStatus() {
        if (MySharedPreferences.getSP_status(context).compareTo("1") == 0) {
            binding.statusIv.setImageResource(R.drawable.gps);
        } else {
            binding.statusIv.setImageResource(R.drawable.gps_red);
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
            getLastLocation();
            // GoNext();
        }
    }

    @Override
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


    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {

        AutoSpaApplication.mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            AutoSpaApplication.mLastLocation = task.getResult();

                        }
                    }
                });
    }


    public void getNoticationData(String notification_data) {
        try {
            JSONObject jsonObject = new JSONObject(notification_data);
            int notification_type = 0;
            notification_type = Integer.parseInt(jsonObject.getString("type"));
            MySharedPreferences.setNotification(context, "");

            if (notification_type == 2)// Request For Accept PN
            {
                callWebServicePendingRequest();
            }
            if (notification_type == 4)  // auto booking cancel PN
            {
                callWebServicePendingRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getProfile();

        int index = 0;
        while (index < Pending_jsonArray.length()) {
            Pending_jsonArray.remove(index);
            pendingRequestAdapter.notifyItemRemoved(index);
            index++;
        }
        pendingRequestAdapter.notifyDataSetChanged();

        callWebServicePendingRequest();
        if (MySharedPreferences.getSP_status(context).compareTo("1") == 0) {
            binding.statusIv.setImageResource(R.drawable.gps);
        } else {
            binding.statusIv.setImageResource(R.drawable.gps_red);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_icon:
                ((MainActivity) getActivity()).OpenDrawer();
                break;
        }
    }

    @Override
    public void acceptJob(String bookingId, String BookingDriverId) {
        AcceptJobWebService(bookingId, BookingDriverId);
    }

    /*APIs*/
    private void callWebServicePendingRequest() {

        if (Util.hasInternet(getActivity())) {
            Util.showShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
            Call<ResponseBody> call = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("language", "eng");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.pendingRequestSp(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                        try {
                            JSONObject jsonObject1 = new JSONObject(Util.convertRetrofitResponce(response));
                            if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                                Pending_jsonArray = jsonObject1.getJSONObject("response").getJSONArray("data");

                                if (Pending_jsonArray != null && Pending_jsonArray.length() > 0) {

                                    binding.recylerview.setVisibility(View.VISIBLE);
                                    binding.noDataTv.setVisibility(View.GONE);

                                    pendingRequestAdapter = new PendingRequestAdapter(context, Pending_jsonArray, PendingRequestFragment.this);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                    binding.recylerview.setLayoutManager(mLayoutManager);
                                    binding.recylerview.setItemAnimator(new DefaultItemAnimator());
                                    binding.recylerview.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                                    binding.recylerview.setAdapter(pendingRequestAdapter);
                                    pendingRequestAdapter.SetonclickListenr(onclickListner);
                                } else {
                                    binding.recylerview.setVisibility(View.GONE);
                                    binding.noDataTv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.recylerview.setVisibility(View.GONE);
                                binding.noDataTv.setVisibility(View.VISIBLE);
                                binding.noDataTv.setText(jsonObject1.getJSONObject("response").getString("message"));
                                Util.errorToast(context, jsonObject1.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                    }
                });
            } catch (Exception e) {
                Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                Log.e("", e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }

    private void AcceptJobWebService(final String booking_id, String BookingDriverId) {

        if (Util.hasInternet(getActivity())) {
            myProgressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                String lat = "", lng = "";
                if (AutoSpaApplication.mLastLocation != null) {
                    lat = String.valueOf(AutoSpaApplication.mLastLocation.getLatitude());
                    lng = String.valueOf(AutoSpaApplication.mLastLocation.getLongitude());
                } else {
                    lat = MySharedPreferences.getLatitude(context);
                    lng = MySharedPreferences.getLongitude(context);
                }

                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("booking_driver_id", BookingDriverId);
                jsonObject.put("booking_id", booking_id);
                jsonObject.put("sp_lat", lat);
                jsonObject.put("sp_long", lng);

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.accept_booking(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(myProgressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                            Log.e("tg", "response from server = " + mJsonObj.toString());

                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                JSONObject jsonObject1 = mJsonObj.getJSONObject("response").getJSONObject("data");
                                ((MainActivity) getActivity()).displayView(AutoSpaConstant.WASHPROCESSFRAGMENT, jsonObject1, null);
                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Util.dismissPrograssDialog(myProgressDialog);
                        t.printStackTrace();
                    }
                });

            } catch (Exception e) {
                Util.dismissPrograssDialog(myProgressDialog);
                e.printStackTrace();
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }

    private void getProfile() {
        if (Util.hasInternet(getActivity())) {
//        myProgressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("language", MySharedPreferences.getLanguage(context));

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.getProfile(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Util.dismissPrograssDialog(myProgressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(response.body().string());
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1 = mJsonObj.getJSONObject("response").getJSONObject("data");

                                MySharedPreferences.setFirstName(context, jsonObject1.getString("first_name"));
                                MySharedPreferences.setLastName(context, jsonObject1.getString("last_name"));
                                MySharedPreferences.setPhoneNumber(context, jsonObject1.getString("mobile"));
                                MySharedPreferences.setProfileImage(context, jsonObject1.getString("img"));
                                MySharedPreferences.setRating(context, jsonObject1.getString("rating"));
                                MySharedPreferences.setNotificationSetting(context, jsonObject1.getString("notification"));
                                MySharedPreferences.setNotificationCount(context, jsonObject1.getString("unread_badge_count"));
                                ((MainActivity) getActivity()).SetProfile();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
//                    Util.dismissPrograssDialog(myProgressDialog);

                    }
                });
            } catch (Exception e) {
//            Util.dismissPrograssDialog(myProgressDialog);
            }
        }
    }
}
