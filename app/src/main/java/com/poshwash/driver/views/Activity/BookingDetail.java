package com.poshwash.driver.views.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Adapter.GetfarePromosAdapter;
import com.poshwash.driver.Adapter.GetfareReviewAdapter;
import com.poshwash.driver.Beans.BeanReviewData;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.BookingDetailBinding;
import com.poshwash.driver.databinding.GetfareLayoutBinding;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetail extends AppCompatActivity implements View.OnClickListener {

    Context context;
    JSONObject dataJsonObject;
    DisplayImageOptions options;
    String bookingId = "";
    String bookingDriverId = "";
    BookingDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.booking_detail);
        context = this;
        initView();

        bookingId = getIntent().getStringExtra("bookingId");
        bookingDriverId = getIntent().getStringExtra("bookingDriverId");

        callWebServiceBookingDetail();

    }

    private void initView() {
        options = Util.displayOption(this);
        clickListner();
    }

    private void clickListner() {

        binding.backIcon.setOnClickListener(this);
        binding.getFareTv.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        try {
            binding.dateTimeTv.setText(Util.convertUtcTimeToLocal(dataJsonObject.getString("booking_date") + " " + dataJsonObject.getString("booking_time"), false, context));

            binding.userNameTv.setText(dataJsonObject.getString("first_name") + " " + dataJsonObject.getString("last_name"));
            binding.locationTv.setText(dataJsonObject.getString("booking_address"));
            binding.userRatingBar.setRating(Float.parseFloat(dataJsonObject.getString("user_rating")));

            if (dataJsonObject.getString("status").compareTo("0") == 0) {
                binding.statusTv.setText(getString(R.string.initiated));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.pending_color));
            } else if (dataJsonObject.getString("status").compareTo("1") == 0) {
                binding.statusTv.setText(getString(R.string.accepted));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.accepted_color));
            } else if (dataJsonObject.getString("status").compareTo("2") == 0) {
                binding.statusTv.setText(getString(R.string.completed));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.complete_color));
                binding.txnLl.setVisibility(View.VISIBLE);
                binding.jobDurationLl.setVisibility(View.VISIBLE);

                binding.jobDurationTv.setText(dataJsonObject.getString("complete_wash_duration") + " " + getString(R.string.minute));

                if (!TextUtils.isEmpty(dataJsonObject.getString("transaction_id"))) {
                    binding.transactionIdTv.setText(dataJsonObject.getString("transaction_id"));
                } else {
                    binding.txnLl.setVisibility(View.GONE);
                    binding.transactionIdTv.setText(dataJsonObject.getString("transaction_id"));
                }

            } else if (dataJsonObject.getString("status").compareTo("3") == 0) {
                binding.statusTv.setText(getString(R.string.cancelled));
                binding.cancelTimeTv.setText(Util.ConvertDateTimeZone(dataJsonObject.getString("booking_cancel_time")));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.cancel_color));
            } else if (dataJsonObject.getString("status").compareTo("4") == 0) {
                binding.statusTv.setText(getString(R.string.refunded));
            } else if (dataJsonObject.getString("status").compareTo("9") == 0) {
                binding.statusTv.setText(getString(R.string.reached));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.reached_color));
            } else if (dataJsonObject.getString("status").compareTo("5") == 0) {
                binding.statusTv.setText(getString(R.string.startwash));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.started_color));
            } else if (dataJsonObject.getString("status").compareTo("7") == 0) {
                binding.statusTv.setText(getString(R.string.cancel_by_admin));
                binding.cancelTimeTv.setText(Util.ConvertDateTimeZone(dataJsonObject.getString("booking_cancel_time")));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.cancel_color));
                binding.cancelLl.setVisibility(View.VISIBLE);
                binding.reasonTv.setText(dataJsonObject.getString("cancel_reason_text"));
            } else if (dataJsonObject.getString("status").compareTo("8") == 0) {
                binding.cancelLl.setVisibility(View.VISIBLE);
                binding.reasonTv.setText(dataJsonObject.getString("cancel_reason_text"));
                binding.statusTv.setText(getString(R.string.cancel_by_customer));
                binding.cancelTimeTv.setText(Util.ConvertDateTimeZone(dataJsonObject.getString("booking_cancel_time")));
                binding.statusTv.setTextColor(ContextCompat.getColor(context, R.color.cancel_color));
            }
            binding.mobileNumberTv.setText(dataJsonObject.getString("mobile"));
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + dataJsonObject.getString("img"), binding.userImageIv, options);
            JSONObject userVehicleData = dataJsonObject.getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle");
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + userVehicleData.getString("vehicle_picture"), binding.vehicleIv, options);
            String make = userVehicleData.getString("make_name");
            String plateNumber = userVehicleData.getString("plate_number");
            String VehicleTypeName = userVehicleData.getJSONObject("VehicleType").getString("name");
            String VehicleModelName = userVehicleData.getString("model_name");
            binding.vehicleNameTv.setText(make + " - " + VehicleModelName + " (" + VehicleTypeName + ")");
            binding.vehicleNumberTv.setText(plateNumber);

            if (userVehicleData.getString("color").compareTo("") == 0) {
                binding.colorLl.setVisibility(View.GONE);
            } else {
                binding.colorLl.setVisibility(View.VISIBLE);
                binding.colorTv.setText(userVehicleData.getString("color"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.backIcon:
                finish();
                break;
            case R.id.getFareTv:
                getFareReview();
                break;
        }
    }


    public void getFareReview() {
        try {
            JSONObject jsonObject = new JSONObject(dataJsonObject.getString("fair_review"));

            ArrayList<BeanReviewData> review_list = new ArrayList<>();
            review_list.clear();

            review_list = CalculatefareReviewData(jsonObject);

            final Dialog dialog;
            GetfareReviewAdapter getfareReviewAdapter;
            GetfarePromosAdapter getfarePromosAdapter;

            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            GetfareLayoutBinding getfareLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.getfare_layout, null, false);
            dialog.setContentView(getfareLayoutBinding.getRoot());


            getfareLayoutBinding.paidAmount.setText("" + Util.DisplayAmount(jsonObject.getString("paidAmount")));
            getfareLayoutBinding.totalAmount.setText("" + Util.DisplayAmount(jsonObject.getString("totalAmount")));

            getfareReviewAdapter = new GetfareReviewAdapter(context, review_list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            getfareLayoutBinding.recylerview.setLayoutManager(mLayoutManager);
            getfareLayoutBinding.recylerview.setItemAnimator(new DefaultItemAnimator());
            getfareLayoutBinding.recylerview.setAdapter(getfareReviewAdapter);

            getfarePromosAdapter = new GetfarePromosAdapter(context, jsonObject.getJSONArray("promoCodes"));
            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
            getfareLayoutBinding.promoRecylerview.setLayoutManager(mLayoutManager1);
            getfareLayoutBinding.promoRecylerview.setItemAnimator(new DefaultItemAnimator());
            getfareLayoutBinding.promoRecylerview.setAdapter(getfarePromosAdapter);

            getfareLayoutBinding.backIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<BeanReviewData> CalculatefareReviewData(JSONObject jsonObject) {

        ArrayList<BeanReviewData> beanReviewDataArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonObject.getJSONArray("vehicleData").length(); i++) {
                BeanReviewData beanReviewData = new BeanReviewData();

                beanReviewData.setRequest_type(jsonObject.getString("request_type"));
                beanReviewData.setRequest_type_amount(jsonObject.getString("request_type_amount"));
                beanReviewData.setVehicle_id(jsonObject.getJSONArray("vehicleData").getJSONObject(i).getString("vehicle_id"));
                beanReviewData.setVehicle_name(jsonObject.getJSONArray("vehicleData").getJSONObject(i).getString("vehicle_name"));
                beanReviewData.setVehicle_amount(jsonObject.getJSONArray("vehicleData").getJSONObject(i).getString("vehicle_amount"));
                beanReviewDataArrayList.add(beanReviewData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanReviewDataArrayList;
    }

    /*APIs*/
    private void callWebServiceBookingDetail() {

        if (Util.hasInternet(this)) {
//        progressDialog.show();
            Util.showShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);

            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("type", "2");
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("booking_id", bookingId);
                jsonObject.put("language", "eng");
                jsonObject.put("booking_driver_id", bookingDriverId);

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.booking_detail(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Util.dismissPrograssDialog(progressDialog);

                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);

                        try {
                            JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                binding.scrollView.setVisibility(View.VISIBLE);
                                binding.noDataTv.setVisibility(View.GONE);
                                dataJsonObject = new JSONObject();
                                dataJsonObject = mJsonObj.getJSONObject("response").getJSONObject("data");
                                setData();
                            } else {
                                binding.scrollView.setVisibility(View.GONE);
                                binding.noDataTv.setVisibility(View.VISIBLE);
                                if (mJsonObj.getJSONObject("response").has("message"))
                                    binding.noDataTv.setText(mJsonObj.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Util.dismissPrograssDialog(progressDialog);
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                        t.printStackTrace();
                    }
                });

            } catch (Exception e) {
//            Util.dismissPrograssDialog(progressDialog);
                Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                e.printStackTrace();
            }
        } else {
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }
}