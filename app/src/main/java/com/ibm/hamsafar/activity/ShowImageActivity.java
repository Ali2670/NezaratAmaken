package com.ibm.hamsafar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.ibm.hamsafar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/2/2019.
 */
public class ShowImageActivity extends Activity{


    private Context context = this;
    private ImageView share = null;
    private ImageView back = null;
    private ImageView photo = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.show_image);

        share = findViewById(R.id.show_image_share );
        back = findViewById(R.id.show_image_back );
        photo = findViewById(R.id.show_image_photo);

        String photo_str = getIntent().getStringExtra("photo");
        byte[] imageAsBytes = Base64.decode(photo_str.getBytes(), Base64.DEFAULT);
        photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes,0, imageAsBytes.length) );
    }
}
