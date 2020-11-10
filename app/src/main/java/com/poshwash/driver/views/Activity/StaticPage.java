package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
 * Created by anand j on 3/30/2017.
 */

public class StaticPage extends AppCompatActivity implements View.OnClickListener {

    TextView txt_term_condition;
    MyProgressDialog progressDialog;
    TextView txt_title;
    Context context;
    String TAG = "StaticPage.java";
    private ImageView toggle_icon;
    String page_slug = "";
    String page_name = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_condition);
        context = this;
        initView();
        page_slug = getIntent().getStringExtra("pagetype");
        page_name = getIntent().getStringExtra("pagename");
        txt_title.setText(page_name);
        callWebServiceStaticContent();
    }

    private void callWebServiceStaticContent() {
        progressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {

            jsonObject = new JSONObject();
            jsonObject.put("page_slug", page_slug);
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
                            txt_term_condition.setText(Util.fromHtml(mJsonObj.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("contents")));
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

    private void initView() {
        toggle_icon = (ImageView) findViewById(R.id.toggle_icon);
        txt_term_condition = (TextView) findViewById(R.id.txt_term_condition);
        txt_title = (TextView) findViewById(R.id.txt_title);
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);

        toggle_icon.setOnClickListener(this);

        txt_title.setText(Util.getPageName(getIntent().getIntExtra("pagetype", -1), context));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_icon:
                finish();
                break;
        }
    }
}

