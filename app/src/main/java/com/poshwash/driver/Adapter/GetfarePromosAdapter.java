package com.poshwash.driver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.R;

import org.json.JSONArray;

/**
 * Created by abhinava on 6/5/2017.
 */

public class GetfarePromosAdapter extends RecyclerView.Adapter<GetfarePromosAdapter.MyViewHolder> {

    JSONArray promoArray = new JSONArray();
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView promoCodeTv, discount_tv, discountAmount_tv, promoCodeText;


        public MyViewHolder(View view) {
            super(view);
            promoCodeTv = (TextView) view.findViewById(R.id.promoCodeTv);
            discount_tv = (TextView) view.findViewById(R.id.discount_tv);
            discountAmount_tv = (TextView) view.findViewById(R.id.discountAmount_tv);
            promoCodeText = (TextView) view.findViewById(R.id.promoCodeText);
        }
    }

    public GetfarePromosAdapter(Context context, JSONArray promoArray) {
        this.promoArray = promoArray;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.getfare_promo_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        try {

            if (i == 0) {
                holder.promoCodeText.setVisibility(View.VISIBLE);
            } else {
                holder.promoCodeText.setVisibility(View.GONE);
            }

            holder.promoCodeTv.setText(promoArray.getJSONObject(i).getString("promoCode"));
            holder.discount_tv.setText(promoArray.getJSONObject(i).getString("discount") + "%");
            holder.discountAmount_tv.setText("" + promoArray.getJSONObject(i).getString("discountAmount"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return promoArray.length();
    }
}
