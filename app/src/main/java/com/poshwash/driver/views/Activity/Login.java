package com.poshwash.driver.views.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.ActivityLoginBinding;
import com.poshwash.driver.databinding.AlertInputBinding;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    final String TAG = "Login.java";
    MyProgressDialog progressDialog;
    ActivityLoginBinding binding;
    private long mLastSubmitClickTime = 0, mLastForgotPINClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.invalidateAll();
        initView();
        if (TextUtils.isEmpty(MySharedPreferences.getDeviceId(Login.this))) {
            if (FirebaseInstanceId.getInstance().getToken() != null) {
                MySharedPreferences.setDeviceId(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());
            }
        }
        checkRequestPermission();
    }

    private void initView() {


        context = Login.this;
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);

        setOnClick();
    }

    private void setOnClick() {

        binding.tvLoginSubmit.setOnClickListener(this);
        binding.tvLoginForgot.setOnClickListener(this);
    }

    private void checkRequestPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            }, 1);
        } else {
            // GoNext();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                checkRequestPermission();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_login_submit:
                if (SystemClock.elapsedRealtime() - mLastSubmitClickTime < 1000) {
                    return;
                }
                mLastSubmitClickTime = SystemClock.elapsedRealtime();
                submit();
                break;
            case R.id.tv_login_forgot:
                if (SystemClock.elapsedRealtime() - mLastForgotPINClickTime < 1000) {
                    return;
                }
                mLastForgotPINClickTime = SystemClock.elapsedRealtime();
                showForgotPassword(R.style.DialogAnimation_2);
                break;
        }
    }

    private void showForgotPassword(int animationSource) {

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final AlertInputBinding alertInputBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.alert_input, null, false);
        dialog.setContentView(alertInputBinding.getRoot());

        alertInputBinding.txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertInputBinding.editDialog.getText().toString().trim().equals("")) {
                    Util.setError(context, alertInputBinding.editDialog);
                    return;
                }
                if (TextUtils.getTrimmedLength(alertInputBinding.editDialog.getText().toString().trim()) < 9) {
                    Util.errorToast(Login.this, getString(R.string.valid_phone_number));
                    return;
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(alertInputBinding.editDialog.getWindowToken(), 0);

                    callForgotPasswordWebService(alertInputBinding.editDialog.getText().toString(), dialog);

                }
            }
        });
        alertInputBinding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(alertInputBinding.editDialog.getWindowToken(), 0);
            }
        });

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void submit() {
        // validate
        String password = binding.etLoginPin.getText().toString().trim();
        String username = binding.etLoginMobileNumber.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Util.errorToast(context, getString(R.string.mobile_number_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Util.errorToast(context, getString(R.string.password_required));
            return;
        }
        callLoginWebservice();
    }

    /*APIs*/
    private void callForgotPasswordWebService(String mobile, final Dialog dialog) {

        if (Util.hasInternet(this)) {
            progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("mobile", mobile);
                jsonObject.put("language", "eng");

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.forget_password_sp(new ConvertJsonToMap().jsonToMap(jsonObject));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {

                            JSONObject mJsonObj = new JSONObject(Util.convertRetrofitResponce(response));
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                Util.successToast(context, mJsonObj.getJSONObject("response").getString("message"));
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Util.dismissPrograssDialog(progressDialog);
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Util.dismissPrograssDialog(progressDialog);
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        } else {
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }

    private void callLoginWebservice() {

        if (Util.hasInternet(this)) {
            progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            String device_token = "";
            if (!TextUtils.isEmpty(MySharedPreferences.getDeviceId(Login.this))) {
                device_token = MySharedPreferences.getDeviceId(Login.this);
            } else {
                if (FirebaseInstanceId.getInstance().getToken() != null) {
                    device_token = FirebaseInstanceId.getInstance().getToken();
                    MySharedPreferences.setDeviceId(getApplicationContext(), device_token);
                }
            }
            if (!TextUtils.isEmpty(device_token)) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("device_type", getString(R.string.android));
                    jsonObject.put("device_id", device_token);
                    jsonObject.put("language", "eng");
                    jsonObject.put("mobile", binding.etLoginMobileNumber.getText().toString());
                    jsonObject.put("password", binding.etLoginPin.getText().toString());

                    MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);

                    call = endpointInterface.driver_login(new ConvertJsonToMap().jsonToMap(jsonObject));

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            Util.dismissPrograssDialog(progressDialog);
                            try {
                                JSONObject mJsonObj = new JSONObject(Util.convertRetrofitResponce(response));
                                Log.e("tg", "response from server = " + mJsonObj.toString());
                                if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                    JSONObject jsonObject1 = mJsonObj.getJSONObject("response").getJSONArray("data").getJSONObject(0);
                                    MySharedPreferences.setUserId(context, jsonObject1.getString("user_id"));
                                    MySharedPreferences.setFirstName(context, jsonObject1.getString("first_name"));
                                    MySharedPreferences.setLastName(context, jsonObject1.getString("last_name"));
                                    MySharedPreferences.setPhoneNumber(context, jsonObject1.getString("mobile"));
                                    MySharedPreferences.setNotificationSetting(context, jsonObject1.getString("notification"));
                                    MySharedPreferences.setNotificationCount(context, jsonObject1.getString("notification_count"));
                                    MySharedPreferences.setProfileImage(context, jsonObject1.getString("img"));
                                    // MySharedPreferences.setLanguage(context, jsonObject1.getString("language"));

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                                Util.dismissPrograssDialog(progressDialog);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Util.dismissPrograssDialog(progressDialog);
                            t.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    Util.dismissPrograssDialog(progressDialog);
                    e.printStackTrace();
                }
            } else {
                Util.dismissPrograssDialog(progressDialog);
                Util.errorToast(context, "There is some issue with access notification please try again.");
                if (TextUtils.isEmpty(MySharedPreferences.getDeviceId(Login.this))) {
                    if (FirebaseInstanceId.getInstance().getToken() != null) {
                        MySharedPreferences.setDeviceId(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());
                    }
                }
                return;
            }
        } else {
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }
}