package com.poshwash.driver.views.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.Delegate.NavigationDelegate;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.ConvertJsonToMap;
import com.poshwash.driver.Utils.LocationService;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.poshwash.driver.databinding.ActivityMainBinding;
import com.poshwash.driver.databinding.DialogLogoutBinding;
import com.poshwash.driver.views.Fragment.CancelBookingNewFragment;
import com.poshwash.driver.views.Fragment.MyBooking;
import com.poshwash.driver.views.Fragment.MyProfile;
import com.poshwash.driver.views.Fragment.MyTransactionHistory;
import com.poshwash.driver.views.Fragment.Notification;
import com.poshwash.driver.views.Fragment.PendingRequestFragment;
import com.poshwash.driver.views.Fragment.Settings;
import com.poshwash.driver.views.Fragment.WashProcessFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Context context;
    public FragmentManager mFragmentManager = null;
    FragmentTransaction fragmentTransaction = null;
    public NavigationDelegate mNavigationDelegate;
    Fragment mFragment = null;
    public static DisplayImageOptions options;
    MyProgressDialog progressDialog;
    public static FragmentManager fragmentManager;
    public GoogleApiClient googleApiClient;
    static ActivityMainBinding binding;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        startService(new Intent(MainActivity.this, LocationService.class));
        mFragmentManager = getSupportFragmentManager();
        fragmentManager = getSupportFragmentManager();
        mNavigationDelegate = (NavigationDelegate) this;
        AutoSpaApplication.recent_activity = MainActivity.this;
        context = this;
        findViewByID();
        setOnClick();
        SetProfile();
        autoOnGPS();
        if (getIntent().getIntExtra("fragmentNumber", 0) == 1) {
            displayView(AutoSpaConstant.WASHPROCESSFRAGMENT, null, null);
        } else {

            if (!MySharedPreferences.getNotification(context).equals("")) {
                setHomeScreenFromNotification();
            } else {
                displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
            }
        }
    }

    public void findViewByID() {
        setSupportActionBar(binding.includeAppBar.contentMain.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setElevation(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.includeAppBar.contentMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {

                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                binding.includeAppBar.contentMain.toolbar.setTranslationX(slideOffset * drawerView.getWidth());
                binding.includeAppBar.contentMain.mainFrame.setTranslationX(slideOffset * drawerView.getWidth());
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        binding.navHeaderMain.userProfilePicIv.setOnClickListener(this);

        options = Util.displayOption(this);
        progressDialog = new MyProgressDialog(context);
        progressDialog.setCancelable(false);

    }

    public void setOnClick() {

        binding.navHeaderMain.navBookingVehicle.setOnClickListener(this);
        binding.navHeaderMain.navPendingBooking.setOnClickListener(this);
        binding.navHeaderMain.navTransactionHistory.setOnClickListener(this);
        binding.navHeaderMain.navSettings.setOnClickListener(this);
        binding.navHeaderMain.navLogout.setOnClickListener(this);
        binding.navHeaderMain.navHome.setOnClickListener(this);
        binding.navHeaderMain.navNotification.setOnClickListener(this);
        binding.navHeaderMain.navCancelBooking.setOnClickListener(this);
        binding.navHeaderMain.navTransactionPastBooking.setOnClickListener(this);
        binding.navHeaderMain.statusLl.setOnClickListener(this);

        binding.navHeaderMain.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (compoundButton.isPressed()) {
                    if (isChecked) {
                        NotificationSettingWebService("1");
                    } else {
                        NotificationSettingWebService("0");
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static void SetProfile() {
        if (MySharedPreferences.getProfileImage(context).compareTo("") != 0) {
            try {
                ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + MySharedPreferences.getProfileImage(context), binding.navHeaderMain.userProfilePicIv, options);
            } catch (Exception e) {
                binding.navHeaderMain.userProfilePicIv.setImageResource(R.drawable.no_image);
            }
        } else {
            binding.navHeaderMain.userProfilePicIv.setImageResource(R.drawable.no_image);
        }
        binding.navHeaderMain.userProfileNameTv.setText(MySharedPreferences.getFirstName(context) + " " + MySharedPreferences.getLastName(context));

        int rating = (int) Float.parseFloat(MySharedPreferences.getRating(context));
        binding.navHeaderMain.ratingBar.setRating(rating);

        if (Integer.parseInt(MySharedPreferences.getNotificationCount(context)) == 0) {
            binding.navHeaderMain.notificationCountTv.setVisibility(View.GONE);
        } else {
            binding.navHeaderMain.notificationCountTv.setText(MySharedPreferences.getNotificationCount(context));
            binding.navHeaderMain.notificationCountTv.setVisibility(View.VISIBLE);
        }

        if (MySharedPreferences.getNotificationSetting(context).compareTo("1") == 0) {
            binding.navHeaderMain.notificationSwitch.setChecked(true);
        } else {
            binding.navHeaderMain.notificationSwitch.setChecked(false);
        }
    }

    public void displayView(String fragmentName, Object obj, Object obj1) {

        fragmentTransaction = mFragmentManager.beginTransaction();

        switch (fragmentName) {
            case AutoSpaConstant.PENDINGREQUESTFRAGMENT: {
                mFragment = PendingRequestFragment.newInstance();
                setPendingBookingFragment();
            }
            break;
            case AutoSpaConstant.HOMEFRAGMENT: {
                mFragment = MyBooking.newInstance(context);
                setMyBookingFragment();
            }
            break;
            case AutoSpaConstant.BOOKINGHISTORY: {
                mFragment = MyBooking.newInstance(context);
                setMyBookingFragment();
            }
            break;
            case AutoSpaConstant.CANCELBOOKING: {
                mFragment = CancelBookingNewFragment.newInstance(context);
                setCancelBookingFragment();
            }
            break;
            case AutoSpaConstant.TRANSACTIONHISTORY: {
                mFragment = MyTransactionHistory.newInstance(context);
                setTransactionHistoryFragment();
            }
            break;
            case AutoSpaConstant.NOTIFICATION: {
                mFragment = Notification.newInstance(context);
                setNotificationFragment();
            }
            break;
            case AutoSpaConstant.SETTING: {
                mFragment = Settings.newInstance(context);
                setSettingFragment();
            }
            break;
            case AutoSpaConstant.PASTBOOKING: {
                mFragment = Settings.newInstance(context);
                setPastBookingFragment();
            }
            break;
            case AutoSpaConstant.PROFILE: {
                mFragment = MyProfile.newInstance(context);
                // setPastBookingFragment();
            }
            break;
            case AutoSpaConstant.WASHPROCESSFRAGMENT: {
                mFragment = WashProcessFragment.newInstance(obj, obj1);
            }
            break;
        }
        if (mFragment != null) {
            try {
                fragmentTransaction.replace(R.id.mainFrame, mFragment).addToBackStack(fragmentName);
                fragmentTransaction.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void OpenDrawer() {
        if (!binding.drawerLayout.isDrawerVisible(GravityCompat.END)) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  Toast.makeText(context, "calll", Toast.LENGTH_SHORT).show();
            //updateDate(intent);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_home:
                displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
                DrawerClose();
                break;
           /* case R.id.nav_manage_vehicle:
                displayView(AutoSpaConstant.ADDFRAGMENT, null, null);
                DrawerClose();
                break;*/
            case R.id.nav_booking_vehicle:
                displayView(AutoSpaConstant.BOOKINGHISTORY, null, null);
                DrawerClose();
                break;
            case R.id.nav_pending_booking:
                displayView(AutoSpaConstant.PENDINGREQUESTFRAGMENT, null, null);
                DrawerClose();
                break;
            case R.id.nav_cancel_booking:
                displayView(AutoSpaConstant.CANCELBOOKING, null, null);
                DrawerClose();
                break;
            case R.id.nav_transaction_history:
                displayView(AutoSpaConstant.TRANSACTIONHISTORY, null, null);
                DrawerClose();
                break;
            case R.id.nav_notification:
                displayView(AutoSpaConstant.NOTIFICATION, null, null);
                DrawerClose();
                break;
            case R.id.nav_settings:
                displayView(AutoSpaConstant.SETTING, null, null);
                DrawerClose();
                break;
            case R.id.status_ll:
                if (MySharedPreferences.getSP_status(context).compareTo("1") == 0) {
                    driverStatusChange("0");
                    binding.navHeaderMain.statusSwitch.setChecked(false);
                    binding.navHeaderMain.statusTv.setText(getString(R.string.offline));
                    binding.navHeaderMain.statusTv.setTextColor(ContextCompat.getColor(context, R.color.offlineColor));
                    MySharedPreferences.setSP_status(context, "0");
                } else {
                    binding.navHeaderMain.statusSwitch.setChecked(true);
                    binding.navHeaderMain.statusTv.setText(getString(R.string.online));
                    binding.navHeaderMain.statusTv.setTextColor(ContextCompat.getColor(context, R.color.onlineColor));
                    driverStatusChange("1");
                    MySharedPreferences.setSP_status(context, "1");
                }

                if (mFragment instanceof PendingRequestFragment) {
                    ((PendingRequestFragment) mFragment).updateStatus();
                }

                //DrawerClose();
                break;
            case R.id.user_profile_pic_iv:
//                displayView(AutoSpaConstant.PROFILE, null, null);
//                DrawerClose();
                break;

            case R.id.nav_logout:
                final Dialog dialog;

                dialog = new Dialog(MainActivity.this);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                DialogLogoutBinding dialogLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_logout, null, false);
                dialog.setContentView(dialogLogoutBinding.getRoot());
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                dialog.show();

                dialogLogoutBinding.dialogTwoactionDescss.setText(getResources().getString(R.string.logout_message));

                dialogLogoutBinding.dialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogoutWebService(getApplicationContext(), true);
                        dialog.dismiss();

                    }
                });

                dialogLogoutBinding.dialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
    }

    public void DrawerClose() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrame);

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (currentFragment instanceof MyBooking) {
            finish();
        } else {
            displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
        }
           /*

            if (currentFragment instanceof HomeFragment) {
                finish();
            } else {
                displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
            }*/

    }

    public void setMyBookingFragment() {

        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.colorskyblue));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));

        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void setPendingBookingFragment() {

        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.colorskyblue));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));

        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void setCancelBookingFragment() {

        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.skybluecolor));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));

        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void setTransactionHistoryFragment() {

        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.skybluecolor));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));


        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void setNotificationFragment() {

        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.skybluecolor));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));


        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_OVER);
    }

    public void setSettingFragment() {

        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));

        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.skybluecolor));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));

        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void setPastBookingFragment() {

        binding.navHeaderMain.transactionPastBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.homeMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.pendingBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.bookingVehicleMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.cancelBookingMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.transactionHistoryMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.notificationMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.settingsMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));
        binding.navHeaderMain.logoutMenu.setTextColor(ContextCompat.getColor(this, R.color.graycolor));


        binding.navHeaderMain.navTransactionPastBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.skybluecolor));
        binding.navHeaderMain.navHome.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navBookingVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navPendingBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navCancelBooking.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navTransactionHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navNotification.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));
        binding.navHeaderMain.navLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitecolor));

        binding.navHeaderMain.notificationCountTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        binding.navHeaderMain.notificationCountTv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
    }

    public void SetLogout(Context context) {

        String deivce_token = MySharedPreferences.getDeviceId(context);
        MySharedPreferences.ClearAll(context);
        MySharedPreferences.setUserEmail(context, "");
        MySharedPreferences.setDeviceId(context, deivce_token);
        Intent intent = new Intent(context, Login.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.back_slide_in_up, R.anim.back_slide_out_up);
    }

    @Override
    public void executeFragment(String fragmentName, Object obj) {

    }

    @Override
    public void goBack() {

    }

    public void autoOnGPS() {
        googleApiClient = null;
        AutoSpaApplication.mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        AutoSpaApplication.mGoogleApiClient.connect();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); // this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 1000);
                        } catch (Exception e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            startService(new Intent(MainActivity.this, LocationService.class));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrame);

        if (currentFragment instanceof WashProcessFragment) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void setHomeScreenFromNotification() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrame);

        if (!MySharedPreferences.getNotification(context).equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(MySharedPreferences.getNotification(context));
                int notification_type = 0;
                notification_type = Integer.parseInt(jsonObject.getString("type"));

                if (notification_type == 2)// Request For Accept PN
                {
                    displayView(AutoSpaConstant.PENDINGREQUESTFRAGMENT, null, null);
                } else if (notification_type == 11) {
                    displayView(AutoSpaConstant.CANCELBOOKING, null, null);
                } else if (notification_type == 10) {
                    displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
                } else {
                    if (currentFragment instanceof WashProcessFragment) {
                        final WashProcessFragment home = (WashProcessFragment) currentFragment;
                        if (!MySharedPreferences.getNotification(context).equals("")) {
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    home.getNoticationData(MySharedPreferences.getNotification(context));
                                }
                            });
                        }
                    } else {
                        displayView(AutoSpaConstant.HOMEFRAGMENT, null, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*APIs*/
    public void LogoutWebService(final Context context, final boolean status) {

        if (Util.hasInternet(this)) {

            if (progressDialog != null)
                progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("user_type", "2");
                jsonObject.put("language", "eng");

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.logOut(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(response.body().string());
                            Log.e("tg", "response from server = " + mJsonObj.toString());
                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                if (status)
                                    SetLogout(context);
                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }

                        } catch (Exception e) {
                            Log.e("error", e.toString());
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
        } else {
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }

    private void NotificationSettingWebService(final String status) {

        if (Util.hasInternet(this)) {
            progressDialog.show();
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();

                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("user_type", "2");
                jsonObject.put("not_status", status);
                jsonObject.put("language", "eng");

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.update_notification_setting(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Util.dismissPrograssDialog(progressDialog);
                        try {
                            JSONObject mJsonObj = new JSONObject(new String(response.body().bytes()));
                            Log.e("tg", "response from server = " + mJsonObj.toString());

                            if (mJsonObj.getJSONObject("response").getBoolean("status")) {
                                MySharedPreferences.setNotificationSetting(context, status);
                                //SetProfile();
                            } else {
                                Util.errorToast(context, mJsonObj.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }

    private void driverStatusChange(String status) {

        if (Util.hasInternet(this)) {
            JSONObject jsonObject = null;
            Call<ResponseBody> call = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject.put("language", "eng");
                jsonObject.put("status", status);

                MyApiEndpointInterface endpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = endpointInterface.update_status(new ConvertJsonToMap().jsonToMap(jsonObject));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
        } else {
            Util.errorToast(this, getResources().getString(R.string.no_internet));
        }
    }
}