package com.poshwash.driver.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    final static String AUTOSPA_SHARED_PREFERENCE = "autoSpa";

    public static void ClearAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    public static void setUserId(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("UserId", unit).apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("UserId", "");
    }

    public static void setFirstName(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("FirstName", unit).apply();
    }

    public static String getFirstName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("FirstName", "");
    }

    public static void setLastName(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("LastName", unit).apply();
    }

    public static String getLastName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("LastName", "");
    }

    public static void setUserEmail(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("UserEmail", unit).apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("UserEmail", "");
    }

    public static void setPhoneNumber(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("PhoneNumber", unit).apply();
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("PhoneNumber", "");
    }

    public static void setLanguage(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("Language", unit).apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("Language", "eng");
    }

    public static void setAddress(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("address", unit).apply();
    }

    public static String getAddress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("address", "");
    }

    public static void setDeviceId(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("DeviceId", unit).apply();

    }

    public static String getDeviceId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("DeviceId", "");
    }


    public static void setIsFirstTime(Context context, String unit) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("isfirstTime", unit).apply();
    }

    public static String getIsFirstTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("isfirstTime", "");
    }

    public static void setProfileImage(Context context, String unit1) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("ProfileImage", unit1).apply();
    }

    public static String getProfileImage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("ProfileImage", "");
    }

    public static void setLongitude(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("Longitude", UserName).apply();
    }

    public static String getLongitude(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("Longitude", "");
    }

    public static void setLatitude(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("Latitude", UserName).apply();
    }

    public static String getLatitude(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("Latitude", "");
    }

    public static void setRating(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("Rating", UserName).apply();
    }

    public static String getRating(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("Rating", "0");
    }

    public static void setNotificationSetting(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("notificationSetting", UserName).apply();
    }

    public static String getNotificationSetting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("notificationSetting", "");
    }

    public static void setSP_status(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("SP_status", UserName).apply();
    }

    public static String getSP_status(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("SP_status", "");
    }

    public static void setNotification(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("Notification", UserName).apply();
    }

    public static String getNotification(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("Notification", "");
    }

    public static void setRatingStatus(Context context, boolean UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putBoolean("RatingStatus", UserName).apply();
    }

    public static boolean getRatingStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getBoolean("RatingStatus", false);
    }

    public static void setBookingDetails(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("BookingDetails", UserName).apply();
    }

    public static String getBookingDetails(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("BookingDetails", "");
    }

    public static void setNotificationCount(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("NotificationCount", UserName).apply();
    }

    public static String getNotificationCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("NotificationCount", "");
    }

    public static void setbookingTimer(Context context, String UserName) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString("bookingTimer", UserName).apply();
    }

    public static String getbookingTimer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTOSPA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getString("bookingTimer", "0");
    }
}