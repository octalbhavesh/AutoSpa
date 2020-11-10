package com.poshwash.driver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.R;

import java.util.List;

/**
 * Created by anandj on 3/17/2017.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MyViewHolder> {

    private List<String> moviesList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RelativeLayout parent_rl;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_settingname);
            parent_rl = (RelativeLayout) view.findViewById(R.id.parent_rl);
        }
    }
    public SettingAdapter(Context context,List<String> moviesList) {
        this.moviesList = moviesList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setting_child, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.title.setText(moviesList.get(position));

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}