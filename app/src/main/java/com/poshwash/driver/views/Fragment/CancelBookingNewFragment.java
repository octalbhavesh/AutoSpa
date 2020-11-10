package com.poshwash.driver.views.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Adapter.CancelBookingAdapterNew;
import com.poshwash.driver.Beans.CancelBookingModel;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.DividerItemDecoration;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.FragmentCancelBookingNewBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CancelBookingNewFragment extends Fragment {

    static private Context context;
    private FragmentCancelBookingNewBinding binding;
    private CancelBookingAdapterNew adapter;
    private ArrayList<CancelBookingModel> arrayList = new ArrayList<>();


    public static CancelBookingNewFragment newInstance(Context contex) {
        CancelBookingNewFragment f = new CancelBookingNewFragment();
        context = contex;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cancel_booking_new, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.cancelList.setLayoutManager(mLayoutManager);
        binding.cancelList.setItemAnimator(new DefaultItemAnimator());
        binding.cancelList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        callWebServiceShowNotification();
    }


    public void callWebServiceShowNotification() {
        if (Util.hasInternet(getActivity())) {
            Call<ResponseBody> call;
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(getActivity()));
                jsonObject.put("language", "eng");

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.cancel_booking_list_sp(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body().string());
                            if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                                JSONArray jsonArray = jsonObject1.getJSONObject("response").getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                                    CancelBookingModel model = new CancelBookingModel();
                                    String date = jsonObject2.optString("booking_date");
                                    model.setDate(date);
                                    String booking_id = jsonObject2.optString("booking_id");
                                    model.setBooking_id(booking_id);
                                    String status = jsonObject2.optString("status");
                                    model.setStatus(status);

                                    JSONArray jsonArray1 = jsonObject2.optJSONArray("BookingUserVehicle");
                                    JSONObject jsonObject3 = jsonArray1.optJSONObject(0);
                                    JSONObject jsonObject4 = jsonObject3.optJSONObject("UserVehicle");

                                    String plate_number = jsonObject4.optString("plate_number");
                                    model.setPlate_number(plate_number);

                                    String img = jsonObject4.optString("vehicle_picture");
                                    model.setImg(img);

                                    String car_name = jsonObject4.optString("make_name") + " " + jsonObject4.optString("model_name");
                                    model.setCar_name(car_name);

                                    arrayList.add(model);
                                }

                                if (arrayList.size() > 0) {
                                    adapter = new CancelBookingAdapterNew(getActivity(), arrayList);
                                    binding.cancelList.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Util.errorToast(getActivity(), jsonObject1.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Util.dismissPrograssDialog(myProgressDialog);

                    }
                });

            } catch (Exception e) {
//            Util.dismissPrograssDialog(myProgressDialog);
                //  Util.dismissShimmerFrameLayout(binding.shimmerFrameLayout.shimmerFrameLayout);
                //  Log.e(TAG, e.toString());
            }
        } else {
            Util.errorToast(getActivity(), getResources().getString(R.string.no_internet));
        }
    }


}
