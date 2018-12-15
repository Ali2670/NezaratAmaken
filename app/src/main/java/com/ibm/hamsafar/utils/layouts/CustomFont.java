package com.ibm.hamsafar.utils.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ibm.hamsafar.utils.Tools;

/**
 * Created by Hamed on 26/04/2018.
 */
public class CustomFont extends android.support.v7.widget.AppCompatTextView {

    public CustomFont(Context context) {
        super(context);
        init();
    }

    public CustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Tools.tf;
        ;
        setTypeface(tf);
    }
}