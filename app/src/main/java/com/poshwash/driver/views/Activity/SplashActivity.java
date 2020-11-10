
package com.poshwash.driver.views.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.LocationService;
import com.poshwash.driver.Utils.MySharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends BaseActivity {
    private final static int REQUEST_LOCATION = 199;
    int SPLASH_HOLD_TIME = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        startService(new Intent(SplashActivity.this, LocationService.class));
        keyHash();
        Fabric.with(this, new Crashlytics());
        holdSplashScreen();

    }

    private void holdSplashScreen() {
        new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_HOLD_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GoNext();
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoNext();
            } else {
                finish();
            }
        }
    }

    public void GoNext() {
        Intent intent = null;
        if (TextUtils.isEmpty(MySharedPreferences.getUserId(SplashActivity.this))) {
            intent = new Intent(SplashActivity.this, Login.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    holdSplashScreen();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    finish();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    public void keyHash() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.poshwash.driver", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}