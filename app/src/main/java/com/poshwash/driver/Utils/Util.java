package com.poshwash.driver.Utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.poshwash.driver.Constant.AutoSpaApplication;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.poshwash.driver.Delegate.MyApiEndpointInterface;
import com.poshwash.driver.R;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anandj on 3/16/2017.
 */

public class Util {

    final static String TAG = "Util.java";

    public static String getUniqueImageName() {
        //will generate a random num
        //between 15-10000
        Random r = new Random();
        int num = r.nextInt(10000 - 15) + 15;
        String fileName = "img_" + num + ".png";
        return fileName;
    }

    public static void showShimmerFrameLayout(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
    }

    public static void dismissShimmerFrameLayout(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
    }

    public static void setError(Context context, EditText text) {
        text.setHint(context.getString(R.string.cannotleftblank));
        text.setHintTextColor(ContextCompat.getColor(context, R.color.redcolor));
        //  text.requestFocus();
    }

    public static void setError(Context context, TextView text) {
        text.setError(context.getString(R.string.cannotleftblank));
        text.requestFocus();
    }

    public static void errorToast(Context context, String message) {
        try {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(context).inflate(R.layout.custom_toast_view, null, false);
            TextView textView = view.findViewById(R.id.customToastText);
            textView.setText(message);

            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void successToast(Context context, String message) {
        try {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(context).inflate(R.layout.custom_toast_view, null, false);
            TextView textView = view.findViewById(R.id.customToastText);
            textView.setText(message);
            textView.setBackground(context.getResources().getDrawable(R.drawable.background_toast_green));

            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissPrograssDialog(MyProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static Typeface setTypefaceRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/lato/lato_regular.ttf");
    }

    public static String getPathForBelowKitkat(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "Not Found";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void callWebserviceUpdateLocation(String lat, String lng, Context context) {

        if (hasInternet(context)) {
            Call<ResponseBody> call = null;
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = new JSONObject();
                jsonObject1.put("user_id", MySharedPreferences.getUserId(context));
                jsonObject1.put("language", MySharedPreferences.getLanguage(context));
                jsonObject1.put("latitude", lat);
                jsonObject1.put("longitude", lng);

                MyApiEndpointInterface myApiEndpointInterface = AutoSpaApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
                call = myApiEndpointInterface.updateLocation(new ConvertJsonToMap().jsonToMap(jsonObject1));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject mjJsonObject = new JSONObject(new String(response.body().bytes()));
                            if (mjJsonObject.getJSONObject("response").getBoolean("status")) {
                                Log.v(TAG, mjJsonObject.getJSONObject("response").getString("message"));
                            } else {
                                //Log.e(TAG,mjJsonObject.getJSONObject("response").getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /*
        This method gets user's current location and publishes message to channel.
     */
    public static void sendUpdatedLocationMessage(Context context, Location location) {

        try {
            if (location != null) {

                HashMap<String, String> value = new HashMap<>();
                value.put("lat", "" + location.getLatitude());
                value.put("long", "" + location.getLongitude());

                String channelName = AutoSpaConstant.PUBNUB_CHANNEL_NAME + "_" + MySharedPreferences.getUserId(context);
                Log.e("chanelName : ", "chanelName : " + channelName);
                AutoSpaApplication.pubnub.publish()
                        .message(value)
                        .channel(channelName) //driver_location_64
                        .async(new PNCallback<PNPublishResult>() {
                            @Override
                            public void onResponse(PNPublishResult result, PNStatus status) {
                                if (!status.isError()) {
                                    Log.e("Success_Data : ", "Success_Data : " + status.getStatusCode());
//                                    updateUI(newLocation);

                                } else {
                                    Log.e("Error_Data : ", "Error_Data : " + status.getErrorData());
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /*
          Helper method that takes in latitude and longitude as parameter and returns a LinkedHashMap representing this data.
          This LinkedHashMap will be the message published by driver.
       */
    private static LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
    }

    public static void setProfilePic(Context context, ImageView imageView) {
        Bitmap default_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.defalut_bg);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new BitmapDrawable(context.getResources(), default_bitmap))
                .showImageForEmptyUri(new BitmapDrawable(context.getResources(), default_bitmap))
                .showImageOnFail(new BitmapDrawable(context.getResources(), default_bitmap))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if (MySharedPreferences.getProfileImage(context).compareTo("") != 0) {
            try {
                ImageLoader.getInstance().displayImage(MySharedPreferences.getProfileImage(context), imageView, options);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.defalut_bg);
            }
        } else {
            imageView.setImageResource(R.drawable.defalut_bg);
        }
    }

    public static void changeTabsFont(TabLayout mTabHost, Context context) {
        ViewGroup vg = (ViewGroup) mTabHost.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/lato/lato_regular.ttf"));
                }
            }
        }
    }


    public static String ConvertDate(String datea, Context context) {
        String convert_date = "";
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dt.parse(datea);
            SimpleDateFormat dt1 = new SimpleDateFormat("hh.mm a , dd MMM yyyy");
            convert_date = dt1.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convert_date;
    }


    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Za-z]).{6,20}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


    public static String DisplayAmount(String amount) {
        double i2 = Double.parseDouble(amount);
        String amt = new DecimalFormat("0.00").format(i2);

        return amt;
    }

    public static void Toast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void hideKeyboard(Context context, AutoCompleteTextView autoCompleteTextView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String convertUtcTimeToLocal(String dateStr, boolean changeFormetStatus, Context context) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(dateStr);
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);
        //  if (changeFormetStatus)
        return ConvertDate(formattedDate, context);
        /*else
            return formattedDate;*/
    }


    public static String convertRetrofitResponce(Response<ResponseBody> responce) {
        try {
            InputStream inputStream = responce.body().byteStream();

            BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            StringBuilder responseStrBuilder = new StringBuilder();
            while ((line = bR.readLine()) != null) {

                responseStrBuilder.append(line);
            }
            inputStream.close();
            return responseStrBuilder.toString();
        } catch (Exception e) {
            return null;
        }
    }


    public static String getPageName(int pagetype, Context context) {
        switch (pagetype) {
            case 0:
                return context.getResources().getString(R.string.aboutus);
            case 1:
                return context.getResources().getString(R.string.contactus);
            case 2:
                return context.getResources().getString(R.string.privacyplicies);
            case 3:
                return context.getResources().getString(R.string.termscondition);
            default:
                return null;

        }
    }


    public static String calculateTimeDiffFromNow(String time, Context context) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);// 2017-11-14T10:01:40.095Z
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);

        Date date = new Date();
        TimeZone timeZone = TimeZone.getDefault();

        //   Date prevTime = sdf.parse(time.replaceAll("Z$", "+0000"));
        Date prevTime = sdf.parse(time);
        sdf.setTimeZone(timeZone);
        long prevMillisecond = prevTime.getTime();
//        long pm = prevMillisecond + 3600000;
        long diff = date.getTime() - prevMillisecond;
        String hms = String.format("%02d hr :%02d min :%02d sec", TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                TimeUnit.MILLISECONDS.toSeconds(diff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));

        String valToReturn;


        //show only seconds
        if (diff <= 1000 * 60) {
            long val = diff / 1000;
            //if(val)
            //valToReturn = val + " Sec";
            valToReturn = context.getString(R.string.just_now);
        }
        //show only minutes
        else if (diff <= 1000 * 60 * 60) {
            long val = diff / (1000 * 60);
            valToReturn = val + " " + context.getString(R.string.minute_text);
        }

        //show only hours
        else {
            long val = diff / (1000 * 60 * 60);
            if (val <= 24)
                valToReturn = val + " " + context.getString(R.string.hour_text);
            else {
                // show only days
                val = val / 24;
                if (val <= 7) {
                    if (val < 2) {
                        valToReturn = val + " " + context.getString(R.string.day);
                    } else {
                        valToReturn = val + " " + context.getString(R.string.days);
                    }
                    // when days are more than 7 show only date
                } else
                    return ConvertDateTimeZone(time);
            }
        }

        Log.i("Utils.java", "Time diff is " + hms);
        // valToReturn = valToReturn.equalsIgnoreCase(context.getString(R.string.just_now)) ?valToReturn:valToReturn + " " + context.getString(R.string.ago);
        if (!valToReturn.equalsIgnoreCase(context.getString(R.string.just_now)))
            return valToReturn + " " + context.getString(R.string.ago);
        else
            return valToReturn;
    }

    public static String ConvertDateTimeZone(String datea) {
        String convert_date = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);// 2017-11-14T10:01:40.095Z
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            sdf.setLenient(false);
            Date prevTime = sdf.parse(datea);
            SimpleDateFormat dt1 = new SimpleDateFormat("hh.mm a , dd MMM yyyy");
            convert_date = dt1.format(prevTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convert_date;
    }

    public static boolean getTimeDifference(String booking_time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm a , dd MMM yyyy");
        try {
            Date oldDate = dateFormat.parse(booking_time);
            Date currentDate = new Date();
            long diff = oldDate.getTime() - currentDate.getTime();
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
            return diffInMin > 0 && diffInMin <= 15;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasInternet(Context context) {
        try {
            ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isActivityFound(Context context) {
        boolean isActivityFound = false;
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
            if (services.get(0).topActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                isActivityFound = true;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return isActivityFound;
    }

    public static DisplayImageOptions displayOption(Context mContext) {
        try {
            Bitmap default_bitmap = Util.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.no_image));
            DisplayImageOptions option = new DisplayImageOptions.Builder()
                    .showImageOnLoading(new BitmapDrawable(mContext.getResources(), default_bitmap))
                    .showImageForEmptyUri(new BitmapDrawable(mContext.getResources(), default_bitmap))
                    .showImageOnFail(new BitmapDrawable(mContext.getResources(), default_bitmap))
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheInMemory(false)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            return option;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}