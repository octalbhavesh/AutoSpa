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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Adapter.TransactionAdapter;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.DividerItemDecoration;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.MyTransactionHistoryBinding;
import com.poshwash.driver.views.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTransactionHistory extends Fragment {

    private TransactionAdapter adapter;
    private List<MyBookingChildModal> transactionList;
    private MyProgressDialog myProgressDialog;
    private final String TAG = "myProgressDialog.java";
    Context context;
    MyTransactionHistoryBinding binding;

    public static MyTransactionHistory newInstance(Context contex) {
        MyTransactionHistory f = new MyTransactionHistory();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_transaction_history, container, false);
        context = getActivity();
        initView();
        ((MainActivity) getActivity()).setTransactionHistoryFragment();
        return binding.getRoot();
    }

    private void initView() {

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(getActivity(), transactionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerview.setLayoutManager(mLayoutManager);
        binding.recyclerview.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        binding.recyclerview.setAdapter(adapter);

        myProgressDialog = new MyProgressDialog(getActivity());
        myProgressDialog.setCancelable(false);

        // call webservice
        callWebServiceShowTransactionHistory();

    }

    private void prepareData(JSONArray jsonArray) {
        transactionList.clear();

        for (int j = 0; j < jsonArray.length(); j++) {
            try {

                JSONObject jsonObject = jsonArray.getJSONObject(j);
                MyBookingChildModal myBookingChildModal = new MyBookingChildModal();

                myBookingChildModal.setId(jsonObject.optString("booking_id"));
                myBookingChildModal.setBooking_Sp_id(jsonObject.optString("booking_driver_id"));
                myBookingChildModal.setOwner_name(jsonObject.optString("first_name") + " " + jsonObject.optString("last_name"));
                myBookingChildModal.setPrice(jsonObject.optString("amount"));
                myBookingChildModal.setService_type(jsonObject.optString("type"));
                myBookingChildModal.setBooking_date(jsonObject.optString("created"));
                myBookingChildModal.setProfile_image_url(jsonObject.optString("img"));
                myBookingChildModal.setTransaction_number(jsonObject.optString("transaction_id"));

                transactionList.add(myBookingChildModal);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }

    /*APIs*/
    private void callWebServiceShowTransactionHistory() {

        if (Util.hasInternet(getActivity())) {
//        myProgressDialog.show();
            Util.showShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
            Call<ResponseBody> call = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject.put("language", "eng");
                jsonObject.put("type", "2");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.myTransaction(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Util.dismissPrograssDialog(myProgressDialog);
                        Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                        try {
                            JSONObject jsonObject1 = new JSONObject(Util.convertRetrofitResponce(response));
                            if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                                JSONArray jsonArray = jsonObject1.getJSONObject("response").getJSONArray("data");

                                if (jsonArray.length() > 0) {
                                    binding.noDataTv.setVisibility(View.GONE);
                                    binding.recyclerview.setVisibility(View.VISIBLE);
                                } else {
                                    binding.noDataTv.setVisibility(View.VISIBLE);
                                    binding.recyclerview.setVisibility(View.GONE);
                                }
                                // pass json array
                                prepareData(jsonArray);
                            } else {
                                binding.noDataTv.setVisibility(View.VISIBLE);
                                binding.recyclerview.setVisibility(View.GONE);
                                Util.errorToast(context, jsonObject1.getJSONObject("response").getString("message"));
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