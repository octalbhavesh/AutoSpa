package com.poshwash.driver.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.databinding.BookingStatusRowItemBinding;
import com.poshwash.driver.databinding.RatingDialogBinding;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.views.Activity.MapViewActivity;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {

    DisplayImageOptions options;
    Context context;
    ArrayList<MyBookingChildModal> myBookinglist;
    String icon = "http://115.112.57.155:8080/okowasch/img/pin.ico";
    String maputl;
    long mLastdetailTabTvClickTime = 0, mLastmapTabTvClickTime = 0, mLastactionTabTvClickTime = 0;
    MyProgressDialog progressDialog;

    public BookingAdapter(Context context, ArrayList<MyBookingChildModal> myBookinglist) {

        this.myBookinglist = myBookinglist;
        this.context = context;
        options = Util.displayOption(context);
        progressDialog = new MyProgressDialog(context);
        progressDialog.setCancelable(false);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        BookingStatusRowItemBinding binding;

        public MyViewHolder(BookingStatusRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.datalayout.setOnClickListener(this);
            binding.detailTabTv.setOnClickListener(this);
            binding.mapTabTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.detailTabTv:

                    if (SystemClock.elapsedRealtime() - mLastdetailTabTvClickTime < 1000) {
                        return;
                    }
                    mLastdetailTabTvClickTime = SystemClock.elapsedRealtime();

                    Intent intent = new Intent(context, BookingDetail.class);
                    intent.putExtra("bookingId", myBookinglist.get(getAdapterPosition()).getId());
                    intent.putExtra("bookingDriverId", myBookinglist.get(getAdapterPosition()).getBooking_Sp_id());
                    context.startActivity(intent);

                    break;

                case R.id.mapTabTv:
                    if (SystemClock.elapsedRealtime() - mLastmapTabTvClickTime < 1000) {
                        return;
                    }
                    mLastmapTabTvClickTime = SystemClock.elapsedRealtime();

                    Intent intent1 = new Intent(context, MapViewActivity.class);
                    intent1.putExtra("booking_lat", myBookinglist.get(getAdapterPosition()).getBooking_lat());
                    intent1.putExtra("booking_lng", myBookinglist.get(getAdapterPosition()).getBooking_long());
                    context.startActivity(intent1);

                    break;
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookingStatusRowItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.booking_status_row_item, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Log.e("start_wash_time : ", "start_wash_time : " + myBookinglist.get(position).getStart_wash_time());
        try {
            maputl = "http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=900x300&markers=" + icon + "|" + myBookinglist.get(position).getBooking_lat() + "," + myBookinglist.get(position).getBooking_long() + "&key=" + context.getResources().getString(R.string.google_api_key);
            ImageLoader.getInstance().displayImage(maputl, holder.binding.mapIv, options);
            if (!myBookinglist.get(position).getImage().equals(""))
                ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookinglist.get(position).getImage(), holder.binding.vehicleIv, options);
            else
                holder.binding.vehicleIv.setImageResource(R.drawable.defalut_bg);

            holder.binding.orderNumberTv.setText("#" + myBookinglist.get(position).getId() + " (" + myBookinglist.get(position).getCar_name() + ")");
            holder.binding.customerNameTv.setText(myBookinglist.get(position).getOwner_name());
            holder.binding.mobileNumberTv.setText(myBookinglist.get(position).getUser_phone());
            holder.binding.dateTimeTv.setText(Util.convertUtcTimeToLocal(myBookinglist.get(position).getBooking_date() + " " + myBookinglist.get(position).getBooking_time(), true, context));

            if (myBookinglist.get(position).getStatus().compareTo("1") == 0) {
                holder.binding.actionTabTv.setText(context.getString(R.string.reached));
            } else if (myBookinglist.get(position).getStatus().compareTo("5") == 0) {
                holder.binding.actionTabTv.setText(context.getString(R.string.complete_wipe));
            } else if (myBookinglist.get(position).getStatus().compareTo("2") == 0) {
                if (myBookinglist.get(position).getIs_rating()) {
                    holder.binding.actionTabTv.setText(context.getString(R.string.completed));
                } else {
                    holder.binding.actionTabTv.setText("Completed Rating Pending");
                }
            } else if (myBookinglist.get(position).getStatus().compareTo("9") == 0) {
                holder.binding.actionTabTv.setText(context.getString(R.string.start_wipe));
            } else {
                holder.binding.actionTabTv.setText(context.getString(R.string.cancelled));
            }

            holder.binding.datalayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.binding.actionLayout.getVisibility() == View.VISIBLE) {
                        HideRow(holder);
                    } else {
                        ShowRow(holder);
                    }
                }
            });

            holder.binding.actionTabTv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (SystemClock.elapsedRealtime() - mLastactionTabTvClickTime < 1000) {
                        return;
                    }
                    mLastactionTabTvClickTime = SystemClock.elapsedRealtime();

                    String bookingtime = "";
                    try {
                        bookingtime = Util.convertUtcTimeToLocal(myBookinglist.get(position).getBooking_date() + " " + myBookinglist.get(position).getBooking_time(), true, context);
                        Log.e("bookingtime : ", "bookingtime : " + bookingtime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (myBookinglist.get(position).getStatus().compareTo("1") == 0 || myBookinglist.get(holder.getAdapterPosition()).getStatus().compareTo("5") == 0 || myBookinglist.get(holder.getAdapterPosition()).getStatus().compareTo("9") == 0) {
                        ((MainActivity) context).displayView(AutoSpaConstant.WASHPROCESSFRAGMENT, null, myBookinglist.get(holder.getAdapterPosition()));
                    } else if (myBookinglist.get(position).getStatus().compareTo("2") == 0 && myBookinglist.get(position).getBooking_transaction().compareTo("0") == 0) {
                        ((MainActivity) context).displayView(AutoSpaConstant.WASHPROCESSFRAGMENT, null, myBookinglist.get(holder.getAdapterPosition()));
                    } else if (myBookinglist.get(position).getStatus().compareTo("2") == 0 && !myBookinglist.get(position).getIs_rating()) {
                        showRatingDialog(position);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRatingDialog(final int position) {

        final Dialog dialog = new Dialog(context);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        final RatingDialogBinding alertInputBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rating_dialog, null, false);
        dialog.setContentView(alertInputBinding.getRoot());

        ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookinglist.get(position).getProfile_image_url(), alertInputBinding.imgUserImage, AutoSpaApplication.options);
        alertInputBinding.txtCustomerName.setText(myBookinglist.get(position).getOwner_name());

        alertInputBinding.txtCompleteRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int rating = (int) alertInputBinding.ratingBar.getRating();
                if (rating == 0) {
                    Util.errorToast(context, "Please select rating.");
                    return;
                }
                callCustomerRating("" + rating, dialog, myBookinglist.get(position).getBooking_Sp_id(), myBookinglist.get(position).getId());

            }
        });

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return myBookinglist.size();
    }

    public void HideRow(MyViewHolder myViewHolder) {

        myViewHolder.binding.arrowIv.setImageResource(R.drawable.downarrow);
        myViewHolder.binding.customerLayout.setVisibility(View.GONE);
        myViewHolder.binding.customerNameTv.setVisibility(View.GONE);
        myViewHolder.binding.mobileLayout.setVisibility(View.GONE);
        myViewHolder.binding.mobileNumberTv.setVisibility(View.GONE);
        myViewHolder.binding.dateTimeTv.setVisibility(View.GONE);
        myViewHolder.binding.locationIv.setVisibility(View.GONE);
        myViewHolder.binding.mapIv.setVisibility(View.GONE);
        myViewHolder.binding.actionLayout.setVisibility(View.GONE);

    }

    public void ShowRow(MyViewHolder myViewHolder) {

        myViewHolder.binding.arrowIv.setImageResource(R.drawable.uparrow);
        myViewHolder.binding.customerLayout.setVisibility(View.VISIBLE);
        myViewHolder.binding.customerNameTv.setVisibility(View.VISIBLE);
        myViewHolder.binding.mobileLayout.setVisibility(View.VISIBLE);
        myViewHolder.binding.mobileNumberTv.setVisibility(View.VISIBLE);
        myViewHolder.binding.dateTimeTv.setVisibility(View.VISIBLE);
        myViewHolder.binding.locationIv.setVisibility(View.VISIBLE);
        myViewHolder.binding.mapIv.setVisibility(View.VISIBLE);
        myViewHolder.binding.actionLayout.setVisibility(View.VISIBLE);
    }

    /*APIs*/
    private void callCustomerRating(String rating, final Dialog dialog, String bookingDriverId, String bookingId) {

        if (Util.hasInternet(context)) {

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
                jsonObject.put("language", MySharedPreferences.getLanguage(context));
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

                                Util.successToast(context, mJsonObj.getJSONObject("response").getString("message"));
                                ((MainActivity) context).displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);

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
            Util.errorToast(context, context.getResources().getString(R.string.no_internet));
        }
    }
}