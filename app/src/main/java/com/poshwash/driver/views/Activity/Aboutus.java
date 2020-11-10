package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
 * Created by anandj on 3/17/2017.
 */

public class Aboutus extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private ImageView toggle_icon;
    private MyProgressDialog progressDialog;
    private TextView txt_abountus;
    private final String TAG = "Aboutus.java";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.aboutus);
        context = this;
        toggle_icon = (ImageView) findViewById(R.id.toggle_icon);
        txt_abountus = (TextView) findViewById(R.id.txt_abountus);
        toggle_icon.setOnClickListener(this);
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);
        callWebServiceAbountUs();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_icon:
                finish();
                break;
        }
    }

    private void callWebServiceAbountUs() {
        progressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("slug", getResources().getStringArray(R.array.static_contents)[0]);
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));
            jsonObject.put("language", "eng");
            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.static_content(new ConvertJsonToMap().jsonToMap(jsonObject));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                        Log.e("tg", "response from server = " + mJsonObj.toString());
                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            txt_abountus.setText(Util.fromHtml(mJsonObj.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("content")));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
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
    }
}