package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.ibm.hamsafar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class EditTripActivity extends Activity {


    @Override
    protected void attachBaseContext( Context base ) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate( bundle );
        setContentView( R.layout.edit_trip );
    }

}
