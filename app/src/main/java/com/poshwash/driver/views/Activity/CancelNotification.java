package com.poshwash.driver.views.Activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Created by abhinava on 6/14/2017.
 */

public class CancelNotification extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = Integer.parseInt(getIntent().getStringExtra("id"));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        finish();
    }
}
