package com.poshwash.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.databinding.TransactionListRowBinding;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<MyBookingChildModal> moviesList;
    DisplayImageOptions options;
    Context context;
    long mLastRowBgClickTime = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TransactionListRowBinding binding;

        public MyViewHolder(TransactionListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.rowBg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.row_bg:
                    if (SystemClock.elapsedRealtime() - mLastRowBgClickTime < 1000) {
                        return;
                    }
                    mLastRowBgClickTime = SystemClock.elapsedRealtime();

                    String booking_id = moviesList.get(getAdapterPosition()).getId();
                    String Booking_driver_id = moviesList.get(getAdapterPosition()).getBooking_Sp_id();

                    getBookingDetails(booking_id, Booking_driver_id);

                    break;

            }
        }
    }

    public TransactionAdapter(Context context, List<MyBookingChildModal> moviesList) {
        this.moviesList = moviesList;
        this.context = context;

        options = Util.displayOption(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TransactionListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.transaction_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.binding.userNameTv.setText(moviesList.get(position).getOwner_name());
        holder.binding.txtTransactionId.setText(moviesList.get(position).getTransaction_number());

        try {
            holder.binding.txtTime.setText(Util.calculateTimeDiffFromNow(moviesList.get(position).getBooking_date(), context));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        holder.binding.txtPrice.setText(moviesList.get(position).getPrice());
        if (!moviesList.get(position).getProfile_image_url().equals(""))
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + moviesList.get(position).getProfile_image_url(), holder.binding.circleimageview, options);
        else
            holder.binding.circleimageview.setImageResource(R.drawable.defalut_bg);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    private void getBookingDetails(String booking_id, String Booking_driver_id) {
        Intent intent = new Intent(context, BookingDetail.class);
        intent.putExtra("bookingId", booking_id);
        intent.putExtra("bookingDriverId", Booking_driver_id);
        context.startActivity(intent);
    }
}
