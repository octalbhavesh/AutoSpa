package com.poshwash.driver.views.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Adapter.BookingAdapter;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.InprogressLayoutBinding;

import java.util.ArrayList;

public class CompletedFragment extends Fragment {
    Context context;
    BookingAdapter bookingAdapter;
    ArrayList<MyBookingChildModal> bookingList = new ArrayList<>();
    InprogressLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.inprogress_layout, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    public void getListData(ArrayList<MyBookingChildModal> bookinglist) {
        if (Util.hasInternet(getActivity())) {
            bookingList.clear();
            for (int i = 0; i < bookinglist.size(); i++) {
                if (bookinglist.get(i).getStatus().compareTo("2") == 0) {
                    bookingList.add(bookinglist.get(i));
                }
            }
            bookingAdapter = new BookingAdapter(context, bookingList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            binding.recyclerView.setLayoutManager(mLayoutManager);
            binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            binding.recyclerView.setAdapter(bookingAdapter);

            if (bookingList.size() > 0) {
                binding.noDataTv.setVisibility(View.GONE);
            } else {
                binding.noDataTv.setVisibility(View.VISIBLE);
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }
}