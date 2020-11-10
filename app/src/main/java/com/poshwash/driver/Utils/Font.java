package com.poshwash.driver.Utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by abhinava on 8/11/2016.
 */
public class Font {
    public static Typeface Bold(Context context) {
        Typeface bold = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Bold.otf");
        return bold;
    }

    public static Typeface Light(Context context) {
        Typeface regular = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf");
        return regular;
    }

    public static Typeface Regular(Context context) {
        Typeface light = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Reg.otf");
        return light;
    }


}
