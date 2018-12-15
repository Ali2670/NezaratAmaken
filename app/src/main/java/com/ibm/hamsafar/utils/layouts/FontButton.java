package com.ibm.hamsafar.utils.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ibm.hamsafar.utils.Tools;

public class FontButton extends android.support.v7.widget.AppCompatButton {


    public FontButton(Context context) {
        super(context);
        init();
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        Typeface tf = Tools.tf;

        setTypeface(tf);
    }
}
