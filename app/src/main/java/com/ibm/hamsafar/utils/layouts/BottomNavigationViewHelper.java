package com.ibm.hamsafar.utils.layouts;

/**
 * Created by Hamed on 04/05/2018.
 */


import android.annotation.SuppressLint;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

public class BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            menuView.buildMenuView();
        } catch (Exception e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        }
    }
}