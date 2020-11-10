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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.poshwash.driver.Adapter.NotificationAdapter;
import com.poshwash.driver.Beans.NotificationModal;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.NotificationBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification extends Fragment {

    static Context context;
    private NotificationAdapter adapter;
    private List<NotificationModal> notificationModalList;
    private MyProgressDialog myProgressDialog;
    public static final String TAG = "Notification.java";
    NotificationBinding binding;

    public static Notification newInstance(Context contex) {
        Notification f = new Notification();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.notification, container, false);
        context = getActivity();
        initView();

        return binding.getRoot();
    }

    private void initView() {
        notificationModalList = new ArrayList<>();
        adapter = new NotificationAdapter(context, notificationModalList, Notification.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recylerview.setLayoutManager(mLayoutManager);
        binding.recylerview.setItemAnimator(new DefaultItemAnimator());
        binding.recylerview.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        binding.recylerview.setAdapter(adapter);

        myProgressDialog = new MyProgressDialog(getActivity());
        myProgressDialog.setCancelable(false);


        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(true);
                callWebServiceShowNotification(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        callWebServiceShowNotification(true);
    }

    private void prepareData(JSONArray jsonArray) {

        if (jsonArray.length() > 0) {
            binding.noDataTv.setVisibility(View.GONE);
            binding.recylerview.setVisibility(View.VISIBLE);
        } else {
            binding.noDataTv.setVisibility(View.VISIBLE);
            binding.recylerview.setVisibility(View.GONE);
        }

        notificationModalList.clear();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject;
                jsonObject = jsonArray.getJSONObject(i);
                NotificationModal notificationModal = new NotificationModal();
                notificationModal.setId(jsonObject.getString("id"));
                notificationModal.setText(jsonObject.getJSONObject("message").getString("msg"));
                notificationModal.setUserName(jsonObject.getJSONObject("message").getString("customer_name"));
                notificationModal.setTime(jsonObject.getString("created"));
                notificationModal.setIs_read(jsonObject.getString("is_read"));
                if (jsonObject.getJSONObject("message").has("booking_id"))
                    notificationModal.setBooking_id(jsonObject.getJSONObject("message").getString("booking_id"));
                if (jsonObject.getJSONObject("message").has("Booking_driver_id"))
                    notificationModal.setBooking_driver_id(jsonObject.getJSONObject("message").getString("Booking_driver_id"));
                if (jsonObject.getJSONObject("message").has("customer_profile"))
                    notificationModal.setPic(jsonObject.getJSONObject("message").getString("customer_profile"));
                if (jsonObject.getJSONObject("message").has("type"))
                    notificationModal.setType(jsonObject.getJSONObject("message").getString("type"));
                notificationModalList.add(notificationModal);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }

    /*APIs*/
    public void callWebServiceShowNotification(boolean status) {
        if (Util.hasInternet(getActivity())) {
            if (status)
                Util.showShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
//            myProgressDialog.show();
            Call<ResponseBody> call;
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject.put("language", "eng");
                jsonObject.put("type", "2");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.showNotification(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (binding.swipeRefresh.isRefreshing())
                            binding.swipeRefresh.setRefreshing(false);
//                    Util.dismissPrograssDialog(myProgressDialog);
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
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
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                    }
                });

            } catch (Exception e) {
//            Util.dismissPrograssDialog(myProgressDialog);
                Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                Log.e(TAG, e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }
}