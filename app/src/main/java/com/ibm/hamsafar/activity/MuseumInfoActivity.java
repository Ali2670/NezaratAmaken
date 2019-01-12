package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/12/2019.
 */
public class MuseumInfoActivity extends Activity {

    private Button toolbar_back = null;
    private TextView toolbar_title = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.museum_info);

        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_text);

        toolbar_back.setOnClickListener(view -> onBackPressed());

        toolbar_title.setText(getResources().getString(R.string.hotel_info_title));

    }
}