package com.poshwash.driver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Beans.BeanReviewData;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.Util;

import java.util.ArrayList;

/**
 * Created by abhinava on 6/5/2017.
 */

public class GetfareReviewAdapter extends RecyclerView.Adapter<GetfareReviewAdapter.MyViewHolder> {

    ArrayList<BeanReviewData> review_list=new ArrayList<BeanReviewData>();
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView vehicle_text_tv,request_type_tv,amount_tv;

        public MyViewHolder(View view) {
            super(view);
            vehicle_text_tv = (TextView) view.findViewById(R.id.vehicle_text_tv);
            request_type_tv = (TextView)view.findViewById(R.id.request_type_tv);
            amount_tv = (TextView)view.findViewById(R.id.amount_tv);
        }
    }
    public GetfareReviewAdapter(Context context,ArrayList<BeanReviewData> review_list) {
        this.review_list = review_list;
        this.context=context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.getfare_review_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i)
    {
        holder.vehicle_text_tv.setText("("+review_list.get(i).getVehicle_name()+") "+Util.DisplayAmount(review_list.get(i).getVehicle_amount()));
        if(review_list.get(i).getRequest_type().compareTo("1")==0)
        {
            holder.request_type_tv.setText("(On Demand)"+Util.DisplayAmount(review_list.get(i).getRequest_type_amount()));
        }
        else
        {
            holder.request_type_tv.setText(""+Util.DisplayAmount(review_list.get(i).getRequest_type_amount()));
        }

        holder.amount_tv.setText(""+Util.DisplayAmount(Double.parseDouble(review_list.get(i).getVehicle_amount())+Double.parseDouble(review_list.get(i).getRequest_type_amount())+""));

    }



    @Override
    public int getItemCount() {
        return review_list.size();
    }
}
