package com.poshwash.driver.Delegate;

/**
 * Created by anandj on 3/15/2017.
 */

public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}