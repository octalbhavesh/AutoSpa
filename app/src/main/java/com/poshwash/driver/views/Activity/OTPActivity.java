package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.Libs.SmsListener;
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

public class OTPActivity extends AppCompatActivity implements View.OnClickListener, SmsListener.OnSmsReceivedListener {

    Context context;
    EditText otp_et;
    Button verify_btn;
    TextView regenrate_otp;
    TextView otp_text;
    MyProgressDialog progressDialog;
    String verify_otp = "";
    Bundle bundle;
    private SmsListener receiver;
    String type = "";
    private long mLastVerifyBtnClickTime = 0, mLastRegenrateOTPClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);
        context = this;

        receiver = new SmsListener();
        receiver.setOnSmsReceivedListener(this);

        progressDialog = new MyProgressDialog(context);
        progressDialog.setCancelable(false);

        otp_et = (EditText) findViewById(R.id.edt_otp);
        verify_btn = (Button) findViewById(R.id.btn_submit);
        regenrate_otp = (TextView) findViewById(R.id.regenrate_otp);
        otp_text = (TextView) findViewById(R.id.otp_text);

        verify_btn.setOnClickListener(this);
        regenrate_otp.setOnClickListener(this);

        verify_otp = getIntent().getStringExtra("otp");
        type = getIntent().getStringExtra("type");
        bundle = getIntent().getExtras();
        otp_et.setText(verify_otp);
    }

    @Override
    public void onClick(View v) {
        if (v == verify_btn) {

            if (SystemClock.elapsedRealtime() - mLastVerifyBtnClickTime < 1000) {
                return;
            }
            mLastVerifyBtnClickTime = SystemClock.elapsedRealtime();

            if (verify_btn.isEnabled()) {
                verify_btn.setEnabled(false);
                String otp = otp_et.getText().toString();

                if (TextUtils.isEmpty(otp)) {
                    verify_btn.setEnabled(true);
                    Util.setError(context, otp_et);
                    return;
                }
                if (otp.compareToIgnoreCase(verify_otp) != 0) {
                    verify_btn.setEnabled(true);
                    Toast(getString(R.string.valid_otp));
                    return;
                }

                if (type != null && type.equalsIgnoreCase("forgot")) {
                    Intent intent = new Intent(context, ResetPassword.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

        if (v == regenrate_otp) {
            if (SystemClock.elapsedRealtime() - mLastRegenrateOTPClickTime < 1000) {
                return;
            }
            mLastRegenrateOTPClickTime = SystemClock.elapsedRealtime();

            ReganrateCallWebService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AutoSpaApplication.getInstance().setLocale(MySharedPreferences.getLanguage(context), context);
    }

    private void ReganrateCallWebService() {
        progressDialog.show();
        Call<ResponseBody> call = null;
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("mobile", bundle.getString("mobile"));
            jsonObject1.put("country_code", bundle.getString("country_code"));
            jsonObject1.put("language", "eng");

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.resendOtp(new ConvertJsonToMap().jsonToMap(jsonObject1));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        // parse response
                        JSONObject jsonObject1 = new JSONObject(response.body().string());
                        if (jsonObject1.getJSONObject("response").getBoolean("status")) {
                            verify_otp = jsonObject1.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("mobile_otp");
                            otp_et.setText(verify_otp);
                        } else {
                            Util.Toast(context, jsonObject1.getJSONObject("response").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    @Override
    public void onSmsReceived(String otp) {
        try {
            otp_et.setText(otp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}