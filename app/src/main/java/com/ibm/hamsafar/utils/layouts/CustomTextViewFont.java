package com.ibm.hamsafar.utils.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ibm.hamsafar.utils.Tools;

public class CustomTextViewFont extends android.support.v7.widget.AppCompatTextView {


    public CustomTextViewFont(Context context) {
        super(context);
        init();
    }

    public CustomTextViewFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextViewFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        Typeface tf = Tools.tf;

        setTypeface(tf);
   /*     Drawable drawable = this.getBackground(); // get current EditText drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if(Build.VERSION.SDK_INT > 16) {
            this.setBackground(drawable); // set the new drawable to EditText
        }else{
            this.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }*/
    }
}
