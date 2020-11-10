package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinava on 6/6/2017.
 */

public class PhoneNumberVerify extends AppCompatActivity implements View.OnClickListener {

    Context context;
    EditText otp_et;
    Button verify_btn;
    LinearLayout regenrate_otp;
    MyProgressDialog progressDialog;
    String verify_otp = "";
    Bundle bundle;
    String number = "";
    private long mLastSubmitClickTime = 0, mLastRegenrateOTPClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);
        context = this;
        progressDialog = new MyProgressDialog(context);
        progressDialog.setCancelable(false);

        otp_et = (EditText) findViewById(R.id.edt_otp);
        verify_btn = (Button) findViewById(R.id.btn_submit);
        regenrate_otp = (LinearLayout) findViewById(R.id.regenrate_otp);

        verify_btn.setOnClickListener(this);
        regenrate_otp.setOnClickListener(this);

        verify_otp = getIntent().getStringExtra("otp");
        number = getIntent().getStringExtra("number");

        otp_et.setText(verify_otp);
        bundle = getIntent().getExtras();
    }

    public void recivedSms(String message) {
        try {
            otp_et.setText(message);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        if (v == verify_btn) {

            if (SystemClock.elapsedRealtime() - mLastSubmitClickTime < 1000) {
                return;
            }
            mLastSubmitClickTime = SystemClock.elapsedRealtime();

            String otp = otp_et.getText().toString();
            if (TextUtils.isEmpty(otp)) {
                Util.setError(context, otp_et);
                return;
            }
            if (otp.compareToIgnoreCase(verify_otp) == 0) {
                VerifyCallWebService(number);
            } else {
                Toast("Please enter valid OTP");
            }
        }

        if (v == regenrate_otp) {
            if (SystemClock.elapsedRealtime() - mLastRegenrateOTPClickTime < 1000) {
                return;
            }
            mLastSubmitClickTime = SystemClock.elapsedRealtime();
            ReganrateCallWebService();
        }
    }

    private void VerifyCallWebService(String verify_otp) {
        progressDialog.show();
        Call<ResponseBody> call = null;
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("user_id", MySharedPreferences.getUserId(context));
            jsonObject1.put("contact", verify_otp);
            jsonObject1.put("language", "eng");

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.updateContactNumber(new ConvertJsonToMap().jsonToMap(jsonObject1));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        // parse response
                        JSONObject jsonObject1 = new JSONObject(response.body().string());
                        if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                            finish();
                        } else {
                            Util.Toast(context, jsonObject1.getJSONObject("response").getString("message"));
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Util.dismissPrograssDialog(progressDialog);
                }
            });
        } catch (Exception e) {
            Util.dismissPrograssDialog(progressDialog);

        }
    }

    private void ReganrateCallWebService() {
        progressDialog.show();
        Call<ResponseBody> call = null;
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("user_id", MySharedPreferences.getUserId(context));
            jsonObject1.put("contact", number);
            jsonObject1.put("language", "eng");


            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.generateOtp(new ConvertJsonToMap().jsonToMap(jsonObject1));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        // parse response
                        JSONObject jsonObject1 = new JSONObject(response.body().string());
                        if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                            verify_otp = jsonObject1.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("otp");
                            otp_et.setText(verify_otp);
                        } else {
                            Util.Toast(context, jsonObject1.getJSONObject("response").getString("message"));
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Util.dismissPrograssDialog(progressDialog);
                }
            });
        } catch (Exception e) {
            Util.dismissPrograssDialog(progressDialog);

        }
    }


    public void Toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.back_slide_in_up, R.anim.back_slide_out_up);
    }
}
