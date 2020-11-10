package com.poshwash.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.databinding.PendingRequestListChildBinding;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.views.Fragment.PendingRequestFragment;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.MyViewHolder> {

    private JSONArray Pending_jsonArray = new JSONArray();
    DisplayImageOptions options;
    Context context;
    MyProgressDialog progressDialog;
    PendingRequestFragment activity;
    public OnclickListner onclickListner;
    long mLastBtnAcceptClickTime = 0, mLastBtnDetailsClickTime = 0;

    public interface OnclickListner {
        void acceptJob(String bookingId, String BookingDriverId);
    }

    public void SetonclickListenr(OnclickListner onclickListner) {
        this.onclickListner = onclickListner;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        PendingRequestListChildBinding binding;

        public MyViewHolder(PendingRequestListChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public PendingRequestAdapter(Context context, JSONArray Pending_jsonArray, PendingRequestFragment activity) {
        this.Pending_jsonArray = Pending_jsonArray;
        this.context = context;
        this.activity = activity;

        options = Util.displayOption(context);

        progressDialog = new MyProgressDialog(this.context);
        progressDialog.setCancelable(false);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PendingRequestListChildBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pending_request_list_child, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        try {

            holder.binding.txtName.setText(Pending_jsonArray.getJSONObject(position).getString("first_name") + " " + Pending_jsonArray.getJSONObject(position).getString("last_name"));
            holder.binding.dateTimeTv.setText(Util.convertUtcTimeToLocal(Pending_jsonArray.getJSONObject(position).getString("booking_date") + " " + Pending_jsonArray.getJSONObject(position).getString("booking_time"), true, context));
            holder.binding.txtLocation.setText(Pending_jsonArray.getJSONObject(position).getString("booking_address"));
            holder.binding.txtMobileNumber.setText(Pending_jsonArray.getJSONObject(position).getString("mobile"));

            if (!Pending_jsonArray.getJSONObject(position).getString("customer_img").equals("")) {
                ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + Pending_jsonArray.getJSONObject(position).getString("customer_img"), holder.binding.userPicIv, options);
            } else {
                holder.binding.userPicIv.setImageResource(R.drawable.defalut_bg);
            }

            holder.binding.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastBtnAcceptClickTime < 1000) {
                            return;
                        }
                        mLastBtnAcceptClickTime = SystemClock.elapsedRealtime();

                        onclickListner.acceptJob(Pending_jsonArray.getJSONObject(position).getString("Booking_id"), Pending_jsonArray.getJSONObject(position).getString("Booking_driver_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.binding.btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if (SystemClock.elapsedRealtime() - mLastBtnDetailsClickTime < 1000) {
                            return;
                        }
                        mLastBtnDetailsClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(context, BookingDetail.class);
                        intent.putExtra("bookingId", Pending_jsonArray.getJSONObject(position).getString("Booking_id"));
                        intent.putExtra("bookingDriverId", Pending_jsonArray.getJSONObject(position).getString("Booking_driver_id"));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

         /*   String vehicleName = "";
            if (Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").length() > 0) {
                if (Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").length() == 1) {
                    vehicleName = Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("model") + " (" + Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getJSONObject("VehicleType").getString("name") + ")";
                } else {
                    vehicleName = Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getString("model") + " (" + Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle").getJSONObject("VehicleType").getString("name") + " + " + ((Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").length()) - 1 + ")");
                }
            } else {
                if (Pending_jsonArray.getJSONObject(position).getJSONArray("BookingVehicleType").length() == 1) {
                    vehicleName = Pending_jsonArray.getJSONObject(position).getJSONArray("BookingVehicleType").getJSONObject(0).getJSONObject("VehicleType").getString("name");
                } else {
                    vehicleName = Pending_jsonArray.getJSONObject(position).getJSONArray("BookingVehicleType").getJSONObject(0).getJSONObject("VehicleType").getString("name") + " + " + ((Pending_jsonArray.getJSONObject(position).getJSONArray("BookingVehicleType").length()) - 1);
                }
            }*/

            if (Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").length() > 0) {
                JSONObject userVehicle = Pending_jsonArray.getJSONObject(position).getJSONArray("BookingUserVehicle").getJSONObject(0).getJSONObject("UserVehicle");
                String make = userVehicle.getJSONObject("Make").getString("name");
                String plateNumber = userVehicle.getString("plate_number");
                String VehicleTypeName = userVehicle.getJSONObject("VehicleType").getString("name");
                String VehicleModelName = userVehicle.getJSONObject("VehicleModel").getString("name");

                holder.binding.txtCarName.setText("Car Name: " + make + " " + VehicleModelName + " - " + "(" + VehicleTypeName + ")");
                holder.binding.txtPlateNumber.setText("Plate Number: " + plateNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return Pending_jsonArray.length();
    }


}

