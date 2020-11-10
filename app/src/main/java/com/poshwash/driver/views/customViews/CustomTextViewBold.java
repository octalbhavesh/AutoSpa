package com.poshwash.driver.views.customViews;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by anandj on 3/20/2017.
 */
@SuppressLint("AppCompatCustomView")
public class CustomTextViewBold extends TextView {


    public CustomTextViewBold(Context context) {
        super(context);
        init();
    }

    public CustomTextViewBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextViewBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomTextViewBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lato/lato_regular.ttf");
        setTypeface(tf);

    }
}
