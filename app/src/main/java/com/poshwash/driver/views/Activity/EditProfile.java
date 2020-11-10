package com.poshwash.driver.views.Activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.poshwash.driver.Constant.AutoSpaConstant;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by anandj on 3/17/2017.
 */

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_icon;
    private TextView txt_heading;
    private RelativeLayout rr_toolbar;
    private CircleImageView user_profleimg;
    private EditText edt_entername;
    private EditText edt_enteremail;
    private EditText edt_phonenumber;
    private EditText edt_compnayname;
    private EditText edt_address;
    private EditText edt_address2;
    private TextView company_name_tv;
    private Button btn_save;
    private DisplayImageOptions options;
    private RelativeLayout rr_camera;
    private String fname;
    private Uri outputFileUri;
    private Context context;
    private MyProgressDialog progressDialog;
    private File photoFile = null;
    private final String TAG = "EditProfile.java";
    private String lat = "", lng = "";
    int PLACE_PICKER_REQUEST = 200;
    String existphone_number = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        // getSupportActionBar().hide();
        setContentView(R.layout.editprofile);
        context = this;
        initView();
        //initAdapter();
    }

    private void initView() {

        back_icon = (ImageView) findViewById(R.id.back_icon);
        txt_heading = (TextView) findViewById(R.id.txt_heading);
        rr_toolbar = (RelativeLayout) findViewById(R.id.rr_toolbar);
        user_profleimg = (CircleImageView) findViewById(R.id.user_profleimg);
        edt_entername = (EditText) findViewById(R.id.edt_entername);
        edt_enteremail = (EditText) findViewById(R.id.edt_enteremail);
        edt_phonenumber = (EditText) findViewById(R.id.edt_phonenumber);

        edt_address = (EditText) findViewById(R.id.edt_address);
        btn_save = (Button) findViewById(R.id.btn_save);
        rr_camera = (RelativeLayout) findViewById(R.id.rr_camera);
        btn_save.setOnClickListener(this);
        back_icon.setOnClickListener(this);
        rr_camera.setOnClickListener(this);

        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);

        edt_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(EditProfile.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        options = Util.displayOption(context);

        autoFillData();
    }

    @SuppressLint("SetTextI18n")
    private void autoFillData() {
        edt_entername.setText(MySharedPreferences.getFirstName(EditProfile.this) + " " + MySharedPreferences.getLastName(EditProfile.this));
        edt_enteremail.setText(MySharedPreferences.getUserEmail(context));
        edt_phonenumber.setText(MySharedPreferences.getPhoneNumber(context));
        existphone_number = MySharedPreferences.getPhoneNumber(context);

        edt_address.setText(MySharedPreferences.getAddress(context));

        lat = MySharedPreferences.getLatitude(context);
        lng = MySharedPreferences.getLongitude(context);

        Util.setProfilePic(context, user_profleimg);

        if (edt_entername.getText().length() != 0)
            edt_entername.setSelection(edt_entername.getText().length());

    }

    private void submit() {
        // validate
        String entername = edt_entername.getText().toString().trim();
        if (TextUtils.isEmpty(entername)) {
            Util.setError(context, edt_entername);
            return;
        } else if (TextUtils.getTrimmedLength(entername) < 1) {
            Toast.makeText(context, "Enter valid name", Toast.LENGTH_SHORT).show();
            return;
        }


     /*   String enteremail = edt_enteremail.getText().toString().trim();
        if (TextUtils.isEmpty(enteremail)) {
            edt_enteremail.setError(getString(R.string.cannotleftblank));
            return;
        }
        if(!Util.isValidEmail(enteremail)){
            edt_enteremail.setError(getString(R.string.validation_error));
            return;
        }*/

        String phonenumber = edt_phonenumber.getText().toString().trim();
        if (TextUtils.isEmpty(phonenumber)) {
            Util.setError(context, edt_phonenumber);
            return;
        }
        if (TextUtils.getTrimmedLength(phonenumber) < 9) {
            Toast.makeText(context, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String compnayname = edt_compnayname.getText().toString().trim();
       /* if (TextUtils.isEmpty(compnayname)) {
            Util.setError(context,edt_compnayname);
            return;
        }*/

        String address = edt_address.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Util.setError(context, edt_address);
            return;
        }

        String address1 = edt_address2.getText().toString().trim();

        boolean is_update = false;
        if (existphone_number.compareTo(phonenumber) == 0) {
            is_update = false;
        } else {
            is_update = true;
        }

        //  editProfileWebservice(entername, phonenumber, compnayname, address, address1, is_update);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                hideSoftKeyboard();
                submit();
                break;
            case R.id.back_icon:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.rr_camera:
                openImageIntent();
                break;
        }
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openImageIntent() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Wayndr" + File.separator);
        root.mkdirs();
        fname = Util.getUniqueImageName();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EditProfile.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("path", String.valueOf(outputFileUri));
        editor.apply();
        Log.e("SetUpProfile", "Uri is " + outputFileUri);


        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, AutoSpaConstant.YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {

                Place place = PlacePicker.getPlace(context, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Log.e("place", toastMsg);
                edt_address.setText(place.getAddress());
                lat = place.getLatLng().latitude + "";
                lng = place.getLatLng().longitude + "";

            }

            if (requestCode == AutoSpaConstant.YOUR_SELECT_PICTURE_REQUEST_CODE) {

                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EditProfile.this);
                    String value = prefs.getString("path", "error");
                    selectedImageUri = Uri.parse(value);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                if (selectedImageUri == null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EditProfile.this);
                    String value = prefs.getString("path", "error");
                    selectedImageUri = Uri.parse(value);
                }

                Bitmap bitmap = null;
                Log.d("SetUpProfile", "Uri cropped is " + outputFileUri);
                //     bitmap = Util.getBitmap(EditProfile.this, selectedImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                        photoFile = new File(Util.getPathForBelowKitkat(context, selectedImageUri));
                    else {
                        photoFile = new File(Util.getPath(context, selectedImageUri));
                    }

                    user_profleimg.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void callWebserviceDeleteProfilePic() {
        progressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.removeProfilePic(new ConvertJsonToMap().jsonToMap(jsonObject));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(response.body().string());
                        Log.e("tg", "response from server = " + mJsonObj.toString());
                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            user_profleimg.setImageResource(R.drawable.defalut_bg);
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Util.dismissPrograssDialog(progressDialog);

                }
            });
        } catch (Exception e) {
            Util.dismissPrograssDialog(progressDialog);
        }
    }


    // Call webservice
    private void editProfileWebservice(final String entername, final String phonenumber, final String compnayname, final String address, String address1, final boolean is_update) {
        progressDialog.show();
        JSONObject jsonObject = null;
        HashMap<String, RequestBody> map = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            map = new HashMap<>();
            jsonObject.put("first_name", entername);
            jsonObject.put("contact", phonenumber);
            jsonObject.put("company_name", compnayname);
            jsonObject.put("address", address);
            jsonObject.put("address2", address1);
            jsonObject.put("latitude", lat);
            jsonObject.put("longitude", lng);

            jsonObject.put("user_id", MySharedPreferences.getUserId(context));

            RequestBody part_data = RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.toString());
            map.put("data", part_data);

            MultipartBody.Part firstImage = null;


            if (photoFile != null) {
                RequestBody requestFileWall1 = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
                firstImage = MultipartBody.Part.createFormData("photo", photoFile.getName(), requestFileWall1);
            }

            MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            if (firstImage != null)
                call = myApiEndpointInterface.editProfile(map, firstImage);
            else
                call = myApiEndpointInterface.editProfileWithoutImage(map);

            final MultipartBody.Part finalFirstImage = firstImage;
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(response.body().string());
                        Log.e("tg", "response from server = " + mJsonObj.toString());
                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            Util.Toast(context, mJsonObj.getJSONObject("response").getString("message"));

                            if (!is_update) {
                                finish();
                            } else {
                                VerfyMobileWebService(phonenumber);
                            }


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
            Log.e(TAG, e.toString());
        }
    }

    private void VerfyMobileWebService(final String phonenumber) {
        progressDialog.show();
        JSONObject jsonObject = null;
        Call<ResponseBody> call = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("user_id", MySharedPreferences.getUserId(context));
            jsonObject.put("contact", phonenumber);

            MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
            call = endpointInterface.generateOtp(new ConvertJsonToMap().jsonToMap(jsonObject));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Util.dismissPrograssDialog(progressDialog);
                    try {
                        JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                        Log.e("tg", "response from server = " + mJsonObj.toString());

                        if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                            Intent intent = new Intent(context, PhoneNumberVerify.class);
                            intent.putExtra("number", phonenumber);
                            intent.putExtra("otp", mJsonObj.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("otp"));

                            startActivity(intent);
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