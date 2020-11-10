package com.poshwash.driver.views.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.views.Activity.EditProfile;
import com.poshwash.driver.views.Activity.FullScreenImageActivity;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends Fragment implements View.OnClickListener {

    static Context context;
    private ImageView toggle_icon;
    private TextView txt_email;
    private TextView txt_phone;
    private TextView txt_company_name;
    private TextView txt_address;

    private RecyclerView recylerview;
    private Button btn_editprofile;
    private RatingBar rating_bar;
    private CircleImageView user_profleimg;
    private TextView txt_username;
    MyProgressDialog myProgressDialog;
    RelativeLayout company_layout;
    RelativeLayout rrImg;

    public static MyProfile newInstance(Context contex) {
        MyProfile f = new MyProfile();
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
        View view = inflater.inflate(R.layout.myprofile, container, false);
        context = getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        rrImg = (RelativeLayout) view.findViewById(R.id.rrImg);
        toggle_icon = (ImageView) view.findViewById(R.id.toggle_icon);
        txt_email = (TextView) view.findViewById(R.id.txt_email);
        txt_phone = (TextView) view.findViewById(R.id.txt_phone);
        txt_address = (TextView) view.findViewById(R.id.txt_address);
        recylerview = (RecyclerView) view.findViewById(R.id.recylerview);
        btn_editprofile = (Button) view.findViewById(R.id.btn_editprofile);
        user_profleimg = (CircleImageView) view.findViewById(R.id.user_profleimg);
        txt_username = (TextView) view.findViewById(R.id.txt_username);
        rating_bar = (RatingBar) view.findViewById(R.id.rating_bar);

        btn_editprofile.setOnClickListener(this);
        toggle_icon.setOnClickListener(this);

        rrImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                intent.putExtra("photos", MySharedPreferences.getProfileImage(context));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //    getProfile();
        Log.e("class", "MyProfile");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_editprofile:
                startActivity(new Intent(getActivity(), EditProfile.class));
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
            case R.id.toggle_icon:
                ((MainActivity) context).OpenDrawer();
                break;
        }
    }

/*    private void getProfile() {
        myProgressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.getProfile(new ConvertJsonToMap().jsonToMap(jsonObject));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(myProgressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(response.body().string());
                        Log.e("tg", "response from server = " + mJsonObj.toString());
                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1 = mJsonObj.getJSONObject("response").getJSONArray("data").getJSONObject(0);

                            MySharedPreferences.setFirstName(context, jsonObject1.getString("name"));
                            MySharedPreferences.setLastName(context, jsonObject1.getString("name"));
                            MySharedPreferences.setUserEmail(context, jsonObject1.getString("email"));
                            MySharedPreferences.setPhoneNumber(context, jsonObject1.getString("phone_number"));
                            MySharedPreferences.setAddress(context, jsonObject1.getString("address"));
                            MySharedPreferences.setProfileImage(context, jsonObject1.getString("profile_image_url"));
                            MySharedPreferences.setLatitude(context, jsonObject1.getString("lat"));
                            MySharedPreferences.setLongitude(context, jsonObject1.getString("lng"));
                            MySharedPreferences.setRating(context, jsonObject1.getString("rating"));
                            MySharedPreferences.setNotificationSetting(context, jsonObject1.getString("notification_status"));
                            autoFillData();
                           // ((MainActivity) getActivity()).SetProfile();
                        }

                    } catch (Exception e) {

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
    }*/
}
