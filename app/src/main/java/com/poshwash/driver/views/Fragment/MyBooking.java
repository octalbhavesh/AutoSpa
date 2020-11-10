package com.poshwash.driver.views.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.poshwash.driver.databinding.MyBookingNewBinding;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.Adapter.ViewPagerAdapter;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBooking extends Fragment implements View.OnClickListener {
    static Context context;
    private ArrayList<MyBookingChildModal> bookinglist;
    //    private MyProgressDialog myProgressDialog;
    private final String TAG = "MyBooking.java";
    private ViewPagerAdapter adapter;
    DisplayImageOptions options;
    private FragmentManager mFragmentManager;
    MyBookingNewBinding binding;

    public static MyBooking newInstance(Context contex) {
        MyBooking f = new MyBooking();
        context = contex;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_booking_new, container, false);

        context = getActivity();
        initView();
        callWebServicesMyBooking();
        return binding.getRoot();
    }

    private void initView() {

        options = Util.displayOption(getActivity());

//        myProgressDialog = new MyProgressDialog(context);
//        myProgressDialog.setCancelable(false);
        bookinglist = new ArrayList<>();
        mFragmentManager = getChildFragmentManager();
        setUpViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        Util.changeTabsFont(binding.tabLayout, getActivity());
        ((MainActivity) getActivity()).setMyBookingFragment();

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getCurrentFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setData();
    }

    private void setData() {

        if (!MySharedPreferences.getProfileImage(context).equals(""))
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + MySharedPreferences.getProfileImage(context), binding.userImage, options);
        else
            binding.userImage.setImageResource(R.drawable.no_image);
        binding.userNameTv.setText(MySharedPreferences.getFirstName(context) + " " + MySharedPreferences.getLastName(context));

        binding.ratingBar.setRating(Float.parseFloat(MySharedPreferences.getRating(context)));
        binding.userRatingTv.setText(Float.parseFloat(MySharedPreferences.getRating(context)) + "/5.0");
        binding.userBonusTv.setText("");
    }

    private void setUpViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(mFragmentManager);
        adapter.addFragment(new InprogressFragment(), getString(R.string.inprogress));
        adapter.addFragment(new CompletedFragment(), getString(R.string.completed));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        getProfile();
        if (AutoSpaApplication.mLastLocation != null) {
            Util.callWebserviceUpdateLocation(String.valueOf(AutoSpaApplication.mLastLocation.getLatitude()), String.valueOf(AutoSpaApplication.mLastLocation.getLongitude()), context);
        }
    }

    private void prepareData(JSONArray jsonArray) {

        bookinglist.clear();

        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                MyBookingChildModal myBookingChildModal = new MyBookingChildModal();
                String start_wash_time = "";
                if (jsonObject.has("start_wash_time"))
                    start_wash_time = jsonObject.getString("start_wash_time");
                myBookingChildModal.setStart_wash_time(start_wash_time);
                myBookingChildModal.setId(jsonObject.optString("booking_id"));
                myBookingChildModal.setBooking_Sp_id(jsonObject.getString("booking_driver_id"));
                myBookingChildModal.setOwner_name(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                myBookingChildModal.setRating_count(jsonObject.getString("user_rating"));
                myBookingChildModal.setPrice(jsonObject.getString("booking_amount"));
                myBookingChildModal.setBooking_type(jsonObject.getString("booking_type"));
                myBookingChildModal.setStatus(jsonObject.getString("status"));
                myBookingChildModal.setBooking_lat(jsonObject.getString("booking_lat"));
                myBookingChildModal.setBooking_long(jsonObject.getString("booking_long"));
                myBookingChildModal.setUser_lat(jsonObject.getString("user_lat"));
                myBookingChildModal.setUser_lon(jsonObject.getString("user_lon"));
                myBookingChildModal.setUser_phone(jsonObject.getString("mobile"));
                myBookingChildModal.setProfile_image_url(jsonObject.getString("img"));
                myBookingChildModal.setBooking_date(jsonObject.getString("booking_date"));
                myBookingChildModal.setBooking_time(jsonObject.getString("booking_time"));
                myBookingChildModal.setIs_rating(jsonObject.getBoolean("is_rating"));
                myBookingChildModal.setBooking_transaction(jsonObject.getString("booking_transaction"));
                myBookingChildModal.setVehicle_type_id(jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").optString("vehicle_type_id"));

                String vehicleName = "";
                if (jsonObject.getJSONArray("BookingUserVehicle").length() > 0) {
                    String vehiclePicture = "";
                    if (jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").length() > 0) {
                        vehiclePicture = jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("vehicle_picture");
                    }
                    myBookingChildModal.setImage(vehiclePicture);
                  /*  if (jsonObject.getJSONArray("BookingUserVehicle").length() == 1) {
                        vehicleName = jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("model") + " (" + jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getJSONObject("VehicleType").getString("name") + ")";
                    } else {
                        vehicleName = jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("model") + " (" + jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getJSONObject("VehicleType").getString("name") + ") + " + ((jsonObject.getJSONArray("BookingUserVehicle").length()) - 1);
                    }*/


                    if (jsonObject.getJSONArray("BookingUserVehicle").length() == 1) {
                        String make_name = jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("make_name");
                        String model_name = jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("model_name");
                        vehicleName = make_name + " - " + model_name + " (" + jsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getJSONObject("VehicleType").getString("name") + ")";
                    }

                }/* else if (jsonObject.getJSONArray("BookingVehicleType").length() > 0) {
                    if (jsonObject.getJSONArray("BookingVehicleType").length() == 1) {
                        vehicleName = jsonObject.getJSONArray("BookingVehicleType").getJSONObject(0).getJSONObject("VehicleType").getString("name");
                    } else {
                        vehicleName = jsonObject.getJSONArray("BookingVehicleType").getJSONObject(0).getJSONObject("VehicleType").getString("name") + " + " + ((jsonObject.getJSONArray("BookingVehicleType").length()) - 1);
                    }
                }*/
                myBookingChildModal.setCar_name(vehicleName);
                bookinglist.add(myBookingChildModal);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        getCurrentFragment();
    }

    private void getCurrentFragment() {

        Fragment mCurrentFragment = null;
        if (binding.viewPager != null) {
            mCurrentFragment = adapter.getRegisteredFragment(binding.viewPager.getCurrentItem());
            if (mCurrentFragment instanceof InprogressFragment) {
                ((InprogressFragment) mCurrentFragment).getListData(bookinglist);
            } else if (mCurrentFragment instanceof CompletedFragment) {
                ((CompletedFragment) mCurrentFragment).getListData(bookinglist);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toggle_icon) {
            ((MainActivity) context).OpenDrawer();
        }
    }

    /*APIs*/
    private void getProfile() {

        if (Util.hasInternet(getActivity())) {
//        myProgressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("language", "eng");

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
                                setData();
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

    private void callWebServicesMyBooking() {

        if (Util.hasInternet(getActivity())) {
//        myProgressDialog.show();

            Util.showShimmerFrameLayout(binding.bookingShimmer.shimmerFrameLayout);

            Call<ResponseBody> call = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject.put("type", "2");
                jsonObject.put("language", "eng");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.my_bookings(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Util.dismissPrograssDialog(myProgressDialog);
                        Util.dismissShimmerFrameLayout(binding.bookingShimmer.shimmerFrameLayout);
                        binding.rlProfile.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body().string());
                            if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                                JSONArray jsonArray = jsonObject1.getJSONObject("response").getJSONArray("data");
                                prepareData(jsonArray);
                            } else {
                                Util.errorToast(getActivity(), jsonObject1.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Util.dismissPrograssDialog(myProgressDialog);
                        Util.dismissShimmerFrameLayout(binding.bookingShimmer.shimmerFrameLayout);
                    }
                });
            } catch (Exception e) {
//            Util.dismissPrograssDialog(myProgressDialog);
                Util.dismissShimmerFrameLayout(binding.bookingShimmer.shimmerFrameLayout);
                Log.e(TAG, e.toString());
            }
        } else {
            binding.rlProfile.setVisibility(View.VISIBLE);
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }
}