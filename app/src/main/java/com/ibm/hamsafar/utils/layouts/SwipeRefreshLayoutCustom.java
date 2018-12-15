package com.ibm.hamsafar.utils.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class SwipeRefreshLayoutCustom extends FrameLayout {

    public SwipeRefreshLayoutCustom(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeRefreshLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean canScrollVertically(int direction) {
        if (super.canScrollVertically(direction)) {
            return true;
        }

        int cc = getChildCount();
        for (int i = 0; i < cc; i++) {
            if (getChildAt(i).canScrollVertically(direction)) {
                return true;
            }
        }

        return false;
    }
}