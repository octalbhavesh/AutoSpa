package com.poshwash.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.databinding.NotificationChildBinding;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.Beans.NotificationModal;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.views.Fragment.Notification;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationModal> moviesList;
    DisplayImageOptions options;
    Context context;
    Notification notification;
    MyProgressDialog myProgressDialog;
    long mLastRowBgClickTime = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NotificationChildBinding notificationChildBinding;

        public MyViewHolder(NotificationChildBinding notificationChildBinding) {
            super(notificationChildBinding.getRoot());
            this.notificationChildBinding = notificationChildBinding;
            notificationChildBinding.rowBg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.row_bg:

                    if (SystemClock.elapsedRealtime() - mLastRowBgClickTime < 1000) {
                        return;
                    }
                    mLastRowBgClickTime = SystemClock.elapsedRealtime();

                    String booking_id = moviesList.get(getAdapterPosition()).getBooking_id();
                    String Booking_driver_id = moviesList.get(getAdapterPosition()).getBooking_driver_id();
                    if (moviesList.get(getAdapterPosition()).is_read().compareTo("0") == 0)
                        getReadNoticafition(moviesList.get(getAdapterPosition()).getId(), booking_id, Booking_driver_id, getAdapterPosition());
                    else {
                        JumpRelatedScreen(booking_id, Booking_driver_id, getAdapterPosition());
                    }
                    break;
               /* case R.id.frameDelete:
                  //  removeNotification(moviesList.get(getAdapterPosition()).getId());
                    break;*/
            }
        }
    }

    public NotificationAdapter(Context context, List<NotificationModal> moviesList, Notification notification) {
        this.moviesList = moviesList;
        this.notification = notification;
        this.context = context;
        myProgressDialog = new MyProgressDialog(this.context);
        myProgressDialog.setCancelable(false);

        options = Util.displayOption(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotificationChildBinding notificationChildBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.notification_child, parent, false);
        return new MyViewHolder(notificationChildBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.notificationChildBinding.txtNotificationtext.setText(moviesList.get(position).getText());
        holder.notificationChildBinding.userNameTv.setText(moviesList.get(position).getUserName());


        // if (moviesList.get(position).getType().compareTo("2") == 0) {
        try {
            holder.notificationChildBinding.txtTime.setText(Util.calculateTimeDiffFromNow(moviesList.get(position).getTime(), context));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*} else {
            try {
                holder.txt_time.setText(Util.calculateTimeDiffFromNow(moviesList.get(position).getTime(), context));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/


        if (!moviesList.get(position).getPic().equals(""))

            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + moviesList.get(position).getPic(), holder.notificationChildBinding.circleimageview, options);
        else
            holder.notificationChildBinding.circleimageview.setImageResource(R.drawable.defalut_bg);


        if (moviesList.get(position).is_read().compareTo("0") == 0)
            holder.notificationChildBinding.rowBg.setBackgroundColor(ContextCompat.getColor(context, R.color.notificationback));
        else
            holder.notificationChildBinding.rowBg.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        //  holder.swipeRevealLayout.close(true);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void JumpRelatedScreen(final String booking_id, final String Booking_driver_id, final int position) {
        if (booking_id != null && !booking_id.equalsIgnoreCase("null") && !booking_id.isEmpty()) {
            if (moviesList.get(position).getType().compareTo("2") == 0) {
                ((MainActivity) context).displayView(AutoSpaConstant.PENDINGREQUESTFRAGMENT, null, null);
            } else if (moviesList.get(position).getType().compareTo("10") == 0) {
                ((MainActivity) context).displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
            } else if (moviesList.get(position).getType().compareTo("11") == 0) {
                ((MainActivity) context).displayView(AutoSpaConstant.CANCELBOOKING, null, null);
            } else {
                getBookingDetails(booking_id, Booking_driver_id);
            }
        } else
            notification.callWebServiceShowNotification(true);
    }


    private void getBookingDetails(String booking_id, String Booking_driver_id) {
        Intent intent = new Intent(context, BookingDetail.class);
        intent.putExtra("bookingId", booking_id);
        intent.putExtra("bookingDriverId", Booking_driver_id);
        context.startActivity(intent);
    }

    /*APIs*/
    private void getReadNoticafition(String notification_id, final String booking_id, final String Booking_driver_id, final int position) {
        if (Util.hasInternet(context)) {
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("notification_id", notification_id);
                jsonObject.put("is_read", "1");
                jsonObject.put("language", MySharedPreferences.getUserId(context));
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.readNotification(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject mjJsonObject = new JSONObject(new String(response.body().bytes()));
                            if (mjJsonObject.getJSONObject("response").getBoolean("status")) {
                                getProfile();
                                JumpRelatedScreen(booking_id, Booking_driver_id, position);

                            } else
                                Util.errorToast(context, mjJsonObject.getJSONObject("response").getString("message"));
                        } catch (Exception e) {
                            Log.e(Notification.TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Log.e(Notification.TAG, e.toString());
            }
        } else {
            Util.errorToast(context, context.getResources().getString(R.string.no_internet));
        }
    }

    private void getProfile() {
        if (Util.hasInternet(context)) {
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("language", MySharedPreferences.getLanguage(context));

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.getProfile(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject mJsonObj = new JSONObject(response.body().string());
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1 = mJsonObj.getJSONObject("response").getJSONObject("data");

                                MySharedPreferences.setFirstName(context, jsonObject1.getString("first_name"));
                                MySharedPreferences.setLastName(context, jsonObject1.getString("last_name"));
                                MySharedPreferences.setPhoneNumber(context, jsonObject1.getString("mobile"));
                                MySharedPreferences.setProfileImage(context, jsonObject1.getString("img"));
                                MySharedPreferences.setRating(context, jsonObject1.getString("rating"));
                                MySharedPreferences.setNotificationSetting(context, jsonObject1.getString("notification"));
                                MySharedPreferences.setNotificationCount(context, jsonObject1.getString("unread_badge_count"));
                                ((MainActivity) context).SetProfile();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        Util.dismissPrograssDialog(myProgressDialog);

                    }
                });
            } catch (Exception e) {
                Util.dismissPrograssDialog(myProgressDialog);
            }
        }
    }

    private void removeNotification(String notification_id) {
        myProgressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("notification_id", notification_id);
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));

            MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = myApiEndpointInterface.removeNotification(new ConvertJsonToMap().jsonToMap(jsonObject));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Util.dismissPrograssDialog(myProgressDialog);
                        JSONObject mjJsonObject = new JSONObject(new String(response.body().bytes()));
                        if (mjJsonObject.getJSONObject("response").getBoolean("status")) {
                            notification.callWebServiceShowNotification(true);
                        } else
                            Util.Toast(context, mjJsonObject.getJSONObject("response").getString("message"));

                    } catch (Exception e) {
                        Log.e(Notification.TAG, e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Util.dismissPrograssDialog(myProgressDialog);
                }
            });
        } catch (Exception e) {
            Log.e(Notification.TAG, e.toString());
        }
    }
}
