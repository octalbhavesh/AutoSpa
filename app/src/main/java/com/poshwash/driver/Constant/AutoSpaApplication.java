package com.poshwash.driver.Constant;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.poshwash.driver.Utils.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by monikab on 8/31/2016.
 */
public class AutoSpaApplication extends MultiDexApplication {

    private static AutoSpaApplication autoSpaApplication;
    public static GoogleApiClient mGoogleApiClient;
    public static Location location;
    public static Activity recent_activity;
    private Retrofit retrofit;
    public static FusedLocationProviderClient mFusedLocationClient;
    public static Location mLastLocation;
    public static DisplayImageOptions options;
    public static PubNub pubnub; // Pubnub instance

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        autoSpaApplication = this;
        initImageLoader(this);
        initPubnub();
        options = Util.displayOption(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /*
            Creates PNConfiguration instance and enters Pubnub credentials to create Pubnub instance.
            This Pubnub instance will be used whenever we need to create connection to Pubnub.
         */
    private void initPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(AutoSpaConstant.PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(AutoSpaConstant.PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(true);
        pubnub = new PubNub(pnConfiguration);
    }

    /**
     * @return VivaLingApplication Single Instance
     */
    public static AutoSpaApplication getInstance() {
        return autoSpaApplication;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public Retrofit getRequestQueue() {
        // initialize the request queue, the queue instance will be created when it is accessed for the first time
        if (retrofit == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // add logging as last interceptor
            builder.addInterceptor(httpLoggingInterceptor); // <-- this is the important line!
            //builder.networkInterceptors().add(httpLoggingInterceptor);
            OkHttpClient okHttpClient = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalControl.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public void setLocale(String lang, Context mContext) {

        if (lang == null) {
            lang = GlobalControl.API_LANGUAGE_ENGLISH;
        }

        if (lang.equalsIgnoreCase(GlobalControl.API_LANGUAGE_ARABIC) || lang.equalsIgnoreCase(GlobalControl.LANGUAGE_ARABIC)) {
            lang = GlobalControl.LANGUAGE_ARABIC;
        } else {
            lang = GlobalControl.LANGUAGE_ENGLISH;
        }

        Locale myLocale = new Locale(lang);
        Resources res = mContext.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        if (Build.VERSION.SDK_INT >= 17) {
            conf.setLayoutDirection(myLocale);
        }
        res.updateConfiguration(conf, dm);
    }
}