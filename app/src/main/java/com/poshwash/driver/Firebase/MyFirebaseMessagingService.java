package com.poshwash.driver.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.views.Activity.SplashActivity;
import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        MySharedPreferences.setDeviceId(getApplicationContext(), refreshedToken);
        Log.d(TAG, "Token : " + refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("message"));

        if (!TextUtils.isEmpty(MySharedPreferences.getUserId(getApplicationContext()))) {
            try {
                if (!TextUtils.isEmpty(MySharedPreferences.getUserId(this))) {

                    JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("message"));
                    getNotificationType(jsonObject);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            getNotificationType(remoteMessage.getData().get("message"));
        }
    }

    private void getNotificationType(JSONObject jsonObject) {
        try {
//            JSONObject jsonObject = new JSONObject(message);
            String messagea = jsonObject.optString("msg");
            int notification_type = Integer.parseInt(jsonObject.getString("type"));

            switch (notification_type) {
                case 1: {
                    Intent intent = new Intent(this, MainActivity.class);
                    String title = getString(R.string.app_name);
                    notificationManager(intent, title, messagea);
                }
                break;
                case 2: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 3: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 4: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 5: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 6: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 7: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 8: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 11: {
                    BookingRequestNotification(jsonObject);
                }
                break;
                case 121: {
                    ShowReminderNotification(jsonObject);
                }
                break;
                case 777: {
                    showProductsUpdateNotification(messagea);
                }
                break;
                case 778: {
                    showBockUser(messagea);
                }
                break;
                default: {
                    Intent intent = new Intent(this, MainActivity.class);
                    String title = getString(R.string.app_name);
                    notificationManager(intent, title, messagea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BookingRequestNotification(JSONObject jsonObject) throws JSONException {

        MySharedPreferences.setNotification(this, jsonObject.toString());

        if (Util.isActivityFound(this)) {
            if (AutoSpaApplication.recent_activity.getClass().getName().compareToIgnoreCase("com.poshwash.driver.views.Activity.MainActivity") == 0) {
                ((MainActivity) AutoSpaApplication.recent_activity).setHomeScreenFromNotification();
            }
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            String title = getString(R.string.app_name);
            String message = jsonObject.getString("msg");

            notificationManager(intent, title, message);

//            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(launchIntent);
        }
    }

    public void showProductsUpdateNotification(String message) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragmentNumber", 1);

            String title = getString(R.string.app_name);
            notificationManager(intent, title, message);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void ShowReminderNotification(JSONObject jsonObject) {
        try {
            Intent intent = new Intent(this, BookingDetail.class);
            intent.putExtra("bookingId", jsonObject.getString("booking_id"));
            intent.putExtra("bookingDriverId", jsonObject.getString("Booking_driver_id"));

            String title = getString(R.string.app_name);
            String message = jsonObject.optString("msg");
            notificationManager(intent, title, message);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void showBockUser(String message) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.LogoutWebService(getApplicationContext(), false);

                    SetLogout();

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void SetLogout() {

        Context context = getApplicationContext();
        String deivce_token = MySharedPreferences.getDeviceId(getApplicationContext());
        MySharedPreferences.ClearAll(getApplicationContext());
        MySharedPreferences.setUserEmail(getApplicationContext(), "");
        MySharedPreferences.setDeviceId(context, deivce_token);
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void notificationManager(Intent intent, String title, String message) {

        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.channel_id))
                    .setSmallIcon(getNotificationIcon())//R.drawable.ic_stat_ic_notification
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.channel_id), "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify(0, notificationBuilder.build());
                }
            } else {
                if (notificationManager != null) {
                    notificationManager.notify(0, notificationBuilder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_icon : R.drawable.app_icon;
    }
}