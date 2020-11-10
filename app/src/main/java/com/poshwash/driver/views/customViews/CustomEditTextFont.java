package com.poshwash.driver.views.customViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by anandj on 3/23/2017.
 */


public class CustomEditTextFont extends EditText {
    public CustomEditTextFont(Context context) {
        super(context);
        init();
    }

    public CustomEditTextFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditTextFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomEditTextFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lato/lato_regular.ttf");
        setTypeface(tf);

    }
}
