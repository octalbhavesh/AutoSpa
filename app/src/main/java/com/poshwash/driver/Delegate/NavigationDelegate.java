package com.poshwash.driver.Delegate;

/**
 * Created by jitendrav on 7/9/2015.
 */
public interface NavigationDelegate {
    public void executeFragment(String fragmentName, Object obj);
    public void goBack();

}
