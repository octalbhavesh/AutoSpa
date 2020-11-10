package com.poshwash.driver.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.poshwash.driver.Beans.CancelBookingModel;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.MyProgressDialog;

import java.util.ArrayList;

/**
 * Created by anand jain on 3/20/2017.
 */

public class CancelBookingAdapterNew extends RecyclerView.Adapter<CancelBookingAdapterNew.MyViewHolder> {

    private ArrayList<CancelBookingModel> moviesList;
    DisplayImageOptions options;
    Context context;
    MyProgressDialog myProgressDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView circleimageview;
        public TextView tvCarName, tvDate, tvUserName, tvStatus, tvAmount;
        public RelativeLayout row_bg;

        public MyViewHolder(View view) {
            super(view);
            tvCarName = (TextView) view.findViewById(R.id.txt_carname);
            tvUserName = (TextView) view.findViewById(R.id.txt_username);
            tvDate = (TextView) view.findViewById(R.id.txt_time);

            circleimageview = (ImageView) view.findViewById(R.id.img_car);

        }


    }

    public CancelBookingAdapterNew(Context context, ArrayList<CancelBookingModel> moviesList) {
        this.moviesList = moviesList;

        this.context = context;

        Bitmap default_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.defalut_bg);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new BitmapDrawable(context.getResources(), default_bitmap))
                .showImageForEmptyUri(new BitmapDrawable(context.getResources(), default_bitmap))
                .showImageOnFail(new BitmapDrawable(context.getResources(), default_bitmap))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cancel_booking_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvCarName.setText(moviesList.get(position).getCar_name());
        holder.tvUserName.setText("Plate Number: " + moviesList.get(position).getPlate_number());
        String[] date = moviesList.get(position).getDate().split("-");
        holder.tvDate.setText(date[1] + "-" + date[2] + "-" + date[0]);


      //  holder.tvStatus.setText("Cancel By Admin");
        if (!moviesList.get(position).getImg().equals(""))
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL
                    + moviesList.get(position).getImg(), holder.circleimageview, options);
        else
            holder.circleimageview.setImageResource(R.drawable.defalut_bg);

        /*holder.userNameTv.setText(moviesList.get(position).getName());
        holder.txt_notificationtext.setText(moviesList.get(position).getText());
        try {
            holder.txt_time.setText(Util.calculateTimeDiffFromNow(moviesList.get(position).getTime(), context));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!moviesList.get(position).getPic().equals(""))
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + moviesList.get(position).getPic(), holder.circleimageview, options);
        else
            holder.circleimageview.setImageResource(R.drawable.defalut_bg);*/


    }

    @Override
    public int getItemCount() {
        return moviesList.size(); //Dynamic Data moviesList.size();
    }


}
