package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private ImageView toggle_icon;
    private EditText edt_oldpassword;
    private EditText edt_newpassword;
    private EditText edt_confimrpassword;
    private Button btn_submit;
    MyProgressDialog progressDialog;
    String oldpassword, newpassword, confimrpassword;
    Context context;
    final String TAG = "ChangePassword.java";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.change_password);
        context = this;
        initView();
    }

    private void initView() {
        toggle_icon = (ImageView) findViewById(R.id.toggle_icon);
        edt_oldpassword = (EditText) findViewById(R.id.edt_oldpassword);
        edt_newpassword = (EditText) findViewById(R.id.edt_newpassword);
        edt_confimrpassword = (EditText) findViewById(R.id.edt_confimrpassword);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(this);
        toggle_icon.setOnClickListener(this);

        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                submit();
                break;
            case R.id.toggle_icon:
                finish();
                break;
        }
    }

    private void submit() {
        // validate
        oldpassword = edt_oldpassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldpassword)) {
            Util.Toast(context, getString(R.string.enteroldpassword));
            return;
        }

        newpassword = edt_newpassword.getText().toString().trim();
        if (TextUtils.isEmpty(newpassword)) {
            Util.Toast(context, getString(R.string.enternewpassword));
            return;
        }
        if (!Util.isValidPassword(newpassword)) {
            Util.Toast(context, getString(R.string.validation_error_password));
            return;
        }

        confimrpassword = edt_confimrpassword.getText().toString().trim();
        if (TextUtils.isEmpty(confimrpassword)) {
            Util.Toast(context, getString(R.string.enterconfirmpassword));
            return;
        }

        if (!newpassword.equals(confimrpassword)) {
            Toast.makeText(this, getString(R.string.confirmpassworddoesnotmatch), Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePassswordWebService();
    }

    private void ChangePassswordWebService() {
        progressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));
            jsonObject.put("new_password", newpassword);
            jsonObject.put("cofirm_password", newpassword);
            jsonObject.put("old_password", oldpassword);
            jsonObject.put("language", "eng");

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.changePassword(new ConvertJsonToMap().jsonToMap(jsonObject));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                        Log.e("tg", "response from server = " + mJsonObj.toString());

                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            Util.Toast(context, mJsonObj.getJSONObject("response").getString("message"));
                            finish();
                        } else {
                            Util.Toast(context, mJsonObj.getJSONObject("response").getString("message"));
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
